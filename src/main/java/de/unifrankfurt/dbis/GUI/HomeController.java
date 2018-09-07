package de.unifrankfurt.dbis.GUI;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.unifrankfurt.dbis.IO.FileIO;
import de.unifrankfurt.dbis.IO.SQLCheckerProject;
import de.unifrankfurt.dbis.Main;
import de.unifrankfurt.dbis.Submission.SQLScript;
import de.unifrankfurt.dbis.Submission.Submission;
import de.unifrankfurt.dbis.Submission.SubmissionParseException;
import de.unifrankfurt.dbis.Submission.TaskSQL;
import de.unifrankfurt.dbis.config.GUIConfig;
import de.unifrankfurt.dbis.config.GUIConfigBuilder;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
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
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

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
    public MenuItem runMenuItem;
    public MenuItem runAllMenuItem;
    public MenuItem exportMenuItem;


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

    //intern data
    private List<VirtualizedScrollPane<CodeArea>> codeAreas;
    private CodeArea activeCodeArea = null;
    private Assignment assignment;
    private GUIConfig GUIConfig;
    private Path projectPath;
    private Path resetScript;


    @Override
    public void initialize(URL url, ResourceBundle rb) {


        System.setOut(new PrintStreamCapturer(console, System.out, "> "));
        System.setErr(new PrintStreamCapturer(console, System.err, "> [ERROR] "));

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


        ContextMenu cm = new ContextMenu();
        MenuItem mi = new MenuItem("löschen");
        mi.setOnAction((x) -> console.setText(""));
        cm.getItems().add(mi);


        console.setContextMenu(cm);

    }

    /**
     * set Path of current project, null represents that there is no active Project.
     *
     * @param projectPath
     */
    public void setProjectPath(Path projectPath) {
        this.projectPath = projectPath;
        this.updateMenu();
    }

    /**
     * initializes new CodeArea with styles and listener.
     *
     * @return CodeArea
     */
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
        Subscription cleanupWhenNoLongerNeedIt2 = codeArea
                .multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .subscribe(ignore -> {
                            if (getSelectedTask() != null)
                                this.assignment.putCodeMap(getSelectedTask(), codeArea.getText());
                        }
                );


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

        Subscription cleanupWhenNoLongerNeedIt3 = codeArea
                .multiPlainChanges()
                .successionEnds(Duration.ofMillis(5000))
                .subscribe(ignore -> {
                    if (this.projectPath != null && this.getSelectedTask() != null) {
                        try {
                            this.saveProject(projectPath);
                        } catch (IOException e) {
                            //;
                        }
                    }
                });

        return codeArea;
    }


    /**
     * creates new Config Object everytime settings get modified.
     */
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


    /**
     * Initializes current Config with given Config or creates empty Config if null.
     *
     * @param GUIConfig given GUIConfig or null
     */
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


    /**
     * disables or enables Partner Config Fields
     * @param bool true disables Fields.
     */
    private void setDisablePartner(boolean bool) {
        namePartnerTextField.setDisable(bool);
        matNrPartnerTextField.setDisable(bool);
        emailPartnerTextField.setDisable(bool);
    }

    /**
     * updates menu and stage if projectPath or assignment changes
     */
    private void updateMenu() {
        boolean assignmentNotExists = this.assignment == null;
        boolean projectNotExists = this.projectPath == null;


        this.saveMenuItem.setDisable(assignmentNotExists | projectNotExists);
        this.saveMenuItem.isDisable();
        this.saveButton.setDisable(assignmentNotExists | projectNotExists);
        this.saveAsMenuItem.setDisable(assignmentNotExists);
        this.saveAsMenuItem.isDisable();
        this.closeMenuItem.setDisable(assignmentNotExists);
        this.closeMenuItem.isDisable();
        this.runAllMenuItem.setDisable(assignmentNotExists);
        this.runAllMenuItem.isDisable();
        this.exportMenuItem.setDisable(assignmentNotExists);

        if (!assignmentNotExists)
            Main.getPrimaryStage().setTitle(this.assignment.getName());
        else
            Main.getPrimaryStage().setTitle("");

    }

    /**
     * Initializes current assignment with given assignment. Null means there is currently no assignment.
     *
     * @param assignment Assignment or null
     */
    public void initAssignment(Assignment assignment) {
        this.assignment = assignment;
        if (assignment == null) {
            this.setActiveCodeArea(null);
            this.codeAreas.clear();
            this.CODEPANE.setCenter(null);
            this.taskListView.setItems(FXCollections.observableArrayList(new ArrayList<>()));
            setProjectPath(null); //implicit updateMenu()
        } else {
            for (String task : assignment.getTasks()) {
                VirtualizedScrollPane<CodeArea> newPane = new VirtualizedScrollPane<>(initCodeArea());
                newPane.getContent().replaceText(assignment.getCodeMap().get(task));
                newPane.getContent().getUndoManager().forgetHistory();
                codeAreas.add(newPane);
            }
            taskListView.setItems(FXCollections.observableArrayList(assignment.getTasks()));
            updateMenu();
        }

    }

    /**
     * sets given CodeArea to active CodeArea.
     *
     * @param codeArea
     */
    private void setActiveCodeArea(CodeArea codeArea) {
        this.activeCodeArea = codeArea;
        this.runButton.setDisable(codeArea == null);
        this.runMenuItem.setDisable(codeArea == null);
    }

    /**
     * Initialize Assignment from Submission.
     *
     * @param submission
     */
    public void initAssignment(Submission<TaskSQL> submission) {
        initAssignment(Assignment.fromSubmission(submission));
    }

    /**
     * disables or enables Partner Config Fields if box is checked or unchecked.
     *
     * @param event
     */
    @FXML
    void handleGemeinschaftsabgabenCheckBox(ActionEvent event) {
        boolean bool = ((CheckBox) event.getSource()).isSelected();
        setDisablePartner(!bool);
        updateConfig();
    }

    /**
     * undo active CodeArea
     * @param actionEvent
     */
    public void undo(ActionEvent actionEvent) {
        if (activeCodeArea != null) {
            activeCodeArea.undo();
            tabPane.getSelectionModel().select(this.tabUebung);
            activeCodeArea.requestFocus();
        }
    }

    /**
     * redo active CodeArea
     * @param actionEvent
     */
    public void redo(ActionEvent actionEvent) {
        if (activeCodeArea != null) {
            activeCodeArea.redo();
            tabPane.getSelectionModel().select(this.tabUebung);
            activeCodeArea.requestFocus();
        }
    }

    /**
     * sets and focus active CodeArea if corresponding task is clicked.
     * @param mouseEvent
     */
    public void taskSelected(MouseEvent mouseEvent) {
        String task = getSelectedTask();
        if (task == null) return;
        VirtualizedScrollPane<CodeArea> codeAreaVirtualizedScrollPane = codeAreas.get(assignment.getTasks().indexOf(task));
        setActiveCodeArea(codeAreaVirtualizedScrollPane.getContent());
        this.CODEPANE.setCenter(codeAreaVirtualizedScrollPane);
        this.activeCodeArea.requestFocus();
    }


    /**
     * @return currently selected Task or null if none is selected.
     */
    public String getSelectedTask() {
        return taskListView.getSelectionModel().getSelectedItem();
    }

    /**
     * Save project to given Path,
     *
     * @param path Path to project.
     * @throws IOException
     */
    public void saveProject(Path path) throws IOException {
        if (getSelectedTask() != null)
            this.assignment.putCodeMap(getSelectedTask(), this.activeCodeArea.getText());
        Files.write(path,
                new Gson().toJson(
                        new SQLCheckerProject(this.GUIConfig, this.assignment)).getBytes(StandardCharsets.UTF_8)
        );
        try {
            saveConfig(defaultConfigPath(path));
        } catch (IOException e) {
            System.err.println("Speichern der Config-Datei fehlgeschlagen: " + e.getMessage());
        }
    }

    /**
     * creates the default path to config for given path to project.
     *
     * @param projectPath Path to current Project
     * @return Path to Config
     */
    private Path defaultConfigPath(Path projectPath) {
        return projectPath.getParent().resolve(projectPath.getFileName().toString() + ".conf");
    }


    /**
     * Load Config from default Path if present.
     */
    private void loadConfigImplicit() {
        try {
            GUIConfig c = FileIO.load(defaultConfigPath(this.projectPath), GUIConfig.class);
            initConfig(c);
            return;
        } catch (IOException e) {
            //nothing;
        }
        try {
            Optional<Path> conf = Files.walk(this.projectPath.getParent(), 1)
                    .filter(Files::isReadable)
                    .filter((x) -> x.getFileName().toString().endsWith(".conf"))
                    .findFirst();
            if (conf.isPresent()) {
                GUIConfig c = FileIO.load(conf.get(), GUIConfig.class);
                initConfig(c);
            }

        } catch (IOException e) {
            //nothing;
        }

    }

    /**
     * Load ResetScript from default path if present.
     */
    private void loadResetImplicit() {
        Path path = defaultResetPath(this.projectPath);
        if (Files.isReadable(path))
            initResetScript(defaultResetPath(this.projectPath));
    }

    /**
     * Creates default Path to ResetScript for given Path to project.
     *
     * @param projectPath
     * @return Path to ResetScript
     */
    private Path defaultResetPath(Path projectPath) {
        return projectPath
                .getParent()
                .resolve(this.assignment.getName() + "_reset.sql");
    }


    /**
     * Creates Alert that File file is not a valid .sqlc file.
     *
     * @param file
     */
    private void alertNoValidSQLCFile(File file) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Not a vaLid SQLChecker File");
        alert.setContentText("Parsing of " + file.toString() + " failed.\nIt seems not to be a valid SQLChecker File.");
        alert.showAndWait();
    }


    /**
     * Save current Config to given Path.
     *
     * @param path Path
     * @throws IOException
     */
    public void saveConfig(Path path) throws IOException {
        Files.write(path,
                (this.GUIConfig.toJson()).getBytes(StandardCharsets.UTF_8)
        );
    }


    /**
     * creates new Project
     * @param actionEvent
     */
    public void newProject(ActionEvent actionEvent) {
        //select template first
        FileChooser templateChooser = new FileChooser();
        templateChooser.setTitle("Öffne Aufgaben-Template Datei");
        templateChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Template File (*.sqlt)", "*.sqlt"));
        templateChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL File (*.sql)", "*.sql"));
        Stage stageTemplate = new Stage();
        File template = templateChooser.showOpenDialog(stageTemplate);
        if (template == null) return;
        Submission<TaskSQL> submission;
        try {
            submission = Submission.fromPath(template.toPath()).onlyTaskSQLSubmission();

        } catch (IOException e) {
            System.err.println("Fehler beim Öffnen der Aufgabe.");
            return;
        } catch (SubmissionParseException e) {
            System.err.println("Fehler beim Einlesen der Aufgabe.");
            return;
        }

        // now select Project Path
        FileChooser projectChooser = new FileChooser();
        projectChooser.setTitle("Lege Speicherort des neuen Projekts fest");
        projectChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Checker File (*.sqlc)", "*.sqlc"));
        projectChooser.setInitialFileName(submission.getName() + ".sqlc");
        projectChooser.setInitialDirectory(template.getParentFile());
        Stage stageProject = new Stage();
        File project = projectChooser.showSaveDialog(stageProject);
        if (project == null) return;
        try {
            saveProject(project.toPath());
            setProjectPath(project.toPath());
            this.initAssignment(submission);
            loadConfigImplicit();
            loadResetImplicit();
        } catch (IOException e) {
            System.err.println("Fehler beim Anlegen der Projekt-Datei.");
            setProjectPath(null);
        }

    }

    /**
     * initializes ResetScript Path.
     *
     * @param path
     */
    private void initResetScript(Path path) {
        this.resetScript = path;
        this.resetButton.setDisable(false);
        this.resetMenuItem.setDisable(false);
        this.resetScriptPathTextField.setText(path.toString());
        this.updateConfig();
    }

    /**
     * checks if ResetPath is ending with .sql.
     *
     * @param resetPath
     * @return true - if path ends with .sql
     */
    private boolean isOkResetPath(Path resetPath) {
        return resetPath.toString().endsWith(".sql");
    }

    /**
     * runs code of given task in new Thread.
     *
     * @param task String
     * @param sql  String
     * @return Thread
     */
    private Thread runCode(String task, String sql) {

        SQLRunner runner = new SQLRunner(this.GUIConfig, sql);
        Thread t = new Thread(runner);
        System.out.println(task + ": SQL Code wird ausgeführt.");
        t.start();
        return t;
    }

    /**
     * Save current Project to new Path.
     * @param actionEvent
     */
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
        Path rollback = this.projectPath;
        try {
            setProjectPath(file.toPath());
            saveProject(file.toPath());
        } catch (IOException e) {
            setProjectPath(rollback);
            System.err.println("Speichern der Projektes fehlgeschlagen: " + e.getMessage());
        }
        updateMenu();
    }

    /**
     * Save project to projectPath.
     * @param actionEvent
     */
    public void saveProject(ActionEvent actionEvent) {
        try {
            saveProject(this.projectPath);
        } catch (IOException e) {
            System.err.println("Speichern der Projektes fehlgeschlagen: " + e.getMessage());
        }
    }

    /**
     * Load Project
     * @param actionEvent
     */
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
            if (!projectOK(project)) {
                alertNoValidSQLCFile(file);
                return;
            }
            initAssignment(project.getAssignment());
            initConfig(project.getGUIConfig());
            setProjectPath(file.toPath());
            loadConfigImplicit();
            loadResetImplicit();
        } catch (JsonSyntaxException e) {
            alertNoValidSQLCFile(file);
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Projekt-Datei: " + e.getMessage());
        }

    }

    private boolean projectOK(SQLCheckerProject project) {
        return Objects.nonNull(project) &&
                Objects.nonNull(project.getAssignment()) &&
                Objects.nonNull(project.getGUIConfig());
    }

    /**
     * exports current Config.
     * @param actionEvent
     */
    public void exportConfig(ActionEvent actionEvent) {
        updateConfig();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichere aktuelle Konfiguration als Datei.");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Konfigurationsdatei (*.conf)", "*.conf"));
        if (projectPath != null) {
            fileChooser.setInitialDirectory(projectPath.getParent().toFile());
        }
        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;
        try {
            saveConfig(file.toPath());
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Config-Datei: " + e.getMessage());
        }
    }

    /**
     * import Config.
     * @param actionEvent
     */
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
            alertNoValidSQLCFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * close current project.
     * @param actionEvent
     */
    public void closeProject(ActionEvent actionEvent) {
        initAssignment((Assignment) null);
        updateMenu();
    }

    /**
     * shows about-page.
     * @param actionEvent
     */
    public void aboutPage(ActionEvent actionEvent) {
        Parent root;
        try {
            URL fxml = getClass().getResource("/aboutPage.fxml");
            root = FXMLLoader.load(fxml);
            Stage stage = new Stage();
            stage.setTitle("Über SQLChecker");
            stage.setScene(new Scene(root, 400, 300));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads reset Script.
     * @param actionEvent
     */
    public void loadResetScript(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importiere Resetskript");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Datei (*.sql)", "*.sql"));
        if (this.resetScript != null) {
            fileChooser.setInitialDirectory(this.resetScript.getParent().toFile());
        } else if (projectPath != null) {
            fileChooser.setInitialDirectory(projectPath.getParent().toFile());
        }
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;
        initResetScript(file.toPath());

    }

    /**
     * resets Database with ResetScript.
     * @param actionEvent
     */
    public void handleResetDatabase(ActionEvent actionEvent) {

        Path resetPath = Paths.get(this.GUIConfig.getResetScript());
        if (!isOkResetPath(resetPath)) {
            System.err.println("Pfad des Reset Skripts nicht ok: " + resetPath);
            return;
        }

        SQLScript script;
        try {
            script = SQLScript.fromPath(resetPath);
        } catch (NoSuchFileException e) {
            System.err.println("Reset Skript nicht gefunden.");
            return;
        } catch (IOException e) {
            System.err.println("Laden der Reset Skripts Fehlgeschlagen.");
            return;
        }

        SQLScriptRunner sr = new SQLScriptRunner(this.GUIConfig, script);
        Thread t = new Thread(sr);
        System.out.println("Resette Datenbank.");
        t.start();

    }


    /**
     * runs code of current task.
     * @param actionEvent
     */
    public void runTaskCode(ActionEvent actionEvent) {
        String sql = this.activeCodeArea.getText();
        runCode(this.getSelectedTask(), sql);
    }


    /**
     * runs code of every task consecutively.
     * @param actionEvent
     */
    public void handleRunAll(ActionEvent actionEvent) {
        new Thread(new Task<>() {
            @Override
            protected Object call() {
                for (String task : assignment.getTasks()) {
                    try {
                        runCode(task, assignment.getCodeMap().get(task)).join();
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Jede Aufgabe wurde ausgeführt.");
                return null;
            }
        }).start();
    }

    /**
     * tests if Code is DBFit usable. TODO
     * @param actionEvent
     */
    public void handleDBFitTest(ActionEvent actionEvent) {
    }

    /**
     * creates zip File with Submission for loading to Olat.
     * @param actionEvent
     */
    public void handleExportOlat(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Erzeuge Abgabe-Datei für Olat");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Datei (*.txt)", "*.txt"));
        if (projectPath != null) {
            fileChooser.setInitialDirectory(projectPath.getParent().toFile());
            fileChooser.setInitialFileName(this.assignment.getName() + "_" + this.GUIConfig.getMatNr() + ".txt");
        }
        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;
        try {
            FileIO.saveText(file.toPath(), new SQLCheckerProject(this.GUIConfig, this.assignment)
                    .createSubmission().serialize());
        } catch (IOException e) {
            System.err.println("Speichern fehlgeschlagen: " + e.getMessage());
        }

    }
}
