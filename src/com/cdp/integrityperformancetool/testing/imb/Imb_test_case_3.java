package com.cdp.integrityperformancetool.testing.imb;

import com.cdp.integrityperformancetool.IntegrityMetricBean;

public class Imb_test_case_3 extends Imb_Test_Runner{


	/**
	 * Ism_test_case_3
	 * 
	 * Test Case Objectives: test the performance of creating and editing Ism objects.
	 * - create 10 Imb objects
	 * - output avg time to create
	 * - create 100 Imb objects
	 * - output avg time to create
	 * - create 1000 Imb objects
	 * - output avg time to create
	 * - create 10000 Imb objects
	 * - output avg time to create
	 * - create 100000 Imb object
	 * - out[ut avg time to create
	 * 
	 * Test Case executed on:
	 * 
	 * Test Case Notes:
	 * 
	 */

	public Imb_test_case_3(){
		System.out.println("Initialized Imb_test_case_2...");
	}

	public String execute(){

		long startTime = System.nanoTime();
		createImbObjs(10);
		long endTime = System.nanoTime();
		long duration1 = (endTime - startTime);
		long avg1 = duration1/10;
		System.out.println("creating 10 imb objects: ");
		System.out.println("duration: " + duration1 + " (ns)");
		System.out.println("avg per object: " + avg1 + " (ns)");

		startTime = System.nanoTime();
		createImbObjs(100);
		endTime = System.nanoTime();
		duration1 = (endTime - startTime);
		avg1 = duration1/100;
		System.out.println("creating 100 imb objects: ");
		System.out.println("duration: " + duration1 + " (ns)");
		System.out.println("avg per object: " + avg1 + " (ns)");

		startTime = System.nanoTime();
		createImbObjs(1000);
		endTime = System.nanoTime();
		duration1 = (endTime - startTime);
		avg1 = duration1/1000;
		System.out.println("creating 1000 imb objects: ");
		System.out.println("duration: " + duration1 + " (ns)");
		System.out.println("avg per object: " + avg1 + " (ns)");

		startTime = System.nanoTime();
		createImbObjs(10000);
		endTime = System.nanoTime();
		duration1 = (endTime - startTime);
		avg1 = duration1/10000;
		System.out.println("creating 10000 imb objects: ");
		System.out.println("duration: " + duration1 + " (ns)");
		System.out.println("avg per object: " + avg1 + " (ns)");

		startTime = System.nanoTime();
		createImbObjs(100000);
		endTime = System.nanoTime();
		duration1 = (endTime - startTime); //ms
		avg1 = duration1/100000;
		System.out.println("creating 100000 imb objects: ");
		System.out.println("duration: " + duration1 + " (ns)");
		System.out.println("avg per object: " + avg1 + " (ns)");

		return "PASS";
	}

	public void createImbObjs(int num){
		for(int i=0;i<num;i++){
			new IntegrityMetricBean("Test", "Test", 23456L);
		}
	}
}
