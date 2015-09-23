package com.cdp.integrityperformancetool;

import com.cdp.integrityperformancetool.reporting.ReportBuilder;
import com.cdp.integrityperformancetool.util.StatisticsFileReader;

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

        //TODO: have a class that abstracts the Hashmap for ease of use
        StatisticsLibrary masterDataLib = testData02.executeStatisticsRetrieval();
        StatisticsCollection triggers = masterDataLib.getStatisticsGroupName("Triggers");

        triggers.setName("Triggers Data");
        print("name: " + triggers.getCollectionName());
        print("avg val: " + triggers.getCollectionAverageValue().toString());
        print("count val: " + triggers.getCollectionCountValue().toString());
        print("max isb arraylist: " + triggers.getCollectionMaximumIsbArrayList().toString());
        print("max val: " + triggers.getCollectionMaximumValue().toString());
        print("min isb arraylist: " + triggers.getCollectionMinimumIsbArrayList().toString());
        print("min val: " + triggers.getCollectionMinimumValue().toString());

        print("total count: " + triggers.getCollectionTotalCountValue().toString());
        print("collection size: " + triggers.getCollectionSize() + "");

        String srcFile = "C:\\Users\\bryan\\IdeaProjects\\Integrity Performance Tool\\Output\\trigger-output-01.csv";
        triggers.writeToFile(srcFile);
        String reportJrxml = "C:\\Users\\bryan\\IdeaProjects\\Integrity Performance Tool\\ReportData\\triggerSummary3.jrxml";
        String outputFile = "C:\\Users\\bryan\\IdeaProjects\\Integrity Performance Tool\\Output\\triggerReport.pdf";
        ReportBuilder triggerReport = new ReportBuilder("Trigger Report", reportJrxml, srcFile, outputFile);
        //test out creating a report from .csv file


        StatisticsCollection triggers_cleaned = new StatisticsCollection("Triggers - Cumulative only");
        for(IntegrityStatisticBean isb : triggers.getCollection()){
            if(isb.getMode().equals("cumulative")){
                triggers_cleaned.addToCollection(isb);
            }
        }
        print("name: " + triggers_cleaned.getCollectionName());
        print("avg val: " + triggers_cleaned.getCollectionAverageValue().toString());
        print("count val: " + triggers_cleaned.getCollectionCountValue().toString());
        print("max isb arraylist: " + triggers_cleaned.getCollectionMaximumIsbArrayList().toString());
        print("max val: " + triggers_cleaned.getCollectionMaximumValue().toString());
        print("min isb arraylist: " + triggers_cleaned.getCollectionMinimumIsbArrayList().toString());
        print("min val: " + triggers_cleaned.getCollectionMinimumValue().toString());

        print("total count: " + triggers_cleaned.getCollectionTotalCountValue().toString());
        print("collection size: " + triggers_cleaned.getCollectionSize()+"");



    }

    public static void print(String arg_str) {
        System.out.println(arg_str + "\n");
    }

}
