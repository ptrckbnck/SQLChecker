package de.unifrankfurt.dbis.newStructure;

import de.unifrankfurt.dbis.Submission.Solution;
import de.unifrankfurt.dbis.Submission.Submission;
import de.unifrankfurt.dbis.Submission.SubmissionParseException;
import de.unifrankfurt.dbis.Submission.Tag;
import de.unifrankfurt.dbis.TestResources;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SolutionTest {

    @Test
    public void getterTest() throws IOException, SubmissionParseException {

        TestResources.Simple simple = TestResources.Simple.getInstance();
        Submission submission = simple.getSubmission();
        Solution solution = new Solution(submission,simple.getDbFitHtml());
        assertEquals(submission,solution.getSubmission());
        assertEquals(simple.getDbFitHtml(),solution.getDBFitHtml());

        List<String> actual = submission.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        assertEquals(actual,solution.getDBFitTags());

        assertEquals(submission.getName(),solution.getName());
    }

    @Test
    public void generateSurveyHTML() throws IOException, SubmissionParseException {
        TestResources.Simple simple = TestResources.Simple.getInstance();
        Submission submission = simple.getSubmission();
        Solution solution = new Solution(submission,simple.getDbFitHtml());
        String a = solution.generateSurveyHTML(submission);
        assertTrue(a.contains("<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Execute</td>\n" +
                "\t\t<td>INSERT INTO simple.color (name,red,green,blue) VALUES\n" +
                "('magenta', 255, 0, 255),\n" +
                "('cyan', 0, 255, 255),\n" +
                "('yellow', 255, 255, 0);\n" +
                "</td>\n" +
                "\t</tr>\n" +
                "</table>"));
    }
}