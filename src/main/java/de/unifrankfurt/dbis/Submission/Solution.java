package de.unifrankfurt.dbis.Submission;


import java.util.ArrayList;
import java.util.List;

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

    public Submission getSubmission() {
        return fullSubmission;
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
}
