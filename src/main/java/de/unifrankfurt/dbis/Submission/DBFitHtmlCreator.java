package de.unifrankfurt.dbis.Submission;


import de.unifrankfurt.dbis.SQL.SQLResultWrapper;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * provides multiple functions for creating DBFIT Html.
 */
abstract public class DBFitHtmlCreator {

    /**
     * creates DBFIT Html for an "normal" TaskSQL. For example one containing an SQL statement.
     *
     * @param task      TaskSQLNonCallable
     * @param statement Statement for interacting with database.
     * @return String DBFit-Html
     * @throws SQLException if something went wrong with database connection.
     */
    public static String htmlCreateNonCallable(TaskNonCallable task, Statement statement) throws SQLException {
        {
            StringBuilder html = new StringBuilder();
            String tableHtml;

            SQLResultWrapper result = SQLResultWrapper.executeStatement(statement, task.getSql());
            tableHtml = result.convertToDBFitHtml();


            String code = (task.isStatic()) ?
                    task.getSql() :
                    Tag.TAG_PREFIX + task.getTag().getName() + Tag.TAG_SUFFIX;

            html.append("<table>\n")
                    .append("\t<tr>\n")
                    .append("\t\t<td>").append(task.getDBFitCommand()).append("</td>\n")
                    .append("\t\t<td>").append(code).append("</td>\n")
                    .append("\t</tr>\n")
                    .append(tableHtml)
                    .append("</table>\n\n");
            return html.toString();
        }
    }
}
