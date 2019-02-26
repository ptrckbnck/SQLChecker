package de.unifrankfurt.dbis.SQL;

public class SQLResultDiffException implements SQLResultDiff {
    private final Exception e;

    public SQLResultDiffException(Exception e) {
        this.e = e;
    }

    @Override
    public String getMessage() {
        return "Exception occured: " + e.getMessage();
    }

    @Override
    public Boolean isOk() {
        return false;
    }
}
