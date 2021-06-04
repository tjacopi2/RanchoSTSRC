package loaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;

public class PictureLoader {
	
	protected static final String Default_Picture_Filename = "Face Pictures.zip";

	public static ZipFile loadPictures(File inputDirectory) throws IOException {
		if (!inputDirectory.exists() || !inputDirectory.isDirectory() ) {
			throw new IOException("Input directory " + inputDirectory.getCanonicalPath() + " does not exist");
		}
		
		// Get all the zip files
		File[] inputFiles = inputDirectory.listFiles( new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".zip");
		    }
		} );
		
		if (inputFiles.length == 0) {
			throw new IOException("No \"" + Default_Picture_Filename + "\" file in input directory " + inputDirectory.getCanonicalPath());
		}
		
		// Pick the correct zip file
		File pictureFile = selectZipPictureFile(inputFiles);
		
		return new ZipFile(pictureFile);
	}

	protected static File selectZipPictureFile(File[] zipFiles) throws IOException {
		if (zipFiles.length == 1) {
			return zipFiles[0];
		}

		for (File f : zipFiles) {
			if (f.getName().equalsIgnoreCase(Default_Picture_Filename)) {
				return f;
			}
		}

		String dirName = "<" + zipFiles[0].getCanonicalPath() + " does not exist";
		if (zipFiles[0].getParent() != null) {
			dirName = zipFiles[0].getParentFile().getCanonicalPath();
		}
		String msg = "Could not determine .zip file containing pictures in inputDirectory " + dirName + "\n" + 
				"Picture file should be named \"" + Default_Picture_Filename + "\" or be the only .zip file in the directory.  Zip files in the directory are: ";
		for (File f : zipFiles) {
			msg = msg + f.getCanonicalPath() + "\n";
		}
		throw new IOException(msg);
	}

	public static Map<String, ZipEntry> buildPictureMap(ZipFile zf) {
		Map<String, ZipEntry> picMap = new HashMap<String, ZipEntry>();
		
		Iterator<? extends ZipEntry> zeIter = zf.entries().asIterator();
		while (zeIter.hasNext()) {
			ZipEntry ze = zeIter.next();
			String key = getKey(ze.getName());
			if (key != null) {
				ZipEntry oldEntry = picMap.put(key, ze);
				if (oldEntry != null) {
					System.err.println("Warning, found two entries for key " + key + "  First entry came from " +
						oldEntry.getName() + " and second entry came from " + ze.getName() );
				}
			} else {
				System.out.println("Entry " + ze.getName() + " was skipped because did not start with an ID.");
			}
		}
		
		return picMap;
	}

	protected static String getKey(String name) {
		// The name starts with a number, use that as the key
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			if (Character.isDigit(ch)) {
				sb.append(ch);
			} else {
				break;
			}
		}
		
		if (sb.length() != 0) {
			return sb.toString();
		} else {
		    return null;
		}
	}

	public static void writePictures(File outputDirectory, ZipFile pictureFile, Map<String, ZipEntry> pictureMap) {
		int numPictureFiles = 0;
		for (String key : pictureMap.keySet()) {
			ZipEntry ze = pictureMap.get(key);
			InputStream is = null;
			FileOutputStream fos = null;
			try {
			  is = pictureFile.getInputStream(ze);
			  File outputFile = new File(outputDirectory, key + ".jpg");
			  fos = new FileOutputStream(outputFile);
			  long bytesRead = is.transferTo(fos);
			  if (bytesRead == 0) {
				  System.err.println("Warning, image file " + key + ".jpg was read with zero bytes");
			  }
			  numPictureFiles++;
			} catch (IOException e) {
				  System.err.println("Warning, error writing image file " + key + ".jpg" );
				  e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			if (numPictureFiles % 200 == 0) {
				System.out.println(numPictureFiles + " of " + pictureMap.size() + " image files written");
			}
		}
		System.out.println(numPictureFiles + " image files written");
	}
}
