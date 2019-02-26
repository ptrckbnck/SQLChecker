package de.unifrankfurt.dbis.SQL;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SQLResultTableTest {

    @Test
    public void toStringTest() {
        SQLResultTable rt = new SQLResultTable(
                List.of("a", "b", "c"),
                List.of("String", "String", "String"),
                List.of(
                        List.of("a1", "b1", "c1"),
                        List.of("a2", "b2", "c2")
                ));
        String expected =
                "┌──────────────────────────┬─────────────────────────┬─────────────────────────┐\n" +
                        "│a                         │b                        │c                        │\n" +
                        "├──────────────────────────┼─────────────────────────┼─────────────────────────┤\n" +
                        "│a1                        │b1                       │c1                       │\n" +
                        "│a2                        │b2                       │c2                       │\n" +
                        "└──────────────────────────┴─────────────────────────┴─────────────────────────┘";
        assertEquals(expected, rt.toString());
    }
}