import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import loaders.NotesLoader;

public class PersonData {
	
	protected static final String Default_Person_Filename = "Personnel File.csv";
	public static final String Unknown = "<Unknown>";
	public static final String UnknownHouseNumber = "0";
	public static final String[] KnownFunnyAddressCodesArray = {"staff", "contractors", "keycode" };
	public static final List<String> KnownFunnyAddressCodes = Arrays.asList(KnownFunnyAddressCodesArray);
	
	
	private String street = null;
	private String houseNumber = null;
	private String name = null;
	private String id = null;
	
	public PersonData(String street, String houseNumber, String name, String id) {
		super();
		this.street = street;
		this.houseNumber = houseNumber;
		this.name = name;
		this.id = id;
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

	public String getId() {
		return id;
	}
	
	public static List<PersonData> loadPeople(File inputDirectory) throws IOException {
		if (!inputDirectory.exists() || !inputDirectory.isDirectory() ) {
			throw new IOException("Input directory " + inputDirectory.getCanonicalPath() + " does not exist");
		}
		
		// Get all the csv files except for notes.csv
		File[] inputFiles = inputDirectory.listFiles( new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return (name.toLowerCase().endsWith(".csv") && !name.equalsIgnoreCase(NotesLoader.NotesFileName));
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
		lines.remove(0);   // First line are column headers
		int lineNumber = 2;         // start at two because we removed the first line
		for (String line : lines) {
			PersonData person = parseLine(line, lineNumber);
			if (person != null) {
     			people.add(person);
			}
			lineNumber++;
		}
		return people;
	}

	protected static PersonData parseLine(String line, int lineNumber) {
		// 14,'RSTSC/Members/Los Palmos/252 Los Palmos,Paulina Thurmann 4,2,4086938057,pthurmann@yahoo.com,'2020/10/27 16:00:28,'2030/10/27 16:00:28,13258283;,,
        String[] columns = line.split(",");
        if (columns.length < 3) {
        	System.err.println("Line " + lineNumber + " is invalid because not enough columns");
        	return null;
        }
        
        // Look at the first column and insure its the numeric ID
        try {
        	Integer.valueOf(columns[0]);
        } catch (NumberFormatException e) {
        	System.err.println("Line " + lineNumber + " has the value " + columns[0] + " for the ID in column 1 which should be a number.  Line is " + line);
        	return null;	
        }
        
        // Get the address out of the second column.  Split apart the house number from the street
        String[] addressColumns = columns[1].split("/");
        String address = addressColumns[addressColumns.length -1].trim();
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
    
        
        // Now process the name.  Some names have a digit after them.  Remove that digit
        String name = columns[2];
        for (int i = name.length() -1; i >=0; i--) {
        	if (Character.isDigit(name.charAt(i))) {
        		name = name.substring(0, i);
        	} else {
        		break;               // we can quit once we run out of digits
        	}
        }
        name = name.replaceAll("\"", "");  // Insure we have no quotes in the name
        name = name.replaceAll("'", "");
        name = name.trim();
        if (name.length() == 0) {
        	System.err.println("Warning:  Line " + lineNumber + " has no detected name. "
        			+ "  This was calculated from value: " + columns[2] + " in line " + line);
        	name = Unknown;
        }
        
		return new PersonData(street, houseNumber, name, columns[0]);
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
				"Personal file should be named \"" + Default_Person_Filename + "\" or be the only .csv file in the directory other than notes.csv.  Other csv files in the directory are: ";
		for (File f : inputFiles) {
			msg = msg + f.getCanonicalPath() + "\n";
		}
		throw new IOException(msg);
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
