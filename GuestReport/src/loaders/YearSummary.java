package loaders;

import java.util.HashMap;

/**
 * This class is a map:
 *    o  The key is the number of the month in the year, 1 thru 12.  If there is no data for that month, there is no entry in the map
 *    o  The value is the MonthSummary, which is the activity for that month.
 */
public class YearSummary extends HashMap<Integer, MonthSummary> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -623723270749409515L;

	public int getTotalHOAMembers() {
		int count = 0;
		for (MonthSummary ms : values()) {
			count = count + ms.getTotalHOAMembers();
		}
		return count;
	}
	
	public int getTotalHOAGuests() {
		int count = 0;
		for (MonthSummary ms : values()) {
			count = count + ms.getTotalHOAGuests();
		}
		return count;
	}
	

	public int getTotalAMMembers() {
		int count = 0;
		for (MonthSummary ms : values()) {
			count = count + ms.getTotalAMMembers();
		}
		return count;
	}

	public int getTotalAMGuests() {
		int count = 0;
		for (MonthSummary ms : values()) {
			count = count + ms.getTotalAMGuests();
		}
		return count;
	}
}
