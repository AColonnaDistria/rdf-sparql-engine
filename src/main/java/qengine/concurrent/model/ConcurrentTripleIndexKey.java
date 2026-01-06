
/*
 * Cette classe est l'unique travail de Mehdi Bakhtar et de Théo Foutel-Rodier.
 * Elle n'est présente ici que dans le seul but de comparer les implémentations au travers de tests de performances.
 * 
 * This class is solely the work of Mehdi Bakhtar and Théo Foutel-Rodier.
 * It is present here only as the purpose of comparating implementations for performance tests.
 */

package qengine.concurrent.model;

import java.io.Serializable;

public class ConcurrentTripleIndexKey implements Comparable<ConcurrentTripleIndexKey>, Serializable {
	// Holds the three dictionary encoded values for a RDF triple
	// can be any index permutation
	
	private int[] values;
	
	/*
	 * int values are dictionary encoded values
	 */
	public ConcurrentTripleIndexKey(int firstValue, int secondValue, int thirdValue) {
		values = new int[] {firstValue, secondValue, thirdValue};
	}
	
	public ConcurrentTripleIndexKey(int[] valueArray) {
		if (valueArray.length == 3) {
			values = valueArray;	
		} else {
			throw new IllegalArgumentException(
					"Only an int[] array of size 3 should be used to construct a TripleIndexKey, but it was " + valueArray.length);
		}
		
	}
	
	public int getFirstKey() {
		return values[0];
	}
	
	public int getSecondKey() {
		return values[1];
	}
	
	public int getThirdKey() {
		return values[2];
	}
	
	/*
	 * Returns key at given index
	 */
	public int getKeyAt(int index) {
		if (index < 0 || index >= 3) {
			throw new IllegalArgumentException("index must be between 0 and 2 (included");
		} else {
			return values[index];
		}
	}
	

	@Override
	public int compareTo(ConcurrentTripleIndexKey otherKey) {
		// check keys from first to last
		
		// first key
		int comparator = Integer.compare(values[0], otherKey.getFirstKey());
		if (comparator != 0) {
			return comparator;
		}
		
		// second key
		comparator = Integer.compare(values[1], otherKey.getSecondKey());
		if (comparator != 0) {
			return comparator;
		}
		
		// third key
		return Integer.compare(values[2], otherKey.getThirdKey());
	}
	
	@Override
	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('<');
        boolean first = true;
        for (int value : values) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(String.valueOf(value));
            first = false;
        }
        sb.append('>');
        return sb.toString();
	}
}
