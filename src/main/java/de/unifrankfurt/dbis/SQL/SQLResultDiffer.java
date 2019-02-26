package de.unifrankfurt.dbis.SQL;

import java.util.List;
import java.util.Objects;

public class SQLResultDiffer {

    public static SQLResultDiff diff(SQLResult expectedResult, SQLResult actualResult) {
        return SQLResultDiffer.diff(expectedResult, actualResult, null);
    }

    static SQLResultDiff diff(SQLResult expectedResult, SQLResult actualResult, List<Integer> order) {
        try {
            if (expectedResult.getClass() != actualResult.getClass()) {
                return new SQLResultDiffTypeMismatch(expectedResult, actualResult);
            }
            if (expectedResult.getClass() == SQLResultTable.class) {
                SQLResultTable expectedTable = (SQLResultTable) expectedResult;
                SQLResultTable actualTable = (SQLResultTable) actualResult;
                if (!expectedTable.getHeader().equals(actualTable.getHeader())) {
                    return new SQLResultDiffSchemaMismatch(expectedTable, actualTable);
                }
                if (haveConflictingTypes(expectedTable.getType(), actualTable.getType())) {
                    return new SQLResultDiffTableTypeMismatch(expectedTable, actualTable);
                }

                if (Objects.isNull(order)) {
                    order = List.of();
                }
                SQLResultTableDiff matched = SQLResultTableMatcher.match(expectedTable, actualTable, order);
                System.err.println(matched);
                if (Objects.isNull(matched)) {
                    return new SQLResultDiffSchemaMismatch(expectedTable, actualTable); //should not be reached
                }
                if (!matched.isOk()) {
                    return new SQLResultDiffTableDataMismatch(expectedTable, actualTable, matched);
                }
                return new SQLResultOK();
            }
            return new SQLResultOK();
        } catch (Exception e) {
            return new SQLResultDiffException(e);
        }
    }


    static boolean haveConflictingTypes(List<String> expectedType, List<String> actualType) {
        /*for (int i = 0; i < expectedType.size(); i++){
            if (!expectedType.get(i).equals(actualType.get(i))){ //TODO Typen weniger streng testen
                return true;
            }
        }*/
        return false;
    }

}
