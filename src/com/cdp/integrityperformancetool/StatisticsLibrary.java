package com.cdp.integrityperformancetool;

import java.util.Collection;
import java.util.HashMap;

/**
 * The StatisticsLibrary class is a container to hold one or more StatisticsCollection objects. A StatisticsLibrary is
 * created for each Integrity Server Statistics source file as output from the <i>executeStatisticsRetrieval</i> method,
 * and contains a StatisticsCollection object for each unique Group value existing in the source .csv data.
 *
 * The main feature of the StatisticsLibrary is the library attribute. This attribute is a HashMap storing String
 * group name as the key, and a StatisticsCollection object as the value. The data is meant to be organized
 * such that a StatisticsCollection object can be organized under a single group name within the Library,
 * and extracted for use by requesting the group name key.
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

    /**
     * Adds a StatisticsCollection object to the library if the group name does not already exist.
     * If a StatisticsCollection object already exists in the library with the same group name provided,
     * then the two StatitsticsColleciton objects are merged into one holding all data from both.
     * @param arg_statGroupName (String) - the group name of the StatisticsCollection being added.
     * @param arg_inputCollection (StatisticsCollection) - the StatisticsCollection object to add.
     */
    public void addToLibrary(String arg_statGroupName, StatisticsCollection arg_inputCollection){
        if(!this.containsGroupName(arg_statGroupName)) this.library.put(arg_statGroupName, arg_inputCollection);
        else{
            StatisticsCollection existing_sc = this.getStatisticsGroupName(arg_statGroupName);
            for(IntegrityStatisticBean isb : existing_sc.getCollection()){
                existing_sc.addToCollection(isb);
            }
        }
    }

    /**
     * Removes all StatisticsCollection Objects from the Library.
     */
    public void clearLibrary(){ this.library.clear();}

    /**
     * Determines if the String group name provided exists as a group within the library.
     * @param arg_statGroupName (String) - the group name to search for.
     * @return (boolean) - Returns 'true' if the group exists, 'false' if it does not.
     */
    public boolean containsGroupName(String arg_statGroupName){
        if(this.library.containsKey(arg_statGroupName)) return true;
        return false;
    }

    /**
     * Returns the string value of the StatisticsLibrary's name.
     * @return (String) - the name of the StatisticsLibrary.
     */
    public String getLibraryName(){ return this.libName; }

    /**
     * Returns a Collection of all StatisticsCollection objects in the library, regardless of group name.
     * @return (Collection(StatisticsCollection) - all StatisticsCollection objects in the library.
     */
    public Collection<StatisticsCollection> getAllValues(){ return this.library.values();}

    /**
     * Returns the StatisticsCollection object of the provided group name.
     * @param arg_name (String) - the group name to get out of the library.
     * @return (StatisticsCollection) - the StatisticsCollection object which is the key for the String key.
     */
    public StatisticsCollection getStatisticsGroupName(String arg_name){
        return this.library.get(arg_name);
    }

    /**
     * Removes the group name/StatisticsCollection pair from the library.
     * @param arg_statGroupName (String) - the group name to remove.
     */
    public void removeFromLibrary(String arg_statGroupName) {
        if (this.library.containsKey(arg_statGroupName)) {
            this.library.remove(arg_statGroupName);
        }
    }

    /**
     * Sets the StatisticsLibrary name to the value provided.
     * @param arg_name (String) - the name to give to the library.
     */
    public void setLibraryName(String arg_name){ this.libName = arg_name; }

}
