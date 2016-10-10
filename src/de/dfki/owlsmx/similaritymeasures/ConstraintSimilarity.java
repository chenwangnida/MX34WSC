/*
 * Created on 22.10.2004
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
import java.util.HashSet;
import java.util.Iterator;

import org.mindswap.pellet.TuBox.NotUnfoldableException;

import de.dfki.owlsmx.Indexer.Index;
import de.dfki.owlsmx.data.LocalOntologyContainer;
import de.dfki.owlsmx.exceptions.MatchingException;
import de.dfki.owlsmx.io.ErrorLog;
import de.dfki.owlsmx.reasoning.PelletReasoner;



/**
 * Implementation of the LOI (loss of information) based similarity measure
 * 
 * @author Benedikt Fries
 *
 */

public class ConstraintSimilarity extends SimilarityMeasure {

    public ConstraintSimilarity() {}
    
    
    public ConstraintSimilarity(Index index) {        
    }
    
    public ConstraintSimilarity(SimilarityMeasure measure) {        
    }
    
    private boolean debug=false;

    /**
     * Splits the unfolded concept
     * 
     * @param set	Set of constraint candidates
     * @param text	unfolded concept
     * @return HashSet with improved constraint candidates
     */
    private HashSet advancedSplit(HashSet set, String text){
        int openbrackets1=0;
        int openbrackets2=0;
        for (int i=0; i<text.length(); i++) {
            if (text.charAt(i)=='[')
                openbrackets1++;
            else if (text.charAt(i)=='(')
                openbrackets2++;
            else if (text.charAt(i)==']')
                openbrackets1--;
            else if (text.charAt(i)==')')
                openbrackets2--;
            else if ( (text.charAt(i)==',') && (openbrackets1==0) && (openbrackets2==0) ) {
                set.add(text.substring(0,i));
                set.add(text.substring(i+1));
                return set;
                }       
        }
        return set;
      }
    
    /**
     * Removes and from the candidates
     * 
     * @param set	Set of constraint candidates
     * @param text	unfolded concept
     * @return HashSet with improved constraint candidates
     */
    private HashSet removeAnd(HashSet set, String text) {
        text = text.substring(4,text.lastIndexOf(")"));
//        if (debug)
//            System.out.println("    removed and from: "+ text);
        set.add(text);
        return set;
    }
    
    /**
     * removes PRIME from the constraint candidates
     * 
     * @param set	Set of constraint candidates
     * @param text	unfolded concept
     * @return HashSet with improved constraint candidates
     */
    private HashSet removePrime(HashSet set, String text) {
//		text = text.substring(7,text.lastIndexOf("]"));
//        if (debug)
//            System.out.println("    removed prim from: "+ text);
        set.add(text.substring(7,text.indexOf(",")));
        set.add(text.substring(text.indexOf(",")+1,text.lastIndexOf("]")));
        return set;
    }
    
    /**
     * removes lists from the text
     * 
     * @param set	Set of constraint candidates
     * @param text	unfolded concept
     * @return HashSet with improved constraint candidates
     */
    private HashSet removeList(HashSet set, String text) {
        text = text.substring(1,text.lastIndexOf("]"));
        if (debug)
            System.out.println("    removed [] from: "+ text);
        return advancedSplit(set,text);
    }
    
    /**
     * Retrieves the constrains of an unfolded concept
     * 
     * @param text	unfolded concept
     * @return		set of constraints
     */
    private HashSet getConstraints(String text) {
        HashSet set = new HashSet();
        set.add(text);
        Iterator iter = set.iterator();
        boolean changed=true;
        String tmpstring;
        HashSet tmpset = new HashSet();        
        while(changed)	{
            changed=false;
            tmpset.clear();
            iter = set.iterator();
            while ( iter.hasNext() ){                
                tmpstring= (String) iter.next();
                if (debug)
                    System.out.println("getConstraints: " + tmpstring);
                
                if (tmpstring.startsWith("[prime-")) {
                    if (debug)
                        System.out.println("                starts with [prime-");
                    tmpset=removePrime(tmpset,tmpstring);
                    changed=true;
                }
                
                else if (tmpstring.startsWith("and(")) {                    
                    if (debug)
                        System.out.println("                starts with and(");
                    tmpset=removeAnd(tmpset,tmpstring);
                    changed=true;
                }
                
                else if (tmpstring.startsWith("[")) {
                    if (debug)
                        System.out.println("                starts with [");
                    tmpset=removeList(tmpset,tmpstring);
                    changed=true;
                }
                else
                    tmpset.add(tmpstring);
            }
            if (changed)
                set= new HashSet(tmpset);            
        }
        return set;
    }
    
//    private HashSet mergeSet(HashSet set1, HashSet set2) {
//    	HashSet set = new HashSet(); 
//    	set.addAll(set1);
//    	set.addAll(set2);
//        return set;
//    }
    
//    private void printHashSet(Set set) {
//        Iterator iter = set.iterator();
//        String tmp;
//        System.out.println("Printing HashSet: ");
//        while ( iter.hasNext() ){
//          tmp= (String) iter.next();
//          System.out.println(tmp);
//        }
//    }
    
    
    /**
     * Computes LOI similarity between the terms
     * 
     * @param set1	Constraints of term 1
     * @param set2	Constraints of term 2
     * @return		LOI similarity between the terms
     */
    private double computeSimilarity(HashSet set1, HashSet set2) {
        float overall=set1.size() + set2.size();
        HashSet set3 = new HashSet(set1);
         
        set1.removeAll(new HashSet(set2) );        
        set2.removeAll(new HashSet(set3) );
        double set1_not_set2 = set1.size();
        double set2_not_set1 = set2.size();
        double loi = (set1_not_set2 + set2_not_set1)/ overall;
    
        return (1.0 - loi);
    }
      
    
    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasureInterface#computeSimilarity(owlsmx.reasoning.PelletReasoner, owlsmx.data.LocalOntologyContainer, java.lang.String, java.lang.String)
     */
    public double computeSimilarity(PelletReasoner reason, LocalOntologyContainer localOntology, String clazz1, String clazz2) {        
        double similarity=0;        
        try {
            String unfolded1 = reason.unfoldTerm(localOntology.getClass(clazz1));
            String unfolded2 = reason.unfoldTerm(localOntology.getClass(clazz2));
            similarity=computeSimilarity(getConstraints(unfolded1),getConstraints(unfolded2));
         } catch (NotUnfoldableException e) {
        	 ErrorLog.instanceOf().report(this.getClass().toString() + "|computeSimilarity: " + e.getMessage() + "\n Could not unfold "  + clazz1 + " " + clazz2);
             e.printStackTrace();
         } catch (MatchingException e) {
        	 ErrorLog.instanceOf().report(this.getClass().toString() + "|computeSimilarity: " + e.getMessage() + "\n Could not unfold "  + clazz1 + " " + clazz2);
			 e.printStackTrace();
		} catch (URISyntaxException e) {
			 ErrorLog.instanceOf().report(this.getClass().toString() + "|computeSimilarity: " + e.getMessage() + "\n Could not unfold "  + clazz1 + " " + clazz2);
			 e.printStackTrace();
		}
        return similarity;
    }
    
    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasureInterface#computeSimilarity(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public double computeSimilarity(String query, String token1, String service, String token2) throws MatchingException {
        
        return computeSimilarity(getConstraints(token1),getConstraints(token2));
    }

    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasureInterface#updateDocument(java.lang.String, java.lang.String)
     */
    public void updateDocument(String document, String tokens) {
        // As we don't keep an index we don't need to update the document        
    }
    
    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasure#usesIndex()
     */
    public boolean usesIndex() {
        return false;
    }
    
    /* (non-Javadoc)
     * @see owlsmx.similaritymeasures.SimilarityMeasure#getSimilarityType()
     */
    public short getSimilarityType() {
        return SimilarityMeasure.SIMILARITY_LOI;
    }


}
