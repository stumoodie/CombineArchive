/*
 * Copyright 2013 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of
 * the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.mbine.co.archive;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Stuart Moodie
 *
 */
public class ZipDemo {
	private static String EXAMPLE_PATH="example_files/example1_test/example_zip"; 
	private static final String filesToZip[] = {
			"readme.txt", "anotherFile.txt", "test_sheet.ps"
	}; 
	
	public static void main(String[] args) throws IOException {
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
        final URI TEST_PATH = Paths.get(System.getProperty("user.home"), "tst.zip").toUri();
		Files.deleteIfExists(Paths.get(TEST_PATH));
        final String TEST_URI = new StringBuilder("jar:").append(TEST_PATH).toString();
		URI uri = URI.create(TEST_URI);
		try(FileSystem fs = FileSystems.newFileSystem(uri, env)){
				Path file1 = Paths.get(EXAMPLE_PATH, filesToZip[0]);
				Path zipfile1 = fs.getPath(filesToZip[0]);
				Path dirs = fs.getPath(EXAMPLE_PATH);
				Files.createDirectories(dirs);
				Files.createDirectories(fs.getPath("foo", "bar"));
				Files.copy(file1, zipfile1);
				Path file2 = Paths.get(EXAMPLE_PATH, filesToZip[2]);
				Path dirFile = fs.getPath(dirs.toString(), filesToZip[2]);
				Files.copy(file2, dirFile);				
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Done");
	}

}
