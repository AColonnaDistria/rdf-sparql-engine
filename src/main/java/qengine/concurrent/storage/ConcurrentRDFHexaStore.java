
/*
 * Cette classe est l'unique travail de Mehdi Bakhtar et de Théo Foutel-Rodier.
 * Elle n'est présente ici que dans le seul but de comparer les implémentations au travers de tests de performances.
 * 
 * This class is solely the work of Mehdi Bakhtar and Théo Foutel-Rodier.
 * It is present here only as the purpose of comparating implementations for performance tests.
 */

package qengine.concurrent.storage;

import fr.boreal.model.logicalElements.api.*;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;

import org.mapdb.BTreeMap;
import org.mapdb.DBMaker;

import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.storage.RDFStorage;
import qengine.concurrent.model.ConcurrentTripleIndexKey;

import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.stream.Stream;

/**
 * Implémentation d'un HexaStore pour stocker des RDFAtom.
 * Cette classe utilise six index pour optimiser les recherches.
 * Les index sont basés sur les combinaisons (Sujet, Prédicat, Objet), (Sujet, Objet, Prédicat),
 * (Prédicat, Sujet, Objet), (Prédicat, Objet, Sujet), (Objet, Sujet, Prédicat) et (Objet, Prédicat, Sujet).
 */
public class ConcurrentRDFHexaStore implements RDFStorage {

    Comparator<Substitution> substitutionComparator = (thisSubst, thatSubst) -> thisSubst.toString().compareTo(thatSubst.toString());
    
	private ConcurrentEncodingDictionary dictionary;
	
	/*
	 * Hexastore indexes are BTreeMap allowing search and put operation in log(n) time complexity
	 * Compared to TreeMap, BTreeMap can have data written and loaded from file (though this add some overhead time)
	 * This can handle data bigger than the system memory. However, as data is relatively small, it is currently held in memory only
	 * 
	 * BTreeMap store encoded triple in a composite key (TripleIndexKey) allowing range querying when matching triple.
	 * 
	 * Triple are thus encoded as key values. However MapDB BTreeMap doesn't allow null value so placeholder Integer is used as value.
	 * 
	 * 6 indexes are covering all permutations or Objects, Subjects and Predicate Term
	 * 
	 * There are an additional 9 BTreeMap holding partial triple as key (only one or two Terms)
	 * Those Tree are also named (maybe wrongly so) indexes but their value hold statistics about the store rather than placeholder values
	 * 
	 * Some of these index are not useful given the types of query we are dealing with
	 * Keeping the current implementation, around half of the indexes could be removed
	 */

	private BTreeMap<ConcurrentTripleIndexKey, Integer> SPOindex;
	private BTreeMap<ConcurrentTripleIndexKey, Integer> SOPindex;
	private BTreeMap<ConcurrentTripleIndexKey, Integer> OSPindex;
	private BTreeMap<ConcurrentTripleIndexKey, Integer> OPSindex;
	private BTreeMap<ConcurrentTripleIndexKey, Integer> PSOindex;
	private BTreeMap<ConcurrentTripleIndexKey, Integer> POSindex;
	
	private BTreeMap<ConcurrentTripleIndexKey, Integer> SPindex;
	private BTreeMap<ConcurrentTripleIndexKey, Integer> SOindex;
	private BTreeMap<ConcurrentTripleIndexKey, Integer> PSindex;
	private BTreeMap<ConcurrentTripleIndexKey, Integer> POindex;
	private BTreeMap<ConcurrentTripleIndexKey, Integer> OSindex;
	private BTreeMap<ConcurrentTripleIndexKey, Integer> OPindex;
	
	private BTreeMap<ConcurrentTripleIndexKey, Integer> Sindex;
	private BTreeMap<ConcurrentTripleIndexKey, Integer> Oindex;
	private BTreeMap<ConcurrentTripleIndexKey, Integer> Pindex;
	

	/*
	 * This is a HashMap holding all BTreeMap indexes with a key identifying them by the order of their information
	 * 
	 * 3 letter keys indicate the "True" indexes encoding the triples
	 * 2 letter and 1 letter keys reffer to Index holding statistics
	 * 
	 * Ex:
	 * - Key : "SOP" -> Subject - Object - Predicate order for an index with all 3 terms as key
	 * - Key : "SO" -> Keys are all combinations of Subject and Objects in the store RDF triples.
	 * 				-> The value associated with a given key is the number of such combination in the store.
	 * - Key : "S" -> Same as "SO" but keys are just all different subjects in the Store.
	 * 			   -> Values are the number of triple with a given subject value
	 */
	private Map<String, BTreeMap<ConcurrentTripleIndexKey, Integer>> indexMap;

	
	public ConcurrentRDFHexaStore() {
		dictionary = new ConcurrentEncodingDictionary();
		
		// map to store the indexes with a string key to avoid multiple if statements
		indexMap = new HashMap<>();
		
		// initialize indexes in memory for the moment
		
		// Index with RDF triples
		SPOindex = DBMaker.newTempTreeMap();
		SOPindex = DBMaker.newTempTreeMap();
		OSPindex = DBMaker.newTempTreeMap();
		OPSindex = DBMaker.newTempTreeMap();
		PSOindex = DBMaker.newTempTreeMap();
		POSindex = DBMaker.newTempTreeMap();
		
		
		// Index with statistics
		SPindex = DBMaker.newTempTreeMap();
		SOindex = DBMaker.newTempTreeMap();
		OSindex = DBMaker.newTempTreeMap();
		OPindex = DBMaker.newTempTreeMap();
		PSindex = DBMaker.newTempTreeMap();
		POindex = DBMaker.newTempTreeMap();
		
		Sindex = DBMaker.newTempTreeMap();
		Oindex = DBMaker.newTempTreeMap();
		Pindex = DBMaker.newTempTreeMap();
		
		// store indexes
		indexMap.put("SPO", SPOindex);
		indexMap.put("SOP", SOPindex);
		indexMap.put("OSP", OSPindex);
		indexMap.put("OPS", OPSindex);
		indexMap.put("PSO", PSOindex);
		indexMap.put("POS", POSindex);
		
		// statistics indexes
		indexMap.put("SP", SPindex);
		indexMap.put("SO", SOindex);
		indexMap.put("OS", OSindex);
		indexMap.put("OP", OPindex);
		indexMap.put("PS", PSindex);
		indexMap.put("PO", POindex);
		
		indexMap.put("S", Sindex);
		indexMap.put("P", Sindex);
		indexMap.put("O", Oindex);

	}
	
	/*
	 * Add a RDF triple to the database
	 * Returns True is added successfully
	 * Returns False otherwise
	 */
	@Override
    public boolean add(RDFTriple triple) {
		boolean success = true;
		// encode each term
		Integer subject = dictionary.encode(triple.getTripleSubject());
		Integer predicate = dictionary.encode(triple.getTriplePredicate());
		Integer object = dictionary.encode(triple.getTripleObject());
		
		// check if the term combination is already present in the store
		ConcurrentTripleIndexKey SPOkey = new ConcurrentTripleIndexKey(subject, predicate, object);
		if (SPOindex.containsKey(SPOkey)) {
			return false;
		}
		
		// put new triple in all indexes (value 0 is a placeholder here)
		SPOindex.put(SPOkey, 0);
		SOPindex.put(
				new ConcurrentTripleIndexKey(subject, object, predicate), 0);
		POSindex.put(
				new ConcurrentTripleIndexKey(predicate, object, subject), 0);
		PSOindex.put(
				new ConcurrentTripleIndexKey(predicate, subject, object), 0);
		OPSindex.put(
				new ConcurrentTripleIndexKey(object, predicate, subject), 0);
		OSPindex.put(
				new ConcurrentTripleIndexKey(object, subject, predicate), 0);
		
		// update statistics
		SPindex.merge(
				new ConcurrentTripleIndexKey(subject, predicate, 0), 1, Integer::sum);
		SOindex.merge(
				new ConcurrentTripleIndexKey(subject, object, 0), 1, Integer::sum);
		PSindex.merge(
				new ConcurrentTripleIndexKey(predicate, subject, 0), 1, Integer::sum);
		POindex.merge(
				new ConcurrentTripleIndexKey(predicate, object, 0), 1, Integer::sum);
		OSindex.merge(
				new ConcurrentTripleIndexKey(object, subject, 0), 1, Integer::sum);
		OPindex.merge(
				new ConcurrentTripleIndexKey(object, predicate, 0), 1, Integer::sum);
		
		Sindex.merge(
				new ConcurrentTripleIndexKey(subject, 0, 0), 1, Integer::sum);
		Oindex.merge(
				new ConcurrentTripleIndexKey(object, 0, 0), 1, Integer::sum);
		Pindex.merge(
				new ConcurrentTripleIndexKey(predicate, 0, 0), 1, Integer::sum);
		return success;
    }
	
	/*
	 * Add all triple from stream to database
	 * Returns True if succeed, false otherwise
	 */
	@Override
    public boolean addAll(Stream<RDFTriple> stream) {
		
		boolean success = true;
		try {
			stream.forEach(triple -> this.add(triple));	
		} catch (Exception e) {
			success = false;
		}
		return success;

    }

	/*
	 * Return the current size of the triple collection
	 */
    @Override
    public long size() {
        return SPOindex.sizeLong();
    }

    /*
     * Evaluate the result of a query
     * RDFTriple may have between 1 and 3 constant terms that will impact index selection for the query
     * 
     * Index selection is made by scanning the query and building an index String such as 'SOP' starting firm constant terms
     */
    @Override
    public Iterator<Substitution> match(RDFTriple triple) {
    	
       	// return 0 if some constant terms are absent from dictionary
    	if (!canTripleExists(triple)) {
    		return new ArrayList<Substitution>().iterator();
    	}
    	
        Term subject = triple.getTripleSubject();
        Term predicate = triple.getTriplePredicate();
        Term object = triple.getTripleObject();
    	
        // get string representations of constant and variable elements of the triple 
//        RDFTripleAnalysis tripleAnalysis = new RDFTripleAnalysis(triple);
        
        String constantTermsString = buildConstantString(subject, predicate, object);
    	
    	// optional indexCode reordering based on statistics here ?
    	// Not implemented yet just choose based on constant terms
        String indexCode = buildFullIndexString(constantTermsString);
    	
    	BTreeMap<ConcurrentTripleIndexKey, Integer> index = indexMap.get(indexCode);
    	
    	// Build start and end keys to do a range query and return all valid answers
    	// they are the encoded Term value for constants and 0 or max values for variables
    	int[] minKeyArray = new int[3];
    	int[] maxKeyArray = new int[3];
    	for (int i = 0; i < 3; i++) {
    		if (i < constantTermsString.length()) {
    			// if is a constant: add the term encoding to the key
    			if (constantTermsString.charAt(i) == 'S') {
    				Integer subjectEncoding = dictionary.encode(subject);
    				minKeyArray[i] = subjectEncoding;
    				maxKeyArray[i] = subjectEncoding;
    			} else if (constantTermsString.charAt(i) == 'O') {
    				Integer objectEncoding = dictionary.encode(object);
    				minKeyArray[i] = objectEncoding;
    				maxKeyArray[i] = objectEncoding;
    			} else if (constantTermsString.charAt(i) == 'P') {
    				Integer predicateEncoding = dictionary.encode(predicate);
    				minKeyArray[i] = predicateEncoding;
    				maxKeyArray[i] = predicateEncoding;
    			}
    			
    		} else {
    			// if it's a variable term: add 0 or max value
    			minKeyArray[i] = 0;
    			maxKeyArray[i] = dictionary.getSize();
    		}
    	}
    
    	ConcurrentTripleIndexKey startKey = new ConcurrentTripleIndexKey(minKeyArray);
    	ConcurrentTripleIndexKey endKey = new ConcurrentTripleIndexKey(maxKeyArray);
    	
    	
    	// find query results
    	ConcurrentNavigableMap<ConcurrentTripleIndexKey, Integer> queryResult = index.subMap(startKey, true, endKey, true);
    	
    	// parse results into Substitutions
    	List<Substitution> substitutions = new ArrayList<>();
    	
    	Map<String, Term> termMap = Map.of(
                "S", subject,
                "O", object,
                "P", predicate
            );
    	
    	for (ConcurrentTripleIndexKey tripleKey : queryResult.keySet()) {
    	    // Add variable elements to a Substitution
    		SubstitutionImpl substitution = new SubstitutionImpl();
    		
    		// go through the indexCode and decode variable terms
    		for (int i = 0; i< indexCode.length(); i++) {
    			// only process the variable terms
    			if (i >= constantTermsString.length()) {
    				Term decodedTerm = dictionary.decode(tripleKey.getKeyAt(i));
    				String indexLetter = String.valueOf(indexCode.charAt(i));
    				Term originalTerm = termMap.get(indexLetter);
    				
    				substitution.add((Variable) originalTerm, decodedTerm);
    			}
    		}
    		
    		substitutions.add(substitution);

    	}
    	
    	return substitutions.iterator();
    }


    @Override
    /*
     * Given a triple return the number of match in the store.
     * If all terms are variables, it returns the size of the database
     */
    public long howMany(RDFTriple triple) {
    	
    	//System.out.println("how many called");
    	// return 0 if some constant terms are absent from dictionary
    	if (!canTripleExists(triple)) {
    		//System.out.println("Triple can't exist");
    		return (long) 0;
    	}
    	
    	// get string representations of constant and variable elements of the triple
    	Term subject = triple.getTripleSubject();
        Term predicate = triple.getTriplePredicate();
        Term object = triple.getTripleObject();

        String constantTermsString = buildConstantString(subject, predicate, object);
        
        
        // map to link index order back to corresponding Term values
        Map<String, Term> termMap = Map.of(
                "S", subject,
                "O", object,
                "P", predicate
            );
        
        // check if constant terms exists
        long count = 0;
        if (constantTermsString.length() == 3) {
        	// only constants so 1 or 0
        	boolean isPresent = this.dictionary.hasTerm(subject)
        			&& this.dictionary.hasTerm(predicate)
        			&& this.dictionary.hasTerm(object);
        	count = Boolean.compare(isPresent, false);
        } else if (constantTermsString.length() == 0) {
        	// all variables so the store size
        	count = this.SPOindex.size();
        } else {
        	// one or two constant terms

        	// build an index key of the length of constant terms to get statistic index
        	int[] indexKeyArray = new int[3];
        	for (int i=0; i < 3; i++) {
        		if (i < constantTermsString.length()) {
            		Integer encodedValue = this.dictionary.encode(
            				termMap.get(String.valueOf(constantTermsString.charAt(i))));
            		indexKeyArray[i] = encodedValue;        			
        		} else {
        			indexKeyArray[i] = 0;
        		}
        	}
        	ConcurrentTripleIndexKey indexKey = new ConcurrentTripleIndexKey(indexKeyArray);

        	// return the associated value
            BTreeMap<ConcurrentTripleIndexKey, Integer> index = this.indexMap.get(constantTermsString);
            
            // if triple not present return 0
            count = index.getOrDefault(indexKey, 0);
        }        
        return count;
    }
    
    
    /*
     * Output all triples from database as a Collection
     */
    @Override
    public Collection<RDFTriple> getAtoms() {

    	// Could test for performance increase here if computing Index size and passing it to ArrayList creation
    	List<RDFTriple> tripleList = new ArrayList<>();
    	
    	// can use any index here, so use SPO order
    	for (ConcurrentTripleIndexKey tripleKey: SPOindex.keySet()) {
    		RDFTriple rdfTriple = new RDFTriple(
    			dictionary.decode(tripleKey.getFirstKey()),
    			dictionary.decode(tripleKey.getSecondKey()),
    			dictionary.decode(tripleKey.getThirdKey())
    				);
    		
    		tripleList.add(rdfTriple);
    	}
    	return tripleList;
    }
    
    /*
     * Returns true if all constant terms of rdf triple are in the dictionary
     */
    public boolean canTripleExists(RDFTriple triple) {
    	Term subject = triple.getTripleSubject();
        Term predicate = triple.getTriplePredicate();
        Term object = triple.getTripleObject();

        // get string representations of constant and variable elements of the triple 
        String constantTermsString = buildConstantString(subject, predicate, object);
        
        Map<String, Term> termMap = Map.of(
                "S", subject,
                "O", object,
                "P", predicate
            );
        
        // returns false if any constant term is missing from the dictionary
        for (char c: constantTermsString.toCharArray()) {
        	String key = String.valueOf(c);
        	if (!dictionary.hasTerm(termMap.get(key))) {
        		return false;
        	}
        }
        
        return true;
        
    }
    
    /*
     * Returns a string with a letter matching each constant part of a RDFTriple 
     */
	private String buildConstantString(Term subject, Term predicate, Term object) {
    	StringBuilder constantStringBuilder = new StringBuilder();
        
        if (subject.isConstant() || subject.isLiteral()) {
        	constantStringBuilder.append("S");
        }
        
        if (predicate.isConstant() || predicate.isLiteral()) {
        	constantStringBuilder.append("P");
        }
        
        if (object.isConstant() || object.isLiteral()) {
        	constantStringBuilder.append("O");
        }
        
        return constantStringBuilder.toString();
	}
	
    /*
     * Fill a string with Triple constant terms with its variable terms
     */
	private String buildFullIndexString(String constantString) {	
    	String possibleLetters = "SOP";
    	
    	
		StringBuilder complementBuilder = new StringBuilder();
    	
        for (char c : possibleLetters.toCharArray()) {
            if (constantString.indexOf(c) == -1) {
                // if the character is not present the prefix
            	complementBuilder.append(c);
            }
        }
    	return constantString + complementBuilder.toString();
	}

	@Override
	public Iterator<Substitution> match(StarQuery q) {
        boolean logQueryEvaluation = false;

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
