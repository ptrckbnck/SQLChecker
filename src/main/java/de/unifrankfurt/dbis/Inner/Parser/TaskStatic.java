package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.SQL.SQLData;
import de.unifrankfurt.dbis.SQL.SQLResults;
import de.unifrankfurt.dbis.config.DataSource;

import java.util.List;
import java.util.StringJoiner;

public class TaskStatic implements TaskInterface {
    private final String name;
    private final Integer score;
    private final String group;
    private final List<Integer> order;
    private final String sql;

    public TaskStatic(String name, Integer score, String group, List<Integer> order, String sql) {
        this.name = name;
        this.score = score;
        this.group = group;
        this.order = order;
        this.sql = sql;
    }

    public List<Integer> getOrder() {
        return order;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSql() {
        return sql;
    }

    public Integer getScore() {
        return score;
    }

    public String getGroup() {
        return group;
    }


    @Override
    public String serialize() {
        return new ParseTokenStatic(name, score, group, order, sql).serialize();
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public SQLData execute(DataSource source) {
        return SQLResults.execute(source, sql);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TaskStatic.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("order=" + order)
                .add("sql='" + sql + "'")
                .toString();
    }
}
