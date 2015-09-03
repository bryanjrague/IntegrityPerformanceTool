package com.cdp.integrityperformancetool.testing;

import com.cdp.integrityperformancetool.StatisticsCollection;
import com.cdp.integrityperformancetool.util.StatisticsFileReader;

import java.util.HashMap;

/**
 * Created by bryan on 8/29/2015.
 */
public class StatisticsFileReader_Tester {

    /**
     * Test Case 1:
     * - open file of test data
     * - convert to StatisticsCollection
     * - check validity of StatisticsCollection data
     */
    public static void main(String[] args) {
        String file1 = "C:\\Users\\bryan\\IdeaProjects\\Integrity Performance Tool\\Input\\TestData_01.csv";

        print(" START TEST CASE 1");
        StatisticsFileReader myReader = new StatisticsFileReader(file1, 1);
        print("retrieving attrributes...");
        print(myReader.getStringFilePath());
        print(""+myReader.getSkipLines());
        print(myReader.getValueSeparator());
        print("executing get stats...");
        HashMap<String, StatisticsCollection> allStats = myReader.executeStatisticsRetrieval();
        print("full: " + allStats.toString());

        StatisticsCollection triggers = allStats.get("Trigger");
        StatisticsCollection userLogin = allStats.get("User Login");
        print("stats collected...");
        print("triggers: size =" + triggers.getCollectionSize());
        triggers.writeToString();
        print("userlogin: size="+ userLogin.getCollectionSize());
        userLogin.writeToString();
        print(" END TEST CASE 1");
    }

    public static void print(String s){
        System.out.println(s);
    }
}
