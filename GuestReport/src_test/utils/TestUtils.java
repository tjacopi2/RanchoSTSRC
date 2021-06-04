package utils;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {
	public static List<Integer> CountSubstrings(String string, String substring) {
		List<Integer> occurances = new ArrayList<Integer>();
		
		int foundIndex = string.indexOf(substring);

		while(foundIndex >=0) {
			occurances.add(foundIndex);
			foundIndex = string.indexOf(substring, foundIndex + substring.length());
		}
		
		return occurances;
	}

}
