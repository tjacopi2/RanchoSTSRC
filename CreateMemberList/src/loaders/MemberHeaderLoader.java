package loaders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MemberHeaderLoader {
	
	private static final String HeaderFileName = "membersHeader.html";
	private static final String ReplacementString = "%INSERT_HERE%";
	
	public static String[] loadHeaders(File inputDirectory) throws IOException {
		String[] headers = new String[2];
		Path p = Paths.get(inputDirectory.getCanonicalPath(), HeaderFileName);
		if (!p.toFile().exists()) {
			String msg = "Could not find " + HeaderFileName + " file in input directory " + inputDirectory.getCanonicalPath();
            throw new IOException(msg);
		}
		
		String data = Files.readString(p);
		headers = data.split(ReplacementString);
		
		if (headers.length != 2) {
			  throw new IOException("Could not find one and only one instance of \"" + ReplacementString +"\" in file " + p.toString());
		}
		
		System.out.println("Successfully loaded html skeleton from " + p.toString());
		return headers;
	}

}
