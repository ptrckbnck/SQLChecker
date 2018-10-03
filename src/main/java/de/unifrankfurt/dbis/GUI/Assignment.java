package de.unifrankfurt.dbis.GUI;

import de.unifrankfurt.dbis.Submission.Submission;
import de.unifrankfurt.dbis.Submission.TaskSQL;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * represents an Student assignment in SQLChecker Gui.
 */
public class Assignment {

    private final String name;
    private final List<String> tasks;
    private Map<String, String> codeMap;

    public Assignment(String name, String... tasks) {
        this(name, Arrays.asList(tasks));
    }

    public Assignment(String name, List<String> tasks) {
        this(
                name,
                tasks,
                tasks.stream().collect(Collectors.toMap(task -> task, x -> ""))
        );
    }


    public Assignment(String name, List<String> tasks, Map<String, String> textOfTask) {
        this.name = name;
        this.tasks = tasks;
        this.codeMap = textOfTask;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public Map<String, String> getCodeMap() {
        return codeMap;
    }


    public String getName() {
        return name;
    }

    /**
     * creates Assignment from Submission. While representing nearly the same Assignment is only used in GUI and
     * Submission in everything related to DBFIT.
     *
     * @param submission Submission<TaskSQL>
     * @return Assignment
     */
    public static Assignment fromSubmission(Submission<TaskSQL> submission) {
        Assignment assignment = new Assignment(
                submission.getName(),
                submission.getTasks().stream().map((x) -> x.getTag().getName()).collect(Collectors.toList()));
        for (TaskSQL t : submission.getTasks()) {
            assignment.putCodeMap(
                    t.getTag().getName(),
                    t.getCodeString());
        }
        return assignment;
    }

    /**
     * sets given to code to given task.
     *
     * @param task
     * @param code
     */
    public void putCodeMap(String task, String code) {
        //hack to make sure code does no start with /*{alphanum}
        if (code.startsWith("/*") && !code.startsWith("/* "))
            code = "/* " + code.substring(2);
        this.codeMap.put(task, code);

    }

}
