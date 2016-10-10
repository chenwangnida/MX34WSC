/*
 * Created on 05.05.2005
 * 
 */
package de.dfki.owlsmx.reasoning;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.mindswap.owl.OWLClass;
import org.mindswap.pellet.TuBox.NotUnfoldableException;
import org.mindswap.pellet.jena.OWLReasoner;

import aterm.ATerm;
import aterm.ATermAppl;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import de.dfki.owlsmx.exceptions.ConceptNotFoundException;
import de.dfki.owlsmx.exceptions.MatchingException;
import de.dfki.owlsmx.io.ErrorLog;
import de.dfki.owlsmx.utils.MatchmakerUtils;

/**
 * An extension of the Pellet reasoner which includes additional functions
 * for unfolding, communication with Mindswap OWL-S API and and serveral
 * convinience functions for OWLS-MX
 * 
 * @author Ben Fries
 *
 */
public class PelletReasoner  extends OWLReasoner {

	/**
	 * Constructor, intitializes superclass OWLReasoner
	 */
	public PelletReasoner() {
		super();		
    }
	
	/**
	 * Returns all classes in the local ontology
	 * 
	 * @return	Set of all RDFNodes in the ontology
	 */
	public java.util.Set getAllClasses() {
		return getClasses();
	}
	
	/**
	 * Returns the descendent classes of a given class
	 * 
	 * @param clazz		OntClass whose descendent classes should be computed
	 * @return			Set of RDFNodes
	 */
	private java.util.Set getDescendantClasses(OntClass clazz) {
		return MatchmakerUtils.union(getSubClasses(clazz));
	}
	
	/**
	 * Returns the immediate subclasse(most generic subsumed concepts) of a given class
	 * 
	 * @param clazz		OntClass of which the subclasses should be computed
	 * @return			Set of RDFNode items
	 */
	private java.util.Set getDirectSubClasses(OntClass clazz) {
		return MatchmakerUtils.union(getSubClasses(clazz,true));
	}
	
	/**
	 * Returns the ancestor classes of a given class in the local ontology
	 * 
	 * @param clazz		OntClass whose ancestor classes should be computed
	 * @return			Set of RDFNodes
	 */
	private java.util.Set getAncestorClasses(OntClass clazz) {
		return MatchmakerUtils.union(getSuperClasses(clazz));
	}
	
	/**
	 * Computes immediate parents of a given class
	 * 
	 * @param clazz		OntClass whose immediate parent classes should be computed
	 * @return			Set of RDFNodes
	 */
	private java.util.Set getDirectParentClasses(OntClass clazz) {
		return MatchmakerUtils.union(getSuperClasses(clazz,true));	
	}	
	
	/**
	 * Computes the equivalent classes of a given clas
	 * 
	 * @param clazz		OntClass whose equivalent classes should be computed
	 * @return			Set of RDFNodes
	 */
	private java.util.Set getAllEquivalentClasses(OntClass clazz) {
		return MatchmakerUtils.union(getEquivalentClasses(clazz));
	}	
	
	/**
	 * If concept exists in the reasoner
	 * 
	 * @param node	RDFNode of the concept
	 * @return		if the concept exists
	 */
	public boolean conceptExists(RDFNode node) {
		return getClasses().contains(node);
	}
	
	/**
	 * @param clazzURI			the class which is questioned whether it exists or not
	 * @return 					returns if the Class already exists inside the reasoner
	 */
	public boolean conceptExists(URI clazzURI) {
		RDFNode node;    	
    	for(Iterator iter = getClasses().iterator();iter.hasNext();) {
    		node = (RDFNode)iter.next();
    		if (node.toString().toLowerCase().equals(clazzURI.toString().toLowerCase()) ) {
    			return true;
    		}    		
    	}
    	return false;
	}
	
	/**
	 * Retrieves the RDFNode of a the class with the given URI
	 * 
	 * @param clazzURI		URI of the class that should be retrieve(String)
	 * @return				RDFNode of the class
	 * @throws ConceptNotFoundException
	 */
	public RDFNode getRDFNode(URI clazzURI) throws ConceptNotFoundException {
    	RDFNode node;
    	Iterator iter = getClasses().iterator();
    	while(iter.hasNext()) {
    		node = (RDFNode)iter.next();    		
    		if (node.toString().toLowerCase().equals(clazzURI.toString().toLowerCase()) )
    			return node;
    	}
    	Resource r = getModel().getResource(clazzURI.toString());
    	if (r!=null)
    		return r.as(RDFNode.class);
    	throw new ConceptNotFoundException(this.getClass().toString() + "|getRDFNode : Could not load class " + clazzURI.toString());
    	
	}
	
	/**
	 * Unfolds a concept with a given URI
	 * 
	 * @param clazzURI		URI of the concept
	 * @return				String of th e unfolded concept
	 * @throws NotUnfoldableException		If the concept can't be unfolde
	 * @throws ConceptNotFoundException		If the concept can't be found
	 * @throws MatchingException			If anything else goes wrong
	 */
	public String unfoldTerm(URI clazzURI) throws NotUnfoldableException, ConceptNotFoundException, MatchingException{
		return unfoldTerm(getRDFNode(clazzURI));
	}
	
    /**
     * Unfolds a Jena OntClass in the local ontology
     * 
     * @param clazz						the class to unfold
     * @return							returns the unfolded String of the input class clazz
     * @throws NotUnfoldableException	if the TBox can't be unfolded
     */
    public String unfoldTerm(OntClass clazz) throws NotUnfoldableException,MatchingException {
    	try {
    		return unfoldTerm(new URI(clazz.getURI()));
		} catch (ConceptNotFoundException e) {		
			e.printStackTrace();
			throw new MatchingException(e.getMessage());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new MatchingException(e.getMessage());
		}
    }
    
    /**
     * Unfolds a Vector of URIs
     * 
     * @param uris						Vector with STRINGs that are URI.toString()
     * @return							STRING composed of whitespace seperated unfolded Classes
     * @throws NotUnfoldableException	if ANY Term can't be unfolded
     * @throws URISyntaxException		if the STRINGs are malformed for a URI
     */
    public String unfoldURIs(Vector uris) throws NotUnfoldableException,MatchingException, URISyntaxException {        
        String result ="";
        URI uri;
        for (int i=0; i < uris.size(); i++) {
        	uri = (URI)uris.get(i);
        	if (conceptExists(uri))
        		result += unfoldTerm(getRDFNode(uri));
        	else
        		System.out.println("Concept " + uri + " did not exist in reasoner");
        }
//        System.out.println(this.getClass().toString() + "|unfoldURIS: " + result);
        return result.trim();
    }

    /**
     * Retrieves the OntClass of a given class from the reasoner
     * 
     * @param stringURI					URI of the class
     * @return							Desired OntClass
     * @throws URISyntaxException		If String was not a valid URI
     * @throws ConceptNotFoundException	If the concept can't be found
     */
    public OntClass getClass(String stringURI) throws URISyntaxException, ConceptNotFoundException {
    	return getClass(new URI(stringURI));		
    }
    
    /**
     * Retrieves the OntClass of a given class from the reasoner
     * 
     * @param uri					URI of the class
     * @return							Desired OntClass
     * @throws URISyntaxException		If String was not a valid URI
     * @throws ConceptNotFoundException	If the concept can't be found
     */
    public OntClass getClass(URI uri) throws URISyntaxException, ConceptNotFoundException {
    	if (uri==null)
    		throw new URISyntaxException("URI was not initialized correctly ","URI was not initialized correctly ");
    	return (OntClass)getRDFNode(uri).as(OntClass.class);
//    	throw new ConceptNotFoundException(this.getClass().toString() + "|getClass: Could not find class " + uri.toString() + " in local ontology");//+ localOntology.getURI().toString());
    }
        
    /**
     * Unfolds a Jena OntClass
     * 
     * @param clazz						the class to unfold
     * @return							returns the unfolded String of the input class clazz
     * @throws NotUnfoldableException	if the TBox can't be unfolded
     */
    public String unfoldTerm(RDFNode clazz) throws NotUnfoldableException,MatchingException {
        if ( getKB().getTBox().Tu==null) {
            getKB().getTBox().split();            
        	}
        ATerm term = node2term(clazz);
        if (term==null) {
            ErrorLog.instanceOf().report("Can't find Term for class " + clazz.toString());
            throw new MatchingException("Can't find Term for class " + clazz.toString());            
        }
        String result =( (ATerm) getKB().getTBox().Tu.unfoldTerm((ATermAppl) term) ).toString();
        return result;
    }
    
    /**
     * Unfolds a given concept in the local ontology
     * 
     * @param clazz		OntClass which should be unfolded
     * @return			String of the unfolded concept
     * @throws NotUnfoldableException		If the concept can't be unfolded in the ontology
     * @throws MatchingException			If anything else goes wrong
     */
    public String unfoldTerm(OWLClass clazz) throws NotUnfoldableException, MatchingException {
    	return unfoldTerm((OntClass)clazz.getImplementation());
    }
    
    /**
     * Retrieves the equivalent class for the given class
     * 
     * @param 	clazz	OntClass whose equivalent classes should be retrieved
     * @return	Set of RDFNode items
     */
    public Set retrieveEquivalentClasses(OntClass clazz)  {
    	Set result = this.getAllEquivalentClasses(clazz);
        result.add(clazz);
        return result;
    }
    
    /**
     * Returns the immediate parents (most specific ancestor)
     * 
     * @param clazz					OntClass of which the immediate parents should be retrieved from
     * @param equivalentClasses		Set of equivalent class (they will be pruned from the result)		
     * @return						Set of RDFNodes which are equivalent to classes
     */
    public Set retrieveDirectParentClasses(OntClass clazz, Set equivalentClasses) {
        Set result = this.getDirectParentClasses(clazz);
        result.removeAll(equivalentClasses);
        return result;
    }
    
    /**
	 * Returns the ancestor classes of a given class in the local ontology
	 * 
	 * @param clazz					OntClass whose ancestor classes should be computed
     * @param equivalentClasses		Set of equivalent classes (will be pruned)
     * @param directParentClasses	Set of parent classes (will be pruned)
	 * @return						Set of RDFNodes
     */
    public Set retrieveAncestorClasses(OntClass clazz, Set equivalentClasses , Set directParentClasses) {
        Set result = this.getAncestorClasses(clazz);
        result.removeAll(equivalentClasses);
        result.removeAll(directParentClasses);
        return result;
    }
    
	/**
	 * Returns the ancestor classes of a given class in the local ontology
	 * 
	 * @param clazz		OntClass whose ancestor classes should be computed
	 * @return			Set of RDFNodes
	 */
    public Set retrieveAllAncestorClasses(OntClass clazz) {
        return this.getAncestorClasses(clazz);
    }
    
    /**
	 * Computes children of a given class
	 * 
	 * @param clazz		OntClass whose children classes should be computed
	 * @return			Set of RDFNodes
     */
    public Set retrieveDirectSubClasses(OntClass clazz, Set equivalentClasses) {
        Set result = this.getDirectSubClasses(clazz);
        result.removeAll(equivalentClasses);
        return result;
    }
    
	/**
	 * Returns the descendent classes of a given class
	 * 
	 * @param clazz				OntClass whose descendent classes should be computed
     * @param equivalentClasses	Set of equivalent classes (will be pruned)
     * @param directSubClasses	Set of equivalent classes (will be pruned)
	 * @return					Set of RDFNodes
     */
    public Set retrieveDescendantClasses(OntClass clazz, Set equivalentClasses , Set directSubClasses) {
        Set result = this.getDescendantClasses(clazz);
        result.removeAll(directSubClasses);
        result.removeAll(equivalentClasses);
        return result;
    }
    
    /**
     * Retrieves the remaining classes 
     * (those who are in no subsumtion relation with a class)
     * 
     * @param clazz					OntClass whose descendent classes should be computed
     * @param equivalentClasses		Set of equivalent classes (will be pruned)
     * @param directSubClasses		Set of child classes (will be pruned)
     * @param directParentClasses	Set of parent classes (will be pruned)
     * @param AncestorClasses		Set of ancestor classes (will be pruned)
     * @param DecendantClasses		Set of descendent classes (will be pruned)
	 * @return					Set of RDFNodes
     */
    public Set retrieveRemainingClasses(OntClass clazz, Set equivalentClasses, Set directParentClasses, Set AncestorClasses, Set directSubClasses, Set DecendantClasses) {
        Set result = this.getAllClasses();
        result.removeAll(directParentClasses);
        result.removeAll(AncestorClasses);
        result.removeAll(DecendantClasses);
        result.removeAll(directSubClasses);
        return result;
    }
    
    /**
     * Retrieves the remaining classes 
     * (those who are in no subsumtion relation with a class)
     * @param clazz					OntClass whose descendent classes should be computed
     * @param equivalentClasses		Set of equivalent classes (will be pruned)
     * @param AllAncestorClasses	Set of ancestor classes (will be pruned)
	 * @return						Set of RDFNodes
     */
    public Set retrieveAllRemainingClasses(OntClass clazz, Set equivalentClasses, Set AllAncestorClasses) {
        Set result = this.getClasses();
        result.removeAll(AllAncestorClasses);
        result.removeAll(equivalentClasses);
        return result;
    }

}
