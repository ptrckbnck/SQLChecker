package de.unifrankfurt.dbis.DBFit;

import de.unifrankfurt.dbis.Submission.Count;
import de.unifrankfurt.dbis.Submission.Solution;
import de.unifrankfurt.dbis.Submission.Submission;

public class ResultStorageBuilder {
    private Solution sol;
    private Submission submission;
    private String resultRaw;
    private Count count;

    public ResultStorageBuilder setSol(Solution sol) {
        this.sol = sol;
        return this;
    }

    public ResultStorageBuilder setSubmission(Submission submission) {
        this.submission = submission;
        return this;
    }

    public ResultStorageBuilder setResultRaw(String resultRaw) {
        this.resultRaw = resultRaw;
        return this;
    }

    public ResultStorageBuilder setCount(Count count) {
        this.count = count;
        return this;
    }

    public ResultStorage createResultStorage() {
        return new ResultStorage(sol, submission, resultRaw, count);
    }
}