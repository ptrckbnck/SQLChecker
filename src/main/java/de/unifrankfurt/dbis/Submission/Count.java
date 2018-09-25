package de.unifrankfurt.dbis.Submission;


import fit.Counts;

/**
 * todo javadocs
 */
public class Count {
    private int right;
    private int wrong;
    private int ignored;
    private int exceptions;

    public Count(int right, int wrong, int ignored, int exceptions) {
        this.right = right;
        this.wrong = wrong;
        this.ignored = ignored;
        this.exceptions = exceptions;
    }

    public Count(Counts counts) {
        this.exceptions=counts.exceptions;
        this.right= counts.right;
        this.ignored = counts.ignores;
        this.wrong = counts.ignores;
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


    public int getRight() {
        return right;
    }

    public int getWrong() {
        return wrong;
    }

    public int getIgnored() {
        return ignored;
    }

    public int getExceptions() {
        return exceptions;
    }
}
