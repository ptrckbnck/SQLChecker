package de.unifrankfurt.dbis.Inner;


import de.unifrankfurt.dbis.Inner.Parser.TaskInterface;
import de.unifrankfurt.dbis.SQL.*;
import de.unifrankfurt.dbis.config.DataSource;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


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
            String sql = t.getSQL();
            SQLData result = SQLResults.execute(datasource, sql);
            if (result instanceof SQLDataFail) {
                //TODO
                System.err.println("WARNING: error while running task: " + t + "\n" + result);
            }
            expectedResults.add(result);
        }
        return new Solution(tasks, name, charset, expectedResults);
    }

    /**
     * TODO test
     *
     * @param sub
     * @param tags
     * @param faultyTags
     * @return
     *//*
    protected static List<Task> fixedTaskList(Base sub, List<String> tags, List<String> faultyTags) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            Tag curTag = tags.get(i);
            Task newTask;
            List<Task> posTasks = sub.getNonStaticTask();
            if (!posTasks.isEmpty()) {
                newTask = posTasks.get(0);
            } else {
                //TODO newTask = new TaskSQL(curTag, parseSchema(tags.get(i).getAddition()), "tag missing");
            }
            tasks.add(newTask);
        }
        return tasks;
    }*/

    /**
     * checks if sublist is a subList of list, gaps allowed.
     * [A,B,D] isSublistWithGaps of [A,B,C,D]
     * conditions: list & sublist have unique items
     *
     * @param list
     * @param sublist
     * @param <T>
     * @return
     */
    public static <T> Boolean isSublistWithGaps(List<T> list, List<T> sublist) {
        int cur = 0;
        for (int i = 0; i < sublist.size(); i++) {
            T item = sublist.get(i);
            if (!list.contains(item)) return false;
            int pos = list.indexOf(item);
            if (pos < cur) return false;
            cur = pos;
        }
        return true;
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


    public void evaluate(ResultStorage store,
                         DataSource source,
                         SQLScript resetScript,
                         Base base,
                         boolean verbose) throws SQLException {
        store.setCharset(base.getCharset());
        resetScript.execute(source);


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
        store.setSqlData(results);
        store.setDiffs(diffs);
    }

    public Base tryToFixTagsFor(Base base) {
        /*List<String> tags = getNonStaticTags();
        List<String> faultyTags = sub.getNonStaticTags();
        if (faultyTags.isEmpty()) return null; //no tags at all
        if (new HashSet<>(faultyTags).size() != faultyTags.size()) return null; //duplicate keys
        if (!isSublistWithGaps(tags, faultyTags)) return null;
        List<Task> tasks = fixedTaskList(sub, tags, faultyTags);
        Base newSub = new Base(sub.getAuthors(), tasks, sub.getName(), sub.getCharset(), baseType);
        newSub.setPath(sub.getPath());*/ //TODO
        return base;
    }


    public SolutionMetadata getMetaData() {
        return new SolutionMetadata(this.name, this.getTags(), this.getNonStaticTags());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.tasks.size(); i++) {
            stringBuilder.append(this.tasks.get(i).serialize()).append("\n").append(this.expectedResults.get(i)).append("\n");
        }
        return "Solution{\n" +
                ", name='" + name + '\n' +
                ", charset=" + charset + "\n"
                + stringBuilder.toString() +
                '}';
    }
}
