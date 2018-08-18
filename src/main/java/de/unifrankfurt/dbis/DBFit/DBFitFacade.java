package de.unifrankfurt.dbis.DBFit;


import de.unifrankfurt.dbis.backend.DB;
import de.unifrankfurt.dbis.config.XConfig;
import de.unifrankfurt.dbis.Submission.Count;
import de.unifrankfurt.dbis.Submission.Submission;
import fit.Parse;
import fit.exception.FitParseException;

import java.sql.SQLException;


/**
 * This is a facade class which provides easy to use functions for checking the SQL part of a students submission via
 * DBFit. todo javadocs
 *
 * @author Max Hofmann, Martin Parnet
 */
public class DBFitFacade {

    /**
     * Connection properties of type MySQLConfig
     */
    private XConfig XConfig;

    /**
     * Initialize a DBFit facade object
     *
     * @param XConfig MySQL Connection configuration
     */
    public DBFitFacade(XConfig XConfig) {
        this.XConfig = XConfig;
    }


    /**
     * Runs a single submission with the main solution. This method does not respect other given alternatives of a
     * solution
     *
     * @param sqlhtml    a html file which contains the statements and the expected result basing on the solution
     * @param submission the submission from which the html was created of
     * @throws SQLException If the function was unable to closeConnection the sql connection
     */
    public NewResultStorage runSubmission(String sqlhtml, Submission submission) throws SQLException {
        NewResultStorage rs = null;
        String errStr = "";
        try (DB db = new DB(this.XConfig)) {
            db.connect();
            Parse target = new Parse(sqlhtml);
            db.doTables(target);
            db.logErrMessage();

            String result = getParseResult(target);

            rs = new NewResultStorage(submission, result, db.getCount());
        } catch (FitParseException ex) {
            errStr = ex.getMessage();
        }
        // an exception was thrown
        if (rs == null) {
            rs = new NewResultStorage(submission, errStr
                    , new Count(0, 0, 0, sqlhtml.split("<table>").length));
        }
        return rs;
    }


    /**
     * This method transforms the Result of the DBFit execution into a html string which can be evaluated
     *
     * @param parse a DBFit Parse object which is returned by the execution
     * @return a String which contains the result of the DBFit execution
     */
    private String getParseResult(Parse parse) {
        StringBuilder storage = new StringBuilder();
        this.printParseStr(storage, parse, 0, 100); //TODO set treshold
        return storage.toString();
    }


    /**
     * Stores the annotated parse String in a class variable. This makes sure that the output is stored in the correct
     * order.
     *
     * @param parse Parse object which should be stored
     * @param iter  a counter which limits the recursion level and the result
     */
    private void printParseStr(StringBuilder storage, Parse parse, int iter, int treshold) {
        if (iter > treshold) {
            storage.append("\n reached threshold");
            return;
        }
        storage.append(parse.leader);
        storage.append(parse.tag);

        if (parse.parts != null) {
            printParseStr(storage, parse.parts, ++iter, treshold);
        } else {
            storage.append(parse.body);
        }

        storage.append(parse.end);

        if (parse.more != null) {
            printParseStr(storage, parse.more, ++iter, treshold);
        } else {
            storage.append(parse.trailer);
        }
    }
}