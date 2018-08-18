package de.unifrankfurt.dbis.Submission;


import de.unifrankfurt.dbis.config.XConfig;
import de.unifrankfurt.dbis.DBFit.FullCheckResult;
import de.unifrankfurt.dbis.DBFit.FullCheckResultBuilder;
import de.unifrankfurt.dbis.DBFit.MySQLTestWrapper;
import de.unifrankfurt.dbis.DBFit.NewResultStorage;
import fit.exception.FitParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Executes the submissions which are stored in the given directory
 * and prints the result/status
 */
public class NewSubmissionExecutor {

    private final XConfig XConfig;
    private final Solution solution;
    private final MySQLTestWrapper test;
    private final NewScript reset;


    public NewSubmissionExecutor(XConfig XConfig, Solution solution, NewScript reset) {
        this.XConfig = XConfig;
        this.reset = reset;
        this.solution = solution;
        this.test = new MySQLTestWrapper();
    }

    /**
     * true if success
     *
     * @return
     */
    public boolean connectDBFit() {
        try {
            this.test.connect(XConfig);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public void closeDBFit() throws SQLException {
        this.test.close();
    }

    /**
     * Check Submission based on this.solution.
     *
     * @param submission Submission<TaskSQL>
     * @return NewResultException
     * @throws SQLException if Database problems occurred.
     */
    public NewResultStorage runCheck(Submission<TaskSQL> submission)
            throws FitParseException, SQLException {

        //TODO currently does not support static tasks for students

        OutputStream stream = new ByteArrayOutputStream();
        PrintStream err = null;
        try {
            err = new PrintStream(
                    stream,
                    true,
                    StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this.test.runSubmission(solution, submission, err);
    }


    /**
     * Load Submissions from Olat zip-Files.
     *
     * @param builder FullCheckResultBuilder
     * @param path    Olat Submission File or directory containing Olat Submission files.
     * @return List of Submissions
     */
    private List<Submission<TaskSQL>> loadStudentSubmission(
            FullCheckResultBuilder builder,
            Path path) throws IOException {

        List<Submission<TaskSQL>> subs = new ArrayList<>();
        HashMap<Path, Exception> errorLogFile = new HashMap<>();

        if (!Files.isDirectory(path)) {
            try {
                subs.add(Submission.fromOlat(path));
            } catch (IOException | SubmissionParseException e) {
                errorLogFile.put(path, e);
            }
        }

        Stream<Path> files = Files.list(path);
        files
                .filter(x -> !Files.isDirectory(x))
                .forEach(x -> {
                            try {
                                subs.add(Submission.fromOlat(x));
                            } catch (IOException | SubmissionParseException e) {
                                errorLogFile.put(x, e);
                            }
                        }
                );
        builder.setErrorLogFile(errorLogFile);
        return subs;
    }


    /**
     * Runs DBFit check for every File in path.
     *
     * @param path Olat Submission File or directory containing Olat Submission files.
     * @return FullCheckResult which stored every result
     * @throws IOException IO
     */
    public FullCheckResult runTest(Path path) throws IOException, SQLException {
        FullCheckResultBuilder builder = new FullCheckResultBuilder();
        List<Submission<TaskSQL>> subs = loadStudentSubmission(builder, path);
        builder.setSubmissions(subs);

        runAllChecks(builder, subs, XConfig.getResetScript(), XConfig.newConnection());
        return builder.createFullCheckResult();
    }

    /**
     * Runs Dbfit checks for every Submission<TaskSQL> in subs bases on this.solution.
     *
     * @param builder FullCheckResultBuilder which is used to create to create FullCheckResult.
     * @param subs    Every Submission that should be tested.
     * @param reset   The Script that resets Database for every check.
     */
    private void runAllChecks(FullCheckResultBuilder builder,
                              List<Submission<TaskSQL>> subs,
                              NewScript reset,
                              Connection connection) throws SQLException {
        Map<Submission<TaskSQL>, Exception> errorLogSubmission = new HashMap<>();
        Map<Submission<TaskSQL>, NewResultStorage> result = new HashMap<>();

        Statement statement = connection.createStatement();

        for (Submission<TaskSQL> sub : subs) {
            if (statement.isClosed()) {
                statement = connection.createStatement();
            }
            resetDB(statement);
            try {
                if (!this.test.isConnected()) {
                    boolean success = test.reconnect();
                    if (!success) {
                        throw new SQLException("DBFit connection broken down");
                    } else {
                        result.put(sub, runCheck(sub));
                    }

                }

            } catch (SQLException | FitParseException e) {
                errorLogSubmission.put(sub, e);
            }
        }
        statement.close();
        connection.close();

        builder.setErrorLogSubmission(errorLogSubmission);
        builder.setResult(result);
    }

    public void resetDB(Statement statement) throws SQLException {
        try {
            this.reset.execute(statement);
        } catch (SQLException e) {
            throw new SQLException("reset database failed", e);
        }
    }

    public void resetDB() throws SQLException {
        Connection connection = this.XConfig.newConnection();
        assert connection != null;
        assert this.reset != null;
        try {
            this.reset.execute(connection.createStatement());
        } catch (SQLException e) {
            throw new SQLException("reset database failed", e);
        } finally {
            connection.close();
        }
    }


    /**
     * checks if name starts with submission name, followed by separator "_";
     *
     * @param name String. Name of Zip entry of Olat Submission. Should be like: "<name>_<mat_nr>.txt"
     * @return true if valid
     */
    private boolean checkOlatFileName(String name) {
        return name.startsWith(getSubmissionName() + "_");
    }

    /**
     * Gets name of Submission this Solution is bases on.
     *
     * @return String
     */
    private String getSubmissionName() {
        return this.solution.getSubmission().getName();
    }
}