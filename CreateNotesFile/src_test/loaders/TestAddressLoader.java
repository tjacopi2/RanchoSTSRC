package loaders;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TestAddressLoader {

	@Test
	void testMakeKey() {
		assertEquals("6346caminoverde", AddressLoader.MakeKey("6346 Camino Verde"));
	}

	@Test
	void testGetNonEmptyValues() {
		String[] data1 = {"a", "b", "c"};
		String[] data2 = {"", "b", "c"};
		String[] data3 = {""};
		String[] data4 = null;
		String[] data5 = {"a", "b", null};
		
		List<String> list = AddressLoader.GetNonEmptyValues(data1);
		assertEquals(3, list.size());
		assertEquals("a", list.get(0));
		assertEquals("b", list.get(1));
		assertEquals("c", list.get(2));
		
		list = AddressLoader.GetNonEmptyValues(data2);
		assertEquals(2, list.size());
		assertEquals("b", list.get(0));
		assertEquals("c", list.get(1));
		
		list = AddressLoader.GetNonEmptyValues(data3);
		assertEquals(0, list.size());

		list = AddressLoader.GetNonEmptyValues(data4);
		assertEquals(0, list.size());
		
		list = AddressLoader.GetNonEmptyValues(data5);
		assertEquals(2, list.size());
		assertEquals("a", list.get(0));
		assertEquals("b", list.get(1));
	}

	@Test
	void testLoadAddressMap() throws IOException {
		File inputDirectory = new File("testData\\TestNotesCreator\\input");
		Map<String, String> namesToAddress = AddressLoader.loadAddressMap(inputDirectory);

		assertEquals(5, namesToAddress.size(), "values are " + namesToAddress);
	}

}
