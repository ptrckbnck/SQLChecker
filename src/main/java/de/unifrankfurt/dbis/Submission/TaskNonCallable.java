package de.unifrankfurt.dbis.Submission;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * TaskSQL Implementation normal SQL statements
 */
public class TaskNonCallable extends Task {

    public TaskNonCallable(Tag tag, String sql) {
        super(tag, sql);
    }

    @Override
    public String generateDBFitHtml(Statement statement) throws SQLException {
        return DBFitHtmlCreator.htmlCreateNonCallable(this, statement);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
