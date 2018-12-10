package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.SQL.SQLResultWrapper;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Base class for every Task that wants to utilize DBFit.
 */
public abstract class TaskSQL implements Task {
    /**
     * the Tag that identifies this Task
     */
    private final Tag tag;

    /**
     * SQL readable commentary.
     */
    private final String comment;

    /**
     * List of SQL commands. DBFit only supports onliner.
     * So length of commands should usual be one.
     */
    private final List<String> commands;


    public TaskSQL(Tag tag, String comment, List<String> commands) {
        this.tag = tag;
        this.comment = comment;
        this.commands = commands;
    }


    @Override
    public Tag getTag() {
        return tag;
    }

    @Override
    public void runSQL(Statement statement) throws SQLException {
        execute(statement);
    }

    @Override
    public List<SQLResultWrapper> executeSQL(Statement statement) throws SQLException {
        List<SQLResultWrapper> list = new ArrayList<>();
        for (String sql : commands) {
            list.add(SQLResultWrapper.executeStatement(statement, sql));
        }
        return list;
    }


    public String getComment() {
        return comment;
    }

    public List<String> getCommands() {
        return commands;
    }


    /**
     * Concatenation of Commentary and every SQL commands, separatedby "\n".
     *
     * @return String
     */
    public String getCodeString() {
        String sb = "";
        if (!this.comment.trim().isEmpty())
            sb = "/* " + this.comment + " */\n";
        sb += String.join("\n", commands);
        return sb;
    }


    /**
     * execudes sql of task in given statement.
     *
     * @param statement statement which should be executed
     * @throws SQLException see Statement.execute()
     */
    private void execute(Statement statement) throws SQLException {
        for (String sql : commands) statement.execute(sql);
    }


    /**
     * @param sql commands
     * @return String List of sql statements
     */
    private static List<String> splitStatements(String sql) { //TODO ; in Kommentaren ignorieren
        String[] code = sql.split("(?<=;)");
        List<String> code_filtered = new ArrayList<>();
        for (String statement : code) {
            String trimmed = statement.trim();
            if (!"".equals(trimmed)) code_filtered.add(trimmed);
        }
        return code_filtered;
    }


    /**
     * Parses a SubmissionToken and creates TaskSQl accordingly.
     *
     * @param token Submission Token that should be parsed.
     * @return TaskSQL
     * @throws SubmissionParseException if Token could not be parsed successfully.
     */
    public static TaskSQL parseToken(SubmissionToken token) throws SubmissionParseException {
        TaskBody body = SubmissionParser.splitBody(token.getBody());
        //TODO splitBody fehlerhaft, rest temporärer entfernt um Fehler zu vermeiden.

        //List<String> statements = splitStatements(body.getSql()); //TODO splitStatements auch fehlerhaft, ';' ist in Kommentaren erlaubts
        List<String> statements = List.of(body.getSql());

        if (statements.size() < 1)
            return new TaskSQLNonCallable(token.getTag(), body.getComment() + "\nNO BODY", "NO BODY");
        String sql = statements.get(0);
        //if (!isCallable(sql)) {
        if (statements.size() != 1) {
            return new TaskSQLNonCallable(token.getTag(), body.getComment() + "\nTOO MANY STATEMENTS", "TOO MANY STATEMENTS");
        }

            /*
            throw new SubmissionParseException(
                    "too many Statements in " + token.getTag().getName(),
                    SubmissionParseException.TOO_MANY_STATEMENTS);*/

        return new TaskSQLNonCallable(token.getTag(), body.getComment(), body.getSql());


        // does not currently support procedures or functions
        /*
        if (isProcedure(sql)) {
            return new TaskSQLProcedure(token.getTag(), body.getComment(), statements);
        }
        if (isFunction(sql)) {
            return new TaskSQLFunction(token.getTag(), body.getComment(), statements);
        }*/
        //throw new SubmissionParseException("Unkown Task: " + token.toString(), SubmissionParseException.UNKNOWN_TASKSQL);
    }

    /**
     * checks if commands starts with "select".
     *
     * @return "Query" if true, otherwise "Execute"
     */
    public String getDBFitCommand() {
        if (commands.get(0).toLowerCase().startsWith("select")) {
            return "Query";
        } else {
            return "Execute";
        }
    }

    /**
     * TaskSQL is static if Tag.name is "static.error" or "static".
     *
     * @return return true if static.
     */
    public boolean isStatic() {
        return tag.getName().equals("static.error") || tag.getName().equals("static");
    }

    /**
     * TaskSQL should not execute successfully if an error is expected.
     *
     * @return return true if Task.name is "static.error";
     */
    public boolean errorExpected() {
        return getTag().getName().equals("static.error");
    }


    /**
     * TODO write tests
     * @param sql
     * @return
     */
    public static CallableType getCallableType(String sql) {

        sql = sql.toLowerCase();
        String sqlHead = sql.substring(0, sql.indexOf("(")).trim();
        String[] sqlHeadTokens = sqlHead.split(" ");
        boolean isFunction = sqlHeadTokens[sqlHeadTokens.length - 2].equals("function");
        boolean isProcedure = sqlHeadTokens[sqlHeadTokens.length - 2].equals("procedure");

        CallableType status;

        if (isFunction) {
            status = CallableType.Function;
        } else if (isProcedure) {
            status = CallableType.Procedure;
        } else {
            status = CallableType.Other;
        }
        return status;
    }
    /**
     * Checks if SQl code has special function notation.
     *
     * @param sql Code that should be checked.
     * @return true if that is the case.
     */
    private static boolean isFunction(String sql) {
        return isCallable(sql) && getCallableType(sql)==CallableType.Function;
    }


    /**
     * Checks if SQl code has special procedure notation.
     *
     * @param sql Code that should be checked.
     * @return true if that is the case.
     */
    private static boolean isProcedure(String sql) {

        return isCallable(sql) && getCallableType(sql)==CallableType.Procedure;
    }

    /**
     * Checks if SQl code has special callable notation.
     *
     * @param sql Code that should be checked.
     * @return true if that is the case.
     */
    private static boolean isCallable(String sql) {
        //logger.info("QueryPipeline isCallable");

        // 1. test : Every callable has to contain an opening and closing bracket
        if (!sql.contains("(") || !sql.contains(")")) {
            return false;
        }

        // 2. test : A callable does not start with specific keywords

        String sqlLower = sql.toLowerCase();
        return !sqlLower.startsWith("create ") &&
                !sqlLower.startsWith("delete ") &&
                !sqlLower.startsWith("drop ") &&
                !sqlLower.startsWith("insert into ") &&
                !sqlLower.startsWith("insert ignore into ") &&
                !sqlLower.startsWith("select ") &&
                !sqlLower.startsWith("update ") &&
                !sqlLower.startsWith("alter ");
    }


    /**
     * creates DBFitHtml code needed for testing.
     *
     * @param statement Statement that connects to database.
     * @return String DBFitHtml code
     * @throws SQLException if problems with database connection occurred.
     */
    public abstract String generateDBFitHtml(Statement statement) throws SQLException;

    public String serialize() {
        return this.tag.serialized() + "\n" + this.getCodeString();
    }

    public String toString() {
        return "{" + this.tag.serialized() + ", /* " + this.comment + " */, " + commands + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskSQL)) return false;
        TaskSQL taskSQL = (TaskSQL) o;
        return Objects.equals(tag, taskSQL.tag) &&
                Objects.equals(comment, taskSQL.comment) &&
                Objects.equals(commands, taskSQL.commands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, comment, commands);
    }

}
