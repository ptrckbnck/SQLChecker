package de.unifrankfurt.dbis.Submission;



import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * todo javadocs
 */
public class NewScript {
    private final List<String> queryList;

    public NewScript(List<String> queryList) {
        this.queryList = queryList;
    }

    public List<String> getQueryList() {
        return queryList;
    }


    public static NewScript fromPath(Path resetPath) throws IOException {
        List<String> lines = Files.readAllLines(resetPath, StandardCharsets.UTF_8);
        return fromList(lines);
    }

    private static NewScript fromList(List<String> lines) throws IOException {
        String delimiter = Tag.TAG_PREFIX + Tag.STATIC + Tag.TAG_SUFFIX;
        StringBuilder sb = null;
        ArrayList<String> queryList = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                if (line.equals(delimiter)) {
                    if (sb != null) {
                        queryList.add(sb.toString());
                    }
                    sb = new StringBuilder();
                } else {
                    if (sb == null) {
                        throw new IOException("missing tag");
                    } else {
                        sb.append(line).append("\n");
                    }
                }
            }
        }
        if (sb != null) {
            queryList.add(sb.toString());
        }

        return new NewScript(queryList);
    }

    public void storeInPath(Path path) throws IOException {
        Stream<String> stream = this.queryList.stream().map(
                x -> Tag.TAG_PREFIX + Tag.STATIC + Tag.TAG_SUFFIX + "\n" + x);
        Files.write(
                path,
                stream.collect(Collectors.toList()),
                StandardCharsets.UTF_8);
    }

    public void execute(Statement statement) throws SQLException {
        for (String sql : this.queryList)
            statement.executeUpdate(sql);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewScript newScript = (NewScript) o;
        return Objects.equals(queryList, newScript.queryList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(queryList);
    }
}
