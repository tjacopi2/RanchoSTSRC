import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import loaders.NotesData;
import loaders.NotesLoader;

class TestMemberHTMLCreator {

	private static final String Person1 = "Tom Smith";
	private static final String Person2 = "Terrie Smith";
	private static final String Person3 = "Katie Smith";
	private static final String Person4 = "Ed Wilson";
	private static final String Person5 = "Jake";
	private static final String Person1_ID = "1";
	private static final String Person2_ID = "2";
	private static final String Person3_ID = "3";
	private static final String Person4_ID = "4";
	private static final String Person5_ID = "5";
	private static final String Color_Blue = "blue";
	private static final String Note1 = "Some silly note";
	private static final String Color_Green = "green";
	private static final String Note2 = "Some other silly note";
	private static final String StreetNumber1 = "123";
	private static final String StreetNumber2 = "456";
	private static final String StreetName1 = "Dondero";
	private static final String StreetName2 = "Sorrento";
	private static final String NoteKey1 = NotesLoader.MakeNotesKey(StreetNumber1, StreetName1);
	private static final String NoteKey2 = NotesLoader.MakeNotesKey(StreetNumber2, StreetName2);
	
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateMembers() {
		List<PersonData> people = new ArrayList<PersonData>();
		people.add(new PersonData(StreetName1, StreetNumber1, Person1, Person1_ID));
		people.add(new PersonData(StreetName1, StreetNumber1, Person2, Person2_ID));
		people.add(new PersonData(StreetName1, StreetNumber1, Person3, Person3_ID));
		people.add(new PersonData(StreetName1, StreetNumber2, Person4, Person4_ID));
		people.add(new PersonData(StreetName2, StreetNumber1, Person5, Person5_ID));
		
		Map<String, ZipEntry> pictureMap = new HashMap<String, ZipEntry>();
		pictureMap.put(Person2_ID, null);
		pictureMap.put(Person4_ID, null);
		
		Map<String, NotesData> notesMap = new HashMap<String, NotesData>();
		notesMap.put(NoteKey1, new NotesData(Color_Blue, Note1));
		notesMap.put(NoteKey2, new NotesData(Color_Green, Note2));
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		MemberHTMLCreator creator = new MemberHTMLCreator(pw);
		creator.generateMembers(people, pictureMap, notesMap);
		pw.flush();
		String data = sw.toString();
		
		System.out.println("test generate members output: " + data);
		
		List<Integer> tdOpen = CountSubstrings(data, "<td");
		List<Integer> tdClose = CountSubstrings(data, "/td");
		List<Integer> trOpen = CountSubstrings(data, "<tr");
		List<Integer> trClose = CountSubstrings(data, "/tr");
		List<Integer> styleTag = CountSubstrings(data, "style");
		List<Integer> imgTag = CountSubstrings(data, "img");
		
		assertEquals(3, trOpen.size());
		assertEquals(3, trClose.size());
		assertEquals(9, tdOpen.size());
		assertEquals(9, tdClose.size());
		assertEquals(1, styleTag.size());
		assertEquals(2, imgTag.size());
		
		assertTrue(data.contains(StreetNumber1 + MemberHTMLCreator.HTMLSpace + StreetName1));
		assertTrue(data.contains(StreetNumber1 + MemberHTMLCreator.HTMLSpace + StreetName2));
		assertTrue(data.contains(StreetNumber2 + MemberHTMLCreator.HTMLSpace + StreetName1));
		assertFalse(data.contains(StreetNumber2 + MemberHTMLCreator.HTMLSpace + StreetName2));
		assertTrue(data.contains(Note1));
		assertFalse(data.contains(Note2));
		for (PersonData pd : people) {
			assertTrue(data.contains(pd.getName()));
		}

		// Validate the order of the html tags.  Do this last because it empties the tagOpen/close lists
		List<List<Integer>> tagOrder = new ArrayList<List<Integer>>();
		// first row
		tagOrder.add(trOpen);
		tagOrder.add(tdOpen);
		tagOrder.add(imgTag);
		tagOrder.add(tdClose);
		tagOrder.add(tdOpen);
		tagOrder.add(styleTag);
		tagOrder.add(tdClose);
		tagOrder.add(tdOpen);
		tagOrder.add(tdClose);
		tagOrder.add(trClose);
		
		// second row
		tagOrder.add(trOpen);
		tagOrder.add(tdOpen);
		tagOrder.add(imgTag);
		tagOrder.add(tdClose);
		tagOrder.add(tdOpen);
		tagOrder.add(tdClose);
		tagOrder.add(tdOpen);
		tagOrder.add(tdClose);
		tagOrder.add(trClose);
		
		// third row
		tagOrder.add(trOpen);
		tagOrder.add(tdOpen);
		tagOrder.add(tdClose);
		tagOrder.add(tdOpen);
		tagOrder.add(tdClose);
		tagOrder.add(tdOpen);
		tagOrder.add(tdClose);
		tagOrder.add(trClose);
		
		validateTagOrder(tagOrder);
	}

	@Test
	void testEndPreviousTableEntryWithNote() {
		Map<String, NotesData> notesMap = new HashMap<String, NotesData>();
		notesMap.put(NoteKey1, new NotesData(Color_Blue, Note1));
		notesMap.put(NoteKey2, new NotesData(Color_Green, Note2));
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		MemberHTMLCreator creator = new MemberHTMLCreator(pw);
		creator.endPreviousTableEntry(notesMap, StreetName1, StreetNumber1);
		pw.flush();
		String data = sw.toString();
		System.err.println(data);
		
		assertTrue(data.contains(StreetName1));
		assertTrue(data.contains(StreetNumber1));
		assertTrue(data.contains(Note1));
		System.out.println(data);
		
		List<Integer> aOpen = CountSubstrings(data, "<a");
		List<Integer> aClose = CountSubstrings(data, "/a");
		List<Integer> tdOpen = CountSubstrings(data, "<td");
		List<Integer> tdClose = CountSubstrings(data, "/td");
		List<Integer> trOpen = CountSubstrings(data, "<tr");
		List<Integer> trClose = CountSubstrings(data, "/tr");
		List<Integer> styleTag = CountSubstrings(data, "style");
		
		assertEquals(1, aOpen.size());
		assertEquals(1, aClose.size());
		assertEquals(2, tdOpen.size());
		assertEquals(3, tdClose.size());
		assertEquals(0, trOpen.size());
		assertEquals(1, trClose.size());
		assertEquals(1, styleTag.size());
		
		// Validate the order of the html tags.  Do this last because it empties the tagOpen/close lists
		List<List<Integer>> tagOrder = new ArrayList<List<Integer>>();
		tagOrder.add(tdClose);
		tagOrder.add(tdOpen);
		tagOrder.add(styleTag);
		tagOrder.add(tdClose);
		tagOrder.add(tdOpen);
		tagOrder.add(tdClose);
		tagOrder.add(trClose);
		validateTagOrder(tagOrder);
	}
	

	@Test
	void testEndPreviousTableEntryWithoutNote() {
		Map<String, NotesData> notesMap = new HashMap<String, NotesData>();
		notesMap.put(NoteKey1, new NotesData(Color_Blue, Note1));
		notesMap.put(NoteKey2, new NotesData(Color_Green, Note2));
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		MemberHTMLCreator creator = new MemberHTMLCreator(pw);
		creator.endPreviousTableEntry(notesMap, StreetName2, StreetNumber1);
		pw.flush();
		String data = sw.toString();
		
		assertTrue(data.contains(StreetName2));
		assertTrue(data.contains(StreetNumber1));
		assertFalse(data.contains(Note1));
		System.out.println(data);
		
		List<Integer> tdOpen = CountSubstrings(data, "<td");
		List<Integer> tdClose = CountSubstrings(data, "/td");
		List<Integer> trOpen = CountSubstrings(data, "<tr");
		List<Integer> trClose = CountSubstrings(data, "/tr");
		List<Integer> styleTag = CountSubstrings(data, "style");
		
		assertEquals(2, tdOpen.size());
		assertEquals(3, tdClose.size());
		assertEquals(0, trOpen.size());
		assertEquals(1, trClose.size());
		assertEquals(0, styleTag.size());
		
		// Validate the order of the html tags.  Do this last because it empties the tagOpen/close lists
		List<List<Integer>> tagOrder = new ArrayList<List<Integer>>();
		tagOrder.add(tdClose);
		tagOrder.add(tdOpen);
		tagOrder.add(tdClose);
		tagOrder.add(tdOpen);
		tagOrder.add(tdClose);
		tagOrder.add(trClose);
		validateTagOrder(tagOrder);
	}

	private void validateTagOrder(List<List<Integer>> tagOrder) {
		int previousTagIndex = -1;
		for (List<Integer> nextTag : tagOrder) {
			assertTrue(nextTag.get(0) > previousTagIndex);
			previousTagIndex = nextTag.get(0);
			nextTag.remove(0);
		}
	}

	public static List<Integer> CountSubstrings(String string, String substring) {
		List<Integer> occurances = new ArrayList<Integer>();
		
		int foundIndex = string.indexOf(substring);

		while(foundIndex >=0) {
			occurances.add(foundIndex);
			foundIndex = string.indexOf(substring, foundIndex + substring.length());
		}
		
		return occurances;
	}
}
