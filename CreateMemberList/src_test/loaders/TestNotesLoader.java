package loaders;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TestNotesLoader {
	
	private static final String Address = "6346 Camino Verde";
	private static final String AddressKey = Address.replaceAll(" ", "").toLowerCase();
	private static final String Color = "blue";
	private static final String Notes = "Watch out for these people";
	

	@Test
	void testParseLine() {
		String line = Address + "," + Color + "," + Notes;
		String[] data = NotesLoader.parseLine(line);
		assertEquals(3, data.length);
		assertEquals(Address, data[0]);
		assertEquals(Color, data[1]);
		assertEquals(Notes, data[2]);
	}


	@Test
	void testParseLineNoColor() {
		String line = Address + "," + "," + Notes;
		String[] data = NotesLoader.parseLine(line);
		assertEquals(3, data.length);
		assertEquals(Address, data[0]);
		assertEquals(NotesLoader.DefaultColor, data[1]);
		assertEquals(Notes, data[2]);
	}
	

	@Test
	void testParseLineNoAddress() {
		String line = " " + "," + "," + Notes;
		String[] data = NotesLoader.parseLine(line);
		assertNull(data);
	}
	

	@Test
	void testParseLineItsGarbage() {
		String line = "asdflkjadshlaksdjhlasdj ";
		String[] data = NotesLoader.parseLine(line);
		assertNull(data);
	}
	

	@Test
	void testLoadNotes() throws IOException {
		try {
			NotesLoader.searchCreateNotesFileOutputDirectory = false;
			File testDataDirectory = new File("testData/TestNotesLoader");
			assertTrue(testDataDirectory.isDirectory(), "Can not find input directory");
			Map<String, NotesData> notesMap = NotesLoader.loadNotes(testDataDirectory);

			assertEquals(3, notesMap.size());
			NotesData data = notesMap.get(AddressKey);
			assertNotNull(data);
			assertEquals(Color, data.color);
			assertEquals(Notes, data.note);

			data = notesMap.get("280dondero");
			assertNotNull(data);
			assertEquals(NotesLoader.DefaultColor, data.color);
			assertTrue(data.note.contains("Late on dues"));
			assertTrue(data.note.contains("And a fun family"));

			data = notesMap.get("12234elmolino");
			assertNotNull(data);
			assertEquals("green", data.color);
			assertEquals("love this family!", data.note);   // All quotes should be removed from test data

			data = notesMap.get("Some bad address");
			assertNull(data);
		} finally {
			NotesLoader.searchCreateNotesFileOutputDirectory = true;
		}
	}
	

	@Test
	void testLoadNotesNoFile() throws IOException {
		File testDataDirectory = new File("testData/TestEmptyDir");
		assertTrue(testDataDirectory.isDirectory(), "Can not find input directory");
		try {
			NotesLoader.searchCreateNotesFileOutputDirectory = false;
	    	Map<String, NotesData> notesMap = NotesLoader.loadNotes(testDataDirectory);
		    assertEquals(0, notesMap.size());
		} finally {
			NotesLoader.searchCreateNotesFileOutputDirectory = true;
		}
	}
	
	@Test
	void testMakeKey() {
		String key = NotesLoader.MakeNotesKey("123", "Los Palmos");
		assertEquals("123lospalmos", key);
	}
	
	@Test
	void testSelectFile() throws IOException {
		File testDataDirectory = new File("testData/TestNotesLoader");
		File testOutputDirectory = new File("testData/TestNotesLoader/output");
		assertTrue(testDataDirectory.isDirectory(), "Can not find input directory");
		File f1 = new File(testDataDirectory, NotesLoader.NotesFileName);
		
		assertNull(NotesLoader.selectFile(null, null));
        assertSame(f1, NotesLoader.selectFile(f1, null));		
        assertSame(f1, NotesLoader.selectFile(null, f1));
        
        File f2 = new File(testOutputDirectory, "dummy.txt");
        f2.delete();
        assertTrue(f2.createNewFile());
        
        // Since f2 is newer, we always should select the newer file.
        assertSame(f2, NotesLoader.selectFile(f1, f2));		
        assertSame(f2, NotesLoader.selectFile(f2, f1));		
        assertSame(f2, NotesLoader.selectFile(f2, f2));		
        
    	File nonExistingFile = new File(testDataDirectory, "somebadFileName.txt");
        assertSame(f2, NotesLoader.selectFile(nonExistingFile, f2));		
        assertSame(f2, NotesLoader.selectFile(f2, nonExistingFile));		
	}
	
	@Test
	void testFindInputDirectory() throws IOException {
		File testInputDirectory = new File("testData/TestNotesLoader");
		assertNotNull(NotesLoader.findInputFile(testInputDirectory));
	}
}
