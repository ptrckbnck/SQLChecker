package de.unifrankfurt.dbis;

import de.unifrankfurt.dbis.Inner.Base;
import de.unifrankfurt.dbis.Inner.BaseBuilder;
import de.unifrankfurt.dbis.Inner.Parser.BaseType;
import de.unifrankfurt.dbis.Inner.Parser.TaskInterface;
import de.unifrankfurt.dbis.Inner.Parser.TaskSQL;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;


public class TemplateGenerator {

    private final Path path;
    private final Path out;
    private final Boolean useStdout;


    public TemplateGenerator(Path path, Path out, Boolean useStdout) {
        this.path = path;
        this.out = out;
        this.useStdout = useStdout;
    }

    public void run() throws IOException {
        Base base = Base.fromPath(path);
        if (!base.getType().equals(BaseType.solution)) {
            System.err.println("File is not of type Solution");
            return;
        }
        List<TaskInterface> tasks = base.getTasks();
        BaseBuilder bb = new BaseBuilder();
        bb.setType(BaseType.template);
        bb.setName(base.getName());
        tasks.stream()
                .filter(obj -> obj instanceof TaskSQL)
                .map(obj -> (TaskSQL) obj)
                .map(TaskSQL::clearExtra)
                .map(this::modifySQL)
                .forEach(bb::addTask);
        bb.setCharset(base.getCharset());
        Base newBase = bb.build();

        if (useStdout) {
            System.out.println(newBase.serialize());
        } else if (Objects.isNull(out)) {
            newBase.storeInPath(Paths.get("template.sqlt"));
        } else {
            if (!out.endsWith("sqlt"))
                newBase.storeInPath(out.getParent().resolve(out.getFileName() + ".sqlt"));
            else {
                newBase.storeInPath(out);
            }
        }
    }

    private TaskSQL modifySQL(TaskSQL task) {
        String sql = task.getSql().trim();

        String comment = "";
        if (sql.startsWith("/* ")) {
            int endcomment = sql.indexOf("*/");
            comment = sql.substring(0, endcomment + 2);
        } else if (sql.startsWith("#")) {
            comment = sql.split("\\r?\\n")[0].trim();
        }
        sql = sql.replace(comment, "").trim();
        sql = comment + "\n" + sql.split(" ")[0].toUpperCase() + " ...\n";
        return new TaskSQL(task.getName(),
                task.getScore(),
                task.getGroup(),
                task.getSchema(),
                task.getOrder(),
                sql);
    }
}
