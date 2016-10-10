/*
 * Created on 22.06.2006
 */
package de.dfki.owlsmx.jade.behaviours.request;

import de.dfki.owlsmx.jade.ontology.SendStatusAction;
import de.dfki.owlsmx.jade.ontology.MatchmakerOntology;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPANames;
import jade.proto.SimpleAchieveREInitiator;
import jade.content.onto.basic.Action;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;

/**
 * @author Patrick Kapahnke
 */
public class RequestSendStatusBehaviour extends SimpleAchieveREInitiator {

    private AID				matchmakerAgent;

    /**
     * Constructs a new RequestClearBehaviour. 
     * @param myAgent the agent executing this <code>RequestAddServicesBehaviour</code>
     */
    public RequestSendStatusBehaviour(Agent myAgent, AID matchmakerAgent) {
        super(myAgent, null);
        
        this.matchmakerAgent = matchmakerAgent;
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
        
        SendStatusAction sendStatus = new SendStatusAction();
        Action action = new Action(matchmakerAgent, sendStatus);
        
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
