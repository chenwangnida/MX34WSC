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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.dfki.owlsmx.io.ErrorLog;
import de.dfki.owlsmx.stemmer.PorterStemmer;
import de.dfki.owlsmx.tokenizer.PrimitiveConceptTokenizer;




/**
 * @author bEn
 * 
 * A VERY simple Index class that is just able to add new Documents
 * but cannot remove any.
 *
 */
public class SimpleIndex implements Index, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public static final short PURE_IDF = 1;
    public static final short LOGARITHMIC_IDF = 2;
    private Set seenClasses = new TreeSet();
    private Map DocumentFrequency = new HashMap();
    public int collection_frequency_component = SimpleIndex.LOGARITHMIC_IDF;   
    PrimitiveConceptTokenizer tokenizer = new PrimitiveConceptTokenizer();
    
    
    
    public static SimpleIndex instanceOf() {
        return index;
    }
    private static SimpleIndex index = new SimpleIndex();
    
    public SimpleIndex() {
    }

    /* (non-Javadoc)
     * @see matchingEngine.Index.Index#existsDocument(java.lang.String)
     */
    public boolean existsDocument(String document) {
        return seenClasses.contains(document);
    }
    
    public boolean existsTerm(String term) {
        if (!DocumentFrequency.containsKey(term))
                return false;
        if (((Integer)DocumentFrequency.get(term)).intValue() <= 0) {
            ErrorLog.instanceOf().report(this.getClass().toString() +"|existsTerm: " + term + " - " + ((Integer)DocumentFrequency.get(term)).intValue() );
            return false;
        }
        return true;        
    }
    

    
    private void incrementDocument(String term,int count) {
       if ((term.indexOf("http")>=0) || (term.indexOf("file")>=0)){
            return;
        }
        term = prepareTerm(term);
        if (DocumentFrequency.containsKey(term))
            DocumentFrequency.put(term,new Integer(( (Integer) DocumentFrequency.get(term)).intValue()+count));
        else
            DocumentFrequency.put(term,new Integer(count));
    }

    /* (non-Javadoc)
     * @see matchingEngine.Index.Index#addDocument(java.lang.String, java.util.Map)
     */
    public void addDocument(String document, Map termFrequencies) {    	
        if (this.existsDocument(document))
            return;
        seenClasses.add(document);
        Iterator iter = termFrequencies.entrySet().iterator();
        Map.Entry me;        
        while (iter.hasNext()) {
            me = (Map.Entry) iter.next();    	
            this.incrementDocument((String)me.getKey(),((Integer)me.getValue()).intValue());
        }
        
    }

    /* (non-Javadoc)
     * @see matchingEngine.Index.Index#getIDF(java.lang.String)
     */
    public double getIDF(String term) {
        String prepared = prepareTerm(term); //prepareTerm(term);
        double idf = getIDF(prepared, collection_frequency_component);        
        
        if (!existsTerm(prepared)) {
        	ErrorLog.instanceOf().report("getIDF: stemmed term not in index " + prepared);            
        	ErrorLog.instanceOf().report("        Classes: " + seenClasses.size());
        	//ErrorLog.instanceOf().report("        term: " + term);
        	if (!existsTerm(term))
        	    ErrorLog.instanceOf().report("        term not in index " + term);
        	else
        	    ErrorLog.instanceOf().report("    but term exists in index " + term);
        	ErrorLog.instanceOf().report("        idf: " + idf);
        }   
        
        return idf;
    }
    
    /* (non-Javadoc)
     * @see matchingEngine.Index.Index#addDocument(java.lang.String, java.util.Set)
     */
    public void addDocument(String document, Set terms) {
        if (this.existsDocument(document))
            return;
        seenClasses.add(document);
        Iterator iter = terms.iterator();
        while (iter.hasNext()) {
            this.incrementDocument((String) iter.next(),1);
        }
        
    }
    
    private String prepareTerm(String term) {
        /*
        if (term.indexOf("#")>=0)
            term = term.substring(term.indexOf("#")+1);
            */
        term = PorterStemmer.instanceOf().stem(term.toLowerCase());                
        return term;
    }

    /* (non-Javadoc)
     * @see matchingEngine.Index.Index#getIDF(java.lang.String, int)
     */
    public double getIDF(String term, int type) { 
        double result = 1.0;
        //inverse document frequency
        if (existsTerm(term) && (type==SimpleIndex.PURE_IDF) )
            result = 1.0/( (double) ((Integer) DocumentFrequency.get(term)).intValue() );
        //logarithmic IDF
        else if (existsTerm(term) && (type==SimpleIndex.LOGARITHMIC_IDF) ) {            
            result = 1.0 + Math.log((double)seenClasses.size()/( (double) ((Integer) DocumentFrequency.get(term)).intValue() ));
        	}       
        return result;        
    }

    /* (non-Javadoc)
     * @see matchingEngine.Indexer.Index#addDocument(java.lang.String, java.lang.String)
     */
    public void addDocument(String document, String terms) {
        tokenizer.setString(terms);
        addDocument(document, tokenizer.getTokenFrequencies());
        tokenizer.clear();
    }

    /* (non-Javadoc)
     * @see matchingEngine.Indexer.Index#clear()
     */
    public void clear() {
        seenClasses.clear();
        DocumentFrequency.clear();
        tokenizer.clear();
    }


}
