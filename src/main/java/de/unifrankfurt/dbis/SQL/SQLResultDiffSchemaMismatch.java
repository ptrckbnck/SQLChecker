package de.unifrankfurt.dbis.SQL;

public class SQLResultDiffSchemaMismatch implements SQLResultDiff {
    private final SQLResultTable expected;
    private final SQLResultTable actual;

    public SQLResultDiffSchemaMismatch(SQLResultTable expected, SQLResultTable actual) {
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
}
