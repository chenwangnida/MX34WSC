/*
 * OWL-S Matchmaker
 * 
 * COPYRIGHT NOTICE
 * 
 * Copyright (C) 2005 DFKI GmbH, Germany
 * Developed by Benedikt Fries, Matthias Klusch
 * 
 * The code is free for non-commercial use only.
 * You can redistribute it and/or modify it under the terms
 * of the Mozilla Public License version 1.1  as
 * published by the Mozilla Foundation at
 * http://www.mozilla.org/MPL/MPL-1.1.txt
 */
package de.dfki.owlsmx;



import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.mindswap.owl.OWLConfig;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owls.service.Service;
import org.mindswap.pellet.TuBox.NotUnfoldableException;


import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;

import de.dfki.owlsmx.Indexer.SimpleIndex;
import de.dfki.owlsmx.data.ConceptServiceRegistry;
import de.dfki.owlsmx.data.DOM;
import de.dfki.owlsmx.data.ExtendedServiceInformation;
import de.dfki.owlsmx.data.InputServiceContainer;
import de.dfki.owlsmx.data.LocalOntologyContainer;
import de.dfki.owlsmx.data.MatchingResult;
import de.dfki.owlsmx.data.OutputServiceContainer;
import de.dfki.owlsmx.data.ServiceInformation;
import de.dfki.owlsmx.exceptions.ConceptNotFoundException;
import de.dfki.owlsmx.exceptions.MatchingException;
import de.dfki.owlsmx.io.ErrorLog;
import de.dfki.owlsmx.reasoning.PelletReasoner;
import de.dfki.owlsmx.similaritymeasures.SimilarityMeasure;
import de.dfki.owlsmx.utils.MatchmakerUtils;
import de.dfki.owlsmx.Indexer.Index;
import de.dfki.owlsmx.Indexer.SimpleIndex;



/**
 * OWLSMXMatchmaker is the core class of the entire matchmaker.
 * It implements the matching algorithm of the same name.
 * 
 * Changes: 24.05.2006 - Patrick Kapahnke - added support for passing services and service requests not
 * only as URI, but also as stream or string.
 * 
 * @author bEn Fries
 *
 */
public class OWLSMXMatchmaker extends SimilarityMatchmaker {
	
	private org.mindswap.owl.OWLKnowledgeBase base = OWLFactory.createKB();
	private LocalOntologyContainer localOntologyContainer = new LocalOntologyContainer();
    private ConceptServiceRegistry registry;
    private PelletReasoner reason;
    
    // 3.07.08 added for supporting pure IR based matching
    private HashMap<Integer, String> unfoldedInputs = new HashMap<Integer, String>();
    private HashMap<Integer, String> unfoldedOutputs = new HashMap<Integer, String>();
    
    // 16.10.08 added for structured (I/O) IR-matching
    private Index inputIndex = new SimpleIndex();
    private Index outputIndex = new SimpleIndex();
    
    // 16.10.08 added for integrative hybrid matching
    private boolean integrative = false;
    
    private void init(SimilarityMeasure similar) {    	
    	reason = new PelletReasoner();    	
        registry= ConceptServiceRegistry.instanceOf();        
        sim = similar;	
    }
    
    /**
     * Initializes the matchmaker without any similarity measure and
     * hence purely semenatic matching
     */
    public OWLSMXMatchmaker() {
	    init(null);   	
    }


    /**
     * Initializes the matchmaker with the given similarity measure
     * 
     * @param similar Similarity measure to be used
     */
    public OWLSMXMatchmaker(SimilarityMeasure similar){
        init(similar);
    }
    

    /**
     * Initializes the matchmaker with the given similarity measure
     * 
     * @param sim  Similarity measure to be used
     */
    public OWLSMXMatchmaker(short sim) {
        SimilarityMeasure similar = switchSimilarityMeasure(sim);
        init(similar);
    }
    
    /**
     * Initializes the matchmaker with the given similarity measure
     * 
     * @param similar Similarity measure to be used
     * @param integrative True, iff integrative hybrid matching (OWLS-MX2) should be performed
     */
    public OWLSMXMatchmaker(SimilarityMeasure similar, boolean integrative){
        init(similar);
        this.integrative = integrative;
    }
    

    /**
     * Initializes the matchmaker with the given similarity measure
     * 
     * @param sim  Similarity measure to be used
     * @param integrative True, iff integrative hybrid matching (OWLS-MX2) should be performed
     */
    public OWLSMXMatchmaker(short sim, boolean integrative) {
        SimilarityMeasure similar = switchSimilarityMeasure(sim);
        init(similar);
        this.integrative = integrative;
    }

        
    public String toString() {
        return registry.toString();
    }
    
    public void print() {
    	System.err.println(this.getClass().toString());
        System.err.println(toString());
    }
        
    /**
     * Retrieves Services in the registry that are registered at certain concepts
     *      
     * @param isInput				if it's an input concept
     * @param classes				Set of OWLClass(es) with the concepts to be retrieved
     * @param concept				concept in the query we want candidate matches for				
     * @param degreeOfMatch			degree of match of the concepts to be retrieved
     * @return SortedSet of ExtendedServiceInformation(s) with the candidate services for these concepts
     * @throws URISyntaxException 
     * @throws ConceptNotFoundException 
     */
    private SortedSet getServicesFromSet(boolean isInput, Set classes, OntClass concept, int degreeOfMatch) throws URISyntaxException, ConceptNotFoundException {
    	this.save();
        SortedSet result = new TreeSet();
        Iterator iter = classes.iterator();
        Set services;
        ServiceInformation info;
        Iterator serviceIterator;
        RDFNode clazz;
        while (iter.hasNext()) {      
        	clazz=(RDFNode)iter.next();
            //toConcept=(OntClass) reason.getClass(,this.localOntology);                           
                services = registry.getServices(isInput, clazz.toString() );
                serviceIterator = services.iterator();
                while(serviceIterator.hasNext()) {
                    info = (ServiceInformation) serviceIterator.next(); 
                    try {
                        if (useSyntacticFilter())
                            result.add(new ExtendedServiceInformation(info, degreeOfMatch, reason.unfoldTerm(clazz)));
                        else {
                            //purely semantic matching doesn't need the unfolded concept
                            result.add(new ExtendedServiceInformation(info, degreeOfMatch, ""));                            
                        }
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
           
        }
        return result;
    }
    
    /**
     * Computes the set of candidates for a given service output concept
     * 
     * @param concept						URI (String) of the given concept
     * @return								SortedSet of ExtendedServiceInformation items
     * @throws URISyntaxException			If the URI is invalid
     * @throws ConceptNotFoundException		If the concept is not found
     */
    private SortedSet getServicesForOutputConcept(String concept) throws URISyntaxException, ConceptNotFoundException {
    	OntClass clazz = localOntologyContainer.getOntClass(concept);
        Set equivalent = reason.retrieveEquivalentClasses(clazz);
        Set parents = reason.retrieveDirectParentClasses(clazz,equivalent);
        Set ancestors = reason.retrieveAncestorClasses(clazz,equivalent,parents);        
        Set childs = reason.retrieveDirectSubClasses(clazz,equivalent);
        Set decendants = reason.retrieveDescendantClasses(clazz,equivalent,childs);
        
        SortedSet candidates = new TreeSet();        
        candidates.addAll(getServicesFromSet(false,equivalent,clazz,SimilarityMatchmaker.EXACT) );
        candidates.addAll(getServicesFromSet(false,childs,clazz,SimilarityMatchmaker.PLUGIN) );
        candidates.addAll(getServicesFromSet(false,decendants,clazz,SimilarityMatchmaker.SUBSUMES) );
        candidates.addAll(getServicesFromSet(false,parents,clazz,SimilarityMatchmaker.SUBSUMED_BY) );
        candidates.addAll(getServicesFromSet(false, reason.retrieveRemainingClasses(clazz, equivalent, parents, ancestors, childs, decendants),clazz,SimilarityMatchmaker.NEAREST_NEIGHBOUR) );    

        return candidates;
    }
    
    /**
     * Computes the set of candidates for a given service input concept
     * 
     * @param concept						URI (String) of the given concept
     * @return								SortedSet of ExtendedServiceInformation items
     * @throws URISyntaxException			If the URI is invalid
     * @throws ConceptNotFoundException		If the concept is not found
     */
    private SortedSet getServicesForInputConcept(String concept) throws URISyntaxException, ConceptNotFoundException {
    	OntClass clazz = localOntologyContainer.getOntClass(concept);
        Set equivalent = reason.retrieveEquivalentClasses(clazz);  
        Set ancestors = reason.retrieveAllAncestorClasses(clazz);
        
        SortedSet candidates = new TreeSet();    
        candidates.addAll(getServicesFromSet(true,equivalent,clazz,SimilarityMatchmaker.EXACT));
        candidates.addAll(getServicesFromSet(true,ancestors,clazz,SimilarityMatchmaker.PLUGIN));
        candidates.addAll(getServicesFromSet(true, reason.retrieveAllRemainingClasses(clazz, equivalent, ancestors),clazz,SimilarityMatchmaker.NEAREST_NEIGHBOUR) );        
        return candidates;
    }
     
    /**
     * Computes the set of candidates for multiple service output concepts
     * 
     * @param outConcepts			Vector of URIs of service output concepts
     * @return Map					Service ID -> DOM
     * @throws URISyntaxException	If a URI is malformed
     * @throws MatchingException	If something else goes wrong
     */
    private Map getOutputCandidates(Vector outConcepts) throws  URISyntaxException, MatchingException {
        if (outConcepts.size()==0) {
            return registry.getAllServices(false);
        }
        OutputServiceContainer resultContainer= new OutputServiceContainer();
        resultContainer.addServices(getServicesForOutputConcept(((URI) outConcepts.get(0)).toString()));
        
        OutputServiceContainer serviceContainer;
        for (int i = 1; i<outConcepts.size();i++) {
            	serviceContainer=new OutputServiceContainer();
                serviceContainer.addServices(getServicesForOutputConcept(((URI) outConcepts.get(i)).toString()));
                resultContainer.merge(serviceContainer);
        }
        System.out.println("Output candidates: " + resultContainer.getServiceMap().values().toString());
        return resultContainer.getServiceMap();
    }
    
    /**
     * Adds services without inputs to the set of input candidates
     * 
     * @param inputCandidates
     * @return
     */
    private Map addEmptyInputs(Map inputCandidates) {
        Map ServicesWithoutInput=registry.getAllServicesWithoutInput();
        Map.Entry me;
        ExtendedServiceInformation exInfo;
        Iterator iter = ServicesWithoutInput.entrySet().iterator();
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            exInfo = (ExtendedServiceInformation)me.getValue();
            inputCandidates.put((Integer)me.getKey(),new ExtendedServiceInformation(exInfo.serviceID, true, exInfo.conceptID, exInfo.noConcepts, SimilarityMatchmaker.EXACT, 0.0));
        }
        return inputCandidates;
    }
    
    /**
     * Computes the set of candidates for multiple service intput concepts
     * 
     * @param inConcepts			Vector of URIs of service output concepts
     * @return Map					Service ID -> DOM
     * @throws URISyntaxException	If a URI is malformed
     * @throws MatchingException	If something else goes wrong
     */
    protected Map getInputCandidates(Vector inConcepts) throws  URISyntaxException, ConceptNotFoundException {
        if (inConcepts.size()<=0)
            return registry.getAllServicesWithoutInput();        
        InputServiceContainer inputs = new InputServiceContainer();
        SortedSet sort;
        for (int i = 0; i<inConcepts.size();i++) {
            sort = getServicesForInputConcept(((URI) inConcepts.get(i)).toString() );
            inputs.addServices(sort);
        }
        System.out.println("Input candidates: " + inputs.getServices().values().toString());
        return addEmptyInputs(inputs.getServices());
    }
    
    /**
     * Converts a Set of DOM to a Set of ExtendedServiceInformation with the best degrees
     * of each DOM
     * 
     * @param serviceDOMs	Set of DOM
     * @return				Set of ExtendedServiceInformation
     */
    protected Set getServiceInformationFromDOM(Set serviceDOMs) {
        Set result = new HashSet();
        Iterator iter = serviceDOMs.iterator();
        while (iter.hasNext()) {
            result.add(((DOM)iter.next()).getBestDegree());
        }
        return result;
    }
    
    
    /**
     * Converts the input candidates into a result set
     * (necessary if a request has no outputs)
     * 
     * @param inputCandidates
     * @return
     */
    private SortedSet inputCandidatesToResult(Map inputCandidates){        
        SortedSet result = new TreeSet();   
        ExtendedServiceInformation inInfo;        
        Map.Entry me;
        Iterator iter = inputCandidates.entrySet().iterator();
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            inInfo=(ExtendedServiceInformation) me.getValue();
            result.add(new MatchingResult(inInfo , inInfo.unfoldedconcept, "") );
        }
    	return result;
    }
    
    /**
     * Performs purely semantic matching
     * Basically it performs a intersection of the candidates for input- and output concepts
     * The only difference is that the worst of both degree is used for the intersection 
     * 
     * @param inConcepts	Vector of URIs of input concepts of the request 
     * @param outConcepts	Vector of URIs of output concepts of the request
     * @return	SortedSet of relevant services
     * @throws URISyntaxException	Thrown if the in/outconcepts are not real URIs
     * @throws MatchingException	If something else goes wrong	
     */
    protected SortedSet semanticMatch(Vector inConcepts, Vector outConcepts) throws URISyntaxException, MatchingException {   	
        Map input = getInputCandidates(inConcepts);      
    	if ( (outConcepts==null) || (outConcepts.size()<=0) )
    		return inputCandidatesToResult(input);
    	Map output 	= 	getOutputCandidates(outConcepts);
       
        SortedSet result = new TreeSet();        
        Map.Entry me;
        Integer ID;

        ExtendedServiceInformation inInfo,outInfo;
        DOM degree;
        Iterator iter = input.entrySet().iterator();
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();
            ID = (Integer)me.getKey();
            if (output.containsKey(ID)) {
                inInfo=(ExtendedServiceInformation) me.getValue();
                degree = ((DOM) output.get(ID));
                outInfo=degree.getBestDegree();
                if ( (inInfo.degreeOfMatch>outInfo.degreeOfMatch) ||
                   ( (inInfo.degreeOfMatch==outInfo.degreeOfMatch) &&
                     (inInfo.similarity<outInfo.similarity) ) )
                     result.add(new MatchingResult(inInfo , inInfo.unfoldedconcept, outInfo.unfoldedconcept) );
                else
                    result.add(new MatchingResult(outInfo , inInfo.unfoldedconcept, outInfo.unfoldedconcept) );
            }
        }
        return result;
    }
    
    /**
     * Syntactic Filter that adjusts the semantic degree of match according to the actual similarity 
     * 
     * @param queryService				name of the query service
     * @param queryInputs				input concepts (URL strings)
     * @param queryOutputs				output concepts (URL strings)
     * @param semanticResults			Results of the semantic Filter (MatchingResult)s
     * @return	SortedSet of MatchingResult items that match the request according to the hybrid filter
     * @throws NotUnfoldableException	If the TBox can't be unfolded
     * @throws URISyntaxException		If the input/output URI's are invalid
     * @throws MatchingException		If the similarity can't be computed
     */
    protected SortedSet syntacticFilter(Service queryService, Vector queryInputs, Vector queryOutputs, SortedSet semanticResults) throws NotUnfoldableException, URISyntaxException, MatchingException {
        SortedSet result = new TreeSet();
        String  input = reason.unfoldURIs(queryInputs);
        String output = reason.unfoldURIs(queryOutputs);
        double sim_input, sim_output;        
        MatchingResult info;
        Iterator iter = semanticResults.iterator();        
         while(iter.hasNext()) {
             sim_input=0.0;
             sim_output=0.0;
            info = (MatchingResult) iter.next();
            if (!info.unfoldedInput.equals("")) {
                sim.setIndex(inputIndex);
                sim_input=sim.computeSimilarity(queryService.getURI().toString(),input,""+info.serviceID+"_I",info.unfoldedInput);
            }
            if (queryOutputs.size()>0) {
            	sim.setIndex(outputIndex);
            	sim_output = sim.computeSimilarity(queryService.getURI().toString(),output,""+info.serviceID+"_O",info.unfoldedOutput);
            }
            if(queryInputs.size() == 0)
            	info.similarity = sim_output;
            else if(queryOutputs.size() == 0)
            	info.similarity = sim_input;
            else
            	info.similarity = (sim_input+sim_output)/2;            	
            if (info.similarity < getSyntacticTreshold()) {
                if ( (info.degreeOfMatch == SimilarityMatchmaker.NEAREST_NEIGHBOUR) ||
                     (info.degreeOfMatch == SimilarityMatchmaker.SUBSUMED_BY))
                    info.degreeOfMatch = SimilarityMatchmaker.FAIL;
                else {
                	if(integrative)
                		info.degreeOfMatch = SimilarityMatchmaker.FAIL;
                	else
                        result.add(info);	
                }
            }
            else
                result.add(info);
            
        }
//         PassedTime.getTime(this.getClass().toString() + " syntacticFilter:");
        return result;
    }
    
    /**
     * IR based approximate matching.
     * 
     * @param profileURI				URI of the request service
     * @return SortedSet of MatchingResult items that match the request according to the IR based approximate matching. Semantic matching degree is fixed to nearest neighbor
     * @throws NotUnfoldableException	If the TBox can't be unfolded
     * @throws URISyntaxException		If the input/output URI's are invalid
     * @throws MatchingException		If the similarity can't be computed
     */
    public SortedSet syntacticFilter(URI profileURI) throws NotUnfoldableException, URISyntaxException, MatchingException {
        try {
        	org.mindswap.owl.OWLOntology onto = base.read(profileURI);
			Service service = onto.getService();
			Vector inputurilist=MatchmakerUtils.getURIList(service.getProfile().getInputs());
        	Vector outputurilist=MatchmakerUtils.getURIList(service.getProfile().getOutputs());
			Set conceptsToAdd = new HashSet();
        	conceptsToAdd.addAll(inputurilist);
        	conceptsToAdd.addAll(outputurilist);
//        	System.err.println("Parameters " + conceptsToAdd.toString());
        	localOntologyContainer.processClasses(base, conceptsToAdd);
            updateReasoner();
			base.unload(profileURI);
			String input = reason.unfoldURIs(inputurilist);
			String output = reason.unfoldURIs(outputurilist);
			inputIndex.addDocument(service.getURI().toString() + "_I", input);
			outputIndex.addDocument(service.getURI().toString() + "_O", output);
//			SimpleIndex.instanceOf().addDocument(service.getURI().toString(), input + " " + output);
						
			// collect all registered services (as ExtendedServiceInformation)
			Map in = registry.getAllServices(true);
			Map out = registry.getAllServices(false);
						
			Map services = new HashMap(in);
			services.putAll(out);
			
			SortedSet results = new TreeSet();
			for(Object obj : services.entrySet()) {
				Map.Entry entry = (Map.Entry) obj;
				ExtendedServiceInformation extendedServiceInfo = (ExtendedServiceInformation) entry.getValue();
				Integer ID = (Integer) entry.getKey();
				
				String unfoldedIn = unfoldedInputs.get(ID);
				String unfoldedOut = unfoldedOutputs.get(ID);
				
				MatchingResult matchingResult = new MatchingResult(extendedServiceInfo, unfoldedIn, unfoldedOut);
				matchingResult.setDegreeOfMatch(DOM.NEAREST_NEIGHBOUR);

		        double sim_input, sim_output; 
	             sim_input=0.0;
	             sim_output=0.0;
	            if (!unfoldedIn.equals("")) {
	            	sim.setIndex(inputIndex);
		            sim_input=sim.computeSimilarity(service.getURI().toString(),input,""+ID,unfoldedIn);
	            }
		        if (outputurilist.size()>0) {
		        	sim.setIndex(outputIndex);
		            sim_output = sim.computeSimilarity(service.getURI().toString(),output,""+ID,unfoldedOut);
		        }
	            if(inputurilist.size() == 0)
	            	matchingResult.similarity = sim_output;
	            else if(outputurilist.size() == 0)
	            	matchingResult.similarity = sim_input;
	            else
	            	matchingResult.similarity = (sim_input+sim_output)/2;            	
		            if (matchingResult.similarity < getSyntacticTreshold())
		            	matchingResult.degreeOfMatch = SimilarityMatchmaker.FAIL;
		            else
		            	results.add(matchingResult);							
			}
		
			return results;
        }
        catch (Exception e){
            e.printStackTrace();
            throw new MatchingException(e.toString());
        }
    }
    
    /**
     * @see de.dfki.owlsmx.Matchmaker#matchRequest(java.io.InputStream)
     */
    public SortedSet syntacticFilter(InputStream profileStream) throws MatchingException {
    	try {
	    	org.mindswap.owl.OWLOntology onto = base.read(profileStream, org.mindswap.utils.URIUtils.createURI(""));
	    	Service service = onto.getService();
	    	base.unload(onto);
	    	return syntacticFilter(service.getURI());
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		throw new MatchingException(e.toString());
    	}
    }
        
    /* (non-Javadoc)
     * @see owlsmx.Matchmaker#addService(java.lang.Integer, org.mindswap.owls.service.Service)
     */
    public void addService(Integer integer, URI profileURI){   	
		try {
			org.mindswap.owl.OWLOntology onto = base.read(profileURI);
			Service service = onto.getService();
			addService(integer, onto, MatchmakerUtils.getURIList(service.getProfile().getInputs()), MatchmakerUtils.getURIList(service.getProfile().getOutputs()) );
		} catch (FileNotFoundException e) {
			ErrorLog.instanceOf().report(this.getClass().toString() + "|addService: Could not add service from this URI" + profileURI.toString());
			e.printStackTrace();
		}
		base.unload(profileURI);
    	
    }
    
    /**
     * 24.05.2006
     * 
     * @see de.dfki.owlsmx.Matchmaker#addService(java.lang.Integer, java.io.InputStream, java.net.URI)
     */
    public void addService(Integer integer, InputStream profileStream, URI baseURI) {
    	org.mindswap.owl.OWLOntology onto = base.read(profileStream, baseURI);
    	if(onto == null) {
    		ErrorLog.instanceOf().report(this.getClass().toString() + "|addService: Unable to add service from input stream");
    		return;
    	}
    	Service service = onto.getService();
    	addService(integer, onto, MatchmakerUtils.getURIList(service.getProfile().getInputs()), MatchmakerUtils.getURIList(service.getProfile().getOutputs()));
    	base.unload(onto);
    }
    
    /**
     * 24.05.2006
     * 
     * @see de.dfki.owlsmx.Matchmaker#addService(java.lang.Integer, java.lang.String, java.net.URI)
     */
    public void addService(Integer integer, String profile, URI baseURI) {
    	// create input stream from profile string
    	InputStream profileStream = new ByteArrayInputStream(profile.getBytes());
    	// pass input stream to the appropriate addService() method
    	addService(integer, profileStream, baseURI);  	
    }
        
    /**
     * Updates the reasoner with the local ontology
     */
    private void updateReasoner() {
    	reason.clear();
    	reason.load((OntModel)localOntologyContainer.getOntology().getImplementation());
    }
    
    /**
     * Little helper function which performs the actual adding of a service
     * 
     * @param serviceID
     * @param onto
     * @param inputurilist
     * @param outputurilist
     */
    private void addService(Integer serviceID,org.mindswap.owl.OWLOntology onto, Vector inputurilist, Vector outputurilist) {
        try {
        	Set conceptsToAdd = new HashSet();
        	conceptsToAdd.addAll(inputurilist);
        	conceptsToAdd.addAll(outputurilist);
        	localOntologyContainer.processClasses(base, conceptsToAdd);
            updateReasoner();        	
            if (useSyntacticFilter()) {
            	String unfoldedInput = reason.unfoldURIs(inputurilist);
            	String unfoldedOutput = reason.unfoldURIs(outputurilist);
            	inputIndex.addDocument("" + serviceID + "_I", unfoldedInput);
            	outputIndex.addDocument("" + serviceID + "_O", unfoldedOutput);
//                SimpleIndex.instanceOf().addDocument(""+serviceID,unfoldedInput+ " " +unfoldedOutput);
                unfoldedInputs.put(serviceID, unfoldedInput);
                unfoldedOutputs.put(serviceID, unfoldedOutput);
            }
            registry.addConcepts(true,serviceID.intValue(), inputurilist );
            registry.addConcepts(false, serviceID.intValue(), outputurilist );
        } catch (Exception e) {e.printStackTrace();}  
    }
   
    /* (non-Javadoc)
     * @see owlsmx.Matchmaker#matchRequest(org.mindswap.owls.service.Service)
     */
    public SortedSet matchRequest(URI profileURI) throws MatchingException{
        try {
//        	System.err.println("Matching request: " + profileURI);
        	org.mindswap.owl.OWLOntology onto = base.read(profileURI);
			Service service = onto.getService();
//			System.err.println("Processing request" + service.getURI());
			Vector inputurilist=MatchmakerUtils.getURIList(service.getProfile().getInputs());
        	Vector outputurilist=MatchmakerUtils.getURIList(service.getProfile().getOutputs());
			Set conceptsToAdd = new HashSet();
        	conceptsToAdd.addAll(inputurilist);
        	conceptsToAdd.addAll(outputurilist);
//        	System.err.println("Parameters " + conceptsToAdd.toString());
        	localOntologyContainer.processClasses(base, conceptsToAdd);
            updateReasoner();
			base.unload(profileURI);
        if (useSyntacticFilter()) {
        	// Is this necessary?? (commented out)
//            SimpleIndex.instanceOf().addDocument(service.getURI().toString(),reason.unfoldURIs(inputurilist)+ " " + reason.unfoldURIs(outputurilist));
            return syntacticFilter(service, inputurilist, outputurilist, semanticMatch(inputurilist, outputurilist) );
        }
        else {
            return semanticMatch(inputurilist, outputurilist);
        	}
        }
        catch (Exception e){
            e.printStackTrace();
            throw new MatchingException(e.toString());
        }
    }
    
    /**
     * @see de.dfki.owlsmx.Matchmaker#matchRequest(java.io.InputStream)
     */
    public SortedSet matchRequest(InputStream profileStream) throws MatchingException {
    	try {
	    	org.mindswap.owl.OWLOntology onto = base.read(profileStream, org.mindswap.utils.URIUtils.createURI(""));
	    	Service service = onto.getService();
	    	Vector inputurilist = MatchmakerUtils.getURIList(service.getProfile().getInputs());
	    	Vector outputurilist = MatchmakerUtils.getURIList(service.getProfile().getOutputs());
	    	Set conceptsToAdd = new HashSet();
	    	conceptsToAdd.addAll(inputurilist);
	    	conceptsToAdd.addAll(outputurilist);
	    	localOntologyContainer.processClasses(base, conceptsToAdd);
	    	updateReasoner();
	    	base.unload(onto);
	    	if(useSyntacticFilter()) {
	        	// Is this necessary?? (commented out)
//	    		SimpleIndex.instanceOf().addDocument(service.getURI().toString(), reason.unfoldURIs(inputurilist) + " " + reason.unfoldURIs(outputurilist));
	    		return syntacticFilter(service, inputurilist, outputurilist, semanticMatch(inputurilist, outputurilist));
	    	}
	    	else {
	    		return semanticMatch(inputurilist, outputurilist);
	    	}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		throw new MatchingException(e.toString());
    	}
    }
    
    /**
     * @see de.dfki.owlsmx.Matchmaker#matchRequest(java.lang.String)
     */
    public SortedSet matchRequest(String profile) throws MatchingException {
    	// create input stream from profile string
    	InputStream profileStream = new ByteArrayInputStream(profile.getBytes());
    	// pass input stream to the appropriate matchRequest() method
    	return matchRequest(profileStream);
    }
    
    /* (non-Javadoc)
     * @see owlsmx.Matchmaker#removeService(java.lang.Integer)
     */
    public void removeService(Integer integer) {
        registry.removeService(integer.intValue());      
    }

    /* (non-Javadoc)
     * @see owlsmx.Matchmaker#load()
     */
    public boolean load() {
        try  {
        	boolean result = LocalOntologyContainer.load();
            File file = new File("registry.data");
        	if (file.exists()) {
            	ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            	registry = (ConceptServiceRegistry) in.readObject();
            	in.close();
        	}
        	else return false;
        	return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            ErrorLog.instanceOf().report(e.toString());             
        } 
    	return false;
    }
    
    /* (non-Javadoc)
     * @see owlsmx.Matchmaker#save()
     */
    public boolean save() {    	
    	try {            
        	boolean result = localOntologyContainer.save();
	        ObjectOutput out = new ObjectOutputStream(new FileOutputStream("registry.data"));
	        out.writeObject(registry);
	        out.close();
	        return result;
        }
        catch (FileNotFoundException e) {
        	e.printStackTrace();
            return false;   
        }
        catch (IOException e) {
        	e.printStackTrace();
            return false;   
        }                
    }
    
    /* (non-Javadoc)
     * @see owlsmx.SimilarityMatchmaker#clear()
     */
    public void clear() {
//        File file = new File("registry.data");        
//    	if (file.exists()) {
//        	file.delete();
//    	}
//    	file = new File("localOntology.owl");
//    	if (file.exists()) {
//    	    file.delete();
//    	}    	
    	ConceptServiceRegistry.instanceOf().clear();
//    	SimpleIndex.instanceOf().clear();
    	inputIndex.clear();
    	outputIndex.clear();
    }
    
    /**
     * Enables the support for profile hierarchies
     * 
     * 
     * @param speedyButProblematic disables general check if a profile is valid - hence profile hierarchies don't cause problems but might lead to secondary problems
     */
    public void enableProfileHierarchies(boolean speedyButProblematic) {
    	if (speedyButProblematic)
    		OWLConfig.setStrictConversion(false);
    	else
    		base.setReasoner("RDFS");
    }
    
    /**
     * Disables the support for profile hierarchies
     */
    public void disableProfileHierarchies() {
    	base.setReasoner(null);
		OWLConfig.setStrictConversion(true);
    }

	public boolean isIntegrative() {
		return integrative;
	}

	public void setIntegrative(boolean integrative) {
		this.integrative = integrative;
	}
    
}

