package de.unifrankfurt.dbis.Inner;


import de.unifrankfurt.dbis.config.DataSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        String delimiter = Tag.TAG_PREFIX + Tag.STATIC + Tag.TAG_SUFFIX;
        StringBuilder sb = null;
        ArrayList<String> queryList = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                if (line.equals(delimiter)) {
                    if (sb != null) {
                        String query = sb.toString().trim();
                        if (!query.isEmpty()) {
                            queryList.add(query);
                        }
                    }
                    sb = new StringBuilder();
                } else {
                    if (sb == null) {
                        return fromListWithoutStatic(lines); //try non static version
                    } else {
                        sb.append(line).append("\n");
                    }
                }
            }
        }
        if (sb != null) {
            String query = sb.toString().trim();
            if (!query.isEmpty()) {
                queryList.add(query);
            }
        }

        return new SQLScript(queryList);
    }
    private static SQLScript fromListWithoutStatic(List<String> lines) {
        String script = String.join("\n", lines).trim();
        List<String> list = Arrays.asList(script.split("(?<=;)"));
        return new SQLScript(list);
    }


    public void storeInPath(Path path) throws IOException {
        Stream<String> stream = this.queryList.stream().map(
                x -> Tag.TAG_PREFIX + Tag.STATIC + Tag.TAG_SUFFIX + "\n" + x);
        Files.write(
                path,
                stream.collect(Collectors.toList()),
                StandardCharsets.UTF_8);
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
