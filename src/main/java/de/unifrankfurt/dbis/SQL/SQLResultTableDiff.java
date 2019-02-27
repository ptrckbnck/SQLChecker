package de.unifrankfurt.dbis.SQL;

import java.util.List;

public class SQLResultTableDiff {
    final private List<Integer> linesExpected; //linesExpected of expected
    final private List<Integer> linesActual;
    final private List<Integer> surplus;
    final private List<Integer> missing;
    final private boolean isOk;

    public SQLResultTableDiff(List<Integer> linesExpected, List<Integer> linesActual, List<Integer> surplus, List<Integer> missing, boolean isOk) {
        this.linesExpected = linesExpected;
        this.linesActual = linesActual;
        this.surplus = surplus;
        this.missing = missing;
        this.isOk = isOk;
    }

    public Boolean isOk() {
        return isOk;
    }

    public List<Integer> getLinesExpected() {
        return linesExpected;
    }

    public List<Integer> getLinesActual() {
        return linesActual;
    }

    public List<Integer> getSurplus() {
        return surplus;
    }

    public List<Integer> getMissing() {
        return missing;
    }
}
