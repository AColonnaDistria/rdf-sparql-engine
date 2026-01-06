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
