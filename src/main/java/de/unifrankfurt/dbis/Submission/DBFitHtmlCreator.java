package de.unifrankfurt.dbis.Submission;


import de.unifrankfurt.dbis.SQL.SQLResultTable;
import de.unifrankfurt.dbis.SQL.SQLResultWrapper;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
    public static String htmlCreateNonCallable(TaskSQLNonCallable task, Statement statement) throws SQLException {
        {
            StringBuilder html = new StringBuilder();
            String tableHtml = "";

            if (task.errorExpected()) html.append("<!--error-->\n");
            else {
                SQLResultWrapper result = SQLResultWrapper.executeStatement(statement, task.getCommands().get(0));
                tableHtml = result.convertToDBFitHtml();
            }


            String code = (task.isStatic()) ?
                    task.getCommands().get(0) :
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


    /**
     * creates DBFit HTML for given TaskSQLFunction.
     *
     * @param task      original TaskSQLFunction
     * @param statement Statement for executing mysql code
     * @return String DBFit-Html
     * @throws SQLException if something went wrong with database connection.
     */
    public static String htmlCreateFunction(TaskSQLFunction task, Statement statement) throws SQLException {
        StringBuilder html = new StringBuilder();
        String tagName = task.getTag().getName();

        for (String q : task.getCommands()) {
            // generatePairs a plan with SET and SELECT statements
            ArrayList<String> queries = new ArrayList<>();

            // stored function calls
            for (String sql : q.split("\n")) {
                queries.add("{ ? = call " + sql + " }");
            }


            String tableHtml = "";
            if (task.errorExpected()) html.append("\n<!--error-->\n");
            else {
                boolean hasRes = false;
                for (String aPlanTmp : queries) {
                    hasRes = statement.execute(aPlanTmp);
                }
                if (hasRes)
                    tableHtml = SQLResultTable.fromResultSet(statement.getResultSet()).convertToDBFitHtml();
                else
                    throw new SQLException("[Callable] " + tagName + " > " + statement.getUpdateCount() + " rows affected!"); //TODO create exception
            }

            String code = (task.isStatic()) ?
                    q :
                    task.getTag().serialized();


            html.append("\n\n<table>")
                    .append("\n\t<tr>")
                    .append("\n\t\t<td>Query</td>")
                    .append("\n\t\t<td>SELECT ").append(code).append("</td>")
                    .append("\n\t</tr>")
                    .append(tableHtml)
                    .append("\n</table>\n\n");
        }

        html.append("\n");

        return html.toString();
    }


    /**
     * creates DbFit Html for given procedure.
     * proceed with care using this function. I did not write it and hope i do not have to rewrite it. Patrick
     *
     * @param task
     * @param statement
     * @return
     * @throws SQLException
     */
    public static String htmlCreateProcedure(TaskSQLProcedure task, Statement statement) throws SQLException {
        //logger.info("Callable");
        // it is a callable statement

        SQLCallable callable = new SQLCallable(task.getCodeString());
        String[] headerCols = callable.generateResultHeader();

        StringBuilder html = new StringBuilder();
        for (String q : task.getCommands()) {
            // generatePairs a plan with SET and SELECT statements
            ArrayList<String> queries = new ArrayList<>();

            // stored procedure calls
            if (!callable.hasOutParameter()) {
                for (String sql : q.split("\n")) {
                    String newSQL = "{ call " + sql + " }";
                    queries.add(newSQL);
                }
            } else {
                // procedure with OUT or INOUT parameters
                queries.addAll(callable.prepareInOutCall(q));
            }

            // Function/procedure and SET/SELECT calls
            boolean hasRes = false;
            for (String aPlanTmp : queries) {
                if (task.errorExpected()) {
                    hasRes = statement.execute(aPlanTmp);
                }
            }

            // Fetch result! The last statement is always the one
            // which produces the final result
            SQLResultTable rs;
            if (hasRes) {
                rs = SQLResultTable.fromResultSet(statement.getResultSet());
            } else {
                throw new SQLException("[Callable] " + task.getTag().getName() + " > " + statement.getUpdateCount() + " rows affected!");
            }

            // Build result
            // One Callable = 1 line
            // Contains a dump of the result (1st line is headers)
            // The calls might be split up in multiple test cases
            //logger.info("QueryPipeline generateHTML");
            StringBuilder html1 = new StringBuilder();
            boolean isStatic = task.isStatic();

            // all the arguments of the call
            String[] data = SQLCallable.parseCallData(q);
            // Stored Procedure
            if ((!callable.isOutOrInout())) {
                if (rs.isEmpty()) {
                    if (isStatic) {
                        html1.append("\n\n<table>" + "\n\t<tr>" + "\n\t\t<td>Execute</td>" + "\n\t\t<td>call ").append(q).append("</td>").append("\n\t</tr>");
                    } else {
                        html1.append("\n\n<table>" + "\n\t<tr>" + "\n\t\t<td>Execute</td>" + "\n\t\t<td>call ").append(Tag.TAG_PREFIX).append(task.getCommands()).append(Tag.TAG_SUFFIX).append("</td>").append("\n\t</tr>");
                    }

                } else {
                    if (isStatic) {
                        html1.append("\n\n<table>" + "\n\t<tr>" + "\n\t\t<td>Query</td>" + "\n\t\t<td>call ").append(q).append("</td>").append("\n\t</tr>");
                    } else {
                        html1.append("\n\n<table>" + "\n\t<tr>" + "\n\t\t<td>Query</td>" + "\n\t\t<td>call ").append(Tag.TAG_PREFIX).append(task.getCommands()).append(Tag.TAG_SUFFIX).append("</td>").append("\n\t</tr>");
                    }


                    // There might be some result
                    // If there is no result then the following loop will
                    // obviously not do anything
                    html1.append(rs.convertToDBFitHtml()); // todo maybe true
                }
                html1.append("\n</table>\n");
            } else {
                // Print the call
                if (isStatic)
                    html1.append("\n\n<table>" + "\n\t<tr>" + "\n\t\t<td>Execute Procedure</td>" + "\n\t\t<td>").append(callable.getName()).append("</td>").append("\n\t</tr>");
                else
                    html1.append("\n\n<table>" + "\n\t<tr>" + "\n\t\t<td>Execute Procedure</td>" + "\n\t\t<td>").append(Tag.TAG_PREFIX).append(task.getCommands()).append(Tag.TAG_SUFFIX).append("</td>").append("\n\t</tr>");

                html1.append("\n\t<tr>");
                for (String aHeader : headerCols) {
                    String h = aHeader;
                    if (h.startsWith("@")) {
                        // convert mysql syntax to dbfit syntax
                        // @val => val?
                        h = h.substring(1) + "?";
                    }
                    html1.append("\n\t\t<td>").append(h).append("</td>");
                    // html += h + " |";
                }
                html1.append("\n\t</tr>");
                // html += "\n";

                // Print the results
                // the sp/function has at least one out or inout parameter
                // IF THERE IS A OUT/INOT PARAMETER THEN THERE IS ALSO SOME OUTPUT
                int imark = 0; // the current position in the input data array
                int omark = 0; // the current position in the output list
                // start at pos 1 because the first line is the table header
                for (int j = 0; j < rs.size(); j++) {
                    // for each line...
                    html1.append("\n\t<tr>");
                    // print row while looking at header structure
                    for (String h : headerCols) {
                        if (h.startsWith("@")) {

                            String resTmp = rs.getData().get(j).get(omark);
                            // remove leading/trailing " and '
                            if (resTmp.charAt(0) == resTmp.charAt(resTmp.length() - 1)) {
                                if ((resTmp.charAt(0) == '\'') || (resTmp.charAt(0) == '\"'))
                                    resTmp = resTmp.substring(1, resTmp.length() - 1);
                            }
                            html1.append("\n\t\t<td>").append(resTmp).append("</td>");
                            omark++;
                        } else {
                            String dataTmp = data[imark];
                            // remove leading/trailing " and '
                            if (dataTmp.charAt(0) == dataTmp.charAt(dataTmp.length() - 1)) {
                                if ((dataTmp.charAt(0) == '\'') || (dataTmp.charAt(0) == '\"'))
                                    dataTmp = dataTmp.substring(1, dataTmp.length() - 1);
                            }
                            System.out.println("data imark" + data[imark]);
                            // html += data[imark];
                            html1.append("\n\t\t<td>").append(dataTmp).append("</td>");
                            imark++;
                        }
                        // html += " | ";
                    }
                    // html += "\n";
                    html1.append("\n\t</tr>");
                }

                html1.append("\n</table>\n");
            }
            html1.append("\n");
            html.append(html1.toString());
        }

        // Add a blank line between every test case
        html.append("\n");

        return html.toString();
    }

}
