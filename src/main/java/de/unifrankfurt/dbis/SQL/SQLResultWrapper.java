package de.unifrankfurt.dbis.SQL;


import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Wraps a ResultSet created by SQL statements,
 */
public class SQLResultWrapper {
    private final ArrayList<Object> result;

    public SQLResultWrapper(SQLResultTable table) {
        this.result = new ArrayList<>();
        this.result.add(table);
    }

    public SQLResultWrapper(Integer count) {
        this.result = new ArrayList<>();
        this.result.add(count);
    }

    public SQLResultWrapper(List<Object> list) {

        result = new ArrayList<>(list);
    }


    /**
     * executes sql.
     *
     * @param statement Statement that should execute sql.
     * @param sql       String SQL command
     * @return SQLResultWrapper
     * @throws SQLException if something went wrong
     */
    public static SQLResultWrapper executeStatement(Statement statement, String sql)
            throws SQLException {

        SQLResultWrapperBuilder builder = new SQLResultWrapperBuilder();
        int updateCount;
        boolean e = statement.execute(sql);

        if (e) builder.add(statement.getResultSet()); // first Result is ResultSet
        else {
            updateCount = statement.getUpdateCount();
            if (updateCount == -1) //no first Result
                return builder.build();
            else
                builder.add(updateCount);
        }
        boolean moreResults;
        while ((moreResults = statement.getMoreResults())
                | ((updateCount = statement.getUpdateCount()) != -1)) {
            if (moreResults) {
                builder.add(statement.getResultSet());
            } else {
                builder.add(updateCount);
            }
        }

        return builder.build();
    }


    /**
     * checks if (first) ResultSet returned a table.
     *
     * @return true if returned table is not empty.)
     */
    public boolean isEmpty() {
        return ((SQLResultTable) this.result.get(0)).data.isEmpty();
    }


    /**
     * creates DBFit html for table.
     *
     * @return String DBFit html
     */
    public String convertToDBFitHtml() {
        Object obj = this.result.get(0);
        if (obj instanceof SQLResultTable)
            return ((SQLResultTable) obj).convertToDBFitHtml();
        else return ""; //if count no DBFITHtml should be created
    }

    @Override
    public String toString() {
        if (this.result.isEmpty()) {
            return "no result";
        } else if (this.result.size() == 1) {
            Object obj = this.result.get(0);
            if (obj instanceof SQLResultTable)
                return obj.toString();
            else if (obj instanceof Integer) {
                return "Query OK, " + obj.toString() + " Spalten wurden verändert.";
            } else return "?" + obj.toString();

        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.result.size(); i++) {
                Object obj = this.result.get(i);
                sb.append("Result ")
                        .append(i)
                        .append(":\n")
                        .append(obj);
            }
            return sb.toString();
        }

    }

    /**
     * returns schema as list or null
     *
     * @return
     */
    public List<String> getHeader() {
        if (this.result.get(0) instanceof SQLResultTable) {
            if (this.isEmpty()) return List.of();
            Object obj = this.result.get(0);
            return ((SQLResultTable) obj).header;
        } else return List.of();
    }
}
