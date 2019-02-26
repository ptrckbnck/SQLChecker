package de.unifrankfurt.dbis.SQL;

public class SQLResultDiffTypeMismatch implements SQLResultDiff {

    private final SQLResult expected;
    private final SQLResult actual;

    public SQLResultDiffTypeMismatch(SQLResult expected, SQLResult actual) {
        this.expected = expected;
        this.actual = actual;
    }

    @Override
    public String getMessage() {
        return "Type mismatch. Expected: " + expected.getClass() + ", but was: " + actual.getClass();
    }

    @Override
    public Boolean isOk() {
        return false;
    }
}
