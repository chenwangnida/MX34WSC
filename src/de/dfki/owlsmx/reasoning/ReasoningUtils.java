/*
 * Created on 25.01.2005
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
package de.dfki.owlsmx.reasoning;
import java.net.URI;
import java.net.URISyntaxException;

import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import org.semanticweb.owl.util.OWLConnection;

import de.dfki.owlsmx.exceptions.MatchingException;


/**
 * A collection of useful functions for reasoning
 * 
 * @author Ben Fries
 *
 */
public class ReasoningUtils {
    OWLConnection conn;
    OWLRDFParser parser;
    private static ReasoningUtils instance = null;
      
    public static ReasoningUtils getInstance() throws MatchingException {
        if (instance==null)
            instance = new ReasoningUtils();
        return instance;
    }
    
    private  ReasoningUtils() {
    }
    
    
    private boolean contains(String Instring,String containedString) {
        return (Instring.indexOf(containedString)>=0);
    }
    
    /**
     * Guesses the ontology URI from a concept URI
     * 
     * @param path					String URI of a conct
     * @return						guessed URI of the used ontology
     * @throws MatchingException	If sth goes wrong
     */
    public URI getOntologyURI(String path) throws MatchingException  {
        if (path.startsWith("#") || (!contains(path,"#")))
            throw new MatchingException(path + " doesn't contain the OntologyURL");
        try {
            return new URI(path.substring(0,path.indexOf("#")));
        }
        catch(URISyntaxException e) { throw new MatchingException("URI error: Couldn't get ontologyURI of " + path);}
    }
    
}
