package loaders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesLoader {
	
	public static boolean searchCreateNotesFileOutputDirectory = true;  // Add this for JUnit tests
	public static String CreateNoteFileOutputDirectory = "../CreateNotesFile/output";
	public static final String NotesFileName = "notes.csv";
	protected static final String DefaultColor = "red";
	
	public static String MakeNotesKey(String houseNumber, String streetName) {
		return MakeNotesKey(houseNumber + streetName);
	}
	
	public static String MakeNotesKey(String address) {
		return address.replaceAll(" ","").replaceAll("\"", "").replaceAll("'", "").toLowerCase();
	}
	
	public static Map<String, NotesData> loadNotes(File inputDirectory) throws IOException {
		Map<String, NotesData> map = new HashMap<String, NotesData>();
		File notesFile = findInputFile(inputDirectory);
		if (notesFile == null) {
			System.out.println("Could not find " + NotesFileName + " file in input directory " + inputDirectory.getCanonicalPath() +
					" so no notes will be added to the html file.");
            return map;
		}
		
		// Read csv file, populate map
		List<String> lines = Files.readAllLines(notesFile.toPath());
		for (String line : lines) {
			String[] data = parseLine(line);
			if (data != null) {
				String key = MakeNotesKey(data[0]);                              // Remove all blanks and make lower case
				String color = data[1].replaceAll("\"", "").replaceAll("'", "");  // Remove all quotes so dont mess up HTML
				String note = data[2].replaceAll("\"", "").replaceAll("'", "");  // Remove all quotes so dont mess up HTML
     			NotesData nd = map.get(key);            
     			if (nd != null) {                       // If we already have a note for this address
     				nd.note = nd.note + "\n" + note;    // append to the existing notes
     			} else {
     				map.put(key, new NotesData(color, note) );
     			}
			}
		}
		
		System.out.println("Loaded " + map.size() + " notes from file " + notesFile.getCanonicalPath());
		return map;
	}

	protected static String[] parseLine(String line) {
		// lines have CSV format:  address,color,notes
		//    example:             6346 Camino Verde,red,Watch out for these people!
        String[] data = null;
		int comma1 = line.indexOf(',');
		if (comma1 > 0) {
			String address = line.substring(0, comma1).strip();
		    int comma2 = line.indexOf(',', comma1 + 1);
		    if (comma2 > 0 && address.length() > 0) {
		    	String color = line.substring(comma1 + 1, comma2).strip();
		    	if (color.length() == 0 ) {
		    		color = DefaultColor;
		    	}
		    	String notes = line.substring(comma2 + 1);
		    	if (notes.length() > 0) {
		    		data = new String[3];
		    		data[0] = address;
		    		data[1] = color;
		    		data[2] = notes;
		    	}
		    }
		}
		return data;
	}
	
	/**
	 * Get the file to use for the notes.csv data.
	 * 
	 * We use the latest of the file in our input directory or in the CreateNotesFile output directory.
	 * This saves the user the hassle of moving the file from the output directory to our input directory.
	 * @param inputDirectory
	 * @return
	 * @throws IOException
	 */
	protected static File findInputFile(File inputDirectory) throws IOException {
		File notesFileInputDirectory = new File(inputDirectory, NotesFileName);
		File notesFileCreateNotesDirectory = null;
		if (searchCreateNotesFileOutputDirectory) {  // Do this so we can turn it off for JUnits
		    notesFileCreateNotesDirectory = new File(CreateNoteFileOutputDirectory, NotesFileName);
		}
		
		return selectFile(notesFileInputDirectory, notesFileCreateNotesDirectory);
	}
	

	/**
	 * Pick the file to use.  Its whichever file exists, or if they both do, its the most recent.
	 * @param f1
	 * @param f2
	 * @return
	 * @throws IOException
	 */
	protected static File selectFile(File f1, File f2) throws IOException {
		
		// If a file doesn't exist, just null it out.
		if (f1 != null && !f1.exists()) {
			f1 = null;
		}
		if (f2 != null && !f2.exists()) {
			f2 = null;
		}

		// Check for the easy case if only one of the two, or neither, file exists
		if (f1 == null && f2 != null) {
			return f2;
		} else if (f1 != null && f2 == null) {
			return f1;
		} else if (f1 == null && f2 == null) {
			return null;
		} 	
		
		// Both files exists, pick the one that is the most recent
		long fileTime1 = Files.getLastModifiedTime(f1.toPath(), LinkOption.NOFOLLOW_LINKS).toMillis();
		long fileTime2 = Files.getLastModifiedTime(f2.toPath(), LinkOption.NOFOLLOW_LINKS).toMillis();
		if (fileTime1 > fileTime2) {
			return f1;
		}
		
		return f2;
	}
}
