package loaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMemberHeaderLoader {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testLoadHeaders() throws IOException {
		File testDataDirectory = new File("input");
		assertTrue(testDataDirectory.isDirectory(), "Can not find input directory");
		String[] data =	MemberHeaderLoader.loadHeaders(testDataDirectory);
		assertEquals(2, data.length);
		assertTrue(data[0].contains("table"));
		assertTrue(data[1].contains("/table"));
	}

	@Test
	void testLoadHeadersNoFile() throws IOException {
		File testDataDirectory = new File("testData/TestEmptyDir");
		assertTrue(testDataDirectory.isDirectory(), "Can not find input directory");
		try {
			MemberHeaderLoader.loadHeaders(testDataDirectory);
			fail("Should have thrown if cannot load the file");
		} catch (IOException e) {
			// expected
		}
	}
}
