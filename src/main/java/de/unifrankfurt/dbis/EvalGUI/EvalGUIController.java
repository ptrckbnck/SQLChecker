package de.unifrankfurt.dbis.EvalGUI;

import de.unifrankfurt.dbis.Inner.*;
import de.unifrankfurt.dbis.Inner.Parser.BaseType;
import de.unifrankfurt.dbis.config.EvalConfig;
import javafx.application.Application;
import javafx.application.HostServices;
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
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
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
    public Button undoFilterButton;
    public CheckBox useRegEx;
    public CheckBox verboseCheckBox;
    private ObservableList<BaseInfo> subInfos;
    private Report report;
    private Task<Integer> running;
    public TextField csvPathTextField;
    private ObservableList<List<BaseInfo>> filterHistory;
    private PrintStream out;

    private Stage primaryStage;
    private Application.Parameters parameters;
    private HostServices hostServices;

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
        this.out = System.out; // change if needed
    }

    /**
     * run by EvalGUIApp after instance of Controller is initialized.
     * parameters, hostservices & primarystage are not available during initialize.
     */
    public void start() {
        getPrimaryStage().setMinHeight(300);
        getPrimaryStage().setMinWidth(400);

        List<String> paras = parameters.getRaw();
        if (paras.size() > 0 && Objects.nonNull(paras.get(0))) {
            try {
                EvalConfig config = EvalConfig.fromPath(Paths.get(paras.get(0)));
                iniConfig(config);
            } catch (IOException e) {
                System.err.println("invalid config path " + paras.get(0) + " ignored");
            }
        }
    }

    private void initTable() {

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
        submissionTable.getColumns().add(validColumn);
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
        return primaryStage;
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
        File file;
        try {
            file = directoryChooser.showDialog(getPrimaryStage());
        } catch (IllegalArgumentException e) {
            directoryChooser.setInitialDirectory(Paths.get(System.getProperty("user.dir")).toFile());
            file = directoryChooser.showDialog(getPrimaryStage());
        }
        if (Objects.isNull(file)) {
            return;
        }
        this.submissionsPathTextField.setText(file.getPath());
    }

    public void loadSolutionsPath(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Solutions");
        List<Path> sols = this.getSolutionsPaths();
        if (Objects.nonNull(sols)
                && !sols.isEmpty()
                && Objects.nonNull(sols.get(0))
                && Objects.nonNull(sols.get(0).getParent())) {
            fileChooser.setInitialDirectory(sols.get(0).getParent().toFile());
        }
        List<File> files;
        try {
            files = fileChooser.showOpenMultipleDialog(getPrimaryStage());
        } catch (IllegalArgumentException e) {
            fileChooser.setInitialDirectory(Paths.get(System.getProperty("user.dir")).toFile());
            files = fileChooser.showOpenMultipleDialog(getPrimaryStage());
        }
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
        File file;
        try {
            file = fileChooser.showOpenDialog(getPrimaryStage());
        } catch (IllegalArgumentException e) {
            fileChooser.setInitialDirectory(Paths.get(System.getProperty("user.dir")).toFile());
            file = fileChooser.showOpenDialog(getPrimaryStage());
        }
        if (Objects.isNull(file)) {
            return;
        }
        this.resetScriptPathTextField.setText(file.getPath());
    }

    public void openOutPath(ActionEvent actionEvent) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose Output Path");
        Path p = this.getOutPath();
        if (!Objects.isNull(p) && Files.exists(p) && Files.isDirectory(p)) {
            dirChooser.setInitialDirectory(p.toFile());
        } else {
            dirChooser.setInitialDirectory(Paths.get(".").toFile());
        }
        File file = dirChooser.showDialog(getPrimaryStage());
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
        EvalConfig config = getEvalConfig();
        try (Connection ignored = config.getDataSource().getConnection()) {
            //empty
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
        this.running = new TaskEvaluation(config, rep, this.getSubmissions(), getPrimaryStage(), runButton, this.getOutPath());
        Thread t = new Thread(this.running);
        t.setDaemon(true);
        t.start();
    }

    private Path getOutPath() {
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
        this.running = new TaskFilter(this.subInfos, this.filterHistory, this.getFilterTerm(), useRegEx.isSelected());
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
        this.verboseCheckBox.setSelected(config.getVerbose());
        this.csvPathTextField.setText(config.getCsvOutputPath());
    }

    private EvalConfig getEvalConfig() {
        return new EvalConfig(
                this.databaseTextField.getText(),
                this.usernameTextField.getText(),
                this.passwordTextField.getText(),
                this.hostTextField.getText(),
                this.portTextField.getText(),
                this.resetScriptPathTextField.getText(),
                this.solutionPathTextField.getText(),
                this.submissionsPathTextField.getText(),
                this.verboseCheckBox.isSelected(),
                this.csvPathTextField.getText()
        );
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void doAbout(ActionEvent actionEvent) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("About");
        FlowPane fp = new FlowPane();
        Label lbl = new Label("Github: ");
        Hyperlink link = new Hyperlink("https://github.com/ptrckbnck/SQLChecker");
        link.setOnAction((x) -> hostServices.showDocument("https://github.com/ptrckbnck/SQLChecker"));
        fp.getChildren().addAll(lbl, link);
        a.getDialogPane().contentProperty().set(fp);
        a.showAndWait();
    }

    public void setParameters(Application.Parameters parameters) {
        this.parameters = parameters;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
}
