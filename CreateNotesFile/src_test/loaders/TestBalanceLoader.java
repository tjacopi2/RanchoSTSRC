package loaders;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestBalanceLoader {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testLoadNameToBalanceMap() throws IOException {
		File inputDirectory = new File("testData\\TestNotesCreator\\input");
		Map<String, String> namesToBalance = BalanceLoader.loadNameToBalanceMap(inputDirectory);

		assertEquals(12, namesToBalance.size(), "values are " + namesToBalance);
	}

}
