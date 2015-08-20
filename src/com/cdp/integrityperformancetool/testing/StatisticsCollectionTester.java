package com.cdp.integrityperformancetool.testing;

import com.cdp.integrityperformancetool.IntegrityStatisticBean;
import com.cdp.integrityperformancetool.StatisticsCollection;
import org.joda.time.DateTime;
import org.joda.time.Period;

public class StatisticsCollectionTester {

	/**
	 * @param args
	 */
	
	public static String fs = java.lang.System.getProperty("file.separator");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Test Case 1:
		//create 10 Isbs
		//add an Isb
		//remove an Isb
		//create new collection using collection_1 as the pre-existing collection
		System.out.println(" START TEST CASE 1");
		System.out.println("Creating test collection 1...");
		StatisticsCollection collection_1 = createTestCollection("Test Collection 1", 10);
		System.out.println(" *************************** ");
		collection_1.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Adding new isb...");
		IntegrityStatisticBean isb_1_1 = new IntegrityStatisticBean();
		collection_1.addToCollection(isb_1_1);
		System.out.println(" *************************** ");
		collection_1.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Removing isb...");
		collection_1.removeFromCollection(isb_1_1);
		System.out.println(" *************************** ");
		collection_1.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Creating new collection using collection_1 data...");
		StatisticsCollection collection_1a = new StatisticsCollection("Duplicate SC", collection_1.getCollection());
		System.out.println(" *************************** ");
		collection_1a.writeToString();
		System.out.println(" *************************** ");
		
		System.out.println(" END TEST CASE 1\n");
		collection_1.clearCollection();
		collection_1a.clearCollection();
		/*Test Case 2: 
		 * Create collection with default constructor, starting with no Isb objects
		 * set the name of the collection
		 * add 4 ISB with fields:
		 * 1) all Long values are 543421
		 * 2) all Long values are 100545456
		 * 3) all Long values are 12
		 * 4) all Long values are 435935934934 
		 * test out all the compute... methods and ensure they are working correctly 
		 * test all get methods and make sure they are correct. 
		 */
		
		System.out.println(" START TEST CASE 2");
		System.out.println("Creating test collection 2...");
		StatisticsCollection collection_2 = new StatisticsCollection();
		collection_2.setName("Test Case 2 collection");
		IntegrityStatisticBean isb_2_1 = createTestIsb("isb_2_1", 543421L, new DateTime());
		IntegrityStatisticBean isb_2_2 = createTestIsb("isb_2_2", 100545456L, new DateTime());
		IntegrityStatisticBean isb_2_3 = createTestIsb("isb_2_3", 12L, new DateTime());
		IntegrityStatisticBean isb_2_4 = createTestIsb("isb_2_4", 435935934934L, new DateTime());
		collection_2.addToCollection(isb_2_1);
		collection_2.addToCollection(isb_2_2);
		collection_2.addToCollection(isb_2_3);
		collection_2.addToCollection(isb_2_4);
		System.out.println(" *************************** ");
		collection_2.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Running orderByIsbAverageValue()...");
		collection_2.orderByIsbAverageValue();
		System.out.println(" *************************** ");
		collection_2.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Running orderByIsbCountValue()...");
		collection_2.orderByIsbCountValue();
		System.out.println(" *************************** ");
		collection_2.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Running orderByIsbNameValue()...");
		collection_2.orderByIsbNameValue();
		System.out.println(" *************************** ");
		collection_2.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Running orderByIsbMaximumValue()...");
		collection_2.orderByIsbMaximumValue();
		System.out.println(" *************************** ");
		collection_2.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Running orderByIsbMinimumValue()...");
		collection_2.orderByIsbMinimumValue();
		System.out.println(" *************************** ");
		collection_2.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Running orderByIsbTotalCountValue()...");
		collection_2.orderByIsbTotalCountValue();
		System.out.println(" *************************** ");
		collection_2.writeToString();
		System.out.println(" *************************** ");
		System.out.println("fields accurate?: " + collection_2.areComputationFieldsAccurate());
		System.out.println("Getting collection data: ");
		System.out.println("Max: " + collection_2.getCollectionMaximumValue());
		System.out.println("Max object size: " + collection_2.getCollectionMaximumIsbArrayList().size());
		System.out.println("Max object: " + collection_2.getCollectionMaximumIsbArrayList().get(0).getName());
		System.out.println("Min: " + collection_2.getCollectionMinimumValue());
		System.out.println("Min object size: " + collection_2.getCollectionMinimumIsbArrayList().size());
		System.out.println("Min object: " + collection_2.getCollectionMinimumIsbArrayList().get(0).getName());
		System.out.println("Avg: " + collection_2.getCollectionAverageValue());
		System.out.println("Count: " + collection_2.getCollectionCountValue());
		System.out.println("Total Count " + collection_2.getCollectionTotalCountValue());
		System.out.println("fields accurate?: " + collection_2.areComputationFieldsAccurate());
		System.out.println(" END TEST CASE 2\n");
		collection_2.clearCollection();
		/*Test Case 3:
		 * create collection 3
		 * add 3 ISBs with fields:
		 *	1) high max, low min, mid avg, name starts with A
		 *	2) mid max, high min, low avg, name starts with M
		 *	3) low max, mid min, high avg, name starts with Z
		 *	
		 *	the run all orderBy... methods and review 
		 *	ensure that the ISBs are in the right order for each
		 *  add a new ISB, get all instance variables and check for accuracy 
		 *  	(they should provide updated values) 
		 */
		System.out.println(" START TEST CASE 3");
		System.out.println("Creating test collection 3");
		
		StatisticsCollection collection_3 = new StatisticsCollection();
		IntegrityStatisticBean isb_3_1 = new IntegrityStatisticBean();
		isb_3_1.setMax(500);
		isb_3_1.setAverage(250);
		isb_3_1.setMin(1);
		isb_3_1.setCount(101L);
		isb_3_1.setTotalCount(202L);
		isb_3_1.setName("az_isb_3_1");
		IntegrityStatisticBean isb_3_2 = new IntegrityStatisticBean();
		isb_3_2.setMax(250);
		isb_3_2.setAverage(1);
		isb_3_2.setMin(500);
		isb_3_2.setCount(102L);
		isb_3_2.setTotalCount(203L);
		isb_3_2.setName("a_isb_3_2");
		IntegrityStatisticBean isb_3_3 = new IntegrityStatisticBean();
		isb_3_3.setMax(1);
		isb_3_3.setAverage(500);
		isb_3_3.setMin(0);
		isb_3_3.setCount(103L);
		isb_3_3.setTotalCount(200L);
		isb_3_3.setName("m_isb_3_3");
		collection_3.addToCollection(isb_3_1);
		collection_3.addToCollection(isb_3_2);
		collection_3.addToCollection(isb_3_3);
		
		System.out.println(" *************************** ");
		collection_3.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Running orderByIsbAverageValue()...");
		collection_3.orderByIsbAverageValue();
		System.out.println(" *************************** ");
		collection_3.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Running orderByIsbCountValue()...");
		collection_3.orderByIsbCountValue();
		System.out.println(" *************************** ");
		collection_3.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Running orderByIsbNameValue()...");
		collection_3.orderByIsbNameValue();
		System.out.println(" *************************** ");
		collection_3.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Running orderByIsbMaximumValue()...");
		collection_3.orderByIsbMaximumValue();
		System.out.println(" *************************** ");
		collection_3.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Running orderByIsbMinimumValue()...");
		collection_3.orderByIsbMinimumValue();
		System.out.println(" *************************** ");
		collection_3.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Running orderByIsbTotalCountValue()...");
		collection_3.orderByIsbTotalCountValue();
		System.out.println(" *************************** ");
		collection_3.writeToString();
		System.out.println(" *************************** ");
		System.out.println("Adding new isb...");
		IntegrityStatisticBean isb_3_4 = new IntegrityStatisticBean();
		isb_3_4.setMax(100);
		isb_3_4.setAverage(100);
		isb_3_4.setMin("100");
		isb_3_4.setCount(100L);
		isb_3_4.setTotalCount(100L);
		isb_3_4.setName("01-isb_3_4");
		collection_3.addToCollection(isb_3_4);
		System.out.println("fields accurate?: " + collection_3.areComputationFieldsAccurate());
		System.out.println("Getting collection data: ");
		System.out.println("Max: " + collection_3.getCollectionMaximumValue());
		System.out.println("Max object size: " + collection_3.getCollectionMaximumIsbArrayList().size());
		System.out.println("Max object: " + collection_3.getCollectionMaximumIsbArrayList().get(0).getName());
		System.out.println("Min: " + collection_3.getCollectionMinimumValue());
		System.out.println("Min object size: " + collection_3.getCollectionMinimumIsbArrayList().size());
		System.out.println("Min object: " + collection_3.getCollectionMinimumIsbArrayList().get(0).getName());
		System.out.println("Avg: " + collection_3.getCollectionAverageValue());
		System.out.println("Count: " + collection_3.getCollectionCountValue());
		System.out.println("Total Count " + collection_3.getCollectionTotalCountValue());
		System.out.println("fields accurate?: " + collection_3.areComputationFieldsAccurate());
		System.out.println(" END TEST CASE 3\n");
		collection_3.clearCollection();
		
		
		
		/*Test Case 4:
		 * create collection 4
		 * time creation of 100,000 isb collection
		 * time all compute... methods
		 * time all StatisticCollection methods that not get/set against 100,000 isb collection
		 * 
		 */
		System.out.println(" START TEST CASE 4");
		System.out.println("Creating very large isb collection...");
		StatisticsCollection collection_4 = new StatisticsCollection();
		long startTime = System.nanoTime();
		for(int i=1;i<100000;i++){ collection_4.addToCollection(createTestIsb("isb_"+i,Long.valueOf(i), new DateTime())); }
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms to create 100,000 isb collection");
		
		System.out.println("Timing method: computeCollectionAverageValue()");
		startTime = System.nanoTime();
		collection_4.computeCollectionAverageValue();
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms\n");
		
		System.out.println("Timing method: computeCollectionCountValue()");
		startTime = System.nanoTime();
		collection_4.computeCollectionCountValue();
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms\n");
		
		System.out.println("Timing method: computeCollectionMaximumObject()");
		startTime = System.nanoTime();
		collection_4.computeCollectionMaximumObject();
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms\n");
		
		System.out.println("Timing method: computeCollectionMinimumObject()");
		startTime = System.nanoTime();
		collection_4.computeCollectionMinimumObject();
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms\n");
		
		System.out.println("Timing method: computeCollectionMinimumValue()");
		startTime = System.nanoTime();
		collection_4.computeCollectionMinimumValue();
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms\n");
		
		System.out.println("Timing method: computeCollectionTotalCountValue()");
		startTime = System.nanoTime();
		collection_4.computeCollectionTotalCountValue();
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms\n");
		
		System.out.println("Timing method: orderByIsbAverageValue()");
		startTime = System.nanoTime();
		collection_4.orderByIsbAverageValue();
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms\n");
		
		System.out.println("Timing method: orderByIsbCountValue()");
		startTime = System.nanoTime();
		collection_4.orderByIsbCountValue();
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms\n");
		
		System.out.println("Timing method: orderByIsbNameValue()");
		startTime = System.nanoTime();
		collection_4.orderByIsbNameValue();
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms\n");
		
		System.out.println("Timing method: orderByIsbMaximumValue()");
		startTime = System.nanoTime();
		collection_4.orderByIsbMaximumValue();
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms\n");
		
		System.out.println("Timing method: orderByIsbMinimumValue()");
		startTime = System.nanoTime();
		collection_4.orderByIsbMinimumValue();
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms\n");
		
		System.out.println("Timing method: orderByIsbTotalCountValue()");
		startTime = System.nanoTime();
		collection_4.orderByIsbTotalCountValue();
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("Took " + (duration/1000000) + "ms\n");
		
		
		System.out.println(" END TEST CASE 4\n");
	}  
	
	private static StatisticsCollection createTestCollection(String collectionName, int size){
		
		StatisticsCollection test_collection = new StatisticsCollection(collectionName);
		for(int i=0;i<size;i++){
			test_collection.addToCollection(createTestIsb("Test_ISB_"+i, new Long(i), new DateTime()));
		}
		return test_collection;
	}
	
	private static IntegrityStatisticBean createTestIsb(String name, Long val, DateTime time){
		
		IntegrityStatisticBean temp_isb = new IntegrityStatisticBean();
		temp_isb.setAverage(val);
		temp_isb.setCount(val);
		temp_isb.setEndDate(time);
		temp_isb.setGroup(name+"_group");
		temp_isb.setKind(name+"_kind");
		temp_isb.setMax(val);
		temp_isb.setMin(val);
		temp_isb.setMode(name+"_mode");
		temp_isb.setName(name);
		temp_isb.setStartDate(time.minus(Period.days(1)));
		temp_isb.setSum(val);
		temp_isb.setTotalCount(val);
		temp_isb.setUnit(name+"_unit");
		temp_isb.setUsed(name+"_used");
		return temp_isb;
	}
	
	
	
}
