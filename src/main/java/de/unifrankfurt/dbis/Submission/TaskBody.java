package de.unifrankfurt.dbis.Submission;

import java.util.Objects;

public class TaskBody {


    private final String sql;

    public TaskBody(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    @Override
    public String toString() {
        return sql;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskBody)) return false;
        TaskBody taskBody = (TaskBody) o;
        return Objects.equals(sql, taskBody.sql);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sql);
    }
}
