package de.unifrankfurt.dbis.config;

import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class represents the Config state for StudentGUI. Provides DataSource.
 */
public class GUIConfig {
    //database
    private final String databaseName;
    private final String username;
    private final String password;
    private final String host;
    private final Integer port;
    private final String resetScript;
    private final String timezone;

    //studentdata
    private final String studentName;
    private final String matNr;
    private final String email;
    private final boolean partnerOk;
    private final String partnerName;
    private final String partnerMatNr;
    private final String partnerEmail;

    public GUIConfig(String databaseName, String username, String password, String host, Integer port, String resetScript, String timezone, String studentName, String matNr, String email, boolean partnerOk, String partnerName, String partnerMatNr, String partnerEmail) {
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.resetScript = resetScript;
        this.timezone = timezone;
        this.studentName = studentName;
        this.matNr = matNr;
        this.email = email;
        this.partnerOk = partnerOk;
        this.partnerName = partnerName;
        this.partnerMatNr = partnerMatNr;
        this.partnerEmail = partnerEmail;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getResetScript() {
        return resetScript;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getNameStudent() {
        return studentName;
    }

    public String getMatNr() {
        return matNr;
    }

    public String getEmail() {
        return email;
    }

    public boolean isPartnerOk() {
        return partnerOk;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public String getPartnerMatNr() {
        return partnerMatNr;
    }

    public String getPartnerEmail() {
        return partnerEmail;
    }

    public static GUIConfig fromJson(String json) {
        return new Gson().fromJson(json, GUIConfig.class);
    }

    /**
     * @return serialisable Json representation of this class. Load with Gson.
     */
    public String toJson() {
        Gson g = new Gson();
        return g.toJson(this);
    }

    @Override
    public String toString() {
        return "GUIConfig{" +
                "databaseName='" + databaseName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", resetScript='" + resetScript + '\'' +
                ", studentName='" + studentName + '\'' +
                ", matNr='" + matNr + '\'' +
                ", email='" + email + '\'' +
                ", partnerOk=" + partnerOk +
                ", partnerName='" + partnerName + '\'' +
                ", partnerMatNr='" + partnerMatNr + '\'' +
                ", partnerEmail='" + partnerEmail + '\'' +
                '}';
    }

    /**
     * @return DataSource Object for connecting to Database defined in this Config.
     */
    public DataSource getDataSource() {
        return new DataSource(this.getHost(),
                String.valueOf(this.getPort()),
                this.getUsername(),
                this.getPassword(),
                this.getDatabaseName(),
                false,
                this.getTimezone());
    }


    /**
     * Attempts to establish a connection MySQL Server that this GUIConfig represents.
     *
     * @return Connection to SQL Server
     * @throws SQLException - if a database access error occurs.
     */
    public Connection newConnection() throws SQLException {
        return this.getDataSource().getConnection();
    }

}
