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

        StatisticsFileReader testData02 = new StatisticsFileReader();
        testData02.setFilePath("C:\\Users\\bryan\\IdeaProjects\\Integrity Performance Tool\\Input\\TestData_02.csv");
        testData02.setValueSeparator(",");
        testData02.setSkipLines(1);

        StatisticsLibrary masterDataLib = testData02.executeStatisticsRetrieval();
        StatisticsCollection triggers_raw = masterDataLib.getStatisticsGroupName("Triggers");

        StatisticsCollection triggers_cumulative = new StatisticsCollection("All Triggers - Cumulative only");
        for(IntegrityStatisticBean isb : triggers_raw.getCollection()){
            if(isb.getMode().equals("cumulative")){
                triggers_cumulative.addToCollection(isb);
            }
        }
        //get all unique names for cumulative trigger stats
        HashMap<String,String> cumulativeUniqueNames = triggers_cumulative.getAllUniqueNameGroupPairs();
        for(String name : cumulativeUniqueNames.keySet()){
            StatisticsCollection temp_collection = new StatisticsCollection("Trigger: " + name + " Cumulative Stats");
            for(IntegrityStatisticBean isb : triggers_cumulative.getCollection()){
                if(isb.getName().equals(name)) temp_collection.addToCollection(isb);
            }
            StringBuilder filePath = new StringBuilder("C:\\Users\\bryan\\IdeaProjects\\Integrity Performance Tool\\Output\\");
            filePath.append("Triggers\\");
            filePath.append("Cumulative Entries\\");
            File temp_dir = new File(filePath.toString());
            if (!temp_dir.exists()) temp_dir.mkdirs();
            filePath.append(cleanString(name));
            filePath.append(".csv");

            temp_collection.writeToFile(filePath.toString());
        }

    }

    public static void print(String arg_str) {
        System.out.println(arg_str + "\n");
    }

    public static String cleanString(String arg_s){
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
