package generators;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import loaders.AMLoader;
import loaders.DataLoader;
import loaders.DaySummary;
import loaders.MonthSummary;
import utils.TestUtils;

class TestDayHTMLGenerator {

	@Test
	void testGenerate() throws IOException, ParseException {
		File inputDirectory = new File("input\\");
		File amInputDirectory = new File("testData\\amList\\");
		File inputTemplateFile = new File(inputDirectory, "dayHeader.html");
		File inputLogFile = new File("testData\\TestDataAll\\log2021-03-17.csv");
		Set<String> amAddresses = AMLoader.LoadData(amInputDirectory);

		Map<Integer, MonthSummary> summaryMapByMonth = new HashMap<Integer, MonthSummary>();
		DataLoader.LoadFile(amAddresses, summaryMapByMonth, inputLogFile);
		Map<Integer, DaySummary> monthSummaryMap = summaryMapByMonth.get(3);
		assertNotNull(monthSummaryMap, "Could not find data for March");
		DaySummary summary = monthSummaryMap.get(17);
		assertNotNull(summary, "Could not find data for the 17th of March");

		DayHTMLGenerator dayGenerator = new DayHTMLGenerator(inputTemplateFile);
		
		String html = dayGenerator.generate(summary);
		
		File outputFile = new File("testData\\output\\TestDayHTMLGenerator.html");
		outputFile.delete();
		Files.writeString(outputFile.toPath(), html, StandardOpenOption.CREATE);
		
		List<Integer> tdOpen = TestUtils.CountSubstrings(html, "<td");
		List<Integer> tdClose =  TestUtils.CountSubstrings(html, "/td");
		List<Integer> trOpen = TestUtils.CountSubstrings(html, "<tr");
		List<Integer> trClose = TestUtils.CountSubstrings(html, "/tr");
		
		assertEquals(12, tdOpen.size());
		assertEquals(12, tdClose.size());
		assertEquals(5, trOpen.size());
		assertEquals(5, trClose.size());
		
		List<Integer> amIndicators = TestUtils.CountSubstrings(html, DayHTMLGenerator.AMIndicator);
		assertEquals(1, amIndicators.size());
	}

	@Test
	void testGenerateOutputFilename() throws IOException, ParseException {
		File inputDirectory = new File("input\\");
		File amInputDirectory = new File("testData\\amList\\");
		File outputDirectory = new File(".\\output");
		File inputTemplateFile = new File(inputDirectory, "dayHeader.html");
		File inputLogFile = new File("testData\\TestDataAll\\log2021-03-17.csv");
		Set<String> amAddresses = AMLoader.LoadData(amInputDirectory);
		
		Map<Integer, MonthSummary> summaryMapByMonth = new HashMap<Integer, MonthSummary>();
		DataLoader.LoadFile(amAddresses, summaryMapByMonth, inputLogFile);
		Map<Integer, DaySummary> monthSummaryMap = summaryMapByMonth.get(3);
		assertNotNull(monthSummaryMap, "Could not find data for March");
		DaySummary summary = monthSummaryMap.get(17);
		assertNotNull(summary, "Could not find data for the 17th of March");

		DayHTMLGenerator dayGenerator = new DayHTMLGenerator(inputTemplateFile);
		
		File outputFile = dayGenerator.generateOutputFilename(outputDirectory, summary);
		assertTrue(outputFile.getName().contains("03-17-2021"));
		assertTrue(outputFile.getName().startsWith("day"));
		assertTrue(outputFile.getName().endsWith(".html"));
		
	}
}
