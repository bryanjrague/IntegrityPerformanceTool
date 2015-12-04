package com.cdp.integrityperformancetool;

import com.cdp.integrityperformancetool.IntegrityStatisticBean;
import com.cdp.integrityperformancetool.StatisticsCollection;
import com.cdp.integrityperformancetool.StatisticsLibrary;
import com.cdp.integrityperformancetool.reporting.ReportBuilder;
import com.cdp.integrityperformancetool.util.MergeSort;
import com.cdp.integrityperformancetool.util.StatisticsFileReader;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The main executable class of the project. This class can be modified to create the procedure required to process
 * the statistics and create files, reports, etc.
 */
public class PerformanceTool {

    private static DateTime runTime = new DateTime();
    private static String runTimeStr = runTime.toString().replace(":","-").replace(".","_").replace("T"," @ ");

    //logging via log4j
    private static Logger masterLog = Logger.getLogger("PerformanceToolOrig");

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
        String masterDataFilePath = masterDir + "\\Input\\Statistics_PROD_10-1-2015_10-14-2015.csv";
        //String masterDataFilePath = masterDir + "\\Input\\Statistics_PROD_10-15-2015_11-1-2015.csv";
        //String masterDataFilePath = masterDir + "\\Input\\Statistics_PROD_11-1-2015_11-15-2015.csv";
        StatisticsLibrary masterDataLib = getStatsLibraryFromFile(masterDataFilePath, ",",1);
        masterLog.info("Source Data File: " +  masterDataFilePath);
        masterLog.info("");

        //////////////////////////////////////////////
        //// Send master statistics library group(s) for processing
        //////////////////////////////////////////////
        processStatisticsGroup("Triggers", masterDataLib);
        //then process statistics for other group names once the report and statistics analysis methods are complete


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

        String outputDirStr = masterDir + "\\Output\\";
        File outputDir = new File(outputDirStr);
        if(!outputDir.exists()) outputDir.mkdir();

        String grpOutputDirStr = masterDir + "\\Output\\"+arg_grpName+"\\";
        File grpOutputDir = new File(grpOutputDirStr);
        if(!grpOutputDir.exists()) grpOutputDir.mkdir();

        //prep file destinations for report building functionality
        String reportOutFileBase = masterDir + "\\Output\\"+arg_grpName+"\\Reports\\";
        File dir = new File(reportOutFileBase);
        if(!dir.exists()) dir.mkdir();



        try{
            ///////////////////////////////////////////////
            /// Get only the specific Statistic Group data from the master lib
            ///////////////////////////////////////////////
            StatisticsCollection statGrp_allStats = arg_library.getStatisticsGroupName(arg_grpName);

            ///////////////////////////////////////////////
            /// Store the "cumulative" and "delta" data into separate StatisticsCollection objects
            ///////////////////////////////////////////////
            StatisticsCollection statGrp_cumulative = new StatisticsCollection("All " + arg_grpName + " - Cumulative only");
            StatisticsCollection statGrp_delta = new StatisticsCollection("All " + arg_grpName + " - Delta only");

            //iterate through all group statistics and sort into either the cumulative or delta collections
            for(IntegrityStatisticBean isb : statGrp_allStats.getCollection()){
                if(isb.getMode().equals("cumulative")){
                    statGrp_cumulative.addToCollection(isb);
                } else if(isb.getMode().equals("delta")){
                    statGrp_delta.addToCollection(isb);
                } else masterLog.warn("WARNING: Statistic for " + arg_grpName + " has unknown mode: " + isb.getMode()
                        +"\n Could not process Statistic!");
            }

            ////////////////////////////////////////////////
            /// For each unique statistic name in the cumulative only collection, create and store a file of all stat entries
            //// If file already exists in output, append to the bottom of the existing file.
            //// Then conduct historical performance analysis on each
            ///////////////////////////////////////////////

            String cumulativeDirStr = masterDir + "\\Output\\"+arg_grpName+"\\RawCumulativeHistoryFiles\\";
            File cumulativeDir = new File(cumulativeDirStr);
            if(!cumulativeDir.exists()) cumulativeDir.mkdir();

            String deltaDirStr = masterDir + "\\Output\\"+arg_grpName+"\\RawDeltaHistoryFiles\\";
            File deltaDir = new File(deltaDirStr);
            if(!deltaDir.exists()) deltaDir.mkdir();

            writeToHistoricalFile(statGrp_cumulative, arg_grpName, cumulativeDirStr);
            writeToHistoricalFile(statGrp_delta, arg_grpName, deltaDirStr);

            String dailyPerformanceDirStr = masterDir + "\\Output\\"+arg_grpName+"\\DailyPerformanceHistoryFiles\\";
            File dailyCumulativeDir = new File(dailyPerformanceDirStr);
            if(!dailyCumulativeDir.exists()) dailyCumulativeDir.mkdir();

            ArrayList<String> cumulativeFileNamesList = new ArrayList<String>(Arrays.asList(cumulativeDir.list()));
            ArrayList<String> deltaFileNamesList = new ArrayList<String>(Arrays.asList(deltaDir.list()));

            //for(int i=0;i<cumulativeFileNamesList.size();i++){
            for(int i=0;i<1;i++){
                String currFileName = cumulativeFileNamesList.get(i);
                masterLog.info("Working on file: " + currFileName);
                String[] currStatNameArray = currFileName.split("\\.");
                masterLog.info(currStatNameArray.length);
                String currStatName = currStatNameArray[0];
                masterLog.info(currStatNameArray.length + "Working on statistic: " + currStatName);

                if(deltaFileNamesList.contains(cumulativeFileNamesList.get(i))){
                    //there is both a cumulative and delta file entry, history analysis can be done

                    StatisticsFileReader cumulativeHistory = new StatisticsFileReader(cumulativeDirStr + currFileName);
                    StatisticsLibrary cumulativeHist_lib = cumulativeHistory.executeStatisticsRetrieval();
                    StatisticsCollection cumulativeHist_statColl = cumulativeHist_lib.getStatisticsGroupName(arg_grpName);

                    StatisticsFileReader deltaHistory = new StatisticsFileReader(deltaDirStr + currFileName);
                    StatisticsLibrary deltaHist_lib = deltaHistory.executeStatisticsRetrieval();
                    StatisticsCollection deltaHist_statColl = deltaHist_lib.getStatisticsGroupName(arg_grpName);

                    cumulativeHist_statColl.collapseStatisticsByDate();
                    deltaHist_statColl.collapseStatisticsByDate();

                    StatisticsCollection dailyPerformanceCollection = new StatisticsCollection();
                    for(IntegrityStatisticBean isb_c : cumulativeHist_statColl.getCollection()){
                        for(IntegrityStatisticBean isb_d : deltaHist_statColl.getCollection()){
                            if( (isb_c.getStartDate().getDayOfYear()==isb_d.getStartDate().getDayOfYear()) &&
                                    (isb_c.getEndDate().getDayOfYear()==isb_c.getEndDate().getDayOfYear()) ){
                                isb_d.setMin(isb_c.getMin());
                                isb_d.setMax(isb_c.getMax());
                                isb_d.setMode("custom");
                            }
                        }
                    }

                    writeToHistoricalFile(deltaHist_statColl, arg_grpName, dailyPerformanceDirStr);

                    try {
                      performHistoryAnalysis(deltaHist_statColl, arg_grpName, currStatName);
                    } catch(Exception e){
                        masterLog.warn("WARNING: Unable to do performance analysis for statistic: " + currStatName + "\n");
                        masterLog.warn("\t Error: " + e + " - ");
                        e.printStackTrace();
                    }
                } else{
                    //there is only a cumulative file entry
                   masterLog.warn("There is only a single historical entry for this statistic. " +
                           "History cannot be analyzed.");
                }

            }

            /*
            masterLog.info("\t" + arg_grpName + " - processed data for statistic: " + name);

            String currStatTotalsFile = sortedTotalsFileBase + "All " + arg_grpName + " - Total Results - By Name.csv";
            StatisticsCollection currCumulativeStats;

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
                currCumulativeStats.orderByIsbNameValue();
                currCumulativeStats.writeCollectionTotalsToFile(currStatTotalsFile);
            // disabled for now...   analyzeHistoricalTrends(currStatTotalsFile, arg_grpName, name, "Summarized");
            }

            masterLog.info("");
            masterLog.info("Appended all cumulative summarized entries to historical summarized results file for: " + arg_grpName);
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
            */
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

    private static void performHistoryAnalysis(StatisticsCollection arg_delta_statColl,
                                                String arg_grpName,
                                                String arg_statName){
        boolean verboseArtithmetic = false;

        String stat_unit = arg_delta_statColl.getCollectionObject(0).getUnit();
        int deltaCollectionSize = arg_delta_statColl.getCollectionSize();
        DateTime[] startDates = new DateTime[deltaCollectionSize];
        DateTime[] endDates = new DateTime[deltaCollectionSize];
        Long[] countTracker = new Long[deltaCollectionSize];
        Long[] avgTracker = new Long[deltaCollectionSize];
        Long[] minTracker = new Long[deltaCollectionSize];
        Long[] maxTracker = new Long[deltaCollectionSize];
        Double[] percChange_count = new Double[deltaCollectionSize];
        Double[] percChange_avg = new Double[deltaCollectionSize];
        Double[] percChange_min = new Double[deltaCollectionSize];
        Double[] percChange_max = new Double[deltaCollectionSize];

        for (int i = 0; i < deltaCollectionSize; i++) {
            startDates[i] = arg_delta_statColl.getCollectionObject(i).getStartDate();
            endDates[i] = arg_delta_statColl.getCollectionObject(i).getEndDate();
            countTracker[i] = arg_delta_statColl.getCollectionObject(i).getCount();
            avgTracker[i] = arg_delta_statColl.getCollectionObject(i).getAverage();
            minTracker[i] = arg_delta_statColl.getCollectionObject(i).getMin();
            maxTracker[i] = arg_delta_statColl.getCollectionObject(i).getMax();


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
                    percChange_avg[i] = ((new Double(avgTracker[i]) - new Double(avgTracker[i - 1])) /
                            (new Double(avgTracker[i - 1]))) * 100.00;
                } catch (ArithmeticException ae) {
                    if (verboseArtithmetic)
                        masterLog.warn("    WARNING: could not compute Total Avg percent change for "
                                + arg_grpName + " '" + arg_statName + "' due to arithmetic error: "
                                + ae);
                    percChange_avg[i] = 0.00;
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
            } else {
                percChange_count[i] = 0.00;
                percChange_avg[i] = 0.00;
                percChange_min[i] = 0.00;
                percChange_max[i] = 0.00;
            }
        }

        String histPerfDir = masterDir + "\\Output\\" + arg_grpName + "\\ComparativeHistoricalPerformanceFiles\\";
        File dir = new File(histPerfDir);
        if (!dir.exists()) dir.mkdir();
        String histPerfFile = histPerfDir + arg_statName + ".csv";

        ArrayList<Long[]> masterStatHist = new ArrayList();
        masterStatHist.add(countTracker);
        masterStatHist.add(minTracker);
        masterStatHist.add(maxTracker);
        masterStatHist.add(avgTracker);

        ArrayList<Double[]> masterPercChange = new ArrayList();
        masterPercChange.add(percChange_count);
        masterPercChange.add(percChange_min);
        masterPercChange.add(percChange_max);
        masterPercChange.add(percChange_avg);

        //calculate and store the group's unique statistic group computations.
        Long[] groupComputationValues = {0L, 0L, 0L, 0L};

        //total count
        for(Long l : countTracker) { groupComputationValues[0] += l; }

        //absolute minimum
        MergeSort minSorter = new MergeSort();
        Long[] sortedMinArray = minSorter.sort(minTracker);
        groupComputationValues[1] = sortedMinArray[0];

        //absolute maximum
        MergeSort maxSorter = new MergeSort();
        Long[] sortedMaxArray = maxSorter.sort(maxTracker);
        groupComputationValues[2] = sortedMaxArray[sortedMaxArray.length-1];

        //absolute average
        Long total = 0L;
        for(Long l : avgTracker) {
            total += l;
        } groupComputationValues[3] = (total/(avgTracker.length));

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
        dir = new File(reportOut);
        if (!dir.exists()) dir.mkdir();

        //prep file destinations for report building functionality
        String reportOutHist = reportOut + "\\ComparativeHistoricalPerformanceReports\\";
        dir = new File(reportOutHist);
        if (!dir.exists()) dir.mkdir();

        try {
            ReportBuilder summaryHistPerfReport = new ReportBuilder(
                    arg_grpName + " - Comparative Historical Performance Report - " + arg_statName,
                    "Historical Performance for statistic: " + arg_grpName + " - " + arg_statName,
                    detailHistPerformanceJrxml,
                    histPerfFile,
                    reportOutHist + arg_statName + ".pdf",
                    arg_statName,
                    arg_grpName,
                    groupComputationValues,
                    stat_unit);
            summaryHistPerfReport.generateReport();
            masterLog.info("    Successfully created report: " + arg_grpName + "- Comparative Historical Performance Report - "
                    + arg_statName);

        } catch (Exception e) {
            masterLog.warn("\t WARNING: Error creating historical performance report for: " + arg_statName);
            masterLog.warn("\t " + e);
            e.printStackTrace();
        }

    }

    private static void writeToHistoricalFile(StatisticsCollection arg_sc, String arg_grpName, String arg_targetDir){

        //get all unique statistic names in the collection arg_sc
        HashMap<String,String> uniqueNames = arg_sc.getAllUniqueNameGroupPairs();

        //iterate through each unique name.
        for(String name : uniqueNames.keySet()){
            try{
                StatisticsCollection temp_collection = new StatisticsCollection();
                //if a statistic in the collection arg_sc matches the current name, add it to the temp collection
                for(IntegrityStatisticBean isb : arg_sc.getCollection()){
                    if(isb.getName().equals(name)) temp_collection.addToCollection(isb);
                }

                StringBuilder filePath = new StringBuilder(arg_targetDir);
                filePath.append(cleanString(name));
                filePath.append(".csv");

                temp_collection.writeToFile(filePath.toString(), false);
            } catch(Exception e){
                masterLog.warn("WARNING: Error writing file for statistic: " + name + "\n \t Error: " + e);
            }
        }
    }

    private static void writeToHistoricalPerformanceFile(String arg_destinationFilePath,
                                                        ArrayList<Long[]> arg_statHistEntries,
                                                        ArrayList<Double[]> arg_percChangeEntries,
                                                        Long[] arg_grpComputeEntries,
                                                        DateTime[] arg_startDates,
                                                        DateTime[] arg_endDates){

        File file = new File(arg_destinationFilePath);
        StringBuilder fileContent = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.##");


       fileContent.append("Start Date,End Date,"
               + "Count,delta_Count,group_Count,"
               + "Min,delta_Min,group_Min,"
               + "Max,delta_Max,group_Max, "
               + "Avg,delta_Avg,group_avg" + "\n");

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
            fileContent.append(arg_grpComputeEntries[3] + "," + "\n");

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
