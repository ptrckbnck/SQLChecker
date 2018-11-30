package de.unifrankfurt.dbis.GUI;

import org.apache.commons.lang3.ArrayUtils;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * this class provides method computeHighlighting. The main method for highlighting SQL-Code in RichTextArea.
 */
public class SQLHighlighter {

    private static final String[] KEYWORDS = new String[]{
            "ABORT", "ACTION", "ADD", "AFTER", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ATTACH", "AUTOINCREMENT",
            "BEFORE", "BEGIN", "BETWEEN", "BY", "CASCADE", "CASE", "CAST", "CHECK", "COLLATE", "COLUMN", "COMMIT",
            "CONFLICT", "CONSTRAINT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
            "DATABASE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DETACH", "DISTINCT", "DROP", "EACH",
            "ELSE", "END", "ESCAPE", "EXCEPT", "EXCLUSIVE", "EXISTS", "EXPLAIN", "FAIL", "FOR", "FOREIGN", "FROM",
            "FULL", "GLOB", "GROUP", "HAVING", "IF", "IGNORE", "IMMEDIATE", "IN", "INDEX", "INDEXED", "INITIALLY",
            "INNER", "INSERT", "INSTEAD", "INTERSECT", "INTO", "IS", "ISNULL", "JOIN", "KEY", "LEFT", "LIKE", "LIMIT",
            "MATCH", "NATURAL", "NO", "NOT", "NOTNULL", "NULL", "OF", "OFFSET", "ON", "OR", "ORDER", "OUTER", "PLAN",
            "PRAGMA", "PRIMARY", "QUERY", "RAISE", "RECURSIVE", "REFERENCES", "REGEXP", "REINDEX", "RELEASE", "RENAME",
            "REPLACE", "RESTRICT", "RIGHT", "ROLLBACK", "ROW", "SAVEPOINT", "SELECT", "SET", "TABLE", "TEMP",
            "TEMPORARY", "THEN", "TO", "TRANSACTION", "TRIGGER", "UNION", "UNIQUE", "UPDATE", "USING", "VACUUM",
            "VALUES", "VIEW", "VIRTUAL", "WHEN", "WHERE", "WITH", "WITHOUT",
    };

    private static final String[] FUNCTIONS = new String[]{
            "ABS", "ACOS", "ADDDATE", "ASCII", "ASIN", "ATAN", "AVG", "BIN", "BINARY", "CASE", "CAST", "CEIL",
            "CEILING", "COALESCE", "CONCAT", "CONCAT_WS", "CONV", "CONVERT", "COS", "COT", "COUNT", "CURRENT_DATE",
            "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DATE", "DATE_ADD", "DATE_FORMAT", "DATE_SUB",
            "DATEDIFF", "DAY", "DEGREES", "ENCRYPT", "EXP", "EXTRACT", "FLOOR", "FORMAT", "FROM_DAYS", "GREATEST",
            "HOUR", "IF", "IFNULL", "INSERT", "INSTR", "ISNULL", "LAST_DAY", "LCASE", "LEAST", "LEFT", "LENGTH", "LN",
            "LOCALTIME", "LOCALTIMESTAMP", "LOG", "LOG10", "LOWER", "LPAD", "LTRIM", "MAX", "MID", "MIN", "MINUTE",
            "MOD", "MONTH", "NULLIF", "PI", "POW", "POWER", "RADIANS", "RAND", "REPEAT", "REPLACE", "REVERSE", "RIGHT",
            "ROUND", "RPAD", "RTRIM", "SECOND", "SESSION_USER", "SIGN", "SIN", "SPACE", "SQRT", "STRCMP", "SUBDATE",
            "SUBSTR", "SUBSTRING", "SUBSTRING_INDEX", "SUM", "SYSDATE", "SYSTEM_USER", "TAN", "TIME", "TIMESTAMP",
            "TO_DAYS", "TRIM", "TRUNCATE", "UCASE", "UPPER", "USER", "YEAR",
    };

    private static final String[] ALL_KEYWORDS = ArrayUtils.addAll(KEYWORDS, FUNCTIONS);

    private static final String KEYWORD_PATTERN = "\\b(?i)(" + String.join("|", ALL_KEYWORDS) + ")\\b";
    private static final String SEMICOLON_PATTERN = ";";
    private static final String STRING_PATTERN = "'([^'\\\\]|\\\\.)*'";
    private static final String NUMERIC_LITERAL_PATTERN = "\\b([0-9]+(\\.[0-9])*)\\b";
    private static final String COMMENT_PATTERN = "((--|#).*$)|(/\\*(.|\n)*\\*/)";


    private static final Pattern PATTERN = Pattern.compile(
            "(?<COMMENT>" + COMMENT_PATTERN + ")" +
                    "|(?<STRING>" + STRING_PATTERN + ")" +
                    "|(?<KEYWORD>" + KEYWORD_PATTERN + ")" +
                    "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")" +
                    "|(?<NUMERIC>" + NUMERIC_LITERAL_PATTERN + ")"

            , Pattern.MULTILINE);


    /**
     * creates SQL highlighted Text for RichTextArea.
     *
     * @param text SQL-Code
     * @return StyleSpans<Collection       <       String>>
     */
    public static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("COMMENT") != null ? "comment" :
                            matcher.group("STRING") != null ? "string" :
                                    matcher.group("KEYWORD") != null ? "keyword" :
                                            matcher.group("SEMICOLON") != null ? "semicolon" :
                                                    matcher.group("NUMERIC") != null ? "numeric" :
                                                            null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
