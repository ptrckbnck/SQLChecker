package de.unifrankfurt.dbis.GUI;

import de.unifrankfurt.dbis.SQL.SQLResultWrapper;
import de.unifrankfurt.dbis.config.GUIConfig;
import de.unifrankfurt.dbis.config.GUIConfigBuilder;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLCreateDatabase extends Task {

    private final GUIConfig guiConfig;

    public SQLCreateDatabase(GUIConfig guiConfig) {
        this.guiConfig = guiConfig;
    }


    @Override
    protected Object call() {
        GUIConfig config = new GUIConfigBuilder().setHost(guiConfig.getHost())
                .setPassword(guiConfig.getPassword())
                .setPort(guiConfig.getPort())
                .setUsername(guiConfig.getUsername())
                .createConfig();
        try (Connection con = config.newConnection()) {
            Statement stmt = con.createStatement();
            SQLResultWrapper.executeStatement(stmt, "CREATE DATABASE IF NOT EXISTS " + this.guiConfig.getDatabaseName() + ";");
            System.out.println("Database " + this.guiConfig.getDatabaseName() + " wurde angelegt.");
        } catch (SQLException e) {
            System.err.println("Erstellen der Database fehlgeschlagen: " + e.getMessage());
        }
        return null;
    }

}
