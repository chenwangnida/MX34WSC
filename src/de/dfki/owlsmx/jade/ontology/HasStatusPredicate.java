/*
 * Created on 22.06.2006
 */

package de.dfki.owlsmx.jade.ontology;

import jade.content.Predicate;
import jade.core.AID;

/**
 * This class implements the predicate, which the matchmaker sends as a response
 * to a SendStatus request.
 * 
 * @author Patrick Kapahnke
 */
public class HasStatusPredicate implements Predicate {
    
    private AID				matchmaker;
    private StatusConcept	status;
    
    /**
     * @param matchmaker the JADE AID of the matchmaker agent
     */
    public void setMatchmaker(AID matchmaker) {
        this.matchmaker = matchmaker;
    }
    
    /**
     * @return the JADE AID of the matchmaker agent
     */
    public AID getMatchmaker() {
        return matchmaker;
    }
    
    /**
     * @param status the status of the matchmaker agent
     */
    public void setStatus(StatusConcept status) {
        this.status = status;
    }
    
    /**
     * @return the status of the matchmaker agent
     */
    public StatusConcept getStatus() {
        return status;
    }

}
