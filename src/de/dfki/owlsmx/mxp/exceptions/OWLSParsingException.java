package de.dfki.owlsmx.mxp.exceptions;

/**
 * Is thrown if anything goes wrong during parsing of
 * a the OWLS file
 * 
 * @author Patrick Kapahnke
 *
 */
public class OWLSParsingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6684513931272059283L;

	/**
     * 
     */
    public OWLSParsingException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public OWLSParsingException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public OWLSParsingException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     */
    public OWLSParsingException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }
}
