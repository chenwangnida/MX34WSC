/*
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
 * 
 */
package de.dfki.owlsmx.gui.util.tasks;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;

import de.dfki.owlsmx.analysis.MemoryContainer;
import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.SwingWorker;

public class AddServicesToCollectionTask {
    private int lengthOfTask;
    private int current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private boolean query = false;
    File[] files;
    Component comp;

    public AddServicesToCollectionTask(Component comp,File[] files, boolean query) {
    	if (files == null)
    		return;
    	this.comp = comp;
        lengthOfTask = files.length-1;
        this.query = query;
        this.files = files; 
    }

    /**
     * Called from ProgressBarDemo to start the task.
     */
    public void go() {
    	MemoryContainer.getInstance().setGUIMemory();
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                current = 0;
                done = false;
                canceled = false;
                return new ActualTask(files, query);
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
    	if (!query)
    		return "Processing service " + current + "/" + lengthOfTask;
        return "Processing service request " + current + "/" + lengthOfTask;
    }

    /**
     * The actual long running task.  This runs in a SwingWorker thread.
     */
    class ActualTask {
        ActualTask(File[] files, boolean query) {
        	int adding = 0;
        	int before = 0;
        	int before_query =  0;
        	if (!query)
        		before = TestCollection.getInstance().getServices().size();
        	else
        		before = TestCollection.getInstance().getQueries().size();
            while ( (!canceled) && (!done) ) {
            	for(int i = 0; i<files.length;i++) {
            		current = i;
            		
                	if ( (files[i].isFile()) && 
 					   ( (files[i].getAbsolutePath().endsWith(".owl")) || 
 					     (files[i].getAbsolutePath().endsWith(".owls") )) ) {
						try {
							before_query = TestCollection.getInstance().getServices().size();
							if (!query)
								TestCollection.getInstance().addService(files[i].toURI());
							else {								
								TestCollection.getInstance().addQuery(files[i].toURI());
							}
							if (TestCollection.getInstance().getServices().size()==before_query)
				            	de.dfki.owlsmx.io.ErrorLog.instanceOf().report(
				            			"Service " + files[i].toURI() + " could not be added");
							adding++;
							if (canceled)
								break;
						} catch (FileNotFoundException e) {
							GUIState.displayWarning(e.getClass().toString(), e.getMessage());
							e.printStackTrace();
						}
                	 }                    
                    //statMessage = "Completed " + i + " out of " + lengthOfTask + ".";
            	}                
                done = true;
                current = lengthOfTask;
            }
            
            if (!query)
            	GUIState.displayWarning(comp,"Adding services to test collection",
            			"Added " + (TestCollection.getInstance().getServices().size() - before) + "/" + adding + " services");
            else
            	GUIState.displayWarning(comp,"Adding service requests to test collection",
            			"Added " + (TestCollection.getInstance().getQueries().size() - before) + "/"  + adding + " service requests");
        }
    }
}

