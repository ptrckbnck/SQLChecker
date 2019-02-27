package de.unifrankfurt.dbis.SQL;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SQLResultTableMatcher {


    public static SQLResultTableDiff match(SQLDataTable expected,
                                           SQLDataTable actual,
                                           Collection<Integer> order) {
        order = Objects.isNull(order) ? new ArrayList<>() : order;

        if (!schemaMatch(expected, actual)) return null;
        final List<PairKeyIndiciesSet> keySplittedExpected = SQLResultTableMatcher.keySplit(expected, order);
        final List<PairKeyIndiciesSet> keySplittedActual = SQLResultTableMatcher.keySplit(actual, order);

        for (int i = 0; i < keySplittedExpected.size(); i++) {
            PairKeyIndiciesSet expectedPair = keySplittedExpected.get(i);
            if (keySplittedActual.size() <= i) { // no values with current key in actual data
                final List<Integer> rows = keySplittedExpected.get(i).getRowsAsList();
                return new SQLResultTableDiff(rows, List.of(), List.of(), rows, false);
            }
            final PairKeyIndiciesSet actualPair = keySplittedActual.get(i);


            if (!expectedPair.getRowValues(expected).equals(actualPair.getRowValues(actual))) { // values of current key do not match
                return new SQLResultTableDiff(expectedPair.getRowsAsList(),
                        actualPair.getRowsAsList(),
                        surplus(expected, actual, expectedPair, actualPair),
                        missing(expected, actual, expectedPair, actualPair),
                        false);
            }
        }
        if (keySplittedActual.size() > keySplittedExpected.size()) {
            final List<Integer> rows = keySplittedActual.get(keySplittedExpected.size()).getRowsAsList();
            return new SQLResultTableDiff(List.of(), rows, rows, List.of(), false);
        }
        return new SQLResultTableDiff(List.of(), List.of(), List.of(), List.of(), true); //ok
    }


    public static List<Integer> surplus(SQLDataTable expected,
                                        SQLDataTable actual,
                                        PairKeyIndiciesSet expectedPair,
                                        PairKeyIndiciesSet actualPair) {
        List<Integer> remainingLines = actualPair.getRowsAsList();
        for (Integer row : expectedPair.getRowsAsList()) {
            List<Object> expectedValue = expected.getData().get(row);
            for (Integer i : remainingLines) {
                final List<Object> actualValue = actual.getData().get(i);
                if (actualValue.equals(expectedValue)) {
                    remainingLines.remove(i);
                    break;
                }
            }
        }
        return remainingLines;
    }

    public static List<Integer> missing(SQLDataTable expected,
                                        SQLDataTable actual,
                                        PairKeyIndiciesSet expectedPair,
                                        PairKeyIndiciesSet actualPair) {
        List<Integer> remainingLines = actualPair.getRowsAsList();
        List<Integer> missing = new ArrayList<>();
        for (Integer row : expectedPair.getRowsAsList()) {
            List<Object> expectedValue = expected.getData().get(row);
            boolean found = false;
            for (Integer i : remainingLines) {
                final List<Object> actualValue = actual.getData().get(i);
                if (actualValue.equals(expectedValue)) {
                    remainingLines.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                missing.add(row);
            }

        }
        return missing;
    }

    /**
     * @param table
     * @param order
     * @return Liste von einer Menge von Indizes. Indizied beziehen sich auf rows des tables. Alle Objekte der Menge haben gleiche Keys.
     */
    public static List<PairKeyIndiciesSet> keySplit(SQLDataTable table,
                                                    Collection<Integer> order) {

        PairKeyIndiciesSet current = null;
        List<PairKeyIndiciesSet> result = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            List<Object> row = table.getData().get(i);
            List<Object> key = keyOfRow(row, order);
            if (Objects.isNull(current)) {
                current = new PairKeyIndiciesSet(key);
                current.addRow(i);
            } else if (key.equals(current.getKey())) {
                current.addRow(i);
            } else {
                result.add(current);
                current = new PairKeyIndiciesSet(key);
                current.addRow(i);
            }
        }
        if (!Objects.isNull(current)) {
            result.add(current);
        }
        return result;
    }

    private static List<Object> keyOfRow(List<Object> row, Collection<Integer> order) {
        return order.stream()
                .map(row::get)
                .collect(Collectors.toList());
    }


    public static boolean schemaMatch(SQLDataTable expected,
                                      SQLDataTable actual) {
        return (expected.getHeader()
                .equals(actual.getHeader()));


    }

    public static class PairKeyIndiciesSet {
        private final List<Object> key;
        private final Set<Integer> rows;

        public PairKeyIndiciesSet(List<Object> key) {
            this.key = key;
            this.rows = new HashSet<>();
        }

        public PairKeyIndiciesSet(List<Object> key, Set<Integer> rows) {
            this.key = key;
            this.rows = rows;
        }

        public List<Object> getKey() {
            return key;
        }

        public Set<Integer> getRows() {
            return rows;
        }

        public void addRow(Integer i) {
            this.rows.add(i);
        }

        public List<Integer> getRowsAsList() {
            return this.rows.stream().sorted().collect(Collectors.toList());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PairKeyIndiciesSet)) return false;
            PairKeyIndiciesSet that = (PairKeyIndiciesSet) o;
            return Objects.equals(key, that.key) &&
                    Objects.equals(rows, that.rows);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, rows);
        }

        public Stream<Integer> stream() {
            return rows.stream();
        }

        @Override
        public String toString() {
            return "PairKeyIndiciesSet{" +
                    "key=" + key +
                    ", rows=" + rows +
                    '}';
        }

        public Map<List<Object>, Long> getRowValues(SQLDataTable table) { //multiset
            return this.rows.stream().collect(Collectors.groupingBy(table.getData()::get, Collectors.counting()));
        }
    }
}
