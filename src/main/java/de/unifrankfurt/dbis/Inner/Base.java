package de.unifrankfurt.dbis.Inner;

import de.unifrankfurt.dbis.IO.FileIO;
import de.unifrankfurt.dbis.Inner.Parser.*;
import de.unifrankfurt.dbis.config.DataSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.nio.file.Files.readAllLines;


/**
 * the Inner class represents a Inner for the SQLChecker.
 * A Inner Object is used as Template for creating a Solution.
 * Then you can check if any Inner Object satisfies created Solution.
 */

public class Base extends CheckerFrame {

    /**
     * authors of this document.
     */
    private final Authors authors;
    private final Path path;
    private final BaseType baseType;


    protected Base(String name, Charset charset, BaseType baseType, List<Student> authors, List<TaskInterface> tasks, Path path) {
        super(tasks, name, charset);
        this.authors = new Authors(authors);
        this.baseType = baseType;
        this.path = path;
    }


    /**
     * Creates a Inner from a serialized Inner stored in file.
     *
     * @param path that should be read from.
     * @return newly created Inner
     * @throws IOException              from File IO
     */
    public static Base fromPath(Path path) throws IOException {
        BaseParser bp = new BaseParser();
        bp.registerSubTokenCreator(SubTokenHead::fromRawToken);
        bp.registerSubTokenCreator(SubTokenStatic::fromRawToken);
        bp.registerSubTokenCreator(SubTokenTask::fromRawToken);
        BaseBuilder bb;
        try {
            //using readAllLines to allow \n & \r\n
            List<String> toParse = readAllLines(path, StandardCharsets.UTF_8);
            bb = bp.parse(toParse).setCharset(StandardCharsets.UTF_8);
        } catch (IOException ex) {
            List<String> toParse = readAllLines(path, StandardCharsets.ISO_8859_1);
            bb = bp.parse(toParse).setCharset(StandardCharsets.ISO_8859_1);
        }
        bb.setPath(path);
        return bb.build();
    }



    public List<Student> getAuthors() {
        return authors;
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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Base that = (Base) o;
        return Objects.equals(authors, that.authors) &&
                Objects.equals(tasks, that.tasks);
    }

    @Override
    public int hashCode() {

        return Objects.hash(authors, tasks);
    }

    @Override
    public String toString() {
        return String.join(System.getProperty("line.separator"), this.serialize());
    }

    public String serialize() {
        return new SubTokenHead(this.baseType, this.name, authors).serialize() +
                this.tasks.stream().map(TaskInterface::serialize).collect(Collectors.joining("\n"));
    }



    public Path getPath(){
        return path;
    }

    public boolean sameSchema(List<Base> others) {
        return others
                .stream()
                .allMatch(this::sameSchema);
    }

    public boolean sameSchema(Base other) {
        return this.getTags().equals(other.getTags());
    }

    public boolean isSubmissionFor(Solution solution) {
        if (!getName().equals(solution.getName())) {
            return false;
        }
        return this.getTags().equals(solution.getNonStaticTags());
    }





    public List<List<String>> getPredefinedSchemata() {
        return this.getTasks()
                .stream()
                .map(x -> {
                    if (TaskSQL.class.isAssignableFrom(x.getClass())) {
                        return ((TaskSQL) x).getScheme();
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
