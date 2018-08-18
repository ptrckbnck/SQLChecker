package de.unifrankfurt.dbis.DBFit;

import de.unifrankfurt.dbis.config.XConfig;
import de.unifrankfurt.dbis.Submission.Solution;
import de.unifrankfurt.dbis.Submission.Submission;
import de.unifrankfurt.dbis.Submission.TaskSQL;
import fit.Parse;
import fit.exception.FitParseException;

import java.io.PrintStream;
import java.sql.SQLException;

public class MySQLTestWrapper {

    //private static Logger logger = LogManager.getLogger(MySQLTestWrapper.class.getName());

    private final CustomMySQLTest mySqlTest;
    private String host;
    private String user;
    private String password;
    private String db;


    public MySQLTestWrapper() {
        this.mySqlTest = new CustomMySQLTest();
        this.host = null;
        this.user = null;
        this.password = null;
        this.db = null;
    }

    /**
     * Establish a connection to the database so that this connection can be used by DBFit
     *
     * @param host     - Host, for example localhost
     * @param user     - Username
     * @param password - password
     * @param db       - sql-database
     * @return true if connection could be established
     */
    public boolean connect(String host, String user, String password, String db) throws SQLException {
        try {
            mySqlTest.connect(host, user, password, db);
        } catch (SQLException e) {
            throw e;
        }
        boolean success = mySqlTest.isConnected();
        if (!success) return false;
        this.host = host;
        this.user = user;
        this.password = password;
        this.db = db;
        return true;
    }

    public boolean reconnect() throws SQLException {
        return this.host != null
                && this.connect(this.host, this.user, this.password, this.db);
    }

    public void close() throws SQLException {
        this.mySqlTest.close();
    }


    /**
     * this evaluates the DBFit solution of a given parser
     *
     * @param err       PrintStream for intercepting DBFit error messages
     * @param fitParser a Parser object
     */
    public void doTables(PrintStream err, Parse fitParser) {

        //intercept dbfit err
        PrintStream defaultErr = System.err;
        System.setErr(err);

        this.mySqlTest.doTables(fitParser);

        System.err.flush();
        System.setErr(defaultErr);
    }

    /**
     * this evaluates the DBFit solution of a given parser
     *
     * @param fitParser a Parser object
     */
    private void doTables(Parse fitParser) {
        this.mySqlTest.doTables(fitParser);
    }

    public NewResultStorage runSubmission(Solution solution,
                                          Submission<TaskSQL> submission,
                                          PrintStream err)
            throws SQLException, FitParseException {
        if (!this.mySqlTest.isConnected())
            throw new SQLException("no database connection");

        String checkStr = solution.generateSurveyHTML(submission);
        Parse target = new Parse(checkStr);
        doTables(err, target);
        return getParseResult(target, submission);
    }


    /**
     * This method transforms the Result of the DBFit execution into a html string which can be evaluated
     *
     * @param parse      a DBFit Parse object which is returned by the execution
     * @param submission - evaluated Submission
     * @return a String which contains the result of the DBFit execution
     */
    private NewResultStorage getParseResult(Parse parse, Submission<TaskSQL> submission) {
        return new NewResultStorage(submission,
                printParseStr(parse),
                this.mySqlTest.getCount());
    }

    private String printParseStr(Parse parse) {
        int RECURSIVE_DEEPNESS = 100;
        StringBuilder builder = new StringBuilder();
        printParseStrHelp(builder, parse, RECURSIVE_DEEPNESS);
        return builder.toString();
    }


    /**
     * Stores the annotated parse String in a class variable. This makes sure that the output is stored in the correct
     * order.
     *
     * @param parse Parse object which should be stored
     * @param iter  a counter which limits the recursion level and the result
     */
    private void printParseStrHelp(StringBuilder storage, Parse parse, int iter) {
        if (iter <= 0) {
            storage.append("\n reached threshold");
            return;
        }
        storage.append(parse.leader);
        storage.append(parse.tag);

        if (parse.parts != null) {
            printParseStrHelp(storage, parse.parts, ++iter);
        } else {
            storage.append(parse.body);
        }

        storage.append(parse.end);

        if (parse.more != null) {
            printParseStrHelp(storage, parse.more, ++iter);
        } else {
            storage.append(parse.trailer);
        }
    }

    public boolean connect(XConfig XConfig) throws SQLException {
        return this.connect(XConfig.getHostname(),
                XConfig.getUsername(),
                XConfig.getPassword(),
                XConfig.getDatabase());
    }

    public boolean isConnected() {
        return this.mySqlTest.isConnected();
    }
}

