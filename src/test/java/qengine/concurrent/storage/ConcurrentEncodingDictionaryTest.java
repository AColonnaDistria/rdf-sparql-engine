/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package qengine.concurrent.storage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fr.boreal.model.logicalElements.api.Literal;
import fr.boreal.model.logicalElements.api.Term;
import fr.boreal.model.logicalElements.factory.impl.SameObjectTermFactory;

class ConcurrentEncodingDictionaryTest {
    private static final Literal<String> OBJ1 = SameObjectTermFactory.instance().createOrGetLiteral("obj1");
    private static final Literal<String> OBJ2 = SameObjectTermFactory.instance().createOrGetLiteral("obj2");
    private static final Literal<String> OBJ3 = SameObjectTermFactory.instance().createOrGetLiteral("obj3");
    private static final Literal<String> OBJ4 = SameObjectTermFactory.instance().createOrGetLiteral("obj4");
    private static final Literal<String> OBJ5 = SameObjectTermFactory.instance().createOrGetLiteral("obj5");
    private static final Literal<String> OBJ6 = SameObjectTermFactory.instance().createOrGetLiteral("obj6");
    private static final Literal<String> OBJ7 = SameObjectTermFactory.instance().createOrGetLiteral("obj7");

	@Test
	public void test() {
		ConcurrentEncodingDictionary dictionary = new ConcurrentEncodingDictionary();
		
		// put -> encode
		Integer id1 = dictionary.encode(OBJ1);
		Integer id2 = dictionary.encode(OBJ2);
		Integer id3 = dictionary.encode(OBJ3);
		Integer id4 = dictionary.encode(OBJ4);
		Integer id5 = dictionary.encode(OBJ5);
		Integer id6 = dictionary.encode(OBJ6);
		Integer id7 = dictionary.encode(OBJ7);

		// size -> getSize
        // Taille = 7
        assertEquals(7, dictionary.getSize());

        // getId -> decode
        // Chaque element veriication id
        
        // Non-applicable test : method not avalaible
        /*
        assertEquals(id1, dictionary.getId(OBJ2));
        assertEquals(id2, dictionary.getId(OBJ2));
        assertEquals(id3, dictionary.getId(OBJ3));
        assertEquals(id4, dictionary.getId(OBJ4));
        assertEquals(id5, dictionary.getId(OBJ5));
        assertEquals(id6, dictionary.getId(OBJ6));
        assertEquals(id7, dictionary.getId(OBJ7));
        */

        // Dans l'autre sens
        assertEquals(OBJ1, dictionary.decode(id1));
        assertEquals(OBJ2, dictionary.decode(id2));
        assertEquals(OBJ3, dictionary.decode(id3));
        assertEquals(OBJ4, dictionary.decode(id4));
        assertEquals(OBJ5, dictionary.decode(id5));
        assertEquals(OBJ6, dictionary.decode(id6));
        assertEquals(OBJ7, dictionary.decode(id7));

        // Test contient
        assertTrue(dictionary.hasTerm(OBJ1));
        assertTrue(dictionary.hasTerm(OBJ2));
        assertTrue(dictionary.hasTerm(OBJ3));
        assertTrue(dictionary.hasTerm(OBJ4));
        assertTrue(dictionary.hasTerm(OBJ5));
        assertTrue(dictionary.hasTerm(OBJ6));
        assertTrue(dictionary.hasTerm(OBJ7));

        assertTrue(dictionary.hasId(id1));
        assertTrue(dictionary.hasId(id2));
        assertTrue(dictionary.hasId(id3));
        assertTrue(dictionary.hasId(id4));
        assertTrue(dictionary.hasId(id5));
        assertTrue(dictionary.hasId(id6));
        assertTrue(dictionary.hasId(id7));
	}
}
