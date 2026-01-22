/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package qengine.watdiv.extract;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.rdf4j.rio.RDFFormat;

import fr.boreal.model.query.api.Query;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.parser.RDFTriplesParser;
import qengine.parser.StarQuerySparQLParser;

public class WatDivExtract {
	private String rdfTriplesFilePath;
	private String starQueriesFilePath;
	
	private List<RDFTriple> rdfTriples;
	private List<StarQuery> starQueries;
	
	private boolean log;

	public WatDivExtract(String rdfTriplesFilePath, String starQueriesFilePath, boolean log) {
		this.rdfTriplesFilePath = rdfTriplesFilePath;
		this.starQueriesFilePath = starQueriesFilePath;
		
		this.rdfTriples = new ArrayList<>();
		this.starQueries = new ArrayList<>();
		
		this.log = log;
	}
	
	public WatDivExtract(String rdfTriplesFilePath, String starQueriesFilePath) {
		this.rdfTriplesFilePath = rdfTriplesFilePath;
		this.starQueriesFilePath = starQueriesFilePath;
		
		this.rdfTriples = new ArrayList<>();
		this.starQueries = new ArrayList<>();
		
		this.log = false;
	}
	
	public Collection<RDFTriple> getRDFTriples() {
		return Collections.unmodifiableCollection(this.rdfTriples);
	}

	public Collection<StarQuery> getStarQueries() {
		return Collections.unmodifiableCollection(this.starQueries);
	}
	
	public void extractAll() throws IOException {
		this.extractRDFData();
		this.extractStarQueries();
	}
	
	public void extractStarQueries() throws IOException {
		this.starQueries.clear();
		
		File starQueriesDirectory = new File(this.starQueriesFilePath);
		
		for (File starQueryFile : starQueriesDirectory.listFiles()) {
			if (starQueryFile.isFile()) {
				String fileAbsolutePath = starQueryFile.getAbsolutePath();

				try (StarQuerySparQLParser queryParser = new StarQuerySparQLParser(fileAbsolutePath)) {
					int queryCount = 0;

					while (queryParser.hasNext()) {
						Query query = queryParser.next();
						if (query instanceof StarQuery starQuery) {
							this.starQueries.add(starQuery); 
							
							if (this.log) {
								System.out.println("Star Query #" + (++queryCount) + ":");
								System.out.println("  Central Variable: " + starQuery.getCentralVariable().label());
								System.out.println("  RDF Atoms:");
							}
							starQuery.getRdfAtoms().forEach(triple -> System.out.println("    " + triple));
						} else {
							System.err.println("Requête inconnue ignorée.");
						}
					}
					if (this.log) {
						System.out.println("Total Queries parsed: " + starQueries.size());
					}
				}
			}
		}
	}
	
	public void extractRDFData() throws IOException {
		this.rdfTriples.clear();
		
		try (RDFTriplesParser rdfAtomParser = new RDFTriplesParser(this.rdfTriplesFilePath)) {
			int count = 0;
			while (rdfAtomParser.hasNext()) {
				RDFTriple triple = rdfAtomParser.next();
				this.rdfTriples.add(triple);  // Stocker le triplet dans la collection
				if (this.log) {
					System.out.println("RDF Triple #" + (++count) + ": " + triple);
				}
			}
			
			if (this.log) {
				System.out.println("Total RDF Triples parsed: " + count);
			}
		}
	}
}
