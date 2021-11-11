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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * Defines the interface of the Combine Archive API. The API aims to provide
 * access to the files in the archive and maintain bookeeping information so that the Manifest
 * and the files contents are consistent.
 * 
 * @author Stuart Moodie
 *
 */
public interface ICombineArchive extends AutoCloseable {

	/**
	 * Get the master file if it is declared in the archive
	 *
	 * @return A pair of a string as the format and an InputStream object as the content of the master file
	 */
	Map.Entry<String, InputStream> getMasterFile();

	/**
	 * Check the archive has a master or not
	 *
	 * @return true if the archive has a file declared as the master. Returns false for otherwise.
	 */
	default boolean hasMasterFile() {
		return false;
	}

	/**
	 * Get the meta-data manager. This provides access to the meta-data associated with this archive
	 * and permits direct manipulation or querying of its associated RDF.
	 * @return the metadata manager, which cannot be null.
	 */
	IMetadataManager getMetadata();
	
	/**
	 * Tests if the given file location can be used to create a new artifact in the archive. This tests to see if the 
	 * file location does not already exist.
	 * @param fileLocation the location to test.
	 * @return true if the createArtifact method will succeed, false otherwise.
	 */
	boolean canCreateArtifact(String fileLocation);
	
	/**
	 * Create a new artifact in the archive. It will create an empty artifact that can then be populated
	 * using <code>writeArtifact</code>. 
	 * @param fileLocation the location of the artifact in the archive. 
	 * @param fileType the mime type of the artifact.
	 * @param master indicating whether the artifact is master or not.
	 * @return an entry describing the newly created artifact.
	 * @throws IllegalArgumentException if <code>canCreateArtifact</code> is false.
	 * @throws CombineArchiveException if there is an IO error.
	 */
	ArtifactInfo createArtifact(String fileLocation, String fileType, boolean master);

	
	/**
	 * Create a new artifact in the archive and then copy the contents of <code>srcFile</code>
	 * into it.
	 * @param fileLocation the location of the artifact in the archive. 
	 * @param fileType the mime type of the artifact.
	 * @param srcFile the path of the src to be copied from. Cannot be null.
	 * @param master the boolean flag telling the file is master or not.
	 * @return an entry describing the newly created artifact.
	 * @throws IllegalArgumentException if <code>canCreateArtifact</code> is false. 
	 * @throws CombineArchiveException if there is an IO error.
	 */
	ArtifactInfo createArtifact(String fileLocation, String fileType, Path srcFile, boolean master);

	/**
	 * Remove the artifact from the archive.
	 * @param artifactInfo the artifact to remove
	 * @throws IllegalArgumentException if the artifactInfo does not exist in the archive. 
	 * @throws CombineArchiveException if there is an IO error.
	 */
	void removeArtifact(ArtifactInfo artifactInfo);

	/**
	 * Opens an input stream to allow the contents of the artifact to be read.
	 * @param artifactInfo the artifact to be read, which must exist.
	 * @return the input stream for the artifact.
	 * @throws IllegalArgumentException if the artifact does not exist.
	 * @throws CombineArchiveException is there are any IO exceptions while manipulating the archive. 
	 */
	InputStream readArtifact(ArtifactInfo artifactInfo);

	/**
	 * Opens an output stream that overwrites the contents of the artifact.
	 * @param artifactInfo the artifact to be written to.
	 * @return the output stream pointing to the specified artifact.
	 * @throws CombineArchiveException if there is an IO error.
	 */
	OutputStream writeArtifact(ArtifactInfo artifactInfo);

	/**
	 * Get the artifact corresponding to the path of the artifact in the archive.
	 * @param path the location of the artifact in the archive.
	 * @return the artifact info about the identified artifact or null if no artifact can be found at the location. 
	 * @throws CombineArchiveException if there is an IO error.
	 */
	ArtifactInfo getArtifact(String path);
	
	/**
	 * Tests if the artifact exists within the archive.
	 * @param artifactInfo the information identifying the artifact.
	 * @return true if artifact corresponding to the artifactIfo exists in the archive, false otherwise.  
	 */
	boolean exists(ArtifactInfo artifactInfo);
	
	/**
	 * An iterator iterating over the artifacts in the archive and providing an ArtifactInfo for each one. Note that
	 * the manifest and metadata files are not excluded from this iterator. 
	 * @return The iterator, which can be empty, but NOT null.
	 */
	Iterator<ArtifactInfo> artifactIterator();
	
	/**
	 * Closes the archive.
	 * @throws CombineArchiveException if there is an IO error.
	 */
	void close();

	/**
	 * Test if the archive is open.
	 * @return true if the archive is open, false otherwise.
	 */
	boolean isOpen();

	/**
	 * The number of artifacts in this archive.
	 * @return The number of artifacts in the archive.
	 */
	int numArtifacts();

	/**
	 * Tests is the archive has been modified by any of the given operations;
	 * @return true if it has been modified, false otherwise.
	 */
	boolean isModified();

	/**
	 * Write the master file to a file
	 *
	 * @throws IOException if there are issues when accessing the master file and writing out the file
	 * @return a File object which is the result of writing the content of the master file into a file
	 */
	File writeMasterFile() throws IOException;
}
