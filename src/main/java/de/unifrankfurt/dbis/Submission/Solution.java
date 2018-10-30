package de.unifrankfurt.dbis.Submission;


import de.unifrankfurt.dbis.DBFit.CustomMySQLTest;
import de.unifrankfurt.dbis.DBFit.ResultStorage;
import de.unifrankfurt.dbis.config.DataSource;
import fit.Parse;
import fit.exception.FitParseException;

import java.io.PrintStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * represents DBFIT Solution File
 */
public class Solution {


    /**
     * Submission wihtout any non-TaskSQL Tasks.
     */
    private Submission<TaskSQL> workSubmission;


    /**
     * html for Dbifit testing.
     */
    private final String dbFitHtml;


    /**
     * submission from which this Solution File where generated from.
     */


    public Solution(Submission<? extends Task> submission, String dbFitHtml) {
        this.workSubmission = submission.onlyTaskSQLSubmission();
        this.dbFitHtml = dbFitHtml;
    }

    public Submission<TaskSQL> getSubmission() {
        return workSubmission;
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

    /**
     * creates html which can be used by dbfit parser to run test.
     * A Solution has result for every tag.
     * This function replaces Tags from this Solution with sql code from submission for given tag.
     * If a Dbfit parser matches result with calculated result from code of submission the Task is success.
     *
     * @param submission
     * @return Html code for DBFIT parser
     */
    public String generateSurveyHTML(Submission<TaskSQL> submission) {
        String result = this.dbFitHtml;
        for (Tag tag : this.workSubmission.getTags()) {
            if (tag.isStatic()) continue;
            String submissionCode = submission.getTaskByTag(tag).getCodeString();
            submissionCode = submissionCode
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
            result = result.replace(tag.serialized(), submissionCode);
        }
        return result;
    }

    public String getName() {
        return this.workSubmission.getName();
    }


    /**
     * runs DBFitTest to evaluate submission.
     *
     * @param root        path of submission
     * @param source      Datasource
     * @param resetScript ResetScript
     * @param submission  Submission
     * @param verbose     verbose Mode
     * @return ResultStorage
     * @throws SQLException
     * @throws FitParseException
     */
    public ResultStorage evaluate(Path root,
                                  DataSource source,
                                  SQLScript resetScript,
                                  Submission<TaskSQL> submission,
                                  boolean verbose)
            throws SQLException, FitParseException{
        String html = this.generateSurveyHTML(submission);
        Parse p = new Parse(html);
        Count count = runDBFitTest(source, resetScript, p);
        String parseResult = getParseResult(p);

        if (verbose) {
            System.out.println(parseResult);
            System.out.println(count);
        }
        return new ResultStorage(root, this, submission, parseResult);
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
     * Stores the annotated parse String in a class variable. This makes sure that the output is stored in the correct
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
}
