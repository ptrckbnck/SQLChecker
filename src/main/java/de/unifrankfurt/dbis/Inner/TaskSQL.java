package de.unifrankfurt.dbis.Inner;

import java.util.List;

/**
 * TaskSQL Implementation normal SQL statements
 */
public class TaskSQL extends Task {
    final List<String> schema;

    public TaskSQL(Tag tag, List<String> schema, String sql) {
        super(tag, sql);
        this.schema = schema;
    }

    public TaskSQL(Tag tag, String sql) {
        this(tag, null, sql);
    }


    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public List<String> getSchema() {
        return schema;
    }
}
