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
import java.nio.file.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @author Stuart Moodie
 *
 */
public class ManifestTest {
	private static final String ORIG_MANIFEST_FILENAME = "orig_manifest.xml";
	private static final String MANIFEST_FILENAME = "manifest.xml";

	public static void main(String[] args) {
		try {
			FileSystem fs = FileSystems.getDefault();
			Path source = fs.getPath(ORIG_MANIFEST_FILENAME);
			Path tgt = fs.getPath(MANIFEST_FILENAME);
			Files.copy(source, tgt, StandardCopyOption.REPLACE_EXISTING);
			IManifestManager man = new ManifestManager(tgt);
			man.load();
			Iterator<String> iter = man.filePathIterator();
			while(iter.hasNext()){
				String locn = iter.next();
				System.out.println("Location=" + locn + ", type=" + man.getFileType(locn));
			}
			man.addEntry("foobar.xml",
					new HashMap<String, String>() {{ put("format", "xml-sbml"); put("master", "false"); }});
			man.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
