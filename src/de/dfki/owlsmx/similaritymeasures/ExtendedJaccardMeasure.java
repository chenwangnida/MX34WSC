/*
 * Created on 13.12.2004
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
import de.dfki.owlsmx.utils.MathUtils;


/**
 * Implementation of the extended Jaccard based similarity measure
 * 
 * @author Benedikt Fries
 *
 */
public class ExtendedJaccardMeasure extends CosineSimilarity {

    /**
     * 
     */
    public ExtendedJaccardMeasure() {
        super();
        term_frequency_component=SimilarityMeasure.TERMWEIGHT_LOGARITHMIC;
    }
    
    public ExtendedJaccardMeasure(Index index) {
        super(index); 
    }
    
    public ExtendedJaccardMeasure(SimilarityMeasure measure) {    	
        super(measure); 
    }


    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasure#getSimilarityType()
     */
    public short getSimilarityType() {
        return SimilarityMeasure.SIMILARITY_EXTENDED_JACCARD;
    }
    
    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasureInterface#computeSimilarity(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public double computeSimilarity(String query, String token1, String service, String token2) throws MatchingException {

        Map pc1 = tokenizer.getTokenFrequencies(token1);
        Map pc2 = tokenizer.getTokenFrequencies(token2);
        
        CosineResultTriple TFs = getTFArrays(pc1, pc2);
        if ( (pc1.size()==0) && (pc2.size()==0) )
            return 1.0;

        double[] weightedPC1 =  weigthAndNormalizeTerms(TFs.term1,TFs.idf);            
        double[] weightedPC2 =  weigthAndNormalizeTerms(TFs.term2,TFs.idf);
        
        double CqCr = MathUtils.vectorDotProduct(weightedPC1,weightedPC2);
        double nenner = (MathUtils.vectorDotProduct(weightedPC1,weightedPC1) + MathUtils.vectorDotProduct(weightedPC2,weightedPC2) - CqCr) ;
        
        double result = 0;
        if (nenner!=0)
            result=(CqCr/nenner);
              	
        return result;
    }
}
