package de.unifrankfurt.dbis.EvalGUI;

import de.unifrankfurt.dbis.Inner.BaseInfo;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.List;

public class FilterHistory implements Observable {

    List<List<BaseInfo>> info;

    @Override
    public void addListener(InvalidationListener invalidationListener) {

    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }
}
