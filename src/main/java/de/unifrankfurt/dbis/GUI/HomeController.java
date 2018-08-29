package de.unifrankfurt.dbis.GUI;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.unifrankfurt.dbis.IO.Assignment;
import de.unifrankfurt.dbis.IO.FileIO;
import de.unifrankfurt.dbis.IO.SQLCheckerProject;
import de.unifrankfurt.dbis.Submission.SQLScript;
import de.unifrankfurt.dbis.config.GUIConfig;
import de.unifrankfurt.dbis.config.GUIConfigBuilder;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.reactfx.Subscription;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static de.unifrankfurt.dbis.GUI.SQLHighlighter.computeHighlighting;


/**
 * @author oXCToo
 */

public class HomeController implements Initializable {

    @FXML
    public MenuItem saveMenuItem;
    @FXML
    public MenuItem saveAsMenuItem;
    public MenuItem loadConfigMenuItem;
    public MenuItem saveConfigMenuItem;
    public MenuItem closeMenuItem;
    public MenuItem redoMenuItem;
    public MenuItem undoMenuItem;
    public Button undoButton;
    public Button redoButton;
    public Tab tabUebung;
    public Tab tabEinstellung;
    public TabPane tabPane;
    public Button saveButton;
    public Button runButton;
    public Button resetButton;
    public MenuItem resetMenuItem;


    //database
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField hostTextField;
    @FXML
    private TextField portTextField;
    @FXML
    private TextField databaseTextField;
    @FXML
    private TextField resetScriptPathTextField;



    @FXML
    private MenuItem saveConfig;
    @FXML
    private ScrollPane studentScrollPane;
    @FXML
    private TextArea console;
    @FXML
    private ListView<String> taskListView;



    //other
    @FXML
    private BorderPane CODEPANE;

    @FXML
    private Accordion settingAccordion;
    @FXML
    private TitledPane DatenbankTitledPane;

    @FXML
    private CheckBox handleGemeinschaftsabgabenCheckBox;


    //Student Data
    @FXML
    private TextField nameStudentTextField;
    @FXML
    private TextField matNrTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private CheckBox gemeinschaftsabgabenCheckBox;
    @FXML
    private TextField namePartnerTextField;
    @FXML
    private TextField matNrPartnerTextField;
    @FXML
    private TextField emailPartnerTextField;

    private List<VirtualizedScrollPane<CodeArea>> codeAreas;
    private CodeArea activeCodeArea = null;

    private Assignment assignment;
    private GUIConfig GUIConfig;
    private Path projectPath;
    private Object resetScript;

    @Override
    public void initialize(URL url, ResourceBundle rb) {


        System.setOut(new PrintStreamCapturer(console, System.out, "> "));

        CODEPANE.getStylesheets().add("/sql.css");

        codeAreas = new ArrayList<>();


        taskListView.setEditable(false);
        taskListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        initConfig(null);
        updateMenu();

        //Config Fields listener

        ChangeListener<Boolean> configChangeListener = (obs, unfocused, focused) ->
        {
            if (unfocused) {
                this.updateConfig();
            }
        };
        databaseTextField.focusedProperty().addListener(configChangeListener);
        usernameTextField.focusedProperty().addListener(configChangeListener);
        passwordTextField.focusedProperty().addListener(configChangeListener);
        hostTextField.focusedProperty().addListener(configChangeListener);
        portTextField.focusedProperty().addListener(configChangeListener);
        resetScriptPathTextField.focusedProperty().addListener(configChangeListener);
        nameStudentTextField.focusedProperty().addListener(configChangeListener);
        matNrTextField.focusedProperty().addListener(configChangeListener);
        emailTextField.focusedProperty().addListener(configChangeListener);
        gemeinschaftsabgabenCheckBox.focusedProperty().addListener(configChangeListener);
        namePartnerTextField.focusedProperty().addListener(configChangeListener);
        matNrPartnerTextField.focusedProperty().addListener(configChangeListener);
        emailPartnerTextField.focusedProperty().addListener(configChangeListener);


        System.out.println("wird oe richtig dargestellt?: ö");

    }


    public CodeArea initCodeArea() {
        CodeArea codeArea = new CodeArea();
        codeArea.setStyle("-fx-font-family: monospaced; -fx-font-size: 10pt;");
        // add line numbers to the left of area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        Subscription cleanupWhenNoLongerNeedIt = codeArea

                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming assignment method at multiple parts in file)
                .multiPlainChanges()

                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(500))

                // run the following code block when previous stream emits an event
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));


        codeArea.undoAvailableProperty()
                .addListener((observable, oldValue, newValue) -> {
                            if (this.activeCodeArea == codeArea) {
                                undoMenuItem.setDisable(!newValue);
                                undoButton.setDisable(!newValue);
                                undoMenuItem.isDisable(); //undoMenuItem setDisable does not show properly without this. no idea
                            }
                        }
                );
        codeArea.redoAvailableProperty()
                .addListener((observable, oldValue, newValue) -> {
                            if (this.activeCodeArea == codeArea) {
                                redoMenuItem.setDisable(!newValue);
                                redoButton.setDisable(!newValue);
                                redoMenuItem.isDisable();
                                codeArea.requestFocus();
                            }
                        }
                );

        return codeArea;
    }



    public void updateConfig() {
        this.GUIConfig = new GUIConfigBuilder().setUsername(usernameTextField.getText())
                .setPassword(passwordTextField.getText())
                .setHost(hostTextField.getText())
                .setPort(Integer.valueOf(portTextField.getText()))
                .setDatabaseName(databaseTextField.getText())
                .setResetScript(resetScriptPathTextField.getText())
                .setStudentName(nameStudentTextField.getText())
                .setMatNr(matNrTextField.getText())
                .setEmail(emailTextField.getText())
                .setPartnerOk(gemeinschaftsabgabenCheckBox.isSelected())
                .setPartnerName(namePartnerTextField.getText())
                .setPartnerMatNr(matNrPartnerTextField.getText())
                .setPartnerEmail(emailTextField.getText())
                .createConfig();
    }

    private void initConfig(GUIConfig GUIConfig) {
        if (GUIConfig == null)
            this.GUIConfig = new GUIConfigBuilder().createConfig();
        else
            this.GUIConfig = GUIConfig;

        usernameTextField.setText(this.GUIConfig.getUsername());
        passwordTextField.setText(this.GUIConfig.getPassword());
        hostTextField.setText(this.GUIConfig.getHost());
        portTextField.setText(this.GUIConfig.getPort().toString());
        databaseTextField.setText(this.GUIConfig.getDatabaseName());
        resetScriptPathTextField.setText(this.GUIConfig.getResetScript());
        nameStudentTextField.setText(this.GUIConfig.getNameStudent());
        matNrTextField.setText(this.GUIConfig.getMatNr());
        emailTextField.setText(this.GUIConfig.getEmail());
        gemeinschaftsabgabenCheckBox.setSelected(this.GUIConfig.isPartnerOk());
        namePartnerTextField.setText(this.GUIConfig.getPartnerName());
        matNrPartnerTextField.setText(this.GUIConfig.getPartnerMatNr());
        emailPartnerTextField.setText(this.GUIConfig.getPartnerEmail());
        setDisablePartner(!this.GUIConfig.isPartnerOk());

    }

    @FXML
    void handleGemeinschaftsabgabenCheckBox(ActionEvent event) {
        boolean bool = ((CheckBox) event.getSource()).isSelected();
        setDisablePartner(!bool);
        updateConfig();
    }

    private void setDisablePartner(boolean bool) {
        namePartnerTextField.setDisable(bool);
        matNrPartnerTextField.setDisable(bool);
        emailPartnerTextField.setDisable(bool);
    }


    public void undo(ActionEvent actionEvent) {

        if (activeCodeArea != null) {
            activeCodeArea.undo();
            tabPane.getSelectionModel().select(this.tabUebung);
            activeCodeArea.requestFocus();
        }
    }

    public void redo(ActionEvent actionEvent) {
        if (activeCodeArea != null) {
            activeCodeArea.redo();
            tabPane.getSelectionModel().select(this.tabUebung);
            activeCodeArea.requestFocus();
        }
    }

    public void taskSelected(MouseEvent mouseEvent) {
        String task = taskListView.getSelectionModel().getSelectedItem();
        if (task == null) return;
        VirtualizedScrollPane<CodeArea> codeAreaVirtualizedScrollPane = codeAreas.get(Arrays.asList(assignment.getTasks()).indexOf(task));
        this.activeCodeArea = codeAreaVirtualizedScrollPane.getContent();
        this.CODEPANE.setCenter(codeAreaVirtualizedScrollPane);
        this.activeCodeArea.requestFocus();
    }

    public void raiseEx(ActionEvent actionEvent) throws Exception {
        throw new Exception("EXCEPTION");
    }

    public void newProject(ActionEvent actionEvent) {
        Assignment a = new Assignment("name", "assignment", "b", "c");
        a.putCodeMap("assignment", "this is assignment");
        a.putCodeMap("b", "this is b");
        a.putCodeMap("c", "this is c");
        this.initAssignment(a);
        this.updateMenu();
    }

    private void updateMenu() {
        this.saveMenuItem.setDisable(this.assignment == null | this.projectPath == null);
        this.saveAsMenuItem.setDisable(this.assignment == null);
        this.closeMenuItem.setDisable(this.assignment == null);
        this.saveButton.setDisable(this.assignment == null | this.projectPath == null);
    }

    public void initAssignment(Assignment assignment) {
        this.assignment = assignment;
        if (assignment == null) {
            this.activeCodeArea = null;
            this.codeAreas.clear();
            this.CODEPANE.setCenter(null);
            this.taskListView.setItems(FXCollections.observableArrayList(new ArrayList<>()));
            this.projectPath = null;
        } else {
            for (int i = 0; i < assignment.getTasks().length; i++) {
                VirtualizedScrollPane<CodeArea> newPane = new VirtualizedScrollPane<>(initCodeArea());
                newPane.getContent().replaceText(assignment.getCodeMap().get(assignment.getTasks()[i]));
                newPane.getContent().getUndoManager().forgetHistory();
                codeAreas.add(newPane);
            }
            taskListView.setItems(FXCollections.observableArrayList(assignment.getTasks()));
        }
    }

    public void saveAsProject(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichere Checker Datei");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Checker File (*.sqlc)", "*.sqlc"));
        if (projectPath != null) {
            fileChooser.setInitialDirectory(projectPath.getParent().toFile());
            fileChooser.setInitialFileName(projectPath.getFileName().toString());
        }
        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;
        try {
            saveProject(file.toPath());
            projectPath = file.toPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateMenu();
    }

    public void saveProject(ActionEvent actionEvent) {
        try {
            saveProject(this.projectPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveProject(Path path) throws IOException {
        Files.write(path,
                new Gson().toJson(
                        new SQLCheckerProject(this.GUIConfig, this.assignment)).getBytes(StandardCharsets.UTF_8)
        );
    }

    public void loadProject(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("öffne Checker Datei");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Checker File (*.sqlc)", "*.sqlc"));
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;
        SQLCheckerProject project;
        try {
            project = FileIO.load(file.toPath(), SQLCheckerProject.class);
            initAssignment(project.getAssignment());
            initConfig(project.getGUIConfig());
            this.projectPath = file.toPath();
            updateMenu();
        } catch (JsonSyntaxException e) {
            alertNoSQLCFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void alertNoSQLCFile(File file) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Not a SQLChecker File");
        alert.setContentText("Parsing of " + file.toString() + " failed.\nIt seems not to be a valid SQLChecker File.");

        alert.showAndWait();
    }

    public void exportConfig(ActionEvent actionEvent) {
        updateConfig();
        System.out.println(GUIConfig);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichere aktuelle Konfiguration als Datei.");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Konfigurationsdatei (*.conf)", "*.conf"));
        if (projectPath != null) {
            fileChooser.setInitialDirectory(projectPath.getParent().toFile());
        }
        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;
        try {
            System.out.println(GUIConfig);
            saveConfig(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig(Path path) throws IOException {
        Files.write(path,
                (this.GUIConfig.toJson()).getBytes(StandardCharsets.UTF_8)
        );
    }

    public void importConfig(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importiere Konfigurationsdatei");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Konfigurationsdatei (*.conf)", "*.conf"));
        if (projectPath != null) {
            fileChooser.setInitialDirectory(projectPath.getParent().toFile());
        }
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;
        try {
            GUIConfig c = FileIO.load(file.toPath(), GUIConfig.class);
            initConfig(c);
            updateMenu();
        } catch (JsonSyntaxException e) {
            alertNoSQLCFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeProject(ActionEvent actionEvent) {
        initAssignment(null);
        updateMenu();
    }

    public void aboutPage(ActionEvent actionEvent) {
        Parent root;
        try {
            URL fxml = getClass().getResource("/aboutPage.fxml");
            root = FXMLLoader.load(fxml);
            Stage stage = new Stage();
            stage.setTitle("Über SQLChecker");
            stage.setScene(new Scene(root, 450, 450));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadResetScript(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importiere Resetskript");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Datei (*.sql)", "*.sql"));
        if (projectPath != null) {
            fileChooser.setInitialDirectory(projectPath.getParent().toFile());
        }
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;
        try {
            this.resetScript = file.toPath();
            this.resetButton.setDisable(false);
            this.resetMenuItem.setDisable(false);
            this.resetScriptPathTextField.setText(file.getPath());
            this.updateConfig();
        } catch (JsonSyntaxException e) {
            alertNoSQLCFile(file);
        }

    }


    public void handleResetDatabase(ActionEvent actionEvent) throws SQLException, IOException {

        Path resetPath = Paths.get(this.GUIConfig.getResetScript());
        if (!isOkResetPath(resetPath)) {
            System.out.println("Pfad des Reset Skript nicht ok: " + resetPath);
            return;
        }

        SQLScript script = SQLScript.fromPath(resetPath);

        Thread.UncaughtExceptionHandler h = (th, ex) -> ex.printStackTrace();
        Thread t = new Thread(new SQLScriptRunner(this.GUIConfig, script));
        t.setUncaughtExceptionHandler(h);
        t.start();
        System.out.println("start!");

    }

    private boolean isOkResetPath(Path resetPath) {
        return resetPath.toString().endsWith(".sql");
    }

    public void runTaskCode(ActionEvent actionEvent) {
    }


    public void handleRun(ActionEvent actionEvent) {
    }

    public void handleRunAll(ActionEvent actionEvent) {
    }

    public void handleDBFitTest(ActionEvent actionEvent) {
    }

    public void handleExportOlat(ActionEvent actionEvent) {
    }
}
