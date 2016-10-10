/*
 * Created on 25.10.2004
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
import java.net.URISyntaxException;
import java.util.SortedSet;

import de.dfki.owlsmx.Indexer.SimpleIndex;
import de.dfki.owlsmx.exceptions.MatchingException;
import de.dfki.owlsmx.similaritymeasures.ConstraintSimilarity;
import de.dfki.owlsmx.similaritymeasures.CosineSimilarity;
import de.dfki.owlsmx.similaritymeasures.ExtendedJaccardMeasure;
import de.dfki.owlsmx.similaritymeasures.JensenShannonMeasure;
import de.dfki.owlsmx.similaritymeasures.SimilarityMeasure;




/**
 * Abstract class for generic syntactic similarity matchmakers
 * 
 * @author bEn
 *
 */
public abstract class SimilarityMatchmaker implements Matchmaker {
    public static final short EXACT = 0;
    public static final short PLUGIN = 1;
    public static final short SUBSUMES = 2;
    public static final short SUBSUMED_BY = 3;
    public static final short NEAREST_NEIGHBOUR = 4;
    public static final short FAIL = 5;    
    protected double similarityTreshold = 0.001;
    protected SimilarityMeasure sim;

    /* (non-Javadoc)
     * @see owlsmx.Matchmaker#addService(Integer, java.net.URI)
     */
    abstract public void addService(Integer integer, java.net.URI profileURI);

    /**
     * @see de.dfki.owlsmx.Matchmaker#addService(Integer, java.lang.String, java.net.URI)
     */
    abstract public void addService(Integer integer, String profile, java.net.URI baseURI);
    
    /**
     * @see de.dfki.owlsmx.Matchmaker#addService(Integer, java.io.InputStream, java.net.URI)
     */
    abstract public void addService(Integer integer, java.io.InputStream profileStream, java.net.URI baseURI);
    
    /* (non-Javadoc)
     * @see owlsmx.Matchmaker#removeService(java.lang.Integer)
     */
    abstract public void removeService(Integer integer);

    /* (non-Javadoc)
     * @see owlsmx.Matchmaker#matchRequest(java.net.URI)
     */
    abstract public SortedSet matchRequest(java.net.URI profileURI) throws URISyntaxException, MatchingException ;
    
    /**
     * @see de.dfki.owlsmx.Matchmaker#matchRequest(java.lang.String)
     */
    abstract public SortedSet matchRequest(String profile) throws MatchingException;
    
    /**
     * @see de.dfki.owlsmx.Matchmaker#matchRequest(java.io.InputStream)
     */
    abstract public SortedSet matchRequest(java.io.InputStream profileStream) throws MatchingException;

    /* (non-Javadoc)
     * @see owlsmx.Matchmaker#load()
     */
    abstract public boolean load(); 

    /* (non-Javadoc)
     * @see owlsmx.Matchmaker#save()
     */
    abstract public boolean save();

    /* (non-Javadoc)
     * @see owlsmx.Matchmaker#clear()
     */
    abstract public void clear();
    
    /**
     * Set the used syntactic similarity treshold to the given value,
     * a service is required to have at least this similarity
     * (take care for an appropriate explanation in the 
     *  context of hybrid filters)
     * 
     * @param treshold double treshold to be used
     */
    public void setSyntacticTreshold(double treshold){
    	similarityTreshold = treshold;
    }
    
    /**
     * Returns the used syntactic similarity treshold
     * (take care for an appropriate explanation in the 
     *  context of hybrid filters)
     * 
     * @return syntactic similarity treshold
     */
    public double getSyntacticTreshold() {
    	return similarityTreshold;
    }

    /**
     * Returns the given similarity measure
     * 
     * @return used similarity measure
     */
    protected SimilarityMeasure getSimilarityMeasure() {
    	return sim;    	
    }
    
    /**
     * Returns the type of the used similarity measure
     * (see SimilarityMeasure for the details) 
     * 
     * @return int Type of used similarity measure
     */
    public int getSimilarityMeasureType() {
    	if (sim!=null)
    		return sim.getSimilarityType();
    	return SimilarityMeasure.SIMILARITY_NONE;
    }

    /**
     * Sets a (usually syntactic) similarity Measure to the Matchmaker
     * They can be exchanged dynamically unless it's set to null (=pure semantic matching) 
     * similar to setSimilarMeasure(SimilarityMeasure)
     * 
     * @param similarity code for the similarity measure to be used (see matchingEngine.SimilarityMeasures.SimilarityMeasure)
     */
    public void setSimilarityMeasure(short similarity)  {
        this.sim = switchSimilarityMeasure(similarity);
   }
    
    /**
     * Sets a (usually syntactic) similarity Measure to the Matchmaker
     * They can be exchanged dynamically unless it's set to null (=pure semantic matching) 
     * similar to setSimilarMeasure(short)
     * 
     * @param sim similarity measure to be used
     */
    public void setSimilarityMeasure(SimilarityMeasure sim)  {        
        this.sim=switchSimilarityMeasure(sim.getSimilarityType());
    }


    /**
     * Returns an new Similarity Measure of the given type
     * 
     * @param similarity Type of similarity measure that should be used
     * @return	the new similarity measure
     */
    protected SimilarityMeasure switchSimilarityMeasure(short similarity) {
        if (similarity == SimilarityMeasure.SIMILARITY_NONE) {
            SimpleIndex.instanceOf().clear();
            return null;
        }
        else if (similarity == SimilarityMeasure.SIMILARITY_JENSEN_SHANNON)
             return new JensenShannonMeasure(this.sim);
        else if (similarity == SimilarityMeasure.SIMILARITY_COSINE)
            return new CosineSimilarity(this.sim);
         else if (similarity == SimilarityMeasure.SIMILARITY_EXTENDED_JACCARD)
             return new ExtendedJaccardMeasure(this.sim);
         else //(similarity == SimilarityMatchmaker.SIMILARITY_LOI)
             return new ConstraintSimilarity(this.sim);   
    }
    
    /**
     * If a syntactic similarity measure is used
     * 
     * @return returns if a syntact similarity measure is used
     */
    protected boolean useSyntacticFilter() {        
        return ( (this.sim!=null) && (this.sim.getSimilarityType()!=SimilarityMeasure.SIMILARITY_NONE) );
    }
}


