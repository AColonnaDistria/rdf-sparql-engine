package qengine.watdiv.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import qengine.model.StarQuery;

public class WatDivTransformStatistics {
	private HashMap<String, ArrayList<StarQuery>> input;
	
	private HashMap<String, Integer> results_numberOfElementsPerAnswersBin;
	
	public WatDivTransformStatistics(
			HashMap<String, ArrayList<StarQuery>> input) {
		this.input = input;
		
		this.results_numberOfElementsPerAnswersBin = new HashMap<>();
	}
	
	public HashMap<String, Integer> getResultsNumberOfElementsPerAnswersBin() {
		return this.results_numberOfElementsPerAnswersBin;
	}

	public void computeAll() {
		this.computeNumberOfAnswers();
	}
		
	public void computeNumberOfAnswers() {
		results_numberOfElementsPerAnswersBin.clear();
		
		for (Map.Entry<String, ArrayList<StarQuery> > entry : input.entrySet()) {
			String answerBin = entry.getKey();
			ArrayList<StarQuery> answerBinValues = entry.getValue();

			results_numberOfElementsPerAnswersBin.put(
				answerBin,
				answerBinValues.size()
			);
		}
	}
}
