package de.unifrankfurt.dbis.Submission;


import de.unifrankfurt.dbis.config.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubmissionTest {
    private static Submission testsubmission = null;
    private static DataSource source = null;
    private static SQLScript resetScript;

    @BeforeAll
    static void setUp() {
        List<Student> students = List.of(new Student("foo", "bar", "foo@bar.de"));

        Task task1 = new TaskSQL(new Tag("Aufgabe1"), null, "/* comment1 */\nSELECT '1' as 'a';");
        Task task2 = new TaskSQL(new Tag("Aufgabe2"), null, "/* comment2 */\nSELECT '2';");
        Task task3 = new TaskSQL(new Tag("Aufgabe3"), null, "/* comment3 */\ncreate table if not exists testtable(id int);");
        Task task4 = new TaskSQL(new Tag("Aufgabe3"), null, "/* comment4 */\n...");
        List<Task> tasks = List.of(task1, task2, task3, task4);
        testsubmission = new Submission(students, tasks, "test_submission");
        testsubmission.setPath(Paths.get("/home/test/submission.txt"));
        source = new DataSource("localhost", "3306", "test", "test", "test", false, "+01:00");

        resetScript = new SQLScript(List.of("CREATE DATABASE IF NOT EXISTS test;"));
    }

    @Test
    void generateResultHeaders() throws SQLException {
        List<List<String>> headers = testsubmission.generateResultHeaders(source, resetScript);
        List<List<String>> list = new ArrayList<>();
        list.add(List.of("a"));
        list.add(List.of("2"));
        list.add(List.of());
        list.add(null);
        assertEquals(list, headers);
    }
}