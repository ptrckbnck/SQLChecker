package de.unifrankfurt.dbis.Inner;


import de.unifrankfurt.dbis.config.DataSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *  The class SQLScript is a container for a list of SQL-statements, a script.
 *  It provides a method to run the script via DataSource-object.
 */
public class SQLScript {
    private final List<String> queryList;

    public SQLScript(List<String> queryList) {
        this.queryList = queryList;
    }

    public List<String> getQueryList() {
        return queryList;
    }


    public static SQLScript fromPath(Path resetPath) throws IOException {
        List<String> lines = Files.readAllLines(resetPath, StandardCharsets.UTF_8);
        return fromList(lines);
    }

    private static SQLScript fromList(List<String> lines){
        return fromListWithoutStatic(lines);
    }
    private static SQLScript fromListWithoutStatic(List<String> lines) {
        String script = String.join("\n", lines).trim();
        List<String> list = Arrays.asList(script.split("(?<=;)"));
        return new SQLScript(list);
    }


    public void execute(Statement statement) throws SQLException {
        for (String sql : this.queryList) {
            //System.out.println("run sql: "+sql);
            try {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                throw new SQLException(e.getMessage() + " in :" + sql, e.getSQLState(), e);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLScript SQLScript = (SQLScript) o;
        return Objects.equals(queryList, SQLScript.queryList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(queryList);
    }

    public void execute(DataSource dataSource) throws SQLException {
        try(Connection con = dataSource.getConnection()){
            try(Statement s = con.createStatement()){
                this.execute(s);
            }
        }
    }

}
