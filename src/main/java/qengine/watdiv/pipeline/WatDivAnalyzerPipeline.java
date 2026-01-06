package qengine.watdiv.pipeline;

import java.io.IOException;
import java.util.List;

import qengine.watdiv.extract.WatDivExtract;
import qengine.watdiv.load.WatDivLoad;
import qengine.watdiv.load.WatDivLoadStatistics;
import qengine.watdiv.transform.WatDivTransformPutInBins;
import qengine.watdiv.transform.WatDivTransformRemoveDuplicates;
import qengine.watdiv.transform.WatDivTransformStatistics;

public class WatDivAnalyzerPipeline {
	private WatDivExtract watDivExtract;
	private WatDivTransformRemoveDuplicates watDivRemoveDuplicates;
	private WatDivTransformPutInBins watDivTransform;
	private WatDivTransformStatistics watDivStatistics;
	private WatDivLoadStatistics watDivLoadStatistics;
	private WatDivLoad watDivLoad;
	
	private String input_rdfTriplesFilePath;
	private String input_starQueriesFilePath;
	private String output_loadStarQueriesInBinsFolder;
	
	private List<Integer> numberOfAnswersBins;

	public WatDivAnalyzerPipeline(
			String input_rdfTriplesFilePath, 
			String input_starQueriesFilePath, 
			String output_loadStarQueriesInBinsFolder,
			List<Integer> numberOfAnswersBins) {
		this.input_rdfTriplesFilePath = input_rdfTriplesFilePath;
		this.input_starQueriesFilePath = input_starQueriesFilePath;
		this.output_loadStarQueriesInBinsFolder = output_loadStarQueriesInBinsFolder;
		
		this.numberOfAnswersBins = numberOfAnswersBins;
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
		
		System.out.println("=====  Put in bins  =====");
	
		this.watDivTransform = new WatDivTransformPutInBins(
			this.watDivRemoveDuplicates.getRDFTriplesResults(),
			this.watDivRemoveDuplicates.getStarQueriesResults(),
			this.numberOfAnswersBins
		);
		this.watDivTransform.putStarQueriesInBins();
		
		System.out.println("=====  Compute statistics  =====");
	
		this.watDivStatistics = new WatDivTransformStatistics(
			this.watDivTransform.getResults()
		);
		this.watDivStatistics.computeAll();
		
		System.out.println("=====  Export statistics  =====");
		this.watDivLoadStatistics = new WatDivLoadStatistics(
			this.watDivStatistics.getResultsNumberOfElementsPerAnswersBin(),
			"data/output/statistics/nb-answers-bin-1M.json"
		);
		this.watDivLoadStatistics.exportAll();
		
		System.out.println("=====  Load  =====");

		this.watDivLoad = new WatDivLoad(
			this.watDivTransform.getResults(),
			"data/output/" + output_loadStarQueriesInBinsFolder
		);
		this.watDivLoad.loadAllStarQueriesInBins();	
	}
}
