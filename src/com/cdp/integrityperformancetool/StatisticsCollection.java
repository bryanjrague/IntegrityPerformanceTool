package com.cdp.integrityperformancetool;

import java.io.File;
import java.nio.file.StandardOpenOption;
import java.util.*;

import com.cdp.integrityperformancetool.util.MergeSort;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * The StatisticsCollection class abstracts a logical grouping of IntegrityStatistic objects. The grouping of
 * IntegrityStatistic objects are stored in the StatisticsCollection's "collection" instance variable as an
 * ArrayList of IntegrityStatistics.
 *
 * The StatisticCollection has many group computation and sort methods,
 * as well as methods to add, remove, and get IntegrityStatistic objects from the collection attribute.
 * Standard <i>get</i> and <i>set</i> methods exist to access the instance variables.
 *
 */
public class StatisticsCollection {

	private static DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss zzz YYYY");

	private ArrayList<IntegrityStatistic> collection;
	private Long collectionAverageValue;
	private Long collectionCountValue;
	private ArrayList<IntegrityStatistic> collectionMaximumIsbObjectArrayList;
	private Long collectionMaximumValue;
	private ArrayList<IntegrityStatistic> collectionMinimumIsbObjectArrayList;
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
		this("New Integrity Statistics Collection", new ArrayList<IntegrityStatistic>());
	}

	public StatisticsCollection(String arg_collectionName){
		this(arg_collectionName, new ArrayList<IntegrityStatistic>());
	}

	public StatisticsCollection(String arg_collectionName, ArrayList<IntegrityStatistic> arg_collection){
		this.collection = arg_collection;
		this.collectionAverageValue = 0L;
		this.collectionCountValue = 0L;
		this.collectionMaximumIsbObjectArrayList = new ArrayList<IntegrityStatistic>();
		this.collectionMaximumValue = 0L;
		this.collectionMinimumIsbObjectArrayList = new ArrayList<IntegrityStatistic>();
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
	 * Adds a single IntegrityStatistic object to the collection.
	 * This action causes for all "recompute" flags to be raised, so that all previously computed
	 * StatisticsCollection values will be recomputed before being returned or used.
	 * @param arg_isb (IntegrityStatistic) - The IntegrityStatistic to add to the existing collection.
	 */
	public void addToCollection(IntegrityStatistic arg_isb){
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
	 * Adds an ArrayList of IntegrityStatistic objects to the collection.
	 * This action causes for all "recompute" flags to be raised, so that all previously computed
	 * StatisticsCollection values will be recomputed before being returned or used.
	 * @param arg_isb_arrayList (IntegrityStatistic) - The IntegrityStatistic to add to the existing collection.
	 */
	public void addToCollection(ArrayList<IntegrityStatistic> arg_isb_arrayList){
		for(IntegrityStatistic isb : arg_isb_arrayList){
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

	/**
	 * Creates a Comparator object for use in sorting a group of String objects by alphabetical order.
	 */
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
	 * Clears the current collection attribute of all IntegrityStatistic objects.
	 * <b>This effectively removes all IntegrityStatistic objects from the StatisticsCollection and raises all
	 * group value recomputation flags.</b>
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
	 * Combines all duplicate named IntegrityStatistic objects in the collection into a single summarized IntegrityStatistic
	 * object containing summarized statistics of all. All IntegrityStatistic objects used in creating the summarized
	 * IntegrityStatistic object are removed from the StatisticsCollection and the summarized IntegrityStatistic is
	 * added to it. The summarized IntegrityStatistic object has:
	 * <ul>
	 *     <li>Start Date = earliest Start Date for the statistic name.</li>
	 *     <li>End Date = latest End Date for the statistic name.</li>
	 *     <li>Group = the statistic Group value shared by all IntegrityStatistics being combined.</li>
	 *     <li>Name = the statistic Name value shared by all IntegrityStatistics being combined.</li>
	 *     <li>Count = cumulative sum of all Count values.</li>
	 *     <li>Total Count = cumulative sum of all Total Count values.</li>
	 *     <li>Minimum = the lowest value Minimum of all IntegrityStatistics with the same name.</li>
	 *     <li>Maximum = the greatest value Maximum of all IntegrityStatistics with the same name.</li>
	 *     <li>Average = the average of Average values of all IntegrityStatistics with the same name.</li>
	 * </ul>
	 * @param arg_statName (String) - The name attribute value of the IntegrityStatistic to combine all duplicates of.
	 * @param arg_statGroup (String) - The group attribute value of the IntegrityStatistic to combine all duplicates of.
	 *                      This is used to ensure that only exact duplicates are combined, and not any statistics that have
	 *                      the same name, but different groupings.
	 */
	public void collapseStatistic(String arg_statName, String arg_statGroup){

		StatisticsCollection temp_collection = new StatisticsCollection();
		Long cumulative_count = 0L;
		Long cumulative_totalCount = 0L;
		Long cumulative_sum = 0L;
		ArrayList<IntegrityStatistic> remove_isbs = new ArrayList<IntegrityStatistic>();
		int counter = 1;
		DateTime startDate = new DateTime();
		DateTime endDate = new DateTime();


		for(IntegrityStatistic isb : this.getCollection()){
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

		temp_collection.computeAllCollectionStatistics();
		IntegrityStatistic collapsedStatistic = temp_collection.getCollectionMaximumIsbArrayList().get(0);
		collapsedStatistic.setAverage(temp_collection.getCollectionAverageValue());
		collapsedStatistic.setCount(cumulative_count);
		collapsedStatistic.setGroup(arg_statGroup);
		collapsedStatistic.setName(arg_statName);
		collapsedStatistic.setMin(temp_collection.getCollectionMinimumValue());
		collapsedStatistic.setMax(temp_collection.getCollectionMaximumValue());
		collapsedStatistic.setTotalCount(cumulative_totalCount);
		collapsedStatistic.setStartDate(startDate);
		collapsedStatistic.setEndDate(endDate);
		collapsedStatistic.setSum(cumulative_sum);
		collapsedStatistic.setStartDate(temp_collection.getCollectionEarliestStartDate());
		collapsedStatistic.setEndDate(temp_collection.getCollectionLatestEndDate());

		for(int isb=0;isb<remove_isbs.size();isb++){
			this.removeFromCollection(remove_isbs.get(isb));
		}

		this.addToCollection(collapsedStatistic);
		remove_isbs.clear();
	}

	/**
	 * Executes the <i>collapseStatistic()</i> method against all IntegrityStatistic objects existing in the
	 * collection.
	 */
	public void collapseAllStatistics(){

		HashMap<String, String> allNames = this.getAllUniqueNameGroupPairs();

		for(String name : allNames.keySet()){
			this.collapseStatistic(name, allNames.get(name));
		}

	}

	/**
	 * Combines IntegrityStatistic objects that have the same exact Start Date, End Date, and Name. All individual
	 * IntegrityStatic objects that were used in creating the summarized IntegrityStatisic object are removed from
	 * the StatisticsCollection, and the summarized IntegrityStatistic object is added to it.
	 * The summarized IntegrityStatistic object has:
	 * <ul>
	 *     <li>Count = cumulative sum of all Count values within the same time period.</li>
	 *     <li>Total Count = cumulative sum of all Total Count values within the same time period.</li>
	 *     <li>Minimum = the lowest value Minimum of all IntegrityStatistics within the same time period.</li>
	 *     <li>Maximum = the greatest value Maximum of all IntegrityStatistics within the same time period.</li>
	 *     <li>Average = the average of Average values of all IntegrityStatistics within the same time period.</li>
	 * </ul>
	 *
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

				if(endIndex>i){ //then combine the IntegrityStatistics
					StatisticsCollection temp_collection2 = new StatisticsCollection();
					for(int j=i;j<endIndex;j++) { temp_collection2.addToCollection(getCollectionObject(j)); }

					IntegrityStatistic temp_isb = new IntegrityStatistic();

					temp_isb.setStartDate(temp_collection2.getCollectionEarliestStartDate());
					temp_isb.setEndDate(temp_collection2.getCollectionLatestEndDate());
					temp_isb.setGroup(getCollectionObject(i).getGroup());
					temp_isb.setName(getCollectionObject(i).getName());
					temp_isb.setKind(getCollectionObject(i).getKind());
					temp_isb.setUnit(getCollectionObject(i).getUnit());

					Long runningCount = 0L;
					for(IntegrityStatistic isb : temp_collection2.getCollection()) {
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
					temp_collection.addToCollection(temp_isb);

					i = endIndex-1;

				} else {
					temp_collection.addToCollection(getCollectionObject(i));
				}
			} else {
				temp_collection.addToCollection(getCollectionObject(i));
			}
		}
		this.clearCollection();
		for(IntegrityStatistic isb : temp_collection.getCollection()) { this.addToCollection(isb); }
	}

	/**
	 * Executes all StatisticsCollection group computation methods.
	 */
	public void computeAllCollectionStatistics(){
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
	 * IntegrityStatistic objects in the collection.
	 */
	public void computeCollectionAverageValue(){
		Long cumulativeSum = 0L;
		for (IntegrityStatistic isb : this.getCollection()){
			cumulativeSum = cumulativeSum + isb.getAverage();
		}
		this.collectionAverageValue = (cumulativeSum)/this.getCollectionSize();
		this.requireAvgValRecompute = false;
	}

	/**
	 * Computes the StatisticsCollection collectionCountValue attribute value by computing the total of all
	 * IntegrityStatistic objects count values in the collection.
	 */
	public void computeCollectionCountValue(){
		Long cumulativeSum = 0L;
		for (IntegrityStatistic isb : this.getCollection()){
			cumulativeSum = cumulativeSum + isb.getCount();
		}
		this.collectionCountValue = cumulativeSum;
		this.requireCntValRecompute = false;
	}

	/**
	 * Determines the earliest End Date used by an IntegrityStatistic object in the StatisticsCollection's collection
	 * and stores this value as a org.joda.time.DateTime object in the collectionEarliestEndDate attribute.
	 */
	public void computeCollectionEarliestEndDate() {
		DateTime test_date = null;
		if (this.getCollectionSize() > 1) {
			for (IntegrityStatistic isb : this.getCollection()) {
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
	 * Determines the earliest Start Date used by an IntegrityStatistic object in the StatisticsCollection's collection
	 * and stores this value as a org.joda.time.DateTime object in the collectionEarliestStartDate attribute.
	 */
	public void computeCollectionEarliestStartDate(){
		DateTime test_date = null;
		if (this.getCollectionSize() > 1) {
			for (IntegrityStatistic isb : this.getCollection()) {
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
	 * Determines the latest End Date used by an IntegrityStatistic object in the StatisticsCollection's collection
	 * and stores this value as a org.joda.time.DateTime object in the collectionLatestEndDate attribute.
	 */
	public void computeCollectionLatestEndDate() {
		DateTime test_date = null;
		if (this.getCollectionSize() > 1) {
			for (IntegrityStatistic isb : this.getCollection()) {
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
	 * Determines the latest Start Date used by an IntegrityStatistic object in the StatisticsCollection's collection
	 * and stores this value as a org.joda.time.DateTime object in the collectionEarliestStartDate attribute.
	 */
	public void computeCollectionLatestStartDate(){
		DateTime test_date = null;
		if (this.getCollectionSize() > 1) {
			for (IntegrityStatistic isb : this.getCollection()) {
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
	 * are saved as an ArrayList in the collectionMaximumIsbObjectArrayList instance variable, which will contain more than
	 * one IntegrityStatistic object if more than one have the same max value that is also the absolute max value
	 * of the collection.
	 */
	public void computeCollectionMaximumObject(){

		if(this.getRequireMaxObjRecompute()) {
			this.computeCollectionMaximumValue();
		}

		ArrayList<IntegrityStatistic> temp_isb_arrayList = new ArrayList<IntegrityStatistic>();
		for (IntegrityStatistic isb : this.getCollection()){
			//use known max of collection to return the object(s) equal to this max.
			if (isb.getMax()==this.collectionMaximumValue) temp_isb_arrayList.add(isb);
		}
		this.collectionMaximumIsbObjectArrayList = temp_isb_arrayList;
		this.requireMaxObjRecompute = false;
	}

	/**
	 * Computes the greatest max value for all IntegrityStatistic objects in the collection and saves this value as
	 * the collectionMaximumValue instance variable.
	 */
	public void computeCollectionMaximumValue() {
		if (this.getCollectionSize()>1) {
			Long[] allMaxVals = new Long[this.getCollectionSize()];
			for (int i = 0; i < this.getCollectionSize(); i++) {
				allMaxVals[i] = this.getCollectionObject(i).getMax();
			}

			MergeSort maxSorter = new MergeSort();
			maxSorter.sort(allMaxVals);
			this.collectionMaximumValue = allMaxVals[allMaxVals.length-1];

		} else if (this.getCollectionSize()==1){
			this.collectionMaximumValue = this.getCollectionObject(0).getMax();
		} else {
			System.out.println("WARNING: COMPUTE MAX VALUE METHOD CALLED WITH NO COLLECTION");
		}
		this.requireMaxValRecompute = false;

	}

	/**
	 * Determines the IntegrityStatistic object(s) in the collection which have the lowest min value. The object(s)
	 * are saved as an ArrayList in the collectionMinimumIsbObjectArrayList instance variable, which will contain more than
	 * one IntegrityStatistic object if more than one have the same min value that is also the absolute min value
	 * of the collection.
	 */
	public void computeCollectionMinimumObject(){
		if(this.getRequireMinValRecompute()) {
			this.computeCollectionMinimumValue();
		}

		ArrayList<IntegrityStatistic> temp_isb_arrayList = new ArrayList<IntegrityStatistic>();
		for (IntegrityStatistic isb : this.getCollection()){
			if(isb.getMin()==this.collectionMinimumValue) temp_isb_arrayList.add(isb);
		}
		this.collectionMinimumIsbObjectArrayList = temp_isb_arrayList;
		this.requireMinObjRecompute = false;
	}

	/**
	 * Computes the lowest min value for all IntegrityStatistic objects in the collection and saves this value as
	 * the collectionMinimumValue instance variable.
	 */
	public void computeCollectionMinimumValue(){
		if (this.getCollectionSize()>1) {
			Long[] allMinVals = new Long[this.getCollectionSize()];
			for (int i = 0; i < this.getCollectionSize(); i++) {
				allMinVals[i] = this.getCollectionObject(i).getMin();
			}

			MergeSort minSorter = new MergeSort();
			minSorter.sort(allMinVals);
			this.collectionMinimumValue = allMinVals[0];

		} else if (this.getCollectionSize()==1){
			this.collectionMinimumValue = this.getCollectionObject(0).getMin();
		} else {
			System.out.println("WARNING: COMPUTE MIN VALUE METHOD CALLED WITH NO COLLECTION");
		}
		this.requireMinValRecompute = false;
	}

	/**
	 * Computes the collectionSumValue instance variable value by adding together all IntegrityStatistic sum values in the
	 * collection.
	 */
	public void computeCollectionSumValue(){
		Long cumulativeSum = 0L;
		for (IntegrityStatistic isb : this.getCollection()){
			cumulativeSum = cumulativeSum + isb.getSum();
		}
		this.collectionSumValue = cumulativeSum;
		this.requiresSumValRecompute = false;
	}

	/**
	 * Computes the collectionTotalCountValue attribute value by adding together all IntegrityStatistic totalCount
	 * values in the collection.
	 */
	public void computeCollectionTotalCountValue(){
		Long cumulativeSum = 0L;
		for (IntegrityStatistic isb : this.getCollection()){
			cumulativeSum = cumulativeSum + isb.getTotalCount();
		}
		this.collectionTotalCountValue = cumulativeSum;
		this.requireTotCntValRecompute = false;
	}

	/**
	 * Returns a HashMap of {name: group} for all unique IntegrityStatistic name-group pairs existing in the collection.
	 * @return HashMap {String: String} representing all unique name-group pairs of IntegrityStatistic objects in the collection.
	 */
	public HashMap<String, String> getAllUniqueNameGroupPairs(){
		HashMap<String, String> uniqueNames = new HashMap<String, String>();

		for(IntegrityStatistic isb : this.getCollection()) {
			if (!uniqueNames.containsKey(isb.getName())) {
				uniqueNames.put(isb.getName(), isb.getGroup());
			}
		}
		return uniqueNames;
	}

	/**
	 * Returns an ArrayList of all IntegrityStatistic objects currently in the StatisticsCollection's collection.
	 * @return ArrayList of IntegrityStatistic objects in the collection
	 */
	public ArrayList<IntegrityStatistic> getCollection(){ return this.collection; }

	/**
	 * Returns the Long value representing the collectionAverageValue instance variable.
	 * @return (Long) - the value of the StatisticsCollection's average value.
	 */
	public Long getCollectionAverageValue(){
		if(this.getRequireAvgValRecompute()) {
			this.computeCollectionAverageValue();
		}
		return this.collectionAverageValue;
	}

	/**
	 * Returns the Long value representing the collectionCountValue instance variable.
	 * @return (Long) - the value of the StatisticsCollection's count value.
	 */
	public Long getCollectionCountValue(){
		if(this.getRequireCntValRecompute()) {
			this.computeAllCollectionStatistics();
		}
		return this.collectionCountValue;
	}

	/**
	 * Returns the DateTime value representing the collectionEarliestEndDate instance variable.
	 * @return (org.joda.time.DateTime) - the value of the StatisticsCollection's earliest end date.
	 */
	public DateTime getCollectionEarliestEndDate(){
		if(this.getRequireEarliestEndDateRecompute()) {
			this.computeCollectionEarliestEndDate();
		}
		return this.collectionEarliestEndDate;

	}

	/**
	 * Returns the DateTime value representing the collectionEarliestStartDate instance variable.
	 * @return (org.joda.time.DateTime) - the value of the StatisticsCollection's earliest start date.
	 */
	public DateTime getCollectionEarliestStartDate(){
		if(this.getRequireEarliestStartDateRecompute()) {
			this.computeCollectionEarliestStartDate();
		}
		return this.collectionEarliestStartDate;
	}

	/**
	 * Returns the DateTime value representing the collectionLatestEndDate instance variable.
	 * @return (org.joda.time.DateTime) - the value of the StatisticsCollection's latest end date.
	 */
	public DateTime getCollectionLatestEndDate(){
		if(this.getRequireLatestEndDateRecompute()) {
			this.computeCollectionLatestEndDate();
		}
		return this.collectionLatestEndDate;

	}

	/**
	 * Returns the DateTime value representing the collectionLatestStartDate instance variable.
	 * @return (org.joda.time.DateTime) - the value of the StatisticsCollection's latest start date.
	 */
	public DateTime getCollectionLatestStartDate(){
		if(this.getRequireLatestStartDateRecompute()) {
			this.computeCollectionEarliestStartDate();
		}
		return this.collectionLatestStartDate;

	}

	/**
	 * Returns the StatisticsCollection's IntegrityStatistic object(s) holding the greatest max value in the collection.
	 * @return ArrayList(IntegrityStatistic) - the IntegrityStatistic(s) with the greatest max value in
	 * the collection.
	 */
	public ArrayList<IntegrityStatistic> getCollectionMaximumIsbArrayList(){
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
	 * Returns the StatisticsCollection's IntegrityStatistic object(s) holding the lowest min value in the collection.
	 * @return ArrayList(IntegrityStatistic) - the IntegrityStatistic(s) with the lowest min value in
	 * the collection.
	 */
	public ArrayList<IntegrityStatistic> getCollectionMinimumIsbArrayList(){
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
	 * Returns the current value of the StatisticsCollection collectionName instance variable.
	 * @return (String) - returns the name of the StatisticsCollection.
	 */
	public String getCollectionName(){ return this.collectionName; }

	/**
	 * Returns a single IntegrityStatistic object found at the provided integer index.
	 * @param index (int) - the index within the collection to retrieve.
	 * @return IntegrityStatistic at the collection index requested.
	 */
	public IntegrityStatistic getCollectionObject(int index){
		try{
			return this.collection.get(index);
		} catch(IndexOutOfBoundsException ioobe){
			System.out.println("StatisticsCollection.getCollectionObject() - ERROR: " + ioobe);
			System.out.println("Returning new default IntegrityStatistic!");
			return new IntegrityStatistic();
		}
	}

	/**
	 * Returns the number of IntegrityStatistic objects in the collection.
	 * @return (int) - Integer number of IntegrityStatistic objects in the collection.
	 */
	public int getCollectionSize(){ return this.collection.size(); }

	/**
	 * Returns the collectionSumValue attribute for the StatisticsCollection object.
	 * Recomputes the attribute value first if needed.
	 * @return (Long) - the cumulative sum of  the "sum" attribute for all IntegrityStatistic objects in the collection.
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
	 * @return (Long) - the cumulative sum of the "totalCount" attribute for all IntegrityStatistic objects in the collection.
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
	 * Reorders the IntegrityStatistic objects in the collection so that they are listed from lowest average value
	 * to greatest average value.
	 */
	public void orderByIsbAverageValue() {
		this.orderByAttributeValue("Average");
	}

	/**
	 * Reorders the IntegrityStatistic objects in the collection so that they are listed from lowest count value
	 * to greatest count value.
	 */
	public void orderByIsbCountValue() {
		this.orderByAttributeValue("Count");
	}

	/**
	 * Reorders the IntegrityStatistic objects in the collection so that they are listed in aplhabetical order.
	 */
	public void orderByIsbNameValue() {
		Hashtable<String, ArrayList<IntegrityStatistic>> name_isb_hashtable = new Hashtable<String, ArrayList<IntegrityStatistic>>();
		String[] allNames = new String[this.getCollectionSize()];
		ArrayList<IntegrityStatistic> ordered_isb_arrayList = new ArrayList<IntegrityStatistic>();

		for (int i=0; i<this.getCollectionSize();i++){
			IntegrityStatistic currIsb = this.getCollectionObject(i);
			String currName = this.getCollectionObject(i).getName();
			allNames[i] = currName;
			if (name_isb_hashtable.containsKey(currName)){
				ArrayList<IntegrityStatistic> temp_isb_arrayList = new ArrayList<IntegrityStatistic>();
				temp_isb_arrayList = name_isb_hashtable.get(currName);
				temp_isb_arrayList.add(currIsb);
				name_isb_hashtable.put(currName, temp_isb_arrayList);
			} else {
				ArrayList<IntegrityStatistic> temp_isb_arrayList = new ArrayList<IntegrityStatistic>();
				temp_isb_arrayList.add(currIsb);
				name_isb_hashtable.put(currName, temp_isb_arrayList);
			}
		}

		Arrays.sort(allNames, ALPHABETICAL_ORDER);
		for (int j=0;j<this.getCollectionSize();j++){
			ArrayList<IntegrityStatistic> temp_isb_arrayList = new ArrayList<IntegrityStatistic>();
			temp_isb_arrayList = name_isb_hashtable.get(allNames[j]);
			for (IntegrityStatistic isb : temp_isb_arrayList){
				ordered_isb_arrayList.add(isb);
			}
		}

		for (int k=0;k<this.getCollectionSize();k++) this.collection.set(k, ordered_isb_arrayList.get(k));
		return;
	}

	/**
	 * Reorders the IntegrityStatistic objects in the collection so that they are listed from lowest max value
	 * to greatest max value.
	 */
	public void orderByIsbMaximumValue() {
		this.orderByAttributeValue("Maximum");
	}

	/**
	 * Reorders the IntegrityStatistic objects in the collection so that they are listed from lowest min value
	 * to greatest min value.
	 */
	public void orderByIsbMinimumValue() {
		this.orderByAttributeValue("Minimum");
	}

	/**
	 * Reorders the IntegrityStatistic objects in the collection so that they are listed from lowest sum value
	 * to greatest sum value.
	 */
	public void orderByIsbSumValue() {
		this.orderByAttributeValue("Sum");
	}

	/**
	 * Reorders the IntegrityStatistic objects in the collection so that they are listed from lowest total count value
	 * to greatest total count value.
	 */
	public void orderByIsbTotalCountValue() {
		this.orderByAttributeValue("Total Count");
	}


	private void orderByAttributeValue(String arg_attribute){
		Hashtable<Long, ArrayList<IntegrityStatistic>> isb_hashtable = new Hashtable<Long, ArrayList<IntegrityStatistic>>();
		ArrayList<Long> allUniqueVals = new ArrayList<Long>();
		ArrayList<IntegrityStatistic> ordered_isb_arrayList = new ArrayList<IntegrityStatistic>();

		for (int i=0; i<this.getCollectionSize();i++){
			IntegrityStatistic currIsb = this.getCollectionObject(i);
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
				ArrayList<IntegrityStatistic> temp_isb_arrayList = new ArrayList<IntegrityStatistic>();
				temp_isb_arrayList = isb_hashtable.get(currVal);
				temp_isb_arrayList.add(currIsb);
				isb_hashtable.put(currVal, temp_isb_arrayList);
			} else {
				ArrayList<IntegrityStatistic> temp_isb_arrayList = new ArrayList<IntegrityStatistic>();
				temp_isb_arrayList.add(currIsb);
				isb_hashtable.put(currVal, temp_isb_arrayList);
			}
		}

		Long[] allFinalVals = new Long[allUniqueVals.size()];
		for(int i=0;i<allUniqueVals.size();i++) { allFinalVals[i] = allUniqueVals.get(i); }

		MergeSort valSorter = new MergeSort();
		allFinalVals = valSorter.sort(allFinalVals);

		for(Long val : allFinalVals){
			ArrayList<IntegrityStatistic> temp_isb_arrayList = isb_hashtable.get(val);
			for (IntegrityStatistic isb : temp_isb_arrayList){
				ordered_isb_arrayList.add(isb);
			}
		}

		this.collection = ordered_isb_arrayList;
		return;
	}

	/**
	 * Removes a single IntegrityStatistic object from the collection.
	 * Raises recompute flags to recompute all StatisticsCollection instance variables.
	 * @param arg_isb (IntegrityStatistic) - the object to remove.
	 */
	public void removeFromCollection(IntegrityStatistic arg_isb){
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
	 * Removes one or more IntegrityStatistic objects from the collection.
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
	 * Removes the IntegrityStatistic in the collection index provided.
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
	 * Each IntegrityStatistic's data is printed to a single line with the columns:
	 * Start Date, End Date, Group, Statistic, Kind, Unit, Count, Total, Sum, Used, Min, Max, Average, Mode
	 * @param arg_filePath (String) - the complete system file path to create the .csv file.
	 * @param arg_forceOverwrite (boolean) - if true and the file already exists, the existing file content will be
	 *                           overwritten.
	 */
	public void writeToFile(String arg_filePath, boolean arg_forceOverwrite) {

		File file = new File(arg_filePath);
		StringBuffer fileContents = new StringBuffer();
		if(!file.exists() || arg_forceOverwrite)
			fileContents.append("Start Date, End Date, Group, Statistic, Kind, Unit, Count, "+
					"Total, Sum, Used, Min, Max, Average, Mode"+"\n");

		for(IntegrityStatistic isb : this.getCollection()){
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
		if(!file.exists()) fileContents.append("Start Date, End Date, Group, Statistic, Kind, Unit, "+
				"Count, Total, Sum, Used, Min, Max, Average, Mode"+"\n");

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
	 * IntegrityStatistic data is printed in the format:
	 * Start Date, End Date, Group, Statistic, Kind, Unit, Count, total, Sum, Used, Min, Max, Average, Mode
	 */
	public void writeToString() {

		StringBuffer fileContents = new StringBuffer();
		int entry_counter = 1;

		fileContents.append("Start Date, End Date, Group, Statistic, Kind, Unit, Count, "+
				"Total, Sum, Used, Min, Max, Average, Mode"+"\n");
		for(IntegrityStatistic isb : this.getCollection()){
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


