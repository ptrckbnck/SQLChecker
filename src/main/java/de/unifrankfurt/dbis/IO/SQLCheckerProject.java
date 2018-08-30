package de.unifrankfurt.dbis.IO;

import de.unifrankfurt.dbis.GUI.Assignment;
import de.unifrankfurt.dbis.Submission.*;
import de.unifrankfurt.dbis.config.GUIConfig;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SQLCheckerProject {
    private final GUIConfig GUIConfig;
    private final Assignment assignment;

    public SQLCheckerProject(GUIConfig GUIConfig, Assignment assignment) {
        this.GUIConfig = GUIConfig;
        this.assignment = assignment;
    }

    public GUIConfig getGUIConfig() {
        return GUIConfig;
    }

    public Assignment getAssignment() {
        return assignment;
    }


    private List<Student> studentList() {
        if (this.GUIConfig.isPartnerOk())
            return List.of(
                    new Student(
                            this.GUIConfig.getNameStudent(),
                            this.GUIConfig.getEmail(),
                            this.GUIConfig.getMatNr()),
                    new Student(
                            this.GUIConfig.getPartnerName(),
                            this.GUIConfig.getPartnerEmail(),
                            this.GUIConfig.getPartnerMatNr())
            );
        else return List.of(
                new Student(
                        this.GUIConfig.getNameStudent(),
                        this.GUIConfig.getEmail(),
                        this.GUIConfig.getMatNr()));
    }

    public Submission<TaskSQL> createSubmission() {
        List<TaskSQL> tasks = assignment.getCodeMap()
                .entrySet()
                .stream()
                .map((entry) -> {
                    try {
                        return TaskSQL.parseToken(
                                new SubmissionToken(
                                        new Tag(entry.getKey()),
                                        entry.getValue()
                                ));
                    } catch (SubmissionParseException e) {
                        System.err.println("Erzeugen der Abgabe Fehlgeschlagen: " + e.getMessage());
                        return null;
                    }
                }).collect(Collectors.toList());
        return new Submission<>(studentList(), tasks, assignment.getName());
    }

    public void olatZip(Path path) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        Files.deleteIfExists(path);
        URI uri = URI.create("jar:file:" + path);

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env, null)) {
            FileIO.saveText(zipfs.getPath(
                    "/" + this.assignment.getName() + "_" + this.GUIConfig.getMatNr() + ".txt"),
                    this.createSubmission().serialize());

        }

    }
}
