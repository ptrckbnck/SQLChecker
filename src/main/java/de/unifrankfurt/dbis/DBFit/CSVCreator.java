package de.unifrankfurt.dbis.DBFit;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CSVCreator {

    private ArrayList<Function<ResultStorage, String>> functions;
    private ArrayList<String> header;

    public CSVCreator() {
        functions = new ArrayList<>();
        header = new ArrayList<>();
    }


    public String createCSVHeader() {
        return String.join(",", header);
    }


    public String create(ResultStorage resultStorage) {
        return functions.stream().map(x -> x.apply(resultStorage)).collect(Collectors.joining(","));
    }

    public CSVCreator useSubmissionPath() {
        Function<ResultStorage, String> f = x -> {
            Path p = x.getSubmissionPath();
            if (Objects.isNull(p)) {
                return "unknown";
            } else {
                return (x.getRoot().getParent().relativize(p).toString());
            }
        };
        this.functions.add(f);
        this.header.add("Path");
        return this;
    }

    public CSVCreator useAuthors() {
        Function<ResultStorage, String> f = x -> Objects.requireNonNullElse(x.getAuthors(), "unknown");
        this.functions.add(f);
        this.header.add("Authors");
        return this;
    }

    public CSVCreator useSolutionName() {
        Function<ResultStorage, String> f = x -> Objects.requireNonNullElse(x.getSolutionName(), "unknown");
        this.functions.add(f);
        this.header.add("Solution");
        return this;
    }

    public CSVCreator useAllStatus(List<String> header) {
        int size = header.size();
        for (int i = 0; i < size; i++) {
            final int finalI = i;
            Function<ResultStorage, String> f = x -> {
                if (size != x.getStatus().size()) {
                    return "";
                } else {
                    return x.getStatus().get(finalI);
                }
            };
            this.functions.add(f);
            this.header.add(header.get(i));
        }
        return this;
    }

    public CSVCreator useErrorMsg() {
        Function<ResultStorage, String> f = x -> Objects.requireNonNullElse(x.getErrorMsg(), "unknown");
        this.functions.add(f);
        this.header.add("ErrorMsg");
        return this;
    }

    public CSVCreator useTaskCount() {
        Function<ResultStorage, String> f = x -> String.valueOf(Objects.requireNonNullElse(x.getTaskCount(), "unknown"));
        this.functions.add(f);
        this.header.add("#Tasks");
        return this;
    }

    public CSVCreator useSuccess() {
        Function<ResultStorage, String> f = x -> String.valueOf(Collections.frequency(x.getStatus(), "pass"));
        this.functions.add(f);
        this.header.add("#Success");
        return this;

    }

    public CSVCreator useRoot() {
        Function<ResultStorage, String> f = x -> {
            if (Objects.isNull(x.getRoot())) {
                return "unknown";
            } else {
                return x.getRoot().toString();
            }

        };
        this.functions.add(f);
        this.header.add("Root");
        return this;
    }

    public CSVCreator useEncoding() {
        Function<ResultStorage, String> f = x -> String.valueOf(x.getEncoding());
        this.functions.add(f);
        this.header.add("Encoding");
        return this;
    }

}