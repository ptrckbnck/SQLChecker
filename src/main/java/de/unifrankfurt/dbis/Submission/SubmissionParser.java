package de.unifrankfurt.dbis.Submission;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubmissionParser {

    public static List<SubmissionToken> tokenizer(String toParse) {

        String raw = toParse.trim();
        String[] splited = raw.split("(?=/\\*)");
        List<SubmissionTokenBuilder> tasks = new ArrayList<>();
        for (String s : splited) {
            System.err.println(s);
            String[] task = s.trim().split("\n", 2);
            String possibleTag = task[0].trim();
            if (!possibleTag.endsWith("*/") || possibleTag.matches(".*\\s+.*"))//contains whitespace
            {
                //no tag
                if (tasks.isEmpty()) {
                    if (!possibleTag.isEmpty()) {
                        System.out.println("Ignored Token:" + possibleTag + "\n");
                    }
                } else {
                    tasks.get(tasks.size() - 1).addToBody(s);
                }
            } else {
                //tag
                SubmissionTokenBuilder builder = new SubmissionTokenBuilder().setTag(Tag.parse(possibleTag));
                if (task.length == 2) {
                    builder.addToBody(task[1].trim());
                }
                tasks.add(builder);
            }
        }
        return tasks.stream()
                .map(SubmissionTokenBuilder::createSubmissionToken)
                .collect(Collectors.toList());
    }



    /**
     * Parses an author header of serialized Submission.
     *
     * @param string author header of Submission
     * @return List of Student found in author header.
     */
    public static List<Student> parseAuthor(String string) {
        String[] authors = string.split("\n");
        ArrayList<Student> authorList = new ArrayList<>();
        for (String author : authors) {
            String[] parts = author.split(";");
            if (parts.length >= 3) {
                authorList.add(new Student(parts[0], parts[1], parts[2]));
            } else {
                return null; //BAD_AUTHOR
            }
        }
        return authorList;
    }


    /**
     * GUIApp function for parsing a serialized Submission.
     *
     * @param lines List of String which contains the serialized Submission
     * @return Submission created
     * @throws SubmissionParseException when something went wring while parsing.
     */
    public static Submission<Task> parseLines(List<String> lines, Charset cs)
            throws SubmissionParseException {
        return parse(String.join("\n", lines), cs);
    }

    public static Submission<Task> parse(String toParse, Charset cs) throws SubmissionParseException {
        ArrayList<Task> tasks = new ArrayList<>();
        List<Student> authors = null;
        String name = "no_name_found";
        List<SubmissionToken> tokens = tokenizer(toParse);
        Map<String, SubmissionToken> map = new HashMap<>();
        for (SubmissionToken token : tokens) {
            map.put(token.getTag().getName(), token);
        }
        //check if Author given
        if (map.get(Tag.AUTHORTAG) != null) {
            authors = parseAuthor(map.get(Tag.AUTHORTAG).getBody());
            tokens.remove(map.get(Tag.AUTHORTAG));
        }

        //check if name given
        if (map.get(Tag.NAMETAG) != null) {
            name = map.get(Tag.NAMETAG).getBody();
            tokens.remove(map.get(Tag.NAMETAG));
        }
        //create Tasks from token
        for (SubmissionToken token : tokens) {
            tasks.add(fromToken(token));
        }
        Submission<Task> sub;
        if (authors == null)
            sub = new Submission<>(tasks, name);
        else
            sub = new Submission<>(authors, tasks, name);
        sub.setCharset(cs);
        return sub;
    }
    /**
     * creates Task from a SubmissionToken
     * parseLines() creates first a List of SubmissionToken when parsing a seralized Submission.
     * This function turns them into Tasks.
     *
     * @param token SubmissionToken
     * @return a newly created Task
     * @throws SubmissionParseException if parsing went wrong
     */
    public static Task fromToken(SubmissionToken token) throws SubmissionParseException {
        if (token.getTag().getPlugin() == null) {
            return TaskSQL.parseToken(token);
        }
        throw new SubmissionParseException(SubmissionParseException.ErrorType.UNKNOWN_PLUGIN);
    }

    public static TaskBody splitBody(String body) {
        StringBuilder comment = new StringBuilder();
        StringBuilder sql = new StringBuilder();
        int togglePos = 0;
        int length = body.length();
        for (int i = 0; i < length; i++) {
            char c = body.charAt(i);

            if (c == '\'') {
                i = skipToToken(body, i, '\'');
            } else if (c == '\"') {
                i = skipToToken(body, i, '\"');
            } else if (c == '`') {
                i = body.indexOf('`', i);
            } else if (c == '#' && length >= i + 1 && body.charAt(i + 1) == ' ') {
                if (i > 0) {
                    sql.append(body.substring(togglePos, i - 1)).append("\n");
                }
                togglePos = body.indexOf("\n", i);
                comment.append(body.substring(i + 2, togglePos++)).append("\n");
                i = togglePos;
            } else if (c == '-'
                    && length > i + 2
                    && body.charAt(i + 1) == '-'
                    && isControlCharacter(body.charAt(i + 2))) { // index test
                if (i > togglePos) {
                    sql.append(body.substring(togglePos, i - 1)).append("\n");
                }

                togglePos = indexOf(body, "\n", i);
                comment.append(substring(body, i + 3, togglePos)).append("\n");
                i = togglePos++;
            } else if (c == '/' && length >= i + 2 && body.charAt(i + 1) == '*' && body.charAt(i + 2) == ' ') {
                if (i > togglePos) {
                    sql.append(body.substring(togglePos, i - 1)).append("\n");
                }
                togglePos = indexOf(body, "*/", i);
                comment.append(body.substring(i + 3, togglePos++)).append("\n");
                i = togglePos++;
            }
        }
        if (togglePos <= length) {
            sql.append(body.substring(togglePos, length));
        }
        return new TaskBody(
                comment.toString().trim(),
                sql.toString().trim());
    }


    private static int skipToToken(String query, int start, char token) {
        boolean esc = false;
        int length = query.length();
        for (int i = start + 1; i < length; i++) {
            char c = query.charAt(i);
            if (c == token && !esc) {
                return i;
            }
            esc = c == '\\';
        }
        return length;
    }


    private static int indexOf(String query, String str, int start) {
        int idx = query.indexOf(str, start);
        idx = idx >= 0 ? idx : query.length();
        return idx;
    }


    private static boolean isControlCharacter(char c) {
        return (c == ' ' || c == '\n' || c == '\t');
    }


    private static String substring(String query, int start, int end) {
        if (start <= end) {
            return query.substring(start, end);
        } else {
            return "";
        }
    }


}
