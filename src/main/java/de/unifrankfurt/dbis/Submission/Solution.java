package de.unifrankfurt.dbis.Submission;


import de.unifrankfurt.dbis.DBFit.CustomMySQLTest;
import de.unifrankfurt.dbis.DBFit.NewResultStorage;
import de.unifrankfurt.dbis.config.DataSource;
import fit.Parse;
import fit.exception.FitParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final Submission<? extends Task> fullSubmission;


    public Solution(Submission<? extends Task> submission, String dbFitHtml) {
        this.fullSubmission = submission;
        this.workSubmission = submission.onlyTaskSQLSubmission();
        this.dbFitHtml = dbFitHtml;
    }

    public Submission<? extends Task> getSubmission() {
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
        List<String> tags = new ArrayList<>();
        for (Tag tag : this.workSubmission.getTags())
            tags.add(tag.getName());
        return tags;
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

    public NewResultStorage evaluate(DataSource source, SQLScript resetScript, Submission<TaskSQL> submission) throws SQLException, FitParseException{
        String html = this.generateSurveyHTML(submission);
        CustomMySQLTest test = new CustomMySQLTest();
        test.connect(source);
        Parse p = new Parse(html);
        resetScript.execute(source);
        test.doTables(p);
        test.close();
        return new NewResultStorage(submission, getParseResult(p), new Count(test.counts));
    }





    private String getParseResult(Parse parse) {
        StringBuilder storage = new StringBuilder();
        this.printParseStr(storage, parse, 0);
        return storage.toString();
    }


    /**
     * Stores the annotated parse String in a class variable. This makes sure that the output is stored in the correct
     * order.
     *
     * @param parse Parse object which should be stored
     * @param iter  a counter which limits the recursion level and the result
     */
    private void printParseStr(StringBuilder storage, Parse parse, int iter) {
        if (iter > 100) {
            storage.append("\n reached threshold");
            return;
        }
        storage.append(parse.leader);
        storage.append(parse.tag);

        if (parse.parts != null) {
            printParseStr(storage, parse.parts, ++iter);
        } else {
            storage.append(parse.body);
        }

        storage.append(parse.end);

        if (parse.more != null) {
            printParseStr(storage, parse.more, ++iter);
        } else {
            storage.append(parse.trailer);
        }
    }
}
