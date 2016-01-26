package com.cdp.integrityperformancetool.reporting;

import java.util.*;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * The ReportBuilder class contains overloaded constructors to accommodate the needs of various reports
 * created via the JasperReports library. Additional constructors and methods can be added to this class
 * to pass required customized report parameters for report construction.
 * <br>
 * A ReportBuilder object has the following fields:
 *<ul>
 * <li>name - Title of the Report to be generated. This value is only used in some ReportBuilder Constructors,
 * and use of the value is dependent on the JasperReports .jrxml file used in report generation.</li>
 * <li>description - description of the report to be generated. This value is only used in some ReportBuilder Constructors,
 * and use of the value is dependent on the JasperReports .jrxml file used in report generation.</li>
 * <li>reportFile - the file path to the JasperReports .jrxml file giving the report template.</li>
 * <li>dataSrcFile - the file path the .csv file containing the report data.</li>
 * <li>reportOutputFile - the file path which the generated report will be saved to.</li>
 * <li>reportParams - HashMap of report parameter names and parameter values. NOTE: Report parameters must be defined
 * in the .jrxml file being used as the reportFile. Undefined report parameters will cause for the report generation
 * to fail.</li>
 * <li>dateRangeStart - the earliest date that data in the report was collected on. This value is only used in some ReportBuilder Constructors,
 * and use of the value is dependent on the JasperReports .jrxml file used in report generation.</li>
 * <li>dateRangeEnd - the latest date that data in the report was collected on. This value is only used in some ReportBuilder Constructors,
 * and use of the value is dependent on the JasperReports .jrxml file used in report generation.</li>
 * </ul>
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

    /**
     * Default constructor, assigning all instance fields to a default value.
     */
    public ReportBuilder(){
        this.name = "DEFAULT NAME";
        this.description = "DEFAULT DESCRIPTION";
        this.reportFile = "DEFAULT REPORT FILE";
        this.dataSrcFile = "DEFAULT DATA SOURCE";
        this.reportOutputFile = "DEFAULT OUTPUT FILE";
    }

    /**
     * Constructor storing report parameters as key:value - {ReportName:name, ReportDescription:description}
     * @param arg_name - The name of the Report.
     * @param arg_description - Description of the Report.
     * @param arg_reportFile - File path to the JasperReports .jrxml file to use with report generation.
     * @param arg_dataSrcFile - File path to the .csv file holding the report data.
     * @param arg_reportOutputFile - File path to output the report generated to.
     */
    public ReportBuilder(String arg_name, String arg_description, String arg_reportFile, String arg_dataSrcFile,
                         String arg_reportOutputFile) {
        this.name = arg_name;
        this.description = arg_description;
        this.reportFile = arg_reportFile;
        this.dataSrcFile = arg_dataSrcFile;
        this.reportOutputFile = arg_reportOutputFile;
        this.reportParams.put("ReportName", this.name);
        this.reportParams.put("ReportDescription", this.description);
    }

    /**
     * Constructor storing report parameters as key:value - {ReportName:name, ReportDescription:description,
     * STATISTIC_UNIT:arg_unit, STATISTIC_GROUP:arg_grpName}
     * @param arg_name - The name of the Report.
     * @param arg_description - Description of the Report.
     * @param arg_reportFile - File path to the JasperReports .jrxml file to use with report generation.
     * @param arg_dataSrcFile - File path to the .csv file holding the report data.
     * @param arg_reportOutputFile - File path to output the report generated to.
     * @param arg_unit - The unit of measure for the report.
     * @param arg_grpName - The name of the statistics group appearing in the report.
     */
    public ReportBuilder(String arg_name, String arg_description, String arg_reportFile, String arg_dataSrcFile,
                         String arg_reportOutputFile, String arg_unit, String arg_grpName) {
        this.name = arg_name;
        this.description = arg_description;
        this.reportFile = arg_reportFile;
        this.dataSrcFile = arg_dataSrcFile;
        this.reportOutputFile = arg_reportOutputFile;
        this.reportParams.put("ReportName", this.name);
        this.reportParams.put("ReportDescription", this.description);
        this.reportParams.put("STATISTIC_UNIT", arg_unit);
        this.reportParams.put("STATISTIC_GROUP", arg_grpName);
    }

    /**
     * Constructor storing report parameters as key:value - {ReportName:arg_name, ReportDescription:arg_description,
     * StartDate:arg_startDate, EndDate:arg_endDate}
     * @param arg_name - The name of the Report.
     * @param arg_description - Description of the Report.
     * @param arg_reportFile - File path to the JasperReports .jrxml file to use with report generation.
     * @param arg_dataSrcFile - File path to the .csv file holding the report data.
     * @param arg_reportOutputFile - File path to output the report generated to.
     * @param arg_startDate - org.joda.time.DateTime object representing the earliest date that report data
     *                  began collection.
     * @param arg_endDate - org.joda.time.DateTime object representing the latest date that report data ended
     *                collection.
     */
    public ReportBuilder(String arg_name, String arg_description, String arg_reportFile, String arg_dataSrcFile,
                         String arg_reportOutputFile, DateTime arg_startDate, DateTime arg_endDate) {
        this.name = arg_name;
        this.description = arg_description;
        this.reportFile = arg_reportFile;
        this.dataSrcFile = arg_dataSrcFile;
        this.reportOutputFile = arg_reportOutputFile;
        this.reportParams.put("ReportName", this.name);
        this.reportParams.put("ReportDescription", this.description);
        this.dateRangeStart = fmt.print(arg_startDate).toString();
        this.dateRangeEnd = fmt.print(arg_endDate).toString();
        this.reportParams.put("StartDate", this.dateRangeStart);
        this.reportParams.put("EndDate", this.dateRangeEnd);
    }

    /**
     * Constructor storing report parameters as key:value - {ReportName:arg_name, ReportDescription:arg_description,
     * STATISTIC_UNIT:arg_unit, STATISTIC_GROUP:arg_grpName, GRP_COUNT:arg_grpComputeVals[0],
     * GRP_MIN:arg_grpComputeVals[1], GRP_MAX:arg_grpComputeVals[2], GRP_AVG:arg_grpComputeVals[3]}
     * @param arg_name - The name of the Report.
     * @param arg_description - Description of the Report.
     * @param arg_reportFile - File path to the JasperReports .jrxml file to use with report generation.
     * @param arg_dataSrcFile - File path to the .csv file holding the report data.
     * @param arg_reportOutputFile - File path to output the report generated to.
     * @param arg_statName - The name of the statistic appearing in the report.
     * @param arg_statGrp - The group that the statistic being reported on belongs to.
     * @param arg_grpComputeVals - Long[] array of computation values appearing as [Cumulaitve Count,
     *                           Absolute Minimum Value, Absolute Maximum Value, Average]
     * @param arg_unit - The unit of measure for the report.
     */
    public ReportBuilder(String arg_name, String arg_description, String arg_reportFile, String arg_dataSrcFile,
                         String arg_reportOutputFile, String arg_statName, String arg_statGrp, Long[] arg_grpComputeVals,
                         String arg_unit) {
        this.name = arg_name;
        this.description = arg_description;
        this.reportFile = arg_reportFile;
        this.dataSrcFile = arg_dataSrcFile;
        this.reportOutputFile = arg_reportOutputFile;
        this.reportParams.put("ReportName", this.name);
        this.reportParams.put("ReportDescription", this.description);
        this.reportParams.put("STATISTIC_NAME", arg_statName);
        this.reportParams.put("STATISTIC_GROUP", arg_statGrp);
        this.reportParams.put("GRP_COUNT", arg_grpComputeVals[0]);
        this.reportParams.put("GRP_MIN", arg_grpComputeVals[1]);
        this.reportParams.put("GRP_MAX", arg_grpComputeVals[2]);
        this.reportParams.put("GRP_AVG", arg_grpComputeVals[3]);
        this.reportParams.put("STATISTIC_UNIT", arg_unit);
    }

    /**
     * Run to generate a PDF file report. The report input data, report parameters, JasperReports .jrxml, and
     * output file location are all preconfigured via the ReportBuilder constructor used for instantiation.
     * This method uses those values to create and output the report.
     */
    public void generateReport() {
        try {
            //System.out.println("About to create report: " + this.name);
            String jasperFile = JasperCompileManager.compileReportToFile(this.reportFile);

            JasperReport jReport = JasperCompileManager.compileReport(this.reportFile);

            JRCsvDataSource jReportCsvSource = new JRCsvDataSource(JRLoader.getLocationInputStream(this.dataSrcFile));
            jReportCsvSource.setUseFirstRowAsHeader(true);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jReport, this.reportParams, jReportCsvSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, this.reportOutputFile);
            //System.out.println("Successfully created report!");
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a parameter and value to the reportParams.
     * @param arg_reportParamName - (String) - The Report parameter valid for the current .jrxml file.
     * @param arg_reportParamVal - (String) - The value to pass as the parameter value.
     */

    public void addReportParam(String arg_reportParamName, String arg_reportParamVal){
        getReportParams().put(arg_reportParamName, arg_reportParamVal);
    }

    /**
     * Retrieve the current name field for the ReportBuilder.
     * @return (String) the current name field value.
     */
    public String getName(){ return this.name;}

    /**
     * Retrieve the current description field for the ReportBuilder.
     * @return (String) the current description field value.
     */
    public String getDescription(){ return this.description;}

    /**
     * Retrieve the current reportFile field for the ReportBuilder.
     * @return (String) the current reportFile field value.
     */
    public String getReportFile(){ return this.reportFile;}

    /**
     * Retrieve the current dataSrcFile field for the ReportBuilder.
     * @return (String) the current dataSrcFile field value.
     */
    public String getDataSrcFile(){ return this.dataSrcFile;}

    /**
     * Retrieve the current reportOutputFile field for the ReportBuilder.
     * @return (String) the current reportOutputFile field value.
     */
    public String getReportOutputFile(){ return this.name;}

    /**
     * Retrieve the String value of a report parameter value currently stored for the ReportBuilder.
     * @param arg_reportParam - (String) the report parameter name existing in reportParams.
     * @return The value of the reportParam key provided.
     */
    public String getReportParam(String arg_reportParam){
        try{
            return getReportParams().get(arg_reportParam);
        } catch(Exception e){
            System.out.println("ReportBuilder.getReportParam() - ERROR: Report Parameter "
                + "provided does not exist. ");
            return null;
        }
    }

    /**
     * Retrieve the current reportParams field for the ReportBuilder.
     * @return (HashMap[String, String]) the current reportParams field value.
     */
    public HashMap<String, String> getReportParams(){ return this.reportParams;}

    /**
     * Retrieve the current dateRangeStart field for the ReportBuilder.
     * @return (String) the current dateRangeStart field value.
     */
    public String getDateRangeStart(){ return this.dateRangeStart;}

    /**
     * Retrieve the current dateRangeEnd field for the ReportBuilder.
     * @return (String) the current dateRangeEnd field value.
     */
    public String getDateRangeEnd(){ return this.dateRangeEnd;}

    /**
     * Set the name field of the ReportBuilder.
     * @param arg_name (String) value to assign to the name field.
     */
    public void setName(String arg_name){ this.name = arg_name;}

    /**
     * Set the description field of the ReportBuilder.
     * @param arg_description (String) value to assign to the description field.
     */
    public void setDescription(String arg_description){ this.description = arg_description;}

    /**
     * Set the reportFile field of the ReportBuilder.
     * @param arg_reportFile (String) value to assign to the name field.
     */
    public void setReportFile(String arg_reportFile){ this.reportFile = arg_reportFile;}

    /**
     * Set the dataSrcFile field of the ReportBuilder.
     * @param arg_dataSrcFile (String) value to assign to the dataSrcFile field.
     */
    public void setDataSrcFile(String arg_dataSrcFile){ this.dataSrcFile = arg_dataSrcFile;}

    /**
     * Set the reportOutputFilefield of the ReportBuilder.
     * @param arg_reportOutputFile (String) value to assign to the reportOutputFile field.
     */
    public void setReportOutputFile(String arg_reportOutputFile){ this.reportOutputFile = arg_reportOutputFile;}

    /**
     * Set the reportParams field of the ReportBuilder.
     * @param arg_reportParams (String) value to assign to the reportParams field.
     */
    public void setReportParams(String arg_reportParams){ this.name = arg_reportParams;}

    /**
     * Set the dateRangeStart field of the ReportBuilder.
     * @param arg_dateRangeStart (String) value to assign to the dateRangeStart field.
     */
    public void setdateRangeStart(String arg_dateRangeStart){ this.name = arg_dateRangeStart;}

    /**
     * Set the dateRangeEnd field of the ReportBuilder.
     * @param arg_dateRangeEnd (String) value to assign to the dateRangeEnd field.
     */
    public void setdateRangeEnd(String arg_dateRangeEnd){ this.name = arg_dateRangeEnd;}

    /**
     * Updates the value of an existing report parameter.
     * @param arg_reportParam - (String) The report parameter to change the value of.
     * @param arg_newVal - (String) The new value to relate to the report parameter.
     */
    public void updateReportParameeter(String arg_reportParam, String arg_newVal){
        try{
            getReportParams().replace(arg_reportParam, arg_newVal);
        } catch(Exception e){
            System.out.println("ReportBuilder.updateReporParameter - ERROR: Parameter name '"
                    + arg_reportParam + " does not exist and cannot be updated. No changes were made to the "
                    + "reportParam field.");
            System.out.println(e);
        }
    }
}
