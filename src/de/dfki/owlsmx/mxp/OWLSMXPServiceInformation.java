package de.dfki.owlsmx.mxp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.net.URL;

import org.mindswap.owl.OWLOntology;
import org.mindswap.owls.grounding.Grounding;
import org.mindswap.owls.grounding.MessageMapList;
import org.mindswap.owls.grounding.MessageMap;
import org.mindswap.owls.grounding.WSDLAtomicGrounding;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.service.Service;

import de.dfki.owlsmx.mxp.exceptions.OWLSParsingException;
import de.dfki.owlsmx.mxp.exceptions.TypeNotFoundException;
import de.dfki.owlsmx.mxp.exceptions.WsdlParsingException;
import de.dfki.wsdlanalyzer.parser.SimpleTypeLookupTable;
import de.dfki.wsdlanalyzer.parser.WsdlFileParser;
import de.dfki.wsdlanalyzer.types.ComplexType;
import de.dfki.wsdlanalyzer.types.Message;
import de.dfki.wsdlanalyzer.types.MessageParameter;
import de.dfki.wsdlanalyzer.types.WsdlFile;



/**
 * This class is for fetching all information relevant for OWLS-MXP
 * by parsing the corresponding WSDL file and finding out the
 * mapping between OWL-S parameters and WSDL messages. It uses
 * the OWL-S API from mindswap to access the service grounding
 * and the parser of the WSDL analyzer.
 * 
 * @author Patrick Kapahnke
 */
public class OWLSMXPServiceInformation {
	
	public static short SIMPLE_TYPE 					= 0;
	public static short COMPLEX_TYPE					= 1;
	
	private Map inputParameterTypes						= new HashMap();
	private Map outputParameterTypes					= new HashMap();
	private URL wsdlURL									= null;
	private WsdlFile wsdlFile							= null;
	private SimpleTypeLookupTable simpleTypeLookupTable;

	/**
	 * This constructor automatically fetches the relevant service
	 * information for OWLS-MXP.
	 * 
	 * @param service a OWLOntology object containing the service
	 * @param simpleTypeLookupTable the SimpleTypeLookupTable to fill
	 * @throws IOException if the corresponding WSDL file could not be read
	 * @throws WsdlParsingException if something goes wrong during parsing of the WSDL file
	 */
	public OWLSMXPServiceInformation(OWLOntology service, SimpleTypeLookupTable simpleTypeLookupTable) throws IOException, WsdlParsingException, OWLSParsingException {
		this.simpleTypeLookupTable = simpleTypeLookupTable;
		fetchServiceInformation(service);
	}
	
	/**
	 * This method is responsible for gathering all the relevant information.
	 * 
	 * @param owlsService a OWLOntology object containing the OWL-S service
	 * @throws IOException if the corresponding WSDL file could not be read
	 * @throws WsdlParsingException if something goes wrong during parsing of the WSDL file
	 */
	private void fetchServiceInformation(OWLOntology owlsService) throws IOException, WsdlParsingException, OWLSParsingException {
    	// read basic service information
		Service service;
		Process process;
		Grounding grounding;
		WSDLAtomicGrounding wsdlGrounding;
		try {
			service			= owlsService.getService();
			process			= service.getProcess();
			grounding		= service.getGrounding(); 
			wsdlGrounding 	= (WSDLAtomicGrounding) grounding.getAtomicGrounding((AtomicProcess) process);
		}
		catch(Exception e) {
			throw new OWLSParsingException();
		}
        
        // parse wsdl file
        // read the wsdl file and store it temporary
        wsdlURL = wsdlGrounding.getDescriptionURL();
        try {
        	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(wsdlURL.openStream()));
        	FileWriter fileWriter = new FileWriter("temp.wsdl");
        	String line = null;
        	while((line = bufferedReader.readLine()) != null) {
        		fileWriter.write(line + "\n");
        	}
        	bufferedReader.close();
        	fileWriter.close();
        }
        catch(IOException e) {
        	throw e;
        }
        // parse the stored wsdl file using the WSDL Analyzer parser
        WsdlFileParser wsdlParser = new WsdlFileParser("temp.wsdl", simpleTypeLookupTable);
        wsdlParser.parseWsdl();
        wsdlFile = wsdlParser.getWsdlfile();
        // delete the temp file
        File tempFile = new File("temp.wsdl");
        tempFile.delete();
        
        // compute the mapping between OWLS parameters and WSDL types
        String inputMessageName = wsdlGrounding.getInputMessage().toString();
        inputMessageName = inputMessageName.substring(inputMessageName.lastIndexOf('/') + 1);
        String outputMessageName = wsdlGrounding.getOutputMessage().toString();
        outputMessageName = outputMessageName.substring(outputMessageName.lastIndexOf('/') + 1);

        Message inputMessage			= wsdlFile.getMessageList().getMessage(inputMessageName);
        Message outputMessage			= wsdlFile.getMessageList().getMessage(outputMessageName);
        MessageMapList inputMapping		= wsdlGrounding.getInputMap();
        MessageMapList outputMapping	= wsdlGrounding.getOutputMap();
        
        MessageMap messageMap;
        String owlsParameterName;
        String messagePartName;
        MessageParameter messagePart;
        Iterator messagePartIterator;
        // input parameters
        Iterator mappingIterator = inputMapping.iterator();
        while(mappingIterator.hasNext()) {
        	messageMap = (MessageMap) mappingIterator.next();
        	owlsParameterName = messageMap.getOWLSParameter().getLocalName();
        	messagePartName = messageMap.getGroundingParameter();
        	messagePartName = messagePartName.substring(messagePartName.lastIndexOf('/') + 1);
        	messagePart = null;
        	messagePartIterator = inputMessage.getParameterList().iterator();
        	while(messagePartIterator.hasNext()) {
        		messagePart = (MessageParameter) messagePartIterator.next();
        		if(messagePart.getName().equals(messagePartName)) break;
        		messagePart = null;
        	}
        	if(messagePart == null) {
        		throw new WsdlParsingException();
        	}
        	inputParameterTypes.put(owlsParameterName, messagePart.getType());
        }
        // output parameters
        mappingIterator = outputMapping.iterator();
        while(mappingIterator.hasNext()) {
        	messageMap = (MessageMap) mappingIterator.next();
        	owlsParameterName = messageMap.getOWLSParameter().getLocalName();
        	messagePartName = messageMap.getGroundingParameter();
        	messagePartName = messagePartName.substring(messagePartName.lastIndexOf('/') + 1);
        	messagePart = null;
        	messagePartIterator = outputMessage.getParameterList().iterator();
        	while(messagePartIterator.hasNext()) {
        		messagePart = (MessageParameter) messagePartIterator.next();
        		if(messagePart.getName().equals(messagePartName)) break;
        		messagePart = null;
        	}
        	if(messagePart == null) {
        		throw new WsdlParsingException();
        	}
        	outputParameterTypes.put(owlsParameterName, messagePart.getType());
        }       
	}
	
	/**
	 * This method tests, if the XSL data type that is assigned to an
	 * OWL-S parameter is a simple type.
	 * 
	 * @param owlsParameter the name of the OWL-S parameter
	 * @return true if the corresponding XSL data type is a simple type
	 * @throws TypeNotFoundException if no mapping could be found for the OWL-S parameter
	 */
	public boolean hasSimpleType(String owlsParameter) throws TypeNotFoundException {
		if(getType(owlsParameter) == SIMPLE_TYPE) return true;
		return false;		
	}
	
	/**
	 * This method tests, if the XSL data type that is assigned to an
	 * OWL-S parameter is a complex type.
	 * 
	 * @param owlsParameter the name of the OWL-S parameter
	 * @return true if the corresponding XSL data type is a complex type
	 * @throws TypeNotFoundException if no mapping could be found for the OWL-S parameter
	 */
	public boolean hasComplexType(String owlsParameter) throws TypeNotFoundException {
		if(getType(owlsParameter) == COMPLEX_TYPE) return true;
		return false;
	}
	
	/**
	 * This method returns the XSL data type for an OWL-S service
	 * parameter. It uses the constants SIMPLE_TYPE and COMPLEX_TYPE
	 * as return falues.
	 * 
	 * @param owlsParameter the name of the OWL-S parameter
	 * @return SIMPLE_TYPE or COMPLEX_TYPE
	 * @throws TypeNotFoundException if no mapping could be found for the OWL-S parameter
	 */
	public short getType(String owlsParameter) throws TypeNotFoundException {
		String typeName = (String) inputParameterTypes.get(owlsParameter);
		if(typeName == null)
			typeName = (String) outputParameterTypes.get(owlsParameter);
		//System.out.println(owlsParameter);
		if(typeName == null) {
			throw new TypeNotFoundException();
		}
		if(simpleTypeLookupTable.lookupSimpleType(typeName)) return SIMPLE_TYPE;
		return COMPLEX_TYPE;		
	}
	
	/**
	 * This method returns the ComplexType (from the WSDL Analyzer)
	 * for a given OWL-S parameter, if it exists.
	 * 
	 * @param owlsParameter the name of the OWL-S parameter
	 * @return the complex type as returned by the WSDL Analyzer or null, if the type is not complex
	 * @throws TypeNotFoundException if no mapping could be found for the OWL-S parameter
	 */
	public ComplexType getComplexType(String owlsParameter) throws TypeNotFoundException {
		if(!hasComplexType(owlsParameter)) {
			return null;
		}
		
		String typeName = (String) inputParameterTypes.get(owlsParameter);
		if(typeName == null)
			typeName = (String) outputParameterTypes.get(owlsParameter);
		return wsdlFile.getTypeList().getType(typeName);
	}
	
	/**
	 * This method returns the name of the simple type
	 * for a given OWL-S parameter, if it exists.
	 * 
	 * @param owlsParameter the name of the OWL-S parameter
	 * @return the name of the simple type as returned by the WSDL Analyzer or null, if the type is not simple
	 * @throws TypeNotFoundException if no mapping could be found for the OWL-S parameter
	 */
	public String getSimpleType(String owlsParameter) throws TypeNotFoundException {
		if(!hasSimpleType(owlsParameter)) {
			return null;
		}
		
		String result = (String) inputParameterTypes.get(owlsParameter);
		if(result == null)
			result = (String) outputParameterTypes.get(owlsParameter);
		
		return result;
	}
	
	/**
	 * This method returns the WSDL type name for
	 * a given OWL-S parameter.
	 * 
	 * @param owlsParameter the name of the OWL-S parameter
	 * @return the name of the WSDL type or null, if the OWL-S parameter does not exist
	 */
	public String getTypeName(String owlsParameter) {
		String result = (String) inputParameterTypes.get(owlsParameter);
		if(result == null)
			result = (String) outputParameterTypes.get(owlsParameter);
		
		return result;
	}
	
	/**
	 * This method returns true, if the given OWL-S
	 * parameter exists in the file and is used as an
	 * input.
	 * 
	 * @param owlsParameter the name of the OWL-S parameter
	 * @return true, if the parameter exists and is an input
	 */
	public boolean isInput(String owlsParameter) {
		return inputParameterTypes.containsKey(owlsParameter);
	}
	
	/**
	 * This method returns true, if the given OWL-S
	 * parameter exists in the file and is used as an
	 * output.
	 * 
	 * @param owlsParameter the name of the OWL-S parameter
	 * @return true, if the parameter exists and is an output
	 */
	public boolean isOutput(String owlsParameter) {
		return outputParameterTypes.containsKey(owlsParameter);
	}
	
	/**
	 * This method returns the WsdlFile as returned by the
	 * WSDL Analyzer.
	 * 
	 * @return the corresponding WsdlFile
	 */
	public WsdlFile getWsdlFile() {
		return wsdlFile;
	}
	
	/**
	 * Returns a string representation of the service information.
	 * 
	 * @return string representation
	 */
	public String toString() {
		String result = "wsdl file: " + wsdlURL.toString() + "\n";
		result += "inputs:\n";
		Iterator iterator = inputParameterTypes.entrySet().iterator();
		Map.Entry entry;
		while(iterator.hasNext()) {
			entry = (Map.Entry) iterator.next();
			result += (String) entry.getKey() + " -> " + (String) entry.getValue() + " (";
			try {
				if(hasSimpleType((String) entry.getKey())) result += "simple type)\n";
				else result += "complex type)\n";
			}
			catch(Exception e) {
				result += "n/a)\n";
			}
		}
		result += "outputs:\n";
		iterator = outputParameterTypes.entrySet().iterator();
		while(iterator.hasNext()) {
			entry = (Map.Entry) iterator.next();
			result += (String) entry.getKey() + " -> " + (String) entry.getValue() + " (";
			try {
				if(hasSimpleType((String) entry.getKey())) result += "simple type)\n";
				else result += "complex type)\n";
			}
			catch(Exception e) {
				result += "n/a)\n";
			}
		}	
		return result;
	}
}
