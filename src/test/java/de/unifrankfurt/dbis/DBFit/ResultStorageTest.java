package de.unifrankfurt.dbis.DBFit;

import de.unifrankfurt.dbis.Submission.Student;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultStorageTest {
    String resultRaw = "<table>\n" +
            "\t<tr>\n" +
            "\t\t<td>dbfit.MySqlTest</td>\n" +
            "\t</tr>\n" +
            "</table>\n" +
            "\n" +
            "<table>\n" +
            "\t<tr>\n" +
            "\t\t<td>Connect</td>\n" +
            "\t\t<td>localhost</td>\n" +
            "\t\t<td>airportuser</td>\n" +
            "\t\t<td>airportuser</td>\n" +
            "\t\t<td>airport</td>\n" +
            "\t</tr>\n" +
            "</table>\n" +
            "\n" +
            "<table>\n" +
            "\t<tr>\n" +
            "\t\t<td>Query</td>\n" +
            "\t\t<td>/*1a*/</td>\n" +
            "\t</tr>\n" +
            "\t<tr>\n" +
            "\t\t<td>CountryISO3166_2LetterCode</td>\n" +
            "\t</tr>\n" +
            "\t<tr>\n" +
            "\t\t<td>CA</td>\n" +
            "\t</tr>\n" +
            "\t<tr>\n" +
            "\t\t<td>DE</td>\n" +
            "\t</tr>\n" +
            "\t<tr>\n" +
            "\t\t<td>FR</td>\n" +
            "\t</tr>\n" +
            "\t<tr>\n" +
            "\t\t<td>GB</td>\n" +
            "\t</tr>\n" +
            "\t<tr>\n" +
            "\t\t<td>GR</td>\n" +
            "\t</tr>\n" +
            "\t<tr>\n" +
            "\t\t<td>IT</td>\n" +
            "\t</tr>\n" +
            "\t<tr>\n" +
            "\t\t<td>NO</td>\n" +
            "\t</tr>\n" +
            "\t<tr>\n" +
            "\t\t<td>te</td>\n" +
            "\t</tr>\n" +
            "\t<tr>\n" +
            "\t\t<td>af</td>\n" +
            "\t</tr>\n" +
            "\t<tr>\n" +
            "\t\t<td>st</td>\n" +
            "\t</tr>\n" +
            "</table>";

    @Test
    void generateReadableResult() {
        String readable = ResultStorage.generateReadableResult(resultRaw);
        assertTrue(readable.contains("CountryISO3166_2LetterCode"));
        assertTrue(readable.contains("st"));
    }

    @Test
    void csv() {
        CSVCreator csvc = new CSVCreator().useSubmissionPath()
                .useAuthors()
                .useMatrikelNr()
                .useSolutionName()
                .useAllStatusWithSQLHeaderCheck(List.of("tag"))
                .useSuccess()
                .useEncoding()
                .useErrorMsg();

        ResultStorage rs = new ResultStorage(Paths.get("root"),
                Paths.get("sub"),
                List.of(new Student("name", "mail", "number")),
                "solname",
                1,
                List.of("pass"),
                null,
                StandardCharsets.UTF_8,
                resultRaw,
                List.of(false));
        assertEquals("../sub,name mail number,number,solname,pass but schema diff,1,UTF-8,", rs.csv(csvc));
    }

}