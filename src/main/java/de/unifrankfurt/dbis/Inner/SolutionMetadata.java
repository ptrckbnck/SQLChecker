package de.unifrankfurt.dbis.Inner;

import java.util.List;

public class SolutionMetadata {

    private final String name;
    private final List<Tag> tags;
    private final List<Tag> nonStaticTags;

    public SolutionMetadata(String name, List<Tag> tags, List<Tag> nonStaticTags) {
        this.name = name;
        this.tags = tags;
        this.nonStaticTags = nonStaticTags;
    }


    public String getName() {
        return name;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public List<Tag> getNonStaticTags() {
        return nonStaticTags;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SolutionMetadata{");
        sb.append("name='").append(name).append('\'');
        sb.append(", tags=").append(tags);
        sb.append(", nonStaticTags=").append(nonStaticTags);
        sb.append('}');
        return sb.toString();
    }
}
