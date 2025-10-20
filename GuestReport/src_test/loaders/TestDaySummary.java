package loaders;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Set;

import org.junit.jupiter.api.Test;

class TestDaySummary {

	@Test
	void testLoadFrom() throws IOException, ParseException {
		File inputDirectory = new File("testData\\amList\\");
		Set<String> amAddresses = AMLoader.LoadData(inputDirectory);
		
		File inputFile = new File("testData\\TestDataAll\\log2021-03-17.csv");

		DaySummary summary = DaySummary.LoadFrom(inputFile, amAddresses);
		
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
		
		assertEquals("6346 Camino Verde", summary.getHouseholds().get(0).getAddress());
		assertEquals(14, summary.getHouseholds().get(0).getPeople());
		assertEquals(12, summary.getHouseholds().get(0).getGuests());
		assertFalse(summary.getHouseholds().get(0).isAmHousehold());
		
		assertEquals("435 Allegan Circle", summary.getHouseholds().get(1).getAddress());
		assertEquals(4, summary.getHouseholds().get(1).getPeople());
		assertEquals(4, summary.getHouseholds().get(1).getGuests());
		assertFalse(summary.getHouseholds().get(1).isAmHousehold());
		
		assertEquals("341 Bodega", summary.getHouseholds().get(2).getAddress());
		assertEquals(2, summary.getHouseholds().get(2).getPeople());
		assertEquals(0, summary.getHouseholds().get(2).getGuests());
		assertTrue(summary.getHouseholds().get(2).isAmHousehold());
		
		assertEquals(8, summary.getEntryHourMap().get(16));
		assertEquals(12, summary.getEntryHourMap().get(19));
		assertNull(summary.getEntryHourMap().get(17));
		assertEquals(2, summary.getEntryHourMap().size());   // Only have two hours of entries
	}

}
