package loaders;

import java.util.HashMap;

/**
 * This class is a map:
 *    o  The key is the number of the day in the month, 1 thru 31.  If there is no data for that day, there is no entry in the map
 *    o  The value is the DaySummary, which is the activity for that day.
 */
public class MonthSummary extends HashMap<Integer, DaySummary> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2353587218301712850L;
	
	public int getTotalHOAMembers() {
		int count = 0;
		for (DaySummary ds : values()) {
			count = count + ds.getTotalHOA();
		}
		return count;
	}
	
	public int getTotalHOAGuests() {
		int count = 0;
		for (DaySummary ds : values()) {
			count = count + ds.getTotalHOAGuests();
		}
		return count;
	}
	

	public int getTotalAMMembers() {
		int count = 0;
		for (DaySummary ds : values()) {
			count = count + ds.getTotalAM();
		}
		return count;
	}

	public int getTotalAMGuests() {
		int count = 0;
		for (DaySummary ds : values()) {
			count = count + ds.getTotalAMGuests();
		}
		return count;
	}

}
