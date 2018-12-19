package de.unifrankfurt.dbis.IO;

import de.unifrankfurt.dbis.GUI.Assignment;
import de.unifrankfurt.dbis.Submission.*;
import de.unifrankfurt.dbis.config.GUIConfig;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * class for maintaining all data in of serialising for a project.
 */
public class SQLCheckerProject {
    private final GUIConfig GUIConfig;
    private final Assignment assignment;

    public SQLCheckerProject(GUIConfig GUIConfig, Assignment assignment) {
        this.GUIConfig = GUIConfig;
        this.assignment = assignment;
    }

    public GUIConfig getGUIConfig() {
        return GUIConfig;
    }

    public Assignment getAssignment() {
        return assignment;
    }


    /**
     * @return List<Student>
     */
    private List<Student> studentList() {
        if (this.GUIConfig.isPartnerOk())
            return List.of(
                    new Student(
                            this.GUIConfig.getNameStudent(),
                            this.GUIConfig.getEmail(),
                            this.GUIConfig.getMatNr()),
                    new Student(
                            this.GUIConfig.getPartnerName(),
                            this.GUIConfig.getPartnerEmail(),
                            this.GUIConfig.getPartnerMatNr())
            );
        else return List.of(
                new Student(
                        this.GUIConfig.getNameStudent(),
                        this.GUIConfig.getEmail(),
                        this.GUIConfig.getMatNr()));
    }

    /**
     * creates Submission with data from this project.
     * @return
     */
    public Submission createSubmission() {
        Map<String, String> map = assignment.getCodeMap();
        List<Task> tasks = assignment.getTasks()
                .stream()
                .map((key) -> Task.parseToken(
                        new SubmissionTokenBuilder().setTag(new Tag(key)).setBody(map.get(key)).createSubmissionToken())).collect(Collectors.toList());
        return new Submission(studentList(), tasks, assignment.getName());
    }

}
