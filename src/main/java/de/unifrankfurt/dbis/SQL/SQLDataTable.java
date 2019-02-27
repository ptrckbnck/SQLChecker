package de.unifrankfurt.dbis.SQL;

import de.vandermeer.asciitable.AsciiTable;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * converts the ResultSet in a easily readable 2dim String List.
 */
public class SQLDataTable implements SQLData {
    private final List<String> header;
    private final List<String> type;
    private final List<List<Object>> data;

    public SQLDataTable(List<String> header, List<String> type, List<List<Object>> data) {
        this.header = header;
        this.type = type;
        this.data = data;
    }


    /**
     * Creates SQLResultTable from ResultSet.
     *
     * @return SQLResultTable
     * @throws SQLException @see ResultSet#getMetaData()
     */
    public static SQLDataTable fromResultSet(ResultSet resultSet) throws SQLException {
        List<List<Object>> table = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<String> head = new ArrayList<>();
        List<String> type = new ArrayList<>();

        //create header & type
        for (int i = 0; i < columnCount; i++) {
            head.add(metaData.getColumnLabel(i + 1));
            type.add(metaData.getColumnTypeName(i + 1));
        }

        //data
        while (resultSet.next()) {
            List<Object> values = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
                values.add(resultSet.getObject(i + 1));
            }
            table.add(values);
        }
        return new SQLDataTable(head, type, table);
    }



    @Override
    public String toString() {
        if (data.isEmpty()) return "leere Menge";
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow(this.header);
        at.addRule();
        for (List<Object> row : this.data) {
            at.addRow(row.stream().map(Objects::toString).collect(Collectors.toList()));
        }
        at.addRule();
        return at.render();
    }


    /**
     * @return true if result table is not empty.
     */
    public boolean isEmpty() {
        return header.isEmpty();
    }

    /**
     * number of values per entry
     *
     * @return Count
     */
    public Integer headerSize() {
        return this.header.size();
    }

    /**
     * number of entries
     *
     * @return Count
     */
    public int size() {
        return this.data.size();
    }

    public List<List<Object>> getData() {
        return data;
    }

    public List<String> getHeader() {
        return header;
    }

    public List<String> getType() {
        return type;
    }

    @Override
    public boolean failed() {
        return false;
    }
}
