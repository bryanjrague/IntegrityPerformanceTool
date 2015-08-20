package com.cdp.integrityperformancetool.testing.imb;

import com.cdp.integrityperformancetool.IntegrityMetricBean;

public class Imb_test_case_1 extends Imb_Test_Runner{

	private static final Float epsilon = 0.0000000000001F; //for use comparing float values
	
	/**
	 * Imb_test_case_1 
	 * 
	 * Test Case Objectives:
	 * - create a IntegrityMetricBean object with default constructor
	 *   (providing no initialization arguments)
	 * - output the default values to screen for accuracy
	 * - compare values to expected.
	 * - edit all of the ISB object's fields.
	 * - get all of the new field values, output to screen for accuracy
	 * - compare new values to expected.
	 * Test Case executed on:
	 * 		Aug. 19, 2015 - Initial, PASS
	 * Test Case Notes:
	 * 
	 */
	
	public Imb_test_case_1() {
		System.out.println("Initialized Ism_test_case_1 ...");
	}
	
	public String execute(){
		
        System.out.println("Creating IMB with default constructor...");
        IntegrityMetricBean IMB_one = new IntegrityMetricBean();
       
        printState(IMB_one);
        System.out.println("Validating field values against expected defaults...");
        
        if(!isEqual("Default Custom Name",IMB_one.getCustomName())){
        	return "ERROR 1";
        }
        if(!isEqual("Default Metric Name",IMB_one.getMetricName())){
        	return "ERROR 2";
        }
        if(!isEqual("No Value Provided", IMB_one.getValueString())){
        	return "ERROR 3";
        }
        	
        System.out.println("VALIDATED\n");
        System.out.println("Editing IMB State to hold new custom name, metric name, long value...");
        IMB_one.setCustomName("Test Custom Name");
        IMB_one.setMetricName("Test Metric Name");
        IMB_one.setValue(3456789L);
        
        printState(IMB_one);
        
        System.out.println("Validating new field values against expected defaults...");
        if(!isEqual("Test Custom Name",IMB_one.getCustomName())){
        	return "ERROR 4";
        }
        
        if(!isEqual("Test Metric Name", IMB_one.getMetricName())){
        	return "ERROR 5";
        } 
        
        if(!isEqual(3456789L,IMB_one.getValueLong())){
        	return "ERROR 6";
        } 
        
        System.out.println("VALIDATED\n");
        System.out.println("Changing the value of the metric to String...");
        IMB_one.setValue("Metric Value 99");
        printState(IMB_one);
        System.out.println("Validating new String field value...");
        if(!isEqual("Metric Value 99", IMB_one.getValueString())){
        	return "ERROR 7";
        }
        
        System.out.println("VALIDATED\n");
        System.out.println("Changing the value of the metric to float...");
        IMB_one.setValue(1234.5678F);
        printState(IMB_one);
        System.out.println("Validating new Float value...");
        if(!isEqual(1234.5678F, IMB_one.getValueFloat())){
        	return "ERROR 8";
        }
        System.out.println("VALIDATED\n");
        
        return "PASS";
	}
	

	
}
