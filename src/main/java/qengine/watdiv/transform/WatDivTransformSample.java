/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package qengine.watdiv.transform;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mapdb.Fun;

import qengine.model.StarQuery;

public class WatDivTransformSample {
	// (number_of_answers_bin)

	// Star queries by number_of_answers_bin
	private HashMap<String, ArrayList<StarQuery>> input;
	private HashMap<String, ArrayList<StarQuery>> results;
	private boolean log;
	
	private int sampleSize;
	
	public WatDivTransformSample(
			HashMap<String, ArrayList<StarQuery>> input,
			int sampleSize) {
		
		this(input, sampleSize, false);
	}

	public WatDivTransformSample(
			HashMap<String, ArrayList<StarQuery>> input,
			int sampleSize,
			boolean log) {
		this.input = input;
		
		this.log = false;
		this.results = new HashMap<>();
		
		this.sampleSize = sampleSize;
	}
	
	public void sample() {
		// by number of answers
		
		for (Map.Entry<String, ArrayList<StarQuery>> entry : this.input.entrySet()) {
			String answers_bin = entry.getKey();
			ArrayList<StarQuery> values = entry.getValue();
			
			Collections.shuffle(values);
			List<StarQuery> sample = values.subList(0, Math.min(this.sampleSize, values.size()));

			if (!this.results.containsKey(answers_bin)) {
				this.results.put(answers_bin, new ArrayList<>(sample));
			}
		}
	}
	
	public HashMap<String, ArrayList<StarQuery>> getResults() {
		return this.results;
	}
}
