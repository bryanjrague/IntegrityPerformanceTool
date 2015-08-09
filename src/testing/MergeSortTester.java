package testing;

/**
 * Created by bryan on 8/7/2015.
 */
public class MergeSortTester {

    public static void main(String[] args){
        Long[] arr = {1L, 3L, 34L, 15L ,8L};
        mergeSort(arr);
    }

    private static void merge(Long[] arg_array, Long[] arg_helper_array, int low, int middle, int high){
        System.out.println("in merge with arr = " + arrToString(arg_array)
                + ", helper = " + arrToString(arg_helper_array)
                + ", low = " + low
                + ", middle = " + middle
                + ", high = " + high);

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
        System.out.println("1: " + arrToString(arg_array) + ", " + arrToString(arg_helper_array));
        System.out.println("Exiting merge...");
    }

    private static void mergeSort(Long[] arg_array) {
        System.out.println("in mergeSort...");
        Long[] helper_array = new Long[arg_array.length];
        mergeSort(arg_array, helper_array, 0, arg_array.length-1);
        System.out.println("Final: " + arrToString(arg_array));
        System.out.println("Exiting mergeSort...");


    }

    private static void mergeSort(Long[] arg_array, Long[] arg_helper_array, int low, int high){
        System.out.println("in mergesort 2 with arr = " + arrToString(arg_array)
                + ", helper = " + arrToString(arg_helper_array)
                + ", low = " + low
                + ", high = " + high);
        if (low < high){
            int middle = (low+high)/2;
            mergeSort(arg_array, arg_helper_array, low, middle);
            mergeSort(arg_array, arg_helper_array, middle+1, high);
            merge(arg_array, arg_helper_array, low, middle, high);

        } //else return
        System.out.println("Exiting mergeSort 2...");
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
}
