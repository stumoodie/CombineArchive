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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author Stuart Moodie
 *
 */
public class ReadOnlyArchiveTest {
//	private static final String BORIS_PREFIX = "example_files/boris_test/Boris.omex";
	private static final String BORIS_PREFIX = "example_files/biomd1000/BIOMD0000001000.omex";
	private Path tmpFile;
	private Path zipPath;

	
	@Before
	public void setUp() throws Exception {
		zipPath = FileSystems.getDefault().getPath(BORIS_PREFIX).toAbsolutePath();
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString("r--r--r--");
		FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
//		tmpFile = Files.createTempFile("tmp", ".zip", attr);
		tmpFile = Files.createTempFile("tmp", ".zip");
		Files.copy(zipPath, tmpFile, StandardCopyOption.REPLACE_EXISTING);
	}
	
	@After
	public void tearDown() throws Exception {
		Files.deleteIfExists(tmpFile);
		tmpFile = null;
		zipPath = null;
	}

	@Test
	public void testArchiveUnmodified(){
		CombineArchiveFactory fact = new CombineArchiveFactory();
		ICombineArchive arch = fact.openArchive(zipPath.toString(), false);
		arch.close();
		String expectedChecksum = calcMd5Checksum(zipPath);
		String actualChecksum = calcMd5Checksum(tmpFile);
		assertEquals("Files unchanged: md5 checksum the same", expectedChecksum, actualChecksum);
	}

	private String calcMd5Checksum(Path file){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			InputStream is = Files.newInputStream(file);
			byte[] buf = new byte[1000];
			int l;
			while((l = is.read(buf)) != -1){
				if(l > 0){
					md.update(buf, 0, l);
				}
			}
			is.close();
			byte[] checksum = md.digest();
			StringBuilder retVal = new StringBuilder();
			for(byte b : checksum){
				retVal.append(Integer.toHexString((b & 0xff) | 0x100).substring(1,  3));
			}
			return retVal.toString();
		} catch (NoSuchAlgorithmException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
