/*
 * Created on 13.06.2006
 */
package de.dfki.owlsmx.jade.behaviours;

import de.dfki.owlsmx.jade.ontology.RemoveServicesAction;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Matching expression for filtering <code>ACLMessage</code> objects, that contain an 
 * <code>RemoveServicesAction</code> agent action a defined in the <code>MatchmakerOntology</code>.
 * 
 * @author Patrick Kapahnke
 */
public class RemoveServicesMatchingExpression implements MessageTemplate.MatchExpression {
    
    private Agent		agent;
    
    public RemoveServicesMatchingExpression(Agent agent) {
        this.agent = agent;
    }
    
    public boolean match(ACLMessage message) {
        try {
            ContentElement content = agent.getContentManager().extractContent(message);
            if(((Action) content).getAction() instanceof RemoveServicesAction) return true;
            else return false;
        }
        catch(Exception e) {
            return false;
        }
    }

}
