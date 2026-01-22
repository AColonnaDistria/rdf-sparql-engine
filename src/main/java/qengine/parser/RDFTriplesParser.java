/*
Authors: Federico Ulliana <federico.ulliana@inria.fr> & Guillaume Pérution-Khili
*/

package qengine.parser;

import fr.boreal.io.api.Parser;
import fr.boreal.io.rdf.RDFParser;
import fr.boreal.io.rdf.RDFTranslationMode;
import fr.boreal.model.logicalElements.api.Atom;
import fr.boreal.model.logicalElements.api.Predicate;
import fr.boreal.model.logicalElements.factory.impl.SameObjectPredicateFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import qengine.model.RDFTriple;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * Parser pour transformer des triplets RDF en RDFAtom.
 */
public class RDFTriplesParser implements Parser<RDFTriple> {

    private static final Predicate TRIPLE_PREDICATE = SameObjectPredicateFactory.instance()
            .createOrGetPredicate("triple", 3);

    private final RDFParser parser;

    private RDFTriple nextRDFTriple;

    private boolean runHasNext = false;
    private boolean noMoreNext = false;

    public RDFTriplesParser(RDFParser parser) {
        this.parser = parser;
    }

    public RDFTriplesParser(File file) throws IOException {
        this(new FileReader(file), getRDFFormat(file));
    }

    public RDFTriplesParser(Reader reader, RDFFormat format) {
        // Utilisation explicite du mode RawRDFTranslator
        this.parser = new RDFParser(reader, format, null, RDFTranslationMode.Raw);
    }

    public RDFTriplesParser(String rdfFilePath) throws FileNotFoundException {
        this(new FileReader(rdfFilePath), getFormat(rdfFilePath));
    }

    @Override
    public boolean hasNext() {
        if (noMoreNext) {
            return false;
        }
        boolean notFoundNext = true;
        while (notFoundNext) {
            if (!parser.hasNext()) {
                noMoreNext = true;
                return false;
            } else {
                Object canidateNext = parser.next();
                if (canidateNext instanceof Atom atom) {
                    nextRDFTriple = convertToRDFAtom(atom);
                    runHasNext = true;
                    return true;
                } else {
                    //skip non-triple statements like prefixes
                    // TODO handle these cases
                }
            }
        }
        noMoreNext = true;
        return false;
    }

    @Override
    public RDFTriple next() {
        if (!runHasNext) {
            // this was a risky call to next() without calling hasNext() first
            if (!hasNext()) {
                // ... and indeed there is not such a next element
                throw new IllegalArgumentException("called next() but there is no such element");
            }
        }

        runHasNext = false;
        return nextRDFTriple;

    }

    @Override
    public void close() {
        parser.close();
    }

    /**
     * Retourne un flux de tous les triplets RDF parsés.
     *
     * @return un flux de RDFAtom
     */
    public Stream<RDFTriple> getRDFAtoms() {
        return this.streamParsedObjects(RDFTriple.class);
    }

    private static RDFFormat getRDFFormat(File file) {
        return org.eclipse.rdf4j.rio.Rio.getParserFormatForFileName(file.getName()).orElse(RDFFormat.TURTLE);
    }

    /**
     * Convertit un atome Integraal standard en RDFTriple.
     *
     * @param atom L'atome à convertir
     * @return L'instance correspondante de RDFTriple
     */
    private RDFTriple convertToRDFAtom(Atom atom) {
        if (atom.getTerms().length != 3) {
            throw new IllegalArgumentException("Un RDFAtom doit contenir exactement trois termes.");
        }

        if (!TRIPLE_PREDICATE.equals(atom.getPredicate())) {
            throw new IllegalArgumentException("Le prédicat de l'atome n'est pas 'triple'.");
        }

        return new RDFTriple(atom.getTerms()[0], atom.getTerms()[1], atom.getTerms()[2]);
    }

    private static RDFFormat getFormat(String rdfFile) {
        if (rdfFile.endsWith(".nt")) {
            return RDFFormat.N3;
        } else {
            return RDFFormat.RDFXML;
        }
    }
}


