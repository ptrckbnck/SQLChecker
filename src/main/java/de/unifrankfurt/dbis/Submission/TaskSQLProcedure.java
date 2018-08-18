package de.unifrankfurt.dbis.Submission;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * TaskSQl Implementation for Procedures TODO verify generateDBFitHtml
 */
public class TaskSQLProcedure extends TaskSQL {
    public TaskSQLProcedure(Tag tag, String comment, List<String> code) {
        super(tag, comment, code);
    }

    @Override
    public String generateDBFitHtml(Statement statement) throws SQLException {
        return DBFitHtmlCreator.htmlCreateProcedure(this, statement);
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
        return this; //TODO implement
    }

}
