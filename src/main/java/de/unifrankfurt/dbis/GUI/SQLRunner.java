package de.unifrankfurt.dbis.GUI;

import de.unifrankfurt.dbis.SQL.SQLData;
import de.unifrankfurt.dbis.SQL.SQLDataTable;
import de.unifrankfurt.dbis.SQL.SQLResults;
import de.unifrankfurt.dbis.config.GUIConfig;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * This class executes SQL statements.
 */
public class SQLRunner extends Task<Integer> {
    private final GUIConfig guiConfig;
    private final String sql;
    private final List<String> schema;
    private final boolean verbose;

    public SQLRunner(GUIConfig guiConfig, List<String> schema, String sql, boolean verbose) {
        this.guiConfig = guiConfig;
        this.schema = schema;
        this.sql = sql;
        this.verbose = verbose;
        this.setOnFailed(getDefaultEventHandler(this, "Ausführen des Codes fehlgeschlagen.", verbose));
    }

    public static EventHandler<WorkerStateEvent> getDefaultEventHandler(Task<Integer> task, String errorMessage, Boolean verbose) {
        return (x) -> {
            System.err.println(errorMessage);
            if (SQLException.class.isAssignableFrom(task.getException().getClass())) {
                System.err.println("SQLException (" + ((SQLException) task.getException()).getErrorCode() + "): " + task.getException().getMessage());
            } else {
                System.err.println(task.getException().getClass() + " " + task.getException().getMessage());
            }
            if (verbose) {
                task.getException().printStackTrace();
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


    @Override
    protected Integer call() {

        checkSQL(sql);
        SQLData result = SQLResults.execute(guiConfig.getDataSource(), sql);
        System.out.println(result);

        if (Objects.isNull(schema) || !result.getClass().isAssignableFrom(SQLDataTable.class)) return 0;

        SchemaTester tester = new SchemaTester(guiConfig.getDataSource(), sql, schema);
        final List<String> header = ((SQLDataTable) result).getHeader();
        List<Boolean> testResult = tester.test(header);
        if (Objects.isNull(testResult)) {
            System.out.println("Schema inkorrekt. Das Schema hat nicht die erwartete Länge " + schema.size() + ".");
        } else if (testResult.contains(false)) {
            System.out.println("Schema inkorrekt. Erwartet: " + schema + ", aber war: " + header + ".");
        } else {
            System.out.println("Schema okay.");
        }
        return 0;
    }
}
