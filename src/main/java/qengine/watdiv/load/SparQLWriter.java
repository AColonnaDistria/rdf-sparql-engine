/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package qengine.watdiv.load;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import fr.boreal.model.logicalElements.api.Variable;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;

public class SparQLWriter {
	private String sparQLSaveFilePath;
	private Collection<StarQuery> starQueries;
	private FileWriter writer;
	
	public SparQLWriter(String sparQLSaveFilePath, Collection<StarQuery> starQueries) {
		this.sparQLSaveFilePath = sparQLSaveFilePath;
		this.starQueries = starQueries;
	}
	
	public void writeAllSparQLQueries() throws IOException {
		this.writer = new FileWriter(this.sparQLSaveFilePath);
		
		for (StarQuery starQuery : starQueries) {
			this.writeSparQLQuery(starQuery);
		}
		
		this.writer.close();
	}

	private void writeSparQLQuery(StarQuery query) throws IOException {
        StringBuilder sparql = new StringBuilder();

        // SELECT clause
        sparql.append("SELECT ");
        for (Variable var : query.getAnswerVariables()) {
            sparql.append(var.label()).append(" ");
        }
        sparql.append("\nWHERE {\n");

        // RDF triples
        List<RDFTriple> triples = query.getRdfAtoms();
        for (RDFTriple triple : triples) {
            sparql.append("  ");
            
            if (!triple.getTripleSubject().isVariable()) {
            	sparql.append("<").append(triple.getTripleSubject().label()).append("> ");
            }
            else {
            	sparql.append(triple.getTripleSubject().label()).append(" ");
            }
                  
            sparql.append("<").append(triple.getTriplePredicate().label()).append("> ")
                  .append("<").append(triple.getTripleObject().label()).append("> .\n");
        }

        sparql.append("}\n\n");

        writer.write(sparql.toString());
        writer.flush();
	}
	
}
