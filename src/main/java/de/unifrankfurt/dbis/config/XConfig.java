package de.unifrankfurt.dbis.config;


import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import de.unifrankfurt.dbis.Submission.SQLScript;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * Config for using with DBFit Backend. Isnt deprecated yet, but should be soon.
 */
public class XConfig {


    public final String version = "1.0";
    //DB
    protected String database;
    protected String username;
    protected String password;
    protected Boolean savePassword;
    protected String hostname;
    protected String port;
    protected String executable;

    //Reset
    protected String resetPath;

    //Solution Generator
    protected String solutionInPath;
    protected String solutionOutPath;
    protected String solutionSamplePath;

    //Submission Executor
    protected String executorSubmissionPath;
    protected String executorSolutionPath;
    protected String executorResultPath;
    protected Boolean executorStaticEnabled;


    //serialized representation of variables with default values.
    private static final String DB_DATABASENAME = "db.databasename";
    private static final String DB_DATABASENAME_DEFAULT = null;

    private static final String DB_USERNAME = "db.username";
    private static final String DB_USERNAME_DEFAULT = "root";

    private static final String DB_PASSWORD = "db.password";
    private static final String DB_PASSWORD_DEFAULT = null;

    private static final String DB_SAVE_PASSWORD = "db.savepassword";
    private static final Boolean DB_SAVE_PASSWORD_DEFAULT = false;

    private static final String DB_HOST = "db.host";
    private static final String DB_HOST_DEFAULT = "localhost";

    private static final String DB_PORT = "db.port";
    private static final String DB_PORT_DEFAULT = "3306";

    private static final String DB_EXECUTABLE = "db.executable";
    private static final String DB_EXECUTABLE_DEFAULT = null;

    private static final String DB_RESETSCRIPT = "db.resetscript";
    private static final String DB_RESETSCRIPT_DEFAULT = null;

    private static final String SOLUTION_INFILE = "test.infile";
    private static final String SOLUTION_INFILE_DEFAULT = null;

    private static final String SOLUTION_OUTFILE = "test.outfile";
    private static final String SOLUTION_OUTFILE_DEFAULT = "/solution.txt";

    private static final String SOLUTION_SAMPLEFILE = "test.samplefile";
    private static final String SOLUTION_SAMPLEFILE_DEFAULT = "/submissions/sample.sql";

    private static final String EXECUTOR_SUBMISSIONS = "chk.submissions";
    private static final String EXECUTOR_SUBMISSIONS_DEFAULT = "/submissions/";

    private static final String EXECUTOR_SOLUTION = "chk.solution";
    private static final String EXECUTOR_SOLUTION_DEFAULT = "/solution.txt";

    private static final String EXECUTOR_OUTPATH = "chk.outpath";
    private static final String EXECUTOR_OUTPATH_DEFAULT = "/out/";

    private static final String EXECUTOR_ALLOW_STATIC = "chk.allowstatic";
    private static final boolean EXECUTOR_ALLOW_STATIC_DEFAULT = false;


    public XConfig(String database, String username, String password, Boolean savePassword,
                   String hostname, String port, String executable, String resetPath,
                   String solutionInPath, String solutionOutPath, String solutionSamplePath,
                   String executorSubmissionPath, String executorSolutionPath,
                   String executorResultPath, Boolean executorStaticEnabled) {
        this.database = database;
        this.username = username;
        this.password = password;
        this.savePassword = savePassword;
        this.hostname = hostname;
        this.port = port;
        this.executable = executable;
        this.resetPath = resetPath;
        this.solutionInPath = solutionInPath;
        this.solutionOutPath = solutionOutPath;
        this.solutionSamplePath = solutionSamplePath;
        this.executorSubmissionPath = executorSubmissionPath;
        this.executorSolutionPath = executorSolutionPath;
        this.executorResultPath = executorResultPath;
        this.executorStaticEnabled = executorStaticEnabled;
    }

    public String getDatabase() {
        return (database == null) ? DB_DATABASENAME_DEFAULT : database;
    }

    public String getUsername() {
        return (username == null) ? DB_USERNAME_DEFAULT : username;
    }

    public String getPassword() {
        return (password == null) ? DB_PASSWORD_DEFAULT : password;
    }

    public Boolean getSavePassword() {
        return (savePassword == null) ? DB_SAVE_PASSWORD_DEFAULT : savePassword;
    }

    public String getHostname() {
        return (hostname == null) ? DB_HOST_DEFAULT : hostname;
    }

    public String getPort() {
        return (port == null) ? DB_PORT_DEFAULT : port;
    }

    public String getExecutable() {
        return (executable == null) ? DB_EXECUTABLE_DEFAULT : executable;
    }

    public String getResetPath() {
        return (resetPath == null) ? DB_RESETSCRIPT_DEFAULT : resetPath;
    }

    public String getSolutionInPath() {
        return (solutionInPath == null) ? SOLUTION_INFILE_DEFAULT : solutionInPath;
    }

    public String getSolutionOutPath() {
        return (solutionOutPath == null) ? SOLUTION_OUTFILE_DEFAULT : solutionOutPath;
    }

    public String getSolutionSamplePath() {
        return (solutionSamplePath == null) ? SOLUTION_SAMPLEFILE_DEFAULT : solutionSamplePath;
    }

    public String getExecutorSubmissionPath() {
        return (executorSubmissionPath == null) ? EXECUTOR_SUBMISSIONS_DEFAULT : executorSubmissionPath;
    }

    public String getExecutorSolutionPath() {
        return (executorSolutionPath == null) ? EXECUTOR_SOLUTION_DEFAULT : executorSolutionPath;
    }

    public String getExecutorResultPath() {
        return (executorResultPath == null) ? EXECUTOR_OUTPATH_DEFAULT : executorResultPath;
    }

    public Boolean getExecutorStaticEnabled() {
        return (executorStaticEnabled == null) ? EXECUTOR_ALLOW_STATIC_DEFAULT : executorStaticEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XConfig XConfig = (XConfig) o;
        return Objects.equals(database, XConfig.database) &&
                Objects.equals(username, XConfig.username) &&
                Objects.equals(password, XConfig.password) &&
                Objects.equals(savePassword, XConfig.savePassword) &&
                Objects.equals(hostname, XConfig.hostname) &&
                Objects.equals(port, XConfig.port) &&
                Objects.equals(executable, XConfig.executable) &&
                Objects.equals(resetPath, XConfig.resetPath) &&
                Objects.equals(solutionInPath, XConfig.solutionInPath) &&
                Objects.equals(solutionOutPath, XConfig.solutionOutPath) &&
                Objects.equals(solutionSamplePath, XConfig.solutionSamplePath) &&
                Objects.equals(executorSubmissionPath, XConfig.executorSubmissionPath) &&
                Objects.equals(executorSolutionPath, XConfig.executorSolutionPath) &&
                Objects.equals(executorResultPath, XConfig.executorResultPath) &&
                Objects.equals(executorStaticEnabled, XConfig.executorStaticEnabled);
    }

    @Override
    public int hashCode() {

        return Objects.hash(database, username, password, savePassword, hostname, port,
                executable, resetPath, solutionInPath, solutionOutPath, solutionSamplePath,
                executorSubmissionPath, executorSolutionPath, executorResultPath,
                executorStaticEnabled);
    }

    /**
     * creates serializable version of key,value pair,
     *
     * @param name String
     * @param val  Object
     * @return String
     */
    private String assembleLine(String name, Object val) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("=");
        if (val != null) sb.append(val.toString());
        sb.append("\n");
        return sb.toString();
    }


    /**
     * creates comment for serialized with description and default value for variable.
     *
     * @param desc         String
     * @param defaultValue String
     * @return String
     */
    private String description(String desc, Object defaultValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(desc);
        if (defaultValue != null) {
            sb.append(" (default: ");
            sb.append(defaultValue.toString());
            sb.append(")");
        }
        sb.append("\n");
        return sb.toString();
    }


    /**
     * create serializable representation of GUIConfig with commentary;
     *
     * @return String
     */
    public String serialString() {

        return ("#SQLChecker Configuration File (version: " + this.version + ")\n\n") +
                "##### MySQL database Settings #####\n" +
                description("Server name or ip-address", DB_HOST_DEFAULT) +
                assembleLine(DB_HOST, hostname) +
                description("Server port", DB_PORT_DEFAULT) +
                assembleLine(DB_PORT, port) +
                description("username for authentication", DB_USERNAME_DEFAULT) +
                assembleLine(DB_USERNAME, username) +
                description("password for authentication", DB_PASSWORD_DEFAULT) +
                assemblePassword(DB_PASSWORD) +
                description("save password", DB_SAVE_PASSWORD_DEFAULT) +
                assembleLine(DB_SAVE_PASSWORD, savePassword) +
                description("database name", DB_DATABASENAME_DEFAULT) +
                assembleLine(DB_DATABASENAME, database) +
                description("path to mysql executables", DB_EXECUTABLE_DEFAULT) +
                assembleLine(DB_EXECUTABLE, executable) +
                "\n#### Survey Settings #####\n" +
                description("path of the reset script that should be used", DB_RESETSCRIPT_DEFAULT) +
                assembleLine(DB_RESETSCRIPT, resetPath) +
                "\n##### DBFitSolution generator settings #####\n" +
                description("The raw solution file, from which the dbfit solution file should be created", SOLUTION_INFILE_DEFAULT) +
                assembleLine(SOLUTION_INFILE, solutionInPath) +
                description("The path at which the output solution file should reside", SOLUTION_OUTFILE_DEFAULT) +
                assembleLine(SOLUTION_OUTFILE, solutionOutPath) +
                description("The path at which the sample submission file should reside\n" +
                        "# This file shows, how a correct submission might look", SOLUTION_SAMPLEFILE_DEFAULT) +
                assembleLine(SOLUTION_SAMPLEFILE, solutionSamplePath) +
                "\n##### Submission executor settings #####\n" +
                description("Folder of the submissions which should be checked", EXECUTOR_SUBMISSIONS_DEFAULT) +
                assembleLine(EXECUTOR_SUBMISSIONS, executorSubmissionPath) +
                description("The path of the dbfit solution file, which should be checked", EXECUTOR_SOLUTION_DEFAULT) +
                assembleLine(EXECUTOR_SOLUTION, executorSolutionPath) +
                description("Output path of the log- and result-files", EXECUTOR_OUTPATH_DEFAULT) +
                assembleLine(EXECUTOR_OUTPATH, executorResultPath) +
                description("True, if static queries should be allowed in Student submissions\n" +
                        "#These queries do not belong to a specific placeholder in the solution file. They get\n" +
                        "executed independently", EXECUTOR_ALLOW_STATIC_DEFAULT) +
                assembleLine(EXECUTOR_ALLOW_STATIC, executorStaticEnabled);
    }


    public void storeInPath(Path configPath) throws IOException {
        Files.write(configPath, List.of(this.serialString()), StandardCharsets.UTF_8);
    }

    /**
     * use storeInPath instead
     *
     * @param file FILE
     * @throws IOException IO
     */
    @Deprecated
    public void storeInFile(File file) throws IOException {
        try (BufferedWriter writer =
                     new BufferedWriter(
                             new OutputStreamWriter(
                                     new FileOutputStream(file),
                                     StandardCharsets.UTF_8))) {
            writer.write(this.serialString());
        }
    }

    private String assemblePassword(String name) {
        if (this.getSavePassword()) {
            return assembleLine(name, this.password);
        } else {
            return assembleLine(name, null);
        }
    }


    /**
     * adds key,value pairs to GUIConfigBuilder cb if variable key exists
     *
     * @param cb    GUIConfigBuilder
     * @param var   serialized name of variable
     * @param value String representation of value
     * @return GUIConfigBuilder
     */
    private static XConfigBuilder build(XConfigBuilder cb, String var, String value) {
        switch (var) {
            case DB_USERNAME:
                return cb.setUsername(value);
            case DB_DATABASENAME:
                return cb.setDatabase(value);
            case DB_PASSWORD:
                return cb.setPassword(value);
            case DB_SAVE_PASSWORD:
                return cb.setSavePassword(Boolean.valueOf(value));
            case DB_HOST:
                return cb.setHostname(value);
            case DB_PORT:
                return cb.setPort(value);
            case DB_EXECUTABLE:
                return cb.setExecutable(value);
            case DB_RESETSCRIPT:
                return cb.setResetPath(value);
            case SOLUTION_INFILE:
                return cb.setSolutionInPath(value);
            case SOLUTION_OUTFILE:
                return cb.setSolutionOutPath(value);
            case SOLUTION_SAMPLEFILE:
                return cb.setSolutionSamplePath(value);
            case EXECUTOR_SUBMISSIONS:
                return cb.setExecutorSubmissionPath(value);
            case EXECUTOR_SOLUTION:
                return cb.setExecutorSolutionPath(value);
            case EXECUTOR_OUTPATH:
                return cb.setExecutorResultPath(value);
            case EXECUTOR_ALLOW_STATIC:
                return cb.setExecutorStaticEnabled(Boolean.valueOf(value));
            default:
                return cb;
        }
    }

    /**
     * @param input BufferedReader
     * @return GUIConfig
     * @throws IOException from BufferedReader
     */
    public static XConfig deserialize(BufferedReader input) throws IOException {
        XConfigBuilder cb = new XConfigBuilder();
        String line;
        while ((line = input.readLine()) != null) {
            if (!line.startsWith("#")) {
                String[] varval = line.split("=");
                if (varval.length < 2) continue;
                cb = build(cb, varval[0], varval[1]);
            }
        }
        return cb.createConfig();
    }

    private static XConfig deserialize(List<String> lines) {
        XConfigBuilder cb = new XConfigBuilder();
        for (String line : lines) {
            if (!line.startsWith("#")) {
                String[] varval = line.split("=");
                if (varval.length < 2) continue;
                cb = build(cb, varval[0], varval[1]);
            }
        }
        return cb.createConfig();
    }

    /**
     * @param input String
     * @return GUIConfig or null if parsing failed
     */
    public static XConfig deserialize(String input) {
        List<String> lines = Arrays.asList(input.split("\n"));
        return deserialize(lines);
    }

    /**
     * loads GUIConfig from Disk
     *
     * @param file File to config location
     * @return GUIConfig
     * @throws IOException from FileReader
     */
    public static XConfig fromFile(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file), StandardCharsets.UTF_8))) {
            return deserialize(br);
        }
    }


    public DataSource getDataSource() {
        MysqlDataSource source = new MysqlDataSource();
        source.setServerName(this.getHostname());
        source.setPortNumber(Integer.parseInt(this.getPort()));
        source.setDatabaseName(this.getDatabase());
        source.setUser(this.getUsername());
        source.setPassword(this.getPassword());
        return source;
    }


    /**
     * Attempts to establish a connection MySQL Server that this GUIConfig represents.
     *
     * @return Connection to MySQL Server
     * @throws SQLException - if a database access error occurs.
     */
    public Connection newConnection() throws SQLException {
        return this.getDataSource().getConnection();
    }

    public SQLScript getResetScript() throws IOException {
        return SQLScript.fromPath(Paths.get(this.resetPath));
    }


    public static XConfig fromPath(Path configPath) throws IOException {
        List<String> lines = Files.readAllLines(configPath);
        return deserialize(lines);
    }


}
