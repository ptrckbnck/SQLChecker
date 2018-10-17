package de.unifrankfurt.dbis.GUI;

import de.unifrankfurt.dbis.SQL.SQLResultWrapper;
import de.unifrankfurt.dbis.config.GUIConfig;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class executes SQL statements.
 */
public class SQLRunner extends Task<Integer> {
    private final GUIConfig guiConfig;
    private final String sql;

    public SQLRunner(GUIConfig guiConfig, String sql) {
        this.guiConfig = guiConfig;
        this.sql = sql;
        this.setOnFailed(getDefaultEventHandler(this, guiConfig, "Ausführen des Codes fehlgeschlagen."));
    }

    public static EventHandler<WorkerStateEvent> getDefaultEventHandler(Task<Integer> task, GUIConfig config, String errorMessage) {
        return (x) -> {
            System.err.println(errorMessage);
            if (SQLException.class.isAssignableFrom(task.getException().getClass())) {
                System.err.println("SQLException (" + ((SQLException) task.getException()).getErrorCode() + "): " + task.getException().getMessage());
//                if (((SQLException) task.getException()).getErrorCode() == 1049) {//unknown database
//                    if (config.getDatabaseName().isEmpty()) return;
//                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                    alert.setTitle("Database existiert nicht");
//                    alert.setHeaderText("Database existiert nicht");
//                    alert.setContentText("Die ausgewählte Database existiert nicht, haben sie das Rese?");
//                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
//                    Optional<ButtonType> result = alert.showAndWait();
//                    if (result.isPresent() && result.get() == ButtonType.OK) {
//                        createDatabase(config);
//                    }
//                }
            } else {
                System.err.println(task.getException().getClass() + " " + task.getException().getMessage());
            }
        };
    }

    /**
     * This function checks given SQL-Code before executing. So far only checks for Keyword "use".
     *
     * @param sql SQL-String
     */
    private void checkSQL(String sql) {
        if (sql.contains("use ") || sql.contains("USE ")) {
            System.out.println("[Warnung] die Database mittels USE zu wechseln hat keinen Effekt.\n" +
                    "[Warnung] Bitte richtige Database in Config angeben.");
        }
    }

    /**
     * creates in Config defined Database if not present.
     */
    private static void createDatabase(GUIConfig config) {
        SQLCreateDatabase createDatabase = new SQLCreateDatabase(config);
        Thread t = new Thread(createDatabase);
        t.start();
    }

    @Override
    protected Integer call() throws SQLException {

        checkSQL(sql);
        try (Connection con = guiConfig.newConnection()) {
            Statement stmt = con.createStatement();
            SQLResultWrapper result = SQLResultWrapper.executeStatement(stmt, sql);
            System.out.println(result);
            return 0;
        }
    }
}
