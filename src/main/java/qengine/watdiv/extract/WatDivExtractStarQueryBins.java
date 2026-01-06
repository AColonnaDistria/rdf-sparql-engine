package qengine.watdiv.extract;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.rdf4j.rio.RDFFormat;

import fr.boreal.model.query.api.Query;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.parser.RDFTriplesParser;
import qengine.parser.StarQuerySparQLParser;

public class WatDivExtractStarQueryBins {
	private String starQueriesFilePath;
	
	// by number of answers
	private HashMap<String, ArrayList<StarQuery>> starQueries;
	private ArrayList<String> numberOfAnswersBins;
	
	private boolean log;

	public WatDivExtractStarQueryBins(String starQueriesFilePath, boolean log) {
		this.starQueriesFilePath = starQueriesFilePath;
		this.starQueries = new HashMap<>();
		
		this.numberOfAnswersBins = new ArrayList<>();
		
		this.log = log;
	}
	
	public WatDivExtractStarQueryBins(String starQueriesFilePath) {
		this(starQueriesFilePath, false);
	}

	public HashMap<String, ArrayList<StarQuery>> getStarQueries() {
		return this.starQueries;
	}

	public ArrayList<String> getNumberOfAnswersBin() {
		return this.numberOfAnswersBins;
	}
	
	private void parseFile(File file, ArrayList<StarQuery> list) throws IOException {

		try (StarQuerySparQLParser queryParser = new StarQuerySparQLParser(file.getAbsolutePath())) {
			int queryCount = 0;

			while (queryParser.hasNext()) {
				Query query = queryParser.next();
				if (query instanceof StarQuery starQuery) {
					list.add(starQuery); 
					
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
	
	public void extractStarQueries() throws IOException {
		this.starQueries.clear();
		this.numberOfAnswersBins.clear();
		
		File starQueriesDirectory = new File(this.starQueriesFilePath);

		for (File answersDirectory : starQueriesDirectory.listFiles()) {
			if (!answersDirectory.isFile()) {
				String answersBin = answersDirectory.getName().replace("answers_", "");

				if (!this.starQueries.containsKey(answersBin)) {
					this.starQueries.put(answersBin, new ArrayList<>());
				}
				
				if (!this.numberOfAnswersBins.contains(answersBin)) {
					this.numberOfAnswersBins.add(answersBin);
				}
				
				ArrayList<StarQuery> answerBinValues = this.starQueries.get(answersBin);
				
				for (File starQuerySet : answersDirectory.listFiles() ) {
					if (starQuerySet.isFile()) {
						this.parseFile(starQuerySet, answerBinValues);
					}
				}
			}
		}
	}
}
