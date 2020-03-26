package de.unifrankfurt.dbis.SQL;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLDataDiffTest {


    @Test
    void diffEqual() {
        SQLData expectedResult = new SQLDataTable(
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
        SQLData expectedResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("2", "2", "3"),
                        List.of("3", "2", "3"),
                        List.of("4", "2", "3")));
        SQLData actualResult = new SQLDataTable(
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
        SQLData expectedResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "INTEGER", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("2", "2", "3"),
                        List.of("3", "2", "3"),
                        List.of("4", "2", "3")));
        SQLData actualResult = new SQLDataTable(
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
        SQLData expectedResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("2", "2", "3"),
                        List.of("3", "2", "3"),
                        List.of("4", "2", "3")));
        SQLData actualResult = new SQLDataTable(
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
        SQLData expectedResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("2", "2", "3"),
                        List.of("3", "2", "3"),
                        List.of("4", "2", "3")));
        SQLData actualResult = new SQLDataTable(
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

    @Test
    void diffDataMismatch2() {
        SQLData expectedResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("1", "2", "3"),
                        List.of("1", "2", "3"),
                        List.of("1", "2", "3")
                ));
        SQLData actualResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("1", "2", "3"),
                        List.of("1", "2", "3"),
                        List.of("1", "2", "3")
                ));
        SQLResultDiff a = SQLResultDiffer.diff(expectedResult, actualResult);
        System.out.println(a.getClass());
        System.out.println(a.getMessage());
        assertFalse(a.isOk());
        assertTrue(a.getClass().isAssignableFrom(SQLResultDiffTableDataMismatch.class));
    }

    @Test
    void diffDataMismatch3() {
        SQLData expectedResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("0", "0", "0"),
                        List.of("1", "2", "3"),
                        List.of("1", "2", "3"),
                        List.of("1", "2", "3"),
                        List.of("1", "2", "3")
                ));
        SQLData actualResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("0", "0", "0"),
                        List.of("1", "2", "3"),
                        List.of("1", "2", "3"),
                        List.of("1", "2", "3")
                ));
        SQLResultDiff a = SQLResultDiffer.diff(expectedResult, actualResult, List.of(0));
        System.out.println(a.getClass());
        System.out.println(a.getMessage());
        assertFalse(a.isOk());
        assertTrue(a.getClass().isAssignableFrom(SQLResultDiffTableDataMismatch.class));
    }

    @Test
    void diffDataMismatch4() {
        SQLData expectedResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("0", "0", "0"),
                        List.of("1", "2", "3"),
                        List.of("1", "2", "3"),
                        List.of("2", "2", "2")
                ));
        SQLData actualResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("0", "0", "0"),
                        List.of("1", "2", "3"),
                        List.of("1", "2", "3"),
                        List.of("1", "2", "3"),
                        List.of("2", "2", "2")
                ));
        SQLResultDiff a = SQLResultDiffer.diff(expectedResult, actualResult, List.of(0));
        System.out.println(a.getClass());
        System.out.println(a.getMessage());
        assertFalse(a.isOk());
        assertTrue(a.getClass().isAssignableFrom(SQLResultDiffTableDataMismatch.class));
    }

    @Test
    void diffDataMismatch5() {
        SQLData expectedResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("0", "0", "0"),
                        List.of("1", "2", "3"),
                        List.of("1", "2", "3"),
                        List.of("2", "2", "2")
                ));

        ArrayList<Object> nullList = new ArrayList<>();
        nullList.add("1");
        nullList.add(null);
        nullList.add("3");
        SQLData actualResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("0", "0", "0"),
                        List.of("1", "2", "3"),
                        nullList,
                        List.of("2", "2", "2")
                ));
        SQLResultDiff a = SQLResultDiffer.diff(expectedResult, actualResult, List.of(0));
        System.out.println(a.getClass());
        System.out.println(a.getMessage());
        assertFalse(a.isOk());
        assertTrue(a.getClass().isAssignableFrom(SQLResultDiffTableDataMismatch.class));
    }

    @Test
    void diffDataMismatch6() {
        SQLData expectedResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("0", "0", "0"),
                        List.of("1", "2", "3")
                ));
        SQLData actualResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("0", "0", "0"),
                        List.of(1, 2, 3)
                ));
        SQLResultDiff a = SQLResultDiffer.diff(expectedResult, actualResult, List.of(0));
        System.out.println(a.getClass());
        System.out.println(a.getMessage());
        assertFalse(a.isOk());
        assertTrue(a.getClass().isAssignableFrom(SQLResultDiffTableDataMismatch.class));
    }

    @Test
    void diffDataMismatch7() {
        SQLData expectedResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("0", "0", "0"),
                        List.of("1", "2", "3")
                ));
        SQLData actualResult = new SQLDataTable(
                List.of("a", "b", "c"),
                List.of("VARCHAR", "VARCHAR", "VARCHAR"),
                List.of(List.of("0", "0", "0"),
                        List.of("1", "2", "")
                ));
        SQLResultDiff a = SQLResultDiffer.diff(expectedResult, actualResult, List.of(0));
        System.out.println(a.getClass());
        System.out.println(a.getMessage());
        assertFalse(a.isOk());
        assertTrue(a.getClass().isAssignableFrom(SQLResultDiffTableDataMismatch.class));
    }
}