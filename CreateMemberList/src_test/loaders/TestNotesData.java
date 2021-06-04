package loaders;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TestNotesData {

	@Test
	void testNotesData() {
		NotesData nd = new NotesData("red", "hello world");
		assertEquals("red", nd.color);
		assertEquals("hello world", nd.note);
	}

	@Test
	void testFuzzyRemove() {
		Map<String, NotesData> notes = new HashMap<String, NotesData>();
		
		String houseNumber1 = "123";
		String houseStreet1 = "Camino Verde Drive";
		String houseNumber2 = "3123";
		String houseStreet2 = "El Molino";
		String houseNumber3 = "3423";
		String houseStreet3 = "Dondero Way";
		
		notes.put(NotesLoader.MakeNotesKey(houseNumber1, houseStreet1), new NotesData("blue", "Hello1"));
		notes.put(NotesLoader.MakeNotesKey(houseNumber2, houseStreet2), new NotesData("red", "Hello2"));
		notes.put(NotesLoader.MakeNotesKey(houseNumber3, houseStreet3), new NotesData("green", "Hello3"));
		
		NotesData nd = NotesData.FuzzyRemove(NotesLoader.MakeNotesKey(houseNumber1, houseStreet1), notes);
		assertNotNull(nd);
		assertEquals("blue", nd.color);
		assertEquals(2, notes.size());
		
		nd = NotesData.FuzzyRemove(NotesLoader.MakeNotesKey(houseNumber1, houseStreet2), notes);
		assertNull(nd);
		assertEquals(2, notes.size());
		
		nd = NotesData.FuzzyRemove(NotesLoader.MakeNotesKey(houseNumber3, houseStreet3), notes);
		assertNotNull(nd);
		assertEquals("green", nd.color);
		assertEquals(1, notes.size());
		
		nd = NotesData.FuzzyRemove(NotesLoader.MakeNotesKey(houseNumber2, houseStreet2), notes);
		assertNotNull(nd);
		assertEquals("red", nd.color);
		assertEquals(0, notes.size());
	}


	@Test
	void testFuzzyRemoveWithSuffixes() {
		Map<String, NotesData> notes = new HashMap<String, NotesData>();
		
		String houseNumber1 = "123";
		String houseStreet1 = "Camino Verde Drive";
		String houseStreet1b = "Camino Verde";
		String houseNumber2 = "3123";
		String houseStreet2 = "El Molino";
		String houseNumber3 = "3423";
		String houseStreet3 = "Dondero Way";
		String houseStreet3b = "Dondero";
		
		notes.put(NotesLoader.MakeNotesKey(houseNumber1, houseStreet1), new NotesData("blue", "Hello1"));
		notes.put(NotesLoader.MakeNotesKey(houseNumber2, houseStreet2), new NotesData("red", "Hello2"));
		notes.put(NotesLoader.MakeNotesKey(houseNumber3, houseStreet3), new NotesData("green", "Hello3"));
		
		NotesData nd = NotesData.FuzzyRemove(NotesLoader.MakeNotesKey(houseNumber1, houseStreet1b), notes);
		assertNotNull(nd);
		assertEquals("blue", nd.color);
		assertEquals(2, notes.size());
		
		nd = NotesData.FuzzyRemove(NotesLoader.MakeNotesKey(houseNumber1, houseStreet2), notes);
		assertNull(nd);
		assertEquals(2, notes.size());
		
		nd = NotesData.FuzzyRemove(NotesLoader.MakeNotesKey(houseNumber3, houseStreet3b), notes);
		assertNotNull(nd);
		assertEquals("green", nd.color);
		assertEquals(1, notes.size());
		
		nd = NotesData.FuzzyRemove(NotesLoader.MakeNotesKey(houseNumber2, houseStreet3b), notes);
		assertNull(nd);
		assertEquals(1, notes.size());
	}
	

	@Test
	void testFuzzyRemoveWithSubstringMatch() {
		Map<String, NotesData> notes = new HashMap<String, NotesData>();
		
		String houseNumber1 = "123";
		String houseStreet1 = "Camino Verde Dr";
		String houseStreet1b = "Camino Verde";
		String houseNumber2 = "3123";
		String houseStreet2 = "El Molino Ct.";
		String houseNumber3 = "3423";
		String houseStreet3 = "Dondero Wy";
		String houseStreet3b = "Dondero";
		
		notes.put(NotesLoader.MakeNotesKey(houseNumber1, houseStreet1), new NotesData("blue", "Hello1"));
		notes.put(NotesLoader.MakeNotesKey(houseNumber2, houseStreet2), new NotesData("red", "Hello2"));
		notes.put(NotesLoader.MakeNotesKey(houseNumber3, houseStreet3), new NotesData("green", "Hello3"));
		
		NotesData nd = NotesData.FuzzyRemove(NotesLoader.MakeNotesKey(houseNumber1, houseStreet1b), notes);
		assertNotNull(nd);
		assertEquals("blue", nd.color);
		assertEquals(2, notes.size());
		
		nd = NotesData.FuzzyRemove(NotesLoader.MakeNotesKey(houseNumber1, houseStreet2), notes);
		assertNull(nd);
		assertEquals(2, notes.size());
		
		nd = NotesData.FuzzyRemove(NotesLoader.MakeNotesKey(houseNumber3, houseStreet3b), notes);
		assertNotNull(nd);
		assertEquals("green", nd.color);
		assertEquals(1, notes.size());
		
		nd = NotesData.FuzzyRemove(NotesLoader.MakeNotesKey(houseNumber2, houseStreet3b), notes);
		assertNull(nd);
		assertEquals(1, notes.size());
		
		nd = NotesData.FuzzyRemove(NotesLoader.MakeNotesKey(houseNumber2, houseStreet2), notes);
		assertNotNull(nd);
		assertEquals("red", nd.color);
		assertEquals(0, notes.size());
	}
}
