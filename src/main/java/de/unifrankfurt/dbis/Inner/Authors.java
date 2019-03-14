package de.unifrankfurt.dbis.Inner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Authors extends ArrayList<Student> {

    public Authors(List<Student> authors) {
        super(authors);
    }

    public Authors() {
        super();
    }

    public List<List<String>> toStringList() {
        return this.stream().map(x -> x.toStringList()).collect(Collectors.toList());
    }
}
