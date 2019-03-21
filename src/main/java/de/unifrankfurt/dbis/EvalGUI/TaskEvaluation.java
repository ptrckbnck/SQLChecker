package de.unifrankfurt.dbis.EvalGUI;

import de.unifrankfurt.dbis.Evaluator;
import de.unifrankfurt.dbis.Inner.Base;
import de.unifrankfurt.dbis.Inner.Report;
import de.unifrankfurt.dbis.Inner.SQLScript;
import de.unifrankfurt.dbis.Inner.Solution;
import de.unifrankfurt.dbis.config.DataSource;
import de.unifrankfurt.dbis.config.EvalConfig;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class TaskEvaluation extends Task<Integer> {
    private final Report report;
    private final List<Base> subs;
    private final Stage stage;
    private final EvalConfig config;
    private final PrintStream out;
    private final Path csvOut;


    public TaskEvaluation(EvalConfig config, Report report, List<Base> subs, Stage stage, Button runButton, PrintStream out, Path csvOut) {
        this.report = report;
        this.subs = subs;
        this.stage = stage;
        this.config = config;
        this.out = out;
        this.csvOut = csvOut;
        this.setOnCancelled((x) -> {
            doCancel();
            stage.setTitle("");
            runButton.setText("Run");
        });
        this.setOnFailed((x) -> {
            stage.setTitle("");
            runButton.setText("Run");
        });
        this.setOnSucceeded((x) -> {
            stage.setTitle("");
            runButton.setText("Run");
        });

        System.err.println(config);
    }

    private void doCancel() {
        System.out.println("Cancelled");
    }


    private void alert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error during Evaluation");
        alert.setContentText(msg);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    @Override
    protected Integer call() throws Exception {

        DataSource source;
        try {
            source = config.getDataSource();
        } catch (Exception e) {
            Platform.runLater(() -> {
                alert("creating Datasource failed:\n" + e.getMessage());
            });
            return 0;
        }
        if (this.isCancelled()) {
            return 0;
        }
        List<Base> samples;
        try {
            samples = config.getSolutions();
        } catch (IOException e) {
            Platform.runLater(() -> {
                alert("loading Solutions failed:\n" + e.getMessage());
            });
            return 0;
        }

        if (this.isCancelled()) {
            return 0;
        }

        List<Solution> sols;
        SQLScript resetScript;
        try {
            resetScript = config.getResetScript();
            sols = Evaluator.createSolutions(config, resetScript, samples, source);
            report.setSolutionMetadata(sols.get(0).getMetaData());
        } catch (SQLException | IOException e) {
            Platform.runLater(() -> {
                alert("running ResetScript failed:\n" + e.getMessage());
            });
            return 0;
        }

        if (this.isCancelled()) {
            return 0;
        }


        int i = 1;
        int count_digits = ((int) Math.log10(subs.size())) + 1;
        for (Base sub : subs) {
            if (this.isCancelled()) {
                return 0;
            }
            final int a = i;
            Platform.runLater(() -> stage.setTitle(String.format("Running... (%" + count_digits + "d / %d)", a, subs.size())));
            i++;
            Evaluator.runSubmissionEvaluation(sols, source, resetScript, sub, report, true, false);
        }
        Platform.runLater(() -> stage.setTitle(""));
        try {
            if (Objects.isNull(csvOut)) {
                report.getCSV().forEach(out::println);
            } else {
                Files.write(csvOut, report.getCSV());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


}
