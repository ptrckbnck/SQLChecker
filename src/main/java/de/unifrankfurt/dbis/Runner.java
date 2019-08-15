package de.unifrankfurt.dbis;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import de.unifrankfurt.dbis.EvalGUI.EvalGUIApp;
import de.unifrankfurt.dbis.Inner.Report;
import de.unifrankfurt.dbis.Inner.Solution;
import de.unifrankfurt.dbis.StudentGUI.StudentGUIApp;
import javafx.application.Application;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.System.exit;

/**
 * Runner is the main executable class for this project.
 * It parses command line arguments and executes accordingly.
 */
public class Runner {
    private static final String version = Runner.class.getPackage().getImplementationVersion();
    private static final String name = Runner.class.getPackage().getName();


    public static String getVersion() {
        return version;
    }

    public static String getName() {
        return name;
    }

    public static void main(String ... args) {
        Runner runner = new Runner();

        Options options = runner.createOptions();
        CommandLine commandLine = runner.argumentParse(options, args);
        if (commandLine == null) return;
        if (commandLine.hasOption("version")) {
            System.out.println(name + " " + version);
            return;
        }

        if (commandLine.hasOption("h")){
            printHelp(options);
            return;
        }


        if (!commandLine.hasOption("e") && !commandLine.hasOption("t")) {
            List<String> newargs = new ArrayList<>();
            String valueS = commandLine.getOptionValue("s");
            if (Objects.nonNull(valueS)) {
                newargs.add("s");
                newargs.add(valueS);
            }
            if (commandLine.hasOption("c") && Objects.nonNull(commandLine.getOptionValue("c"))) {
                newargs.add("c");
                newargs.add(commandLine.getOptionValue("c"));
            }
            if (commandLine.hasOption("v")) {
                newargs.add("v");
            }
            Application.launch(StudentGUIApp.class, newargs.toArray(new String[0]));
            return;
        }

        if (commandLine.hasOption("e")){
            String configPath = null;
            if (commandLine.hasOption("c")) {
                configPath = commandLine.getOptionValue("c");
            }
            if (!commandLine.hasOption("noGui")) {
                List<String> newArgs = new ArrayList<>();
                newArgs.add(configPath);
                Application.launch(EvalGUIApp.class, newArgs.toArray(new String[0]));
                return;
            }
            if (Objects.isNull(configPath)) {
                System.out.println("no config defined.");
                return;
            }

            boolean doCsv = commandLine.hasOption("csv");
            String saveCSV = commandLine.getOptionValue("csv");

            // check out path
            if (doCsv && !Objects.isNull(saveCSV)) {
                Path path = Paths.get(saveCSV);
                try {
                    Files.write(path, List.of(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    System.out.println("Cannot write CSV to " + e.getMessage());
                    return;
                }
            }


            try {
                Evaluator evaluator = new Evaluator(configPath);

                boolean verbose = false;
                if (commandLine.hasOption("v")) {
                    verbose = true;
                }
                if (verbose) System.out.println("Loading Ressources:----------------------------");
                evaluator.loadRessources(verbose);
                if (verbose) System.out.println("create Solution----------------------------");
                List<Solution> sol = evaluator.createSolutions();
                if (sol.isEmpty()){
                    System.err.println("No solution created");
                    exit(0);
                }
                if (verbose){
                    for(Solution s :sol){
                        System.out.println("Result of " + s.getName());
                        System.out.println(s.getExpectedResultPrintable());
                    }
                }
                if (verbose) {
                    System.out.println("run Evaluation----------------------------");
                }
                Report report = evaluator.runEvaluation(verbose,
                        commandLine.hasOption("onlyBest"),
                        sol.get(0).getMetaData(),
                        sol);
                if (verbose) {
                    System.out.println("create csv----------------------------");
                }
                if (doCsv){
                    final List<String> csv = report.getCSV();
                    if (Objects.isNull(saveCSV)) {
                        csv.forEach(System.out::println);
                    }
                    else {
                        Path path = Paths.get(saveCSV);
                        try {
                            Files.write(path, csv, StandardCharsets.UTF_8);
                        } catch(IOException e){
                            System.out.println("could not write CSV at "+saveCSV+": "+e.getMessage());
                        }
                    }
                }


            } catch (IOException | SQLException e) {

                if (commandLine.hasOption("v")){
                    e.printStackTrace();
                } else {
                    System.err.println(e.toString());
                }

            }
            AbandonedConnectionCleanupThread.checkedShutdown();
        }

        if (commandLine.hasOption("t")) {

            boolean hasO = commandLine.hasOption("o");
            String oValue = commandLine.getOptionValue("o");
            String tValue = commandLine.getOptionValue("t");

            Path out = null;

            if (Objects.isNull(tValue)) {
                //should not be reached.
                System.err.println("Argument of -t was not defined.");
                return;
            }
            Path in = Paths.get(tValue);

            if (Objects.nonNull(oValue)) {
                out = Paths.get(oValue);
            }

            boolean useStdout = !hasO;

            try {
                new TemplateGenerator(in, out, useStdout).run();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * parses arguments
     *
     * @param options defined via createOptions()
     * @param args    command line arguments
     * @return new CommandLine or null if Parse exception occurred.
     */
    private CommandLine argumentParse(Options options, String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return cmd;
    }

    /**
     * prints help for commandline usage
     *
     * @param options defined via createOptions()
     */
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        String ls = System.lineSeparator();
        String header = ls + "SQLChecker" + ls
                + "A tool a create and evaluate exercises for SQL.";
        String footer = ls + "visit our Github: " + ls + "https://github.com/ptrckbnck/SQLChecker";
        formatter.printHelp("sqlchecker", header, options, footer, true);
    }

    /**
     * creates cli Options for this program
     *
     * @return Options
     */
    private Options createOptions() {
        Options options = new Options();

        OptionGroup optStart = new OptionGroup();
        optStart.addOption(Option.builder("s")
                .longOpt("start")
                .desc("runs SQLChecker-StudentGUI. This parameter can be omitted. " +
                        "Use this if you want to solve an exercise.\n" +
                        "You can directly load a project file via argument Path.")
                .hasArg(true)
                .optionalArg(true)
                .argName("Path (*.sqlc)")
                .build());
        optStart.addOption(Option.builder("e")
                .longOpt("evaluate")
                .desc("starts the evaluation process of submissions.")
                .build());
        optStart.addOption(Option.builder("t")
                .longOpt("template")
                .desc("generates Student Template from given Solution. Without specifying outPath, result is printed to stdout. ")
                .hasArg()
                .optionalArg(false)
                .argName("Path (*.sql)")
                .build());
        options.addOptionGroup(optStart);

        options.addOption(Option.builder("noGui")
                .longOpt("noGui")
                .desc("Evaluation without GUI. You need to define a valid config file").build());


        options.addOption(Option.builder("c")
                .longOpt("config")
                .desc("Path to config file. (*.ini) or (*.conf), depends if you run Evaluation or StudentGUI.")
                .hasArg()
                .optionalArg(true)
                .argName("Path")
                .build());

        Option verbose = Option.builder("v")
                .longOpt("verbose")
                .desc("verbose mode. Prints a lot of information, mainly for debugging.")
                .build();
        options.addOption(verbose);

        options.addOption(Option.builder("csv")
                .longOpt("csv")
                .desc("puts csv-report of evaluations to file at Path or System.out by default.")
                .hasArg()
                .optionalArg(true)
                .argName("Path")
                .build());

        Option onlyBest = Option.builder("onlyBest")
                .longOpt("onlyBest")
                .desc("in csv mode, do not print all evaluations, only the best of each student. ")
                .build();
        options.addOption(onlyBest);

        options.addOption(Option.builder("o")
                .longOpt("out")
                .hasArg()
                .desc("Only relevant with -t. Path where template should be saved. If you do not specify Path." +
                        " Result is placed in same dir as solution.")
                .argName("Path")
                .required(false)
                .optionalArg(true)
                .build());

        Option help = Option.builder("h")
                .longOpt("help")
                .desc("prints this help")
                .build();
        options.addOption(help);

        Option version = Option.builder(null)
                .longOpt("version")
                .desc("prints version of SQLChecker")
                .build();
        options.addOption(version);

        return options;
    }
}
