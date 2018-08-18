package de.unifrankfurt.dbis.Submission;

import dbfit.environment.MySqlProcedureParametersParser;
import dbfit.environment.ParamDescriptor;
import dbfit.util.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  this is a relic from first version. I did not write it. Patrick. TODO Rewrite
 */
public class SQLCallable {


    private String rawsql;
    private String name = "?";

    private CallableType type = CallableType.None;

    private ArrayList<ParamDescriptor> args;

    public SQLCallable(String sql) {
        this.rawsql = sql;
        args = new ArrayList<>();
        init();
    }

    private void init() {
        // store name

        // it is either a function or a procedure
        this.type = TaskSQL.getCallableType(rawsql);

        // add the name of the function/procedure
        String header = parseCallableHeader();
        this.name = header.substring(0, header.indexOf("(")).trim();
        System.out.println("SQLCallable.name=\"" + name + "\"");

        String paramStr = header.substring(header.indexOf("(") + 1, header.length() - 1);
        System.out.println("pExp=\"" + paramStr + "\"");
        MySqlProcedureParametersParser parser = new MySqlProcedureParametersParser();
        List<ParamDescriptor> argsraw = parser.parseParameters(paramStr);
        for (ParamDescriptor pd : argsraw) {
            System.out.println(">" + pd.name);
            System.out.println(">" + pd.type);
            System.out.println(">" + pd.direction);
            System.out.println(">" + pd.toString());
            System.out.println("> - - <");
        }
        // avoid references
        this.args.addAll(argsraw);


        String[] tokens = getHeaderTokens(header);
        // each token, except the first one stores a parameter
        String[] argumentNames = new String[tokens.length - 1];
        for (int i = 1; i < tokens.length; i++) {
            if (type == CallableType.Function) {
                // function
                argumentNames[i - 1] = tokens[i].split(" ")[0];
            } else if (type == CallableType.Procedure) {
                // procedure
                argumentNames[i - 1] = tokens[i].split(" ")[1];
            }
        }

        System.out.println("ARGS=" + Arrays.toString(argumentNames));
    }

    private static String[] getHeaderTokens(String header) {
        //Sample Input: functionName({params comma separated})

        //    System.out.println("INPUT: " + header);

        int counter = 0;
        ArrayList<String> tokens = new ArrayList<>();

        // method name
        String name = header.substring(0, header.indexOf('('));
        //    System.out.println("NAME=\"" + name + "\"");
        tokens.add(name);

        header = header.substring(header.indexOf('(') + 1, header.lastIndexOf(')'));
        //    System.out.println("INPUT-2: \"" + header + "\"");

        for (int i = 0; i < header.length(); i++) {
            // charAt is a constant time operation!
            char c = header.charAt(i);
            // check the character
            if (c == '(') {
                counter++;
            } else if (c == ')') {
                counter--;
            } else if (c == ',') {
                // new token!
                if (counter == 0) {
                    tokens.add("");
                }
            }

            // if ( ((c == ',') && (counter != 0)) || (c != ","))
            // if ( (c != ',') || (counter != 0) )
            if (!((c == ',') && (counter == 0))) {
                // if the list only stores a name
                if (tokens.size() < 2) tokens.add("");
                // some other char, add it to the top of the list
                String t = tokens.get(tokens.size() - 1);
                t += c;
                tokens.set(tokens.size() - 1, t);
            }
        }
        //    System.out.println("TOKENS=" + tokens.size());
        String[] tokenArray = (tokens.toArray(new String[tokens.size()]));
        for (int i = 0; i < tokens.size(); i++)
            tokenArray[i] = tokenArray[i].trim();

        return tokenArray;
    }

    private String parseCallableHeader() {

        // see https://dev.mysql.com/doc/refman/5.0/en/create-procedure.html

        //     System.out.println("INPUT: \n" + sql);

        String sqlNew = rawsql.toLowerCase();

        boolean isFunction = (this.type == CallableType.Function); // todo tesst equal
        /*
        // this is a safer isFunction check!
        String sqlHead = sql.substring(0, sql.indexOf(")"));
        String[] sqlHeadTokens = sqlHead.split(" ");
        boolean isFunction = sqlHeadTokens[sqlHeadTokens.length - 2].equals("function");
        */

        // boolean isFunction = sql.contains(" function ");
        //   System.out.println("isFunction=" + isFunction);

        int headerStartIdx;
        int headerEndIdx;
        if (isFunction) {
            headerStartIdx = sqlNew.indexOf(" function ") + " function ".length();
            // i.e. RETURNS int(8)
            // returns is also part of a correct mysql function definition
            headerEndIdx = sqlNew.indexOf("returns ");
        } else {
            headerStartIdx = sqlNew.indexOf(" procedure ") + " procedure ".length();
            // check for enclosing brackets!
            headerEndIdx = sqlNew.indexOf("(") + 1;
            int count = 1;
            // System.out.println("[ " + headerEndIdx + " >" + sql.substring(0, headerEndIdx));
            while (count != 0) {
                if (headerEndIdx == rawsql.length()) {
                    // System.out.println("Reached EndOfLine, possibly malformed query!");
                    break;
                }
                if (sqlNew.charAt(headerEndIdx) == ')') count--;
                if (sqlNew.charAt(headerEndIdx) == '(') count++;
                headerEndIdx++;
            }
            // System.out.println("[ " + headerEndIdx + " >" + sqlNew.substring(0, headerEndIdx));

        }
        //   System.out.println("s=" + headerStartIdx + ", e=" + headerEndIdx);
        String header = rawsql.substring(headerStartIdx, headerEndIdx);

        header = header.replace("\n", "").trim();
        //   System.out.println("Header(1)=\"" + header + "\"");
        return header;
    }

    public String getName() {
        return this.name;
    }

    public String getStatement() {
        return this.rawsql;
    }

    public boolean isOutOrInout() {
        for (ParamDescriptor arg : args) {
            if (arg.direction.isOutOrInout())
                return true;
        }
        return false;
    }

    public CallableType getType() {
        return this.type;
    }

    public boolean isFunction() {
        return (this.type == CallableType.Function);
    }

    public boolean isProcedure() {
        return (this.type == CallableType.Procedure);
    }

    /**
     * @return True if one of the arguments has the direction
     * OUT or INOUT
     */
    public boolean hasOutParameter() {
        for (ParamDescriptor arg : args) {
            if (arg.direction.isOutOrInout()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Generates a result header for the arguments of this
     * callable object
     *
     * @return A string which contains all arguments (in order)
     * INOUT arguments occur twice: Once as an IN argument and
     * once as an OUT argument. The last element stores the function
     * output and is named "@"
     */
    public String[] generateResultHeader() {
        ArrayList<String> cols = new ArrayList<>();

        for (ParamDescriptor pd : args) {
            // check for the type of this argument
            if (pd.direction == Direction.INPUT) {
                cols.add(pd.name);
            } else if (pd.direction == Direction.OUTPUT) {
                cols.add("@" + pd.name);
            } else if (pd.direction == Direction.INPUT_OUTPUT) {
                cols.add(pd.name);
                cols.add("@" + pd.name);
            }
        }

        if (isFunction()) {
            cols.add("@");
        }

        return (cols.toArray(new String[cols.size()]));
    }


    /**
     * @return The parameters defined in the definition of callable
     */
    public ArrayList<ParamDescriptor> getParameters() {
        // avoid references

        return new ArrayList<>(args);
    }


    /**
     * Parses the data given by the call of this function or stored
     * procedure
     *
     * @param call The call itself, this is the name, followed
     *             by the arguments in brackets. Variables (for OUT parameters)
     *             start with "@". Examples: <br>
     *             CalcLength("HelloWorld", @strlength), <br>
     *             PlusEins(15) <- The one argument of this function
     *             has the type INOUT <br>
     *             SumAB(5, 4) <- Two IN parameters, stored function which returns 9
     * @return The data/arguments defined in the given call. Examples: <br>
     * CalcLength("HelloWorld", @strlength) becomes ["HelloWorld", @strlength] <br>
     * SumAB(5, 4) becomes [5, 4]
     */
    public static String[] parseCallData(String call) {
        ArrayList<String> data = new ArrayList<>();
        data.add("");

        // check for empty argument list
        if (call.indexOf(")") == call.indexOf("(") + 1)
            return (new String[0]);

        // X(a,b) => a,b
        call = call.substring(call.indexOf("(") + 1, call.length() - 1);
        //System.out.println("stripped=\"" + call + "\"");

        char lastSeen = '?'; // ' or ""
        int counter = 0;
        for (int i = 0; i < call.length(); i++) {
            char current = call.charAt(i);
            if (current == ',') {
                if (counter == 0) {
                    // Separator detected!
                    // Trim previous element
                    String tmp = data.get(data.size() - 1);
                    tmp = tmp.trim();
                    data.set(data.size() - 1, tmp);
                    // new element
                    data.add("");
                } else {
                    // the , is surrounded by " or '
                    String tmp = data.get(data.size() - 1);
                    tmp += ",";
                    data.set(data.size() - 1, tmp);
                }
            } else {
                // simply append the current char
                String tmp = data.get(data.size() - 1);
                tmp += current;
                data.set(data.size() - 1, tmp);
                // update the counter
                if (counter != 0) {
                    if (current == lastSeen) {
                        // closing quotation
                        counter = 0;
                    }
                } else {
                    if ((current == '\"') || (current == '\''))
                        // beginning quotation
                        counter = 1;
                    lastSeen = current;
                }
            }
        }
        // trim last element
        String tmp = data.get(data.size() - 1);
        tmp = tmp.trim();
        data.set(data.size() - 1, tmp);

        return (data.toArray(new String[data.size()]));
    }


    /**
     * Generates all the required SQL SET statements for the INOUT
     * parameters in this call. This function also replaces the
     * INOUT data arguments with their appropriate variables. These
     * are the variables which are used in the previously generated
     * SQL SET statements.
     *
     * @param call The call which should be prepared for execution
     * @return A list with all the preparing SQL statements.
     * The 2nd last element in the list is the call itself with
     * correct variable names. The last element is a SELECT
     * statement which queries the value of the INOUT and OUT
     * variables which were used in the call
     */
    public ArrayList<String> prepareInOutCall(String call) {
        // contains SQL set commands for appropriate arguments
        // ASSUMPTION!! DATA.length == args.length !!!!
        ArrayList<String> sqlsets = new ArrayList<>();
        String[] data = SQLCallable.parseCallData(call);
        String newCall = this.name + "(";
        String sqlGetter = "SELECT ";
        int outputColumns = 0;

        for (int i = 0; i < args.size(); i++) {
            // for every argument
            ParamDescriptor pd = args.get(i);
            // Check if it an INOUT argument
            // Only those require to set some variables
            // before performing the call
            // Also generatePairs a new call, which contains placeholders
            // for all INOUT arguments
            String colId;
            String tmpSQL = "";
            if (pd.direction == Direction.INPUT_OUTPUT) {
                String sql = "SET @" + pd.name + " = " + data[i];
                sqlsets.add(sql);
                // replace current data value with variable name
                colId = "@" + pd.name;
                data[i] = colId;
            } else {
                colId = data[i];
            }
            // Deal with separators
            if (i > 0) {
                colId = ", " + colId;
            }
            if (outputColumns > 0) {
                tmpSQL = ", ";
            }
            // Check if the algorithm has encountered a variable
            // Variable could either be just added (INOUT)
            // or been there before (OUT). Variables always start
            // with @
            if (data[i].charAt(0) == '@') {
                tmpSQL += data[i];
                // append the just created part to the
                // SELECT statement
                sqlGetter += tmpSQL;
                outputColumns++;
            }
            // Apply variable replacement in call
            newCall += colId;
        }

        // The 2nd last element is the call itself
        sqlsets.add("{ call " + newCall + ") }");

        // The last element is a select and produces the final
        // result set
        sqlsets.add(sqlGetter);

        // if there are no inout arguments, then the list will be empty
        return sqlsets;
    }

}
