package de.unifrankfurt.dbis.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import de.unifrankfurt.dbis.Submission.SQLScript;
import de.unifrankfurt.dbis.Submission.Submission;
import de.unifrankfurt.dbis.Submission.SubmissionParseException;
import de.unifrankfurt.dbis.Submission.TaskSQL;
import org.ini4j.Ini;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Config used for Evaluation.
 */
public class EvalConfig {

    //TODO ADD timezone?
    //DB
    protected String database;
    protected String username;
    protected String password;
    protected String hostname;
    protected String port;

    //Reset
    protected String resetPath;
    protected String solutionPaths;
    protected String submissionPath;


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

    public String getSolutionPaths() {
        return solutionPaths;
    }

    public String getSubmissionPath() {
        return submissionPath;
    }

    public EvalConfig(String database,
                      String username,
                      String password,
                      String hostname,
                      String port,
                      String resetPath,
                      String solutionPaths,
                      String submissionPath) {
        this.database = database;
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
        this.resetPath = resetPath;
        this.solutionPaths = solutionPaths;
        this.submissionPath = submissionPath;
    }

    public static EvalConfig fromPath(Path path) throws IOException {
        String conf = new String(Files.readAllBytes(path));
        if (path.getFileName().toString().toLowerCase().endsWith(".ini")){
            return EvalConfig.parseINI(conf);
        }
        try{
            return new Gson().fromJson(conf, EvalConfig.class);
        }catch(JsonSyntaxException e){
            throw new IOException("Could not parseLines Config (" + path + "): " + e.getMessage(), e);
        }

    }

    private static EvalConfig parseINI(String conf) throws IOException {
        Ini ini = new Ini();
        ini.load(new StringReader(conf));
        return new EvalConfig(ini.get("db","database"),
                ini.get("db","username"),
                ini.get("db","password"),
                ini.get("db","hostname"),
                ini.get("db","port"),
                ini.get("files","resetPath"),
                ini.get("files","solutionPaths"),
                ini.get("files","submissionPath"));
    }


    public DataSource getDataSource() {
        return new DataSource(this.hostname,
                port,
                username,
                password,
                database,
                false,
                null);
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
        String[] pathes = this.solutionPaths.split(",");
        List<Submission<TaskSQL>> submissions = new ArrayList<>();
        for (String path : pathes){
            submissions.add(Submission.fromPath(Paths.get(path)).onlyTaskSQLSubmission());
        }
        return submissions;
    }


    public boolean configOK() throws IOException{
        List<String> msg = new ArrayList<>();
        boolean ok = nonNull("database", this.database, msg)&&
            nonNull("hostname", this.hostname, msg)&&
            nonNull("password", this.password, msg)&&
            nonNull("username", this.username, msg)&&
            nonNull("resetPath", this.resetPath, msg)&&
            nonNull("solutionPaths", this.solutionPaths, msg)&&
            nonNull("submissionPath", this.submissionPath, msg);
        if (!ok) {
            throw new IOException("Loading of config Failed: "+String.join("\n, ",msg));
        }
        return ok;
    }

    private boolean nonNull(String attribute, String value,List<String>  msg) {
        if (Objects.nonNull(value)) return true;
        msg.add("Attribute "+attribute+" missing.");
        return false;
    }


    public void storeInPath(Path configPath, boolean json) throws IOException {
        if (json) {
            Files.write(configPath,
                    new GsonBuilder().setPrettyPrinting()
                            .create()
                            .toJson(this)
                            .getBytes(StandardCharsets.UTF_8));}
        else {
            StringWriter writer = new StringWriter();
            this.toINI().store(writer);
            Files.write(configPath,writer.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    public Ini toINI() {
        Ini ini = new Ini();

        ini.put("db","database",Objects.requireNonNullElse(this.database,""));
        ini.put("db","username",Objects.requireNonNullElse(this.username,""));
        ini.put("db","password",Objects.requireNonNullElse(this.password,""));
        ini.put("db","hostname",Objects.requireNonNullElse(this.hostname,""));
        ini.put("db","port",Objects.requireNonNullElse(this.port,""));
        ini.putComment("db","settings for database connection");
        ini.put("files","resetPath",Objects.requireNonNullElse(this.resetPath,""));
        ini.put("files","solutionPaths",Objects.requireNonNullElse(this.solutionPaths,""));
        ini.put("files","submissionPath",Objects.requireNonNullElse(this.submissionPath,""));
        ini.putComment("files","pathes to files");
        return ini;
    }

    public void storeInPath(Path configPath) throws IOException {
        storeInPath(configPath, false);
    }

    @Override
    public String toString() {
        return "EvalConfig{" +
                "database='" + database + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", hostname='" + hostname + '\'' +
                ", port='" + port + '\'' +
                ", resetPath='" + resetPath + '\'' +
                ", solutionPaths='" + solutionPaths + '\'' +
                ", submissionPath='" + submissionPath + '\'' +
                '}';
    }
}
