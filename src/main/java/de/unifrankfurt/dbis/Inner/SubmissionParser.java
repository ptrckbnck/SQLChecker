package de.unifrankfurt.dbis.Inner;

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
     * Parses an author header of serialized Inner.
     *
     * @param string author header of Inner
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
     * GUIApp function for parsing a serialized Inner.
     *
     * @param lines List of String which contains the serialized Inner
     * @return Inner created
     */
    public static Submission parseLines(List<String> lines, Charset cs) {
        return parse(String.join("\n", lines), cs);
    }

    public static Submission parse(String toParse, Charset cs) {
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
        Submission sub;
        if (authors == null)
            sub = new Submission(tasks, name, cs);
        else
            sub = new Submission(authors, tasks, name, cs);
        return sub;
    }
    /**
     * creates Task from a SubmissionToken
     * parseLines() creates first a List of SubmissionToken when parsing a seralized Inner.
     * This function turns them into Tasks.
     *
     * @param token SubmissionToken
     * @return a newly created Task
     */
    public static Task fromToken(SubmissionToken token) {
        return Task.parseToken(token);
    }


}
