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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * 
 * @author Stuart Moodie
 *
 */
public class JenaTest {
	private static final String personURI = "http://somewhere/JohnSmith";
	private static final String givenName = "John";
	private static final String familyName = "Smith";
	private static final String fullName = givenName + " " + familyName;
	private static final String XPathURI = "file:///mainmdl.xml?/PharmML";
	private static final String VCARD_NS = "http://www.w3.org/2006/vcard/ns#";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Model mdl = ModelFactory.createDefaultModel();
		mdl.setNsPrefix("dcterms", DCTerms.NS);
		mdl.setNsPrefix("vcard", VCARD_NS);
		Property formattedNameProp = ResourceFactory.createProperty(VCARD_NS, "FormattedName");
		Property nameProp = ResourceFactory.createProperty(VCARD_NS, "Name");
		Property givenNameProp = ResourceFactory.createProperty(VCARD_NS, "givenName");
		Property familyNameProp = ResourceFactory.createProperty(VCARD_NS, "familyName");
//		Property additionalNameProp = ResourceFactory.createProperty(VCARD_NS, "additionalName");
		Property emailProp = ResourceFactory.createProperty(VCARD_NS, "Email");
		
		Resource johnSmith = mdl.createResource(personURI);
		johnSmith.addProperty(formattedNameProp, fullName);
		Resource names = mdl.createResource();
		johnSmith.addProperty(nameProp, names);
		names.addProperty(givenNameProp, givenName);
		names.addProperty(familyNameProp, familyName);
		Resource emailAddr = mdl.createResource("mailto:John@somewhere.com");
		johnSmith.addProperty(emailProp, emailAddr);
		
		Resource docRoot = mdl.createResource(XPathURI);
//		TimeZone tz = new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "GMT");
		Date creationDate = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SXXX");
//		format.setTimeZone(tz);
		Resource dateProp = mdl.createResource();
		docRoot.addProperty(DCTerms.created, dateProp);
		dateProp.addProperty(DCTerms.date, mdl.createResource().addProperty(DCTerms.created, format.format(creationDate)));
		docRoot.addProperty(DCTerms.creator, names);
		mdl.write(System.out);
//		try(InputStream in = new FileInputStream("rdftest.xml")){
//			mdl.read(in, null);
//			StmtIterator iter = mdl.listStatements();
//			while(iter.hasNext()){
//				 Statement stmt      = iter.nextStatement();  // get next statement
//				 Resource  subject   = stmt.getSubject();     // get the subject
//				 Property  predicate = stmt.getPredicate();   // get the predicate
//				 RDFNode   object    = stmt.getObject();      // get the object
//
//				 System.out.print(subject.toString());
//				 System.out.print(" " + predicate.toString() + " ");
//				 if (object instanceof Resource) {
//					 System.out.print(object.toString());
//				 } else {
//					 // object is a literal
//					 System.out.print(" \"" + object.toString() + "\"");
//				 }
//
//				 System.out.println(" .");
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

}
