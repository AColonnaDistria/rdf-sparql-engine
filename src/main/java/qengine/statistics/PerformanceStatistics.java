/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

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
