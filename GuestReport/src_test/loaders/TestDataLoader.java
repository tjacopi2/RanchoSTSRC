package loaders;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import loaders.DaySummary.Household;

class TestDataLoader {
	
	@Test
	void testFetchLogFiles() throws IOException, ParseException {
		File inputDirectory = new File("testData\\TestDataLogFilesSubdirectories");

		List<File> logFiles = new ArrayList<File>();
		DataLoader.FetchLogFiles(inputDirectory, logFiles);
		assertEquals(4, logFiles.size(), "Unexpected number of log files found");
	}


	@Test
	void testLoadFromSingleFile() throws IOException, ParseException {
		File inputDirectory = new File("testData\\amList\\");
		Set<String> amAddresses = AMLoader.LoadData(inputDirectory);
		
		File inputFile = new File("testData\\TestDataAll\\log2021-03-17.csv");
		Map<Integer, Map<Integer, DaySummary>> summaryMapByMonth = new HashMap<Integer, Map<Integer, DaySummary>>();
		DataLoader.LoadFile(amAddresses, summaryMapByMonth, inputFile);
		
		assertEquals(1, summaryMapByMonth.size());   // should only have one month of data
		Map<Integer, DaySummary> marchData = summaryMapByMonth.get(3);  // Get March data
		assertNotNull(marchData, "March data not found");
		
		assertEquals(1, marchData.size());           // should only have one day of data
		DaySummary summary = marchData.get(17);
		assertNotNull(summary, "Data for the 17th not found");
		
		assertEquals(20, summary.getTotalPeople());
		assertEquals(16, summary.getTotalGuests());
		assertEquals(2, summary.getTotalAM());
		assertEquals(0, summary.getTotalAMGuests());
		assertEquals(18, summary.getTotalHOA());
		assertEquals(16, summary.getTotalHOAGuests());
		assertEquals(3, summary.getHouseholds().size());
		assertEquals(2, summary.getDate().get(Calendar.MONTH));
		assertEquals(2021, summary.getDate().get(Calendar.YEAR));
		assertEquals(17, summary.getDate().get(Calendar.DAY_OF_MONTH));
		
		List<Household> householdList = summary.getHouseholdsList();
		assertEquals("6346 Camino Verde", householdList.get(0).getAddress());
		assertEquals(14, householdList.get(0).getPeople());
		assertEquals(12, householdList.get(0).getGuests());
		assertFalse(householdList.get(0).isAmHousehold());
		
		assertEquals("435 Allegan Circle", householdList.get(1).getAddress());
		assertEquals(4, householdList.get(1).getPeople());
		assertEquals(4, householdList.get(1).getGuests());
		assertFalse(householdList.get(1).isAmHousehold());
		
		assertEquals("341 Bodega", householdList.get(2).getAddress());
		assertEquals(2, householdList.get(2).getPeople());
		assertEquals(0, householdList.get(2).getGuests());
		assertTrue(householdList.get(2).isAmHousehold());
		
		assertEquals(8, summary.getEntryHourMap().get(16));
		assertEquals(12, summary.getEntryHourMap().get(19));
		assertNull(summary.getEntryHourMap().get(17));
		assertEquals(2, summary.getEntryHourMap().size());   // Only have two hours of entries
	}

	
	
	@Test
	void testLoadFrom() throws IOException, ParseException {
		File inputDirectory = new File("testData\\TestDataAll");

		List<File> logFiles = new ArrayList<File>();
		DataLoader.FetchLogFiles(inputDirectory, logFiles);
		Map<Integer, Map<Integer, DaySummary>> logFileMap = DataLoader.LoadData(logFiles, new HashSet<String>()); 
		
		assertEquals(2, logFileMap.size());
		
		Map<Integer, DaySummary> monthToDaySummaryMap = logFileMap.get(2);
		assertEquals(2, monthToDaySummaryMap.size());
		
		assertEquals(25, monthToDaySummaryMap.get(25).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(9, monthToDaySummaryMap.get(25).getTotalPeople());
		assertEquals(26, monthToDaySummaryMap.get(26).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(16, monthToDaySummaryMap.get(26).getTotalPeople());
		
		monthToDaySummaryMap = logFileMap.get(3);    // Now get march data
		assertEquals(3, monthToDaySummaryMap.size());
		
		assertEquals(16, monthToDaySummaryMap.get(16).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(2, monthToDaySummaryMap.get(16).getTotalPeople());
		assertEquals(1, monthToDaySummaryMap.get(16).getEntryHourMap().get(9));
		assertEquals(1, monthToDaySummaryMap.get(16).getEntryHourMap().get(16));
		assertEquals(17, monthToDaySummaryMap.get(17).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(20, monthToDaySummaryMap.get(17).getTotalPeople());
		assertEquals(18, monthToDaySummaryMap.get(18).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(20, monthToDaySummaryMap.get(18).getTotalPeople());
		assertEquals(20, monthToDaySummaryMap.get(18).getEntryHourMap().get(7));
	
	}
	

	@Test
	void testLoadFromCombinedLogFile() throws IOException, ParseException {
		File inputDirectory = new File("testData\\TestDataCombinedLogFile");

		List<File> logFiles = new ArrayList<File>();
		DataLoader.FetchLogFiles(inputDirectory, logFiles);
		Map<Integer, Map<Integer, DaySummary>> monthToDayMap = DataLoader.LoadData(logFiles, new HashSet<String>()); 
		
		assertEquals(2, monthToDayMap.size());    // Should have two months of data
		
		Map<Integer, DaySummary> monthToDaySummaryMap = monthToDayMap.get(2);   // Get Feburary data
		assertEquals(2, monthToDaySummaryMap.size());                           // Should have two days in Feb
		
		assertEquals(25, monthToDaySummaryMap.get(25).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(9, monthToDaySummaryMap.get(25).getTotalPeople());
		assertEquals(26, monthToDaySummaryMap.get(26).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(16, monthToDaySummaryMap.get(26).getTotalPeople());
		
		monthToDaySummaryMap = monthToDayMap.get(3);    // Now get march data
		assertEquals(3, monthToDaySummaryMap.size());
		
		assertEquals(16, monthToDaySummaryMap.get(16).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(2, monthToDaySummaryMap.get(16).getTotalPeople());
		assertEquals(1, monthToDaySummaryMap.get(16).getEntryHourMap().get(9));
		assertEquals(1, monthToDaySummaryMap.get(16).getEntryHourMap().get(16));
		assertEquals(17, monthToDaySummaryMap.get(17).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(20, monthToDaySummaryMap.get(17).getTotalPeople());
		assertEquals(18, monthToDaySummaryMap.get(18).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(20, monthToDaySummaryMap.get(18).getTotalPeople());
		assertEquals(20, monthToDaySummaryMap.get(18).getEntryHourMap().get(7));
	
	}

}
