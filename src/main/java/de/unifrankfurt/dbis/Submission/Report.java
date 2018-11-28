package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.DBFit.CSVCreator;
import de.unifrankfurt.dbis.DBFit.ResultStorage;

import java.util.ArrayList;
import java.util.List;

public class Report {
    private final List<ResultStorage> resultStorages;
    private final CSVCreator csvCreator;

    public Report(List<ResultStorage> resultStorages, CSVCreator csvCreator) {
        this.resultStorages = resultStorages;
        this.csvCreator = csvCreator;
    }


    public List<String> getCSV() {
        List<String> list = new ArrayList<>();
        list.add(csvCreator.createCSVHeader());
        this.resultStorages.forEach((x) -> list.add(x.csv(csvCreator)));
        return list;
    }
}
