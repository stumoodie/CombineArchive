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

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * 
 * Defines the interface of the Combine Archive API. The API aims to provide
 * access to the files in the archive and maintain bookkeeping information so that the Manifest
 * and the files contents are consistent.
 * 
 * @author Stuart Moodie
 *
 */
public interface ICombineArchive extends AutoCloseable {
	
	/**
	 * Get the meta-data manager. This provides access to the meda-data associated with this archive
	 * and permits direct manipulation or querying of its associated RDF.
	 * @return the metadata manager, which cannot be null.
	 */
	MetadataManager getMetadata();
	
	/**
	 * Tests if the given file location can be used to create a new resource in the archive. This tests to see if the 
	 * file location does not already exist.
	 * @param fileLocation the location to test.
	 * @return true if the createResource method will succeed, false otherwise.
	 */
	boolean canCreateResource(String fileLocation);
	
	/**
	 * Create a new resource in the archive. It will create an empty resource that can then be populated
	 * using <code>writeResource</code>. 
	 * @param fileLocation the location of the resource in the archive. 
	 * @param fileType the mime type of the resource.
	 * @return an entry describing the newly created resource.
	 * @throws IllegalArgumentException if <code>canCreateResource</code> is false.
	 */
	Entry createResource(String fileLocation, String fileType);

	
	/**
	 * Create a new resource in the archive and then copy the contents of <code>srcFile</code>
	 * into it.
	 * @param fileLocation the location of the resource in the archive. 
	 * @param fileType the mime type of the resource.
	 * @param srcFile the path of the src to be copied from. Cannot be null.
	 * @return an entry describing the newly created resource.
	 * @throws IllegalArgumentException if <code>canCreateResource</code> is false. 
	 */
	Entry createResource(String fileLocation, String fileType, Path srcFile);

	/**
	 * Remove the resource from the archive.
	 * @param entry
	 */
	void removeResource(Entry entry);

	InputStream readResource(Entry resource);

	OutputStream writeResource(Entry resource);

	Entry getEntry(String path);
	
	boolean exists(Entry resource);
	
	Iterator<Entry> entryIterator();
}
