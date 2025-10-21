package loaders;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaySummary {
	
	private GregorianCalendar date = null;
	private int totalPeople = 0;
	private int totalGuests = 0;
	private int totalAM = 0;
	private int totalAMGuests = 0;
	private Map<String, Household> households = new HashMap<String, Household>();
	private File htmlDetailFile = null;
	private Map<Integer, Integer> entryHourMap = new HashMap<Integer, Integer>();
	
	
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

	public Map<String, Household> getHouseholds() {
		return households;
	}

	public List<Household> getHouseholdsList() {
		List<Household> householdList = new ArrayList<Household>();
		householdList.addAll(households.values());
		Collections.sort(householdList, new HouseholdComparator());  	
		return householdList;
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
	
	public int setTotalAM(int newAM) {
		return totalAM = newAM;
	}

	public int setTotalAMGuests(int newAMGuests) {
		return totalAMGuests = newAMGuests;
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
