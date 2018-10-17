package de.unifrankfurt.dbis.SQL;

import de.vandermeer.asciitable.AsciiTable;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * converts the ResultSet in a easily readable 2dim String List.
 */
public class SQLResultTable {
    List<String> header;
    List<List<String>> data;

    SQLResultTable(List<String> header, List<List<String>> data) {
        this.header = header;
        this.data = data;
    }


    /**
     * Creates SQLResultTable from ResultSet.
     *
     * @return SQLResultTable
     * @throws SQLException @see ResultSet#getMetaData()
     */
    public static SQLResultTable fromResultSet(ResultSet resultSet) throws SQLException {
        List<List<String>> table = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<String> head = new ArrayList<>();

        //create header
        for (int i = 0; i < columnCount; i++)
            head.add(metaData.getColumnLabel(i + 1));

        //data
        while (resultSet.next()) {
            List<String> values = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
                values.add(resultSet.getString(i + 1));
            }
            table.add(values);
        }
        return new SQLResultTable(head, table);
    }

    /**
     * creates a Html String used for DBFIT Solution.
     * It basically surrounds every value with <td></td>
     * and surround that with <tr></tr>.
     *
     * @return String of html
     */
    public String convertToDBFitHtml() {
        StringBuilder html = new StringBuilder();

        html.append("\t<tr>\n");
        for (String relem : this.header) {
            html.append("\t\t<td>").append(relem).append("</td>\n");
        }
        html.append("\t</tr>\n");

        for (List<String> row : this.data) {
            html.append("\t<tr>\n");
            for (String relem : row) {
                html.append("\t\t<td>").append(relem).append("</td>\n");
            }
            html.append("\t</tr>\n");
        }
        return html.toString();
    }

    @Override
    public String toString() {
        if (data.isEmpty()) return "leere Menge";
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow(this.header);
        at.addRule();
        for (List<String> row : this.data) {
            at.addRow(row);
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

    public List<List<String>> getData() {
        return data;
    }
}
