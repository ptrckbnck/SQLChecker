package de.unifrankfurt.dbis;

import dbfit.MySqlTest;
import fit.Parse;
import fit.exception.FitParseException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class DBFITTEST {

    @Test
    public void test() throws SQLException, FitParseException {
        //System.out.println("test");
        assertTrue(true);


        MySqlTest test = new MySqlTest();
        test.connect("localhost", "test", "test", "test");

        String html = "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>dbfit.MySqlTest</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table> <tr> <td>Connect</td> <td>localhost</td> <td>test</td> <td>test</td> <td>test</td> </tr> </table>\n" +
                "\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Execute</td>\n" +
                "\t\t<td>create table test2(test Integer);</td>\n" +
                "\t</tr>\n" +
                "</table>" +
                "\n" +
                "\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Query</td>\n" +
                "\t\t<td>SELECT 1;</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>1</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>1</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Query</td>\n" +
                "\t\t<td>SELECT 1;</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>1</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>1</td>\n" +
                "\t</tr>\n" +
                "</table>\n";

        Parse p = new Parse(html);
        test.doTables(p);
        //System.out.println(new Gson().toJson(test.counts));
        //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(p));
        //System.out.println(getParseResult(p));
    }
        private String getParseResult(Parse parse) {
            StringBuilder storage = new StringBuilder();
            this.printParseStr(storage, parse, 0);
            return storage.toString();
        }


        /**
         * Stores the annotated parseLines String in a class variable. This makes sure that the output is stored in the correct
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

