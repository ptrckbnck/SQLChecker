package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.IO.FileIO;
import de.unifrankfurt.dbis.SQL.SQLResultWrapper;
import de.unifrankfurt.dbis.config.DataSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.readAllLines;


/**
 * the Submission class represents a Submission for the SQLChecker.
 * A Submission Object is used as Template for creating a Solution.
 * Then you can check if any Submission Object satisfies created Solution.
 */

public class Submission {

    /**
     * authors of this document.
     */
    private final List<Student> authors;

    /**
     * defined Tasks
     */
    private final List<Task> tasks;

    /**
     * Name of this Submission.
     * The Name of Submission for a given solution should be identical.
     */
    private final String name;
    private Charset charset;
    private Path path = null;

    public Submission(List<Task> tasks, String name) {
        this.tasks = new ArrayList<>(tasks);
        this.name = name;
        this.authors = new ArrayList<>();
    }

    public Submission(List<Student> authors, List<Task> tasks, String name) {
        this.authors = authors;
        this.tasks = new ArrayList<>(tasks);
        this.name = name;
    }

    /**
     * Creates a Submission from a serialized Submission stored in file.
     *
     * @param submissionPath that should be read from.
     * @return newly created Submission
     * @throws IOException              from File IO
     * @throws SubmissionParseException when parsing goes wrong
     */
    public static Submission fromPath(Path submissionPath) throws IOException, SubmissionParseException {
        Submission sub;
        try {
            //using readAllLines to allow \n & \r\n
            List<String> toParse = readAllLines(submissionPath, StandardCharsets.UTF_8);
            sub = SubmissionParser.parseLines(toParse, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            List<String> toParse = readAllLines(submissionPath, StandardCharsets.ISO_8859_1);
            sub = SubmissionParser.parseLines(toParse, StandardCharsets.ISO_8859_1);
        }
        return sub;
    }


    public Charset getCharset() {
        return charset;
    }

    public List<Student> getAuthors() {
        return authors;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public String getName() {
        return name;
    }

    /**
     * create a serialized version of authors section.
     *
     * @throws IOException from writer
     */
    private List<String> serializeAuthor() {
        List<String> lines = new ArrayList<>();
        if (authors.size() == 0) return lines;
        lines.add(Tag.TAG_PREFIX + Tag.AUTHORTAG + Tag.TAG_SUFFIX);
        for (Student student : authors) {
            lines.add(student.serialize());
        }
        lines.add("");
        return lines;
    }


    /**
     * Serialize this Object and store this in Path submissionPath.
     * Deserialize again with fromPath().
     *
     * @param submissionPath Path to which serialized Submission should be written.
     * @throws IOException IO
     */
    public void storeInPath(Path submissionPath) throws IOException {
        //Files.write(submissionPath, serializeAuthor(), StandardCharsets.UTF_8);
        //Files.write(submissionPath, serializeTasks(), StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        FileIO.saveText(submissionPath, serialize());
    }

    /**
     * List of every Tag.
     *
     * @return List<Tag>
     */
    public List<Tag> getTags() {
        ArrayList<Tag> tags = new ArrayList<>();
        for (Task task : tasks) tags.add(task.getTag());
        return tags;
    }

    /**
     * Writes every serialized Task to writer.
     */
    private List<String> serializeTasks() {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.serialize());
        }
        return lines;
    }


    public Solution generateSolution(DataSource source, SQLScript resetScript) throws SQLException {
        StringBuilder builder = new StringBuilder();
        try (Connection connection = source.getConnection()) {
            builder.append(generateDBFitHeader(source));

            for (Task sql : this.tasks) {
                try (Statement s = connection.createStatement()){
                    builder.append(sql.generateDBFitHtml(s));

                } catch (SQLException e) {
                    throw new SQLException(e.getMessage()+". Error generating html of: " + sql.getTag().serialized(), e);
                }
            }
            Solution sol = new Solution(this, builder.toString());
            resetScript.execute(source);
            sol.setResultHeaders(sol.getSubmission().generateResultHeaders(source));
            return sol;
        }
    }


    private static String generateDBFitHeader(DataSource source) {
        return generateDBFitHeader(source.getHost(),
                source.getUser(),
                source.getPassword(),
                source.getDatabase());
    }

    private static String generateDBFitHeader(String hostname, String username, String password, String database) {

        String driverName = "dbfit.MySqlTest";

        String header = "<table>\n"
                + "\t<tr>\n"
                + "\t\t<td>" + driverName + "</td>\n"
                + "\t</tr>\n"
                + "</table>\n\n";


        /* Connection properties in the following order: <br>
         * host (default:localhost) <br>
         * dbUser (default:root) <br>
         * dbUserPw (default:) <br>
         * dbName (default:dbfit) <br>
         */

        header += "<table>\n"
                + "\t<tr>\n"
                + "\t\t<td>Connect</td>\n"
                + "\t\t<td>" + hostname + "</td>\n"
                + "\t\t<td>" + username + "</td>\n"
                + "\t\t<td>" + password + "</td>\n"
                + "\t\t<td>" + database+ "</td>\n"
                + "\t</tr>\n"
                + "</table>\n\n";

        return header;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Submission that = (Submission) o;
        return Objects.equals(authors, that.authors) &&
                Objects.equals(tasks, that.tasks);
    }

    @Override
    public int hashCode() {

        return Objects.hash(authors, tasks);
    }

    @Override
    public String toString() {
        return this.serialize();
    }

    public String serialize() {
        return Stream.concat(serializeAuthor().stream(), serializeTasks().stream())
                .collect(Collectors.joining("\n"));
    }


    /**
     * Get Task with given tag.
     * Should not be used for static Tasks, there could be multiple.
     *
     * @param tag Tag
     * @return Task if any exists with given Tag else null.
     */
    public Task getTaskByTag(Tag tag) {
        HashMap<Tag, Task> tagTaskHashMap = new HashMap<>();
        for (Task task : this.tasks)
            tagTaskHashMap.put(task.getTag(), task);
        return tagTaskHashMap.get(tag);

    }

    public void setCharset(Charset cs) {
        this.charset = cs;
    }


    public void setPath(Path path) {
        this.path = path;
    }

    public Path getPath(){
        return path;
    }

    public boolean sameSchema(List<Submission> others) {
        return others
                .stream()
                .allMatch(this::sameSchema);
    }

    public boolean sameSchema(Submission other) {
        return this.getTags().equals(other.getTags());
    }

    public boolean isSubmissionFor(Solution solution) {
        return this.getTags().equals(solution.getSubmission().getNonStaticTags());
    }

    public List<Tag> getNonStaticTags() {
        return this.getTags()
                .stream()
                .filter((tag)->!tag.isStatic())
                .collect(Collectors.toList());
    }

    public List<String> getTagStrings() {
        return this.getTags().stream().map(Tag::getName).collect(Collectors.toList());
    }

    /**
     * inner lists are schema of tables. empty inner list if success but no table(create table etc.). null if sql-error
     *
     * @param source
     * @return
     * @throws SQLException
     */
    public List<List<String>> generateResultHeaders(DataSource source) throws SQLException {
        List<List<String>> sqlHeaders = new ArrayList<>();
        try (Connection connection = source.getConnection()) {
            for (Task sql : this.tasks) {
                try (Statement s = connection.createStatement()) {
                    SQLResultWrapper result = SQLResultWrapper.executeStatement(s, sql.getSql());
                    sqlHeaders.add(result.getHeader());
                } catch (SQLException e) {
                    System.err.println(e);
                    sqlHeaders.add(null);
                }
            }
        }

        return sqlHeaders;
    }
}
