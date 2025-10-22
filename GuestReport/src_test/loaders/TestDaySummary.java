package loaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.junit.jupiter.api.Test;

import loaders.DaySummary.Household;

class TestDaySummary {
	
	@Test
	void testDaySummary() throws IOException, ParseException {
		
		DaySummary ds = new DaySummary();
		Household hh1 = new Household();
		hh1.setPeople(10);
		hh1.setGuests(4);
		Household hh2 = new Household();
		hh2.setPeople(12);
		hh2.setGuests(3);
		hh2.setAmHousehold(true);
		Household hh3 = new Household();
		hh3.setPeople(1);
		hh3.setGuests(0);
		ds.getHouseholds().put("address1", hh1);
		ds.getHouseholds().put("address2", hh2);
		ds.getHouseholds().put("address3", hh3);
		
		assertEquals(11, ds.getTotalHOA());
		assertEquals(12, ds.getTotalAM());
		assertEquals(4, ds.getTotalHOAGuests());
		assertEquals(3, ds.getTotalAMGuests());
		
		// List should be returned in descending sorted order of # people in households
		List<Household> hhList = ds.getHouseholdsList();
		assertEquals(3, hhList.size());
		assertSame(hh2, hhList.get(0));
		assertSame(hh1, hhList.get(1));
		assertSame(hh3, hhList.get(2));
	}
}
