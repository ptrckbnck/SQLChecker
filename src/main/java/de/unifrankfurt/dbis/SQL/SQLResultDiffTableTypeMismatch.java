package de.unifrankfurt.dbis.SQL;

public class SQLResultDiffTableTypeMismatch implements SQLResultDiff {
    private final SQLDataTable expected;
    private final SQLDataTable actual;

    public SQLResultDiffTableTypeMismatch(SQLDataTable expected, SQLDataTable actual) {
        this.expected = expected;
        this.actual = actual;
    }

    @Override
    public String getMessage() {
        return "Dataytype mismatch; Expected " + expected.getType() + " ,but was " + actual.getType();
    }

    @Override
    public Boolean isOk() {
        return false;
    }
}
