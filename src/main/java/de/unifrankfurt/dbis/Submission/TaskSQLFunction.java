package de.unifrankfurt.dbis.Submission;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * TaskSQL Implementation of Function TODO verify generateDBFitHtml()
 */
public class TaskSQLFunction extends TaskSQL {
    public TaskSQLFunction(Tag tag, String comment, List<String> statements) {
        super(tag, comment, statements);
    }

    @Override
    public String generateDBFitHtml(Statement statement) throws SQLException {
        return DBFitHtmlCreator.htmlCreateFunction(this, statement);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Task getStudentTemplate() {
        return this; //TODO imlement
    }

}
