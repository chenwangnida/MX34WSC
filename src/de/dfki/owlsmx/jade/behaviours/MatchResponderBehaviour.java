/*
 * Created on 12.06.2006
 */
package de.dfki.owlsmx.jade.behaviours;

import java.net.URI;
import java.util.SortedSet;
import java.util.Iterator;

import de.dfki.owlsmx.SimilarityMatchingEngine;
import de.dfki.owlsmx.jade.ontology.MatchAction;
import de.dfki.owlsmx.jade.ontology.MatchedServiceConcept;
import de.dfki.owlsmx.jade.ontology.MatchesPredicate;
import de.dfki.owlsmx.jade.ontology.MatchingResultConcept;
import de.dfki.owlsmx.jade.ontology.ServiceConcept;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.proto.SimpleAchieveREResponder;
import jade.domain.FIPANames;
import jade.util.leap.List;
import jade.util.leap.LinkedList;


/**
 * This class extends the JADE <code>SimpleAchieveREResponder</code> behaviour to handle
 * "MatchAction" requests. It generates a <code>MessageTemplate</code> object, which filters
 * all relevant request messages and thus does not wrongly process requests such as "RemoveServicesAction"
 * or "AddServicesAction". The result notification of this Responder is a "inform" message with a
 * "MatchesPredicate" predicate (as defined in the <code>MatchmakerOntology</code>) as content, which
 * yields the ranking, that the matchmaker produced. A "failure" message as result notification
 * states, that something went wrong during computation.
 * 
 * @author Patrick Kapahnke
 */
public class MatchResponderBehaviour extends SimpleAchieveREResponder {

    private SimilarityMatchingEngine		matchmaker;
    private MatchAction							matchAction;
    
    public MatchResponderBehaviour(Agent agent, SimilarityMatchingEngine matchmaker) {
        super(agent, MessageTemplate.and(new MessageTemplate(new MatchMatchingExpression(agent)), SimpleAchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST)));
        
        this.matchmaker = matchmaker;        
    }
    
    protected ACLMessage prepareResponse(ACLMessage request) {
        
        ACLMessage response = request.createReply();
        
        // try to fetch the request content
        try {
            ContentElement content = myAgent.getContentManager().extractContent(request);
            matchAction = (MatchAction) ((Action) content).getAction();
            response.setPerformative(ACLMessage.AGREE);
        }
        // send a "not_understood" if the content could not be extracted for some reason
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
            ServiceConcept service		= matchAction.getService();
            int variant					= matchAction.getVariant();
            int minimumDegreeOfMatch	= matchAction.getMinimumDOM();
            double minimumSimilarity	= matchAction.getMinimumSIM();
            int sortingType				= matchAction.getSortingType();
            
            matchmaker.setSimilarityMeasure((short) variant);
            SortedSet resultSet;
            if(service.getContent() != null) {
                resultSet = matchmaker.matchRequest(service.getContent(), minimumDegreeOfMatch, minimumSimilarity, sortingType);
            }
            else {
                URI uri = new URI(service.getURI());
                resultSet = matchmaker.matchRequest(uri, minimumDegreeOfMatch, minimumSimilarity, sortingType);
            }
            
            // construct the result using the MatchmakerOntology
            MatchesPredicate matches = new MatchesPredicate();
            matches.setRequest(service);
            MatchingResultConcept matchingResult = new MatchingResultConcept();
            List resultList = new LinkedList();
            Iterator setIter = resultSet.iterator();
            de.dfki.owlsmx.data.MatchedService next;
            MatchedServiceConcept matchedService;
            while(setIter.hasNext()) {
                next = (de.dfki.owlsmx.data.MatchedService) setIter.next();
                matchedService	= new MatchedServiceConcept();
                service			= new ServiceConcept();
                service.setURI(next.getServiceURI().toString());
                if(next.getServiceAsString() != null) service.setContent(next.getServiceAsString());
                matchedService.setService(service);
                matchedService.setDOM(next.getDegreeOfMatch());
                matchedService.setSIM(next.getSyntacticSimilarity());
                resultList.add(matchedService);
            }
            matchingResult.setMatchedServices(resultList);
            matches.setResult(matchingResult);
            
            // fill the content slot of the ACLMessage with the constructed result
            myAgent.getContentManager().fillContent(result, matches);
            result.setPerformative(ACLMessage.INFORM);       
        }
        // send a "failure" message if something went wrong
        catch(Throwable t) {
            t.printStackTrace();
            result.setPerformative(ACLMessage.FAILURE);
        }
        
        return result;
    }
}
