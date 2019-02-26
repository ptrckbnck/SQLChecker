package de.unifrankfurt.dbis.SQL;

import de.vandermeer.asciitable.AsciiTable;

import java.util.*;
import java.util.stream.Collectors;

public class SQLResultDiffTableDataMismatch implements SQLResultDiff {
    private final SQLResultTable expected;
    private final SQLResultTable actual;
    private final SQLResultTableDiff diff;

    public SQLResultDiffTableDataMismatch(SQLResultTable expected, SQLResultTable actual, SQLResultTableDiff diff) {
        this.expected = expected;
        this.actual = actual;
        this.diff = diff;
    }


    @Override
    public String getMessage() {
        List<Integer> a = diff.getActual();
        List<Integer> e = diff.getExpected();
        Map<List<Object>, Integer> aData = getDataOfRows(diff.getActual(), actual);
        Map<List<Object>, Integer> eData = getDataOfRows(diff.getExpected(), expected);
    }

    private Map<List<Object>, Integer> getDataOfRows(List<Integer> rows, SQLResultTable table) {

        return rows.stream().map(i -> table.getData().get(i)).collect(gr
    });
}

    private String getString(List<Integer> lines, Set<List<Object>> expectedData, Set<List<Object>> actualData) {
        missing = expectedData.removeAll(actualData);
        surplus = //TODO is multiset
        return "Data mismatch in line(s) " + lines.toString() + " . Expected :\n" + render(expectedData)
                + "Missing:\n" + render(expectedData.)
                + "Surplus:\n";
    }

    public String render(Collection<List<Object>> data) {
        AsciiTable at = new AsciiTable();
        at.addRule();
        for (List<Object> row : data) {
            at.addRow(row.stream().map(Objects::toString).collect(Collectors.toList()));
        }
        at.addRule();
        return at.render();
    }

    @Override
    public Boolean isOk() {
        return false;
    }
}
