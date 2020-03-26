package de.unifrankfurt.dbis.SQL;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SQLDataTableTest {

    @Test
    public void toStringTest() {
        SQLDataTable rt = new SQLDataTable(
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