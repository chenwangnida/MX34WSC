/*
 * Created on 31.05.2006
 */
package de.dfki.owlsmx.jade.ontology;

import jade.content.Concept;

/**
 * This class implements a concept for a web service. It contains fields for the URI
 * of the service and it's content as <code>String</code>. The content is not mandatory.
 * 
 * @author Patrick Kapahnke
 */
public class ServiceConcept implements Concept {

    private String		serviceContent = null;
    private String		serviceURI;
    
    public void setContent(String serviceContent) {
        this.serviceContent = serviceContent;
    }
    
    public String getContent() {
        return serviceContent;
    }
    
    public void setURI(String serviceURI) {
        this.serviceURI = serviceURI;
    }
    
    public String getURI() {
        return serviceURI;
    }
}
