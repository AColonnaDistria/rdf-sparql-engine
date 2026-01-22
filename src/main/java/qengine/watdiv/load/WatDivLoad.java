/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


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
