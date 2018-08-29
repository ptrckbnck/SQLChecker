package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.SQL.SQLResultWrapper;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * A Submission is fragmented in Tasks.
 * The main use of Task is to define one Exercise.
 * It can also be used do define static supplementation.
 * Tasks can be dependent on each other.
 * For example can a SQLTask with a Select statement only make sense
 * if the SQLTask with a create table statement before was executed.
 */
public interface Task {

    /**
     * each Tag has an identifier which should be unique in ine Submission.
     * Exemption are static Tags.
     *
     * @return Tag
     */
    Tag getTag();

    /**
     * Runs sql if task has sql
     * Tasks which do not inherit from TaskSQL should do nothing.
     * Result is ignored.
     *
     * @param statement SQL Statement
     * @throws SQLException SQL
     */
    void runSQL(Statement statement) throws SQLException;


    /**
     * Executes sql if task has sql
     * Tasks which do not inherit from TaskSQL should do nothing.
     * Returns result of query as SQLResultWrapper
     *
     * @param statement SQL Statement
     * @return SQLResultWrapper
     * @throws SQLException SQL
     */
    List<SQLResultWrapper> executeSQL(Statement statement) throws SQLException;

    Task getStudentTemplate();

    /**
     * return a String representation for serializing
     *
     * @return String
     */
    String serialize();
}
