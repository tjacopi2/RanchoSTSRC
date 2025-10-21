package generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import loaders.DaySummary;

public class MonthHTMLGenerator {
	
	protected static final int[] numDaysInMonth = {31,28,31,30,31,30,31,31,30,31,30,31};
	protected static final String TableDataTag = "%TABLEROWS%";
	protected static final String DateTag = "%DATE%";
	protected static final String StringDateTag = "%STRING_DATE%";
	protected static final String TemplateHtmlFileName = "month_" + DateTag + ".html";
	protected static final String DayNotInMonth = "    <td><span class=\"date\">&nbsp;</span></td>\n";

	private MonthGraphHTMLGenerator graphGenerator = null;
	private String templateData = null;
	
	public MonthHTMLGenerator(File monthHTMLtemplate, MonthGraphHTMLGenerator aGraphGenerator) throws IOException {
		templateData = Files.readString(monthHTMLtemplate.toPath());
		graphGenerator = aGraphGenerator;
	}

	@SuppressWarnings("unchecked")
	public String generate(Map<Integer, DaySummary> dayToSummary) {
		int[] dayOfWeekHOAPeople = {0,0,0,0,0,0,0};
		int[] dayOfWeekHOAGuests = {0,0,0,0,0,0,0};
		int[] dayOfWeekAMPeople = {0,0,0,0,0,0,0};
		int[] dayOfWeekAMGuests = {0,0,0,0,0,0,0};
		@SuppressWarnings("rawtypes")
		Map[] dayOfWeekPersonCountByHour = { new HashMap<Integer, Integer>(), 
				new HashMap<Integer, Integer>(), new HashMap<Integer, Integer>(), new HashMap<Integer, Integer>(), 
				new HashMap<Integer, Integer>(), new HashMap<Integer, Integer>(), new HashMap<Integer, Integer>()
		};
		
		GregorianCalendar date = dayToSummary.values().iterator().next().getDate();
		int month = date.get(Calendar.MONTH);
		int year = date.get(Calendar.YEAR);

		// Calculate when first day of month is
		// create calendar at day 1 to find which day the month starts.
		GregorianCalendar gc = new GregorianCalendar(year, month, 1);
		int firstDayOfMonth = gc.get(Calendar.DAY_OF_WEEK);
		
		// Put list of logFileSummaries into a map by day
		//Map<Integer, DaySummary> dayToSummary = createDayMap(summaryList);
		
		int dayNumber = 1;
		int dayInWeek = 1;
		int lastDayOfMonth = numDaysInMonth[month];  

		// sb will hold all the html table data (tags)
		StringBuffer sb = new StringBuffer();
		sb.append("  <tr>\n");   // start row
		
		// Fill out the start of the week that is before the first of the month
		for (int i = 1; i < firstDayOfMonth; i++) {
			sb.append(DayNotInMonth);
			dayInWeek++;
		}
		
		while (dayNumber <= lastDayOfMonth) {
			if (dayInWeek > 7) {
				// create new week in table.
				dayInWeek = 1;
				sb.append("  </tr>\n");
				sb.append("  <tr>\n");   // start row
			}

			DaySummary summary = dayToSummary.get(dayNumber);
			sb.append("<td><span class=\"date\">");
			if (summary != null && summary.getHtmlDetailFile() != null) {
				sb.append("<a href=\"./" + summary.getHtmlDetailFile().getName() + "\">");
				sb.append(dayNumber);
				sb.append("</a>");
			} else {
			    sb.append(dayNumber);
			}
			sb.append("</span>");
			if (summary != null ) {
                sb.append(String.format("<br><br>HOA: %d/%d<br>AM: %d/%d<br>", summary.getTotalHOA(), summary.getTotalHOAGuests(),
                		summary.getTotalAM(), summary.getTotalAMGuests()));
                dayOfWeekAMPeople[dayInWeek-1] = dayOfWeekAMPeople[dayInWeek-1] + summary.getTotalAM();
                dayOfWeekAMGuests[dayInWeek-1] = dayOfWeekAMGuests[dayInWeek-1] + summary.getTotalAMGuests();
                dayOfWeekHOAPeople[dayInWeek-1] = dayOfWeekHOAPeople[dayInWeek-1] + summary.getTotalHOA();
                dayOfWeekHOAGuests[dayInWeek-1] = dayOfWeekHOAGuests[dayInWeek-1] + summary.getTotalHOAGuests();
                combineEntryHourMap(dayOfWeekPersonCountByHour[dayInWeek-1], summary.getEntryHourMap());
			}
			sb.append("</td>");
			
			dayInWeek++;
			dayNumber++;
		}
		
		// Fill out the rest of the week
		for (int i = dayInWeek; i < 8; i++) {
			sb.append(DayNotInMonth);
			dayInWeek++;
		}
		
		// end row
		sb.append("  </tr>\n");
		
		// add in day of week totals
		sb.append("  <tfoot><tr>\n");
		for (int i = 0; i <7; i++) {
			sb.append("<td>");
			if (dayOfWeekHOAPeople[i] != 0 || dayOfWeekHOAGuests[i] != 0 || dayOfWeekAMGuests[i] != 0 || dayOfWeekAMPeople[i] != 0 )  {
		        sb.append(String.format("<br><br>HOA: %d/%d<br>AM: %d/%d<br>", dayOfWeekHOAPeople[i], dayOfWeekHOAGuests[i],
                		dayOfWeekAMPeople[i], dayOfWeekAMGuests[i]));
			}
			if (graphGenerator != null && dayOfWeekPersonCountByHour[i].size() > 0) {
				String graphJavascript = graphGenerator.generateGraph(i, dayOfWeekPersonCountByHour[i]);
				sb.append(graphJavascript);
			}
			sb.append("</td>");
		}
		sb.append("  </tr></tfoot>\n");
		
		// Add month and year to template
		String strDate = String.valueOf(month+1) + "-" + String.valueOf(year);
		String monthString = new DateFormatSymbols().getMonths()[month];
		String calendarTitle = monthString + " " + String.valueOf(year);
		
		return templateData.replaceAll(DateTag, strDate).replace(StringDateTag, calendarTitle).replace(TableDataTag, sb.toString());
	}
	
	public File generateOutputFilename(File outputDirectory, DaySummary summary) {
		SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
		String strDate = df.format(summary.getDate().getTime());
		
		return new File(outputDirectory, TemplateHtmlFileName.replace(DateTag, strDate));
	}
	
	protected static void combineEntryHourMap(Map<Integer, Integer> totalsMap, Map<Integer, Integer> valuesMap) {
		for (Integer key : valuesMap.keySet()) {
			Integer totalsValue = totalsMap.get(key);
			if (totalsValue == null) {
				totalsValue = 0;
			}
			totalsMap.put(key, totalsValue + valuesMap.get(key));
		}
	}
}
