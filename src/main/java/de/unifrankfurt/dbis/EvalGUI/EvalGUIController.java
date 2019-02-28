package de.unifrankfurt.dbis.EvalGUI;

import de.unifrankfurt.dbis.Evaluator;
import de.unifrankfurt.dbis.Inner.*;
import de.unifrankfurt.dbis.config.EvalConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
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
    private List<Submission> subs;
    private Report report;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.err.println("TEST");
        initTable();

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

    public ObservableList<SubmissionInfo> getOList() {
        return FXCollections.observableList(List.of(new SubmissionInfo(Paths.get("root"),
                        "name",
                        StandardCharsets.UTF_8,
                        List.of(new Student("name", "mail", "matnr")),
                        Boolean.TRUE),
                new SubmissionInfo(null,
                        null,
                        null,
                        null,
                        Boolean.FALSE)
                )
        );
    }

    private Path getResetScriptPath() {
        return Paths.get(this.resetScriptPathTextField.getText());
    }

    private Window getPrimaryStage() {
        return EvalGUIApp.getPrimaryStage();
    }

    public void loadSubmissionsPath(ActionEvent actionEvent) {
        System.err.println("TEST3");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Submissions Path");
        /*if (!Objects.isNull(getResetScriptPath())) {
            directoryChooser.setInitialDirectory(getSubmissionsPath().toFile());
        }*/
        File file = directoryChooser.showDialog(getPrimaryStage());
        System.err.println("Test4");
        if (Objects.isNull(file)) {
            return;
        }
        System.err.println("SCHLUBBA");
        this.submissionsPathTextField.setText(file.getPath());
    }

    private Path getSubmissionsPath() {
        return Paths.get(this.submissionsPathTextField.getText());
    }

    public void loadSolutionsPath(ActionEvent actionEvent) {
        this.solutionPathTextField.setText("/home/xyntek/Dropbox/SQLChecker/NEU_TESTS/Blatt4_solution_Eval_new.sql");
    }

    public void loadSubmissions(ActionEvent actionEvent) {
        report = new Report();
        subs = Evaluator.loadSubmissions(getSubmissionsPath(), report, submission -> true);
        List<SubmissionInfo> infos = subs.stream().map(Submission::getSubmissionInfo).collect(Collectors.toList());
        report.getResultStorages()
                .stream()
                .map(ResultStorage::getSubmissionInfo)
                .forEach(infos::add);
        this.submissionTable.setItems(FXCollections.observableList(infos));
    }

    public void loadResetScriptPath(ActionEvent actionEvent) {
        this.resetScriptPathTextField.setText("/home/xyntek/Dropbox/SQLChecker/NEU_TESTS/Blatt4_reset_Eval.sql"); //TODO
    }

    public void run(ActionEvent actionEvent) throws IOException, SQLException {
        EvalConfig config = new EvalConfig(this.databaseTextField.getText(), this.usernameTextField.getText(), this.passwordTextField.getText(), this.hostTextField.getText(), this.portTextField.getText(), this.resetScriptPathTextField.getText(), this.solutionPathTextField.getText(), this.submissionsPathTextField.getText());
        //TODO hier weiter
        Evaluator.runEachEvaluation(Evaluator.createSolutions(config, config.getResetScript(), this.subs, config.getDataSource()), config.getDataSource(), config.getResetScript(), true, false, report, this.subs);
    }
}
