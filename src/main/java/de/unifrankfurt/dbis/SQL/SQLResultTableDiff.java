package de.unifrankfurt.dbis.SQL;

import java.util.List;

public class SQLResultTableDiff {
    final private List<Integer> expected;
    final private List<Integer> actual;
    final private boolean isOk;

    public SQLResultTableDiff(List<Integer> expected, List<Integer> actual) {
        this.expected = expected;
        this.actual = actual;
        this.isOk = false;
    }

    public SQLResultTableDiff() {
        this.isOk = true;
        this.expected = null;
        this.actual = null;
    }

    public Boolean isOk() {
        return isOk;
    }


    public List<Integer> getExpected() {
        return expected;
    }

    public List<Integer> getActual() {
        return actual;
    }

    @Override
    public String toString() {
        return "SQLResultTableDiff{" +
                "expected=" + expected +
                ", actual=" + actual +
                ", isOk=" + isOk +
                '}';
    }
}
