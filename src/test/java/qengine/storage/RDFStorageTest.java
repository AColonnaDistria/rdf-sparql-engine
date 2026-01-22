/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package qengine.storage;

import fr.boreal.model.logicalElements.api.*;
import fr.boreal.model.logicalElements.factory.impl.SameObjectTermFactory;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import fr.boreal.model.query.api.Query;

import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.parser.RDFTriplesParser;
import qengine.parser.StarQuerySparQLParser;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour les classes qui implémentent {@link RDFStorage}.
 */
public abstract class RDFStorageTest {
	
	public static final String STAR_QUERIES_UNIT_TEST_DATASET_PATH = "data/unit-tests/star-queries/STAR_ALL_workload.queryset";
	public static final String RDF_TRIPLES_UNIT_TEST_DATASET_PATH = "data/unit-tests/rdf-triples/100K.nt";
	
    private static final Literal<String> SUBJECT_1 = SameObjectTermFactory.instance().createOrGetLiteral("subject1");
    private static final Literal<String> PREDICATE_1 = SameObjectTermFactory.instance().createOrGetLiteral("predicate1");
    private static final Literal<String> OBJECT_1 = SameObjectTermFactory.instance().createOrGetLiteral("object1");
    private static final Literal<String> SUBJECT_2 = SameObjectTermFactory.instance().createOrGetLiteral("subject2");
    private static final Literal<String> PREDICATE_2 = SameObjectTermFactory.instance().createOrGetLiteral("predicate2");
    private static final Literal<String> OBJECT_2 = SameObjectTermFactory.instance().createOrGetLiteral("object2");
    private static final Literal<String> OBJECT_3 = SameObjectTermFactory.instance().createOrGetLiteral("object3");
    private static final Variable VAR_X = SameObjectTermFactory.instance().createOrGetVariable("?x");
    private static final Variable VAR_Y = SameObjectTermFactory.instance().createOrGetVariable("?y");
    

    private static final Literal<String> SUBJECT_3 = SameObjectTermFactory.instance().createOrGetLiteral("subject3");
    private static final Literal<String> PREDICATE_3 = SameObjectTermFactory.instance().createOrGetLiteral("predicate3");
    private static final Literal<String> OBJECT_4 = SameObjectTermFactory.instance().createOrGetLiteral("object4");

    private static final Literal<String> SUBJECT_4 = SameObjectTermFactory.instance().createOrGetLiteral("subject4");
    private static final Literal<String> PREDICATE_4 = SameObjectTermFactory.instance().createOrGetLiteral("predicate4");
    private static final Literal<String> OBJECT_5 = SameObjectTermFactory.instance().createOrGetLiteral("object5");

    /* Returns an instance of the class tested (that should implement RDFStorage) */
    public abstract RDFStorage newInstance();
    
    @Test
    public void testAddAllRDFAtoms() {
        RDFStorage store = this.newInstance();

        // Version stream
        // Ajouter plusieurs RDFAtom
        RDFTriple rdfAtom1 = new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_1);
        RDFTriple rdfAtom2 = new RDFTriple(SUBJECT_2, PREDICATE_2, OBJECT_2);

        Set<RDFTriple> rdfAtoms = Set.of(rdfAtom1, rdfAtom2);

        assertTrue(store.addAll(rdfAtoms.stream()), "Les RDFAtoms devraient être ajoutés avec succès.");

        // Vérifier que tous les atomes sont présents
        Collection<RDFTriple> atoms = store.getAtoms();
        assertTrue(atoms.contains(rdfAtom1), "La base devrait contenir le premier RDFAtom ajouté.");
        assertTrue(atoms.contains(rdfAtom2), "La base devrait contenir le second RDFAtom ajouté.");

        // Version collection
        store = this.newInstance();
        
        assertTrue(store.addAll(rdfAtoms), "Les RDFAtoms devraient être ajoutés avec succès.");

        // Vérifier que tous les atomes sont présents
        atoms = store.getAtoms();
        assertTrue(atoms.contains(rdfAtom1), "La base devrait contenir le premier RDFAtom ajouté.");
        assertTrue(atoms.contains(rdfAtom2), "La base devrait contenir le second RDFAtom ajouté.");
    }

    @Test
    public void testAddRDFAtom() {
        RDFStorage store = this.newInstance();

    	RDFTriple rdfAtom = new RDFTriple(SUBJECT_3, PREDICATE_3, OBJECT_4);
        
        assertTrue(store.add(rdfAtom));
        Collection<RDFTriple> atoms = store.getAtoms();
        
        assertTrue(atoms.contains(rdfAtom), "La base devrait contenir le RDFAtom ajouté.");
    }

    @Test
    public void testAddDuplicateAtom() {
        RDFStorage store = this.newInstance();

    	RDFTriple rdfAtom1 = new RDFTriple(SUBJECT_4, PREDICATE_4, OBJECT_5);
    	RDFTriple rdfAtom2 = new RDFTriple(SUBJECT_4, PREDICATE_4, OBJECT_5);
        
        assertTrue(store.add(rdfAtom1));
        assertTrue(!store.add(rdfAtom2));
        Collection<RDFTriple> atoms = store.getAtoms();
        
        assertTrue(atoms.contains(rdfAtom1), "La base devrait contenir le RDFAtom ajouté.");
        assertTrue(atoms.size() == 1, "Seulement 1 triplet RDF doit être contenu dans la collection.");
    }

    @Test
    public void testSize() {
        RDFStorage store = this.newInstance();
        
        assertTrue(store.add(new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_1)));
        assertTrue(store.add(new RDFTriple(SUBJECT_2, PREDICATE_1, OBJECT_2)));
        assertTrue(store.add(new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_3)));
        assertTrue(store.add(new RDFTriple(SUBJECT_3, PREDICATE_3, OBJECT_4)));
        assertTrue(store.add(new RDFTriple(SUBJECT_4, PREDICATE_4, OBJECT_5)));

        assertTrue(store.size() == 5);
    }

    @Test
    public void testMatchAtom() {
        RDFStorage store = this.newInstance();
        
        store.add(new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_1));
        store.add(new RDFTriple(SUBJECT_2, PREDICATE_1, OBJECT_2));
        store.add(new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_3));

        // Cas : objet variable
        RDFTriple matchObjVar = new RDFTriple(SUBJECT_1, PREDICATE_1, VAR_X);
        List<Substitution> subsObjVar = new ArrayList<>();
        store.match(matchObjVar).forEachRemaining(subsObjVar::add);

        assertEquals(2, subsObjVar.size(), "Objet variable: deux correspondances attendues");
        Substitution subObj1 = new SubstitutionImpl();
        subObj1.add(VAR_X, OBJECT_1);
        Substitution subObj3 = new SubstitutionImpl();
        subObj3.add(VAR_X, OBJECT_3);
        assertTrue(subsObjVar.contains(subObj1));
        assertTrue(subsObjVar.contains(subObj3));

        // Cas : sujet variable
        RDFTriple matchSubVar = new RDFTriple(VAR_X, PREDICATE_1, OBJECT_2);
        List<Substitution> subsSubVar = new ArrayList<>();
        store.match(matchSubVar).forEachRemaining(subsSubVar::add);

        assertEquals(1, subsSubVar.size(), "Sujet variable: une correspondance attendue");
        Substitution subSub = new SubstitutionImpl();
        subSub.add(VAR_X, SUBJECT_2);
        assertTrue(subsSubVar.contains(subSub));

        // Cas : prédicat variable
        RDFTriple matchPredVar = new RDFTriple(SUBJECT_1, VAR_X, OBJECT_1);
        List<Substitution> subsPredVar = new ArrayList<>();
        store.match(matchPredVar).forEachRemaining(subsPredVar::add);

        assertEquals(1, subsPredVar.size(), "Prédicat variable: une correspondance attendue");
        Substitution subPred = new SubstitutionImpl();
        subPred.add(VAR_X, PREDICATE_1);
        assertTrue(subsPredVar.contains(subPred));

        // Cas : sujet et objet variables
        RDFTriple matchSubObjVar = new RDFTriple(VAR_X, PREDICATE_1, VAR_Y);
        List<Substitution> subsSubObjVar = new ArrayList<>();
        store.match(matchSubObjVar).forEachRemaining(subsSubObjVar::add);

        assertEquals(3, subsSubObjVar.size(), "Sujet et objet variables: trois correspondances attendues");

        Substitution s1 = new SubstitutionImpl(); s1.add(VAR_X, SUBJECT_1); s1.add(VAR_Y, OBJECT_1);
        Substitution s2 = new SubstitutionImpl(); s2.add(VAR_X, SUBJECT_1); s2.add(VAR_Y, OBJECT_3);
        Substitution s3 = new SubstitutionImpl(); s3.add(VAR_X, SUBJECT_2); s3.add(VAR_Y, OBJECT_2);

        assertTrue(subsSubObjVar.contains(s1));
        assertTrue(subsSubObjVar.contains(s2));
        assertTrue(subsSubObjVar.contains(s3));
    }

    @Test
    public void testMatchStarQuery() {
        RDFStorage store = this.newInstance();
        
        Oracle oracle = new Oracle();
        
        try {
        	File rdftriplesFile = new File(RDF_TRIPLES_UNIT_TEST_DATASET_PATH);
        	RDFTriplesParser rdfTriplesParser = new RDFTriplesParser(rdftriplesFile);
        	
        	while (rdfTriplesParser.hasNext()) {
        		RDFTriple triple = rdfTriplesParser.next();
        		
        		store.add(triple);
        		oracle.add(triple);
        	}
        } 
        catch (IOException e) {
			e.printStackTrace();
		}
        
    	try {
			StarQuerySparQLParser starQueryParser = new StarQuerySparQLParser(STAR_QUERIES_UNIT_TEST_DATASET_PATH);

	    	while (starQueryParser.hasNext()) {
	    		Query query = starQueryParser.next();
	    		
	    		if (query instanceof StarQuery) {
                    StarQuery starQuery = (StarQuery) query;
                    
                    Iterator<Substitution> subs_store = store.match(starQuery);
                    Iterator<Substitution> subs_oracle = oracle.match(starQuery);
                    
                    Set<Substitution> oracleSet = new HashSet<>();
                    subs_oracle.forEachRemaining(oracleSet::add);

                    Set<Substitution> storeSet = new HashSet<>();
                    subs_store.forEachRemaining(storeSet::add);
                    
                    assertEquals(oracleSet.size(), storeSet.size(), "Le nombre de substitutions diffère de l'oracle pour une requête.");
                    assertEquals(oracleSet, storeSet, "Le contenu des substitutions diffère de l'oracle pour une requête.");
	    		}
	    	}
    	} 
    	catch (IOException e) {
			e.printStackTrace();
		}
    	
    }

    @Test
    public void testHowManyTriple() {
        RDFStorage store = this.newInstance();
        
    	Oracle oracle = new Oracle();

    	store.add(new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_1));
    	store.add(new RDFTriple(SUBJECT_2, PREDICATE_1, OBJECT_2));
    	store.add(new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_3));

    	oracle.add(new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_1));
    	oracle.add(new RDFTriple(SUBJECT_2, PREDICATE_1, OBJECT_2));
    	oracle.add(new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_3));

        RDFTriple pattern1 = new RDFTriple(SUBJECT_1, PREDICATE_1, VAR_X);
        RDFTriple pattern2 = new RDFTriple(VAR_X, PREDICATE_1, VAR_Y);
        RDFTriple pattern3 = new RDFTriple(VAR_X, VAR_Y, OBJECT_1);
        RDFTriple pattern4 = new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_1);
        RDFTriple pattern5 = new RDFTriple(SUBJECT_4, PREDICATE_4, OBJECT_5);
        
        assertEquals(oracle.howMany(pattern1), store.howMany(pattern1), "howMany(S1, P1, ?) diffère.");
        assertEquals(oracle.howMany(pattern2), store.howMany(pattern2), "howMany(?, P1, ?) diffère.");
        assertEquals(oracle.howMany(pattern3), store.howMany(pattern3), "howMany(?, ?, O1) diffère.");
        assertEquals(oracle.howMany(pattern4), store.howMany(pattern4), "howMany(S1, P1, O1) diffère.");
        assertEquals(oracle.howMany(pattern5), store.howMany(pattern5), "howMany(S1, P1, 05) diffère.");
    }
}
