package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.TestResources;
import de.unifrankfurt.dbis.config.XConfig;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import static org.junit.Assert.*;
import static org.junit.Assume.assumeNoException;



public class SubmissionTest {



    @Test
    public void fromFileNoAuthor() throws IOException, SubmissionParseException {
        TestResources.DBFitSubmissionData nAuthor = TestResources.getSubmissionNoAuthor();
        Submission sub = Submission.fromPath(nAuthor.getPath());
        assertEquals(sub, nAuthor.getSubmission());
    }

    @Test
    public void fromFileWAuthor() throws IOException, SubmissionParseException {
        TestResources.DBFitSubmissionData wAuthor = TestResources.getSubmissionWAuthor();
        Submission sub = Submission.fromPath(wAuthor.getPath());
        assertEquals(sub, wAuthor.getSubmission());
    }


    @Test
    public void storeInFileNoAuthor() throws IOException {
        List<Task> taskList = new ArrayList<>();
        taskList.add(
                new TaskSQLNonCallable(
                        new Tag("tag"),
                        "this is comment",
                        "Select * from * ;"));
        taskList.add(
                new TaskSQLNonCallable(
                        new Tag("tag2"),
                        "this is comment 2",
                        "SELECT USER(), DATABASE() ;"));
        Submission sub = new Submission<>(taskList, "Submission");
        String serialized = sub.serialize();

        String expected = "/*tag*/\n" +
                "/* this is comment */\n" +
                "Select * from * ;\n" +
                "\n" +
                "/*tag2*/\n" +
                "/* this is comment 2 */\n" +
                "SELECT USER(), DATABASE() ;";

        assertEquals(serialized.trim(), expected.trim());
    }

    @Test
    public void storeInFileWAuthor() throws IOException {
        List<TaskSQL> taskList = new ArrayList<>();
        taskList.add(
                new TaskSQLNonCallable(
                        new Tag("tag"),
                        "this is comment",
                        "Select * from * ;"));
        taskList.add(
                new TaskSQLNonCallable(
                        new Tag("tag2"),
                        "this is comment 2",
                        "SELECT USER(), DATABASE() ;"));
        List<Student> students = new ArrayList<>();
        students.add(new Student("foo", "bar", "test@test.de"));
        students.add(new Student("schlub", "ba", "schlubba@test.de"));
        Submission sub = new Submission<>(students, taskList, "Submission");

        String expected = "/*authors*/\n" +
                "foo;bar;test@test.de\n" +
                "schlub;ba;schlubba@test.de\n" +
                "\n" +
                "/*tag*/\n" +
                "/* this is comment */\n" +
                "Select * from * ;\n" +
                "\n" +
                "/*tag2*/\n" +
                "/* this is comment 2 */\n" +
                "SELECT USER(), DATABASE() ;";

        assertEquals(sub.serialize().trim(), expected.trim());
    }

    @Test //TODO
    public void storeInFileProcedure() {

    }

    @Test //TODO
    public void storeInFileFunction() {

    }


    /**
     * needs database configured for simple config.
     *
     * @throws IOException
     * @throws SQLException
     */
    @Test
    public void generateSolution() throws IOException, SQLException {
        TestResources.Simple instance = TestResources.Simple.getInstance();
        XConfig a = instance.getConfig();
        try {
            Connection connection = a.newConnection();
            connection.close();
        }catch (SQLException e) {
            assumeNoException(e);
        }

        List<TaskSQL> taskList = new ArrayList<>();
        taskList.add(
                new TaskSQLNonCallable(
                        new Tag("tag"),
                        "this is comment",
                        "Select * from color ;"));
        taskList.add(
                new TaskSQLNonCallable(
                        new Tag("tag2"),
                        "this is comment 2",
                        "SELECT USER(), DATABASE() ;"));
        List<Student> students = new ArrayList<>();
        students.add(new Student("foo", "bar", "test@test.de"));
        students.add(new Student("schlub", "ba", "schlubba@test.de"));
        Submission<Task> sub = new Submission<>(students, taskList, "submission");

        instance.runReset();

        Solution sol = sub.onlyTaskSQLSubmission().generateSolution(a);
        String expected = "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>dbfit.MySqlTest</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table> <tr> <td>Connect</td> <td>localhost</td> <td>test</td> <td>test</td> <td>simple</td> </tr> </table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Query</td>\n" +
                "\t\t<td>/*tag*/</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>name</td>\n" +
                "\t\t<td>red</td>\n" +
                "\t\t<td>green</td>\n" +
                "\t\t<td>blue</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>blue</td>\n" +
                "\t\t<td>0</td>\n" +
                "\t\t<td>0</td>\n" +
                "\t\t<td>255</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>green</td>\n" +
                "\t\t<td>0</td>\n" +
                "\t\t<td>255</td>\n" +
                "\t\t<td>0</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>red</td>\n" +
                "\t\t<td>255</td>\n" +
                "\t\t<td>0</td>\n" +
                "\t\t<td>0</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Query</td>\n" +
                "\t\t<td>/*tag2*/</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>USER()</td>\n" +
                "\t\t<td>DATABASE()</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>test@localhost</td>\n" +
                "\t\t<td>simple</td>\n" +
                "\t</tr>\n" +
                "</table>";

        assertEquals(sol.getDBFitHtml().trim(), expected.trim());
    }


    @Test
    public void generateSolutionStatic() throws IOException, SQLException {
        TestResources.Simple simple = TestResources.Simple.getInstance();

        XConfig tr = simple.getConfig();

        try {
            Connection connection = tr.newConnection();
            connection.close();
        }catch (SQLException e) {
            assumeNoException(e);
        }

        List<Task> taskList = new ArrayList<>();
        taskList.add(
                new TaskSQLNonCallable(
                        new Tag("static"),
                        "this is comment",
                        "Select * from color ;"));
        taskList.add(
                new TaskSQLNonCallable(
                        new Tag("static.error"),
                        "this is comment 2",
                        "*;"));
        List<Student> students = new ArrayList<>();
        students.add(new Student("foo", "bar", "test@test.de"));
        students.add(new Student("schlub", "ba", "schlubba@test.de"));
        Submission sub = new Submission<>(students, taskList, "Submission");

        simple.runReset();

        Solution sol = sub.generateSolution(tr);
        String expected = "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>dbfit.MySqlTest</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table> <tr> <td>Connect</td> <td>localhost</td> <td>test</td> <td>test</td> <td>simple</td> </tr> </table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Query</td>\n" +
                "\t\t<td>Select * from color ;</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>name</td>\n" +
                "\t\t<td>red</td>\n" +
                "\t\t<td>green</td>\n" +
                "\t\t<td>blue</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>blue</td>\n" +
                "\t\t<td>0</td>\n" +
                "\t\t<td>0</td>\n" +
                "\t\t<td>255</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>green</td>\n" +
                "\t\t<td>0</td>\n" +
                "\t\t<td>255</td>\n" +
                "\t\t<td>0</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>red</td>\n" +
                "\t\t<td>255</td>\n" +
                "\t\t<td>0</td>\n" +
                "\t\t<td>0</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<!--error-->\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Execute</td>\n" +
                "\t\t<td>*;</td>\n" +
                "\t</tr>\n" +
                "</table>\n";


        assertEquals(expected.trim(),sol.getDBFitHtml().trim());
    }

    @Test
    public void olatReady() throws IOException, SubmissionParseException {
        Path tmp = Files.createTempDirectory("olat");
        Submission<TaskSQL> sub = TestResources.getSubmissionWAuthor().getSubmission();
        sub.olatReady(tmp);
        String name = sub.getName() + "_" + sub.getAuthors().get(0).getMatriculationNumber()+".zip";
        Path file = tmp.resolve(Paths.get(name));
        assertTrue(Files.exists(file));
        assertEquals(Submission.fromOlat(file),sub);
    }
}