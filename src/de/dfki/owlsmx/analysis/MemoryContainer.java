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

package de.dfki.owlsmx.analysis;

import de.dfki.owlsmx.utils.MatchmakerUtils;

public class MemoryContainer extends de.dfki.owlsmx.analysis.DataStorage  {
	private static MemoryContainer _instance = null;
    private final double waiting = 1.5;
    private final boolean debug = true;
	
	public static MemoryContainer getInstance() {
		if (_instance==null)
			_instance = new MemoryContainer();
		return _instance;
	}

	private MemoryContainer() {
		baseValue = 0;
	}

	private long getCurrentMemory() {
		if (debug)
			System.err.print("Memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
		MatchmakerUtils.wait(waiting);
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}
	
	public long getCurrentMatchmakerMemory() {
		return getCurrentMemory()-baseValue;
	}
	
	public void setGUIMemory() {
//		baseValue = getCurrentMemory();		
	}
	
	public void addState(int serviceCount) {
		de.dfki.owlsmx.utils.MatchmakerUtils.wait(3);
		storage.put(new Integer(serviceCount), new Long(getCurrentMatchmakerMemory()/1024));
	}
	
}
