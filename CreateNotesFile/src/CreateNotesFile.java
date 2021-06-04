import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loaders.AddressLoader;
import loaders.BalanceLoader;

public class CreateNotesFile {

	protected static String outputFileName = "notes.csv";
	protected static File inputDirectory = new File("input");
	protected static File outputDirectory = new File("output");
	
	public static void main(String[] args) throws IOException {
	
		File[] inputFiles = inputDirectory.listFiles();
		if (inputFiles.length < 1) {
			throw new IOException("Error, cannot file any files in input directory: " + inputDirectory.getCanonicalPath());
		}
		Map<String, String> namesToAddress = AddressLoader.loadAddressMap(inputDirectory);
		Map<String, String> namesToBalance = BalanceLoader.loadNameToBalanceMap(inputDirectory);
		
		// Need to copy the keySet since we are going to remove entries from it.
		Set<String> balanceNames = new HashSet<String>(namesToBalance.keySet());
		balanceNames.removeAll(namesToAddress.keySet());
		if (balanceNames.size() > 0) {
		  System.out.println("Warning: Could not find " + balanceNames.size() + " names in address table.  Names are " + balanceNames);
		}
		
		File notesFile = new File(outputDirectory, outputFileName);
		PrintWriter out = new PrintWriter(notesFile);
		
		int rowsWritten = NotesCreator.createNotes(out, namesToBalance, namesToAddress);
		out.flush();
		out.close();
		System.out.println("Wrote " + rowsWritten + " entries to " + notesFile.getCanonicalPath());
	}

}
