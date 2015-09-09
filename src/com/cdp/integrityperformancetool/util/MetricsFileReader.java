package com.cdp.integrityperformancetool.util;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import com.cdp.integrityperformancetool.MetricsCollection;

public class MetricsFileReader {

	private static int mapSize; // 4K - make this * 1024 to 4MB in a real system.
	private String metricsFilePath;
	private ArrayList<String> metricsToRetrieve;
	
	public MetricsFileReader(){
		mapSize = 1 * 1024;
		metricsFilePath = "";
		metricsToRetrieve = new ArrayList<String>();
	}

	public MetricsFileReader(String arg_metricsFilePath, ArrayList<String> arg_metricsToRetrieve){
		mapSize = 1 * 1024;
		metricsFilePath = arg_metricsFilePath;
		metricsToRetrieve = arg_metricsToRetrieve;
	}
	
	public MetricsFileReader(int arg_mapSize, String arg_metricsFilePath, ArrayList<String> arg_metricsToRetrieve){
		mapSize = arg_mapSize;
		metricsFilePath = arg_metricsFilePath;
		metricsToRetrieve = arg_metricsToRetrieve;
	}
	
	public void addMetric(String arg_metric){
		if(!metricsToRetrieve.contains(arg_metric)){
			metricsToRetrieve.add(arg_metric);
		}
	}
	
	public void addMetrics(ArrayList<String> arg_metrics){
		for(String m : arg_metrics){
			addMetric(m);
		}
	}
	
	public void clearMetricsToRetrieve(){
		metricsToRetrieve.clear();
	}
	
	public MetricsCollection executeMetricsRetrieval(){
		
	}
	
	public int getMapSize(){ return mapSize; }
	
	public String getMetricsFilePath(){ return metricsFilePath;}
	
	public ArrayList<String> getMetricsToRetrieve(){ return metricsToRetrieve;}
	
	public void removeMetric(String arg_metric){
		if(metricsToRetrieve.contains(arg_metric)){
			metricsToRetrieve.remove(arg_metric);
		}
	}
	
	public void removeMetrics(ArrayList<String> arg_metrics){
		for(String m : arg_metrics){
			removeMetric(m);
		}
	}
	
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
