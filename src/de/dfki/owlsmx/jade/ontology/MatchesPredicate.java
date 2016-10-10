/*
 * Created on 13.06.2006
 */

package de.dfki.owlsmx.jade.ontology;

import jade.content.Predicate;

/**
 * This class implements the "matches" predicate, which can be used to state the result,
 * that the matchmaker computed during his matching process.
 * 
 * @author Patrick Kapahnke
 */
public class MatchesPredicate implements Predicate {
    
    private ServiceConcept			request;
    private MatchingResultConcept	result;

    public void setRequest(ServiceConcept request) {
        this.request = request;
    }
    
    public ServiceConcept getRequest() {
        return request;
    }
    
    public void setResult(MatchingResultConcept result) {
        this.result = result;
    }
    
    public MatchingResultConcept getResult() {
        return result;
    }
}
