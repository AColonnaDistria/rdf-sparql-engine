package qengine.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PerformanceStatistics {
	private HashMap<String, ArrayList<Double>> performanceResults;

	private HashMap<String, Double> meanbyBucket= new HashMap<>();
	private HashMap<String, Double> stdbyBucket= new HashMap<>();
	
	public PerformanceStatistics(HashMap<String, ArrayList<Double>> performanceResults) {
		this.performanceResults = performanceResults;
	}

	public Map<String, Double> getMeans() {
		return Collections.unmodifiableMap(this.meanbyBucket);
	}

	public Map<String, Double> getStds() {
		return Collections.unmodifiableMap(this.stdbyBucket);
	}
	
	public double getMeanByBucket(String bucket) {
		return this.meanbyBucket.get(bucket);
	}

	public double getStdByBucket(String bucket) {
		return this.stdbyBucket.get(bucket);
	}
	
	public Iterator<String> getBuckets() {
		return performanceResults.keySet().iterator();
	}
	
	public void computeStatistics() {
		this.meanbyBucket.clear();
		this.stdbyBucket.clear();
		
		for (Map.Entry<String, ArrayList<Double>> entry : performanceResults.entrySet()) {
			String bucket = entry.getKey();
			ArrayList<Double> results = entry.getValue();
			
			double mean = results.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
			double std = Math.sqrt(results.stream().mapToDouble(x -> (Math.pow(x - mean, 2))).sum() / (double) (results.stream().count() - 1));
			
			this.meanbyBucket.put(bucket, mean);
			this.stdbyBucket.put(bucket, std);
		}
	}
}
