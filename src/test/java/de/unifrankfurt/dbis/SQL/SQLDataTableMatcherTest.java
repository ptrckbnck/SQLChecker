package de.unifrankfurt.dbis.SQL;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SQLDataTableMatcherTest {

    @Test
    void match() {
    }

    @Test
    void printKeySplit() {
    }

    @Test
    void keySplit() {
        SQLDataTable table = new SQLDataTable(List.of("0", "1", "2", "3", "4", "5"),
                List.of("", "", ""),
                List.of(List.of("1", "1", "2", "1", "2", "3"),
                        List.of("1", "1", "2", "2", "3", "4"),
                        List.of("1", "2", "2", "1", "2", "3"),
                        List.of("1", "2", "3", "x", "x", "x")));
        List<SQLResultTableMatcher.PairKeyIndiciesSet> splitted = SQLResultTableMatcher.keySplit(table, List.of());
        assertEquals(List.of(new SQLResultTableMatcher.PairKeyIndiciesSet(List.of(), Set.of(0, 1, 2, 3))), splitted);

        splitted = SQLResultTableMatcher.keySplit(table, List.of(0));
        assertEquals(List.of(new SQLResultTableMatcher.PairKeyIndiciesSet(List.of("1"), Set.of(0, 1, 2, 3))), splitted);

        splitted = SQLResultTableMatcher.keySplit(table, List.of(0, 1));
        assertEquals(List.of(
                new SQLResultTableMatcher.PairKeyIndiciesSet(List.of("1", "1"), Set.of(0, 1)),
                new SQLResultTableMatcher.PairKeyIndiciesSet(List.of("1", "2"), Set.of(2, 3))),
                splitted);

        splitted = SQLResultTableMatcher.keySplit(table, List.of(0, 1, 2));
        assertEquals(List.of(
                new SQLResultTableMatcher.PairKeyIndiciesSet(List.of("1", "1", "2"), Set.of(0, 1)),
                new SQLResultTableMatcher.PairKeyIndiciesSet(List.of("1", "2", "2"), Set.of(2)),
                new SQLResultTableMatcher.PairKeyIndiciesSet(List.of("1", "2", "3"), Set.of(3))),
                splitted);

        table = new SQLDataTable(List.of("0", "1", "2", "3", "4", "5"),
                List.of("", "", "", "", "", ""), List.of());
        splitted = SQLResultTableMatcher.keySplit(table, List.of(0, 1, 2));
        assertEquals(List.of(), splitted);
    }

    @Test
    void schemaMatch() {
    }
}