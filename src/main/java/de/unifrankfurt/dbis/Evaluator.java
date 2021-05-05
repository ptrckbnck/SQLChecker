package de.unifrankfurt.dbis;

import de.unifrankfurt.dbis.Inner.*;
import de.unifrankfurt.dbis.config.DataSource;
import de.unifrankfurt.dbis.config.EvalConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class Evaluator {
    private List<Base> samples;
    private final String configPath;
    private EvalConfig config;
    private Path submissionsPath;
    private SQLScript resetScript;
    private DataSource source;

    public Evaluator(String configPath) {
        this.configPath = configPath;
    }

    public static List<Solution> createSolutions(EvalConfig config, SQLScript resetScript, List<Base> samples, DataSource source) throws SQLException {
        List<Solution> sols = new ArrayList<>();
        for (Base s : samples) {
            try {
                resetScript.execute(source);
            } catch (SQLException e) {
                throw new SQLException("ResetScript: " + e.getMessage(), e);
            }

            Solution sol = s.generateSolution(config.getDataSource(), resetScript);
            if (Objects.isNull(sol)) {
                System.err.println("Cresting Solution for " + resetScript + " failed.");
            } else {
                sols.add(sol);
            }
        }
        return sols;
    }

    public static void runEachEvaluation(List<Solution> sols,
                                         DataSource source,
                                         SQLScript resetScript,
                                         boolean verbose,
                                         boolean csvOnlyBest,
                                         Report report,
                                         List<Base> subs) {
        int i = 1;
        int count_digits = ((int) Math.log10(subs.size())) + 1;
        for (Base sub : subs) {
            if (verbose) {
                System.out.format("submission %" + count_digits + "d of %d submissions%n", i++, subs.size());
                System.out.flush();
            }
            runSubmissionEvaluation(sols, source, resetScript, sub, report, verbose, csvOnlyBest);
        }
    }

    public static void runSubmissionEvaluation(List<Solution> sols,
                                               DataSource source,
                                               SQLScript resetScript,
                                               Base sub,
                                               Report report,
                                               boolean verbose,
                                               boolean csvOnlyBest) {
        if(verbose) {
            System.out.println(
                    "EVALUATION: [" + sub.getAuthors()
                            .stream()
                            .map(Student::getName)
                            .collect(Collectors.joining(","))
                            + "] "
                            + sub.getPath().getFileName().toString());
        }
        List<ResultStorage> curStorages = new ArrayList<>();



        //run evaluation for every given solution
        for (Solution sol : sols) {
            if (sub.getPath() == null) {
                System.err.println(sol.getName() + " is null");
            }
            ResultStorage resultStorage = new ResultStorage(sub.getPath());
            resultStorage.setValid(true);
            resultStorage.setCharset(sub.getCharset());
            resultStorage.setAuthors(sub.getAuthors());
            resultStorage.setSolution(sol);
            resultStorage.setSolutionName(sol.getName());

            try {
                if (Objects.isNull(sub.getName())) {
                    throw new InvalidSubmissionException("Not a submission");
                }
                if (sol.getTasks().size() != sub.getTasks().size()) {
                    String errorString =
                            String.format("Submission named \"%s\" has invalid number of tasks: %d",
                                    sub.getName(), sub.getTasks().size());
                    throw new InvalidSubmissionException(errorString);
                }
                sol.evaluate(resultStorage, source, resetScript, sub, verbose);
            } catch (Exception e) {
                resultStorage.setBase(sub)
                        .setSolution(sol)
                        .setException(e);
                System.err.println(errorMsg(sol, sub, e.getMessage()));

            }
            curStorages.add(resultStorage);
        }
        if (csvOnlyBest){
            report.add(onlyBest(curStorages));
        }else{
            report.addAll(curStorages);
        }
    }

    private static String errorMsg(Solution sol, Base sub, String message) {
        if (Objects.isNull(sol))
            return "FAILED Path:" + sub.getPath() + " Authors:" + sub.getAuthors() + " ErrorMsg:" + message;
        return "FAILED Path:" + sub.getPath() + " Authors:" + sub.getAuthors()
                + " Solution:" + sol.getName() + " ErrorMsg:" + message;
    }


    public static List<Base> loadSubmissions(Path root, Report report) {
        int depth = 0;
        if (Files.isDirectory(root)) {
            depth = 2;
        }
        List<Path> pathes = new ArrayList<>();
        try {
            Files.walk(root, depth).forEach((x) -> {
                if (Files.isDirectory(x)) return;
                pathes.add(x);
            });
        } catch (IOException e) {
            System.err.println("root path is no valid File nor Directory.");
        }
        return loadSubmissions(pathes, report);
    }

    public static List<Base> loadSubmissions(Collection<Path> submissionPaths, Report report) {
        List<Base> list = new ArrayList<>();
        for (Path p : submissionPaths) {
            try {
                Base sub = Base.fromPath(p);
                list.add(sub);

            } catch (IOException e) {
                report.add(new ResultStorage(p)
                        .setException(e)
                        .setValid(false));
            }
        }
        return list;
    }

    private static ResultStorage onlyBest(List<ResultStorage> storages) {
        storages.sort(ResultStorage.resultComperator());
        return storages.get(0);
    }

    public void loadRessources(Boolean verbose) throws IOException {
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
    }

    public boolean haveAllSameScheme(List<Base> subs) {
        assert Objects.nonNull(samples);
        assert !samples.isEmpty();
        int size = samples.size();
        if (size == 1) return true;
        Base base = subs.get(0);
        return base.sameSchema(samples.subList(1, size));
    }

    public List<Solution> createSolutions() throws SQLException {
        return Evaluator.createSolutions(config, resetScript, samples, source);
    }

    public Report runEvaluation(boolean verbose, boolean csvOnlyBest, SolutionMetadata metadata, List<Solution> sols) {
        Report report = new Report();
        report.setRootPath(submissionsPath);
        report.setSolutionMetadata(metadata);
        List<Base> subs = loadSubmissions(submissionsPath, report);
        runEachEvaluation(sols, source, resetScript, verbose, csvOnlyBest, report, subs);
        return report;
    }


}
