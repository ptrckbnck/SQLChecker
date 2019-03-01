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

    private SubmissionInfo(Path path, String name, Charset charset, List<Student> authors, Boolean valid, Submission submission) {
        this.path = path;
        this.name = name;
        this.charset = charset;
        this.authors = authors;
        this.valid = valid;
        this.submission = submission;
    }


    public SubmissionInfo(Path path, String name, Charset charset, List<Student> authors) {
        this.path = path;
        this.name = name;
        this.charset = charset;
        this.authors = authors;
        this.valid = false;
        this.submission = null;
    }

    public static SubmissionInfo of(Submission submission) {
        return new SubmissionInfo(submission.getPath(), submission.getName(), submission.getCharset(), submission.getAuthors(), true, submission);
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
}
