package loaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVReader;

import loaders.DaySummary.Household;

public class DataLoader {
    public static final String GUEST = "Guest_";
	 
	public static Map<Integer, YearSummary> LoadData(List<File> logFiles, Set<String> amAddresses) {
		int count = 0;
		Map<Integer, YearSummary> yearSummaries = new HashMap<Integer, YearSummary>();
		for (File f : logFiles) {
			try {
				LoadFile(amAddresses, yearSummaries, f);
				count++;
			} catch (IOException | ParseException e) {
				System.err.println("Skipping log file " + f.getAbsolutePath() + " because not a valid log format.  Error: " + e.getMessage());
				//e.printStackTrace();
			}
			
		}				
		System.out.println("Successfully read " + count + " log files");
		
		return yearSummaries;
	}

	public static void LoadFile(Set<String> amAddresses, Map<Integer, YearSummary> yearSummaries,
			File logFile) throws FileNotFoundException, ParseException, IOException {
		// Read the log file
		CSVReader reader = new CSVReader(new FileReader(logFile));
		Iterator<String[]> iter = reader.iterator();
		while (iter.hasNext()) {
			String[] values = iter.next();
			if (values.length >= 3) {
				String strDate = values[0];
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date2 = df.parse(strDate);
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(date2);
				
				// Is this the first time we have seen this year?
				Integer year = gc.get(Calendar.YEAR);
				YearSummary yearSummary = yearSummaries.get(year);
				if (yearSummary == null) {
					yearSummary = new YearSummary();
					yearSummaries.put(year, yearSummary);
				}
				
				// Is this the first time we have seen this month?
				Integer month = gc.get(Calendar.MONTH) + 1;
				MonthSummary monthSummaryMap = yearSummary.get(month);
				if (monthSummaryMap == null) {
					monthSummaryMap = new MonthSummary();
					yearSummary.put(month, monthSummaryMap);
				}
				
				// Is this the first time we have seen this day in the month?
				Integer day = gc.get(Calendar.DAY_OF_MONTH);
				DaySummary daySummary = monthSummaryMap.get(day);
				if (daySummary == null ) {
					daySummary = new DaySummary();
					daySummary.setDate(gc);
					monthSummaryMap.put(day, daySummary);
				}
				
				Integer entryHour = gc.get(Calendar.HOUR_OF_DAY);
				Integer currentCount = daySummary.getEntryHourMap().get(entryHour);
				if (currentCount != null) {
					daySummary.getEntryHourMap().put(entryHour, currentCount + 1);
				} else {
					daySummary.getEntryHourMap().put(entryHour, 1);
				}
				
				String address = values[1];
				Household h = daySummary.getHouseholds().get(address);
				if (h == null) {
					h = new Household();
					h.setAddress(address);
					if (amAddresses.contains(address)) {
		    			h.setAmHousehold(true);
		    		}
					daySummary.getHouseholds().put(address, h);
				}
				
				if (values[2] != null && values[2].startsWith(GUEST)) {
					h.setGuests(h.getGuests() + 1);
				}
				h.setPeople(h.getPeople() + 1);
				
			}
		}
		reader.close();
	}

	// Recursively search to find all possible log files
	public static void FetchLogFiles(File inputDirectory, List<File> logFiles) {
		for (File f : inputDirectory.listFiles()) {
			if (f.isDirectory()) {
				FetchLogFiles(f, logFiles);
			} else {
				if (f.getName().toLowerCase().endsWith(".csv")) {
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
