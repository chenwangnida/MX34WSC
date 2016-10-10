/*
 * Created on 01.11.2004
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
package de.dfki.owlsmx;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Comparator;

import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLOntology;

import de.dfki.owlsmx.data.MatchedService;
import de.dfki.owlsmx.data.MatchingResult;
import de.dfki.owlsmx.data.SortingType;
import de.dfki.owlsmx.exceptions.MatchingException;
import de.dfki.owlsmx.io.ErrorLog;
import de.dfki.owlsmx.mxp.OWLSMXP;
import de.dfki.owlsmx.similaritymeasures.SimilarityMeasure;
import de.dfki.owlsmx.data.DOM;


/**
 * The matching engine handles the matchmaker and the registered services
 * 
 * Changes: 24.05.2006 - Patrick Kapahnke - added support for passing services and service requests not 
 * only as URI, but also as stream or string. Request results also contain the service in string format.
 * 
 * @author bEn
 *
 */
public class SimilarityMatchingEngine implements MatchingEngine {

	private Map advertisedServices = new HashMap();
	// maps service ID's to corresponding OWLOntology objects
	private Map serviceOntologies = new HashMap();
	private OWLSMXMatchmaker matcher = null;
	private LinkedList newKeys;
	public static final boolean debug = false;

    
    public String printServiceRegistry() {    	
        String result = "Local Services: \n";
        SortedSet keys = new TreeSet(advertisedServices.keySet());
        Iterator iter = keys.iterator();
        Integer me;
        while (iter.hasNext()) {
            me = (Integer) iter.next();
            //result += " " + me + " - " + ((URI)advertisedServices.get(me)).toString() + "\n";
            result += " " + me + " - " + advertisedServices.get(me).toString();
            if(serviceOntologies.containsKey(me)) result += " (local stored ontology)";
            result += "\n";
        }
        return result;
    }
    
    
    private void init() {     
        newKeys=new LinkedList();
        for (int i = 1;i<6;i++ )
            newKeys.addLast(new Integer(i));
    }


    /**
     * Constructor
     * Initializes Matatchmaker
     */
    public SimilarityMatchingEngine() {
        matcher=new OWLSMXMatchmaker();
        init();
        
    }
  
    
    /**
     * Constructor
     * Initializes Matatchmaker with given SimilarityMeasure
     * 
     * @param sim SimilarityMeasure to be used
     */
    public SimilarityMatchingEngine(SimilarityMeasure sim) {
        matcher=new OWLSMXMatchmaker(sim);
        init();
        
    }
    
    /**
     * Constructor
     * Initializes Matatchmaker with given SimilarityMeasure
     * 
     * @param sim SimilarityMeasure to be used
     */
    public SimilarityMatchingEngine(short sim) throws MatchingException{
        matcher=new OWLSMXMatchmaker(sim);
        init();
        
    }
    
    /**
     * Constructor
     * Initializes Matatchmaker with given SimilarityMeasure
     * 
     * @param sim SimilarityMeasure to be used
     * @param integrative True, iff integrative hybrid matching (OWLS-MX2) should be performed
     */
    public SimilarityMatchingEngine(SimilarityMeasure sim, boolean integrative) {
        matcher=new OWLSMXMatchmaker(sim, integrative);
        init();
        
    }
    
    /**
     * Constructor
     * Initializes Matatchmaker with given SimilarityMeasure
     * 
     * @param sim SimilarityMeasure to be used
     * @param integrative True, iff integrative hybrid matching (OWLS-MX2) should be performed
     */
    public SimilarityMatchingEngine(short sim, boolean integrative) throws MatchingException{
        matcher=new OWLSMXMatchmaker(sim, integrative);
        init();
        
    }
    
   
    /**
     * @return new Integer key for a service
     */
    private Integer getNewKey() {
        Integer integer = (Integer) (newKeys.getFirst());
       // int tmpint = integer.intValue();
        newKeys.removeFirst();        
        newKeys.addLast(new Integer( ((Integer)newKeys.getLast()).intValue()+1));
        return integer;
    }

    /** 
     * @param key removes a key from the list of new keys
     */
    private void removeKey(Integer key) {
        newKeys.addFirst(key);
    }

    /* (non-Javadoc)
     * @see owlsmx.MatchingEngine#addService(java.net.URI)
     */
    public void addService(URI profileURI) {
        if (!advertisedServices.containsKey(profileURI)) {
        	try {
			    Integer ID=getNewKey();
        	    advertisedServices.put(ID,profileURI);
//        	    if (debug)
//        	        System.out.println("Service ID " + ID + " " + profileURI.toString() );
        	    matcher.addService(ID,profileURI);
        	    
        	} catch (Exception e) {
        	    System.out.println("Service failed " + profileURI.toString());
        	    e.printStackTrace();
        	
        	}
        }   
    }
    
    /**
     * 24.05.2006
     * 
     * @see de.dfki.owlsmx.MatchingEngine#addService(java.lang.String, java.net.URI)
     */
    public void addService(String profile, URI baseURI) {
//    	// create input stream and pass it to the addService() method for streams
//    	InputStream profileStream = new ByteArrayInputStream(profile.getBytes());
//    	addService(profileStream, baseURI);
    	
    	// get new service ID
    	Integer ID = getNewKey();
    	
    	// create input stream and read ontology
    	InputStream profileStream = new ByteArrayInputStream(profile.getBytes());
    	org.mindswap.owl.OWLKnowledgeBase kb = OWLFactory.createKB();
    	OWLOntology onto = kb.read(profileStream, baseURI);
    	// kb.unload(onto);
    	
    	// add service to the map of advertised services
    	advertisedServices.put(ID, baseURI);
    	
    	// add service ontology to ontology map
    	serviceOntologies.put(ID, onto);
    	
    	// add service to the matchmaker
    	matcher.addService(ID, profile, baseURI);
    }
    
    /**
     * 24.05.2006
     * 
     * @see de.dfki.owlsmx.MatchingEngine#addService(java.io.InputStream, java.net.URI)
     */
    public void addService(InputStream profileStream, URI baseURI) {
//    	// get new service id
//    	Integer ID = getNewKey();
//    	
//    	// add service to the map of advertised services
//    	advertisedServices.put(ID, baseURI);
//    	
//    	// add service to the matchmaker
//    	matcher.addService(ID, profileStream, baseURI);
    	
    	try {
    		// read the profile from the input stream
	    	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(profileStream));
	    	String profile = "";
	    	String line = bufferedReader.readLine();
	    	while(line != null) {
	    		profile += line + "\n";
	    		line = bufferedReader.readLine();
	    	}
	    	bufferedReader.close();
	    	
	    	// pass profile to the appropriate methot
	    	addService(profile, baseURI);
    	}
    	catch(IOException e) {
    		System.out.println("Service failed " + baseURI.toString());
    		e.printStackTrace();
    	}
    }


    /**
     * Removes the service with the given ID from the advertisements
     * 
     * @param ID
     */
    private void removeService(Integer ID) {
        if (advertisedServices.containsKey(ID)) {
            //Service service = (Service) advertisedServices.get(ID);
            advertisedServices.remove(ID);
            serviceOntologies.remove(ID);
            removeKey(ID);
            matcher.removeService(ID);
        }
    }
    
    /* (non-Javadoc)
     * @see owlsmx.MatchingEngine#removeService(java.net.URI)
     */
    public void removeService(java.net.URI uri) {
    	Map.Entry me;
    	for (Iterator iter = advertisedServices.entrySet().iterator(); iter.hasNext();) {
    		me = (Map.Entry) iter.next();
    		if ( ((URI)me.getValue()).equals(uri)) {
    			removeService((Integer) me.getKey());
    			return;
    		}    			
    	}
    }

    /**
     * Checks if the given service should really be used
     * 
     * @param service				service that is checked
     * @param minimumDegreeOfMatch	minimum degree used for this matching
     * @param treshold				syntactic similarity treshold to be used
     * @return If the given service is not relevant
     */
    private boolean shouldPruneService(MatchedService service,int minimumDegreeOfMatch, double treshold){
        if (  (service.degreeOfMatch>minimumDegreeOfMatch) ) {
        	de.dfki.owlsmx.io.ErrorLog.instanceOf().report("Had to skip (" + service.serviceURI + "/" + service.degreeOfMatch + "/" + service.similarity + ") Degree of service " + service.degreeOfMatch + ">" + minimumDegreeOfMatch );
        	return true;
        }
        if ( (matcher.sim==null) || (matcher.sim.getSimilarityType()==SimilarityMeasure.SIMILARITY_NONE)) {
//        	owlsmx.io.ErrorLog.instanceOf().report("Had to skip (" + service.serviceURI + "/" + service.degreeOfMatch + "/" + service.similarity + ") Degree of service " + service.degreeOfMatch + " is not relevant for semantic matching" );
        	return (service.degreeOfMatch>=OWLSMXMatchmaker.NEAREST_NEIGHBOUR) ;
        }
        if ((service.degreeOfMatch==OWLSMXMatchmaker.NEAREST_NEIGHBOUR) && (service.similarity<treshold)) {
        	de.dfki.owlsmx.io.ErrorLog.instanceOf().report("Had to skip (" + service.serviceURI + "/" + service.degreeOfMatch + "/" + service.similarity + ") Sim of service " + service.similarity + "<" + treshold );
        	return true;
        }
        if ((service.degreeOfMatch==OWLSMXMatchmaker.SUBSUMED_BY) && (service.similarity<treshold)) {
        	de.dfki.owlsmx.io.ErrorLog.instanceOf().report("Had to skip (" + service.serviceURI + "/" + service.degreeOfMatch + "/" + service.similarity + ") Sim of service " + service.similarity + "<" + treshold );
        	return true;
        }
    	return false;    	
    }
    
    /**
     * Matches a service with the given URI against the local advertisements
     * using given minimum degree of match and syntactic similarity treshold.
     * The results are ordered with hybrid sorting (<code>SortingType.HYBRID</code>).
     * 
     * @param profileURI				URI of the service
     * @param minimumDegreeOfMatch		minimum hybrid degree of match
     * @param treshold					syntactic similarity treshold
     * @return							SortedSet of MatchedService items
     * @throws MatchingException		Thrown if something goes wrong
     */
    public SortedSet matchRequest(URI profileURI, int minimumDegreeOfMatch, double treshold) throws MatchingException {
    	double synTr = matcher.getSyntacticTreshold();
    	double tresh = treshold;
    	if ( (matcher.sim==null) ||(matcher.sim.getSimilarityType() == SimilarityMeasure.SIMILARITY_NONE)) {
    		ErrorLog.instanceOf().report("Reset treshold to 0.0 as syntactic matching is used");
    		tresh = 0.0;
    	}
    	matcher.setSyntacticTreshold(tresh);
        SortedSet result = new TreeSet();        
        MatchedService entry;
        SortedSet OWLSMXResult = matchRequest(profileURI);
        //owlsmx.io.ErrorLog.instanceOf().debug("MXResult:\n   " + OWLSMXResult.toString());
        //matcher.print();
        for (Iterator iter = OWLSMXResult.iterator();iter.hasNext();) {
            entry = (MatchedService) iter.next();            
        	//System.err.println(this.getClass().toString() + "(" + entry.degreeOfMatch + "/"  + entry.similarity + ") <= (" + minimumDegreeOfMatch + "/" + treshold + ")");
        	if (!shouldPruneService(entry, minimumDegreeOfMatch, treshold))
        			result.add(entry);
            
        }
    	matcher.setSyntacticTreshold(synTr);
    	
        return result;
    }
    
    /**
     * Matches a service with the given URI against the local advertisements
     * using given minimum degree of match and syntactic similarity treshold.
     * The results are ordered with the specified sorting type. Use the 
     * constants <code>SEMANTIC</code>, <code>SYNTACTIC</code> or
     * <code>HYBRID</code> that are defined in the <code>SortingType</code> class.
     * 
     * @param profileURI				URI of the service
     * @param minimumDegreeOfMatch		minimum hybrid degree of match
     * @param treshold					syntactic similarity treshold
     * @param sortingType				the type of sorting as defined in <code>SortingType</code>
     * @return							SortedSet of MatchedService items
     * @throws MatchingException		Thrown if something goes wrong
     */
    public SortedSet matchRequest(URI profileURI, int minimumDegreeOfMatch, double treshold, int sortingType) throws MatchingException {
        if(sortingType != SortingType.SEMANTIC
                && sortingType != SortingType.SYNTACTIC
                && sortingType != SortingType.HYBRID) throw new MatchingException();
        
        SortedSet matchingResult = matchRequest(profileURI, minimumDegreeOfMatch, treshold);
        // hybrid sorting is the standard sorting type of the OWLSMXMatchmaker
        if(sortingType == SortingType.HYBRID) return matchingResult;
        return sortMatchingResult(matchingResult, sortingType);
    }
    
    /* (non-Javadoc)
     * @see owlsmx.MatchingEngine#matchRequest(java.net.URI)
     */
    public SortedSet matchRequest(URI profileURI) throws MatchingException{
        try {
        	org.mindswap.owl.OWLKnowledgeBase kb = org.mindswap.owl.OWLFactory.createKB();
        	SortedSet Matchingresult = new TreeSet();
            SortedSet result = matcher.matchRequest(profileURI);
            Iterator iter = result.iterator();    
            MatchingResult entry;
            URI uri;
            MatchedService matchedService;
            Integer serviceID;
            while(iter.hasNext()) {
                entry =(MatchingResult)iter.next();
                serviceID = new Integer(entry.serviceID);
                if (advertisedServices.containsKey(serviceID)) {                
                	uri=new URI(((URI)advertisedServices.get(serviceID)).toString());            
                	OWLOntology onto;
                	if(serviceOntologies.containsKey(serviceID))
                		onto = (OWLOntology) serviceOntologies.get(serviceID);
                	else onto = kb.read(uri);
                	matchedService = new MatchedService(entry, uri);
                	matchedService.setOntology(onto);
                   	Matchingresult.add(matchedService);
                   	// if(!serviceOntologies.containsKey(serviceID)) kb.unload(onto);
               	}              
            }

       return Matchingresult;
        }
        catch(Exception e) {
            e.printStackTrace();
            return new TreeSet();
        }
    }

    /**
     * Matches a service given as input stream against the local advertisements
     * using given minimum degree of match and syntactic similarity treshold.
     * The results are ordered with hybrid sorting (<code>SortingType.HYBRID</code>).
     * 
     * @param profileStream				input stream that contains the service
     * @param minimumDegreeOfMatch		minimum hybrid degree of match
     * @param treshold					syntactic similarity treshold
     * @return							SortedSet of MatchedService items
     * @throws MatchingException		Thrown if something goes wrong
     */
    public SortedSet matchRequest(InputStream profileStream, int minimumDegreeOfMatch, double treshold) throws MatchingException {
    	double synTr = matcher.getSyntacticTreshold();
    	double tresh = treshold;
    	if ((matcher.sim == null) || (matcher.sim.getSimilarityType() == SimilarityMeasure.SIMILARITY_NONE)) {
    		ErrorLog.instanceOf().report("Reset treshold to 0.0 as syntactic matching is used");
    		tresh = 0.0;
    	}
    	matcher.setSyntacticTreshold(tresh);
        SortedSet result = new TreeSet();        
        MatchedService entry;
        SortedSet owlsmxResult = matchRequest(profileStream);
        for (Iterator iter = owlsmxResult.iterator();iter.hasNext();) {
            entry = (MatchedService) iter.next();            
        	if (!shouldPruneService(entry, minimumDegreeOfMatch, treshold))
        			result.add(entry);
            
        }
    	matcher.setSyntacticTreshold(synTr);
    	
        return result;
    }

    /**
     * Matches a service given as input stream against the local advertisements
     * using given minimum degree of match and syntactic similarity treshold.
     * The results are ordered with the specified sorting type. Use the 
     * constants <code>SEMANTIC</code>, <code>SYNTACTIC</code> or
     * <code>HYBRID</code> that are defined in the <code>SortingType</code> class.
     * 
     * @param profileStream				input stream that contains the service
     * @param minimumDegreeOfMatch		minimum hybrid degree of match
     * @param treshold					syntactic similarity treshold
     * @param sortingType				the type of sorting as defined in <code>SortingType</code>
     * @return							SortedSet of MatchedService items
     * @throws MatchingException		Thrown if something goes wrong
     */
    public SortedSet matchRequest(InputStream profileStream, int minimumDegreeOfMatch, double treshold, int sortingType) throws MatchingException {
        if(sortingType != SortingType.SEMANTIC
                && sortingType != SortingType.SYNTACTIC
                && sortingType != SortingType.HYBRID) throw new MatchingException();
        
        SortedSet matchingResult = matchRequest(profileStream, minimumDegreeOfMatch, treshold);
        // hybrid sorting is the standard sorting type of the OWLSMXMatchmaker
        if(sortingType == SortingType.HYBRID) return matchingResult;
        return sortMatchingResult(matchingResult, sortingType);        
    }
    
    /**
     * Matches a service given as string against the local advertisements
     * using given minimum degree of match and syntactic similarity treshold.
     * The results are ordered with hybrid sorting (<code>SortingType.HYBRID</code>).
     * 
     * @param profile					string that contains the service
     * @param minimumDegreeOfMatch		minimum hybrid degree of match
     * @param treshold					syntactic similarity treshold
     * @return							SortedSet of MatchedService items
     * @throws MatchingException		Thrown if something goes wrong
     */
    public SortedSet matchRequest(String profile, int minimumDegreeOfMatch, double treshold) throws MatchingException {
    	// construct input stream around string
    	InputStream inputStream = new ByteArrayInputStream(profile.getBytes());
    	// pass stream and additional parameters to the appropriate matchRequest() method
    	return matchRequest(inputStream, minimumDegreeOfMatch, treshold);
    }    
 
    /**
     * Matches a service given as string against the local advertisements
     * using given minimum degree of match and syntactic similarity treshold.
     * The results are ordered with the specified sorting type. Use the 
     * constants <code>SEMANTIC</code>, <code>SYNTACTIC</code> or
     * <code>HYBRID</code> that are defined in the <code>SortingType</code> class.
     * 
     * @param profile					string that contains the service
     * @param minimumDegreeOfMatch		minimum hybrid degree of match
     * @param treshold					syntactic similarity treshold
     * @param sortingType				the type of sorting as defined in <code>SortingType</code>
     * @return							SortedSet of MatchedService items
     * @throws MatchingException		Thrown if something goes wrong
     */
    public SortedSet matchRequest(String profile, int minimumDegreeOfMatch, double treshold, int sortingType) throws MatchingException {
        if(sortingType != SortingType.SEMANTIC
                && sortingType != SortingType.SYNTACTIC
                && sortingType != SortingType.HYBRID) throw new MatchingException();
        
        SortedSet matchingResult = matchRequest(profile, minimumDegreeOfMatch, treshold);
        // hybrid sorting is the standard sorting type of the OWLSMXMatchmaker
        if(sortingType == SortingType.HYBRID) return matchingResult;
        return sortMatchingResult(matchingResult, sortingType);        
    }
    
    /**
     * @see de.dfki.owlsmx.MatchingEngine#matchRequest(java.io.InputStream)
     */
    public SortedSet matchRequest(InputStream profileStream) throws MatchingException {
    	try {
    		org.mindswap.owl.OWLKnowledgeBase kb = org.mindswap.owl.OWLFactory.createKB();
    		SortedSet matchingResult = new TreeSet();
    		SortedSet result = matcher.matchRequest(profileStream);
    		Iterator iter = result.iterator();
    		MatchingResult entry;
    		URI uri;
    		Integer serviceID;
    		MatchedService matchedService;
    		while(iter.hasNext()) {
    			entry = (MatchingResult) iter.next();
    			serviceID = new Integer(entry.serviceID);
    			if(advertisedServices.containsKey(serviceID)) {
    				uri = new URI(((URI) advertisedServices.get(serviceID)).toString());
    				OWLOntology onto;
    				if(serviceOntologies.containsKey(serviceID))
    					onto = (OWLOntology) serviceOntologies.get(serviceID);
    				else onto = kb.read(uri);
    				matchedService = new MatchedService(entry, uri);
    				matchedService.setOntology(onto);
    				matchingResult.add(matchedService);
    				// if(!serviceOntologies.containsKey(serviceID)) kb.unload(onto);
    			}
    		}
    		return matchingResult;
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		return new TreeSet();
    	}
    }
    
    /**
     * @see de.dfki.owlsmx.MatchingEngine#matchRequest(java.lang.String)
     */
    public SortedSet matchRequest(String profile) throws MatchingException {
    	// construct input stream around string
    	InputStream inputStream = new ByteArrayInputStream(profile.getBytes());
    	// pass input stream to the appropriate matchRequest() method
    	return matchRequest(inputStream);
    }
    
    /**
     * This method sorts a matching result (consisting of MatchedService objects) according to
     * a given type of sorting as defined in the <code>SortingType</code> class.
     * 
     * @param matchingResult the result of a matching request
     * @param sortingType the type of sorting as defined in <code>SortingType</code>
     * @return the sorted matching result or <code>null</code>, if no valid sorting type was specified
     */
    public static SortedSet sortMatchingResult(SortedSet matchingResult, int sortingType) {
        SortedSet result;
        
        // initialize the sorted set according to a sorting type
        switch(sortingType) {
        case SortingType.SEMANTIC:
            result = new TreeSet(new Comparator() {
                
                public int compare(Object o1, Object o2) {
                    int domS1 = ((MatchedService) o1).getDegreeOfMatch();
                    int domS2 = ((MatchedService) o2).getDegreeOfMatch();
                    
//                    // equal degrees of match
//                    if(domS1 == domS2) return 0;
//                    // matched service 1 has a higher degree of match as matched service 2
//                    if(domS1 < domS2) return -1;
//                    // matched service 1 has a lower degree of match as matched service 2
//                    return 1;
                    
                    if(domS1 < domS2) return -1;
                    return 1;
                }
            });
            break;
        
        case SortingType.SYNTACTIC:
            result = new TreeSet(new Comparator() {
                
                public int compare(Object o1, Object o2) {
                    double simS1 = ((MatchedService) o1).getSyntacticSimilarity();
                    double simS2 = ((MatchedService) o2).getSyntacticSimilarity();
                    
//                    // equal syntactic similarity
//                    if(simS1 == simS2) return 0;
//                    // matched service 1 has a higher syntactic similarity as matched service 2
//                    if(simS1 > simS2) return -1;
//                    // matched service 1 has a lower syntactic similarity as matched service 2
//                    return 1;
                    
                    if(simS1 > simS2) return -1;
                    return 1;
                }
            });
            break;
        
        case SortingType.HYBRID:
            result = new TreeSet(new Comparator() {
               
                public int compare(Object o1, Object o2) {
                    int domS1		= ((MatchedService) o1).getDegreeOfMatch();
                    int domS2		= ((MatchedService) o2).getDegreeOfMatch();
                    double simS1	= ((MatchedService) o1).getSyntacticSimilarity();
                    double simS2	= ((MatchedService) o2).getSyntacticSimilarity();
                    
                    // degree of match of service 1 is higher than degree of match of service 2
                    if(domS1 < domS2) return -1;
                    // degree of match of service 1 is lower than degree of match of service 2
                    if(domS1 > domS2) return 1;
                    
//                    // degree of match of service 1 equals degree of match of service 2, so use the
//                    // syntactic similarity for comparison
//                    if(simS1 > simS2) return -1;
//                    if(simS1 < simS2) return 1;
//                    
//                    // all values are equal
//                    return 0;
                    
                    if(simS1 > simS2) return -1;
                    return 1;
                }
            });
            break; 
            
    	default:
    	    // no valid sorting type as method parameter
            return null;
        }
        
        // add all matching result to the new container with appropriate comparator
        result.addAll(matchingResult);
        
        return result;
    }
    
    /**
     * This method applies the OWLS-MXP test to a matching result and returns
     * all services, that are execution compatible on the WSDL datatype level.
     * It preserves the sorting of the original matching result by passing it's
     * comparater to the new result.
     * 
     * @param matchingResult a SortedSet of MatchedService items
     * @param request the request service as OWLOntology
     * @return all services of the matching result that are execution compatible, preserving the original sorting
     */
    public static SortedSet useOWLSMXPFilter(SortedSet matchingResult, OWLOntology request) {    	
    	SortedSet newResult = new TreeSet(matchingResult.comparator());
    	Iterator resultIterator = matchingResult.iterator();
    	while(resultIterator.hasNext()) {
    		MatchedService candidate = (MatchedService) resultIterator.next();
    		try {
    			if(OWLSMXP.matchTypes(request, candidate.getOntology())) {
    				newResult.add(candidate);
    			}
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	return newResult;
    }
    

    /* (non-Javadoc)
     * @see owlsmx.MatchingEngine#save()
     */
    public boolean save() {   
        boolean result = save("");
        return result;
    }
    
    /**
     * Saves the matching engine to the given path
     * 
     * @param targetpath	Path where to save the engine
     * @return	if saving was successful
     */
    public boolean save(String targetpath) {
        try {
        	// create new directory "state"
        	File dirToCreate = new File(targetpath + "state");
        	if(!dirToCreate.exists()) dirToCreate.mkdir();
        	// directory could not be created, because there is a file with the same name
        	if(!dirToCreate.isDirectory()) return false;
        	
        	// delete old "Service_x.owls" files
        	File[] filesInDir = dirToCreate.listFiles();
        	String filename;
        	for(int i = 0; i < filesInDir.length; i++) {
        		filename = filesInDir[i].getName();
        		if(filename.startsWith("Service_") && filename.endsWith(".owls")) filesInDir[i].delete();
        	}
        	
        	// store the advertisedServices map
            FileWriter writer = new FileWriter(targetpath + "state\\Services.map",false);
            Iterator iter = advertisedServices.entrySet().iterator();
            Map.Entry me;
            while (iter.hasNext()) {
                me = (Map.Entry) iter.next();
                writer.write("(" + me.getKey() + ";" + me.getValue() + ")\n");
                
                // save stored ontologies as well
                if(serviceOntologies.containsKey(me.getKey())) {
                	FileWriter ontologyWriter = new FileWriter(targetpath + "state\\Service_" + me.getKey() + ".owls", false);
                	((OWLOntology) serviceOntologies.get(me.getKey())).write(ontologyWriter);
                	ontologyWriter.close();
                }
                
            }
            writer.close();
            return matcher.save();
        } 
        catch (Exception e) { 
        	e.printStackTrace();
        	return false; 
        }
    }

    /* (non-Javadoc)
     * @see owlsmx.MatchingEngine#load()
     */
    public boolean load() {
        boolean result =  load("");
        //this.matcher.load();
        return result;
    }
    
    /**
     * Loades the matching engine from the given path
     * 
     * @param path			Path from where to load the engine
     * @return				if loading was successful
     */
    private boolean load(String path) { 
    	try {
            clear();
            // read the "Services.map" and store data in the advertisedServices map
            BufferedReader reader = new BufferedReader(new FileReader(path + "state\\Services.map" ));
            String line;
            String[] tokens;
            
				while ( (line = reader.readLine()) != null) {
				    line = line.trim();
				    line = line.substring(line.indexOf("(")+1,line.lastIndexOf(")"));
				    tokens = line.split(";");
				    if (tokens.length>=2)
				        advertisedServices.put(new Integer(tokens[0]),new URI(tokens[1]) );
				}			
            reader.close();
            
            // read all owls files and store them in serviceOntologies map
            File[] filesInDir = new File(path + "state").listFiles();
            String filename;
            Integer ID;
            OWLOntology ontology;
            org.mindswap.owl.OWLKnowledgeBase kb = OWLFactory.createKB();
            for(int i = 0; i < filesInDir.length; i++) {
            	filename = filesInDir[i].getName();
            	// skip files that don't start with "Service_" or not end with ".owls"
            	if(!filename.startsWith("Service_") || !filename.endsWith(".owls")) continue;
            	ID = new Integer(filename.substring(8, filename.indexOf(".")));
            	ontology = kb.read(new FileInputStream(filesInDir[i]), (URI) advertisedServices.get(ID));
            	serviceOntologies.put(ID, ontology);
            	// kb.unload(ontology);            	
            }
            
            return matcher.load();
        } 
    	catch (NumberFormatException e) {				
			e.printStackTrace();
			return false;
		}
    	catch (IOException e) {				
			e.printStackTrace();
			return false;
		}
    	catch (URISyntaxException e) {				
			e.printStackTrace();
			return false;
		}        
    }

    
    /**
     * Sets the SimilarityMeasure to the stated measure
     * 
     * @param sim	SimilarityMeasure to be used
     */
    public void setSimilarityMeasure(SimilarityMeasure sim) {
        matcher.setSimilarityMeasure(sim);   
    }
    
    /**
     * Sets the SimilarityMeasure to the stated measure
     * 
     * @param sim	SimilarityMeasure to be used
     */
    public void setSimilarityMeasure(short sim) {
        matcher.setSimilarityMeasure(sim);   
    }
    
    
    /**
     * Chooses, if integrative or compensative hybrid matching should be used.
     * 
     * @param integrative True, if integrative hybrid matching should be used.
     */
    public void setIntegrative(boolean integrative) {
    	matcher.setIntegrative(integrative);
    }


    /* (non-Javadoc)
     * @see owlsmx.MatchingEngine#clear()
     */
    public void clear() {
    	advertisedServices.clear();
    	serviceOntologies.clear();
        matcher.clear();        
    }
    
    public void print() {
    	System.out.println(this.getClass().toString());
    	this.printServiceRegistry();
    	matcher.print();
    }
    
    /**
     * Enables support for profile hierarchies
     * 
     * @param speedyButProblematic If the entire semantic check should be disabled (hence speedy but could cause problems) 
     */
    public void enableProfileHierarchies(boolean speedyButProblematic) {
    	matcher.enableProfileHierarchies(speedyButProblematic);
    }
    
    /**
     * Disables the support for profile hierarchies
     */
    public void disableProfileHierarchies() {
    	matcher.disableProfileHierarchies();
    }
    
    public String toString() {    	
    	return printServiceRegistry() + "\n" + matcher.toString();
    }
    
    public int getNumberOfRegisteredServices() {
        return advertisedServices.size();
    }
    
    /**
     * Matches a service with the given URI against the local advertisements
     * using solely IR based approximate matching. The result is a service ranking
     * containing all results with a syntactic similarity value greater than the
     * previously defined threshold. Since no logic based filters are applied, all
     * results are marked as nearest neighbor.
     * 
     * @param profileURI				URI of the service
     * @return							SortedSet of MatchedService items
     * @throws MatchingException		Thrown if something goes wrong or no syntactic similarity measure was given previously
     */
    public SortedSet irBasedMatchRequest(URI profileURI, double threshold) throws MatchingException {
    	try {
	    	if(matcher.sim == null || matcher.sim.getSimilarityType() == SimilarityMeasure.SIMILARITY_NONE)
	    		throw new MatchingException("IR based approximate matching can only be applied if a similarity measure is given!");
	    	
	    	double oldThreshold = matcher.getSyntacticTreshold();
	    	matcher.setSyntacticTreshold(threshold);
	    	
	        SortedSet result = new TreeSet();        
	        MatchedService entry;
	        SortedSet OWLSMXResult = matcher.syntacticFilter(profileURI);
	        for (Iterator iter = OWLSMXResult.iterator();iter.hasNext();) {
	            entry = (MatchedService) iter.next();            
	        	//System.err.println(this.getClass().toString() + "(" + entry.degreeOfMatch + "/"  + entry.similarity + ") <= (" + minimumDegreeOfMatch + "/" + treshold + ")");
	        	if (!shouldPruneService(entry, DOM.NEAREST_NEIGHBOUR, threshold))
	        			result.add(entry);
	            
	        }
	    	matcher.setSyntacticTreshold(oldThreshold);
	    	
	        return result;
    	}
    	catch(Exception e) {
    		throw new MatchingException("Could not match " + profileURI + "!", e);
    	}
    }
    
    /**
     * @see de.dfki.owlsmx.MatchingEngine#matchRequest(java.io.InputStream)
     */
    public SortedSet irBasedMatchRequest(InputStream profileStream, double threshold) throws MatchingException {
    	try {
	    	if(matcher.sim == null || matcher.sim.getSimilarityType() == SimilarityMeasure.SIMILARITY_NONE)
	    		throw new MatchingException("IR based approximate matching can only be applied if a similarity measure is given!");
	    		    	
	    	double oldThreshold = matcher.getSyntacticTreshold();
	    	matcher.setSyntacticTreshold(threshold);
	    	
    		org.mindswap.owl.OWLKnowledgeBase kb = org.mindswap.owl.OWLFactory.createKB();
    		SortedSet matchingResult = new TreeSet();
    		SortedSet result = matcher.syntacticFilter(profileStream);
    		Iterator iter = result.iterator();
    		MatchingResult entry;
    		URI uri;
    		Integer serviceID;
    		MatchedService matchedService;
    		while(iter.hasNext()) {
    			entry = (MatchingResult) iter.next();
	       		serviceID = new Integer(entry.serviceID);
	       		if(advertisedServices.containsKey(serviceID)) {
	       			uri = new URI(((URI) advertisedServices.get(serviceID)).toString());
	       			OWLOntology onto;
	       			if(serviceOntologies.containsKey(serviceID))
	       				onto = (OWLOntology) serviceOntologies.get(serviceID);
	       			else onto = kb.read(uri);
	       			matchedService = new MatchedService(entry, uri);
	       			matchedService.setOntology(onto);
	       			matchingResult.add(matchedService);
	       			// if(!serviceOntologies.containsKey(serviceID)) kb.unload(onto);
	       		}
    		}
    		
	    	matcher.setSyntacticTreshold(oldThreshold);
	    	
    		return matchingResult;
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		return new TreeSet();
    	}
    }
}
