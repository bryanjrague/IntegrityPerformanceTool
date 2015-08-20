package testing;

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
		// Create basic collection and the get... methods available.
		//create 10 Isbs and add to colleciton 1, save the file for review/accuracy
		StatisticsCollection collection_1 = createTestCollection("Test Collection 1", 10);
		collection_1.writeToFile("." + fs + "_StatisticsCollection_test_1.csv");
		collection_1.computeAllCollectionStatistics();
		printCollectionDetails(collection_1);
		
		/*Create collection 2
		 * add 4 ISB with fields:
		 * 1) all Long values are 543421
		 * 2) all Long values are 100545456
		 * 3) all Long values are 12
		 * 4) all Long values are 435935934934 
		 * test out all the compute... methods and ensure they are working correctly 
		 *  
		 */
		IntegrityStatisticBean isb_1 = createTestIsb("Test2_isb1", 543421L, new DateTime());
		IntegrityStatisticBean isb_2 = createTestIsb("Test2_isb2", 100545456L, new DateTime());
		IntegrityStatisticBean isb_3 = createTestIsb("Test2_isb3", 12L, new DateTime());
		IntegrityStatisticBean isb_4 = createTestIsb("Test2_isb4", 435935934934L, new DateTime());

		StatisticsCollection collection_2 = new StatisticsCollection("Test Collection 2");
		collection_2.addToCollection(isb_1);
		collection_2.addToCollection(isb_2);
		collection_2.addToCollection(isb_3);
		collection_2.addToCollection(isb_4);

		printCollectionDetails(collection_2);
		collection_2.computeAllCollectionStatistics();
		printCollectionDetails(collection_2);


		/*create collection 3
		 *add 3 ISBs with fields:
		 *	1) high max, low min, mid avg, name starts with A
		 *	2) mid max, high min, low avg, name starts with M
		 *	3) low max, mid min, high avg, name starts with Z
		 *	
		 *	the test out the orderBy..., methods and save each to a unique file for review. 
		 *	ensure that the ISBs are in the right order for each 
		 */
		
		 /* create collection 4 which is initially empty
		  * add 1 ISB
		  * check status of requiresStatisticsRecompute
		  * run all compute... methods, make sure fields are accurate
		  * add another ISB
		  * check status of requiresStatisticsRecompute
		  * run all compute...methods, make sure fields are accurate
		  * remove an ISB
		  * check status of requiresStatisticsRecompute
		  * run all compute...methods, make sure fields are accurate
		  * TODO: think if the recompute should be auto, and when it makes sense to do this.
		  */
	}  
	
	private static StatisticsCollection createTestCollection(String collectionName, int size){
		//takes in an integer "size" value and creates a StatisticsCollection object containing size
		//number of ISBs
		//see createTestIsb method for unique values provided to ISBs created.
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

	public static void printCollectionDetails(StatisticsCollection sc){
		//output collection info
		System.out.println("Collection data for:  " + sc.getCollectionName());
		System.out.println("Size: " + sc.getCollectionSize());
		System.out.println("Max: " + sc.getCollectionMaximumValue());
		System.out.println("Max Obj: " + sc.getCollectionMaximumIsbObject().get(0).getName());
		System.out.println("Min: " + sc.getCollectionMinimumValue());
		System.out.println("Min Obj: " + sc.getCollectionMinimumIsbObject().get(0).getName());
		System.out.println("Count: " + sc.getCollectionCountValue());
		System.out.println("Total Count: " + sc.getCollectionTotalCountValue());
		System.out.println("Average: " + sc.getCollectionAverageValue());
		System.out.println("");
	}
	
	
	
}
