package de.unifrankfurt.dbis.SQL;


import java.sql.SQLException;

public class SQLResultFail implements SQLResult {

    private final SQLException sqlException;

    public SQLResultFail(SQLException e) {
        this.sqlException = e;
    }

    public SQLException getSqlException() {
        return sqlException;
    }

    @Override
    public String toString() {
        return this.sqlException.toString();
    }
}
