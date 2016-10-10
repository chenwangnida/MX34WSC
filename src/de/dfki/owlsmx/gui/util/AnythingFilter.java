package de.dfki.owlsmx.gui.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class AnythingFilter extends FileFilter {
    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
    	return true;
    	}

    //The description of this filter
    public String getDescription() {
    	return "Anything";
    }
}
