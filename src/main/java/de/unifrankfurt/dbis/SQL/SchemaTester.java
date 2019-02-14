package de.unifrankfurt.dbis.SQL;

import de.unifrankfurt.dbis.config.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SchemaTester {

    private final DataSource source;
    private final String sql;
    private final List<String> schema;

    public SchemaTester(DataSource source, String sql, List<String> schema) {
        this.source = source;
        this.sql = sql;
        this.schema = schema;
    }


    public List<String> getTestSchema() throws SQLException {
        try (Connection connection = source.getConnection()) {
            try (Statement s = connection.createStatement()) {
                SQLResultWrapper result = SQLResultWrapper.executeStatement(s, sql);
                return result.getHeader();
            } catch (SQLException e) {
                throw e;
            }
        }
    }

    public List<Boolean> diffHeaders(List<String> expectedHeader, List<String> testHeader) {
        if (expectedHeader.size() != testHeader.size()) {
            return null;
        }
        ArrayList<Boolean> diff = new ArrayList<>();
        for (int i = 0; i < expectedHeader.size(); i++) {
            diff.add(expectedHeader.get(i).equals(testHeader.get(i)));
        }
        return diff;
    }

    public List<Boolean> test(List<String> testHeader) {
        return diffHeaders(schema, testHeader);
    }
}
