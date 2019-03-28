package de.unifrankfurt.dbis.Inner;

import java.util.List;
import java.util.StringJoiner;

public class SolutionMetadata {

    private final String name;
    private final List<String> tags;
    private final List<String> nonStaticTags;
    private final Solution.ScoreGroup scoreGroup;

    public SolutionMetadata(String name, List<String> tags, List<String> nonStaticTags, Solution.ScoreGroup scoreGroup) {
        this.name = name;
        this.tags = tags;
        this.nonStaticTags = nonStaticTags;
        this.scoreGroup = scoreGroup;
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

    public Solution.ScoreGroup getScoreGroup() {
        return scoreGroup;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SolutionMetadata.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("tags=" + tags)
                .add("nonStaticTags=" + nonStaticTags)
                .add("scoreGroup=" + scoreGroup)
                .toString();
    }


}
