import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FindAssociateMembers {

	public static void main(String[] args) throws IOException {
		File inputFile = new File("input\\", "Personnel File.csv");
		File outputFile = new File("output\\", "amList.txt");
		
		List<String> lines = Files.readAllLines(inputFile.toPath());
		Set<String> amLines = new HashSet<String>();
		for (String line : lines) {
			if (line.contains("2021 Associate Members")) {
			  amLines.add(line.trim());
			}
		}
		for (String line : amLines) {
	    	Files.writeString(outputFile.toPath(), line + "\n", StandardOpenOption.APPEND);
		}
	}

}
