package de.unifrankfurt.dbis;

import de.unifrankfurt.dbis.DBFit.NewResultStorage;
import de.unifrankfurt.dbis.Submission.*;
import de.unifrankfurt.dbis.config.DataSource;
import de.unifrankfurt.dbis.config.EvalConfig;
import fit.exception.FitParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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

    public void runEvaluationVerbose() {
        List<Submission<TaskSQL>> subs = loadSubmissions(submissions);

        subs.forEach((sub)-> {
            try {
                System.out.println("EVALUATION: "+sub.getAuthors());
                NewResultStorage evaluate = sol.evaluate(source, resetScript, sub);
                System.out.println(evaluate.getRawText());
                System.out.println(evaluate.getCount());
                System.out.println(createReport(evaluate,sub));
            } catch (SQLException | FitParseException e) {
                System.out.println(sub.getAuthors()+"[Failed:"+e.getMessage()+"]");
            }
        });
    }


    public void runEvaluation(){
        /*XConfig config  = new XConfigBuilder().setDatabase("simple")
                .setHostname("localhost")
                .setPassword("simple")
                .setUsername("simple")
                .setResetPath("/home/xyntek/simple/reset_simple.txt")
                .createConfig();
        SQLScript r = config.getResetScript();
        DataSource dataSource = config.getDataSource();
        r.execute(dataSource);

        Submission<TaskSQL> submission = Submission.fromPath(Paths.get("/home/xyntek/simple/submission/sample_with_author.sql")).onlyTaskSQLSubmission();
        Solution sol = submission.generateSolution(config.getDataSource());*/
        List<Submission<TaskSQL>> subs = loadSubmissions(submissions);

        subs.forEach((sub)-> {
            try {
                System.out.println(createReport(sol.evaluate(source, resetScript, sub),sub));
            } catch (SQLException | FitParseException e) {
                System.out.println(sub.getAuthors()+"[Failed:"+e.getMessage()+"]");
            }
        });

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

    private String createReport(NewResultStorage storage, Submission<TaskSQL> sub){
        Document soup = Jsoup.parse(storage.getRawText());
        Elements elements = soup.getElementsByTag("table");
        List<Element> tasks = filterTasks(elements);
        List<String> status = getStatus(tasks);
        List<String> tags = sample.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        String path = (sub.getPath()==null)?"no path found":sub.getPath().toString();
        return report(path, sub.getAuthors().toString(), tags, status, storage.getCount());
    }


    private String report(String path, String authors, List<String> tags, List<String> status, Count count) {
        if (tags.size()!=status.size()) return "tags-status mismatch";
        return IntStream.range(0,tags.size())
                .mapToObj((i)->tags.get(i)+":"+status.get(i))
                .collect(Collectors.joining(", ",
                        path + " "+authors+"[",
                        "]"+count.toString()));
    }
    private List<String> getStatus(List<Element> tasks) {
        List<String> statusList = new ArrayList<>();
        for (Element e : tasks){
            Elements tds = e.getElementsByTag("td");
            for(Element td : tds) {
                if (td.hasAttr("class")){
                    String c = td.attr("class");
                    if (c.equals("class=\"error\"")) {
                        statusList.add("error");
                        break;
                    }
                    if (c.equals(("class=\"fail\""))){
                        statusList.add("fail");
                        break;
                    }
                    if (c.equals(("class=\"ignore\""))){
                        statusList.add("fail");
                        break;
                    }
                }
            }
            statusList.add("pass");
        }
        return statusList;
    }

    /**
     * filsters alls Nodes who do not contain tasks.
     * @param elements
     * @return
     */
    private List<Element> filterTasks(List<Element> elements) {
        List<Element> tasks = new ArrayList<>();
        for( Element e : elements){
            Elements tds = e.getElementsByTag("td");
            boolean b = true;
            for(Element td : tds) {
                if (td.hasText() &&
                        (td.text().equals("dbfit.MySqlTest") || td.text().equals("Connect"))) {
                    b = false;
                    break;
                }
            }
            if (b) tasks.add(e);
        }
        return tasks;
    }

    public Boolean configOK() {
        return this.config.configOK();
    }


}
