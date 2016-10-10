/*
 * Created on 08.12.2004
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.TuBox.NotUnfoldableException;

import de.dfki.owlsmx.Indexer.Index;
import de.dfki.owlsmx.Indexer.SimpleIndex;
import de.dfki.owlsmx.data.LocalOntologyContainer;
import de.dfki.owlsmx.exceptions.MatchingException;
import de.dfki.owlsmx.reasoning.PelletReasoner;
import de.dfki.owlsmx.tokenizer.PrimitiveConceptTokenizer;
import de.dfki.owlsmx.utils.CollectionUtils;
import de.dfki.owlsmx.utils.CosineResultTriple;
import de.dfki.owlsmx.utils.MathUtils;
import de.dfki.owlsmx.utils.StringUtils;


/**
 * Implementation of the cosine similarity measure
 * 
 * @author Benedikt Fries
 *
 */
public class CosineSimilarity extends SimilarityMeasure {
    protected int term_frequency_component=SimilarityMeasure.TERMWEIGHT_LOGARITHMIC;
    PrimitiveConceptTokenizer tokenizer = new PrimitiveConceptTokenizer();
    protected boolean useIndex = true;
    
    
    /**
     * 	Constructor
     * 	Creates an index
     */
    public CosineSimilarity() {
        super(SimpleIndex.instanceOf());
    }
    
    /**
     * Constructor,
     * Uses given index
     * @param index index that should be used
     */
    public CosineSimilarity(Index index) {
        super(index); 
    }
    
    /**
     * Constructor,
     * Uses the index of the given similarity Measure
     * 
     * @param measure	measure whose index should be used
     */
    public CosineSimilarity(SimilarityMeasure measure) {
        super(measure); 
    }
    
    /**
     * Updates the overall document frequencies with the primitive concepts of a concept
     * @param classname				document to be updated
     * @param primitiveConcepts		the primitive concepts of the concept/document
     */
    protected void updateDocumentFrequency(String classname, Map primitiveConcepts) {
        if (this.usesIndex())
            SimpleIndex.instanceOf().addDocument(classname, primitiveConcepts);
    }
    
    /**
     * Computes the binary term weight for given term frequencies
     * 
     * @param termFrequencies	term frequencies to be used
     * @return					computed weighted TF
     */
    protected double[] binaryTermWeight(int[] termFrequencies) {
        double[] result = new double[termFrequencies.length];
        for (int i = 0; i<termFrequencies.length;i++) {
            if (termFrequencies[i]>0)
                result[i]=1.0;
            else
                result[i]=0.0;
        } 
        return result;
    }
    
    
    /**
     * Computes the agmented normalized term weight for given term frequencies
     * 
     * @param termFrequencies	term frequencies to be used
     * @return					computed weighted TF
     */
    protected double[] augmentedNormalizedTermFrequency(int[] termFrequencies) {
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
    
    /**
     * Computes the logarithmic term weight for given term frequencies
     * 
     * @param termFrequencies	term frequencies to be used
     * @return					computed weighted TF
     */
    protected double[] logrithmicTermFrequency(int[] termFrequencies) {
        double[] result = new double[termFrequencies.length];
        for (int i = 0; i<termFrequencies.length;i++) {
            if (termFrequencies[i]<1)
                result[i]=0.0;
            else
                result[i]=( Math.log( (double) termFrequencies[i]) + 1.0 );                    
        }
        return result;
    }
   
    /**
     * Computes the relative term weight for given term frequencies
     * 
     * @param termFrequencies	term frequencies to be used
     * @return					computed weighted TF
     */
    protected double[] relativeTermWeight(int[] termFrequencies) {
        double[] result = new double[termFrequencies.length];
        double sum = MathUtils.vectorSum(termFrequencies);
        if (sum!=0) {
            for (int i = 0; i<termFrequencies.length;i++) {
                result[i]=termFrequencies[i]/sum;
            }
        }
        return result;
    }
    
    /**
     * Applies a given IDF to the weighted term frequencies
     * 
     * @param result	weighted TFs
     * @param idf		IDF to be used
     * @return			fully weighted TFs
     */
    protected double[] idf(double[] result, double[] idf) {
        for (int i = 0; i<result.length;i++) {
        	result[i] = result[i]*idf[i];
    	}
    	return result;
    }
    
    /**
     * Weights and normalizes terms frequencies without using an IDF
     * 
     * @param termFrequencies	term frequencies to be used
     * @return					weighted and normalized TFs
     */
    protected double[] weigthAndNormalizeTerms(int[] termFrequencies) {
    	double[] idf = new double[termFrequencies.length];
        for (int i = 0; i<idf.length;i++) {
        	idf[i] = 1.0;
    	}
    	return weigthAndNormalizeTerms(termFrequencies, idf);
    }
    
    /**
     * Weights and normalizes terms frequencies using an IDF
     * 
     * @param idf				used inverse document frequency
     * @param termFrequencies	term frequencies to be used
     * @return					weighted and normalized TFs
     */
    protected double[] weigthAndNormalizeTerms(int[] termFrequencies, double[] idf) {
        double[] result = new double[termFrequencies.length];        
        switch(term_frequency_component) {
        case SimilarityMeasure.TERMWEIGHT_BINARY:  
//          Binary weight
            result = binaryTermWeight(termFrequencies); 
            break;
        case SimilarityMeasure.TERMWEIGHT_TERMFREQUENCY:
//          Pure term frequency
            for (int i = 0; i<termFrequencies.length;i++) {
                result[i]=termFrequencies[i];
            }
            break;
        case SimilarityMeasure.TERMWEIGHT_AUGMENTED:  
//          Augmented normalized term frequency
            result = idf(augmentedNormalizedTermFrequency(termFrequencies),idf); 
            break;
        case SimilarityMeasure.TERMWEIGHT_LOGARITHMIC:  
//          Logrithmic term frequency
            result = idf(logrithmicTermFrequency(termFrequencies),idf); 
            break;
        case SimilarityMeasure.TERMWEIGHT_RELATIVE:  
//          relative importance
            result = relativeTermWeight(termFrequencies); 
            break;
        }
        
        return result;
    }
    
    /**
     * Computes TF and IDF of terms that are in both documents 
     * 
     * @param pc1	Map with TFs of document 1
     * @param pc2	Map with TFs of document 2
     * @return		CosineResultTriple which contains TFs of documen 1, TFs of document 2, IDF of the used terms
     * @throws MatchingException	If something goes wrong
     */
    protected CosineResultTriple getTFArrays(Map pc1, Map pc2) throws MatchingException {
        Set size = CollectionUtils.union(pc1.keySet(),pc2.keySet());
        int[] r1 = new int[size.size()];
        int[] r2 = new int[size.size()];
        double[] idf = new double[size.size()];
        
        Iterator iter = size.iterator();
        String current;
        int count = 0;
        //System.out.println("Unweighted term:");
        while (iter.hasNext()) {
            current = (String) iter.next();
            
            if ( (!pc1.containsKey(current)) && (!pc2.containsKey(current)))
            	de.dfki.owlsmx.io.ErrorLog.instanceOf().report("Problem with current: " +  current);
            
            if (pc1.containsKey(current))
                r1[count]=((Integer)pc1.get(current)).intValue();
            else
                r1[count]=0;
            
            if (pc2.containsKey(current))
                r2[count]=((Integer)pc2.get(current)).intValue();
            else
                r2[count]=0;
            if ( (r1[count]==0) && (r2[count]==0))
               de.dfki.owlsmx.io.ErrorLog.instanceOf().report("A new Problem with current: " +  current);
            //if (this.usesIndex())
                idf[count] = index.getIDF(current);
            //else
            //    idf[count] = 1;
            //System.out.println("           r1: " + r1[count] + "  r2: " + r2[count] + " idf " + idf[count]);
            count++;
        }
        
    	return new CosineResultTriple(r1,r2,idf);
    }
   
    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasureInterface#updateDocument(java.lang.String, java.lang.String)
     */
    public void updateDocument(String document, String tokens) {
        this.updateDocumentFrequency(document,StringUtils.getPrimitiveConcepts(tokens));
    }
    
    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasureInterface#computeSimilarity(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public double computeSimilarity(String query, String token1, String service, String token2) throws MatchingException {
        Map pc1 = tokenizer.getTokenFrequencies(token1);
        Map pc2 = tokenizer.getTokenFrequencies(token2);        
        
        CosineResultTriple TFs = getTFArrays(pc1, pc2);
        double[] weightedPC1 =  weigthAndNormalizeTerms(TFs.term1);            
        double[] weightedPC2 =  weigthAndNormalizeTerms(TFs.term2,TFs.idf);
        
        if ( (weightedPC1.length==0) && (weightedPC2.length==0) ) {
            return 1.0;
        }                
        else if ( (weightedPC1.length==0) || (weightedPC2.length==0) ) {
            return 0;
        }        
        return (MathUtils.vectorDotProduct(weightedPC1,weightedPC2)/(MathUtils.vectorNorm(weightedPC1) * MathUtils.vectorNorm(weightedPC2) ) );
    }
        
    /**
     * Change the used weighting
     * 
     * @param type	Desired weighting method
     */
    public void setWeigth(int type) {
        this.term_frequency_component=type;
    }

    /** 
     * @return currently used weighting method
     */
    public int getWeigth() {
        return term_frequency_component;
    }

    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasureInterface#computeSimilarity(owlsmx.reasoning.PelletReasoner, owlsmx.data.LocalOntologyContainer, java.lang.String, java.lang.String)
     */
    public double computeSimilarity(PelletReasoner reason, LocalOntologyContainer localOntology, String clazz1, String clazz2) {
    	try {
			return computeSimilarity(clazz1.toString(), unfoldTerm(reason, localOntology, clazz1), clazz2.toString(), unfoldTerm(reason,localOntology, clazz2));
		} catch (NotUnfoldableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MatchingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;
    }

    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasure#getSimilarityType()
     */
    public short getSimilarityType() {
        return SimilarityMeasure.SIMILARITY_COSINE;
    }





}
