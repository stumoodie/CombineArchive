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
 * @author Stuart Moodie
 *
 */
public interface ICombineArchive extends AutoCloseable {
	
	/**
	 * Also need to add metadata when creating the archive. 
	 */
	void addMetadata();
	
	Object getMetadata();
	
	boolean isValidPath(String fileLocation);
	
	Entry createResource(String fileLocation, String fileType);

	Entry createResource(String fileLocation, String fileType, Path srcFile);

	void removeResource(Entry entry);

	InputStream readResource(Entry resource);

	OutputStream writeResource(Entry resource);

	Entry getEntry(String path);
	
	boolean exists(Entry resource);
	
	Iterator<Entry> entryIterator();
}
