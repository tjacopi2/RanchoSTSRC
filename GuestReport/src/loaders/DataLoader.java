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
	
	public static Map<Integer, List<LogFileSummary>> LoadData(File inputDirectory, Set<String> amAddresses) {
		int count = 0;
		Map<Integer, List<LogFileSummary>> summaryMapByMonth = new HashMap<Integer, List<LogFileSummary>>();
		for (File f : inputDirectory.listFiles(new LogFileNameFilter())) {
			try {
				LogFileSummary summary = LogFileSummary.LoadFrom(f, amAddresses);
				Integer month = summary.getDate().get(Calendar.MONTH) + 1;
				List<LogFileSummary> summaryList = summaryMapByMonth.get(month);
				if (summaryList == null) {
					summaryList = new ArrayList<LogFileSummary>();
					summaryMapByMonth.put(month, summaryList);
				}
				
				summaryList.add(summary);
				count++;
			} catch (IOException | ParseException e) {
				System.err.println("Skipping log file " + f.getAbsolutePath());
				e.printStackTrace();
			}
			
		}
		
		for (List<LogFileSummary> monthValues : summaryMapByMonth.values()) {
			Collections.sort(monthValues, new DateComparator());
		}
		
		System.out.println("Successfully read " + count + " log files");
		
		return summaryMapByMonth;
	}

	public static class DateComparator implements Comparator<LogFileSummary> {
		@Override
		public int compare(LogFileSummary o1, LogFileSummary o2) {
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
