package de.dfki.owlsmx.mxp;

import java.util.ArrayList;
import java.util.Iterator;

import de.dfki.owlsmx.mxp.exceptions.TypeNotFoundException;
import de.dfki.wsdlanalyzer.mapping.Mapping;
import de.dfki.wsdlanalyzer.parser.SimpleTypeLookupTable;
import de.dfki.wsdlanalyzer.types.Attribute;
import de.dfki.wsdlanalyzer.types.ComplexType;
import de.dfki.wsdlanalyzer.types.Element;
import de.dfki.wsdlanalyzer.types.WsdlFile;



/**
 * This class is for checking the compatibility of two WSDL
 * data types.
 * 
 * @author Patrick Kapahnke
 *
 */
public class OWLSMXPTypeCompatibilityChecker {

	private SimpleTypeLookupTable		simpleTypeLookupTable;
	private OWLSMXPServiceInformation	requestService;
	private OWLSMXPServiceInformation	candidateService;
	private WsdlFile					requestFile;
	private WsdlFile					candidateFile;
	
	/**
	 * Constructor takes a filled SimpleTypeLookupTable and the
	 * OWLSMXPServiceInformation for the request and the candidate.
	 * 
	 * @param simpleTypeLookupTable a previously filled SimpleTypeLookupTable
	 * @param requestService the service information for the request
	 * @param candidateService the service information for the candidate
	 */
	OWLSMXPTypeCompatibilityChecker(SimpleTypeLookupTable simpleTypeLookupTable, OWLSMXPServiceInformation requestService, OWLSMXPServiceInformation candidateService) {
		this.simpleTypeLookupTable	= simpleTypeLookupTable;
		this.requestService			= requestService;
		this.candidateService		= candidateService;
		requestFile					= requestService.getWsdlFile();
		candidateFile				= candidateService.getWsdlFile();
	}
		
	/**
	 * Tests if the simple candidate type matches the simple request type.
	 * 
	 * @param candidateType the name of the candidate type
	 * @param requestType the name of the request type
	 * @return true if the simple datatypes match
	 */
	public boolean matchSimpleTypes(String candidateType, String requestType) {
		int matchingScore = simpleTypeLookupTable.getMatchingScore(candidateType, requestType);
		// table entries with a value of 1 stand for direct or
		// indirect compatibility of simple datatypes 
		if(matchingScore == 1) return true;
		return false;
	}
	
	/**
	 * Tests if a complex candidate type matches the simple request type.
	 * It always returns false in this implementation.
	 * 
	 * @param candidateType the complex type of the candidate
	 * @param requestType the name of the simple type of the request
	 * @return false
	 */
	public boolean matchMixedTypes(ComplexType candidateType, String requestType) {
		return false;
	}

	/**
	 * Tests if the simple candidate type matches the complex request type.
	 * It always returns false in this implementation.
	 * 
	 * @param candidateType the name of the simple type of the candidate
	 * @param requestType the complex type of the request
	 * @return false
	 */
	public boolean matchMixedTypes(String candidateType, ComplexType requestType) {
		return false;
	}
	
	/**
	 * Matches a complex candidate type against a complex request type by first checking
	 * for equal grouping styles (sequence, choice, all) and then recursing for every
	 * element and required attribute.
	 * 
	 * @param candidateType the complex type of the candidate
	 * @param requestType the complex type of the request
	 * @param isInput if the types are used by input parameters
	 * @return true, if the candidate matches the request
	 */
	public boolean matchComplexTypes(ComplexType candidateType, ComplexType requestType, boolean isInput) {
		// test for equality of structure types (sequence, choice, all)
		if(candidateType.getGrouping() != candidateType.getGrouping()) return false;
		
		// compute all possible mappings between request and candidate attributes
		// and store them in a score map
		Iterator candidateAttributeIter	= candidateType.attributeIterator();
		Iterator requestAttributeIter	= requestType.attributeIterator();
		// fetch all request attributes that are "required"
		// and store them in a new list
		ArrayList requestAttributes = new ArrayList();
		while(requestAttributeIter.hasNext()) {
			Attribute requestAttribute = (Attribute) requestAttributeIter.next();
			if(requestAttribute.getUse() != null && requestAttribute.getUse().equals("required"))
				requestAttributes.add(requestAttribute);
		}
		// filter all candidate attributes that are "prohibited"
		ArrayList candidateAttributes = new ArrayList();
		while(candidateAttributeIter.hasNext()) {
			Attribute candidateAttribute = (Attribute) candidateAttributeIter.next();
			if(candidateAttribute.getUse() == null || !candidateAttribute.getUse().equals("prohibited"))
				candidateAttributes.add(candidateAttribute);
		}	
		OWLSMXPMapping mapping = new OWLSMXPMapping(requestAttributes.size(), candidateAttributes.size());
		for(int i = 0; i < requestAttributes.size(); i++) {
			// find out request attribute datatype
			Attribute requestAttribute = (Attribute) requestAttributes.get(i);
			String requestAttributeType = requestAttribute.getType();
			for(int j = 0; j < candidateAttributes.size(); j++) {
				// find out candidate attribute datatype
				Attribute candidateAttribute = (Attribute) candidateAttributes.get(j);
				String candidateAttributeType = candidateAttribute.getType();
				// match data types
				boolean result = matchSimpleTypes(candidateAttributeType, requestAttributeType);
				// store the possible mapping
				if(result) mapping.addPossibleMappingElement(i, j);
			}
		}
		// test if a mapping exists
		if(!mapping.existsMapping()) return false;
				
		// compute all possible mappings between request and candidate elements
		// and store them in a score map
		ArrayList candidateElements	= candidateType.getElementList();
		ArrayList requestElements	= requestType.getElementList();
		mapping = new OWLSMXPMapping(requestElements.size(), candidateElements.size());
		for(int i = 0; i < requestElements.size(); i++) {
			// find out request element datatype
			Element requestElement = (Element) requestElements.get(i);
			String requestElementType = requestElement.getType();
			for(int j = 0; j < candidateElements.size(); j++) {
				// find out candidate element datatype
				Element candidateElement = (Element) candidateElements.get(j);
				String candidateElementType = candidateElement.getType();
				// call matchTypes recursivly
				boolean result = matchTypes(candidateElementType, requestElementType, isInput);
				// store the possible mapping
				if(result) mapping.addPossibleMappingElement(i, j);
			}
		}
		// test if a mapping exists
		if(!mapping.existsMapping()) return false;
		
		// passed all tests
		return true;
	}
	
	/**
	 * Matches two WSDL parameters by specifiing their type name.
	 * This method automatically checks, if the types are simple or
	 * complex and calls the appropriate method for each case.
	 * 
	 * @param parameter1 the name of the candidate parameter
	 * @param parameter2 the name of the request parameter
	 * @param isInput if the parameters are used as inputs
	 * @return true if parameter1 matches parameter2
	 */
	public boolean matchTypes(String parameter1, String parameter2, boolean isInput) {
		// check if parameters have simple or complex types
		boolean parameter1HasSimpleType	= false;
		boolean parameter2HasSimpleType	= false;
		if(simpleTypeLookupTable.lookupSimpleType(parameter1)) parameter1HasSimpleType = true;
		if(simpleTypeLookupTable.lookupSimpleType(parameter2)) parameter2HasSimpleType = true;
		
		// simple - simple
		if(parameter1HasSimpleType && parameter2HasSimpleType) {
			return matchSimpleTypes(parameter1, parameter2);
		}
		
		// simple - complex
		if(parameter1HasSimpleType && !parameter2HasSimpleType) {
			ComplexType parameter2Type;
			if(isInput)
				parameter2Type = candidateFile.getTypeList().getType(parameter2);
			else
				parameter2Type = requestFile.getTypeList().getType(parameter2);
			return matchMixedTypes(parameter1, parameter2Type);
		}
		
		// complex - simple
		if(!parameter1HasSimpleType && parameter2HasSimpleType) {
			ComplexType parameter1Type;
			if(isInput)
				parameter1Type = requestFile.getTypeList().getType(parameter1);
			else
				parameter1Type = candidateFile.getTypeList().getType(parameter1);
			return matchMixedTypes(parameter1Type, parameter2);
		}		

		// complex - complex
		if(!parameter1HasSimpleType && !parameter2HasSimpleType) {
			ComplexType parameter1Type;
			ComplexType parameter2Type;
			if(isInput) {
				parameter1Type = requestFile.getTypeList().getType(parameter1);
				parameter2Type = candidateFile.getTypeList().getType(parameter2);
			}
			else {
				parameter1Type = candidateFile.getTypeList().getType(parameter1);
				parameter2Type = requestFile.getTypeList().getType(parameter2);				
			}
			return matchComplexTypes(parameter1Type, parameter2Type, isInput);
		}

		// error
		return false;
	}
	
	/**
	 * Matches the candidate parameter against the request parameter. Both parameter
	 * are specified by their OWL-S parameter name. The method automatically finds
	 * out their corresponding WSDL representation and passes it to the matchTypes
	 * method.
	 * 
	 * @param candidateParameter the OWL-S candidate parameter
	 * @param requestParameter the OWL-S request parameter
	 * @param isInput if the parameters are used as inputs
	 * @return true, if the candidate matches the request
	 * @throws TypeNotFoundException if no corresponding WSDL type could be found for one of the OWL-S parameter names
	 */
	public boolean matchOWLSParameterTypes(String candidateParameter, String requestParameter, boolean isInput) throws TypeNotFoundException {
		String parameter1Type;
		String parameter2Type;
		
		if(isInput) {
			parameter2Type	= candidateService.getTypeName(candidateParameter);
			parameter1Type	= requestService.getTypeName(requestParameter);
		}
		else {
			parameter1Type	= candidateService.getTypeName(candidateParameter);
			parameter2Type	= requestService.getTypeName(requestParameter);
		}
		
		if(parameter1Type == null || parameter2Type == null) throw new TypeNotFoundException();
		
		return matchTypes(parameter1Type, parameter2Type, isInput);
	}
}
