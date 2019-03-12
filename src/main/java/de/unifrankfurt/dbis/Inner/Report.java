package de.unifrankfurt.dbis.Inner;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
                .useAuthors()
                .useMatrikelNr()
                .useSolutionName()
                .useAllStatus()
                .useSuccess()
                .useEncoding()
                .useErrorMsg();
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
}
