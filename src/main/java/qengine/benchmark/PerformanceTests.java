/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package qengine.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import fr.boreal.model.logicalElements.api.Substitution;
import fr.boreal.model.query.api.Query;
import qengine.concurrent.storage.ConcurrentGiantTable;
import qengine.concurrent.storage.ConcurrentRDFHexaStore;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.statistics.PerformanceStatistics;
import qengine.storage.GiantTable;
import qengine.storage.Oracle;
import qengine.storage.RDFHexaStore;
import qengine.storage.RDFStorage;
import qengine.watdiv.extract.WatDivExtract;
import qengine.watdiv.extract.WatDivExtractStarQueryBins;

public class PerformanceTests {
	private static final String INPUT_DIR = "data/input/";
	private static final String OUTPUT_DIR = "data/output/";

	private static final String RDF_TRIPLES_INPUT_DIR = "data/input/rdf-triples/data_500k.nt";
	private static final String STAR_QUERIES_INPUT_DIR = "data/input/star-queries-sample/";

	private static final String EXPORT_PERFORMANCE_STATISTICS_RAW = "data/output/statistics/performance-statistics-raw";
	private static final String EXPORT_PERFORMANCE_STATISTICS = "data/output/statistics/performance-statistics";
	
	private static final String EXPORT_LOADING_TIME_STATISTICS = "data/output/statistics/loading-time-statistics.json";

	private static final String EXPORT_CORRECTION_FILEPATH = "data/output/statistics/correction-statistics.json";
	private static final String EXPORT_COMPLETUDE_FILEPATH = "data/output/statistics/completude-statistics.json";
	
	
	static final int BATCH_SIZE = 20;
	
	private RDFHexaStore hexastore;
	private GiantTable giantTable;
	private Oracle oracle;

	private ConcurrentRDFHexaStore concurrentHexastore;
	private ConcurrentGiantTable concurrentGiantTable;
	
	private HashMap<String, ArrayList<StarQuery>> starQueriesInBins;
	private HashMap<String, ArrayList<Double>> performanceResults;

	private HashMap<String, Double> loadingTimeResults;
	
	private HashMap<String, Double> correctionResults;
	private HashMap<String, Double> completudeResults;
	
	private ArrayList<String> numberOfAnswersBins;
	
	private static final boolean TEST_HEXASTORE = true;
	private static final boolean TEST_CONCURRENT_HEXASTORE = true;
	
	private static final boolean TEST_CONCURRENT_GIANT_TABLE = true;
	private static final boolean TEST_GIANT_TABLE = true;
	
	public PerformanceTests() {
		this.hexastore = new RDFHexaStore();
		this.giantTable = new GiantTable();
		this.oracle = new Oracle();
		
		this.concurrentHexastore = new ConcurrentRDFHexaStore();
		this.concurrentGiantTable = new ConcurrentGiantTable();
		
		this.performanceResults = new HashMap<>();
		this.loadingTimeResults = new HashMap<>();
		
		this.completudeResults = new HashMap<>();
		this.correctionResults = new HashMap<>();
	}
	
	public static void main(String[] args) {
		try {
			PerformanceTests performanceTests = new PerformanceTests();
			
			performanceTests.loadData();
			performanceTests.runTests();
			
			//performanceTests.runTestsCorrectionCompletude();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void runLoadingDataTest(String name, Collection<RDFTriple> rdfTriples, RDFStorage store) throws IOException {
		long start = System.nanoTime();
		
		store.addAll(rdfTriples);
		
		long end = System.nanoTime();
		Double time = (double)(end - start);
		
		loadingTimeResults.put(name, time);
		
		this.exportLoadingTimeResults(name);
		
		// export Results
	}
	
	public void loadData() {
		try {
			WatDivExtract watDivExtract = new WatDivExtract(
					RDF_TRIPLES_INPUT_DIR,
					"",
					true
			);
			
			watDivExtract.extractRDFData();
			
			Collection<RDFTriple> rdfTriples = watDivExtract.getRDFTriples();

			System.out.println("===== LOAD HEXASTORE =====");
			if (TEST_HEXASTORE) {
				this.runLoadingDataTest("HEXASTORE", rdfTriples, this.hexastore);
			}

			System.out.println("===== LOAD GIANT TABLE =====");
			if (TEST_GIANT_TABLE) {
				this.runLoadingDataTest("GIANT-TABLE", rdfTriples, this.giantTable);
			}

			System.out.println("===== LOAD CONCURRENT HEXASTORE =====");
			if (TEST_CONCURRENT_HEXASTORE) {
				this.runLoadingDataTest("CONCUR-HEXASTORE", rdfTriples, this.concurrentHexastore);
			}

			System.out.println("===== LOAD CONCURRENT GIANT TABLE =====");
			if (TEST_CONCURRENT_GIANT_TABLE) {
				this.runLoadingDataTest("CONCUR-GIANT-TABLE", rdfTriples, this.concurrentGiantTable);
			}

			System.out.println("===== LOAD ORACLE =====");
			this.runLoadingDataTest("ORACLE", rdfTriples, this.oracle);
			
			WatDivExtractStarQueryBins watDivExtractStarQueryBins = new WatDivExtractStarQueryBins(STAR_QUERIES_INPUT_DIR);
			watDivExtractStarQueryBins.extractStarQueries();
			
			this.starQueriesInBins = watDivExtractStarQueryBins.getStarQueries();
			this.numberOfAnswersBins = watDivExtractStarQueryBins.getNumberOfAnswersBin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runTests() {
		try {
			if (TEST_HEXASTORE) {
				System.out.println("==== TEST PERFORMANCE HEXASTORE ====");
				
				this.runResponseTimeTest("HEXASTORE", this.hexastore, 100);
			}

			if (TEST_GIANT_TABLE) {
				System.out.println("==== TEST PERFORMANCE GIANT TABLE ====");
				
				this.runResponseTimeTest("GIANT-TABLE", this.giantTable, 100);
			}

			if (TEST_CONCURRENT_HEXASTORE) {
				System.out.println("==== TEST PERFORMANCE CONCURRENT HEXASTORE ====");
				
				this.runResponseTimeTest("CONCUR-HEXASTORE", this.concurrentHexastore, 100);
			}
			
			if (TEST_CONCURRENT_GIANT_TABLE) {
				System.out.println("==== TEST PERFORMANCE CONCURRENT GIANT TABLE ====");
				
				this.runResponseTimeTest("CONCUR-GIANT-TABLE", this.concurrentGiantTable, 100);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void runTestsCorrectionCompletude() {
		try {
			if (TEST_HEXASTORE) {
				System.out.println("==== TEST CORRECTION/COMPLETUDE HEXASTORE ====");
				
				this.runTestCorrectionCompletude("HEXASTORE", this.hexastore);
			}

			if (TEST_GIANT_TABLE) {
				System.out.println("==== TEST CORRECTION/COMPLETUDE GIANT TABLE ====");
				
				this.runTestCorrectionCompletude("GIANT-TABLE", this.giantTable);
			}

			if (TEST_CONCURRENT_HEXASTORE) {
				System.out.println("==== TEST CORRECTION/COMPLETUDE CONCURRENT HEXASTORE ====");
				
				this.runTestCorrectionCompletude("CONCUR-HEXASTORE", this.concurrentHexastore);
			}
			
			if (TEST_CONCURRENT_GIANT_TABLE) {
				System.out.println("==== TEST CORRECTION/COMPLETUDE CONCURRENT GIANT TABLE ====");
				
				this.runTestCorrectionCompletude("CONCUR-GIANT-TABLE", this.concurrentGiantTable);
			}
			
			this.exportCorrection();
			this.exportCompletude();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void runTestCorrectionCompletude(String name, RDFStorage store) {
		double correction = 0.0;
		double completude = 0.0;
		
		double total = 0.0;
		
		for (Map.Entry<String, ArrayList<StarQuery>> entry : this.starQueriesInBins.entrySet()) {
			for (StarQuery starQuery : entry.getValue()) {
                Iterator<Substitution> subs_store = store.match(starQuery);
                Iterator<Substitution> subs_oracle = oracle.match(starQuery);
                
                Set<Substitution> oracleSet = new HashSet<>();
                subs_oracle.forEachRemaining(oracleSet::add);

                Set<Substitution> storeSet = new HashSet<>();
                subs_store.forEachRemaining(storeSet::add);
                
                Set<Substitution> intersection = new HashSet<>(oracleSet); 
                intersection.retainAll(storeSet);
                
                if (intersection.size() != storeSet.size()) {
                	System.out.println("nope");
                }
                
                if (intersection.size() != oracleSet.size()) {
                	System.out.println("nope");
                }
                
                if (!storeSet.isEmpty() && !oracleSet.isEmpty()) {
                    correction += (double) intersection.size() / storeSet.size();
                    completude += (double) intersection.size() / oracleSet.size();
                    total += 1.0;
                }
			}
		}
		
		double meanCorrection = correction / total;
		double meanCompletude = completude / total;
		
		correctionResults.put(name, meanCorrection);
		completudeResults.put(name, meanCompletude);
	}
	
	private void warmup(RDFStorage store) {
		// Warm up
		Collections.shuffle(this.numberOfAnswersBins);
		for (String bin : this.numberOfAnswersBins) {
	        ArrayList<StarQuery> queries = this.starQueriesInBins.get(bin);
			Collections.shuffle(queries);
			
	        for (StarQuery q : queries) {
	            Iterator<Substitution> subs = store.match(q);
	            while(subs.hasNext()) subs.next();
	        }
	    }

	}
	
	private void runResponseTimeTest(String name, RDFStorage store, int REPEAT) throws IOException {
		this.performanceResults.clear();
		
		this.warmup(store);
		
		for (String answersBin : this.numberOfAnswersBins) {
			ArrayList<StarQuery> starQueriesBin = this.starQueriesInBins.get(answersBin);
			
			if (!performanceResults.containsKey(answersBin)) {
				performanceResults.put(answersBin, new ArrayList<>());
			}
			
			int batch_size = 20;

			for (int batch_start = 0; batch_start < starQueriesBin.size(); batch_start += batch_size) {
				List<StarQuery> batch = starQueriesBin.subList(batch_start, Math.min(batch_start + batch_size, starQueriesBin.size()));

				long start = System.nanoTime();

				for (int i = 0; i < REPEAT; ++i) {
					for (StarQuery starQuery : batch) {
						Iterator<Substitution> subs = store.match(starQuery);
						while (subs.hasNext()) subs.next();
					}

				}
				
				long stop = System.nanoTime();
				long timeNanoseconds = stop - start;

				performanceResults.get(answersBin).add((double)timeNanoseconds / (double)REPEAT);
			}
		}

		this.exportResults(name);
	}

	public void exportCorrection() throws IOException {
		String exportFilePathStats = EXPORT_CORRECTION_FILEPATH;

		JsonObjectBuilder mainBuilder = Json.createObjectBuilder();

	    for (Map.Entry<String, Double> entry : this.correctionResults.entrySet()) {
	        mainBuilder.add(entry.getKey(), entry.getValue());
	    }

	    try (FileWriter writer = new FileWriter(exportFilePathStats)) {
	        writer.write(mainBuilder.build().toString());
	    }
	}

	public void exportCompletude() throws IOException {
		String exportFilePathStats = EXPORT_COMPLETUDE_FILEPATH;

		JsonObjectBuilder mainBuilder = Json.createObjectBuilder();

	    for (Map.Entry<String, Double> entry : this.completudeResults.entrySet()) {
	        mainBuilder.add(entry.getKey(), entry.getValue());
	    }

	    try (FileWriter writer = new FileWriter(exportFilePathStats)) {
	        writer.write(mainBuilder.build().toString());
	    }
	}
	
	public void exportLoadingTimeResults(String name) throws IOException {
		String exportFilePathStats = EXPORT_LOADING_TIME_STATISTICS;

		JsonObjectBuilder mainBuilder = Json.createObjectBuilder();

	    for (Map.Entry<String, Double> entry : this.loadingTimeResults.entrySet()) {
	        mainBuilder.add(entry.getKey(), entry.getValue());
	    }

	    try (FileWriter writer = new FileWriter(exportFilePathStats)) {
	        writer.write(mainBuilder.build().toString());
	    }
	}
		
	public void exportResults(String name) throws IOException {
		String exportFilePathRaw = EXPORT_PERFORMANCE_STATISTICS_RAW + "-" + name + ".json";
		String exportFilePathStats = EXPORT_PERFORMANCE_STATISTICS + "-" + name + ".json";
		
		JsonObjectBuilder mainBuilder = Json.createObjectBuilder();

	    for (Map.Entry<String, ArrayList<Double>> entry : this.performanceResults.entrySet()) {
	        String answersBin = entry.getKey();
	        ArrayList<Double> answerBinsValues = entry.getValue();

	        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
	        for (Double time : entry.getValue()) {
	            arrayBuilder.add(time);
	        }

	        mainBuilder.add(answersBin, arrayBuilder);
	    }

	    try (FileWriter writer = new FileWriter(exportFilePathRaw)) {
	        writer.write(mainBuilder.build().toString());
	    }
	    

		PerformanceStatistics stats = new PerformanceStatistics(
			this.performanceResults
		);
		stats.computeStatistics();
		

		JsonObjectBuilder mainBuilder2 = Json.createObjectBuilder();

		Map<String, Double> means = stats.getMeans();
		Map<String, Double> stds = stats.getStds();

		for (String answerBin : this.numberOfAnswersBins) {
			JsonObjectBuilder innerBuilder = Json.createObjectBuilder();

	        innerBuilder.add("mean", means.get(answerBin));
	        innerBuilder.add("std", stds.get(answerBin));
	        
	        mainBuilder2.add(answerBin, innerBuilder);
		}
		
	    try (FileWriter writer = new FileWriter(exportFilePathStats)) {
	        writer.write(mainBuilder2.build().toString());
	    }
	}
}
