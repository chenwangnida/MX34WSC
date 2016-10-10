/*
 * Created on 12.06.2006
 */
package de.dfki.owlsmx.jade.behaviours;

import java.net.URI;
import java.net.URISyntaxException;

import de.dfki.owlsmx.SimilarityMatchingEngine;
import de.dfki.owlsmx.jade.ontology.AddServicesAction;
import de.dfki.owlsmx.jade.ontology.ServiceConcept;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.proto.SimpleAchieveREResponder;
import jade.domain.FIPANames;
import jade.util.leap.List;
import jade.util.leap.Iterator;


/**
 * This class extends the JADE <code>SimpleAchieveREResponder</code> behaviour to handle
 * "AddService" requests. It generates a <code>MessageTemplate</code> object, which filters
 * all relevant request messages and thus does not wrongly process requests such as "RemoveServicesAction"
 * or "MatchAction". The result notification of this Responder is a simple "inform" message with no
 * content, if the matchmaker succeeded or a "failure" message if something goes wrong. 
 * 
 * @author Patrick Kapahnke
 */
public class AddServicesResponderBehaviour extends SimpleAchieveREResponder {
    
    private SimilarityMatchingEngine		matchmaker;
    private AddServicesAction						addServicesAction;
    
    AddServicesResponderBehaviour(Agent agent, SimilarityMatchingEngine matchmaker) {
        super(agent, MessageTemplate.and(new MessageTemplate(new AddServicesMatchingExpression(agent)), SimpleAchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST)));
                
        this.matchmaker = matchmaker;
    }
    
    protected ACLMessage prepareResponse(ACLMessage request) {
        
        ACLMessage response = request.createReply();
        
        // try to fetch the request content
        try {
            ContentElement content = myAgent.getContentManager().extractContent(request);
            addServicesAction = (AddServicesAction) ((Action) content).getAction();
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
	        List services = addServicesAction.getServiceList();
	        Iterator serviceIter = services.iterator();
	        ServiceConcept next;
	        URI uri;
	        while(serviceIter.hasNext()) {
	            next = (ServiceConcept) serviceIter.next();
	            try {
	                uri = new URI(next.getURI());
	            }
	            catch(URISyntaxException e) {
	                // skip malformed uri inputs
	                continue;
	            }
	            // add service directly from URI...
	            if(next.getContent() == null) matchmaker.addService(uri);
	            // ... or by passing it's content in string format
	            else matchmaker.addService(next.getContent(), uri);            
	        }
	        
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
