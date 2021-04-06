package de.unifrankfurt.dbis.StudentGUI;

import de.unifrankfurt.dbis.config.DataSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SchemaTester {
    private final DataSource dataSource;
    private final List<String> schema;
    private final String sql;

    public SchemaTester(DataSource dataSource, String sql, List<String> schema) {
        this.dataSource = dataSource;
        this.sql = sql;
        this.schema = schema;
    }

    /**
     * assumes lists have same length
     *
     * @param s1
     * @param s2
     * @return
     */
    static List<Boolean> listEquals(List<?> s1, List<?> s2) {
        Iterator<?> iter1 = s1.iterator();
        Iterator<?> iter2 = s2.iterator();
        ArrayList<Boolean> bools = new ArrayList<>();
        while (iter1.hasNext() && iter2.hasNext())
            bools.add(iter1.next().equals(iter2.next()));
        return bools;
    }

    public List<Boolean> test(List<String> header) {
        return listEquals(schema, header);
    }


}
