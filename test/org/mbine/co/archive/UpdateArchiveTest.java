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

import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * 
 * @author Stuart Moodie
 *
 */
public class UpdateArchiveTest {
	private static final String EG1_PREFIX = "example_files/example1_test";
	private static final String EG2_PREFIX = "example_files/example2_test";
	private static final String EG2_ZIP = EG2_PREFIX + "/test_files";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Path srcZipPath = FileSystems.getDefault().getPath(EG1_PREFIX, "example.zip").toAbsolutePath();
			Path zipPath = FileSystems.getDefault().getPath("updated_example.zip").toAbsolutePath();
			Files.copy(srcZipPath, zipPath, StandardCopyOption.REPLACE_EXISTING);
			CombineArchiveFactory fact = new CombineArchiveFactory();
			try (ICombineArchive arch = fact.openArchive(zipPath.toString(), true)) {
				Path readMePath = FileSystems.getDefault().getPath(EG2_ZIP, "anotherFile.txt");
				ArtifactInfo entry = arch.createArtifact(readMePath.toString(), "text/plain");
				OutputStream writer = arch.writeArtifact(entry);
				Files.copy(readMePath, writer);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
