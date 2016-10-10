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
package de.dfki.owlsmx.gui;


import java.util.Set;

import de.dfki.owlsmx.gui.util.UpdateDataListener;

public class Result extends javax.swing.JPanel implements UpdateDataListener {

	AnswerSet simple = new AnswerSet();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Result() {
		//setLayout(new FlowLayout());
		setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
		add(new ResultOpenWindow(), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 400));
		add(simple, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 0, 650, 450));
	}
	
	public Result(Set result) {
		//setLayout(new FlowLayout());
		setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
		add(new ResultOpenWindow(result), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 400));
		add(new AnswerSet(result), new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 0, 650, 450));
	}

	public void updateData() {
		simple.updateData();		
	}

}
