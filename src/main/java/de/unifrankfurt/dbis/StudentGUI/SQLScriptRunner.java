package de.unifrankfurt.dbis.StudentGUI;

import de.unifrankfurt.dbis.Inner.SQLScript;
import de.unifrankfurt.dbis.config.GUIConfig;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * this class executes SQLScripts
 */
public class SQLScriptRunner extends Task<Integer> {

    private final GUIConfig guiConfig;
    private final SQLScript script;


    public SQLScriptRunner(GUIConfig guiConfig, SQLScript script, Boolean verbose) {
        this.guiConfig = guiConfig;
        this.script = script;
        this.setOnFailed(SQLRunner.getDefaultEventHandler(this, "Resetten der Datenbank fehlgeschlagen", verbose));
    }


    @Override
    protected Integer call() throws SQLException {
        Statement statement = null;
        ResultSet rs = null;
        try (Connection connection = guiConfig.newConnection()) {

            statement = connection.createStatement();
            script.execute(statement);
            rs = statement.executeQuery("SHOW tables;");
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
            rs = statement.executeQuery("SELECT DATABASE();");
            rs.next();
            String database = rs.getString(1);
            System.out.println("Datenbank wurde zurückgesetzt.");
            System.out.println("Datenbank " + database + " besitzt nun folgende Tabellen:");
            System.out.println(String.join(", ", tables));
        } catch (SQLException e) {
            System.err.println(e);
            throw e;
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
        return null;
    }
}
