package com.cdp.integrityperformancetool.testing.isb;


import org.joda.time.DateTime;

import com.cdp.integrityperformancetool.IntegrityStatisticBean;

public class Isb_test_case_1 {

	/**
	 * Isb_test_case_1 
	 * 
	 * Test Case Objectives:
	 * - create a IntegrityStatisticBean object, providing no initialization arguments
	 * - output the default values to screen for accuracy
	 * - edit all of the ISB object's fields.
	 * - get all of the new field values, output to screen for accuracy
	 * Test Case executed on:
	 * Test Case Notes:
	 * 
	 */
	
	
	/**
	 * @param args
	 */
	public Isb_test_case_1(){
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
	}
	
	 public void printState(IntegrityStatisticBean isb){
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

}
