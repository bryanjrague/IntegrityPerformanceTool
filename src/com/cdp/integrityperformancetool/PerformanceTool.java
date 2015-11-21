package com.cdp.integrityperformancetool;

import com.cdp.integrityperformancetool.reporting.ReportBuilder;
import com.cdp.integrityperformancetool.util.StatisticsFileReader;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * The main executable class of the project. This class can be modified to create the procedure required to process
 * the statistics and create files, reports, etc.
 */
public class PerformanceTool {

    private static DateTime runTime = new DateTime();
    private static String runTimeStr = runTime.toString().replace(":","_").replace(".","_");

    //logging via log4j
    private static Logger masterLog = Logger.getLogger("PerformanceTool");

    private static DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM-dd-YYYY");

    ////////////////////////////////////////////////
    ///// Make sure to change this directory to the local machine directory
    ////////////////////////////////////////////////
    private static String masterDir = "C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool";

    private static ArrayList<String> allTopTenStatNames = new ArrayList<String>(); //stores all stats which appear on any of the top ten % lists
    private static ArrayList<IntegrityStatisticBean> targetListStats =  new ArrayList<IntegrityStatisticBean>();; //stores any stats which appear on more than one of the top ten % lists

    //report building variables holding filepaths to the .jrxml files used to create reports.
    private static String topTenAvgValReportJrxml = "C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool"
            +"\\ReportBuildingData\\ipt_topTenPercent_avg.jrxml";
    private static String topTenMaxValReportJrxml = "C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool"
            +"\\ReportBuildingData\\ipt_topTenPercent_max.jrxml";
    private static String topTenCountReportJrxml = "C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool"
            +"\\ReportBuildingData\\ipt_topTenPercent_count.jrxml";
    private static String topTenTotCntReportJrxml = "C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool"
            +"\\ReportBuildingData\\ipt_topTenPercent_total.jrxml";
    private static String targetReportJrxml = "C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool"
            +"\\ReportBuildingData\\ipt_topTenPercent_target.jrxml";
    private static String summaryHistPerformanceJrxml = "C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool"
            +"\\ReportBuildingData\\hist_performance.jrxml";

    //percentage that the latest historical entry of a statistic must have changed by in order for a detailed report
    //  about the statistic to be created.
    private static Float histDeltaThreshold = 0.1F;


    public static void main(String args[]){

        //initialize logging
        PropertyConfigurator.configure(masterDir + "\\Logging\\log4j.properties");

        masterLog.info("");
        masterLog.info(" ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **");
        masterLog.info(" BEGIN EXECUTION OF INTEGRITY PERFORMANCE TOOL AT: " + runTimeStr);
        masterLog.info(" ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **");
        masterLog.info("");


        //////////////////////////////////////////////
        //// Get all statistics from the master data file
        /////////////////////////////////////////////
        //String masterDataFilePath = masterDir + "\\Input\\Statistics_PROD_10-1-2015_10-14-2015.csv";
        // String masterDataFilePath = masterDir + "\\Input\\Statistics_PROD_10-15-2015_11-1-2015.csv";
        String masterDataFilePath = masterDir + "\\Input\\Statistics_PROD_11-1-2015_11-15-2015.csv";
        StatisticsLibrary masterDataLib = getStatsLibraryFromFile(masterDataFilePath, ",",1);
        masterLog.info("Source Data File: " +  masterDataFilePath);
        masterLog.info("");

        //////////////////////////////////////////////
        //// Send master statistics library group(s) for processing
        //////////////////////////////////////////////
        processStatisticsGroup("Triggers", masterDataLib);
        //then process statistics for other group names once the report and statistics analysis methods are complete

        //next steps!
        // Open the "All Triggers - Summarized Results - Historical Data.csv"
        // for each trigger name, find all entries for the specific trigger name
        // compare the entries of each unique name for each entry in the file
        // save the differences is number of times run (count), average execution time, etc.,  as another file
        // identify those triggers which have changed in use or execution speed by a certain percentage TBD
        // for those triggers which have changed in use or execution speed by the percentage amount, create a
        // report highlighting how they have changed and by how much


        masterLog.info("Done processing all statistics!");
        masterLog.info("");
        masterLog.info(" ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **");
        masterLog.info(" END EXECUTION OF INTEGRITY PERFORMANCE TOOL AT: " + runTimeStr);
        masterLog.info(" ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **");
        masterLog.info("");
    }

    public static void print(String arg_str) {
        System.out.println(arg_str + "\n");
    }

    private static String cleanString(String arg_s){
        //removes chars that are illegal for filenames
        //used to remove illegal chars from trigger/query/report/etc names used in file names
        //so that the names can be saved
        arg_s = arg_s.replace(":", "");
        arg_s = arg_s.replace(",", "");
        arg_s = arg_s.replace("\"", "");
        arg_s = arg_s.replace(".", "");
        arg_s = arg_s.replace("/", "");
        arg_s = arg_s.replace("(", "");
        arg_s = arg_s.replace(")", "");
        arg_s = arg_s.replace(">", "");
        arg_s = arg_s.replace("<", "");
        arg_s = arg_s.replace("=", "");
        arg_s = arg_s.replace(".", "");
        arg_s = arg_s.replace("?", "");
        return arg_s;
    }

    /*
    public static Double unitConversion(Double input, String u){
		//takes in the double of statistics and the unit of statistic, then conducts unit conversions based on the unit
			if(input>0) {
				if(u.equals("Ms")) { timeConversion = true; return (input/100000.0); }
				else if(u.equals("ms")) { timeConversion = true; return (input/1000.0); }
				else return input;
			} else return input;

	}
     */

    private static StatisticsLibrary getStatsLibraryFromFile(String arg_fileName, String arg_valSeparator, int arg_skipLines){

        StatisticsFileReader temp_fileReader = new StatisticsFileReader(arg_fileName,
                arg_valSeparator,
                arg_skipLines);
        return temp_fileReader.executeStatisticsRetrieval();

    }

    //the heavy-lifting method
    //takes the StatisticsLibrary, extracts the StatisticsCollection object with the arg_grpName key
    //  from the StatisticsLibrary, creates output files and reports.
    private static void processStatisticsGroup(String arg_grpName, StatisticsLibrary arg_library){

        masterLog.info("About to process statistics group: " + arg_grpName);
        ///////////////////////////////////////////////
        /// Get only the specific Statistic Group data from the master lib
        ///////////////////////////////////////////////
        try{
            StatisticsCollection statGrp_raw = arg_library.getStatisticsGroupName(arg_grpName);

            ///////////////////////////////////////////////
            /// Get only the cumulative data from the Collection, create new collection to store them
            ///////////////////////////////////////////////
            StatisticsCollection statGrp_cumulative = new StatisticsCollection("All " + arg_grpName + " - Cumulative only");
            for(IntegrityStatisticBean isb : statGrp_raw.getCollection()){
                if(isb.getMode().equals("cumulative")){
                    statGrp_cumulative.addToCollection(isb);
                }
            }

            ////////////////////////////////////////////////
            /// For each unique statistic name in the cumulative only collection, create and store a file of all stat entries
            //// If file already exists in output, append to the bottom of the existing file.
            ///////////////////////////////////////////////

            //get all unique names for cumulative trigger stats
            HashMap<String,String> cumulativeUniqueNames = statGrp_cumulative.getAllUniqueNameGroupPairs();
            int counter = 0;
            for(String name : cumulativeUniqueNames.keySet()){
                StatisticsCollection temp_collection = new StatisticsCollection("Trigger: " + name + " Cumulative Stats");
                for(IntegrityStatisticBean isb : statGrp_cumulative.getCollection()){
                    if(isb.getName().equals(name)) temp_collection.addToCollection(isb);
                }
                //StringBuilder filePath = new StringBuilder("C:\\Users\\bryan\\IdeaProjects\\Integrity Performance Tool\\Output\\");
                StringBuilder filePath = new StringBuilder(masterDir + "\\Output\\");
                filePath.append(arg_grpName+"\\");
                filePath.append("Cumulative Entries\\");
                File temp_dir = new File(filePath.toString());
                if (!temp_dir.exists()) temp_dir.mkdirs();
                filePath.append(cleanString(name));
                filePath.append(".csv");
                counter++;
                temp_collection.writeToFile(filePath.toString(), false);
                masterLog.info("\t" + arg_grpName + " - processed data for statistic: " + name);
            }

            String sortedTotalsFileBase = masterDir + "\\Output\\"+arg_grpName+"\\Totals\\";
            File dir = new File(sortedTotalsFileBase);
            if(!dir.exists()) dir.mkdir();

            String topTenFileBase = masterDir + "\\Output\\"+arg_grpName+"\\TopTenPercent\\";
            dir = new File(topTenFileBase);
            if(!dir.exists()) dir.mkdir();

            //////////////////////////////////////////////////
            /// Gather all individual cumulative statistic totals and store in single file:
            //////////////////////////////////////////////////

            String currStatTotalsFile = sortedTotalsFileBase + "All " + arg_grpName + " - Summarized Results - Historical Data.csv";
            StatisticsCollection currCumulativeStats = new StatisticsCollection();

            for(String name : cumulativeUniqueNames.keySet()){
                StringBuilder currFilePath = new StringBuilder(masterDir + "\\Output\\");
                currFilePath.append(arg_grpName+"\\");
                currFilePath.append("Cumulative Entries\\");
                currFilePath.append(cleanString(name) + ".csv");

                StatisticsFileReader currCumulativeStatsFile = new StatisticsFileReader(currFilePath.toString());
                StatisticsLibrary currCumulativeLibrary = currCumulativeStatsFile.executeStatisticsRetrieval();
                currCumulativeStats = currCumulativeLibrary.getStatisticsGroupName(arg_grpName);
                currCumulativeStats.collapseAllStatistics();
                currCumulativeStats.computeAllCollectionStatistics();
                currCumulativeStats.writeCollectionTotalsToFile(currStatTotalsFile);
            }

            masterLog.info("");
            masterLog.info("Appended all summarized entries to historical summarized results file for: " + arg_grpName);
            masterLog.info("\t " + currStatTotalsFile);
            masterLog.info("");

            ///////////////////////////////////////////////////
            //// Send the historical data file for analysis to find
            // those performance which has changed beyond a percentage.
            //////////////////////////////////////////////////
            analyzeHistoricalTrends(currStatTotalsFile, arg_grpName);

            ///////////////////////////////////////////////////
            //// Gather unsorted total statistics and sort. run various sort functions. save output as files.
            ///////////////////////////////////////////////////
            StatisticsFileReader unsortedTotalsStatsFile = new StatisticsFileReader(currStatTotalsFile);
            StatisticsLibrary unsortedTotalsLibrary = unsortedTotalsStatsFile.executeStatisticsRetrieval();
            StatisticsCollection unsortedStatistics = unsortedTotalsLibrary.getStatisticsGroupName(arg_grpName);

            unsortedStatistics.collapseAllStatistics();
            //StatisticsCollection now has all finalized, unique stats for the group with no duplicate stat names.

            //prep file destinations for report building functionality
            String reportOutFileBase = masterDir + "\\Output\\"+arg_grpName+"\\Reports\\";
            dir = new File(reportOutFileBase);
            if(!dir.exists()) dir.mkdir();


            /////////////////////////////////////////////////////
            ///// Compute, sort, and build reports for data sets
            ////////////////////////////////////////////////////
            masterLog.info("About to Build Reports for " + arg_grpName + "...");
            //sort by average value and save to file
            String avgValFile = sortedTotalsFileBase + "All " + arg_grpName + " - Total Results - By Average Value.csv";
            unsortedStatistics.orderByIsbAverageValue();
            unsortedStatistics.writeToFile(avgValFile, true);
            //create file holding the top ten percent greatest avg val triggers
            String topTenAvgValFile = topTenFileBase + arg_grpName + " - Top Ten Percent by Greatest Avg Val.csv";
            computeTopTen(avgValFile, topTenAvgValFile, arg_grpName);

            //build the report using the ordered top ten percent data
            ReportBuilder topTenAvgValReport = new ReportBuilder(arg_grpName + " - Longest Average Execution",
                    "Top 10% of " + arg_grpName + " with the longest average execution times, sorted from greatest"
                            +" to least average execution time. The 'Avg. Execution Time'"
                            +" represents the average execution time for all executions within the report time period."
                            +" The 'Num. Exec.' represents the number of times the statistic was captured within the"
                            +" report time period.",
                    topTenAvgValReportJrxml,
                    topTenAvgValFile,
                    reportOutFileBase + arg_grpName + " - Top Ten Percent by Greatest Avg Val.pdf",
                    unsortedStatistics.getCollectionEarliestStartDate(),
                    unsortedStatistics.getCollectionLatestEndDate());
            topTenAvgValReport.generateReport();
            masterLog.info("Successfully created report: " + arg_grpName + " - Longest Average Execution");

            //sort by maximum value and save to file
            String maxValFile = sortedTotalsFileBase+ "All " + arg_grpName + " - Total Results - By Maximum Value.csv";
            unsortedStatistics.orderByIsbMaximumValue();
            unsortedStatistics.writeToFile(maxValFile, true);
            //create file holding the top ten percent greatest avg val triggers
            String topTenMaxValFile = topTenFileBase + arg_grpName + " - Top Ten Percent by Greatest Max Val.csv";
            computeTopTen(maxValFile, topTenMaxValFile, arg_grpName);

            //build the report using the ordered top ten percent data
            ReportBuilder topTenMaxValReport = new ReportBuilder(arg_grpName + " - Greatest Maximum Execution Time",
                    "Top 10% of " + arg_grpName + " with the greatest maximum execution times per single execution,"
                            +" sorted from greatest to least maximum execution time."
                            + " The 'Max. Execution time' represents the longest time a single execution took to"
                            + " complete within the report time period."
                            +" The 'Num. Exec.' represents the number of times the statistic was captured within the"
                            +" report time period.",
                    topTenMaxValReportJrxml,
                    topTenMaxValFile,
                    reportOutFileBase + arg_grpName + " - Top Ten Percent by Greatest Max Val.pdf",
                    unsortedStatistics.getCollectionEarliestStartDate(),
                    unsortedStatistics.getCollectionLatestEndDate());
            topTenMaxValReport.generateReport();
            masterLog.info("Successfully created report: " + arg_grpName + " - Greatest Maximum Execution Time");

            //sort by count value and save to file
            String cntValFile = sortedTotalsFileBase + "All " + arg_grpName + " - Total Results - By  Count Value.csv";
            unsortedStatistics.orderByIsbCountValue();
            unsortedStatistics.writeToFile(cntValFile, true);
            //create file holding the top ten percent greatest avg val triggers
            String topTenCntValFile = topTenFileBase + arg_grpName + " - Top Ten Percent by Greatest Count Val.csv";
            computeTopTen(cntValFile, topTenCntValFile, arg_grpName);

            //build the report using the ordered top ten percent data
            ReportBuilder topTenCountReport = new ReportBuilder(arg_grpName + " - Number of Executions Within Time Period",
                    "Top 10% of " + arg_grpName + " with the greatest total executions within the report time period,"
                            + " sorted from greatest to least executions within the time period."
                            + " '# of Executions' represents the number of executions within the report time period. 'Cumulative"
                            + " Total Exec.' represents the cumulative total number of executions since the last server statistics"
                            + " reset.",
                    topTenCountReportJrxml,
                    topTenCntValFile,
                    reportOutFileBase + arg_grpName + " - Top Ten Percent by Greatest Count Val.pdf",
                    unsortedStatistics.getCollectionEarliestStartDate(),
                    unsortedStatistics.getCollectionLatestEndDate());
            topTenCountReport.generateReport();
            masterLog.info("Successfully created report: " + arg_grpName + " - Number of Executions Within Time Period");

            //sort by total count value and save to file
            String totCntValFile = sortedTotalsFileBase + "All " + arg_grpName + " - Total Results - By Total Count Value.csv";
            unsortedStatistics.orderByIsbTotalCountValue();
            unsortedStatistics.writeToFile(totCntValFile, true);
            //create file holding the top ten percent greatest avg val triggers
            String topTenTotCntValFile = topTenFileBase + arg_grpName + " - Top Ten Percent by Greatest Total Count Val.csv";
            computeTopTen(totCntValFile, topTenTotCntValFile, arg_grpName);

            //build the report using the ordered top ten percent data
            ReportBuilder topTenTotCntReport = new ReportBuilder(arg_grpName + " - Greatest Cumulative Executions",
                    "Top 10% of " + arg_grpName + " with the greatest total executions since the beginning of server statistics"
                            + " collections, sorted from greatest to least."
                            + " '# of Executions' represents the number of executions within the report time period. 'Cumulative"
                            + " Total Exec.' represents the cumulative total number of executions since the last server statistics"
                            + " reset.",
                    topTenTotCntReportJrxml,
                    topTenTotCntValFile,
                    reportOutFileBase + arg_grpName + " - Top Ten Percent Greatest Cumulative Executions.pdf",
                    unsortedStatistics.getCollectionEarliestStartDate(),
                    unsortedStatistics.getCollectionLatestEndDate());
            topTenTotCntReport.generateReport();
            masterLog.info("Successfully created report: " + arg_grpName + " - Greatest Cumulative Executions");

            ///////////////////////////////////////////////////////////////////
            //create the target list report based on those targetListStats entries which appear on more than one of the top
            //ten reports.
            //////////////////////////////////////////////////////////////////

            StatisticsCollection targetCollection = new StatisticsCollection();
            //filter through all stats that appeared on at least one report and add those that appear on more than one
            // report to the statistics collection

            for (IntegrityStatisticBean isb : targetListStats) targetCollection.addToCollection(isb);

            String targetFile = sortedTotalsFileBase + "All " + arg_grpName + " - Target For Review and Action.csv";
            //targetCollection.orderByIsbNameValue();
            targetCollection.writeToFile(targetFile, true);

            //build the target report
            ReportBuilder targetReport = new ReportBuilder(arg_grpName + " - Target For Action",
                    arg_grpName + " statistics that have appeared on more than one top ten percent report. These items"
                            + " need to be reviewed as they have one or more combination of high use, long average execution time,"
                            + " or long maximum execution time.",
                    targetReportJrxml,
                    targetFile,
                    reportOutFileBase + arg_grpName + " - Target For Action List.pdf",
                    targetCollection.getCollectionEarliestStartDate(),
                    targetCollection.getCollectionLatestEndDate());
            targetReport.generateReport();
            masterLog.info("Successfully created report: " + arg_grpName + " - Target For Action");

            //clear class variables for next statistic group iteration.
            targetListStats.clear();
            allTopTenStatNames.clear();

            masterLog.info("");
            masterLog.info("Done creating reports for " + arg_grpName);

        } catch(Exception e) {
            masterLog.info("WARNING: ISSUE PROCESSING GROUP : " + arg_grpName + " - " + e);
            e.printStackTrace();
        }



    }

    private static void computeTopTen(String arg_srcFile, String arg_destFile, String arg_grpName){

        //compute and save the top 10% of statistic grp with the longest average runnning time which are not scheduled
        // triggers.
        StatisticsFileReader sortedFile = new StatisticsFileReader(arg_srcFile);
        StatisticsLibrary sortedLib = sortedFile.executeStatisticsRetrieval();
        StatisticsCollection sortedStats = sortedLib.getStatisticsGroupName(arg_grpName);

        StatisticsCollection topTenPercent = new StatisticsCollection();

        double dbl_topTenPercent = (sortedStats.getCollectionSize()-1) * .10;
        int int_topTenPercent = (int) dbl_topTenPercent;
        int index = sortedStats.getCollectionSize()-1;
        int endIndex = ((sortedStats.getCollectionSize()-1)-int_topTenPercent);
        while(index>endIndex){
            if (arg_grpName.equals("Triggers")) {
                //scheduled triggers are currently being discluded from reports.
                if (!sortedStats.getCollectionObject(index).getName().contains("Scheduled")) {
                    topTenPercent.addToCollection(sortedStats.getCollectionObject(index));
                    //determine if this stat has been added to any other top ten % report.
                    //if yes, it is on more than one report, add it to the target list report data
                    determineIfTargetStat(sortedStats.getCollectionObject(index));

                } else{
                    --endIndex;
                }
            } else {
                topTenPercent.addToCollection(sortedStats.getCollectionObject(index));
                //determine if this stat has been added to any other top ten % report.
                //if yes, it is on more than one report, add it to the target list report data
                determineIfTargetStat(sortedStats.getCollectionObject(index));
            }
            --index;
        }
        topTenPercent.writeToFile(arg_destFile, true);
        topTenPercent.clearCollection();
    }

    private static void determineIfTargetStat(IntegrityStatisticBean isb){
        //determines if the name of the argument isb has appeared in a different report previously
        boolean foundMatch = false;
        //check if the isb.getName() value is already in the list of all top ten % stats
        for(String name : allTopTenStatNames) {
            if (name.equals(isb.getName())) {
                //if yes, break
                foundMatch = true;
                break;
            }
        }
        if(!foundMatch) {
            //if no match, add the name to the list
            allTopTenStatNames.add(isb.getName());
        } else {
            //if allTopTenStatNames already contains the name and targetListStats does not already contain the name, add it.
            foundMatch = false;
            for(int i=0;i<targetListStats.size();i++){
                if(isb.getName().equals(targetListStats.get(i).getName())){
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch){ targetListStats.add(isb); }
        }
    }

    private static void analyzeHistoricalTrends(String arg_historyFilePath, String arg_grpName){
        //for the provided statistics group, read the  "All <Group Name> - Summarized Results - Historical Data.csv"
        //determine all unique statistic names
        //for each:
        // 1) get all unique entries in the historical file.
        // 2) compare the statistic entries for each unique name and determine how much they have changed
        // 3) determine if any have changed outside of a standard percentage
        // 4) for any who have changed beyond a standard percentage, generate a report:
        //     - Report will give the statistic group, name, and charts of how each statistic
        //      has performed over time.
        //     - Report will be saved as ..\Reports\<Group Name>\<Group Name> Performance To Review.pdf
        masterLog.info("Analysing Historical data within file: " + arg_historyFilePath);

        //controls output of warnings related to arithmetic.
        //currently turns off due to "sum" usually being null for data and outputting a warning
        // everytime it's percent change is calculated.
        boolean verboseArtithmetic = false;

        //prep file destinations for report building functionality
        String reportOutFileBase = masterDir + "\\Output\\"+arg_grpName+"\\Reports\\Summarized Performance\\";
        File dir = new File(reportOutFileBase);
        if(!dir.exists()) dir.mkdir();

        StatisticsFileReader history = new StatisticsFileReader(arg_historyFilePath);
        StatisticsLibrary hist_lib = history.executeStatisticsRetrieval();
        StatisticsCollection hist_statColl = hist_lib.getStatisticsGroupName(arg_grpName);

        //calculate and store the group's unique statistic computations.
        Long[] groupComputationValues = new Long[6];
        StatisticsCollection tempGrpComputeCollection = new StatisticsCollection();
        for(IntegrityStatisticBean isb : hist_statColl.getCollection()) tempGrpComputeCollection.addToCollection(isb);

        tempGrpComputeCollection.collapseAllStatistics();
        tempGrpComputeCollection.computeAllCollectionStatistics();

        groupComputationValues[0] = tempGrpComputeCollection.getCollectionCountValue();
        groupComputationValues[1] = tempGrpComputeCollection.getCollectionTotalCountValue();
        groupComputationValues[2] = tempGrpComputeCollection.getCollectionSumValue();
        groupComputationValues[3] = tempGrpComputeCollection.getCollectionMinimumValue();
        groupComputationValues[4] = tempGrpComputeCollection.getCollectionMaximumValue();
        groupComputationValues[5] = tempGrpComputeCollection.getCollectionAverageValue();

        //get all unique names of stats to iterate through
        HashMap<String, String> uniqueHistNames = hist_statColl.getAllUniqueNameGroupPairs();

        for(String name : uniqueHistNames.keySet()){ //iterate through unique names in collection
            StatisticsCollection temp_collection = new StatisticsCollection();

            for(IntegrityStatisticBean isb : hist_statColl.getCollection()){
                if (isb.getName().equals(name)) {
                    temp_collection.addToCollection(isb);
                }
            }

            if(temp_collection.getCollectionSize()>1) {

                int collectionSize = temp_collection.getCollectionSize();
                DateTime[] startDates = new DateTime[collectionSize];
                DateTime[] endDates = new DateTime[collectionSize];
                Long[] countTracker = new Long[collectionSize];
                Long[] totalCountTracker = new Long[collectionSize];
                Long[] sumTracker = new Long[collectionSize];
                Long[] minTracker = new Long[collectionSize];
                Long[] maxTracker = new Long[collectionSize];
                Long[] avgTracker = new Long[collectionSize];
                Float[] percChange_count = new Float[collectionSize];
                Float[] percChange_totalCount = new Float[collectionSize];
                Float[] percChange_sum = new Float[collectionSize];
                Float[] percChange_min = new Float[collectionSize];
                Float[] percChange_max = new Float[collectionSize];
                Float[] percChange_avg = new Float[collectionSize];

                for(int i=0;i<collectionSize;i++){
                    countTracker[i] = temp_collection.getCollectionObject(i).getCount();
                    totalCountTracker[i] = temp_collection.getCollectionObject(i).getTotalCount();
                    sumTracker[i] = temp_collection.getCollectionObject(i).getSum();
                    minTracker[i] = temp_collection.getCollectionObject(i).getMin();
                    maxTracker[i] = temp_collection.getCollectionObject(i).getMax();
                    avgTracker[i] = temp_collection.getCollectionObject(i).getAverage();
                    startDates[i] = temp_collection.getCollectionObject(i).getStartDate();
                    endDates[i] = temp_collection.getCollectionObject(i).getEndDate();

                    if (i>=1){
                        try{
                            percChange_count[i] = ( (countTracker[i]-countTracker[i-1]) /
                                    (countTracker[i-1]) )*100.0F;
                        } catch(ArithmeticException ae){
                            if (verboseArtithmetic) masterLog.warn("    WARNING: could not compute Count percent change for "
                                    + arg_grpName + " '" + temp_collection.getCollectionObject(0).getName() + "' due to arithmetic error: "
                                    + ae);
                            percChange_count[i] = 0.0F;
                        }
                        try{
                            percChange_totalCount[i] = ( (totalCountTracker[i]-totalCountTracker[i-1]) /
                                    (totalCountTracker[i-1]) )*100.0F;
                        } catch(ArithmeticException ae){
                            if (verboseArtithmetic) masterLog.warn("    WARNING: could not compute Total Count percent change for "
                                    + arg_grpName + " '" + temp_collection.getCollectionObject(0).getName() + "' due to arithmetic error: "
                                    + ae);
                            percChange_totalCount[i] = 0.0F;
                        }
                        try{
                            percChange_sum[i] = ( (sumTracker[i]-sumTracker[i-1]) /
                                    (sumTracker[i-1]) )*100.0F;
                        } catch(ArithmeticException ae){
                            if(verboseArtithmetic) masterLog.warn("    WARNING: could not compute Total Sum percent change for "
                                    + arg_grpName + " '" + temp_collection.getCollectionObject(0).getName() + "' due to arithmetic error: "
                                    + ae);
                            percChange_sum[i] = 0.0F;
                        }
                        try{
                            percChange_min[i] = ( (minTracker[i]-minTracker[i-1]) /
                                    (minTracker[i-1]) )*100.0F;
                        } catch(ArithmeticException ae){
                            if (verboseArtithmetic) masterLog.warn("    WARNING: could not compute Total Min percent change for "
                                    + arg_grpName + " '" + temp_collection.getCollectionObject(0).getName() + "' due to arithmetic error: "
                                    + ae);
                            percChange_min[i] = 0.0F;
                        }
                        try{
                            percChange_max[i] = ( (maxTracker[i]-maxTracker[i-1]) /
                                    (maxTracker[i-1]) )*100.0F;
                        } catch(ArithmeticException ae){
                            if (verboseArtithmetic) masterLog.warn("    WARNING: could not compute Total Max percent change for "
                                    + arg_grpName + " '" + temp_collection.getCollectionObject(0).getName() + "' due to arithmetic error: "
                                    + ae);
                            percChange_max[i] = 0.0F;
                        }
                        try{
                            percChange_avg[i] = ( (avgTracker[i]-avgTracker[i-1]) /
                                    (avgTracker[i-1]) )*100.0F;
                        } catch(ArithmeticException ae){
                            if (verboseArtithmetic) masterLog.warn("    WARNING: could not compute Total Avg percent change for "
                                    + arg_grpName + " '" + temp_collection.getCollectionObject(0).getName() + "' due to arithmetic error: "
                                    + ae);
                            percChange_avg[i] = 0.0F;
                        }
                    } else {
                        percChange_count[i] = 0.0F;
                        percChange_totalCount[i] = 0.0F;
                        percChange_sum[i] = 0.0F;
                        percChange_min[i] = 0.0F;
                        percChange_max[i] = 0.0F;
                        percChange_avg[i] = 0.0F;
                    }
                }// end for loop through this statistic's data entries

                String histPerfDir = masterDir + "\\Output\\" + arg_grpName + "\\Historical Performance\\";
                dir = new File(histPerfDir);
                if(!dir.exists()) dir.mkdir();

                String histPerfFile = histPerfDir + name + ".csv";

                ArrayList<Long[]> masterStatHist = new ArrayList();
                masterStatHist.add(countTracker);
                masterStatHist.add(totalCountTracker);
                masterStatHist.add(sumTracker);
                masterStatHist.add(minTracker);
                masterStatHist.add(maxTracker);
                masterStatHist.add(avgTracker);

                ArrayList<Float[]> masterPercChange = new ArrayList();
                masterPercChange.add(percChange_count);
                masterPercChange.add(percChange_totalCount);
                masterPercChange.add(percChange_sum);
                masterPercChange.add(percChange_min);
                masterPercChange.add(percChange_max);
                masterPercChange.add(percChange_avg);

                writeToHistoricalPerformanceFile(histPerfFile,
                        masterStatHist,
                        masterPercChange,
                        groupComputationValues,
                        startDates,
                        endDates);

                //build the summarized historical report
                ReportBuilder summaryHistPerfReport = new ReportBuilder(arg_grpName+ " - Summarized Performance Report - " + name,
                        "Summarized Historical Performance for statistic: " + arg_grpName + " - " + name,
                        summaryHistPerformanceJrxml,
                        histPerfFile,
                        reportOutFileBase + arg_grpName + " - Summarized Performance Report - " + name + ".pdf",
                        name,
                        arg_grpName);
                summaryHistPerfReport.generateReport();
                masterLog.info("    Successfully created report: " + arg_grpName + "- Summarized Performance Report - " + name);

                masterLog.info("    Historical analysis done for statistic: "+ arg_grpName
                        + " - " + name);
                masterLog.info("");
            } else { //end if temp_collection has more than one entry.
                masterLog.warn("    Could not conduct historical analysis, only one entry for statistic: " + arg_grpName
                        + " - " + name);
                masterLog.info("");
            }
            temp_collection.clearCollection();

        } //end for loop iteration through unique names in collection.
    }


    public static void writeToHistoricalPerformanceFile(String arg_destinationFilePath,
                                                        ArrayList<Long[]> arg_statHistEntries,
                                                        ArrayList<Float[]> arg_percChangeEntries,
                                                        Long[] arg_grpComputeEntries,
                                                        DateTime[] arg_startDates,
                                                        DateTime[] arg_endDates){

        File file = new File(arg_destinationFilePath);
        StringBuilder fileContent = new StringBuilder();

        if(!file.exists()) fileContent.append("Start Date,End Date,Count, delta_Count, group_Count, Total Count, delta_Total Count, group_Total Count, "
                + "Sum, delta_Sum, group_Sum, Min, delta_Min, group_Min, Max, delta_Max, group_Max, Avg, delta_Avg, "
                + "group_avg" + "\n");

        for(int i=0;i<arg_statHistEntries.get(0).length;i++) {
            fileContent.append(fmt.print(arg_startDates[i]) + ",");
            fileContent.append(fmt.print(arg_endDates[i]) + ",");
            fileContent.append(arg_statHistEntries.get(0)[i] + ",");
            fileContent.append(arg_percChangeEntries.get(0)[i] + ",");
            fileContent.append(arg_grpComputeEntries[0] + ",");
            fileContent.append(arg_statHistEntries.get(1)[i] + ",");
            fileContent.append(arg_percChangeEntries.get(1)[i] + ",");
            fileContent.append(arg_grpComputeEntries[1] + ",");
            fileContent.append(arg_statHistEntries.get(2)[i] + ",");
            fileContent.append(arg_percChangeEntries.get(2)[i] + ",");
            fileContent.append(arg_grpComputeEntries[2] + ",");
            fileContent.append(arg_statHistEntries.get(3)[i] + ",");
            fileContent.append(arg_percChangeEntries.get(3)[i] + ",");
            fileContent.append(arg_grpComputeEntries[3] + ",");
            fileContent.append(arg_statHistEntries.get(4)[i] + ",");
            fileContent.append(arg_percChangeEntries.get(4)[i] + ",");
            fileContent.append(arg_grpComputeEntries[4] + ",");
            fileContent.append(arg_statHistEntries.get(5)[i] + ",");
            fileContent.append(arg_percChangeEntries.get(5)[i] + ",");
            fileContent.append(arg_grpComputeEntries[5]);
            fileContent.append("\n");
        }

        if (fileContent.toString().length()!=0){
            Path filePath = Paths.get(arg_destinationFilePath);

            try {

                if(file.exists()){
                    Files.write(filePath, fileContent.toString().getBytes(), StandardOpenOption.APPEND);
                } else{
                    Files.write(filePath, fileContent.toString().getBytes());
                }
                masterLog.info("    Sucessfully wrote file: " + arg_destinationFilePath);
            } catch (IOException x) {
                masterLog.warn("    IOException: %s%n", x);
            }
        } else {
            masterLog.warn("    WARNING: EMPTY DATA FOR TARGET FILE: " + arg_destinationFilePath);
            //fileContents is empty
            //TODO: throw error or warn?
        }

    }

}
