import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.opencsv.CSVWriter;

public class NotesCreator {

	public static int createNotes(Writer writer, Map<String,String> namesToBalances, Map<String, String> namesToAddresses) throws IOException {
		CSVWriter csvWriter = new CSVWriter(writer);
		
		int rowsWritten = 0;
		String[] row = new String[3];
		row[1] = "";   // This is the color, use the default
		for (String name : namesToBalances.keySet()) {
			String balance = namesToBalances.get(name);
			row[2] = "Balanced owed: $" + balance;
			String address = namesToAddresses.get(name);
			if (address != null) {
				row[0] = address;
				csvWriter.writeNext(row);
				rowsWritten++;
			} else {
				System.out.println("Warning, could not find name " + name + " in nameToAddress map");
			}
		}
		csvWriter.flush();
		csvWriter.close();
		
		return rowsWritten;
	}
}
