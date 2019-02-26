package de.unifrankfurt.dbis.Inner;

/**
 *
 */

import de.unifrankfurt.dbis.SQL.SQLResult;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ResultStorage {
    private List<Student> authors;
    private String solutionName;
    private List<EvalStatus> status;
    private String errorMsg;
    private List<Integer> score;
    private Charset charset;
    private List<SQLResult> sqlResults;
    private Exception exception;
    private Submission submission;
    private Solution solution;
    private Path submissionPath;

    public static Comparator<? super ResultStorage> resultComperator() {
        return Comparator.comparingInt(o -> (o.score.stream().reduce(0, Integer::sum)));
    }

    public List<Student> getAuthors() {
        return authors;
    }

    public ResultStorage setAuthors(List<Student> authors) {
        this.authors = authors;
        return this;
    }

    public String getSolutionName() {
        return solutionName;
    }

    public ResultStorage setSolutionName(String solutionName) {
        this.solutionName = solutionName;
        return this;
    }

    public List<EvalStatus> getStatus() {
        return status;
    }

    public ResultStorage setStatus(List<EvalStatus> status) {
        this.status = status;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public ResultStorage setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public List<Integer> getScore() {
        return score;
    }

    public ResultStorage setScore(List<Integer> score) {
        this.score = score;
        return this;
    }


    public Charset getCharset() {
        return charset;
    }

    public ResultStorage setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public List<SQLResult> getSqlResults() {
        return sqlResults;
    }

    public ResultStorage setSqlResults(List<SQLResult> sqlResults) {
        this.sqlResults = sqlResults;
        return this;
    }


    public String csv(CSVCreator csvc) {
        return csvc.create(this);
    }


    public Charset getEncoding() {
        return this.charset;
    }

    private String report(Path path, String authors, List<String> tags, List<String> status) {
        String prefix = "SUCCESS Path:" + path + " Authors:" + authors + " Solution:" + solutionName + " Evaluation:[";
        return IntStream.range(0, tags.size())
                .mapToObj((i) -> tags.get(i) + ":" + status.get(i))
                .collect(Collectors.joining(", ",
                        prefix,
                        "]"));
    }

    public Exception getException() {
        return exception;
    }

    public ResultStorage setException(Exception e) {
        this.exception = e;
        return this;
    }

    public Submission getSubmission() {
        return submission;
    }

    public ResultStorage setSubmission(Submission sub) {
        this.submission = sub;
        return this;
    }

    public Solution getSolution() {
        return solution;
    }

    public ResultStorage setSolution(Solution sol) {
        this.solution = sol;
        return this;
    }

    public Path getSubmissionPath() {
        return (submissionPath != null ? submissionPath : submission.getPath());
    }

    public ResultStorage setSubmissionPath(Path p) {
        this.submissionPath = p;
        return this;
    }

    @Override
    public String toString() {
        return "ResultStorage{" +
                "authors=" + authors +
                ", solutionName='" + solutionName + '\'' +
                ", status=" + status +
                ", errorMsg='" + errorMsg + '\'' +
                ", score=" + score +
                ", charset=" + charset +
                ", sqlResults=" + sqlResults +
                ", exception=" + exception +
                ", submission=" + submission +
                ", solution=" + solution +
                ", submissionPath=" + submissionPath +
                '}';
    }
}