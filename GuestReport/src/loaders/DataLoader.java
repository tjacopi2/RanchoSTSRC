package loaders;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataLoader {
	
	public static Map<Integer, List<DaySummary>> LoadData(List<File> logFiles, Set<String> amAddresses) {
		int count = 0;
		Map<Integer, List<DaySummary>> summaryMapByMonth = new HashMap<Integer, List<DaySummary>>();
		for (File f : logFiles) {
			try {
				DaySummary summary = DaySummary.LoadFrom(f, amAddresses);
				Integer month = summary.getDate().get(Calendar.MONTH) + 1;
				List<DaySummary> summaryList = summaryMapByMonth.get(month);
				if (summaryList == null) {
					summaryList = new ArrayList<DaySummary>();
					summaryMapByMonth.put(month, summaryList);
				}
				
				summaryList.add(summary);
				count++;
			} catch (IOException | ParseException e) {
				System.err.println("Skipping log file " + f.getAbsolutePath());
				e.printStackTrace();
			}
			
		}
		
		for (List<DaySummary> monthValues : summaryMapByMonth.values()) {
			Collections.sort(monthValues, new DateComparator());
		}
		
		System.out.println("Successfully read " + count + " log files");
		
		return summaryMapByMonth;
	}

	// Recursively search to find all possible log files
	public static void FetchLogFiles(File inputDirectory, List<File> logFiles) {
		for (File f : inputDirectory.listFiles()) {
			if (f.isDirectory()) {
				FetchLogFiles(f, logFiles);
			} else {
				if (f.getName().toLowerCase().startsWith("log") && f.getName().toLowerCase().endsWith(".csv")) {
					logFiles.add(f);
				}
			}
		}
	}

	public static class DateComparator implements Comparator<DaySummary> {
		@Override
		public int compare(DaySummary o1, DaySummary o2) {
			int day1 = o1.getDate().get(Calendar.DAY_OF_MONTH);
			int day2 = o2.getDate().get(Calendar.DAY_OF_MONTH);
			if (day1 > day2) {
				return 1;
			} else if (day1 == day2) {
				return 0;
			} else {
				return -1;
			}
		}
		
	}
}
