package de.unifrankfurt.dbis.Inner;

import de.unifrankfurt.dbis.Inner.Parser.TaskInterface;
import de.unifrankfurt.dbis.Inner.Parser.TaskSQL;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CheckerFrame {


    /**
     * defined Tasks
     */
    protected final List<TaskInterface> tasks;

    /**
     * Name of this Inner.
     * The Name of Inner for a given solution should be identical.
     */
    protected final String name;
    protected final Charset charset;

    protected CheckerFrame(List<TaskInterface> tasks, String name, Charset charset) {
        this.tasks = tasks;
        this.name = name;
        this.charset = charset;
    }

    public List<TaskInterface> getTasks() {
        return tasks;
    }

    public String getName() {
        return name;
    }

    public Charset getCharset() {
        return charset;
    }

    public List<String> getTags() {
        return this.getTasks()
                .stream()
                .map(TaskInterface::getName)
                .collect(Collectors.toList());
    }

    public List<String> getNonStaticTags() {
        return this.getTasks()
                .stream()
                .filter(x -> x.getClass().isAssignableFrom(TaskSQL.class))
                .map(TaskInterface::getName)
                .collect(Collectors.toList());
    }

}
