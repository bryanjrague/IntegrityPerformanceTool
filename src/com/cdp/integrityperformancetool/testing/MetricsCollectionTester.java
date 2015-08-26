package com.cdp.integrityperformancetool.testing;

import com.cdp.integrityperformancetool.MetricsCollection;
import com.cdp.integrityperformancetool.IntegrityMetricBean;

public class MetricsCollectionTester {

	
	public static void main(String[] args) {
		
		/*Test Case 1
		 * create an empty collection
		 * add 3 imb objects to it:
		 * 		- 1 with a Long value
		 * 		- 1 with a String value
		 * 		- 1 with a Float value
		 * get the name of each
		 * get the value of each
		 * get the collection size
		 * add another imb object
		 * get the collection size
		 * change the name of an imb
		 */
		
		System.out.println(" START TEST CASE 1");
		System.out.println("Creating test collection 1...");
		MetricsCollection collection_1 = new MetricsCollection();
		IntegrityMetricBean imb_1_1 = new IntegrityMetricBean();
		imb_1_1.setCustomName("imb_1");
		imb_1_1.setMetricName("Metric A");
		imb_1_1.setValue("Data A");
		IntegrityMetricBean imb_1_2 = new IntegrityMetricBean();
		imb_1_2.setCustomName("imb_2");
		imb_1_2.setMetricName("Metric B");
		imb_1_2.setValue(23245L);
		IntegrityMetricBean imb_1_3 = new IntegrityMetricBean();
		imb_1_3.setCustomName("imb_3");
		imb_1_3.setMetricName("Metric C");
		imb_1_3.setValue(34.568435F);
		collection_1.addToCollection(imb_1_1);
		collection_1.addToCollection(imb_1_2);
		collection_1.addToCollection(imb_1_3);
		writeToString(collection_1);
		IntegrityMetricBean imb_1_4 = new IntegrityMetricBean();
		imb_1_1.setCustomName("imb_4");
		imb_1_1.setMetricName("Metric D");
		imb_1_1.setValue(156789); //try out the int setter
		collection_1.addToCollection(imb_1_4);
		writeToString(collection_1);
		collection_1.setCollectionName("New name!");
		writeToString(collection_1);
		System.out.println(" END TEST CASE 1\n");
		
		/*Test Case 2
		 * create a collection with 100 imbs
		 * remove 50 imbs
		 * clear the collection
		 * add back 100 imbs
		 * create a new collection using the 100 imbs now in the original colleciton.
		 * 
		 */
		System.out.println(" START TEST CASE 2");
		MetricsCollection collection_2 = createTestMC("large collection", 100);
		writeToString(collection_2);
		int[] remove = new int[50];
		for(int i=0;i<50;i++){ remove[i] = i; }
		collection_2.removeFromCollection(remove);
		writeToString(collection_2);
		collection_2.clearCollection();
		writeToString(collection_2);
		collection_2.addToCollection(createTestMC("temp", 100).getCollection());
		writeToString(collection_2);
		System.out.println(" END TEST CASE 2\n");
		/*
		 * Test Case 3
		 * time the creation of 100,000 imbs
		 *
		 */

		System.out.println(" START TEST CASE 3");
		System.out.println("Creating very large isb collection...");
		long startTime = System.nanoTime();
		MetricsCollection collection_3 = createTestMC("large collection", 100000);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms to create 100,000 imb collection");
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
	
	private static IntegrityMetricBean createTestImb(String cn, String mc, Long val){
		return new IntegrityMetricBean(cn, mc, val);
	}
	
	private static MetricsCollection createTestMC(String name, int num_imb){
		MetricsCollection temp_mc = new MetricsCollection(name);
		for(int i=0; i<num_imb; i++){
			temp_mc.addToCollection(createTestImb(i+"_customName", i+"_metricName",Long.valueOf(i)));
		}
		return temp_mc;
	}

}
