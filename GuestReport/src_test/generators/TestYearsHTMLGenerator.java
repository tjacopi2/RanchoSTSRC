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
import loaders.YearSummary;
import utils.TestUtils;

class TestYearsHTMLGenerator {

	private static final String DummyYearFileName2021 = "dummy1.html";
	private static final String DummyYearFileName2022 = "dummy2.html";
	private static final String DummyYearFileName2023 = "dummy3.html";
	private static final String DummyYearFileName2024 = "dummy4.html";
	private static final String DummyYearFileName2025 = "dummy5.html";

	@Test
	void testGenerate() throws IOException, ParseException {
		File inputDirectory = new File("input\\");
		File amInputDirectory = new File("testData\\amList\\");
		File inputTemplateFile = new File(inputDirectory, "yearHeader.html");    // We can reuse the same template as individual years
		File inputLogDirectory = new File("testData\\TestDataCombinedLogFileMultYears");

		Set<String> amAddresses = AMLoader.LoadData(amInputDirectory);
		List<File> logFiles = new ArrayList<File>();
		DataLoader.FetchLogFiles(inputLogDirectory, logFiles);
		
		Map<Integer, YearSummary> yearSummaries = DataLoader.LoadData(logFiles, amAddresses);
		
		// Validate the years returned
		assertEquals(5, yearSummaries.size());
		YearSummary yearSummary = yearSummaries.get(2021);
		assertNotNull(yearSummary, "Could not find data for 2021.  Instead data was for year " + yearSummaries.keySet());
		
		YearsHTMLGenerator yearsGenerator = new YearsHTMLGenerator(inputTemplateFile);
		Map<Integer, File> dummyYearHtmlMap = new HashMap<Integer, File>();
		dummyYearHtmlMap.put(2021, new File(DummyYearFileName2021));
		dummyYearHtmlMap.put(2022, new File(DummyYearFileName2022));
		dummyYearHtmlMap.put(2023, new File(DummyYearFileName2023));
		dummyYearHtmlMap.put(2024, new File(DummyYearFileName2024));
		dummyYearHtmlMap.put(2025, new File(DummyYearFileName2025));
		
		String html = yearsGenerator.generate(yearSummaries, dummyYearHtmlMap);  
		
		File outputFile = new File("testData\\output\\TestYearsHTMLGenerator.html");
		outputFile.delete();
		Files.writeString(outputFile.toPath(), html, StandardOpenOption.CREATE);
		
		
		List<Integer> tdOpen = TestUtils.CountSubstrings(html, "<td");
		List<Integer> tdClose =  TestUtils.CountSubstrings(html, "/td");
		List<Integer> trOpen = TestUtils.CountSubstrings(html, "<tr");
		List<Integer> trClose = TestUtils.CountSubstrings(html, "/tr");
		
		assertEquals(tdOpen.size(), tdClose.size());
		assertEquals(trOpen.size(), trClose.size());
		assertTrue(html.contains(DummyYearFileName2021));
		assertTrue(html.contains(DummyYearFileName2022));
		assertTrue(html.contains(DummyYearFileName2023));
		assertTrue(html.contains(DummyYearFileName2024));
		assertTrue(html.contains(DummyYearFileName2025));
	}
	
	@Test
	void testGenerateOutputFilename() throws IOException, ParseException {
		File outputDirectory = new File(".\\output");
		
		File outputFile = YearsHTMLGenerator.generateOutputFilename(outputDirectory);
		assertTrue(outputFile.getName().contains("Summary"));
		assertTrue(outputFile.getName().endsWith(".html"));
		
	}
	
}
