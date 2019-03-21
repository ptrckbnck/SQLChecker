package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.SQL.SQLData;
import de.unifrankfurt.dbis.SQL.SQLResults;
import de.unifrankfurt.dbis.config.DataSource;

import java.util.List;
import java.util.StringJoiner;

public class TaskSQL implements TaskInterface {
    private final String name;
    private final Integer score;
    private final String group;
    private final List<String> schema;
    private final List<Integer> order;
    private final String sql;


    public TaskSQL(String name, Integer score, String group, List<String> schema, List<Integer> order, String sql) {
        this.name = name;
        this.score = score;
        this.group = group;
        this.schema = schema;
        this.order = order;
        this.sql = sql;
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

    public List<String> getSchema() {
        return schema;
    }

    public List<Integer> getOrder() {
        return order;
    }


    @Override
    public String serialize() {
        return new ParseTokenTask(name, score, group, schema, order, sql).serialize();
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
                .add("score=" + score)
                .add("group='" + group + "'")
                .add("schema=" + schema)
                .add("order=" + order)
                .add("sql='" + sql + "'")
                .toString();
    }
}
