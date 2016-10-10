/*
 * Created on 22.03.2005
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

import java.net.URI;

/**
 * A simple tokenizer which will retrieve only URLs as token 
 * 
 * @author bEn
 *
 */
public class URLTokenizer extends Tokenizer {

    protected boolean isTokenChar(char c) {
        return ( (!Character.isWhitespace(c)) && ( Character.isLetterOrDigit(c) || (c==':') || (c=='/') || (c=='#') ) );
    }
    
    boolean isURL(String token) {
        try {
            new URI(token);
        }
        catch (Exception e) {
            return false;
        }
        return ( ((token.indexOf("file:")>=0) || ( token.indexOf("http:")>=0 ) || token.startsWith("#")) && (token.indexOf("#")>=0) );
    }

    protected void filterToken(String token) {
        if ( (token==null) || (token.equals("")))
            return;        
        if (token.indexOf("file:")>=0)
            token = token.substring(token.indexOf("file:"));
        if ( token.indexOf("http:")>=0 )
            token = token.substring(token.indexOf("http:"));
        if (isURL(token))
            updateToken(token);
    }
}
