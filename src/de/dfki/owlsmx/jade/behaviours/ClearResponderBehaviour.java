/*
 * Created on 14.06.2006
 */
package de.dfki.owlsmx.jade.behaviours;

import de.dfki.owlsmx.SimilarityMatchingEngine;
import de.dfki.owlsmx.jade.ontology.ClearAction;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.proto.SimpleAchieveREResponder;
import jade.domain.FIPANames;

/**
 * This class extends the JADE <code>SimpleAchieveREResponder</code> behaviour to handle
 * "Clear" requests. It generates a <code>MessageTemplate</code> object, which filters
 * all relevant request messages and thus does not wrongly process requests such as "RemoveServicesAction"
 * or "MatchAction". The result notification of this Responder is a simple "inform" message with no
 * content, if the matchmaker succeeded or a "failure" message if something goes wrong. 
 * 
 * @author Patrick Kapahnke
 */
public class ClearResponderBehaviour extends SimpleAchieveREResponder {
    
    private SimilarityMatchingEngine		matchmaker;
    private ClearAction						clearAction;
    
    ClearResponderBehaviour(Agent agent, SimilarityMatchingEngine matchmaker) {
        super(agent, MessageTemplate.and(new MessageTemplate(new ClearMatchingExpression(agent)), SimpleAchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST)));
                
        this.matchmaker = matchmaker;
    }
    
    protected ACLMessage prepareResponse(ACLMessage request) {
        
        ACLMessage response = request.createReply();
        
        // try to fetch the request content
        try {
            ContentElement content = myAgent.getContentManager().extractContent(request);
            clearAction = (ClearAction) ((Action) content).getAction();
            response.setPerformative(ACLMessage.AGREE);
        }
        // answer with "not_understood" if the content could not be extracted for any reason
        catch (Exception e) {
            e.printStackTrace();
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
        }
                        
        return response;
    }

    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
        ACLMessage result = request.createReply();
        
        try {
	        // execute the request
            matchmaker.clear();
	        
	        result.setPerformative(ACLMessage.INFORM);
        }
        // send a "failure" response if something goes wrong
        catch(Throwable t) {
            t.printStackTrace();
            result.setPerformative(ACLMessage.FAILURE);
        }     
        
        return result;
    }
}
