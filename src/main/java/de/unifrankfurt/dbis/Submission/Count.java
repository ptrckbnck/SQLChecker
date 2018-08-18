package de.unifrankfurt.dbis.Submission;


/**
 * todo javadocs
 */
public class Count {
    public int right;
    public int wrong;
    public int ignored;
    public int exceptions;

    public Count(int right, int wrong, int ignored, int exceptions) {
        this.right = right;
        this.wrong = wrong;
        this.ignored = ignored;
        this.exceptions = exceptions;
    }

    public void add(Count count) {
        this.right += count.right;
        this.wrong += count.wrong;
        this.ignored += count.ignored;
        this.exceptions += count.exceptions;
    }

    public String toString() {
        return String.format("Right: %d Wrong: %d Ignored: %d Exception: %d ", right, wrong, ignored, exceptions);
    }
}
