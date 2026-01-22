/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package qengine.storage;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import fr.boreal.model.logicalElements.api.Substitution;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;

/**
 * Contrat pour un système de stockage de données RDF
 */
public interface RDFStorage {

    /**
     * Ajoute un RDFAtom dans le store.
     *
     * @param t le triplet à ajouter
     * @return true si le RDFAtom a été ajouté avec succès, false s'il est déjà présent
     */
    boolean add(RDFTriple t);

    /**
     * @param a atom
     * @return un itérateur de substitutions correspondant aux match des atomes
     *          (i.e., sur quels termes s'envoient les variables)
     */
    Iterator<Substitution> match(RDFTriple a);


    /**
     * @param q star query
     * @return an itérateur de subsitutions décrivrant les réponses à la requete
     */
    Iterator<Substitution> match(StarQuery q);


    /**
     * @param a atom
     * @return
     */
    long howMany(RDFTriple a);


    /**
     * Retourne le nombre d'atomes dans le Store.
     *
     * @return le nombre d'atomes
     */
    long size();

    /**
     * Retourne une collections contenant tous les atomes du store.
     * Utile pour les tests unitaires.
     *
     * @return une collection d'atomes
     */
    Collection<RDFTriple> getAtoms();

    /**
     * Ajoute des RDFAtom dans le store.
     *
     * @param atoms les RDFAtom à ajouter
     * @return true si au moins un RDFAtom a été ajouté, false s'ils sont tous déjà présents
     */
    default boolean addAll(Stream<RDFTriple> atoms) {
        return atoms.map(this::add).reduce(Boolean::logicalOr).orElse(false);
    }

    /**
     * Ajoute des RDFAtom dans le store.
     *
     * @param atoms les RDFAtom à ajouter
     * @return true si au moins un RDFAtom a été ajouté, false s'ils sont tous déjà présents
     */
    default boolean addAll(Collection<RDFTriple> atoms) {
        return this.addAll(atoms.stream());
    }
}
