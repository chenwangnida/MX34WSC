/*
 * Created on 10.03.2005
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
package de.dfki.owlsmx.tokenizer;

import java.util.HashSet;
import java.util.Set;


/**
 * A simple tokenizer which will compute the primitive concepts for a given string
 * 
 * @author bEn
 *
 */
public class PrimitiveConceptTokenizer extends Tokenizer{
    //PorterStemmer stemmer = PorterStemmer.instanceOf();
    Set skipWords = new HashSet();
    
    public PrimitiveConceptTokenizer() {
        constructor();
    }
    
    /**
     * @param string
     */
    public PrimitiveConceptTokenizer(String string) {
        super(string);
        constructor();
    }
    
    private void constructor() {
        /*
        */
        skipWords.add("all");
        skipWords.add("and");
        skipWords.add("all");
        skipWords.add("not");
        skipWords.add("prime");
        
    }
    
    protected boolean isTokenChar(char c) {
        return ( (!Character.isWhitespace(c)) && ( Character.isLetterOrDigit(c) || (c==':') || (c=='/')  || (c=='.')  || (c=='-')  || (c=='_')) );
    }
    
    protected boolean isNumber(String number){
    	try {
    	Integer.parseInt(number.trim());
    		return true;
    	}
    	catch(Exception e) {
    		return false;
    	}
    	
    }
    
    protected void filterToken(String token) {
        token = token.toLowerCase();
        if ( (token==null) || (token.equals("")) || (token.indexOf("file:")>=0) || (token.indexOf("http:")>=0) || (isNumber(token)) )
            return;
        if (!skipWords.contains(token))
            updateToken(token);
    }
    
    public static void main(String[] args) {
    	PrimitiveConceptTokenizer token = new PrimitiveConceptTokenizer();
    	token.setString("and([prime-http://127.0.0.1/ontology/my_ontology.owl#4WheeledCar,and([prime-http://127.0.0.1/ontology/my_ontology.owl#Car,and([prime-http://127.0.0.1/ontology/my_ontology.owl#Auto,and([and([and([and([and([and([and([and([and([and([and([and([and([and([and([and([all(http://127.0.0.1/ontology/my_ontology.owl#Power,http://127.0.0.1/ontology/my_ontology.owl#MoveableThing),all(http://127.0.0.1/ontology/my_ontology.owl#Profitable,and([prime-http://127.0.0.1/ontology/my_ontology.owl#DesignedThing,all(http://127.0.0.1/ontology/my_ontology.owl#designedBy,and([prime-http://127.0.0.1/ontology/my_ontology.owl#Designer,http://127.0.0.1/ontology/books.owl#Person]))]))]),max(http://127.0.0.1/ontology/my_ontology.owl#Wheel,4)]),min(http://127.0.0.1/ontology/my_ontology.owl#Color,1)]),all(http://127.0.0.1/ontology/my_ontology.owl#hasValue,_TOP_)]),all(http://127.0.0.1/ontology/my_ontology.owl#madeBy,http://127.0.0.1/ontology/my_ontology.owl#Company)]),all(http://127.0.0.1/ontology/my_ontology.owl#Model,and([prime-http://127.0.0.1/ontology/my_ontology.owl#DesignedThing,all(http://127.0.0.1/ontology/my_ontology.owl#designedBy,and([prime-http://127.0.0.1/ontology/my_ontology.owl#Designer,http://127.0.0.1/ontology/books.owl#Person]))]))]),all(http://127.0.0.1/ontology/my_ontology.owl#LifeTime,and([prime-http://127.0.0.1/ontology/my_ontology.owl#DesignedThing,all(http://127.0.0.1/ontology/my_ontology.owl#designedBy,and([prime-http://127.0.0.1/ontology/my_ontology.owl#Designer,http://127.0.0.1/ontology/books.owl#Person]))]))]),max(http://127.0.0.1/ontology/my_ontology.owl#Person,5)]),and([prime-http://127.0.0.1/ontology/my_ontology.owl#WheeledVehicle,and([prime-http://127.0.0.1/ontology/my_ontology.owl#Vehicle,_TOP_])])]),and([prime-http://127.0.0.1/ontology/my_ontology.owl#Machine,and([http://127.0.0.1/ontology/simplified_sumo.owl#Object,not(http://127.0.0.1/ontology/simplified_sumo.owl#ElectricDevice)])])]),all(http://127.0.0.1/ontology/my_ontology.owl#Shape,and([prime-http://127.0.0.1/ontology/my_ontology.owl#DesignedThing,all(http://127.0.0.1/ontology/my_ontology.owl#designedBy,and([prime-http://127.0.0.1/ontology/my_ontology.owl#Designer,http://127.0.0.1/ontology/books.owl#Person]))]))]),and([prime-http://127.0.0.1/ontology/my_ontology.owl#PeopleTransporter,http://127.0.0.1/ontology/my_ontology.owl#Transporter])]),min(http://127.0.0.1/ontology/my_ontology.owl#Engine,1)]),all(http://127.0.0.1/ontology/my_ontology.owl#Rigid,and([prime-http://127.0.0.1/ontology/my_ontology.owl#DesignedThing,all(http://127.0.0.1/ontology/my_ontology.owl#designedBy,and([prime-http://127.0.0.1/ontology/my_ontology.owl#Designer,http://127.0.0.1/ontology/books.owl#Person]))]))]),all(http://127.0.0.1/ontology/my_ontology.owl#belongsTo,http://127.0.0.1/ontology/books.owl#Person)]),all(http://127.0.0.1/ontology/my_ontology.owl#Speed,http://127.0.0.1/ontology/my_ontology.owl#MoveableThing)])])])])and([prime-http://127.0.0.1/ontology/my_ontology.owl#1PersonBicycle,and([and([prime-http://127.0.0.1/ontology/my_ontology.owl#Bicycle,and([min(http://127.0.0.1/ontology/my_ontology.owl#Wheel,2),and([prime-http://127.0.0.1/ontology/my_ontology.owl#Cycle,http://127.0.0.1/ontology/simplified_sumo.owl#Object])])]),min(http://127.0.0.1/ontology/my_ontology.owl#Person,1)])])");
    	for (java.util.Iterator iter = token.getTokenFrequencies().entrySet().iterator(); iter.hasNext();)
    		System.out.println(((java.util.Map.Entry)iter.next()).getKey());
    }

}
