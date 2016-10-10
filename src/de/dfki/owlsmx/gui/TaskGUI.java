/*
 * Created on 8. August 2005, 13:02
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
 */

package de.dfki.owlsmx.gui;

import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.UpdateDataListener;


/**
 *
 * @author  B
 */
public class TaskGUI extends javax.swing.JPanel implements UpdateDataListener {

//    private javax.swing.JLabel SelectLabel;
    private javax.swing.ButtonGroup TaskButtonGroup;
    private javax.swing.JRadioButton createTC;
    private javax.swing.JRadioButton createTest;
    private javax.swing.JButton OK;
    private javax.swing.JLabel logo;
    private javax.swing.JRadioButton runQuery;
    private javax.swing.JRadioButton runTest;

    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BaseWindow base_win;
    /** Creates new form Introduction */
    public TaskGUI(BaseWindow b) {
        base_win = b;        
        initComponents();
    }
    


    private void initComponents() {
        TaskButtonGroup = new javax.swing.ButtonGroup();
//        SelectLabel = new javax.swing.JLabel();
        createTC = new javax.swing.JRadioButton();
        createTest = new javax.swing.JRadioButton();
        runTest = new javax.swing.JRadioButton();
        runQuery = new javax.swing.JRadioButton();
        OK = new javax.swing.JButton();
        logo = new javax.swing.JLabel();

        TaskButtonGroup.add(createTest);
        TaskButtonGroup.add(createTC);
        TaskButtonGroup.add(runQuery);
        TaskButtonGroup.add(runTest);

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

//        SelectLabel.setFont(new java.awt.Font("Tahoma", 0, 12));
//        SelectLabel.setText("Select desired task:");
//        add(SelectLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 140, -1, -1));

        runQuery.setText("Service matchmaking");
        runQuery.setSelected(true);
        add(runQuery, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, -1, -1));
        
        createTC.setText("Service retrieval test collection");
        add(createTC, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, -1, -1));

        runTest.setText("Service retrieval performance");
        add(runTest, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 70, -1, -1));

        OK.setText("OK");
        OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });
        add(OK, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 100, 200, -1));

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource(GUIState.logoPath)));
        add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, -1, -1));

        setMinimumSize(new java.awt.Dimension(800, 400));
        setPreferredSize(new java.awt.Dimension(800, 400));
    }


    private void okActionPerformed(java.awt.event.ActionEvent evt) {
    	if (evt.getSource().equals(OK)) {
            base_win.showWizard();	
            if(createTC.isSelected())
            	GUIState.getInstance().setCurrentTask(GUIState.CREATE_TC_TASK);
            else if (runQuery.isSelected())
            	GUIState.getInstance().setCurrentTask(GUIState.QUERY_TASK);
            else if (runTest.isSelected())
            	GUIState.getInstance().setCurrentTask(GUIState.RUN_TEST_TASK);
            System.out.println(GUIState.getInstance().getCurrentTask());
    	}
    	TestCollection.getInstance().updateData();
    }           
    
 
    

    
	public void updateData() {
	}
    
}
