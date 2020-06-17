package de.unifrankfurt.dbis.Inner;

/**
 *
 */

import de.unifrankfurt.dbis.SQL.SQLData;
import de.unifrankfurt.dbis.SQL.SQLResultDiff;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ResultStorage {
    private List<Student> authors;
    private String solutionName;
    private List<SQLResultDiff> diffs;
    private List<Integer> score;
    private Charset charset;
    private List<SQLData> sqlData;
    private Exception exception;
    private Base base;
    private Solution solution;
    final private Path submissionPath;
    private Boolean valid;
    private final StringBuilder feedback;

    public ResultStorage(Path submissionPath) {
        this.submissionPath = submissionPath;
        this.feedback = new StringBuilder();

    }

    public static Comparator<? super ResultStorage> resultComperator() {
        return Comparator.comparingInt(ResultStorage::getSumScore);
    }

    public String getFeedback() {
        return feedback.toString();
    }

    public ResultStorage addFeedback(String feedback) {
        this.feedback.append(feedback).append('\n');
        return this;
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

    public ResultStorage setDiffs(List<SQLResultDiff> diffs) {
        this.diffs = diffs;
        return this;
    }

    public List<SQLResultDiff> getDiff() {
        return diffs;
    }


    public ResultStorage setScore(List<Integer> score) {
        this.score = score;
        return this;
    }

    public List<Integer> getScore() {
        return score;
    }

    public List<SQLResultDiff> getDiffs() {
        return diffs;
    }

    public Boolean getValid() {
        return valid;
    }

    public ResultStorage setValid(Boolean valid) {
        this.valid = valid;
        return this;
    }

    public Charset getCharset() {
        return charset;
    }

    public ResultStorage setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public List<SQLData> getSqlData() {
        return sqlData;
    }

    public ResultStorage setSqlData(List<SQLData> sqlData) {
        this.sqlData = sqlData;
        return this;
    }

    public Integer getSumScore() {
        return this.score.stream().reduce(Integer::sum).orElse(0);
    }

    public String csv(CSVCreator csvc) {
        return csvc.create(this);
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

    public Base getBase() {
        return base;
    }

    public ResultStorage setBase(Base sub) {
        this.base = sub;
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
        return (submissionPath != null ? submissionPath : base.getPath());
    }

    @Override
    public String toString() {
        return "ResultStorage{" +
                "authors=" + authors +
                ", solutionName='" + solutionName + '\'' +
                ", diffs=" + diffs +
                ", score=" + score +
                ", charset=" + charset +
                ", sqlResults=" + sqlData +
                ", exception=" + exception +
                ", submission=" + base +
                ", solution=" + solution +
                ", submissionPath=" + submissionPath +
                '}';
    }


}