package com.cdp.integrityperformancetool;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by bryan on 9/19/2015.
 */
public class StatisticsLibrary {

    private String libName;
    private HashMap<String, StatisticsCollection> library;

    public StatisticsLibrary(){
        this.libName = "Default Library Name";
        this.library = new HashMap<String, StatisticsCollection>();
    }

    public StatisticsLibrary(HashMap<String, StatisticsCollection> arg_library){
        this.libName = "Default Library Name";
        this.library = arg_library;
    }

    public StatisticsLibrary(String arg_libName){
        this.libName = arg_libName;
        this.library = new HashMap<String, StatisticsCollection>();
    }

    public StatisticsLibrary(String arg_libName, HashMap<String, StatisticsCollection> arg_library){
        this.libName = arg_libName;
        this.library = arg_library;
    }

    public void addToLibrary(String arg_statGroupName, StatisticsCollection arg_inputCollection){
        this.library.put(arg_statGroupName, arg_inputCollection);
    }

    public void clearLibrary(){ this.library.clear();}

    public boolean containsGroupName(String arg_statGroupName){
        if(this.library.containsKey(arg_statGroupName)) return true;
        return false;
    }

    public String getLibraryName(){ return this.libName; }

    public Collection<StatisticsCollection> getAllValues(){ return this.library.values();}

    public StatisticsCollection getStatisticsGroupName(String arg_name){
        return this.library.get(arg_name);
    }

    public void removeFromLibrary(String arg_statGroupName) {
        if (this.library.containsKey(arg_statGroupName)) {
            this.library.remove(arg_statGroupName);
        }
    }

    public void setLibraryName(String arg_name){ this.libName = arg_name; }

}
