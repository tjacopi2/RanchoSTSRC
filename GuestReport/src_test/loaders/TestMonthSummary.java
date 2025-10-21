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
		File amInputDirectory = new File("testData\\amList\\");
		
		File inputLogDirectory = new File("testData\\TestDataAll");
		Set<String> amAddresses = AMLoader.LoadData(amInputDirectory);
		List<File> logFiles = new ArrayList<File>();
		DataLoader.FetchLogFiles(inputLogDirectory, logFiles);
		Map<Integer, MonthSummary> summaryMapByMonth = DataLoader.LoadData(logFiles, amAddresses);
		

		MonthSummary marchData = summaryMapByMonth.get(3);
		assertNotNull(marchData);
		assertEquals(3, marchData.size());
		assertEquals(40, marchData.getTotalHOAMembers());
		assertEquals(36, marchData.getTotalHOAGuests());
		assertEquals(2, marchData.getTotalAMMembers());
		assertEquals(0, marchData.getTotalAMGuests());
	}
}
