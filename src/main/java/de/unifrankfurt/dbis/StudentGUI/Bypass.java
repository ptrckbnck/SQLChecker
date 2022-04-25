package de.unifrankfurt.dbis.StudentGUI;

import de.unifrankfurt.dbis.IO.FileIO;
import de.unifrankfurt.dbis.IO.SQLCheckerProject;
import de.unifrankfurt.dbis.Inner.Base;
import de.unifrankfurt.dbis.config.GUIConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bypass {
    // This class direcly creates submission-file from user-input bypassing StudentGui

    public Bypass() {

    }


    public static List<String> splitAssignment(String raw) {
        Stream<String> codeStream = Arrays.stream(raw.split("\\/\\*%%\\P{Cc}*%%\\*\\/"));
        return codeStream.skip(1)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public static void createSubmission(String[] author,
                                        Path assignmentPath,
                                        Path template,
                                        String out) throws IOException {
        GUIConfig guiConfig = Bypass.fakeGuiConfig(author);
        Assignment assignment = Assignment.fromSubmission(Base.fromPath(template));
        String raw;
        raw = FileIO.loadString(assignmentPath);
        assignment.setCodes(splitAssignment(raw));

        Path submissionPath;
        if (out == null) {
            submissionPath = template.getParent()
                    .resolve(assignment.getName() + "_" + guiConfig.getMatNr() + ".txt");
        } else {
            submissionPath = Path.of(out);
        }
        Base submission = new SQLCheckerProject(guiConfig, assignment)
                .createSubmission();
        if (submission == null) {
            System.err.println("Creating Submission failed");
        } else {
            submission.storeInPath(submissionPath);
        }
    }

    private static GUIConfig fakeGuiConfig(String[] author) {
        return new GUIConfig(null,
                null,
                null,
                null,
                null,
                null,
                null,
                Objects.requireNonNullElse(author[0], ""),
                Objects.requireNonNullElse(author[1], ""),
                Objects.requireNonNullElse(author[2], ""),
                false,
                null,
                null,
                null
        );
    }


    public static void projectToSubmission(Path projectPath, Path submissionPath) throws IOException {
        SQLCheckerProject project = FileIO.load(projectPath, SQLCheckerProject.class);
        if (project != null) {
            Base submission = project.createSubmission();
            submission.storeInPath(submissionPath);
        } else {
            System.err.println("export failed");

        }
    }

}
