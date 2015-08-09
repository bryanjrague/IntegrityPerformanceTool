package com.xerox.integrityperformancetool;

/**
 * Created by bryan on 6/19/2015.
 */
public class IntegrityMetricBean {

    private String customName;
    private String metricName;
    private java.lang.Object value;

    //contructor with default values
    public IntegrityMetricBean(){
        this("Default Custom Name", "Default Metric Name", new java.lang.Object());
    }

    //constructor with metricName and value
    public IntegrityMetricBean(String arg_metricName, java.lang.Object arg_value){
        this("Default Custom Name", arg_metricName, arg_value);
    }

    //constructor with all three class variables given
    public IntegrityMetricBean(String arg_customName, String arg_metricName, java.lang.Object arg_value){
        this.customName = arg_customName;
        this.metricName = arg_metricName;
        this.value = arg_value;
    }

    public String getCustomName(){ return this.customName; }

    public String getMetricName(){ return this.metricName; }

    public java.lang.Object getValue(){ return this.value; }

    public void setCustomName(String arg_customName){ this.customName = arg_customName; }

    public void setMetricName(String arg_metricName){ this.metricName = arg_metricName; }

    public void setValue(java.lang.Object arg_value){ this.value = arg_value; }
}
