package de.unifrankfurt.dbis.IO;

import de.unifrankfurt.dbis.GUI.Assignment;
import de.unifrankfurt.dbis.config.GUIConfig;
import de.unifrankfurt.dbis.config.GUIConfigBuilder;

import java.io.IOException;
import java.nio.file.Path;

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


    public static SQLCheckerProject newProject(Path templatePath, Path projectPath) throws IOException {
        Assignment template = FileIO.load(templatePath, Assignment.class);
        SQLCheckerProject project = new SQLCheckerProject(new GUIConfigBuilder().createConfig(), template);
        FileIO.save(projectPath, project);
        return project;
    }

}
