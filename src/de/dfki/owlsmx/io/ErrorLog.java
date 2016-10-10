/*
 * Created on 10.03.2005
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
package de.dfki.owlsmx.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * A primitve Errorlog which is used to keep track of problems which appear
 * internally
 * 
 * 
 * @author Ben Fries
 *
 */
public final class ErrorLog {

    static ErrorLog error = new ErrorLog();
    final static String filename = "error.data";
    static final boolean useErrorlog = true;
    static final boolean debug = false;
    static final boolean display = false;
    FileWriter writer;
    
    /**
     * 	Empty constructor
     */
    private ErrorLog(){
    }
    
    /**
     * @return	Single instance of the ErrorLog
     */
    public static ErrorLog instanceOf() {
        return error;
    }
    
    /**
     * @param text	Text which should be added to the log
     */
    public void report(String text) {
        if (!ErrorLog.useErrorlog)
            return;
        debug(text);
        try {
        	if (writer==null) {
    	        writer = new FileWriter(filename,false);      
    	        writer.write("Errorlog " +(new Date()).toString() +"\n");    
        	}
            writer.write(text + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
      
    }
    
    
    public static void debug(String text) {
        if (display)
        	System.err.println( text);
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    protected void finalize() {
        try {
	        if (writer!=null) {
	        	writer.close();
	        	return;
	        }
	        File file = new File("error.data");
	        if (file.exists()) {
	           	file.delete();
	        }       
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }
}
