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

    public final static String TAG_ADDITION_SEPARATOR = "%%";
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
    private final String addition;

    public final static String AUTHORTAG = "authors";

    public final static String NAMETAG = "submission_name";

    public final static String STATIC = "static";


    public Tag(String name) {
        this.name = name;
        this.addition = null;
    }

    public Tag(String name, String addition) {
        this.name = name;
        this.addition = addition;
    }

    /**
     * Creating a tag basing on a specific String
     *
     * @param line a String which should be checked for a tag
     */
    public static Tag parse(String line) {
        if (line.startsWith(TAG_PREFIX) && line.endsWith(TAG_SUFFIX)) {
            line = line.substring(TAG_PREFIX.length(), line.length() - TAG_SUFFIX.length());
            String[] tagString = line.split(TAG_ADDITION_SEPARATOR, 2);
            String tag = tagString[0];
            if (tag.isEmpty()) {
                return null;
            }
            if (tag.matches(".*\\s.*")) { //contains whitespace
                return null;
            }
            if (tagString.length == 1) {
                return new Tag(tag);
            } else {
                return new Tag(tag, tagString[1]);
            }

        } else {
            return null;
        }
    }

    /**
     * @return String representation of Tag for serializing
     */
    public String serialized() {
        if (Objects.isNull(addition))
            return TAG_PREFIX + name + TAG_SUFFIX;
        else {
            return TAG_PREFIX + name + TAG_ADDITION_SEPARATOR + addition + TAG_SUFFIX;
        }
    }

    public String getName() {
        return name;
    }

    public String getAddition() {
        return addition;
    }

    public boolean isStatic() {
        return this.name.equals(STATIC);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name) &&
                Objects.equals(addition, tag.addition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, addition);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", addition='" + addition + '\'' +
                '}';
    }
}
