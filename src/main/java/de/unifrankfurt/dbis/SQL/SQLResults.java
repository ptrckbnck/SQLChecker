package de.unifrankfurt.dbis.SQL;

import de.unifrankfurt.dbis.config.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class SQLResults {

    public static SQLData execute(DataSource source, String sql) {
        try (Connection con = source.getConnection()) {
            Statement stmt = con.createStatement();
            boolean e = stmt.execute(sql);
            int updateCount;
            SQLData result;
            if (e) {
                ResultSet resultSet = stmt.getResultSet(); // first Result is ResultSet
                result = SQLDataTable.fromResultSet(resultSet);
            } else {
                updateCount = stmt.getUpdateCount();
                if (updateCount == -1) { //no first Result
                    System.err.println("WARNING: Executing '" + sql + "' generated no Resultset");
                    result = new SQLDataFail(null);
                } else {
                    result = new SQLDataUpdate(updateCount);
                }
            }
            if (!((!stmt.getMoreResults()) && (stmt.getUpdateCount() == -1))) {
                System.err.println("WARNING: Executing '" + sql + "' generated multiple Resultsets");
            }
            return result;

        } catch (SQLException e) {
            return new SQLDataFail(e);
        }

    }
}

