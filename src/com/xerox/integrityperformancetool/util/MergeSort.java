package com.xerox.integrityperformancetool.util;

/**
 * Created by bryan on 8/9/2015.
 *
 * Class implementing the Mergesort algorithm for use with Long[] arrays.
 */
public class MergeSort {

    private Long[] long_array;
    private Long[] long_helper_array;

    public MergeSort(){
        //System.out.println("Initialized MergeSort...");
    }

    public Long[] sort(Long[] input){
        this.long_array = input;
        this.long_helper_array = new Long[this.long_array.length];
        mergeSort(this.long_array, this.long_helper_array, 0, this.long_array.length-1);
        return this.long_array;
    }

    private void mergeSort(Long[] arg_array, Long[] arg_helper_array, int low, int high){
        if (low < high){
            int middle = (low+high)/2;
            mergeSort(arg_array, arg_helper_array, low, middle);
            mergeSort(arg_array, arg_helper_array, middle+1, high);
            merge(arg_array, arg_helper_array, low, middle, high);
        }
    }

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

        int remaining = middle - helperLeft;
        for (int i=0;i<=remaining;i++) { arg_array[current + i] = arg_helper_array[helperLeft + i]; }

    }


}
