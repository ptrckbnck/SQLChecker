package de.unifrankfurt.dbis.SQL;

class SQLResultOK implements SQLResultDiff {
    @Override
    public String getMessage() {
        return "ok";
    }

    @Override
    public Boolean isOk() {
        return true;
    }
}
