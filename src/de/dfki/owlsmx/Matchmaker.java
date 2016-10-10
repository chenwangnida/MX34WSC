/*
 * Created on 30.10.2004
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

import java.net.URI;

/**
 * Interface for generic matchmakers
 * 
 * Changes: 
 * 24.05.2006 - Patrick Kapahnke - added support for passing services and service requests not 
 * only as URI, but also as stream or string.
 * 
 * @author bEn
 *
 */
public interface Matchmaker{
    
    /**
     * Adds a service to the matchmaker with the given ID,
     * loads it in advance from the given URI
     * 
     * @param integer		ID of the service
     * @param profileURI	URI from which the service will be loaded
     */
    public void addService(Integer integer, java.net.URI profileURI);
    
    /**
     * Adds a service to the matchmaker with the given ID
     * 
     * @param integer		ID of the service
     * @param profile		service profile as string
     * @param baseURI		the base URI
     */
    public void addService(Integer integer, String profile, java.net.URI baseURI);
    
    /**
     * Adds a service to the matchmaker with the given ID
     * 
     * @param integer		ID of the service
     * @param profileStream	service profile as stream
     * @param baseURI		the base URI
     */
    public void addService(Integer integer, java.io.InputStream profileStream, URI baseURI);
	
    /**
     * Removes a service with the given ID from the matchmaker
     * 
     * @param integer		ID of the service
     */
    public void removeService(Integer integer);
    
    /**
     * Matches the service request which will be loaded from the given URI
     * against all services registered at the matchmaker
     * 
     * @param profileURI     
     * @return SortedSet of (MatchedService) services 
     * @throws java.net.URISyntaxException
     * @throws de.dfki.owlsmx.exceptions.MatchingException
     */
    public java.util.SortedSet matchRequest(java.net.URI profileURI) throws java.net.URISyntaxException, de.dfki.owlsmx.exceptions.MatchingException;
    
     /**
     * Matches the service request which is specified by a string
     * against all services registered at the matchmaker
     *  
     * @param profile
     * @return SortedSet of (MatchedService) services
     * @throws de.dfki.owlsmx.exceptions.MatchingException
     */
    public java.util.SortedSet matchRequest(String profile) throws de.dfki.owlsmx.exceptions.MatchingException;
    
    /**
     * Matches the service request which will be read from the input stream
     * against all services registered at the matchmaker
     * 
     * @param profileStream
     * @return SortedSet of (MatchedService) services
     * @throws de.dfki.owlsmx.exceptions.MatchingException
     */
    public java.util.SortedSet matchRequest(java.io.InputStream profileStream) throws de.dfki.owlsmx.exceptions.MatchingException;
    
    /**
     * Loads the last saved state of the matchmaker
     * 
     * @return If the matchmaker was loaded successfully
     */
    public boolean load();
    
    /**
     * Saves the current state of the matchmaker
     * 
     * @return If the matchmaker was saved successfully
     */
    public boolean save();
    
    /**
     * Clears the matchmaker
     */
    public void clear();
}
