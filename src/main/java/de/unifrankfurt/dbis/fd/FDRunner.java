package de.unifrankfurt.dbis.fd;

import com.google.gson.Gson;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

/**
 * Command line program which implements the FDSolver.
 */
public class FDRunner {

    /**
     * OutputManger manages the output stream for print
     */
    private static class OutputManager {
        private final PrintWriter writer;

        OutputManager(String file) throws FileNotFoundException, UnsupportedEncodingException {
            if (file == null) {
                this.writer = null;
                return;
            }
            this.writer = new PrintWriter(file, "UTF-8");
        }

        /**
         * prints msg in correct steam
         *
         * @param msg to print
         */
        public void println(String msg) {
            if (this.writer == null) System.out.println(msg);
            else {
                this.writer.println(msg);
                this.writer.flush();
            }
        }

        /**
         * closes steam if needed
         */
        private void close() {
            if (this.writer != null) this.writer.close();
        }
    }

    /**
     * creates cli Options for this program
     *
     * @return Options
     */
    private Options createOptions() {
        Options options = new Options();
        Option input = Option.builder("i")
                .longOpt("input")
                .desc("input file path. Mutual exclusive with -p")
                .hasArg()
                .argName("FILE")
                .build();
        options.addOption(input);

        Option output = Option.builder("o")
                .longOpt("output")
                .desc("output file path.")
                .hasArg()
                .argName("FILE")
                .build();
        options.addOption(output);

        Option help = Option.builder("h").longOpt("help").build();
        options.addOption(help);

        Option onlyRead = Option.builder("r")
                .longOpt("onlyRead")
                .desc("only reads input without analysing it")
                .build();
        options.addOption(onlyRead);

        Option delimiter = Option.builder("d")
                .longOpt("delimiter")
                .hasArg()
                .desc("String delimiting attributes in input. Default is \" \"")
                .argName("STRING")
                .build();
        options.addOption(delimiter);

        Option attributes = Option.builder("a")
                .longOpt("attributes")
                .argName("ATTRIBUTE ..")
                .hasArgs()
                .desc("forced attributes. Overrides defined attributes from data")
                .build();
        options.addOption(attributes);

        Option prompt = Option.builder("p")
                .longOpt("prompt")
                .desc("opens command prompt. Mutual exclusive with -o")
                .build();
        options.addOption(prompt);

        Option json = Option.builder("j")
                .longOpt("json")
                .desc("writes report in json format. ignored with -r")
                .build();
        options.addOption(json);
        return options;
    }

    private CommandLine argumentParse(Options options, String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return cmd;
    }


    public static int main(String[] args) {
        FDRunner fdr = new FDRunner();
        Options options = fdr.createOptions();
        CommandLine cl = fdr.argumentParse(options, args);
        if (cl == null) {
            System.err.println("Argument parsing failed");
            return 4;
        }

        //print help
        if (cl.hasOption("h") | (cl.hasOption("i") & cl.hasOption("p"))) {
            fdr.printHelp(options);
            return 0;
        }

        //choose output
        String outFile = null;
        if (cl.hasOption("o")) {
            outFile = cl.getOptionValue("o");
        }
        OutputManager om = null;
        try {
            om = new OutputManager(outFile);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (om == null) {
            System.err.println("Creating output file failed");
            return 3;
        }

        //choose input
        BufferedReader bufReader = null;
        if (cl.hasOption("i")) {
            String inFile = cl.getOptionValue("i");
            try {
                FileReader reader = new FileReader(inFile);
                bufReader = new BufferedReader(reader);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (cl.hasOption("p")) {
            bufReader = new BufferedReader(new InputStreamReader(System.in));
        } else System.err.println("should be -i or -p");

        if (bufReader == null) {
            System.err.println("input file not found");
            return 2;
        }


        //check if attributes given
        ArrayList<String> input = new ArrayList<>();
        List<String> attributeList = null;
        if (cl.hasOption("a")) {
            attributeList = cl.getArgList();
        }

        //choose data delimiter
        String delimiter = " ";
        if (cl.hasOption("d"))
            delimiter = cl.getOptionValue("d");

        try {
            String x;
            //first line
            x = bufReader.readLine();
            if (x != null) {
                //check if first line defines attributes
                if (!x.contains("->")) {
                    if (attributeList == null) {
                        //attributes from parameter have higher priority
                        attributeList = Arrays.asList(x.split(delimiter));
                    }
                } else input.add(x);
                if (cl.hasOption("r") | !cl.hasOption("j")) om.println(x);
            }
            //remainder
            Boolean exit = false;
            while ((!exit) && ((x = bufReader.readLine()) != null)) {
                if (x.isEmpty()) exit = true;
                else {
                    input.add(x);
                    if (cl.hasOption("r") | !cl.hasOption("j")) om.println(x);
                }
            }
            om.close();
        } catch (IOException e) {
            e.printStackTrace();
            return 10;
        } finally {
            if (outFile == null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //only read
        if (cl.hasOption("r")) return 0;

        //create FDRelation
        FDRelation fdRelation = new FDRelation(attributeList);
        //parse input
        for (String str : input) {
            FDSimpleRelation simple = FDSimpleRelation.parse(str, delimiter);
            if (simple == null) {
                System.err.println("Failed parsing: " + str);
                return 0;
            }
            try {
                fdRelation.add(simple);
            } catch (FDKey.EmptyException e) {
                System.err.println("Empty keys are not allowed in " + str);
                return 0;
            } catch (FDRelation.UnexpectedAttributeException e) {
                System.err.println("Unexpected attributes " + e.getMessage() + " in " + str);
                return 0;
            }
        }


        FDSolver solver = new FDSolver(fdRelation);
        if (cl.hasOption("j")) {
            om.println(new Report(input, solver).toJson());
        } else om.println(solver.report());
        return 0;
    }

    /**
     * class which combines all useful information about relation
     */
    static class Report {
        private final List<String> input;
        private final Set<String> attributes;
        private final Set<String> forcedAttributes;
        private final Map<String, FDKeySet> relation;
        private final Set<String> prim;
        private final Set<String> notPrim;
        private final FDKeySet keyCandidates;
        private final int normalForm;


        Report(List<String> input, FDSolver solver) {
            this.input = input;
            this.attributes = solver.getRelation().getAttributes();
            this.relation = solver.getRelation().getData();
            this.forcedAttributes = solver.getRelation().getForcedAttributes();
            this.keyCandidates = solver.getKeyCandidates();
            this.prim = solver.getPrim();
            this.notPrim = solver.getNotPrim();
            this.normalForm = solver.getNF();
        }

        Report(List<String> input, Set<String> attributes, Set<String> forcedAttributes, Map<String, FDKeySet> relation, Set<String> prim, Set<String> notPrim, FDKeySet keyCandidates, int normalForm) {
            this.input = input;
            this.attributes = attributes;
            this.forcedAttributes = forcedAttributes;
            this.relation = relation;
            this.prim = prim;
            this.notPrim = notPrim;
            this.keyCandidates = keyCandidates;
            this.normalForm = normalForm;
        }

        String toJson() {
            return new Gson().toJson(this);
        }

        public String toString() {
            return this.toJson();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Report report = (Report) o;
            return normalForm == report.normalForm &&
                    Objects.equals(input, report.input) &&
                    Objects.equals(attributes, report.attributes) &&
                    Objects.equals(forcedAttributes, report.forcedAttributes) &&
                    Objects.equals(relation, report.relation) &&
                    Objects.equals(prim, report.prim) &&
                    Objects.equals(notPrim, report.notPrim) &&
                    Objects.equals(keyCandidates, report.keyCandidates);
        }

        @Override
        public int hashCode() {

            return Objects.hash(input, attributes, forcedAttributes, relation, prim, notPrim, keyCandidates, normalForm);
        }
    }

    /**
     * prints help msg
     *
     * @param options cli options
     */
    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        String ls = System.lineSeparator();
        String header = ls + "Analyses functional dependencies of given relation." + ls + ls;
        String footer = ls + "Example input:" + ls
                + "A B C    -- optional line that defines all attributes" + ls
                + "A -> B   -- first line of relation" + ls
                + "C -> A B -- another line of relation" + ls
                + "use empty line to finish input." + ls + ls
                + "Please report issues to Patrick.Bonack@gmail.com";
        formatter.printHelp("fda", header, options, footer, true);
    }

}
