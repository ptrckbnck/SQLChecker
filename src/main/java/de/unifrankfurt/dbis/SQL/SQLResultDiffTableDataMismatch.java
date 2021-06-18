package de.unifrankfurt.dbis.SQL;

import de.vandermeer.asciitable.AsciiTable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SQLResultDiffTableDataMismatch implements SQLResultDiff {
    private final SQLDataTable expected;
    private final SQLDataTable actual;
    private final SQLResultTableDiff diff;

    public SQLResultDiffTableDataMismatch(SQLDataTable expected, SQLDataTable actual, SQLResultTableDiff diff) {
        this.expected = expected;
        this.actual = actual;
        this.diff = diff;
    }


    private String lines() {
        final List<Integer> lines = diff.getLinesExpected();
        if (lines.isEmpty()) {
            return "no lines"; //should not happen
        }
        if (lines.size() == 1) {
            return lines.get(0).toString();
        }
        return lines.get(0).toString() + " - " + lines.get(lines.size() - 1);
    }

    @Override
    public String getMessage() {
        return "Data mismatch in line(s): " + lines() + "\nExpected:\n" + render(lineNrsToData(diff.getLinesExpected(), expected.getData()))
                + "\nActual:\n" + render(lineNrsToData(diff.getLinesActual(), actual.getData()))
                + missing()
                + surplus();
    }


    private String missing() {
        final List<Integer> missing = this.diff.getMissing();
        //if (missing.isEmpty() || missing.equals(this.diff.getLinesExpected())) return "";
        if (missing.isEmpty()) return "";
        return "\nMissing:\n" + render(lineNrsToData(missing, expected.getData()));
    }

    private String surplus() {
        final List<Integer> surplus = this.diff.getSurplus();
        //if (surplus.isEmpty() || surplus.equals(this.diff.getLinesActual())) return "";
        if (surplus.isEmpty()) return "";
        return "\nSurplus:\n" + render(lineNrsToData(diff.getSurplus(), actual.getData()));
    }

    @Override
    public Boolean isOk() {
        return false;
    }

    @Override
    public String getMinimalMessage() {
        return "Data mismatch in line(s): " + lines()
                + missing()
                + surplus();
    }


    public List<List<Object>> lineNrsToData(List<Integer> list, List<List<Object>> data) {
        return list.stream().map(data::get).collect(Collectors.toList());
    }

    public String render(Collection<List<Object>> data) {
        if (data.isEmpty()) return "EMPTY";
        AsciiTable at = new AsciiTable();
        at.addRule();
        for (List<Object> row : data) {
            at.addRow(row.stream().map(o -> (Objects.isNull(o) || !o.getClass().isAssignableFrom(String.class)) ? Objects.toString(o) : "'" + o + "'").collect(Collectors.toList())); //TODO Better toString representation, custom SQLDataobject?
        }
        at.addRule();
        try {
            return at.render();
        } catch (IllegalArgumentException ex) {
            return "Could not render Table: " + ex.getMessage();
        }
    }
}
