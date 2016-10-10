/*
 * Created on 06.06.2006
 */
package de.dfki.owlsmx.jade.behaviours.request;

import java.util.Set;
import java.util.Map;
import java.util.Iterator;

import de.dfki.owlsmx.jade.ontology.AddServicesAction;
import de.dfki.owlsmx.jade.ontology.MatchmakerOntology;
import de.dfki.owlsmx.jade.ontology.ServiceConcept;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPANames;
import jade.proto.SimpleAchieveREInitiator;
import jade.util.leap.LinkedList;
import jade.content.onto.basic.Action;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;


/**
 * This class can be used to initiate a FIPA request interaction with the <code>MatchmakerAgent</code>,
 * which causes the matchmaker to add a list of services to it's registry. The handler methods should get
 * overridden to perform the intended actions (inform the user, extract the resulting information etc.).
 * 
 * @author Patrick Kapahnke
 */
public class RequestAddServicesBehaviour extends SimpleAchieveREInitiator {

    private AID				matchmakerAgent;
    private Set				services;
    private Map				serviceContents = null;

    /**
     * Constructs a new RequestAddServicesBehaviour. 
     * @param myAgent the agent executing this <code>RequestAddServicesBehaviour</code>
     * @param matchmakerAgent the matchmaker agent to which the request is to be sent
     * @param services <code>Set</code> of <code>String</code> objects representing the URI's of the services which should be added
     */
    public RequestAddServicesBehaviour(Agent myAgent, AID matchmakerAgent, Set services) {
        super(myAgent, null);
        
        this.matchmakerAgent	= matchmakerAgent;
        this.services 			= services;
    }
    
    /**
     * Constructs a new RequestAddServicesBehaviour. 
     * @param myAgent the agent executing this <code>RequestAddServicesBehaviour</code>
     * @param matchmakerAgent the matchmaker agent to which the request is to be sent
     * @param services <code>Set</code> of <code>String</code> objects representing the URI's of the services which should be added
     * @param serviceContents mapping of service URI's to service contents in <code>String</code> format
     */
    public RequestAddServicesBehaviour(Agent myAgent, AID matchmakerAgent, Set services, Map serviceContents) {
        super(myAgent, null);
        
        this.matchmakerAgent	= matchmakerAgent;
        this.services 			= services;
        this.serviceContents	= serviceContents;
    }

    /**
     * This method is called when the request has been accepted by the matchmaker agent.
     * This implementation does nothing.
     * @param agree the agree-message received from the matchmaker agent
     * @see jade.proto.SimpleAchieveREInitiator#handleAgree(jade.lang.acl.ACLMessage)
     */
    //@Override
    protected void handleAgree(ACLMessage agree)
    {
        // Does nothing
    }

    /**
     * This method is called when the request is not accepted by the matchmaker agent.
     * This implementation does nothing.
     * @param refuse the refuse-message received from the matchmaker agent
     * @see jade.proto.SimpleAchieveREInitiator#handleRefuse(jade.lang.acl.ACLMessage)
     */
    //@Override
    protected void handleRefuse(ACLMessage refuse)
    {
        // Does nothing
    }

    /**
     * This method is called when the matchmaker agent successfully added the specified services to his registry.
     * This implementation does nothing.
     * @param inform the inform-message received from the matchmaker agent
     * @see jade.proto.SimpleAchieveREInitiator#handleRefuse(jade.lang.acl.ACLMessage)
     * @see jade.proto.SimpleAchieveREInitiator#handleInform(jade.lang.acl.ACLMessage)
     */
    //@Override
    protected void handleInform(ACLMessage inform)
    {
        // Does nothing
    }

    /**
     * This method is called when the matchmaker agent failed to add the specified services for some reason.
     * This implementation does nothing.
     * @param failure the failure-message received from the matchmaker agent
     * @see jade.proto.SimpleAchieveREInitiator#handleRefuse(jade.lang.acl.ACLMessage)
     * @see jade.proto.SimpleAchieveREInitiator#handleInform(jade.lang.acl.ACLMessage)
     */
    //@Override
    protected void handleFailure(ACLMessage failure)
    {
        // Does nothing
    }

    /**
     * Creates a request message that is understood by the matchmaker agent
     * @see jade.proto.SimpleAchieveREInitiator#prepareRequest(jade.lang.acl.ACLMessage)
     */
    //@Override
    protected ACLMessage prepareRequest(ACLMessage message) {
        ACLMessage requestMessage = new ACLMessage(ACLMessage.REQUEST);
        requestMessage.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        requestMessage.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
        requestMessage.setOntology(MatchmakerOntology.ONTOLOGY_NAME);
        requestMessage.addReceiver(matchmakerAgent);
        
        // setup the content of the message
        LinkedList servicesToAdd = new LinkedList();
        Iterator iterator = services.iterator();
        Object next;
        ServiceConcept service;
        String content;
        while(iterator.hasNext()) {
            next = iterator.next();
            if(next instanceof String) {
                service = new ServiceConcept();
                service.setURI((String) next);
                if(serviceContents != null && serviceContents.containsKey(next)) 
                    service.setContent((String) serviceContents.get(next));
                servicesToAdd.add(service);
            }
        }
        AddServicesAction addServices = new AddServicesAction();
        addServices.setServiceList(servicesToAdd);
        Action action = new Action(matchmakerAgent, addServices);
        
        try {
            myAgent.getContentManager().fillContent(requestMessage, action);
        }
        catch(CodecException e) {
            e.printStackTrace();
        }
        catch(OntologyException e) {
            e.printStackTrace();
        }
        
        return requestMessage;
    }
}
