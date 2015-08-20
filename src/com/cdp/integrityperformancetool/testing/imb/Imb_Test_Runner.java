package com.cdp.integrityperformancetool.testing.imb;

import com.cdp.integrityperformancetool.IntegrityMetricBean;

/**
 * Created by bryan on 8/7/2015.
 */
public class Imb_Test_Runner {

	private static final Float epsilon = 0.0000000000001F; //for use comparing float values

	private static boolean tc_1_result = false;
	private static boolean tc_2_result = false;
	private static boolean tc_3_result = false;

    public static void main(String[] args){
    	System.out.println("");
        System.out.println("**  ** BEGIN TEST CASE 1  **  **");
    	System.out.println("");
    	Imb_test_case_1 imb_TC_1 = new Imb_test_case_1();
    	String tc_1_response = imb_TC_1.execute();//run test case 1 and output response
    	System.out.println(tc_1_response);
    	if(tc_1_response.equals("PASS")) tc_1_result=true;
    	System.out.println("");
    	System.out.println("**  ** END TEST CASE 1  **  **");
    	System.out.println("");

		System.out.println("");
		System.out.println("**  ** BEGIN TEST CASE 2  **  **");
		System.out.println("");
		Imb_test_case_2 imb_TC_2 = new Imb_test_case_2();
		String tc_2_response = imb_TC_2.execute();//run test case 1 and output response
		System.out.println(tc_2_response);
		if(tc_2_response.equals("PASS")) tc_2_result=true;
		System.out.println("");
		System.out.println("**  ** END TEST CASE 2  **  **");
		System.out.println("");

		System.out.println("");
		System.out.println("**  ** BEGIN TEST CASE 2  **  **");
		System.out.println("");
		Imb_test_case_3 imb_TC_3 = new Imb_test_case_3();
		String tc_3_response = imb_TC_3.execute();//run test case 1 and output response
		System.out.println(tc_3_response);
		if(tc_2_response.equals("PASS")) tc_3_result=true;
		System.out.println("");
		System.out.println("**  ** END TEST CASE 2  **  **");
		System.out.println("");

		System.out.println("Final Results:");
		System.out.println("Test Case 1: " + tc_1_result);
		System.out.println("Test Case 2: " + tc_2_result);
		System.out.println("Test Case 3: " + tc_3_result);
    }

	public void printState(IntegrityMetricBean imb){
		//print current state of imb object for sanity check and debug.
		System.out.println(" ** IntegrityMetricBean State **");
		System.out.println(" Custom Name: " + imb.getCustomName() + " (" + imb.getCustomName().getClass() + ")");
		System.out.println(" Metric Name: " + imb.getMetricName() + " (" + imb.getMetricName().getClass() + ")");
		if(imb.getValueType()==0){
			System.out.println(" Value: " + imb.getValueLong() + " (" + imb.getValueLong().getClass() + ")");
		} else if(imb.getValueType()==1){
			System.out.println(" Value: " + imb.getValueString() + " (" + imb.getValueString().getClass() + ")");
		} else {
			System.out.println(" Value: " + imb.getValueFloat() + " (" + imb.getValueFloat().getClass() + ")");
		}
		System.out.println(" Value Type: " + imb.getValueType());
		System.out.println(" *******************************\n");
	}

	public boolean isEqual(Long expected, Long actual){
		if(expected.equals(actual)) return true;
		return false;
	}

	public boolean isEqual(Float expected, Float actual){
		if(Math.abs(expected-actual)< epsilon) return true;
		return false;
	}

	public boolean isEqual(String expected, String actual){
		if(expected.equals(actual)) return true;
		return false;
	}


    
}
