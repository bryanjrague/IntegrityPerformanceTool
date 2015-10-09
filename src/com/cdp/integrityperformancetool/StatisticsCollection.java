package com.cdp.integrityperformancetool.reporting;

import java.util.*;
import java.io.*;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * Created by bryan on 6/19/2015.
 */
public class ReportBuilder {


    public ReportBuilder(String name, String reportFile, String dataSrcFile, String reportOutputFile, String reportName){
        HashMap reportParams = new HashMap();
        reportParams.put(reportName, name);


        try{
            System.out.println("About to create report: " + name);
            String jasperFile = JasperCompileManager.compileReportToFile(reportFile);

            JasperReport jReport = JasperCompileManager.compileReport(reportFile);

            JRCsvDataSource jReportCsvSource = new JRCsvDataSource(JRLoader.getLocationInputStream(dataSrcFile));
            jReportCsvSource.setUseFirstRowAsHeader(true);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jReport, reportParams, jReportCsvSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, reportOutputFile);
            System.out.println("Successfully created report!");
        }  catch (JRException e)
        {
            e.printStackTrace();
        }
    }


}
