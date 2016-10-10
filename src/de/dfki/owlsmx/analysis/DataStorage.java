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

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public abstract class DataStorage {
	protected Map storage = new TreeMap();
	protected long baseValue;
	
	/**
	 *	Empty Constructor 
	 */
	public DataStorage() {
	}
	
	
	public void clear() {
		baseValue = 0;
		storage.clear();
	}
	
	public Map getStoredValues() {
		return storage;
	}
	

	public long getBaseValue() {
		return baseValue;
	}
	

	public void setBaseValue(long baseValue) {
		this.baseValue = baseValue;
	}
	
	abstract public void addState(int serviceCount);
	
	public String toString() {
		String result = "";
		Map.Entry me;
		for(Iterator iter = storage.entrySet().iterator();iter.hasNext();) {
			me = (Map.Entry) iter.next();
			result += me.getKey() + " " + me.getValue() + "\n";
		}
		return result;
	}
}
