/*
 * Created on 02.11.2004
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.mindswap.owls.process.Parameter;
import org.mindswap.owls.process.ParameterList;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.io.owl_rdf.Renderer;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;

import de.dfki.owlsmx.data.ServiceConcept;
import de.dfki.owlsmx.data.ServiceEntry;
import de.dfki.owlsmx.exceptions.MatchingException;
import de.dfki.owlsmx.io.ErrorLog;




/**
 * @author bEn
 *
 */
public class MatchmakerUtils  extends SetUtils{
    
    public static String getConcept(String classuri) {
        if (StringUtils.contains(classuri,"#"))
                return classuri.substring(classuri.lastIndexOf("#"));
        return classuri;
    }
    
    public static String getConcept(URI classuri) {
        return getConcept(classuri.toString());
    }
    
    private static String makeLineLocal(String line) {
        String result = line;
        try {
            if (StringUtils.contains(line,"http://") && StringUtils.contains(line,"#")&&(!StringUtils.contains(line,"http://www.w3.org/2001/XMLSchema"))) {                
                result=line.substring(0,line.indexOf("http://")) + line.substring(line.lastIndexOf("#"));
            }
            else if (StringUtils.contains(line,"file:/") && StringUtils.contains(line,"#")&&(!StringUtils.contains(line,"http://www.w3.org/2001/XMLSchema"))) {
                result=line.substring(0,line.indexOf("file:/")) + line.substring(line.lastIndexOf("#"));
            }    
        return result;
        }
        catch (Exception e) { 
            ErrorLog.instanceOf().report("Something went wrong! "+ line + " " +(StringUtils.contains(line,"file:/") && StringUtils.contains(line,"#")) );
            return line;
            }
    }
    
    public static String replace(String inString, String toReplace, String replacement) {
        //System.out.println("Replacing in " + inString + " " + toReplace + " with "+ toReplace);
        if (StringUtils.contains(inString,toReplace))
                inString = inString.substring(0,inString.indexOf(toReplace)) + replacement + inString.substring(inString.indexOf(toReplace)+ toReplace.length()+1);
        //System.out.println("Replace leads to" + inString);
        return inString;
    }
    
    public static String getLocalConceptFromLine(String line, String localOntology) {
        String result = line;
        if ( (StringUtils.contains(line,"http://") || StringUtils.contains(line,"file:/") )&& StringUtils.contains(line,"#") && StringUtils.contains(line,"\"") &&(!StringUtils.contains(line,"http://www.w3.org/2001/XMLSchema"))) {                
            result=localOntology + line.substring(line.lastIndexOf("#"),line.lastIndexOf("\""));
        }
        return result;
    }
 
    public static void makeOntologyLocal(String localOntology, String filename, Set localConcepts) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));        
        Vector vec = new Vector();
        String line = "";
        String localized="";

        while ( (line = reader.readLine()) != null) {
            line = line.trim();
            localized=getLocalConceptFromLine(line,localOntology);
            if (localConcepts.contains(localized)) {
                line = makeLineLocal(line);
            }
            vec.add(line + "\n");
        }
        reader.close();
        
        FileWriter writer = new FileWriter(filename,false);
        for(int i=0;i<vec.size();i++) {
            //System.out.println("REPLACE" + (String)vec.get(i));
            writer.write((String)vec.get(i));
        }
        writer.flush();
        writer.close();
    }
    
    public static void replaceOntologyInFile(String filename, String replacement) throws IOException {
        String toReplace = "";
        BufferedReader reader = new BufferedReader(new FileReader(filename));        
        Vector vec = new Vector();
        String line = "";

        boolean replace=false;
        while ( (line = reader.readLine()) != null) {
            line = line.trim();
            if ( !replace && (StringUtils.contains(line,"file:/")) && StringUtils.contains(line,filename) ) {                
                toReplace = line.substring(line.indexOf("file:/"),line.indexOf(filename)) + filename;
                //System.out.println("REPLACE" + line);
                replace=true;
            }
            if ((replace) && (StringUtils.contains(line,"file:/")) && StringUtils.contains(line,filename) &&(!StringUtils.contains(line,"http://www.w3.org/2001/XMLSchema")) ){               
                line = replace(line,  toReplace,  replacement);
                //System.out.println("REPLACE" + line);
            }
            vec.add(line + "\n");
        }
        reader.close();
        
        FileWriter writer = new FileWriter(filename,false);
        for(int i=0;i<vec.size();i++) {
            //System.out.println("REPLACE" + (String)vec.get(i));
            writer.write((String)vec.get(i));
        }
        writer.flush();
        writer.close();
    
        
    }    
    
    public static String degreeOfMatchIntToString(int dom) {
        switch(dom) {
        case(0):
            return "EXACT";
        case(1):
            return "PLUG-IN";
        case(2):
            return "SUBSUMES";
        case(3):
            return "SUBSUMED-BY";
        case(4):
            return "NEAREST NEIGHBOUR";        
        case(5):
            return "FAIL";
        default:
            return "COULDN'T TRANSLATE " + dom;
        }        
    }
    
    public static void printOWLClassSet(Set set) throws MatchingException{
    	try {
	        Iterator iter = set.iterator();
	        System.out.println("Printing HashSet: ");
	        while ( iter.hasNext() ){
					System.out.println(((OWLClass)iter.next()).getURI().toString());
	        }
        } catch (Exception e) {
        	throw new MatchingException(e.getMessage());
        }          
    }    
    
    public static String printServiceEntrySet(Set set) {
        Iterator iter = set.iterator();
        ServiceEntry entry;
        String result = "";
        while ( iter.hasNext() ){
            entry = (ServiceEntry) iter.next();
            result = result + "(" + entry.ID+"," + entry.degreeOfMatch +"," + entry.similarity +")" + " ";
        }
        return result;
    }
    /*
    public static void printConceptServiceSet(Map map) {
        Iterator iter = map.entrySet().iterator();
        String tmp;
        ServiceConcept concept;
        System.out.println("Printing conceptService: ");
        ServiceConcept services;
        Set InSet,OutSet;
        while ( iter.hasNext() ){
            Map.Entry me = (Map.Entry)iter.next();
            System.out.println("Viewing concept: " + (String)me.getKey());   
            services = (ServiceConcept)me.getValue();
            InSet=services.getInputs();
            OutSet=services.getOutputs();
            System.out.println("Output Services:" + printServiceEntrySet(OutSet));
            System.out.println("Input  Services:" + printServiceEntrySet(InSet));
        }
    }
    */
    private static String getStringFromEntries(Set set) {
        String result="(";
        ServiceEntry entry;
        Iterator iter = set.iterator();
        while ( iter.hasNext() ){
            entry = (ServiceEntry) iter.next();
            result = result + entry.ID + ";" + entry.degreeOfMatch + ";" + entry.similarity + ";;";
        }
        int last = result.lastIndexOf(";;");
        if (last>0)
           result = result.substring(0,last);
        result = result + ")";
        return result;
    }
    
    private static SortedSet  entriesFromString(String parsed) {
        SortedSet result = new TreeSet();
        parsed = parsed.substring(parsed.indexOf("(")+1,parsed.lastIndexOf(")"));    
        String[] tokens = parsed.split(";;");
        String[] entryTokens;
        String token;
        for (int i = 0; i<tokens.length; i++) {            
            token = tokens[i].trim();
            entryTokens = token.split(";");            
            if (entryTokens.length>=3) {
                result.add(new ServiceEntry((new Integer(entryTokens[0].trim())).intValue(), (new Integer(entryTokens[1].trim()).intValue()), (new Double(entryTokens[2].trim())).doubleValue()));
            }                    
        }
        return result;
    }
    
    public static Map loadConceptServiceSet(String path) throws IOException {
        Map result = new HashMap();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        String[] tokens;
        ServiceConcept services;
        SortedSet entries;
        Iterator iter;
        while ( (line = reader.readLine()) != null) {
            line = line.trim();
            line = line.substring(line.indexOf("(")+1,line.lastIndexOf(")"));            
            tokens = line.split(";;;");
            services = new ServiceConcept();
            entries = entriesFromString(tokens[1]);
            iter = entries.iterator();
            while (iter.hasNext()) 
                services.addInput((ServiceEntry)iter.next());
            entries = entriesFromString(tokens[2]);
            iter = entries.iterator();
            while (iter.hasNext()) 
                services.addOutput((ServiceEntry)iter.next());
            result.put(tokens[0],services);
        }
        
        reader.close();
        return result;
    }    
    
    public static boolean saveConceptServiceSet(Map map, String targetpath) {
        try {
        FileWriter writer = new FileWriter(targetpath,false);
        Iterator iter = map.entrySet().iterator();
        ServiceConcept services;
        while ( iter.hasNext() ){
            Map.Entry me = (Map.Entry)iter.next();               
            services = (ServiceConcept)me.getValue();
            writer.write("(" + (String)me.getKey() + ";;;" + getStringFromEntries(services.getInputs()) + ";;;" + getStringFromEntries(services.getOutputs()) + ")\n");
           
        }
        
        writer.close();
        return true;
        }catch(IOException e) { return false;}
    }
    
    public static boolean save(OWLOntology ontology,String path){
		try {
			Renderer render = new Renderer();
			FileWriter writer = new FileWriter(path,false);
			render.renderOntology(ontology, writer);		
			writer.close(); 
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (RendererException e) {
			e.printStackTrace();
			return false;
		}		
		return true;
    }
    
    public static URI getOntologyURI(URI ClassURI) throws MatchingException  {
        String path = ClassURI.toString();
        if (path.startsWith("#") || (!StringUtils.contains(path,"#")))
            throw new MatchingException(ClassURI.toString() + " doesn't contain the OntologyURL");
        try {
            return new URI(path.substring(0,path.indexOf("#")));
        }
        catch(URISyntaxException e) { throw new MatchingException("URI error: Couldn't get ontologyURI of " + ClassURI.toString());}
    }
    
    public static Vector getClassTextFromFile(OWLClass clazz, String filename) throws IOException, MatchingException{
        try {
        String searchterm1 = clazz.getURI().toString();        
        String searchterm2 =  "#" + clazz.getURI().getFragment();
        searchterm1 = searchterm1.substring(0,searchterm1.indexOf(searchterm2));
        if (searchterm1.indexOf(".")>0)
            searchterm1 = searchterm1.substring(0,searchterm1.lastIndexOf("."));        
        //System.out.println("Searching the term: " + searchterm1 + searchterm2);
        
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String lastline = "";
        String line = "";
        boolean current_class=false;
        Vector result = new Vector();
        int class_count=0;
        
        while ( (line = reader.readLine()) != null) {
            line = line.trim();
//            if ( (line.indexOf("\"" + searchterm1)>0) && (line.indexOf(searchterm2+ "\"")>0) && 
//                 (line.startsWith("<owl:Class")) && 
//                 ( (lastline.startsWith("</")) || (lastline.endsWith("/>")) ) ) {               
//                System.out.println("found at least "  + lastline);
//                System.out.println("               "  + line);
//            }
            if ( (line.indexOf("\"" + searchterm1)>0) && (line.indexOf(searchterm2+ "\"")>0) && 
                 (line.startsWith("<owl:Class")) && 
                 ( (lastline.startsWith("</")) || (lastline.endsWith("/>")) ) ) {   
                	current_class=true;
                	line=makeLineLocal(line);
                	
            }
            
            if (current_class && (line.startsWith("<owl:Class"))  ) {
                class_count++;
            }
            
            if (current_class)        {
                result.add(line);
                //System.out.println( current_class + " " + class_count + " " + line.startsWith("<owl:Class ")  + " " + line);
            	}
            if (current_class && ( (line.startsWith("</owl:Class")) ) ) {
                class_count--;
                if (class_count<=0)
                    current_class=false; }
            lastline = line;
        }
        reader.close();
        return result;
        } catch (Exception e) {
        	throw new MatchingException(e.getMessage());
        }    
    }
    
    public static void appendClassVectorToOntologyFile(Vector classinfo, String ontologypath) throws IOException {
       //System.out.println("appendClassVectorToOntologyFile" +ontologypath);
        BufferedReader reader = new BufferedReader(new FileReader(ontologypath));        
        Vector tmp = new Vector();
        String line = "";
        while ( (line = reader.readLine()) != null) {
            line = line.trim();
            //System.out.println(line);
            if(line.startsWith("</rdf:RDF>")) {
                for (int i=0; i<classinfo.size();i++)
                    tmp.add((String)classinfo.get(i) + "\n");
            }
            tmp.add(line + "\n");
        }
        reader.close();
        
        FileWriter writer = new FileWriter(ontologypath,false);
        for (int i=0; i<tmp.size();i++)
            writer.write((String)tmp.get(i));
        
        writer.flush();
        writer.close();
    }
    
    public static Vector getURIList(ParameterList resourcen){
        Iterator iter = resourcen.iterator();
        Vector urilist = new Vector();        
        while (iter.hasNext()) {
            urilist.add(((Parameter) iter.next()).getParamType().getURI());
        }
//    	System.out.println("getURIList" + urilist);
        return urilist;
    }
    
    public  static boolean setContainsService(ServiceEntry se, Set set) {
        Iterator iter = set.iterator();
        ServiceEntry entry;
        while(iter.hasNext()) {
            entry = (ServiceEntry) iter.next();
            if (entry.ID == se.ID)
                return true;
        }
        return false;
    }
    
    public static SortedSet mapToSortedSet(Map map) {
        SortedSet result = new TreeSet();
        Iterator iter = map.entrySet().iterator();
        Map.Entry me;
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            result.add(me.getValue());
        }
        return result;
    }
    
    public Set removeEntryFromSet(ServiceEntry entry1, Set set) {
        ServiceEntry tmpentry;
        TreeSet deleteSet = new TreeSet();
        
        Iterator iter = set.iterator();
        while(iter.hasNext()) {
            tmpentry=(ServiceEntry)iter.next();
            if (entry1.ID==tmpentry.ID)
                deleteSet.add(tmpentry);
        }
        
        set.removeAll(deleteSet);
               
        return set;
    }
    
    public static boolean existsEntryInSet(ServiceEntry entry1, Set set) {
        Iterator iter = set.iterator();
        ServiceEntry tmpentry;
        while(iter.hasNext()) {
            tmpentry=(ServiceEntry)iter.next();
            if (entry1.ID==tmpentry.ID)
                return true;
        }
        return false;
    }
    
    public static void wait(int seconds) {
        System.gc();
        try {
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }              
    }

    public static void wait(double seconds) {
        System.gc();
        try {
            Thread.sleep( (int)(1000 * seconds));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }              
    }
    
}



