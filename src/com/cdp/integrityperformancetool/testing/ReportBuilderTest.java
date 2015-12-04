package com.cdp.integrityperformancetool.testing;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import java.util.HashMap;

/**
 * Created by bryan on 11/29/2015.
 */
public class ReportBuilderTest {

    private static String sourceFile = "C:\\Users\\bryan\\IdeaProjects\\IntegrityPerformanceTool\\Output\\Triggers\\HistoricalDetailedPerformance\\Aggregate Approvers to Parent RFCIT Service Version pre.csv";
   // private static String sourceFile = "C:\\Users\\bryan\\IdeaProjects\\IntegrityPerformanceTool\\Output\\Triggers\\HistoricalDetailedPerformance\\Apply Routing Rule - RFC From Project - Change Management pre.csv";
    private static String reportJrxmlFile = "C:\\Users\\bryan\\IdeaProjects\\IntegrityPerformanceTool\\ReportBuildingData\\graphTest4.jrxml";
    private static HashMap reportParams = new HashMap();
    private static String reportOutputFileLocation = "C:\\Users\\bryan\\IdeaProjects\\IntegrityPerformanceTool\\TEST GRAPH.PDF";

    public static void main(String args[]){

        reportParams.put("STATISTIC_NAME", "Test Statistic XYZ");
        try{

            System.out.println("About to create report...");
            String jasperFile = JasperCompileManager.compileReportToFile(reportJrxmlFile);

            JasperReport jReport = JasperCompileManager.compileReport(reportJrxmlFile);

            JRCsvDataSource jReportCsvSource = new JRCsvDataSource(JRLoader.getLocationInputStream(sourceFile));
            jReportCsvSource.setUseFirstRowAsHeader(true);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jReport, reportParams, jReportCsvSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, reportOutputFileLocation );
            System.out.println("Successfully created report!");
        }  catch (JRException e) {
            e.printStackTrace();
        }

    }

}
