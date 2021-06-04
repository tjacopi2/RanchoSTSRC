package generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import utils.TestUtils;

class TestMonthGraphHTMLGenerator {

	@Test
	void testGenerateFullMap() throws IOException, ParseException {
		File inputDirectory = new File("input\\");
		File inputTemplateFile = new File(inputDirectory, "monthGraphHeader.html");

		MonthGraphHTMLGenerator monthGraphGenerator = new MonthGraphHTMLGenerator(inputTemplateFile);
		
		Map<Integer, Integer> hourToPersonCountMap = new HashMap<Integer, Integer>();
		hourToPersonCountMap.put(11,2);
		hourToPersonCountMap.put(12,4);	
		hourToPersonCountMap.put(13,42);	
		hourToPersonCountMap.put(16,21);	
		hourToPersonCountMap.put(17,11);	
		hourToPersonCountMap.put(19,2);	
		
		doTest(monthGraphGenerator, 1, hourToPersonCountMap);
	}
	

	@Test
	void testGenerateMapOneEntry() throws IOException, ParseException {
		File inputDirectory = new File("input\\");
		File inputTemplateFile = new File(inputDirectory, "monthGraphHeader.html");

		MonthGraphHTMLGenerator monthGraphGenerator = new MonthGraphHTMLGenerator(inputTemplateFile);
		
		Map<Integer, Integer> hourToPersonCountMap = new HashMap<Integer, Integer>();
		hourToPersonCountMap.put(11,4);
		
		doTest(monthGraphGenerator, 2, hourToPersonCountMap);
	}
	

	@Test
	void testGenerateMapFullRange() throws IOException, ParseException {
		File inputDirectory = new File("input\\");
		File inputTemplateFile = new File(inputDirectory, "monthGraphHeader.html");

		MonthGraphHTMLGenerator monthGraphGenerator = new MonthGraphHTMLGenerator(inputTemplateFile);
		
		Map<Integer, Integer> hourToPersonCountMap = new HashMap<Integer, Integer>();
		hourToPersonCountMap.put(0,4);
		hourToPersonCountMap.put(23,8);
		
		doTest(monthGraphGenerator, 3, hourToPersonCountMap);
	}

	private void doTest(MonthGraphHTMLGenerator monthGraphGenerator, int graphId,
			Map<Integer, Integer> hourToPersonCountMap) throws IOException {
		String html = monthGraphGenerator.generateGraph(graphId, hourToPersonCountMap);
		
		File outputFile = new File("testData\\output\\TestMonthGraphHTMLGenerator" + graphId + ".html");
		outputFile.delete();
		Files.writeString(outputFile.toPath(), html, StandardOpenOption.CREATE);
		
		
		List<Integer> scriptOpen = TestUtils.CountSubstrings(html, "<script");
		List<Integer> scriptClose = TestUtils.CountSubstrings(html, "/script");
		assertEquals(scriptOpen.size(), scriptClose.size());
		
		// Validate that the div id of the chart is correct
		List<Integer> chartDivCount = TestUtils.CountSubstrings(html, "chart_div" + graphId);
		assertEquals(2, chartDivCount.size());
		
		// Validate the right number of table rows
		List<Integer> keys = new ArrayList<Integer>(hourToPersonCountMap.keySet());
		Collections.sort(keys);
		int numTableRows = keys.get(keys.size()-1) - keys.get(0) + 1;
		List<Integer> tableRowLocations =  TestUtils.CountSubstrings(html, ", 0, 0], f: '");
		assertEquals(numTableRows, tableRowLocations.size());
		
		for (int i = keys.get(0); i < keys.get(keys.size()-1) + 1; i++) {
			
			// Validate that the hour appears in the data list
			int hour = i;
			String ampm = "am";
			if (hour > 11) {
				ampm = "pm";
			}
			if (hour > 12) {
				hour = hour - 12;
			}
			String text = ", 0, 0], f: '" + String.valueOf(hour) + " " + ampm + "'";
			List<Integer> textLocations = TestUtils.CountSubstrings(html, text);
			assertEquals(1, textLocations.size(), "Cannot find text: " + text);
			
			// Validate that the person count appears in the data list.  There are dups
			// so just make sure it exists.
			Integer personCount = hourToPersonCountMap.get(i);
			if (personCount == null) {
				personCount = 0;
			}
			text = String.valueOf(personCount) + "],";
			textLocations = TestUtils.CountSubstrings(html, text);
			assertTrue(textLocations.size() > 0,  "Cannot find text: " + text );
		}
	}
	
}
