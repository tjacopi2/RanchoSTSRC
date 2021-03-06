import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import generators.DayHTMLGenerator;
import generators.MonthGraphHTMLGenerator;
import generators.MonthHTMLGenerator;
import generators.YearHTMLGenerator;
import loaders.AMLoader;
import loaders.DataLoader;
import loaders.LogFileNameFilter;
import loaders.LogFileSummary;

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
	    if (!logDirectory.isDirectory() || logDirectory.listFiles(new LogFileNameFilter()).length == 0) {
	    	System.err.println("Error.....no log files in directory " + logDirectory.getCanonicalPath());
	    	System.exit(4);
	    } else {
	        System.out.println("Log directory: " + logDirectory.getCanonicalPath());	    	
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

		// Load associate member addresses
		Set<String> amAddresses = AMLoader.LoadData(inputDirectory);
		
		// Generate individual day & month html files
		LogFileSummary aSummary = null;
		Map<Integer, File> monthFileMap = new HashMap<Integer, File>();
		Map<Integer, List<LogFileSummary>> logFileMap = DataLoader.LoadData(logDirectory, amAddresses);
		DayHTMLGenerator dayGenerator = new DayHTMLGenerator(inputDayHeaderTemplateFile);
		MonthGraphHTMLGenerator monthGraphGenerator = new MonthGraphHTMLGenerator(inputMonthGraphHeaderTemplateFile);
		MonthHTMLGenerator monthGenerator = new MonthHTMLGenerator(inputMonthHeaderTemplateFile, monthGraphGenerator);
		for (Integer month : logFileMap.keySet()) {
			List<LogFileSummary> summaryList = logFileMap.get(month);
			for (LogFileSummary summary : summaryList) {
				String html = dayGenerator.generate(summary);
				File outputFile = dayGenerator.generateOutputFilename(outputDirectory, summary);
				outputFile.delete();
				Files.writeString(outputFile.toPath(), html, StandardOpenOption.CREATE);
				summary.setHtmlDetailFile(outputFile);
				if (aSummary == null) {
					aSummary = summary;
				}
			}
			String monthHtml = monthGenerator.generate(summaryList);
			File outputMonthFile = monthGenerator.generateOutputFilename(outputDirectory, summaryList.get(0));
			outputMonthFile.delete();
			Files.writeString(outputMonthFile.toPath(), monthHtml, StandardOpenOption.CREATE);
			monthFileMap.put(month, outputMonthFile);
		}
		
		YearHTMLGenerator yearGenerator = new YearHTMLGenerator(inputYearHeaderTemplateFile);
		String yearHtml = yearGenerator.generate(logFileMap, monthFileMap);
		File outputYearFile = yearGenerator.generateOutputFilename(outputDirectory, aSummary);
		outputYearFile.delete();
		Files.writeString(outputYearFile.toPath(), yearHtml, StandardOpenOption.CREATE);
		System.out.println("Start at " + outputYearFile.getCanonicalPath());	
		
		Desktop.getDesktop().browse(outputYearFile.toURI());   // And lets be fancy and open the browser to display the top page
	}

}
