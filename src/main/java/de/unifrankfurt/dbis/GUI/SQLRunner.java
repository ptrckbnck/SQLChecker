package de.unifrankfurt.dbis.GUI;

import de.unifrankfurt.dbis.SQL.SQLResultWrapper;
import de.unifrankfurt.dbis.config.GUIConfig;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLRunner extends Task {
    private final GUIConfig guiConfig;
    private final String sql;

    public SQLRunner(GUIConfig guiConfig, String sql) {
        this.guiConfig = guiConfig;
        this.sql = sql;
    }

    @Override
    protected Object call() {
        try (Connection con = guiConfig.newConnection()) {
            Statement stmt = con.createStatement();
            SQLResultWrapper result = SQLResultWrapper.executeStatement(stmt, sql);
            Platform.runLater(() -> System.out.println(result));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
