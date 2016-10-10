/*
 * Created on 07.03.2005
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

import java.io.Serializable;

/**
 * Result of the Matching process
 * 
 * @author bEn
 *
 */
public class MatchingResult implements Comparable,Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int serviceID;
    public int degreeOfMatch = DOM.FAIL;
    public double similarity = 0.0;
    public String unfoldedInput ="";
    public String unfoldedOutput ="";
    
    public MatchingResult(ExtendedServiceInformation info, String unfoldedInput, String unfoldedOutput) {
        this.serviceID = info.serviceID; 
        this.degreeOfMatch = info.degreeOfMatch;
        this.unfoldedInput = unfoldedInput;
        this.unfoldedOutput = unfoldedOutput;
    }
    
    MatchingResult(int serviceID, int degreeOfMatch, String unfoldedInput, String unfoldedOutput) {
        this.serviceID = serviceID; 
        this.degreeOfMatch = degreeOfMatch;
        this.unfoldedInput = unfoldedInput;
        this.unfoldedOutput = unfoldedOutput;
    }
    
    MatchingResult(int serviceID, int degreeOfMatch, double similarity, String unfoldedInput, String unfoldedOutput) {
        this.serviceID = serviceID;
        this.degreeOfMatch = degreeOfMatch;
        this.similarity = similarity;
        this.unfoldedInput = unfoldedInput;
        this.unfoldedOutput = unfoldedOutput;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj2) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        if (this == obj2) {
          return EQUAL;
        } 
        MatchingResult service = (MatchingResult) obj2;
        if ( (this.serviceID==service.serviceID) 
                && (this.degreeOfMatch==service.degreeOfMatch) && (this.similarity==service.similarity) )
            return EQUAL;
        else if ( (this.degreeOfMatch < service.degreeOfMatch) || 
                    ( (this.degreeOfMatch==service.degreeOfMatch) && (this.similarity>service.similarity) ) )
            return BEFORE;
        return AFTER;
    }

	/**
	 * @return Returns the degreeOfMatch.
	 */
	public int getDegreeOfMatch() {
		return degreeOfMatch;
	}

	/**
	 * @param degreeOfMatch The degreeOfMatch to set.
	 */
	public void setDegreeOfMatch(int degreeOfMatch) {
		this.degreeOfMatch = degreeOfMatch;
	}

	/**
	 * @return Returns the serviceID.
	 */
	public int getServiceID() {
		return serviceID;
	}

	/**
	 * @param serviceID The serviceID to set.
	 */
	public void setServiceID(int serviceID) {
		this.serviceID = serviceID;
	}

	/**
	 * @return Returns the similarity.
	 */
	public double getSimilarity() {
		return similarity;
	}

	/**
	 * @param similarity The similarity to set.
	 */
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	/**
	 * @return Returns the unfoldedInput.
	 */
	public String getUnfoldedInput() {
		return unfoldedInput;
	}

	/**
	 * @param unfoldedInput The unfoldedInput to set.
	 */
	public void setUnfoldedInput(String unfoldedInput) {
		this.unfoldedInput = unfoldedInput;
	}

	/**
	 * @return Returns the unfoldedOutput.
	 */
	public String getUnfoldedOutput() {
		return unfoldedOutput;
	}

	/**
	 * @param unfoldedOutput The unfoldedOutput to set.
	 */
	public void setUnfoldedOutput(String unfoldedOutput) {
		this.unfoldedOutput = unfoldedOutput;
	}
}
