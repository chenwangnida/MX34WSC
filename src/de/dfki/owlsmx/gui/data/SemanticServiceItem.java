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

public class SemanticServiceItem extends HybridServiceItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SemanticServiceItem(URI uri) {
		super(uri);
	}
	
	public SemanticServiceItem(ServiceItem item) {
		super(item);
	}
	
	public SemanticServiceItem(HybridServiceItem item){
		super(item);
	}

	public String toString() {
		String result = "(" + dom + " - " + name;
		result = result.replaceAll("[\\r\\f]","");
		return result;
	}


	public int compareTo(Object obj) {
		if (!this.getClass().equals(obj.getClass()))
			return 1;
		SemanticServiceItem item = (SemanticServiceItem) obj;
		if (dom<item.dom)
				return -1;
		if ( (dom == item.dom) && (this.uri.equals(item.uri)) && (this.fileURI.equals(item.fileURI)) )
			return 0;
		return -1;
	}	
}
