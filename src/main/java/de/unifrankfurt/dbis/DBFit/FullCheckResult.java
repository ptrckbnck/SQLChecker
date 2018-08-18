package de.unifrankfurt.dbis.DBFit;

import de.unifrankfurt.dbis.Submission.Submission;
import de.unifrankfurt.dbis.Submission.TaskSQL;


import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * FullCheckResult represents the result of DBFit tests of a List of Solution.
 */
public class FullCheckResult {
    /**
     * List tested submissions.
     */
    private final List<Submission<TaskSQL>> submissions;

    /**
     * Map of Files that could not create Submission to Exception.
     */
    private final Map<Path, Exception> errorLogFile;

    /**
     * Map of Submission that created Exception during check to Exception.
     */
    private final Map<Submission<TaskSQL>, Exception> errorLogSubmission;

    /**
     * Map of Submission to final ResultStorage.
     */
    private final Map<Submission<TaskSQL>, NewResultStorage> result;



    FullCheckResult(List<Submission<TaskSQL>> submissions,
                    Map<Path, Exception> errorLogFile,
                    Map<Submission<TaskSQL>, Exception> errorLogSubmission,
                    Map<Submission<TaskSQL>, NewResultStorage> result) {
        this.submissions = submissions;
        this.errorLogFile = errorLogFile;
        this.errorLogSubmission = errorLogSubmission;
        this.result = result;
    }

    public List<Submission<TaskSQL>> getSubmissions() {
        return submissions;
    }

    public Map<Path, Exception> getErrorLogFile() {
        return errorLogFile;
    }

    public Map<Submission<TaskSQL>, Exception> getErrorLogSubmission() {
        return errorLogSubmission;
    }

    public Map<Submission<TaskSQL>, NewResultStorage> getResult() {
        return result;
    }
}
