/* 
 * OWL-S Matchmaker
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
package de.dfki.owlsmx.gui.util.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class JPGFilter extends FileFilter  implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5012601654392224589L;

	//Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
    	if (f.isDirectory())
    		return true;
        if (f.getAbsolutePath().indexOf(".")<0) {
            return false;
        }
        
        String extension = f.getAbsolutePath().toLowerCase();
        if (extension != null) {
        extension = extension.substring(extension.lastIndexOf("."));        
        	if (extension.indexOf("jpg")>=0)
	                    return true;
	        else
	        	return false;	            
	        }

	    return false;
    	}

    //The description of this filter
    public String getDescription() {
    	return "JPG";
    }
	
}