package com.cdp.integrityperformancetool;

import java.io.File;
import java.nio.file.StandardOpenOption;
import java.util.*;

import com.cdp.integrityperformancetool.util.MergeSort;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * The StatisticsCollection class abstracts a logical grouping of IntegrityStatisticBean objects. The grouping of
 * IntegrityStatisticBean objects are stored in the StatisticsCollection's "collection" attribute as an
 * ArrayList of IntegrityStatisticBeans. The StatisticsColleciton is capable of computing group totals for most of the
 * IntegrityStatisticBean attributes. For example, the StatisticsColleciton can compute and store the collection's
 * Average value, which is computed as the average of all IntegrityStatisticBean average values existing in the colleciton.
 *
 * The StatisticCollection has many group computation and sort methods,
 * as well as methods to add, remove, and get IntegrityStatisticBean objects from the collection attribute.
 * Standard <i>get</i> and <i>set</i> methods exist to access the instance attributes.
 *
 */
public class StatisticsCollection {

	private static DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss zzz YYYY");

	private ArrayList<IntegrityStatisticBean> collection;
	private Long collectionAverageValue;
	private Long collectionCountValue;
	private ArrayList<IntegrityStatisticBean> collectionMaximumIsbObjectArrayList;
	private Long collectionMaximumValue;
	private ArrayList<IntegrityStatisticBean> collectionMinimumIsbObjectArrayList;
	private Long collectionMinimumValue;
	private String collectionName;
	private Long collectionTotalCountValue;
	private Long collectionSumValue;
	private DateTime collectionEarliestStartDate;
	private DateTime collectionLatestStartDate;
	private DateTime collectionEarliestEndDate;
	private DateTime collectionLatestEndDate;
	//private Long collectionUsedValue;

	//The following boolean fields keep track of what Instance fields are up to date
	//if an Isb is added to/removed from the collection, then computations will need
	//to be redone before returning values.
	private boolean requireAvgValRecompute = false;
	private boolean requireCntValRecompute = false;
	private boolean requireMaxObjRecompute = false;
	private boolean requireMaxValRecompute = false;
	private boolean requireMinObjRecompute = false;
	private boolean requireMinValRecompute = false;
	private boolean requiresSumValRecompute = false;
	private boolean requireEarliestStartDateRecompute = false;
	private boolean requiresLatestStartDateRecompute = false;
	private boolean requireEarliestEndDateRecompute = false;
	private boolean requiresLatestEndDateRecompute = false;
	private boolean requireTotCntValRecompute = false;

	public StatisticsCollection(){
		this("New Integrity Statistics Collection", new ArrayList<IntegrityStatisticBean>());
	}

	public StatisticsCollection(String arg_collectionName){
		this(arg_collectionName, new ArrayList<IntegrityStatisticBean>());
	}

	public StatisticsCollection(String arg_collectionName, ArrayList<IntegrityStatisticBean> arg_collection){
		this.collection = arg_collection;
		this.collectionAverageValue = 0L;
		this.collectionCountValue = 0L;
		this.collectionMaximumIsbObjectArrayList = new ArrayList<IntegrityStatisticBean>();
		this.collectionMaximumValue = 0L;
		this.collectionMinimumIsbObjectArrayList = new ArrayList<IntegrityStatisticBean>();
		this.collectionMinimumValue = 0L;
		this.collectionName = arg_collectionName;
		this.collectionTotalCountValue = 0L;
		this.collectionSumValue = 0L;
		this.collectionEarliestStartDate = new DateTime();
		this.collectionLatestStartDate = new DateTime();
		this.collectionEarliestEndDate = new DateTime();
		this.collectionLatestEndDate = new DateTime();
		//compute all statistics if the arg_collection is not empty.
		if (getCollectionSize()>0) computeAllCollectionStatistics();

	}

	/**
	 * Adds a single IntegrityStatisticBean object to the collection.
	 * This action causes for all "recompute" flags to be raised, so that all previously computed
	 * StatisticsCollection values will be recomputed before being returned or used.
	 * @param arg_isb (IntegrityStatisticBean) - The IntegrityStatisticBean to add to the existing collection.
	 */
	public void addToCollection(IntegrityStatisticBean arg_isb){
		this.collection.add(arg_isb);
		this.requireAvgValRecompute = true;
		this.requireCntValRecompute = true;
		this.requireMaxObjRecompute = true;
		this.requireMaxValRecompute = true;
		this.requireMinObjRecompute = true;
		this.requireMinValRecompute = true;
		this.requiresSumValRecompute = true;
		this.requireTotCntValRecompute = true;
		this.requireEarliestStartDateRecompute = true;
		this.requiresLatestStartDateRecompute = true;
		this.requireEarliestEndDateRecompute = true;
		this.requiresLatestEndDateRecompute = true;
	}

	/**
	 * Adds an ArrayList of IntegrityStatisticBean objects to the collection.
	 * This action causes for all "recompute" flags to be raised, so that all previously computed
	 * StatisticsCollection values will be recomputed before being returned or used.
	 * @param arg_isb_arrayList (IntegrityStatisticBean) - The IntegrityStatisticBean to add to the existing collection.
	 */
	public void addToCollection(ArrayList<IntegrityStatisticBean> arg_isb_arrayList){
		for(IntegrityStatisticBean isb : arg_isb_arrayList){
			this.collection.add(isb);
		}
		this.requireAvgValRecompute = true;
		this.requireCntValRecompute = true;
		this.requireMaxObjRecompute = true;
		this.requireMaxValRecompute = true;
		this.requireMinObjRecompute = true;
		this.requireMinValRecompute = true;
		this.requiresSumValRecompute = true;
		this.requireTotCntValRecompute = true;
		this.requireEarliestStartDateRecompute = true;
		this.requiresLatestStartDateRecompute = true;
		this.requireEarliestEndDateRecompute = true;
		this.requiresLatestEndDateRecompute = true;
	}

	/**
	 * Return boolean true if no computational fields need to be updated before they should be returned.
	 * @return (boolean) - <b>true</b> if none of the StatisticsCollection's group computation values need to be
	 * recomputed. <b>false</b> if any of the group computation values need to be recomputed.
	 */
	public boolean areComputationFieldsAccurate(){
		if(!requireAvgValRecompute &&
				!requireCntValRecompute &&
				!requireMaxObjRecompute &&
				!requireMaxValRecompute &&
				!requireMinObjRecompute &&
				!requireMinValRecompute &&
				!requiresSumValRecompute &&
				!requireEarliestStartDateRecompute &&
				!requiresLatestStartDateRecompute &&
				!requireEarliestEndDateRecompute &&
				!requiresLatestEndDateRecompute &&
				!requireTotCntValRecompute) return true;
		return false;
	}

	private static Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {
		public int compare(String str1, String str2) {
			int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
			if (res == 0) {
				res = str1.compareTo(str2);
			}
			return res;
		}
	};

	/**
	 * Clears the current collection attribute of all IntegrityStatisticBean objects.
	 * This effectively removes all IntegrityStatisticBean objects from the StatisticsCollection and raises all
	 * group value recomputation flags.
	 */
	public void clearCollection(){
		this.collection.clear();
		this.requireAvgValRecompute = true;
		this.requireCntValRecompute = true;
		this.requireMaxObjRecompute = true;
		this.requireMaxValRecompute = true;
		this.requireMinObjRecompute = true;
		this.requireMinValRecompute = true;
		this.requiresSumValRecompute = true;
		this.requireTotCntValRecompute = true;
		this.requireEarliestStartDateRecompute = true;
		this.requiresLatestStartDateRecompute = true;
		this.requireEarliestEndDateRecompute = true;
		this.requiresLatestEndDateRecompute = true;
	}

	/**
	 * Combines all duplicate named IntegrityStatisticBean objects in the collection into a single IntegrityStatisticBean
	 * object in the collection, and removes the duplicates. Each individual IntegrityStatisticBean's values are combined
	 * and recomputed appropriately into the final collapsed Statistic.
	 * @param arg_statName (String) - The name attribute value of the IntegrityStatisticBean to combine all duplicates of.
	 * @param arg_statGroup (String) - The group attribute value of the IntegrityStatisticBean to combine all duplicates of.
	 *                      This is used to ensure that only exact duplicates are combined, and not any statistics that have
	 *                      the same name, but different groupings.
	 */
	public void collapseStatistic(String arg_statName, String arg_statGroup){

		StatisticsCollection temp_collection = new StatisticsCollection();
		Long cumulative_count = 0L;
		Long cumulative_totalCount = 0L;
		Long cumulative_sum = 0L;
		ArrayList<IntegrityStatisticBean> remove_isbs = new ArrayList<IntegrityStatisticBean>();
		int counter = 1;
		DateTime startDate = new DateTime();
		DateTime endDate = new DateTime();


		for(IntegrityStatisticBean isb : this.getCollection()){
			if (isb.getName().equals(arg_statName) && isb.getGroup().equals(arg_statGroup)){
				temp_collection.addToCollection(isb);
				cumulative_count = cumulative_count + isb.getCount();
				cumulative_totalCount = cumulative_totalCount + isb.getTotalCount();
				cumulative_sum = cumulative_sum + isb.getSum();
				remove_isbs.add(isb);

				if(counter==1){
					startDate = isb.getStartDate();
					endDate = isb.getEndDate();
				} else{
					if(isb.getStartDate().getMillis()<startDate.getMillis()){
						startDate = isb.getStartDate();
					}
					if(isb.getEndDate().getMillis()>endDate.getMillis()){
						endDate = isb.getEndDate();
					}

				}
				counter++;
			}
		}

		//temp_collection.writeToString();
		temp_collection.computeAllCollectionStatistics();
		IntegrityStatisticBean collapsedStatistic = temp_collection.getCollectionMaximumIsbArrayList().get(0);
		collapsedStatistic.setAverage(temp_collection.getCollectionAverageValue());
		//System.out.println("avg: " + temp_collection.getCollectionAverageValue());
		collapsedStatistic.setCount(cumulative_count);
		collapsedStatistic.setGroup(arg_statGroup);
		collapsedStatistic.setName(arg_statName);
		collapsedStatistic.setMin(temp_collection.getCollectionMinimumValue());
		//System.out.println("min: " + temp_collection.getCollectionMinimumValue());
		collapsedStatistic.setMax(temp_collection.getCollectionMaximumValue());
		//System.out.println("max: " +temp_collection.getCollectionMaximumValue());
		collapsedStatistic.setTotalCount(cumulative_totalCount);
		collapsedStatistic.setStartDate(startDate);
		collapsedStatistic.setEndDate(endDate);
		//System.out.println("start:" + startDate + ", endDate: " + endDate);
		collapsedStatistic.setSum(cumulative_sum);
		collapsedStatistic.setStartDate(temp_collection.getCollectionEarliestStartDate());
		collapsedStatistic.setEndDate(temp_collection.getCollectionLatestEndDate());

		for(int isb=0;isb<remove_isbs.size();isb++){
			this.removeFromCollection(remove_isbs.get(isb));
		}

		this.addToCollection(collapsedStatistic);
		remove_isbs.clear();
		//writeToString();
	}

	/**
	 * Executes the <i>collapseStatistic</i> method against all IntegrityStatisticBean objects existing in the
	 * collection.
	 */
	public void collapseAllStatistics(){
		//performs the same operation as collapseStatistic, except for all Statistics
		//in the collection.

		HashMap<String, String> allNames = this.getAllUniqueNameGroupPairs();

		for(String name : allNames.keySet()){
			this.collapseStatistic(name, allNames.get(name));
		}

	}

	/**
	 * Groups IntegrityStatisticBeans by start and end date. Those that have the same start and end date are combined
	 * into a single IntegrityStatisticBean.
	 */
	public void collapseStatisticsByDate(){

		StatisticsCollection temp_collection = new StatisticsCollection();

		for(int i=0;i<this.getCollectionSize();i++){
			DateTime endDate = this.getCollectionObject(i).getEndDate();
			DateTime startDate = this.getCollectionObject(i).getStartDate();
			DateTime next_endDate;
			DateTime next_startDate;

			int[] endDateID_array = {endDate.getYear(), endDate.getMonthOfYear(), endDate.getDayOfMonth()};
			int[] startDateID_array = {startDate.getYear(), startDate.getMonthOfYear(), startDate.getDayOfMonth()};
			int[] nextEndDateID_array = new int[3];
			int[] nextStartDateID_array = new int[3];

			if((i+1)<this.getCollectionSize()){
				int increment = 1;
				do {
					next_endDate = this.getCollectionObject(i + increment).getEndDate();
					next_startDate = this.getCollectionObject(i + increment).getStartDate();

					nextEndDateID_array[0] = next_endDate.getYear();
					nextEndDateID_array[1] = next_endDate.getMonthOfYear();
					nextEndDateID_array[2] = next_endDate.getDayOfMonth();

					nextStartDateID_array[0] = next_startDate.getYear();
					nextStartDateID_array[1] = next_startDate.getMonthOfYear();
					nextStartDateID_array[2] = next_startDate.getDayOfMonth();

					increment += 1;

				} while(endDateID_array[0]==nextEndDateID_array[0] &&
						endDateID_array[1]==nextEndDateID_array[1] &&
						endDateID_array[2]==nextEndDateID_array[2] &&
						startDateID_array[0]==nextStartDateID_array[0] &&
						startDateID_array[1]==nextStartDateID_array[1] &&
						startDateID_array[2]==nextStartDateID_array[2] &&
						(i+increment)<getCollectionSize());

				int endIndex = i + increment - 1;
				//int endIndex = i + increment - 1;
				//System.out.println("i: " + i +  ", increment: " + increment + ", endIndex: " + endIndex);

				if(endIndex>i){ //then combine the IntegrityStatisticBeans
					StatisticsCollection temp_collection2 = new StatisticsCollection();
					for(int j=i;j<endIndex;j++) { temp_collection2.addToCollection(getCollectionObject(j)); }

					IntegrityStatisticBean temp_isb = new IntegrityStatisticBean();

					temp_isb.setStartDate(temp_collection2.getCollectionEarliestStartDate());
					temp_isb.setEndDate(temp_collection2.getCollectionLatestEndDate());
					temp_isb.setGroup(getCollectionObject(i).getGroup());
					temp_isb.setName(getCollectionObject(i).getName());
					temp_isb.setKind(getCollectionObject(i).getKind());
					temp_isb.setUnit(getCollectionObject(i).getUnit());

					Long runningCount = 0L;
					for(IntegrityStatisticBean isb : temp_collection2.getCollection()) {
						runningCount += isb.getCount();
					}

					temp_isb.setCount(runningCount);
					temp_isb.setTotalCount(temp_collection2.getCollectionTotalCountValue());
					temp_isb.setSum(temp_collection2.getCollectionSumValue());
					temp_isb.setUsed(getCollectionObject(i).getUsed());
					temp_isb.setMin(temp_collection2.getCollectionMinimumValue());
					temp_isb.setMax(temp_collection2.getCollectionMaximumValue());
					temp_isb.setAverage(temp_collection2.getCollectionAverageValue());
					temp_isb.setMode(getCollectionObject(i).getMode());
					//System.out.println("wrote to temp");
					temp_collection.addToCollection(temp_isb);

					i = endIndex-1;

				} else { //just add the single Integrity StatisticBean back
					temp_collection.addToCollection(getCollectionObject(i));
				}
			} else {
				//just add the single Integrity StatisticBean back
				temp_collection.addToCollection(getCollectionObject(i));
			}
		}
		//finally, replace the collapsed collection as this.collection
		this.clearCollection();
		for(IntegrityStatisticBean isb : temp_collection.getCollection()) { this.addToCollection(isb); }
		//System.out.println(" ** ** temp ** **");
		//temp_collection.writeToString();
		//System.out.println(" ** ** temp ** **");
		//this.writeToString();
	}

	/**
	 * Executes all StatisticsCollection group computation methods.
	 */
	public void computeAllCollectionStatistics(){
		//run all computation methods so that the collection has updated field values.
		this.computeCollectionAverageValue();
		this.computeCollectionCountValue();
		this.computeCollectionMaximumValue();
		this.computeCollectionMaximumObject();
		this.computeCollectionMinimumValue();
		this.computeCollectionMinimumObject();
		this.computeCollectionSumValue();
		this.computeCollectionTotalCountValue();
		this.computeCollectionEarliestStartDate();
		this.computeCollectionLatestStartDate();
		this.computeCollectionEarliestEndDate();
		this.computeCollectionLatestEndDate();
	}

	/**
	 * Computes the StatisticsCollection collectionAverageValue attribute value by averaging the average value of all
	 * IntegrityStatisticBean objects in the collection.
	 */
	public void computeCollectionAverageValue(){
		Long cumulativeSum = 0L;
		for (IntegrityStatisticBean isb : this.getCollection()){
			cumulativeSum = cumulativeSum + isb.getAverage();
		}
		this.collectionAverageValue = (cumulativeSum)/this.getCollectionSize();
		this.requireAvgValRecompute = false;
	}

	/**
	 * Computes the StatisticsCollection collectionCountValue attribute value by computing the total of all
	 * IntegrityStatisticBean objects count values in the collection.
	 */
	public void computeCollectionCountValue(){
		Long cumulativeSum = 0L;
		for (IntegrityStatisticBean isb : this.getCollection()){
			cumulativeSum = cumulativeSum + isb.getCount();
		}
		this.collectionCountValue = cumulativeSum;
		this.requireCntValRecompute = false;
	}

	/**
	 * Determines the earliest endDate used by an IntegrityStatisticBean object in the StatisticsCollection's collection
	 * and stores this value as a org.joda.time.DateTime object in the collectionEarliestEndDate attribute.
	 */
	public void computeCollectionEarliestEndDate() {
		DateTime test_date = null;
		if (this.getCollectionSize() > 1) {
			for (IntegrityStatisticBean isb : this.getCollection()) {
				if (test_date == null) {
					test_date = isb.getEndDate();
				} else {
					if (isb.getEndDate().getMillis() < test_date.getMillis()) {
						test_date = isb.getEndDate();
					}
				}
			}
		} else if (this.getCollectionSize() == 1) {
			test_date = this.collection.get(0).getEndDate();
		} else {
			//there is nothing in the collection to work with...
		}
		this.collectionEarliestEndDate = test_date;
		this.requireEarliestEndDateRecompute = false;
	}

	/**
	 * Determines the earliest startDate used by an IntegrityStatisticBean object in the StatisticsCollection's collection
	 * and stores this value as a org.joda.time.DateTime object in the collectionEarliestStartDate attribute.
	 */
	public void computeCollectionEarliestStartDate(){
		DateTime test_date = null;
		if (this.getCollectionSize() > 1) {
			for (IntegrityStatisticBean isb : this.getCollection()) {
				if (test_date == null) {
					test_date = isb.getStartDate();
				} else {
					if (isb.getStartDate().getMillis() < test_date.getMillis()) {
						test_date = isb.getStartDate();
					}
				}
			}
		} else if (this.getCollectionSize() == 1) {
			test_date = this.getCollectionObject(0).getStartDate();
		} else {
			//there is nothing in the collection to work with...
		}
		this.collectionEarliestStartDate = test_date;
		this.requireEarliestStartDateRecompute = false;
	}

	/**
	 * Determines the latest endDate used by an IntegrityStatisticBean object in the StatisticsCollection's collection
	 * and stores this value as a org.joda.time.DateTime object in the collectionLatestEndDate attribute.
	 */
	public void computeCollectionLatestEndDate() {
		DateTime test_date = null;
		if (this.getCollectionSize() > 1) {
			for (IntegrityStatisticBean isb : this.getCollection()) {
				if (test_date == null) {
					test_date = isb.getEndDate();
				} else {
					if (isb.getEndDate().getMillis() > test_date.getMillis()) {
						test_date = isb.getEndDate();
					}
				}
			}
		} else if (this.getCollectionSize() == 1) {
			test_date = getCollectionObject(0).getEndDate();
		} else {
			//there is nothing in the collection to work with...
		}
		this.collectionLatestEndDate = test_date;
		this.requiresLatestEndDateRecompute = false;
	}

	/**
	 * Determines the latest startDate used by an IntegrityStatisticBean object in the StatisticsCollection's collection
	 * and stores this value as a org.joda.time.DateTime object in the collectionEarliestStartDate attribute.
	 */
	public void computeCollectionLatestStartDate(){
		DateTime test_date = null;
		if (this.getCollectionSize() > 1) {
			for (IntegrityStatisticBean isb : this.getCollection()) {
				if (test_date == null) {
					test_date = isb.getStartDate();
				} else {
					if (isb.getStartDate().getMillis() > test_date.getMillis()) {
						test_date = isb.getStartDate();
					}
				}
			}
		} else if (this.getCollectionSize() == 1) {
			test_date = this.getCollectionObject(0).getStartDate();
		} else {
			//there is nothing in the collection to work with...
		}
		this.collectionLatestStartDate = test_date;
		this.requiresLatestStartDateRecompute = false;
	}

	/**
	 * Determines the IntegrityStatistic object(s) in the collection which have the greatest max value. The object(s)
	 * are saved as an ArrayList in the collectionMaximumIsbObjectArrayList attribute, which will contain more than
	 * one IntegrityStatisticBean object if more than one have the same max value that is also the absolute max value
	 * of the collection.
	 */
	public void computeCollectionMaximumObject(){

		if(this.getRequireMaxObjRecompute()) {
			this.computeCollectionMaximumValue();
		}

		ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
		for (IntegrityStatisticBean isb : this.getCollection()){
			//use known max of collection to return the object(s) equal to this max.
			if (isb.getMax()==this.collectionMaximumValue) temp_isb_arrayList.add(isb);
		}
		this.collectionMaximumIsbObjectArrayList = temp_isb_arrayList;
		this.requireMaxObjRecompute = false;
	}

	/**
	 * Computes the greatest max value for all IntegrityStatisticBean objects in the collection and saves this value as
	 * the collectionMaximumValue attribute.
	 */
	public void computeCollectionMaximumValue() {
		if (this.getCollectionSize()>1) {
			//retrieve all individual maximums into Long[]
			Long[] allMaxVals = new Long[this.getCollectionSize()];
			for (int i = 0; i < this.getCollectionSize(); i++) {
				allMaxVals[i] = this.getCollectionObject(i).getMax();
			}

			//use the Mergesort algorithm to determine the maximum value
			MergeSort maxSorter = new MergeSort();
			maxSorter.sort(allMaxVals); //max values are now sorted lowest to highest
			this.collectionMaximumValue = allMaxVals[allMaxVals.length-1];

		} else if (this.getCollectionSize()==1){
			this.collectionMaximumValue = this.getCollectionObject(0).getMax();
		} else {
			System.out.println("WARNING: COMPUTE MAX VALUE METHOD CALLED WITH NO COLLECTION");
			//was called with an empty collection
			//**TODO: throw error or assign default value?
		}
		this.requireMaxValRecompute = false;

	}

	/**
	 * Determines the IntegrityStatistic object(s) in the collection which have the lowest min value. The object(s)
	 * are saved as an ArrayList in the collectionMinimumIsbObjectArrayList attribute, which will contain more than
	 * one IntegrityStatisticBean object if more than one have the same min value that is also the absolute min value
	 * of the collection.
	 */
	public void computeCollectionMinimumObject(){
		if(this.getRequireMinValRecompute()) {
			this.computeCollectionMinimumValue();
		}

		ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
		for (IntegrityStatisticBean isb : this.getCollection()){
			//use known min of collection to return the object(s) equal to this min.
			if(isb.getMin()==this.collectionMinimumValue) temp_isb_arrayList.add(isb);
		}
		this.collectionMinimumIsbObjectArrayList = temp_isb_arrayList;
		this.requireMinObjRecompute = false;
	}

	/**
	 * Computes the lowest min value for all IntegrityStatisticBean objects in the collection and saves this value as
	 * the collectionMinimumValue attribute.
	 */
	public void computeCollectionMinimumValue(){
		if (this.getCollectionSize()>1) {
			//retrieve all individual minimums into Long[]
			Long[] allMinVals = new Long[this.getCollectionSize()];
			for (int i = 0; i < this.getCollectionSize(); i++) {
				allMinVals[i] = this.getCollectionObject(i).getMin();
			}

			//use the Mergesort algorithm to determine the minimum value
			MergeSort minSorter = new MergeSort();
			minSorter.sort(allMinVals); //min values are now sorted lowest to highest
			this.collectionMinimumValue = allMinVals[0];

		} else if (this.getCollectionSize()==1){
			this.collectionMinimumValue = this.getCollectionObject(0).getMin();
		} else {
			System.out.println("WARNING: COMPUTE MIN VALUE METHOD CALLED WITH NO COLLECTION");
			//was called with an empty collection...
			//**TODO: throw error or assign default value?
		}
		this.requireMinValRecompute = false;
	}

	/**
	 * Computes the collectionSumValue attribute value by adding together all IntegrityStatisticBean sum values in the
	 * collection.
	 */
	public void computeCollectionSumValue(){
		Long cumulativeSum = 0L;
		for (IntegrityStatisticBean isb : this.getCollection()){
			cumulativeSum = cumulativeSum + isb.getSum();
		}
		this.collectionSumValue = cumulativeSum;
		this.requiresSumValRecompute = false;
	}

	/**
	 * Computes the collectionTotalCountValue attribute value by adding together all IntegrityStatisticBean totalCount
	 * values in the collection.
	 */
	public void computeCollectionTotalCountValue(){
		Long cumulativeSum = 0L;
		for (IntegrityStatisticBean isb : this.getCollection()){
			cumulativeSum = cumulativeSum + isb.getTotalCount();
		}
		this.collectionTotalCountValue = cumulativeSum;
		this.requireTotCntValRecompute = false;
	}

	/**
	 * Returns a HashMap of {name: group} for all unique IntegrityStatisticBean name-group pairs existing in the collection.
	 * @return HashMap {String: String} representing all unique name-group pairs of IntegrityStatisticBean objects in the collection.
	 */
	public HashMap<String, String> getAllUniqueNameGroupPairs(){
		HashMap<String, String> uniqueNames = new HashMap<String, String>();

		for(IntegrityStatisticBean isb : this.getCollection()) {
			if (!uniqueNames.containsKey(isb.getName())) {
				uniqueNames.put(isb.getName(), isb.getGroup());
			}
		}
		return uniqueNames;
	}

	/**
	 * Returns an ArrayList of all IntegrityStatisticBean objects currently in the StatisticsCollection's collection.
	 * @return ArrayList
	 */
	public ArrayList<IntegrityStatisticBean> getCollection(){ return this.collection; }

	/**
	 * Returns the Long value representing the collectionAverageValue attribute.
	 * @return (Long) - the value of the StatisticsCollection's average value.
	 */
	public Long getCollectionAverageValue(){
		if(this.getRequireAvgValRecompute()) {
			this.computeCollectionAverageValue();
		}
		return this.collectionAverageValue;
	}

	/**
	 * Returns the Long value representing the collectionCountValue attribute.
	 * @return (Long) - the value of the StatisticsCollection's count value.
	 */
	public Long getCollectionCountValue(){
		if(this.getRequireCntValRecompute()) {
			this.computeAllCollectionStatistics();
		}
		return this.collectionCountValue;
	}

	/**
	 * Returns the DateTime value representing the collectionEarliestEndDate attribute.
	 * @return (org.joda.time.DateTime) - the value of the StatisticsCollection's earliest end date.
	 */
	public DateTime getCollectionEarliestEndDate(){
		if(this.getRequireEarliestEndDateRecompute()) {
			this.computeCollectionEarliestEndDate();
		}
		return this.collectionEarliestEndDate;

	}

	/**
	 * Returns the DateTime value representing the collectionEarliestStartDate attribute.
	 * @return (org.joda.time.DateTime) - the value of the StatisticsCollection's earliest start date.
	 */
	public DateTime getCollectionEarliestStartDate(){
		if(this.getRequireEarliestStartDateRecompute()) {
			this.computeCollectionEarliestStartDate();
		}
		return this.collectionEarliestStartDate;
	}

	/**
	 * Returns the DateTime value representing the collectionLatestEndDate attribute.
	 * @return (org.joda.time.DateTime) - the value of the StatisticsCollection's latest end date.
	 */
	public DateTime getCollectionLatestEndDate(){
		if(this.getRequireLatestEndDateRecompute()) {
			this.computeCollectionLatestEndDate();
		}
		return this.collectionLatestEndDate;

	}

	/**
	 * Returns the DateTime value representing the collectionLatestStartDate attribute.
	 * @return (org.joda.time.DateTime) - the value of the StatisticsCollection's latest start date.
	 */
	public DateTime getCollectionLatestStartDate(){
		if(this.getRequireLatestStartDateRecompute()) {
			this.computeCollectionEarliestStartDate();
		}
		return this.collectionLatestStartDate;

	}

	/**
	 * Returns the StatisticsCollection's IntegrityStatisticBean object(s) holding the greatest max value in the collection.
	 * @return ArrayList(IntegrityStatisticBean) - the IntegrityStatisticBean(s) with the greatest max value in
	 * the collection.
	 */
	public ArrayList<IntegrityStatisticBean> getCollectionMaximumIsbArrayList(){
		if(this.getRequireMaxObjRecompute()) {
			this.computeCollectionMaximumObject();
		}
		return this.collectionMaximumIsbObjectArrayList;
	}

	/**
	 * Returns the StatisticCollection's maximum value.
	 * @return (Long) - the maximum value in the collection.
	 */
	public Long getCollectionMaximumValue(){
		if(this.getRequireMaxValRecompute()) {
			this.computeCollectionMaximumValue();
		}
		return this.collectionMaximumValue;
	}

	/**
	 * Returns the StatisticsCollection's IntegrityStatisticBean object(s) holding the lowest min value in the collection.
	 * @return ArrayList(IntegrityStatisticBean) - the IntegrityStatisticBean(s) with the lowest min value in
	 * the collection.
	 */
	public ArrayList<IntegrityStatisticBean> getCollectionMinimumIsbArrayList(){
		if(this.getRequireMinObjRecompute()) {
			this.computeCollectionMinimumObject();
		}
		return this.collectionMinimumIsbObjectArrayList;
	}

	/**
	 * Returns the StatisticCollection's minimum value.
	 * @return (Long) - the minimum value in the collection.
	 */
	public Long getCollectionMinimumValue(){
		if(this.getRequireMinValRecompute()) {
			this.computeCollectionMinimumValue();
		}
		return this.collectionMinimumValue;
	}

	/**
	 * Returns the current value of the StatisticsCollection collectionName attribute.
	 * @return (String) - returns the name of the StatisticsCollection.
	 */
	public String getCollectionName(){ return this.collectionName; }

	/**
	 * Returns a single IntegrityStatisticBean object found at the provided integer index.
	 * @param index (int) - the index within the collection to retrieve.
	 * @return IntegrityStatisticBean at the collection index requested.
	 */
	public IntegrityStatisticBean getCollectionObject(int index){
		try{
			return this.collection.get(index);
		} catch(IndexOutOfBoundsException ioobe){
			System.out.println("StatisticsCollection.getCollectionObject() - ERROR: " + ioobe);
			System.out.println("Returning new default IntegrityStatisticBean!");
			return new IntegrityStatisticBean();
		}
	}

	/**
	 * Returns the number of IntegrityStatisticBean objects in the collection.
	 * @return (int) - Integer number of IntegrityStatisticBean objects in the collection.
	 */
	public int getCollectionSize(){ return this.collection.size(); }

	/**
	 * Returns the collectionSumValue attribute for the StatisticsCollection object.
	 * Recomputes the attribute value first if needed.
	 * @return (Long) - the cumulative sum of  the "sum" attribute for all IntegrityStatisticBean objects in the collection.
	 */
	public Long getCollectionSumValue(){
		if(this.getRequiredSumValRecompute()) {
			this.computeCollectionSumValue();
		}
		return this.collectionSumValue;
	}

	/**
	 * Returns the collectionTotalCountValue attribute for the StatisticsCollection object.
	 * Recomputes the attribute value first if needed.
	 * @return (Long) - the cumulative sum of the "totalCount" attribute for all IntegrityStatisticBean objects in the collection.
	 */
	public Long getCollectionTotalCountValue(){
		if(this.getRequireTotCntValRecompute()) {
			this.computeCollectionTotalCountValue();
		}
		return this.collectionTotalCountValue;
	}

	private boolean getRequireAvgValRecompute(){ return this.requireAvgValRecompute; }
	private boolean getRequireCntValRecompute(){ return this.requireCntValRecompute; }
	private boolean getRequireEarliestEndDateRecompute() { return this.requireEarliestEndDateRecompute; }
	private boolean getRequireEarliestStartDateRecompute() { return this.requireEarliestStartDateRecompute; }
	private boolean getRequireLatestEndDateRecompute() { return this.requiresLatestEndDateRecompute; }
	private boolean getRequireLatestStartDateRecompute() { return this.requiresLatestStartDateRecompute; }
	private boolean getRequireMaxObjRecompute(){ return this.requireMaxObjRecompute; }
	private boolean getRequireMaxValRecompute(){ return this.requireMaxValRecompute; }
	private boolean getRequireMinObjRecompute(){ return this.requireMinObjRecompute; }
	private boolean getRequireMinValRecompute(){ return this.requireMinValRecompute; }
	private boolean getRequiredSumValRecompute() { return this.requiresSumValRecompute; }
	private boolean getRequireTotCntValRecompute(){ return this.requireTotCntValRecompute; }

	/**
	 * Reorders the IntegrityStatisticBean objects in the collection so that they are listed from lowest average value
	 * to greatest average value.
	 */
	public void orderByIsbAverageValue() {
		this.orderByAttributeValue("Average");
	}

	/**
	 * Reorders the IntegrityStatisticBean objects in the collection so that they are listed from lowest count value
	 * to greatest count value.
	 */
	public void orderByIsbCountValue() {
		this.orderByAttributeValue("Count");
	}

	/**
	 * Reorders the IntegrityStatisticBean objects in the collection so that they are listed in aplhabetical order.
	 */
	public void orderByIsbNameValue() {
		Hashtable<String, ArrayList<IntegrityStatisticBean>> name_isb_hashtable = new Hashtable<String, ArrayList<IntegrityStatisticBean>>();
		String[] allNames = new String[this.getCollectionSize()];
		ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();

		for (int i=0; i<this.getCollectionSize();i++){
			IntegrityStatisticBean currIsb = this.getCollectionObject(i);
			String currName = this.getCollectionObject(i).getName();
			allNames[i] = currName;
			if (name_isb_hashtable.containsKey(currName)){
				ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
				temp_isb_arrayList = name_isb_hashtable.get(currName);
				temp_isb_arrayList.add(currIsb);
				name_isb_hashtable.put(currName, temp_isb_arrayList);
			} else {
				ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
				temp_isb_arrayList.add(currIsb);
				name_isb_hashtable.put(currName, temp_isb_arrayList);
			}
		}

		Arrays.sort(allNames, ALPHABETICAL_ORDER); //TODO: check if this really works...
		//names are now sorted alphabetically
		//iterate through array and place isbs into new ArrayList
		for (int j=0;j<this.getCollectionSize();j++){
			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
			temp_isb_arrayList = name_isb_hashtable.get(allNames[j]);
			for (IntegrityStatisticBean isb : temp_isb_arrayList){
				ordered_isb_arrayList.add(isb);
			}
		}

		for (int k=0;k<this.getCollectionSize();k++) this.collection.set(k, ordered_isb_arrayList.get(k));
		//collection is now in order of names according to alphabetical order
		return;
	}

	/**
	 * Reorders the IntegrityStatisticBean objects in the collection so that they are listed from lowest max value
	 * to greatest max value.
	 */
	public void orderByIsbMaximumValue() {
		this.orderByAttributeValue("Maximum");
	}

	/**
	 * Reorders the IntegrityStatisticBean objects in the collection so that they are listed from lowest min value
	 * to greatest min value.
	 */
	public void orderByIsbMinimumValue() {
		this.orderByAttributeValue("Minimum");
	}

	/**
	 * Reorders the IntegrityStatisticBean objects in the collection so that they are listed from lowest sum value
	 * to greatest sum value.
	 */
	public void orderByIsbSumValue() {
		this.orderByAttributeValue("Sum");
	}

	/**
	 * Reorders the IntegrityStatisticBean objects in the collection so that they are listed from lowest total count value
	 * to greatest total count value.
	 */
	public void orderByIsbTotalCountValue() {
		this.orderByAttributeValue("Total Count");
	}


	private void orderByAttributeValue(String arg_attribute){
		Hashtable<Long, ArrayList<IntegrityStatisticBean>> isb_hashtable = new Hashtable<Long, ArrayList<IntegrityStatisticBean>>();
		ArrayList<Long> allUniqueVals = new ArrayList<Long>();
		ArrayList<IntegrityStatisticBean> ordered_isb_arrayList = new ArrayList<IntegrityStatisticBean>();

		for (int i=0; i<this.getCollectionSize();i++){
			IntegrityStatisticBean currIsb = this.getCollectionObject(i);
			Long currVal;
			switch(arg_attribute){
				case "Average":
					currVal = currIsb.getAverage();
					break;
				case "Count":
					currVal = currIsb.getCount();
					break;
				case "Total Count":
					currVal = currIsb.getTotalCount();
					break;
				case "Minimum":
					currVal = currIsb.getMin();
					break;
				case "Maximum":
					currVal = currIsb.getMax();
					break;
				case "Sum":
					currVal = currIsb.getSum();
					break;
				default:
					currVal = 0L;
					break;
			}

			if (!allUniqueVals.contains(currVal)) { allUniqueVals.add(currVal); }

			if (isb_hashtable.containsKey(currVal)){
				ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
				temp_isb_arrayList = isb_hashtable.get(currVal);
				temp_isb_arrayList.add(currIsb);
				isb_hashtable.put(currVal, temp_isb_arrayList);
			} else {
				ArrayList<IntegrityStatisticBean> temp_isb_arrayList = new ArrayList<IntegrityStatisticBean>();
				temp_isb_arrayList.add(currIsb);
				isb_hashtable.put(currVal, temp_isb_arrayList);
			}
		}

		Long[] allFinalVals = new Long[allUniqueVals.size()];
		for(int i=0;i<allUniqueVals.size();i++) { allFinalVals[i] = allUniqueVals.get(i); }

		MergeSort valSorter = new MergeSort();
		allFinalVals = valSorter.sort(allFinalVals);

		for(Long val : allFinalVals){
			ArrayList<IntegrityStatisticBean> temp_isb_arrayList = isb_hashtable.get(val);
			for (IntegrityStatisticBean isb : temp_isb_arrayList){
				ordered_isb_arrayList.add(isb);
			}
		}

		this.collection = ordered_isb_arrayList;
		return;
	}

	/**
	 * Removes a single IntegrityStatisticBean object from the collection.
	 * Raises recompute flags to recompute all StatisticsCollection attribute values.
	 * @param arg_isb (IntegrityStatisticBean) - the object to remove.
	 */
	public void removeFromCollection(IntegrityStatisticBean arg_isb){
		this.collection.remove(arg_isb);
		this.requireAvgValRecompute = true;
		this.requireCntValRecompute = true;
		this.requireMaxObjRecompute = true;
		this.requireMaxValRecompute = true;
		this.requireMinObjRecompute = true;
		this.requireMinValRecompute = true;
		this.requireTotCntValRecompute = true;
		this.requireEarliestStartDateRecompute = true;
		this.requiresLatestStartDateRecompute = true;
		this.requireEarliestEndDateRecompute = true;
		this.requiresLatestEndDateRecompute = true;
	}

	/**
	 * Removes one or more IntegrityStatisticBean objects from the collection.
	 * @param indicesToRemove (int[]) - array of integers representing the collection indices to remove.
	 */
	public void removeFromCollection(int[] indicesToRemove){

		StatisticsCollection remove_collection = new StatisticsCollection("temp removal collection");
		for(int i : indicesToRemove) remove_collection.addToCollection(this.getCollectionObject(i));

		try{
			this.collection.removeAll(remove_collection.getCollection());
		} catch(IndexOutOfBoundsException ioobe){
			System.out.println("StatisticsCollection.removeFromCollection(int[]) - ERROR: " + ioobe);
		} catch (Exception e){
			System.out.println("StatisticsCollection.removeFromCollection(int[]) - ERROR: " + e);
		}
		remove_collection.clearCollection();

		this.requireAvgValRecompute = true;
		this.requireCntValRecompute = true;
		this.requireMaxObjRecompute = true;
		this.requireMaxValRecompute = true;
		this.requireMinObjRecompute = true;
		this.requireMinValRecompute = true;
		this.requireTotCntValRecompute = true;
		this.requireEarliestStartDateRecompute = true;
		this.requiresLatestStartDateRecompute = true;
		this.requireEarliestEndDateRecompute = true;
		this.requiresLatestEndDateRecompute = true;
	}

	/**
	 * Removes the IntegrityStatisticBean in the collection index provided.
	 * @param index (int) - integer index to remove from the collection.
	 */
	public void removeFromCollection(int index){
		try{
			this.removeFromCollection(index);
		} catch(IndexOutOfBoundsException ioobe){
			System.out.println("StatisticsCollection.removeFromCollection(int) - ERROR: " + ioobe);
		} catch (Exception e){
			System.out.println("StatisticsCollection.removeFromCollection(int) - ERROR: " + e);
		}
	}

	/**
	 * Sets the StatisticsCollection name.
	 * @param arg_name (String) - name of the collection.
	 */
	public void setName(String arg_name) { this.collectionName = arg_name;}

	/**
	 * Creates a .csv file of all collection contents.
	 * Each IntegrityStatisticBean's data is printed to a single line with the columns:
	 * Start Date, End Date, Group, Statistic, Kind, Unit, Count, Total, Sum, Used, Min, Max, Average, Mode
	 * @param arg_filePath (String) - the complete system file path to create the .csv file.
	 * @param arg_forceOverwrite (boolean) - if true and the file already exists, the existing file content will be
	 *                           overwritten.
	 */
	public void writeToFile(String arg_filePath, boolean arg_forceOverwrite) {

		//write to file as CSV following the same format as the output Server Statistics .csv file
		//cols: Start Date, End Date, Group, Statistic (Name), Kind, Unit, Count, total, Sum, Min, Max, Average, Mode

		File file = new File(arg_filePath);
		StringBuffer fileContents = new StringBuffer();
		if(!file.exists() || arg_forceOverwrite) fileContents.append("Start Date, End Date, Group, Statistic, Kind, Unit, Count, Total, Sum, Used, Min, Max, Average, Mode"+"\n");

		for(IntegrityStatisticBean isb : this.getCollection()){
			fileContents.append(fmt.print(isb.getStartDate()) + ","
					+ fmt.print(isb.getEndDate()) + ","
					+ isb.getGroup() + ","
					+ isb.getName() + ","
					+ isb.getKind() + ","
					+ isb.getUnit() + ","
					+ isb.getCount() + ","
					+ isb.getTotalCount() + ","
					+ isb.getSum() + ","
					+ isb.getUsed() + ","
					+ isb.getMin() + ","
					+ isb.getMax() + ","
					+ isb.getAverage()  + ","
					+ isb.getMode() + "\n");
		}

		if (fileContents.toString().length()!=0){
			Path filePath = Paths.get(arg_filePath);

			try {
				if(file.exists() && !arg_forceOverwrite){
					Files.write(filePath, fileContents.toString().getBytes(), StandardOpenOption.APPEND);
				} else{
					Files.write(filePath, fileContents.toString().getBytes());
				}

			} catch (IOException x) {
				System.out.format("StatisticsCollection.writeToFile() - WARNING: IOException: %s%n", x);
			}
		} else {
			System.out.println("StatisticsCollection.writeToFile() - WARNING: EMPTY DATA WRITTEN TO FILE...");
		}
	}

	/**
	 * Writes a single .csv entry using the StatisticsCollection total values as follows Column Name (value provided):
	 * Start Date (Earliest Collection Start Date), End Date (Latest Collection End Date), Group (group value of 1st collection
	 * item), Statistic (name value of 1st collection item), Kind (kind value of 1st collection item, Unit (unit value of 1st
	 * collection item), Count (collection count), Total (collection total count), Sum (collection sum), Used (used value of
	 * 1st collection item), Min (collection min), Max (collection max), Average (collection average), Mode (mode value of
	 * 1st collection item).
	 *
	 * This method will either create a new file or append to the bottom of an existing file.
	 * @param arg_filePath (String) - full system file path of the .csv file to create or append to.
	 */
	public void writeCollectionTotalsToFile(String arg_filePath) {

		File file = new File(arg_filePath);
		StringBuffer fileContents = new StringBuffer();
		if(!file.exists()) fileContents.append("Start Date, End Date, Group, Statistic, Kind, Unit, Count, Total, Sum, Used, Min, Max, Average, Mode"+"\n");


		fileContents.append(fmt.print(this.getCollectionEarliestStartDate()) + ","
				+ fmt.print(this.getCollectionLatestEndDate()) + ","
				+ this.getCollectionObject(0).getGroup() + ","
				+ this.getCollectionObject(0).getName() + ","
				+ this.getCollectionObject(0).getKind() + ","
				+ this.getCollectionObject(0).getUnit() + ","
				+ this.getCollectionCountValue() + ","
				+ this.getCollectionTotalCountValue() + ","
				+ this.getCollectionSumValue() + ","
				+ this.getCollectionObject(0).getUsed() + ","
				+ this.getCollectionMinimumValue() + ","
				+ this.getCollectionMaximumValue() + ","
				+ this.getCollectionAverageValue()  + ","
				+ this.getCollectionObject(0).getMode() + "\n");


		if (fileContents.toString().length()!=0){
			Path filePath = Paths.get(arg_filePath);
			try {
				if(file.exists()){
					Files.write(filePath, fileContents.toString().getBytes(), StandardOpenOption.APPEND);
				} else{
					Files.write(filePath, fileContents.toString().getBytes());
				}
			} catch (IOException x) {
				System.out.format("IOException: %s%n", x);
			}
		} else {
			System.out.println("StatisticsCollection.writeCollectionTotalsToFile() - WARNING: EMPTY DATA WRITTEN TO FILE...");
		}
	}

	/**
	 * Utility method for troubleshooting or debugging.
	 * Prints the contents of the collection to the system console, with one collection object per line.
	 * IntegrityStatisticBean data is printed in the format:
	 * Start Date, End Date, Group, Statistic, Kind, Unit, Count, total, Sum, Used, Min, Max, Average, Mode
	 */
	public void writeToString() {
		//write to file as CSV following the same format as the output Server Statistics .csv file
		//cols: Start Date, End Date, Group, Statistic (Name), Kind, Unit, Count, total, Sum, Min, Max, Average, Mode
		StringBuffer fileContents = new StringBuffer();
		int entry_counter = 1;

		fileContents.append("Start Date, End Date, Group, Statistic, Kind, Unit, Count, total, Sum, Used, Min, Max, Average, Mode"+"\n");
		for(IntegrityStatisticBean isb : this.getCollection()){
			fileContents.append(entry_counter + ": "
					+ fmt.print(isb.getStartDate()) + ","
					+ fmt.print(isb.getEndDate()) + ","
					+ isb.getGroup() + ",'"
					+ isb.getName() + "',"
					+ isb.getKind() + ","
					+ isb.getUnit() + ","
					+ isb.getCount() + ","
					+ isb.getTotalCount() + ","
					+ isb.getSum() + ","
					+ isb.getUsed() + ","
					+ isb.getMin() + ","
					+ isb.getMax() + ","
					+ isb.getAverage()  + ","
					+ isb.getMode() + "\n");
			entry_counter++;
		}
		System.out.println(fileContents.toString());
	}
}


