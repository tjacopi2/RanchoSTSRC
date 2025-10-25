package loaders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class HOALoader {
	protected final static String HOA_FILENAME = "hoaAddresses.txt";
	protected final static String[] Suffixes = {"way", "drive", "street", "court"};
	
	public static Set<String> LoadData(File inputDirectory) throws IOException {
		File amFile = new File(inputDirectory, HOA_FILENAME);
		Set<String> hoaSet = new HashSet<String>();
		hoaSet.addAll(Files.readAllLines(amFile.toPath()));
		
		System.out.println("Loaded " + hoaSet.size() + " HOA member addresses");
		
		// To help address matching, add versions of the address without the suffixes, plus
		// lower case versions of the address.
		HashSet<String> additionalAddresses = new HashSet<String>();
		for (String hoaAddress : hoaSet) {
			hoaAddress = hoaAddress.trim();            // Get a version with no trailing blanks
			additionalAddresses.add(hoaAddress);
			hoaAddress = hoaAddress.toLowerCase();     // Get a lower case version
			additionalAddresses.add(hoaAddress);       // Add this because its a lower case version
			
			for (String suffix : Suffixes) {
				if (hoaAddress.endsWith(suffix)) {
					String newAddress = hoaAddress.replace(suffix, "").trim();
					additionalAddresses.add(newAddress);
				}
			}
		}
		
		hoaSet.addAll(additionalAddresses);
		
		return hoaSet;
	}

}
