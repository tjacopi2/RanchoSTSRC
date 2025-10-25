import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import generators.DayHTMLGenerator;
import generators.MonthGraphHTMLGenerator;
import generators.MonthHTMLGenerator;
import generators.YearHTMLGenerator;
import generators.YearsHTMLGenerator;
import loaders.HOALoader;
import loaders.DataLoader;
import loaders.DaySummary;
import loaders.MonthSummary;
import loaders.YearSummary;

public class GuestReport {
	
	public static final String DAY_HEADER_FILENAME = "dayHeader.html";
	public static final String MONTH_HEADER_FILENAME = "monthHeader.html";
	public static final String MONTH_GRAPH_HEADER_FILENAME = "monthGraphHeader.html";
	public static final String YEAR_HEADER_FILENAME = "yearHeader.html";

	public static void main(String[] args) throws IOException {
		File logDirectory = new File("entryLogs");
		if (args.length > 0 && args[0] != null) {
			File possibleLogDirectory = new File(args[0]);
			if (possibleLogDirectory.exists() && possibleLogDirectory.isDirectory()) {
               logDirectory = possibleLogDirectory;
			}
		}
		List<File> logFiles = new ArrayList<File>();
		DataLoader.FetchLogFiles(logDirectory, logFiles);
	    if (logFiles.size() == 0) {
	    	System.err.println("Error.....no log files in directory " + logDirectory.getCanonicalPath());
	    	System.exit(4);
	    } else {
	        System.out.println("Log directory: " + logDirectory.getCanonicalPath());	 
	        System.out.println("Have found " + logFiles.size() + " log files in directory and subdirectories.");	 
	    }

	    // Validate we can see the files in the input directory
		File inputDirectory = new File("input");
		File inputDayHeaderTemplateFile = new File(inputDirectory, DAY_HEADER_FILENAME);
		if (!inputDayHeaderTemplateFile.exists()) {
			System.err.println("Error, cannot find " + inputDayHeaderTemplateFile.getCanonicalPath());
			System.exit(4);
		}
		File inputMonthHeaderTemplateFile = new File(inputDirectory, MONTH_HEADER_FILENAME);
		if (!inputMonthHeaderTemplateFile.exists()) {
			System.err.println("Error, cannot find " + inputMonthHeaderTemplateFile.getCanonicalPath());
			System.exit(4);
		}
		File inputMonthGraphHeaderTemplateFile = new File(inputDirectory, MONTH_GRAPH_HEADER_FILENAME);
		if (!inputMonthGraphHeaderTemplateFile.exists()) {
			System.err.println("Error, cannot find " + inputMonthGraphHeaderTemplateFile.getCanonicalPath());
			System.exit(4);
		}
		File inputYearHeaderTemplateFile = new File(inputDirectory, YEAR_HEADER_FILENAME);
		if (!inputYearHeaderTemplateFile.exists()) {
			System.err.println("Error, cannot find " + inputYearHeaderTemplateFile.getCanonicalPath());
			System.exit(4);
		}
		
		File outputDirectory = new File("output");
		if (!outputDirectory.exists() ) {
			outputDirectory.mkdir();
		}
		System.out.println("Output html files will be written to " + outputDirectory.getCanonicalPath());

		// Load hoa member addresses
		Set<String> hoaAddresses = HOALoader.LoadData(inputDirectory);
		
		// Generate individual day & month html files
		Map<Integer, YearSummary> yearSummaries = DataLoader.LoadData(logFiles, hoaAddresses);
		
		DayHTMLGenerator dayGenerator = new DayHTMLGenerator(inputDayHeaderTemplateFile);
		MonthGraphHTMLGenerator monthGraphGenerator = new MonthGraphHTMLGenerator(inputMonthGraphHeaderTemplateFile);
		MonthHTMLGenerator monthGenerator = new MonthHTMLGenerator(inputMonthHeaderTemplateFile, monthGraphGenerator);
		YearHTMLGenerator yearGenerator = new YearHTMLGenerator(inputYearHeaderTemplateFile);
		YearsHTMLGenerator yearsGenerator = new YearsHTMLGenerator(inputYearHeaderTemplateFile);
		
		Map<Integer, File> yearFileMap = new HashMap<Integer, File>();
		for (Integer year : yearSummaries.keySet()) {
			YearSummary yearSummary = yearSummaries.get(year);			
			DaySummary aSummary = null;
			Map<Integer, File> monthFileMap = new HashMap<Integer, File>();
			for (Integer month : yearSummary.keySet()) {
				MonthSummary dayOfMonthSummaryMap = yearSummary.get(month);
				for (DaySummary summary : dayOfMonthSummaryMap.values()) {
					String html = dayGenerator.generate(summary);
					File outputFile = dayGenerator.generateOutputFilename(outputDirectory, summary);
					outputFile.delete();
					Files.writeString(outputFile.toPath(), html, StandardOpenOption.CREATE);
					summary.setHtmlDetailFile(outputFile);
					if (aSummary == null) {
						aSummary = summary;
					}
				}
				String monthHtml = monthGenerator.generate(dayOfMonthSummaryMap);
				DaySummary anyDaySummary = dayOfMonthSummaryMap.values().iterator().next();
				File outputMonthFile = monthGenerator.generateOutputFilename(outputDirectory, anyDaySummary);
				outputMonthFile.delete();
				Files.writeString(outputMonthFile.toPath(), monthHtml, StandardOpenOption.CREATE);
				monthFileMap.put(month, outputMonthFile);
			}

			String yearHtml = yearGenerator.generate(yearSummary, monthFileMap);
			File outputYearFile = yearGenerator.generateOutputFilename(outputDirectory, aSummary);
			outputYearFile.delete();
			Files.writeString(outputYearFile.toPath(), yearHtml, StandardOpenOption.CREATE);
			yearFileMap.put(year, outputYearFile);
		}
		String yearsHtml = yearsGenerator.generate(yearSummaries, yearFileMap);
		File outputYearsFile = YearsHTMLGenerator.generateOutputFilename(outputDirectory);
		outputYearsFile.delete();
		Files.writeString(outputYearsFile.toPath(), yearsHtml, StandardOpenOption.CREATE);
		
		System.out.println("Start at " + outputYearsFile.getCanonicalPath());	
		
		Desktop.getDesktop().browse(outputYearsFile.toURI());   // And lets be fancy and open the browser to display the top page
	}

}
