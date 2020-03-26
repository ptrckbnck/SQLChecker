package de.unifrankfurt.dbis.IO;

import de.unifrankfurt.dbis.Inner.Base;
import de.unifrankfurt.dbis.Inner.BaseBuilder;
import de.unifrankfurt.dbis.Inner.Parser.BaseType;
import de.unifrankfurt.dbis.Inner.Parser.TaskSQL;
import de.unifrankfurt.dbis.Inner.Student;
import de.unifrankfurt.dbis.StudentGUI.Assignment;
import de.unifrankfurt.dbis.config.GUIConfig;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
     * creates Inner with data from this project.
     *
     * @return
     */
    public Base createSubmission_old() {
        /*List<String> codes = assignment.getCodes();
        List<String> tasks = assignment.getTasks();
        List<TaskInterface> list = IntStream.range(0, tasks.size())
                .mapToObj(i -> Task.parseToken(
                        new SubmissionTokenBuilder().
                                setTag(new Tag(tasks.get(i)))
                                .setBody(codes.get(i))
                                .createSubmissionToken()))
                .collect(Collectors.toList());
        return new Base(studentList(), list, assignment.getName(), StandardCharsets.UTF_8, baseType);*///TODO fix
        return null;
    }


    public List<Student> getStudents() {
        ArrayList<Student> newList = new ArrayList<>();
        Student student1 = new Student(this.GUIConfig.getNameStudent(),
                this.getGUIConfig().getEmail(),
                this.getGUIConfig().getMatNr());
        newList.add(student1);
        if (this.GUIConfig.isPartnerOk()) {
            Student student2 = new Student(this.GUIConfig.getPartnerName(),
                    this.getGUIConfig().getPartnerEmail(),
                    this.getGUIConfig().getPartnerMatNr());
            newList.add(student2);
        }
        return newList;
    }

    public Base createSubmission() {
        List<String> codes = assignment.getCodes();
        List<String> tasks = assignment.getTasks();
        BaseBuilder bb = new BaseBuilder();
        bb.setType(BaseType.submission);
        bb.setName(assignment.getName());
        bb.setCharset(StandardCharsets.UTF_8);
        bb.setStudents(this.getStudents());
        IntStream.range(0, tasks.size())
                .mapToObj(i -> new TaskSQL(tasks.get(i), null, null, null, null, codes.get(i)))
                .forEach(bb::addTask);
        return bb.build();
    }

}
