package de.unifrankfurt.dbis.EvalGUI;

import de.unifrankfurt.dbis.Inner.SubmissionInfo;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.List;

public class FilterHistory implements Observable {

    List<List<SubmissionInfo>> info;

    @Override
    public void addListener(InvalidationListener invalidationListener) {

    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }
}
