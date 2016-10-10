/*
 * 
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

public class HybridServiceItem extends ServiceItem implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int dom=-1;
	protected double similarity=-1;
	protected boolean datatypeCompatible = true;
	
	public HybridServiceItem(URI uri) {
		super(uri);
	}
	
	public String toString() { 
		String result = "(" + dom + ", " + ( (double)Math.round(similarity*100))/100 + ") " + name; 
		result = result.replaceAll("[\\r\\f]","");
		return result;
	}
	

	public HybridServiceItem(ServiceItem item) {
		this.uri = item.getURI();
		this.fileURI = item.fileURI;
		this.processed = item.processed;
		this.inputs = item.getInputs();
		this.outputs = item.getOutputs();		
		this.name = item.name;
		this.hasWSDLGrounding = item.hasWSDLGrounding;
		name = name.replaceAll("[\\r\\f]","");
	}
	
	public HybridServiceItem(HybridServiceItem item) {
		this.uri = item.getURI();
		this.fileURI = item.fileURI;
		this.processed = item.processed;
		this.inputs = item.getInputs();
		this.outputs = item.getOutputs();
		this.name = item.name;
		name = name.replaceAll("[\\r\\f]","");
		this.dom = item.getDegreeOfMatch();
		this.similarity = item.getSyntacticSimilarity();
		this.datatypeCompatible = item.datatypeCompatible;
	}
	
	public int getDegreeOfMatch() {
		return dom;
	}
	
	public void setDegreeOfMatch(int dom) {
		this.dom = dom;
	}
	
	public double getSyntacticSimilarity() {
		return similarity;
	}
	
	public void setSyntacticSimilarity(double similarity) {
		this.similarity = similarity;
	}
	
	public boolean isDataTypeCompatible() {
		return datatypeCompatible;
	}
	
	public void setDataTypeCompatible(boolean compatible) {
		datatypeCompatible = compatible;
	}
	
	public int compareTo(Object obj) {
		if (!this.getClass().equals(obj.getClass()))
			return 1;
		HybridServiceItem item = (HybridServiceItem) obj;
		if ( (dom < item.dom) || ((dom == item.dom) && (similarity<item.similarity)) )
				return -1;
		if ( ( (dom == item.dom) && (similarity == item.similarity) && (uri.equals(item.uri)) ) &&
				(this.uri.equals(item.uri)) && (this.fileURI.equals(item.fileURI)) )
			return 0;
		return -1;
	}	


}
