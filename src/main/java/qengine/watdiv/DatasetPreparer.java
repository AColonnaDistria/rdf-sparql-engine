package qengine.watdiv;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import qengine.watdiv.pipeline.WatDivAnalyzerPipeline;
import qengine.watdiv.pipeline.WatDivSamplePipeline;

public class DatasetPreparer {
	private static final String DEFAULT_INPUT_DIR = "data/input/";
	private static final String DEFAULT_INPUT_RDF_TRIPLES = "rdf-triples/data_2M.nt";
	private static final String DEFAULT_INPUT_STAR_QUERIES = "star-queries";

	private static final String DEFAULT_OUTPUT_DIR = "data/output/";
	
	private static final String DEFAULT_OUTPUT_STAR_QUERIES = "star-queries";
	private static final String DEFAULT_OUTPUT_STAR_QUERIES_SAMPLE = "star-queries-sample-1M";
	
	private static final List<Integer> DEFAULT_NUMBER_OF_ANSWERS_BINS = List.of(0, 1, 20, 100);
	
	private WatDivAnalyzerPipeline analyzerPipeline;
	private WatDivSamplePipeline samplePipeline;
	
	private String inputDirPath;
	private String outputDirPath;

	private String inputRdfTriplesPath;
	private String inputStarQueriesPath;

	private String outputStarQueriesPath;

	public static void main(String[] args) {
		try {
			DatasetPreparer preparer = new DatasetPreparer();
			
			preparer.run();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Default
	public DatasetPreparer() {
		this(
			DEFAULT_INPUT_DIR, 
			DEFAULT_INPUT_RDF_TRIPLES,
			DEFAULT_INPUT_STAR_QUERIES,
			DEFAULT_OUTPUT_DIR,
			DEFAULT_OUTPUT_STAR_QUERIES,
			DEFAULT_NUMBER_OF_ANSWERS_BINS
		);
	}

	public DatasetPreparer(
		String inputDir,
		String inputRdfTriples, 
		String inputStarQueries,
		String outputDir, 
		String outputStarQueries,
		List<Integer> numberOfAnswersBins) {
		
		this.inputDirPath = inputDir;
		// concatenation of paths
		this.inputRdfTriplesPath = Paths.get(this.inputDirPath, inputRdfTriples).toString();
		this.inputStarQueriesPath = Paths.get(this.inputDirPath, inputStarQueries).toString();
		
		this.outputDirPath = outputDir;
		this.outputStarQueriesPath = Paths.get(this.outputDirPath, outputStarQueries).toString();
		
		this.analyzerPipeline = new WatDivAnalyzerPipeline(
			this.inputRdfTriplesPath, 
			this.inputStarQueriesPath,
			this.outputStarQueriesPath,
			numberOfAnswersBins
		);

		this.samplePipeline = new WatDivSamplePipeline(
			this.inputRdfTriplesPath, 
			this.inputStarQueriesPath,
			DEFAULT_OUTPUT_STAR_QUERIES_SAMPLE,
			numberOfAnswersBins,
			100
		);
	}
	
	public void run() throws IOException {
		System.out.println("===== Analyzer =====");
		this.analyzerPipeline.execute();

		System.out.println("===== Sample =====");
		this.samplePipeline.execute();
	}
}