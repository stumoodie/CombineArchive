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


import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.DCTerms;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Stuart Moodie
 *
 */
public class MetadataManager implements IMetadataManager {
	/**
	 * 
	 */
	private static final String W3C_DTF_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SXXX";
	private final Path metaPath; 
	private Model model;
	private final DateFormat format;

	public MetadataManager(Path metaFile) {
		this.metaPath = metaFile;
		format = new SimpleDateFormat(W3C_DTF_FORMAT);
	}

	/* (non-Javadoc)
	 * @see org.mbine.co.archive.IMetadataManager#load()
	 */
	@Override
	public void load(){
		try(InputStream in = Files.newInputStream(metaPath, StandardOpenOption.READ)){
			model = ModelFactory.createDefaultModel();
			model.read(in, null);
		} catch (Exception e) {
			new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.mbine.co.archive.IMetadataManager#updateModifiedTimestamp()
	 */
	@Override
	public void updateModifiedTimestamp(){
//		Resource url = 
//		Selector selector = new SimpleSelector(ResourceFactory.createResource("file:///."), DCTerms.modified, (Object)null);
		StmtIterator iter = model.listStatements(ResourceFactory.createResource("file:///"), DCTerms.modified, (RDFNode)null);
		
//		Resource docRoot = model.getResource("file:///.");
//		Statement stmt = docRoot.getProperty(DCTerms.modified);
		Date modifiedTimestamp = new Date();
		if(iter.hasNext()){
			Statement stmt = iter.next();
			stmt.changeObject(format.format(modifiedTimestamp));
		}
		else{
			Resource docRoot = model.getResource("file:///");
			docRoot.addProperty(DCTerms.modified, format.format(modifiedTimestamp));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mbine.co.archive.IMetadataManager#getRDFModel()
	 */
	@Override
	public Model getRDFModel(){
		return this.model;
	}
	
	/* (non-Javadoc)
	 * @see org.mbine.co.archive.IMetadataManager#save()
	 */
	@Override
	public void save(){
		try(OutputStream of = Files.newOutputStream(metaPath,StandardOpenOption.WRITE)){
			model.write(of);
		} catch (Exception e) {
			new RuntimeException(e);
		}
	}
	
}
