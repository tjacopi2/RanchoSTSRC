package loaders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class AMLoader {
	protected final static String AM_FILENAME = "AMList.txt";
	
	public static Set<String> LoadData(File inputDirectory) throws IOException {
		File amFile = new File(inputDirectory, AM_FILENAME);
		Set<String> amSet = new HashSet<String>();
		amSet.addAll(Files.readAllLines(amFile.toPath()));
		
		System.out.println("Loaded " + amSet.size() + " associate member addresses");
		return amSet;
	}

}
