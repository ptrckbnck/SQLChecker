package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.DBFit.ResultStorage;

import java.util.ArrayList;
import java.util.List;

public class Report {
    private final List<ResultStorage> resultStorages;
    private final String csvHeader;

    public Report(List<ResultStorage> resultStorages, String csvHeader) {
        this.resultStorages = resultStorages;
        this.csvHeader = csvHeader;
    }

    public List<String> getCSV() {
        List<String> list = new ArrayList<>(List.of(csvHeader));
        this.resultStorages.forEach((x) -> list.add(x.createCSV()));
        return list;
    }
}
