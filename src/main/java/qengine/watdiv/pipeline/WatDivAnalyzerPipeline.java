/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


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
