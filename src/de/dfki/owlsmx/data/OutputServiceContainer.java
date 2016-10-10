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
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import de.dfki.owlsmx.exceptions.MatchingException;



/**
 * A containser that computes the potential degree of match for the output concepts
 * 
 * @author bEn
 *
 */
public class OutputServiceContainer {

    Map services = new HashMap();
    
    public OutputServiceContainer(){  
    }
    
    void addService(ExtendedServiceInformation exInfo) {
        Integer ID = new Integer(exInfo.serviceID);
        if (services.containsKey(ID))            
            ((DOM)services.get(ID)).mergeService(exInfo);
        else
            services.put(ID, new DOM(exInfo));
    }
    
    public void addServices(SortedSet set) {        
        Iterator iter = set.iterator();
        while (iter.hasNext()) {
            addService((ExtendedServiceInformation)iter.next());
        }
    }
    
    boolean existService(Integer ServiceID) {
        return services.containsKey(ServiceID);
    }
    
    DOM getService(Integer ServiceID) {
        if (existService(ServiceID))
            return (DOM)services.get(ServiceID);           
        return null;
    }
    
    public void merge(OutputServiceContainer output) throws MatchingException {     
        Iterator iter = services.entrySet().iterator();
        Map.Entry me;
        Integer ID;
        Vector remove = new Vector();
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            ID = (Integer) me.getKey();            
            if (!output.existService(ID))
                remove.add(ID);
            else
                ((DOM) me.getValue()).mergeDOM(output.getService(ID));
        }
        for (int i = 0; i<remove.size();i++)
            services.remove((Integer)remove.get(i));
        
    }
    
    SortedSet getServicesSet() {
        return new TreeSet(services.values());
    }

    public Map getServiceMap() {
        return services;
    }
}
