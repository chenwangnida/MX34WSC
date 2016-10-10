/*
 * Created on 11.01.2005
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
package de.dfki.owlsmx.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;

import de.dfki.owlsmx.exceptions.MatchingException;


/**
 * @author bEn
 *
 */
public class OntologyUtils {

    /**
     * 
     */
    public OntologyUtils() {
    }
    
    public static URI getOntologyURI(URI ClassURI) throws MatchingException {
        String path = ClassURI.toString();
        if (path.startsWith("#") || (!StringUtils.contains(path,"#")))
            throw new MatchingException(ClassURI.toString() + " doesn't contain the OntologyURL");
        try {
            return new URI(path.substring(0,path.indexOf("#")));
        }
        catch(URISyntaxException e) { throw new MatchingException("URI error: Couldn't get ontologyURI of " + ClassURI.toString());}
    }
    
    public static boolean classInSet(OWLClass clazz, Set classes) throws MatchingException {
    	try {
        String token = clazz.getURI().toString();
        if (!StringUtils.contains(token,("#")))
            return false;
        token= token.substring(token.lastIndexOf("#"));
        Iterator iter = classes.iterator();
        OWLClass current_clazz;
        while(iter.hasNext()) {
            current_clazz = (OWLClass) iter.next();
            //System.out.println(token + "is in " + current_clazz.getURI().toString() + " " + (current_clazz.getURI().toString().indexOf(token)>=0) );
            if (current_clazz.getURI().toString().indexOf(token)>=0) {                
                return true;
            }
        }
    	} catch(Exception e) {
    		throw new MatchingException(e.getMessage());
    	}
        return false;
    }
    
    public static Set classesNotInOntology(Set classes, OWLOntology ontology) throws MatchingException {
        //System.out.println("Testing  Ontology" + ontology.toString());
        Set result = new HashSet();
    	try {
        Set ontology_classes = ontology.getClasses();
        Iterator iter = classes.iterator();
        OWLClass clazz;
        while(iter.hasNext()) {
            clazz = (OWLClass) iter.next();
            if (classInSet(clazz,ontology_classes))
                result.add(clazz);                   
        }       
	} catch(Exception e) {
		throw new MatchingException(e.getMessage());
	}
        return result;
    }

    public static String getLocalConcept( String localURI, String absoluteURI) throws MatchingException {
        if ( containsGlobalOntologyURI(absoluteURI) ) {
            String uri = absoluteURI;
            return localURI + uri.substring(uri.lastIndexOf("#"));
        }
        throw new MatchingException("Couldn't retrieve local URI of " + absoluteURI);
    }
    
    public static Vector getLocalConcepts( String localURI, Vector absoluteURIs) throws MatchingException {
        Vector result = new Vector();
        for (int i=0; i<absoluteURIs.size();i++)
            result.add( getLocalConcept( localURI, (String) absoluteURIs.get(i)) );
        return result;
    }
    
    public static boolean containsGlobalOntologyURI(String line) {
        String testline = line.toLowerCase();
        if ( ( (testline.indexOf("http://")>=0) || (testline.indexOf("file:/")>=0)) &&
                (testline.indexOf("#")>=0) && (!(testline.indexOf("xmlns:")>=0)) && (!(testline.indexOf("xmlschema#")>=0)) && (!(testline.indexOf("http://www.w3.org/2001/xmlschema")>=0)) && (!(testline.indexOf("entity")>=0)) )
            return true;                
        return false;
    }
    
    public static String removeURI(String line) {        
        if ( (!containsGlobalOntologyURI(line)) )
            return line;
        int pos = 0;
        if ((line.toLowerCase().indexOf("http://")>=0))
            pos = line.toLowerCase().indexOf("http://");
        else if (line.toLowerCase().indexOf("file:/")>=0)
            pos = line.toLowerCase().indexOf("file:/");
        else 
            return line;
        String result = line.substring(0,pos) + line.substring(line.indexOf("#"));

        return result;
    }
    
    public static void makeOntologyLocal(String localURI, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));        
        Vector vec = new Vector();
        String line = "";
        String localized="";

        while ( (line = reader.readLine()) != null) {
            line = line.trim();
            localized=removeURI(line);
            vec.add(localized + "\n");
        }
        reader.close();
        
        FileWriter writer = new FileWriter(filename,false);
        for(int i=0;i<vec.size();i++) {
            writer.write((String)vec.get(i));
        }
        writer.flush();
        writer.close();
    }

}
