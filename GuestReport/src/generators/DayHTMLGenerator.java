package generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;

import loaders.LogFileSummary;
import loaders.LogFileSummary.Household;

public class DayHTMLGenerator {
	
	protected static final String TableDataTag = "%TABLEROWS%";
	protected static final String DateTag = "%DATE%";
	protected static final String TemplateHtmlFileName = "day_" + DateTag + ".html";
    protected static final String AMIndicator = " - AM";
	
	private String templateData = null;
	
	public DayHTMLGenerator(File dayHTMLtemplate) throws IOException {
		templateData = Files.readString(dayHTMLtemplate.toPath());
	}

	public String generate(LogFileSummary summary) {
		StringBuffer sb = new StringBuffer();
		for (Household h : summary.getHouseholds()) {
			sb.append("    <tr><td>");
			sb.append(h.getAddress());
			if (h.isAmHousehold()) {
				sb.append(AMIndicator);
			}
			sb.append("</td><td>");
			sb.append(h.getPeople());
			sb.append("</td><td>");
			sb.append(h.getGuests());
			sb.append("</td></tr>\n");
		}
		sb.append("  <tfoot>");
		sb.append("    <tr><td>");
		sb.append("</td><td>");
		sb.append(summary.getTotalPeople());
		sb.append("</td><td>");
		sb.append(summary.getTotalGuests());
		sb.append("</td></tr>\n");
		sb.append("  </tfoot>");
		
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		String strDate = df.format(summary.getDate().getTime());
		
		return templateData.replaceAll(DateTag, strDate).replace(TableDataTag, sb.toString());
	}
	
	public File generateOutputFilename(File outputDirectory, LogFileSummary summary) {
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		String strDate = df.format(summary.getDate().getTime());
		
		return new File(outputDirectory, TemplateHtmlFileName.replace(DateTag, strDate));
	}
}
