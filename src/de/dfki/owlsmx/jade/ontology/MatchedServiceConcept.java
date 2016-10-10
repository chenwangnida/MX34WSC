/*
 * Created on 31.05.2006
 */
package de.dfki.owlsmx.jade.ontology;

import jade.content.Concept;

/**
 * This class implements the concept of a matched service including service information
 * as well as the determined degree of match and syntactic similarity.
 * 
 * @author Patrick Kapahnke
 */
public class MatchedServiceConcept implements Concept {
    
    private ServiceConcept		service;
    private int			degreeOfMatch;
    private double		syntacticSimilarity;
    
    public void setService(ServiceConcept service) {
        this.service = service;
    }
    
    public ServiceConcept getService() {
        return service;
    }
        
    /**
     * @param degreeOfMatch the degree of match of the matched service as defined in <code>de.dfki.owlsmx.data.DegreeOfMatch</code>
     */
    public void setDOM(int degreeOfMatch) {
        this.degreeOfMatch = degreeOfMatch;
    }
    
    /**
     * @return the degree of match of the matched service as defined in <code>de.dfki.owlsmx.data.DegreeOfMatch</code>
     */
    public int getDOM() {
        return degreeOfMatch;
    }
    
    /**
     * @param syntacticSimilarity the determined syntactic similarity value for this service
     */
    public void setSIM(double syntacticSimilarity) {
        this.syntacticSimilarity = syntacticSimilarity;
    }
    
    /**
     * @return the determined syntactic similarity value for this service
     */
    public double getSIM() {
        return syntacticSimilarity;
    }
}
