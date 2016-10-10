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
package de.dfki.owlsmx.gui.data;

import java.io.Serializable;
import java.util.regex.Pattern;

import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.TabState;

public class TaskContent implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Integer task;
	public int step;
	public int totalSteps;
	public int screen;
	public String taskTitle;
	public String screenTitle;
	public String screenText;
	
	//NO_TASK;Select task;1;1;TASK;Taskselection;Just some initial screen
	public TaskContent(String content) {
		Pattern p = Pattern.compile(";");
		String[] items = p.split(content);
		if (items.length!=7)
			return;
		task = switchTask(items[0]);
		taskTitle = items[1];
		totalSteps = Integer.parseInt(items[2]);
		step = Integer.parseInt(items[3]);		
		screen = switchTab(items[4]); 
		screenTitle = items[5];
		screenText = items[6];
	}
	
	private Integer switchTask(String task){
		if (task.toUpperCase().equals("NO_TASK"))
			return new Integer(GUIState.NO_TASK);
		else if (task.toUpperCase().equals("QUERY_TASK"))
			return new Integer(GUIState.QUERY_TASK);
		else if (task.toUpperCase().equals("CREATE_TC_TASK"))
			return new Integer(GUIState.CREATE_TC_TASK);
		else if (task.toUpperCase().equals("RUN_TEST_TASK"))
			return new Integer(GUIState.RUN_TEST_TASK);
		return  new Integer(GUIState.OTHER_TASK);
	}
	
	private int switchTab(String tab) {
        if (tab.toUpperCase().equals("TASK"))
            return TabState.TASK;
        if (tab.toUpperCase().equals("SERVICES"))
            return TabState.SERVICES;
        if (tab.toUpperCase().equals("QUERIES"))
            return TabState.QUERIES;
        if (tab.toUpperCase().equals("ANSWERSET"))
            return TabState.ANSWERSET;
        if (tab.toUpperCase().equals("TESTCOLLECTION"))
            return TabState.TESTCOLLECTION;
        if (tab.toUpperCase().equals("MATCHMAKER"))
            return TabState.MATCHMAKER;
        if (tab.toUpperCase().equals("EVALUATION"))
            return TabState.EVALUATION;
        if (tab.toUpperCase().equals("RESULT"))
            return TabState.RESULT;
        if (tab.toUpperCase().equals("PERFORMANCE"))
            return TabState.PERFORMANCE;
        /* 
        if (tab.equals(gui_settings))
            return TabState.SETTINGS;
        if (tab.equals(gui_intro))
            return TabState.ABOUT;
         */
        return TabState.UNKNOWN;
    }
	
	public String toString() {
		return task.toString() + ". " + taskTitle + " Step " + step +"/" + totalSteps + ": " + screenTitle;
	}
}
