package testing;

import com.xerox.integrityperformancetool.*;
import org.joda.time.DateTime;
/**
 * Created by bryan on 8/7/2015.
 */
public class Isb_Imb_Tester {

    public static void main(String[] args){
        //create IntegrityStatisticBean and modify it's instance variables.
        System.out.println("Creating ISB:...");
        IntegrityStatisticBean ISB_one = new IntegrityStatisticBean();
        printState(ISB_one);
        System.out.println("Editing ISB state...");
        ISB_one.setAverage(1213456L);
        ISB_one.setCount(1222L);
        ISB_one.setEndDate(new DateTime(2015, 8, 6, 12, 37, 40));
        ISB_one.setGroup("Test Group");
        ISB_one.setKind("Test Kind");
        ISB_one.setMax(999999999999L);
        ISB_one.setMin(0L);
        ISB_one.setMode("Test Mode");
        ISB_one.setName("Test Name");
        ISB_one.setStartDate(new DateTime(2015, 4, 1, 8, 0, 0));
        ISB_one.setSum(434343434L);
        ISB_one.setTotalCount(3684L);
        ISB_one.setUnit("Test Unit");
        ISB_one.setUsed("Test Used");
        printState(ISB_one);

        //create IntegrityMetricBean from default constructor and modify it's instance variables.
        System.out.println("Creating IMB with default constructor...");
        IntegrityMetricBean IMB_one = new IntegrityMetricBean();
        printState(IMB_one);
        System.out.println("Editing IMB State to have a Long value");
        IMB_one.setCustomName("Test Custom Name");
        IMB_one.setMetricName("Test Metric Name");
        IMB_one.setValue(3456789L);
        printState(IMB_one);
        System.out.println("Creating IMB with two-arg constructor using boolean value...");
        IntegrityMetricBean IMB_two = new IntegrityMetricBean("Metric Name 2", true);
        printState(IMB_two);
        System.out.println("Creating IMB with three-arg constructor using String value...");
        IntegrityMetricBean IMB_three = new IntegrityMetricBean("Custom Name 3", "Metric Name 3", "Metric value 3");
        printState(IMB_three);
    }

    public static void printState(IntegrityStatisticBean isb){
        //print current state of isb object for sanity-check and debug.
        System.out.println(" ** IntegrityStatisticBean State **");
        System.out.println(" Average: " + isb.getAverage() + " (" + isb.getAverage().getClass() + ")");
        System.out.println(" Count: " + isb.getCount() + " (" + isb.getCount().getClass() + ")");
        System.out.println(" End Date: " + isb.getEndDate() + " (" + isb.getEndDate().getClass() + ")");
        System.out.println(" Group: " + isb.getGroup() + " (" + isb.getGroup().getClass() + ")");
        System.out.println(" Kind: " + isb.getKind() + " (" + isb.getKind().getClass() + ")");
        System.out.println(" Max: " + isb.getMax() + " (" + isb.getMax().getClass() + ")");
        System.out.println(" Min: " + isb.getMin() + " (" + isb.getMin().getClass() + ")");
        System.out.println(" Mode: " + isb.getMode() + " (" + isb.getMode().getClass() + ")");
        System.out.println(" Name: " + isb.getName() + " (" + isb.getName().getClass() + ")");
        System.out.println(" Start Date: " + isb.getStartDate() + " (" + isb.getStartDate().getClass() + ")");
        System.out.println(" Sum: " + isb.getSum()  + " (" + isb.getSum().getClass() + ")");
        System.out.println(" Total Count: " + isb.getTotalCount() + " (" + isb.getTotalCount().getClass() + ")");
        System.out.println(" Unit: " + isb.getUnit() + " (" + isb.getUnit().getClass() + ")");
        System.out.println(" Used: " + isb.getUsed() + " (" + isb.getUsed().getClass() + ")");
        System.out.println(" ***********************************\n");
    }

    public static void printState(IntegrityMetricBean imb){
        //print current state of imb object for sanity check and debug.
        System.out.println(" ** IntegrityMetricBean State **");
        System.out.println(" Custom Name: " + imb.getCustomName() + " (" + imb.getCustomName().getClass() + ")");
        System.out.println(" Metric Name: " + imb.getMetricName() + " (" + imb.getMetricName().getClass() + ")");
        System.out.println(" Value: " + imb.getValue() + " (" + imb.getValue().getClass() + ")");
        System.out.println(" *******************************\n");
    }

}
