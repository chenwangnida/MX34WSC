/*
 * Created on 06.06.2006
 */
package de.dfki.owlsmx.jade.behaviours.request;

import java.net.URI;

import de.dfki.owlsmx.jade.ontology.MatchAction;
import de.dfki.owlsmx.jade.ontology.MatchmakerOntology;
import de.dfki.owlsmx.jade.ontology.ServiceConcept;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPANames;
import jade.proto.SimpleAchieveREInitiator;
import jade.content.onto.basic.Action;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;


/**
 * This class can be used to initiate a FIPA request interaction with the <code>MatchmakerAgent</code> to
 * perform a matching request given a service (or service content), the matchmaking variant M0-M4 of the
 * <code>OWLSMXMatchmaker</code> to use, the minimum degree of match and a syntactic similarity treshold.
 * The handler methods should get overridden to perform the intended actions (inform the user, extract 
 * the resulting information etc.). The "inform" message, that is the result of a successful invokation of
 * the matchmaker, contains a "MatchesPredicate" predicate with the ranking produced by the matchmaker as content.
 * 
 * @author Patrick Kapahnke
 */
public class RequestMatchBehaviour extends SimpleAchieveREInitiator {

    private AID				matchmakerAgent;
    private URI				serviceURI = null;
    private int				variant;
    private String			serviceContent = null;
    private int				minimumDegreeOfMatch = 5;
    private double			minimumSimilarity = 0.0;
    private int				sortingType = 0;

    /**
     * Constructs a new RequestMatchBehaviour.
     * @param myAgent the agent executing this <code>RequestMatchBehaviour</code>
     * @param matchmakerAgent the matchmaker agent to which the request is to be sent
     * @param serviceURI the URI of the request service
     * @param variant the variant M0-M4 of the matchmaker. Use constants defined in <code>de.dfki.owlsmx.similaritymeasures.SimilarityMeasure</code>
     */
    public RequestMatchBehaviour(Agent myAgent, AID matchmakerAgent, URI serviceURI, int variant) {
        super(myAgent, null);
        
        this.matchmakerAgent		= matchmakerAgent;
        this.serviceURI				= serviceURI;
        this.variant				= variant;
    }
    
    /**
     * Constructs a new RequestMatchBehaviour.
     * @param myAgent the agent executing this <code>RequestMatchBehaviour</code>
     * @param matchmakerAgent the matchmaker agent to which the request is to be sent
     * @param serviceURI the URI of the request service
     * @param variant the variant M0-M4 of the matchmaker. Use constants defined in <code>de.dfki.owlsmx.similaritymeasures.SimilarityMeasure</code>
     * @param minimumDegreeOfMatch the minimum degree of match as defined in <code>de.dfki.owlsmx.data.DegreeOfMatch</code>
     * @param minimumSimilarity the minimum syntactic similarity
     */
    public RequestMatchBehaviour(Agent myAgent, AID matchmakerAgent, URI serviceURI, int variant, int minimumDegreeOfMatch, double minimumSimilarity) {
        super(myAgent, null);
        
        this.matchmakerAgent		= matchmakerAgent;
        this.serviceURI				= serviceURI;
        this.variant				= variant;
        this.minimumDegreeOfMatch	= minimumDegreeOfMatch;
        this.minimumSimilarity		= minimumSimilarity;
    }

    /**
     * Constructs a new RequestMatchBehaviour.
     * @param myAgent the agent executing this <code>RequestMatchBehaviour</code>
     * @param matchmakerAgent the matchmaker agent to which the request is to be sent
     * @param serviceURI the URI of the request service
     * @param variant the variant M0-M4 of the matchmaker. Use constants defined in <code>de.dfki.owlsmx.similaritymeasures.SimilarityMeasure</code>
     * @param minimumDegreeOfMatch the minimum degree of match as defined in <code>de.dfki.owlsmx.data.DegreeOfMatch</code>
     * @param minimumSimilarity the minimum syntactic similarity
     * @param sortingType the sorting type for the ranking as defined in <code>de.dfki.owlsmx.data.SortingType</code>
     */
    public RequestMatchBehaviour(Agent myAgent, AID matchmakerAgent, URI serviceURI, int variant, int minimumDegreeOfMatch, double minimumSimilarity, int sortingType) {
        super(myAgent, null);
        
        this.matchmakerAgent		= matchmakerAgent;
        this.serviceURI				= serviceURI;
        this.variant				= variant;
        this.minimumDegreeOfMatch	= minimumDegreeOfMatch;
        this.minimumSimilarity		= minimumSimilarity;
        this.sortingType			= sortingType;
    }

    /**
     * Constructs a new RequestMatchBehaviour
     * @param myAgent the agent executing this <code>RequestMatchBehaviour</code>
     * @param matchmakerAgent the matchmaker agent to which the request is to be sent
     * @param serviceContent the request service
     * @param variant the variant M0-M4 of the matchmaker. Use constants defined in <code>de.dfki.owlsmx.similaritymeasures.SimilarityMeasure</code>
     */
    public RequestMatchBehaviour(Agent myAgent, AID matchmakerAgent, String serviceContent, int variant) {
        super(myAgent, null);
        
        this.matchmakerAgent 		= matchmakerAgent;
        this.serviceContent 		= serviceContent;
        this.variant				= variant;
    }
    
    /**
     * Constructs a new RequestMatchBehaviour
     * @param myAgent the agent executing this <code>RequestMatchBehaviour</code>
     * @param matchmakerAgent the matchmaker agent to which the request is to be sent
     * @param serviceContent the request service
     * @param variant the variant M0-M4 of the matchmaker. Use constants defined in <code>de.dfki.owlsmx.similaritymeasures.SimilarityMeasure</code>
     * @param minimumDegreeOfMatch the minimum degree of match as defined in <code>de.dfki.owlsmx.data.DegreeOfMatch</code>
     * @param minimumSimilarity the minimum syntactic similarity
     */
    public RequestMatchBehaviour(Agent myAgent, AID matchmakerAgent, String serviceContent, int variant, int minimumDegreeOfMatch, double minimumSimilarity) {
        super(myAgent, null);
        
        this.matchmakerAgent 		= matchmakerAgent;
        this.serviceContent 		= serviceContent;
        this.variant				= variant;
        this.minimumDegreeOfMatch 	= minimumDegreeOfMatch;
        this.minimumSimilarity 		= minimumSimilarity;
    }
    
    /**
     * Constructs a new RequestMatchBehaviour
     * @param myAgent the agent executing this <code>RequestMatchBehaviour</code>
     * @param matchmakerAgent the matchmaker agent to which the request is to be sent
     * @param serviceContent the request service
     * @param variant the variant M0-M4 of the matchmaker. Use constants defined in <code>de.dfki.owlsmx.similaritymeasures.SimilarityMeasure</code>
     * @param minimumDegreeOfMatch the minimum degree of match as defined in <code>de.dfki.owlsmx.data.DegreeOfMatch</code>
     * @param minimumSimilarity the minimum syntactic similarity
     * @param sortingType the sorting type for the ranking as defined in <code>de.dfki.owlsmx.data.SortingType</code>
     */
    public RequestMatchBehaviour(Agent myAgent, AID matchmakerAgent, String serviceContent, int variant, int minimumDegreeOfMatch, double minimumSimilarity, int sortingType) {
        super(myAgent, null);
        
        this.matchmakerAgent 		= matchmakerAgent;
        this.serviceContent 		= serviceContent;
        this.variant				= variant;
        this.minimumDegreeOfMatch 	= minimumDegreeOfMatch;
        this.minimumSimilarity 		= minimumSimilarity;
        this.sortingType			= sortingType;
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
     * This method is called when the matchmaker agent successfully executed the matching request.
     * Matching results are included in the message body.
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
     * This method is called when the matchmaker agent failed to perform the request.
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
        ServiceConcept service = new ServiceConcept();
        if(serviceURI != null) service.setURI(serviceURI.toString());
        else {
            service.setURI("");
            service.setContent(serviceContent);
        }
        MatchAction match = new MatchAction();
        match.setVariant(variant);
        match.setMinimumDOM(minimumDegreeOfMatch);
        match.setMinimumSIM(minimumSimilarity);
        match.setService(service);
        
        Action action = new Action(matchmakerAgent, match);
        
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
