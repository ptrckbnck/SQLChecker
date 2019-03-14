package de.unifrankfurt.dbis.Inner.Parser;

import com.google.gson.Gson;
import de.unifrankfurt.dbis.Inner.Authors;
import de.unifrankfurt.dbis.Inner.BaseBuilder;
import de.unifrankfurt.dbis.Inner.Student;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class SubTokenHead implements SubToken {
    public final static String id = "head";
    private final BaseType type;
    private final String name;
    private final Authors students;

    public SubTokenHead(BaseType type, String name, Authors students) {
        this.type = type;
        this.name = name;
        this.students = students;
    }

    public static SubTokenHead fromRawToken(RawToken rawToken) {
        if (!rawToken.getName().equals(id)) return null;
        String addition = rawToken.getAddition();
        AdditionHead head = parseAddition(addition);
        if (Objects.isNull(head)) return null;
        Authors students = head.getStudents()
                .stream()
                .map(Student::parse).collect(Collectors.toCollection(Authors::new));
        BaseType type;
        try {
            type = BaseType.valueOf(head.getType());
        } catch (IllegalArgumentException e) {
            type = null;
        }
        return new SubTokenHead(type, head.getSubmission_name(), students);
    }

    private static AdditionHead parseAddition(String addition) {
        try {
            return new Gson().fromJson(addition, AdditionHead.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void build(BaseBuilder bb) {
        bb.setName(name)
                .setType(type)
                .setStudents(students);

    }

    public String serialize() {
        AdditionHead head = new AdditionHead(this.type.name(), this.name, this.students.toStringList());
        return "/*%%head%%" + new Gson().toJson(head) + "%%*/\n";
    }

    public BaseType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<Student> getStudents() {
        return students;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SubTokenHead.class.getSimpleName() + "[", "]")
                .add("type='" + type + "'")
                .add("name='" + name + "'")
                .add("students=" + students)
                .toString();
    }

    private static class AdditionHead {
        private final String type;
        private final String name;
        private final List<List<String>> authors;

        public AdditionHead(String type, String name, List<List<String>> students) {
            this.type = type;
            this.name = name;
            this.authors = students;
        }

        public String getType() {
            return type;
        }

        public String getSubmission_name() {
            return name;
        }

        public List<List<String>> getStudents() {
            return authors;
        }
    }
}


