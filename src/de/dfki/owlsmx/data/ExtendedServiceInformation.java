/*
 * Created on 06.02.2005
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

import de.dfki.owlsmx.utils.MatchmakerUtils;

/**
 * Extended service information contains additional the similarity and degree
 * of match with witch a service matches the current request
 * Can also store the unfolded concepts
 * 
 * @author bEn
 *
 */
public class ExtendedServiceInformation extends ServiceInformation  implements Comparable {
    public int degreeOfMatch;
    public double similarity = 0;
    public String unfoldedconcept = "";

    /**
     * @param serviceID
     * @param isInput
     * @param conceptID
     * @param noConcepts
     */
    public ExtendedServiceInformation(int serviceID, boolean isInput,
            int conceptID, int noConcepts, int degreeOfMatch, double similarity) {
        super(serviceID, isInput, conceptID, noConcepts);
        this.degreeOfMatch=degreeOfMatch;
        this.similarity = similarity;
        
    }
    
    public void setSimilarity(double sim) {
    	this.similarity = sim;
    }
    
    public void setDegreeOfMatch(int dom){
    	this.degreeOfMatch = dom;
    }
    
    public ExtendedServiceInformation(ServiceInformation info, int degreeOfMatch) {
        super(info.serviceID, info.isInput, info.conceptID, info.noConcepts);
        this.degreeOfMatch=degreeOfMatch;
    }
    
    public ExtendedServiceInformation(ServiceInformation info, int degreeOfMatch, double similarity) {
        super(info.serviceID, info.isInput, info.conceptID, info.noConcepts);
        this.degreeOfMatch=degreeOfMatch;
        this.similarity = similarity;
    }
    
    public ExtendedServiceInformation(ServiceInformation info, int degreeOfMatch,  String unfoldedconcept) {
        super(info.serviceID, info.isInput, info.conceptID, info.noConcepts);
        this.degreeOfMatch=degreeOfMatch;
        this.unfoldedconcept = unfoldedconcept;
    }
    
    public void addUnfoldedInformation(String unfoldedConcept) {
        unfoldedconcept += " " + unfoldedConcept.trim();
        unfoldedconcept = unfoldedconcept.trim();
    }
    
    public boolean containsUnfoldedInformation() {
        return unfoldedconcept != "";
    }

    public int compareTo(Object obj2) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        if (this == obj2) {
          return EQUAL;
        } 
        ExtendedServiceInformation service = (ExtendedServiceInformation) obj2;
        if ( (this.serviceID==service.serviceID) && (this.conceptID==service.conceptID) 
                && (this.noConcepts==service.noConcepts) && (this.isInput==service.isInput)
                && (this.degreeOfMatch==service.degreeOfMatch) && (this.similarity==service.similarity) )
            return EQUAL;
        else if ( (this.serviceID==service.serviceID) && (this.noConcepts==service.noConcepts) 
                && (this.degreeOfMatch==service.degreeOfMatch) && (this.similarity==service.similarity) 
                && (this.isInput==service.isInput) && (this.conceptID<service.conceptID) )
            return BEFORE;
        else if ( (this.degreeOfMatch==service.degreeOfMatch) && (this.similarity==service.similarity) 
                && (this.isInput==service.isInput) && (this.serviceID<service.serviceID) )
            return BEFORE;
        else if ( (this.degreeOfMatch < service.degreeOfMatch) || 
                    ( (this.degreeOfMatch==service.degreeOfMatch) && (this.similarity>service.similarity) ) )
            return BEFORE;
        return AFTER;
    }
    
    public String toString() {
        return "(S-ID (" + serviceID + "), C-ID (" +  conceptID + "), " + noConcepts + ", isInput(" +isInput + "), " + MatchmakerUtils.degreeOfMatchIntToString(degreeOfMatch) + ", sim (" + similarity + ") )";
    }
}
