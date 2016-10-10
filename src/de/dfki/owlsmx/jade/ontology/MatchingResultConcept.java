/*
 * Created on 31.05.2006
 */
package de.dfki.owlsmx.jade.ontology;

import jade.content.Concept;
import jade.util.leap.List;

/**
 * This class implements the concept for a matching result, which is a collection
 * of matched services, which also contain their resulting values in their data
 * structure.
 * 
 * @author Patrick Kapahnke
 */
public class MatchingResultConcept implements Concept {
    
    private List		matchedServices;
    
    /**
     * @param matchedServices a <code>List</code> of <code>MatchedServiceConcept</code> objects
     */
    public void setMatchedServices(List matchedServices) {
        this.matchedServices = matchedServices;
    }
    
    /**
     * @return the <code>List</code> of <code>MatchedServiceConcept</code> objects
     */
    public List getMatchedServices() {
        return matchedServices;
    }

}
