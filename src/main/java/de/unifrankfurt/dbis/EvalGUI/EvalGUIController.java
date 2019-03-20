package de.unifrankfurt.dbis.EvalGUI;

import de.unifrankfurt.dbis.Inner.*;
import de.unifrankfurt.dbis.Inner.Parser.BaseType;
import de.unifrankfurt.dbis.config.EvalConfig;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EvalGUIController implements Initializable {

    public TableView<BaseInfo> submissionTable;
    public TextField databaseTextField;
    public TextField usernameTextField;
    public TextField hostTextField;
    public PasswordField passwordTextField;
    public TextField portTextField;
    public TextField resetScriptPathTextField;
    public TextField timezoneTextField;
    public TextField solutionPathTextField;
    public TextField submissionsPathTextField;
    public Button LoadSubmissionsButton;
    public Tab tabEinstellung;
    public Button runButton;
    public Button filterButton;
    public TextField filterTextField;
    private static Method columnToFitMethod;
    public Button undoFilterButton;
    private ObservableList<BaseInfo> subInfos;
    private Report report;
    private Task<Integer> running;
    public TextField csvPathTextField;
    private ObservableList<List<BaseInfo>> filterHistory;
    private PrintStream out;

    public List<Base> getSubmissions() {
        return this.subInfos.stream()
                .map(BaseInfo::getBase)
                .filter(x -> !Objects.isNull(x))
                .collect(Collectors.toList());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();
        this.timezoneTextField.setText("+01:00");
        this.subInfos = FXCollections.observableArrayList();
        this.filterHistory = FXCollections.observableArrayList();
        this.undoFilterButton.setDisable(true);
        this.runButton.setDisable(true);
        submissionTable.setItems(subInfos);

        subInfos.addListener((ListChangeListener<BaseInfo>) change -> {
            if (this.subInfos.isEmpty())
                this.runButton.setDisable(true);
            else {
                this.runButton.setDisable(false);
            }
        });

        filterHistory.addListener((ListChangeListener<List<BaseInfo>>) change -> {
            if (filterHistory.isEmpty()) {
                this.undoFilterButton.setDisable(true);

            } else {
                this.undoFilterButton.setDisable(false);
            }

        });

        this.out = System.out; //TODO maybe set outstream

    }

    public void initTable() {

        TableColumn<BaseInfo, Path> pathColumn = new TableColumn<>("Path");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        TableColumn<BaseInfo, String> nameColumn = new TableColumn<>("Submission");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<BaseInfo, Charset> charsetColumn = new TableColumn<>("Charset");
        charsetColumn.setCellValueFactory(new PropertyValueFactory<>("charset"));

        TableColumn<BaseInfo, List<Student>> authorsColumn = new TableColumn<>("Authors");
        authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));

        TableColumn<BaseInfo, Boolean> validColumn = new TableColumn<>("Valid");
        validColumn.setCellValueFactory(new PropertyValueFactory<>("valid"));

        TableColumn<BaseInfo, Integer> idColumn = new TableColumn<>("#");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<BaseInfo, String> errorColumn = new TableColumn<>("Error");
        errorColumn.setCellValueFactory(new PropertyValueFactory<>("error"));

        TableColumn<BaseInfo, BaseType> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("baseType"));


        submissionTable.getColumns().add(idColumn);
        submissionTable.getColumns().add(typeColumn);
        submissionTable.getColumns().add(pathColumn);
        submissionTable.getColumns().add(nameColumn);
        submissionTable.getColumns().add(charsetColumn);
        submissionTable.getColumns().add(authorsColumn);
        submissionTable.getColumns().add(validColumn); //todo needs better definition what valid means
        submissionTable.getColumns().add(errorColumn);
    }

    /***
     * looks up Path defined in submissionsPathTextField
     * @return Path or null if textfield is empty
     */
    private Path getSubmissionsPath() throws NullPointerException {
        String p = this.submissionsPathTextField.getText();
        if (p.isBlank()) return null;
        return Paths.get(this.submissionsPathTextField.getText());
    }

    /***
     * looks up Path defined in resetScriptPathTextField
     * @return Path or null if textfield is empty
     */
    private Path getResetScriptPath() {
        String p = this.resetScriptPathTextField.getText();
        if (p.isBlank()) return null;
        return Paths.get(this.resetScriptPathTextField.getText());
    }

    private Stage getPrimaryStage() {
        return EvalGUIApp.getPrimaryStage();
    }

    /***
     * looks up all Paths defined in resetScriptPathTextField seperated by ,
     * @return List<Path> or null if textfield is empty
     */
    private List<Path> getSolutionsPaths() {
        String p = this.solutionPathTextField.getText();
        if (p.isBlank()) return null;
        return Arrays.stream(p.split(",")).map(Paths::get).collect(Collectors.toList());
    }

    public void loadSubmissionsPath(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Submissions Path");
        Path p = this.getSubmissionsPath();
        if (!Objects.isNull(p)) {
            directoryChooser.setInitialDirectory(Objects.requireNonNull(p).toFile());
        }
        File file = directoryChooser.showDialog(getPrimaryStage());
        if (Objects.isNull(file)) {
            return;
        }
        this.submissionsPathTextField.setText(file.getPath());
    }

    public void loadSolutionsPath(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Solutions");
        List<Path> sols = this.getSolutionsPaths();
        if (!Objects.isNull(sols) && !sols.isEmpty()) {
            fileChooser.setInitialDirectory(sols.get(0).getParent().toFile());
        }
        List<File> files = fileChooser.showOpenMultipleDialog(getPrimaryStage());
        if (Objects.isNull(files)) {
            return;
        }
        this.solutionPathTextField.setText(files.stream().map(File::getPath).collect(Collectors.joining(",")));
    }

    public void loadSubmissions(ActionEvent actionEvent) {
        try {
            Path submissionsPath = getSubmissionsPath();
            if (Objects.isNull(submissionsPath)) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("No Submissions defined");
                a.showAndWait();
                return;
            }
            System.err.println(submissionsPath);
            report = new Report();
            report.setRootPath(this.getSubmissionsPath());
            subInfos.clear();
            List<BaseInfo> infos;
            try {
                infos = Bases.loadBaseInfos(submissionsPath);
            } catch (IOException e) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Laden der Submissions fehlgeschlagen\n" + e.getMessage());
                a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                a.showAndWait();
                return;
            }

            subInfos.addAll(infos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadResetScriptPath(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose ResetScript");
        Path p = this.getResetScriptPath();
        if (!Objects.isNull(p)) {
            fileChooser.setInitialDirectory(p.toFile());
        }
        File file = fileChooser.showOpenDialog(getPrimaryStage());
        if (Objects.isNull(file)) {
            return;
        }
        this.resetScriptPathTextField.setText(file.getPath());
    }

    public void openCSVPath(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose CSV Path");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        Path p = this.getCSVOut();
        if (!Objects.isNull(p)) {
            fileChooser.setInitialDirectory(p.getParent().toFile());
        }
        File file = fileChooser.showSaveDialog(getPrimaryStage());
        if (Objects.isNull(file)) {
            return;
        }
        this.csvPathTextField.setText(file.getPath());
    }


    public void run(ActionEvent actionEvent) {
        if (!Objects.isNull(running) && !this.running.isDone()) {
            running.cancel();
            running = null;
            runButton.setText("Run");
            getPrimaryStage().setTitle("");
            return;
        }
        runButton.setText("Stop");
        getPrimaryStage().setTitle("Running ...");
        EvalConfig config = new EvalConfig(this.databaseTextField.getText(), this.usernameTextField.getText(), this.passwordTextField.getText(), this.hostTextField.getText(), this.portTextField.getText(), this.resetScriptPathTextField.getText(), this.solutionPathTextField.getText(), this.submissionsPathTextField.getText());
        System.err.println("test1"); //TODO remove
        try (Connection c = config.getDataSource().getConnection()) {
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Verbindung zur Datenbank konnte nicht hergestellt werden.\n" + e.getMessage());
            a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            a.showAndWait();
            runButton.setText("Run");
            getPrimaryStage().setTitle("");
            return;
        }


        Report rep = new Report();
        if (!this.isFiltered()) {
            rep.addAll(report.getResultStorages());
        }
        rep.setSolutionMetadata(report.getSolutionMetadata());
        rep.setRootPath(report.getRootPath());
        this.running = new TaskEvaluation(config, rep, this.getSubmissions(), getPrimaryStage(), runButton, out, this.getCSVOut());
        Thread t = new Thread(this.running);
        t.setDaemon(true);
        t.start();
    }

    private Path getCSVOut() {
        final String text = this.csvPathTextField.getText();
        if (text.isBlank())
            return null;
        return Paths.get(text);
    }

    private boolean isFiltered() {
        return !this.filterHistory.isEmpty();
    }

    public void filter(ActionEvent actionEvent) {
        if (!Objects.isNull(running)) {
            running.cancel();
        }
        this.running = new TaskFilter(this.subInfos, this.filterHistory, this.getFilterTerm());
        Thread t = new Thread(this.running);
        t.setDaemon(true);
        t.start();
    }

    private String getFilterTerm() {
        return this.filterTextField.getText();
    }

    public void undoFilter(ActionEvent actionEvent) {
        if (filterHistory.isEmpty()) return;
        List<BaseInfo> filtered = this.filterHistory.remove(filterHistory.size() - 1);
        this.subInfos.setAll(filtered);
    }


    public void exportConfig(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wähle Speicherort für Config");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("INI Datei", "*.ini"),
                new FileChooser.ExtensionFilter("All", "*"));
        File file = fileChooser.showSaveDialog(getPrimaryStage());
        if (Objects.isNull(file)) {
            return;
        }
        EvalConfig config = this.getEvalConfig();

        try {
            config.storeInPath(file.toPath());
        } catch (IOException e) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("Config konnte nicht gespeichert werden");
            a.setContentText(e.getMessage());
            a.showAndWait();
        }

    }

    public void loadConfig(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wähle Config");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("INI Datei", "*.ini"),
                new FileChooser.ExtensionFilter("All", "*"));
        File file = fileChooser.showOpenDialog(getPrimaryStage());
        if (Objects.isNull(file)) {
            return;
        }
        EvalConfig config;
        try {
            config = EvalConfig.fromPath(file.toPath());
        } catch (IOException e) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("Keine gültige Config");
            a.setContentText(e.getMessage());
            a.showAndWait();
            return;
        }
        this.iniConfig(config);
    }

    private void iniConfig(EvalConfig config) {
        this.solutionPathTextField.setText(config.getSolutionPaths());
        this.resetScriptPathTextField.setText(config.getResetPath());
        this.submissionsPathTextField.setText(config.getSubmissionPath());
        this.usernameTextField.setText(config.getUsername());
        this.databaseTextField.setText(config.getDatabase());
        this.passwordTextField.setText(config.getPassword());
        this.hostTextField.setText(config.getHostname());
        this.portTextField.setText(config.getPort());
    }

    private EvalConfig getEvalConfig() {
        return new EvalConfig(this.databaseTextField.getText(),
                this.usernameTextField.getText(),
                this.passwordTextField.getText(),
                this.hostTextField.getText(),
                this.portTextField.getText(),
                this.resetScriptPathTextField.getText(),
                this.solutionPathTextField.getText(),
                this.submissionsPathTextField.getText());
    }

    public void doAbout(ActionEvent actionEvent) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("About");
        FlowPane fp = new FlowPane();
        Label lbl = new Label("Github: ");
        Hyperlink link = new Hyperlink("https://github.com/ptrckbnck/SQLChecker");
        link.setOnAction((x) -> EvalGUIApp.hostServices().showDocument("https://github.com/ptrckbnck/SQLChecker"));
        fp.getChildren().addAll(lbl, link);
        a.getDialogPane().contentProperty().set(fp);
        a.showAndWait();
    }
}
