package rs.rafstorage.local_storage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class LocalStorage {

    private String rootPath;

    private long maxSize = 1000000;
    private int maxFiles = 5;
    private List<String> extensions = new ArrayList<>();

    private String figPathForJSON="";

    private Map<String, Integer> numFileMap = new HashMap<>();


    private Map<String, String> pathMap = new HashMap<>();

    @JsonIgnore
    private ObjectMapper objectMapper = new ObjectMapper();

    private String rootName;

    public LocalStorage()
    {

    }

    public LocalStorage(String rootPath)
    {
        File root = new File(rootPath);

        String p = parsePath(rootPath);
        if(!root.exists())
        {
            this.rootPath= rootPath;
            this.figPathForJSON = rootPath + File.separator + "config.json";
            createRootStorage();
            this.numFileMap.put(rootPath, maxFiles);
            this.pathMap.put(rootName,p);
            rewriteConfig();
        }

    }

    private String parsePath(String rootPath)
    {
        String[] strings = rootPath.split("\\\\");
        String rootName = strings[strings.length-1];
        this.setRootName(rootName);
        String[] arr = Arrays.copyOf(strings, strings.length-1);

        String temp="";
        for(int i = 0; i<arr.length; i++)
        {
            if(i == arr.length-1)
                temp += arr[i];
            else
                temp += arr[i] + "\\";
        }
        return temp;
    }

    public void createRootStorage() {

        File rootDir = new File(this.rootPath);
        rootDir.mkdir();
    }
    public void rewriteConfig()
    {
        try {
            objectMapper.writeValue(new File(this.figPathForJSON), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getRootPath() {
        return rootPath;
    }


    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }


    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public int getMaxFiles() {
        return maxFiles;
    }

    public void setMaxFiles(int maxFiles) {
        this.maxFiles = maxFiles;
    }


    public List<String> getExtensions() {
        return extensions;
    }


    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }

    public String getFigPathForJSON() {
        return figPathForJSON;
    }

    public void setFigPathForJSON(String figPathForJSON) {
        this.figPathForJSON = figPathForJSON;
    }

    public Map<String, Integer> getNumFileMap() {
        return numFileMap;
    }

    public void setNumFileMap(Map<String, Integer> numFileMap) {
        this.numFileMap = numFileMap;
    }

    public Map<String, String> getPathMap() {
        return pathMap;
    }

    public void setPathMap(Map<String, String> pathMap) {
        this.pathMap = pathMap;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setRootName(String rootName) {
        this.rootName = rootName;
    }

    public String getRootName() {
        return rootName;
    }
}
