package de.unifrankfurt.dbis.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Builder class for SQLResultWrapper
 * ResultSet normally has only one Result.
 * But if an function or procedure is executed it can have multiple.
 * So an list of Results are needed.
 */
public class SQLResultWrapperBuilder {
    private ArrayList<Object> list;

    public SQLResultWrapperBuilder() {
        this.list = new ArrayList<>();
    }

    public void add(ResultSet resultSet) throws SQLException {
        list.add(SQLResultTable.fromResultSet(resultSet));
    }

    public void add(Integer updateCount) {
        list.add(updateCount);
    }

    public SQLResultWrapper build() {
        return new SQLResultWrapper(list);
    }
}
