package de.unifrankfurt.dbis;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import de.unifrankfurt.dbis.Submission.Solution;
import de.unifrankfurt.dbis.Submission.SubmissionParseException;
import javafx.application.Application;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static java.lang.System.exit;

/**
 * Runner is the main executable class for this project.
 * It parses command line arguments and executes accordingly.
 * does not work atm.
 */
public class Runner {
    private static final String version = "1.0.2";
    private static final String name = "SQL Checker";


    public static String getVersion() {
        return version;
    }

    public static String getName() {
        return name;
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
                .desc("runs SQLChecker. this argument can be omitted.")
                .build());
        optStart.addOption(Option.builder("e")
                .longOpt("evaluate")
                .desc("evaluate Submission")
                .build());
        options.addOptionGroup(optStart);

        options.addOption(Option.builder("c")
                .longOpt("config")
                .desc("config path")
                .hasArg()
                .optionalArg(true)
                .argName("FILE")
                .build());

        Option verbose = Option.builder("v")
                .longOpt("verbose")
                .desc("verbose mode")
                .build();
        options.addOption(verbose);

        options.addOption(Option.builder("csv")
                .longOpt("csv")
                .desc("puts csv report to file path or System.out if omitted")
                .hasArg()
                .optionalArg(true)
                .argName("FILE")
                .build());

        Option onlyBest = Option.builder("onlyBest")
                .longOpt("onlyBest")
                .desc("in csv mode do not print all evaluations. only best of student")
                .build();
        options.addOption(onlyBest);
                                                           

        Option help = Option.builder("h")
                .longOpt("help")
                .desc("prints this help")
                .build();
        options.addOption(help);




        return options;
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
        String header = ls + "SQLChecker" + ls + ls;
        String footer = ls + "" + ls;
        formatter.printHelp("sqlchecker", header, options, footer, true);
    }


    public static void main(String ... args) {
        Runner runner = new Runner();

        Options options = runner.createOptions();
        CommandLine commandLine = runner.argumentParse(options, args);
        if (commandLine == null) return;


        if (commandLine.hasOption("h")){
            printHelp(options);
            return;
        }

        if (commandLine.getOptions().length == 0 || commandLine.hasOption("s")){
            Application.launch(GUIApp.class, args);
            return;
        }


        if (commandLine.hasOption("e")){
            String configPath;
            if (commandLine.hasOption("c")) {
                configPath = commandLine.getOptionValue("c");
            } else {
                System.out.println("no config defined.");
                return;
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
                        System.out.println("DBFitHtml of "+ sol.toString());
                        System.out.println(s.getDBFitHtml());
                    }
                }
                if (verbose) System.out.println("run Evaluation----------------------------");
                List<String> csv = evaluator.runEvaluation(verbose,commandLine.hasOption("onlyBest"));

                boolean doCsv = commandLine.hasOption("csv");
                String saveCSV = commandLine.getOptionValue("csv");
                if (doCsv){
                    if (saveCSV==null){
                        csv.forEach(System.out::println);
                    }
                    else {
                        Path path = Paths.get(saveCSV);
                        try {
                            Files.write(path,csv, StandardCharsets.UTF_8);
                        } catch(IOException e){
                            System.out.println("could not write CSV at "+saveCSV+": "+e.getMessage());
                        }
                    }
                }



            } catch (IOException | SQLException e) {

                if (commandLine.hasOption("v")){
                    e.printStackTrace();
                }else System.err.println(e.getMessage());

            } catch (SubmissionParseException e) {
                if (commandLine.hasOption("v")){
                    e.printStackTrace();
                }else System.err.println(e.getMessage()+" "+e.getErrorCode());

            }

        }
        // MysSQLDatasource creates abandoned connection. do not know why. this helps exit program.
        try {
            AbandonedConnectionCleanupThread.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return;
    }
}
