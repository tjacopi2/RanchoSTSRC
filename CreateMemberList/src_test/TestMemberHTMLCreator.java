import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMemberHTMLCreator {

	private static final String Person1 = "Tom Smith";
	private static final String Person2 = "Terrie Smith";
	private static final String Person3 = "Katie Smith";
	private static final String Person4 = "Ed Wilson";
	private static final String Person5 = "Jake";
	private static final String Person1_PictureFile = "1.jpg";
	private static final String Person4_PictureFile = "4.jpg";
	private static final String Person1_Note1 = "Some silly note";
	private static final String Person2_Note2 = "Some other silly note";
	private static final String StreetNumber1 = "123";
	private static final String StreetNumber2 = "456";
	private static final String StreetName1 = "Dondero";
	private static final String StreetName2 = "Sorrento";
	
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateMembers() {
		List<PersonData> people = new ArrayList<PersonData>();
		people.add(new PersonData(StreetName1, StreetNumber1, Person1, Person1_PictureFile, Person1_Note1));
		people.add(new PersonData(StreetName1, StreetNumber1, Person2, "", Person2_Note2));
		people.add(new PersonData(StreetName1, StreetNumber1, Person3, "", ""));
		people.add(new PersonData(StreetName1, StreetNumber2, Person4, Person4_PictureFile, ""));
		people.add(new PersonData(StreetName2, StreetNumber1, Person5, "", ""));
		
				
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		MemberHTMLCreator creator = new MemberHTMLCreator(pw);
		creator.generateMembers(people);
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
		assertTrue(data.contains(Person1_Note1));
		assertTrue(data.contains(Person2_Note2));
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
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		MemberHTMLCreator creator = new MemberHTMLCreator(pw);
		creator.endPreviousTableEntry(Person1_Note1, StreetName1, StreetNumber1);
		pw.flush();
		String data = sw.toString();
		System.err.println(data);
		
		assertTrue(data.contains(StreetName1));
		assertTrue(data.contains(StreetNumber1));
		assertTrue(data.contains(Person1_Note1));
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
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		MemberHTMLCreator creator = new MemberHTMLCreator(pw);
		creator.endPreviousTableEntry("", StreetName2, StreetNumber1);
		pw.flush();
		String data = sw.toString();
		
		assertTrue(data.contains(StreetName2));
		assertTrue(data.contains(StreetNumber1));
		assertFalse(data.contains(Person1_Note1));
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
		int count = 0;
		for (List<Integer> nextTag : tagOrder) {
			assertTrue(nextTag.get(0) > previousTagIndex, "error at tag # " + count);
			previousTagIndex = nextTag.get(0);
			nextTag.remove(0);
			count++;
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
