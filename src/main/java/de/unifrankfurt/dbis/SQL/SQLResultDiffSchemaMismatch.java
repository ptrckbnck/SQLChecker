package de.unifrankfurt.dbis.SQL;

public class SQLResultDiffSchemaMismatch implements SQLResultDiff {
    private final SQLDataTable expected;
    private final SQLDataTable actual;

    public SQLResultDiffSchemaMismatch(SQLDataTable expected, SQLDataTable actual) {
        this.expected = expected;
        this.actual = actual;
    }

    @Override
    public String getMessage() {
        return "Schema mismatch; Expected " + expected.getHeader() + ", but was " + actual.getHeader();
    }

    @Override
    public Boolean isOk() {
        return false;
    }

    @Override
    public String getMinimalMessage() {
        return "Schema mismatch: was " + actual.getHeader();
    }
}
