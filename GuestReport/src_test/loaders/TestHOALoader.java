package loaders;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

import org.junit.jupiter.api.Test;

class TestHOALoader {

	@Test
	void testLoadFrom() throws IOException, ParseException {
		File inputDirectory = new File("testData\\hoaList\\");

		Set<String> hoaAddresses = HOALoader.LoadData(inputDirectory);
		assertTrue( hoaAddresses.size() > 1000);
		assertTrue(hoaAddresses.contains("6346 Camino Verde Drive"));
		assertTrue(hoaAddresses.contains("6346 camino verde"));
		assertTrue(hoaAddresses.contains("185 Castillon Way"));
	}

}
