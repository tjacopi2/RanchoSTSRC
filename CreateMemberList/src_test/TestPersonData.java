

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TestPersonData {

	@Test
	void testGetStreet() {
		PersonData pd = new PersonData("test street","123", "test name", "amy.jpg", "some notes");
		assertEquals("test street", pd.getStreet());
	}

	@Test
	void testGetHouseNumber() {
		PersonData pd = new PersonData("test street","123", "test name", "amy.jpg", "some notes");
		assertEquals("123", pd.getHouseNumber());
	}

	@Test
	void testGetName() {
		PersonData pd = new PersonData("test street","123", "test name", "amy.jpg", "some notes");
		assertEquals("test name", pd.getName());
	}

	@Test
	void testGetPictureName() {
		PersonData pd = new PersonData("test street","123", "test name", "amy.jpg", "some notes");
		assertEquals("amy.jpg", pd.getPictureFileName());
	}

	@Test
	void testGetNotes() {
		PersonData pd = new PersonData("test street","123", "test name", "amy.jpg", "some notes");
		assertEquals("some notes", pd.getNotes());
	}
	
	@Test
	void testFindColumnNameIndex() throws IOException {
	
		List<String> columnNames = Arrays.asList(PersonData.AddressColumnName, PersonData.PersonNameColumnName,PersonData.NotesColumnName, PersonData.PictureFileColumnName, "otherName", "otherColName2");
		assertEquals(0, PersonData.findColumnNameIndex(columnNames, PersonData.AddressColumnName, "dummyColumnLine"));
		assertEquals(1, PersonData.findColumnNameIndex(columnNames, PersonData.PersonNameColumnName, "dummyColumnLine"));
		assertEquals(2, PersonData.findColumnNameIndex(columnNames, PersonData.NotesColumnName, "dummyColumnLine"));
		assertEquals(3, PersonData.findColumnNameIndex(columnNames, PersonData.PictureFileColumnName, "dummyColumnLine"));
		

		try {
			PersonData.findColumnNameIndex(columnNames, "bad column name", "dummyColumnLine");
			fail("a bad column name did not throw");
		} catch (IOException e) {
			// expected
		}
	}
	
	@Test
	void testFindColumnMappings() throws IOException {
		
		String goodColumnHeaders = "col1," + PersonData.AddressColumnName +","+PersonData.NotesColumnName+", other column,"+PersonData.PersonNameColumnName+","+PersonData.PictureFileColumnName;
		Map<String, Integer> columnMappings = PersonData.findColumnMappings(goodColumnHeaders);
	
		assertEquals(1, columnMappings.get(PersonData.AddressColumnName));
		assertEquals(4, columnMappings.get(PersonData.PersonNameColumnName));
		assertEquals(2, columnMappings.get(PersonData.NotesColumnName));
		assertEquals(5, columnMappings.get(PersonData.PictureFileColumnName));
		
		try {
			String badColumnHeaders = "col1," + PersonData.AddressColumnName +","+PersonData.NotesColumnName+", other column,"+PersonData.PictureFileColumnName;
			columnMappings = PersonData.findColumnMappings(badColumnHeaders);
			fail("a missing column name did not throw");
		} catch (IOException e) {
			// expected
		}
	}

	@Test
	void testLoadPeople() throws IOException {
		File testDataDirectory = new File("input");
		assertTrue(testDataDirectory.isDirectory(), "Can not find input directory");
		List<PersonData> people = PersonData.loadPeople(testDataDirectory);
		assertTrue(people.size() > 20);
		
		// loop through each personData and validate it.
		for (PersonData pd : people) {
			assertTrue(pd.getHouseNumber().length() > 0);
			assertTrue(pd.getName().length() > 0);
			assertTrue(pd.getStreet().length() > 0);
			try {
				Integer.valueOf(pd.getHouseNumber());
			} catch (NumberFormatException e) {
				fail("invalid house number " + pd.getHouseNumber());
			}
		}
	}

	@Test
	void testSortPeople() {
		PersonData[] pdArray = { new PersonData("Camino Verde","123", "Tom", "amy.jpg", "some notes"),
				new PersonData("Camino Verde","123", "Terrie", "amy.jpg", "some notes"),
				new PersonData("Camino Verde","19", "John", "amy.jpg", "some notes"),
				new PersonData("Dondero","3", "Alice", "amy.jpg", "some notes"),
				new PersonData("Dondero",PersonData.UnknownHouseNumber, "Julia", "amy.jpg", "some notes"),
				new PersonData(PersonData.Unknown,"", "Karen", "amy.jpg", "some notes"),
				new PersonData("Airtight","999", "Stacey", "amy.jpg", "some notes"),
				new PersonData("Dondero","123", "Katie", "amy.jpg", "some notes") 
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
		File testDataFile = new File("input/" + PersonData.Default_Person_Filename);
		assertTrue(testDataFile.exists(), "Can not find input file " + testDataFile.getCanonicalPath());
		List<PersonData> people = PersonData.LoadData(testDataFile);
		assertTrue(people.size() > 20);
	}

	@Test
	void testParseLine() {
		Map<String, Integer> columnMapping = createColumnMapping();
		String line = "Member,182 Castillon Way,HO Amzi Slutzky,Outstanding Dues $142,Amzi.jpg,\r\n";
		PersonData pd = PersonData.parseLine(line, 1, columnMapping);
		assertEquals("Amzi.jpg", pd.getPictureFileName());
		assertEquals("182", pd.getHouseNumber());
		assertEquals("Castillon Way", pd.getStreet());
		assertEquals("HO Amzi Slutzky", pd.getName());
		assertEquals("Outstanding Dues $142", pd.getNotes());
	}
	
	@Test
	void testSplitLine() {
		String[] tokens = PersonData.splitLine("ab,cd,ef");
		assertEquals(3, tokens.length);
		assertEquals("ab", tokens[0]);
		assertEquals("cd", tokens[1]);
		assertEquals("ef", tokens[2]);
		
		tokens = PersonData.splitLine(",ab,cd,ef");
		assertEquals(4, tokens.length);
		assertEquals("", tokens[0]);
		assertEquals("ab", tokens[1]);
		assertEquals("cd", tokens[2]);
		assertEquals("ef", tokens[3]);
		
		tokens = PersonData.splitLine(",ab,cd, ,ef,");
		assertEquals(6, tokens.length);
		assertEquals("", tokens[0]);
		assertEquals("ab", tokens[1]);
		assertEquals("cd", tokens[2]);
		assertEquals(" ", tokens[3]);
		assertEquals("ef", tokens[4]);
		assertEquals("", tokens[5]);
		
		tokens = PersonData.splitLine(",,,");
		assertEquals(4, tokens.length);
		assertEquals("", tokens[0]);
		assertEquals("", tokens[1]);
		assertEquals("", tokens[2]);
		assertEquals("", tokens[3]);
		
		tokens = PersonData.splitLine("");
		assertEquals(1, tokens.length);
		assertEquals("", tokens[0]);
	}
	

	@Test
	void testSplitLineWithQuotes() {
		String[] tokens = PersonData.splitLine("\"ab\",cd,\"ef\"");
		assertEquals(3, tokens.length);
		assertEquals("ab", tokens[0]);
		assertEquals("cd", tokens[1]);
		assertEquals("ef", tokens[2]);
		
		tokens = PersonData.splitLine(",\"ab,cd,ef\"");
		assertEquals(2, tokens.length);
		assertEquals("", tokens[0]);
		assertEquals("ab,cd,ef", tokens[1]);
		
		tokens = PersonData.splitLine(",ab,\"cd,\",ef,");
		assertEquals(5, tokens.length);
		assertEquals("", tokens[0]);
		assertEquals("ab", tokens[1]);
		assertEquals("cd,", tokens[2]);
		assertEquals("ef", tokens[3]);
		assertEquals("", tokens[4]);
		
	}
	
	private Map<String, Integer> createColumnMapping() {
		Map<String, Integer> columnMapping = new HashMap<String, Integer>();
		columnMapping.put(PersonData.AddressColumnName, 1);
		columnMapping.put(PersonData.PersonNameColumnName, 2);
	    columnMapping.put(PersonData.NotesColumnName, 3);
		columnMapping.put(PersonData.PictureFileColumnName, 4);
		return columnMapping;
	}

	@Test
	void testParseLineNoStreetName() {
		Map<String, Integer> columnMapping = createColumnMapping();
		String line = "Member,182,HO Amzi Slutzky,Outstanding Dues $142,Amzi.jpg,\r\n";
		PersonData pd = PersonData.parseLine(line, 1, columnMapping);
		assertEquals("Amzi.jpg", pd.getPictureFileName());
		assertEquals("182", pd.getHouseNumber());
		assertEquals(PersonData.Unknown, pd.getStreet());
		assertEquals("HO Amzi Slutzky", pd.getName());
		assertEquals("Outstanding Dues $142", pd.getNotes());
		
	}
	

	@Test
	void testParseLineNoHouseNumber() {
		Map<String, Integer> columnMapping = createColumnMapping();
		String line = "Member,Castillon Way,HO Amzi Slutzky,Outstanding Dues $142,Amzi.jpg,\r\n";
		PersonData pd = PersonData.parseLine(line, 1, columnMapping);
		assertEquals("Amzi.jpg", pd.getPictureFileName());
		assertEquals(PersonData.UnknownHouseNumber, pd.getHouseNumber());
		assertEquals("Castillon Way", pd.getStreet());
		assertEquals("HO Amzi Slutzky", pd.getName());
		assertEquals("Outstanding Dues $142", pd.getNotes());
	}
	

	@Test
	void testParseLineNoName() {
		Map<String, Integer> columnMapping = createColumnMapping();
		String line = "Member,182 Castillon Way,,Outstanding Dues $142,Amzi.jpg,\r\n";
		PersonData pd = PersonData.parseLine(line, 1, columnMapping);
		assertEquals("Amzi.jpg", pd.getPictureFileName());
		assertEquals("182", pd.getHouseNumber());
		assertEquals("Castillon Way", pd.getStreet());
		assertEquals(PersonData.Unknown, pd.getName());
		assertEquals("Outstanding Dues $142", pd.getNotes());
	}


	@Test
	void testParseLineGarbageData() {
		Map<String, Integer> columnMapping = createColumnMapping();
		String line = "x1asdfkasdhalskdhads";
		PersonData pd = PersonData.parseLine(line, 6, columnMapping);
		assertNull(pd);
	}

	@Test
	void testParseLineNoStreetAndNumber() {
		Map<String, Integer> columnMapping = createColumnMapping();
		String line = "Member,,HO Amzi Slutzky,Outstanding Dues $142,Amzi.jpg,\r\n";
		PersonData pd = PersonData.parseLine(line, 1, columnMapping);
		assertEquals("Amzi.jpg", pd.getPictureFileName());
		assertEquals(PersonData.UnknownHouseNumber, pd.getHouseNumber());
		assertEquals(PersonData.Unknown, pd.getStreet());
		assertEquals("HO Amzi Slutzky", pd.getName());
		assertEquals("Outstanding Dues $142", pd.getNotes());
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
