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
import java.util.SortedSet;
import java.util.TreeSet;

import de.dfki.owlsmx.analysis.PassedTimeContainer;
import de.dfki.owlsmx.gui.data.Query;
import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.SwingWorker;

public class RunQueriesTask {
    private int lengthOfTask;
    private int current = 1;
    private boolean done = false;
    private boolean canceled = false;
    private SortedSet queries = new TreeSet();
    private boolean isRunning = false;

    public RunQueriesTask() {
    	queries = TestCollection.getInstance().getQueries();    	
        lengthOfTask = queries.size();
        
    }

    /**
     * Called from ProgressBarDemo to start the task.
     */
    public void go() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                current = 1;
                done = false;
                canceled = false;
                return new ActualTask(queries);
            }
        };
        isRunning = true;
        worker.start();
    }
    
    public boolean isRunning() {
    	return isRunning;
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
        return "Processing request " + current + "/" + lengthOfTask + ".";
    }

    /**
     * The actual long running task.  This runs in a SwingWorker thread.
     */
    class ActualTask {
        ActualTask(SortedSet queries) {
        	PassedTimeContainer.getInstance().clear();
        	PassedTimeContainer.getInstance().setStartTime();
        	int processed = 0;
        	Query query;        	
	    	for(Iterator iter = queries.iterator(); iter.hasNext();current++) {
	    		query = (Query) iter.next();
	    		if (!query.isProcessed()) {
	    			TestCollection.getInstance().runQuery(query);
	    			processed++;
	    			if ( (GUIState.getInstance().getDisplayAQR()))
	    				PassedTimeContainer.getInstance().addState(current);
//	    			query.setProcessed(true);
	    		}
	            if (canceled)
	            	break;
	    	}                
	        done = true;
	        current = lengthOfTask;
	    }
        
    }
}
