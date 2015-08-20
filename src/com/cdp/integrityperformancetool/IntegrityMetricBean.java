package com.cdp.integrityperformancetool;

/**
 * Created by bryan on 6/19/2015.
 */
public class IntegrityMetricBean {

    private String customName;
    private String metricName;
    private Long valueLong;
    private String valueString;
    private Float valueFloat;
    private int valueType; //0=long, 1=string,2=float, to help determine what type of data is stored
    						// in the metric

    //contructor with default values
    public IntegrityMetricBean(){
        this("Default Custom Name", "Default Metric Name", "No Value Provided");
    }

    //constructor with  metric Name and long value
    public IntegrityMetricBean(String arg_metricName, long arg_value){
        this("Default Custom Name", arg_metricName, arg_value);
    }
    
    //constructor with metric Name and string value
    public IntegrityMetricBean(String arg_metricName, String arg_value){
        this("Default Custom Name", arg_metricName, arg_value);
    }
    
    //constructor with metric Name and float value
    public IntegrityMetricBean(String arg_metricName, Float arg_value){
        this("Default Custom Name", arg_metricName, arg_value);
    }
    
    public IntegrityMetricBean(String arg_customName, String arg_metricName, long arg_value){
    	this.customName = arg_customName;
    	this.metricName = arg_metricName;
    	this.valueLong = arg_value;
    	this.valueType = 0;
    }
    
    public IntegrityMetricBean(String arg_customName, String arg_metricName, String arg_value){
    	this.customName = arg_customName;
    	this.metricName = arg_metricName;
    	this.valueString = arg_value;
    	this.valueType = 1;
    }
 
    public IntegrityMetricBean(String arg_customName, String arg_metricName, Float arg_value){
    	this.customName = arg_customName;
    	this.metricName = arg_metricName;
    	this.valueFloat = arg_value;
    	this.valueType = 2;
    }

    public String getCustomName(){ return this.customName; }

    public String getMetricName(){ return this.metricName; }
    
    public Float getValueFloat(){
    	return this.valueFloat;
    }
    
    public Long getValueLong(){
    	return this.valueLong;
    }
    
    public String getValueString(){
    	return this.valueString;
    }
    
    public int getValueType(){
    	return this.valueType;
    }

    public void setCustomName(String arg_customName){ this.customName = arg_customName; }

    public void setMetricName(String arg_metricName){ this.metricName = arg_metricName; }

    public void setValue(Long arg_value) {
    	//set value as long, disable all others
    	this.valueLong = arg_value;
    	this.valueType = 0;
    }
    
    public void setValue(String arg_value){ 
    	//set value as string, disable all others
    	this.valueString = arg_value;
    	this.valueType = 1;
    }
    
    public void setValue(Float arg_value) {
    	//set value as float, disable all others
    	this.valueFloat = arg_value;
    	this.valueType = 2;
    }
    
    
}
