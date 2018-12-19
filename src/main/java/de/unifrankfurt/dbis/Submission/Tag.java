package de.unifrankfurt.dbis.Submission;

import java.util.Objects;

/**
 * Tags are used to separate Tasks from each other as well as additional content to check a exercise. To allow the
 * execution of a file in MySQL the tags are enclosed with sql multiline comment indicator and must not contain
 * whitespaces. Tags can have a plugin specification separated from the tag name by ::
 */
public class Tag {
    /**
     * The name of a tag that can be every expression that does not contain whitespaces colon for example "ex1" or
     * "exercise1" for the first Task. Non-static Tag names have to be unique within a solution file.
     */
    private final String name;

    /**
     * A optional name for a plugin. Like "alternative" to indicate that this exercise is a alternative solution for a
     * proceeding exercise or "FD" to indicate that the exercise is a functional dependency exercise
     */
    private final String plugin;
    /**
     * The tag prefix is the prefix of a multiline SQL comment to guarantee the execution of a submission within another
     * SQL application
     */
    public final static String TAG_PREFIX = "/*";

    /**
     * The tag suffix is the suffix of a multiline SQL comment to guarantee the execution of a submission within another
     * SQL application
     */
    public final static String TAG_SUFFIX = "*/";

    /**
     * The separator is a arbitrary sequence of symbol just for separating the tag name from the plugin name
     */
    public final static String PLUGIN_SEPERATOR = "::";


    public final static String AUTHORTAG = "authors";

    public final static String NAMETAG = "submission_name";

    public final static String STATIC = "static";


    public Tag(String name) {
        this.name = name;
        this.plugin = null;
    }

    public Tag(String name, String plugin) {
        this.name = name;
        this.plugin = plugin;
    }

    /**
     * @return String representation of Tag for serializing
     */
    public String serialized() {
        if (plugin == null)
            return TAG_PREFIX + name + TAG_SUFFIX;
        else
            return TAG_PREFIX + name + PLUGIN_SEPERATOR + plugin + TAG_SUFFIX;
    }

    /**
     * Creating a tag basing on a specific String
     *
     * @param line a String which should be checked for a tag
     */
    public static Tag parse(String line) {
        if (line.startsWith(TAG_PREFIX) && line.endsWith(TAG_SUFFIX) && !line.contains(" ")) {
            String tag = line.substring(TAG_PREFIX.length(), line.indexOf(TAG_SUFFIX));
            if (tag.contains(PLUGIN_SEPERATOR)) {
                String[] parts = tag.split(PLUGIN_SEPERATOR);
                return new Tag(parts[0], parts[1]);
            } else {
                return new Tag(tag);
            }
        } else {
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public String getPlugin() {
        return plugin;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", plugin='" + plugin + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name) &&
                Objects.equals(plugin, tag.plugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, plugin);
    }

    public boolean isStatic() {
        return this.name.equals(STATIC);
    }
}
