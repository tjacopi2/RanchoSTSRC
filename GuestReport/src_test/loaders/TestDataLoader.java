package loaders;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TestDataLoader {

	@Test
	void testLoadFrom() throws IOException, ParseException {
		File inputDirectory = new File("testData\\TestDataAll");

		Map<Integer, List<LogFileSummary>> logFileMap = DataLoader.LoadData(inputDirectory, new HashSet<String>()); 
		
		assertEquals(2, logFileMap.size());
		
		List<LogFileSummary> logFileList = logFileMap.get(2);
		assertEquals(2, logFileList.size());
		
		assertEquals(25, logFileList.get(0).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(9, logFileList.get(0).getTotalPeople());
		assertEquals(26, logFileList.get(1).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(16, logFileList.get(1).getTotalPeople());
		
		logFileList = logFileMap.get(3);
		assertEquals(3, logFileList.size());
		
		assertEquals(16, logFileList.get(0).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(2, logFileList.get(0).getTotalPeople());
		assertEquals(1, logFileList.get(0).getEntryHourMap().get(9));
		assertEquals(1, logFileList.get(0).getEntryHourMap().get(16));
		assertEquals(17, logFileList.get(1).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(20, logFileList.get(1).getTotalPeople());
		assertEquals(18, logFileList.get(2).getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(20, logFileList.get(2).getTotalPeople());
		assertEquals(20, logFileList.get(2).getEntryHourMap().get(7));
	
	}

}
