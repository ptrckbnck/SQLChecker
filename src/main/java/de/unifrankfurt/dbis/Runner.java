package de.unifrankfurt.dbis;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import de.unifrankfurt.dbis.Submission.SubmissionParseException;
import javafx.application.Application;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Runner is the main executable class for this project.
 * It parses command line arguments and executes accordingly.
 * does not work atm.
 */
public class Runner {
    private static final String version = "1.0.1";
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
        optStart.setRequired(true);
        options.addOptionGroup(optStart);

        options.addOption(Option.builder("c")
                .longOpt("config")
                .desc("config path")
                .hasArg()
                .argName("FILE")
                .build());

        Option verbose = Option.builder("v")
                .longOpt("verbose")
                .desc("verbode mode")
                .build();
        options.addOption(verbose);


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


    public static int main(String ... args) {
        Runner runner = new Runner();

        Options options = runner.createOptions();
        CommandLine commandLine = runner.argumentParse(options, args);
        if (commandLine == null) return 1;


        if (commandLine.hasOption("h")){
            printHelp(options);
            return 0;
        }

        if (commandLine.getOptions().length == 0 || commandLine.hasOption("s")){
            Application.launch(GUIApp.class, args);
            return 0;
        }


        if (commandLine.hasOption("e")){
            String configPath;
            if (commandLine.hasOption("c")) {
                configPath = commandLine.getOptionValue("c");
            } else {
                System.out.println("no config defined.");
                return 0;
            }

            try {
                Evaluator evaluator = new Evaluator(configPath);
                if (commandLine.hasOption("v")){
                    System.out.println("Loading Ressources:");
                    evaluator.loadRessourcesVerbose();
                    if (!evaluator.configOK()) {
                        System.err.println("Config faulty");
                        return 0;
                    }
                    System.out.println("create Solution");
                    evaluator.createSolutionVerbose();
                    System.out.println("run Evaluation");
                    evaluator.runEvaluationVerbose();
                }else {

                    evaluator.loadRessources();
                    if (!evaluator.configOK()) {
                        System.err.println("Config faulty");
                        return 0;
                    }
                    evaluator.createSolution();
                    evaluator.runEvaluation();
                }
            } catch (IOException | SQLException e) {
                System.err.println(e.getMessage());
                if (commandLine.hasOption("v"))e.printStackTrace();
            } catch (SubmissionParseException e) {
                System.err.println(e.getMessage()+":"+e.getErrorCode());
                if (commandLine.hasOption("v"))e.printStackTrace();
            }

        }
        // MysSQLDatasource creates abandoned connection. do not know why. this helps exit program.
        try {
            AbandonedConnectionCleanupThread.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
