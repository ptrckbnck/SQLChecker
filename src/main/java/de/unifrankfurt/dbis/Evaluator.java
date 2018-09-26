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

    public void loadRessources() throws IOException, SubmissionParseException {
        config = EvalConfig.fromPath(Paths.get(configPath));
        submissions = Paths.get(config.getSubmissionPaths());
        resetScript = config.getResetScript();
        source = config.getDataSource();
        sample = config.getSolution().onlyTaskSQLSubmission();
    }
    public void loadRessourcesVerbose() throws IOException, SubmissionParseException {
        System.out.println("Loading Config");
        config = EvalConfig.fromPath(Paths.get(configPath));
        submissions = Paths.get(config.getSubmissionPaths());
        System.out.println("Loading Reset");
        resetScript = config.getResetScript();
        System.out.println("create Datasource");
        source = config.getDataSource();
        System.out.println("Loading Sample");
        sample = config.getSolution();
    }

    private Solution solution() throws SQLException {
        resetScript.execute(source);
        return sample.generateSolution(config.getDataSource());
    }

    public void createSolution() throws SQLException {
        this.sol = solution();
    }
    public void createSolutionVerbose() throws SQLException {
        this.sol = solution();
        System.out.println("DBFITHTML of Solution:\n"+sol.getDBFitHtml());
    }

    public List<String> runEvaluationVerbose() {
        List<Submission<TaskSQL>> subs = loadSubmissions(submissions);

        List<String> csv = new ArrayList<>();
        csv.add(this.sol.generateCSVHeader());

        subs.forEach((sub)-> {
            try {
                System.out.println("EVALUATION: "+sub.getAuthors());
                NewResultStorage evaluate = sol.evaluate(source, resetScript, sub);
                System.out.println(evaluate.getRawText());
                System.out.println(evaluate.getCount());
                System.out.println(evaluate.createReport(this.sample));
                csv.add(evaluate.createCSV());
            } catch (SQLException | FitParseException e) {
                System.out.println(sub.getAuthors()+"[Failed:"+e.getMessage()+"]");
            }
        });
        return csv;
    }


    public List<String> runEvaluation(){
        List<Submission<TaskSQL>> subs = loadSubmissions(submissions);

        List<String> csv = new ArrayList<>();
        csv.add(this.sol.generateCSVHeader());
        subs.forEach((sub)-> {
            try {
                NewResultStorage newResultStorage = sol.evaluate(source, resetScript, sub);
                System.out.println(newResultStorage.createReport(this.sample));
                csv.add(newResultStorage.createCSV());
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
