package de.unifrankfurt.dbis.DBFit;

/**
 *
 */

import de.unifrankfurt.dbis.SQL.SQLResultTable;
import de.unifrankfurt.dbis.Submission.Solution;
import de.unifrankfurt.dbis.Submission.Student;
import de.unifrankfurt.dbis.Submission.Submission;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ResultStorage {
    private final Path submissionPath;
    private final List<Student> authors;
    private final String solutionName;
    private final List<String> status;
    private final String errorMsg;
    private final int taskCount;
    private final Path root;
    private final Charset charset;
    private final String resultRaw;


    private ResultStorage(Path root, Path submissionPath, List<Student> authors, String solutionName, int taskCount, List<String> status, String errorMSG, Charset charset, String resultRaw) {
        this.root = root;
        this.submissionPath = submissionPath;
        this.authors = Objects.requireNonNullElse(authors, List.of());
        this.solutionName = Objects.requireNonNullElse(solutionName, "unknown solution");
        this.taskCount = taskCount;
        this.charset = charset;
        this.resultRaw = Objects.requireNonNullElse(resultRaw, "");
        this.status = Objects.requireNonNullElse(status, Arrays.asList(new String[taskCount]));
        this.errorMsg = Objects.requireNonNullElse(errorMSG, "");
    }

    /**
     * normal resultStorage if everything went fine.
     * @param sol
     * @param submission
     * @param resultRaw
     */
    public ResultStorage(Path root, Solution sol, Submission submission, String resultRaw) {
        this(root,
                submission.getPath(),
                submission.getAuthors(),
                sol.getName(),
                sol.getSubmission().getTags().size(),
                ResultStorage.getStatusList(resultRaw),
                null, submission.getCharset(), resultRaw);
    }

    /**
     * resultstorages if somethign went wrong during dbfit
     * @param sol
     * @param submission
     * @param exception
     */
    public ResultStorage(Path root, Solution sol, Submission submission, Exception exception) {
        this(root,
                submission.getPath(),
                submission.getAuthors(),
                sol.getName(),
                sol.getSubmission().getTags().size(),
                null,
                exception.toString(), submission.getCharset(), null);
    }

    /**
     * result Storages if submission has wrong scheme
     * @param taskCount
     * @param submission
     * @param exception
     */
    public ResultStorage(Path root, int taskCount, Submission submission, Exception exception) {
        this(root,
                submission.getPath(),
                submission.getAuthors(),
                null,
                taskCount,
                null,
                exception.toString(),
                submission.getCharset(),
                null);
    }

    /**
     * result Storages if submission could not be loaded
     *
     * @param taskCount
     * @param submission
     * @param exception
     */
    public ResultStorage(Path root, int taskCount, Path submission, Exception exception) {
        this(root,
                submission,
                null,
                null,
                taskCount,
                null,
                exception.toString(),
                null,
                null);
    }

    public Path getSubmissionPath() {
        return submissionPath;
    }

    public List<Student> getAuthors() {
        return authors;
    }

    public String getSolutionName() {
        return solutionName;
    }

    public List<String> getStatus() {
        return status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public Path getRoot() {
        return root;
    }

    public static String generateReadableResult(String resultRaw) {
        Document soup = Jsoup.parse(resultRaw);
        Elements tables = soup.getElementsByTag("table");
        StringBuilder builder = new StringBuilder();
        List<Element> tableList = new ArrayList<>(tables);
        for (Element table : tableList.subList(2, tableList.size())) {
            Elements rows = table.getElementsByTag("tr");
            List<Element> rowList = new ArrayList<>(rows);

            Element type = rowList.get(0);
            for (Element element : type.getElementsByTag("td")) {
                builder.append(element.html()).append("\n");
            }

            if (rowList.size() == 1) {
                builder.append("Empty Table\n\n");
                continue;
            }

            List<String> header = new ArrayList<>();
            for (Element attr : rowList.get(1).getElementsByTag("td")) {
                header.add(attr.text());
            }
            List<List<String>> list = new ArrayList<>();
            for (Element row : rowList.subList(2, rowList.size())) {
                Elements cells = row.getElementsByTag("td");
                List<String> listt = new ArrayList<>();
                for (Element element : cells) {
                    listt.add(element.html());
                }
                for (int i = listt.size(); i < header.size(); i++) {
                    listt.add("<threshold reached>");
                }
                list.add(listt);
            }

            SQLResultTable resultTable = new SQLResultTable(header, list);
            builder.append(resultTable.toString()).append("\n");


        }
        return builder.toString();
    }

    public Charset getCharset() {
        return charset;
    }

    public String getResultRaw() {
        return resultRaw;
    }

    private static List<String> getStatusList(String rawHtml) {
        Document soup = Jsoup.parse(rawHtml);
        Elements elements = soup.getElementsByTag("table");
        List<Element> tasks = filterTasks(elements);
        return getStatus(tasks);
    }

    private static List<String> getStatus(List<Element> tasks) {
        List<String> statusList = new ArrayList<>();
        for (Element e : tasks){
            Elements elements = e.getElementsByAttribute("class");
            String curStatus = "pass";
            for(Element element: elements) {
                String c = element.attr("class");
                if (c.equals("error")) {
                    curStatus="error";
                    break;
                }
                if (c.equals(("fail"))){
                    curStatus="fail";
                    break;
                }
                if (c.equals("ignore")){
                    curStatus="ignore";
                    break;
                }
            }
            statusList.add(curStatus);

        }
        return statusList;
    }

    /**
     * filsters alls Nodes who do not contain tasks.
     * @param elements
     * @return
     */
    private static List<Element> filterTasks(List<Element> elements) {
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

    public String csv(CSVCreator csvc) {
        return csvc.create(this);
    }



    /**
     * method for comparing results of evaluation. prefers results with more passes. Modify if needed.
     *
     * @return
     */
    public static Comparator<? super ResultStorage> resultComperator() {
        return (x, y) -> {
            int xs = x.getCountPass(x.status);
            int ys = y.getCountPass(y.status);
            if (xs > ys) {
                return -1;
            }
            if (xs < ys) {
                return 1;
            }
            return 0;
        };

    }


    public int getCountPass(List<String> status) {
        return Collections.frequency(status, "pass");
    }

    public Charset getEncoding() {
        return this.charset;
    }

    public String createReport(List<String> tags) {
        return report(this.submissionPath, String.valueOf(authors), tags, status);
    }

    private String report(Path path, String authors, List<String> tags, List<String> status) {
        String prefix = "SUCCESS Path:" + path + " Authors:" + authors + " Solution:" + solutionName + " Evaluation:[";
        return IntStream.range(0, tags.size())
                .mapToObj((i) -> tags.get(i) + ":" + status.get(i))
                .collect(Collectors.joining(", ",
                        prefix,
                        "]"));
    }

    public String getReadableResult() {
        return ResultStorage.generateReadableResult(resultRaw);
    }
}