package com.cdp.integrityperformancetool.testing;

import com.cdp.integrityperformancetool.IntegrityMetricBean;
import com.cdp.integrityperformancetool.MetricsCollection;
import com.cdp.integrityperformancetool.util.MetricsFileReader;

public class MetricsFileReaderTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String filePath = "C:\\Users\\bryan\\IdeaProjects\\Integrity Performance Tool\\Input\\MetricsTestData01.txt";

		/*
		 * Test Case 1:
		 * test out the extractStaticMetric method against single line samples of the metric file.
		 * pull out various metrics that exist on the same line. 
		 * store them into IntegrityMetricBeans and MetricsCollections
		 * check the IntegrityMetricBean and MetricsCollections against expected values.
		 */

		print(" START TEST CASE 1 ");

		MetricsFileReader mfr = new MetricsFileReader(filePath);
		MetricsCollection test_collection1 = new MetricsCollection();
		print(mfr.getMetricsFilePath());
		print(mfr.getMapSize()+"");
		mfr.setMapSize(8 * 1024);
		print(mfr.getMapSize()+"");
		mfr.setMetricsFilePath("new\\Place");
		print(mfr.getMetricsFilePath());
		
		String testString = "Code Cache: peak=19641600, current=19390464, last gc=?, max=50331648";
		String valueString = mfr.extractStaticMetric(testString, "peak=");
		print(valueString);
		IntegrityMetricBean imb1 = new IntegrityMetricBean("Code Cache: Peak", 
														   "Peak Code Cache", 
														   Long.valueOf(valueString));
		test_collection1.addToCollection(imb1);
		
		
		String valueString2 = mfr.extractStaticMetric(testString, "current=");
		print("2: " + valueString2);
		IntegrityMetricBean imb2 = new IntegrityMetricBean("Code Cache: Current", 
														   "Current Code Cache", 
														   Long.valueOf(valueString2));
		test_collection1.addToCollection(imb2);
		
		String valueString3 = mfr.extractStaticMetric(testString, "last gc=");
		print("3: " + valueString3);
		IntegrityMetricBean imb3 = new IntegrityMetricBean();
		try{
			imb3.setCustomName("Code Cache: last gc");
			imb3.setMetricName("Current Code Cache"); 
			imb3.setValue(Long.valueOf(valueString3));
		} catch(NumberFormatException nfe){
			imb3.setCustomName("Code Cache: last gc");
			imb3.setMetricName("Current Code Cache"); 
			imb3.setValue(valueString3);
		}
		test_collection1.addToCollection(imb3);
		
		String valueString4 = mfr.extractStaticMetric(testString, "max=");
		print("4: " + valueString4);
		IntegrityMetricBean imb4 = new IntegrityMetricBean("Code Cache: max", 
														   "max Code Cache", 
														   Long.valueOf(valueString4));
		test_collection1.addToCollection(imb4);
		
		testString = "Server Time:	Wed May 27 11:54:46 PDT 2015";
		String valueString5 = mfr.extractStaticMetric(testString, "Server Time:");
		print("5: " + valueString5);
		IntegrityMetricBean imb5 = new IntegrityMetricBean();
		try{
			imb5.setCustomName("Server Time");
			imb5.setMetricName("Server Time"); 
			imb5.setValue(Long.valueOf(valueString5));
		} catch(NumberFormatException nfe){
			imb5.setCustomName("Server Time");
			imb5.setMetricName("Server Time"); 
			imb5.setValue(valueString5);
		}
		test_collection1.addToCollection(imb5);
		
		testString = "Driver Version:	11.2.0.1.0";
		String valueString6 = mfr.extractStaticMetric(testString, "Driver Version:");
		print("6: " + valueString6);
		IntegrityMetricBean imb6 = new IntegrityMetricBean();
		try{
			imb6.setCustomName("Driver Version");
			imb6.setMetricName("Driver Version"); 
			imb6.setValue(Long.valueOf(valueString5));
		} catch(NumberFormatException nfe){
			imb6.setCustomName("Driver Version");
			imb6.setMetricName("Driver Version"); 
			imb6.setValue(valueString6);
		}
		test_collection1.addToCollection(imb6);

		writeToString(test_collection1);
		print( "END TEST CASE 1 \n");

		/*
		 *Test Case 2:
		 * test use of the extractMetrics against static metrics (lines 1-37 of file)
		 * and dynamic metrics in the properties file.
		 * output data for consistency check.
		 */
		print(" START TEST CASE 2 ");
		MetricsFileReader mfr2 = new MetricsFileReader(filePath);

		MetricsCollection test_collection2 = mfr2.extractMetrics();
		writeToString(test_collection2);
		print("END TEST CASE 2 \n");

	}
	
	public static void print(String s){
		System.out.println(s);
	}
	
	private static void writeToString(MetricsCollection mc){
		System.out.println(" ** Metrics Collection Data for : " + mc.getCollectionName() + " **");
		System.out.println("MetricsCollection size: " + mc.getCollectionSize());
		int counter = 1;
		System.out.println("#: Custom Name, Metric Name, Value");
		for (IntegrityMetricBean imb : mc.getCollection()){
			StringBuffer output = new StringBuffer();
			int valType = imb.getValueType();
			output.append(counter + ": " 
					+ imb.getCustomName() + ", "
					+ imb.getMetricName() + ", ");
			if(valType==0) output.append(imb.getValueLong());
			else if(valType==1) output.append(imb.getValueString());
			else if(valType==2) output.append(imb.getValueFloat());
			counter++;
			System.out.println(output);
		}
	}

}
