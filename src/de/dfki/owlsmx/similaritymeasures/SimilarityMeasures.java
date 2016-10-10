/*
 * Created on 21.02.2005
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

import de.dfki.owlsmx.utils.MathUtils;

/**
 * A collection of useful functions for similarity measures
 * 
 * @author bEn
 *
 */
public class SimilarityMeasures {
    int termWeight = 0;    
    static final int BINARY_WEIGHT=0;
    static final int TERM_FREQUENCY=1;
    static final int AUGMENTED_NORMALIZED_TERM_FREQUENCY=2;
    static final int LOGARITHMIC_TERM_FREQUENCY=3;
    static final int RELATIVE_TERM_WEIGHT=4;
    
    public SimilarityMeasures() {        
    }
    
    public SimilarityMeasures(int termWeight) {
        this.termWeight = termWeight;
    }
    
    public double loiSimilarity(String term1, String term2) {
        return 0;
    }    
    
    public double cosineSimilarity(int[] tf1, int[] tf2, double[] idf) {
        double[] weight1 = idf(weigthTerms(tf1),idf);
        double[] weight2 = idf(weigthTerms(tf2),idf);
        return( MathUtils.vectorDotProduct(weight1,weight2) / ( MathUtils.vectorNorm(weight1) * MathUtils.vectorNorm(weight2) ) );        
    }
    
    protected double h(double x) {
        return -x*Math.log(x);
    }
    
    public double jensenShannonSimilarity(int[] tf1, int[] tf2) {
        double[] weight1 = weigthTerms(tf1);
        double[] weight2 = weigthTerms(tf2);
        
        double sum = 0;
        for (int i = 0; i< weight1.length; i++) {
            if (( (tf1[i]!=0) && (tf2[i]!=0) ))
                sum += h(weight1[i]+weight2[i]) - h(weight1[i]) - h(weight2[i]);
        }
        
        return MathUtils.log2 + (0.5 * sum);
    }
    
    public double extendedJaccardSimilarity(int[] tf1, int[] tf2, double[] idf) {
        double[] weight1 = idf(weigthTerms(tf1),idf);
        double[] weight2 = idf(weigthTerms(tf2),idf);
        double CqCr = MathUtils.vectorDotProduct(weight1,weight2);
        double nenner = (MathUtils.vectorDotProduct(weight1,weight1) + MathUtils.vectorDotProduct(weight2,weight2) - CqCr) ;
        if (nenner==0)
            return 0;         
        return (CqCr/nenner);
        
    }
    
    protected double[] weigthTerms(int[] termFrequencies) {
        double[] result = new double[termFrequencies.length];        
        switch(termWeight) {
        case SimilarityMeasures.BINARY_WEIGHT:  
//          Binary weight
            result = binaryTermWeight(termFrequencies); 
            break;
        case SimilarityMeasures.TERM_FREQUENCY:
//          Pure term frequency
            for (int i = 0; i<termFrequencies.length;i++) {
                result[i]=termFrequencies[i];
            }
            break;
        case SimilarityMeasures.AUGMENTED_NORMALIZED_TERM_FREQUENCY:  
//          Augmented normalized term frequency
            result = augmentedNormalizedTermFrequency(termFrequencies); 
            break;
        case SimilarityMeasures.LOGARITHMIC_TERM_FREQUENCY:  
//          logarithmic term frequency
            result = logrithmicTermFrequency(termFrequencies); 
            break;
        case SimilarityMeasures.RELATIVE_TERM_WEIGHT:  
//          relative importance
            result = relativeTermWeight(termFrequencies); 
            break;
        }
        return result;
    }
    
    public double[] binaryTermWeight(int[] termFrequencies) {
        double[] result = new double[termFrequencies.length];
        for (int i = 0; i<termFrequencies.length;i++) {
            if (termFrequencies[i]>0)
                result[i]=1.0;
            else
                result[i]=0.0;
        } 
        return result;
    }
    
    public double[] augmentedNormalizedTermFrequency(int[] termFrequencies) {
        double[] result = new double[termFrequencies.length];
        int max = 1; 
        for (int i = 0; i<termFrequencies.length;i++) {
                if (termFrequencies[i]>max)
                    max=termFrequencies[i];
        }
        for (int i = 0; i<termFrequencies.length;i++) {
            result[i]=(0.5 + (0.5 * termFrequencies[i])/max);
        }
        return result;
    }
    
    public double[] logrithmicTermFrequency(int[] termFrequencies) {
        double[] result = new double[termFrequencies.length];
        for (int i = 0; i<termFrequencies.length;i++) {
            if (termFrequencies[i]<1)
                result[i]=0.0;
            else
                result[i]=( Math.log( (double) termFrequencies[i]) + 1.0 );                    
        }
        return result;
    }
   
    public double[] relativeTermWeight(int[] termFrequencies) {
        double[] result = new double[termFrequencies.length];
        double sum = MathUtils.vectorSum(termFrequencies);
        if (sum!=0) {
            for (int i = 0; i<termFrequencies.length;i++) {
                result[i]=termFrequencies[i]/sum;
            }
        }
        return result;
    }
    
    public double[] idf(double[] result, double[] idf) {
        for (int i = 0; i<result.length;i++) {
        	result[i] = result[i]*idf[i];
    	}
    	return result;
    }
	
    
}
