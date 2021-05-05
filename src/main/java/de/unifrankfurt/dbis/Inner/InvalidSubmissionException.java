package de.unifrankfurt.dbis.Inner;

public class InvalidSubmissionException extends Exception {
    public InvalidSubmissionException(String errorString) {
        super(errorString);
    }
}
