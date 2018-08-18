package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.config.XConfig;

import java.io.IOException;
import java.net.URI;
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


    /**
     * generates Solution for DBFIT.
     *
     * @param XConfig GUIConfig File needed for database connection.
     * @return generated Solution
     * @throws SQLException Database connection problems
     */
    public Solution generateSolution(XConfig XConfig) throws SQLException {
        List<TaskSQL> sqlTasks = getTaskSQLList();
        StringBuilder builder = new StringBuilder();
        Connection connection;
        try {
            connection = XConfig.newConnection();
        } catch (SQLException e) {
            throw new SQLException("could not establish connection with database", e);
        }
        builder.append(generateDBFitHeader(XConfig));

        for (TaskSQL sql : sqlTasks) {
            try {
                builder.append(sql.generateDBFitHtml(connection.createStatement()));
            } catch (SQLException e) {
                throw new SQLException("error generating html of: " + sql.getTag().serialized(), e);
            }
        }

        return new Solution(this, builder.toString());
    }


    /**
     * creates Header Section of DBFit html.
     *
     * @param conf GUIConfig
     * @return String DBFit html
     */
    private static String generateDBFitHeader(XConfig conf) {

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

        //TODO add linebreak if dbfit allows it
        header += "<table> <tr> <td>Connect</td> "
                + "<td>" + conf.getHostname() + "</td> "
                + "<td>" + conf.getUsername() + "</td> "
                + "<td>" + conf.getPassword() + "</td> "
                + "<td>" + conf.getDatabase() + "</td> </tr> </table>\n\n";

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


    /**
     * Creates zip-file for submitting to olat.
     * Do not modify after creating.
     *
     * @param path path to where Submission should be saved
     * @throws IOException IO
     */
    public boolean olatReady(Path path) throws IOException {
        if (this.authors.size() < 1) return false;
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        final String fileName = this.name + "_" + this.authors.get(0).getMatriculationNumber();

        Path subPath = path.resolve(fileName + ".txt");
        this.storeInPath(subPath);
        Path outPath = path.resolve(fileName + ".zip");
        String path1 = outPath.toUri().getPath();
        URI uri = URI.create("jar:file:"+ path1);



        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env, null)){
            Files.move(subPath,
                    zipfs.getPath("/", fileName));

        }catch (IOException e){
            e.printStackTrace();

        }
        return true;
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


}
