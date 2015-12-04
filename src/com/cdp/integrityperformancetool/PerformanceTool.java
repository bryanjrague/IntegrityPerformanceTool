package com.cdp.integrityperformancetool;

import com.cdp.integrityperformancetool.reporting.ReportBuilder;
import com.cdp.integrityperformancetool.util.StatisticsFileReader;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static String runTimeStr = runTime.toString().replace(":","-").replace(".","_").replace("T"," @ ");

    //logging via log4j
    private static Logger masterLog = Logger.getLogger("PerformanceTool");

    private static DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MMM-dd-yyyy HH:mm:ss");

    ////////////////////////////////////////////////
    ///// Make sure to change this directory to the local machine directory
    ////////////////////////////////////////////////
    //private static String masterDir = "C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool";
    private static String masterDir = "C:\\Users\\bryan\\IdeaProjects\\IntegrityPerformanceTool";

    private static ArrayList<String> allTopTenStatNames = new ArrayList<String>(); //stores all stats which appear on any of the top ten % lists
    private static ArrayList<IntegrityStatisticBean> targetListStats =  new ArrayList<IntegrityStatisticBean>();; //stores any stats which appear on more than one of the top ten % lists

    //report building variables holding filepaths to the .jrxml files used to create reports.
    private static String topTenAvgValReportJrxml = masterDir +"\\ReportBuildingData\\ipt_topTenPercent_avg.jrxml";
    private static String topTenMaxValReportJrxml = masterDir +"\\ReportBuildingData\\ipt_topTenPercent_max.jrxml";
    private static String topTenCountReportJrxml = masterDir +"\\ReportBuildingData\\ipt_topTenPercent_count.jrxml";
    private static String topTenTotCntReportJrxml = masterDir +"\\ReportBuildingData\\ipt_topTenPercent_total.jrxml";
    private static String targetReportJrxml = masterDir +"\\ReportBuildingData\\ipt_topTenPercent_target.jrxml";
    private static String detailHistPerformanceJrxml = masterDir +"\\ReportBuildingData\\ipt_detailedPerformanceReport.jrxml";
    private static String summaryHistPerformanceJrxml = masterDir +"\\ReportBuildingData\\ipt_summarizedPerformanceReport.jrxml";

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
        //String masterDataFilePath = masterDir + "\\Input\\Statistics_PROD_10-15-2015_11-1-2015.csv";
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

        //prep file destinations for report building functionality
        String reportOutFileBase = masterDir + "\\Output\\"+arg_grpName+"\\Reports\\";
        File dir = new File(reportOutFileBase);
        if(!dir.exists()) dir.mkdir();

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
                temp_collection.writeToFile(filePath.toString(), false);
                try{
                    analyzeHistoricalTrends(filePath.toString(), arg_grpName, name, "Detailed");
                } catch(Exception e){
                    masterLog.warn("WARNING: Exception during history analysis: " + e);
                }

                masterLog.info("\t" + arg_grpName + " - processed data for statistic: " + name);
            }

            String sortedTotalsFileBase = masterDir + "\\Output\\"+arg_grpName+"\\Totals\\";
            dir = new File(sortedTotalsFileBase);
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
            // disabled for now...   analyzeHistoricalTrends(currStatTotalsFile, arg_grpName, name, "Summarized");
            }

            masterLog.info("");
            masterLog.info("Appended all summarized entries to historical summarized results file for: " + arg_grpName);
            masterLog.info("\t " + currStatTotalsFile);
            masterLog.info("");

            ///////////////////////////////////////////////////
            //// Gather unsorted total statistics and sort. run various sort functions. save output as files.
            ///////////////////////////////////////////////////
            StatisticsFileReader unsortedTotalsStatsFile = new StatisticsFileReader(currStatTotalsFile);
            StatisticsLibrary unsortedTotalsLibrary = unsortedTotalsStatsFile.executeStatisticsRetrieval();
            StatisticsCollection unsortedStatistics = unsortedTotalsLibrary.getStatisticsGroupName(arg_grpName);

            unsortedStatistics.collapseAllStatistics();

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

            //combine all pdf reports into a single pdf doc.
            createReportPdfBook(arg_grpName);

            masterLog.info("Done processing statistics and creating reports for Group: " + arg_grpName);
        } catch(Exception e) {
            masterLog.info("WARNING: ISSUE PROCESSING GROUP : " + arg_grpName + " - " + e);
            e.printStackTrace();
        }



    }

    private static void createReportPdfBook(String arg_grpName){
        String reportDirStr = masterDir + "\\Output\\"+arg_grpName+"\\Reports\\DetailedPerformanceReports\\";
        File reportDir = new File(reportDirStr);
        ArrayList<String> pdfFileNames = new ArrayList<String>(Arrays.asList(reportDir.list()));
        PDFMergerUtility pdfUtil = new PDFMergerUtility();

        for(String fileName : pdfFileNames) { pdfUtil.addSource(reportDirStr + fileName); }

        try{
            String masterReportBookStr = masterDir + "\\Integrity Performance Tool - " + arg_grpName + " - " + fmt.print(runTime).replace(":","-") + ".pdf";
            pdfUtil.setDestinationFileName(masterReportBookStr);
            pdfUtil.mergeDocuments();
            masterLog.info("");
            masterLog.info("Combined all " + arg_grpName + " Reports into master report book!");
            masterLog.info(masterReportBookStr);
            masterLog.info("");
        } catch(Exception e){
            masterLog.warn("ERROR: Problem writing all reports to final PDF: " + e);
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

    private static void analyzeHistoricalTrends(String arg_historyFilePath, String arg_grpName, String arg_statName, String arg_analysisType){
        //arg_analysisType is one of either "Detailed" or "Summarized" and determines the report built and save location.
        if(arg_analysisType.equals("Detailed") || arg_analysisType.equals("Summarized")) {

            masterLog.info("\t Analysing Historical data within file: " + arg_historyFilePath);
            masterLog.info("\t Analysis Type: " + arg_analysisType);

            //controls output of warnings related to arithmetic.
            //currently turns off due to "sum" usually being null for data and outputting a warning
            // everytime it's percent change is calculated.
            boolean verboseArtithmetic = false;

            StatisticsFileReader history = new StatisticsFileReader(arg_historyFilePath);
            StatisticsLibrary hist_lib = history.executeStatisticsRetrieval();
            StatisticsCollection hist_statColl = hist_lib.getStatisticsGroupName(arg_grpName);

            //if analyzing summarized stats, get only the IntegrityStatisticBeans related to the statistic name provided
            if (arg_analysisType.equals("Summarized")) {
                StatisticsCollection temp_collection = new StatisticsCollection();
                for(IntegrityStatisticBean isb : hist_statColl.getCollection()){
                    if(isb.getName().equals(arg_statName) && isb.getGroup().equals(arg_grpName)){
                        temp_collection.addToCollection(isb);
                    }
                }
                hist_statColl.clearCollection();
                for(IntegrityStatisticBean isb : temp_collection.getCollection()) hist_statColl.addToCollection(isb);
            }

            if(hist_statColl.getCollectionSize()>0) {
                int collectionSize = hist_statColl.getCollectionSize();
                DateTime[] startDates = new DateTime[collectionSize];
                DateTime[] endDates = new DateTime[collectionSize];
                Long[] countTracker = new Long[collectionSize];
                Long[] totalCountTracker = new Long[collectionSize];
                Long[] sumTracker = new Long[collectionSize];
                Long[] minTracker = new Long[collectionSize];
                Long[] maxTracker = new Long[collectionSize];
                Long[] avgTracker = new Long[collectionSize];
                Double[] percChange_count = new Double[collectionSize];
                Double[] percChange_totalCount = new Double[collectionSize];
                Double[] percChange_sum = new Double[collectionSize];
                Double[] percChange_min = new Double[collectionSize];
                Double[] percChange_max = new Double[collectionSize];
                Double[] percChange_avg = new Double[collectionSize];
                String stat_unit = hist_statColl.getCollectionObject(0).getUnit();

                for (int i = 0; i < collectionSize; i++) {

                    countTracker[i] = hist_statColl.getCollectionObject(i).getCount();
                    totalCountTracker[i] = hist_statColl.getCollectionObject(i).getTotalCount();
                    sumTracker[i] = hist_statColl.getCollectionObject(i).getSum();
                    minTracker[i] = hist_statColl.getCollectionObject(i).getMin();
                    maxTracker[i] = hist_statColl.getCollectionObject(i).getMax();
                    avgTracker[i] = hist_statColl.getCollectionObject(i).getAverage();
                    startDates[i] = hist_statColl.getCollectionObject(i).getStartDate();
                    endDates[i] = hist_statColl.getCollectionObject(i).getEndDate();


                    if (i >= 1) {
                        try {
                            percChange_count[i] = ((new Double(countTracker[i]) - new Double(countTracker[i - 1])) /
                                    (new Double(countTracker[i - 1]))) * 100.00;
                        } catch (ArithmeticException ae) {
                            if (verboseArtithmetic)
                                masterLog.warn("    WARNING: could not compute Count percent change for "
                                        + arg_grpName + " '" + arg_statName + "' due to arithmetic error: "
                                        + ae);
                            percChange_count[i] = 0.00;
                        }
                        try {
                            percChange_totalCount[i] = ((new Double(totalCountTracker[i]) - new Double(totalCountTracker[i - 1])) /
                                    (new Double(totalCountTracker[i - 1]))) * 100.00;
                        } catch (ArithmeticException ae) {
                            if (verboseArtithmetic)
                                masterLog.warn("    WARNING: could not compute Total Count percent change for "
                                        + arg_grpName + " '" + arg_statName + "' due to arithmetic error: "
                                        + ae);
                            percChange_totalCount[i] = 0.00;
                        }
                        try {
                            percChange_sum[i] = ((new Double(sumTracker[i]) - new Double(sumTracker[i - 1])) /
                                    (new Double(sumTracker[i - 1]))) * 100.00;
                        } catch (ArithmeticException ae) {
                            if (verboseArtithmetic)
                                masterLog.warn("    WARNING: could not compute Total Sum percent change for "
                                        + arg_grpName + " '" + arg_statName + "' due to arithmetic error: "
                                        + ae);
                            percChange_sum[i] = 0.00;
                        }
                        try {
                            percChange_min[i] = ((new Double(minTracker[i]) - new Double(minTracker[i - 1])) /
                                    (new Float(minTracker[i - 1]))) * 100.00;
                        } catch (ArithmeticException ae) {
                            if (verboseArtithmetic)
                                masterLog.warn("    WARNING: could not compute Total Min percent change for "
                                        + arg_grpName + " '" + arg_statName + "' due to arithmetic error: "
                                        + ae);
                            percChange_min[i] = 0.00;
                        }
                        try {
                            percChange_max[i] = ((new Double(maxTracker[i]) - new Double(maxTracker[i - 1])) /
                                    (new Float(maxTracker[i - 1]))) * 100.00;
                        } catch (ArithmeticException ae) {
                            if (verboseArtithmetic)
                                masterLog.warn("    WARNING: could not compute Total Max percent change for "
                                        + arg_grpName + " '" + arg_statName + "' due to arithmetic error: "
                                        + ae);
                            percChange_max[i] = 0.00;
                        }
                        try {
                            percChange_avg[i] = ((new Double(avgTracker[i]) - new Double(avgTracker[i - 1])) /
                                    (new Double(avgTracker[i - 1]))) * 100.00;
                        } catch (ArithmeticException ae) {
                            if (verboseArtithmetic)
                                masterLog.warn("    WARNING: could not compute Total Avg percent change for "
                                        + arg_grpName + " '" + arg_statName + "' due to arithmetic error: "
                                        + ae);
                            percChange_avg[i] = 0.00;
                        }
                    } else {
                        percChange_count[i] = 0.00;
                        percChange_totalCount[i] = 0.00;
                        percChange_sum[i] = 0.00;
                        percChange_min[i] = 0.00;
                        percChange_max[i] = 0.00;
                        percChange_avg[i] = 0.00;
                    }

                }// end for loop through this statistic's data entries

                String histPerfFile;

                if (arg_analysisType.equals("Detailed")) {
                    String histPerfDir = masterDir + "\\Output\\" + arg_grpName + "\\HistoricalDetailedPerformance\\";
                    File dir = new File(histPerfDir);
                    if (!dir.exists()) dir.mkdir();
                    histPerfFile = histPerfDir + arg_statName + ".csv";
                } else {
                    String histPerfDir = masterDir + "\\Output\\" + arg_grpName + "\\HistoricalSummarizedPerformance\\";
                    File dir = new File(histPerfDir);
                    if (!dir.exists()) dir.mkdir();
                    histPerfFile = histPerfDir + arg_statName + ".csv";
                }

                ArrayList<Long[]> masterStatHist = new ArrayList();
                masterStatHist.add(countTracker);
                masterStatHist.add(totalCountTracker);
                masterStatHist.add(sumTracker);
                masterStatHist.add(minTracker);
                masterStatHist.add(maxTracker);
                masterStatHist.add(avgTracker);

                ArrayList<Double[]> masterPercChange = new ArrayList();
                masterPercChange.add(percChange_count);
                masterPercChange.add(percChange_totalCount);
                masterPercChange.add(percChange_sum);
                masterPercChange.add(percChange_min);
                masterPercChange.add(percChange_max);
                masterPercChange.add(percChange_avg);

                //calculate and store the group's unique statistic computations.
                Long[] groupComputationValues = new Long[6];

                hist_statColl.computeAllCollectionStatistics();

                groupComputationValues[0] = hist_statColl.getCollectionCountValue();
                groupComputationValues[1] = hist_statColl.getCollectionTotalCountValue();
                groupComputationValues[2] = hist_statColl.getCollectionSumValue();
                groupComputationValues[3] = hist_statColl.getCollectionMinimumValue();
                groupComputationValues[4] = hist_statColl.getCollectionMaximumValue();
                groupComputationValues[5] = hist_statColl.getCollectionAverageValue();

                try {
                    writeToHistoricalPerformanceFile(histPerfFile,
                            masterStatHist,
                            masterPercChange,
                            groupComputationValues,
                            startDates,
                            endDates);
                } catch (Exception e) {
                    masterLog.warn("\t WARNING: Error writing historical performance file for: " + arg_statName);
                    masterLog.warn("\t " + e);
                }

                String reportOut = masterDir + "\\Output\\" + arg_grpName + "\\Reports\\";
                File dir = new File(reportOut);
                if (!dir.exists()) dir.mkdir();

                //prep file destinations for report building functionalit
                String reportOutHist;
                if (arg_analysisType.equals("Detailed")) {
                    reportOutHist = reportOut + "\\DetailedPerformanceReports\\";
                    dir = new File(reportOutHist);
                    if (!dir.exists()) dir.mkdir();
                } else {
                    reportOutHist = reportOut + "\\SummarizedPerformanceReports\\";
                    dir = new File(reportOutHist);
                    if (!dir.exists()) dir.mkdir();
                }


                try {
                    //build the summarized historical report
                    if(arg_analysisType.equals("Detailed")) {
                        ReportBuilder summaryHistPerfReport = new ReportBuilder(arg_grpName + " - Detailed Performance Report - " + arg_statName,
                                "Historical Performance for statistic: " + arg_grpName + " - " + arg_statName,
                                detailHistPerformanceJrxml,
                                histPerfFile,
                                reportOutHist + arg_statName + ".pdf",
                                arg_statName,
                                arg_grpName,
                                groupComputationValues,
                                stat_unit);
                        summaryHistPerfReport.generateReport();
                        masterLog.info("    Successfully created report: " + arg_grpName + "- Detailed Performance Report - "
                                + arg_statName);
                    } else {
                        ReportBuilder summaryHistPerfReport = new ReportBuilder(arg_grpName + " - Summarized Performance Report - " + arg_statName,
                                "Historical Performance for statistic: " + arg_grpName + " - " + arg_statName,
                                summaryHistPerformanceJrxml,
                                histPerfFile,
                                reportOutHist + arg_statName + ".pdf",
                                arg_statName,
                                arg_grpName,
                                groupComputationValues,
                                stat_unit);
                        summaryHistPerfReport.generateReport();
                        masterLog.info("    Successfully created report: " + arg_grpName + "- Summarized Performance Report - "
                                + arg_statName);
                    }
                } catch (Exception e) {
                    masterLog.warn("\t WARNING: Error creating historical performance report for: " + arg_statName);
                    masterLog.warn("\t " + e);
                }
            }
        } else {
            masterLog.warn("WARNING: INVALID value '" + arg_analysisType + " for 'arg_analysisType' in analyzeHistoricalTrends() invocation. No analysis"
                   + " or reports generated!");
        }


        masterLog.info("    Historical analysis done for statistic: "+ arg_statName
                + " - " + arg_statName);
        masterLog.info("");
    }


    public static void writeToHistoricalPerformanceFile(String arg_destinationFilePath,
                                                        ArrayList<Long[]> arg_statHistEntries,
                                                        ArrayList<Double[]> arg_percChangeEntries,
                                                        Long[] arg_grpComputeEntries,
                                                        DateTime[] arg_startDates,
                                                        DateTime[] arg_endDates){

        File file = new File(arg_destinationFilePath);
        StringBuilder fileContent = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.##");


       fileContent.append("Start Date,End Date,Count, delta_Count, group_Count, Total Count, delta_Total Count, group_Total Count, "
                + "Sum, delta_Sum, group_Sum, Min, delta_Min, group_Min, Max, delta_Max, group_Max, Avg, delta_Avg, "
                + "group_avg" + "\n");

        for(int i=0;i<arg_statHistEntries.get(0).length;i++) {
            fileContent.append(fmt.print(arg_startDates[i]) + ",");
            fileContent.append(fmt.print(arg_endDates[i]) + ",");
            fileContent.append(arg_statHistEntries.get(0)[i] + ",");
            fileContent.append(df.format(arg_percChangeEntries.get(0)[i]) + ",");
            fileContent.append(arg_grpComputeEntries[0] + ",");
            fileContent.append(arg_statHistEntries.get(1)[i] + ",");
            fileContent.append(df.format(arg_percChangeEntries.get(1)[i]) + ",");
            fileContent.append(arg_grpComputeEntries[1] + ",");
            fileContent.append(arg_statHistEntries.get(2)[i] + ",");
            fileContent.append(df.format(arg_percChangeEntries.get(2)[i]) + ",");
            fileContent.append(arg_grpComputeEntries[2] + ",");
            fileContent.append(arg_statHistEntries.get(3)[i] + ",");
            fileContent.append(df.format(arg_percChangeEntries.get(3)[i]) + ",");
            fileContent.append(arg_grpComputeEntries[3] + ",");
            fileContent.append(arg_statHistEntries.get(4)[i] + ",");
            fileContent.append(df.format(arg_percChangeEntries.get(4)[i]) + ",");
            fileContent.append(arg_grpComputeEntries[4] + ",");
            fileContent.append(arg_statHistEntries.get(5)[i] + ",");
            fileContent.append(df.format(arg_percChangeEntries.get(5)[i]) + ",");
            fileContent.append(arg_grpComputeEntries[5]);
            fileContent.append("\n");

            //System.out.println(i+":"+fileContent.toString());
        }

        if (fileContent.toString().length()!=0){
            Path filePath = Paths.get(arg_destinationFilePath);

            try {

              //  if(file.exists()){
              //      Files.write(filePath, fileContent.toString().getBytes(), StandardOpenOption.APPEND);
              //  } else{
                    Files.write(filePath, fileContent.toString().getBytes());
              //  }
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
