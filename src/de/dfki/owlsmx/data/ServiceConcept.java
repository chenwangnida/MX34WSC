/*
 * Created on 01.11.2004
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
import java.util.TreeSet;

import de.dfki.owlsmx.utils.MatchmakerUtils;



/**
 * A datastructure for the concept service registry which contains a Map 
 * for services that have the given concept as input and one for services
 * that have the given concept as output
 * 
 * @author bEn
 *
 */
public class ServiceConcept {
    private Map outputservices;
    private Map  inputservices;
    
    /**
     * 
     */
    public ServiceConcept() {
        outputservices= new HashMap();
        inputservices = new HashMap();
    }
    
    public void add(boolean input, ServiceEntry service) {
        if (input)
            addInput(service);
        else
            addOutput(service);
    }
    
    public void forceAdd(boolean input, ServiceEntry service) {
        Integer id = new Integer(service.ID);
        if (input)
            inputservices.put(id,service);
        else
            outputservices.put(id,service);
    }
    
    public void addInput(ServiceEntry service){
        Integer id = new Integer(service.ID);
        if ((inputservices.containsKey(id)) && (service.smallerThan(inputservices.get(id))) )
            return;
        else
            inputservices.put(id,service);
    }
    public void addOutput(ServiceEntry service){
        Integer id = new Integer(service.ID);
        if ((outputservices.containsKey(id)) && (service.smallerThan(outputservices.get(id))) )
            return;
        else
            outputservices.put(id,service);
    }
    
    public TreeSet getOutputs() {
        return new TreeSet(MatchmakerUtils.mapToSortedSet(outputservices));
    }

    public TreeSet getInputs() {
        return new TreeSet(MatchmakerUtils.mapToSortedSet(inputservices));
    }
    
    public void removeService(int id) {
        Integer Id = new Integer(id);        
        outputservices.remove(Id);
        inputservices.remove(Id);        
    }
    
    public void resetSimilarity() {
        Iterator iter = outputservices.entrySet().iterator();
        Map.Entry me;
        ServiceEntry entry;
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            entry = (ServiceEntry) me.getValue();
            entry.resetSimilarity();
        }
        
        iter = inputservices.entrySet().iterator();
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            entry = (ServiceEntry) me.getValue();
            entry.resetSimilarity();
        }
    }
}
