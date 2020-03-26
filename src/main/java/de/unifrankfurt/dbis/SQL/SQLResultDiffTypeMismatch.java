package de.unifrankfurt.dbis.SQL;

public class SQLResultDiffTypeMismatch implements SQLResultDiff {

    private final SQLData expected;
    private final SQLData actual;

    public SQLResultDiffTypeMismatch(SQLData expected, SQLData actual) {
        this.expected = expected;
        this.actual = actual;
    }

    @Override
    public String getMessage() {
        if (!expected.failed() && actual.failed()) {
            return "Failed: " + actual.toString();
        } else {
            return "Type mismatch. Expected: " + expected.getClass().getSimpleName() + ", but was: " + actual.getClass().getSimpleName();
        }
    }

    @Override
    public Boolean isOk() {
        return false;
    }
}
