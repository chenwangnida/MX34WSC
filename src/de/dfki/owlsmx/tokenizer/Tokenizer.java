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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A generic tokenizer
 * 
 * @author bEn
 *
 */
public abstract class Tokenizer{
    String string="";
    Map tokens = new HashMap();    
    Iterator tokenIterator;
   
    public Tokenizer() {
        
    }
    
    public Tokenizer(String string) {
        this.string = string;
        tokenize();
    }
          
    public boolean hasNext() {
        return ( (tokenIterator!=null) && (tokenIterator.hasNext()) );
    }
 
    public String next() throws Exception {
        if (tokenIterator==null)
            tokenIterator = iterator();
        if (hasNext())
            return (String) tokenIterator.next();
        else
            throw new Exception("Tokenizer out of bound");
    }
    
    public void clear() {
        tokenIterator = null;
        tokens = new HashMap();
        this.string = "";  
    }
    
    public void setString(String string) {
        clear();
        this.string = string;        
        tokenize();
    }
    
    public int getFrequency(String token) {
        if (!tokens.containsKey(token))
            return 0;
        else
            return ((Integer)tokens.get(token)).intValue();
    }
    
    public Iterator iterator() {
        return tokens.keySet().iterator();
    }
    
    public Set getTokens(){
        return tokens.keySet();
    }
    
    public Map getTokenFrequencies() {
        return tokens;
    }
    
    protected void tokenize() {
        if ( (this.string==null) || (this.string=="") )
            return;
        String token = "";
        char   tmpChar;
        char[] characters = string.toCharArray();
        for (int i=0; i<characters.length; i++) {
            tmpChar = characters[i];
            if (isTokenChar(tmpChar)) {
                token += tmpChar;
                if (i == characters.length-1)
                    filterToken(token);
            }
            else if (token!=""){
            	//System.out.println( i + " " + token);
                filterToken(token);
                token = "";
            }                
        }
    }
       
    protected void updateToken(String token) {
        int frequency=0;
        if (tokens.containsKey(token))
            frequency = ((Integer)tokens.get(token)).intValue();
        tokens.put(token,new Integer(frequency+1));
    }
    
    protected boolean isTokenChar(char c) {
        return !Character.isWhitespace(c);
    }    
    
    protected void filterToken(String token) {
        if (!token.equals(""))
            updateToken(token);
    }
    
    public Map getTokenFrequencies(String string) {
        this.setString(string);
        return this.getTokenFrequencies();
    }
    
}	
