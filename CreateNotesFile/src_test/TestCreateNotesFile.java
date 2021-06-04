import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opencsv.CSVReader;

class TestCreateNotesFile {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testMain() throws IOException {
    	File inputDirectory = new File("testData\\TestNotesCreator\\input");
    	File outputDirectory = new File("testData\\TestNotesCreator\\output");
    	CreateNotesFile.inputDirectory = inputDirectory;
      	CreateNotesFile.outputDirectory = outputDirectory;
      	
      	CreateNotesFile.main(null);
      	
      	File[] files = outputDirectory.listFiles();
      	assertEquals(1, files.length);
      	CSVReader reader = new CSVReader(new FileReader(files[0]));
      	
      	int rowCount = 0;
		Iterator<String[]> iter = reader.iterator();
		while (iter.hasNext()) {
			String[] values = iter.next();
			rowCount++;
			assertEquals(3, values.length);
			assertEquals("", values[1]);
			assertTrue(values[0].contains("Portal") || values[0].contains("Dondero"));
			assertTrue(values[2].contains("-300"));
		}
		assertEquals(2, rowCount);
	}

}
