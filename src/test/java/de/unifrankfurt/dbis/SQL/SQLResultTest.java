package de.unifrankfurt.dbis.SQL;

import de.unifrankfurt.dbis.config.DataSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

class SQLResultTest {
    private DataSource datasource = new DataSource("localhost", "3306", "airportuser", "airportuser", "airport", false, "+01:00");

    @Test
    void table() {
        SQLResult result = SQLResults.execute(datasource, "Select '1';");
        System.out.println(result);
        System.out.println(((SQLResultTable) result).getType());
    }

    @Test
    void error() {
        SQLResult result = SQLResults.execute(datasource, "asdf;");
        System.out.println(result);
    }

    @Test
    void error2() {
        SQLResult result = SQLResults.execute(datasource, "");
        System.out.println(result);
    }

    @Test
    void update1() throws SQLException {
        try {
            SQLResult result = SQLResults.execute(datasource, "CREATE TABLE TEST_TABLE( ID int);");
            System.out.println(result);
        } finally {
            try (Connection con = datasource.getConnection()) {
                con.createStatement().execute("DROP TABLE IF EXISTS TEST_TABLE;");
            }
        }
    }

    @Test
    void update2() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            con.createStatement().execute("CREATE TABLE TEST_TABLE( ID int);");
            SQLResult result = SQLResults.execute(datasource, "INSERT INTO TEST_TABLE (ID)\n" +
                    "VALUES (1)");
            System.out.println(result);
        } finally {
            try (Connection con = datasource.getConnection()) {
                con.createStatement().execute("DROP TABLE IF EXISTS TEST_TABLE;");
            }
        }
    }
}