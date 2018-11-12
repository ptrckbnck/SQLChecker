package de.unifrankfurt.dbis.config;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource{
    private MysqlDataSource source;
    private final String host;
    private final Integer port;
    private final String user;
    private final String password;
    private final String database;
    private final Boolean useSSL;
    private final String serverTimezone;

    public DataSource(String host,
                      String port,
                      String user,
                      String password,
                      String database,
                      Boolean useSSL,
                      String serverTimezone) {
        this.host = host;
        this.port = Integer.parseInt(port);
        this.user = user;
        this.password = password;
        this.database = database;
        this.useSSL = useSSL;
        this.serverTimezone = serverTimezone;
        this.source = new MysqlDataSource();
        source.setServerName(host);
        source.setPortNumber(this.port);
        source.setDatabaseName(database);
        source.setUser(user);
        source.setPassword(password);
        try {
            source.setUseSSL(useSSL);
            source.setAllowPublicKeyRetrieval(true);
            source.setCharacterEncoding("UTF-8");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if ((serverTimezone != null) && (!this.serverTimezone.trim().isEmpty())) {
                source.setSessionVariables("time_zone='" + this.serverTimezone + "'"); //source.setServerTimezone does not work
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public Boolean getUseSSL() {
        return useSSL;
    }

    public String getServerTimezone() {
        return serverTimezone;
    }
    public Connection getConnection() throws SQLException {
        Connection c;
        try {
            c = this.source.getConnection();
        } catch (SQLException e) {
            source.setDatabaseName(null);
            c = this.source.getConnection();
            source.setDatabaseName(this.getDatabase());
        }
        return c;
    }


}
