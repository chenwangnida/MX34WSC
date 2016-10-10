/*
 * Created on 13.06.2006
 */
package de.dfki.owlsmx.jade.behaviours;

import de.dfki.owlsmx.SimilarityMatchingEngine;

import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;


/**
 * This is the central behaviour of the <code>MatchmakerAgent</code>, which handles a instance
 * of the <code>SimilarityMatchingEngine</code> and the five subbehaviours for requests. Each
 * subbehaviour is re-scheduled after termination.
 * 
 * @author Patrick Kapahnke
 */
public class MatchmakerBehaviour extends ParallelBehaviour {

    private SimilarityMatchingEngine		matchmaker;
    
    public MatchmakerBehaviour(Agent agent) {
        super(agent, ParallelBehaviour.WHEN_ALL);
        
        // initialize the matching engine
        matchmaker = new SimilarityMatchingEngine();
        
        // add all responder behaviours
        addSubBehaviour(new AddServicesResponderBehaviour(agent, matchmaker));
        addSubBehaviour(new RemoveServicesResponderBehaviour(agent, matchmaker));
        addSubBehaviour(new ClearResponderBehaviour(agent, matchmaker));
        addSubBehaviour(new MatchResponderBehaviour(agent, matchmaker));
        addSubBehaviour(new SendStatusResponderBehaviour(agent, matchmaker));
    }
    
    protected boolean checkTermination(boolean currentDone, int currentResult) {
        // reset the current behaviour if it's done
        if(currentDone) {
            getCurrent().reset();
        }
        
        // do not terminate
        return false;
    }
}
