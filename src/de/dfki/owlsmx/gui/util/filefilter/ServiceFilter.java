package de.dfki.owlsmx.gui.util.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ServiceFilter extends FileFilter  implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1750259913365792520L;

	//Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
    	if (f.isDirectory())
    		return true;
    	if ( (! (f.getAbsolutePath().indexOf(".")>0) ) ) {
            return false;
        }
        
        String extension = f.getAbsolutePath().toLowerCase();
        if (extension != null) {
        extension = extension.substring(extension.lastIndexOf("."));        
        	if ( (extension.indexOf("owl")>0) || (extension.indexOf("owls")>0) )
	                    return true;
	        else
	        	return false;	            
	        }

	    return false;
    	}

    //The description of this filter
    public String getDescription() {
    	return "OWLS Service (.owl, .owls)";
    }
}
