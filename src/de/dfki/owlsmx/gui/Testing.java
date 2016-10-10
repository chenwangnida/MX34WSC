/*
 * Created on 30. Juli 2005, 21:27
 * 
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.UpdateDataListener;
import de.dfki.owlsmx.gui.util.tasks.AddServicesToMatchmakerTask;
import de.dfki.owlsmx.gui.util.tasks.RunQueriesTask;


/**
 *
 * @author  B
 */
public class Testing extends javax.swing.JPanel implements ActionListener, UpdateDataListener{
	
	private javax.swing.JCheckBox answerSet;
	private javax.swing.JButton apply;
	private javax.swing.JLabel logo;
	private javax.swing.JCheckBox memoryConsumption;
	private javax.swing.JCheckBox queryResponseTime;
	private javax.swing.JCheckBox recallPrecision;
	private javax.swing.JButton runAllQueries;
	private javax.swing.JLabel testLabel;
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Timer timer;
	private ProgressMonitor progressMonitor;
	private AddServicesToMatchmakerTask addServicesTask;
	private RunQueriesTask runQueriesTask;
	private int length = 0;
	
	/**
     * Creates new form Evaluation 
     */
    public Testing() {
        initComponents();
        updateButtons();
    }
    

    private void initComponents() {
        recallPrecision = new javax.swing.JCheckBox();
        queryResponseTime = new javax.swing.JCheckBox();
        memoryConsumption = new javax.swing.JCheckBox();
        testLabel = new javax.swing.JLabel();
        runAllQueries = new javax.swing.JButton();
        logo = new javax.swing.JLabel();
        answerSet = new javax.swing.JCheckBox();
        apply = new javax.swing.JButton();
        apply.addActionListener(this);

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setMinimumSize(new java.awt.Dimension(800, 400));
        setPreferredSize(new java.awt.Dimension(800, 400));
        recallPrecision.setText("Recall/Precision");
        recallPrecision.addActionListener(this);
        add(recallPrecision, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 40, -1, -1));

        queryResponseTime.setText("Average query response time");
        queryResponseTime.addActionListener(this);
        add(queryResponseTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 80, -1, -1));

        memoryConsumption.setText("Memory consumption");
        memoryConsumption.addActionListener(this);
        add(memoryConsumption, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 80, -1, -1));

        testLabel.setText("Tests to perform");
        add(testLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, -1, -1));

        runAllQueries.setText("Apply");
        runAllQueries.addActionListener(this);
        add(runAllQueries, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 140, 350, -1));

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource(GUIState.logoPath)));
        add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, -1, -1));

        answerSet.setText("Answer set");
        answerSet.addActionListener(this);
        add(answerSet, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 40, -1, -1));

//        apply.setText("Apply");
//        apply.addActionListener(this);
//        add(apply, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 110, 350, -1));

    }
    
    public void updateButtons() {
    	answerSet.setSelected(GUIState.getInstance().getDisplayAnswerSet());
    	memoryConsumption.setSelected(GUIState.getInstance().getMemoryConsumption());
    	queryResponseTime.setSelected(GUIState.getInstance().getDisplayAQR());
    	recallPrecision.setSelected(GUIState.getInstance().getPrecisionRecall());
    }
    
    public void processButtonState(){
//    	System.err.println("memoryConsumption " + memoryConsumption.isSelected());
		GUIState.getInstance().setDisplayAnswerSet(answerSet.isSelected());		
		GUIState.getInstance().setMemoryConsumption(memoryConsumption.isSelected());
		GUIState.getInstance().setDisplayAQR(queryResponseTime.isSelected());
		GUIState.getInstance().setPrecisionRecall(recallPrecision.isSelected());
    }
    
	public void actionPerformed(ActionEvent event) {			
		if (event.getSource().equals(runAllQueries)){
			processButtonState();
			if ( (!TestCollection.getInstance().checkQueriesAndServicesSets(this)) ) {
				return;
			}
			addServicesTask = new AddServicesToMatchmakerTask();
			runQueriesTask = new RunQueriesTask();
	        length = (runQueriesTask.getLengthOfTask() + addServicesTask.getLengthOfTask());
			progressMonitor = new ProgressMonitor(
				this, "Matchmaking","Loading services into matchmaker", 
				0, length);
			progressMonitor.setProgress(addServicesTask.getCurrent());
			progressMonitor.setMillisToDecideToPopup( 0 );
			progressMonitor.setMillisToPopup( 0 );	        
			timer = new Timer(500,this);
			timer.start();
			timer.addActionListener(this);	
			addServicesTask.go();
		}
		
		else if (event.getSource().equals(timer)) {
	        progressMonitor.setProgress(addServicesTask.getCurrent() + runQueriesTask.getCurrent());
	        if (!addServicesTask.isDone())
	        	progressMonitor.setNote(addServicesTask.getMessage());
	        else
	        	progressMonitor.setNote(runQueriesTask.getMessage());
	        if (progressMonitor.isCanceled()) {
	            timer.stop();
	            if (!addServicesTask.isDone()) {
	                GUIState.displayWarning(this.getClass().toString(), "Added only " + addServicesTask.getCurrent() + " of " + addServicesTask.getLengthOfTask() + " services");
	            }
	            else if (!runQueriesTask.isDone()) {
	                GUIState.displayWarning(this.getClass().toString(), "Applied only " + runQueriesTask.getCurrent() + " of " + runQueriesTask.getLengthOfTask() + " queries");
	            }
	            addServicesTask.stop();
	        	runQueriesTask.stop();
	            progressMonitor.close();
	            de.dfki.owlsmx.gui.data.TestCollection.getInstance().updateData();
	        }
	        else if (addServicesTask.isDone() && (!runQueriesTask.isRunning())) {
	            addServicesTask.stop();	            
	            progressMonitor.setNote("Running queries");
	    		runQueriesTask.go();
	        }
	        else if (runQueriesTask.isDone()) {
	        	progressMonitor.close();	
	        	runQueriesTask.stop();
	            timer.stop();
	            de.dfki.owlsmx.gui.data.TestCollection.getInstance().updateData();
	        }	        	
		}

	}
    
	public void updateData() {
	}

    
}
