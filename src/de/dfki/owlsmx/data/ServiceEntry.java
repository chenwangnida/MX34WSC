/*
 * Created on 26.10.2004
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
import java.net.URISyntaxException;


/**
 * Implementation of a service entry in the Concept Service Registry
 * 
 * @author bEn
 *
 */
public class ServiceEntry implements Comparable{
    boolean debug = false;
    public int ID,degreeOfMatch;
    public double similarity;
    public URI uri;
    public String dominantConcept;
    
    public ServiceEntry() {
        
    }
    
    public ServiceEntry(int id, int degree, double sim, String concept) {
        degreeOfMatch = degree;
        ID = id;
        similarity=sim;
        dominantConcept=concept;
    }
    
    public ServiceEntry(int id, int degree, double sim) {
        degreeOfMatch = degree;
        ID = id;
        similarity=sim;
    }
    
   
    public ServiceEntry(URI uri, int degree, double sim) {
        this.uri=uri;
        this.degreeOfMatch = degree;
        this.similarity=sim;
    }
    
    public ServiceEntry(String uri, int degree, double sim) throws URISyntaxException {
        this.uri=new URI(uri);
        this.degreeOfMatch = degree;
        this.similarity=sim;
    }
    
    public ServiceEntry(int id, URI uris, int degree, double sim) {
        degreeOfMatch = degree;
        ID = id;
        similarity=sim;
        uri=uris;
    } 
    
    public void resetSimilarity() {
        similarity=0;
    }
       
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj2) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        if (this == obj2) {
          return 0;
        } 
        ServiceEntry service2 = (ServiceEntry) obj2;
        if (debug)
            System.out.println("Comparing " + this.ID + "(" + this.degreeOfMatch + "," + this.similarity + ") with " + service2.ID + "(" + service2.degreeOfMatch + "," + service2.similarity + ")");
        if ( (this.degreeOfMatch==service2.degreeOfMatch) && 
             (this.similarity==service2.similarity)           
           ) {
           if (this.ID == service2.ID)              
                    return EQUAL;
           else { 
           		  return BEFORE; } 
           }
        else if ( (this.degreeOfMatch < service2.degreeOfMatch) || 
                  ( (this.degreeOfMatch==service2.degreeOfMatch) && 
                    (this.similarity>service2.similarity) ) ||
                    ( (this.degreeOfMatch==service2.degreeOfMatch) && 
                            (this.similarity==service2.similarity) &&
                            this.ID<service2.ID)
                ) {
                return BEFORE; }
        if (debug)
            System.out.println("AFTER");
        return AFTER;
    }
          
    public boolean equals(Object obj) {
        //if ( this == obj ) return true;
        if ( !(obj instanceof ServiceEntry) ) return false;
        ServiceEntry service2 = (ServiceEntry) obj;
        return ( 
             (this.degreeOfMatch==service2.degreeOfMatch) && 
             (this.similarity==service2.similarity) &&
             (this.ID == service2.ID) 
             );
    }
    
    public boolean smallerThan(Object obj) {
        if ( this == obj ) return false;
        if ( !(obj instanceof ServiceEntry) ) return false;
        ServiceEntry service2 = (ServiceEntry) obj;
        return ( 
             (this.degreeOfMatch<=service2.degreeOfMatch) || 
             ((this.similarity<=service2.similarity) &&
             (this.degreeOfMatch==service2.degreeOfMatch)) 
             );    
    }
    
    public String toString() {
        if (uri==null)
            return "(" + ID + "/" + de.dfki.owlsmx.utils.MatchmakerUtils.degreeOfMatchIntToString(degreeOfMatch) + "/" + similarity + ")";
        return "("+ uri + "/" + ID + "/" + de.dfki.owlsmx.utils.MatchmakerUtils.degreeOfMatchIntToString(degreeOfMatch) + "/" + similarity + ")";
        
    }
    
}
