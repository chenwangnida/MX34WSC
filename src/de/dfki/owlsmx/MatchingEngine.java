/*
 * Created on 20.10.2004
 * OWL-S Matchmaker
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
package de.dfki.owlsmx;

import java.io.InputStream;
import java.net.URI;
import java.util.SortedSet;

import de.dfki.owlsmx.exceptions.MatchingException;



/**
 * MatchingEngine: Interface with general functions for matchmaking
 * 
 * Changes: 24.05.2006 - Patrick Kapahnke - added support for passing services and service requests not
 * only as URI, but also as stream or string.
 * 
 * @author bEn Fries
 *
 */
public interface MatchingEngine {    
    
    /**
     * Registers the advertised service with the given URI at the MatchingEngine
     * 
     * @param profileURI	URI of the service that should be registered
     */
    void addService(URI profileURI);
	
    /**
     * 24.05.2006
     * 
     * Registers the advertised service specified by a string at the MatchingEngine
     * 
     * @param profile		service profile in string format that should be registered
     * @param baseURI		the base URI
     */
    void addService(String profile, URI baseURI);
    
    /**
     * 24.05.2006
     * 
     * Registers the advertised service provided via a input stream at the MatchingEngine
     * 
     * @param profileStream	service profile as input stream that should be registered
     * @param baseURI		the base URI
     */
    void addService(java.io.InputStream profileStream, URI baseURI);
    

    /**
     * Removes the service with the given URI
     * 
     * @param uri	URI of the service that should be removed
     */
    void removeService(java.net.URI uri);
    
    /**
     * Retrieves advertisements that are relevant for a given service request
     * 
     * @param profileURI URI of the service request
     * @return	SortedSet of matching MatchedService items with degree of match >= subsumed-by
     * @throws MatchingException	If something goes wrong during matching
     */
    SortedSet matchRequest(URI profileURI) throws MatchingException;
    
    /**
     * Retrieves advertisements that are relevant for a given service request
     * 
     * @param profileStream InputStream of the service request
     * @return SortedSet of matching MatchedService items with degree of match >= subsumed-by
     * @throws MatchingException If something goes wrong during matching
     */
    SortedSet matchRequest(InputStream profileStream) throws MatchingException;
    
    /**
     * Retrieves advertisements that are relevant for a given service request
     * 
     * @param profile String containing the service request
     * @return SortedSet of matching MatchedService items with degree of match >= subsumed-by
     * @throws MatchingException If something goes wrong during matching
     */
    SortedSet matchRequest(String profile) throws MatchingException;
    
    /**
     * Saves the matching engine
     * 
     * @return If saving is successful
     */
    boolean save();
    
    /**
     * Loads the matching engine (previous saving necessary!)
     * 
     * @return If loading is successful
     */
    boolean load();
    
    /**
     * Clears (=resets) the matching engine
     * 
     */
    void clear();
	
}
