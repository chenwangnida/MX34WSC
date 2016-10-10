/*
 * Created on 14.06.2006
 */
package de.dfki.owlsmx.jade;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.net.URI;
import java.net.URISyntaxException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.content.lang.sl.SLCodec;
import jade.domain.FIPANames;

import de.dfki.owlsmx.jade.behaviours.request.RequestClearBehaviour;
import de.dfki.owlsmx.jade.behaviours.request.RequestAddServicesBehaviour;
import de.dfki.owlsmx.jade.behaviours.request.RequestRemoveServicesBehaviour;
import de.dfki.owlsmx.jade.behaviours.request.RequestMatchBehaviour;
import de.dfki.owlsmx.jade.behaviours.request.RequestSendStatusBehaviour;
import de.dfki.owlsmx.similaritymeasures.SimilarityMeasure;
import de.dfki.owlsmx.data.DegreeOfMatch;
import de.dfki.owlsmx.data.SortingType;

/**
 * This class implements a test agent for the matchmaker request behaviours defined in 
 * <code>de.dfki.owlsmx.jade.behaviours.request</code>. A command line UI allows the
 * execution of each behaviour. The agent takes as parameter the globaly unique JADE
 * agent identifier of the matchmaker agent.
 * 
 * @author Patrick Kapahnke
 */
public class TestRequesterAgent extends Agent {

    public TestRequesterAgent() {
        super();
    }
    
    public void setup() {
        getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
        getContentManager().registerOntology(de.dfki.owlsmx.jade.ontology.MatchmakerOntology.getInstance());
        
        Object[] args = getArguments();
        if(args.length < 1 || !(args[0] instanceof String)) doDelete();
        
		final AID matchmakerAgent = new AID((String) args[0], AID.ISGUID);
		System.out.println(args[0]);
		
		final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("commands:");
		System.out.println("  clear");
		System.out.println("  addservices [service uri's]");
		System.out.println("  removeservices [service uri's]");
		System.out.println("  match [service uri] [M0-M4] [degree of match] [syntactic similarity] [sorting type]");
		System.out.println("  sendstatus");
		
		addBehaviour(new CyclicBehaviour(this) {
		    
		    public void action() {
		        String line = null;
		        try {
		            if(reader.ready() && (line = reader.readLine()) != null) {
		                StringTokenizer tokenizer = new StringTokenizer(line, " ,;");
		                tokenizer.nextToken();
		                
		                if(line.startsWith("clear")) {
		                    System.out.println("starting clear request behaviour...");
		                    myAgent.addBehaviour(new RequestClearBehaviour(myAgent, matchmakerAgent) {
		                        
		                        protected void handleAgree(ACLMessage agree) {
		                            System.out.println("matchmaker agent agreed to clear request.");
		                        }
		                        
		                        protected void handleInform(ACLMessage inform) {
		                            System.out.println("matchmaker agent cleared his service registry.");
		                        }
		                        
		                        protected void handleFailure(ACLMessage failure) {
		                            System.err.println("error: matchmaker agent sent failure message!");
		                        }
		                    });
		                    return;
		                }
		                
		                if(line.startsWith("addservices")) {
		                    TreeSet services = new TreeSet();
		                    while(tokenizer.hasMoreTokens()) {
		                        services.add(tokenizer.nextToken());
		                    }
		                    if(services.isEmpty()) {
		                        System.err.println("error: no services specified!");
		                        return;
		                    }
		                    myAgent.addBehaviour(new RequestAddServicesBehaviour(myAgent, matchmakerAgent, services) {
		                        
		                        protected void handleAgree(ACLMessage agree) {
		                            System.out.println("matchmaker agent agreed to add services.");
		                        }
		                        
		                        protected void handleInform(ACLMessage inform) {
		                            System.out.println("matchmaker agent successfully executed the add services request.");
		                        }
		                        
		                        protected void handleFailure(ACLMessage failure) {
		                            System.err.println("error: matchmaker agent sent failure message!");
		                        }
		                    });
		                    
		                    return;
		                }
		                
		                if(line.startsWith("removeservices")) {
		                    TreeSet services = new TreeSet();
		                    while(tokenizer.hasMoreTokens()) {
		                        services.add(tokenizer.nextToken());
		                    }
		                    if(services.isEmpty()) {
		                        System.err.println("error: no services specified!");
		                        return;
		                    }
		                    myAgent.addBehaviour(new RequestRemoveServicesBehaviour(myAgent, matchmakerAgent, services) {
		                        
		                        protected void handleAgree(ACLMessage agree) {
		                            System.out.println("matchmaker agent agreed to remove services.");
		                        }
		                        
		                        protected void handleInform(ACLMessage inform) {
		                            System.out.println("matchmaker agent successfully executed the remove serivces request.");
		                        }
		                        
		                        protected void handleFailure(ACLMessage failure) {
		                            System.err.println("error: matchmaker agent sent failure message!");
		                        }
		                    });
		                    
		                    return;
		                }
		                
		                if(line.startsWith("match")) {
		                    if(tokenizer.countTokens() != 5) {
		                        System.err.println("error: invalid number of parameters!");
		                        return;
		                    }
		                    String token = tokenizer.nextToken();
		                    URI serviceURI;
		                    try {
		                        serviceURI = new URI(token);
		                    }
		                    catch(URISyntaxException e) {
		                        System.err.println("error: \"" + token + "\" is not a valid URI!");
		                        return;
		                    }
		                    token = tokenizer.nextToken();
		                    int variant;
		                    if(token.equals("M0"))		variant = SimilarityMeasure.SIMILARITY_NONE;
		                    else if(token.equals("M1"))	variant = SimilarityMeasure.SIMILARITY_LOI;
		                    else if(token.equals("M2"))	variant = SimilarityMeasure.SIMILARITY_EXTENDED_JACCARD;
		                    else if(token.equals("M3"))	variant = SimilarityMeasure.SIMILARITY_COSINE;
		                    else if(token.equals("M4"))	variant = SimilarityMeasure.SIMILARITY_JENSEN_SHANNON;
		                    else {
		                        System.err.println("error: invalid OWLS-MX variant!");
		                        return;
		                    }
		                    
		                    token = tokenizer.nextToken();
		                    int minimumDOM;
		                    if(token.equals("EXACT"))					minimumDOM = DegreeOfMatch.EXACT;
		                    else if(token.equals("PLUGIN"))				minimumDOM = DegreeOfMatch.PLUGIN;
		                    else if(token.equals("SUBSUMES"))			minimumDOM = DegreeOfMatch.SUBSUMES;
		                    else if(token.equals("SUBSUMED_BY"))		minimumDOM = DegreeOfMatch.SUBSUMED_BY;
		                    else if(token.equals("NEAREST_NEIGHBOUR"))	minimumDOM = DegreeOfMatch.NEAREST_NEIGHBOUR;
		                    else {
		                        System.err.println("error: invalid minimum degree of match!");
		                        return;
		                    }
		                    
		                    double minimumSIM = (new Double(tokenizer.nextToken())).doubleValue();
		                    
		                    token = tokenizer.nextToken();
		                    int sortingType;
		                    if(token.equals("SEMANTIC"))				sortingType = SortingType.SEMANTIC;
		                    else if(token.equals("SYNTACTIC"))			sortingType = SortingType.SYNTACTIC;
		                    else if(token.equals("HYBRID"))				sortingType = SortingType.HYBRID;
		                    else {
		                        System.err.println("error: invalid sorting type!");
		                        return;
		                    }
		                    
		                    myAgent.addBehaviour(new RequestMatchBehaviour(myAgent, matchmakerAgent, serviceURI, variant, minimumDOM, minimumSIM, sortingType) {
		                        
		                        protected void handleAgree(ACLMessage agree) {
		                            System.out.println("matchmaker agent agreed to the matching request.");
		                        }
		                        
		                        protected void handleInform(ACLMessage inform) {
		                            System.out.println("matchmaker agent successfully executed the matching request:");
		                            System.out.println(inform.getContent());
		                        }
		                        
		                        protected void handleFailure(ACLMessage failure) {
		                            System.err.println("error: matchmaker agent sent failure message!");
		                        }	                        
		                    });
		                    
		                    return;
		                }
		                
		                if(line.startsWith("sendstatus")) {
		                    System.out.println("starting sendstatus request behaviour...");
		                    myAgent.addBehaviour(new RequestSendStatusBehaviour(myAgent, matchmakerAgent) {
		                        
		                        protected void handleAgree(ACLMessage agree) {
		                            System.out.println("matchmaker agent agreed to sendstatus request.");
		                        }
		                        
		                        protected void handleInform(ACLMessage inform) {
		                            System.out.println("matchmaker agent successfully sent his status:");
		                            System.out.println(inform.getContent());
		                        }
		                        
		                        protected void handleFailure(ACLMessage failure) {
		                            System.err.println("error: matchmaker agent sent failure message!");
		                        }
		                    });		                    
		                    return;
		                }
		                
		                System.err.println("error: invalid command!");
		            }
		        }
		        catch(IOException e) {
		            System.err.println("error: IO error!");
		        }
		    }
		});
    }
}
