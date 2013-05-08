package org.mbine.co.archive;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class ZipDemo {
	private static final String filesToZip[] = {
			"readme.txt", "anotherFile.txt", "test_sheet.ps"
	}; 
	
	public static void main(String[] args) {
		Map<String, String> env = new HashMap<>();
		env.put("create", "false");
		URI uri = URI.create("jar:file:///Users/smoodie/tst.zip");
		try(FileSystem fs = FileSystems.newFileSystem(uri, env)){
			for(String fName : filesToZip){
				Path extnlTxtFile = Paths.get(fName);
				Path pathInZipfile = fs.getPath(fName);
				Files.copy(extnlTxtFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
			}
			Files.delete(fs.getPath(filesToZip[2]));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Done");
	}

}
