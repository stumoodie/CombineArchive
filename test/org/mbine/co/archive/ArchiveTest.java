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

/**
 * 
 * @author Stuart Moodie
 *
 */
public class ArchiveTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Path zipPath = FileSystems.getDefault().getPath("tst.zip")
					.toAbsolutePath();
			Files.deleteIfExists(zipPath);
			CombineArchiveFactory fact = new CombineArchiveFactory();
			try (ICombineArchive arch = fact.createArchive(zipPath.toString(), true)) {
				Path readMePath = FileSystems.getDefault().getPath("readme.txt");
				Entry entry = arch.createResource(readMePath.toString(), "txt");
				OutputStream writer = arch.writeResource(entry);
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
