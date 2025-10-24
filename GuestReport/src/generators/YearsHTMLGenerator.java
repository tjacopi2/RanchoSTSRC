package generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import loaders.YearSummary;

public class YearsHTMLGenerator {
	
	protected static final String TableDataTag = "%TABLEROWS%";
	protected static final String DateTag = "%DATE%";
	protected static final String TimePeriodTag = "%TIME_PERIOD%";
	protected static final String StringDateTag = "%STRING_DATE%";
	protected static final String TemplateHtmlFileName = "usageSummary.html";
	protected static final String DayNotInMonth = "    <td><span class=\"date\">&nbsp;</span></td>\n";

	
	private String templateData = null;
	
	public YearsHTMLGenerator(File yearsHTMLtemplate) throws IOException {
		templateData = Files.readString(yearsHTMLtemplate.toPath());
	}

	public String generate(Map<Integer, YearSummary> yearsSummary, Map<Integer, File> yearToFileMap) {
		StringBuffer sb = new StringBuffer();
		
		// We want to sort the years so they appear in order
		ArrayList<Integer> yearList = new ArrayList<Integer>(yearsSummary.keySet());
		Collections.sort(yearList);
 
		for (Integer year: yearList) {
			YearSummary yearSummary = yearsSummary.get(year);
			sb.append("  <tr>\n");   // start row
			int totalHOAMembers = yearSummary.getTotalHOAMembers();
			int totalAMMembers = yearSummary.getTotalAMMembers();
			int totalHOAGuests = yearSummary.getTotalHOAGuests();
			int totalAMGuests = yearSummary.getTotalAMGuests();

			sb.append("<td>");
			sb.append("<a href=\"./" + yearToFileMap.get(year).getName() + "\">");
			sb.append(year.toString());
			sb.append("</a>");
			sb.append("</td>");

			sb.append("<td>");
			sb.append(String.format("%,d / %,d", totalHOAMembers + totalAMMembers, totalHOAGuests + totalAMGuests));
			sb.append("</td>");

			sb.append("<td>");
			sb.append(String.format("%,d / %,d", totalHOAMembers, totalHOAGuests));
			sb.append("</td>");

			sb.append("<td>");
			sb.append(String.format("%,d / %,d", totalAMMembers, totalAMGuests));
			sb.append("</td>");
			sb.append("  </tr>\n");   // end row
			//sb.append("  </tr>\n");   // start row
		}
		
		return templateData.replaceAll(DateTag, "All Years").replace(TimePeriodTag, "Year").replace(TableDataTag, sb.toString());
	}
	
	public static File generateOutputFilename(File outputDirectory) {
		return new File(outputDirectory, TemplateHtmlFileName);
	}
	
}
