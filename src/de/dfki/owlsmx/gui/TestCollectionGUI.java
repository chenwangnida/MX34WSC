/*
 * TestCollection.java
 *
 * Created on 30. Juli 2005, 13:56
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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.UpdateDataListener;

/**
 * GUI responsible for saving and loading of the testcollection 
 * and specifying name, version and comment.
 *
 * @author  Ben
 */
public class TestCollectionGUI extends javax.swing.JPanel implements ActionListener,UpdateDataListener {
    
	// Variables declaration - do not modify
	private javax.swing.JTextArea comment;
	private javax.swing.JLabel comment_label;
	private javax.swing.JScrollPane comment_scroll;
	private javax.swing.JButton loadTC;
	private javax.swing.JButton clearTC;
	private javax.swing.JLabel logo;
	private javax.swing.JTextField name;
	private javax.swing.JLabel name_label;
	private javax.swing.JButton saveTC;
	private javax.swing.JTextField version;
	private javax.swing.JLabel version_label;
	private JFileChooser fc_open = new JFileChooser();
	private JFileChooser fc_close = new JFileChooser();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates new form TestCollection 
     */
    public TestCollectionGUI() {
        initComponents();
        fc_close.setDialogTitle("Save Testcollection");
    	//fc_close.setDialogType(JFileChooser.SAVE_DIALOG);
    	fc_close.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	fc_open.setDialogTitle("Load Testcollection");
    	fc_open.setDialogType(JFileChooser.OPEN_DIALOG);
    	fc_open.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }

    private void initComponents() {
        saveTC = new javax.swing.JButton();
        clearTC = new javax.swing.JButton();
        name = new javax.swing.JTextField();
        name.addActionListener(this);
        name_label = new javax.swing.JLabel();
        version_label = new javax.swing.JLabel();
        version = new javax.swing.JTextField();
        version.addActionListener(this);
        loadTC = new javax.swing.JButton();
        comment_label = new javax.swing.JLabel();
        comment_scroll = new javax.swing.JScrollPane();
        comment = new javax.swing.JTextArea();        
        logo = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setMinimumSize(new java.awt.Dimension(800, 400));
        setPreferredSize(new java.awt.Dimension(800, 450)); 
        
       
        
        saveTC.setText("Save test collection");
        saveTC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	saveTCActionPerformed(evt);
            }
        });
        add(saveTC, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 40, 250, -1));
        
        clearTC.setText("Clear current test collection");
        clearTC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	clearTC();            	
            }
        });
        add(clearTC, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 70, 250, -1));
        

        add(name, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 410, -1));

        name_label.setText("Name");
        add(name_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 410, -1));

        version_label.setText("Version");
        add(version_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 410, -1));

        add(version, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, 410, -1));

        loadTC.setText("Load test collection");
        loadTC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadTCActionPerformed(evt);
            }
        });

        add(loadTC, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 10, 250, -1));

        comment_label.setText("Comments");
        add(comment_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 110, 400, -1));

        comment_scroll.setViewportView(comment);

        add(comment_scroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, 410, 250));

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource(GUIState.logoPath)));
        add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, -1, -1));

    }

    private void updateTCDetails() {
    	TestCollection.getInstance().setName(name.getText());
    	TestCollection.getInstance().setVersion(version.getText());
    	TestCollection.getInstance().setComment(comment.getText());
    }
    
    private void loadTCDetails() {
//    	System.err.println("TC Name:" + TestCollection.getInstance().getName());
    	name.setText(TestCollection.getInstance().getName());    	
    	version.setText(TestCollection.getInstance().getVersion());
    	comment.setText(TestCollection.getInstance().getComment());
    }
    
    private void loadTCActionPerformed(java.awt.event.ActionEvent evt) {
        if (fc_open.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			TestCollection.getInstance().load(fc_open.getSelectedFile().getAbsolutePath(),this);
//			System.out.println("Trying to load " + fc_open.getSelectedFile().getAbsolutePath());
			loadTCDetails();
			TestCollection.getInstance().updateData();
        }
        
    }
    
    private void saveTCActionPerformed(java.awt.event.ActionEvent evt) {    	
    	updateTCDetails(); 	
        if (fc_close.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            TestCollection.getInstance().save(fc_close.getSelectedFile().getAbsolutePath());
        }
    }
    
	public void actionPerformed(ActionEvent event) {
		updateTCDetails();	
	}
    
	public void clearTC() {
		if (!(JOptionPane.showConfirmDialog(this, "Should the current test collection really be cleared ?", "Clear test collection", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION ))
			return;
		TestCollection.getInstance().clearTC();
		this.comment.setText("");
		this.name.setText("");
		this.version.setText("");
		updateTCDetails();
	}
    
	public void updateData() {
		updateTCDetails();	
	}


}
