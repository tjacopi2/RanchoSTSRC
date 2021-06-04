package loaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

import org.junit.jupiter.api.Test;

class TestAMLoader {

	@Test
	void testLoadFrom() throws IOException, ParseException {
		File inputDirectory = new File("testData\\amList\\");

		Set<String> amAddresses = AMLoader.LoadData(inputDirectory);
		assertEquals(3, amAddresses.size());
		assertTrue(amAddresses.contains("6945 Angelo Lane"));
		assertTrue(amAddresses.contains("117 Bernal Road"));
		assertTrue(amAddresses.contains("341 Bodega"));
	}

}
