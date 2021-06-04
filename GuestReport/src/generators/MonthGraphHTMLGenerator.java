package generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MonthGraphHTMLGenerator {
	
	protected static final String GraphDataTag = "%GRAPHDATA%";
	protected static final String GraphIDTag = "%GRAPH_ID%";
	
	private String templateData = null;

	public MonthGraphHTMLGenerator(File monthGraphHTMLtemplate) throws IOException {
		templateData = Files.readString(monthGraphHTMLtemplate.toPath());
	}

	public String generateGraph(int dayInWeek, Map<Integer, Integer> hourToPersonCountMap) {
		// Get a list of hours that people arrived sorted in ascending order
		List<Integer> keys = new ArrayList<Integer>(hourToPersonCountMap.keySet());
		Collections.sort(keys);
		
		// Start at the earliest hour and go thru the latest one.
		StringBuffer sb = new StringBuffer();
		for (int i = keys.get(0); i < keys.get(keys.size()-1) + 1; i++) {
			// Each data row looks like:  [{v: [8, 0, 0], f: '8 am'}, 27],
			int hour = i;
			String ampm = "am";
			if (i > 11) {
				ampm = "pm";
			}
			if (i > 12) {
				hour = i - 12;
			}
			sb.append("            [{v: [");
			sb.append(i);
			sb.append(", 0, 0], f: '");
			sb.append(hour);
			sb.append(" ");
			sb.append(ampm);
			sb.append("'}, ");
			Integer personCount = hourToPersonCountMap.get(i);
			if (personCount == null) {
				personCount = 0;
			}
			sb.append(personCount);
			sb.append("],\n");
		}
		
		// run thru map creating data array
		// replace day of week		
		// replace data array
		
		return templateData.replaceAll(GraphIDTag, String.valueOf(dayInWeek)).replace(GraphDataTag, sb.toString());
	}
}
