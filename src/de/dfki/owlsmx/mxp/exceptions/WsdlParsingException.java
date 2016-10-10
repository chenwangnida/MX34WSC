package de.dfki.owlsmx.mxp.exceptions;

/**
 * Is thrown if anything goes wrong during parsing of
 * a WSDL file
 * 
 * @author Patrick Kapahnke
 *
 */
public class WsdlParsingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6684513939572059283L;

	/**
     * 
     */
    public WsdlParsingException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public WsdlParsingException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public WsdlParsingException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     */
    public WsdlParsingException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

}