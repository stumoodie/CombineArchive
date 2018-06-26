/*
 * Copyright 2017 EMBL - European Bioinformatics Institute
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
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * 
 * @author Stuart Moodie
 *
 */

public class CombineArchiveFactory implements ICombineArchiveFactory {
	private static final String JAR_URI_PREFIX = "jar:";
	private static final String MANIFEST_FILE_NAME = "manifest.xml";
	private static final String METADATA_FILE_NAME = "metadata.rdf";
//	private static final String VCARD_NS = "http://www.w3.org/2006/vcard/ns#";
	
	
	@Override
	public ICombineArchive openArchive(String path, boolean createFlag) {
		try{
			Map<String, String> env = new HashMap<>();
			env.put("create", Boolean.toString(createFlag));
			Path zipLocn = Paths.get(path).toAbsolutePath();
			URI zipUri = URI.create(JAR_URI_PREFIX + zipLocn.toUri().toString());
			ICombineArchive retVal = null;
			FileSystem zipFs = FileSystems.newFileSystem(zipUri, env);
			Path maniPath = zipFs.getPath(MANIFEST_FILE_NAME);
			IManifestManager man = new ManifestManager(maniPath);
			if(!Files.exists(maniPath)){
				if(createFlag){
					Files.createFile(maniPath);
					man.save();
				}
			}
			else{
				man.load();
			}
			Path metadataPath = zipFs.getPath(METADATA_FILE_NAME);
			IMetadataManager meta = new MetadataManager(metadataPath);
			if(!Files.exists(metadataPath)){
				if(createFlag){
					createMetadata(metadataPath);
				}
			}
			else{
				meta.load();
			}
			retVal = new CombineArchive(zipFs, man, meta);

			return retVal;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void createMetadata(Path metadataPath) throws IOException{
		Model mdl = ModelFactory.createDefaultModel();
		mdl.setNsPrefix("dcterms", DCTerms.NS);
		
		Resource docRoot = mdl.createResource("file:///");
		Date creationDate = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SXXX");
		docRoot.addProperty(DCTerms.created, format.format(creationDate));
		docRoot.addProperty(DCTerms.creator, "libCombineArchive");
		
		try(OutputStream of = Files.newOutputStream(metadataPath)){
			mdl.write(of);
		}
	}

	@Override
	public boolean canOpenArchive(String path, boolean createFlag) {
		Path zipLocn = Paths.get(path);
		return Files.isRegularFile(zipLocn) && Files.isReadable(zipLocn) && Files.isWritable(zipLocn);
	}

}
