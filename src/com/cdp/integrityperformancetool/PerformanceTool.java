package com.cdp.integrityperformancetool;

import com.cdp.integrityperformancetool.reporting.ReportBuilder;
import com.cdp.integrityperformancetool.util.StatisticsFileReader;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by bryan on 6/19/2015.
 */
public class PerformanceTool {

    private static DateTime runTime = new DateTime();
    private static String runTimeStr = runTime.toString().replace(":","_").replace(".","_");
    private static String masterDir = "C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool";

    public static void main(String args[]){

        //////////////////////////////////////////////
        //// Get all statistics from the master data file
        /////////////////////////////////////////////


        String masterFilePath = masterDir + "\\Input\\Statistics_PROD_10-1-2015_10-14-2015.csv";
        StatisticsLibrary masterDataLib = getStatsLibraryFromFile(masterFilePath, ",",1);

        processStatisticsGroup("Triggers", masterDataLib);

        String reportOutFileBase = masterDir + "\\Output\\Reports\\";
        File dir = new File(reportOutFileBase);
        if(!dir.exists()) dir.mkdir();

       ReportBuilder test = new ReportBuilder("Long Execution Triggers",
                "Top 10% of triggers with longest average execution time, excluding scheduled triggers.",
                "C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool\\ReportBuildingData\\ipt_topTenPercent_avg.jrxml",
                "C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool\\Output\\Triggers\\TopTenPercent\\Triggers - Top Ten Percent by Greatest Avg Val.csv",
                reportOutFileBase+"test.pdf");
        test.generateReport();
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

    private static void processStatisticsGroup(String arg_grpName, StatisticsLibrary arg_library){

        ///////////////////////////////////////////////
        /// Get only the specific Statistic Group data from the master lib
        ///////////////////////////////////////////////
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
            temp_collection.writeToFile(filePath.toString());
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

        String currStatTotalsFile = sortedTotalsFileBase + "All " + arg_grpName + " - Total Results - No Sort.csv";
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


        ///////////////////////////////////////////////////
        //// Gather unsorted total statistics and sort. run various sort functions. save output as files.
        ///////////////////////////////////////////////////
        StatisticsFileReader unsortedTotalsStatsFile = new StatisticsFileReader(currStatTotalsFile);
        StatisticsLibrary unsortedTotalsLibrary = unsortedTotalsStatsFile.executeStatisticsRetrieval();
        StatisticsCollection unsortedStatistics = unsortedTotalsLibrary.getStatisticsGroupName(arg_grpName);

       // unsortedStatistics.collapseAllStatistics();

        //   //sort by average value and save to file
        String avgValFile = sortedTotalsFileBase + "All " + arg_grpName + " - Total Results - By Average Value.csv";
        unsortedStatistics.orderByIsbAverageValue();
        unsortedStatistics.writeToFile(avgValFile);
        //create file holding the top ten percent greatest avg val triggers
        String topTenAvgValFile = topTenFileBase + arg_grpName + " - Top Ten Percent by Greatest Avg Val.csv";
        computeTopTen(avgValFile, topTenAvgValFile, arg_grpName);

        //sort by maximum value and save to file
        String maxValFile = sortedTotalsFileBase+ "All " + arg_grpName + " - Total Results - By Maximum Value.csv";
        unsortedStatistics.orderByIsbMaximumValue();
        unsortedStatistics.writeToFile(maxValFile);
        //create file holding the top ten percent greatest avg val triggers
        String topTenMaxValFile = topTenFileBase + arg_grpName + " - Top Ten Percent by Greatest Max Val.csv";
        computeTopTen(maxValFile, topTenMaxValFile, arg_grpName);

        //sort by count value and save to file
        String cntValFile = sortedTotalsFileBase + "All " + arg_grpName + " - Total Results - By  Count Value.csv";
        unsortedStatistics.orderByIsbCountValue();
        unsortedStatistics.writeToFile(cntValFile);
        //create file holding the top ten percent greatest avg val triggers
        String topTenCntValFile = topTenFileBase + arg_grpName + " - Top Ten Percent by Greatest Count Val.csv";
        computeTopTen(cntValFile, topTenCntValFile, arg_grpName);

        //sort by total count value and save to file
        String totCntValFile = sortedTotalsFileBase + "All " + arg_grpName + " - Total Results - By Total Count Value.csv";
        unsortedStatistics.orderByIsbTotalCountValue();
        unsortedStatistics.writeToFile(totCntValFile);
        //create file holding the top ten percent greatest avg val triggers
        String topTenTotCntValFile = topTenFileBase + arg_grpName + " - Top Ten Percent by Greatest Total Count Val.csv";
        computeTopTen(totCntValFile, topTenTotCntValFile, arg_grpName);
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
                if (!sortedStats.getCollectionObject(index).getName().contains("Scheduled")) {
                    topTenPercent.addToCollection(sortedStats.getCollectionObject(index));
                } else{
                    --endIndex;
                }
            } else {
                topTenPercent.addToCollection(sortedStats.getCollectionObject(index));
            }
            --index;
        }
        topTenPercent.writeToFile(arg_destFile);

    }

}
