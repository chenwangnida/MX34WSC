/*
 * Created on 13.06.2006
 */
package de.dfki.owlsmx.jade;

import de.dfki.owlsmx.jade.behaviours.MatchmakerBehaviour;

import jade.core.Agent;
import jade.content.lang.sl.SLCodec;
import jade.domain.FIPANames;


/**
 * Implementation of the matchmaker agent, which handles four JADE <code>SimpleAchieveREResponder</code> 
 * behaviours composed by <code>MatchmakerBehaviour</code> for executing the request types "add services", 
 * "remove services", "clear" and "match".
 * 
 * @author Patrick Kapahnke
 */
public class MatchmakerAgent extends Agent {

    public MatchmakerAgent() {
        super();
    }
    
    public void setup() {
        // setup content manager
        getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
        getContentManager().registerOntology(de.dfki.owlsmx.jade.ontology.MatchmakerOntology.getInstance());
        
        addBehaviour(new MatchmakerBehaviour(this));
    }
}
