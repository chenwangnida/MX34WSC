/*
 * Created on 08.08.2006
 */
package de.dfki.owlsmx.mxp;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;


import org.mindswap.owl.OWLOntology;
import org.mindswap.owls.process.InputList;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.OutputList;
import org.mindswap.owls.process.Output;

import de.dfki.owlsmx.mxp.exceptions.OWLSParsingException;
import de.dfki.owlsmx.mxp.exceptions.TypeNotFoundException;
import de.dfki.owlsmx.mxp.exceptions.WsdlParsingException;
import de.dfki.wsdlanalyzer.parser.SimpleTypeLookupTable;


/**
 * This class provides the functionality of a datatype compatibility
 * checker for OWLS-MX on the level of WSDL groundings. It's static
 * methods can be used to directly check the matching between parameters
 * of a request and a candidate service.
 * If this class is instanced, services can be added to let OWLS-MXP
 * precompute all possible Input/Output compatibilities between all
 * parameters. This functionality is used for example in the interaction 
 * of the matchmaker agent and the composition planning agent to ensure
 * execution completeness for a computed service composition plan on
 * the grounding level. 
 * 
 * @author Patrick Kapahnke
 */
public class OWLSMXP {
	
	private SimpleTypeLookupTable simpleTypeLookupTable = new SimpleTypeLookupTable();
	private Map serviceInfos = new HashMap();
	private boolean locked = false;
	private OWLSMXPCompatibilityMatrix compatibilityMatrix = new OWLSMXPCompatibilityMatrix();
    
    public OWLSMXP() {
    }
    
    /**
     * This methods adds a service to OWLSMXP, for which it
     * automatically precomputes all possible IO datatype
     * compatibilities. The results of these computations
     * is then stored in a compatibility matrix.
     * @param service the service to add
     */
    public void addService(OWLOntology service) {
    	// TODO Exception o.Ä.
    	if(locked) return;
    	
    	// read in the service information
    	OWLSMXPServiceInformation serviceInfo = null;
    	try {
    		serviceInfo = new OWLSMXPServiceInformation(service, simpleTypeLookupTable);
    	}
    	catch(Exception e) {
    		// TODO
    		return;
    	}
    	
    	String serviceName = service.getService().getName();    	
    	// store the service info for following computations
    	serviceInfos.put(serviceName, serviceInfo);
    	
    	// find out inputs and outputs of the service
    	InputList inputList 	= service.getService().getProcess().getInputs();
    	OutputList outputList	= service.getService().getProcess().getOutputs();
    	
    	// offset is used to determine the correct position in the matrix
    	int offset = compatibilityMatrix.getNumberOfRows();
    	
    	// remember the inputs and prepare the compatibility matrix
    	for(int i = 0; i < inputList.size(); i++) {
    		String inputName = serviceName + "#" + ((Input) inputList.get(i)).getLocalName();
    		compatibilityMatrix.addRow(inputName);
    	}
 
    	// test every input of the new service against all outputs
    	for(int i = 0; i < inputList.size(); i++) {
    		String candidateParameterName = ((Input) inputList.get(i)).getLocalName();
    		for(int j = 0; j < compatibilityMatrix.getNumberOfColumns(); j++) {
    			String requestServiceName = compatibilityMatrix.getColumnName(j).split("#")[0];
    			String requestParameterName = compatibilityMatrix.getColumnName(j).split("#")[1];
    			OWLSMXPServiceInformation requestServiceInfo = (OWLSMXPServiceInformation) serviceInfos.get(requestServiceName);
    			// check type compatibility and store the result in the matrix
    			OWLSMXPTypeCompatibilityChecker checker = new OWLSMXPTypeCompatibilityChecker(simpleTypeLookupTable, requestServiceInfo, serviceInfo);
    			try {
    				boolean result = checker.matchOWLSParameterTypes(candidateParameterName, requestParameterName, true);
    				compatibilityMatrix.setEntry(offset + i, j, result);
    			}
    			catch(Exception e) {
    			}
    		}
    	}
    	
    	// offset is used to determine the correct position in the matrix
    	offset = compatibilityMatrix.getNumberOfColumns();
    	  	
    	// remember the outputs and prepare the compatibility matrix
    	for(int i = 0; i < outputList.size(); i++) {
    		String outputName = serviceName + "#" + ((Output) outputList.get(i)).getLocalName();
    		compatibilityMatrix.addColumn(outputName);
    	}
    	
    	// test every input against all outputs of the new service
    	for(int i = 0; i < compatibilityMatrix.getNumberOfRows(); i++) {
    		String candidateServiceName = compatibilityMatrix.getRowName(i).split("#")[0];
    		String candidateParameterName = compatibilityMatrix.getRowName(i).split("#")[1];
    		OWLSMXPServiceInformation candidateServiceInfo = (OWLSMXPServiceInformation) serviceInfos.get(candidateServiceName);
    		for(int j = 0; j < outputList.size(); j++) {
    			String requestParameterName = ((Output) outputList.get(j)).getLocalName();
    			// check type compatibility and store the result in the matrix
    			OWLSMXPTypeCompatibilityChecker checker = new OWLSMXPTypeCompatibilityChecker(simpleTypeLookupTable, serviceInfo, candidateServiceInfo);
    			try {
    				boolean result = checker.matchOWLSParameterTypes(candidateParameterName, requestParameterName, true);
    				compatibilityMatrix.setEntry(i, offset + j, result);
    			}
    			catch(Exception e) {
    			}  			
    		}
    	}
    }
    
    /**
     * Checks the compatibility of an output parameter of a service to
     * an input parameter of another service by fetching the pre-computed
     * compatibility information.
     * 
     * @param firstServiceName the name of the service, that produces the output
     * @param outputParameterName the name of the output parameter
     * @param secondServiceName the name of the service, whose input should be filled with information of the ouput of the other service
     * @param inputParameterName the name of the input parameter
     * @return true, if the compatibility check was positive in the precomputation
     */
    public boolean checkDatatypeCompatibility(String firstServiceName, String outputParameterName, String secondServiceName, String inputParameterName) {
    	// look up the precomputed value in the compatibility matrix
    	return compatibilityMatrix.getEntry(secondServiceName + "#" + inputParameterName, firstServiceName + "#" + outputParameterName).booleanValue();
    }
    
    /**
     * Returns the string representation of the contained compatibility matrix
     * 
     * @return string representation
     */
    public String toString() {
    	return compatibilityMatrix.toString();
    }
    
    /**
     * This method can be used to export the contained compatibility
     * matrix as byte array. 
     * 
     * @return a byte array representing the contained compatibility matrix object
     * @throws IOException if anything goes wrong during serialization
     */
    public byte[] exportCompatibilityMatrix() throws IOException {
    	try {
    		// serialize the object to a byte array output stream
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		ObjectOutputStream oos = new ObjectOutputStream(baos);
    		oos.writeObject(compatibilityMatrix);
    		return baos.toByteArray();
    	}
    	catch(IOException e) {
    		throw e;
    	}
    }
    
    /**
     * This method can be used to import a compatibility matrix, which can
     * be passed to the method as serialized object (as returned by the
     * export method). As soon as a compatibility matrix is imported,
     * a "locked" flag is set to true to disallow the adding of new services,
     * since the service information, which would be necessary to compute the
     * new compatibility values, is lost during export/import.
     * 
     * @param serializedCompatibilityMatrix the byte array containing the serialized compatibility matrix
     * @throws IOException if anything goes wrong during de-serialization
     * @throws Exception
     */
    public void importCompatibilityMatrix(byte[] serializedCompatibilityMatrix) throws IOException, Exception {
    	try {
    		// de-serialize the object using a byte array input stream
    		ByteArrayInputStream bais = new ByteArrayInputStream(serializedCompatibilityMatrix);
    		ObjectInputStream ois = new ObjectInputStream(bais);
    		locked = true;
    		compatibilityMatrix = (OWLSMXPCompatibilityMatrix) ois.readObject();
    	}
    	catch(IOException e) {
    		throw e;
    	}
    	catch(Exception e) {
    		throw e;
    	}
    }
        
    /**
     * This static method tests, given a request, a candidate and 
     * a mapping of request parameters to candidate parameters, if
     * every mapped parameter has compatible types in the underlying
     * WSDL grounding. It throws an IOException, if a WSDL file
     * could not be read for any reason, a WsdlParsingException if
     * anything went wrong during the parsing of the WSDL files and 
     * a TypeNotFoundException, if no relation between a OWLS Parameter 
     * and a WSDL Type could be found.
     * 
     * @param request a OWLOntology containing the request service
     * @param candidate a OWLOntology containing the candidate service
     * @param parameterMapping a mapping between OWL-S request parameters and OWL-S candidate parameters
     * @return true, if the type of every parameter mapping is compatible in the grounding 
     * @throws IOException if a WSDL file could not be read
     * @throws WsdlParsingException if something went wrong durch parsing of the WSDL files
     * @throws TypeNotFoundException if a WSDL type could not be found for an OWL-S parameter
     */
    public static boolean matchTypes(OWLOntology request, OWLOntology candidate, Map parameterMapping) throws IOException, TypeNotFoundException {
    	// read in the service informations for candidate and request
    	SimpleTypeLookupTable simpleTypeLookupTable = new SimpleTypeLookupTable();    	
    	OWLSMXPServiceInformation requestInfo;
    	OWLSMXPServiceInformation candidateInfo;	
    	try {
    		requestInfo		= new OWLSMXPServiceInformation(request, simpleTypeLookupTable);
    		candidateInfo	= new OWLSMXPServiceInformation(candidate, simpleTypeLookupTable);
    	}
    	catch(IOException e) {
    		throw e;
    	}
    	catch(WsdlParsingException e) {
    		return false;
    	}
    	catch(OWLSParsingException e) {
    		return false;
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	    	
    	// compare every parameter datatype with it's correspondent as defined in the parameter mapping
    	Entry entry;
    	String requestParameter;
    	String candidateParameter;
    	Iterator iterator = parameterMapping.entrySet().iterator();
    	boolean success = true;
    	OWLSMXPTypeCompatibilityChecker checker = new OWLSMXPTypeCompatibilityChecker(simpleTypeLookupTable, requestInfo, candidateInfo);
    	while(iterator.hasNext()) {
    		entry = (Entry) iterator.next();
    		requestParameter	= (String) entry.getKey();
    		candidateParameter	= (String) entry.getValue();
    		boolean isInput = requestInfo.isInput(requestParameter);
    		boolean result = checker.matchOWLSParameterTypes(candidateParameter, requestParameter, isInput);   		
    		if(!result) {
    			// incompatible types, so finish the checking process
    			success = false;
    			break;
    		}
    	}
    	
    	// passed all tests
        return success;
    }
   
    /**
     * This static method tests, given a request and a candidate, if
     * every parameter has a compatible parameter in the underlying
     * WSDL grounding. It throws an IOException, if a WSDL file
     * could not be read for any reason, a WsdlParsingException if
     * anything went wrong during the parsing of the WSDL files and 
     * a TypeNotFoundException, if no relation between a OWLS Parameter 
     * and a WSDL Type could be found.
     * 
     * @param request a OWLOntology containing the request service
     * @param candidate a OWLOntology containing the candidate service
     * @return true, if the type of every parameter in the candidate service has a compatible type in the grounding of the request service  
     * @throws IOException if a WSDL file could not be read
     * @throws WsdlParsingException if something went wrong durch parsing of the WSDL files
     * @throws TypeNotFoundException if a WSDL type could not be found for an OWL-S parameter
     */
    public static boolean matchTypes(OWLOntology request, OWLOntology candidate) throws IOException, TypeNotFoundException {
    	// read in the service informations for candidate and request
    	SimpleTypeLookupTable simpleTypeLookupTable = new SimpleTypeLookupTable();    	
    	OWLSMXPServiceInformation requestInfo;
    	OWLSMXPServiceInformation candidateInfo;  	
    	try {
    		requestInfo		= new OWLSMXPServiceInformation(request, simpleTypeLookupTable);
    		candidateInfo	= new OWLSMXPServiceInformation(candidate, simpleTypeLookupTable);
    	}
    	catch(IOException e) {
    		throw e;
    	}
    	catch(WsdlParsingException e) {
    		return false;
    	}
    	catch(OWLSParsingException e) {
    		return false;
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	
    	// test every input of the candidate against every input of the request and remember
    	// all possible mappings
    	OWLSMXPTypeCompatibilityChecker checker = new OWLSMXPTypeCompatibilityChecker(simpleTypeLookupTable, requestInfo, candidateInfo);
    	InputList candidateInputs	= candidate.getService().getProcess().getInputs();
    	InputList requestInputs		= request.getService().getProcess().getInputs();
    	OWLSMXPMapping mapping = new OWLSMXPMapping(candidateInputs.size(), requestInputs.size());
    	for(int i = 0; i < candidateInputs.size(); i++) {
    		String candidateInputName = ((Input) candidateInputs.get(i)).getLocalName();
    		for(int j = 0; j < requestInputs.size(); j++) {
    			String requestInputName = ((Input) requestInputs.get(j)).getLocalName();
    			// test for compatibility and remember the possible mapping, if the test succeeds
    			boolean result = checker.matchOWLSParameterTypes(candidateInputName, requestInputName, true);
    			if(result) mapping.addPossibleMappingElement(i, j);
    		}
    	}
    	// check, if a mapping exists for every parameter
    	if(!mapping.existsMapping()) return false;
    	
    	// test every output of the request against ever output of the candidate and remember
    	// all possible mappings
    	OutputList candidateOutputs		= candidate.getService().getProcess().getOutputs();
    	OutputList requestOutputs		= request.getService().getProcess().getOutputs();
    	mapping = new OWLSMXPMapping(requestOutputs.size(), candidateOutputs.size());
    	for(int i = 0; i < requestOutputs.size(); i++) {
    		String requestOutputName = ((Output) requestOutputs.get(i)).getLocalName();
    		for(int j = 0; j < candidateOutputs.size(); j++) {
    			String candidateOutputName = ((Output) candidateOutputs.get(j)).getLocalName();
    			// test for compatibility and remember the possible mapping, if the test succeeds
    			boolean result = checker.matchOWLSParameterTypes(candidateOutputName, requestOutputName, false);
    			if(result) mapping.addPossibleMappingElement(i, j);
    		}
    	}
    	// check, if a mapping exists for every parameter
    	if(!mapping.existsMapping()) return false;

    	// passed all tests
    	return true;
    }
}
