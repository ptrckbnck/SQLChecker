package de.unifrankfurt.dbis.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.unifrankfurt.dbis.Submission.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Config used for Evaluation.
 */
public class EvalConfig {


    public final String version = "1.0";
    //DB
    protected String database;
    protected String username;
    protected String password;
    protected String hostname;
    protected String port;

    //Reset
    protected String resetPath;
    protected String solutionPath;
    protected String submissionPaths;


    public String getVersion() {
        return version;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHostname() {
        return hostname;
    }

    public String getPort() {
        return port;
    }

    public String getResetPath() {
        return resetPath;
    }

    public String getSolutionPath() {
        return solutionPath;
    }

    public String getSubmissionPaths() {
        return submissionPaths;
    }

    public EvalConfig(String database,
                      String username,
                      String password,
                      String hostname,
                      String port,
                      String resetPath,
                      String solutionPath,
                      String submissionPaths) {
        this.database = database;
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
        this.resetPath = resetPath;
        this.solutionPath = solutionPath;
        this.submissionPaths = submissionPaths;
    }

    public void storeInPath(Path configPath) throws IOException {
        Files.write(configPath,
                new GsonBuilder().setPrettyPrinting()
                        .create()
                        .toJson(this)
                        .getBytes(StandardCharsets.UTF_8));
    }

    public static EvalConfig fromPath(Path path) throws IOException {
        return new Gson().fromJson(new String(Files.readAllBytes(path)), EvalConfig.class);
    }


    public DataSource getDataSource() {
        return new DataSource(this.hostname,
                port,
                username,
                password,
                database);
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


    public List<Submission<TaskSQL>> getSolutions() throws IOException, SubmissionParseException {
        String[] pathes = this.solutionPath.split(";");
        List<Submission<TaskSQL>> submissions = new ArrayList<>();
        for (String path : pathes){
            submissions.add(Submission.fromPath(Paths.get(path)).onlyTaskSQLSubmission());
        }
        return submissions;
    }


    public Boolean configOK() {
    return Objects.nonNull(this.database)&&
            Objects.nonNull(this.hostname)&&
            Objects.nonNull(this.password)&&
            Objects.nonNull(this.username)&&
            Objects.nonNull(this.resetPath)&&
            Objects.nonNull(this.solutionPath)&&
            Objects.nonNull(this.submissionPaths);
    }
}
