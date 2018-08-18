package de.unifrankfurt.dbis.DBFit;

/**
 *
 */


import de.unifrankfurt.dbis.Submission.Count;
import de.unifrankfurt.dbis.Submission.Submission;

import java.util.ArrayList;

/**
 * Stores results as raw text and as csv
 *
 * @author Max Hofmann
 */
public class NewResultStorage {

    /**
     * Raw result text (html with annotations)
     */
    private String raw;

    /**
     * Path of the submission file corresponding to the results
     * stored in this class
     */
    private Submission submission;


    public Count count;

    /**
     * Log entry corresponding to the raw results
     */
    private String logEntry = "";

    private ArrayList<String> status;

    public Count staticCount;

    /**
     * Creates a ResultStorage object
     *
     * @param resultRaw The raw annotated html results for this submission
     */
    public NewResultStorage(Submission submission, String resultRaw, Count count) {
        this.status = new ArrayList<>();
        this.raw = resultRaw;
        this.submission = submission;
        this.count = count;
    }

    /**
     * @return The annotated raw text as html
     */
    public String getRawText() {
        return this.raw;
    }


    /**
     * Checks if the results indicate, that this submission is
     * completely correct
     *
     * @return True if (WRONG + IGNORED + ERRORS == 0)
     */
    public boolean isPassed() {
        return (count.wrong + count.ignored + count.exceptions == 0);
    }

    public void setCount(Count count) {
        this.count = count;
    }

    /**
     * @return
     */
    public Count getCount() {
        return count;
    }

    /*TODO
    private String appendix() {
        return submission.getFilePath() + CSV.delimiter
                + submission.getTagMissing() + CSV.delimiter + count.toCSV() + staticCount.toCSV()
                + staticAmount + CSV.delimiter;
    }*/


    /*
    public ArrayList<String> toCSV() {
        ArrayList<String> csvLines = new ArrayList<>();

        if (submission.getStudents() != null) {
            for (Student student : submission.getStudents()) {
                String csvLine = student.getName() + CSV.delimiter + student.getMatriculationNumber() + CSV.delimiter
                        + student.getEmailAddress() + CSV.delimiter + appendix();
                csvLines.add(csvLine);
            }
        } else {
            String csvLine = CSV.delimiter + CSV.delimiter + CSV.delimiter + appendix();
            csvLines.add(csvLine);
        }

        return csvLines;
    }*/

    public void evaluate() {
        String[] statements = raw.split("</table>");

        // skip the connection and driver definition if exists
        int start = (statements.length > 2) ? 2 : 0;


        boolean expectsError;

        for (int i = start; i < statements.length; i++) {
            String statusLine = "";
            String statement = statements[i];

            expectsError = statement.contains("<!--error-->");

            if (statement.contains("class=\"pass\"")) {
                statusLine += "p";
                if (expectsError) {
                    count.right = count.right > 0 ? count.right - 1 : 0;
                    count.exceptions++;
                }
            }
            if (statement.contains("class=\"ignore\"")) {
                statusLine += "i";
            }
            if (statement.contains("class=\"fail\"")) {
                statusLine += "f";
            }
            if (statement.contains("class=\"error\"")) {
                statusLine += "e";
                if (expectsError) {
                    count.right++;
                    count.exceptions = count.exceptions > 0 ? count.exceptions : 0;
                }
            }

            // errorExpected, but query has no status annotation
            if (expectsError) {
                if (!((statement.contains("class=\"pass\""))
                        || (statement.contains("class=\"ignore\""))
                        || (statement.contains("class=\"fail\""))
                        || (statement.contains("class=\"error\"")))) {
                    count.exceptions++;
                } else {
                }
            }
            status.add(statusLine);
        }
    }


    /**
     * For storing information about executing static queries of a Student
     * submission
     *
     * @param staticRs The result set which was created from executing
     *                 the static queries of a Student submission
     * @param qamount  Amount of static queries executed
     */
    public void setStaticResults(NewResultStorage staticRs, int qamount) {
        // store count & amount
        /*
      Amount of static queries executed
     */
        int staticAmount = qamount;
        staticCount = staticRs.count;
    }

    @Override
    public String toString() {
        return "NewResultStorage{" +
                "raw='" + raw + '\'' +
                ", submission=" + submission.getName() +
                ", count=" + count +
                ", logEntry='" + logEntry + '\'' +
                '}';
    }
}
