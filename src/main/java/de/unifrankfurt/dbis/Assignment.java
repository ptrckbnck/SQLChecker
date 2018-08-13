package de.unifrankfurt.dbis;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;

public class Assignment {

    private String name;
    private final String[] tasks;
    private HashMap<String, String> codeMap;

    public Assignment(String name, String... tasks){
        this.name = name;
        this.tasks = tasks;
        this.codeMap = new HashMap<>();
        Arrays.stream(tasks).forEach(x->codeMap.put(x,""));
    }

    public String[] getTasks() {
        return tasks;
    }

    public HashMap<String, String> getCodeMap() {
        return codeMap;
    }

    public void putCodeMap(String task, String code) {
        this.codeMap.put(task, code);
    }

}
