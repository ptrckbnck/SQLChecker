package de.unifrankfurt.dbis.Submission;


/**
 * Exception which can occur during parsing of an serialized Submission.
 */
public class SubmissionParseException extends Exception {


    /**
     * The errorCode provides additional information where the error occurred.
     * See constants defined in this class.
     */
    private final ErrorType errorType;

    /**
     * constructor
     *
     * @param errorType
     */
    public SubmissionParseException(ErrorType errorType) {

        this.errorType = errorType;
    }


    /**
     * constructor
     *
     * @param message   Message
     * @param errorType
     */
    public SubmissionParseException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    /**
     * returns ErrorType.
     *
     * @return int
     */
    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return "SubmissionParseException(" + errorType + "): " + super.getMessage();
    }

    public enum ErrorType {
        UNKNOWN_PLUGIN, EMPTY_TASK, TOO_MANY_STATEMENTS, UNKNOWN_TASKSQL, BAD_AUTHOR, TAG_MISSING, UNKNOWN
    }
}
