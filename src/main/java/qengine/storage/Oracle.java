/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package qengine.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fr.boreal.model.logicalElements.api.Substitution;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import fr.boreal.model.query.api.FOQuery;
import fr.boreal.model.queryEvaluation.api.FOQueryEvaluator;
import fr.boreal.query_evaluation.generic.GenericFOQueryEvaluator;
import fr.boreal.storage.natives.SimpleInMemoryGraphStore;
import fr.lirmm.boreal.util.stream.CloseableIteratorWithoutException;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import fr.boreal.model.formula.api.FOFormula;
import fr.boreal.model.formula.api.FOFormulaConjunction;
import fr.boreal.model.logicalElements.api.*;

public class Oracle implements RDFStorage {
	SimpleInMemoryGraphStore data;
	
	public Oracle() {
		data = new SimpleInMemoryGraphStore();
	}
	
	@Override
	public boolean add(RDFTriple t) {
		return data.add(t);
	}

	@Override
	public Iterator<Substitution> match(RDFTriple query) {
		CloseableIteratorWithoutException<Atom> subs_atoms = data.match(query);
		ArrayList<Substitution> substitutions = new ArrayList<>();
		
		while (subs_atoms.hasNext()) {
			Atom atom = subs_atoms.next();
			Substitution sub = new SubstitutionImpl();
			
			if (query.getTripleSubject().isVariable()) {
                sub.add((Variable) query.getTripleSubject(), atom.getTerm(0));
            }
			
            if (query.getTriplePredicate().isVariable()) {
                sub.add((Variable) query.getTriplePredicate(), atom.getTerm(1));
            }
            
            if (query.getTripleObject().isVariable()) {
                sub.add((Variable) query.getTripleObject(), atom.getTerm(2));
            }
            
            substitutions.add(sub);
		}
		
		return substitutions.iterator();
	}

	@Override
	public Iterator<Substitution> match(StarQuery q) {
		FOQuery<FOFormulaConjunction> foQuery = q.asFOQuery(); // Conversion en FOQuery
		FOQueryEvaluator<FOFormula> evaluator = GenericFOQueryEvaluator.defaultInstance(); // Créer un évaluateur
		Iterator<Substitution> queryResults = evaluator.evaluate(foQuery, data); // Évaluer la requête

		return queryResults;
	}

	@Override
	public long howMany(RDFTriple a) {
		long count = 0;
		
		CloseableIteratorWithoutException<Atom> subs_atoms = data.match(a);
		while (subs_atoms.hasNext()) {
			subs_atoms.next();
			++count;
		}
		
		return count;
	}

	@Override
	public long size() {
		return data.size();
	}

	@Override
	public Collection<RDFTriple> getAtoms() {
		return data.getAtoms()
                .map(atom -> new RDFTriple(atom.getTerms()))
                .collect(Collectors.toList());
	}

}
