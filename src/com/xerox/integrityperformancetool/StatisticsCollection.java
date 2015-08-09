package com.xerox.integrityperformancetool;

import java.util.ArrayList;

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
    private Long collectionTotalCount;

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
       
    }

    public void computeCollectionMaximumValue() {
        if (getCollectionSize()>1) {
            //retrieve all individual maximums into Long[]
            Long[] allMaxVals = new Long[getCollectionSize()];
            for (int i = 0; i < getCollectionSize(); i++) {
                allMaxVals[i] = this.collection.get(i).getMax();
            }

            //use the "merge-sort algorithm to determine the maximum value
            mergeSort(allMaxVals);


        } else if (getCollectionSize()==1){
            this.collectionMaximumValue = this.collection.get(0).getMax();
        } else {
            //**TODO: throw error or assign default value?
        }
        
    }

    public void computeCollectionMinimumObject(){

    }

    public void computeCollectionMinimumValue(){

    }

    public void computeCollectionTotalCountValue(){

    }

    //TODO populate and uncomment
    //public ArrayList<IntegrityStatisticBean> getCollection(){ }

    public Long getCollectionAverageValue(){ return this.collectionAverageValue; }

    public Long getCollectionCountValue(){ return this.collectionCountValue; }

    //TODO populate and uncomment
    //public ArrayList<IntegrityStatisticBean> getCollectionMaximumIsbObject(){ }

    //TODO populate and uncomment
    //public Long getCollectionMaximumValue(){ }

    //TODO populate and uncomment
    //public ArrayList<IntegrityStatisticBean> getCollectionMinimumIsbObject(){ }

    //TODO populate and uncomment
    //public Long getCollectionMinimumValue(){ }

    //TODO populate and uncomment
    //public String getCollectionName(){ }

    public int getCollectionSize(){ return this.collection.size(); }

    //TODO populate and uncomment
    //public Long getCollectionTotalCount(){ }

    private void merge(Long[] arg_array, Long[] arg_helper_array, int low, int middle, int high){
        for (int i=low; i<=high; i++){ arg_helper_array[i] = arg_array[i]; }

        int helperLeft = low;
        int helperRight = middle + 1;
        int current = low;

        while (helperLeft <= middle && helperRight <=high){
            if(arg_helper_array[helperLeft] <= arg_helper_array[helperRight]){
                arg_array[current] = arg_helper_array[helperLeft];
                helperLeft++;
            } else {
                arg_array[current] = arg_helper_array[helperRight];
                helperRight++;
            }
            current++;
        }

        int remaining = middle - helperLeft++;
        for (int i=0;i<=remaining;i++) { arg_array[current + i] = arg_helper_array[helperLeft + i]; }


    }
    
    private void mergeSort(Long[] arg_array) {
        Long[] helper_array = new Long[arg_array.length];
        mergeSort(arg_array, helper_array, 0, arg_array.length);
    }
    
    private void mergeSort(Long[] arg_array, Long[] arg_helper_array, int low, int high){
        if (low < high){
            int middle = (low+high)/2;
            mergeSort(arg_array, arg_helper_array, low, middle);
            mergeSort(arg_array, arg_helper_array, middle+1, high);
            merge(arg_array, arg_helper_array, low, middle, high);

        } //else return
    }

    //TODO populate and uncomment
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
