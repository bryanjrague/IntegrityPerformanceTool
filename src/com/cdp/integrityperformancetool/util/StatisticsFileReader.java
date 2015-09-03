package com.cdp.integrityperformancetool.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.Instant;
import org.joda.time.DateTime;

import com.cdp.integrityperformancetool.IntegrityStatisticBean;
import com.cdp.integrityperformancetool.StatisticsCollection;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by bryan on 8/7/2015.
 */
public class StatisticsFileReader {

    private String str_filePath; //** file being read must meet expected column formatting **
    //private static Path path_filePath;
    private String str_value_separator;
    private int int_skipLines;

    public StatisticsFileReader(String arg_filePath){
        this.str_filePath  = arg_filePath;
    //    this.path_filePath = Paths.get(URI.create(arg_filePath));
        this.str_value_separator = ",";
        this.int_skipLines = 1;
    }

    public StatisticsFileReader(String arg_filePath, String arg_value_separator){
        this.str_filePath  = arg_filePath;
   //     this.path_filePath = Paths.get(URI.create(arg_filePath));
        this.str_value_separator = arg_value_separator;
        this.int_skipLines = 1;
    }

    public StatisticsFileReader(String arg_filePath, int arg_skipLines){
        this.str_filePath  = arg_filePath;
    //    this.path_filePath = Paths.get(URI.create(arg_filePath));
        this.str_value_separator = ",";
        this.int_skipLines = arg_skipLines;
    }

    public StatisticsFileReader(String arg_filePath, String arg_value_separator, int arg_skipLines){
        this.str_filePath  = arg_filePath;
    //    this.path_filePath = Paths.get(URI.create(arg_filePath));
        this.str_value_separator = arg_value_separator;
        this.int_skipLines = arg_skipLines;
    }

    private IntegrityStatisticBean arrayToIsb(String[] arg_str_array){
        IntegrityStatisticBean isb = new IntegrityStatisticBean();
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

    private String[] correctColumnFormatting(String[] arg_stringArray){
        int combineThrough = (arg_stringArray.length-14)+3;
        //combineThrough represents the highest column number which the name of the statistic
        //has been spread out through (col 4 through combineThrough) due to one or more
        //of the designated str_valueSeparator characters appearing in the name of
        //the statistic. Here it is assumed that the csv file being read meets the
        //format of 13 columns, where the 4th from the left, starting with column 1, is the
        //"Statistic" column representing the name of the statistic.
        int offset = 0;

        for(int col=4;col<arg_stringArray.length;col++){
            if(col<=combineThrough){
                arg_stringArray[3] = arg_stringArray[3] + "," + arg_stringArray[col];
                offset += 1;
            } else arg_stringArray[col-offset] = arg_stringArray[col];
        }
        //System.out.println("offset: " + offset);
        //the above loop is working to correct the unintended splitting of the name
        //value into more than one String[] index due to the presence of the value
        //separator in the name. It combines columns 4 through combineThrough and
        //shifts the remaining columns back into their expected column positions.

        return arg_stringArray;
    }

    //returns hashmap{statisticsCollection.group, arraylist<isb>}
    public HashMap<String, StatisticsCollection> executeStatisticsRetrieval() {

        HashMap<String, StatisticsCollection> hashMap_stat_mapper = new HashMap<String, StatisticsCollection>();
        try {
            InputStream is = new FileInputStream(new File(str_filePath));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String current_stats;
            int lineCount = 1;
            while ((current_stats = br.readLine()) != null) {
                debug("Line " + lineCount);
                if (lineCount > this.int_skipLines) {
                    String[] currStatsArray = current_stats.split(this.str_value_separator);
                    //TODO: how to deal with blank values in the .csv file, causes nullpointer
                    if (currStatsArray.length > 14) {
                        currStatsArray = correctColumnFormatting(currStatsArray);
                    }

                    debug("current string: ");
                    for(int i=0;i<currStatsArray.length;i++){debug(i + "."+currStatsArray[i]);}

                    IntegrityStatisticBean temp_isb = arrayToIsb(currStatsArray);
                    debug("bean: ");
                    printState(temp_isb);

                    if (hashMap_stat_mapper.containsKey(temp_isb.getGroup())) {
                        hashMap_stat_mapper.get(temp_isb.getGroup()).addToCollection(temp_isb);
                    } else {
                        StatisticsCollection temp_collection = new StatisticsCollection(temp_isb.getGroup()
                                + " Group Collection");
                        temp_collection.addToCollection(temp_isb);
                        hashMap_stat_mapper.put(temp_isb.getGroup(), temp_collection);
                    }
                    debug( hashMap_stat_mapper.toString());
                }
                lineCount +=1;
            }
        }catch(FileNotFoundException fnfe){
            //TODO: error catching
            System.out.println("WARNING: FILE NOT FOUND - " + fnfe);
        }catch(IOException ioe){
            //TODO: error catching
            System.out.println("WARNING: IOEXCEPTION - " + ioe);
        } catch(Exception e){
            //TODO: error catching
            System.out.println("WARNING: GENERAL EXCEPTION - " + e);
        }

        return hashMap_stat_mapper;
    }



    public String getStringFilePath(){ return this.str_filePath; }

    public int getSkipLines(){ return this.int_skipLines; }

    public String getValueSeparator() { return this.str_value_separator; }

    public void setFilePath(String arg_filePath){ this.str_filePath = arg_filePath; }

    public void setSkipLines(int arg_numLines) { this.int_skipLines = arg_numLines; }

    public void setValueSeparator(String arg_separator){ this.str_value_separator = arg_separator; }

    private void debug(String s){
        //TODO: remove
        System.out.println(s);
    }

    private void printState(IntegrityStatisticBean isb){
        //TODO: remove
        //print current state of imb object for sanity check and debug.
        System.out.println(" ** IntegrityStatisticBean State **");
        System.out.println(" **********************************");
        System.out.println("average: " + isb.getAverage());
        System.out.println("count: " + isb.getCount());
        System.out.println("endDate: " + isb.getEndDate());
        System.out.println("group: " + isb.getGroup());
        System.out.println("kind: " + isb.getKind());
        System.out.println("max: " + isb.getMax());
        System.out.println("min: " + isb.getMin());
        System.out.println("mode: " + isb.getMode());
        System.out.println("name: " + isb.getName());
        System.out.println("startDate: " + isb.getStartDate());
        System.out.println("sum: " + isb.getSum());
        System.out.println("totalCount: " + isb.getTotalCount());
        System.out.println("unit: " + isb.getUnit());
        System.out.println("used: " + isb.getUsed());
        System.out.println(" **********************************\n");
    }
}
