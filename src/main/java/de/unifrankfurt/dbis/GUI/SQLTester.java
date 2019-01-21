package de.unifrankfurt.dbis.GUI;

import de.unifrankfurt.dbis.SQL.SchemaTester;
import de.unifrankfurt.dbis.config.GUIConfig;
import javafx.concurrent.Task;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * This class executes SQL statements.
 */
public class SQLTester extends Task<Integer> {
    private final GUIConfig guiConfig;
    private final String sql;
    private final List<String> schema;

    public SQLTester(GUIConfig guiConfig, String sql, List<String> schema, boolean verbose) {
        this.guiConfig = guiConfig;
        this.sql = sql;
        this.schema = schema;
        this.setOnFailed(SQLRunner.getDefaultEventHandler(this, "Fehler beim Durchführen des Tests aufgetreten", verbose));
    }

    @Override
    protected Integer call() throws SQLException {
        SchemaTester tester = new SchemaTester(guiConfig.getDataSource(), sql, schema);
        List<String> testSchema = tester.getTestSchema();
        if (Objects.isNull(schema)) {
            System.out.println("Kein erwartetes Schema angegeben.");

        } else {
            List<Boolean> result = tester.test(testSchema);
            if (Objects.isNull(result)) {
                System.out.println("Schema inkorrekt. Das Schema hat nicht die erwartete Länge " + schema.size() + ".");
            } else if (result.contains(false)) {
                System.out.println("Schema inkorrekt. Erwartet: " + schema + ", aber war: " + testSchema + ".");
            } else {
                System.out.println("Schema okay.");
            }
        }
        return 0;
    }
}
