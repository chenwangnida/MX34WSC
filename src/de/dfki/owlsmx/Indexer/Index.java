/*
 * Created on 10.12.2004
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
package de.dfki.owlsmx.Indexer;

import java.util.Map;
import java.util.Set;


/**
 * A generic interface for indexes
 * 
 * @author Ben Fries
 *
 */
public interface Index {
    
    /**
     * Checks if a document already exists in the index
     * 
     * @param document	Document which should be checked
     * @return			If the document exists
     */
    public boolean existsDocument(String document);
    
    /**
     * Adds a document to the index
     * 
     * @param document			Document which should be added
     * @param termFrequencies	Map primitive term => term frequencies in the document
     */
    public void addDocument(String document, Map termFrequencies);
    
    /**
     * Adds a document to the index
     * 
     * @param document			Document which should be added
     * @param terms				Set of terms which appear (term frequency of 1)
     */
    public void addDocument(String document, Set terms);
    
    /**
     * Adds a document to the index
     * 
     * @param document		Document which should be added
     * @param terms			String content of the document
     */
    public void addDocument(String document, String terms);
    
    /**
     * Computes the IDF for a given term (using default method)
     * 
     * @param term	Term whose IDF should compute
     * @return		double inverse document frequency
     */
    public double getIDF(String term);
    
    /**
     * Computes the IDF for a given term (using default method)
     * 
     * @param term	Term whose IDF should compute
     * @param type	Computation method to be used
     * @return		double inverse document frequency
     */
    public double getIDF(String term, int type);

    /**
     *	Resets the index 
     */
    public void clear();
}
