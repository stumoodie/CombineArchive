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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author Stuart Moodie
 *
 */
public class ManifestManager {
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<omexManifest xmlns=\"http://identifiers.org/combine.specifications/omex-manifest\">";
	private static final String XML_FOOTER = "</omexManifest>\n";
	private final Path maniPath; 
	private final Map<String, String> manifestMap;

	public ManifestManager(Path manifestFile) {
		this.maniPath = manifestFile;
		this.manifestMap = new HashMap<>();
	}

	public void load(){
		try(InputStream in = Files.newInputStream(maniPath, StandardOpenOption.READ)){
			parseFile(in);
		} catch (Exception e) {
			new RuntimeException(e);
		}
	}
	
	public void addEntry(String path, String fileType){
		this.manifestMap.put(path, fileType);
	}
	
	public void removeEntry(String path){
		if(this.manifestMap.remove(path) == null){
			throw new IllegalArgumentException("Path did not exist in the manifest");
		}
	}
	
	public boolean hasEntry(String path){
		return this.manifestMap.containsKey(path);
	}
	
	public String getFileType(String path){
		return this.manifestMap.get(path);
	}
	
	public Iterator<String> filePathIterator(){
		return this.manifestMap.keySet().iterator();
	}
	
	public void save(){
		try(BufferedWriter out = Files.newBufferedWriter(maniPath, StandardCharsets.UTF_8, StandardOpenOption.WRITE)){
			writeFile(out);
		} catch (Exception e) {
			new RuntimeException(e);
		}
	}
	
	private void writeFile(BufferedWriter out) throws IOException {
		out.write(XML_HEADER);
		out.newLine();
		for(String path : this.manifestMap.keySet()){
			out.write("\t<content location=\"");
			out.write(path);
			out.write("\" format=\"");
			out.write(this.manifestMap.get(path));
			out.write("\"/>");
			out.newLine();
		}
		out.write(XML_FOOTER);
	}

	private void parseFile(InputStream r) throws SAXException, IOException, ParserConfigurationException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(r);
		NodeList nodeList = doc.getElementsByTagName("content");
		for(int i = 0; i < nodeList.getLength(); i++){
			Element child = (Element)nodeList.item(i);
			String locn = child.getAttribute("location").trim();
			String type = child.getAttribute("format");
			this.manifestMap.put(locn, type);
		}
	}
	
}
