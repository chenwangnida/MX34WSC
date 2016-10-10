/*
 * Created on 08.12.2004
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

import java.util.Map;

import de.dfki.owlsmx.Indexer.SimpleIndex;
import de.dfki.owlsmx.stemmer.PorterStemmer;
import de.dfki.owlsmx.tokenizer.PrimitiveConceptTokenizer;


/**
 * @author bEn
 *
 */
public class StringUtils {
    static PorterStemmer stemmer = PorterStemmer.instanceOf();
    
    public static boolean contains(String Instring,String containedString) {
        return (Instring.indexOf(containedString)>=0);
    }
    
    public static String getStemmedConceptName(String uri) {
        String result = "";
        if (contains(uri,"#"))
            result = uri.substring(uri.indexOf("#")+1);
        else
            result = uri;
        
        if ( (contains(result, "#")) || (contains(result, "http")) || (contains(result, "file")) )
            System.out.println("stemmedConcept is a Uri " + result);
        result = stemmer.stem(result);
        if (result==null)
            System.out.println("stemmedConcept is null " + result);
        return result;
    }

public static Map getPrimitiveConcepts(String unfoldedConcept){
    PrimitiveConceptTokenizer token = new PrimitiveConceptTokenizer(unfoldedConcept);
    return token.getTokenFrequencies();
    	/*
        String tmp="";
        Map map = new HashMap();
        
        if (unfoldedConcept.indexOf("prime-")<0)
    	    map.put(unfoldedConcept, new Integer(1));
       
        else
            for (int i=0; i<unfoldedConcept.length(); i++) {
                if ( (unfoldedConcept.charAt(i)=='[') || 
                        (unfoldedConcept.charAt(i)=='(') || 
                        (unfoldedConcept.charAt(i)==',') ||
                        (unfoldedConcept.charAt(i)==']') ||
                        (unfoldedConcept.charAt(i)==')') ) {
                    tmp=tmp.trim();
                    if ((tmp!="") && (tmp.length()>0) && (tmp!="all")) {
                        if ( (tmp.equals("not")) || (tmp.equals("and")) || (tmp.equals("all")) ||  ( (!tmp.startsWith("prime-")) && (!(tmp.indexOf("_TOP_")>=0)) && (!(tmp.indexOf("_BOTTOM_")>=0)) ) ) {
                            //	System.out.println("skipped: " + tmp);
                        }                         
                        else  {
                            tmp = getStemmedConceptName(tmp);
                            if ( (tmp==null) || (tmp.equals(""))) {
                                tmp="";
                                continue;
                            }
                            if (map.containsKey(tmp)) {                   
                                map.put(tmp, new Integer(((Integer)map.get(tmp)).intValue()+1) );
                            }
                	       else
                	           map.put(tmp, new Integer(1));
                        	}
                        tmp="";                        
                    }                    
                }                
                else if (unfoldedConcept.charAt(i)==' ')
                    continue;
                else {
                    tmp=tmp+(unfoldedConcept.charAt(i));
                	}       
            }
        return map;
        */
    	
      }

public static void main(String[] args) {    
    String test = "and([prime-http://www.develin.de/owl#DieselFZ,and([all(http://www.develin.de/owl#faehrtMit,and([prime-http://www.develin.de/owl#Diesel,http://www.develin.de/owl#Krafstoff])),and([prime-http://www.develin.de/owl#KFZ,and([and([and([and([all(http://www.develin.de/owl#faehrtAuf,and([prime-http://www.develin.de/owl#Rad,_TOP_])),all(http://www.develin.de/owl#angetriebenDurch,and([prime-http://www.develin.de/owl#Motor,_TOP_]))]),all(http://www.develin.de/owl#moeglicherUntergrund,and([prime-http://www.develin.de/owl#Strasse,_TOP_]))]),all(http://www.develin.de/owl#faehrtMit,and([prime-http://www.develin.de/owl#Kraftstoff,_TOP_]))]),and([prime-http://www.develin.de/owl#FZ,_TOP_])])])])])";
    Map pc = getPrimitiveConcepts(test);
    CollectionUtils.print(pc);
    SimpleIndex index = SimpleIndex.instanceOf();
    index.addDocument("DieselFZ",pc);

    if (index.getIDF("_TOP_")==0.2)
        System.out.println("IDF of _TOP_: 0.2 ");
    else
        System.out.println("Miscalculated IDF of _TOP_ it should be 0.2 but is " + index.getIDF("_TOP_"));
	}

}
