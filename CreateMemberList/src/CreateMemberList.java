import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import loaders.MemberHeaderLoader;
import loaders.NotesData;
import loaders.NotesLoader;
import loaders.PictureLoader;

public class CreateMemberList {
	
	private static final String MembersHTMLFileName = "members.html";
	protected static File inputDirectory = new File("input");
	protected static File outputDirectory = new File("output");

	public static void main(String[] args) throws IOException {
		// Get all input files
		File[] inputFiles = inputDirectory.listFiles();
		if (inputFiles.length < 1) {
			throw new IOException("Error, cannot file any files in input directory: " + inputDirectory.getCanonicalPath());
		}

		// Load membersHeader.html
		String[] htmlHeaders = MemberHeaderLoader.loadHeaders(inputDirectory);

		// Load Notes
		Map<String, NotesData> notesMap = NotesLoader.loadNotes(inputDirectory);
		
		// Load pictures
		ZipFile pictureFile = PictureLoader.loadPictures(inputDirectory);
		Map<String, ZipEntry> pictureMap = PictureLoader.buildPictureMap(pictureFile);
		System.out.println("Picture file " + pictureFile.getName() + " contains " + pictureFile.size() + " entries");
		
		// Read members.csv
		List<PersonData> people = PersonData.loadPeople(inputDirectory);
		
		// Prepare the output directory
		if (!outputDirectory.exists()) {
			boolean success = outputDirectory.mkdir();
			if (!success) {
				System.err.println("Could not create output directory " + outputDirectory.getCanonicalPath());
				return;
			}
		}
		
		// Open the membersHtml file to write
		File membersHTMLFile = new File(outputDirectory, MembersHTMLFileName);
		PrintWriter out = new PrintWriter(membersHTMLFile);
		MemberHTMLCreator htmlCreator = new MemberHTMLCreator(out);
		
		// Write the html file
		out.write(htmlHeaders[0]);                                  // Write first part of html file
		htmlCreator.generateMembers(people, pictureMap, notesMap);  // Write an entry for each member
		out.write(htmlHeaders[1]);                                  // Write the last part of the file 
		out.flush();
		out.close();

		// Write all the image files
		PictureLoader.writePictures(outputDirectory, pictureFile, pictureMap);
		pictureFile.close();
		
		// Write out a warning for all addresses in the notes.csv file but were not in the person data exported from the FOB system.
		// That may be valid as not all addresses are in the FOB system.
		int invalidEntries = 0;
		StringBuffer sb = new StringBuffer();
		for (String address : notesMap.keySet()) {
    		// There are some bogus values in the notes.csv data, so unless the address begins with a digit assume it can be ingored.
    		if (Character.isDigit(address.charAt(0))) {
    			invalidEntries++;
    			sb.append(address);
    			sb.append(", ");
    		}
    	}
		if (sb.length() > 0) {
			System.out.println("Warning: A total of " + invalidEntries + " addresses were found in the notes.csv file but not in the person list");
		    System.out.println("...The addresses are: " + sb.toString());
		}

	}

}
