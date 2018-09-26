package de.unifrankfurt.dbis.DBFit;

/**
 *
 */


import de.unifrankfurt.dbis.Submission.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Stores results as raw text and as csv
 *
 * @author Max Hofmann
 */
public class ResultStorage {

    /**
     * Raw result text (html with annotations)
     */
    private final String raw;

    private final Submission submission;

    private final Count count;

    private final Solution sol;

    private List<String> statusList = null;

    /**
     * Creates a ResultStorage object
     *
     * @param sol
     * @param resultRaw The raw annotated html results for this submission
     */
    public ResultStorage(Solution sol, Submission submission, String resultRaw, Count count) {
        this.sol = sol;
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
     * @return
     */
    public Count getCount() {
        return count;
    }


    @Override
    public String toString() {
        return "ResultStorage{" +
                "raw='" + raw + '\'' +
                ", submission=" + submission +
                ", count=" + count +
                ", sol=" + sol +
                '}';
    }

    public Submission getSubmission() {
        return this.submission;
    }


    public String createCSV(){
        List<String> status = getStatusList();
        Submission sub = this.submission;
        String path = (sub.getPath()==null)?"no path found":sub.getPath().toString();
        return csv(path, sub.getAuthors().toString(), this.sol.getName(),status, getCountPass());
    }

    public List<String> getStatusList() {
        if(this.statusList!=null) return this.statusList;
        Document soup = Jsoup.parse(getRawText());
        Elements elements = soup.getElementsByTag("table");
        List<Element> tasks = filterTasks(elements);
        return this.statusList = getStatus(tasks);
    }

    public int getCountPass(){
        return Collections.frequency(this.getStatusList(),"pass");
    }

    private String csv(String path, String authors, String solutionName, List<String> status, int countPass) {
        List<String> fullList= new ArrayList<>();
        fullList.add(path);
        fullList.add(authors);
        fullList.add(solutionName);
        fullList.addAll(status);
        fullList.add(String.valueOf(countPass));
        return fullList.stream()
                .collect(Collectors.joining("\", \"",
                        "\"",
                        "\""));

    }

    public String createReport(){
        List<String> status = getStatusList();
        List<String> tags = sol.getSubmission()
                .getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
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
        String prefix = "Path:" + path + " Authors:"+authors+" Solution:"+sol.getSubmission().getName()+" Evaluation:[";
        return IntStream.range(0,tags.size())
                .mapToObj((i)->tags.get(i)+":"+status.get(i))
                .collect(Collectors.joining(", ",
                        prefix,
                        "] DBFITCount:["+count.toString()))+"]";
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
