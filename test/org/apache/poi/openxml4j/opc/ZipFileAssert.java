/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

/*
 * Portions Copyright 2013, EMBL - European Bioinformatics Institute.
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

package org.apache.poi.openxml4j.opc;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.*;
import junit.framework.AssertionFailedError;

/**
 * Compare the contents of 2 zip files.
 * 
 * @author CDubettier
 * @author Stuart Moodie. Modified to allow the comparison of some files to be skipped.
 * @author Mihai Glonţ 
 */
public class ZipFileAssert {
	private ZipFileAssert() {
	}

	static final int BUFFER_SIZE = 2048;

	protected static boolean equals(
			TreeMap<String, ByteArrayOutputStream> file1,
			TreeMap<String, ByteArrayOutputStream> file2, String[] ignoredFiles) {
		Set<String> ignoredFilesSet = new TreeSet<String>(Arrays.asList(ignoredFiles));
		Set<String> listFile1 = file1.keySet();
		if (listFile1.size() == file2.keySet().size()) {
			for (Iterator<String> iter = listFile1.iterator(); iter.hasNext();) {
				String fileName = iter.next();
				if(!ignoredFilesSet.contains(fileName)){
					// extract the contents for both
					ByteArrayOutputStream contain2 = file2.get(fileName);
					ByteArrayOutputStream contain1 = file1.get(fileName);

					if (contain2 == null) {
						// file not found in archive 2
						fail(fileName + " not found in 2nd zip");
						return false;
					}
					// no need to check for contain1. The key come from it

					if ((fileName.endsWith(".xml")) || fileName.endsWith(".rels")) {
						// we have a xml file
						// TODO
						// YK: the original OpenXML4J version attempted to compare xml using xmlunit (http://xmlunit.sourceforge.net),
						// but POI does not depend on this library
					} else {
						// not xml, may be an image or other binary format
						if (contain2.size() != contain1.size()) {
							// not the same size
							fail(fileName
									+ " does not have the same size in both zip:"
									+ contain2.size() + "!=" + contain1.size());
							return false;
						}
						byte array1[] = contain1.toByteArray();
						byte array2[] = contain2.toByteArray();
						for (int i = 0; i < array1.length; i++) {
							if (array1[i] != array2[i]) {
								fail(fileName + " differ at index:" + i);
								return false;
							}
						}
					}
				}
			}
		} else {
			// not the same number of files -> cannot be equals
			fail("not the same number of files in zip:"
					+ listFile1.size() + "!=" + file2.keySet().size());
			return false;
		}
		return true;
	}

	protected static TreeMap<String, ByteArrayOutputStream> decompress(
			File filename) throws IOException {
		// store the zip content in memory
		// let s assume it is not Go ;-)
		TreeMap<String, ByteArrayOutputStream> zipContent = new TreeMap<String, ByteArrayOutputStream>();

		byte data[] = new byte[BUFFER_SIZE];
		/* Open file to decompress */
		FileInputStream file_decompress = new FileInputStream(filename);

		/* Create a buffer for the decompressed files */
		BufferedInputStream buffi = new BufferedInputStream(file_decompress);

		/* Open the file with the buffer */
		ZipInputStream zis = new ZipInputStream(buffi);

		/* Processing entries of the zip file */
		ZipEntry entree;
		int count;
		while ((entree = zis.getNextEntry()) != null) {

			/* Create a array for the current entry */
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			zipContent.put(entree.getName(), byteArray);

			/* copy in memory */
			while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
				byteArray.write(data, 0, count);
			}
			/* Flush the buffer */
			byteArray.flush();
			byteArray.close();
		}

		zis.close();

		return zipContent;
	}

	/**
	 * Asserts that two files are equal. Throws an <tt>AssertionFailedError</tt>
	 * if they are not.
	 * <p>
	 * 
	 */
	public static void assertEquals(File expected, File actual, String ... ignoredFiles) {
		assertNotNull(expected);
		assertNotNull(actual);

		assertTrue("File does not exist [" + expected.getAbsolutePath()
				+ "]", expected.exists());
		assertTrue("File does not exist [" + actual.getAbsolutePath()
				+ "]", actual.exists());

		assertTrue("Expected file not readable", expected.canRead());
		assertTrue("Actual file not readable", actual.canRead());

		try {
			TreeMap<String, ByteArrayOutputStream> file1 = decompress(expected);
			TreeMap<String, ByteArrayOutputStream> file2 = decompress(actual);
			equals(file1, file2, ignoredFiles);
		} catch (IOException e) {
			throw new AssertionFailedError(e.toString());
		}
	}
}
