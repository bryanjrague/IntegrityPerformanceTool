package com.cdp.integrityperformancetool;

import java.util.*;

import com.cdp.integrityperformancetool.util.MergeSort;
import org.joda.time.DateTime;

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
    
    //The following boolean fields keep track of what Instance fields are up to date
    //if an Isb is added to/removed from the collection, then computations will need
    //to be redone before returning values.
    private boolean requireAvgValRecompute = false;
    private boolean requireCntValRecompute = false;
    private boolean requireMaxObjRecompute = false;
    private boolean requireMaxValRecompute = false;
    private boolean requireMinObjRecompute = false;
    private boolean requireMinValRecompute = false;
    private boolean requireTotCntValRecompute = false;
    
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

        //compute all statistics if the arg_collection is not empty.
        if (getCollectionSize()>0) computeAllCollectionStatistics();

    }

    public void addToCollection(IntegrityStatisticBean arg_isb){
    	this.collection.add(arg_isb);
    	this.requireAvgValRecompute = true;
    	this.requireCntValRecompute = true;
    	this.requireMaxObjRecompute = true;
    	this.requireMaxValRecompute = true;
    	this.requireMinObjRecompute = true;
    	this.requireMinValRecompute = true;
    	this.requireTotCntValRecompute = true;
    }
    
    public void addToCollection(ArrayList<IntegrityStatisticBean> arg_isb_arrayList){ 
		for(IntegrityStatisticBean isb : arg_isb_arrayList){
			this.collection.add(isb); 
		}
		this.requireAvgValRecompute = true;
    	this.requireCntValRecompute = true;
    	this.requireMaxObjRecompute = true;
    	this.requireMaxValRecompute = true;
    	this.requireMinObjRecompute = true;
    	this.requireMinValRecompute = true;
    	this.requireTotCntValRecompute = true;
	}
    
    public boolean areComputationFieldsAccurate(){
    	if(!requireAvgValRecompute &&
    	   !requireCntValRecompute &&
    	   !requireMaxObjRecompute &&
    	   !requireMaxValRecompute &&
    	   !requireMinObjRecompute &&
    	   !requireMinValRecompute && 
    	   !requireTotCntValRecompute) return true;
    	return false;
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

	public void collapseStatistic(String arg_statName, String arg_statGroup){
		//this method identifies all statistics that have identical names and groups.
		//for each, the statistics are combined into a single statistic in the collection
		//deleting all the other instances. the minimum start date and maximum end date
		//are stored for this collapsed statistic

		StatisticsCollection temp_collection = new StatisticsCollection();
		Long cumulative_count = 0L;
		Long cumulative_totalCount = 0L;
		Long cumulative_sum = 0L;
		ArrayList<IntegrityStatisticBean> remove_isbs = new ArrayList<IntegrityStatisticBean>();
		int counter = 1;
		DateTime startDate = new DateTime();
		DateTime endDate = new DateTime();


		for(IntegrityStatisticBean isb : this.collection){
			if (isb.getName().equals(arg_statName) && isb.getGroup().equals(arg_statGroup)){
				temp_collection.addToCollection(isb);
				cumulative_count = cumulative_count + isb.getCount();
				cumulative_totalCount = cumulative_totalCount + isb.getTotalCount();
				cumulative_sum = cumulative_sum + isb.getSum();
				remove_isbs.add(isb);

				if(counter==1){
					startDate = isb.getStartDate();
					endDate = isb.getEndDate();
				} else{
					if(isb.getStartDate().getMillis()<startDate.getMillis()){
						startDate = isb.getStartDate();
					}
					if(isb.getEndDate().getMillis()>endDate.getMillis()){
						endDate = isb.getEndDate();
					}

				}
				counter++;
			}
		}
		temp_collection.writeToString();
		temp_collection.computeAllCollectionStatistics();
		IntegrityStatisticBean collapsedStatistic = temp_collection.getCollectionMaximumIsbArrayList().get(0);
		collapsedStatistic.setAverage(temp_collection.getCollectionAverageValue());
		System.out.println("avg: " + temp_collection.getCollectionAverageValue());
		collapsedStatistic.setCount(cumulative_count);
		collapsedStatistic.setGroup(arg_statGroup);
		collapsedStatistic.setName(arg_statName);
		collapsedStatistic.setMin(temp_collection.getCollectionMinimumValue());
		System.out.println("min: " + temp_collection.getCollectionMinimumValue());
		collapsedStatistic.setMax(temp_collection.getCollectionMaximumValue());
		System.out.println("max: " +temp_collection.getCollectionMaximumValue());
		collapsedStatistic.setTotalCount(cumulative_totalCount);
		collapsedStatistic.setStartDate(startDate);
		collapsedStatistic.setEndDate(endDate);
		System.out.println("start:" + startDate + ", endDate: " + endDate);
		collapsedStatistic.setSum(cumulative_sum);

		for(int isb=0;isb<remove_isbs.size();isb++){
			removeFromCollection(remove_isbs.get(isb));
		}

		addToCollection(collapsedStatistic);
		remove_isbs.clear();
		writeToString();
	}

	public void collapseAllStatistics(){
		//performs the same operation as collapseStatistic, except for all Statistics
		//in the collection.

		HashMap<String, String> allNames = getAllUniqueNameGroupPairs();

		for(String name : allNames.keySet()){
			collapseStatistic(name, allNames.get(name));
		}

	}

    public void computeAllCollectionStatistics(){
    	//run all computation methods so that the collection has updated field values.
    	this.computeCollectionAverageValue();
    	this.computeCollectionCountValue();
    	this.computeCollectionMaximumValue();
    	this.computeCollectionMaximumObject();
    	this.computeCollectionMinimumValue();
    	this.computeCollectionMinimumObject();
    	this.computeCollectionTotalCountValue();
    }

    public void computeCollectionAverageValue(){
        Long cumulativeSum = 0L;
        for (IntegrityStatisticBean isb : this.collection){
            cumulativeSum = cumulativeSum + isb.getAverage();
        }
        this.collectionAverageValue = (cumulativeSum)/getCollectionSize();
        this.requireAvgValRecompute = false;
    }

    public void computeCollectionCountValue(){
        Long cumulativeSum = 0L;
        for (IntegrityStatisticBean isb : this.collection){
            cumulativeSum = cumulativeSum + isb.getCount();
        }
        this.collectionCountValue = cumulativeSum;
        this.requireCntValRecompute = false;
    }

    public void computeCollectionMaximumObject(){
    	if(this.requireMaxValRecompute) {
    		computeCollectionMaximumValue();
    	}
    	
    	ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    	for (IntegrityStatisticBean isb : this.collection){
    		//use known max of collection to return the object(s) equal to this max.
    		if (isb.getMax()==this.collectionMaximumValue) temp_isb_arrayList.add(isb);
    	}
    	this.collectionMaximumIsbObjectArrayList = temp_isb_arrayList;
    	this.requireMaxObjRecompute = false;
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
        	System.out.println("WARNING: COMPUTE MAX VALUE METHOD CALLED WITH NO COLLECTION");
        	//was called with an empty collection
            //**TODO: throw error or assign default value?
        }
        this.requireMaxValRecompute = false;

    }

    public void computeCollectionMinimumObject(){
    	if(this.requireMinValRecompute) {
    		computeCollectionMinimumValue();
    	}
    	
    	ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    	for (IntegrityStatisticBean isb : this.collection){
    		//use known min of collection to return the object(s) equal to this min.
    		if(isb.getMin()==this.collectionMinimumValue) temp_isb_arrayList.add(isb);
    	}
    	this.collectionMinimumIsbObjectArrayList = temp_isb_arrayList;
    	this.requireMinObjRecompute = false;
    }

    public void computeCollectionMinimumValue(){
    	 if (getCollectionSize()>1) {
             //retrieve all individual minimums into Long[]
             Long[] allMinVals = new Long[getCollectionSize()];
             for (int i = 0; i < getCollectionSize(); i++) {
                 allMinVals[i] = this.collection.get(i).getMin();
             }

             //use the Mergesort algorithm to determine the minimum value
             MergeSort minSorter = new MergeSort();
             minSorter.sort(allMinVals); //min values are now sorted lowest to highest
             this.collectionMinimumValue = allMinVals[0];

         } else if (getCollectionSize()==1){
             this.collectionMinimumValue = this.collection.get(0).getMin();
         } else {
        	 System.out.println("WARNING: COMPUTE MIN VALUE METHOD CALLED WITH NO COLLECTION");
        	 //was called with an empty collection...
             //**TODO: throw error or assign default value?
         }
    	 this.requireMinValRecompute = false;
    }

    public void computeCollectionTotalCountValue(){
    	 Long cumulativeSum = 0L;
         for (IntegrityStatisticBean isb : this.collection){
             cumulativeSum = cumulativeSum + isb.getTotalCount();
         }
         this.collectionTotalCountValue = cumulativeSum;
         this.requireTotCntValRecompute = false;
    }

    public ArrayList<IntegrityStatisticBean> getCollection(){ return this.collection; }

    public HashMap<String, String> getAllUniqueNameGroupPairs(){
		//returns a HashMap of all unique statistic {name: group} pairs in the collection
		HashMap<String, String> uniqueNames = new HashMap<String, String>();

		for(IntegrityStatisticBean isb : this.collection) {
			if (!uniqueNames.containsKey(isb.getName())) {
				uniqueNames.put(isb.getName(), isb.getGroup());
			}
		}
		return uniqueNames;
	}

	public Long getCollectionAverageValue(){
    	if(this.requireAvgValRecompute) {
    		computeCollectionAverageValue();
    	}
    	return this.collectionAverageValue; 
    }

    public Long getCollectionCountValue(){ 
    	if(this.requireCntValRecompute) {
    		computeAllCollectionStatistics();
    	}
    	return this.collectionCountValue; 
    }

    public ArrayList<IntegrityStatisticBean> getCollectionMaximumIsbArrayList(){
    	if(this.requireMaxObjRecompute) {
    		computeCollectionMaximumObject();
    	}
    	return this.collectionMaximumIsbObjectArrayList; 
    	}

    public Long getCollectionMaximumValue(){ 
    	if(this.requireMaxValRecompute) {
    		computeCollectionMaximumValue();
    	}
    	return this.collectionMaximumValue; 
    }

    public ArrayList<IntegrityStatisticBean> getCollectionMinimumIsbArrayList(){ 
    	if(this.requireMinObjRecompute) {
    		computeCollectionMinimumObject();
    	}
    	return this.collectionMinimumIsbObjectArrayList; 
    }

    public Long getCollectionMinimumValue(){
    	if(this.requireMinValRecompute) {
    		computeCollectionMinimumValue();
    	}
    	return this.collectionMinimumValue; 
    }

    public String getCollectionName(){ return this.collectionName; }

	public IntegrityStatisticBean getCollectionObject(int index){ return this.collection.get(index); }

    public int getCollectionSize(){ return this.collection.size(); }

    public Long getCollectionTotalCountValue(){ 
    	if(this.requireTotCntValRecompute) {
    		computeCollectionTotalCountValue();
    	}
    	return this.collectionTotalCountValue; 
    }
   
    public boolean getRequireAvgValRecompute(){ return this.requireAvgValRecompute; }
    public boolean getRequireCntValRecompute(){ return this.requireCntValRecompute; }
    public boolean getRequireMaxObjRecompute(){ return this.requireMaxObjRecompute; }
    public boolean getRequireMaxValRecompute(){ return this.requireMaxValRecompute; }
    public boolean getRequireMinObjRecompute(){ return this.requireMinObjRecompute; }
    public boolean getRequireMinValRecompute(){ return this.requireMinValRecompute; }
    public boolean getRequireTotCntValRecompute(){ return this.requireTotCntValRecompute; }
    
    public void orderByIsbAverageValue() { 	
    	Hashtable<Long, ArrayList<IntegrityStatisticBean>> avgVal_isb_hashtable = new Hashtable<Long, ArrayList<IntegrityStatisticBean>>();
    	Long[] allAvgVals = new Long[getCollectionSize()];
    	ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();

    	for (int i=0; i<getCollectionSize();i++){
    		IntegrityStatisticBean currIsb = this.collection.get(i);
    		Long currAvgVal = this.collection.get(i).getAverage();
    		allAvgVals[i] = currAvgVal;
    		if (avgVal_isb_hashtable.containsKey(currAvgVal)){
    			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    			temp_isb_arrayList = avgVal_isb_hashtable.get(currAvgVal);
    			temp_isb_arrayList.add(currIsb);
    			avgVal_isb_hashtable.put(currAvgVal, temp_isb_arrayList);
    		} else {
    			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    			temp_isb_arrayList.add(currIsb);
    			avgVal_isb_hashtable.put(currAvgVal, temp_isb_arrayList);
    		}
    		
    	}
    	
    	 MergeSort avgValSorter = new MergeSort();
         avgValSorter.sort(allAvgVals); //avg values are now sorted lowest to highest
    	//iterate through array and place isbs into new ArrayList
         System.out.println("size in avg val: " + getCollectionSize());
         for (int j=0;j<getCollectionSize();j++){
        	 ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
        	 temp_isb_arrayList = avgVal_isb_hashtable.get(allAvgVals[j]);
        	 for (IntegrityStatisticBean isb : temp_isb_arrayList){
        		 ordered_isb_arrayList.add(isb);
        	 }
         }
        
         for (int k=0;k<getCollectionSize();k++) this.collection.set(k, ordered_isb_arrayList.get(k));
         ordered_isb_arrayList.clear();
         //collection is now in order of objects from lowest to highest average value
         return;
    }

    public void orderByIsbCountValue() {
    	Hashtable<Long, ArrayList<IntegrityStatisticBean>> cntVal_isb_hashtable = new Hashtable<Long, ArrayList<IntegrityStatisticBean>>();
    	Long[] allCntVals = new Long[getCollectionSize()];
    	ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    	
    	for (int i=0; i<getCollectionSize();i++){
    		IntegrityStatisticBean currIsb = this.collection.get(i);
    		Long currCntVal = this.collection.get(i).getCount();
    		allCntVals[i] = currCntVal;
    		if (cntVal_isb_hashtable.containsKey(currCntVal)){
    			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    			temp_isb_arrayList = cntVal_isb_hashtable.get(currCntVal);
    			temp_isb_arrayList.add(currIsb);
    			cntVal_isb_hashtable.put(currCntVal, temp_isb_arrayList);
    		//	System.out.println("1: " + temp_isb_arrayList.toString());
    		//	System.out.println("2: " + cntVal_isb_hashtable.toString());
    		} else {
    			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    			temp_isb_arrayList.add(currIsb);
    			cntVal_isb_hashtable.put(currCntVal, temp_isb_arrayList);
    		//	System.out.println("3: " + temp_isb_arrayList.toString());
    		//	System.out.println("4: " + cntVal_isb_hashtable.toString());
    		}
    	}

    	 MergeSort cntValSorter = new MergeSort();
         cntValSorter.sort(allCntVals); //count values are now sorted lowest to highest
    	//iterate through array and place isbs into new ArrayList
         for (int j=0;j<getCollectionSize();j++){
        	 ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
        	 temp_isb_arrayList = cntVal_isb_hashtable.get(allCntVals[j]);
        	 for (IntegrityStatisticBean isb : temp_isb_arrayList){
        		 ordered_isb_arrayList.add(isb);
        	 }
         }
         
         for (int k=0;k<getCollectionSize();k++) this.collection.set(k, ordered_isb_arrayList.get(k));
         ordered_isb_arrayList.clear();
         //collection is now in order of objects from lowest to highest count value
         return;
    }

    public void orderByIsbNameValue() {
    	Hashtable<String, ArrayList<IntegrityStatisticBean>> name_isb_hashtable = new Hashtable<String, ArrayList<IntegrityStatisticBean>>();
    	String[] allNames = new String[getCollectionSize()];
    	ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();

    	for (int i=0; i<getCollectionSize();i++){
    		IntegrityStatisticBean currIsb = this.collection.get(i);
    		String currName = this.collection.get(i).getName();
    		allNames[i] = currName;
    		if (name_isb_hashtable.containsKey(currName)){
    			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    			temp_isb_arrayList = name_isb_hashtable.get(currName);
    			temp_isb_arrayList.add(currIsb);
    			name_isb_hashtable.put(currName, temp_isb_arrayList);
    		} else {
    			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    			temp_isb_arrayList.add(currIsb);
    			name_isb_hashtable.put(currName, temp_isb_arrayList);
    		}
    	}

    	Arrays.sort(allNames, ALPHABETICAL_ORDER); //TODO: check if this really works...
    	//names are now sorted alphabetically
    	//iterate through array and place isbs into new ArrayList
         for (int j=0;j<getCollectionSize();j++){
        	 ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
        	 temp_isb_arrayList = name_isb_hashtable.get(allNames[j]);
        	 for (IntegrityStatisticBean isb : temp_isb_arrayList){
        		 ordered_isb_arrayList.add(isb);
        	 }
         }
         
         for (int k=0;k<getCollectionSize();k++) this.collection.set(k, ordered_isb_arrayList.get(k));
         ordered_isb_arrayList.clear();
         //collection is now in order of names according to alphabetical order
         return;
    }

    public void orderByIsbMaximumValue() {
    	Hashtable<Long, ArrayList<IntegrityStatisticBean>> maxVal_isb_hashtable = new Hashtable<Long, ArrayList<IntegrityStatisticBean>>();
    	Long[] allMaxVals = new Long[getCollectionSize()];
    	ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();

    	for (int i=0; i<getCollectionSize();i++){
    		IntegrityStatisticBean currIsb = this.collection.get(i);
    		Long currMaxVal = this.collection.get(i).getMax();
    		allMaxVals[i] = currMaxVal;
    		if (maxVal_isb_hashtable.containsKey(currMaxVal)){
    			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    			temp_isb_arrayList = maxVal_isb_hashtable.get(currMaxVal);
    			temp_isb_arrayList.add(currIsb);
    			maxVal_isb_hashtable.put(currMaxVal, temp_isb_arrayList);
    		} else {
    			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    			temp_isb_arrayList.add(currIsb);
    			maxVal_isb_hashtable.put(currMaxVal, temp_isb_arrayList);
    		}
    	}

    	 MergeSort maxValSorter = new MergeSort();
         maxValSorter.sort(allMaxVals); //max values are now sorted lowest to highest
    	//iterate through array and place isbs into new ArrayList
         for (int j=0;j<getCollectionSize();j++){
        	 ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
        	 temp_isb_arrayList = maxVal_isb_hashtable.get(allMaxVals[j]);
        	 for (IntegrityStatisticBean isb : temp_isb_arrayList){
        		 ordered_isb_arrayList.add(isb);
        	 }
         }
         
         for (int k=0;k<getCollectionSize();k++) this.collection.set(k, ordered_isb_arrayList.get(k));
         ordered_isb_arrayList.clear();
         //collection is now in order of objects from lowest to highest max value
         return;
    }

    public void orderByIsbMinimumValue() {
    	Hashtable<Long, ArrayList<IntegrityStatisticBean>> minVal_isb_hashtable = new Hashtable<Long, ArrayList<IntegrityStatisticBean>>();
    	Long[] allMinVals = new Long[getCollectionSize()];
    	ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();

    	for (int i=0; i<getCollectionSize();i++){
    		IntegrityStatisticBean currIsb = this.collection.get(i);
    		Long currMinVal = this.collection.get(i).getMin();
    		allMinVals[i] = currMinVal;
    		if (minVal_isb_hashtable.containsKey(currMinVal)){
    			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    			temp_isb_arrayList = minVal_isb_hashtable.get(currMinVal);
    			temp_isb_arrayList.add(currIsb);
    			minVal_isb_hashtable.put(currMinVal, temp_isb_arrayList);
    		} else {
    			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    			temp_isb_arrayList.add(currIsb);
    			minVal_isb_hashtable.put(currMinVal, temp_isb_arrayList);
    		}
    	}

    	 MergeSort minValSorter = new MergeSort();
         minValSorter.sort(allMinVals); //min values are now sorted lowest to highest
    	//iterate through array and place isbs into new ArrayList
         for (int j=0;j<getCollectionSize();j++){
        	 ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
        	 temp_isb_arrayList = minVal_isb_hashtable.get(allMinVals[j]);
        	 for (IntegrityStatisticBean isb : temp_isb_arrayList){
        		 ordered_isb_arrayList.add(isb);
        	 }
         }
         
         for (int k=0;k<getCollectionSize();k++) this.collection.set(k, ordered_isb_arrayList.get(k));
         ordered_isb_arrayList.clear();
         //collection is now in order of objects from lowest to highest min value
         return;
    }

    public void orderByIsbTotalCountValue() {
    	Hashtable<Long, ArrayList<IntegrityStatisticBean>> totCntVal_isb_hashtable = new Hashtable<Long, ArrayList<IntegrityStatisticBean>>();
    	Long[] allTotCntVals = new Long[getCollectionSize()];
    	ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();

    	for (int i=0; i<getCollectionSize();i++){
    		IntegrityStatisticBean currIsb = this.collection.get(i);
    		Long currTotCntVal = this.collection.get(i).getTotalCount();
    		allTotCntVals[i] = currTotCntVal;
    		if (totCntVal_isb_hashtable.containsKey(currTotCntVal)){
    			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    			temp_isb_arrayList = totCntVal_isb_hashtable.get(currTotCntVal);
    			temp_isb_arrayList.add(currIsb);
    			totCntVal_isb_hashtable.put(currTotCntVal, temp_isb_arrayList);
    		} else {
    			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
    			temp_isb_arrayList.add(currIsb);
    			totCntVal_isb_hashtable.put(currTotCntVal, temp_isb_arrayList);
    		}
    	}
    	
    	 MergeSort totCntSorter = new MergeSort();
         totCntSorter.sort(allTotCntVals); //total count values are now sorted lowest to highest
    	//iterate through array and place isbs into new ArrayList
         for (int j=0;j<getCollectionSize();j++){
        	 ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
        	 temp_isb_arrayList = totCntVal_isb_hashtable.get(allTotCntVals[j]);
        	 for (IntegrityStatisticBean isb : temp_isb_arrayList){
        		 ordered_isb_arrayList.add(isb);
        	 }
         } 
         for (int k=0;k<getCollectionSize();k++) this.collection.set(k, ordered_isb_arrayList.get(k));
         ordered_isb_arrayList.clear();
         //collection is now in order of objects from lowest to highest total count value
         return;
	}

    public void removeFromCollection(IntegrityStatisticBean arg_isb){ 
    	this.collection.remove(arg_isb); 
    	this.requireAvgValRecompute = true;
    	this.requireCntValRecompute = true;
    	this.requireMaxObjRecompute = true;
    	this.requireMaxValRecompute = true;
    	this.requireMinObjRecompute = true;
    	this.requireMinValRecompute = true;
    	this.requireTotCntValRecompute = true;
    }



    public void removeFromCollection(int[] indicesToRemove){

		StatisticsCollection remove_collection = new StatisticsCollection("temp removal collection");
		for(int i : indicesToRemove) remove_collection.addToCollection(this.getCollectionObject(i));

		this.collection.removeAll(remove_collection.getCollection());
		remove_collection.clearCollection();

    	this.requireAvgValRecompute = true;
    	this.requireCntValRecompute = true;
    	this.requireMaxObjRecompute = true;
    	this.requireMaxValRecompute = true;
    	this.requireMinObjRecompute = true;
    	this.requireMinValRecompute = true;
    	this.requireTotCntValRecompute = true;
    }
    
    public void removeFromCollection(int index){ 
    	this.collection.remove(index); 
    }
    
    public void setName(String arg_name) { this.collectionName = arg_name;}

    public void writeToFile(String arg_filePath) {
    	
    	//write to file as CSV following the same format as the output Server Statistics .csv file
    	//cols: Start Date, End Date, Group, Statistic (Name), Kind, Unit, Count, total, Sum, Min, Max, Average, Mode
    	StringBuffer fileContents = new StringBuffer();
		fileContents.append("Start Date, End Date, Group, Statistic, Kind, Unit, Count, Total, Sum, Used, Min, Max, Average, Mode"+"\n");
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
				writer.close();
	    	} catch (IOException x) {
	    	    System.out.format("IOException: %s%n", x);
	    	}
    	} else {
			System.out.println("WARNING: EMPTY DATA WRITTEN TO FILE...");
    		//fileContents is empty
    		//TODO: throw error or warn?
    	}
    }
    
    //TODO: implemented for testing...repurpose into usable function or remove from final product
    public void writeToString() {
    	//write to file as CSV following the same format as the output Server Statistics .csv file
    	//cols: Start Date, End Date, Group, Statistic (Name), Kind, Unit, Count, total, Sum, Min, Max, Average, Mode
    	StringBuffer fileContents = new StringBuffer();
    	int entry_counter = 1;
    	fileContents.append("Start Date, End Date, Group, Statistic, Kind, Unit, Count, total, Sum, Used, Min, Max, Average, Mode"+"\n");
    	for(IntegrityStatisticBean isb : this.collection){
    		fileContents.append(entry_counter + ": " 
    				+ isb.getStartDate() + ","
    				+ isb.getEndDate() + "," 
    				+ isb.getGroup() + ",'"
    				+ isb.getName() + "',"
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
    		entry_counter++;
    	}
    	System.out.println(fileContents.toString());
    }
}

