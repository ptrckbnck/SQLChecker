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
import java.util.List;
import java.util.Objects;


public class Evaluator {
    private List<Submission> samples;
    private String configPath;
    private EvalConfig config;
    private List<Solution> sols;
    private Path submissionsPath;
    private SQLScript resetScript;
    private DataSource source;
    private List<String> solutionScheme;

    public Evaluator(String configPath) {
        this.configPath = configPath;
    }

    public void loadRessources(Boolean verbose) throws IOException, SubmissionParseException {
        if (verbose) System.out.println("Loading Config");
        config = EvalConfig.fromPath(Paths.get(configPath));
        config.configOK(); //throws IOException if not;
        submissionsPath = Paths.get(config.getSubmissionPath());
        if (verbose) System.out.println("Loading Reset");
        resetScript = config.getResetScript();
        if (verbose) System.out.println("create Datasource");
        source = config.getDataSource();
        if (verbose) System.out.println("Loading Sample");
        samples = config.getSolutions();
        if (samples.isEmpty()) throw new IOException("No Solution File");
        if (!haveAllSameScheme(samples)) throw new IOException("tags of solutions do not match");
        this.solutionScheme = this.samples.get(0).getTagStrings();
    }


    public List<Solution> createSolutions() throws SQLException {
        sols = new ArrayList<>();
        for (Submission s : samples) {
            try {
                resetScript.execute(source);
            } catch (SQLException e) {
                throw new SQLException("ResetScript: " + e.getMessage(), e);
            }

            Solution sol = s.generateSolution(config.getDataSource(), resetScript);
            sols.add(sol);
        }
        return sols;
    }

    public boolean haveAllSameScheme(List<Submission> subs) {
        assert Objects.nonNull(samples);
        assert !samples.isEmpty();
        int size = samples.size();
        if (size == 1) return true;
        Submission submission = subs.get(0);
        return submission.sameSchema(samples.subList(1,size));
    }


    public Report runEvaluation(boolean verbose, boolean csvOnlyBest) {
        List<ResultStorage> resultStorages = new ArrayList<>();
        List<Submission> subs = loadSubmissions(submissionsPath, resultStorages);
        int i = 1;
        int count_digits = ((int) Math.log10(subs.size())) + 1;
        for (Submission sub : subs) {
            if (verbose) {
                System.err.format("submission %" + count_digits + "d of %d submissions%n", i++, subs.size());
                System.err.flush();
                System.out.flush();
            }
            runSubmissionEvaluation(sub, resultStorages, verbose, csvOnlyBest);
        }
        return new Report(resultStorages, this.sols.get(0).csvCreator());
    }


    public void runSubmissionEvaluation(Submission sub, List<ResultStorage> storages, boolean verbose, boolean csvOnlyBest) {
        if(verbose) {
            System.out.println(("EVALUATION: " + sub.getAuthors() + " " + sub.getPath().toString()));
        }

        List<ResultStorage> curStorages = new ArrayList<>();
        if (!sub.isSubmissionFor(sols.get(0))) {
            Submission fixedSub = sols.get(0).tryToFixTagsFor(sub);
            if (Objects.isNull(fixedSub)) {
                storages.add(new ResultStorage(submissionsPath,
                        this.solutionScheme.size(),
                        sub,
                        new Exception("Tags do not match")));
                System.out.println(errorMsg(null,
                        sub,
                        "Submissions tags do not match with solution"));
                return;
            }
            sub = fixedSub;
        }

        for (Solution sol : this.sols){
            ResultStorage evaluate;
            try {
                evaluate = sol.evaluate(submissionsPath, source, resetScript, sub, verbose);
                if (verbose) {
                    System.out.println(evaluate.getReadableResult());
                }
                System.out.println(evaluate.createReport(this.solutionScheme));
            } catch (SQLException | FitParseException e) {
                evaluate = new ResultStorage(submissionsPath, sol, sub, e);
                System.out.println(errorMsg(sol, sub, e.getMessage()));

            }
            curStorages.add(evaluate);
        }
        if (csvOnlyBest){
            storages.add(onlyBest(curStorages));
        }else{
            storages.addAll(curStorages);
        }
    }

    private String errorMsg(Solution sol, Submission sub, String message) {
        if (Objects.isNull(sol))
            return "FAILED Path:" + sub.getPath() + " Authors:" + sub.getAuthors() + " ErrorMsg:" + message;
        return "FAILED Path:" + sub.getPath() + " Authors:" + sub.getAuthors()
                + " Solution:" + sol.getSubmission().getName() + " ErrorMsg:" + message;
    }

    private ResultStorage onlyBest(List<ResultStorage> storages) {
        storages.sort(ResultStorage.resultComperator());
        return storages.get(0);
    }

    /**
     * loads every submissionsPath
     * @param submissionsPath path to submissionsPath
     * @param storages storage-container.if loading of a submission was unsuccessful, a new storage is added to container.
     * @return
     */
    private List<Submission> loadSubmissions(Path submissionsPath, List<ResultStorage> storages) {
        ArrayList<Submission> submissionList = new ArrayList<>();
        int depth = 0;
        if (Files.isDirectory(submissionsPath)) {
            depth = 2;
        }
        try {
            Files.walk(submissionsPath, depth).forEach((x) -> {
                try {
                    if (Files.isDirectory(x)) return;
                    Submission s = Submission.fromPath(x);
                    s.setPath(x);
                    submissionList.add(s);
                    System.out.println("Submission loaded: "+x);
                } catch (SubmissionParseException e) {
                    storages.add(new ResultStorage(submissionsPath, this.solutionScheme.size(), x, e));
                    System.out.println("SubmissionParseException, "+e.getMessage()+": "+x);
                } catch (IOException e) {
                    storages.add(new ResultStorage(submissionsPath, this.solutionScheme.size(), x, e));
                    System.out.println("IOException, " + e.getMessage()+": "+x);
                }
            });
        } catch (IOException e) {
            System.err.println("Submission path is no valid File nor Directory.");
        }
        return submissionList;
    }


}
