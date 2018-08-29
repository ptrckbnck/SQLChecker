package de.unifrankfurt.dbis.config;

import com.google.gson.Gson;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class GUIConfig {
    //database
    private final String databaseName;
    private final String username;
    private final String password;
    private final String host;
    private final Integer port;
    private final String resetScript;

    //studentdata
    private final String studentName;
    private final String matNr;
    private final String email;
    private final boolean partnerOk;
    private final String partnerName;
    private final String partnerMatNr;
    private final String partnerEmail;

    public GUIConfig(String databaseName, String username, String password, String host, Integer port, String resetScript, String studentName, String matNr, String email, boolean partnerOk, String partnerName, String partnerMatNr, String partnerEmail) {
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.resetScript = resetScript;
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

    public DataSource getDataSource() {
        MysqlDataSource source = new MysqlDataSource();
        source.setServerName(this.getHost());
        source.setPortNumber(this.getPort());
        source.setDatabaseName(this.getDatabaseName());
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

}
