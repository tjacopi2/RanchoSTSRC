package loaders;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVReader;

public class DaySummary {
	
    public static final String GUEST = "Guest_";
    
	private GregorianCalendar date = null;
	private int totalPeople = 0;
	private int totalGuests = 0;
	private int totalAM = 0;
	private int totalAMGuests = 0;
	private List<Household> households = new ArrayList<Household>();
	private File htmlDetailFile = null;
	private Map<Integer, Integer> entryHourMap = new HashMap<Integer, Integer>();
	
	public static DaySummary LoadFrom(File logFile, Set<String> amAddresses) throws IOException, ParseException {
		DaySummary summary = new DaySummary();
		Map<String, Household> householdMap = new HashMap<String, Household>();
		
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
				if (summary.date == null) {
					summary.date = gc;
				}

				Integer entryHour = gc.get(Calendar.HOUR_OF_DAY);
				Integer currentCount = summary.entryHourMap.get(entryHour);
				if (currentCount != null) {
					summary.entryHourMap.put(entryHour, currentCount + 1);
				} else {
					summary.entryHourMap.put(entryHour, 1);
				}
				
				String address = values[1];
				Household h = householdMap.get(address);
				if (h == null) {
					h = new Household();
					h.setAddress(address);
					householdMap.put(address, h);
				}
				if (values[2] != null && values[2].startsWith(GUEST)) {
					h.guests++;
				}
				h.people++;
			}
    	}
    	reader.close();
    	
    	for (Household h : householdMap.values()) {
    		summary.households.add(h);
    		summary.totalGuests = summary.totalGuests + h.guests;
    		summary.totalPeople = summary.totalPeople + h.people;
    		if (amAddresses.contains(h.getAddress())) {
    			summary.totalAM = summary.totalAM + h.people;
    			summary.totalAMGuests = summary.totalAMGuests + h.guests;
    			h.setAmHousehold(true);
    		}
    	}
    	
    	Collections.sort(summary.households, new HouseholdComparator());  	
		return summary;
		
	}
	
	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

	public int getTotalPeople() {
		return totalPeople;
	}

	public void setTotalPeople(int totalPeople) {
		this.totalPeople = totalPeople;
	}

	public int getTotalGuests() {
		return totalGuests;
	}

	public void setTotalGuests(int totalGuests) {
		this.totalGuests = totalGuests;
	}

	public List<Household> getHouseholds() {
		return households;
	}

	public void setHouseholds(List<Household> households) {
		this.households = households;
	}

	public File getHtmlDetailFile() {
		return htmlDetailFile;
	}

	public void setHtmlDetailFile(File htmlDetailFile) {
		this.htmlDetailFile = htmlDetailFile;
	}

	public int getTotalAM() {
		return totalAM;
	}

	public int getTotalAMGuests() {
		return totalAMGuests;
	}
	
	public int getTotalHOA() {
		return totalPeople - totalAM;
	}
	
	public int getTotalHOAGuests() {
		return totalGuests - totalAMGuests;
	}

	public Map<Integer, Integer> getEntryHourMap() {
		return entryHourMap;
	}

	public static class Household {
		private String address;
		private int people = 0;
		private int guests = 0;
		private boolean amHousehold = false;
		
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public int getPeople() {
			return people;
		}
		public void setPeople(int people) {
			this.people = people;
		}
		public int getGuests() {
			return guests;
		}
		public void setGuests(int guests) {
			this.guests = guests;
		}
		public boolean isAmHousehold() {
			return amHousehold;
		}
		public void setAmHousehold(boolean amHousehold) {
			this.amHousehold = amHousehold;
		}
		
	}

	public static class HouseholdComparator implements Comparator<Household> {
		@Override
		public int compare(Household o1, Household o2) {
			if (o1.people > o2.people) {
				return -1;
			} else if (o1.people == o2.people) {
				return 0;
			} else {
				return 1;
			}
		}
		
	}
}
