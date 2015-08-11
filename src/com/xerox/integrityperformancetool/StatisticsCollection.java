package com.xerox.integrityperformancetool;

import java.util.ArrayList;
import com.xerox.integrityperformancetool.util.MergeSort;
/**
 * Created by bryan on 8/7/2015.
 */
public class StatisticsCollection {

    private ArrayList<IntegrityStatisticBean> collection = new java.util.ArrayList<IntegrityStatisticBean>();
    private Long collectionAverageValue = 0L;
    private Long collectionCountValue = 0L;
    private ArrayList<IntegrityStatisticBean> collectionMaximumIsbObject = new java.util.ArrayList<IntegrityStatisticBean>();
    private Long collectionMaximumValue = 0L;
    private ArrayList<IntegrityStatisticBean> collectionMinimumIsbObject = new java.util.ArrayList<IntegrityStatisticBean>();
    private Long collectionMinimumValue = 0L;
    private String collectionName = "";
    private Long collectionTotalCount = 0L;

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
        this.collectionAverageValue = cumulativeSum;
    }

    public void computeCollectionMaximumObject(){

       if (getCollectionSize()>1){
           //retrieve all
       } else if (getCollectionSize()==1){

       } else {
           //TODO: throw error/exception
       }
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
            //**TODO: throw error/exception
        }
        
    }

    public void computeCollectionMinimumObject(){

    }

    public void computeCollectionMinimumValue(){
        if (getCollectionSize()>1) {
            //retrieve all individual minimums into Long[]
            Long[] allMinVals = new Long[getCollectionSize()];
            for (int i = 0; i < getCollectionSize(); i++) {
                allMinVals[i] = this.collection.get(i).getMin();
            }

            //use the Mergesort algorithm to determine the maximum value
            MergeSort maxSorter = new MergeSort();
            maxSorter.sort(allMinVals); //min values are now sorted lowest to highest
            this.collectionMinimumValue = allMinVals[0];

        } else if (getCollectionSize()==1){
            this.collectionMinimumValue = this.collection.get(0).getMin();
        } else {
            //**TODO: throw error/exception
        }
    }

    public void computeCollectionTotalCountValue(){
        Long cumulativeSum = 0L;
        for (IntegrityStatisticBean isb : this.collection){
            cumulativeSum = cumulativeSum + isb.getTotalCount();
        }
        this.collectionTotalCount = cumulativeSum;
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

    public Long getCollectionTotalCount(){ return this.collectionTotalCount; }

    //public void orderByIsbAverageValue() {

    //}

    //TODO populate and uncomment
    //public void orderByIsbCountValue() {

   // }

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
