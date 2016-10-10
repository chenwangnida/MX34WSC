package de.dfki.wsdlanalyzer.parser;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class WsdlErrorHandler implements ErrorHandler 
{

	public void warning(SAXParseException arg0) throws SAXException 
	{
		System.out.println("\n###### Parser_Warning ######\n");
		System.out.println("line: "+arg0.getLineNumber()+" column: "+arg0.getColumnNumber());
		System.out.println("publicID: "+arg0.getPublicId()+" systemID: "+arg0.getSystemId());
		System.out.println("Message: "+arg0.getMessage());
	}

	public void error(SAXParseException arg0) throws SAXException 
	{
		System.out.println("\n###### Parser_Error ######\n");
		System.out.println("line: "+arg0.getLineNumber()+" column: "+arg0.getColumnNumber());
		System.out.println("publicID: "+arg0.getPublicId()+" systemID: "+arg0.getSystemId());
		System.out.println("Message: "+arg0.getMessage());
	}

	public void fatalError(SAXParseException arg0) throws SAXException 
	{
		System.out.println("\n###### Parser_Fatal_Error ######\n");
		System.out.println("line: "+arg0.getLineNumber()+" column: "+arg0.getColumnNumber());
		System.out.println("publicID: "+arg0.getPublicId()+" systemID: "+arg0.getSystemId());
		System.out.println("Message: "+arg0.getMessage());
	}

}
