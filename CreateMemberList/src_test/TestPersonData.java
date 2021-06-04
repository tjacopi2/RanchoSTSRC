

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class TestPersonData {

	@Test
	void testGetStreet() {
		PersonData pd = new PersonData("test street","123", "test name", "456");
		assertEquals("test street", pd.getStreet());
	}

	@Test
	void testGetHouseNumber() {
		PersonData pd = new PersonData("test street","123", "test name", "456");
		assertEquals("123", pd.getHouseNumber());
	}

	@Test
	void testGetName() {
		PersonData pd = new PersonData("test street","123", "test name", "456");
		assertEquals("test name", pd.getName());
	}

	@Test
	void testGetId() {
		PersonData pd = new PersonData("test street","123", "test name", "456");
		assertEquals("456", pd.getId());
	}

	@Test
	void testLoadPeople() throws IOException {
		File testDataDirectory = new File("input");
		assertTrue(testDataDirectory.isDirectory(), "Can not find input directory");
		List<PersonData> people = PersonData.loadPeople(testDataDirectory);
		assertTrue(people.size() > 100);
		
		// loop through each personData and validate it.
		for (PersonData pd : people) {
			assertTrue(pd.getHouseNumber().length() > 0);
			assertTrue(pd.getName().length() > 0);
			assertTrue(pd.getStreet().length() > 0);
			try {
				Integer.valueOf(pd.getId());
			} catch (NumberFormatException e) {
				fail("invalid ID " + pd.getId());
			}
			try {
				Integer.valueOf(pd.getHouseNumber());
			} catch (NumberFormatException e) {
				fail("invalid house number " + pd.getHouseNumber());
			}
		}
	}

	@Test
	void testSortPeople() {
		PersonData[] pdArray = { new PersonData("Camino Verde","123", "Tom", "456"),
				new PersonData("Camino Verde","123", "Terrie", "456"),
				new PersonData("Camino Verde","19", "John", "456"),
				new PersonData("Dondero","3", "Alice", "456"),
				new PersonData("Dondero",PersonData.UnknownHouseNumber, "Julia", "456"),
				new PersonData(PersonData.Unknown,"", "Karen", "567"),
				new PersonData("Airtight","999", "Stacey", "456"),
				new PersonData("Dondero","123", "Katie", "456") 
		};
		List<PersonData> pdList = Arrays.asList(pdArray);
		
		PersonData.sortPeople(pdList);
		assertEquals("Karen", pdList.get(0).getName());
		assertEquals("Stacey", pdList.get(1).getName());
		assertEquals("John", pdList.get(2).getName());
		assertTrue("Tom".equals(pdList.get(3).getName()) || "Terrie".equals(pdList.get(3).getName()));
		assertTrue("Tom".equals(pdList.get(4).getName()) || "Terrie".equals(pdList.get(4).getName()));
		assertEquals("Julia", pdList.get(5).getName());
		assertEquals("Alice", pdList.get(6).getName());
		assertEquals("Katie", pdList.get(7).getName());
	}

	@Test
	void testLoadData() throws IOException {
		File testDataFile = new File("input/Personnel File.csv");
		assertTrue(testDataFile.exists(), "Can not find input file " + testDataFile.getCanonicalPath());
		List<PersonData> people = PersonData.LoadData(testDataFile);
		assertTrue(people.size() > 100);
	}

	@Test
	void testParseLine() {
		String line = "14,'RSTSC/Members/Los Palmos/252 Los Palmos,Paulina Thurmann 4,2,4086938057,pthurmann@yahoo.com,'2020/10/27 16:00:28,'2030/10/27 16:00:28,13258283;,,\r\n";
		PersonData pd = PersonData.parseLine(line, 1);
		assertEquals("14", pd.getId());
		assertEquals("252", pd.getHouseNumber());
		assertEquals("Los Palmos", pd.getStreet());
		assertEquals("Paulina Thurmann", pd.getName());
	}
	
	@Test
	void testParseLineNoStreetName() {
		String line = "14,'RSTSC/Members/Los Palmos/252 ,Paulina Thurmann 4,2,4086938057,pthurmann@yahoo.com,'2020/10/27 16:00:28,'2030/10/27 16:00:28,13258283;,,\r\n";
		PersonData pd = PersonData.parseLine(line, 2);
		assertEquals("14", pd.getId());
		assertEquals("252", pd.getHouseNumber());
		assertEquals(PersonData.Unknown, pd.getStreet());
		assertEquals("Paulina Thurmann", pd.getName());
	}
	

	@Test
	void testParseLineNoHouseNumber() {
		String line = "14,'RSTSC/Members/Los Palmos/Los Palmos,Paulina Thurmann 4,2,4086938057,pthurmann@yahoo.com,'2020/10/27 16:00:28,'2030/10/27 16:00:28,13258283;,,\r\n";
		PersonData pd = PersonData.parseLine(line, 3);
		assertEquals("14", pd.getId());
		assertEquals(PersonData.UnknownHouseNumber, pd.getHouseNumber());
		assertEquals("Los Palmos", pd.getStreet());
		assertEquals("Paulina Thurmann", pd.getName());
	}
	

	@Test
	void testParseLineNoName() {
		String line = "14,'RSTSC/Members/Los Palmos/252 Los Palmos,,2,4086938057,pthurmann@yahoo.com,'2020/10/27 16:00:28,'2030/10/27 16:00:28,13258283;,,\r\n";
		PersonData pd = PersonData.parseLine(line, 4);
		assertEquals("14", pd.getId());
		assertEquals("252", pd.getHouseNumber());
		assertEquals("Los Palmos", pd.getStreet());
		assertEquals(PersonData.Unknown, pd.getName());
	}


	@Test
	void testParseLineNoID() {
		String line = "x14,'RSTSC/Members/Los Palmos/252 Los Palmos,Paulina Thurmann 4,2,4086938057,pthurmann@yahoo.com,'2020/10/27 16:00:28,'2030/10/27 16:00:28,13258283;,,\r\n";
		PersonData pd = PersonData.parseLine(line, 5);
		assertNull(pd);
	}

	@Test
	void testParseLineGarbageData() {
		String line = "x1asdfkasdhalskdhads";
		PersonData pd = PersonData.parseLine(line, 6);
		assertNull(pd);
	}

	@Test
	void testParseLineNoStreetAndNumber() {
		String line = "14,'RSTSC/Members/Los Palmos/ ,Paulina Thurmann 4,2,4086938057,pthurmann@yahoo.com,'2020/10/27 16:00:28,'2030/10/27 16:00:28,13258283;,,\r\n";
		PersonData pd = PersonData.parseLine(line, 7);
		assertEquals("14", pd.getId());
		assertEquals(PersonData.UnknownHouseNumber, pd.getHouseNumber());
		assertEquals(PersonData.Unknown, pd.getStreet());
		assertEquals("Paulina Thurmann", pd.getName());
	}
	
	@Test
	void testSelectPersonFile() throws IOException {
		File[] files = { new File(PersonData.Default_Person_Filename) };
		File f = PersonData.selectPersonFile(files);
		assertSame(files[0], f);
	}
	

	@Test
	void testSelectPersonFile_multipleFiles() throws IOException {
		File[] files = { new File("dummy.csv"), new File(PersonData.Default_Person_Filename) };
		File f = PersonData.selectPersonFile(files);
		assertSame(files[1], f);
	}


	@Test
	void testSelectPersonFile_ambigiousMultipleFiles() throws IOException {
		File[] files = { new File("dummy.csv"), new File("dummy2.csv") };
		try {
		  PersonData.selectPersonFile(files);
		  fail("select person file did not throw when it should have");
		} catch (IOException e) {
			// expected
		}
	}
	
}
