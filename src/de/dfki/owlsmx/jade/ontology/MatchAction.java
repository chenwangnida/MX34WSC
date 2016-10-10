/*
 * Created on 06.06.2006
 */
package de.dfki.owlsmx.jade.ontology;

import jade.content.AgentAction;

/**
 * This class implements the agent action which models the act
 * of matchmaking using specific parameters.
 * 
 * @author Patrick Kapahnke
 */
public class MatchAction implements AgentAction {

    private ServiceConcept			service;
    private int				variant;
    private int				minimumDOM = 5;
    private double			minimumSIM = 0.0;
    private int				sortingType = 0;
    
    public void setService(ServiceConcept service) {
        this.service = service;
    }
    
    public ServiceConcept getService() {
        return service;
    }
    
    /**
     * @param variant a OWLS-MX matchmaking variant M0-M4 as defined in <code>de.dfki.owlsmx.similaritymeasures.SimilarityMeasure</code>
     */
    public void setVariant(int variant) {
        this.variant = variant;
    }

    /**
     * @return a OWLS-MX matchmaking variant M0-M4 as defined in <code>de.dfki.owlsmx.similaritymeasures.SimilarityMeasure</code>
     */
    public int getVariant() {
        return variant;
    }
    
    /**
     * @param minimumDOM the minimum degree of match as defined in <code>de.dfki.owlsmx.data.DegreeOfMatch</code>
     */
    public void setMinimumDOM(int minimumDOM) {
        this.minimumDOM = minimumDOM;
    }

    /**
     * @return the minimum degree of match as defined in <code>de.dfki.owlsmx.data.DegreeOfMatch</code>
     */
    public int getMinimumDOM() {
        return minimumDOM;
    }
    
    /**
     * @param minimumSIM the syntactic similarity treshold, when using variant M1-M4. For variant M0, this value is useless
     */
    public void setMinimumSIM(double minimumSIM) {
        this.minimumSIM = minimumSIM;
    }
    
    /**
     * @return the syntactic similarity treshols
     */
    public double getMinimumSIM() {
        return minimumSIM;
    }
    
    /**
     * @param sortingType the sorting type of the matching result as defined in <code>de.dfki.owlsmx.data.SortingType</code>
     */
    public void setSortingType(int sortingType) {
        this.sortingType = sortingType;
    }
    
    /**
     * @return the sorting type of the matching result as defined in <code>de.dfki.owlsmx.data.SortingType</code>
     */
    public int getSortingType() {
        return sortingType;
    }
}