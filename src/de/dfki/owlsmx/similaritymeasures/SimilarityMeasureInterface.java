/*
 * Created on 20.10.2004
 * 
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
package de.dfki.owlsmx.similaritymeasures;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.mindswap.pellet.TuBox.NotUnfoldableException;

import de.dfki.owlsmx.data.LocalOntologyContainer;
import de.dfki.owlsmx.exceptions.MatchingException;
import de.dfki.owlsmx.reasoning.PelletReasoner;



/**
 * @author bEn Fries
 * 
 * The Similarity Measure Interface is used to compute the similiarity between concepts 
 * without modifiying the reasoner too much.
 * Next to computing the similarity there should als be a possibility to collect the entire
 * similarities for an concept.
 *
 */
public interface SimilarityMeasureInterface {



/**
 * Returns all collected similarities for this concept
 * 
 * @param clazz The class whose similarities should be computed
 * @return the HashMap with the similarities (string concept name -> double similarity value)
 */
public HashMap getStoredSimilarities(com.hp.hpl.jena.ontology.OntClass clazz) ;

/**
 * Computes the similarity between two concepts
 * 
 * @param query					query concept (URI)
 * @param token1				unfolded query concept
 * @param service				service concept (URI)
 * @param token2				unfolded query concept
 * @return						similarity between the concepts
 * @throws MatchingException	If something goes wrong
 */
public abstract double computeSimilarity(String query, String token1, String service, String token2) throws MatchingException;

/**
 * Computes the similarity between two concepts
 * 
 * @param reason			reasoner
 * @param localOntology		local ontology
 * @param clazz1			query concept (URI)
 * @param clazz2			service concept (URI)
 * @return					similarity between the concepts
 */
public abstract double computeSimilarity(PelletReasoner reason, LocalOntologyContainer localOntology, String clazz1, String clazz2);

/**
 * A convenienc function for unfolding
 * 
 * @param reason					used reasoner
 * @param localOntology				used local ontology
 * @param term						Concept to be unfolded (URI)
 * @return							Unfolded string of the given concept
 * @throws NotUnfoldableException	If the concept can't be unfolded in the ontology
 * @throws URISyntaxException		If the concept string is not a valid URI
 * @throws MatchingException		If anything else goes wrong
 */
public String unfoldTerm(PelletReasoner reason, LocalOntologyContainer localOntology, String term) throws NotUnfoldableException, MatchingException, URISyntaxException;

/**
 * Update a document with a given content
 * 
 * @param document		Document to be updated
 * @param tokens		Content with which it should be updated
 */
public void updateDocument(String document, String tokens);


}



