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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;


/**
 * A container that is used to compute the degree for the service input concepts
 * 
 * @author bEn
 *
 */
public class InputServiceContainer {
    Map services = new HashMap();
    
    private void addService(Integer ID, ExtendedServiceInformation exInfo) {
        SortedSet set = (SortedSet) services.get(ID);
        Vector remove = new Vector();
        ExtendedServiceInformation existingInfo;
        Iterator iter = set.iterator();
        boolean add = true;
        while (iter.hasNext()) {
            existingInfo = (ExtendedServiceInformation) iter.next();
            if  (existingInfo.conceptID==exInfo.conceptID) {                
                if (existingInfo.degreeOfMatch>exInfo.degreeOfMatch)
                    remove.add(existingInfo);
                else
                    add = false;
            }                
        }
        set.removeAll(remove);
        if (add)
            set.add(exInfo);
        services.put(ID,set);
    }
    
    public void addServices(SortedSet servicesInformations) {
        ExtendedServiceInformation exInfo;
        Iterator iter = servicesInformations.iterator();
        Integer ID;
        while (iter.hasNext()) {
            exInfo = (ExtendedServiceInformation) iter.next();
            //System.out.println(exInfo.toString());
            ID = new Integer(exInfo.serviceID);
            if (services.containsKey(ID))
                addService(ID,exInfo);
            else {
                SortedSet set = new TreeSet();
                set.add(exInfo);
                services.put(ID,set);
            }                
        }            
    }
     
    private boolean isRelevant(SortedSet extendedServiceInformations) {
        ExtendedServiceInformation exInfo;
        Iterator iter = extendedServiceInformations.iterator();
        Set concepts = new HashSet();
        int size=0;
        while (iter.hasNext()) {
            exInfo = (ExtendedServiceInformation) iter.next();
            size=exInfo.noConcepts;
            concepts.add(new Integer(exInfo.conceptID));
        }
        if (concepts.size()==size)
            return true;
        else if (concepts.size()>size){
            System.out.println("ERROR: more concepts relevant than should exist");
            return true;
        }  
        return false;
    }
    
    private ExtendedServiceInformation getBestDegree(SortedSet extendedServiceInformations) {
        Map potentialResult = new HashMap();
        
        ExtendedServiceInformation exInfo;
        Integer conceptID;
        ExtendedServiceInformation mainInfo;
        Iterator iter = extendedServiceInformations.iterator();
        while (iter.hasNext()) {
            exInfo = (ExtendedServiceInformation) iter.next();
            conceptID=new Integer(exInfo.conceptID);
            if (!potentialResult.containsKey(conceptID))
                potentialResult.put(conceptID,exInfo);
            mainInfo = (ExtendedServiceInformation) potentialResult.get(conceptID);
            if (mainInfo.degreeOfMatch>exInfo.degreeOfMatch)
                potentialResult.put(conceptID,exInfo);
        }
        
        Map.Entry me;
        iter = potentialResult.entrySet().iterator();
        mainInfo = new ExtendedServiceInformation(0, true,0, 3, DOM.EXACT, 1.0);
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            exInfo = (ExtendedServiceInformation) me.getValue();
            mainInfo.addUnfoldedInformation(exInfo.unfoldedconcept);
            if (mainInfo.degreeOfMatch<=exInfo.degreeOfMatch)
                mainInfo = exInfo;
        }
        return mainInfo;
    }
    
    public Map getServices() {
        Map result = new HashMap();
        Map.Entry me;
        SortedSet set;
        ExtendedServiceInformation exInfo;
        Iterator iter = services.entrySet().iterator();
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            set = (TreeSet) me.getValue();
            if (isRelevant(set)) {
                exInfo=getBestDegree(set);
                result.put(new Integer(exInfo.serviceID),exInfo);
            }
        }
        return result;
    }
    
}
