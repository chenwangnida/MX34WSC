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


public class PassedTimeContainer extends de.dfki.owlsmx.analysis.DataStorage {

	private static PassedTimeContainer _instance = new PassedTimeContainer();
    private final boolean debug = true;
	
	
	public static PassedTimeContainer getInstance() {
		if (_instance==null)
			_instance = new PassedTimeContainer();
		return _instance;
	}
	
	protected PassedTimeContainer() {
		baseValue = getCurrentTime();
	}
	
	private long getCurrentTime() {
		return System.currentTimeMillis();
	}
	
	public long getCurrentProcessingTime() {
		
		return getCurrentTime() - baseValue;
	}
	
	public void setStartTime() {
		baseValue = getCurrentTime();		
	}
	
	public void addState(int serviceCount) {
		if (debug)
			System.err.print("Time: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
		if (serviceCount==0)
			storage.put(new Integer(0), new Long(0));
//		System.err.println(this.getClass().toString() + "|addState: Services" + serviceCount + " / " + (getCurrentProcessingTime())/serviceCount) ;
		storage.put(new Integer(serviceCount), new Long((getCurrentProcessingTime())/serviceCount));
	}
}
