package de.unifrankfurt.dbis.SQL;

public class SQLDataUpdate implements SQLData {
    private final int updateCount;

    public SQLDataUpdate(int updateCount) {
        this.updateCount = updateCount;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    @Override
    public String toString() {
        return "Query OK, " + updateCount + " rows affected";
    }

    @Override
    public boolean failed() {
        return false;
    }
}
