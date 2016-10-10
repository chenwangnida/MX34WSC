package de.dfki.wsdlanalyzer.parser;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * parser creates a dom-tree from wsdl-file
 * from which our internal datastructure is created
 */
public class Parser 
{
//	create dom-tree from wsdl file
	private Document document;
	
	public Parser(String filename)
	{
		//for validating
		//final String schemaSource = "XMLSchema.xsd";
		final String schemaSource = "WsdlSchema.xsd";
		final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
		final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
		final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
		
		//System.out.println("*** WsdlParser ***\n");
		// Step 1: create a DocumentBuilderFactory and configure it
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		 // Set namespaceAware to true to get a DOM Level 2 tree with nodes
        // containing namesapce information.  This is necessary because the
        // default value from JAXP 1.0 was defined to be false.
        dbf.setNamespaceAware(true);

        // Set the validation mode to either: no validation
		//dbf.setValidating(true);
		dbf.setValidating(false);
		
		//set parser attributes
		dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		dbf.setAttribute(JAXP_SCHEMA_SOURCE,new File(schemaSource));
		
		//Step 2: create a DocumentBuilder that satisfies the constraints
        // specified by the DocumentBuilderFactory
        DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			db.setErrorHandler(new WsdlErrorHandler());
	        // Step 3: parse the input file
	        document = db.parse(new File(filename));

	        
		} 
		catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
	}
	
	public Parser(InputSource content)
	{
		//for validating
		//final String schemaSource = "XMLSchema.xsd";
		final String schemaSource = "WsdlSchema.xsd";
		final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
		final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
		final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
		
		//System.out.println("*** WsdlParser ***\n");
		// Step 1: create a DocumentBuilderFactory and configure it
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		 // Set namespaceAware to true to get a DOM Level 2 tree with nodes
        // containing namesapce information.  This is necessary because the
        // default value from JAXP 1.0 was defined to be false.
        dbf.setNamespaceAware(true);

        // Set the validation mode to either: no validation
		//dbf.setValidating(true);
		dbf.setValidating(false);
		
		//set parser attributes
		dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		dbf.setAttribute(JAXP_SCHEMA_SOURCE,new File(schemaSource));
		
		//Step 2: create a DocumentBuilder that satisfies the constraints
        // specified by the DocumentBuilderFactory
        DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			db.setErrorHandler(new WsdlErrorHandler());
	        // Step 3: parse the input file
	        document = db.parse(content);

	        
		} 
		catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
	}
	
	public Document getDomTree()
	{
		return document;
	}
}
