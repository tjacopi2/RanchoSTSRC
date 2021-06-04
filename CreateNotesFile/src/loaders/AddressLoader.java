package loaders;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;


public class AddressLoader {
	public static final String AddressFileName = "Name Address.csv";
	
	public static String MakeKey(String text) {
		return text.replaceAll(" ","").toLowerCase();
	}
	
	public static List<String> GetNonEmptyValues(String[] values) {
		List<String> nonEmptyStrings = new ArrayList<String>();
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				if (values[i] != null && values[i].trim().length() > 0) {
					nonEmptyStrings.add(values[i]);
				}
			}
		}
		return nonEmptyStrings;
	}
	
	public static Map<String, String> loadAddressMap(File inputDirectory) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		File addressFile = new File(inputDirectory, AddressFileName);
		if (!addressFile.exists()) {
			String text = "Could not find " + AddressFileName + " file in input directory " + inputDirectory.getCanonicalPath();
			System.err.println(text);
            throw new IOException(text);
		}
		
	    CSVReader reader = new CSVReader(new FileReader(addressFile));
		Iterator<String[]> iter = reader.iterator();
		while (iter.hasNext()) {
			String[] values = iter.next();
			List<String> nonEmptyStrings = GetNonEmptyValues(values);
			if (nonEmptyStrings.size() == 2) {
				String oldValue = map.put(MakeKey(nonEmptyStrings.get(0)), nonEmptyStrings.get(1));
				if (oldValue != null) {
					System.out.println("Warning, names " + nonEmptyStrings.get(0) + " has been seen twice, once for address " + 
				        oldValue + " and for " + nonEmptyStrings.get(1));
				}
			}
		}
		
		System.out.println("Loaded " + map.size() + " addresses from file " + addressFile.getCanonicalPath());
		return map;
	}

}
