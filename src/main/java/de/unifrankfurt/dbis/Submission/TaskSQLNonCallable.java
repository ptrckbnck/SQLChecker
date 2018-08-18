package de.unifrankfurt.dbis.Submission;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * TaskSQL Implementation normal SQL statements
 */
public class TaskSQLNonCallable extends TaskSQL {

    public TaskSQLNonCallable(Tag tag, String comment, String code) {
        super(tag, comment, List.of(code));
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


    @Override
    public TaskSQLNonCallable getStudentTemplate() {
        List<String> list = Arrays.asList(super.getCodeString().split(" "));
        return new TaskSQLNonCallable(super.getTag(),
                super.getComment(),
                (list.isEmpty() ? "" : list.get(0)) + "...");
    }

}
