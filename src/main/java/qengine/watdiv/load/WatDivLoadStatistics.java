/*
Author: Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>
Copyright 2026 Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package qengine.watdiv.load;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

public class WatDivLoadStatistics {
	private HashMap<String, Integer> results_numberOfElementsPerAnswersBin;
	private String exportFilePath_AnswersBin;

	public WatDivLoadStatistics(
			HashMap<String, Integer> results_numberOfElementsPerAnswersBin,
			String exportFilePath_AnswersBin) {
		this.results_numberOfElementsPerAnswersBin = results_numberOfElementsPerAnswersBin;
		
		this.exportFilePath_AnswersBin = exportFilePath_AnswersBin;
	}
	
	public void exportAll() throws IOException {
		this.exportAnswersBin();
	}

	public void exportAnswersBin() throws IOException {
		String exportFilePath = this.exportFilePath_AnswersBin;
		
		JsonObjectBuilder builder = Json.createObjectBuilder();
		for (Map.Entry<String, Integer> entry : this.results_numberOfElementsPerAnswersBin.entrySet()) {
		    builder.add(entry.getKey(), entry.getValue());
		}
		
		try (FileWriter writer = new FileWriter(exportFilePath)) {
		    writer.write(builder.build().toString());
		}
	}
}
