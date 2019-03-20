package de.unifrankfurt.dbis.Inner;

import de.unifrankfurt.dbis.Inner.Parser.BaseType;
import de.unifrankfurt.dbis.Inner.Parser.TaskInterface;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class BaseBuilder {
    private String name;
    private BaseType type = BaseType.submission;
    private List<Student> students = List.of();
    private List<TaskInterface> tasks = new ArrayList<>();
    private Charset charset;
    private Path path;

    public BaseBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public BaseBuilder setType(BaseType type) {
        this.type = type;
        return this;
    }

    public BaseBuilder setStudents(List<Student> students) {
        this.students = students;
        return this;
    }

    public BaseBuilder addTask(TaskInterface task) {
        this.tasks.add(task);
        return this;
    }

    public Base build() {
        if (!Objects.isNull(type)) {
            switch (type) {
                case template:
                case submission:
                    // tasks = this.tasks.stream().filter(x -> x.getClass().isAssignableFrom(TaskSQL.class)).collect(Collectors.toList());
                case solution:
            }
        }
        return new Base(name, charset, type, students, tasks, path);
    }

    public BaseBuilder setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public BaseBuilder setPath(Path path) {
        this.path = path;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BaseBuilder.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("type=" + type)
                .add("students=" + students)
                .add("tasks=" + tasks)
                .add("charset=" + charset)
                .add("path=" + path)
                .toString();
    }
}
