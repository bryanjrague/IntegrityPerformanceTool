package com.cdp.integrityperformancetool;

import com.cdp.integrityperformancetool.reporting.ReportBuilder;
import com.cdp.integrityperformancetool.util.StatisticsFileReader;

import java.io.File;
import java.util.HashMap;

/**
 * Created by bryan on 6/19/2015.
 */
public class PerformanceTool {

    public static void main(String args[]){


        //////////////////////////////////////////////
        //// Get all statistics from the master data file
        /////////////////////////////////////////////
        // StatisticsFileReader testData02 = new StatisticsFileReader();
        //testData02.setFilePath("C:\\Users\\bryan\\IdeaProjects\\Integrity Performance Tool\\Input\\TestData_02.csv");
        // testData02.setFilePath("C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool\\Input\\TestData_02.csv");
        // testData02.setValueSeparator(",");
        // testData02.setSkipLines(1);

        // StatisticsLibrary masterDataLib = testData02.executeStatisticsRetrieval();

        StatisticsFileReader prodData = new StatisticsFileReader();

        prodData.setFilePath("C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool\\Input\\Statistics_PROD_10-1-2015_10-14-2015.csv");
        prodData.setValueSeparator(",");
        prodData.setSkipLines(1);

        StatisticsLibrary masterDataLib = prodData.executeStatisticsRetrieval();

        ///////////////////////////////////////////////
        /// Get only the Trigger data from the master lib
        ///////////////////////////////////////////////
        StatisticsCollection triggers_raw = masterDataLib.getStatisticsGroupName("Triggers");

        ///////////////////////////////////////////////
        /// Get only the cumulative triggers from the Collection, create new collection to store them
        ///////////////////////////////////////////////
        StatisticsCollection triggers_cumulative = new StatisticsCollection("All Triggers - Cumulative only");
        for(IntegrityStatisticBean isb : triggers_raw.getCollection()){
            if(isb.getMode().equals("cumulative")){
                triggers_cumulative.addToCollection(isb);
            }
        }

        ////////////////////////////////////////////////
        /// For each unique trigger in the cumulative only collection, create and store a file of all stat entries
        ///////////////////////////////////////////////

        //get all unique names for cumulative trigger stats
        HashMap<String,String> cumulativeUniqueNames = triggers_cumulative.getAllUniqueNameGroupPairs();
        int counter = 0;
        for(String name : cumulativeUniqueNames.keySet()){
            StatisticsCollection temp_collection = new StatisticsCollection("Trigger: " + name + " Cumulative Stats");
            for(IntegrityStatisticBean isb : triggers_cumulative.getCollection()){
                if(isb.getName().equals(name)) temp_collection.addToCollection(isb);
            }
            //StringBuilder filePath = new StringBuilder("C:\\Users\\bryan\\IdeaProjects\\Integrity Performance Tool\\Output\\");
            StringBuilder filePath = new StringBuilder("C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool\\Output\\");
            filePath.append("Triggers\\");
            filePath.append("Cumulative Entries\\");
            File temp_dir = new File(filePath.toString());
            if (!temp_dir.exists()) temp_dir.mkdirs();
            filePath.append(cleanString(name));
            filePath.append(".csv");
            counter++;
            temp_collection.writeToFile(filePath.toString());
        }



        //////////////////////////////////////////////////
        /// Gather all individual cumulative trigger totals and store in single file:
        //////////////////////////////////////////////////
        StringBuilder currStatTotalsFile = new StringBuilder("C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool\\Output\\");
        currStatTotalsFile.append("Triggers\\");
        currStatTotalsFile.append("All Triggers - Total Results - No Sort.csv");
        StatisticsCollection currCumulativeStats = new StatisticsCollection();

        for(String name : cumulativeUniqueNames.keySet()){
            StringBuilder currFilePath = new StringBuilder("C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool\\Output\\");
            currFilePath.append("Triggers\\");
            currFilePath.append("Cumulative Entries\\");
            currFilePath.append(cleanString(name) + ".csv");

            StatisticsFileReader currCumulativeStatsFile = new StatisticsFileReader(currFilePath.toString());
            StatisticsLibrary currCumulativeLibrary = currCumulativeStatsFile.executeStatisticsRetrieval();
            currCumulativeStats = currCumulativeLibrary.getStatisticsGroupName("Triggers");
            currCumulativeStats.collapseAllStatistics();
            currCumulativeStats.computeAllCollectionStatistics();
            currCumulativeStats.writeCollectionTotalsToFile(currStatTotalsFile.toString());
        }

        ///////////////////////////////////////////////////
        //// Gather unsorted total results and sort. Save as sorted files
        ///////////////////////////////////////////////////
        String sortedTotalsFileBase = "C:\\Users\\USX25908\\IdeaWorkspace\\IntegrityPerformanceTool\\Output\\Triggers\\";

        StatisticsFileReader unsortedTotalsStatsFile = new StatisticsFileReader(currStatTotalsFile.toString());
        StatisticsLibrary unsortedTotalsLibrary = unsortedTotalsStatsFile.executeStatisticsRetrieval();
        StatisticsCollection triggersTotalStatistics = unsortedTotalsLibrary.getStatisticsGroupName("Triggers");

        triggersTotalStatistics.collapseAllStatistics();

        //sort by average value and save to file
        String avgValFile = sortedTotalsFileBase + "All Triggers - Total Results - By Average Value";
        triggersTotalStatistics.orderByIsbAverageValue();
        triggersTotalStatistics.writeToFile(avgValFile);

        //sort by maximum value and save to file
    //    String maxValFile = sortedTotalsFileBase+ "All Triggers - Total Results - By Maximum Value";
    //    triggersTotalStatistics.orderByIsbMaximumValue();
    //    triggersTotalStatistics.writeToFile(maxValFile);

        //sort by count value and save to file
    //    String cntValFile = sortedTotalsFileBase + "All Triggers - Total Results - By Total Count Value";
    //    triggersTotalStatistics.orderByIsbTotalCountValue();
    //    triggersTotalStatistics.writeToFile(cntValFile);

        StatisticsFileReader sortedAvgValsFile = new StatisticsFileReader(avgValFile);
        StatisticsLibrary sortedAvgValLib = sortedAvgValsFile.executeStatisticsRetrieval();
        StatisticsCollection sortedAvgValStats = sortedAvgValLib.getStatisticsGroupName("Triggers");

        StatisticsCollection topTenPercentLongestTriggers = new StatisticsCollection();

        double dbl_topTenPercent = (sortedAvgValStats.getCollectionSize()-1) * .10;
        int int_topTenPercent = (int) dbl_topTenPercent;
        int index = sortedAvgValStats.getCollectionSize()-1;
        do{
            if(!sortedAvgValStats.getCollectionObject(index).getName().contains("Scheduled")){
                topTenPercentLongestTriggers.addToCollection(sortedAvgValStats.getCollectionObject(index));
            }
            index--;
        } while(index>(sortedAvgValStats.getCollectionSize()-1)-int_topTenPercent);
        String topTenFile = sortedTotalsFileBase + "Top Ten Longest Average Value Triggers.csv";
        topTenPercentLongestTriggers.writeToFile(topTenFile);
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

}
