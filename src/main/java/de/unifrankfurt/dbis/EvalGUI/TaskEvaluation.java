package de.unifrankfurt.dbis.EvalGUI;

import de.unifrankfurt.dbis.Evaluator;
import de.unifrankfurt.dbis.Inner.Report;
import de.unifrankfurt.dbis.Inner.SQLScript;
import de.unifrankfurt.dbis.Inner.Solution;
import de.unifrankfurt.dbis.Inner.Submission;
import de.unifrankfurt.dbis.config.DataSource;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.List;

public class TaskEvaluation extends Task<Integer> {
    final List<Solution> solutions;
    final DataSource source;
    final SQLScript resetScript;
    final Report report;
    final List<Submission> subs;
    final Stage stage;
    final Button runButton;


    //TODO FIX
    public TaskEvaluation(List<Solution> solutions, DataSource source, SQLScript resetScript, Report report, List<Submission> subs, Stage stage, Button runButton) {
        this.solutions = solutions;
        this.source = source;
        this.resetScript = resetScript;
        this.report = report;
        this.subs = subs;
        this.stage = stage;
        this.runButton = runButton;
    }


    @Override
    protected Integer call() throws Exception {
        int i = 1;
        int count_digits = ((int) Math.log10(subs.size())) + 1;
        for (Submission sub : subs) {
            if (this.isCancelled()) {
                System.out.println("CANCELED");
                Platform.runLater(() -> stage.setTitle(""));
                Platform.runLater(() -> runButton.setText("Run"));
                return 0;
            }
            final int a = i;
            Platform.runLater(() -> stage.setTitle(String.format("Running... (%" + count_digits + "d / %d)", a, subs.size())));
            i++;
            Evaluator.runSubmissionEvaluation(solutions, source, resetScript, sub, report, true, false);
        }
        Platform.runLater(() -> stage.setTitle(""));
        try {
            System.err.println(String.join("\n", report.getCSV()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> runButton.setText("Run"));
        return 0;
    }
}
