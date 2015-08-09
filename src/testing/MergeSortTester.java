package testing;

import com.xerox.integrityperformancetool.util.MergeSort;

/**
 * Created by bryan on 8/7/2015.
 */
public class MergeSortTester {

    public static void main(String[] args){
      //  Long[] arr = {1L, 3L, 34L, 15L ,8L};
        Long[] arr = {34L, 6578L, 0L, 23L, 5L, 56L, 87L, -1L};
        System.out.println(arrToString(arr));
        MergeSort sorter = new MergeSort();
        sorter.sort(arr);
        System.out.println(arrToString(arr));
    }

    private static void merge(Long[] arg_array, Long[] arg_helper_array, int low, int middle, int high){
        indentPrint("in merge with arr = " + arrToString(arg_array)
                + ", helper = " + arrToString(arg_helper_array)
                + ", low = " + low
                + ", middle = " + middle
                + ", high = " + high, 2);

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
        indentPrint("arr: " + arrToString(arg_array) + ", helper:" + arrToString(arg_helper_array), 2);
        indentPrint("Exiting merge...", 2);
    }

    private static void mergeSort(Long[] arg_array) {
        indentPrint("in mergeSort...", 0);
        Long[] helper_array = new Long[arg_array.length];
        mergeSort(arg_array, helper_array, 0, arg_array.length-1);
        indentPrint("Final: " + arrToString(arg_array), 0);
        indentPrint("Exiting mergeSort...", 0);


    }

    private static void mergeSort(Long[] arg_array, Long[] arg_helper_array, int low, int high){
        indentPrint("in mergesort 2 with arr = " + arrToString(arg_array)
                + ", helper = " + arrToString(arg_helper_array)
                + ", low = " + low
                + ", high = " + high, 1);
        if (low < high){
            int middle = (low+high)/2;
            mergeSort(arg_array, arg_helper_array, low, middle);
            mergeSort(arg_array, arg_helper_array, middle+1, high);
            merge(arg_array, arg_helper_array, low, middle, high);

        } //else return
        indentPrint("Exiting mergeSort 2...", 1);
    }

    private static String arrToString(Long[] arr){
        String tempStr = "[";
        for (int i=0;i<arr.length;i++){
            tempStr = tempStr + arr[i];
            if (i+1<arr.length) tempStr = tempStr + ", ";
        }
        tempStr = tempStr + "]";
        return tempStr;
    }

    private static void indentPrint(String s, int indent){

        String temp = "";
        for (int i=1;i<=indent;i++){ temp = temp + "\t"; }
        System.out.println(temp + s);

    }
}
