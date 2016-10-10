/*
 * Created on 14.12.2004
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

import java.util.Map;

import de.dfki.owlsmx.Indexer.Index;
import de.dfki.owlsmx.exceptions.MatchingException;
import de.dfki.owlsmx.utils.CosineResultTriple;


/**
 * Implementation of the Jensen Shannon divergence based similarity measure
 * 
 * @author Benedikt Fries
 *
 */
public class JensenShannonMeasure extends CosineSimilarity {
    private final static double log2 = Math.log(2);
    public boolean onlyIntersection = true;
    
    /**
     * 
     */
    public JensenShannonMeasure() {        
        super();
        this.term_frequency_component=SimilarityMeasure.TERMWEIGHT_RELATIVE;
    }
    
    public JensenShannonMeasure(Index index) {
        super(index);
        this.term_frequency_component=SimilarityMeasure.TERMWEIGHT_RELATIVE;
    }
    
    public JensenShannonMeasure(SimilarityMeasure measure) {    	
        super(measure); 
        this.term_frequency_component=SimilarityMeasure.TERMWEIGHT_RELATIVE;
    }

    double h(double x) {
        return -x*Math.log(x);
    }
    
    /**
     * Computes the similarity for given weighted term frequencies
     * 
     * @param v1	weighted TF of document 1
     * @param v2	weighted TF of document 2
     * @return		similarity
     */
    double computeSimilarity(double[] v1, double[] v2) {
        double sum = 0;
        double tmp = 0;
        for (int i = 0; i< v1.length; i++) {
            /*  
            if ( (v1[i]==0) || (v2[i]==0) )
                sum += (v1[i]+v2[i])*log2;
            else    
              */    
            if (( (v1[i]!=0) && (v2[i]!=0) ))   {
                tmp = h(v1[i]+v2[i]) - h(v1[i]) - h(v2[i]);                
                if (!new Double(tmp).isNaN())
                    sum += tmp;
            }
        }
        
        return sum;
    }
        
    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasureInterface#computeSimilarity(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public double computeSimilarity(String query, String token1, String service, String token2) throws MatchingException {
        Map pc1 = tokenizer.getTokenFrequencies(token1);
        Map pc2 = tokenizer.getTokenFrequencies(token2);

        CosineResultTriple TFs = getTFArrays(pc1, pc2);
        double[] weightedPC1 =  weigthAndNormalizeTerms(TFs.term1);            
        double[] weightedPC2 =  weigthAndNormalizeTerms(TFs.term2);
        double temp = computeSimilarity(weightedPC1,weightedPC2);
        double result = -0.5 * temp / log2;
        return result;
    }
    
    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasure#getSimilarityType()
     */
    public short getSimilarityType() {
        return SimilarityMeasure.SIMILARITY_EXTENDED_JACCARD;
    }
}
