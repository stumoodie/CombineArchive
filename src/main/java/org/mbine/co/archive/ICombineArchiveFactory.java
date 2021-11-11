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

/**
 * 
 * @author Stuart Moodie
 *
 */
public interface ICombineArchiveFactory {
	
	/**
	 * Tests whether an archive can be opened with the given path and creation flag. 
	 * @param path the path to use for the archive.
	 * @param createFlag When set to true a new archive would be created if the files does not exist, otherwise the 
	 * 			archive file must exists. It does not test if the file contains a valid archive. 
	 * @return true is the archive can be opened, false otherwise.
	 */
	boolean canOpenArchive(String path, boolean createFlag);

	/**
	 * Opens an archive with the given path and creation flag. 
	 * @param path the file location of the the archive.
	 * @param createFlag When set to true a new archive will be created if the file does not exist, otherwise the 
	 * 			archive file must exists. 
	 * @return A instance of ICombineArchive.
	 */
	ICombineArchive openArchive(String path, boolean createFlag);
}
