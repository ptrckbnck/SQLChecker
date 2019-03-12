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
    protected final Submission submission;
    protected final Integer id;

    private SubmissionInfo(Path path, String name, Charset charset, List<Student> authors, Boolean valid, Submission submission, Integer id) {
        this.path = path;
        this.name = name;
        this.charset = charset;
        this.authors = authors;
        this.valid = valid;
        this.submission = submission;
        this.id = id;
    }


    public static SubmissionInfo of(Submission submission, Integer id) {
        return new SubmissionInfo(submission.getPath(), submission.getName(), submission.getCharset(), submission.getAuthors(), true, submission, id);
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

    public Submission getSubmission() {
        return submission;
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
                ", submission=" + submission +
                ", id=" + id +
                '}';
    }
}


