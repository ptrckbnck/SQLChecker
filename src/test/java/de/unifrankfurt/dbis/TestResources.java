package de.unifrankfurt.dbis;


import de.unifrankfurt.dbis.Submission.*;
import de.unifrankfurt.dbis.config.XConfig;
import de.unifrankfurt.dbis.config.XConfigBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestResources {


    public static class Simple{
        private static Simple instance = new Simple();
        private XConfig config;
        private Path configPath;
        private Submission<TaskSQL> submission;
        private Path submissionPath;
        private Solution solution;
        private SQLScript reset;
        private Path resetPath;
        private Path dbFitHtmlPath;
        private String dbFitHtml;

        private Simple(){
            this.config = null;
            this.configPath = null;
            this.submission = null;
            this.submissionPath = resourcePath("/test/simple/raw_simple.txt");
            this.solution = null;
            this.reset = null;
            this.resetPath = resourcePath("/test/simple/reset_simple.txt");
            this.dbFitHtmlPath = resourcePath("/test/simple/dbFitHtml.txt");
            this.dbFitHtml = null;
        }

        public static Simple getInstance(){
            return instance;
        }

        public XConfig getConfig() throws IOException {
            if (this.config == null)
                this.config = createSimpleConfig() ;
            return config;
        }

        public Path getConfigPath() throws IOException {
            if (this.configPath == null) {
                this.configPath = Files.createTempFile("XConfig", "");
                this.getConfig().storeInPath(this.configPath);
            }
            return configPath;
        }

        public Submission<TaskSQL> getSubmission() throws IOException, SubmissionParseException {
            if (submission == null) {
                submission = Submission.fromPath(submissionPath).onlyTaskSQLSubmission();
            }
            return submission;
        }

        public Path getSubmisionPath(){
            return resourcePath("/test/simple/raw_simple.txt");
        }

        public Solution getSolution() throws IOException, SubmissionParseException {
            if (solution == null){
                Path dbFitHtml = resourcePath("/test/simple/dbFitHtml.txt");
                Stream<String> lines = Files.lines(dbFitHtml, StandardCharsets.UTF_8);

                solution = new Solution(
                        getSubmission(),
                        lines.collect(Collectors.joining("/n")));
            }
            return solution;
        }

        public SQLScript getReset() throws IOException {
            if (reset == null)
                reset = SQLScript.fromPath(getResetPath());
            return reset;
        }

        public Path getResetPath(){
            return resetPath;
        }

        public Path getDbFitHtmlPath() {
            return dbFitHtmlPath;
        }

        public String getDbFitHtml() throws IOException {
            if (dbFitHtml == null)
                dbFitHtml = Files.lines(dbFitHtmlPath)
                        .collect(Collectors.joining("\n"));
            return dbFitHtml;
        }

        public void runReset() throws SQLException,IOException {
            Connection connection = this.getConfig().newConnection();
            assert connection!=null;
            try {
                this.getReset().execute(connection.createStatement());
            }catch (SQLException e){
                throw new SQLException("reset database failed",e);
            } finally{
                connection.close();
            }

        }
    }

    private static Path resourcePath(String path){
        try {
            URI p = TestResources.class.getResource(path).toURI();
            return Paths.get(p);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static XConfig createSimpleConfig() throws IOException {
        File simpleConfigExecutorResult = Files.createTempDirectory("result").toFile();
        return new XConfigBuilder()
                .setDatabase("simple")
                .setHostname("localhost")
                .setPort("3306")
                .setUsername("test")
                .setPassword("test")
                .setSavePassword(true)
                .setResetPath(resourcePath("/test/simple/reset_simple.txt").toAbsolutePath().toString())
                .setSolutionInPath(resourcePath("/test/simple/raw_simple.txt").toAbsolutePath().toString())
                .setExecutorSolutionPath(resourcePath("/test/simple/dbFitHtml.txt").toAbsolutePath().toString())
                .setExecutorStaticEnabled(false)
                .setExecutorResultPath(simpleConfigExecutorResult.getAbsolutePath())
                .createConfig();
    }


    public static XConfig getSampleConfig(){
        return new XConfigBuilder()
                .setHostname("192.168.1.1")
                .setPort("1234")
                .setUsername("username")
                .setPassword("password")
                .setSavePassword(true)
                .setDatabase("database")
                .setExecutable("exe")
                .setResetPath("reset.sql")
                .setSolutionInPath("raw.sql")
                .setSolutionOutPath("solution.txt")
                .setSolutionSamplePath("sample.sql")
                .setExecutorSubmissionPath("/subs/")
                .setExecutorSolutionPath("solution.txt")
                .setExecutorResultPath("/result/")
                .setExecutorStaticEnabled(true)
                .createConfig();
    }

    public static Path getSampleConfigPath() {
        return resourcePath("/test/config/sampleConfig.txt");
    }

    public static String getSampleConfigString() {
        return "#SQLChecker Configuration File (version: 1.0)\n" +
                "\n" +
                "##### MySQL database Settings #####\n" +
                "#Server name or ip-address (default: localhost)\n" +
                "db.host=192.168.1.1\n" +
                "#Server port (default: 3306)\n" +
                "db.port=1234\n" +
                "#username for authentication (default: root)\n" +
                "db.username=username\n" +
                "#password for authentication\n" +
                "db.password=password\n" +
                "#save password (default: false)\n" +
                "db.savepassword=true\n" +
                "#database name\n" +
                "db.databasename=database\n" +
                "#path to mysql executables\n" +
                "db.executable=exe\n" +
                "\n" +
                "#### Survey Settings #####\n" +
                "#path of the reset script that should be used\n" +
                "db.resetscript=reset.sql\n" +
                "\n" +
                "##### DBFitSolution generator settings #####\n" +
                "#The raw solution file, from which the dbfit solution file should be created\n" +
                "test.infile=raw.sql\n" +
                "#The path at which the output solution file should reside (default: /solution.txt)\n" +
                "test.outfile=solution.txt\n" +
                "#The path at which the sample submission file should reside\n" +
                "# This file shows, how a correct submission might look (default: /submissions/sample.sql)\n" +
                "test.samplefile=sample.sql\n" +
                "\n" +
                "##### Submission executor settings #####\n" +
                "#Folder of the submissions which should be checked (default: /submissions/)\n" +
                "chk.submissions=/subs/\n" +
                "#The path of the dbfit solution file, which should be checked (default: /solution.txt)\n" +
                "chk.solution=solution.txt\n" +
                "#Output path of the log- and result-files (default: /out/)\n" +
                "chk.outpath=/result/\n" +
                "#True, if static queries should be allowed in Student submissions\n" +
                "#These queries do not belong to a specific placeholder in the solution file. They get\n" +
                "executed independently (default: false)\n" +
                "chk.allowstatic=true\n";
    }

    public static class ConfigData {
        private final Path path;
        private final XConfig XConfig;

        public ConfigData(Path path, XConfig XConfig){
            this.path = path;
            this.XConfig = XConfig;
        }

        public Path getPath() {
            return path;
        }

        public XConfig getXConfig() {
            return XConfig;
        }
    }

    @Deprecated
    public static DBFitSubmissionData getSimple(){
        Path path = resourcePath("/test/simple/raw_simple.txt");
        Submission<TaskSQL> sub = simpleSubmission();
        return new DBFitSubmissionData(path,sub,"");
    }

    @Deprecated
    private static Submission<TaskSQL> simpleSubmission(){
        List< TaskSQL > taskList = new ArrayList<>();
        taskList.add(new TaskSQLNonCallable(new Tag("1a"),
                "",
                "INSERT INTO simple.color (name,red,green,blue) VALUES\n" +
                "('magenta', 255, 0, 255),\n" +
                "('cyan', 0, 255, 255),\n" +
                "('yellow', 255, 255, 0);"));
        taskList.add(new TaskSQLNonCallable(new Tag("1b"),
                "",
                "CREATE TABLE `simple`.`animal` (\n" +
                        "`name` VARCHAR(45) NOT NULL,\n" +
                        "`genus` VARCHAR(45) NOT NULL,\n" +
                        "`species` VARCHAR(45) NOT NULL,\n" +
                        "PRIMARY KEY (`name`)\n" +
                        ");\n"));
        taskList.add(new TaskSQLNonCallable(new Tag("1c"),
                "",
                "ALTER TABLE `simple`.`animal`\n" +
                "ADD COLUMN `family` VARCHAR(45) NOT NULL AFTER `name`;\n"));
        taskList.add(new TaskSQLNonCallable(new Tag("1d"),
                "",
                "INSERT INTO `simple`.`animal` (`name`, `family`, `genus`, `species`) VALUES\n" +
                "('Tiger', 'Felidae', 'Panthera', 'P. tigris');\n"));
        taskList.add(new TaskSQLNonCallable(new Tag("2a"),
                "",
                "/*Welche Farben beinhalten rot?*/\n" +
                "select c.name from color c where c.red > 0;\n"));
        taskList.add(new TaskSQLNonCallable(new Tag("2b"),
                "",
                "/*alle tiere*/\n" +
                "select a.name from animal a;\n"));
        return new Submission<TaskSQL>(taskList, "submission");
    }

    public static class DBFitSubmissionData {
        private final Path path;
        private final Submission<TaskSQL> submission;
        private final String submissionString;

        public DBFitSubmissionData(Path path,
                                   Submission<TaskSQL> submission,
                                   String submissionString) {
            this.path = path;
            this.submission = submission;
            this.submissionString = submissionString;
        }

        public Path getPath() {
            return path;
        }

        public Submission<TaskSQL> getSubmission() {
            return submission;
        }

        public String getSubmissionString() {
            return submissionString;
        }

        public Solution getSolution() {
            return null;
        }
    }
    public static DBFitSubmissionData getSubmissionNoAuthor(){
        Path path = resourcePath("/test/submission/noAuthor.txt");
        List< TaskSQL > taskList = new ArrayList<>();
        taskList.add(
                new TaskSQLNonCallable(
                        new Tag("tag"),
                        "this is comment",
                        "Select * from * ;"));
        taskList.add(
                new TaskSQLNonCallable(
                        new Tag("tag2"),
                        "this is comment 2",
                        "SELECT USER(), DATABASE() ;"));
        Submission<TaskSQL> sub = new Submission<>(taskList, "submission");
        String submissionString = "/*tag*/\n" +
                "/* this is comment */\n" +
                "Select * from * ;\n" +
                "\n" +
                "/*tag2*/\n" +
                "/* this is comment 2 */\n" +
                "SELECT USER(), DATABASE() ;";
        return new DBFitSubmissionData(path,sub,submissionString);
    }

    public static DBFitSubmissionData getSubmissionWAuthor(){
        Path path = resourcePath("/test/submission/wAuthor.txt");
        List<TaskSQL> taskList = new ArrayList<>();
        taskList.add(
                new TaskSQLNonCallable(
                        new Tag("tag"),
                        "this is comment",
                        "Select * from * ;"));
        taskList.add(
                new TaskSQLNonCallable(
                        new Tag("tag2"),
                        "this is comment 2",
                        "SELECT USER(), DATABASE() ;"));
        List<Student> students = new ArrayList<>();
        students.add(new Student("foo","bar","test@test.de"));
        students.add(new Student("schlub","ba","schlubba@test.de"));
        Submission<TaskSQL> sub = new Submission<>(students,taskList, "submission");
        String submissionString = "/*authors*/\n" +
                "foo;bar;test@test.de\n" +
                "schlub;ba;schlubba@test.de\n" +
                "\n" +
                "/*tag*/\n" +
                "/* this is comment */\n" +
                "Select * from * ;\n" +
                "\n" +
                "/*tag2*/\n" +
                "/* this is comment 2 */\n" +
                "SELECT USER(), DATABASE() ;";
        return new DBFitSubmissionData(path, sub, submissionString);
    }



    public static Path getProjectZip(){
        return (resourcePath("/test/CheckerProject/project.zip"));
    }

    public static Path getProjectTemplate(){
        return (resourcePath("/test/CheckerProject/template.txt"));
    }

    public static Path getProjectPath(){
        return (resourcePath("/test/CheckerProject/"));
    }
}