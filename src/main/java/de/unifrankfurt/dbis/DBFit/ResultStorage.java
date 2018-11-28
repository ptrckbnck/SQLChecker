package de.unifrankfurt.dbis.DBFit;

/**
 *
 */

import de.unifrankfurt.dbis.Submission.Solution;
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
    private final String authors;
    private final String solutionName;
    private final List<String> status;
    private final String errorMsg;
    private final int taskCount;
    private final Path root;
    private final Charset charset;

    //TODO replace authors string with authors object
    private ResultStorage(Path root, Path submissionPath, String authors, String solutionName, int taskCount, List<String> status, String errorMSG, Charset charset) {
        this.root = root;
        this.submissionPath = submissionPath;
        this.authors = Objects.requireNonNullElse(authors, "unknown authors");
        this.solutionName = Objects.requireNonNullElse(solutionName, "unknown solution");
        this.taskCount = taskCount;
        this.charset = charset;
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
                String.valueOf(submission.getAuthors()),
                sol.getName(),
                sol.getSubmission().getTags().size(),
                ResultStorage.getStatusList(resultRaw),
                null, submission.getCharset());
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
                String.valueOf(submission.getAuthors()),
                sol.getName(),
                sol.getSubmission().getTags().size(),
                null,
                exception.toString(), submission.getCharset());
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
                String.valueOf(submission.getAuthors()),
                null,
                taskCount,
                null,
                exception.toString(),
                submission.getCharset());
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
                null);
    }

    public Path getSubmissionPath() {
        return submissionPath;
    }

    public String getAuthors() {
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

//
//    private String csv(String path, String authors, String solutionName, List<String> status, int countPass, String errorMsg) {
//        List<String> fullList = new ArrayList<>();
//        fullList.add(path);
//        fullList.add(authors);
//        fullList.add(solutionName);
//        fullList.addAll(status);
//        fullList.add(String.valueOf(countPass));
//        fullList.add(errorMsg);
//        return fullList.stream()
//                .collect(Collectors.joining("\", \"",
//                        "\"",
//                        "\""));
//
//    }

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

//    public String createCSV() {
//        List<String> statusTemp;
//        if (this.taskCount != this.status.size()) {
//            statusTemp = Arrays.asList(new String[taskCount]);
//        } else {
//            statusTemp = status;
//        }
//        String path;
//        if (Objects.isNull(submissionPath)) {
//            path = "unknown";
//        } else {
//            path = (root.getParent().relativize(submissionPath).toString());
//        }
//        return csv(path, authors, solutionName, statusTemp, getCountPass(status), errorMsg);
//
//    }

    public int getCountPass(List<String> status) {
        return Collections.frequency(status, "pass");
    }

    public Charset getEncoding() {
        return this.charset;
    }

    public String createReport(List<String> tags) {
        return report(this.submissionPath, authors, tags, status);
    }

    private String report(Path path, String authors, List<String> tags, List<String> status) {
        String prefix = "SUCCESS Path:" + path + " Authors:" + authors + " Solution:" + solutionName + " Evaluation:[";
        return IntStream.range(0, tags.size())
                .mapToObj((i) -> tags.get(i) + ":" + status.get(i))
                .collect(Collectors.joining(", ",
                        prefix,
                        "]"));
    }

}