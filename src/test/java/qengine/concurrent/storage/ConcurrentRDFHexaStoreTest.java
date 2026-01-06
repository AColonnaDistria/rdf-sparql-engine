package qengine.concurrent.storage;

import fr.boreal.model.logicalElements.api.*;
import fr.boreal.model.logicalElements.factory.impl.SameObjectTermFactory;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import fr.boreal.model.query.api.Query;

import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.parser.RDFTriplesParser;
import qengine.parser.StarQuerySparQLParser;
import qengine.storage.RDFStorage;
import qengine.storage.RDFStorageTest;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe {@link ConcurrentRDFHexaStore}.
 */
public class ConcurrentRDFHexaStoreTest extends RDFStorageTest {
    public RDFStorage newInstance() {
    	return new ConcurrentRDFHexaStore();
    }
}
