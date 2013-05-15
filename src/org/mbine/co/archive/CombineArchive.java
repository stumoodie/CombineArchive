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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

/**
 * 
 * @author Stuart Moodie
 *
 */
public class CombineArchive implements ICombineArchive {
	private final FileSystem fs;
	private final ManifestManager manifest;
	private final MetadataManager metadataManager;
	
	CombineArchive(FileSystem fs, ManifestManager manMan, MetadataManager metaManager) {
		this.fs = fs;
		this.manifest = manMan;
		this.metadataManager = metaManager; 
	}

	@Override
	public void close() throws Exception {
		metadataManager.load();
		metadataManager.updateModifiedTimestamp();
		metadataManager.save();
		fs.close();
	}

	@Override
	public Entry createResource(String fileLocation, String fileType) {
		if(!canCreateResource(fileLocation)) throw new IllegalArgumentException("Invalid file location: " + fileLocation);
		try{
			Path newResPath = this.fs.getPath(fileLocation);
			this.manifest.load();
			if(newResPath.getParent() != null && !Files.exists(newResPath.getParent())){
				Files.createDirectories(newResPath.getParent());
			}
			Files.createFile(newResPath);
			this.manifest.addEntry(newResPath.toString(), fileType);
			Entry retVal = new Entry(fileLocation, fileType);
			this.manifest.save();
			return retVal;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void removeResource(Entry entry) {
		if(!this.exists(entry)) throw new IllegalArgumentException("entry must exist: " + entry.getPath());

		Path entryPath = fs.getPath(entry.getPath());
		try {
			this.manifest.load();
			Files.delete(entryPath);
			this.manifest.removeEntry(entryPath.toString());
			this.manifest.save();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public InputStream readResource(Entry entry) {
		if(!this.exists(entry)) throw new IllegalArgumentException("entry must exist: " + entry.getPath());

		Path entryPath = this.fs.getPath(entry.getPath());
		InputStream strm = null;
		try {
			strm = Files.newInputStream(entryPath, StandardOpenOption.READ);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return strm;
	}

	@Override
	public OutputStream writeResource(Entry entry) {
		if(!this.exists(entry)) throw new IllegalArgumentException("entry must exist: " + entry.getPath());
		
		Path entryPath = this.fs.getPath(entry.getPath());
		OutputStream strm = null;
		try {
			strm = Files.newOutputStream(entryPath, StandardOpenOption.WRITE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return strm;
	}

	@Override
	public Entry getEntry(String path) {
		return null;
	}

	@Override
	public boolean exists(Entry entry) {
		Path rPath = this.fs.getPath(entry.getPath());
		return Files.exists(rPath);
	}

	@Override
	public boolean canCreateResource(String fileLocation) {
		boolean retVal = true;
		try{
			if(fileLocation != null){
				Path testPath = this.fs.getPath(fileLocation);
				retVal = !Files.exists(testPath);
			}
			else{
				retVal = false;
			}
		}
		catch(InvalidPathException e){
			retVal = false;
		}
		return retVal;
	}

	@Override
	public Entry createResource(String fileLocation, String fileType, Path srcFile) {
		try{
			Entry entry = this.createResource(fileLocation, fileType);
			Path zipEntryPath = this.fs.getPath(entry.getPath());
			Files.copy(srcFile, zipEntryPath, StandardCopyOption.REPLACE_EXISTING);
			return entry;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Iterator<Entry> entryIterator() {
		final Iterator<String> pathIter = this.manifest.filePathIterator();
		return new Iterator<Entry>(){

			@Override
			public boolean hasNext() {
				return pathIter.hasNext();
			}

			@Override
			public Entry next() {
				String path = pathIter.next();
				return new Entry(path, manifest.getFileType(path));
			}

			@Override
			public void remove() {
				new UnsupportedOperationException("Removal not supported by this iterator.");
			}
			
		};
	}

	@Override
	public MetadataManager getMetadata() {
		return this.metadataManager;
	}

}
