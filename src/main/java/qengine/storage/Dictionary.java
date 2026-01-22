/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package qengine.storage;

import java.util.ArrayList;
import java.util.HashMap;

import fr.boreal.model.logicalElements.api.Term;

public class Dictionary {
	HashMap<Term, Integer> term2id;
	ArrayList<Term> id2term;
	int count;
	
	public Dictionary() {
		this.term2id = new HashMap<Term, Integer>();
		this.id2term = new ArrayList<Term>();
		this.count = 0;
	}
	
	public Integer put(Term term) {
		if (this.containsValue(term)) {
			return this.getId(term);
		}
		
		int id = count;
		++count;
		
		this.term2id.put(term, id);
		this.id2term.add(term);
		
		return id;
	}
	
	public boolean containsValue(Term term) {
		return this.term2id.containsKey(term);
	}

	public boolean containsId(Integer id) {
		return 0 <= id && id < this.id2term.size();
	}
	
	public Integer getId(Term term) {
		return this.term2id.get(term);
	}

	public Integer getId(Term term, Integer defaultValue) {
		Integer id = this.term2id.get(term);
		
		return (id != null) ? id : defaultValue;
	}
	
	public Integer tryGetIdOrCreate(Term term) {
		Integer id = getId(term);
		if (id == null) {
			id = this.put(term);
		}
		
		return id;
	}

	public Term getValue(int id) {
		return this.id2term.get(id);
	}
	
	public int size() {
		return this.term2id.size();
	}
}
