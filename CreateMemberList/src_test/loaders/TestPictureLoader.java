package loaders;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestPictureLoader {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testLoadPictures() throws IOException {
		File testDataDirectory = new File("input");
		assertTrue(testDataDirectory.isDirectory(), "Can not find input directory");
		ZipFile zipFile = PictureLoader.loadPictures(testDataDirectory);
		
		assertTrue(zipFile.size() > 100);
		zipFile.close();
	}

	@Test
	void testSelectPictureFile() throws IOException {
		File[] files = { new File(PictureLoader.Default_Picture_Filename) };
		File f = PictureLoader.selectZipPictureFile(files);
		assertSame(files[0], f);
	}
	
	@Test
	void testSelectPictureFile_NotDefaultName() throws IOException {
		File[] files = { new File("Other Face Pictures.zip") };
		File f = PictureLoader.selectZipPictureFile(files);
		assertSame(files[0], f);
	}

	@Test
	void testSelectPictureFile_MultipleFiles() throws IOException {
		File[] files = { new File("dummy.zip"), new File(PictureLoader.Default_Picture_Filename), new File("memberx.zip") };
		File f = PictureLoader.selectZipPictureFile(files);
		assertSame(files[1], f);
	}
	

	@Test
	void testSelectPictureFile_AmbigiousMultipleFiles() throws IOException {
		File[] files = { new File("xys.zip"), new File("memberx.zip") };
		try {
		  PictureLoader.selectZipPictureFile(files);
		  fail("Ambigious files did not deliver an error");
		} catch (IOException e) {
			// expected
		}
	}

	@Test
	void testLoadPictures_NoFile() throws IOException {
		File testDataDirectory = new File("testData/TestEmptyDir");
		assertTrue(testDataDirectory.isDirectory(), "Can not find input directory");
		try {
			PictureLoader.loadPictures(testDataDirectory);
			fail("Should have thrown if cannot load the file");
		} catch (IOException e) {
			// expected
		}
	}
	

	@Test
	void testGetKey() throws IOException {
		assertEquals("12", PictureLoader.getKey("12_Tom.zip"));
		assertEquals("1", PictureLoader.getKey("1-Tom.zip"));
		assertEquals("1", PictureLoader.getKey("1 2-Tom.zip1"));
		assertNull(PictureLoader.getKey("x1-Tom.zip"));
		assertEquals("123", PictureLoader.getKey("123.zip"));
		assertEquals("123", PictureLoader.getKey("123"));
		assertNull(PictureLoader.getKey("abc"));
		assertNull(PictureLoader.getKey(""));
	}
	

	@Test
	void testBuildPictureMap() throws IOException {
		File testDataDirectory = new File("input");
		assertTrue(testDataDirectory.isDirectory(), "Can not find input directory");
		ZipFile zipFile = PictureLoader.loadPictures(testDataDirectory);
		
		assertTrue(zipFile.size() > 100);
		
		Map<String, ZipEntry> map = PictureLoader.buildPictureMap(zipFile);
		assertTrue(map.size() > 100);
		for (String key : map.keySet()) {
			try {
			    Integer.valueOf(key);
			} catch (NumberFormatException e) {
				fail("Key " + key + " is not an integer");
			}
		}
		zipFile.close();
	}
	

	@Test
	void testWritePictures() throws IOException {
		File testDataDirectory = new File("testData/TestWritePictures");
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
	      
		ZipFile zipFile = PictureLoader.loadPictures(testInputDataDirectory);
		assertTrue(zipFile.size() == 4);
		Map<String, ZipEntry> map = PictureLoader.buildPictureMap(zipFile);
		
		PictureLoader.writePictures(testOutputDataDirectory, zipFile, map);
		zipFile.close();
		
		filesList = testOutputDataDirectory.listFiles();
		assertEquals(4, filesList.length);
		for(File file : filesList) {
			assertTrue(file.length() > 3000, "Unexpected file length " + file.length());
			
			assertTrue(file.getName().equalsIgnoreCase("1.jpg") ||
					file.getName().equalsIgnoreCase("4.jpg")||
					file.getName().equalsIgnoreCase("7.jpg")||
					file.getName().equalsIgnoreCase("9.jpg"), "Unexpected ouput file name " + file.getName());
		}
		
		
	}
}
