package com.cdp.integrityperformancetool;

import org.joda.time.DateTime;

/**
 * The IntegrityStatistic class abstracts a single statistic row entry within the Integrity Statistics output .csv data.
 * Data from each of the 14 standard columns generated in the Integrity Server Statistics are stored in an instance
 * variable.
 *
 * Each IntegrityStatistic has the following fields:
 * <ul>
 *     <li>average - (Long) holding the value of the 'Average' column in the Integrity Statistics .csv file</li>
 *     <li>count - (Long) holding the value of the 'Count' column in the Integrity Statistics .csv file</li>
 *     <li>endDate - (org.joda.time.DateTime) holding the value of the 'End Date' column in the Integrity Statistics .csv file</li>
 *     <li>group - (String) holding the value of the 'Group' column in the Integrity Statistics .csv file</li>
 *     <li>kind - (String) holding the value of the 'Kind' column in the Integrity Statistics .csv file</li>
 *     <li>max - (Long) holding the value of the 'Max' column in the Integrity Statistics .csv file</li>
 *     <li>min - (Long) holding the value of the 'Min' column in the Integrity Statistics .csv file</li>
 *     <li>mode - (String) holding the value of the 'String' column in the Integrity Statistics .csv file</li>
 *     <li>name - (String) holding the value of the 'Statistic' column in the Integrity Statistics .csv file</li>
 *     <li>startDate - (org.joda.time.DateTime) holding the value of the 'Start Date' column in the Integrity Statistics .csv file</li>
 *     <li>sum - (Long) holding the value of the 'Sum' column in the Integrity Statistics .csv file</li>
 *     <li>totalCount - (Long) holding the value of the 'Total' column in the Integrity Statistics .csv file</li>
 *     <li>unit - (String)holding the value of the 'Unit' column in the Integrity Statistics .csv file</li>
 *     <li>used - (String) holding the value of the 'Used' column in the Integrity Statistics .csv file</li>
 * </ul>
 */
public class IntegrityStatistic {

    //instance variables
    private Long average = 0L;
    private Long count = 0L;
    private DateTime endDate = new DateTime();
    private String group = "";
    private String kind = "";
    private Long max = 0L;
    private Long min = 0L;
    private String mode;
    private String name = "";
    private DateTime startDate = new DateTime();
    private Long sum = 0L;
    private Long totalCount = 0L;
    private String unit = "";
    private String used = "";

    /**
     * Default constructor which creates an IntegrityStatistic with default values.
     */
    public IntegrityStatistic(){

    }

    /**
     * Removes chars prohibited from file names.
     * @param arg_s - String value to remove prohibited chars from.
     * @return
     */
    private static String cleanString(String arg_s){
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
        arg_s = arg_s.replace("?", "");
        return arg_s;
    }

    /**
     * Retrieves the current average value of the IntegrityStatistic.
     * @return (Long) The current average value of the IntegrityStatistic.
     */
    public Long getAverage(){ return this.average; }

    /**
     * Retrieves the current count value of the IntegrityStatistic.
     * @return (Long) The current count value of the IntegrityStatistic.
     */
    public Long getCount(){ return this.count; }

    /**
     * Retrieves the current End Date value of the IntegrityStatistic.
     * @return (org.joda.time.DateTime) The current End Date value of the IntegrityStatistic.
     */
    public DateTime getEndDate(){ return this.endDate; }

    /**
     * Retrieves the current group value of the IntegrityStatistic.
     * @return (String) The current group value of the IntegrityStatistic.
     */
    public String getGroup(){ return this.group; }

    /**
     * Retrieves the current kind value of the IntegrityStatistic.
     * @return (String) The current kind value of the IntegrityStatistic.
     */
    public String getKind(){ return this.kind; }

    /**
     * Retrieves the current maximum value of the IntegrityStatistic.
     * @return (Long) The current maximum value of the IntegrityStatistic.
     */
    public Long getMax(){ return this.max; }

    /**
     * Retrieves the current minimum value of the IntegrityStatistic.
     * @return (Long) The current minimum value of the IntegrityStatistic.
     */
    public Long getMin(){ return this.min; }

    /**
     * Retrieves the current mode value of the IntegrityStatistic.
     * @return (Long) The current mode value of the IntegrityStatistic.
     */
    public String getMode(){ return this.mode; }

    /**
     * Retrieves the current name value of the IntegrityStatistic.
     * @return (Long) The current name value of the IntegrityStatistic.
     */
    public String getName(){ return this.name; }

    /**
     * Retrieves the current Start Date value of the IntegrityStatistic.
     * @return (org.joda.time.DateTime) The current Start Date value of the IntegrityStatistic.
     */
    public DateTime getStartDate(){ return this.startDate; }

    /**
     * Retrieves the current sum value of the IntegrityStatistic.
     * @return (Long) The current sum value of the IntegrityStatistic.
     */
    public Long getSum(){ return this.sum; }

    /**
     * Retrieves the current total count value of the IntegrityStatistic.
     * @return (Long) The current totcal count value of the IntegrityStatistic.
     */
    public Long getTotalCount(){ return this.totalCount; }

    /**
     * Retrieves the current unit value of the IntegrityStatistic.
     * @return (String) The current unit value of the IntegrityStatistic.
     */
    public String getUnit(){ return this.unit; }

    /**
     * Retrieves the current average value of the IntegrityStatistic.
     * @return (String) The current average value of the IntegrityStatistic.
     */
    public String getUsed(){ return this.used; }

    /**
     * Prints the IntegrityStatistic's fields to the system console.
     * For troubleshooting and debugging
     */
    public void printIsbState(){
        StringBuilder statStr = new StringBuilder();
        statStr.append(this.getStartDate().toString() + ","
                + this.getEndDate().toString() + ","
                + this.getGroup() + ",'"
                + this.getName() + "',"
                + this.getKind() + ","
                + this.getUnit() + ","
                + this.getCount() + ","
                + this.getTotalCount() + ","
                + this.getSum() + ","
                + this.getUsed() + ","
                + this.getMin() + ","
                + this.getMax() + ","
                + this.getAverage()  + ","
                + this.getMode() + "\n");
        System.out.println(statStr);
    }

    /**
     * Sets the value of the average field for the IntegrityStatistic.
     * @param arg_average - (Long) value to assign to the average field.
     */
    public void setAverage(Long arg_average){ this.average = arg_average; }

    /**
     * Sets the value of the average field for the IntegrityStatistic.
     * @param arg_average - (int) value to assign to the average field.
     */
    public void setAverage(int arg_average){ this.average = Long.valueOf(arg_average); }

    /**
     * Sets the value of the average field for the IntegrityStatistic.
     * @param arg_average - (String) value to assign to the average field. NOTE:
     *                    The String must be in a format that can be converted to a
     *                    number. A default value of 0L is assigned if a the
     *                    NumberFormatException is encountered converting the
     *                    String to a Long object.
     */
    public void setAverage(String arg_average){
        try{
            this.average = Long.valueOf(arg_average);
        } catch(NumberFormatException nfe){
            this.average = 0L;
        }
    }

    /**
     * Sets the value of the count field for the IntegrityStatistic.
     * @param arg_count - (Long) value to assign to the count field.
     */
    public void setCount(Long arg_count){ this.count = arg_count; }

    /**
     * Sets the value of the count field for the IntegrityStatistic.
     * @param arg_count - (int) value to assign to the count field.
     */
    public void setCount(int arg_count){ this.count = Long.valueOf(arg_count); }

    /**
     * Sets the value of the count field for the IntegrityStatistic.
     * @param arg_count - (String) value to assign to the count field. NOTE:
     *                    The String must be in a format that can be converted to a
     *                    number. A default value of 0L is assigned if a the
     *                    NumberFormatException is encountered converting the
     *                    String to a Long object.
     */
    public void setCount(String arg_count){
        try{
            this.count = Long.valueOf(arg_count);
        } catch(NumberFormatException nfe){
            this.count = 0L;
        }
    }

    /**
     * Sets the value of the endDate field for the IntegrityStatistic.
     * @param arg_endDate - (DateTime) value to assign to the endDate field.
     */
    public void setEndDate(DateTime arg_endDate){ this.endDate = arg_endDate; }

    /**
     * Sets the value of the group field for the IntegrityStatistic.
     * @param arg_group - (String) value to assign to the group field.
     */
    public void setGroup(String arg_group){ this.group = arg_group; }

    /**
     * Sets the value of the kind field for the IntegrityStatistic.
     * @param arg_kind - (String) value to assign to the kind field.
     */
    public void setKind(String arg_kind){ this.kind = arg_kind; }

    /**
     * Sets the value of the max field for the IntegrityStatistic.
     * @param arg_max - (Long) value to assign to the max field.
     */
    public void setMax(Long arg_max){ this.max = arg_max; }

    /**
     * Sets the value of the max field for the IntegrityStatistic.
     * @param arg_max - (int) value to assign to the max field.
     */
    public void setMax(int arg_max){ this.max = Long.valueOf(arg_max); }

    /**
     * Sets the value of the max field for the IntegrityStatistic.
     * @param arg_max - (String) value to assign to the max field. NOTE:
     *                    The String must be in a format that can be converted to a
     *                    number. A default value of 0L is assigned if a the
     *                    NumberFormatException is encountered converting the
     *                    String to a Long object.
     */
    public void setMax(String arg_max){
        try{
            this.max = Long.valueOf(arg_max);
        } catch(NumberFormatException nfe){
            this.max = 0L;
        }
    }

    /**
     * Sets the value of the min field for the IntegrityStatistic.
     * @param arg_min - (Long) value to assign to the min field.
     */
    public void setMin(Long arg_min){ this.min = arg_min; }

    /**
     * Sets the value of the max field for the IntegrityStatistic.
     * @param arg_min - (int) value to assign to the max field.
     */
    public void setMin(int arg_min){ this.min = Long.valueOf(arg_min); }

    /**
     * Sets the value of the min field for the IntegrityStatistic.
     * @param arg_min - (String) value to assign to the min field. NOTE:
     *                    The String must be in a format that can be converted to a
     *                    number. A default value of 0L is assigned if a the
     *                    NumberFormatException is encountered converting the
     *                    String to a Long object.
     */
    public void setMin(String arg_min){
        try{
            this.min = Long.valueOf(arg_min);
        } catch(NumberFormatException nfe){
            this.min = 0L;
        }
    }

    /**
     * Sets the value of the kind field for the IntegrityStatistic.
     * @param arg_mode - (String) value to assign to the kind field.
     */
    public void setMode(String arg_mode){ this.mode = arg_mode; }

    /**
     * Sets the value of the name field for the IntegrityStatistic.
     * @param arg_name - (String) value to assign to the name field.
     */
    public void setName(String arg_name){ this.name = cleanString(arg_name); }

    /**
     * Sets the value of the start date field for the IntegrityStatistic.
     * @param arg_startDate - (org.joda.time.DateTime) value to assign to the start date field.
     */
    public void setStartDate(DateTime arg_startDate){ this.startDate = arg_startDate; }

    /**
     * Sets the value of the sum field for the IntegrityStatistic.
     * @param arg_sum - (Long) value to assign to the sum field.
     */
    public void setSum(Long arg_sum){ this.sum = arg_sum; }

    /**
     * Sets the value of the kind field for the IntegrityStatistic.
     * @param arg_sum - (int) value to assign to the kind field.
     */
    public void setSum(int arg_sum){
        try{
            this.sum = Long.valueOf(arg_sum);
        } catch(NumberFormatException nfe){
            this.sum = 0L;
        }
    }

    /**
     * Sets the value of the sum field for the IntegrityStatistic.
     * @param arg_sum - (String) value to assign to the sum field. NOTE:
     *                    The String must be in a format that can be converted to a
     *                    number. A default value of 0L is assigned if a the
     *                    NumberFormatException is encountered converting the
     *                    String to a Long object.
     */
    public void setSum(String arg_sum){
        try{
            this.sum = Long.valueOf(arg_sum);
        } catch(NumberFormatException nfe) {
            this.sum = 0L;
        }
    }

    /**
     * Sets the value of the total count field for the IntegrityStatistic.
     * @param arg_totalCount - (Long) value to assign to the total count field.
     */
    public void setTotalCount(Long arg_totalCount){ this.totalCount = arg_totalCount; }

    /**
     * Sets the value of the total count field for the IntegrityStatistic.
     * @param arg_totalCount - (int) value to assign to the total count field.
     */
    public void setTotalCount(int arg_totalCount){ this.totalCount = Long.valueOf(arg_totalCount); }

    /**
     * Sets the value of the total count field for the IntegrityStatistic.
     * @param arg_totalCount - (String) value to assign to the total count field. NOTE:
     *                    The String must be in a format that can be converted to a
     *                    number. A default value of 0L is assigned if a the
     *                    NumberFormatException is encountered converting the
     *                    String to a Long object.
     */
    public void setTotalCount(String arg_totalCount){
        try{
            this.totalCount = Long.valueOf(arg_totalCount);
        } catch(NumberFormatException nfe){
            this.totalCount = 0L;
        }
    }

    /**
     * Sets the value of the unit field for the IntegrityStatistic.
     * @param arg_unit - (String) value to assign to the unit field.
     */
    public void setUnit(String arg_unit){ this.unit = arg_unit; }

    /**
     * Sets the value of the used field for the IntegrityStatistic.
     * @param arg_used - (String) value to assign to the used field.
     */
    public void setUsed(String arg_used){ this.used = arg_used; }

}
