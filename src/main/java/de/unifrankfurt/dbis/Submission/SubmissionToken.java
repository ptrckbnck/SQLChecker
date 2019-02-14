package de.unifrankfurt.dbis.Submission;

/**
 * Container for Tag + Body of a Task.
 * A Submission Token should be parsed by SubmissionParser.fromToken().
 */
public class SubmissionToken {
    /**
     * Tag of this Submission Token
     */
    private final Tag tag;

    /**
     * complete Body of Token, everything that was found after a tag but before another Tag or File ending.
     */
    private final String body;


    public SubmissionToken(Tag tag, String body) {
        this.tag = tag;
        this.body = body;
    }


    public String getBody() {
        return body;
    }

    public Tag getTag() {
        return tag;
    }

    public String toString() {
        return tag.serialized() + "\n" + body;
    }


    public String getAddition() {
        return tag.getAddition();
    }
}
