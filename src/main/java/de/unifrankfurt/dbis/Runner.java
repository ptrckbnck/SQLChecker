package de.unifrankfurt.dbis;

import de.unifrankfurt.dbis.config.XConfig;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Runner is the main executable class for this project.
 * It parses command line arguments and executes accordingly.
 * does not work atm.
 */
public class Runner {

    private static String defaultConfigPath = "config/main.ini";
    private static final String version = "1.0.1";
    private static final String name = "SQL Checker";
    private static Logger logger = LogManager.getLogger(Runner.class.getName());


    public static String getVersion() {
        return version;
    }

    public static String getName() {
        return name;
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * creates cli Options for this program
     *
     * @return Options
     */
    private Options createOptions() {
        Options options = new Options();
        Option config = Option.builder("c")
                .longOpt("config")
                .desc("config path. default is " + defaultConfigPath)
                .hasArg()
                .argName("FILE")
                .build();
        options.addOption(config);

        OptionGroup optgrp = new OptionGroup();

        Option exec = Option.builder("e")
                .longOpt("exec")
                .desc("SubmissionExecutor")
                .build();
        optgrp.addOption(exec);

        Option gen = Option.builder("s")
                .longOpt("solutionGen")
                .desc("SolutionGenerator")
                .build();
        optgrp.addOption(gen);

        Option gui = Option.builder("g")
                .longOpt("gui")
                .desc("start gui")
                .build();
        options.addOption(gui);


        options.addOptionGroup(optgrp);


        Option configBuild = Option.builder("b")
                .longOpt("buildConfig")
                .hasArg()
                .argName("<config path>")
                .desc("build new config")
                .build();
        options.addOption(configBuild);

        Option help = Option.builder("h")
                .longOpt("help")
                .desc("print help")
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
    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        String ls = System.lineSeparator();
        String header = ls + "TODO" + ls + ls;
        String footer = ls + "TODO" + ls;
        formatter.printHelp("sqlchecker", header, options, footer, true);
    }


    public static int main(String[] args) {
        final long startTime = System.currentTimeMillis();
        Runner runner = new Runner();
        Logger logger = runner.getLogger();
        logger.info("Application started");
        logger.info("SQLChecker, version " + Runner.getVersion());
        boolean success = true;

        Options options = runner.createOptions();
        CommandLine commandLine = runner.argumentParse(options, args);
        if (commandLine == null) return 1;


        String configPath;
        if (commandLine.hasOption("c")) {
            configPath = commandLine.getOptionValue("c");
        } else {
            configPath = defaultConfigPath;
        }

        if (commandLine.hasOption("h")) {
            runner.printHelp(options);

        } else if (commandLine.hasOption("g")) {
            logger.info("starting gui");
            //TODO start gui
            return 0;
        } else if (commandLine.hasOption("e") | commandLine.hasOption("s") | commandLine.hasOption("t")) {
            logger.info("Using XConfig: " + configPath);
            XConfig XConfig = null;
            try {
                XConfig = XConfig.fromFile(new File(configPath));
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
            if (XConfig != null) {
                if (commandLine.hasOption("e")) {
                    //TODO
                }

            } else if (commandLine.hasOption("b")) {
                //TODO
            } else runner.printHelp(options);


            logger.info("Application finished");
            final long endTime = System.currentTimeMillis();

            System.out.println("Total execution time: " + (endTime - startTime));
            return (success ? 0 : 1);
        }
        return 1;
    }
}
