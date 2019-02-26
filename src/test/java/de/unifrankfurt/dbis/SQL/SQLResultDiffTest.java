package de.unifrankfurt.dbis.SQL;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLResultDiffTest {


    @Test
    void diffEqual() {
        SQLResult expectedResult = new SQLResultTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("2", "2", "3"),
                        List.of("3", "2", "3"),
                        List.of("4", "2", "3")));

        SQLResultDiff a = SQLResultDiffer.diff(expectedResult, expectedResult);
        assertTrue(a.isOk());
    }

    @Test
    void diffUnorederedEqual() {
        SQLResult expectedResult = new SQLResultTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("2", "2", "3"),
                        List.of("3", "2", "3"),
                        List.of("4", "2", "3")));
        SQLResult actualResult = new SQLResultTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("2", "2", "3"),
                        List.of("4", "2", "3"),
                        List.of("3", "2", "3")
                ));
        SQLResultDiff a = SQLResultDiffer.diff(expectedResult, actualResult);
    }

    //TODO implement TYPE matcher
    void diffTypeMismatch() {
        SQLResult expectedResult = new SQLResultTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "INTEGER", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("2", "2", "3"),
                        List.of("3", "2", "3"),
                        List.of("4", "2", "3")));
        SQLResult actualResult = new SQLResultTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("2", "2", "3"),
                        List.of("4", "2", "3"),
                        List.of("3", "2", "3")
                ));
        SQLResultDiff a = SQLResultDiffer.diff(expectedResult, actualResult);
        assertFalse(a.isOk());
        assertTrue(a.getClass().isAssignableFrom(SQLResultDiffTypeMismatch.class));
    }

    @Test
    void diffSchemaMismatch() {
        SQLResult expectedResult = new SQLResultTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("2", "2", "3"),
                        List.of("3", "2", "3"),
                        List.of("4", "2", "3")));
        SQLResult actualResult = new SQLResultTable(
                List.of("a", "b", "d"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("2", "2", "3"),
                        List.of("4", "2", "3"),
                        List.of("3", "2", "3")
                ));
        SQLResultDiff a = SQLResultDiffer.diff(expectedResult, actualResult);
        System.out.println(a.getMessage());
        assertFalse(a.isOk());
        assertTrue(a.getClass().isAssignableFrom(SQLResultDiffSchemaMismatch.class));
    }

    @Test
    void diffDataMismatch1() {
        SQLResult expectedResult = new SQLResultTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("2", "2", "3"),
                        List.of("3", "2", "3"),
                        List.of("4", "2", "3")));
        SQLResult actualResult = new SQLResultTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("2", "2", "3"),
                        List.of("4", "2", "3"),
                        List.of("3", "3", "2")
                ));
        SQLResultDiff a = SQLResultDiffer.diff(expectedResult, actualResult);
        System.out.println(a.getClass());
        System.out.println(a.getMessage());
        assertFalse(a.isOk());
        assertTrue(a.getClass().isAssignableFrom(SQLResultDiffTableDataMismatch.class));
    }

}