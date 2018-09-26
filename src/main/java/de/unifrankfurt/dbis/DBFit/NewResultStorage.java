package de.unifrankfurt.dbis.DBFit;

/**
 *
 */


import de.unifrankfurt.dbis.Evaluator;
import de.unifrankfurt.dbis.Submission.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Stores results as raw text and as csv
 *
 * @author Max Hofmann
 */
public class NewResultStorage {

    /**
     * Raw result text (html with annotations)
     */
    private String raw;

    /**
     * Path of the submission file corresponding to the results
     * stored in this class
     */
    private Submission submission;


    private Count count;

    /**
     * Log entry corresponding to the raw results
     */
    private String logEntry = "";

    private ArrayList<String> status;

    private Count staticCount;

    /**
     * Creates a ResultStorage object
     *
     * @param resultRaw The raw annotated html results for this submission
     */
    public NewResultStorage(Submission submission, String resultRaw, Count count) {
        this.status = new ArrayList<>();
        this.raw = resultRaw;
        this.submission = submission;
        this.count = count;
    }

    /**
     * @return The annotated raw text as html
     */
    public String getRawText() {
        return this.raw;
    }


    /**
     * Checks if the results indicate, that this submission is
     * completely correct
     *
     * @return True if (WRONG + IGNORED + ERRORS == 0)
     */
    public boolean isPassed() {
        return (count.getWrong() + count.getIgnored() + count.getExceptions() == 0);
    }

    public void setCount(Count count) {
        this.count = count;
    }

    /**
     * @return
     */
    public Count getCount() {
        return count;
    }



    @Override
    public String toString() {
        return "NewResultStorage{" +
                "raw='" + raw + '\'' +
                ", submission=" + submission.getName() +
                ", count=" + count +
                ", logEntry='" + logEntry + '\'' +
                '}';
    }

    public Submission getSubmission() {
        return this.submission;
    }


    public String createCSV(){
        List<String> status = getStatusList();
        Submission sub = this.submission;
        String path = (sub.getPath()==null)?"no path found":sub.getPath().toString();
        return csv(path, sub.getAuthors().toString(), status, getCount());
    }

    private List<String> getStatusList() {
        Document soup = Jsoup.parse(getRawText());
        Elements elements = soup.getElementsByTag("table");
        List<Element> tasks = filterTasks(elements);
        return getStatus(tasks);
    }

    private String csv(String path, String authors, List<String> status, Count count) {
        List<String> fullList= new ArrayList<>();
        fullList.add(path);
        fullList.add(authors);
        fullList.addAll(status);
        fullList.add(count.toString());
        return fullList.stream()
                .collect(Collectors.joining("\", \"",
                        "\"",
                        "\""));

    }

    public String createReport(Submission<TaskSQL> sample){
        List<String> status = getStatusList();
        List<String> tags = sample.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        Submission sub = this.submission;
        String path = (sub.getPath()==null)?"no path found":sub.getPath().toString();
        return report(path, sub.getAuthors().toString(), tags, status, getCount());
    }

    private String report(String path, String authors, List<String> tags, List<String> status, Count count) {
        if (tags.size()!=status.size()){
            return path+ ": tags-status mismatch!\"" +
                    "tags: "+tags+
                    "status"+status;
        }
        return IntStream.range(0,tags.size())
                .mapToObj((i)->tags.get(i)+":"+status.get(i))
                .collect(Collectors.joining(", ",
                        path + " "+authors+"[",
                        "]"+count.toString()));
    }


    private List<String> getStatus(List<Element> tasks) {
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
}
