package de.unifrankfurt.dbis.Submission;

import java.util.Objects;

public class TaskBody {

    private final String comment;
    private final String sql;

    public TaskBody(String comment, String sql) {
        this.comment = comment;
        this.sql = sql;
    }

    public String getComment() {
        return comment;
    }

    public String getSql() {
        return sql;
    }

    @Override
    public String toString() {
        return comment + '\n' + sql + '\n';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskBody taskBody = (TaskBody) o;
        return Objects.equals(comment, taskBody.comment) &&
                Objects.equals(sql, taskBody.sql);
    }

    @Override
    public int hashCode() {

        return Objects.hash(comment, sql);
    }
}
