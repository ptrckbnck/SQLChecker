package de.unifrankfurt.dbis.EvalGUI;

import de.unifrankfurt.dbis.Evaluator;
import de.unifrankfurt.dbis.Inner.*;
import de.unifrankfurt.dbis.config.DataSource;
import de.unifrankfurt.dbis.config.EvalConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EvalGUIController implements Initializable {

    public TableView<SubmissionInfo> submissionTable;
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
    private ObservableList<SubmissionInfo> subInfos;
    private Report report;
    private Task<Integer> running;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.err.println("TEST");
        initTable();
        this.solutionPathTextField.setText("/home/xyntek/Dropbox/SQLChecker/NEU_TESTS/Blatt4_solution_Eval_new.sql");
        this.resetScriptPathTextField.setText("/home/xyntek/Dropbox/SQLChecker/NEU_TESTS/Blatt4_reset_Eval.sql"); //TODO
        this.submissionsPathTextField.setText("/home/xyntek/Dropbox/SQLChecker/NEU_TESTS/Abgaben");
        this.hostTextField.setText("localhost");
        this.passwordTextField.setText("airportuser");
        this.timezoneTextField.setText("+01:00");
        this.databaseTextField.setText("airport");
        this.portTextField.setText("3306");
        this.usernameTextField.setText("airportuser");
        this.subInfos = FXCollections.observableList(new ArrayList<>());

        submissionTable.setItems(subInfos);
    }

    public List<Submission> getSubmissions() {
        return this.subInfos.stream()
                .map(SubmissionInfo::getSubmission)
                .filter(x -> !Objects.isNull(x))
                .collect(Collectors.toList());
    }

    public void initTable() {

        TableColumn<SubmissionInfo, Path> pathColumn = new TableColumn<>("Path");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        TableColumn<SubmissionInfo, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<SubmissionInfo, Charset> charsetColumn = new TableColumn<>("Charset");
        charsetColumn.setCellValueFactory(new PropertyValueFactory<>("charset"));

        TableColumn<SubmissionInfo, List<Student>> authorsColumn = new TableColumn<>("Authors");
        authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));

        TableColumn<SubmissionInfo, Boolean> validColumn = new TableColumn<>("Valid");
        validColumn.setCellValueFactory(new PropertyValueFactory<>("valid"));

        submissionTable.getColumns().add(pathColumn);
        submissionTable.getColumns().add(nameColumn);
        submissionTable.getColumns().add(charsetColumn);
        submissionTable.getColumns().add(authorsColumn);
        submissionTable.getColumns().add(validColumn);

    }

    private Path getResetScriptPath() {
        return Paths.get(this.resetScriptPathTextField.getText());
    }

    private Stage getPrimaryStage() {
        return EvalGUIApp.getPrimaryStage();
    }

    public void loadSubmissionsPath(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Submissions Path");
        /*if (!Objects.isNull(getResetScriptPath())) {
            directoryChooser.setInitialDirectory(getSubmissionsPath().toFile());
        }*/
        File file = directoryChooser.showDialog(getPrimaryStage());
        if (Objects.isNull(file)) {
            return;
        }
        this.submissionsPathTextField.setText(file.getPath());
    }

    private Path getSubmissionsPath() {
        return Paths.get(this.submissionsPathTextField.getText());
    }

    public void loadSolutionsPath(ActionEvent actionEvent) {
        //TODO
    }

    public void loadSubmissions(ActionEvent actionEvent) {
        report = new Report();
        report.setRootPath(this.getSubmissionsPath());
        subInfos.clear();
        List<SubmissionInfo> infos = Evaluator.loadSubmissions(getSubmissionsPath(), report)
                .stream()
                .map(SubmissionInfo::of)
                .collect(Collectors.toList());
        subInfos.addAll(infos);
    }


    public void loadResetScriptPath(ActionEvent actionEvent) {//TODO
    }

    public void run(ActionEvent actionEvent) {
        if (!Objects.isNull(running)) {
            running.cancel();
            running = null;
            return;
        }
        this.runButton.setText("Stop");
        this.getPrimaryStage().setTitle("Running ...");
        System.err.println("test");
        EvalConfig config = new EvalConfig(this.databaseTextField.getText(), this.usernameTextField.getText(), this.passwordTextField.getText(), this.hostTextField.getText(), this.portTextField.getText(), this.resetScriptPathTextField.getText(), this.solutionPathTextField.getText(), this.submissionsPathTextField.getText());
        System.err.println("test2");
        DataSource source;
        try {
            source = config.getDataSource();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Datasource failed");
            alert.setHeaderText("Datasource failed");
            alert.setContentText("Datasource failed");
            alert.showAndWait();
            this.getPrimaryStage().setTitle("");
            return;

        }

        System.err.println(source);
        System.err.println("test3");


        List<Submission> samples;
        try {
            samples = config.getSolutions();
        } catch (IOException | SubmissionParseException e) {
            e.printStackTrace();
            this.getPrimaryStage().setTitle("");
            return;
        }

        System.out.println(samples);
        System.err.println("test4");
        List<Solution> sols;
        SQLScript resetScript;
        try {
            resetScript = config.getResetScript();
            sols = Evaluator.createSolutions(config, resetScript, samples, source);
            report.setSolutionMetadata(sols.get(0).getMetaData());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            this.getPrimaryStage().setTitle("");
            return;
        }
        System.err.println(sols);
        System.err.println("test5");

        this.running = new TaskEvaluation(sols, source, resetScript, report, this.getSubmissions(), getPrimaryStage(), runButton);
        Thread t = new Thread(this.running);
        t.setDaemon(true);
        t.start();
        System.err.println("test6");

    }

    public void filter(ActionEvent actionEvent) {
        if (!Objects.isNull(running)) {
            running.cancel();
            running = null;
            return;
        }
        this.running = new TaskFilter(this.subInfos, this.getFilterTerm());
        Thread t = new Thread(this.running);
        t.setDaemon(true);
        t.start();
    }

    private String getFilterTerm() {
        return this.filterTextField.getText();
    }
}
