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
