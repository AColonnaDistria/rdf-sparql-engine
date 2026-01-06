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
