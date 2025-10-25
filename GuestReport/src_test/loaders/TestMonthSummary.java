package loaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

class TestMonthSummary {
	
	@Test
	void testSummarizeData() throws IOException, ParseException {
		File amInputDirectory = new File("testData\\hoaList\\");
		
		File inputLogDirectory = new File("testData\\TestDataAll");
		Set<String> amAddresses = HOALoader.LoadData(amInputDirectory);
		List<File> logFiles = new ArrayList<File>();
		DataLoader.FetchLogFiles(inputLogDirectory, logFiles);
		Map<Integer, YearSummary> yearSummaries = DataLoader.LoadData(logFiles, amAddresses);
		
		// Validate the year returned
		assertEquals(1, yearSummaries.size());
		YearSummary yearSummary = yearSummaries.get(2021);
		assertNotNull(yearSummary, "Could not find data for 2021.  Instead data was for year " + yearSummaries.keySet());
		
		MonthSummary marchData = yearSummary.get(3);
		assertNotNull(marchData);
		assertEquals(3, marchData.size());
		assertEquals(38, marchData.getTotalHOAMembers());
		assertEquals(32, marchData.getTotalHOAGuests());
		assertEquals(4, marchData.getTotalAMMembers());
		assertEquals(4, marchData.getTotalAMGuests());
	}
}
