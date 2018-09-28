package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.config.DataSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * the Submission class represents a Submission for the SQLChecker.
 * A Submission Object is used as Template for creating a Solution.
 * Then you can check if any Submission Object satisfies created Solution.
 */
public class Submission<e extends Task> {

    /**
     * authors of this document.
     */
    private final List<Student> authors;

    /**
     * defined Tasks
     */
    private final List<e> tasks;

    /**
     * Name of this Submission.
     * The Name of Submission for a given solution should be identical.
     */
    private final String name;
    private Path path = null;

    public Submission(List<? extends e> tasks, String name) {
        this.tasks = new ArrayList<>(tasks);
        this.name = name;
        this.authors = new ArrayList<>();
    }

    public Submission(List<Student> authors, List<? extends e> tasks, String name) {
        this.authors = authors;
        this.tasks = new ArrayList<>(tasks);
        this.name = name;
    }


    public List<Student> getAuthors() {
        return authors;
    }

    public List<e> getTasks() {
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
        Files.write(submissionPath, serializeAuthor());
        Files.write(submissionPath, serializeTasks(), StandardOpenOption.APPEND, StandardOpenOption.WRITE);
    }


    /**
     * Runs every SQL Code of every Task containing SQL.
     * Does not evaluate result.
     * Use this function to see if code runs or to update your database.
     *
     * @param connection Connection which should be used for database communication.
     * @throws SQLException creates SQLException if any kind of problem with database occurred.
     */
    public void runSQL(Connection connection) throws SQLException {
        connection.setAutoCommit(true);
        Statement statement = connection.createStatement();
        for (Task task : tasks) {
            task.runSQL(statement);
        }
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


    /**
     * Return a List of every TaskSQL
     * Workaround to work with List of TaskSQL instead of List of Task.
     * If needed a walker function should be implemented.
     *
     * @return List of TaskSQL
     */
    public List<TaskSQL> getTaskSQLList() {
        List<TaskSQL> list = new ArrayList<>();
        for (Task task : this.tasks) {
            if (task instanceof TaskSQL) {
                list.add((TaskSQL) task);
            }
        }
        return list;
    }


    public Solution generateSolution(DataSource source) throws SQLException {
        List<TaskSQL> sqlTasks = getTaskSQLList();
        StringBuilder builder = new StringBuilder();
        try (Connection connection = source.getConnection()) {
            builder.append(generateDBFitHeader(source));

            for (TaskSQL sql : sqlTasks) {
                try (Statement s = connection.createStatement()){
                    builder.append(sql.generateDBFitHtml(s));
                } catch (SQLException e) {
                    throw new SQLException(e.getMessage()+". Error generating html of: " + sql.getTag().serialized(), e);
                }
            }
            return new Solution(this, builder.toString());
        } catch (SQLException e) {
            throw e;
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
/*

        //old
        header += "<table> <tr> <td>Connect</td> "
                + "<td>" + conf.getHostname() + "</td> "
                + "<td>" + conf.getUsername() + "</td> "
                + "<td>" + conf.getPassword() + "</td> "
                + "<td>" + conf.getDatabase() + "</td> </tr> </table>\n\n";
*/

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
     * Creates Submission with only TaskSQL. Any other task will be ignored.
     *
     * @return Submission<TaskSQL>
     */
    public Submission<TaskSQL> onlyTaskSQLSubmission() {
        return new Submission<>(
                this.authors,
                this.getTaskSQLList(), name);
    }


    /**
     * Get Task with given tag.
     * Should not be used for static Tasks, there could be multiple.
     *
     * @param tag Tag
     * @return Task if any exists with given Tag else null.
     */
    public e getTaskByTag(Tag tag) {
        HashMap<Tag, e> tagTaskHashMap = new HashMap<>();
        for (e task : this.tasks)
            tagTaskHashMap.put(task.getTag(), task);
        return tagTaskHashMap.get(tag);

    }


    public static Submission<TaskSQL> fromOlat(Path path) throws IOException, SubmissionParseException {
        FileSystem system = FileSystems.newFileSystem(path, null);
        List<Path> rootDirs = new ArrayList<>();
        system.getRootDirectories().forEach(rootDirs::add);
        if (rootDirs.isEmpty()) throw new IOException("File contains no Submission");
        Path dir = rootDirs.get(0); // olat zip should not have more than one root dir.
        Optional<Path> result = Files.walk(dir)
                .filter(x -> !Files.isDirectory(x))
                .findFirst();
        if (result.isPresent()) return Submission.fromPath(result.get()).onlyTaskSQLSubmission();
        throw new IOException("File contains no Submission");
    }

    /**
     * creates a Template for Student Submission.
     * Tasks do not contain the solution, but commentary and hints.
     * For example, TaskSQLNonCallable hints the first word of the sql command.
     *
     * @return Submission
     */
    private Submission<e> createStudentTemplate() {
        List<e> list = new ArrayList<>();
        for (e task : this.tasks) {
            task.getStudentTemplate();
        }
        return new Submission<>(list, this.name);
    }


    /**
     * Creates a Submission from a serialized Submission stored in file.
     *
     * @param submissionPath that should be read from.
     * @return newly created Submission
     * @throws IOException              from File IO
     * @throws SubmissionParseException when parsing goes wrong
     */
    public static Submission<Task> fromPath(Path submissionPath) throws IOException, SubmissionParseException {
        List<String> lines = Files.readAllLines(submissionPath, StandardCharsets.UTF_8);
        return SubmissionParser.parse(lines);
    }


    public void setPath(Path path) {
        this.path = path;
    }

    public Path getPath(){
        return path;
    }

    public boolean sameSchema(List<Submission<TaskSQL>> others) {
        return others.stream().allMatch(this::sameSchema);
    }

    public boolean sameSchema(Submission<TaskSQL> other) {
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

    public List<String> getNonStaticTagStrings() {
        return this.getTags()
                .stream()
                .filter((tag) -> !tag.isStatic())
                .map(Tag::getName)
                .collect(Collectors.toList());
    }
}
