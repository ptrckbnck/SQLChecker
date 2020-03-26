package de.unifrankfurt.dbis.SQL;


import java.sql.SQLException;

public class SQLDataFail implements SQLData {

    private final SQLException sqlException;

    public SQLDataFail(SQLException e) {
        this.sqlException = e;
    }

    public SQLException getSqlException() {
        return sqlException;
    }

    @Override
    public String toString() {
        return this.sqlException.toString();
    }

    @Override
    public boolean failed() {
        return true;
    }
}
