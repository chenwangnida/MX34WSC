/*
 * Created on 06.06.2006
 */
package de.dfki.owlsmx.jade.ontology;

import jade.content.AgentAction;
import jade.util.leap.List;

/**
 * This cass implements an agent action, which models the act of removing
 * a collection of serivces from the service registry of the matchmaker.
 * 
 * @author Patrick Kapahnke
 */
public class RemoveServicesAction implements AgentAction {
    
    private List		serviceList;
    
    /**
     * @param serviceList a <code>List</code> of <code>ServiceConcept</code> objects. Only the uri of the service is relavant for the remove operation
     */
    public void setServiceList(List serviceList) {
        this.serviceList = serviceList;
    }
    
    /**
     * @return a <code>List</code> of <code>ServiceConcept</code> objects
     */
    public List getServiceList() {
        return serviceList;
    }

}
