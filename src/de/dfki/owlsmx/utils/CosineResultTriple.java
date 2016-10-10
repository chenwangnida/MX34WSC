/*
 * Created on 11.12.2004
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
package de.dfki.owlsmx.utils;

import de.dfki.owlsmx.exceptions.MatchingException;

/**
 * @author bEn
 *
 */
public class CosineResultTriple {    
    public int[] term1;
    public int[] term2;
    public double[] idf;
    
    public CosineResultTriple() {
        
    }
   
    public CosineResultTriple(int[] t1, int[] t2, double[] idfs) throws MatchingException {
        term1 = new int[t1.length];
        term2 = new int[t2.length];
        idf = new double[idfs.length];
        for (int i=0;i<t1.length;i++) {            
            term1[i] =t1[i];
            term2[i] =t2[i];
            if ( (t1[i]==0) && (t2[i]==0) && (term1[i]==0) && (term2[i]==0) )
                throw new MatchingException("Two unweighted terms should never be 0 ");
            idf[i]  =idfs[i];
        }
    }


}
