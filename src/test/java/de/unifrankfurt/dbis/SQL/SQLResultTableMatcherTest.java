package de.unifrankfurt.dbis.SQL;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLResultTableMatcherTest {

    @Test
    void matched() {
        SQLDataTable expectedTable = new SQLDataTable(List.of("a", "b"),
                List.of("test", "test"),
                List.of(List.of(1, 2),
                        List.of(3, 4)));
        SQLResultTableDiff result = SQLResultTableMatcher.match(expectedTable, expectedTable);
        assertTrue(result.isOk());

    }

    @Test
    void matchedFailedOrdered() {
        SQLDataTable expectedTable = new SQLDataTable(List.of("a", "b"),
                List.of("test", "test"),
                List.of(List.of(1, 2),
                        List.of(3, 4)));
        SQLDataTable actualTable = new SQLDataTable(List.of("a", "b"),
                List.of("test", "test"),
                List.of(List.of(3, 4),
                        List.of(1, 2)));
        SQLResultTableDiff result = SQLResultTableMatcher.match(expectedTable, actualTable, List.of(0));
        assertFalse(result.isOk());
        System.err.println(new SQLResultDiffTableDataMismatch(expectedTable, actualTable, result).getMessage());
    }


    @Test
    void matchFailed() {
        SQLDataTable expectedTable = new SQLDataTable(List.of("a", "b"),
                List.of("test", "test"),
                List.of(List.of(1, 2),
                        List.of(3, 4)));
        SQLDataTable actualTable = new SQLDataTable(List.of("a", "b"),
                List.of("test", "test"),
                List.of(List.of(2, 2),
                        List.of(3, 4)));
        SQLResultTableDiff result = SQLResultTableMatcher.match(expectedTable, actualTable);
        assertFalse(result.isOk());
        System.err.println(new SQLResultDiffTableDataMismatch(expectedTable, actualTable, result).getMessage());
    }

    /**
     * testet Aufgabe 5 des Beispiels aus /examples
     */
    @Test
    void exampleTest() {
        SQLDataTable actualTable = new SQLDataTable(
                List.of("FlightNo", "PlaneID"),
                List.of("String", "String"),
                List.of(List.of("LH1354", "1"),
                        List.of("IBE3843", "2"),
                        List.of("LH3842", "4"))
        );
        SQLDataTable expectedTable = new SQLDataTable(
                List.of("FlightNo", "PlaneID"),
                List.of("String", "String"),
                List.of(List.of("IBE3843", "2"),
                        List.of("LH1354", "1"),
                        List.of("LH3842", "4"))
        );
        SQLResultTableDiff result = SQLResultTableMatcher.match(expectedTable, actualTable);
        assertTrue(result.isOk());
        result = SQLResultTableMatcher.match(expectedTable, actualTable, List.of(0));
        assertFalse(result.isOk());
    }
}