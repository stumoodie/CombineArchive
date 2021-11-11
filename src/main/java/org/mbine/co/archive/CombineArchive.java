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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.*;

/**
 * 
 * @author Stuart Moodie
 *
 */
public class CombineArchive implements ICombineArchive {
	private static final String METADATA = "metadata.rdf";
	private static final String MANIFEST = "manifest.xml";
	private final FileSystem fs;
	private final IManifestManager manifest;
	private final IMetadataManager metadataManager;
	private boolean contentChanged;
	
	CombineArchive(FileSystem fs, IManifestManager manMan, IMetadataManager metaManager) {
		this.fs = fs;
		this.manifest = manMan;
		this.metadataManager = metaManager;
		this.contentChanged = false;
	}

	@Override
	public void close() {
		try {
			if(contentChanged){
				metadataManager.load();
				metadataManager.updateModifiedTimestamp();
				metadataManager.save();
			}
			fs.close();
			this.contentChanged = false;
		} catch (IOException e) {
			throw new CombineArchiveException(e);
		}
	}
	
	
	@Override
	public boolean isModified(){
		return this.contentChanged;
	}

	@Override
	public File writeMasterFile() throws IOException {
		Map.Entry<String, InputStream> masterFile = getMasterFile();
		Path tmpFile = Files.createTempFile("omex-master-file", "");
		if (masterFile.getKey() != "" && masterFile.getValue() != null) {
			InputStream stream = masterFile.getValue();
			File targetFile = tmpFile.toFile();
			FileUtils.copyInputStreamToFile(stream, targetFile);
			return targetFile;
		}
		return null;
	}

	@Override
	public ArtifactInfo createArtifact(String fileLocation, String fileType, boolean master) {
		if (!canCreateArtifact(fileLocation)) {
			throw new IllegalArgumentException("Invalid file location: " + fileLocation);
		}
		try {
			Path newResPath = getPath(fileLocation);
			this.manifest.load();
			if (newResPath.getParent() != null && !Files.exists(newResPath.getParent())) {
				Files.createDirectories(newResPath.getParent());
			}
			Files.createFile(newResPath);
			Map data = new HashMap<String, String>();
			data.put("format", fileType);
			data.put("master", Boolean.toString(master));
			this.manifest.addEntry(newResPath.toString(), data);
			ArtifactInfo retVal = new ArtifactInfo(fileLocation, fileType, master);
			this.manifest.save();
			this.contentChanged = true;
			return retVal;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void removeArtifact(ArtifactInfo artefactInfo) {
		if (!this.exists(artefactInfo)) {
			throw new IllegalArgumentException("entry must exist: " + artefactInfo.getPath());
		}

		Path entryPath = fs.getPath(artefactInfo.getPath()).toAbsolutePath();
		try {
			this.manifest.load();
			Files.delete(entryPath);
			this.manifest.removeEntry(entryPath.toString());
			this.manifest.save();
			this.contentChanged = true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public InputStream readArtifact(ArtifactInfo artefactInfo) {
		if (!this.exists(artefactInfo)) {
			throw new IllegalArgumentException("entry must exist: " + artefactInfo.getPath());
		}

		Path entryPath = getPath(artefactInfo.getPath()).toAbsolutePath();
		InputStream strm = null;
		try {
			strm = Files.newInputStream(entryPath, StandardOpenOption.READ);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return strm;
	}

	@Override
	public OutputStream writeArtifact(ArtifactInfo artefactInfo) {
		if (!this.exists(artefactInfo)) {
			throw new IllegalArgumentException("entry must exist: " + artefactInfo.getPath());
		}
		
		try {
			Path entryPath = getPath(artefactInfo.getPath()).toAbsolutePath();
			OutputStream strm = null;
			strm = Files.newOutputStream(entryPath, StandardOpenOption.WRITE);
			this.contentChanged = true;
			return strm;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ArtifactInfo getArtifact(String path) {
		ArtifactInfo retVal = null;
		if (this.manifest.hasEntry(path)) {
			retVal = new ArtifactInfo(path, this.manifest.getFileType(path), false);
		}
		return retVal;
	}

	@Override
	public boolean exists(ArtifactInfo artefactInfo) {
		Path rPath = getPath(artefactInfo.getPath());
		return Files.exists(rPath);
	}

	@Override
	public boolean canCreateArtifact(String fileLocation) {
		boolean retVal = true;
		try{
			if(fileLocation != null){
				Path testPath = getPath(fileLocation);
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
	public ArtifactInfo createArtifact(String fileLocation, String fileType, Path srcFile, boolean master) {
		try{
			ArtifactInfo artInfo = this.createArtifact(fileLocation, fileType, master);
			Path zipEntryPath = getPath(artInfo.getPath()).toAbsolutePath();
			Files.copy(srcFile, zipEntryPath, StandardCopyOption.REPLACE_EXISTING);
			this.contentChanged = true;
			return artInfo;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Iterator<ArtifactInfo> artifactIterator() {
		List<ArtifactInfo> retVal = new LinkedList<>();
		Iterator<String> pathIter = this.manifest.filePathIterator();
		while (pathIter.hasNext()){
			String pathStr = pathIter.next();
			Path path = this.fs.getPath(pathStr);
			if (pathStr.equals(".")) {
				continue;
			}
			if (!MANIFEST.equals(pathStr) && !METADATA.equals(pathStr) &&
				Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
				boolean master = this.manifest.isMasterFile(pathStr);
				retVal.add(new ArtifactInfo(pathStr, this.manifest.getFileType(pathStr), master));
			}
		}
		return retVal.iterator();
	}

	@Override
	public Map.Entry<String, InputStream> getMasterFile() {
		Iterator<ArtifactInfo> iterator = artifactIterator();
		boolean foundMasterFile = false;
		Map.Entry<String, InputStream> tmp = new AbstractMap.SimpleEntry<>("", null);
		while (!foundMasterFile && iterator.hasNext()) {
			ArtifactInfo artifactInfo = iterator.next();
			foundMasterFile = artifactInfo.isMaster();
			if (foundMasterFile) {
				String format = artifactInfo.getFormat();
				InputStream stream = readArtifact(artifactInfo);
				tmp = new AbstractMap.SimpleEntry<>(format, stream);
			}
		}
		return tmp;
	}

	@Override
	public boolean hasMasterFile() {
		Iterator<ArtifactInfo> iterator = artifactIterator();
		boolean foundMasterFile = false;
		while (!foundMasterFile && iterator.hasNext()) {
			ArtifactInfo artifactInfo = iterator.next();
			foundMasterFile = artifactInfo.isMaster();
		}
		return foundMasterFile;
	}

	@Override
	public IMetadataManager getMetadata() {
		return this.metadataManager;
	}

	
	private Path getPath(String pathStr){
//		Pattern pat = Pattern.compile("^\\./");
//		Matcher mat = pat.matcher(pathStr);
//		pathStr = mat.replaceFirst("");
//		Path retVal = this.fs.getPath(pathStr).toAbsolutePath();
		Path retVal = this.fs.getPath(pathStr);
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.mbine.co.archive.ICombineArchive#isOpen()
	 */
	@Override
	public boolean isOpen() {
		return this.fs.isOpen();
	}

	/* (non-Javadoc)
	 * @see org.mbine.co.archive.ICombineArchive#numArtifacts()
	 */
	@Override
	public int numArtifacts() {
		return this.manifest.numEntries();
	}
	

}
