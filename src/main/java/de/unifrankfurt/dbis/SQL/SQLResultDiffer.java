package de.unifrankfurt.dbis.SQL;

import java.util.Collection;
import java.util.List;

public class SQLResultDiffer {

    public static SQLResultDiff diff(SQLData expectedResult, SQLData actualResult) {
        return SQLResultDiffer.diff(expectedResult, actualResult, List.of());
    }

    public static SQLResultDiff diff(SQLData expectedResult, SQLData actualResult, Collection<Integer> order) {
        try {
            if (expectedResult.getClass() != actualResult.getClass()) {
                return new SQLResultDiffTypeMismatch(expectedResult, actualResult);
            }
            if (expectedResult.getClass() == SQLDataTable.class) {
                SQLDataTable expectedTable = (SQLDataTable) expectedResult;
                SQLDataTable actualTable = (SQLDataTable) actualResult;
                if (!expectedTable.getHeader().equals(actualTable.getHeader())) {
                    return new SQLResultDiffSchemaMismatch(expectedTable, actualTable);
                }
                if (haveConflictingTypes(expectedTable.getType(), actualTable.getType())) {
                    return new SQLResultDiffTableTypeMismatch(expectedTable, actualTable);
                }

                SQLResultTableDiff matched = SQLResultTableMatcher.match(expectedTable, actualTable, order);
                if (!matched.isValid()) {
                    return new SQLResultDiffSchemaMismatch(expectedTable, actualTable); //should not be reached
                }
                if (!matched.isOk()) {
                    return new SQLResultDiffTableDataMismatch(expectedTable, actualTable, matched);
                }
            }
            return new SQLResultDiffOK();
        } catch (Exception e) {
            return new SQLResultDiffException(e);
        }
    }


    static boolean haveConflictingTypes(List<String> expectedType, List<String> actualType) {
        //currently no type testing. maybe later
        return false;
    }

}
