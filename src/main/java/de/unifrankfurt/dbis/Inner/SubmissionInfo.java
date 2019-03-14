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
    protected final Base base;
    protected final Integer id;

    private SubmissionInfo(Path path, String name, Charset charset, List<Student> authors, Boolean valid, Base base, Integer id) {
        this.path = path;
        this.name = name;
        this.charset = charset;
        this.authors = authors;
        this.valid = valid;
        this.base = base;
        this.id = id;
    }


    public static SubmissionInfo of(Base base, Integer id) {
        return new SubmissionInfo(base.getPath(), base.getName(), base.getCharset(), base.getAuthors(), true, base, id);
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

    public Base getBase() {
        return base;
    }

    public Boolean getValid() {
        return valid;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "SubmissionInfo{" + "path=" + path +
                ", name='" + name + '\'' +
                ", charset=" + charset +
                ", authors=" + authors +
                ", valid=" + valid +
                ", submission=" + base +
                ", id=" + id +
                '}';
    }
}


