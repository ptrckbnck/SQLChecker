package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.DBFit.ResultStorage;
import de.unifrankfurt.dbis.config.DataSource;
import fit.exception.FitParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

class SolutionTest {
    private static Submission<TaskSQL> testsubmission = null;
    private static String DBFitHtml = null;
    private static DataSource source = null;
    private static SQLScript resetScript;


    @BeforeAll
    static void setUp() {
        testsubmission =
                new Submission<>(List.of(new Student("foo", "bar", "foo@bar.de")),
                        List.of(
                                new TaskSQLNonCallable(new Tag("Aufgabe1"), "comment1", "SELECT '1';"),
                                new TaskSQLNonCallable(new Tag("Aufgabe2"), "comment2", "SELECT '2';")),
                        "test_submission");
        testsubmission.setPath(Paths.get("/home/test/submission.txt"));

        DBFitHtml = "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>dbfit.MySqlTest</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Connect</td>\n" +
                "\t\t<td>localhost</td>\n" +
                "\t\t<td>test</td>\n" +
                "\t\t<td>test</td>\n" +
                "\t\t<td>test</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Query</td>\n" +
                "\t\t<td>/*Aufgabe1*/</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>1</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>1</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Query</td>\n" +
                "\t\t<td>/*Aufgabe2*/</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>2</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>2</td>\n" +
                "\t</tr>\n" +
                "</table>";

        source = new DataSource("localhost", "3306", "test", "test", "test");

        resetScript = new SQLScript(List.of("CREATE DATABASE IF NOT EXISTS test;"));
    }


    @Test
    void getSubmission() throws FitParseException, SQLException {
        Solution solution = new Solution(testsubmission, DBFitHtml);
        ResultStorage eval = solution.evaluate(Paths.get("/home/test/"), source, resetScript, testsubmission, false);
        //System.out.println(solution.generateCSVHeader());
        //System.out.println(eval.createCSV());
    }

    @Test
    void getDBFitHtml() {
    }

    @Test
    void getDBFitTags() {
    }

    @Test
    void generateSurveyHTML() {
    }

    @Test
    void getName() {
    }

    @Test
    void evaluate() {
    }

    @Test
    void generateCSVHeader() {
    }
}