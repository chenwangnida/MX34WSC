/*
 * Created on 08.01.2005
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
 * Is thrown if something happens during adding a class to the local ontology
 * 
 * @author Ben Fries
 *
 */
public class AddClassException extends MatchingException {

	private static final long serialVersionUID = -8089911108102944143L;

	/**
     * 
     */
    public AddClassException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public AddClassException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public AddClassException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }


    /**
     * @param arg0
     * @param arg1
     */
    public AddClassException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

}
