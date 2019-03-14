package de.unifrankfurt.dbis.StudentGUI;

import de.unifrankfurt.dbis.Inner.Base;
import de.unifrankfurt.dbis.Inner.Parser.TaskInterface;

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
     * creates Assignment from Inner. While representing nearly the same Assignment is only used in StudentGUI and
     * Inner in everything related to DBFIT.
     *
     * @param base Inner
     * @return Assignment
     */
    public static Assignment fromSubmission(Base base) {
        return new Assignment(
                base.getName(),
                base.getTags(),
                base.getTasks().stream().map(TaskInterface::getSQL).collect(Collectors.toList()),
                base.getPredefinedSchemata());
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
