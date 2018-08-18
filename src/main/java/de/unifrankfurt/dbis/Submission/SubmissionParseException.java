package de.unifrankfurt.dbis.Submission;


/**
 * Exception which can occur during parsing of an serialized Submission.
 */
public class SubmissionParseException extends Exception {
    /**
     * defined plugin is not supported
     */
    public final static int UNKNOWN_PLUGIN = 10;

    /**
     * Task has no body
     */
    public static final int EMPTY_TASK = 11;

    /**
     * TaskSQL does only support one sql statement.
     */
    public static final int TOO_MANY_STATEMENTS = 12;

    /**
     * could not find matching TASKSQL
     */
    public static final int UNKNOWN_TASKSQL = 13;

    /**
     * Student is not well declared
     */
    public static final int BAD_AUTHOR = 14;

    /**
     * first non-empty line is not Tag
     */
    public static final int TAG_MISSING = 20;


    /**
     * Unknown or not yet implemented error code
     */
    public static final int UNKNOWN = -1;

    /**
     * The errorCode provides additional information where the error occurred.
     * See constants defined in this class.
     */
    private final int errorCode;


    /**
     * constructor
     *
     * @param errorCode @see errorCode
     */
    public SubmissionParseException(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * constructor
     *
     * @param message   Message
     * @param errorCode @see errorCode
     */
    public SubmissionParseException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }


    /**
     * returns ErrorCode. See constants defined in this class.
     *
     * @return int
     */
    public int getErrorCode() {
        return errorCode;
    }
}
