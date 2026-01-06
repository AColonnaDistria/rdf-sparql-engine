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
