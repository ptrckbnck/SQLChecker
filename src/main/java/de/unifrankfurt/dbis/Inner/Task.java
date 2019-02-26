package de.unifrankfurt.dbis.Inner;

import de.unifrankfurt.dbis.SQL.SQLResult;
import de.unifrankfurt.dbis.SQL.SQLResults;
import de.unifrankfurt.dbis.config.DataSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Base class for every Task that wants to utilize DBFit.
 */
public abstract class Task {
    /**
     * the Tag that identifies this Task
     */
    private final Tag tag;


    private final String sql;


    public Task(Tag tag, String sql) {
        this.tag = tag;
        this.sql = sql;
    }

    public Tag getTag() {
        return tag;
    }

    /**
     * Parses a SubmissionToken and creates TaskSQl accordingly.
     *
     * @param token Inner Token that should be parsed.
     * @return TaskSQL
     */
    public static Task parseToken(SubmissionToken token) {
        TaskBody body = new TaskBody(token.getBody());
        String addition = token.getAddition();
        return new TaskSQL(token.getTag(), parseSchema(addition), body.getSql());
    }

    public static List<String> parseSchema(String string) {
        if (Objects.isNull(string)) return null;
        if (!(string.startsWith("[") && string.endsWith("]"))) return null;
        string = string.substring(1, string.length() - 1);
        String[] splited = string.split(",");
        return Arrays.stream(splited).map(String::trim).collect(Collectors.toList());
    }
    /**
     * Concatenation of Commentary and every SQL commands, separatedby "\n".
     *
     * @return String
     */
    public String getSql() {
        return sql;
    }


    /**
     * TaskSQL is static if Tag.name is "static.error" or "static".
     *
     * @return return true if static.
     */
    public boolean isStatic() {
        return tag.isStatic();
    }


    public String serialize() {
        return this.tag.serialized() + "\n" + this.getSql();
    }

    public String toString() {
        return "{" + this.tag.toString() + ", " + this.sql + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return tag.equals(task.tag) &&
                sql.equals(task.sql);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, sql);
    }

    public SQLResult execute(DataSource dataSource) {
        return SQLResults.execute(dataSource, this.getSql());
    }
}
