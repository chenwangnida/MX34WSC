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
 */
package de.dfki.owlsmx.gui.data;

import java.net.URI;

public class Query extends ServiceItem implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//private Set answerset = new HashSet();
	
	public Query(URI uri) {
		super(uri);		
	}
	/*
	public void addServiceToAnswerset(ServiceItem item){
		answerset.add(item);
	}
	
	public Set getAnswerset() {
		return answerset;
	}
*/
}
