package qengine.watdiv.pipeline;

import java.io.IOException;
import java.util.List;

import qengine.watdiv.extract.WatDivExtract;
import qengine.watdiv.load.WatDivLoad;
import qengine.watdiv.load.WatDivLoadStatistics;
import qengine.watdiv.transform.WatDivTransformPutInBins;
import qengine.watdiv.transform.WatDivTransformRemoveDuplicates;
import qengine.watdiv.transform.WatDivTransformSample;
import qengine.watdiv.transform.WatDivTransformStatistics;

public class WatDivSamplePipeline {
	private WatDivExtract watDivExtract;
	private WatDivTransformRemoveDuplicates watDivRemoveDuplicates;
	private WatDivTransformPutInBins watDivTransformPutInBins;
	private WatDivTransformSample watDivTransformSample;
	private WatDivTransformStatistics watDivStatistics;
	private WatDivLoadStatistics watDivLoadStatistics;
	
	private WatDivLoad watDivLoad;
	
	private String input_rdfTriplesFilePath;
	private String input_starQueriesFilePath;
	private String output_loadStarQueriesInBinsFolder;
	
	private List<Integer> numberOfAnswersBins;

	private int sampleSize;
	
	public WatDivSamplePipeline(
			String input_rdfTriplesFilePath, 
			String input_starQueriesFilePath, 
			String output_loadStarQueriesInBinsFolder,
			List<Integer> numberOfAnswersBins,
			int sampleSize) {
		this.input_rdfTriplesFilePath = input_rdfTriplesFilePath;
		this.input_starQueriesFilePath = input_starQueriesFilePath;
		this.output_loadStarQueriesInBinsFolder = output_loadStarQueriesInBinsFolder;
		
		this.numberOfAnswersBins = numberOfAnswersBins;
		
		this.sampleSize = sampleSize;
	}
	
	public void execute() throws IOException {
		System.out.println("=====  Extract  =====");
		
		this.watDivExtract = new WatDivExtract(
			input_rdfTriplesFilePath, 
			input_starQueriesFilePath
		); 
		this.watDivExtract.extractAll();

		System.out.println("=====  Remove Duplicates  =====");
		
		this.watDivRemoveDuplicates = new WatDivTransformRemoveDuplicates(
				this.watDivExtract.getRDFTriples(),
				this.watDivExtract.getStarQueries()
		); 
		this.watDivRemoveDuplicates.removeDuplicates();
		
		System.out.println("=====  Transform (Put in bins)  =====");
	
		this.watDivTransformPutInBins = new WatDivTransformPutInBins(
			this.watDivRemoveDuplicates.getRDFTriplesResults(),
			this.watDivRemoveDuplicates.getStarQueriesResults(),
			this.numberOfAnswersBins
		);
		this.watDivTransformPutInBins.putStarQueriesInBins();

		System.out.println("=====  Transform (Sample)  =====");
	
		this.watDivTransformSample = new WatDivTransformSample(
				this.watDivTransformPutInBins.getResults(),
				this.sampleSize
		);
		
		this.watDivTransformSample.sample();

		System.out.println("=====  Compute statistics  =====");
	
		this.watDivStatistics = new WatDivTransformStatistics(
			this.watDivTransformSample.getResults()
		);
		this.watDivStatistics.computeAll();
		
		System.out.println("=====  Export statistics  =====");
		this.watDivLoadStatistics = new WatDivLoadStatistics(
			this.watDivStatistics.getResultsNumberOfElementsPerAnswersBin(),
			"data/output/statistics/nb-sample-answers-bin-1M.json"
		);
		this.watDivLoadStatistics.exportAll();
		
		System.out.println("=====  Load  =====");

		this.watDivLoad = new WatDivLoad(
			this.watDivTransformSample.getResults(),
			"data/output/" + output_loadStarQueriesInBinsFolder
		);
		this.watDivLoad.loadAllStarQueriesInBins();	
	}
}
