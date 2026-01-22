/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package qengine.watdiv.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mapdb.Fun;

import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.storage.Oracle;
import fr.boreal.model.logicalElements.api.Substitution;

public class WatDivTransformPutInBins {
	private Collection<RDFTriple> rdfTriples;
	private Collection<StarQuery> starQueries;
	
	// (number_of_answers_bin)
	private HashMap<String, ArrayList<StarQuery>> results;
	
	private List<Integer> numberOfAnswersBinsPoints;
	
	private Oracle oracle; // Oracle system
	
	private boolean log;
	
	public WatDivTransformPutInBins(
			Collection<RDFTriple> rdfTriples, 
			Collection<StarQuery> starQueries, 
			List<Integer> numberOfAnswersBins) {
		this.rdfTriples = rdfTriples;
		this.starQueries = starQueries;
		
		this.numberOfAnswersBinsPoints = numberOfAnswersBins;
		
		this.log = false;
		this.results = new HashMap<>();
		
		this.loadAllTriples();
	}

	public WatDivTransformPutInBins(
			Collection<RDFTriple> rdfTriples, 
			Collection<StarQuery> starQueries, 
			List<Integer> numberOfAnswersBins, 
			boolean log) {
		this.rdfTriples = rdfTriples;
		this.starQueries = starQueries;
		
		this.numberOfAnswersBinsPoints = numberOfAnswersBins;
		
		this.log = log;
		this.results = new HashMap<>();

		this.loadAllTriples();
	}
	
	public void putStarQueriesInBins() {
		// by number of answers
		for (StarQuery starQuery : this.starQueries) {
			int number_of_answers = (int) this.countAnswers(this.oracle.match(starQuery));
			
			String number_of_answers_bin = this.findNumberOfAnswersBin(number_of_answers);
			
			if (!this.results.containsKey(number_of_answers_bin)) {
				this.results.put(number_of_answers_bin, new ArrayList<>());
			}
			
			this.results.get(number_of_answers_bin).add(starQuery);
		}
	}
	
	public HashMap<String, ArrayList<StarQuery>> getResults() {
		return this.results;
	}

	private void loadAllTriples() {
		this.oracle = new Oracle();
		
		// load all triples
		for (RDFTriple rdfTriple : this.rdfTriples) {
			oracle.add(rdfTriple);
		}
	}

	private String findBin(int value, List<Integer> bins) {
		for (int index = 0; index < bins.size() - 1; ++index) {
			if (value <= bins.get(index + 1)) {
				return String.format("%d-%d", bins.get(index), bins.get(index + 1));
			}
		}
		
		return String.format("%d+", bins.getLast());
	}
	
	private String findNumberOfAnswersBin(int number_of_answers_value) {
		return findBin(number_of_answers_value, numberOfAnswersBinsPoints);
	}
	
	private long countAnswers(Iterator<Substitution> subs) {
		// seems inefficient
		
		long count = 0;
		while (subs.hasNext()) {
			count += 1;
			subs.next();
		}
		
		return count;
	}
	
}
