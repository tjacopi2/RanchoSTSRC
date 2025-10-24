package generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import loaders.AMLoader;
import loaders.DataLoader;
import loaders.DaySummary;
import loaders.MonthSummary;
import loaders.YearSummary;
import utils.TestUtils;

class TestYearHTMLGenerator {

	private static final String DummyMonthFileName1 = "dummy1.html";
	private static final String DummyMonthFileName2 = "dummy2.html";

	@Test
	void testGenerate() throws IOException, ParseException {
		File inputDirectory = new File("input\\");
		File amInputDirectory = new File("testData\\amList\\");
		File inputTemplateFile = new File(inputDirectory, "yearHeader.html");
		File inputLogDirectory = new File("testData\\TestDataAll");

		Set<String> amAddresses = AMLoader.LoadData(amInputDirectory);
		List<File> logFiles = new ArrayList<File>();
		DataLoader.FetchLogFiles(inputLogDirectory, logFiles);
		
		Map<Integer, YearSummary> yearSummaries = DataLoader.LoadData(logFiles, amAddresses);
		
		// Validate the year returned
		assertEquals(1, yearSummaries.size());
		YearSummary yearSummary = yearSummaries.get(2021);
		assertNotNull(yearSummary, "Could not find data for 2021.  Instead data was for year " + yearSummaries.keySet());
		
		YearHTMLGenerator yearGenerator = new YearHTMLGenerator(inputTemplateFile);
		Map<Integer, File> dummyMonthHtmlMap = new HashMap<Integer, File>();
		dummyMonthHtmlMap.put(2, new File(DummyMonthFileName1));
		dummyMonthHtmlMap.put(3, new File(DummyMonthFileName2));
		
		String html = yearGenerator.generate(yearSummary, dummyMonthHtmlMap);  
		
		File outputFile = new File("testData\\output\\TestYearHTMLGenerator.html");
		outputFile.delete();
		Files.writeString(outputFile.toPath(), html, StandardOpenOption.CREATE);
		
		
		List<Integer> tdOpen = TestUtils.CountSubstrings(html, "<td");
		List<Integer> tdClose =  TestUtils.CountSubstrings(html, "/td");
		List<Integer> trOpen = TestUtils.CountSubstrings(html, "<tr");
		List<Integer> trClose = TestUtils.CountSubstrings(html, "/tr");
		
		assertEquals(tdOpen.size(), tdClose.size());
		assertEquals(trOpen.size(), trClose.size());
		assertTrue(html.contains(DummyMonthFileName1));
		assertTrue(html.contains(DummyMonthFileName2));
	}
	
	@Test
	void testGenerateOutputFilename() throws IOException, ParseException {
		File inputDirectory = new File("input\\");
		File outputDirectory = new File(".\\output");
		File amInputDirectory = new File("testData\\amList\\");
		File inputTemplateFile = new File(inputDirectory, "yearHeader.html");
		File inputLogFile = new File("testData\\TestDataAll\\log2021-03-17.csv");
		Set<String> amAddresses = AMLoader.LoadData(amInputDirectory);
		
		Map<Integer, YearSummary> yearSummaries = new HashMap<Integer, YearSummary>();
		DataLoader.LoadFile(amAddresses, yearSummaries, inputLogFile);
		
		// Validate the year returned
		assertEquals(1, yearSummaries.size());
		YearSummary yearSummary = yearSummaries.get(2021);
		assertNotNull(yearSummary, "Could not find data for 2021.  Instead data was for year " + yearSummaries.keySet());
		
		MonthSummary monthSummaryMap = yearSummary.get(3);
		assertNotNull(monthSummaryMap, "Could not find data for March");
		DaySummary summary = monthSummaryMap.get(17);
		assertNotNull(summary, "Could not find data for the 17th of March");

		YearHTMLGenerator yearGenerator = new YearHTMLGenerator(inputTemplateFile);
		
		File outputFile = yearGenerator.generateOutputFilename(outputDirectory, summary);
		assertTrue(outputFile.getName().contains("2021"));
		assertTrue(outputFile.getName().startsWith("summary"));
		assertTrue(outputFile.getName().endsWith(".html"));
		
	}
	
}
