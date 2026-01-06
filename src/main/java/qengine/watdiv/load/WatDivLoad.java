package qengine.watdiv.load;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mapdb.Fun;

import qengine.model.RDFTriple;
import qengine.model.StarQuery;

import java.nio.file.Paths; // Java 7
import java.io.IOException;
import java.nio.file.Files; // Java 7

public class WatDivLoad {
	Map<String, ArrayList<StarQuery>>  starQueriesInBins;
	
	private String loadStarQueriesInBinsFolder;
	
	public WatDivLoad(
			Map<String, ArrayList<StarQuery>> starQueriesInBins, 
			String loadStarQueriesInBinsFolder) {
		this.starQueriesInBins = starQueriesInBins;
		this.loadStarQueriesInBinsFolder = loadStarQueriesInBinsFolder;
	}
	
	public void loadAllStarQueriesInBins() throws IOException {
		for (Map.Entry<String, ArrayList<StarQuery>> entry : starQueriesInBins.entrySet()) {
			String d_answers = String.format("answers_%s", entry.getKey());
			ArrayList<StarQuery> values = entry.getValue();
		
			Path directory_path = Paths.get(loadStarQueriesInBinsFolder, d_answers);

			if (!Files.exists(directory_path)) { 
				Files.createDirectories(directory_path);
			}
			
			Path filepath = directory_path.resolve(String.format("%s.queryset", d_answers));
			
			SparQLWriter sparQLWriter = new SparQLWriter(filepath.toString(), values);
			sparQLWriter.writeAllSparQLQueries();
		}
	}
}
