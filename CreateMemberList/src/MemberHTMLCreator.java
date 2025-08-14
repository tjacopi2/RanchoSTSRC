import java.io.PrintWriter;
import java.util.List;

public class MemberHTMLCreator {
	private PrintWriter writer;
	protected static final String HTMLSpace = "&nbsp;";
	protected static final String ID_REPLACEMENT_TAG = "%ID%";
	protected static final String NAME_REPLACEMENT_TAG = "%NAME%";
	protected static final String ImageTag = "<img src=\"" + ID_REPLACEMENT_TAG + ".jpg\" alt=\"" + NAME_REPLACEMENT_TAG + "\" width=\"200\" height=\"200\" />";
	protected static final String COLOR_REPLACEMENT_TAG = "%COLOR%";
	protected static final String NOTE_TEXT_REPLACEMENT_TAG = "%NOTE%";
	protected static final String NotesTag = "    <td style=\"color: " + COLOR_REPLACEMENT_TAG + ";\">" + NOTE_TEXT_REPLACEMENT_TAG;
	
	public MemberHTMLCreator(PrintWriter writer) {
		super();
		this.writer = writer;
	}

	public void generateMembers(List<PersonData> people) {
		/*
		 * <tr>
             <td>Tom Jacopi3&nbsp <img src="Dante Mingione_3.jpg" alt="tom" width="200" height="200"></img> &nbsp &nbsp Terrie Jacopi4&nbsp <img src="Dante Mingione_4.jpg" alt="terrie" width="200" height="200"></img></td>
             <td>3 Camino Verde</td>
             <td style="color: red;">No entry.  Behind on dues.</td>
           </tr>
		 */
		String previousStreet = "";
		String previousHouseNumber = "";
		String notes = "";
		boolean firstTime = true;
		for (PersonData person : people) {
			
			// If the address has changed, make a new table entry
			if (!previousStreet.equalsIgnoreCase(person.getStreet()) || !previousHouseNumber.equalsIgnoreCase(person.getHouseNumber())) {
				if (!firstTime) {
					endPreviousTableEntry(notes, previousStreet, previousHouseNumber);
				}
				firstTime = false;
				
				previousStreet = person.getStreet();
				previousHouseNumber = person.getHouseNumber();
				notes = "";
				
				// Start a new table entry
				writer.println("  <tr>");
				writer.print("    <td>");
			}
			
			// Write new person
			//writer.print(person.getName());
			
			// Write each persons name as a link, and when the link fires it calls our javascript function
			//<a href="javascript:sendLogRequest('tom jacopi', '6346 Camino Verde');">Tom Jacopi</a>
			writer.print("<a href=\"javascript:sendLogRequest('");
			writer.print(person.getName());
			writer.print("', '");
			writer.print(previousHouseNumber);
			writer.print(' ');
			writer.print(previousStreet);
			// print address
			writer.print("');\">");
			writer.print(person.getName());
			writer.print("</a>");
			
			notes = notes + "  " + person.getNotes();
			//writer.print(HTMLSpace);writer.print(HTMLSpace);
			
			// Write the image tag if we have a new person
			if (person.getPictureFileName() != null && person.getPictureFileName().length()>0) {
				String personEntry = ImageTag.replace(ID_REPLACEMENT_TAG, person.getPictureFileName()).replace(NAME_REPLACEMENT_TAG, person.getName());
				writer.print(personEntry);
			}
			writer.print("<br>");    // Break the line after each persons name
		}
		
		// close off previous table entry
		endPreviousTableEntry(notes, previousStreet, previousHouseNumber);
	}

	protected void endPreviousTableEntry(String notes, String previousStreet, String previousHouseNumber) {
		/*  This is that the method should produce
		 *    <a>Guest</a><br>                        </td>
              <td style="color: blue;">Some silly note</td>
              <td>123&nbspDondero</td>
           </tr>
		 */
		
		// End the tag for people
		writer.print("<a href=\"./guestPassForm.html?address=" + previousHouseNumber + " " + previousStreet + "\" target=\"_blank\">Guest</a><br>");
		writer.println("</td>");

		// Write notes
		notes = notes.trim();
		if (notes.length() > 0) {
			String notesEntry = NotesTag.replace(COLOR_REPLACEMENT_TAG, "red").replace(NOTE_TEXT_REPLACEMENT_TAG, notes);
			writer.print(notesEntry);
		} else {
			writer.print("    <td>");
		}
		writer.println("</td>");

		// write address
		writer.print("    <td>");
		writer.print(previousHouseNumber);
		writer.print(HTMLSpace);
		writer.print(previousStreet);
		writer.println("</td>");

		writer.println("  </tr>");
	}
	
}
