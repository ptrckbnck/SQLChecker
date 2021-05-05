package de.unifrankfurt.dbis.Inner;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Report {
    private List<ResultStorage> resultStorages;
    private Path rootPath;
    private SolutionMetadata solutionMetadata;

    public Report() {
        this.resultStorages = new ArrayList<>();
    }

    public void clearStorages() {
        this.resultStorages.clear();
    }

    public List<String> getCSV() {
        return getCSV(this.defaultCSVCreator());
    }

    public List<String> getCSV(CSVCreator csvCreator) {
        List<String> list = new ArrayList<>();
        list.add(csvCreator.createCSVHeader());
        this.resultStorages.forEach((x) -> list.add(x.csv(csvCreator)));
        return list;
    }

    public CSVCreator defaultCSVCreator() {
        return new CSVCreator(this).useSubmissionPath()
                .useName()
                .useEmail()
                .useMatrikelNr()
                .useSolutionName()
                .useAllStatus()
                .useAllGroups()
                .useSumScore()
                .useEncoding()
                .useErrorMsg()
                .useFeedback();
    }

    private CSVCreator compactCSVCreator() {
        return new CSVCreator(this).useSubmissionCompactPath()
                .useName()
                .useEmail()
                .useMatrikelNr()
                .useAllGroups()
                .useSumScore();
    }


    public void add(ResultStorage resultStorage) {
        this.resultStorages.add(resultStorage);
    }

    public void addAll(List<ResultStorage> curStorages) {
        this.resultStorages.addAll(curStorages);
    }

    public List<ResultStorage> getResultStorages() {
        return resultStorages;
    }

    public Path getRootPath() {
        return rootPath;
    }

    public Report setRootPath(Path rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public SolutionMetadata getSolutionMetadata() {
        return solutionMetadata;
    }

    public Report setSolutionMetadata(SolutionMetadata metaData) {
        this.solutionMetadata = metaData;
        return this;
    }

    public List<String> getCompactCSV() {
        class SubmissiopathSolutionGroup {
            final String submissionpath;
            final String solution;
            final String group;

            public SubmissiopathSolutionGroup(String submissionpath, String solution, String group) {
                this.submissionpath = submissionpath;
                this.solution = solution;
                this.group = group;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof SubmissiopathSolutionGroup)) return false;
                SubmissiopathSolutionGroup that = (SubmissiopathSolutionGroup) o;
                return Objects.equals(submissionpath, that.submissionpath) &&
                        Objects.equals(solution, that.solution) &&
                        Objects.equals(group, that.group);
            }

            @Override
            public int hashCode() {
                return Objects.hash(submissionpath, solution, group);
            }
        }

        List<String> groups = this.getSolutionMetadata().getScoreGroup().getGroups();

        HashMap<SubmissiopathSolutionGroup, Integer> ssgMap = new HashMap<>();
        for (ResultStorage resultStorage : resultStorages) {
            int i = 0;
            boolean is_invalid = Objects.isNull(resultStorage.getScore());
            for (String group : groups) {
                SubmissiopathSolutionGroup ssg = new SubmissiopathSolutionGroup(
                        resultStorage.getSubmissionPath().toString(),
                        resultStorage.getSolutionName(),
                        group
                );
                if (is_invalid) {
                    ssgMap.put(ssg, 0);
                } else {
                    ssgMap.put(ssg, resultStorage.getScore().get(i++));
                }

            }
        }


        List<String> allSubmissionPath = getAllSubmissionPath(this.resultStorages);
        List<String> allSolutionNames = getAllSolutionNames(this.resultStorages);
        Map<String, ResultStorage> baseStorages = new HashMap<>();
        resultStorages.stream()
                .filter(x -> x.getSolutionName().equals(allSolutionNames.get(0)))
                .forEach(x -> baseStorages.put(x.getSubmissionPath().toString(), x));

        List<ResultStorage> newResultStorages = new ArrayList<>();
        for (String path : allSubmissionPath) {
            List<Integer> max_values = new ArrayList<>();
            for (String group : groups) {
                List<Integer> list = new ArrayList<>();
                for (String solutionName : allSolutionNames) {
                    list.add(ssgMap.get(new SubmissiopathSolutionGroup(
                            path,
                            solutionName,
                            group)));
                }
                Integer max = Collections.max(list);
                max_values.add(max);
            }
            ResultStorage baseStorage = baseStorages.get(path);
            ResultStorage newResultStorage = new ResultStorage(baseStorage.getSubmissionPath());
            newResultStorage.setAuthors(baseStorage.getAuthors());
            newResultStorage.setCharset(baseStorage.getCharset());
            newResultStorage.setScore(max_values);
            newResultStorages.add(newResultStorage);
        }

        CSVCreator csvCreator = this.compactCSVCreator();
        List<String> list = new ArrayList<>();
        list.add(csvCreator.createCSVHeader());
        newResultStorages.forEach((x) -> list.add(x.csv(csvCreator)));

        System.out.println(allSubmissionPath);
        newResultStorages.forEach(System.out::println);
        return list;
    }


    private List<String> getAllSolutionNames(List<ResultStorage> resultStorages) {
        return resultStorages
                .stream()
                .map(ResultStorage::getSolutionName)
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    private List<String> getAllSubmissionPath(List<ResultStorage> resultStorages) {
        return resultStorages
                .stream()
                .map(ResultStorage::getSubmissionPath)
                .map(Path::toString)
                .distinct()
                .sorted()
                .collect(Collectors.toUnmodifiableList());
    }


    public List<String> getFullCSV() {
        CSVCreator csvCreator = new CSVCreator(this).useSubmissionPath()
                .useName()
                .useEmail()
                .useMatrikelNr()
                .useSolutionName()
                .useAllStatus()
                .useAllGroups()
                .useSumScore()
                .useEncoding()
                .useErrorMsg();

        List<String> list = new ArrayList<>();
        list.add(csvCreator.createCSVHeader());
        this.resultStorages.forEach((x) -> list.add(x.csv(csvCreator)));
        return list;
    }

    public List<String> getFeedbackCSV() {
        CSVCreator csvCreator = new CSVCreator(this)
                .useSubmissionCompactPath()
                .useName()
                .useEmail()
                .useMatrikelNr()
                .useSolutionName()
                .useMinimalFeedback();

        List<String> list = new ArrayList<>();
        list.add(csvCreator.createCSVHeader());
        this.resultStorages.forEach((x) -> list.add(x.csv(csvCreator)));
        return list;
    }
}
