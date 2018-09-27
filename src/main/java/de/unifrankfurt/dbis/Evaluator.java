package de.unifrankfurt.dbis;

import de.unifrankfurt.dbis.DBFit.ResultStorage;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class Evaluator {
    private List<Submission<TaskSQL>> samples;
    private String configPath;
    private EvalConfig config;
    private List<Solution> sols;
    private Path submissions;
    private SQLScript resetScript;
    private DataSource source;

    public Evaluator(String configPath) {
        this.configPath = configPath;
    }

    public void loadRessources(Boolean verbose) throws IOException, SubmissionParseException {
        if (verbose) System.out.println("Loading Config");
        config = EvalConfig.fromPath(Paths.get(configPath));

        config.configOK(); //throws IOException if not;
        submissions = Paths.get(config.getSubmissionPath());
        if (verbose) System.out.println("Loading Reset");
        resetScript = config.getResetScript();
        if (verbose) System.out.println("create Datasource");
        source = config.getDataSource();
        if (verbose) System.out.println("Loading Sample");
        samples = config.getSolutions();
        if (samples.isEmpty()) throw new IOException("No Solution File");
        if (!haveAllSameScheme(samples)) throw new IOException("tags of solutions do not match");
    }

    public List<Solution> createSolutions() throws SQLException {
        sols = new ArrayList<>();
        for ( Submission<TaskSQL> s : samples) {
            try {
                resetScript.execute(source);
            } catch (SQLException e) {
                throw new SQLException("ResetScript: " + e.getMessage(), e);
            }

            sols.add(s.generateSolution(config.getDataSource()));
        }
        return sols;
    }

    public boolean haveAllSameScheme(List<Submission<TaskSQL>> subs){
        assert Objects.nonNull(samples);
        assert !samples.isEmpty();
        int size = samples.size();
        if (size == 1) return true;
        Submission<TaskSQL> submission = subs.get(0);
        return submission.sameSchema(samples.subList(1,size));
    }


    public List<String> runEvaluation(boolean verbose, boolean csvOnlyBest) {
        List<Submission<TaskSQL>> subs = loadSubmissions(submissions);
        List<String> csv = new ArrayList<>();
        csv.add(this.sols.get(0).generateCSVHeader());
        for (Submission<TaskSQL> sub : subs) {
            runSubmissionEvaluation(sub, csv, verbose, csvOnlyBest);
        }
        return csv;
    }

    public void runSubmissionEvaluation(Submission<TaskSQL> sub, List<String> csv, boolean verbose, boolean csvOnlyBest) {
        if(verbose) {
            System.out.println(("EVALUATION: " + sub.getAuthors() + " " + sub.getPath().toString()));
        }
        List<ResultStorage> storages = new ArrayList<>();


        for (Solution sol : this.sols){
            ResultStorage evaluate;
            if (!sub.isSubmissionFor(sol)){
                evaluate = new ResultStorage(sol,sub,"Tags do not match");
                System.out.println(errorMsg(sol, sub, "Submissions tags do not match with solution"));
            }else{
                try {
                    evaluate = sol.evaluate(sol, source, resetScript, sub);
                    if(verbose)System.out.println(evaluate.getRawText());
                    if(verbose)System.out.println(evaluate.getCount());
                    System.out.println(evaluate.createReport());
                 } catch (SQLException | FitParseException e) {
                    evaluate = new ResultStorage(sol,sub,"DBFIT parsing failed");
                    System.out.println(errorMsg(sol, sub, e.getMessage()));
                }
            }storages.add(evaluate);
        }
        if (csvOnlyBest){
            csv.add(onlyBest(storages));
        }else{
            for(ResultStorage storage: storages)csv.add(storage.createCSV());
        }
    }

    private String errorMsg(Solution sol, Submission<TaskSQL> sub, String message) {
        return "FAILED Path:" + sub.getPath() + " Authors:" + sub.getAuthors()
                + " Solution:" + sol.getSubmission().getName() + " ErrorMsg:" + message;
    }

    private String onlyBest(List<ResultStorage> storages) {
        storages.sort(resultComperator());
        return storages.get(0).createCSV();
    }

    /**
     * method for comapring results of evaluation. Modify if needed.
     * @return
     */
    private Comparator<? super ResultStorage> resultComperator(){
        return (x, y) -> {
            int xs = x.getCountPass();
            int ys = y.getCountPass();
            if(xs>ys){
                return -1;
            }
            if(xs<ys){
                return 1;
            }
            return 0;
        };

    }

    private List<Submission<TaskSQL>> loadSubmissions(Path submissions)  {
        ArrayList<Submission<TaskSQL>> submissionList = new ArrayList<>();
        int depth = 0;
        if (Files.isDirectory(submissions)){
            depth = 2;
        }
        try {
            Files.walk(submissions, depth).forEach((x)-> {
                try {
                    if (Files.isDirectory(x)) return;
                    Submission<TaskSQL> s = Submission.fromPath(x).onlyTaskSQLSubmission();
                    s.setPath(x);
                    submissionList.add(s);
                    System.out.println("Submission loaded: "+x);
                } catch (SubmissionParseException e) {
                    System.out.println("SubmissionParseException, "+e.getMessage()+": "+x);
                } catch (IOException e) {
                    System.out.println("IOException, " + e.getMessage()+": "+x);
                }
            });
        } catch (IOException e) {
            System.err.println("Submission path is no valid File nor Directory.");
        }
        return submissionList;
    }


}
