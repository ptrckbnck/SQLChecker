package de.unifrankfurt.dbis;

import de.unifrankfurt.dbis.IO.FileIO;

import java.io.IOException;
import java.nio.file.Path;

public class SQLCheckerProject {
    private final Config config;
    private final Assignment assignment;

    public SQLCheckerProject(Config config, Assignment assignment) {
        this.config = config;
        this.assignment = assignment;
    }

    public Config getConfig() {
        return config;
    }

    public Assignment getAssignment() {
        return assignment;
    }


    public static SQLCheckerProject newProject(Path templatePath, Path projectPath) throws IOException {
        Assignment template = FileIO.load(templatePath, Assignment.class);
        SQLCheckerProject project = new SQLCheckerProject(new ConfigBuilder().createConfig(), template);
        FileIO.save(projectPath,project);
        return project;
    }

}
