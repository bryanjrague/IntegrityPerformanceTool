package com.xerox.integrityperformancetool.util;

import java.util.HashMap;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.xerox.integrityperformancetool.*;

/**
 * Created by bryan on 8/7/2015.
 */
public class StatisticsFileReader {

    private String str_filePath;
    private static Path path_filePath;

    public StatisticsFileReader(String arg_filePath){
        this.str_filePath  = arg_filePath;
        this.path_filePath = Paths.get(URI.create(arg_filePath));
    }

    public HashMap<String, StatisticsCollection> executeStatisticsRetrieval(){
        return new HashMap();
    }

    public String getStringFilePath(){
        return this.str_filePath;
    }



    public void setFilePath(String arg_filePath){
        this.str_filePath = arg_filePath;
    }
}
