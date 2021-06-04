import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.opencsv.CSVReader;

import loaders.AddressLoader;
import loaders.BalanceLoader;

class TestNotesCreator {


	@Test
	void testCreateNotes() throws IOException {
		//	public static void createNotes(Writer writer, Map<String,String> namesToBalances, Map<String, String> namesToAddresses) throws IOException {
        StringWriter sw = new StringWriter();
        
    	File inputDirectory = new File("testData\\TestNotesCreator\\input");
		Map<String, String> namesToAddress = AddressLoader.loadAddressMap(inputDirectory);
		Map<String, String> namesToBalance = BalanceLoader.loadNameToBalanceMap(inputDirectory);
		
		int rowsWritten = NotesCreator.createNotes(sw, namesToBalance, namesToAddress);

		// Validate that the csv produced looks correct
		assertEquals(2, rowsWritten, "CSV data is " + sw.toString());
		CSVReader reader = new CSVReader(new StringReader(sw.toString()));
		Iterator<String[]> iter = reader.iterator();
		while (iter.hasNext()) {
			String[] values = iter.next();
			assertEquals(3, values.length);
			assertEquals("", values[1]);
			assertTrue(values[0].contains("Portal") || values[0].contains("Dondero"));
			assertTrue(values[2].contains("-300"));
		}
	}

}
