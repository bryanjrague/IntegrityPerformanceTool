package com.cdp.integrityperformancetool.testing.imb;

import com.cdp.integrityperformancetool.IntegrityMetricBean;

public class Imb_test_case_2 extends Imb_Test_Runner{

	/**
	 * Imb_test_case_2 
	 * 
	 * Test Case Objectives:
	 * - create a IntegrityMetricBean object with the 2-arg constructor
	 * 		for each value type
	 * - output the values to screen for accuracy
	 * - compare values to expected.
	 * - create a IntegrityMetricBean object with the 3-arg constructor
	 * 		for each value type
	 * - output the values to screen for accuracy
	 * - compare values to expected.
	 * 
	 * Test Case executed on:
	 * 		Aug 19, 2015 - PASS
	 * Test Case Notes:
	 * 
	 */
	
	public Imb_test_case_2() {
		System.out.println("Initialized Imb_test_case_2...");
	}
	
	public String execute(){
		//***************************2-arg creations

		System.out.println("Creating IMB with two-arg constructor using long value...");
		IntegrityMetricBean IMB_2 = new IntegrityMetricBean("Metric Name 2", 345000L);
		printState(IMB_2);
		System.out.println("Validating field values against expected defaults...");
		if(!isEqual("Default Custom Name", IMB_2.getCustomName())){
			return "ERROR 1";
		}
		if(!isEqual("Metric Name 2", IMB_2.getMetricName())){
			return "ERROR 2";
		}
		if(!isEqual(345000L, IMB_2.getValueLong())){
			return "ERROR 3";
		}
		System.out.println("VALIDATED\n");

		System.out.println("Creating IMB with two-arg constructor using String value...");
		IntegrityMetricBean IMB_3 = new IntegrityMetricBean("Metric Name 3", "String Data");
		printState(IMB_3);
		System.out.println("Validating field values against expected defaults...");
		if(!isEqual("Default Custom Name", IMB_3.getCustomName())){
			return "ERROR 4";
		}
		if(!isEqual("Metric Name 3", IMB_3.getMetricName())){
			return "ERROR 5";
		}
		if(!isEqual("String Data", IMB_3.getValueString())){
			return "ERROR 6";
		}
		System.out.println("VALIDATED\n");

		System.out.println("Creating IMB with two-arg constructor using Float value...");
		IntegrityMetricBean IMB_4 = new IntegrityMetricBean("Metric Name 4", 53876.435F);
		printState(IMB_4);
		System.out.println("Validating field values against expected defaults...");
		if(!isEqual("Default Custom Name", IMB_4.getCustomName())){
			return "ERROR 7";
		}
		if(!isEqual("Metric Name 4", IMB_4.getMetricName())){
			return "ERROR 8";
		}
		if(!isEqual(53876.435F, IMB_4.getValueFloat())){
			return "ERROR 9";
		}
		System.out.println("VALIDATED\n");

		//***************3-arg creations

		System.out.println("Creating IMB with three-arg constructor using String value...");
		IntegrityMetricBean IMB_5 = new IntegrityMetricBean("Custom Name 5", "Metric Name 5", 871360L);
		printState(IMB_5);
		System.out.println("Validating field values against expected defaults...");
		if(!isEqual("Custom Name 5", IMB_5.getCustomName())){
			return "ERROR 10";
		}
		if(!isEqual("Metric Name 5", IMB_5.getMetricName())){
			return "ERROR 11";
		}
		if(!isEqual(871360L, IMB_5.getValueLong())){
			return "ERROR 12";
		}
		System.out.println("VALIDATED\n");

		System.out.println("Creating IMB with three-arg constructor using String value...");
		IntegrityMetricBean IMB_6 = new IntegrityMetricBean("Custom Name 6", "Metric Name 6", "String Data 6");
		printState(IMB_6);
		System.out.println("Validating field values against expected defaults...");
		if(!isEqual("Custom Name 6", IMB_6.getCustomName())){
			return "ERROR 13";
		}
		if(!isEqual("Metric Name 6", IMB_6.getMetricName())){
			return "ERROR 14";
		}
		if(!isEqual("String Data 6", IMB_6.getValueString())){
			return "ERROR 15";
		}
		System.out.println("VALIDATED\n");

		System.out.println("Creating IMB with three-arg constructor using String value...");
		IntegrityMetricBean IMB_7 = new IntegrityMetricBean("Custom Name 7", "Metric Name 7", 6387.699856F);
		printState(IMB_7);
		System.out.println("Validating field values against expected defaults...");
		if(!isEqual("Custom Name 7", IMB_7.getCustomName())){
			return "ERROR 16";
		}
		if(!isEqual("Metric Name 7", IMB_7.getMetricName())){
			return "ERROR 17";
		}
		if(!isEqual(6387.699856F, IMB_7.getValueFloat())){
			return "ERROR 18";
		}
		System.out.println("VALIDATED\n");

		return "PASS";
	}

}
