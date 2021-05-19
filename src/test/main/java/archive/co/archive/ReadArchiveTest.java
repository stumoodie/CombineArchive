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

package main.java.archive.co.archive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.mbine.co.archive.ArtifactInfo;
import org.mbine.co.archive.CombineArchiveFactory;
import org.mbine.co.archive.ICombineArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Stuart Moodie
 *
 */
public class ReadArchiveTest {
	private static final String EG1_PREFIX = "example_files/example1_test";
	private static final String EG2_PREFIX = "example_files/example2_test";
	private static final String EG2_ZIP = EG2_PREFIX + "/test_files";
	private static final String TEST_ZIP = "example.zip";
	private static final String[] EXPECTED_UPDATE_FILES = {
		"readme.txt", "test_sheet.ps", "abc/foo/readme.txt", "anotherFile.txt", "manifest.xml", "metadata.rdf"
	};
	private ICombineArchive arch;
	private String[] expectedFiles = {
			"readme.txt", "test_sheet.ps", "abc/foo/readme.txt"
	};
	private Path tmpFile;
	private int fileCount;

	
	@Before
	public void setUp() throws Exception {
		Path zipPath = FileSystems.getDefault().getPath(EG1_PREFIX, TEST_ZIP).toAbsolutePath();
//		Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-r--r--");
//		FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
//		tmpFile = Files.createTempFile("tmp", ".zip", attr);
		tmpFile = Files.createTempFile("tmp", ".zip");
		Files.copy(zipPath, tmpFile, StandardCopyOption.REPLACE_EXISTING);
		CombineArchiveFactory fact = new CombineArchiveFactory();
		arch = fact.openArchive(tmpFile.toString(), false);
	}
	
	
	@After
	public void tearDown() throws Exception {
		if(arch != null && arch.isOpen()){
			arch.close();
		}
		Files.deleteIfExists(tmpFile);
		arch = null;
		tmpFile = null;
	}
	
//	public static void listZipContents(Path zipLocn){
//		try{
//			Map<String, String> env = new HashMap<>();
//			env.put("create", Boolean.toString(false));
//			StringBuilder buf = new StringBuilder(URI_PREFIX);
//			buf.append(zipLocn.toString());
//			URI zipUri = URI.create(buf.toString());
//			FileSystem zipFs = FileSystems.newFileSystem(zipUri, env);
//			Files.walkFileTree(zipFs.getRootDirectories().iterator().next(), new FileVisitor<Path>() {
//
//				@Override
//				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
//					System.out.println("pre visit dir=" + dir.toString());
//					return FileVisitResult.CONTINUE;
//				}
//
//				@Override
//				public FileVisitResult visitFile(Path file,	BasicFileAttributes attrs) throws IOException {
//					System.out.println("Visited path=" + file.toString());
//					return FileVisitResult.CONTINUE;
//				}
//
//				@Override
//				public FileVisitResult visitFileFailed(Path file,
//						IOException exc) throws IOException {
//					System.out.println("Visit fail=" + file.toString());
//					return FileVisitResult.CONTINUE;
//				}
//
//				@Override
//				public FileVisitResult postVisitDirectory(Path dir,
//						IOException exc) throws IOException {
//					System.out.println("Post visit dir=" + dir.toString());
//					return FileVisitResult.CONTINUE;
//				}
//
//			});
//			for(Path stor : zipFs.getRootDirectories()){
//				String fName = stor.toString();
//				System.out.println("File = " + fName);
//				
//			}
//			zipFs.close();
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	@Test
	public void testReadArchive(){
		Set<String> fNames = new TreeSet<String>(Arrays.asList(expectedFiles));
		Iterator<ArtifactInfo> iter = arch.artifactIterator();
		while(iter.hasNext()){
			ArtifactInfo entry = iter.next();
			boolean actualResult = fNames.remove(entry.getPath());
			assertTrue("File not found", actualResult);
		}
		assertTrue("Missing expected file(s)", fNames.isEmpty());
	}
	
	@Test
	public void testUpdateArchive() throws Exception{
		Path readMePath = FileSystems.getDefault().getPath(EG2_ZIP, "anotherFile.txt");
		ArtifactInfo entry = arch.createArtifact(readMePath.getFileName().toString(), "text/plain");
		OutputStream writer = arch.writeArtifact(entry);
		Files.copy(readMePath, writer);
		writer.close();
		arch.close();
		checkContent(tmpFile, EXPECTED_UPDATE_FILES);
	}


	private void checkContent(Path tmpFile2, String[] expectedUpdateFiles) throws Exception {
//		final Set<String> expectedPaths = new TreeSet<String>(Arrays.asList(expectedUpdateFiles)); 
		String zipFilePath = "jar:" + tmpFile2.toUri().toString();
		Map<String, String> env = new HashMap<>();
		env.put("create", Boolean.toString(false));
		FileSystem zipFs = FileSystems.newFileSystem(new URI(zipFilePath), env);
		for(String fileName : expectedUpdateFiles){
			Path testPath = zipFs.getPath(fileName);
			assertTrue("File in archive", Files.exists(testPath));
		}
		fileCount = 0;
		for (Path root : zipFs.getRootDirectories()) {
			Files.walkFileTree(root, new FileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					fileCount++;
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file,
						IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir,
						IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
		}
		assertEquals("All expected num files found", expectedUpdateFiles.length, fileCount);
	}
	
//	public static void main(String[] args) {
//		try {
////			Path zipPath = FileSystems.getDefault().getPath(BORIS_PREFIX, "Boris.omex").toAbsolutePath();
////			Path zipPath = FileSystems.getDefault().getPath("/Users/smoodie/tst.zip").toAbsolutePath();
//				Iterator<ArtifactInfo> iter = arch.artifactIterator();
//				while(iter.hasNext()){
//					ArtifactInfo entry = iter.next();
//					System.out.println(entry.getPath());
////					InputStream reader = arch.readArtifact(entry);
////					BufferedReader r = new BufferedReader(new InputStreamReader(reader));
////					String buf = r.readLine();
////					while(buf != null){
////						System.out.println(buf);
////						buf = r.readLine();
////					}
////					r.close();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
