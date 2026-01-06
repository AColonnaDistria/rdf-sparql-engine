
/*
 * Cette classe est l'unique travail de Mehdi Bakhtar et de Théo Foutel-Rodier.
 * Elle n'est présente ici que dans le seul but de comparer les implémentations au travers de tests de performances.
 * 
 * This class is solely the work of Mehdi Bakhtar and Théo Foutel-Rodier.
 * It is present here only as the purpose of comparating implementations for performance tests.
 */

package qengine.concurrent.storage;

import fr.boreal.model.logicalElements.api.Substitution;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import java.util.List;

import java.util.ArrayList;

import fr.boreal.model.logicalElements.api.Term;
import fr.boreal.model.logicalElements.api.Variable;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.storage.RDFStorage;

/**
 * Implémentation Giant-Table pour stocker des RDFTriple.
 */
public class ConcurrentGiantTable implements RDFStorage {

    Comparator<Substitution> substitutionComparator = (thisSubst, thatSubst) -> thisSubst.toString().compareTo(thatSubst.toString());
    
    private final ConcurrentEncodingDictionary dictionary;
    //for encoding and decoding RDF triples

    private final List<int[]> tableRows;
    //the table here is a list of rows where each row is an encoded RDF triple

    public ConcurrentGiantTable() {
        this.dictionary = new ConcurrentEncodingDictionary();
        this.tableRows = new ArrayList<>();
    }

    //if we want to create a giant-table with a given dictionary
    public ConcurrentGiantTable(ConcurrentEncodingDictionary dictionary) {
        this.dictionary = dictionary;
        this.tableRows = new ArrayList<>();
    }


    @Override
    public boolean add(RDFTriple triple) {
        int encodedSubject = dictionary.encode(triple.getTripleSubject());
        int encodedPredicate = dictionary.encode(triple.getTriplePredicate());
        int encodedObject = dictionary.encode(triple.getTripleObject());

        for (int[] encodedTripleRow : tableRows) {
            if (encodedTripleRow[0] == encodedSubject
                    && encodedTripleRow[1] == encodedPredicate
                    && encodedTripleRow[2] == encodedObject) {
                //if the triple we want to add is already in the table, we don't add it
                return false;
            }
        }

        tableRows.add(new int[]{ encodedSubject, encodedPredicate, encodedObject });
        return true;
    }

    @Override
    public long size() { return tableRows.size(); }

    public static boolean isVariable(Term term) { return term instanceof Variable; }

    @Override
    public Iterator<Substitution> match(RDFTriple triple) {
        List<Substitution> result = new ArrayList<>();

        Term subject = triple.getTripleSubject();
        Term predicate = triple.getTriplePredicate();
        Term object = triple.getTripleObject();

        //if either one of the non-variable terms of the triple is not encoded in the dictionary
        //then it is for sure not in the table and doesn't match any encoded triple
        //we can immediately return the empty iterator as a result
        int encodedSubject = -1;
        int encodedPredicate = -1;
        int encodedObject = -1;
        if (!isVariable(subject)) {
            if (!dictionary.hasTerm(subject)) { return result.iterator(); }
            else { encodedSubject = dictionary.encode(subject); }
        }
        if (!isVariable(predicate)) {
            if (!dictionary.hasTerm(predicate)) { return result.iterator(); }
            else { encodedPredicate = dictionary.encode(predicate); }
        }
        if (!isVariable(object)) {
            if (!dictionary.hasTerm(object)) { return result.iterator(); }
            else { encodedObject = dictionary.encode(object); }
        }

        //otherwise, we have to check the table entirely
        for (int[] encodedTripleRow : tableRows) {
            //for a given row of the table,
            //if any encoded non-variable term of the triple we are trying to match isn't equal
            //to its corresponding row value (first one for the subject, second one for the predicate, and third one for the object),
            //then we do not have a match, we can go check the next row
            if (!isVariable(subject) && (encodedSubject != encodedTripleRow[0])) { continue; }
            if (!isVariable(predicate) && (encodedPredicate != encodedTripleRow[1])) { continue; }
            if (!isVariable(object) && (encodedObject != encodedTripleRow[2])) { continue; }

            //we found a match
            SubstitutionImpl substitution = new SubstitutionImpl();
            if (isVariable(subject)) { substitution.add( (Variable) subject, dictionary.decode(encodedTripleRow[0])); }
            if (isVariable(predicate)) { substitution.add( (Variable) predicate, dictionary.decode(encodedTripleRow[1])); }
            if (isVariable(object)) { substitution.add( (Variable) object, dictionary.decode(encodedTripleRow[2])); }
            result.add(substitution);
        }
        return result.iterator();
    }

    //used for star query evaluation (called on query patterns)
    @Override
    public long howMany(RDFTriple triple) {
        //we just return the table's size (number of RDF triples stored) because we have no index on GiantTable that gives us stats for matching particular triples
        //note: doing this makes the name of this method semantically inaccurate
        //on GiantTable, it's more of a  "how many matching triples at most" rather than an "how many matching triples"
        return this.size();
    }

    @Override
    public Collection<RDFTriple> getAtoms() {
	        List<RDFTriple> result = new ArrayList<>();
	        //we just take each row and rebuild each RDFTriple/Atom by decoding
	        for (int[] encodedTripleRow : tableRows) {
	            Term subject = dictionary.decode(encodedTripleRow[0]);
	            Term predicate = dictionary.decode(encodedTripleRow[1]);
	            Term object = dictionary.decode(encodedTripleRow[2]);
	            result.add(new RDFTriple(subject, predicate, object));
	        }
	        return result;
	    }
	
		@Override
		public Iterator<Substitution> match(StarQuery q) {boolean logQueryEvaluation = false;
	
	    if (logQueryEvaluation) {
	        System.out.println("Evaluating star query \"" + q.getLabel() + "\":");
	    }
	
	    //we consider here a star queries to have a single variable which is the subject
	    //meaning that all branches of the star/RDF patterns of the query have constant predicates and objects
	    //the final result is thus the intersection between all individual query results.
	    //to do so efficiently we merge the smallest results first        
	    
	    Variable variable = q.getCentralVariable();
	    //the center variable of the star (subject)
	    List<RDFTriple> rdfPatterns = q.getRdfAtoms();
	    //the branches of the star / the RDF patterns of the query
	    
	    // we sort triples by increasing match count against the store.
	    rdfPatterns.sort(
	    		(triple1, triple2) -> Long.compare(howMany(triple1), howMany(triple2))
	    		);
	
	    ////evaluating the subqueries associated with each pattern and ordering/sorting them
	
	    // removed the iterator definition here as it was only used to return an empty iterator
	    // the empty iterator is generated from an empty list directly in the empty return case now
	
	    List<Substitution> currentStarQueryResult = new ArrayList<>();
	    RDFTriple rdfPattern;
	    for (int rdfPatternIndex = 0; rdfPatternIndex < rdfPatterns.size(); rdfPatternIndex++) {
	        if (logQueryEvaluation && rdfPatternIndex != 0) {
	            System.out.println();
	            System.out.println("    Partial query result:");
	            System.out.println("    " + currentStarQueryResult);
	        }
	
	        rdfPattern = rdfPatterns.get(rdfPatternIndex);
	        Iterator<Substitution> patternMatchResultIterator = match(rdfPattern);
	
	        if (!patternMatchResultIterator.hasNext()) {
	            //the subquery for that pattern/branch of the star returns no match triples
	            //so the result of the star query is also nothing (empty iterator)
	            return new ArrayList<Substitution>().iterator();
	        }
	        
	        // get all substitutions for the current triple query
	        List<Substitution> patternMatchResult = new ArrayList<>();
	        patternMatchResultIterator.forEachRemaining(patternMatchResult::add);
	        
	        patternMatchResult.sort(substitutionComparator);
	        if (logQueryEvaluation) {
	            System.out.println("    Subquery " + (rdfPatternIndex + 1) + " (" + rdfPattern + ") result:");
	            System.out.println("    " + patternMatchResult);
	        }
	
	        //for the first pattern result (smallest number of matching triples on the store)
	        if (rdfPatternIndex == 0) {
	            currentStarQueryResult = patternMatchResult;
	            continue;
	        }
	
	
	        //now, let's intersect/fuse/merge the subquery result to the query's partial result
	        //both lists are sorted so we can do things efficiently with a two pointers strategy
	        List<Substitution> nextStarQueryResult = new ArrayList<>();
	
	        int thisIndex = 0;
	        int thatIndex = 0;
	
	        while (thisIndex < currentStarQueryResult.size()
	                && thatIndex < patternMatchResult.size()) {
	            Substitution thisSubstitution = currentStarQueryResult.get(thisIndex);
	            Substitution thatSubstitution = patternMatchResult.get(thatIndex);
	            
	            int substitutionComparison = substitutionComparator.compare(thisSubstitution, thatSubstitution);
	
	            if (substitutionComparison == 0) {
	                nextStarQueryResult.add(thisSubstitution);
	                thisIndex++;
	                thatIndex++;
	            } else if (substitutionComparison < 0) {
	                thisIndex++;
	            } else {
	                thatIndex++;
	            }
	        }
	
	        currentStarQueryResult = nextStarQueryResult;
	        //also sorted
	    }
	
	    if (logQueryEvaluation) {
	        System.out.println();
	        System.out.println("    Query result:");
	        System.out.println("    " + currentStarQueryResult);
	    }
	    
	    // return an iterator
	    return currentStarQueryResult.iterator();
	}
}