package de.unifrankfurt.dbis.Inner;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

public class SubmissionInfo {
    protected final Path path;
    protected final String name;
    protected final Charset charset;
    protected final List<Student> authors;
    protected final Boolean valid;


    public SubmissionInfo(Path path, String name, Charset charset, List<Student> authors, Boolean valid) {
        this.path = path;
        this.name = name;
        this.charset = charset;
        this.authors = authors;
        this.valid = valid;
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Charset getCharset() {
        return charset;
    }

    public List<Student> getAuthors() {
        return authors;
    }

    public Boolean getValid() {
        return valid;
    }
}
