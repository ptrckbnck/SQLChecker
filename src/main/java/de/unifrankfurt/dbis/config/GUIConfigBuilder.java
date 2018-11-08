package de.unifrankfurt.dbis.config;

/**
 * Builder class for GUIConfig
 */
public class GUIConfigBuilder {
    private String databaseName = "";
    private String username = "";
    private String password = "";
    private String host = "localhost";
    private Integer port = 3306;
    private String resetScript = "";
    private String studentName = "";
    private String matNr = "";
    private String email = "";
    private boolean partnerOk = false;
    private String partnerName = "";
    private String partnerMatNr = "";
    private String partnerEmail = "";
    private String timezone = "+01:00";

    public GUIConfigBuilder setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public GUIConfigBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public GUIConfigBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public GUIConfigBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public GUIConfigBuilder setPort(Integer port) {
        this.port = port;
        return this;
    }

    public GUIConfigBuilder setResetScript(String resetScript) {
        this.resetScript = resetScript;
        return this;
    }

    public GUIConfigBuilder setStudentName(String studentName) {
        this.studentName = studentName;
        return this;
    }

    public GUIConfigBuilder setMatNr(String matNr) {
        this.matNr = matNr;
        return this;
    }

    public GUIConfigBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public GUIConfigBuilder setPartnerOk(boolean partnerOk) {
        this.partnerOk = partnerOk;
        return this;
    }

    public GUIConfigBuilder setPartnerName(String partnerName) {
        this.partnerName = partnerName;
        return this;
    }

    public GUIConfigBuilder setPartnerMatNr(String partnerMatNr) {
        this.partnerMatNr = partnerMatNr;
        return this;
    }

    public GUIConfigBuilder setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail;
        return this;
    }

    public GUIConfigBuilder setTimezone(String timezone) {
        this.timezone = timezone;
        return this;
    }
    public GUIConfig createConfig() {
        return new GUIConfig(databaseName, username, password, host, port, resetScript, timezone, studentName, matNr, email, partnerOk, partnerName, partnerMatNr, partnerEmail);
    }


}