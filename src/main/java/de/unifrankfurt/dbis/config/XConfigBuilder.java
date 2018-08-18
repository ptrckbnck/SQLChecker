package de.unifrankfurt.dbis.config;


/**
 * Builder class for GUIConfig
 */
public class XConfigBuilder {
    private String database;
    private String username;
    private String password;
    private Boolean savePassword;
    private String hostname;
    private String port;
    private String executable;
    private String resetPath;
    private String solutionInPath;
    private String solutionOutPath;
    private String solutionSamplePath;
    private String executorSubmissionPath;
    private String executorSolutionPath;
    private String executorResultPath;
    private Boolean executorStaticEnabled;

    public XConfigBuilder() {


    }

    public XConfigBuilder(XConfig XConfig) {
        database = XConfig.database;
        username = XConfig.username;
        password = XConfig.password;
        savePassword = XConfig.savePassword;
        hostname = XConfig.hostname;
        port = XConfig.port;
        executable = XConfig.executable;

        resetPath = XConfig.resetPath;
        solutionInPath = XConfig.solutionInPath;
        solutionOutPath = XConfig.solutionOutPath;
        solutionSamplePath = XConfig.solutionSamplePath;
        executorSubmissionPath = XConfig.executorSubmissionPath;
        executorSolutionPath = XConfig.executorSolutionPath;
        executorResultPath = XConfig.executorResultPath;
        executorStaticEnabled = XConfig.executorStaticEnabled;
    }

    public XConfigBuilder setDatabase(String database) {
        this.database = database;
        return this;
    }

    public XConfigBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public XConfigBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public XConfigBuilder setSavePassword(Boolean savePassword) {
        this.savePassword = savePassword;
        return this;
    }

    public XConfigBuilder setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public XConfigBuilder setPort(String port) {
        this.port = port;
        return this;
    }

    public XConfigBuilder setExecutable(String executable) {
        this.executable = executable;
        return this;
    }

    public XConfigBuilder setResetPath(String resetPath) {
        this.resetPath = resetPath;
        return this;
    }

    public XConfigBuilder setSolutionInPath(String solutionInPath) {
        this.solutionInPath = solutionInPath;
        return this;
    }

    public XConfigBuilder setSolutionOutPath(String solutionOutPath) {
        this.solutionOutPath = solutionOutPath;
        return this;
    }

    public XConfigBuilder setSolutionSamplePath(String solutionSamplePath) {
        this.solutionSamplePath = solutionSamplePath;
        return this;
    }

    public XConfigBuilder setExecutorSubmissionPath(String executorSubmissionPath) {
        this.executorSubmissionPath = executorSubmissionPath;
        return this;
    }

    public XConfigBuilder setExecutorSolutionPath(String executorSolutionPath) {
        this.executorSolutionPath = executorSolutionPath;
        return this;
    }

    public XConfigBuilder setExecutorResultPath(String executorResultPath) {
        this.executorResultPath = executorResultPath;
        return this;
    }

    public XConfigBuilder setExecutorStaticEnabled(Boolean executorStaticEnabled) {
        this.executorStaticEnabled = executorStaticEnabled;
        return this;
    }

    public XConfig createConfig() {
        return new XConfig(database, username, password, savePassword, hostname, port, executable, resetPath, solutionInPath, solutionOutPath, solutionSamplePath, executorSubmissionPath, executorSolutionPath, executorResultPath, executorStaticEnabled);
    }

    public Boolean getSavePassword() {
        return savePassword;
    }
}