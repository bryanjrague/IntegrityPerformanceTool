package com.cdp.integrityperformancetool.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
		try{
			tempImb.setValue(Long.valueOf(arg_value)); //attempt to set long
		} catch(NumberFormatException nfe){
			try{
				tempImb.setValue(Float.valueOf(arg_value)); //attempt to set float
			} catch(NumberFormatException nfe2){
				try{
					tempImb.setValue(arg_value); //default and set to string
				}catch(Exception e){
					//TODO: warn in logger - error 
					tempImb.setValue("ERROR - NO DATA");
				}
			}
		}
		return tempImb;
	}
	
	public MetricsCollection executeMetricsRetrieval(){
		MetricsCollection staticCollection = new MetricsCollection("Static Metrics from file '" + metricsFilePath + "'");
		MetricsCollection dynamicCollection = new MetricsCollection("Dynamic Metrics from file '" + metricsFilePath + "'");
		MetricsCollection finalCollection = new MetricsCollection("All Metrics from file '" + metricsFilePath + "'");
		try{
			staticCollection = getStaticMetrics();
			//dynamicCollection = getDynamicMetrics();
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
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 17:
				case 18:
				case 19:
				case 20:
				case 21:
				case 36:
				default:
					break;
			}
		}
		br.close();
		return temp_collection;
	}
	
	//public MetricsCollection getDynamicMetrics(Path arg_filePath) throws FileNotFoundException, IOException, Exception{
	//	BufferedReader br = new BufferedReader( new FileReader(metricsFilePath));
		
	//	br.close();
		
	//}
	
	public int getMapSize(){ return mapSize; }
	
	public String getMetricsFilePath(){ return metricsFilePath;}
	
	private String searchForMetric(String grepfor, Path path) throws IOException {
        final byte[] tosearch = grepfor.getBytes(StandardCharsets.UTF_8);
        StringBuilder report = new StringBuilder();
        int padding = 1; // need to scan 1 character ahead in case it is a word boundary.
        int linecount = 1;
        int matches = 0;
        boolean inword = false;
        boolean scantolineend = false;
        try {
        	FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
            final long length = channel.size();
            int pos = 0;
            while (pos < length) {
                long remaining = length - pos;
                // int conversion is safe because of a safe MAPSIZE.. Assume a reaosnably sized tosearch.
                int trymap = mapSize + tosearch.length + padding;
                int tomap = (int)Math.min(trymap, remaining);
                // different limits depending on whether we are the last mapped segment.
                int limit = trymap == tomap ? mapSize : (tomap - tosearch.length);
                MappedByteBuffer buffer = channel.map(MapMode.READ_ONLY, pos, tomap);
                System.out.println("Mapped from " + pos + " for " + tomap);
                pos += (trymap == tomap) ? mapSize : tomap;
                for (int i = 0; i < limit; i++) {
                    final byte b = buffer.get(i);
                    if (scantolineend) {
                        if (b == '\n') {
                            scantolineend = false;
                            inword = false;
                            linecount ++;
                        }
                    } else if (b == '\n') {
                        linecount++;
                        inword = false;
                    } else if (b == '\r' || b == ' ') {
                        inword = false;
                    } else if (!inword) {
                        if (wordMatch(buffer, i, tomap, tosearch)) {
                            matches++;
                            i += tosearch.length - 1;
                            if (report.length() > 0) {
                                report.append(", ");
                            }
                            report.append(linecount);
                            scantolineend = true;
                        } else {
                            inword = true;
                        }
                    }
                }
            }
        } catch (Exception e){
        	//?
        }
        return "Times found at--" + matches + "\nWord found at--" + report;
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
