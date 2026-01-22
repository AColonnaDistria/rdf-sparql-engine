/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package qengine.benchmark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.boreal.model.logicalElements.api.Substitution;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.storage.RDFHexaStore;
import qengine.watdiv.extract.WatDivExtract;
import qengine.watdiv.extract.WatDivExtractStarQueryBins;

import qengine.storage.RDFStorage;;

public class TwoSquaredTest {

	public static void main(String[] args) throws IOException {
		RDFStorage store = new RDFHexaStore();
		
		// Load triplets
		
		String RDF_TRIPLES_INPUT_DIR = "data/input/rdf-triples/data_2M.nt";
		String STAR_QUERIES_INPUT_DIR = "data/input/star-queries-sample/";

	
		WatDivExtract watDivExtractTriples = new WatDivExtract(
				RDF_TRIPLES_INPUT_DIR,
				"",
				true
		);
		
		watDivExtractTriples.extractRDFData();
		Collection<RDFTriple> rdfTriples = watDivExtractTriples.getRDFTriples();
		
		for (RDFTriple triple : rdfTriples) {
			store.add(triple);
		}

		WatDivExtractStarQueryBins watDivExtract = new WatDivExtractStarQueryBins(
				STAR_QUERIES_INPUT_DIR
		);
		
		watDivExtract.extractStarQueries();
		HashMap<String, ArrayList<StarQuery>> map = watDivExtract.getStarQueries();
		
		ArrayList<StarQuery> starQueries = new ArrayList<>();
		
		for (Map.Entry<String, ArrayList<StarQuery>> entry : map.entrySet()) {
			starQueries.addAll(entry.getValue());
		}
		
		Collections.shuffle(starQueries);
		
		// test performance
		long start = System.nanoTime();
		
		for (StarQuery starQuery : starQueries) {
            Iterator<Substitution> subs = store.match(starQuery);
            
            while(subs.hasNext()) subs.next();
		}
		
		long end = System.nanoTime();
		long temps = end - start;
		
		System.out.println(temps);
	}

}
