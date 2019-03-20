package de.unifrankfurt.dbis.EvalGUI;

import de.unifrankfurt.dbis.Inner.Base;
import de.unifrankfurt.dbis.Inner.BaseInfo;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TaskFilter extends Task<Integer> {
    private final ObservableList<BaseInfo> observableList;
    private final String filterTerm;
    private final ObservableList<List<BaseInfo>> history;
    private final boolean regex;

    public TaskFilter(ObservableList<BaseInfo> observableList, ObservableList<List<BaseInfo>> history, String filterTerm, boolean regex) {
        this.observableList = observableList;
        this.filterTerm = filterTerm;
        this.history = history;
        this.regex = regex;
    }


    @Override
    protected Integer call() throws Exception {
        List<BaseInfo> newSubs = new ArrayList<>();
        BiFunction<String, String, Boolean> filter;
        if (regex) {
            try {
                Pattern.compile(filterTerm);
            } catch (PatternSyntaxException exception) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("invalid regular expression");
                    alert.showAndWait();
                });
                return 0;
            }
            filter = String::matches;
        } else {
            filter = String::contains;
        }
        for (BaseInfo info : observableList) {
            final Base base = info.getBase();
            if (Objects.isNull(base)) continue;
            String text = base.serialize();
            if (filter.apply(text, filterTerm)) {
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
