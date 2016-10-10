/*
 * Created on 21.11.2005
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

import java.io.File;
import java.io.FileNotFoundException;
import de.dfki.owlsmx.analysis.MemoryContainer;
import de.dfki.owlsmx.gui.data.Query;
import de.dfki.owlsmx.gui.data.ServiceItem;
import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.SwingWorker;


public class AddServiceToRelevanceset {


	    private int lengthOfTask;
	    private int current = 0;
	    private boolean done = false;
	    private boolean canceled = false;
	    private Query query;
	    File[] files;

	    public AddServiceToRelevanceset(File[] files, Query query) {
	    	if (files == null)
	    		return;
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
//	                statMessage = "Adding service to relevance set of query " + query.getName();
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
//	        statMessage = null;
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
	        return "Processing service " + current + "/" + lengthOfTask;
	    }

	    /**
	     * The actual long running task.  This runs in a SwingWorker thread.
	     */
	    class ActualTask {
	        ActualTask(File[] files, Query query) {
	            while ( (!canceled) && (!done) ) {
	            	ServiceItem tmpService;
	            	for(int i = 0; i<files.length;i++) {
	            		current = i;
	                	if ( (files[i].isFile()) && 
	 					   ( (files[i].getAbsolutePath().endsWith(".owl")) || 
	 					     (files[i].getAbsolutePath().endsWith(".owls") )) ) {
							try {
									tmpService = TestCollection.getInstance().addService(files[i].toURI());
									if (query==null)
										de.dfki.owlsmx.io.ErrorLog.instanceOf().report("Query was empty and I don't know why");
									if (tmpService==null)
										de.dfki.owlsmx.io.ErrorLog.instanceOf().report("Service was not found for file" + files[i] + " and query " + query);
									TestCollection.getInstance().addServiceToAnswerset(query.getURI(),tmpService.getURI());
								if (canceled)
									break;
							} catch (FileNotFoundException e) {
								GUIState.displayWarning(e.getClass().toString(), e.getMessage());
								e.printStackTrace();
							}
	                	 }                    
//	                    statMessage = "Completed " + i + "/" + lengthOfTask + ".";
	            	}                
	                done = true;
	            }
	        }
	    }
	}
