package com.cdp.integrityperformancetool.testing.isb;

import com.cdp.integrityperformancetool.IntegrityStatisticBean;
import com.cdp.integrityperformancetool.testing.imb.Imb_test_case_1;

public class Isb_Test_Runner {
	
	//NOTE: DateTime values will not be tested beyond spot checking
	//due to the inherent difficulties of creating two DateTime objects
	//at the exact same time, storing one with the ISB, the other in
	//the java class for comparison, and then comparing them at another 
	//time.
	
	private static boolean tc_1_result = false;
	
	public static void main(String args[]){
		System.out.println("");
        System.out.println("**  ** BEGIN TEST CASE 1  **  **");
    	System.out.println("");
		Isb_test_case_1 isb_TC_1 = new Isb_test_case_1();
		String tc_1_response = isb_TC_1.execute();
		System.out.println(tc_1_response);
    	if(tc_1_response.equals("PASS")) tc_1_result=true;
    	System.out.println("");
    	System.out.println("**  ** END TEST CASE 1  **  **");
    	System.out.println("");
		
    	System.out.println("Final Results:");
		System.out.println("Test Case 1: " + tc_1_result);
		//System.out.println("Test Case 2: " + tc_2_result);
		//System.out.println("Test Case 3: " + tc_3_result);
    	
	}
	
	

	public void printState(IntegrityStatisticBean isb){
		//print current state of imb object for sanity check and debug.
		System.out.println(" ** IntegrityStatisticBean State **");
		System.out.println(" **********************************");
		System.out.println("average: " + isb.getAverage());
		System.out.println("count: " + isb.getCount());
		System.out.println("endDate: " + isb.getEndDate());
		System.out.println("group: " + isb.getGroup());
		System.out.println("kind: " + isb.getKind());
		System.out.println("max: " + isb.getMax());
		System.out.println("min: " + isb.getMin());
		System.out.println("mode: " + isb.getMode());
		System.out.println("name: " + isb.getName());
		System.out.println("startDate: " + isb.getStartDate());
		System.out.println("sum: " + isb.getSum());
		System.out.println("totalCount: " + isb.getTotalCount());
		System.out.println("unit: " + isb.getUnit());
		System.out.println("used: " + isb.getUsed());
		System.out.println(" **********************************\n");
	}

	public boolean isEqual(Long expected, Long actual){
		if(expected.equals(actual)) return true;
		return false;
	}

	
	public boolean isEqual(String expected, String actual){
		if(expected.equals(actual)) return true;
		return false;
	}

}
