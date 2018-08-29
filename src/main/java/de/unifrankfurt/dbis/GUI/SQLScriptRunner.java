package de.unifrankfurt.dbis.GUI;

import de.unifrankfurt.dbis.Submission.SQLScript;
import de.unifrankfurt.dbis.config.GUIConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLScriptRunner implements Runnable {

    private final GUIConfig guiConfig;
    private final SQLScript script;


    public SQLScriptRunner(GUIConfig guiConfig, SQLScript script) {
        this.guiConfig = guiConfig;
        this.script = script;
    }

    public void run() {
        Statement statement = null;
        ResultSet rs = null;
        try (Connection connection = guiConfig.newConnection()) {

            statement = connection.createStatement();
            script.execute(statement);
            rs = statement.executeQuery("SHOW tables;");
            System.out.println("Datenbank wurde zurückgesetzt");
            System.out.println("Datenbank " + guiConfig.getDatabaseName() + " besitzt nun folgende Tabellen:");
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
