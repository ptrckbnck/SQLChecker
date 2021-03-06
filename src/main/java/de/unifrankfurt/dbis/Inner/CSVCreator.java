package de.unifrankfurt.dbis.Inner;


import de.unifrankfurt.dbis.SQL.SQLResultDiff;
import org.apache.commons.lang3.StringEscapeUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CSVCreator {

    private final Report report;
    private final ArrayList<Function<ResultStorage, String>> functions;
    private final ArrayList<String> header;

    public CSVCreator(Report report) {
        this.functions = new ArrayList<>();
        this.header = new ArrayList<>();
        this.report = report;
    }


    public String createCSVHeader() {
        return String.join(",", header);
    }


    public String create(ResultStorage resultStorage) {
        return functions.stream().map(x -> x.apply(resultStorage)).collect(Collectors.joining(","));
    }

    private void addFunction(Function<ResultStorage, String> f) {
        this.functions.add(f.andThen(StringEscapeUtils::escapeCsv));
    }

    public CSVCreator useSubmissionPath() {
        Path p = report.getRootPath();
        Function<ResultStorage, String> f = x -> {
            try {
                if (p.getRoot().getParent() == null) {
                    return (p.getRoot().relativize(x.getSubmissionPath()).toString());
                } else {
                    return (p.getRoot().getParent().relativize(p).toString());
                }
            } catch (Exception e) {
                return "unknown";
            }
        };
        this.addFunction(f);
        this.header.add("Path");
        return this;
    }


    public CSVCreator useAuthors() {
        Function<ResultStorage, String> f = x -> csvAuthors(x.getAuthors());
        this.addFunction(f);
        this.header.add("Authors");
        return this;
    }

    private String csvAuthors(List<Student> authors) {
        if (Objects.isNull(authors)) return "unknown";
        return authors.stream().map(Student::toString).collect(Collectors.joining("; "));
    }

    public CSVCreator useSolutionName() {
        Function<ResultStorage, String> f = x -> Objects.requireNonNullElse(x.getSolutionName(), "unknown");
        this.addFunction(f);
        this.header.add("Solution");
        return this;
    }

    public CSVCreator useAllStatus() {
        final List<String> tags = report.getSolutionMetadata().getTags();
        int size = tags.size();
        for (int i = 0; i < size; i++) {
            final int finalI = i;
            Function<ResultStorage, String> f = x -> {
                if (Objects.isNull(x.getDiff()) || size != x.getDiff().size()) {
                    return "";
                } else {
                    return x.getDiff().get(finalI).isOk().toString();
                }
            };
            this.addFunction(f);

            this.header.add(tags.get(i));
        }
        return this;
    }

    public CSVCreator useAllGroups() {
        final List<String> groups = report.getSolutionMetadata().getScoreGroup().getGroups();
        int size = groups.size();
        for (int i = 0; i < size; i++) {
            final int finalI = i;
            Function<ResultStorage, String> f = x -> {
                try {
                    return x.getScore().get(finalI).toString();
                } catch (Exception e) {
                    return "0";
                }
            };
            this.addFunction(f);

            this.header.add(groups.get(i));
        }
        return this;
    }

    public CSVCreator useSumScore() {
        Function<ResultStorage, String> f = x ->
        {
            try {
                return x.getSumScore().toString();
            } catch (Exception e) {
                return "0";
            }
        };
        this.addFunction(f);
        this.header.add("sum");
        return this;
    }



    public CSVCreator useErrorMsg() {
        Function<ResultStorage, String> f = x -> {
            Exception ex = x.getException();
            if (Objects.isNull(ex)) return "none";
            else return ex.getMessage();
        };
        this.addFunction(f);
        this.header.add("ErrorMsg");
        return this;
    }

    public CSVCreator useTaskCount() {
        Function<ResultStorage, String> f = x -> String.valueOf(this.report.getSolutionMetadata().getNonStaticTags().size());
        this.addFunction(f);
        this.header.add("#Tasks");
        return this;
    }

    public CSVCreator useSuccess() {
        Function<ResultStorage, String> f = x -> {
            List<SQLResultDiff> diff = x.getDiff();
            if (Objects.isNull(diff)) {
                return "";
            } else {
                return String.valueOf(x.getDiff().stream().map(SQLResultDiff::isOk).filter(i -> i).count());
            }
        };
        this.addFunction(f);
        this.header.add("#Success");
        return this;

    }

    public CSVCreator useRoot() {
        Function<ResultStorage, String> f = x -> {
            if (Objects.isNull(this.report.getRootPath())) {
                return "unknown";
            } else {
                return this.report.getRootPath().toString();
            }

        };
        this.addFunction(f);
        this.header.add("Root");
        return this;
    }

    public CSVCreator useEncoding() {
        Function<ResultStorage, String> f = x -> String.valueOf(x.getCharset());
        this.addFunction(f);
        this.header.add("Encoding");
        return this;
    }

    public CSVCreator useMatrikelNr() {
        Function<ResultStorage, String> f = x -> {
            try {
                return x.getAuthors().stream()
                        .map(Student::getMatriculationNumber)
                        .sorted()
                        .collect(Collectors.joining(";"));
            } catch (Exception e) {
                return "unknown";
            }
        };
        this.addFunction(f);
        this.header.add("MatrikelNr");
        return this;
    }


    public CSVCreator useName() {
        Function<ResultStorage, String> f = x -> {
            try {
                return x.getAuthors().stream()
                        .map(Student::getName)
                        .sorted()
                        .collect(Collectors.joining(";"));
            } catch (Exception e) {
                return "unknown";
            }
        };
        this.addFunction(f);
        this.header.add("Name");
        return this;
    }

    public CSVCreator useEmail() {
        Function<ResultStorage, String> f = x -> {
            try {
                return x.getAuthors().stream()
                        .map(Student::getEmailAddress)
                        .sorted()
                        .collect(Collectors.joining(";"));
            } catch (Exception e) {
                return "unknown";
            }
        };
        this.addFunction(f);
        this.header.add("Email");
        return this;
    }

    public CSVCreator useFeedback() {
        Function<ResultStorage, String> f = x -> {
            try {
                return x.getFeedback();
            } catch (Exception e) {
                return "unknown";
            }
        };
        this.addFunction(f);
        this.header.add("Feedback");
        return this;
    }

    public CSVCreator useSubmissionCompactPath() {
        Function<ResultStorage, String> f = x -> {
            try {
                Path subp = x.getSubmissionPath();
                if (subp.getParent() == null) {
                    return subp.getFileName().toString();
                } else {
                    return subp.getParent().getFileName().resolve(subp.getFileName()).toString();
                }
            } catch (Exception e) {
                return "unknown";
            }
        };
        this.addFunction(f);
        this.header.add("Path");
        return this;
    }

    public CSVCreator useMinimalFeedback() {
        Function<ResultStorage, String> f = x -> {
            try {
                return x.getMinimalFeedback();
            } catch (Exception e) {
                return "unknown";
            }
        };
        this.addFunction(f);
        this.header.add("Feedback");
        return this;
    }
}