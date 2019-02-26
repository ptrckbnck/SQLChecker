package de.unifrankfurt.dbis.Inner;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CheckerFrame {


    /**
     * defined Tasks
     */
    protected final List<Task> tasks;

    /**
     * Name of this Inner.
     * The Name of Inner for a given solution should be identical.
     */
    protected final String name;
    protected final Charset charset;

    protected CheckerFrame(List<Task> tasks, String name, Charset charset) {
        this.tasks = tasks;
        this.name = name;
        this.charset = charset;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public String getName() {
        return name;
    }

    public Charset getCharset() {
        return charset;
    }

    public List<Tag> getTags() {
        return this.getTasks()
                .stream()
                .map(Task::getTag)
                .collect(Collectors.toList());
    }

    public List<Tag> getNonStaticTags() {
        return this.getTasks()
                .stream()
                .map(Task::getTag)
                .filter(x -> !x.isStatic())
                .collect(Collectors.toList());
    }

    protected List<Task> getTasksByTag(Tag curTag) {
        return this.getTasks()
                .stream()
                .filter(x -> x.getTag().equals(curTag))
                .collect(Collectors.toList());
    }

    protected List<String> getTagNames() {
        return this.getTasks()
                .stream()
                .map(x -> x.getTag().getName())
                .collect(Collectors.toList());
    }
}
