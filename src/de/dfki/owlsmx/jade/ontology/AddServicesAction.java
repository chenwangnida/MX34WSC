/*
 * Created on 31.05.2006
 */
package de.dfki.owlsmx.jade.ontology;

import jade.content.AgentAction;
import jade.util.leap.List;

/**
 * This class implements the agent action which models the act
 * of adding services to the service registry of the matchmaker.
 * 
 * @author Patrick Kapahnke
 */
public class AddServicesAction implements AgentAction {
    
    private List		servicesToAdd;
    
    /**
     * @param servicesToAdd a <code>List</code> of <code>ServiceConcept</code> objects
     */
    public void setServiceList(List servicesToAdd) {
        this.servicesToAdd = servicesToAdd;
    }
    
    /**
     * @return a <code>List</code> of <code>ServiceConcept</code> objects
     */
    public List getServiceList() {
        return servicesToAdd;
    }
}
