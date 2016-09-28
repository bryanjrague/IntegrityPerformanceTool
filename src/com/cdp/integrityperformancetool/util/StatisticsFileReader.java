package com.cdp.integrityperformancetool.util;

import java.io.*;

import com.cdp.integrityperformancetool.StatisticsLibrary;
import org.joda.time.Instant;

import com.cdp.integrityperformancetool.IntegrityStatistic;
import com.cdp.integrityperformancetool.StatisticsCollection;
import org.joda.time.format.DateTimeFormat;

/**
 * The StatisticsFileReader class reads a provided .csv file and converts it to a StatisticsLibrary, containing a
 * StatisticsCollection for every unique "Group" column in the source .csv file. The .csv file to be read
 * must be in the expected formatting of the Integrity Server Statistics .csv output file with columns matching:
 * Start Date, End Date, Group, Statistic, Kind, Unit, Count, Total, Sum, Used, Min, Max, Average, Mode
 *
 * See the <i>executeStatisticsRetrieval()</i> method for more information about the output StatisticsLibrary object.
 */
public class StatisticsFileReader {

    private String str_filePath;
    private String str_value_separator;
    private int int_skipLines;
    private String[] statGrpsInScope;

    public StatisticsFileReader(){
        this.setFilePath(" ");
        this.setValueSeparator(",");
        this.setSkipLines(1);
        this.setStatGrpsInScope(new String[0]);
    }

    public StatisticsFileReader(String arg_filePath){
        this();
        this.setFilePath(arg_filePath);
    }

    public StatisticsFileReader(String arg_filePath, String[] arg_grpsInScope){
        this();
        this.setFilePath(arg_filePath);
        this.setStatGrpsInScope(arg_grpsInScope);
    }

    public StatisticsFileReader(String arg_filePath, String arg_value_separator, String[] arg_grpsInScope){
        this.setFilePath(arg_filePath);
        this.setValueSeparator(arg_value_separator);
        this.setSkipLines(1);
        this.setStatGrpsInScope(arg_grpsInScope);
    }

    public StatisticsFileReader(String arg_filePath, int arg_skipLines, String[] arg_grpsInScope){
        this.setFilePath(arg_filePath);
        this.setValueSeparator(",");
        this.setSkipLines(arg_skipLines);
        this.setStatGrpsInScope(arg_grpsInScope);
    }

    public StatisticsFileReader(String arg_filePath, String arg_value_separator, int arg_skipLines, String[] arg_grpsInScope){
        this(arg_filePath, arg_value_separator, arg_grpsInScope);
        this.setSkipLines(arg_skipLines);
    }

    private IntegrityStatistic arrayToIsb(String[] arg_str_array){

        IntegrityStatistic isb = new IntegrityStatistic();
        Instant start_instant = new Instant().parse(arg_str_array[0],
                DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss zzz YYYY"));
        Instant end_instant = new Instant().parse(arg_str_array[1],
                DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss zzz YYYY"));

        isb.setStartDate(start_instant.toDateTime());
        isb.setEndDate(end_instant.toDateTime());
        isb.setGroup(arg_str_array[2]);
        isb.setName(arg_str_array[3]);
        isb.setKind(arg_str_array[4]);
        isb.setUnit(arg_str_array[5]);
        isb.setCount(arg_str_array[6]);
        isb.setTotalCount(arg_str_array[7]);
        isb.setSum(arg_str_array[8]);
        isb.setUsed(arg_str_array[9]);
        isb.setMin(arg_str_array[10]);
        isb.setMax(arg_str_array[11]);
        isb.setAverage(arg_str_array[12]);
        isb.setMode(arg_str_array[13]);

        return isb;
    }

    /** Accounts for some statistic names containing the str_value_separator. in this case, the name may be
     *  split into more than one column unnecessarily, which creates more columns than expected causing issues
     *  later on with data processing. This method reconfigures the columns to correct for the situation.
     */
    private String[] correctColumnFormatting(String[] arg_stringArray){
        int combineThrough = (arg_stringArray.length-14)+3;
        int offset = 0;

        for(int col=4;col<arg_stringArray.length;col++){
            if(col<=combineThrough){
                arg_stringArray[3] = arg_stringArray[3] + this.str_value_separator + arg_stringArray[col];
                offset += 1;
            } else arg_stringArray[col-offset] = arg_stringArray[col];
        }
        return arg_stringArray;
    }

    /**
     * Reads the .csv file stored as the StatisticsFileReader class' "str_filePath" attribute. Processes all the
     * statistics and exports them as a single StatisticsLirary object. Contained within the StatisticsLibrary is
     * a StatisticsCollection object for each unique Group name in the source file. Each StatisticsCollection object
     * contains IntegrityStatistic objects of the group name, where each IntegrityStatistic object holds data from a
     * single line of the source .csv file.
     * @return (StatisticsLibrary) - the processed statistics in the form of a StatisticsLibrary, holding StatisticsCollection
     * objects, each holding IntegrityStatistic Objects.
     */
    public StatisticsLibrary executeStatisticsRetrieval() {

        StatisticsLibrary lib_stat_mapper = new StatisticsLibrary();
        try {
            InputStream is = new FileInputStream(new File(str_filePath));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String current_line;
            int lineCount = 1;
            while ((current_line = br.readLine()) != null) {
                String current_stats = current_line.replace(",,", ", ,").replace("\"", "");
                if (lineCount > this.int_skipLines) {
                    String[] currStatsArray = current_stats.split(this.str_value_separator);

                    if (currStatsArray.length > 14) {
                        currStatsArray = correctColumnFormatting(currStatsArray);
                    }

                    IntegrityStatistic temp_isb = arrayToIsb(currStatsArray);

                    if(statGrpsInScope.length>0){
                        for(String g: statGrpsInScope) {
                            if (temp_isb.getGroup().equals(g)) {
                                if (lib_stat_mapper.containsGroupName(temp_isb.getGroup())) {
                                    lib_stat_mapper.getStatisticsGroupName(temp_isb.getGroup()).addToCollection(temp_isb);
                                } else {
                                    StatisticsCollection temp_collection = new StatisticsCollection(temp_isb.getGroup());
                                    temp_collection.addToCollection(temp_isb);
                                    lib_stat_mapper.addToLibrary(temp_isb.getGroup(), temp_collection);
                                }
                                break;
                            }
                        }
                    } else{
                        if (lib_stat_mapper.containsGroupName(temp_isb.getGroup())) {
                            lib_stat_mapper.getStatisticsGroupName(temp_isb.getGroup()).addToCollection(temp_isb);
                        } else {
                            StatisticsCollection temp_collection = new StatisticsCollection(temp_isb.getGroup());
                            temp_collection.addToCollection(temp_isb);
                            lib_stat_mapper.addToLibrary(temp_isb.getGroup(), temp_collection);
                        }
                    }
                }
                lineCount +=1;
            }
            for(StatisticsCollection sc : lib_stat_mapper.getAllValues()){
                sc.computeAllCollectionStatistics();
            }

        }catch(FileNotFoundException fnfe){
            System.out.println("StatisticsFileReader.executeStatisticsRetrieval - WARNING: FILE NOT FOUND - " + fnfe);
        }catch(IOException ioe){
            System.out.println("StatisticsFileReader.executeStatisticsRetrieval - WARNING: IOEXCEPTION - " + ioe);
        } catch(Exception e){
            System.out.println("StatisticsFileReader.executeStatisticsRetrieval - WARNING: GENERAL EXCEPTION - " + e);
        }

        return lib_stat_mapper;
    }

    /**
     * Returns the current list of Statistic Group names which are in scope for retrieval, analysis and reporting.
     * @return (String[]) Array of Statistic Group names in scope for.
     */
    public String[] getStatGrpsInScope(){ return this.statGrpsInScope; }

    /**
     * Returns the current filepath to the source .csv file.
     * @return (String) - filepath to the source file that will be read.
     */
    public String getStringFilePath(){ return this.str_filePath; }

    /**
     * Returns the integer number of lines that will be skipped when the source file is read,
     * starting from the first line of the source file.
     * @return (int) - number of lines that will be skipped.
     */
    public int getSkipLines(){ return this.int_skipLines; }

    /**
     * Returns the String being recognized as the value separator in the source .csv file.
     * @return (Sting) - the .csv file value separator String
     */
    public String getValueSeparator() { return this.str_value_separator; }

    /**
     * Sets the .csv filepath to be read.
     * @param arg_filePath (String) - the full filepath to the source .csv file to be read.
     */
    public void setFilePath(String arg_filePath){ this.str_filePath = arg_filePath; }

    /**
     * Sets a String[] as the current list of Statistic Groups that are in scope for retrieval, analysis, and
     * reporting.
     * @param arg_grpsInScope (String[]) Array of Statistic Groups that are in scope.
     */
    public void setStatGrpsInScope(String[] arg_grpsInScope){ this.statGrpsInScope = arg_grpsInScope; }

    /**
     * Sets the number of lines to skip reading in the source .csv file.
     * @param arg_numLines (int) - the number of lines to skip when reading the source file.
     */
    public void setSkipLines(int arg_numLines) { this.int_skipLines = arg_numLines; }

    /**
     * Sets the String that will be recognized as the .csv value separator in the source file.
     * @param arg_separator (String) - the value to be recognized as the value separator.
     */
    public void setValueSeparator(String arg_separator){ this.str_value_separator = arg_separator; }
}
