package de.unifrankfurt.dbis;

public class ConfigBuilder {
    private String databaseName = "";
    private String username = "";
    private String password = "";
    private String host = "";
    private String port = "";
    private String resetScript = "";
    private String studentName = "";
    private String matNr = "";
    private String email = "";
    private boolean partnerOk = false;
    private String partnerName = "";
    private String partnerMatNr = "";
    private String partnerEmail = "";

    public ConfigBuilder setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public ConfigBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public ConfigBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public ConfigBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public ConfigBuilder setPort(String port) {
        this.port = port;
        return this;
    }

    public ConfigBuilder setResetScript(String resetScript) {
        this.resetScript = resetScript;
        return this;
    }

    public ConfigBuilder setStudentName(String studentName) {
        this.studentName = studentName;
        return this;
    }

    public ConfigBuilder setMatNr(String matNr) {
        this.matNr = matNr;
        return this;
    }

    public ConfigBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public ConfigBuilder setPartnerOk(boolean partnerOk) {
        this.partnerOk = partnerOk;
        return this;
    }

    public ConfigBuilder setPartnerName(String partnerName) {
        this.partnerName = partnerName;
        return this;
    }

    public ConfigBuilder setPartnerMatNr(String partnerMatNr) {
        this.partnerMatNr = partnerMatNr;
        return this;
    }

    public ConfigBuilder setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail;
        return this;
    }

    public Config createConfig() {
        return new Config(databaseName, username, password, host, port, resetScript, studentName, matNr, email, partnerOk, partnerName, partnerMatNr, partnerEmail);
    }
}