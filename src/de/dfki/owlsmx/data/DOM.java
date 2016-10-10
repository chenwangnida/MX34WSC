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

import de.dfki.owlsmx.exceptions.MatchingException;

/**
 * An implementation of the degree of match for output concepts
 * (as it is impossible to determine the best degree for outputs
 * unless all services are checked, hence always two potential
 * DOMs (subsumes/subsumed-by) have to be remembered)
 * 
 * @author bEn
 *
 */
public class DOM extends DegreeOfMatch{
    public ExtendedServiceInformation plugin,subsumedBy;

    DOM() {
        plugin = new ExtendedServiceInformation(0, false,0, 2, DOM.FAIL, 0.0);
        subsumedBy = new ExtendedServiceInformation(0, false,0, 2, DOM.FAIL, 0.0);
    }
    
    public DOM(ExtendedServiceInformation exInfo) {
        plugin = exInfo;
        subsumedBy = exInfo;
    }
    
    /* (non-Javadoc)
     * @see matchingEngine.DegreeOfMatch#isExact()
     */
    boolean isExact() {
        return plugin.degreeOfMatch==DegreeOfMatch.EXACT;
    }

    /* (non-Javadoc)
     * @see matchingEngine.DegreeOfMatch#isPlugin()
     */
    boolean isPlugin() {
        return plugin.degreeOfMatch==DegreeOfMatch.PLUGIN;
    }

    /* (non-Javadoc)
     * @see matchingEngine.DegreeOfMatch#isSubsumes()
     */
    boolean isSubsumes() {
        return plugin.degreeOfMatch==DegreeOfMatch.SUBSUMES;
    }

    /* (non-Javadoc)
     * @see matchingEngine.DegreeOfMatch#isSubsumedBy()
     */
    boolean isSubsumedBy() {
        return subsumedBy.degreeOfMatch==DegreeOfMatch.SUBSUMED_BY;
    }

    /* (non-Javadoc)
     * @see matchingEngine.DegreeOfMatch#isNearestNeighbour()
     */
    boolean isNearestNeighbour() {
       return plugin.degreeOfMatch==DegreeOfMatch.NEAREST_NEIGHBOUR;
    }

    /* (non-Javadoc)
     * @see matchingEngine.DegreeOfMatch#isFail()
     */
    boolean isFail() {
        return false;
    }
    
    /**
     * merges the degreeOfMatch for a SINGLE concept so that the best dom remains
     * 
     * @param info one information of many candidates for the single concept
     */
    public void mergeService(ExtendedServiceInformation info) {
        if (plugin == null)
            plugin =info;
        if (subsumedBy == null)
            subsumedBy =info;
        
        //it should be onlz ONE Service
        if ( (this.plugin.serviceID!=info.serviceID) ||
             (this.subsumedBy.serviceID!=info.serviceID) )        
            return;
        
        //exact is already the best possible degree
        if (plugin.degreeOfMatch==DegreeOfMatch.EXACT)
            return;
        
        //If we get an exact degree we set all degrees to exact
        if (info.degreeOfMatch==DegreeOfMatch.EXACT) {
            this.plugin=info;
            this.subsumedBy=info;
            return;            
       }            
        
        //Getting an subsumedBy degree we only update the subsumedBy part 
        if (info.degreeOfMatch==DegreeOfMatch.SUBSUMED_BY) {
            if (subsumedBy.similarity<info.similarity)
                this.subsumedBy = info;
            return;
        }
        
       //Else we simple chose the best degreeOfMatch
       if ( (info.degreeOfMatch<plugin.degreeOfMatch) ||
                ( (info.degreeOfMatch==plugin.degreeOfMatch) &&
                        (info.similarity>plugin.similarity) ) )
            plugin=info;
       
       if (info.degreeOfMatch==DegreeOfMatch.NEAREST_NEIGHBOUR) {
           if ( (info.degreeOfMatch<subsumedBy.degreeOfMatch) ||
              ( (info.degreeOfMatch==subsumedBy.degreeOfMatch) &&
                (info.similarity>subsumedBy.similarity) ) )
               this.subsumedBy = info;
           return;
       }
    }
  
    public ExtendedServiceInformation getBestDegree(){
        if ( (this.isExact()) || (this.isPlugin()) || (this.isSubsumes()))
            return plugin;
        else if (this.isSubsumedBy())
            return subsumedBy;
        return plugin;
    }
    
    public ExtendedServiceInformation getPlugin(){
        return plugin;
    }
    
    public ExtendedServiceInformation getSubsumedBy(){
        return subsumedBy;
    }
    
    public void mergeDOM(DOM dom) throws MatchingException {
        if ( (!plugin.containsUnfoldedInformation()) || (!subsumedBy.containsUnfoldedInformation()) )
        	de.dfki.owlsmx.io.ErrorLog.instanceOf().report("Couldn't find unfolding information (" + (!plugin.containsUnfoldedInformation()) + "|" + (!subsumedBy.containsUnfoldedInformation()) + ")");
        ExtendedServiceInformation plugin_candidate = dom.getPlugin();
        ExtendedServiceInformation subsumed_by_candidate = dom.getSubsumedBy();
        
        if (dom.isExact()) {
            plugin.addUnfoldedInformation(plugin_candidate.unfoldedconcept);
            subsumedBy.addUnfoldedInformation(plugin_candidate.unfoldedconcept);
            return;}

        
        if ( (plugin_candidate.degreeOfMatch==DOM.NEAREST_NEIGHBOUR) ||
             (subsumed_by_candidate.degreeOfMatch==DOM.NEAREST_NEIGHBOUR) ) {
            if  ( (plugin_candidate.degreeOfMatch < subsumed_by_candidate.degreeOfMatch) ) {                
                plugin.degreeOfMatch = subsumed_by_candidate.degreeOfMatch;                
                subsumedBy.degreeOfMatch = subsumed_by_candidate.degreeOfMatch;                
                plugin.addUnfoldedInformation(subsumed_by_candidate.unfoldedconcept);
                subsumedBy.addUnfoldedInformation(subsumed_by_candidate.unfoldedconcept);
            }
            else {
                plugin.degreeOfMatch = plugin_candidate.degreeOfMatch;
                subsumedBy.degreeOfMatch = plugin_candidate.degreeOfMatch;
                plugin.addUnfoldedInformation(plugin_candidate.unfoldedconcept);
                subsumedBy.addUnfoldedInformation(plugin_candidate.unfoldedconcept);
            }  
            return;
        }
        
        if ( ( ( (plugin.degreeOfMatch==DOM.PLUGIN) || (plugin.degreeOfMatch==DOM.SUBSUMES) ) &&
                 (plugin_candidate.degreeOfMatch==DOM.SUBSUMED_BY) ) ||
             ( ( (plugin_candidate.degreeOfMatch==DOM.PLUGIN) || (plugin_candidate.degreeOfMatch==DOM.SUBSUMES) ) &&
                 (plugin.degreeOfMatch==DOM.SUBSUMED_BY) ) ){
            plugin.degreeOfMatch=DOM.NEAREST_NEIGHBOUR;
            plugin.addUnfoldedInformation(plugin_candidate.unfoldedconcept);
            subsumedBy.addUnfoldedInformation(plugin_candidate.unfoldedconcept);
            return;
        }
        
        if  (plugin.degreeOfMatch < plugin_candidate.degreeOfMatch) {
            plugin.degreeOfMatch=plugin_candidate.degreeOfMatch;
            plugin.addUnfoldedInformation(plugin_candidate.unfoldedconcept);
        }
        if (subsumedBy.degreeOfMatch < subsumed_by_candidate.degreeOfMatch)
            subsumedBy.degreeOfMatch=subsumed_by_candidate.degreeOfMatch;
        	subsumedBy.addUnfoldedInformation(subsumed_by_candidate.unfoldedconcept);
    }
    
    public String toString() {
    	return this.getBestDegree().toString();
    }

}
