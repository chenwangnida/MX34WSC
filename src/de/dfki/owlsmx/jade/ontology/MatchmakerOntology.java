/*
 * Created on 31.05.2006
 */
package de.dfki.owlsmx.jade.ontology;

import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.BasicOntology;

import jade.content.schema.ConceptSchema;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.PredicateSchema;
import jade.content.schema.PrimitiveSchema;
import jade.content.schema.ObjectSchema;

/**
 * This extension of the JADE <code>Ontology</code> class should be used for
 * communication with the <code>MatchmakerAgent</code>. It defines concepts
 * for services and matching results and agent actions to alter the matchmaker
 * service registry and to perform matching requests.
 * 
 * @author Patrick Kapahnke
 */
public class MatchmakerOntology extends Ontology {
    
    // the name of this ontology
    public static final String 	ONTOLOGY_NAME = "Matchmaker_ontology";
    
    // vocabulary  
    // concepts
    public static final String	SERVICE = "Service";
    public static final String	SERVICE_CONTENT = "Content";
    public static final String	SERVICE_URI = "URI";
    public static final String	MATCHEDSERVICE = "MatchedService";
    public static final String	MATCHEDSERVICE_SERVICE = "Service";
    public static final String	MATCHEDSERVICE_DOM = "DOM";
    public static final String	MATCHEDSERVICE_SIM = "SIM";
    public static final String	MATCHINGRESULT = "MatchingResult";
    public static final String	MATCHINGRESULT_MATCHEDSERVICES = "MatchedServices";
    public static final String	STATUS = "Status";
    public static final String	STATUS_NUMOFSERVICES = "NumOfServices";
    
    // actions
    public static final String	ADDSERVICES = "AddServices";
    public static final String	ADDSERVICES_SERVICELIST = "ServiceList";
    public static final String	REMOVESERVICES = "RemoveServices";
    public static final String	REMOVESERVICES_SERVICELIST = "ServiceList";
    public static final String	MATCH = "Match";
    public static final String	MATCH_SERVICE = "Service";
    public static final String	MATCH_VARIANT = "Variant";
    public static final String	MATCH_MINIMUMDOM = "MinimumDOM";
    public static final String	MATCH_MINIMUMSIM = "MinimumSIM";
    public static final String	MATCH_SORTINGTYPE = "SortingType";
    public static final String	CLEAR = "Clear";
    public static final String	SENDSTATUS = "SendStatus";
    
    // predicates
    public static final String	MATCHES = "Matches";
    public static final String	MATCHES_REQUEST = "Request";
    public static final String	MATCHES_RESULT = "Result";
    public static final String	HASSTATUS = "HasStatus";
    public static final String	HASSTATUS_MATCHMAKER = "Matchmaker";
    public static final String	HASSTATUS_STATUS = "Status";
    
    private static Ontology		theInstance = new MatchmakerOntology();
    
    public static Ontology getInstance() {
        return theInstance;
    }
    
    // hidden constructor
    private MatchmakerOntology() {
        // extend BasicOntology
        super(ONTOLOGY_NAME, BasicOntology.getInstance());
        
        try {
            add(new ConceptSchema(SERVICE), ServiceConcept.class);
            add(new ConceptSchema(MATCHEDSERVICE), MatchedServiceConcept.class);
            add(new ConceptSchema(MATCHINGRESULT), MatchingResultConcept.class);
            add(new ConceptSchema(STATUS), StatusConcept.class);
            add(new AgentActionSchema(ADDSERVICES), AddServicesAction.class);
            add(new AgentActionSchema(REMOVESERVICES), RemoveServicesAction.class);
            add(new AgentActionSchema(MATCH), MatchAction.class);
            add(new AgentActionSchema(CLEAR), ClearAction.class);
            add(new AgentActionSchema(SENDSTATUS), SendStatusAction.class);
            add(new PredicateSchema(MATCHES), MatchesPredicate.class);
            add(new PredicateSchema(HASSTATUS), HasStatusPredicate.class);
            
            // structure of the schema for the Service concept
            ConceptSchema cs = (ConceptSchema) getSchema(SERVICE);
            cs.add(SERVICE_CONTENT, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            cs.add(SERVICE_URI, (PrimitiveSchema) getSchema(BasicOntology.STRING));
                                     
            // structure of the schema for the MatchedService concept
            cs = (ConceptSchema) getSchema(MATCHEDSERVICE);
            cs.add(MATCHEDSERVICE_SERVICE, (ConceptSchema) getSchema(SERVICE));
            cs.add(MATCHEDSERVICE_DOM, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
            cs.add(MATCHEDSERVICE_SIM, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
            
            // structure of the schema for the MatchingResult concept
            cs = (ConceptSchema) getSchema(MATCHINGRESULT);
            cs.add(MATCHINGRESULT_MATCHEDSERVICES, (ConceptSchema) getSchema(MATCHEDSERVICE), 0, ObjectSchema.UNLIMITED);
            
            // structure of the schema for the Status concept
            cs = (ConceptSchema) getSchema(STATUS);
            cs.add(STATUS_NUMOFSERVICES, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
            
            // structure of the schema for the AddServices agent action
            AgentActionSchema as = (AgentActionSchema) getSchema(ADDSERVICES);
            as.add(ADDSERVICES_SERVICELIST, (ConceptSchema) getSchema(SERVICE), 1, ObjectSchema.UNLIMITED);
            
            // structure of the schema for the RemoveServices agent action
            as = (AgentActionSchema) getSchema(REMOVESERVICES);
            as.add(REMOVESERVICES_SERVICELIST, (ConceptSchema) getSchema(SERVICE), 1, ObjectSchema.UNLIMITED);
            
            // structure of the schema for the Match agent action
            as = (AgentActionSchema) getSchema(MATCH);
            as.add(MATCH_SERVICE, (ConceptSchema) getSchema(SERVICE));
            as.add(MATCH_VARIANT, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
            as.add(MATCH_MINIMUMDOM, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.OPTIONAL);
            as.add(MATCH_MINIMUMSIM, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.OPTIONAL);
            as.add(MATCH_SORTINGTYPE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.OPTIONAL);
            
            // structure of the schema for the Clear agent action
            as = (AgentActionSchema) getSchema(CLEAR);
            
            // structure of the schema for the SendStatus agent action
            as = (AgentActionSchema) getSchema(SENDSTATUS);
            
            // structure of the schema for the Matches predicate
            PredicateSchema ps = (PredicateSchema) getSchema(MATCHES);
            ps.add(MATCHES_REQUEST, (ConceptSchema) getSchema(SERVICE));
            ps.add(MATCHES_RESULT, (ConceptSchema) getSchema(MATCHINGRESULT));  
            
            // structure of the schema for the HasStatus predicate
            ps = (PredicateSchema) getSchema(HASSTATUS);
            ps.add(HASSTATUS_MATCHMAKER, (ConceptSchema) getSchema(BasicOntology.AID));
            ps.add(HASSTATUS_STATUS, (ConceptSchema) getSchema(STATUS));
        }
        catch (OntologyException e) {
            e.printStackTrace();
        }
    }

}
