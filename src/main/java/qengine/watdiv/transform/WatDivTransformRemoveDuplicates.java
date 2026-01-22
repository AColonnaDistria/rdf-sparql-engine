/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to dea
l in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package qengine.watdiv.transform;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.mapdb.Fun;

import qengine.model.RDFTriple;
import qengine.model.StarQuery;

public class WatDivTransformRemoveDuplicates {
	// input
	private Collection<RDFTriple> rdfTriplesInput;
	private Collection<StarQuery> starQueriesInput;

	// output
	private Collection<RDFTriple> rdfTriplesResults;
	private Collection<StarQuery> starQueriesResults;
	
	private boolean log;
	
	public WatDivTransformRemoveDuplicates(
			Collection<RDFTriple> rdfTriplesInput, Collection<StarQuery> starQueriesInput) {
		
		this(rdfTriplesInput, starQueriesInput, false);
	}

	public WatDivTransformRemoveDuplicates(
			Collection<RDFTriple> rdfTriplesInput, Collection<StarQuery> starQueriesInput,
			boolean log) {
		this.rdfTriplesInput = rdfTriplesInput;
		this.starQueriesInput = starQueriesInput;
		
		this.rdfTriplesResults = new ArrayList<>();
		this.starQueriesResults = new ArrayList<>();
		
		this.log = log;
	}
	
	public void removeDuplicates() {
		// Star Query
		HashSet<StarQuery> starQuerySet = new HashSet<>(this.starQueriesInput);
		
		this.starQueriesResults.clear();
		this.starQueriesResults.addAll(starQuerySet);
		
		// RDF Triples
		HashSet<RDFTriple> rdfTriplesSet = new HashSet<>(this.rdfTriplesInput);
		
		this.rdfTriplesResults.clear();
		this.rdfTriplesResults.addAll(rdfTriplesSet);
	}
	
	public Collection<RDFTriple> getRDFTriplesResults() {
		return this.rdfTriplesResults;
	}

	public Collection<StarQuery> getStarQueriesResults() {
		return this.starQueriesResults;
	}
}
