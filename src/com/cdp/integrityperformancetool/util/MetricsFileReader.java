package com.cdp.integrityperformancetool.util;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import com.cdp.integrityperformancetool.IntegrityMetricBean;
import com.cdp.integrityperformancetool.MetricsCollection;

public class MetricsFileReader {

	private static int mapSize; 
	private String metricsFilePath;
	private String metricNames = "";
	
	public MetricsFileReader(){
		mapSize = 10 * 1024; //X  * 1024 = X kilobytes to read at a time.
		metricsFilePath = "";
	}

	public MetricsFileReader(String arg_metricsFilePath){
		mapSize = 10 * 1024; //X * 1024 = X kilobytes to read at a time.
		metricsFilePath = arg_metricsFilePath;
	}
	
	public MetricsFileReader(int arg_mapSize, String arg_metricsFilePath){
		mapSize = arg_mapSize;
		metricsFilePath = arg_metricsFilePath;
	}
	
	public IntegrityMetricBean createImb(String arg_cName, String arg_mName, String arg_value){
		IntegrityMetricBean tempImb = new IntegrityMetricBean();
		tempImb.setCustomName(arg_cName);
		tempImb.setMetricName(arg_mName);
		if(arg_value.indexOf(".")>0) {
			try {
				tempImb.setValue(Float.valueOf(arg_value)); //attempt to set float
			} catch (NumberFormatException nfe2) {
				try {
					tempImb.setValue(arg_value); //default and set to string
				} catch (Exception e) {
					//TODO: warn in logger - error
					tempImb.setValue("ERROR - NO DATA");
				}
			}
		} else {
			try {
				tempImb.setValue(Long.valueOf(arg_value)); //attempt to set long
			} catch (NumberFormatException nfe) {
				try {
					tempImb.setValue(arg_value); //default and set to string
				} catch (Exception e) {
					//TODO: warn in logger - error
					tempImb.setValue("ERROR - NO DATA");
				}
			}
		}
		return tempImb;
	}
	
	public MetricsCollection extractMetrics(){
		MetricsCollection staticCollection = new MetricsCollection(
				"Static Metrics from file '" + metricsFilePath + "'");
		MetricsCollection dynamicCollection = new MetricsCollection(
				"Dynamic Metrics from file '" + metricsFilePath + "'");
		MetricsCollection finalCollection = new MetricsCollection(
				"All Metrics from file '" + metricsFilePath + "'");
		try{
			//staticCollection = getStaticMetrics();
			dynamicCollection = getDynamicMetrics();
		} catch (FileNotFoundException fnfe){
			
		} catch (IOException ioe){
			
		} catch (Exception e){
			
		}
		
		
		finalCollection.addToCollection(staticCollection.getCollection());
		finalCollection.addToCollection(dynamicCollection.getCollection());
		
		
		return finalCollection;
	}
	
	public String extractStaticMetric(String arg_fullString, String arg_target){
		
		int targetStrLength = arg_target.length();
		int indexStart = arg_fullString.indexOf(arg_target);
		int indexEnd = 0;
		if (indexStart>=0){
			for(int i = -1; (i = arg_fullString.indexOf(",", i + 1)) != -1;){
				if(i>indexStart) {
					indexEnd = i;
					break;
				}
			}
			if(indexEnd>0){
				return arg_fullString.substring((indexStart+targetStrLength), indexEnd).trim(); 
			} else return arg_fullString.substring((indexStart+targetStrLength)).trim();
		} else {
			//TODO: warn in logger
			return "METRIC NAME NOT FOUND - NO DATA";
		}
	}

	public MetricsCollection getStaticMetrics() throws FileNotFoundException, IOException, Exception{
		MetricsCollection temp_collection = new MetricsCollection();
		BufferedReader br = new BufferedReader( new FileReader(metricsFilePath));
		
		for(int i=0;i<37;i++){
			String currentLine = br.readLine();
			switch(i){
				case 0: 
					temp_collection.addToCollection(
							createImb("Code Cache: Peak", "Peak Code Cache", 
									extractStaticMetric(currentLine, "peak=") ) );
					temp_collection.addToCollection(
							createImb("Code Cache: Current", "Current Code Cache", 
									extractStaticMetric(currentLine, "current=") ) );
					temp_collection.addToCollection(
							createImb("Code Cache: last gc", "last gc Code Cache", 
									extractStaticMetric(currentLine, "last gc=") ) );
					temp_collection.addToCollection(
							createImb("Code Cache: max", "Max Code Cache", 
									extractStaticMetric(currentLine, "max=") ) );
					break;
				case 1:
					temp_collection.addToCollection(
							createImb("Eden Space: Peak", "Peak Eden Space",
									extractStaticMetric(currentLine, "peak=") ) );
					temp_collection.addToCollection(
							createImb("Eden Space: Current", "Current Eden Space",
									extractStaticMetric(currentLine, "current=") ) );
					temp_collection.addToCollection(
							createImb("Eden Space: last gc", "last gc Eden Space",
									extractStaticMetric(currentLine, "last gc=") ) );
					temp_collection.addToCollection(
							createImb("Eden Space: max", "Max Eden Space",
									extractStaticMetric(currentLine, "max=") ) );
					break;
				case 2:
					temp_collection.addToCollection(
							createImb("Survivor Space: Peak", "Peak Survivor Space",
									extractStaticMetric(currentLine, "peak=") ) );
					temp_collection.addToCollection(
							createImb("Survivor Space: Current", "Current Survivor Space",
									extractStaticMetric(currentLine, "current=") ) );
					temp_collection.addToCollection(
							createImb("Survivor Space: last gc", "last gc Survivor Space",
									extractStaticMetric(currentLine, "last gc=") ) );
					temp_collection.addToCollection(
							createImb("Survivor Space: max", "Max Survivor Space",
									extractStaticMetric(currentLine, "max=") ) );
					break;
				case 3:
					temp_collection.addToCollection(
							createImb("CMS Old Gen: Peak", "Peak CMS Old Gen",
									extractStaticMetric(currentLine, "peak=") ) );
					temp_collection.addToCollection(
							createImb("CMS Old Gen: Current", "Current CMS Old Gen",
									extractStaticMetric(currentLine, "current=") ) );
					temp_collection.addToCollection(
							createImb("CMS Old Gen: last gc", "last gc CMS Old Gen",
									extractStaticMetric(currentLine, "last gc=") ) );
					temp_collection.addToCollection(
							createImb("CMS Old Gen: max", "Max CMS Old Gen",
									extractStaticMetric(currentLine, "max=") ) );
					break;
				case 4:
					temp_collection.addToCollection(
							createImb("CMS Perm Gen: Peak", "Peak CMS Perm Gen",
									extractStaticMetric(currentLine, "peak=") ) );
					temp_collection.addToCollection(
							createImb("CMS Perm Gen: Current", "Current CMS Perm Gen",
									extractStaticMetric(currentLine, "current=") ) );
					temp_collection.addToCollection(
							createImb("CMS Perm Gen: last gc", "last gc CMS Perm Gen",
									extractStaticMetric(currentLine, "last gc=") ) );
					temp_collection.addToCollection(
							createImb("CMS Perm Gen: max", "Max CMS Perm Gen",
									extractStaticMetric(currentLine, "max=") ) );
					break;
				case 5:
					temp_collection.addToCollection(
							createImb("Copy: #gc", "Copy #gc",
									extractStaticMetric(currentLine, "#gc=") ) );
					temp_collection.addToCollection(
							createImb("Copy: time", "Copy time",
									extractStaticMetric(currentLine, "time=") ) );
					break;
				case 6:
					temp_collection.addToCollection(
							createImb("ConcurrentMarkSweep: #gc", "ConcurrentMarkSweep #gc",
									extractStaticMetric(currentLine, "#gc=") ) );
					temp_collection.addToCollection(
							createImb("ConcurrentMarkSweep: time", "ConcurrentMarkSweep time",
									extractStaticMetric(currentLine, "time=") ) );
					break;
				case 8:

					temp_collection.addToCollection(
							createImb("Server Time", "Server Time",
									extractStaticMetric(currentLine, "Server Time:") ) );
					break;
				case 9:
					temp_collection.addToCollection(
							createImb("DB Time", "DB Time",
									extractStaticMetric(currentLine, "DB Time:") ) );
					break;
				case 10:
					temp_collection.addToCollection(
							createImb("Database", "Database",
									extractStaticMetric(currentLine, "Database:") ) );
					break;
				case 11:
					temp_collection.addToCollection(
							createImb("Database Version", "Database Version",
									extractStaticMetric(currentLine, "Database Version:") ) );
					break;
				case 13:
					System.out.println(i + "DB Major: " + currentLine);
					temp_collection.addToCollection(
							createImb("Database Major", "Database Major",
									extractStaticMetric(currentLine, "Database Major:") ) );
					break;
				case 14:
					temp_collection.addToCollection(
							createImb("Database Minor", "Database Minor",
									extractStaticMetric(currentLine, "Database Minor:") ) );
					break;
				case 15:
					temp_collection.addToCollection(
							createImb("Driver Name", "Driver Name",
									extractStaticMetric(currentLine, "Driver Name:") ) );
					break;
				case 16:
					temp_collection.addToCollection(
							createImb("Driver Version", "Driver Version",
									extractStaticMetric(currentLine, "Driver Version:") ) );
					break;
				case 17:
					temp_collection.addToCollection(
							createImb("Driver Major", "Driver Major",
									extractStaticMetric(currentLine, "Driver Major:") ) );
					break;
				case 18:
					temp_collection.addToCollection(
							createImb("Driver Minor", "Driver Minor",
									extractStaticMetric(currentLine, "Driver Minor:") ) );
					break;
				case 19:
					temp_collection.addToCollection(
							createImb("JDBC Major", "JDBC Major",
									extractStaticMetric(currentLine, "JDBC Major:") ) );
					break;
				case 20:
					temp_collection.addToCollection(
							createImb("JDBC Minor", "JDBC Minor",
									extractStaticMetric(currentLine, "JDBC Minor:") ) );
					break;
				case 21:
					temp_collection.addToCollection(
							createImb("Database user", "Database user",
									extractStaticMetric(currentLine, "Database user:") ) );
					break;
				case 36:
					temp_collection.addToCollection(
							createImb("DB Average Response Time", "DB Average Response Time",
									extractStaticMetric(currentLine, "Average response time:") ) );
					break;
				default:
					break;
			}
		}
		br.close();
		return temp_collection;
	}
	
	public MetricsCollection getDynamicMetrics() throws FileNotFoundException, IOException, Exception{
		MetricsCollection tempCollection = new MetricsCollection("Dynamic Metrics from '" +
			metricsFilePath + "'");
		Properties singleVals = new Properties();
		String singleValPropFile = "C:\\Users\\bryan\\IdeaProjects\\Integrity Performance Tool\\" +
				"Properties\\SingleValueDynamicMetricList.properties";
		InputStream singleValPropInput = new FileInputStream(singleValPropFile);
		singleVals.load(singleValPropInput);
		//singleVals.load(getClass().getClassLoader().getResourceAsStream(singleValPropInput));

	//	Properties multiVals = new Properties();
	//	String multiValPropFile = "C:\\Users\\bryan\\IdeaProjects\\Integrity Performance Tool\\" +
	//			"Properties\\MultiValueDynamicMetricList.properties;";
	//	InputStream multiValPropInput = new FileInputStream(multiValPropFile);
	//	multiVals.load(multiValPropInput);
		//multiVals.load(getClass().getClassLoader().getResourceAsStream(multiValPropInput));

		Enumeration<?> e = singleVals.propertyNames();
		while (e.hasMoreElements()) {
			String metricKey = (String) e.nextElement();
			String metricName = singleVals.getProperty(metricKey);
			System.out.println(metricKey + ": " + metricName);
			String value = getDynamicMetricValue(metricName);
			tempCollection.addToCollection(
				createImb(metricKey, metricName, value));
		}

		return tempCollection;
	}
	
	public int getMapSize(){ return mapSize; }
	
	public String getMetricsFilePath(){ return metricsFilePath;}

	private String getValueAtLine(int arg_targetLineNum) throws FileNotFoundException, IOException{
		BufferedReader br = new BufferedReader( new FileReader(metricsFilePath));
		String tempStr = "NO VALUE";
		for(int i=0;i<arg_targetLineNum;i++){
			tempStr = br.readLine();
		}
		br.close();
		return tempStr;
	}
	
	private String getDynamicMetricValue(String grepfor) throws IOException {
		Path filepath = Paths.get(metricsFilePath);
        final byte[] toSearch = grepfor.getBytes(StandardCharsets.UTF_8);
        StringBuilder foundAtLine = new StringBuilder();
        int padding = 1; // need to scan 1 character ahead in case it is a word boundary.
        int lineCount = 1;
        int matches = 0;
        boolean inWord = false;
        boolean scanToLineEnd = false;
        try {
        	FileChannel channel = FileChannel.open(filepath, StandardOpenOption.READ);
            final long length = channel.size();
            int pos = 0;
            while (pos < length) {
                long remaining = length - pos;
                // int conversion is safe because of a safe MAPSIZE.. Assume a reaosnably sized tosearch.
                int tryMap = mapSize + toSearch.length + padding;
                int toMap = (int)Math.min(tryMap, remaining);
                // different limits depending on whether we are the last mapped segment.
                int limit = tryMap == toMap ? mapSize : (toMap - toSearch.length);
                MappedByteBuffer buffer = channel.map(MapMode.READ_ONLY, pos, toMap);
              //  System.out.println("Mapped from " + pos + " for " + toMap);
                pos += (tryMap == toMap) ? mapSize : toMap;
                for (int i = 0; i < limit; i++) {
                    final byte b = buffer.get(i);
                    if (scanToLineEnd) {
                        if (b == '\n') {
							scanToLineEnd = false;
                            inWord = false;
                            lineCount ++;
                        }
                    } else if (b == '\n') {
                        lineCount++;
                        inWord = false;
                    } else if (b == '\r' || b == ' ') {
                        inWord = false;
                    } else if (!inWord) {
                        if (wordMatch(buffer, i, toMap, toSearch)) {
                            matches++;
                            i += toSearch.length - 1;
                            if (foundAtLine.length() > 0) {
                                foundAtLine.append(", ");
                            }
                            foundAtLine.append(lineCount);
							scanToLineEnd = true;
                        } else {
                            inWord = true;
                        }
                    }
                }
            }
        } catch (Exception e){
        	//TODO: warn in logger
        }
/*
        int metricNameLine = Integer.parseInt(foundAtLine.toString());
		int valueLineStart = Integer.parseInt(foundAtLine.toString()) + 2;
		String str_values = getValueAtLine(valueLineStart);
		String str_metrics = getValueAtLine(metricNameLine);
		int startCol =




		System.out.println(metricNameLine + ", " + valueLine);

		for(int m=0;m<str_metrics.length;m++){
			System.out.println(str_metrics[m] + ", " + str_values[m]);
			if(str_metrics[m].trim().equals(grepfor)){
				return str_values[m].trim();
			}
		}
		return "METRIC NOT FOUND: " + grepfor;
		*/
		return "method doesn't work";
    }
	
	public void setMapSize(int arg_size){ mapSize = arg_size; }
	
	public void setMetricsFilePath(String arg_filePath){ metricsFilePath = arg_filePath;}

    private static boolean wordMatch(MappedByteBuffer buffer, int pos, int tomap, byte[] tosearch) {
        //assume at valid word start.
        for (int i = 0; i < tosearch.length; i++) {
            if (tosearch[i] != buffer.get(pos + i)) {
                return false;
            }
        }
        byte nxt = (pos + tosearch.length) == tomap ? (byte)' ' : buffer.get(pos + tosearch.length); 
        return nxt == ' ' || nxt == '\n' || nxt == '\r';
    }

}
