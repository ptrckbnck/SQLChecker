package de.unifrankfurt.dbis.Submission;


import de.unifrankfurt.dbis.DBFit.CSVCreator;
import de.unifrankfurt.dbis.DBFit.CustomMySQLTest;
import de.unifrankfurt.dbis.DBFit.ResultStorage;
import de.unifrankfurt.dbis.config.DataSource;
import fit.Parse;
import fit.exception.FitParseException;

import java.io.PrintStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * represents DBFIT Solution File
 */
public class Solution {


    /**
     * Submission wihtout any non-TaskSQL Tasks.
     */
    private Submission workSubmission;


    /**
     * html for Dbifit testing.
     */
    private final String dbFitHtml;

    private List<List<String>> resultHeaders;

    public List<List<String>> getResultHeaders() {
        return resultHeaders;
    }

    //TODO replace with constructor parameter
    public void setResultHeaders(List<List<String>> resultHeaders) {
        this.resultHeaders = resultHeaders;
    }


    /**
     * submission from which this Solution File where generated from.
     */


    public Solution(Submission submission, String dbFitHtml) {
        this.workSubmission = submission;
        this.dbFitHtml = dbFitHtml;
    }

    protected static List<Task> fixedTaskList(Submission sub, List<Tag> tags, List<Tag> faultyTags) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            Tag curTag = tags.get(i);
            Task newTask;
            if (faultyTags.contains(curTag)) {
                newTask = sub.getTaskByTag(curTag);
            } else {
                newTask = new TaskNonCallable(curTag, "tag missing");
            }
            tasks.add(newTask);
        }
        return tasks;
    }

    public String getDBFitHtml() {
        return dbFitHtml;
    }


    /**
     * get all String names of all Tags
     *
     * @return List<String> of tag name
     */
    public List<String> getDBFitTags() {
        return this.workSubmission.getTagStrings();
    }

    public Submission getSubmission() {
        return workSubmission;
    }

    public String getName() {
        return this.workSubmission.getName();
    }

    /**
     * creates html which can be used by dbfit parser to run test.
     * A Solution has result for every tag.
     * This function replaces Tags from this Solution with sql code from submission for given tag.
     * If a Dbfit parser matches result with calculated result from code of submission the Task is success.
     *
     * @param submission
     * @return Html code for DBFIT parser
     */

    public String generateSurveyHTML(Submission submission) {
        String result = this.dbFitHtml;
        for (Tag tag : this.workSubmission.getTags()) {
            if (tag.isStatic()) continue;
            String submissionCode = submission.getTaskByTag(tag).getSql();
            submissionCode = submissionCode
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
            result = result.replace(tag.serialized(), submissionCode);
        }
        return result;
    }

    protected Count runDBFitTest(DataSource source, SQLScript resetScript, Parse p) throws SQLException {
        CustomMySQLTest test = new CustomMySQLTest();
        test.connect(source);
        resetScript.execute(source);
        PrintStream err = System.err; // catch dbfit output
        System.setErr(null);
        test.doTables(p);
        test.close();
        System.setErr(err);
        return new Count(test.counts);
    }

    public String generateCSVHeader(){
        ArrayList<String> fullList = new ArrayList<>();
        fullList.add("Path");
        fullList.add("Authors");
        fullList.add("Solution");
        fullList.addAll(this.workSubmission.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        fullList.add("#Success");
        fullList.add("ErrorMsg");
        return fullList.stream()
                .collect(Collectors.joining("\", \"",
                        "\"",
                        "\""));
    }


    protected String getParseResult(Parse parse) {
        StringBuilder storage = new StringBuilder();
        this.evaluateDBFitParse(storage, parse, 0);
        return storage.toString();
    }


    /**
     * Stores the annotated parseLines String in a class variable. This makes sure that the output is stored in the correct
     * order.
     *
     * @param parse Parse object which should be stored
     * @param iter  a counter which limits the recursion level and the result
     */
    private void evaluateDBFitParse(StringBuilder storage, Parse parse, int iter) {
        if (iter > 100) {
            storage.append("\n reached threshold");
            return;
        }
        storage.append(parse.leader);
        storage.append(parse.tag);

        if (parse.parts != null) {
            evaluateDBFitParse(storage, parse.parts, ++iter);
        } else {
            storage.append(parse.body);
        }

        storage.append(parse.end);

        if (parse.more != null) {
            evaluateDBFitParse(storage, parse.more, ++iter);
        } else {
            storage.append(parse.trailer);
        }
    }

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

    public ResultStorage evaluate(Path root,
                                  DataSource source,
                                  SQLScript resetScript,
                                  Submission submission
    ) throws FitParseException, SQLException {
        return evaluate(root, source, resetScript, submission, false);
    }
    /**
     * runs DBFitTest to evaluate submission.
     *
     * @param root        path of submission
     * @param source      Datasource
     * @param resetScript ResetScript
     * @param submission  Submission
     * @return ResultStorage
     * @throws SQLException
     * @throws FitParseException
     */
    public ResultStorage evaluate(Path root,
                                  DataSource source,
                                  SQLScript resetScript,
                                  Submission submission,
                                  boolean verbose)
            throws SQLException, FitParseException {
        resetScript.execute(source);
        List<List<String>> subHeaders = submission.generateResultHeaders(source);
        List<Boolean> diff = diffResultHeaders(this.resultHeaders, subHeaders);

        String html = this.generateSurveyHTML(submission);
        /*if (verbose) {
            System.out.println(html);
        }*/
        Parse p = new Parse(html);
        Count count = runDBFitTest(source, resetScript, p);
        String parseResult = getParseResult(p);


        return new ResultStorage(root, this, submission, parseResult, diff);
    }

    private List<Boolean> diffResultHeaders(List<List<String>> resultHeaders, List<List<String>> subHeaders) {
        ArrayList<Boolean> diff = new ArrayList<>();
        for (int i = 0; i < resultHeaders.size(); i++) {
            if (resultHeaders.size() != subHeaders.size()) {
                diff.add(false);
            } else {
                diff.add(resultHeaders.get(i).equals(subHeaders.get(i)));
            }
        }
        return diff;
    }

    public CSVCreator csvCreator() {
        return new CSVCreator().useSubmissionPath()
                .useAuthors()
                .useMatrikelNr()
                .useSolutionName()
                .useAllStatusWithSQLHeaderCheck(this.getDBFitTags())
                .useSuccess()
                .useEncoding()
                .useErrorMsg();
    }

    public Submission tryToFixTagsFor(Submission sub) {
        List<Tag> tags = this.workSubmission.getNonStaticTags();
        List<Tag> faultyTags = sub.getNonStaticTags();
        if (faultyTags.isEmpty()) return null; //no tags at all
        if (new HashSet<>(faultyTags).size() != faultyTags.size()) return null; //duplicate keys
        if (!isSublistWithGaps(tags, faultyTags)) return null;
        List<Task> tasks = fixedTaskList(sub, tags, faultyTags);
        Submission newSub = new Submission(sub.getAuthors(), tasks, sub.getName());
        newSub.setCharset(sub.getCharset());
        newSub.setPath(sub.getPath());
        return newSub;
    }
}
