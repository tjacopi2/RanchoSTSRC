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
import utils.TestUtils;

class TestMonthHTMLGenerator {


	@Test
	void testGenerate() throws IOException, ParseException {
		File inputDirectory = new File("input\\");
		File amInputDirectory = new File("testData\\amList\\");
		File inputTemplateFile = new File(inputDirectory, "monthHeader.html");
		File inputLogDirectory = new File("testData\\TestDataAll");

		Set<String> amAddresses = AMLoader.LoadData(amInputDirectory);
		List<File> logFiles = new ArrayList<File>();
		DataLoader.FetchLogFiles(inputLogDirectory, logFiles);
		Map<Integer, MonthSummary> logFileMap = DataLoader.LoadData(logFiles, amAddresses); 
		Map<Integer, DaySummary> marchData = logFileMap.get(3);
		assertNotNull(marchData, "Could not find data for March");
		
		MonthHTMLGenerator monthGenerator = new MonthHTMLGenerator(inputTemplateFile, null);
		
		String html = monthGenerator.generate(marchData);    // Use march
		
		File outputFile = new File("testData\\output\\TestMonthHTMLGenerator.html");
		outputFile.delete();
		Files.writeString(outputFile.toPath(), html, StandardOpenOption.CREATE);
		
		
		List<Integer> tdOpen = TestUtils.CountSubstrings(html, "<td");
		List<Integer> tdClose =  TestUtils.CountSubstrings(html, "/td");
		List<Integer> trOpen = TestUtils.CountSubstrings(html, "<tr");
		List<Integer> trClose = TestUtils.CountSubstrings(html, "/tr");
		
		assertEquals(tdOpen.size(), tdClose.size());
		assertEquals(trOpen.size(), trClose.size());
	}
	
	@Test
	void testGenerateOutputFilename() throws IOException, ParseException {
		File inputDirectory = new File("input\\");
		File outputDirectory = new File(".\\output");
		File amInputDirectory = new File("testData\\amList\\");
		File inputTemplateFile = new File(inputDirectory, "monthHeader.html");
		File inputLogFile = new File("testData\\TestDataAll\\log2021-03-17.csv");
		Set<String> amAddresses = AMLoader.LoadData(amInputDirectory);
		
		Map<Integer, MonthSummary> summaryMapByMonth = new HashMap<Integer, MonthSummary>();
		DataLoader.LoadFile(amAddresses, summaryMapByMonth, inputLogFile);
		Map<Integer, DaySummary> monthSummaryMap = summaryMapByMonth.get(3);
		assertNotNull(monthSummaryMap, "Could not find data for March");
		DaySummary summary = monthSummaryMap.get(17);
		assertNotNull(summary, "Could not find data for the 17th of March");

		MonthHTMLGenerator monthGenerator = new MonthHTMLGenerator(inputTemplateFile, null);
		
		File outputFile = monthGenerator.generateOutputFilename(outputDirectory, summary);
		assertTrue(outputFile.getName().contains("03-2021"));
		assertTrue(outputFile.getName().startsWith("month"));
		assertTrue(outputFile.getName().endsWith(".html"));
		
	}
	

	@Test
	void testCombineEntryHourMap() throws IOException, ParseException {
		Map<Integer, Integer> map1 = new HashMap<Integer, Integer>();
		Map<Integer, Integer> map2 = new HashMap<Integer, Integer>();
		Map<Integer, Integer> map3 = new HashMap<Integer, Integer>();
		Map<Integer, Integer> totalsMap = new HashMap<Integer, Integer>();
		
		map1.put(1,2);
		map1.put(2,4);
		map2.put(3,2);
		map2.put(2,5);
		map3.put(3,1);
		map3.put(2,6);
		MonthHTMLGenerator.combineEntryHourMap(totalsMap, map1);
		MonthHTMLGenerator.combineEntryHourMap(totalsMap, map2);
		MonthHTMLGenerator.combineEntryHourMap(totalsMap, map3);
		
		assertEquals(3, totalsMap.size());
		assertEquals(2, totalsMap.get(1));
		assertEquals(15, totalsMap.get(2));
		assertEquals(3, totalsMap.get(3));
	}
}
