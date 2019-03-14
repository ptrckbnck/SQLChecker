package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.SQL.SQLData;
import de.unifrankfurt.dbis.SQL.SQLResults;
import de.unifrankfurt.dbis.config.DataSource;

import java.util.List;
import java.util.StringJoiner;

public class TaskSQL implements TaskInterface {
    private final String name;
    private final List<String> scheme;
    private final List<Integer> order;
    private final String sql;

    public TaskSQL(String name, List<String> scheme, List<Integer> order, String sql) {
        this.name = name;
        this.scheme = scheme;
        this.order = order;
        this.sql = sql;
    }

    public List<String> getScheme() {
        return scheme;
    }

    public List<Integer> getOrder() {
        return order;
    }

    public String getSQL() {
        return sql;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String serialize() {
        return new SubTokenTask(name, scheme, order, sql).serialize();
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public SQLData execute(DataSource source) {
        return SQLResults.execute(source, sql);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TaskSQL.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("scheme=" + scheme)
                .add("order=" + order)
                .add("sql='" + sql + "'")
                .toString();
    }
}
