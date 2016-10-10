/*
 * Created on 28.10.2005
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

package de.dfki.owlsmx.gui.util.tasks;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import de.dfki.owlsmx.analysis.MemoryContainer;
import de.dfki.owlsmx.analysis.PassedTimeContainer;
import de.dfki.owlsmx.gui.data.ServiceItem;
import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.MatchmakerInterface;
import de.dfki.owlsmx.gui.util.SwingWorker;

public class AddServicesToMatchmakerTask {
    private int lengthOfTask;
    private int current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private Set services = new TreeSet();

    public AddServicesToMatchmakerTask() {
    	services = TestCollection.getInstance().getServices();    	
        lengthOfTask = services.size();
        
    }

    /**
     * Called from ProgressBarDemo to start the task.
     */
    public void go() {
    	MemoryContainer.getInstance().clear();
		MemoryContainer.getInstance().setGUIMemory();
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                current = 1;
                done = false;
                canceled = false;
                return new ActualTask(services);
            }
        };
        worker.start();
    }

    /**
     * Called from ProgressBarDemo to find out how much work needs
     * to be done.
     */
    public int getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     * Called from ProgressBarDemo to find out how much has been done.
     */
    public int getCurrent() {
        return current;
    }

    public void stop() {
        canceled = true;
    }

    /**
     * Called from ProgressBarDemo to find out if the task has completed.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Returns the most recent status message, or null
     * if there is no current status message.
     */
    public String getMessage() {
        return "Processing service " + current + "/" + lengthOfTask + ".";
    }

    /**
     * The actual long running task.  This runs in a SwingWorker thread.
     */
    class ActualTask {
        ActualTask(Set services) {        	
        	MatchmakerInterface.getInstance().createMatchmaker();
        	ServiceItem service;
        	PassedTimeContainer.getInstance().setStartTime();
        	de.dfki.owlsmx.io.ErrorLog.debug(this.getClass().toString() + "Services to add: " + services.size());
	    	for(Iterator iter = services.iterator(); iter.hasNext();current++) {	    		
	    		service = (ServiceItem) iter.next();
//	    		if (!service.isProcessed()) {
	    			//System.err.println("ActualTask: " + service.getURI());
	    		de.dfki.owlsmx.io.ErrorLog.debug(this.getClass().toString()+": Adding service: " + service.getURI().toString());
	    			MatchmakerInterface.getInstance().addService(service.getURI());
	    			if ( (GUIState.getInstance().getMemoryConsumption()))				
		    			MemoryContainer.getInstance().addState(current);
	    			//TestCollection.getInstance().setProcessed(service,true);
//	    		}
//	    		else
//	    			System.err.println("Skipped service " + service.getURI());
	    		if (canceled)
	            	break;
	    	}                
	        done = true;
	        current = lengthOfTask;
	        MemoryContainer.getInstance().addState(lengthOfTask);
	    }
        
    }
}
