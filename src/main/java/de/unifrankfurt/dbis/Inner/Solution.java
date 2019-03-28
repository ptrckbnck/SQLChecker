package de.unifrankfurt.dbis.Inner;


import de.unifrankfurt.dbis.Inner.Parser.TaskInterface;
import de.unifrankfurt.dbis.SQL.*;
import de.unifrankfurt.dbis.config.DataSource;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


public class Solution extends CheckerFrame {
    private final List<SQLData> expectedResults;

    protected Solution(List<TaskInterface> tasks, String name, Charset charset, List<SQLData> sqlData) {
        super(tasks, name, charset);
        this.expectedResults = sqlData;
    }

    public static Solution createSolution(Base base, SQLScript resetScript, DataSource datasource) {
        return createSolution(base.getTasks(), base.getName(), base.getCharset(), resetScript, datasource);
    }

    public static Solution createSolution(List<TaskInterface> tasks, String name, Charset charset, SQLScript resetScript, DataSource datasource) {
        try {
            resetScript.execute(datasource);
        } catch (SQLException e) {
            System.err.println("WARNING: error running ResetScript: " + e.getMessage());
            return null;
        }
        List<SQLData> expectedResults = new ArrayList<>();
        for (TaskInterface t : tasks) {
            String sql = t.getSql();
            SQLData result = SQLResults.execute(datasource, sql);
            if (result instanceof SQLDataFail) {
                System.err.println("WARNING: error while running task: " + t + "\n" + result);
                return null;
            }
            expectedResults.add(result);
        }
        return new Solution(tasks, name, charset, expectedResults);
    }




    public List<SQLData> getExpectedResults() {
        return expectedResults;
    }

    public String getExpectedResultPrintable() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.tasks.size(); i++) {
            builder.append(tasks.get(i).serialize()).append("\n").append(expectedResults.get(i).toString()).append("\n");
        }
        return builder.toString();
    }

    public ScoreGroup getScoreGroup() {
        ScoreGroup group = new ScoreGroup();
        this.tasks.forEach(group::addTask);
        return group;
    }

    public void evaluate(ResultStorage store,
                         DataSource source,
                         SQLScript resetScript,
                         Base base,
                         boolean verbose) throws SQLException {
        store.setCharset(base.getCharset());
        resetScript.execute(source);

        ScoreGroup group = getScoreGroup();
        List<SQLResultDiff> diffs = new ArrayList<>();
        List<SQLData> results = new ArrayList<>();
        int subTask = 0;
        for (int i = 0; i < this.getTasks().size(); i++) {
            TaskInterface t = this.getTasks().get(i);
            SQLData expectedResult = this.getExpectedResults().get(i);
            SQLData actualResult;
            if (t.isStatic()) {
                actualResult = t.execute(source);
            } else {
                actualResult = base.getTasks().get(subTask++).execute(source);
            }
            results.add(actualResult);
            SQLResultDiff diff = SQLResultDiffer.diff(expectedResult, actualResult);
            diffs.add(diff);
            if (verbose) {
                String s = t.getName() + ": " + diff.getMessage();
                System.out.println(s);
            }
        }

        store.setScore(group.analyseDiffs(diffs));
        store.setSqlData(results);
        store.setDiffs(diffs);
    }

    public SolutionMetadata getMetaData() {
        return new SolutionMetadata(this.name, this.getTags(), this.getNonStaticTags(), getScoreGroup());
    }

    public class ScoreGroup {
        List<String> groups;
        Map<String, Integer> mapGroupScore;
        Map<String, List<String>> mapGroupTask;

        private ScoreGroup() {
            groups = new ArrayList<>();
            mapGroupScore = new HashMap<>();
            mapGroupTask = new HashMap<>();
        }

        private void addTask(TaskInterface task) {
            String group = Objects.requireNonNullElse(task.getGroup(), task.getName()); //if no group given, default to name;
            Integer score = task.getScore();
            if (groups.contains(group)) {
                mapGroupTask.get(group).add(task.getName());
            } else {
                groups.add(group);
                mapGroupTask.put(group, new ArrayList<>(List.of(task.getName())));
            }
            if (Objects.nonNull(score)) {
                mapGroupScore.putIfAbsent(group, task.getScore());
            }
        }

        public List<String> getGroups() {
            return groups;
        }

        public Integer getScore(String group) {
            return groups.contains(group) ? this.mapGroupScore.getOrDefault(group, 1) : null;
        }

        public List<String> getMember(String group) {
            return groups.contains(group) ? this.mapGroupTask.get(group) : null;
        }

        public List<Integer> analyseDiffs(List<SQLResultDiff> diffs) {
            assert Solution.this.tasks.size() == diffs.size();
            HashMap<String, Boolean> map = new HashMap<>();
            for (int i = 0; i < Solution.this.tasks.size(); i++) {
                String task = tasks.get(i).getName();
                Boolean bool = diffs.get(i).isOk();
                map.put(task, bool);
            }
            List<Integer> scores = new ArrayList<>();
            for (String group : groups) {
                List<String> groupTasks = getMember(group);
                List<Boolean> l = groupTasks.stream().map(map::get).collect(Collectors.toList());
                if (l.contains(false)) {
                    scores.add(0);
                } else {
                    scores.add(getScore(group));
                }
            }
            return scores;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", ScoreGroup.class.getSimpleName() + "[", "]")
                    .add("groups=" + groups)
                    .add("mapGroupScore=" + mapGroupScore)
                    .add("mapGroupTask=" + mapGroupTask)
                    .toString();
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.tasks.size(); i++) {
            stringBuilder.append(this.tasks.get(i).serialize())
                    .append("\n")
                    .append(this.expectedResults.get(i))
                    .append("\n");
        }
        return "Solution{\n" +
                ", name='" + name + '\n' +
                ", charset=" + charset + "\n"
                + stringBuilder.toString() +
                '}';
    }
}
