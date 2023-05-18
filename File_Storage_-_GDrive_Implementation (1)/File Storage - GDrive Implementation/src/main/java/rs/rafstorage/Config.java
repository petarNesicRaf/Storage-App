package rs.rafstorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.MAX_VALUE;

public class Config {

    private long maxSize;
    private List<String> extensions;
    private HashMap<String, Integer> maxFiles;

    public Config() {
        maxSize = 1024;
        extensions = new ArrayList<>();
        maxFiles = new HashMap<>();
    }

    public Config(long maxSize) {
        this.maxSize = maxSize;
        extensions = new ArrayList<>();
        maxFiles = new HashMap<>();
    }

    public Config(List<String> extensions) {
        maxSize = 1024;
        this.extensions = extensions;
        maxFiles = new HashMap<>();
    }

    public Config(long maxSize, List<String> extensions) {
        this.maxSize = maxSize;
        this.extensions = extensions;
        maxFiles = new HashMap<>();
    }

    public long getMaxSize() {
        return maxSize;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public void addMaxFile(String path, int number){
        maxFiles.put(path, number);
    }

    public int getMaxFile(String path){
        if (maxFiles.containsKey(path))
            return maxFiles.get(path);
        return MAX_VALUE;
    }
}