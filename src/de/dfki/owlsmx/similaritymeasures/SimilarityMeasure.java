/*
 * Created on 10.12.2004
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
import java.util.Map;

import org.mindswap.pellet.TuBox.NotUnfoldableException;


import com.hp.hpl.jena.ontology.OntClass;

import de.dfki.owlsmx.Indexer.Index;
import de.dfki.owlsmx.Indexer.SimpleIndex;
import de.dfki.owlsmx.data.LocalOntologyContainer;
import de.dfki.owlsmx.exceptions.MatchingException;
import de.dfki.owlsmx.reasoning.PelletReasoner;

/**
 * Abstract implementation of common functions for similarity measures
 * 
 * @author bEn
 *
 */
public abstract class SimilarityMeasure implements SimilarityMeasureInterface {
    protected Index index;    
    protected HashMap similarities=new HashMap();
    final public static short SIMILARITY_NONE=-1;
    final public static short SIMILARITY_LOI=0;
    final public static short SIMILARITY_EXTENDED_JACCARD=1;
    final public static short SIMILARITY_COSINE=2;
    final public static short SIMILARITY_JENSEN_SHANNON=3;
    
    final public static short TERMWEIGHT_TERMFREQUENCY	= 200;
    final public static short TERMWEIGHT_BINARY 		= 201;
    final public static short TERMWEIGHT_AUGMENTED 		= 202;
    final public static short TERMWEIGHT_LOGARITHMIC	= 203;
    final public static short TERMWEIGHT_RELATIVE		= 204;
    
    public abstract short getSimilarityType();
    
    SimilarityMeasure() {     
        this.index= SimpleIndex.instanceOf();
    }
    
    SimilarityMeasure(Index index){
        this.index=index;
    };
    
    SimilarityMeasure(SimilarityMeasure measure){
        if ( (measure!=null) && (measure.usesIndex()))
            this.index = measure.getIndex();
        else
            this.index = SimpleIndex.instanceOf();
            
    };
    
    /**
     * Replaces the current index with the give one
     * 
     * @param index	index to be used
     */
    public void setIndex(Index index) {
        if (!usesIndex())
            return;
        this.index = index;
    }    
       
    /**
     * @return currently used index
     */
    public Index getIndex() {
        return index;
    }
    
    /**
     * @return if the current measure uses an index
     */
    public boolean usesIndex() {
        return true;
    }



    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasureInterface#computeSimilarity(owlsmx.reasoning.PelletReasoner, owlsmx.data.LocalOntologyContainer, java.lang.String, java.lang.String)
     */
    public abstract double  computeSimilarity(PelletReasoner reason, LocalOntologyContainer localOntology, String clazz1, String clazz2);
    
    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasureInterface#getStoredSimilarities(com.hp.hpl.jena.ontology.OntClass)
     */
    public HashMap getStoredSimilarities(OntClass clazz) {
        String classname = clazz.getURI().toString();
        return (HashMap) similarities.get(classname);
    } 

//    /**
//     * Computes the similarity between two concepts
//     * 
//     * @param clazz The class whose similarities should be returned
//     * @return the HashMap with the similarities (string concept name -> double similarity value)
//     */
//    public HashMap computeSimilarities(String clazz, Set toConcepts, PelletReasoner reason)  {        
//        Set set = reason.getClasses();
//        //set.add(clazz);
//        HashMap result = new HashMap();
//        Iterator iter = set.iterator();
//        OntClass tmpClass;
//        Double similarity;
//        while (iter.hasNext()) {
//        	tmpClass = (OntClass) iter.next();            
//            if (toConcepts.contains(tmpClass.getURI().toString()) || clazz.equals(tmpClass)) {   
//                similarity = new Double(computeSimilarity(reason, clazz,tmpClass.getURI().toString()));
//                result.put(tmpClass.getURI().toString(), similarity );
//            }
//        }
//        return result;
//    }

    /**
     * all similarity values for the given concept will be stored (remembered ;-) )
     * 
     * @param clazz Concept whose similarity values should be remembered
     */
    public void remember(OntClass clazz) {
        String classname = clazz.getURI().toString();
        if (!similarities.containsKey(classname) ){
            similarities.put(classname, new HashMap()) ; 
            }

    	}

    /**
     * the similarity values for the given concept will be deleted (forgotten ;-) ) 
     * and no new values will be stored
     * 
     * @param clazz
     */
    public void forget(OntClass clazz) {
        String classname = clazz.getURI().toString();
        if (similarities.containsKey(classname) ) {
            similarities.remove(classname); 
            }
    	}

    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasureInterface#unfoldTerm(owlsmx.reasoning.PelletReasoner, owlsmx.data.LocalOntologyContainer, java.lang.String)
     */
    public String unfoldTerm(PelletReasoner reason, LocalOntologyContainer localOntology, String term) throws NotUnfoldableException, MatchingException, URISyntaxException {
    	String result = reason.unfoldTerm(localOntology.getClass(term));
    	System.out.println(this.getClass().toString() + "|unfoldTerm: " + result);
        return result;
    }
    
    /**
     * Updates the overall document frequencies with the primitive concepts of a concept
     * @param document				document to add (a service)
     * @param termFrequencies		Map: term -> termFrequencies of document
     */
    public void addDocument(String document, Map termFrequencies) {
        SimpleIndex.instanceOf().addDocument(document, termFrequencies);
    }
    
    
}
