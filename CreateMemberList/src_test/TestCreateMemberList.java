import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import loaders.NotesLoader;

class TestCreateMemberList {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testMain_SmallData() throws IOException {
		try {
			NotesLoader.searchCreateNotesFileOutputDirectory = false;
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

			CreateMemberList.inputDirectory = testInputDataDirectory;
			CreateMemberList.outputDirectory = testOutputDataDirectory;
			CreateMemberList.main(null);

			filesList = testOutputDataDirectory.listFiles();
			assertEquals(11, filesList.length);
			File htmlFile = null;
			for(File file : filesList) {
				assertTrue(file.length() > 1000, "Unexpected file length " + file.length());

				if (htmlFile == null && file.getName().endsWith(".html")) {
					htmlFile = file;
				} else {
					assertTrue(file.getName().endsWith(".jpg"));
				}
			}

			assertNotNull(htmlFile, "Could not find html file");
			String data = Files.readString(htmlFile.toPath());

			// Validate html file
			List<Integer> tdOpen = TestMemberHTMLCreator.CountSubstrings(data, "<td");
			List<Integer> tdClose = TestMemberHTMLCreator.CountSubstrings(data, "/td");
			List<Integer> trOpen = TestMemberHTMLCreator.CountSubstrings(data, "<tr");
			List<Integer> trClose = TestMemberHTMLCreator.CountSubstrings(data, "/tr");
			List<Integer> styleTag = TestMemberHTMLCreator.CountSubstrings(data, "style");
			List<Integer> imgTag = TestMemberHTMLCreator.CountSubstrings(data, "img");

			// The final data should have 14 html table rows.....1 is the header and 13 are data.
			// There are 17 people, 10 with pictures and 2 with notes
			assertEquals(14, trOpen.size());
			assertEquals(14, trClose.size());
			assertEquals(39, tdOpen.size());
			assertEquals(39, tdClose.size());
			assertEquals(5, styleTag.size());  // there are 3 style tags in header + 2 from notes.csv
			assertEquals(10, imgTag.size());
		} finally {
			NotesLoader.searchCreateNotesFileOutputDirectory = true;
		}
	}

}
