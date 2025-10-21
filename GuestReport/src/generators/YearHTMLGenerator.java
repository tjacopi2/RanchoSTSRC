package generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import loaders.DaySummary;

public class YearHTMLGenerator {
	
	protected static final String TableDataTag = "%TABLEROWS%";
	protected static final String DateTag = "%DATE%";
	protected static final String StringDateTag = "%STRING_DATE%";
	protected static final String TemplateHtmlFileName = "summary_" + DateTag + ".html";
	protected static final String DayNotInMonth = "    <td><span class=\"date\">&nbsp;</span></td>\n";

	
	private String templateData = null;
	
	public YearHTMLGenerator(File yearHTMLtemplate) throws IOException {
		templateData = Files.readString(yearHTMLtemplate.toPath());
	}

	public String generate(Map<Integer, Map<Integer, DaySummary>> logSummaryMap, Map<Integer, File> monthToFileMap) {
		int year = -1;
		String[] monthNames = new DateFormatSymbols().getMonths();
		StringBuffer sb = new StringBuffer();
 
		for (int i = 1; i<13; i++) {
		//	sb.append("  <tr>\n");   // start row

			Map<Integer, DaySummary> monthLogRecords = logSummaryMap.get(i);
			if (monthLogRecords != null) {
				sb.append("  <tr>\n");   // start row
				if (year < 0) {
					year = monthLogRecords.values().iterator().next().getDate().get(Calendar.YEAR);
				}
				YearSummaryData overallSummary = summerizeData(monthLogRecords);
				
				sb.append("<td>");
				sb.append("<a href=\"./" + monthToFileMap.get(i).getName() + "\">");
				sb.append(monthNames[i-1]);
				sb.append("</a>");
				sb.append("</td>");
				
				sb.append("<td>");
                sb.append(String.format("%d/%d", overallSummary.totalHOAMembers + overallSummary.totalAMMembers, overallSummary.totalHOAGuests + overallSummary.totalAMGuests));
                sb.append("</td>");
                
                sb.append("<td>");
                sb.append(String.format("%d/%d", overallSummary.totalHOAMembers, overallSummary.totalHOAGuests));
                sb.append("</td>");
                
                sb.append("<td>");
                sb.append(String.format("%d/%d", overallSummary.totalAMMembers, overallSummary.totalAMGuests));
                sb.append("</td>");
            	sb.append("  </tr>\n");   // end row
			} else {
				//sb.append("<td></td><td></td><td></td><td></td>");
			}
			//sb.append("  </tr>\n");   // start row
		}
		
		
	/*	
		// add in day of week totals
		sb.append("  <tfoot><tr>\n");
		for (int i = 0; i <7; i++) {
			sb.append("<td>");
			if (dayOfWeekHOAPeople[i] != 0 || dayOfWeekHOAGuests[i] != 0 || dayOfWeekAMGuests[i] != 0 || dayOfWeekAMPeople[i] != 0 )  {
		        sb.append(String.format("<br><br>HOA: %d/%d<br>AM: %d/%d<br>", dayOfWeekHOAPeople[i], dayOfWeekHOAGuests[i],
                		dayOfWeekAMPeople[i], dayOfWeekAMGuests[i]));
			}
			sb.append("</td>");
		}
		sb.append("  </tr></tfoot>\n");
		*/
		
		return templateData.replaceAll(DateTag, String.valueOf(year)).replace(TableDataTag, sb.toString());
	}
	
	public File generateOutputFilename(File outputDirectory, DaySummary summary) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		String strDate = df.format(summary.getDate().getTime());
		
		return new File(outputDirectory, TemplateHtmlFileName.replace(DateTag, strDate));
	}
	
	protected YearSummaryData summerizeData(Map<Integer, DaySummary> summaryMap) {
		YearSummaryData overallSummary = new YearSummaryData();
		for (DaySummary logSummary : summaryMap.values()) {
			overallSummary.totalHOAMembers = overallSummary.totalHOAMembers + logSummary.getTotalHOA();
			overallSummary.totalHOAGuests = overallSummary.totalHOAGuests + logSummary.getTotalHOAGuests();
			overallSummary.totalAMMembers = overallSummary.totalAMMembers + logSummary.getTotalAM();
			overallSummary.totalAMGuests = overallSummary.totalAMGuests + logSummary.getTotalAMGuests();
		}
		return overallSummary;
	}
	
	public static class YearSummaryData {
		public int totalHOAMembers = 0;
		public int totalHOAGuests = 0;
		public int totalAMMembers = 0;
		public int totalAMGuests = 0;
	}
}
