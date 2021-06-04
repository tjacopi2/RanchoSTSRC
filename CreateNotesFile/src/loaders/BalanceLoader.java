package loaders;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;

public class BalanceLoader {
	
	public static final String BalanceFileName = "Name Dues Owed.csv";
	
	public static Map<String, String> loadNameToBalanceMap(File inputDirectory) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		File balancesFile = new File(inputDirectory, BalanceFileName);
		if (!balancesFile.exists()) {
			String text = "Could not find " + BalanceFileName + " file in input directory " + inputDirectory.getCanonicalPath();
			System.err.println(text);
            throw new IOException(text);
		}
		
	    CSVReader reader = new CSVReader(new FileReader(balancesFile));
		Iterator<String[]> iter = reader.iterator();
		while (iter.hasNext()) {
			String[] values = iter.next();
			List<String> nonEmptyStrings = AddressLoader.GetNonEmptyValues(values);
			if (nonEmptyStrings.size() == 2) {
				String oldValue = map.put(AddressLoader.MakeKey(nonEmptyStrings.get(0)), nonEmptyStrings.get(1));
				if (oldValue != null) {
					System.out.println("Warning, name " + nonEmptyStrings.get(0) + " has been seen twice, once for balance " + 
				        oldValue + " and for " + nonEmptyStrings.get(1));
				}
			}
		}
		
		System.out.println("Loaded " + map.size() + " names from file " + balancesFile.getCanonicalPath());
		return map;
	}

}
