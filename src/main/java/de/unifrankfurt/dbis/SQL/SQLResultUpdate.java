package de.unifrankfurt.dbis.SQL;

public class SQLResultUpdate implements SQLResult {
    private final int updateCount;

    public SQLResultUpdate(int updateCount) {
        this.updateCount = updateCount;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    @Override
    public String toString() {
        return "Query OK, " + updateCount + " rows affected";
    }

}
