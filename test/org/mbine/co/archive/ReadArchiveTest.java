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
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author Stuart Moodie
 *
 */
public class ReadArchiveTest {
	private static final String URI_PREFIX = "jar:file://";
//	private static final String BORIS_PREFIX = "example_files/boris_test";

	
	public static void listZipContents(Path zipLocn){
		try{
			Map<String, String> env = new HashMap<>();
			env.put("create", Boolean.toString(false));
			StringBuilder buf = new StringBuilder(URI_PREFIX);
			buf.append(zipLocn.toString());
			URI zipUri = URI.create(buf.toString());
			FileSystem zipFs = FileSystems.newFileSystem(zipUri, env);
			Files.walkFileTree(zipFs.getRootDirectories().iterator().next(), new FileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					System.out.println("pre visit dir=" + dir.toString());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file,	BasicFileAttributes attrs) throws IOException {
					System.out.println("Visited path=" + file.toString());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file,
						IOException exc) throws IOException {
					System.out.println("Visit fail=" + file.toString());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir,
						IOException exc) throws IOException {
					System.out.println("Post visit dir=" + dir.toString());
					return FileVisitResult.CONTINUE;
				}

			});
			for(Path stor : zipFs.getRootDirectories()){
				String fName = stor.toString();
				System.out.println("File = " + fName);
				
			}
			zipFs.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
//			Path zipPath = FileSystems.getDefault().getPath(BORIS_PREFIX, "Boris.omex").toAbsolutePath();
//			Path zipPath = FileSystems.getDefault().getPath("/Users/smoodie/tst.zip").toAbsolutePath();
			Path zipPath = FileSystems.getDefault().getPath(args[0]).toAbsolutePath();
			listZipContents(zipPath);
			CombineArchiveFactory fact = new CombineArchiveFactory();
			try (ICombineArchive arch = fact.openArchive(zipPath.toString(), false)) {
				Iterator<ArtifactInfo> iter = arch.artifactIterator();
				while(iter.hasNext()){
					ArtifactInfo entry = iter.next();
					System.out.println(entry.getPath());
//					InputStream reader = arch.readArtifact(entry);
//					BufferedReader r = new BufferedReader(new InputStreamReader(reader));
//					String buf = r.readLine();
//					while(buf != null){
//						System.out.println(buf);
//						buf = r.readLine();
//					}
//					r.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
