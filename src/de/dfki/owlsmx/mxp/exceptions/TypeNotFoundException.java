package de.dfki.owlsmx.mxp.exceptions;

/**
 * Is thrown if anything goes wrong during parsing of
 * a WSDL file
 * 
 * @author Patrick Kapahnke
 *
 */
public class TypeNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6684394850283948592L;

	/**
     * 
     */
    public TypeNotFoundException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public TypeNotFoundException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public TypeNotFoundException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     */
    public TypeNotFoundException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

}