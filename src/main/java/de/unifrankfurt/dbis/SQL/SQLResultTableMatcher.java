package de.unifrankfurt.dbis.SQL;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SQLResultTableMatcher {


    public static SQLResultTableDiff match(SQLResultTable expected,
                                           SQLResultTable actual,
                                           Collection<Integer> order) {
        order = !Objects.isNull(order) ? new ArrayList<>() : order;

        if (!schemaMatch(expected, actual)) return null;
        List<PairKeyIndiciesSet> keySplittedExpected = SQLResultTableMatcher.keySplit(expected, order);
        List<PairKeyIndiciesSet> keySplittedActual = SQLResultTableMatcher.keySplit(actual, order);

        for (int i = 0; i < keySplittedExpected.size(); i++) {
            PairKeyIndiciesSet expectedPair = keySplittedExpected.get(i);
            if (keySplittedActual.size() <= i) {

                return new SQLResultTableDiff(expectedPair.getRowsAsList(), List.of());
            }
            PairKeyIndiciesSet actualPair = keySplittedActual.get(i);
            if (!expectedPair.equals(actualPair)) {
                return new SQLResultTableDiff(expectedPair.getRowsAsList(), actualPair.getRowsAsList());
            }

            if (!expectedPair.getRowValues(expected).equals(actualPair.getRowValues(actual))) {
                return new SQLResultTableDiff(expectedPair.getRowsAsList(), actualPair.getRowsAsList());
            }
        }
        if (keySplittedActual.size() > keySplittedExpected.size()) {
            return new SQLResultTableDiff(List.of(), keySplittedActual.get(keySplittedExpected.size() + 1).getRowsAsList()); //trailing rows
        }
        System.err.println(keySplittedActual);
        System.err.println(keySplittedExpected);//TODO remove
        return new SQLResultTableDiff(); //ok
    }


    /**
     * @param table
     * @param order
     * @return Liste von einer Menge von Indizes. Indizied beziehen sich auf rows des tables. Alle Objekte der Menge haben gleiche Keys.
     */
    public static List<PairKeyIndiciesSet> keySplit(SQLResultTable table,
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


    public static boolean schemaMatch(SQLResultTable expected,
                                      SQLResultTable actual) {
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

        public Set<List<Object>> getRowValues(SQLResultTable table) {
            return this.getRows().stream().map(i -> table.getData().get(i)).collect(Collectors.toSet());
        }
    }
}
