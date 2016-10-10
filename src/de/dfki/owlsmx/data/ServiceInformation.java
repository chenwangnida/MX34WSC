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

import java.io.Serializable;

/**
 * Data structure for basic service infomrmation
 * 
 * Changes: 30.05.2006 - Patrick Kapahnke ServiceInformation is now Serializable to prevent error while saving
 * the current state of the matchmaker
 * 
 * @author bEn
 *
 */
public class ServiceInformation implements Comparable, Serializable {

	private static final long serialVersionUID = -100L;
	
    public int serviceID, conceptID, noConcepts;
    boolean isInput;
    
    public ServiceInformation() {
    }
    
    public ServiceInformation( int serviceID, boolean isInput,  int conceptID, int noConcepts) {
        this.serviceID=serviceID;
        this.conceptID=conceptID;
        this.noConcepts=noConcepts;
        this.isInput=isInput;
    }

    public int compareTo(Object obj2) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        if (this == obj2) {
          return EQUAL;
        } 
        ServiceInformation service = (ServiceInformation) obj2;
        if ( (this.serviceID==service.serviceID) && (this.conceptID==service.conceptID) 
                && (this.noConcepts==service.noConcepts) && (this.isInput==service.isInput))
            return EQUAL;
        else if ( (this.serviceID==service.serviceID) && (this.noConcepts==service.noConcepts) 
                && (this.isInput==service.isInput) && (this.conceptID<service.conceptID) )
            return BEFORE;
        return AFTER;
    }
    
    public String toString() {
        return "(S-ID (" + serviceID + "), C-ID (" +  conceptID + "), " + noConcepts + ", isInput(" +isInput + ") )";
    }
    
}
