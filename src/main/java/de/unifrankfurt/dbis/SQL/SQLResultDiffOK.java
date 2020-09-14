package de.unifrankfurt.dbis.SQL;

class SQLResultDiffOK implements SQLResultDiff {
    @Override
    public String getMessage() {
        return "ok";
    }

    @Override
    public Boolean isOk() {
        return true;
    }

    @Override
    public String getMinimalMessage() {
        return "";
    }
}
