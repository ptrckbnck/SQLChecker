package de.unifrankfurt.dbis.Inner;

import de.unifrankfurt.dbis.SQL.SQLDataTable;
import de.unifrankfurt.dbis.config.DataSource;
import de.unifrankfurt.dbis.config.EvalConfig;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

class SolutionTest {

    @Test
    public void testZeroYearBehavior() throws Exception {
        EvalConfig config = new EvalConfig("krankenhaus",
                "krankenhaususer",
                "krankenhaususer",
                "localhost",
                "3306",
                "",
                "",
                "",
                false,
                "");
        DataSource ds = config.getDataSource();
        Connection con = ds.getConnection();
        try (con) {
            Statement stmt = con.createStatement();
            String sql = "select name, gebdat, geschlecht from mitarbeiter\n" +
                    "where YEAR(gebdat) < 1900\n" +
                    "union all\n" +
                    "select name, gebdat, geschlecht from patient\n" +
                    "where YEAR(gebdat) < 1900;";
            ResultSet resultSet = stmt.executeQuery(sql);
            SQLDataTable table = SQLDataTable.fromResultSet(resultSet);
            System.out.println(table);
        }
    }
}
