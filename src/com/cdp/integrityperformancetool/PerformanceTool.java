package com.cdp.integrityperformancetool;

import com.cdp.integrityperformancetool.reporting.ReportBuilder;
import com.cdp.integrityperformancetool.util.MergeSort;
import com.cdp.integrityperformancetool.util.StatisticsFileReader;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

/**
 * The main executable class of the Integrity Performance Tool project. This class's main procedure can be modified as
 * required to process statistics and create files, reports, etc. All methods provided can be
 * customized to reporting and analysis needs.
 *
 * The "..\Properties\config.properties" files is referenced for a number of key directories. This file
 * should be reviewed for accuracy prior to executing the PerformanceTool class.
 */
public class PerformanceTool {

    private static final DateTime runTime = new DateTime();

    //masterLog logging outputs to .\Logging\PerformanceTool.log
    private static final Logger masterLog = Logger.getLogger("PerformanceTool");
    private static DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MMM-dd-yyyy HH:mm:ss");

    //masterDir stores the current working directory of the Integrity Performance Tool project
    private static final Path currentRelativePath = Paths.get("");
    private static final String masterDir = currentRelativePath.toAbsolutePath().toString();

    //StatisticsCollection used to create the group summary reports
    private static StatisticsCollection statTotalComputationsColl = new StatisticsCollection();

    //report building variables holding file paths to the .jrxml files used to create reports.
    private static final String historicalPerformanceJrxml = masterDir +"\\ReportBuildingData\\ipt_historicalPerformanceReport.jrxml";
    private static final String summarizedPerformanceJrxml = masterDir +"\\ReportBuildingData\\ipt_summarizedPerformanceReport.jrxml";

    /**
     * Main method of PerformanceTool class. Executes the procedure to process statistics anc create reports
     * @param args - Expects no arguments. Arguments will not be processed, even if they are provided.
     */
    public static void main(String args[]){

        //gather application property values
        Properties appProps = new Properties();
        try{
            appProps.load(new FileInputStream(masterDir + "\\Properties\\config.properties"));
        } catch (FileNotFoundException fnfe){
            System.out.println("ERROR: Properties File cannot be located." +
                    " Ensure that the directory 'Properties\\config.propreties' exists before executing again." +
                    " Exiting application...");
            System.out.println(fnfe);
            return;
        } catch(IOException ioe){
            System.out.println("ERROR: Issue gathering config.properties file: " + ioe );
            System.out.println("Exiting application...");
            return;
        }
        String log4jProp = appProps.getProperty("log4jPropFilePath");
        String[] statGroups = appProps.getProperty("statGroupsInScope").split(",");
        String rawDataFile = appProps.getProperty("inputDataFilePath");

        if(rawDataFile==null || rawDataFile.length()<1){
            masterLog.warn("ERROR: No input data file provided. Please populate the 'inputDataFilePath'"
                    + " property of .\\Properties\\config.properties with the file path to the input data.");
            masterLog.warn("Exiting application...");
            return;
        }

        //initialize logging
        PropertyConfigurator.configure(masterDir + log4jProp);

        masterLog.info("");
        masterLog.info(" ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **");
        masterLog.info(" BEGIN EXECUTION OF INTEGRITY PERFORMANCE TOOL AT: " + fmt.print(runTime));
        masterLog.info(" ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **");
        masterLog.info("");

        StatisticsLibrary masterDataLib = new StatisticsLibrary();


        //////////////////////////////////////////////
        //// Get all statistics from the input data file
        /////////////////////////////////////////////
        String masterDataFilePath = masterDir + rawDataFile;
        masterLog.info("Retrieving data from Source file: " + masterDataFilePath);
        masterDataLib = getStatsLibraryFromFile(masterDataFilePath, ",", 1, statGroups);
        masterLog.info("Successfully retrieved data.");
        masterLog.info("");


        //////////////////////////////////////////////
        //// Send statistics group(s) for processing
        //////////////////////////////////////////////
        for(String group : statGroups) {
            processStatisticsGroup(group, masterDataLib);
            statTotalComputationsColl.getCollection().clear();
        }

        DateTime endTime = new DateTime();
        masterLog.info("");
        masterLog.info(" ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **");
        masterLog.info(" END EXECUTION OF INTEGRITY PERFORMANCE TOOL AT: " + fmt.print(endTime));
        masterLog.info(" Total Run Time = " + (endTime.getMillis() - runTime.getMillis())/1000L + " seconds");
        masterLog.info(" ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **");
        masterLog.info("");
    }

    /**
     * Removes characters which are invalid for file names, and replaces them with valid characters.
     * The following characters are replaced (space-separated): , \ . / ( ) < > = . ? : * | %
     * @param arg_s - (String) the String object to replace invalid characters from.
     * @return The String provided, with all invalid file name characters removed from the String
     */
    private static String cleanString(String arg_s){
        //removes chars that are illegal for filenames
        //used to remove illegal chars from trigger/query/report/etc names used in file names
        //so that the names can be saved
        return arg_s.replace(":", "-")
                .replace(",", "-")
                .replace("\"", "-")
                .replace(".", "-")
                .replace("/", "-")
                .replace("(", "-")
                .replace(")", "-")
                .replace(">", "GT")
                .replace("<", "LT")
                .replace("=", "EQ")
                .replace(".", "-")
                .replace("?", "-")
                .replace("*", "-")
                .replace("|", "-");
    }

    /**
     * Computes and returns the percent change for a single statistic between an original and new data point.
     * @param arg_origVal - (Long) The original value.
     * @param arg_newVal - (Long) The new value.
     * @param verbose - (boolean) Boolean value, which enables arithmetic error logging when true.
     * @param arg_grpName - (String) The Statistics Group name which the statistic being analyzed is a part of. Used for
     *                    optional error logging.
     * @param arg_statName - (String) The name of the Statistic being analyzed. User for optional error message.
     * @return Returns a Double object representing the Percent Change between the original and new values.
     */
    private static Double computePercentChange(Long arg_origVal, Long arg_newVal, boolean verbose,
                                               String arg_grpName, String arg_statName){
        try {
            return Math.abs( (new Double(arg_newVal) - new Double(arg_origVal)) /
                    (new Double(arg_origVal)) ) * 100.00;
        } catch (ArithmeticException ae) {
            if (verbose)
                masterLog.warn("    WARNING: could not compute Count percent change for "
                        + arg_grpName + " '" + arg_statName + "' due to arithmetic error: "
                        + ae);
            return 0.00;
        }

    }

    /**
     * Creates a single PDF document merging all reports for the Statistics Group. NOTE: The reports must exist prior
     * to using this method.
     * @param arg_grpName - (String) The Statistics Group to be processed, which exists in the StatisticsLibrary.
     *                    Examples: "Triggers", "Queries".
     * @param arg_bookName - (String) The file name to give to the output PDF Document.
     */
    private static void createReportPdfBook(String arg_grpName, String arg_bookName){

        String reportDirStr = masterDir + "\\Output\\"+cleanString(arg_grpName)+"\\Reports\\ComparativeHistoricalPerformanceReports\\";
        File reportDir = new File(reportDirStr);
        ArrayList<String> pdfFileNames = new ArrayList<String>(Arrays.asList(reportDir.list()));
        PDFMergerUtility pdfUtil = new PDFMergerUtility();

        for(String fileName : pdfFileNames) { pdfUtil.addSource(reportDirStr + fileName); }

        try{
            String masterReportBookStr = masterDir + "\\Output\\"+cleanString(arg_grpName)+"\\Reports\\"+arg_bookName;
            pdfUtil.setDestinationFileName(masterReportBookStr);
            pdfUtil.mergeDocuments();
            masterLog.info("");
            masterLog.info("Successfully created report book: " + arg_bookName);
            masterLog.info(masterReportBookStr);
            masterLog.info("");
        } catch(Exception e){
            masterLog.warn("ERROR: Problem writing all reports to final PDF: " + e);
        }
    }

    /**
     * Retrieves raw data from a .csv file of Integrity Statistics and creates a StatisticsLibrary object, where statistics
     * data is sorted into StatisticsCollections based on "Group" column value in the raw data.
     * @param arg_fileName - (String) the .csv file which contains raw Integrity Statistics data.
     * @param arg_valSeparator - (String) the String value which represents the delimeter in the .csv file.
     * @param arg_skipLines -(int) the number of lines to skip when reading the raw data .csv file. A value
     *                      of 1 will skip the column headers appearing in the first row of a standard Integrity
     *                      Statistics raw data dump.
     * @return A StatisticsLibrary object containing a StatisticsCollection object for every unique "Group" value in
     * the arg_fileName .csv file provided. Each StatisticsCollection object then contains all statistics data for that
     * Statistics Group, and can be used for further statistics analysis.
     */
    private static StatisticsLibrary getStatsLibraryFromFile(String arg_fileName, String arg_valSeparator,
                                                             int arg_skipLines, String[] arg_statGrpsInScope){

        StatisticsFileReader temp_fileReader = new StatisticsFileReader(arg_fileName,
                arg_valSeparator,
                arg_skipLines,
                arg_statGrpsInScope);
        return temp_fileReader.executeStatisticsRetrieval();

    }

    /**
     * Processes statistics and compares the performance of each row of statistic data to previous
     * performance. Creates reports and .csv files of statistic performance.
     * @param arg_statColl - (StatisticsCollecton) The Statistics Collection representing the delta statistics
     *                           for the statistic.
     * @param arg_grpName - (String) The Statistics Group name of the statistic.
     * @param arg_statName - (String) The name of the statistic being analyzed.
     */
    private static void performHistoryAnalysis(StatisticsCollection arg_statColl,
                                               String arg_grpName,
                                               String arg_statName){
        boolean verboseArtithmetic = true;

        String stat_unit = arg_statColl.getCollectionObject(0).getUnit();
        int deltaCollectionSize = arg_statColl.getCollectionSize();
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
            startDates[i] = arg_statColl.getCollectionObject(i).getStartDate();
            endDates[i] = arg_statColl.getCollectionObject(i).getEndDate();
            countTracker[i] = arg_statColl.getCollectionObject(i).getCount();
            avgTracker[i] = arg_statColl.getCollectionObject(i).getAverage();
            minTracker[i] = arg_statColl.getCollectionObject(i).getMin();
            maxTracker[i] = arg_statColl.getCollectionObject(i).getMax();

            if (i >= 1) {
                percChange_count[i] = computePercentChange(countTracker[i], countTracker[i-1],verboseArtithmetic,
                        arg_grpName, arg_statName);
                percChange_avg[i] = computePercentChange(avgTracker[i], avgTracker[i-1],verboseArtithmetic,
                        arg_grpName, arg_statName);
                percChange_min[i] = computePercentChange(minTracker[i], minTracker[i-1],verboseArtithmetic,
                        arg_grpName, arg_statName);
                percChange_max[i] = computePercentChange(maxTracker[i], maxTracker[i - 1],verboseArtithmetic,
                        arg_grpName, arg_statName);
            } else {
                percChange_count[i] = 0.00;
                percChange_avg[i] = 0.00;
                percChange_min[i] = 0.00;
                percChange_max[i] = 0.00;
            }
        }

        String histPerfDir = masterDir + "\\Output\\" + cleanString(arg_grpName) + "\\ComparativeHistoricalPerformanceFiles\\";
        File dir = new File(histPerfDir);
        if (!dir.exists()) dir.mkdir();
        String histPerfFile = histPerfDir + cleanString(arg_statName) + ".csv";

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
        //group total count
        for(Long l : countTracker) { groupComputationValues[0] += l; }

        //group minimum
        MergeSort minSorter = new MergeSort();
        Long[] sortedMinArray = new Long[minTracker.length];
        for(int i=0;i<minTracker.length;i++) { sortedMinArray[i] = minTracker[i]; }
        minSorter.sort(sortedMinArray);
        groupComputationValues[1] = sortedMinArray[0];

        //group maximum
        MergeSort maxSorter = new MergeSort();
        Long[] sortedMaxArray = new Long[maxTracker.length];
        for(int i=0;i<maxTracker.length;i++) { sortedMaxArray[i] = maxTracker[i]; }
        minSorter.sort(sortedMaxArray);
        groupComputationValues[2] = sortedMaxArray[sortedMaxArray.length-1];

        //group average
        Long total = 0L;
        for(Long l : avgTracker) {
            total += l;
        } groupComputationValues[3] = (total/(avgTracker.length));

        //add group computation summary to statTotalComputationsColl
        IntegrityStatistic total_isb = new IntegrityStatistic();
        total_isb.setStartDate(arg_statColl.getCollectionEarliestStartDate());
        total_isb.setEndDate(arg_statColl.getCollectionLatestEndDate());
        total_isb.setGroup(arg_grpName);
        total_isb.setName(arg_statName);
        total_isb.setKind(arg_statColl.getCollectionObject(0).getKind());
        total_isb.setUnit(arg_statColl.getCollectionObject(0).getUnit());
        total_isb.setCount(groupComputationValues[0]);
        total_isb.setMin(groupComputationValues[1]);
        total_isb.setMax(groupComputationValues[2]);
        total_isb.setAverage(groupComputationValues[3]);
        statTotalComputationsColl.addToCollection(total_isb);

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

        String reportOut = masterDir + "\\Output\\" + cleanString(arg_grpName) + "\\Reports\\";
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
                    historicalPerformanceJrxml,
                    histPerfFile,
                    reportOutHist + arg_statName + ".pdf",
                    arg_statName,
                    arg_grpName,
                    groupComputationValues,
                    stat_unit);
            summaryHistPerfReport.generateReport();
            masterLog.info("    Successfully created report: " + arg_grpName + "- Comparative Historical Performance Report - "
                    + arg_statName + ".pdf");

        } catch (Exception e) {
            masterLog.warn("\t WARNING: Error creating historical performance report for: " + arg_statName);
            masterLog.warn("\t " + e);
            // e.printStackTrace();
        }
        masterLog.info("");

    }

    /**
     * Takes in a StatisicsLibrary object and Statistics group name, processes the Statistics which are
     * of the group name provided. Outputs .csv files and .pdf reports of statistics data for the
     * group.
     * @param arg_grpName - (String) The Statistics Group to be processed, which exists in the StatisticsLibrary.
     *                    Examples: "Triggers", "Queries".
     * @param arg_library - (StatisticsLibrary) The StatisticsLibrary object which contains the raw data to be processed
     */
    private static void processStatisticsGroup(String arg_grpName, StatisticsLibrary arg_library){

        masterLog.info(" -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --");
        masterLog.info(" About to process statistics group: " + arg_grpName);
        masterLog.info(" -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --");

        String outputDirStr = masterDir + "\\Output\\";
        File outputDir = new File(outputDirStr);
        if(!outputDir.exists()) outputDir.mkdir();

        String grpOutputDirStr = masterDir + "\\Output\\"+cleanString(arg_grpName)+"\\";
        File grpOutputDir = new File(grpOutputDirStr);
        if(!grpOutputDir.exists()) grpOutputDir.mkdir();

        //prep file destinations for report building functionality
        String reportOutFileBase = masterDir + "\\Output\\"+cleanString(arg_grpName)+"\\Reports\\";
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
            for(IntegrityStatistic isb : statGrp_allStats.getCollection()){
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

            String cumulativeDirStr = masterDir + "\\Output\\"+cleanString(arg_grpName)+"\\RawCumulativeHistoryFiles\\";
            File cumulativeDir = new File(cumulativeDirStr);
            if(!cumulativeDir.exists()) cumulativeDir.mkdir();

            String deltaDirStr = masterDir + "\\Output\\"+cleanString(arg_grpName)+"\\RawDeltaHistoryFiles\\";
            File deltaDir = new File(deltaDirStr);
            if(!deltaDir.exists()) deltaDir.mkdir();

            String cumulativeCollapsedDirStr = masterDir + "\\Output\\"+cleanString(arg_grpName)+"\\CollapsedCumulativeHistoryFiles\\";
            File cumulativeCollapsedDir = new File(cumulativeCollapsedDirStr);
            if(!cumulativeCollapsedDir.exists()) cumulativeCollapsedDir.mkdir();

            String deltaCollapsedDirStr = masterDir + "\\Output\\"+cleanString(arg_grpName)+"\\CollapsedDeltaHistoryFiles\\";
            File deltaCollapsedDir = new File(deltaCollapsedDirStr);
            if(!deltaCollapsedDir.exists()) deltaCollapsedDir.mkdir();

            String dailyPerformanceDirStr = masterDir + "\\Output\\"+cleanString(arg_grpName)+"\\DailyPerformanceHistoryFiles\\";
            File dailyCumulativeDir = new File(dailyPerformanceDirStr);
            if(!dailyCumulativeDir.exists()) dailyCumulativeDir.mkdir();

            /////////////////
            //For each uniquely named statistic in the cumulative and delta StatisticsCollection objects,
            // write a separate file containing all cumulative or delta entries for that specific statistics
            // that are contained within the StatisticsCollection.
            ///////////////////
            writeToHistoricalFile(statGrp_cumulative, cumulativeDirStr);
            writeToHistoricalFile(statGrp_delta, deltaDirStr);

            ///////////////////
            ///get a list of all .csv files created for both delta and cumulative data
            ///////////////////
            ArrayList<String> cumulativeFileNamesList = new ArrayList<String>(Arrays.asList(cumulativeDir.list()));
            ArrayList<String> deltaFileNamesList = new ArrayList<String>(Arrays.asList(deltaDir.list()));


            //iterate through the uniquely named statistic cumulative files
            for(int i=0;i<cumulativeFileNamesList.size();i++){
                //for(int i=0;i<3;i++){
                String currFileName = cumulativeFileNamesList.get(i);
                String[] currStatNameArray = currFileName.split("\\.");
                String currStatName = currStatNameArray[0];
                masterLog.info("(" + (i+1) + " of " + cumulativeFileNamesList.size() + ")"
                        + " Working on group '" + arg_grpName + "' statistic: '" + currStatName + "' ");

                //Performance analysis of each uniquely named statistic can only be done if there is both a
                // delta and cumulative file to reference.
                if(deltaFileNamesList.contains(cumulativeFileNamesList.get(i))){

                    StatisticsFileReader cumulativeHistory = new StatisticsFileReader(cumulativeDirStr + currFileName);
                    StatisticsLibrary cumulativeHist_lib = cumulativeHistory.executeStatisticsRetrieval();
                    StatisticsCollection cumulativeHist_statColl = cumulativeHist_lib.getStatisticsGroupName(arg_grpName);

                    StatisticsFileReader deltaHistory = new StatisticsFileReader(deltaDirStr + currFileName);
                    StatisticsLibrary deltaHist_lib = deltaHistory.executeStatisticsRetrieval();
                    StatisticsCollection deltaHist_statColl = deltaHist_lib.getStatisticsGroupName(arg_grpName);

                    /////////////////////////////////////////
                    //The raw data sometimes contains multiple statistics entries for the same date-time periods.
                    //the collapseStatisticsByDate() method combines all statistics of the same name, Start Date, and
                    //End Date into a single statistic.
                    ////////////////////////////////////
                    cumulativeHist_statColl.collapseStatisticsByDate();
                    deltaHist_statColl.collapseStatisticsByDate();

                    //write the collapsed data to file.
                    cumulativeHist_statColl.writeToFile(cumulativeCollapsedDirStr+currFileName, false);
                    deltaHist_statColl.writeToFile(deltaCollapsedDirStr+currFileName, false);


                    //create a new StatisticsCollection to hold the combined cumulative/delta data as a single
                    //IntegrityStatistic object.
                    StatisticsCollection dailyPerformanceCollection = new StatisticsCollection();
                    for(IntegrityStatistic is_c : cumulativeHist_statColl.getCollection()){
                        for(IntegrityStatistic is_d : deltaHist_statColl.getCollection()){
                            if( (is_c.getStartDate().getDayOfYear()==is_d.getStartDate().getDayOfYear()) &&
                                    (is_c.getEndDate().getDayOfYear()==is_d.getEndDate().getDayOfYear()) ){
                                is_d.setMin(is_c.getMin());
                                is_d.setMax(is_c.getMax());
                                is_d.setMode("custom");
                                dailyPerformanceCollection.addToCollection(is_d);
                            }
                        }
                    }

                    //write the combined data to file
                    writeToHistoricalFile(dailyPerformanceCollection, dailyPerformanceDirStr);


                    //if there is data to work with, do analysis on daily performance and change in performance.
                    try {
                        if (dailyPerformanceCollection.getCollectionSize()>0){
                            performHistoryAnalysis(dailyPerformanceCollection, arg_grpName, currStatName);
                        } else {
                            //there is only a cumulative file entry
                            masterLog.warn("There is only a single historical entry for this statistic. " +
                                    "History cannot be analyzed.");
                        }

                    } catch(Exception e){
                        masterLog.warn("WARNING: Unable to do performance analysis for statistic: " + currStatName + "\n");
                        masterLog.warn("\t Error: " + e + " - ");
                        //e.printStackTrace();
                    }
                } else{
                    //there is only a cumulative file entry
                    masterLog.warn("There is only a single historical entry for this statistic. " +
                            "History cannot be analyzed.");
                }
            }
        } catch(Exception e) {
            masterLog.info("WARNING: ISSUE PROCESSING GROUP : " + arg_grpName + " - " + e);
        }

        //prepare data for high level report output
        String totalsReportFileStr = reportOutFileBase + cleanString(arg_grpName) + " - High Level Historical Review.csv";
        statTotalComputationsColl.orderByIsbNameValue();
        statTotalComputationsColl.writeToFile(totalsReportFileStr, true);

        try {
            ReportBuilder highLevelReview = new ReportBuilder(
                    arg_grpName + " - High Level Historical Review",
                    "Historical Performance for statistic: " + arg_grpName,
                    summarizedPerformanceJrxml,
                    totalsReportFileStr,
                    reportOutFileBase + cleanString(arg_grpName) + " - High Level Historical Review.pdf",
                    statTotalComputationsColl.getCollectionObject(0).getUnit(),
                    statTotalComputationsColl.getCollectionObject(0).getGroup()
            );
            highLevelReview.generateReport();
            masterLog.info("    Successfully created report: " + arg_grpName + "- High Level Historical Review.pdf");

        } catch (Exception e) {
            masterLog.warn("\t WARNING: Error creating high level historical review for: " + arg_grpName);
            masterLog.warn("\t " + e);
            //e.printStackTrace();
        }

        masterLog.info(" -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- ");
        masterLog.info(" Done processing statistics group: " + arg_grpName);
        masterLog.info(" -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- ");
    }

    /**
     * Writes the data of each uniquely named statistic found in a StatisticsCollection object to a .csv file. The
     * output .csv file contains all entries of each uniquely named statisitc within the StatisticsCollection object.
     * Output .csv File formatting is as follows :
     * Start Date (Earliest Collection Start Date), End Date (Latest Collection End Date), Group (group value of 1st collection
     * item), Statistic (name value of 1st collection item), Kind (kind value of 1st collection item, Unit (unit value of 1st
     * collection item), Count (collection count), Total (collection total count), Sum (collection sum), Used (used value of
     * 1st collection item), Min (collection min), Max (collection max), Average (collection average), Mode (mode value of
     * 1st collection item).
     * @param arg_sc - (StatisticsCollection) The StatisticsCollection object containing data to write to a file.
     * @param arg_targetDir - (String) The destination directory for the file once created.
     */
    private static void writeToHistoricalFile(StatisticsCollection arg_sc, String arg_targetDir){

        //get all unique statistic names in the collection arg_sc
        HashMap<String,String> uniqueNames = arg_sc.getAllUniqueNameGroupPairs();

        //iterate through each unique name.
        for(String name : uniqueNames.keySet()){
            try{
                StatisticsCollection temp_collection = new StatisticsCollection();
                //if a statistic in the collection arg_sc matches the current name, add it to the temp collection
                for(IntegrityStatistic isb : arg_sc.getCollection()){
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

    /**
     * Writes the historical performance compoarison data to a .csv file. The formatting of the .csv file created
     * has the following columns [Column Name (description)]:
     * <ol>
     *     <li>Start Date (Start Date of performance analysis)</li>
     *     <li>End date (End Date of performance analysis)</li>
     *     <li>Count (Number of Executions within time period)</li>
     *     <li>delta_Count (Difference in Count between this time period and the previous)</li>
     *     <li>group_Count (Total number of executions for all time periods)</li>
     *     <li>Min (Minimum execution time for time period)</li>
     *     <li>delta_Min (Change in Minimum value between this time period and the previous)</li>
     *     <li>group_Min (Absolute Minimum for all time periods)</li>
     *     <li>Max (Maximum execution time for time period)</li>
     *     <li>delta_Max (Change in Maximum value between this time period and the previous)</li>
     *     <li>group_Max (Absolute Maximum for all time periods)</li>
     *     <li>Avg (The average execution time for all executions in the time period)</li>
     *     <li>delta_Avg (The change in Average value between this time period and the previous)</li>
     *     <li>group_Avg (The Average for all time periods)</li>
     * </ol>
     * @param arg_destinationFilePath - (String) The file location to save the .csv file to.
     * @param arg_statHistEntries - (ArrayList<Long[]>) ArrayList containing historical data entries computed by
     *                            the performHistoryAnalysis() method.
     * @param arg_percChangeEntries - (ArrayList<Long[]>) ArrayList containing percent change entries computed by
     *                            the performHistoryAnalysis() method.
     * @param arg_grpComputeEntries - (Long[]) - Array of group computation values for the historical data.
     * @param arg_startDates - (DateTime[]) Array of DateTime Start Date values for the historical data.
     * @param arg_endDates - (DateTime[]) Array of DateTime End Date values for the historical data.
     */
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
                + "Max,delta_Max,group_Max,"
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
        }

    }

}
