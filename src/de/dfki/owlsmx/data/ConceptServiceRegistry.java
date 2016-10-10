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
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


/**
 * The ConceptServiceRegistry stores for concepts the services which have this concept as
 * inputs or outputs. Hence it is an attatchment to the local ontology but only contains
 * links to concepts that are actually used by services.
 * Singleton as there should only be one local ontology and hence only one attatchment.
 * 
 * @author bEn
 *
 */
public class ConceptServiceRegistry implements Serializable {

	private static final long serialVersionUID = -2157801882458901868L;
	Map outputs = new HashMap();
    Map inputs = new HashMap();
    static private ConceptServiceRegistry registry = new ConceptServiceRegistry();
    
    /**
     * Constructor (empty)
     */
    private ConceptServiceRegistry() {
    }
    
    /**
     * @return only instance of the ConceptServiceRegistry
     */
    public static ConceptServiceRegistry instanceOf() {
        return registry;
    }
    
    /**
     * @param isInput
     * @param concept
     * @param ServiceID
     * @param conceptID
     * @param noConcepts
     */
    public void updateOutputMap( boolean isInput, String concept, int ServiceID, int conceptID, int noConcepts) {
        if (outputs.containsKey(concept))
            ((HashSet)outputs.get(concept)).add(new ServiceInformation(ServiceID, isInput, conceptID,noConcepts));
        else {
            Set concepts = new HashSet();
            concepts.add(new ServiceInformation(ServiceID, isInput, conceptID, noConcepts));
            outputs.put(concept,concepts);
        } 
    }
    
    /**
     * @param isInput
     * @param concept
     * @param ServiceID
     * @param conceptID
     * @param noConcepts
     */
    public void updateInputMap( boolean isInput, String concept, int ServiceID, int conceptID, int noConcepts) {
        if (inputs.containsKey(concept))
            ((HashSet)inputs.get(concept)).add(new ServiceInformation(ServiceID, isInput, conceptID,noConcepts));
        else {
            Set concepts = new HashSet();
            concepts.add(new ServiceInformation(ServiceID, isInput, conceptID, noConcepts));
            inputs.put(concept,concepts);
        } 
    }
    
    /**
     * @param concept
     * @param isInput
     * @param ServiceID
     * @param conceptNumber
     * @param totalNumberOfConcepts
     * @return	if adding the service was successful
     */
    public boolean addService(String concept, boolean isInput, int ServiceID, int conceptNumber, int totalNumberOfConcepts) {
        if (isInput) 
            updateInputMap(isInput, concept, ServiceID, conceptNumber, totalNumberOfConcepts);
        else
            updateOutputMap(isInput, concept, ServiceID, conceptNumber, totalNumberOfConcepts);
        return true;
    }
    
    /**
     * @param isInput
     * @param ServiceID
     * @param concepts
     */
    public void addConcepts(boolean isInput, int ServiceID, Set concepts) {
        Iterator iter = concepts.iterator();
        String current_concept="";
        int conceptNumber=0;
        while (iter.hasNext()) {            
            current_concept = (String) iter.next();
            addService(current_concept, isInput, ServiceID, conceptNumber, concepts.size());
            conceptNumber++;
        }
    }
    
    /**
     * @param isInput
     * @param ServiceID
     * @param conceptURIs
     */
    public void addConcepts(boolean isInput, int ServiceID, Vector conceptURIs) {
        String current_concept="";
        for (int conceptNumber=0; conceptNumber<conceptURIs.size();conceptNumber++) {
            current_concept = ((URI) conceptURIs.get(conceptNumber)).toString();
            addService(current_concept, isInput, ServiceID, conceptNumber, conceptURIs.size());
        }
    }
    
    /**
     * Returns the services which have a given concept as output
     * 
     * @param concept	URI (String) of a given concept
     * @return	Set of serivice information
     */
    private Set getOutputs(String concept) {
        if (outputs.containsKey(concept))
            return (HashSet)outputs.get(concept);        
        return new HashSet();
    }   
    
    /**
     * Returns the services which have a given concept as input
     * 
     * @param concept	URI (String) of a given concept
     * @return	Set of serivice information
     */
    private Set getInputs(String concept) {
        if (inputs.containsKey(concept))
        	return (HashSet)inputs.get(concept); 
        return new HashSet();        
    }
    
    /**
     * Returns the services for a given concept
     * 
     * @param isInput	If service should be returned which have the concepts as input 
     * @param concept	URI (String) of a concept
     * @return	Set of ServiceInformation
     */
    public Set getServices(boolean isInput, String concept) {
        if (isInput) {
            if (inputs.containsKey(concept))
                return (HashSet)inputs.get(concept);
        }            
        else {
            if (outputs.containsKey(concept))
                return (HashSet)outputs.get(concept);
        }
        return new HashSet();
    }
    
    /**
     * Returns the services for a set of concepts
     * 
     * @param isInput	If service should be returned which have one of the concepts as input 
     * @param concepts	A set with concept URIs as Strings
     * @return	Set of ServiceInformation
     */
    public Set getServices(boolean isInput, Set concepts) {
        Set result =  new HashSet();
        Iterator iter = concepts.iterator();
        while (iter.hasNext()) {
            if (isInput)
                result.addAll(getInputs((String)iter.next()));
            else
                result.addAll(getOutputs((String)iter.next()));
        }        
        return result;
    }
    
    /**
     * Returns all concepts which are used by services
     * 
     * @return	Set of concepts(String)
     */
    public Set getConcepts() {
        Set result = new HashSet();
        result.addAll(outputs.keySet());
        result.addAll(inputs.keySet());
        return result;
    }
    
    public String toString() {
        String result = "ConceptServiceRegistry \n";
        Set concepts = new HashSet();
        
        result += "Outputs (" + outputs.size() + " Concepts):\n";
        Map.Entry me; 
        Iterator iter = outputs.entrySet().iterator();
        Iterator iter2;
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            concepts = (HashSet)me.getValue();
            result += "- " + (String)me.getKey() + "\n";
            iter2 = concepts.iterator();            
            while (iter2.hasNext()) {
                result += "    "+ ((ServiceInformation)iter2.next()).toString() + " (" + concepts.size() + " Items)" + "\n";
            }
        }     
        result += "\n";
        result += "Inputs (" + inputs.size() + " Concepts):\n";
        iter = inputs.entrySet().iterator();
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            concepts = (HashSet)me.getValue();
            result += "- " + (String)me.getKey() + " (" + concepts.size() + " Items)" + "\n";
            iter2 = concepts.iterator();
            
            while (iter2.hasNext()) {
                result += "    "+((ServiceInformation)iter2.next()).toString() + "\n";
            }
        }
        
        return result;   
    }
        
    /**
     * Removes a service from a map with services (works for both, input and output maps)
     * 
     * @param ServiceID	Service to be removed
     * @param map	Map from which the service should be removed from
     */
    private void removeServiceFromMap(int ServiceID, Map map) {
        Set concepts = new HashSet();
        Vector toRemove = new Vector();
        
        Map.Entry me; 
        Iterator iter = map.entrySet().iterator();
        Iterator iter2;
        ServiceInformation service;
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            concepts = (HashSet)me.getValue();
            iter2 = concepts.iterator();            
            while (iter2.hasNext()) {
                service=(ServiceInformation)iter2.next();
                if (service.serviceID == ServiceID)
                    toRemove.add(service);
            }
            for (int i=0; i<toRemove.size();i++) {
                ((HashSet)me.getValue()).remove((ServiceInformation) toRemove.get(i));
            }
        } 
    }
   
    /**
     * Removes a service from the registry
     * 
     * @param ServiceID int of service which should be removed
     */
    public void removeService(int ServiceID) {
        removeServiceFromMap(ServiceID, inputs);
        removeServiceFromMap(ServiceID, outputs);        
    }
    
    /**
     * Returns all Services which have no input concepts
     * (by taking the services with output concepts and 
     * removing them from the list of services with input concepts)
     * 
     * @return Map ID => ExtendedServiceInformation
     */
    public Map getAllServicesWithoutInput() {
        Map in = getAllServices(true);
        Map out = getAllServices(false);
        Map.Entry me;
        Integer ID;
        Iterator iter = in.entrySet().iterator();
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            ID = (Integer)me.getKey();
            if (out.containsKey(ID))
                out.remove(ID);
        }
        return out;
    }
    
    /**
     * Returns all services (which are either registered with their input or output concepts)
     * Default degree of match of EXACT and syntactic similarity 1.0 is added.
     * 
     * @param isInput	If services with input concepts should be returned
     * @return	Map of ExtendedServiceInformation
     */
    public Map getAllServices(boolean isInput) {
        Map current;
        Map result = new HashMap();
        if (isInput)
            current = inputs;
        else
            current = outputs;
        
        Map.Entry me;
        HashSet set;
        ServiceInformation info;
        Iterator iter = current.entrySet().iterator();
        Iterator service_iterator;
        Integer ID;
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            set = (HashSet) me.getValue();
            service_iterator = set.iterator();
            while (service_iterator.hasNext()) {
                info = (ServiceInformation)service_iterator.next();
            	ID = new Integer(info.serviceID);
            	if (!result.containsKey(ID))
                	result.put(ID, new ExtendedServiceInformation(info, DOM.EXACT, 1.0));
            }            
        }        
        return result;
    }
        
    /**
     * If a given concept is included in the registry
     * 
     * @param uri	URI of the concept
     * @return	If the concept is in the registry
     */
    public boolean containsConcept(String uri) {
        return (inputs.containsKey(uri)||outputs.containsKey(uri));
    }
    
    /**
     * Resets the registry
     */
    public void clear() {
    	outputs = new HashMap();
        inputs = new HashMap();
    }
    
}
