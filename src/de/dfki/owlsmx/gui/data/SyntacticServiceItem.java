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

import java.net.URI;

public class SyntacticServiceItem extends HybridServiceItem implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SyntacticServiceItem(URI uri) {
		super(uri);
	}
	
	public SyntacticServiceItem(ServiceItem item) {
		super(item);
	}
	
	public SyntacticServiceItem(HybridServiceItem item){
		super(item);
	}
	
	public String toString() {
		String result = ( (double)Math.round(similarity*100))/100 + " - " + name;
		result = result.replaceAll("[\\r\\f]","");
		return result;
	}

	public int compareTo(Object obj) {
		if (!this.getClass().equals(obj.getClass()))
			return 1;
		SyntacticServiceItem item = (SyntacticServiceItem) obj;
		if (similarity<item.similarity)
				return -1;
		if ( (similarity == item.similarity)  &&
				(this.uri.equals(item.uri)) && (this.fileURI.equals(item.fileURI)) )
			return 0;
		return -1;
	}	
}
