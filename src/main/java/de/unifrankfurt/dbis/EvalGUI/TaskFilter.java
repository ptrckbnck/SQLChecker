package de.unifrankfurt.dbis.EvalGUI;

import de.unifrankfurt.dbis.Inner.Base;
import de.unifrankfurt.dbis.Inner.SubmissionInfo;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskFilter extends Task<Integer> {
    private final ObservableList<SubmissionInfo> observableList;
    private final String filterTerm;
    private final ObservableList<List<SubmissionInfo>> history;

    public TaskFilter(ObservableList<SubmissionInfo> observableList, ObservableList<List<SubmissionInfo>> history, String filterTerm) {
        this.observableList = observableList;
        this.filterTerm = filterTerm;
        this.history = history;
    }


    @Override
    protected Integer call() throws Exception {
        List<SubmissionInfo> newSubs = new ArrayList<>();
        for (SubmissionInfo info : observableList) {
            final Base base = info.getBase();
            if (!Objects.isNull(base) && base.serialize().contains(filterTerm)) {
                newSubs.add(info);
            }
        }
        if (newSubs.size() == observableList.size()) return 0;
        Platform.runLater(() -> {
            history.add(new ArrayList<>(observableList));
            observableList.setAll(newSubs);
        });

        return 0;
    }
}
