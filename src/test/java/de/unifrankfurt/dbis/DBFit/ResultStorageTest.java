package de.unifrankfurt.dbis.DBFit;

import org.junit.jupiter.api.Test;

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
}