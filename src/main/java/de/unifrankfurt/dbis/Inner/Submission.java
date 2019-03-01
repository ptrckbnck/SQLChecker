package de.unifrankfurt.dbis.Inner;

import de.unifrankfurt.dbis.IO.FileIO;
import de.unifrankfurt.dbis.config.DataSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.readAllLines;


/**
 * the Inner class represents a Inner for the SQLChecker.
 * A Inner Object is used as Template for creating a Solution.
 * Then you can check if any Inner Object satisfies created Solution.
 */

public class Submission extends CheckerFrame {

    /**
     * authors of this document.
     */
    private final List<Student> authors;

    private Path path = null;

    public Submission(List<Task> tasks, String name, Charset charset) {
        this(List.of(), tasks, name, charset);
    }

    public Submission(List<Student> authors, List<Task> tasks, String name, Charset charset) {
        super(tasks, name, charset);
        this.authors = authors;
    }


    /**
     * Creates a Inner from a serialized Inner stored in file.
     *
     * @param submissionPath that should be read from.
     * @return newly created Inner
     * @throws IOException              from File IO
     */
    public static Submission fromPath(Path submissionPath) throws IOException {
        Submission sub;
        try {
            //using readAllLines to allow \n & \r\n
            List<String> toParse = readAllLines(submissionPath, StandardCharsets.UTF_8);
            sub = SubmissionParser.parseLines(toParse, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            List<String> toParse = readAllLines(submissionPath, StandardCharsets.ISO_8859_1);
            sub = SubmissionParser.parseLines(toParse, StandardCharsets.ISO_8859_1);
        }
        sub.setPath(submissionPath);
        return sub;
    }



    public List<Student> getAuthors() {
        return authors;
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
     * @param submissionPath Path to which serialized Inner should be written.
     * @throws IOException IO
     */
    public void storeInPath(Path submissionPath) throws IOException {
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


    public Submission setPath(Path path) {
        this.path = path;
        return this;
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
        if (!getName().equals(solution.getName())) {
            return false;
        }
        return this.getTags().equals(solution.getNonStaticTags());
    }



    public List<String> getTagStrings() {
        return this.getTags().stream().map(Tag::getName).collect(Collectors.toList());
    }


    public List<List<String>> getPredefinedSchemata() {
        return this.getTasks()
                .stream()
                .map(x -> {
                    if (TaskSQL.class.isAssignableFrom(x.getClass())) {
                        return ((TaskSQL) x).getSchema();
                    } else {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    public Solution generateSolution(DataSource dataSource, SQLScript resetScript) {
        return Solution.createSolution(this.tasks, this.name, this.charset, resetScript, dataSource);
    }

}
