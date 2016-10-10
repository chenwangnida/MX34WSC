/*
 * Created on 15.11.2005
 * OWL-S Matchmaker
 * 
 * COPYRIGHT NOTICE
 * 
 * Copyright (C) 2005 DFKI GmbH, Germany
 * Developed by Benedikt Fries, Matthias Klusch
 * 
 * The code is free for non-commercial use only.
 * You can redistribute it and/or modify it under the terms
 * of the Mozilla Public License version 1.1  as
 * published by the Mozilla Foundation at
 * http://www.mozilla.org/MPL/MPL-1.1.txt
 */

package de.dfki.owlsmx.gui.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.service.Service;

import de.dfki.owlsmx.gui.data.ServiceItem;
import de.dfki.owlsmx.mxp.OWLSMXPServiceInformation;
import de.dfki.wsdlanalyzer.parser.SimpleTypeLookupTable;

public class ServiceLoader {
	OWLKnowledgeBase kb = OWLFactory.createKB();
	
	public class ServiceContent {
		public URI uri;
		public URI fileURI;
		public Set inputs = new HashSet();
		public Set outputs = new HashSet();
		public String name;	
		public boolean hasWSDLGrounding;
		
		public ServiceContent(URI Serviceuri) {
			uri = Serviceuri;
			fileURI = Serviceuri;
			name = Serviceuri.toString();
		}
	}
	
	private static ServiceLoader _instance = new ServiceLoader();
	
	private ServiceLoader() {		
	}
	
	public static ServiceLoader getInstance() {
		return _instance;
	}
	
	private URI getFileURI(URI uri) throws URISyntaxException {
		String tmp = uri.toString();
		if (tmp.indexOf("#")<0)
			return uri;
		tmp = tmp.substring(0,tmp.lastIndexOf("#"));
//		System.out.println("FileURI" + tmp);
		return new URI(tmp);
	}
	
	public ServiceContent loadService(URI uri) {
		ServiceContent content = new ServiceContent(uri);
		try {
			kb.setReasoner("RDFS");
			//Service service = kb.readService(uri);
			OWLOntology onto = kb.read(uri);
			Service service = onto.getService();
			
			if (service==null) {
				GUIState.displayWarning("Loading service", 
						"Could not load service " + uri.toString() + "\n" +
						"Either the URI is wrong or the service is not valid OWL-S 1.1");
				throw new FileNotFoundException();
			}
			content.name = service.getName();
			if( (service.getURI()!=null) && (!service.getURI().toString().equals("")) )
				content.uri =  service.getURI();

			if( (getFileURI(service.getURI())!=null) && (!getFileURI(service.getURI()).toString().equals("")) )
				content.fileURI = getFileURI(service.getURI());
			Iterator iter = service.getProfile().getInputs().iterator();			
			while (iter.hasNext()) {
				content.inputs.add(((Input) iter.next()).getParamType().getURI());
				
			}
			iter = service.getProfile().getOutputs().iterator();
			while (iter.hasNext()) {
				content.outputs.add(((Output) iter.next()).getParamType().getURI());
			}
			
			// test, if a WSDL file could be found and parsed correctly
			try {
				new OWLSMXPServiceInformation(onto, new SimpleTypeLookupTable());
				content.hasWSDLGrounding = true;
			}
			catch(Exception e) {
				content.hasWSDLGrounding = false;
			}
			
			kb.unload(uri);
			
		} catch (Exception e) {	
			if (GUIState.debug)
			JOptionPane.showMessageDialog(null,
				    "Couldn't read service " + uri.toString() + "\n It will be added without any additional information.",
				    this.getClass().toString() + ": " + e.getMessage(),
				    JOptionPane.WARNING_MESSAGE);
			//e.printStackTrace();
		}
		
		return content;
	}
	
	private void saveServiceItemInfo(ServiceContent item) {
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(new File("serviceList.txt"), true));
			String uri = item.fileURI.toString();
			if (uri.contains("/"))
				uri = uri.substring(uri.lastIndexOf("/"));
			String line = uri + " (" + item.inputs.size() + "/" + item.outputs.size() + ") (#InputConcepts/#OutputConcepts)";
//			System.err.println(line);
			output.write(line);
			output.newLine();
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
