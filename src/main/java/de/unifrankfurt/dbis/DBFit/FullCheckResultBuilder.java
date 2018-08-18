package de.unifrankfurt.dbis.DBFit;

import de.unifrankfurt.dbis.Submission.Submission;
import de.unifrankfurt.dbis.Submission.TaskSQL;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder for FullCheckResult
 */
public class FullCheckResultBuilder {
    private List<Submission<TaskSQL>> submissions;
    private Map<Path, Exception> errorLogFile;
    private Map<Submission<TaskSQL>, Exception> errorLogSubmission;
    private Map<Submission<TaskSQL>, NewResultStorage> result;

    public FullCheckResultBuilder setSubmissions(List<Submission<TaskSQL>> submissions) {
        this.submissions = submissions;
        return this;
    }

    public FullCheckResultBuilder setErrorLogFile(HashMap<Path, Exception> errorLogFile) {
        this.errorLogFile = errorLogFile;
        return this;
    }

    public FullCheckResultBuilder setErrorLogSubmission(Map<Submission<TaskSQL>, Exception> errorLogSubmission) {
        this.errorLogSubmission = errorLogSubmission;
        return this;
    }

    public FullCheckResultBuilder setResult(Map<Submission<TaskSQL>, NewResultStorage> result) {
        this.result = result;
        return this;
    }

    public FullCheckResult createFullCheckResult() {
        return new FullCheckResult(
                submissions,
                errorLogFile,
                errorLogSubmission,
                result);
    }
}