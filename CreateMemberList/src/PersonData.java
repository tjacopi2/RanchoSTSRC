import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonData {
	
	protected static final String Default_Person_Filename = "ranchoHouseholdDatasheets.csv";
	public static final String Unknown = "<Unknown>";
	public static final String UnknownHouseNumber = "0";
	public static final String[] KnownFunnyAddressCodesArray = {"staff", "contractors", "keycode" };
	public static final List<String> KnownFunnyAddressCodes = Arrays.asList(KnownFunnyAddressCodesArray);
	
	public static final String PersonNameColumnName = "Full Name";
	public static final String AddressColumnName = "Address";
	public static final String NotesColumnName = "Check-in Notes";
	public static final String PictureFileColumnName = "Picture File Name";
	
	private String street = null;
	private String houseNumber = null;
	private String name = null;
	private String pictureFileName = null;
	private String notes = null;
	
	public PersonData(String street, String houseNumber, String name, String pictureFileName, String notes) {
		super();
		this.street = street;
		this.houseNumber = houseNumber;
		this.name = name;
		this.pictureFileName = pictureFileName;
		this.notes = notes;
	}

	public String getStreet() {
		return street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public String getName() {
		return name;
	}

	public String getPictureFileName() {
		return pictureFileName;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public static List<PersonData> loadPeople(File inputDirectory) throws IOException {
		if (!inputDirectory.exists() || !inputDirectory.isDirectory() ) {
			throw new IOException("Input directory " + inputDirectory.getCanonicalPath() + " does not exist");
		}
		
		// Get all the csv files except for notes.csv
		File[] inputFiles = inputDirectory.listFiles( new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return (name.toLowerCase().endsWith(".csv") );
		    }
		} );
		
		
		if (inputFiles.length == 0) {
			throw new IOException("No \"" + Default_Person_Filename + "\" file in input directory " + inputDirectory.getCanonicalPath());
		}
		
		// Pick the correct zip file
		File personFile = selectPersonFile(inputFiles);
		
		// Load data from person file
		List<PersonData> people = LoadData(personFile);
		
		sortPeople(people);
		
		System.out.println("Successfully loaded " + people.size() + " members from " + personFile.getCanonicalPath());
		return people;
	}
	
	protected static void sortPeople(List<PersonData> people) {
		Collections.sort(people, new PersonDataComparator());
	}

	protected static List<PersonData> LoadData(File personFile) throws IOException {
		List<PersonData> people = new ArrayList<PersonData>();
		
		List<String> lines = Files.readAllLines(personFile.toPath());
		
		Map<String, Integer> columnMapping = findColumnMappings(lines.get(0));
		lines.remove(0);   // First line are column headers
		int lineNumber = 2;         // start at two because we removed the first line
		for (String line : lines) {
			PersonData person = parseLine(line, lineNumber, columnMapping);
			if (person != null) {
     			people.add(person);
			}
			lineNumber++;
		}
		return people;
	}
	

	protected static Map<String, Integer> findColumnMappings(String columnHeaders) throws IOException {
		Map<String, Integer> columnMapping = new HashMap<String, Integer>();
        String[] columnNames = columnHeaders.split(",");
        if (columnNames.length < 4) {
        	throw new IOException("File is invalid because first line much have at least 4 column names, and it only has " + columnNames.length + "  Line is: "+ columnHeaders);
        }

        List<String> columnNameList = Arrays.asList(columnNames); 
        columnMapping.put(PersonNameColumnName, findColumnNameIndex(columnNameList, PersonNameColumnName, columnHeaders));
        columnMapping.put(AddressColumnName, findColumnNameIndex(columnNameList, AddressColumnName, columnHeaders));
        columnMapping.put(NotesColumnName, findColumnNameIndex(columnNameList, NotesColumnName, columnHeaders));
        columnMapping.put(PictureFileColumnName, findColumnNameIndex(columnNameList, PictureFileColumnName, columnHeaders));
	
		return columnMapping;
	}
	
	protected static Integer findColumnNameIndex(List<String> columnNames, String columnName, String columnHeaders) throws IOException {
	  int index = columnNames.indexOf(columnName);
	  if (index < 0) {
	   	throw new IOException("File is invalid because there is not a column called " + columnName + "  Column Header Line is: "+ columnHeaders);
	  }
	  return Integer.valueOf(index);
	}

	protected static PersonData parseLine(String line, int lineNumber, Map<String, Integer> columnMapping) {
		// Member,182 Castillon Way,HO Amzi Slutzky,Outstanding Dues $142,Amzi.jpg,
        //String[] columns = line.split(",");
        String[] columns = PersonData.splitLine(line);
        
        // This data format gives blanks lines, so ingore them.
        if (columns.length == 1 && columns[0].trim().length() == 0) {
        	return null;
        }
        if (columns.length < 4) {
        	System.err.println("Line " + lineNumber + " is invalid because not enough columns");
        	return null;
        }
                    
        // Get the address out of the address column.  Split apart the house number from the street
        String address = columns[columnMapping.get(AddressColumnName)].trim();
        StringBuffer sbStreet = new StringBuffer();
        StringBuffer sbHouseNumber = new StringBuffer();
        for (int i = 0; i < address.length(); i++) {
        	char currentChar = address.charAt(i);
        	if (sbStreet.length()>0) {                        // If we already are appending to the street 
        		sbStreet.append(currentChar);                 // ..then anything at this point must be the street
        	} else if (Character.isDigit(currentChar)) {    // If we have not done the street yet and its a digit
        		sbHouseNumber.append(currentChar);            // ..then its the number
        	} else 
        		sbStreet.append(currentChar);                 // Not a number, so must be the first char of the street
        }
        
        // Now validate that the street and house number look ok and insure no quotes in the address
        String street = sbStreet.toString().trim().replaceAll("\"", "").replaceAll("'", "");
        String houseNumber = sbHouseNumber.toString().trim().replaceAll("\"", "").replaceAll("'", "");
        if (street.length() == 0 && houseNumber.length() == 0) {
        	System.err.println("Warning:  Line " + lineNumber + " has no detected address. "
        			+ "  This was calculated from value: " + columns[1]);
        	street = Unknown;
        	houseNumber = UnknownHouseNumber;
        } else if (street.length() == 0) {
    		System.out.println("Warning:  Line " + lineNumber + " has the address " + address + " but no street name.  "
    				+ "  This was calculated from value: " + columns[1]);
            street = Unknown;
        } else if (houseNumber.length() == 0) {
        	if (!KnownFunnyAddressCodes.contains(street.toLowerCase())) {
    		  System.out.println("Warning:  Line " + lineNumber + " has the address " + address + " but no house number.  "
    				+ "  This was calculated from value: " + columns[1]);
        	}
            houseNumber = UnknownHouseNumber;
        }
    
        
        // Now process the name.  
        String name = columns[columnMapping.get(PersonNameColumnName)];
        name = name.replaceAll("\"", "");  // Insure we have no quotes in the name
        name = name.replaceAll("'", "");
        name = name.trim();
        if (name.length() == 0) {
        	System.err.println("Warning:  Line " + lineNumber + " has no detected name. "
        			+ "  This was calculated from value: " + columns[2] + " in line " + line);
        	name = Unknown;
        }
        
        // Get any notes
        String notes = "";
        if (columns.length > columnMapping.get(NotesColumnName)) {
        	notes = columns[columnMapping.get(NotesColumnName)].trim();
        }
        
        // Get the image file name
        String imageFileName = "";
        if (columns.length > columnMapping.get(PictureFileColumnName)) {
        	imageFileName = columns[columnMapping.get(PictureFileColumnName)].trim();
        }
        
		return new PersonData(street, houseNumber, name, imageFileName, notes);
	}

	protected static File selectPersonFile(File[] inputFiles) throws IOException {
		if (inputFiles.length == 1) {
			return inputFiles[0];
		}

		for (File f : inputFiles) {
			if (f.getName().equalsIgnoreCase(Default_Person_Filename)) {
				return f;
			}
		}

		String dirName = "<" + inputFiles[0].getCanonicalPath() + "> does not exist";
		if (inputFiles[0].getParent() != null) {
			dirName = inputFiles[0].getParentFile().getCanonicalPath();
		}
		String msg = "Could not determine .csv file containing person information in inputDirectory " + dirName + "\n" + 
				"Personal file should be named \"" + Default_Person_Filename + "\" or be the only .csv file in the directory.  Other csv files in the directory are: ";
		for (File f : inputFiles) {
			msg = msg + f.getCanonicalPath() + "\n";
		}
		throw new IOException(msg);
	}

	protected static String[] splitLine(String line) {
		List<String> strings = new ArrayList<String>();
		int startIndex = 0;
		boolean inQuotedColumn = false;
		for (int i = 0; i<line.length(); i++) {
			if (line.charAt(i) == ',' && !inQuotedColumn) {
				if (i == startIndex) {
					strings.add("");
				} else {
					// trim quotes if needed
					String colData = line.substring(startIndex,i);
					if (colData.length() >= 3 && colData.charAt(0) == '"' && colData.charAt(colData.length()-1) == '"') {
					  // This was a quoted column, strip the quotes.
						colData = colData.substring(1, colData.length()-1);
					}
				    strings.add(colData);
				}
				startIndex = i+1;
			} else {
				if (line.charAt(i) == '"') {
					if (inQuotedColumn) {
						inQuotedColumn = false;
					} else if (startIndex == i) {           // Is this the first char in the column?
						inQuotedColumn = true;
					}
				}
			}
		}
		
		// Add the last token
		if (startIndex == line.length()) {
			strings.add("");
		} else {
			// trim quotes if needed
			String colData = line.substring(startIndex);
			if (colData.length() >= 3 && colData.charAt(0) == '"' && colData.charAt(colData.length()-1) == '"') {
			  // This was a quoted column, strip the quotes.
				colData = colData.substring(1, colData.length()-1);
			}
		    strings.add(colData);
		}
		return strings.toArray(new String[0]);
	}
	
	// Compare first on the street, then on the number.  This groups all entries by the street and then ascending address within the street.
	private static class PersonDataComparator implements Comparator<PersonData> {
		@Override
		public int compare(PersonData o1, PersonData o2) {
			int streetCompare = o1.street.compareTo(o2.street);
			if (streetCompare == 0) {
				// We need to do integer comparisons so "9" will sort ahead of "10"
				Integer o1Integer = Integer.valueOf(o1.houseNumber);
				Integer o2Integer = Integer.valueOf(o2.houseNumber);
				return o1Integer.compareTo(o2Integer);
			}
			return streetCompare;
		}
		
	}
}
