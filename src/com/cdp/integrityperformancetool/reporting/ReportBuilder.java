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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by bryan on 6/19/2015.
 */
public class ReportBuilder {

    private String name;
    private String description;
    private String reportFile;
    private String dataSrcFile;
    private String reportOutputFile;
    private HashMap reportParams = new HashMap();
    private String dateRangeStart = "";
    private String dateRangeEnd = "";
    private static DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MMM dd YYYY");

    public ReportBuilder(String arg_name, String arg_description, String arg_reportFile, String arg_dataSrcFile,
                         String arg_reportOutputFile) {
        this.name = arg_name;
        this.description = arg_description;
        this.reportFile = arg_reportFile;
        this.dataSrcFile = arg_dataSrcFile;
        this.reportOutputFile = arg_reportOutputFile;
        this.reportParams.put("ReportName", this.name);
        this.reportParams.put("ReportDescription", this.description);
        this.reportParams.put("StartDate", this.dateRangeStart);
        this.reportParams.put("EndDate", this.dateRangeEnd);
    }

    public ReportBuilder(String arg_name, String arg_description, String arg_reportFile, String arg_dataSrcFile,
                         String arg_reportOutputFile, DateTime startDate, DateTime endDate) {
        this.name = arg_name;
        this.description = arg_description;
        this.reportFile = arg_reportFile;
        this.dataSrcFile = arg_dataSrcFile;
        this.reportOutputFile = arg_reportOutputFile;
        this.reportParams.put("ReportName", this.name);
        this.reportParams.put("ReportDescription", this.description);
        this.dateRangeStart = fmt.print(startDate).toString();
        this.dateRangeEnd = fmt.print(endDate).toString();
        this.reportParams.put("StartDate", this.dateRangeStart);
        this.reportParams.put("EndDate", this.dateRangeEnd);
    }

    public ReportBuilder(String arg_name, String arg_description, String arg_reportFile, String arg_dataSrcFile,
                         String arg_reportOutputFile, String arg_statName, String arg_statGrp) {
        this.name = arg_name;
        this.description = arg_description;
        this.reportFile = arg_reportFile;
        this.dataSrcFile = arg_dataSrcFile;
        this.reportOutputFile = arg_reportOutputFile;
        this.reportParams.put("ReportName", this.name);
        this.reportParams.put("ReportDescription", this.description);
        this.reportParams.put("STATISTIC_NAME", arg_statName);
        this.reportParams.put("STATISTIC_GROUP", arg_statGrp);
    }

    public void generateReport(){
        try{

            System.out.println("About to create report: " + this.name);
            String jasperFile = JasperCompileManager.compileReportToFile(this.reportFile);

            JasperReport jReport = JasperCompileManager.compileReport(this.reportFile);

            JRCsvDataSource jReportCsvSource = new JRCsvDataSource(JRLoader.getLocationInputStream(this.dataSrcFile));
            jReportCsvSource.setUseFirstRowAsHeader(true);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jReport, this.reportParams, jReportCsvSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, this.reportOutputFile);
            System.out.println("Successfully created report!");
        }  catch (JRException e) {
            e.printStackTrace();
        }
    }


}
