package com.cdp.integrityperformancetool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import com.cdp.integrityperformancetool.util.MergeSort;
import java.util.Comparator;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * Created by bryan on 8/7/2015.
 */
public class StatisticsCollection {

    private ArrayList<IntegrityStatisticBean> collection;
    private Long collectionAverageValue;
    private Long collectionCountValue;
    private ArrayList<IntegrityStatisticBean> collectionMaximumIsbObjectArrayList;
    private Long collectionMaximumValue;
    private ArrayList<IntegrityStatisticBean> collectionMinimumIsbObjectArrayList;
    private Long collectionMinimumValue;
    private String collectionName;
    private Long collectionTotalCountValue;
    private Boolean requiresStatisticsRecompute;//used to identify when it is recommended that all collection stats be
    											//recomputed to maintain accuracy of those instance fields.

    public StatisticsCollection(){
        this("New Integrity Statistics Collection", new ArrayList<IntegrityStatisticBean>());
    }

    public StatisticsCollection(String arg_collectionName){
        this(arg_collectionName, new ArrayList<IntegrityStatisticBean>());
    }

    public StatisticsCollection(String arg_collectionName, ArrayList<IntegrityStatisticBean> arg_collection){
        this.collection = arg_collection;
        this.collectionAverageValue = 0L;
        this.collectionCountValue = 0L;
        this.collectionMaximumIsbObjectArrayList = new ArrayList<IntegrityStatisticBean>();
        this.collectionMaximumValue = 0L;
        this.collectionMinimumIsbObjectArrayList = new ArrayList<IntegrityStatisticBean>();
        this.collectionMinimumValue = 0L;
        this.collectionName = arg_collectionName;
        this.collectionTotalCountValue = 0L;
        this.requiresStatisticsRecompute = false;

        //compute all statistics if the arg_collection is not empty.
        if (getCollectionSize()>0) computeAllCollectionStatistics();

    }

    public void addToCollection(IntegrityStatisticBean arg_isb){
    	this.collection.add(arg_isb);
    	this.requiresStatisticsRecompute = true;
    }

    private static Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {
        public int compare(String str1, String str2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
            if (res == 0) {
                res = str1.compareTo(str2);
            }
            return res;
        }
    };


    public void clearCollection(){ this.collection.clear(); }

    public void computeAllCollectionStatistics(){
    	//run all computation methods so that the collection has updated field values.
    	computeCollectionAverageValue();
    	computeCollectionCountValue();
    	computeCollectionMaximumValue();
    	computeCollectionMaximumObject();
    	computeCollectionMinimumValue();
    	computeCollectionMinimumObject();
    	computeCollectionTotalCountValue();
    	this.requiresStatisticsRecompute = false;
    }

    public void computeCollectionAverageValue(){
        Long cumulativeSum = 0L;
        for (IntegrityStatisticBean isb : this.collection){
            cumulativeSum = cumulativeSum + isb.getAverage();
        }
        this.collectionAverageValue = (cumulativeSum)/getCollectionSize();
    }

    public void computeCollectionCountValue(){
        Long cumulativeSum = 0L;
        for (IntegrityStatisticBean isb : this.collection){
            cumulativeSum = cumulativeSum + isb.getCount();
        }
        this.collectionCountValue = cumulativeSum;
    }

    public void computeCollectionMaximumObject(){
    	ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    	for (IntegrityStatisticBean isb : this.collection){
    		//use known max of collection to return the object(s) equal to this max.
    		if (isb.getMax()==this.collectionMaximumValue) temp_isb_arrayList.add(isb);
    	}
    	this.collectionMaximumIsbObjectArrayList = temp_isb_arrayList;
    }

    public void computeCollectionMaximumValue() {
        if (getCollectionSize()>1) {
            //retrieve all individual maximums into Long[]
            Long[] allMaxVals = new Long[getCollectionSize()];
            for (int i = 0; i < getCollectionSize(); i++) {
                allMaxVals[i] = this.collection.get(i).getMax();
            }

            //use the Mergesort algorithm to determine the maximum value
            MergeSort maxSorter = new MergeSort();
            maxSorter.sort(allMaxVals); //max values are now sorted lowest to highest
            this.collectionMaximumValue = allMaxVals[allMaxVals.length-1];

        } else if (getCollectionSize()==1){
            this.collectionMaximumValue = this.collection.get(0).getMax();
        } else {
        	//was called with an empty collection
            //**TODO: throw error or assign default value?
        }

    }

    public void computeCollectionMinimumObject(){
    	ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    	for (IntegrityStatisticBean isb : this.collection){
    		//use known min of collection to return the object(s) equal to this min.
    		if(isb.getMin()==this.collectionMinimumValue) temp_isb_arrayList.add(isb);
    	}
    	this.collectionMinimumIsbObjectArrayList = temp_isb_arrayList;
    }

    public void computeCollectionMinimumValue(){
    	 if (getCollectionSize()>1) {
             //retrieve all individual minimums into Long[]
             Long[] allMinVals = new Long[getCollectionSize()];
             for (int i = 0; i < getCollectionSize(); i++) {
                 allMinVals[i] = this.collection.get(i).getMax();
             }

             //use the Mergesort algorithm to determine the maximum value
             MergeSort maxSorter = new MergeSort();
             maxSorter.sort(allMinVals); //max values are now sorted lowest to highest
             this.collectionMaximumValue = allMinVals[allMinVals.length-1];

         } else if (getCollectionSize()==1){
             this.collectionMinimumValue = this.collection.get(0).getMin();
         } else {
        	 //was called with an empty collection...
             //**TODO: throw error or assign default value?
         }
    }

    public void computeCollectionTotalCountValue(){
    	 Long cumulativeSum = 0L;
         for (IntegrityStatisticBean isb : this.collection){
             cumulativeSum = cumulativeSum + isb.getTotalCount();
         }
         this.collectionTotalCountValue = cumulativeSum;
    }

    public ArrayList<IntegrityStatisticBean> getCollection(){ return this.collection; }

    public Long getCollectionAverageValue(){ return this.collectionAverageValue; }

    public Long getCollectionCountValue(){ return this.collectionCountValue; }

    public ArrayList<IntegrityStatisticBean> getCollectionMaximumIsbObject(){ return this.collectionMaximumIsbObjectArrayList; }

    public Long getCollectionMaximumValue(){ return this.collectionMaximumValue; }

    public ArrayList<IntegrityStatisticBean> getCollectionMinimumIsbObject(){ return this.collectionMinimumIsbObjectArrayList; }

    public Long getCollectionMinimumValue(){ return this.collectionMinimumValue; }

    public String getCollectionName(){ return this.collectionName; }

    public int getCollectionSize(){ return this.collection.size(); }

    public Long getCollectionTotalCountValue(){ return this.collectionTotalCountValue; }

    public Boolean getRequiresStatisticsRecompute(){ return this.requiresStatisticsRecompute; }

    public void orderByIsbAverageValue() {
    	Hashtable<Long, ArrayList<IntegrityStatisticBean>> avgVal_isb_hashtable = new Hashtable<Long, ArrayList<IntegrityStatisticBean>>();
    	Long[] allAvgVals = new Long[getCollectionSize()];
    	ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    	ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();

    	for (int i=0; i<getCollectionSize();i++){
    		IntegrityStatisticBean currIsb = this.collection.get(i);
    		Long currAvgVal = this.collection.get(i).getAverage();
    		allAvgVals[i] = currAvgVal;
    		if (avgVal_isb_hashtable.containsKey(currAvgVal)){
    			temp_isb_arrayList = avgVal_isb_hashtable.get(currAvgVal);
    			temp_isb_arrayList.add(currIsb);
    			avgVal_isb_hashtable.put(currAvgVal, temp_isb_arrayList);
    		} else {
    			temp_isb_arrayList.add(currIsb);
    			avgVal_isb_hashtable.put(currAvgVal, temp_isb_arrayList);
    		}
    		temp_isb_arrayList.clear();
    	}

    	 MergeSort avgValSorter = new MergeSort();
         avgValSorter.sort(allAvgVals); //avg values are now sorted lowest to highest
    	//iterate through array and place isbs into new ArrayList
         for (int j=0;j<getCollectionSize();j++){
        	 temp_isb_arrayList = avgVal_isb_hashtable.get(allAvgVals[j]);
        	 for (IntegrityStatisticBean isb : temp_isb_arrayList){
        		 ordered_isb_arrayList.add(isb);
        	 }
         }
         this.collection = ordered_isb_arrayList;
         //collection is now in order of objects from lowest to highest average value
    }

    public void orderByIsbCountValue() {
    	Hashtable<Long, ArrayList<IntegrityStatisticBean>> cntVal_isb_hashtable = new Hashtable<Long, ArrayList<IntegrityStatisticBean>>();
    	Long[] allCntVals = new Long[getCollectionSize()];
    	ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    	ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();

    	for (int i=0; i<getCollectionSize();i++){
    		IntegrityStatisticBean currIsb = this.collection.get(i);
    		Long currAvgVal = this.collection.get(i).getCount();
    		allCntVals[i] = currAvgVal;
    		if (cntVal_isb_hashtable.containsKey(currAvgVal)){
    			temp_isb_arrayList = cntVal_isb_hashtable.get(currAvgVal);
    			temp_isb_arrayList.add(currIsb);
    			cntVal_isb_hashtable.put(currAvgVal, temp_isb_arrayList);
    		} else {
    			temp_isb_arrayList.add(currIsb);
    			cntVal_isb_hashtable.put(currAvgVal, temp_isb_arrayList);
    		}
    		temp_isb_arrayList.clear();
    	}

    	 MergeSort cntValSorter = new MergeSort();
         cntValSorter.sort(allCntVals); //count values are now sorted lowest to highest
    	//iterate through array and place isbs into new ArrayList
         for (int j=0;j<getCollectionSize();j++){
        	 temp_isb_arrayList = cntVal_isb_hashtable.get(allCntVals[j]);
        	 for (IntegrityStatisticBean isb : temp_isb_arrayList){
        		 ordered_isb_arrayList.add(isb);
        	 }
         }
         this.collection = ordered_isb_arrayList;
         //collection is now in order of objects from lowest to highest count value
    }

    public void orderByIsbNameValue() {
    	Hashtable<String, ArrayList<IntegrityStatisticBean>> name_isb_hashtable = new Hashtable<String, ArrayList<IntegrityStatisticBean>>();
    	String[] allNames = new String[getCollectionSize()];
    	ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    	ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();

    	for (int i=0; i<getCollectionSize();i++){
    		IntegrityStatisticBean currIsb = this.collection.get(i);
    		String currName = this.collection.get(i).getName();
    		allNames[i] = currName;
    		if (name_isb_hashtable.containsKey(currName)){
    			temp_isb_arrayList = name_isb_hashtable.get(currName);
    			temp_isb_arrayList.add(currIsb);
    			name_isb_hashtable.put(currName, temp_isb_arrayList);
    		} else {
    			temp_isb_arrayList.add(currIsb);
    			name_isb_hashtable.put(currName, temp_isb_arrayList);
    		}
    		temp_isb_arrayList.clear();
    	}

    	Arrays.sort(allNames, ALPHABETICAL_ORDER); //TODO: check if this really works...
    	//names are now sorted alphabetically
    	//iterate through array and place isbs into new ArrayList
         for (int j=0;j<getCollectionSize();j++){
        	 temp_isb_arrayList = name_isb_hashtable.get(allNames[j]);
        	 for (IntegrityStatisticBean isb : temp_isb_arrayList){
        		 ordered_isb_arrayList.add(isb);
        	 }
         }
         this.collection = ordered_isb_arrayList;
         //collection is now in order of names according to alphabetical order
    }

    public void orderByIsbMaximumValue() {
    	Hashtable<Long, ArrayList<IntegrityStatisticBean>> maxVal_isb_hashtable = new Hashtable<Long, ArrayList<IntegrityStatisticBean>>();
    	Long[] allMaxVals = new Long[getCollectionSize()];
    	ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    	ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();

    	for (int i=0; i<getCollectionSize();i++){
    		IntegrityStatisticBean currIsb = this.collection.get(i);
    		Long currAvgVal = this.collection.get(i).getMax();
    		allMaxVals[i] = currAvgVal;
    		if (maxVal_isb_hashtable.containsKey(currAvgVal)){
    			temp_isb_arrayList = maxVal_isb_hashtable.get(currAvgVal);
    			temp_isb_arrayList.add(currIsb);
    			maxVal_isb_hashtable.put(currAvgVal, temp_isb_arrayList);
    		} else {
    			temp_isb_arrayList.add(currIsb);
    			maxVal_isb_hashtable.put(currAvgVal, temp_isb_arrayList);
    		}
    		temp_isb_arrayList.clear();
    	}

    	 MergeSort maxValSorter = new MergeSort();
         maxValSorter.sort(allMaxVals); //max values are now sorted lowest to highest
    	//iterate through array and place isbs into new ArrayList
         for (int j=0;j<getCollectionSize();j++){
        	 temp_isb_arrayList = maxVal_isb_hashtable.get(allMaxVals[j]);
        	 for (IntegrityStatisticBean isb : temp_isb_arrayList){
        		 ordered_isb_arrayList.add(isb);
        	 }
         }
         this.collection = ordered_isb_arrayList;
         //collection is now in order of objects from lowest to highest max value
    }

    public void orderByIsbMinimumValue() {
    	Hashtable<Long, ArrayList<IntegrityStatisticBean>> minVal_isb_hashtable = new Hashtable<Long, ArrayList<IntegrityStatisticBean>>();
    	Long[] allMinVals = new Long[getCollectionSize()];
    	ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    	ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();

    	for (int i=0; i<getCollectionSize();i++){
    		IntegrityStatisticBean currIsb = this.collection.get(i);
    		Long currAvgVal = this.collection.get(i).getMax();
    		allMinVals[i] = currAvgVal;
    		if (minVal_isb_hashtable.containsKey(currAvgVal)){
    			temp_isb_arrayList = minVal_isb_hashtable.get(currAvgVal);
    			temp_isb_arrayList.add(currIsb);
    			minVal_isb_hashtable.put(currAvgVal, temp_isb_arrayList);
    		} else {
    			temp_isb_arrayList.add(currIsb);
    			minVal_isb_hashtable.put(currAvgVal, temp_isb_arrayList);
    		}
    		temp_isb_arrayList.clear();
    	}

    	 MergeSort minValSorter = new MergeSort();
         minValSorter.sort(allMinVals); //min values are now sorted lowest to highest
    	//iterate through array and place isbs into new ArrayList
         for (int j=0;j<getCollectionSize();j++){
        	 temp_isb_arrayList = minVal_isb_hashtable.get(allMinVals[j]);
        	 for (IntegrityStatisticBean isb : temp_isb_arrayList){
        		 ordered_isb_arrayList.add(isb);
        	 }
         }
         this.collection = ordered_isb_arrayList;
         //collection is now in order of objects from lowest to highest min value
    }

    public void orderByIsbTotalCountValue() {
    	Hashtable<Long, ArrayList<IntegrityStatisticBean>> totCntVal_isb_hashtable = new Hashtable<Long, ArrayList<IntegrityStatisticBean>>();
    	Long[] allTotCntVals = new Long[getCollectionSize()];
    	ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    	ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();

    	for (int i=0; i<getCollectionSize();i++){
    		IntegrityStatisticBean currIsb = this.collection.get(i);
    		Long currAvgVal = this.collection.get(i).getMax();
    		allTotCntVals[i] = currAvgVal;
    		if (totCntVal_isb_hashtable.containsKey(currAvgVal)){
    			temp_isb_arrayList = totCntVal_isb_hashtable.get(currAvgVal);
    			temp_isb_arrayList.add(currIsb);
    			totCntVal_isb_hashtable.put(currAvgVal, temp_isb_arrayList);
    		} else {
    			temp_isb_arrayList.add(currIsb);
    			totCntVal_isb_hashtable.put(currAvgVal, temp_isb_arrayList);
    		}
    		temp_isb_arrayList.clear();
    	}
    	
    	 MergeSort totCntSorter = new MergeSort();
         totCntSorter.sort(allTotCntVals); //total count values are now sorted lowest to highest
    	//iterate through array and place isbs into new ArrayList
         for (int j=0;j<getCollectionSize();j++){
        	 temp_isb_arrayList = totCntVal_isb_hashtable.get(allTotCntVals[j]);
        	 for (IntegrityStatisticBean isb : temp_isb_arrayList){
        		 ordered_isb_arrayList.add(isb);
        	 }
         } 
         this.collection = ordered_isb_arrayList;
         //collection is now in order of objects from lowest to highest total count value
	}

    public void removeFromCollection(IntegrityStatisticBean arg_isb){ 
    	this.collection.remove(arg_isb); 
    	this.requiresStatisticsRecompute = true;
    }

    public void writeToFile(String arg_filePath) {
    	//write to file as CSV following the same format as the output Server Statistics .csv file
    	//cols: Start Date, End Date, Group, Statistic (Name), Kind, Unit, Count, total, Sum, Min, Max, Average, Mode
    	StringBuffer fileContents = new StringBuffer();
    	for(IntegrityStatisticBean isb : this.collection){
    		fileContents.append(isb.getStartDate() + ","
    				+ isb.getEndDate() + "," 
    				+ isb.getGroup() + ","
    				+ isb.getName() + ","
    				+ isb.getKind() + ","
    				+ isb.getUnit() + ","
    				+ isb.getCount() + ","
    				+ isb.getTotalCount() + ","
    				+ isb.getSum() + ","
    				+ isb.getUsed() + ","
    				+ isb.getMin() + ","
    				+ isb.getMax() + ","
    				+ isb.getAverage()  + ","
    				+ isb.getMode() + "\n");
    	}
    	
    	if (fileContents.toString().length()!=0){
	    	Charset charset = Charset.forName("US-ASCII");
	    	Path filePath = Paths.get(arg_filePath);
	    	
	    	try (BufferedWriter writer = Files.newBufferedWriter(filePath, charset)) {
	    	    writer.write(fileContents.toString(), 0, fileContents.toString().length());
	    	} catch (IOException x) {
	    	    System.out.format("IOException: %s%n", x);
	    	}
    	} else {

    		//fileContents is empty
    		//TODO: throw error or warn?
    	}
    }
    
    //TODO: implemented for testing...repurpose into usable function or remove from final product
    public void writeToString() {
    	//write to file as CSV following the same format as the output Server Statistics .csv file
    	//cols: Start Date, End Date, Group, Statistic (Name), Kind, Unit, Count, total, Sum, Min, Max, Average, Mode
    	StringBuffer fileContents = new StringBuffer();
    	fileContents.append("Start Date, End Date, Group, Statistic, Kind, Unit, Count, total, Sum, Used, Min, Max, Average, Mode"+"\n");
    	for(IntegrityStatisticBean isb : this.collection){
    		fileContents.append(isb.getStartDate() + ","
    				+ isb.getEndDate() + "," 
    				+ isb.getGroup() + ","
    				+ isb.getName() + ","
    				+ isb.getKind() + ","
    				+ isb.getUnit() + ","
    				+ isb.getCount() + ","
    				+ isb.getTotalCount() + ","
    				+ isb.getSum() + ","
    				+ isb.getUsed() + ","
    				+ isb.getMin() + ","
    				+ isb.getMax() + ","
    				+ isb.getAverage()  + ","
    				+ isb.getMode() + "\n");
    	}
    	System.out.println(fileContents.toString());
    }
}

