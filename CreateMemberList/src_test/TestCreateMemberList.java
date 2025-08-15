import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestCreateMemberList {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testMain_SmallData() throws IOException {
			File testDataDirectory = new File("testData/TestCreateMemberList_small");
			File testInputDataDirectory = new File(testDataDirectory, "input");
			File testOutputDataDirectory = new File(testDataDirectory, "output");
			assertTrue(testInputDataDirectory.isDirectory(), "Can not find input directory");
			assertTrue(testOutputDataDirectory.isDirectory(), "Can not find output directory");

			// Clean up output directory
			File filesList[] = testOutputDataDirectory.listFiles();
			for(File file : filesList) {
				if(file.isFile()) {
					if (!file.delete()) {
						fail("Setup error : Could not delete file " + file.getCanonicalPath());
					};
				}
			}
			
			// Copy jpg files to output directory
			filesList = testInputDataDirectory.listFiles();
			for(File file : filesList) {
				if(file.isFile()) {
					if (file.getName().endsWith(".jpg")) {
						File newFile = new File(testOutputDataDirectory, file.getName());
						Files.copy(file.toPath(), newFile.toPath());
					};
				}
			}

			CreateMemberList.inputDirectory = testInputDataDirectory;
			CreateMemberList.outputDirectory = testOutputDataDirectory;
			CreateMemberList.main(null);

			filesList = testOutputDataDirectory.listFiles();
			File htmlFile = null;
			assertTrue(filesList.length >= 3);
			for(File file : filesList) {
				if (file.getName().endsWith(".html")) {
					htmlFile = file;
				}
			}
			assertNotNull(htmlFile, "Could not find an html file in directory " + testOutputDataDirectory.getName());
			String data = Files.readString(htmlFile.toPath());

			//System.out.println("generated html");
			//System.out.println(data);
			// Validate html file
			List<Integer> tdOpen = TestMemberHTMLCreator.CountSubstrings(data, "<td");
			List<Integer> tdClose = TestMemberHTMLCreator.CountSubstrings(data, "/td");
			List<Integer> trOpen = TestMemberHTMLCreator.CountSubstrings(data, "<tr");
			List<Integer> trClose = TestMemberHTMLCreator.CountSubstrings(data, "/tr");
			List<Integer> styleTag = TestMemberHTMLCreator.CountSubstrings(data, "style");
			List<Integer> imgTag = TestMemberHTMLCreator.CountSubstrings(data, "img");

			// The final data should have 14 html table rows.....1 is the header and 13 are data.
			// There are 21 people, 2 with pictures and 2 with notes
			assertEquals(14, trOpen.size());
			assertEquals(14, trClose.size());
			assertEquals(39, tdOpen.size());
			assertEquals(39, tdClose.size());
			assertEquals(5, styleTag.size());  // there are 3 style tags in header + 2 from notes.csv
			assertEquals(2, imgTag.size());
	}

}
