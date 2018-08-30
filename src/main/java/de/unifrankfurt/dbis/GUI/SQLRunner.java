package de.unifrankfurt.dbis.GUI;

import de.unifrankfurt.dbis.SQL.SQLResultWrapper;
import de.unifrankfurt.dbis.config.GUIConfig;
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

        checkSQL(sql);
        try (Connection con = guiConfig.newConnection()) {
            Statement stmt = con.createStatement();
            SQLResultWrapper result = SQLResultWrapper.executeStatement(stmt, sql);
            System.out.println(result);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private void checkSQL(String sql) {
        if (sql.contains("use ") || sql.contains("USE ")) {
            System.out.println("[Warnung] die Database mittels USE zu wechseln hat keinen Effekt.\n" +
                    "[Warnung] Bitte richtige Database in Config angeben.");
        }
    }
}
