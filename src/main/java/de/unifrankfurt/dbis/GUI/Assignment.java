package de.unifrankfurt.dbis.GUI;

import de.unifrankfurt.dbis.Submission.Submission;
import de.unifrankfurt.dbis.Submission.Task;

import java.util.List;
import java.util.stream.Collectors;

/**
 * represents an Student assignment in SQLChecker Gui.
 */
public class Assignment {

    private final String name;
    private final List<String> tasks;
    private final List<List<String>> schemata;
    private final List<String> codes;


    public Assignment(String name, List<String> tasks, List<String> codes, List<List<String>> schemata) {
        this.name = name;
        this.tasks = tasks;
        this.codes = codes;
        this.schemata = schemata;
    }

    /**
     * creates Assignment from Submission. While representing nearly the same Assignment is only used in GUI and
     * Submission in everything related to DBFIT.
     *
     * @param submission Submission
     * @return Assignment
     */
    public static Assignment fromSubmission(Submission submission) {
        return new Assignment(
                submission.getName(),
                submission.getTagStrings(),
                submission.getTasks().stream().map(Task::getSql).collect(Collectors.toList()),
                submission.getSchemata());
    }

    public List<String> getTasks() {
        return tasks;
    }

    public String getName() {
        return name;
    }

    public List<List<String>> getSchemata() {
        return schemata;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCode(int task, String sql) {
        this.codes.set(task, sql);
    }

    public String getCodeOf(int task) {
        return this.codes.get(task);
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "name='" + name + '\'' +
                ", tasks=" + tasks +
                ", schemata=" + schemata +
                ", codes=" + codes +
                '}';
    }
}
