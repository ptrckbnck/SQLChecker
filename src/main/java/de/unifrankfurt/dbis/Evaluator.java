package de.unifrankfurt.dbis;

import de.unifrankfurt.dbis.DBFit.NewResultStorage;
import de.unifrankfurt.dbis.Submission.*;
import de.unifrankfurt.dbis.config.DataSource;
import de.unifrankfurt.dbis.config.EvalConfig;
import fit.exception.FitParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Evaluator {
    private Submission<TaskSQL> sample;
    private String configPath;
    private EvalConfig config;
    private Solution sol;
    private Path submissions;
    private SQLScript resetScript;
    private DataSource source;

    public Evaluator(String configPath) {
        this.configPath = configPath;
    }

    public void loadRessources(Boolean verbose) throws IOException, SubmissionParseException {
        if (verbose) System.out.println("Loading Config");
        config = EvalConfig.fromPath(Paths.get(configPath));
        submissions = Paths.get(config.getSubmissionPaths());
        if (verbose) System.out.println("Loading Reset");
        resetScript = config.getResetScript();
        if (verbose) System.out.println("create Datasource");
        source = config.getDataSource();
        if (verbose) System.out.println("Loading Sample");
        sample = config.getSolution().onlyTaskSQLSubmission();
    }

    public Solution createSolution() throws SQLException {
        resetScript.execute(source);
        return sol=sample.generateSolution(config.getDataSource());
    }


    public List<String> runEvaluation(boolean verbose) {
        List<Submission<TaskSQL>> subs = loadSubmissions(submissions);

        List<String> csv = new ArrayList<>();
        csv.add(this.sol.generateCSVHeader());

        subs.forEach((sub)-> {
            try {
                if(verbose)System.out.println("EVALUATION: "+sub.getAuthors() +" "+sub.getPath().toString());
                NewResultStorage evaluate = sol.evaluate(source, resetScript, sub);
                if(verbose)System.out.println(evaluate.getRawText());
                if(verbose)System.out.println(evaluate.getCount());
                System.out.println(evaluate.createReport(this.sample));
                csv.add(evaluate.createCSV());
            } catch (SQLException | FitParseException e) {
                System.out.println(sub.getAuthors()+"[Failed:"+e.getMessage()+"]");
            }
        });
        return csv;
    }



    private List<Submission<TaskSQL>> loadSubmissions(Path submissions)  {
        ArrayList<Submission<TaskSQL>> submissionList = new ArrayList<>();
        int depth = 0;
        if (Files.isDirectory(submissions)){
            depth = 1;
        }
        try {
            Files.walk(submissions, depth).forEach((x)-> {
                try {
                    Submission<TaskSQL> s = Submission.fromPath(x).onlyTaskSQLSubmission();
                    s.setPath(x);
                    submissionList.add(s);
                    System.out.println(x+" loaded.");
                } catch (SubmissionParseException e) {
                    System.out.println("SubmissionParseException: "+e.getMessage()+": "+x+" could not be loaded.");
                } catch (IOException e) {
                    System.out.println(e.getMessage()+": "+x+" could not be loaded.");
                }
            });
        } catch (IOException e) {
            System.err.println("Submission path is no valid File nor Directory.");
        }
        return submissionList;
    }





    public Boolean configOK() {
        return this.config.configOK();
    }


}
