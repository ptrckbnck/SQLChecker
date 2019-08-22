package de.unifrankfurt.dbis.SQL;

import java.util.List;
import java.util.StringJoiner;

public class SQLResultTableDiff {
    final private List<Integer> linesExpected; //linesExpected of expected
    final private List<Integer> linesActual;
    final private List<Integer> surplus;
    final private List<Integer> missing;
    final private boolean isOk; // True if actual and expected match
    final private boolean isValid; // True if actual and expected can be diffed.
    // Currently can only be False if Schema do not match.

    public SQLResultTableDiff(List<Integer> linesExpected, List<Integer> linesActual, List<Integer> surplus, List<Integer> missing, boolean isOk, boolean isValid) {
        this.linesExpected = linesExpected;
        this.linesActual = linesActual;
        this.surplus = surplus;
        this.missing = missing;
        this.isOk = isOk;
        this.isValid = isValid;
    }

    public SQLResultTableDiff(List<Integer> linesExpected, List<Integer> linesActual, List<Integer> surplus, List<Integer> missing, boolean isOk) {
        this.linesExpected = linesExpected;
        this.linesActual = linesActual;
        this.surplus = surplus;
        this.missing = missing;
        this.isOk = isOk;
        this.isValid = true;
    }

    public static SQLResultTableDiff newInvalid() {
        return new SQLResultTableDiff(List.of(),
                List.of(),
                List.of(),
                List.of(),
                false,
                false);
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

    public boolean isValid() {
        return isValid;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", SQLResultTableDiff.class.getSimpleName() + "[", "]")
                .add("linesExpected=" + linesExpected)
                .add("linesActual=" + linesActual)
                .add("surplus=" + surplus)
                .add("missing=" + missing)
                .add("isOk=" + isOk)
                .add("isValid=" + isValid)
                .toString();
    }
}
