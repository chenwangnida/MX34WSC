/*
 * Created on 07.02.2005
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
package de.dfki.owlsmx.data;

import java.net.URI;
import java.io.StringWriter;

import de.dfki.owlsmx.utils.MatchmakerUtils;



/**
 * Final representation of a matching service, which will be returned as result
 * 
 * Changes: 29.05.2006 - Patrick Kapahnke MatchedService has now the ability to remember the
 * service as OWLOntology object and to return it as a string.
 * 
 * 
 * @author bEn
 *
 */
public class MatchedService implements Comparable {
    public URI serviceURI;
    public int serviceID;
    public int degreeOfMatch;
    public double similarity;
    private org.mindswap.owl.OWLOntology ontology = null;
    
    MatchedService(int serviceID, URI serviceURI, int degreeOfMatch, double similarity) {
        this.serviceID=serviceID;
        this.serviceURI=serviceURI;
        this.degreeOfMatch=degreeOfMatch;
        this.similarity=similarity;
    }
    
    MatchedService(ExtendedServiceInformation exInfo) {
        this.serviceID=exInfo.serviceID;
        this.degreeOfMatch=exInfo.degreeOfMatch;
        this.similarity=exInfo.similarity;
    }
 
    MatchedService(MatchingResult exInfo) {
        this.serviceID=exInfo.serviceID;
        this.degreeOfMatch=exInfo.degreeOfMatch;
        this.similarity=exInfo.similarity;
    }
    
    public MatchedService(MatchingResult exInfo, URI serviceURI) {
        this.serviceID=exInfo.serviceID;
        this.degreeOfMatch=exInfo.degreeOfMatch;
        this.similarity=exInfo.similarity;
        this.serviceURI = serviceURI;
    }
    
    MatchedService(ExtendedServiceInformation exInfo, URI serviceURI) {
        this.serviceID=exInfo.serviceID;
        this.degreeOfMatch=exInfo.degreeOfMatch;
        this.similarity=exInfo.similarity;
        this.serviceURI=serviceURI;
    }
    
    MatchedService(ServiceEntry entry, URI serviceURI) {
        this.serviceID=entry.ID;
        this.degreeOfMatch=entry.degreeOfMatch;
        this.similarity=entry.similarity;
        this.serviceURI=entry.uri;
    }
    
    
    public void setURI(URI serviceURI) {
        this.serviceURI=serviceURI;
    }
    
    /**
     * Sets the mindswap OWL-S API service representation for this container.
     * 
     * @param ontology service representation
     */
    public void setOntology(org.mindswap.owl.OWLOntology ontology) {
    	this.ontology = ontology;
    }
    
    /**
     * Returns the mindswap OWL-S API service representation.
     * 
     * @return OWLOntology containing the service, that this container stands for
     */
    public org.mindswap.owl.OWLOntology getOntology() {
    	return ontology;
    }
    
    /**
     * Returns the matched service as string, if setOntology() was used to specify the
     * OWLSOntology object.
     * 
     * @return serivce as string, if OWLSOntology was set, else null.
     */
    public String getServiceAsString() {
    	if(ontology == null) return null;
    	
    	StringWriter stringWriter = new StringWriter();
    	ontology.write(stringWriter);
    	return stringWriter.toString();
    }
    
    public URI getServiceURI() {
    	return serviceURI;
    }
    
    public int getDegreeOfMatch() {
    	return degreeOfMatch;
    }
    
    public double getSyntacticSimilarity() {
    	return similarity;
    }
    
    public String toString() {
        return "(S-ID (" + serviceID + "), " +  MatchmakerUtils.degreeOfMatchIntToString(degreeOfMatch) + ", SIM(" + similarity + ", " +serviceURI + " )";
    }
    
    public int compareTo(Object obj2) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        if (this == obj2) {
          return EQUAL;
        } 
        MatchedService service = (MatchedService) obj2;
        if ( (this.serviceID==service.serviceID) && (this.degreeOfMatch==service.degreeOfMatch) && (this.similarity==service.similarity) )
            return EQUAL;
        else if ( (this.degreeOfMatch < service.degreeOfMatch) || 
                    ( (this.degreeOfMatch==service.degreeOfMatch) && (this.similarity>service.similarity) ) )
            return BEFORE;
        return AFTER;
    }
}
