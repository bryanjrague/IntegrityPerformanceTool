package com.cdp.integrityperformancetool.testing.isb;


import org.joda.time.DateTime;

import com.cdp.integrityperformancetool.IntegrityStatisticBean;

public class Isb_test_case_1 extends Isb_Test_Runner{

	/**
	 * Isb_test_case_1 
	 * 
	 * Test Case Objectives:
	 * - create a IntegrityStatisticBean object, providing no initialization arguments
	 * - output the default values to screen for accuracy
	 * - edit all of the ISB object's fields.
	 * - get all of the new field values, output to screen for accuracy, validate
	 * Test Case executed on:
	 * Test Case Notes:
	 * 
	 */
	
	
	/**
	 * @param args
	 */
	public Isb_test_case_1(){
		 System.out.println("Initialized Isb_test_case_1...");
	}
	
	public String execute(){
		System.out.println("Creating Isb object with default constructor...");
		IntegrityStatisticBean isb_1 = new IntegrityStatisticBean();
		printState(isb_1);
		System.out.println("Validating default values against expected...");
		if(!isEqual(0L, isb_1.getAverage())) { return "ERROR 1"; }
		if(!isEqual(0L, isb_1.getCount())) { return "ERROR 2"; }
		if(!isEqual("", isb_1.getGroup())) { return "ERROR 3"; }
		if(!isEqual("", isb_1.getKind())) { return "ERROR 4"; }
		if(!isEqual(0L, isb_1.getMax())) { return "ERROR 5"; }
		if(!isEqual(0L, isb_1.getMin())) { return "ERROR 6"; }
		if(!isEqual("", isb_1.getName())) { return "ERROR 7"; }
		if(!isEqual(0L, isb_1.getSum())) { return "ERROR 8"; }
		if(!isEqual(0L, isb_1.getTotalCount())) { return "ERROR 9"; }
		if(!isEqual("", isb_1.getUnit())) { return "ERROR 10"; }
		if(!isEqual("", isb_1.getUsed())) { return "ERROR 11"; }
		System.out.println("VALIDATED\n");
		
		System.out.println("Editing Isb object state...");
		isb_1.setAverage(23L);
		isb_1.setCount(587L);
		isb_1.setEndDate(new DateTime());
		isb_1.setGroup("isb_1 group");
		isb_1.setKind("isb_1 kind");
		isb_1.setMax(28495L);
		isb_1.setMin(23L);
		isb_1.setMode("isb_1 mode");
		isb_1.setName("isb_1 name");
		isb_1.setStartDate(new DateTime());
		isb_1.setSum(385749L);
		isb_1.setTotalCount(24532L);
		isb_1.setUnit("Ms");
		isb_1.setUsed("isb_1 used");
		printState(isb_1);
		System.out.println("Validating edits...");
		if(!isEqual(23L, isb_1.getAverage())) { return "ERROR 1"; }
		if(!isEqual(587L, isb_1.getCount())) { return "ERROR 2"; }
		if(!isEqual("isb_1 group", isb_1.getGroup())) { return "ERROR 3"; }
		if(!isEqual("isb_1 kind", isb_1.getKind())) { return "ERROR 4"; }
		if(!isEqual(28495L, isb_1.getMax())) { return "ERROR 5"; }
		if(!isEqual(23L, isb_1.getMin())) { return "ERROR 6"; }
		if(!isEqual("isb_1 name", isb_1.getName())) { return "ERROR 7"; }
		if(!isEqual(385749L, isb_1.getSum())) { return "ERROR 8"; }
		if(!isEqual(24532L, isb_1.getTotalCount())) { return "ERROR 9"; }
		if(!isEqual("Ms", isb_1.getUnit())) { return "ERROR 10"; }
		if(!isEqual("isb_1 used", isb_1.getUsed())) { return "ERROR 11"; }
		System.out.println("VALIDATED\n");
		
		System.out.println("Editing Isb object state with ints using overloaded set methods...");
		isb_1.setAverage(231);
		isb_1.setCount(5871);
		isb_1.setMax(284951);
		isb_1.setMin(231);
		isb_1.setSum(3857491);
		isb_1.setTotalCount(245321);
		printState(isb_1);
		System.out.println("Validating edits...");
		if(!isEqual(231L, isb_1.getAverage())) { return "ERROR 1"; }
		if(!isEqual(5871L, isb_1.getCount())) { return "ERROR 2"; }
		if(!isEqual(284951L, isb_1.getMax())) { return "ERROR 5"; }
		if(!isEqual(231L, isb_1.getMin())) { return "ERROR 6"; }
		if(!isEqual(3857491L, isb_1.getSum())) { return "ERROR 8"; }
		if(!isEqual(245321L, isb_1.getTotalCount())) { return "ERROR 9"; }
		System.out.println("VALIDATED\n");
		
		System.out.println("Editing Isb object state with Strings using overloaded set methods...");
		isb_1.setAverage("2312");
		isb_1.setCount("58712");
		isb_1.setMax("2849512");
		isb_1.setMin("2312");
		isb_1.setSum("38574912");
		isb_1.setTotalCount("2453212");
		printState(isb_1);
		System.out.println("Validating edits...");
		if(!isEqual(2312L, isb_1.getAverage())) { return "ERROR 1"; }
		if(!isEqual(58712L, isb_1.getCount())) { return "ERROR 2"; }
		if(!isEqual(2849512L, isb_1.getMax())) { return "ERROR 5"; }
		if(!isEqual(2312L, isb_1.getMin())) { return "ERROR 6"; }
		if(!isEqual(38574912L, isb_1.getSum())) { return "ERROR 8"; }
		if(!isEqual(2453212L, isb_1.getTotalCount())) { return "ERROR 9"; }
		System.out.println("VALIDATED\n");
		
		return "PASS";
	}

}
