package de.unifrankfurt.dbis.Submission;


import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Template {
    private Submission<Task> submission;
    private NewScript resetScript;
    public static final String TEMPLATE_NAME = "template.sql";
    public static final String RESET_SCRIPT_NAME = "reset.sql";


    Template(Submission<Task> submission, NewScript resetScript) {
        this.submission = submission;
        this.resetScript = resetScript;
    }


    public static Template fromPath(Path path) throws IOException, SubmissionParseException {
        FileSystem system = FileSystems.newFileSystem(path, null);
        List<Path> rootDirs = new ArrayList<>();
        system.getRootDirectories().forEach(rootDirs::add);
        if (rootDirs.isEmpty()) throw new IOException("File contains no Files");
        Path dir = rootDirs.get(0); // template zip should not have more than one root dir.

        List<Path> submissionPath = new ArrayList<>();
        List<Path> resetScriptPath = new ArrayList<>();
        Files.walk(dir)
                .filter(x -> !Files.isDirectory(x))
                .takeWhile(x -> true)
                .forEach(x -> {
                    if (x.getFileName().toString().equalsIgnoreCase(Template.RESET_SCRIPT_NAME)) {
                        resetScriptPath.add(x);
                    } else if (x.getFileName().toString().equalsIgnoreCase(Template.TEMPLATE_NAME)) {
                        submissionPath.add(x);
                    }
                });
        if (submissionPath.size() != 1 && resetScriptPath.size() != 1)
            throw new IOException("Container contains no valid Template");

        Submission<Task> submission = Submission.fromPath(submissionPath.get(0));
        NewScript reset = NewScript.fromPath(resetScriptPath.get(0));
        return new Template(submission, reset);
    }

    public void writeFilesWorkingDirectory(Path path) throws IOException {
        String cleanedSubmissionName = this.submission.getName().trim();
        Path exercisePath = path.resolve(cleanedSubmissionName);
        Path submissionPath = exercisePath.resolve(Template.TEMPLATE_NAME);
        Path resetScriptPath = exercisePath.resolve(Template.RESET_SCRIPT_NAME);

        Files.createDirectories(exercisePath);

        this.submission.storeInPath(submissionPath);
        this.resetScript.storeInPath(resetScriptPath);

    }

    public NewScript getResetScript() {
        return resetScript;
    }

    public Submission<Task> getSubmission() {
        return submission;
    }
}
