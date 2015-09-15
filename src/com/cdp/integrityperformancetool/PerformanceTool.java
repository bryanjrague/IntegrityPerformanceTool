package com.cdp.integrityperformancetool;

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
        HashMap<String, StatisticsCollection> masterData = testData02.executeStatisticsRetrieval();

        masterData.get("EnumerationConfig").writeToString();
        masterData.get("Per Query").writeToString();
        masterData.get("User Query").writeToString();

        StatisticsCollection orderedUserQuery = masterData.get("User Query");
        orderedUserQuery.setName("User Query- ordered A-Z");
        orderedUserQuery.orderByIsbNameValue();
        orderedUserQuery.writeToString();

        StatisticsCollection triggers = masterData.get("Triggers");
        triggers.orderByIsbAverageValue();
        triggers.writeToString();
        triggers.orderByIsbCountValue();
        triggers.writeToString();
        triggers.orderByIsbMaximumValue();
        triggers.writeToString();
        triggers.orderByIsbTotalCountValue();
        triggers.writeToString();


    }

}
