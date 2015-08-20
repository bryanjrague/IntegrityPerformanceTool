package com.cdp.integrityperformancetool;

import org.joda.time.DateTime;

/**
 * Created by bryan on 6/19/2015.
 */
public class IntegrityStatisticBean {

    private Long average;
    private Long count;
    private DateTime endDate;
    private String group;
    private String kind;
    private Long max;
    private Long min;
    private String mode;
    private String name;
    private DateTime startDate;
    private Long sum;
    private Long totalCount;
    private String unit;
    private String used;


    //constructor that provides defaults upon instantiation
    public IntegrityStatisticBean(){
        this.average = 0L;
        this.count = 0L;
        this.endDate = new DateTime();
        this.group = "";
        this.kind = "";
        this.max = 0L;
        this.min = 0L;
        this.mode = "";
        this.name = "";
        this.startDate = new DateTime();
        this.sum = 0L;
        this.totalCount = 0L;
        this.unit = "";
        this.used = "";

    }

    public Long getAverage(){ return this.average; }

    public Long getCount(){ return this.count; }

    public DateTime getEndDate(){ return this.endDate; }

    public String getGroup(){ return this.group; }

    public String getKind(){ return this.kind; }

    public Long getMax(){ return this.max; }

    public Long getMin(){ return this.min; }

    public String getMode(){ return this.mode; }

    public String getName(){ return this.name; }

    public DateTime getStartDate(){ return this.startDate; }

    public Long getSum(){ return this.sum; }

    public Long getTotalCount(){ return this.totalCount; }

    public String getUnit(){ return this.unit; }

    public String getUsed(){ return this.used; }

    public void setAverage(Long arg_average){ this.average = arg_average; }
    
    public void setAverage(int arg_average){ this.average = Long.valueOf(arg_average); }
    
    public void setAverage(String arg_average){ this.average = Long.valueOf(arg_average); }

    public void setCount(Long arg_count){ this.count = arg_count; }
    
    public void setCount(int arg_count){ this.count = Long.valueOf(arg_count); }
    
    public void setCount(String arg_count){ this.count = Long.valueOf(arg_count); }

    public void setEndDate(DateTime arg_endDate){ this.endDate = arg_endDate; }

    public void setGroup(String arg_group){ this.group = arg_group; }

    public void setKind(String arg_kind){ this.kind = arg_kind; }

    public void setMax(Long arg_max){ this.max = arg_max; }
    
    public void setMax(int arg_max){ this.max = Long.valueOf(arg_max); }
    
    public void setMax(String arg_max){ this.max = Long.valueOf(arg_max); }

    public void setMin(Long arg_min){ this.min = arg_min; }
    
    public void setMin(int arg_min){ this.min = Long.valueOf(arg_min); }
    
    public void setMin(String arg_min){ this.min = Long.valueOf(arg_min); }

    public void setMode(String arg_mode){ this.mode = arg_mode; }

    public void setName(String arg_name){ this.name = arg_name; }

    public void setStartDate(DateTime arg_startDate){ this.startDate = arg_startDate; }

    public void setSum(Long arg_sum){this.sum = arg_sum; }
    
    public void setSum(int arg_sum){this.sum = Long.valueOf(arg_sum); }
    
    public void setSum(String arg_sum){this.sum = Long.valueOf(arg_sum); }

    public void setTotalCount(Long arg_totalCount){ this.totalCount = arg_totalCount; }

    public void setTotalCount(int arg_totalCount){ this.totalCount = Long.valueOf(arg_totalCount); }
    
    public void setTotalCount(String arg_totalCount){ this.totalCount = Long.valueOf(arg_totalCount); }

    public void setUnit(String arg_unit){ this.unit = arg_unit; }

    public void setUsed(String arg_used){ this.used = arg_used; }
}
