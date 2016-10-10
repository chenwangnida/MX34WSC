/*
 * Created on 17.11.2004
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
package de.dfki.owlsmx.exceptions;

/**
 * Is thrown if anything goes wrong during the matching process 
 * (Ancestor of all exceptions that occur in the matchmaker)
 * 
 * @author Benedikt Fries
 *
 */
public class MatchingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6684513929120055253L;

	/**
     * 
     */
    public MatchingException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public MatchingException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public MatchingException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     */
    public MatchingException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

}
