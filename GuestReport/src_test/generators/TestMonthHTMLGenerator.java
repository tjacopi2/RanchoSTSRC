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
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import loaders.AMLoader;
import loaders.DataLoader;
import loaders.DaySummary;
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
		Map<Integer, List<DaySummary>> logFileMap = DataLoader.LoadData(logFiles, amAddresses); 
		
		MonthHTMLGenerator monthGenerator = new MonthHTMLGenerator(inputTemplateFile, null);
		
		String html = monthGenerator.generate(logFileMap.get(3));    // Use march
		
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
		DaySummary summary = DaySummary.LoadFrom(inputLogFile, amAddresses);

		MonthHTMLGenerator monthGenerator = new MonthHTMLGenerator(inputTemplateFile, null);
		
		File outputFile = monthGenerator.generateOutputFilename(outputDirectory, summary);
		assertTrue(outputFile.getName().contains("03-2021"));
		assertTrue(outputFile.getName().startsWith("month"));
		assertTrue(outputFile.getName().endsWith(".html"));
		
	}
	

	@Test
	void testCreateDayMap() throws IOException, ParseException {
		File inputDirectory = new File("input\\");
		File inputTemplateFile = new File(inputDirectory, "monthHeader.html");
		
		File inputLogDirectory = new File("testData\\TestDataAll");
		List<File> logFiles = new ArrayList<File>();
		DataLoader.FetchLogFiles(inputLogDirectory, logFiles);
		Map<Integer, List<DaySummary>> logFileMap = DataLoader.LoadData(logFiles, new HashSet<String>()); 

		List<DaySummary> marchList = logFileMap.get(3);
		assertNotNull(marchList);
		assertEquals(3, marchList.size());
		MonthHTMLGenerator monthGenerator = new MonthHTMLGenerator(inputTemplateFile, null);
		
		Map<Integer, DaySummary> dayToSummary = monthGenerator.createDayMap(marchList);
		assertEquals(3, dayToSummary.size());
		assertNotNull(dayToSummary.get(16));
		assertNotNull(dayToSummary.get(17));
		assertNotNull(dayToSummary.get(18));
		
		DaySummary summary = dayToSummary.get(16);
		assertEquals(16, summary.getDate().get(Calendar.DAY_OF_MONTH));
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
