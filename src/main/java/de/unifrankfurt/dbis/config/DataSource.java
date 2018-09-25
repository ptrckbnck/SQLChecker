package de.unifrankfurt.dbis.config;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource{
    private MysqlDataSource source;
    private final String host;
    private final Integer port;
    private final String user;
    private final  String password;
    private final String database;

    public DataSource(String host, String port, String user, String password, String database) {
        this.host = host;
        this.port = Integer.parseInt(port);
        this.user = user;
        this.password = password;
        this.database = database;
        this.source = new MysqlDataSource();
        source.setServerName(host);
        source.setPortNumber(this.port);
        source.setDatabaseName(database);
        source.setUser(user);
        source.setPassword(password);
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

    public Connection getConnection() throws SQLException {
        try {
            return this.source.getConnection();
        } catch (SQLException e) {
            MysqlDataSource m = new MysqlDataSource();
            m.setServerName(host);
            m.setPortNumber(this.port);
            m.setUser(user);
            m.setPassword(password);
            return m.getConnection();
        }
    }


}
