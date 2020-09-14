package de.unifrankfurt.dbis.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import de.unifrankfurt.dbis.Inner.Base;
import de.unifrankfurt.dbis.Inner.SQLScript;
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
import java.util.StringJoiner;


/**
 * Config used for Evaluation.
 */
public class EvalConfig {
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


    private final String csvOutputPath;
    //Output
    protected Boolean verbose;

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
                      String submissionPath,
                      boolean verbose,
                      String csvOutputPath) {
        this.database = database;
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
        this.resetPath = resetPath;
        this.solutionPaths = solutionPaths;
        this.submissionPath = submissionPath;
        this.verbose = verbose;
        this.csvOutputPath = csvOutputPath;
    }

    private static EvalConfig parseINI(String conf) throws IOException {
        Ini ini = new Ini();
        ini.load(new StringReader(conf));
        return new EvalConfig(
                Objects.requireNonNullElse(ini.get("db", "database"), ""),
                Objects.requireNonNullElse(ini.get("db", "username"), ""),
                Objects.requireNonNullElse(ini.get("db", "password"), ""),
                Objects.requireNonNullElse(ini.get("db", "hostname"), ""),
                Objects.requireNonNullElse(ini.get("db", "port"), ""),
                Objects.requireNonNullElse(ini.get("files", "resetPath"), ""),
                Objects.requireNonNullElse(ini.get("files", "solutionPaths"), ""),
                Objects.requireNonNullElse(ini.get("files", "submissionPath"), ""),
                Boolean.parseBoolean(ini.get("output", "verbose")),
                Objects.requireNonNullElse(ini.get("output", "csvOutputPath"), "")
        );
    }

    public Boolean getVerbose() {
        return verbose;
    }

    public static EvalConfig fromPath(Path path) throws IOException {
        String conf = new String(Files.readAllBytes(path));
        if (path.getFileName().toString().toLowerCase().endsWith(".ini")) {
            return EvalConfig.parseINI(conf);
        }
        try {
            return new Gson().fromJson(conf, EvalConfig.class);
        } catch (JsonSyntaxException e) {
            return EvalConfig.parseINI(conf);
        }

    }

    public String getCsvOutputPath() {
        return csvOutputPath;
    }


    public DataSource getDataSource() {
        return new DataSource(this.hostname,
                port,
                username,
                password,
                database,
                false,
                "+01:00");
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


    public List<Base> getSolutions() throws IOException {
        String[] pathes = this.solutionPaths.split(",");
        List<Base> bases = new ArrayList<>();
        for (String path : pathes){
            bases.add(Base.fromPath(Paths.get(path)));
        }
        return bases;
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

        ini.put("db", "database", Objects.requireNonNullElse(this.database, ""));
        ini.put("db", "username", Objects.requireNonNullElse(this.username, ""));
        ini.put("db", "password", Objects.requireNonNullElse(this.password, ""));
        ini.put("db", "hostname", Objects.requireNonNullElse(this.hostname, ""));
        ini.put("db", "port", Objects.requireNonNullElse(this.port, ""));
        ini.putComment("db", "settings for database connection");
        ini.put("files", "resetPath", Objects.requireNonNullElse(this.resetPath, ""));
        ini.put("files", "solutionPaths", Objects.requireNonNullElse(this.solutionPaths, ""));
        ini.put("files", "submissionPath", Objects.requireNonNullElse(this.submissionPath, ""));
        ini.putComment("files", "pathes to files");
        ini.put("output", "verbose", Boolean.toString(this.verbose));
        ini.put("output", "csvOutputPath", Objects.requireNonNullElse(this.csvOutputPath, ""));
        ini.putComment("output", "output config");
        return ini;
    }

    public void storeInPath(Path configPath) throws IOException {
        storeInPath(configPath, false);
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", EvalConfig.class.getSimpleName() + "[", "]")
                .add("database='" + database + "'")
                .add("username='" + username + "'")
                .add("password='" + password + "'")
                .add("hostname='" + hostname + "'")
                .add("port='" + port + "'")
                .add("resetPath='" + resetPath + "'")
                .add("solutionPaths='" + solutionPaths + "'")
                .add("submissionPath='" + submissionPath + "'")
                .add("verbose=" + verbose)
                .toString();
    }
}
