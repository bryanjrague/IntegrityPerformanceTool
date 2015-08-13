package com.xerox.integrityperformancetool;

import java.util.ArrayList;
import java.util.Hashtable;
import com.xerox.integrityperformancetool.util.MergeSort;
/**
 * Created by bryan on 8/7/2015.
 */
public class StatisticsCollection {

    private ArrayList<IntegrityStatisticBean> collection;
    private Long collectionAverageValue;
    private Long collectionCountValue;
    private ArrayList<IntegrityStatisticBean> collectionMaximumIsbObject;
    private Long collectionMaximumValue;
    private ArrayList<IntegrityStatisticBean> collectionMinimumIsbObject;
    private Long collectionMinimumValue;
    private String collectionName;
    private Long collectionTotalCountValue;

    public StatisticsCollection(){
        this("New Integrity Statistics Collection");
    }

    public StatisticsCollection(String arg_collectionName){
        this.collectionName = arg_collectionName;
    }

    public StatisticsCollection(String arg_collectionName, ArrayList<IntegrityStatisticBean> arg_collection){
        this.collection = arg_collection;
        this.collectionName  = arg_collectionName;
    }

    public void addToCollection(IntegrityStatisticBean arg_isb){ this.collection.add(arg_isb); }

    public void clearCollection(){ this.collection.clear(); }

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
    	this.collectionMaximumIsbObject = temp_isb_arrayList;
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
    	this.collectionMinimumIsbObject = temp_isb_arrayList;
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

    public ArrayList<IntegrityStatisticBean> getCollectionMaximumIsbObject(){ return this.collectionMaximumIsbObject; }

    public Long getCollectionMaximumValue(){ return this.collectionMaximumValue; }

    public ArrayList<IntegrityStatisticBean> getCollectionMinimumIsbObject(){ return this.collectionMinimumIsbObject; }

    public Long getCollectionMinimumValue(){ return this.collectionMinimumValue; }
    
    public String getCollectionName(){ return this.collectionName; }

    public int getCollectionSize(){ return this.collection.size(); }

    public Long getCollectionTotalCount(){ return this.collectionTotalCountValue; }

    //TODO: populate and uncomment
    //public void orderByIsbAverageValue() {
    	//create hashtable of {avg_value, ArrayList<IntegrityStatisticBean>}
    	//create Long[] of avg_value
    	//mergeSort avg_value
    	//iterate through array and place isbs into new ArrayList
    	//return finalized arrayList of isbs in order of low to high avg_val
    //}

    //TODO populate and uncomment
    //public void orderByIsbCountValue() {

    //}

    //TODO populate and uncomment
    //public void orderByIsbNameValue() {

    //}

    //TODO populate and uncomment
    //public void orderByIsbMaximumValue() {

    //}

    //TODO populate and uncomment
    //public void orderByIsbMinimumValue() {

   // }

    //TODO populate and uncomment
    //public void orderByIsbTotalCountValue() {

   // }

    public void removeFromCollection(IntegrityStatisticBean arg_isb){ this.collection.remove(arg_isb); }

    //TODO populate and uncomment
    //public void writeToFile(String arg_filePath) {

    //}
}
