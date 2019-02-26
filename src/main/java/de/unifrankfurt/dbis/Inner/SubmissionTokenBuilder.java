package de.unifrankfurt.dbis.Inner;

public class SubmissionTokenBuilder {
    private Tag tag;
    private String body = "";

    public SubmissionTokenBuilder setTag(Tag tag) {
        this.tag = tag;
        return this;
    }

    public SubmissionTokenBuilder setBody(String body) {
        this.body = body;
        return this;
    }

    public SubmissionTokenBuilder addToBody(String body) {
        this.body += body;
        return this;
    }

    public SubmissionToken createSubmissionToken() {
        if (body.trim().isEmpty())
            System.out.println(tag + " has empty body");
        return new SubmissionToken(tag, body);
    }
}