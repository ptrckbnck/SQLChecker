package de.unifrankfurt.dbis.Inner;

import java.util.List;

public class SolutionMetadata {

    private final String name;
    private final List<String> tags;
    private final List<String> nonStaticTags;

    public SolutionMetadata(String name, List<String> tags, List<String> nonStaticTags) {
        this.name = name;
        this.tags = tags;
        this.nonStaticTags = nonStaticTags;
    }


    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getNonStaticTags() {
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
