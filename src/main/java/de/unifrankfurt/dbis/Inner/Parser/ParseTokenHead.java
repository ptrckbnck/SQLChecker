package de.unifrankfurt.dbis.Inner.Parser;

import com.google.gson.Gson;
import de.unifrankfurt.dbis.Inner.Authors;
import de.unifrankfurt.dbis.Inner.BaseBuilder;
import de.unifrankfurt.dbis.Inner.Student;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ParseTokenHead implements ParseToken {
    public final static String id = "head";
    private final BaseType type;
    private final String baseName;
    private final Authors students;

    public ParseTokenHead(BaseType type, String baseName, Authors students) {
        this.type = type;
        this.baseName = baseName;
        this.students = students;
    }

    public static ParseTokenHead fromRawToken(RawToken rawToken) {
        if (!Objects.equals(rawToken.getType(), id)) return null;
        //getName is ignored
        String addition = rawToken.getAddition();
        AdditionHead head = parseAddition(addition);
        if (Objects.isNull(head)) return null;
        List<List<String>> headStudents = head.getStudents();

        Authors students;
        if (Objects.isNull(headStudents)) {
            students = new Authors();
        } else {
            students = head.getStudents()
                    .stream()
                    .map(Student::parse)
                    .collect(Collectors.toCollection(Authors::new));
        }

        BaseType type;
        if (Objects.isNull(head.getType())) {
            type = BaseType.unknown;
        } else {
            try {
                type = BaseType.valueOf(head.getType());
            } catch (IllegalArgumentException e) {
                type = BaseType.unknown;
            }
        }

        return new ParseTokenHead(type, head.getSubmission_name(), students);
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
        bb.setName(baseName)
                .setType(type)
                .setStudents(students);

    }

    public String serialize() {
        AdditionHead head = new AdditionHead(this.type.name(), this.baseName, this.students.toStringList());
        return "/*%%%%head%%" + new Gson().toJson(head) + "%%*/\n";
    }

    public BaseType getType() {
        return type;
    }

    public String getBaseName() {
        return baseName;
    }

    public List<Student> getStudents() {
        return students;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParseTokenHead.class.getSimpleName() + "[", "]")
                .add("type='" + type + "'")
                .add("baseName='" + baseName + "'")
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


