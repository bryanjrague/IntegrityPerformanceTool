package com.cdp.integrityperformancetool;

import java.util.ArrayList;

public class MetricsCollection {

	private ArrayList<IntegrityMetricBean> collection;
	private String name;
	
	public MetricsCollection(){
		this("Default MetricsCollection Name", new ArrayList<IntegrityMetricBean>());
	}
	
	public MetricsCollection(String arg_name){
		this(arg_name, new ArrayList<IntegrityMetricBean>());
	}
	
	public MetricsCollection(ArrayList<IntegrityMetricBean> arg_collection){
		this("Default MetricsCollection Name", arg_collection);
	}
	
	public MetricsCollection(String arg_name, ArrayList<IntegrityMetricBean> arg_collection){
		this.name = arg_name;
		this.collection = arg_collection;
	}
	
	public void addToCollection(IntegrityMetricBean arg_imb){ this.collection.add(arg_imb); }
	
	public void addToCollection(ArrayList<IntegrityMetricBean> arg_imb_arrayList){ 
		for(IntegrityMetricBean imb : arg_imb_arrayList){
			this.collection.add(imb); 
		}
	}
	
	public void clearCollection(){ this.collection.clear(); }
	
	public ArrayList<IntegrityMetricBean> getCollection(){ return this.collection; }
	
	public String getCollectionName(){ return this.name; }
	
	public int getCollectionSize(){ return this.collection.size(); }
	
	public void setCollectionName(String arg_name) { this.name = arg_name; }
	
	public void removeFromCollection(IntegrityMetricBean arg_imb){ this.collection.remove(arg_imb); }
	
	public void removeFromCollection(int[] indices){ 
    	for(int i : indices) { this.collection.remove(i); } 
    }
	
	public void removeFromCollection(int index){ 
    	this.collection.remove(index); 
    }
	
	public void writeToFile(){
		//TODO: determine method for this
	}
}
