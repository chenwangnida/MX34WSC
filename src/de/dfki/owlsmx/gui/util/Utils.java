package de.dfki.owlsmx.gui.util;

public class Utils {

    public static String getConcept(String line) {     
    	return getConcept(line, false);
    }
    
    public static String getConcept(java.net.URI line) {     
    	return getConcept(line.toString());
    }
    
    public static String getConcept(java.net.URI line, boolean removeHash) {     
    	return getConcept(line.toString(), removeHash);
    }

    public static String getConcept(String line, boolean removeHash) {     
        if (!(line.toLowerCase().indexOf("http://")>=0))
            return line;
        else if (line.toLowerCase().indexOf("file:/")>=0)
            return line;
        if (removeHash)
        	return line.substring(line.indexOf("#")+1);
        return line.substring(line.indexOf("#"));
    }
}
