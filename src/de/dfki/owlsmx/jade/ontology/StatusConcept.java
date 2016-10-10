/*
 * Created on 22.06.2006
 */
package de.dfki.owlsmx.jade.ontology;

import jade.content.Concept;

/**
 * This class implements the concept of a status of the matchmaker agent.
 * 
 * @author Patrick Kapahnke
 */
public class StatusConcept implements Concept {

    private int			numOfServices;
    
    /**
     * @param numOfServices the number of registered services
     */
    public void setNumOfServices(int numOfServices) {
        this.numOfServices = numOfServices;
    }
    
    /**
     * @return the number of registered services
     */
    public int getNumOfServices() {
        return numOfServices;
    }
}
