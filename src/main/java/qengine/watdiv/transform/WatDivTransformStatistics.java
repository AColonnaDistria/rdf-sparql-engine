/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


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
