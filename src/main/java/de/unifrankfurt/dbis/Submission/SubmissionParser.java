package de.unifrankfurt.dbis.Submission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmissionParser {

    public static List<SubmissionToken> tokenizer(List<String> lines)
            throws SubmissionParseException {
        ArrayList<SubmissionToken> token = new ArrayList<>();
        Tag currentTag = null;
        StringBuilder currentBody = null;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            //if line is Tag -> tag is Tag else tag is null
            Tag tag = Tag.parse(line);
            //check initial Tag
            if (currentTag == null && tag == null)
                throw new SubmissionParseException(SubmissionParseException.TAG_MISSING);
            //Tag is not null -> line is Tag
            if (tag != null) {
                // currentTag can only be null if not tags were found so far.
                if (currentTag == null) {
                    currentTag = tag;
                    currentBody = new StringBuilder();
                } else {
                    token.add(new SubmissionToken(currentTag, currentBody.toString().trim()));
                    currentTag = tag;
                    currentBody = new StringBuilder();
                }
                //No Tag was found
            } else {
                currentBody.append(line).append("\n");
            }
        }
        //add last Token
        if (currentTag != null)
            token.add(new SubmissionToken(currentTag, currentBody.toString()));
        return token;
    }

    /**
     * Parses an author header of serialized Submission.
     *
     * @param string author header of Submission
     * @return List of Student found in author header.
     * @throws SubmissionParseException if a Student could not be parsed
     */
    public static List<Student> parseAuthor(String string) throws SubmissionParseException {
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
    public static Submission<Task> parse(List<String> lines)
            throws SubmissionParseException {
        ArrayList<Task> tasks = new ArrayList<>();
        List<Student> authors = null;
        String name = "no_name_found";
        List<SubmissionToken> tokens = tokenizer(lines);
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
        if (authors == null)
            return new Submission<>(tasks, name);
        else
            return new Submission<>(authors, tasks, name);
    }

    /**
     * creates Task from a SubmissionToken
     * parse() creates first a List of SubmissionToken when parsing a seralized Submission.
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
        throw new SubmissionParseException(SubmissionParseException.UNKNOWN_PLUGIN);
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
