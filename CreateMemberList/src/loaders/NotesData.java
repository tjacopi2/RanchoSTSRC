package loaders;

import java.util.Map;

public class NotesData {
	
	protected static String[] KeySuffixes = {"", "Way", "Drive", "Court", "Avenue", "Ave", "Ct."};
	
	public String color = "red";
	public String note = "";
	
	public NotesData(String aColor, String aNote) {
		if (aColor.length() > 0) {
		  color = aColor;
		}
		note = aNote;
	}
	
	
	public static NotesData FuzzyRemove(String key, Map<String, NotesData> notesMap) {
		NotesData foundNotesData = null;
		
		// Try all the suffixes until we find one that works (note the first suffix is really no suffix).
		for (String suffix : KeySuffixes) {
			String key2 = NotesLoader.MakeNotesKey(key + suffix);
			foundNotesData = notesMap.remove(key2);
			if (foundNotesData != null) {
				return foundNotesData;
			}
		}
		
		// If the key is a substring match, we call that good enough.
		for (String notesKey : notesMap.keySet()) {
			if (notesKey.startsWith(key) || key.startsWith(notesKey)) {
				foundNotesData = notesMap.remove(notesKey);
				return foundNotesData;
			}
		}
		
		return null;
	}
}