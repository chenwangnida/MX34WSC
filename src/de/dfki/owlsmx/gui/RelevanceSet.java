/*
 * Created on 30. Juli 2005, 21:27
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
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import de.dfki.owlsmx.gui.data.Query;
import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.UpdateDataListener;
import de.dfki.owlsmx.gui.util.filefilter.ServiceFilter;
import de.dfki.owlsmx.gui.util.tasks.AddServiceToRelevanceset;


/**
 *
 * @author  B
 */
public class RelevanceSet extends javax.swing.JPanel implements ListSelectionListener, ActionListener,UpdateDataListener {
    
	private javax.swing.JScrollPane answerScrollpane;
	private javax.swing.JButton RemoveServiceButton;
	private javax.swing.JButton addServiceFromFolderButton;
	private javax.swing.JButton addServiceButton;
	private javax.swing.JList answerList;
	private javax.swing.JLabel answerLabel;
	private javax.swing.JLabel logo;
	private javax.swing.JLabel queryLabel;
	private javax.swing.JList queryList;
	private javax.swing.JScrollPane queryScrollpane;
	private javax.swing.JLabel serviceLabel;
	private javax.swing.JList serviceList;
	private javax.swing.JScrollPane serviceScrollpane;
	private JCheckBox markAllServices;
	private JCheckBox markAllAnswers;
    private JFileChooser fc_open;
	private Timer timer;
	private ProgressMonitor progressMonitor;
	private AddServiceToRelevanceset task;
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/** Creates new form TestDesigner */
    public RelevanceSet() {
    	initComponents();
        //updateData();
    }
    
    private void initComponents() {
        serviceScrollpane = new javax.swing.JScrollPane();
        serviceList = new javax.swing.JList(TestCollection.getInstance().getServiceDataModel());
        queryScrollpane = new javax.swing.JScrollPane();
        queryList = new javax.swing.JList(TestCollection.getInstance().getQueryDataModel());
        queryList.addListSelectionListener(this);
        queryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serviceLabel = new javax.swing.JLabel();
        queryLabel = new javax.swing.JLabel();
        answerScrollpane = new javax.swing.JScrollPane();
        answerList = new javax.swing.JList(TestCollection.getInstance().getAnswerSetDataModel());
        answerLabel = new javax.swing.JLabel();
        addServiceButton = new javax.swing.JButton();
        RemoveServiceButton = new javax.swing.JButton();

        
        addServiceFromFolderButton = new javax.swing.JButton();
        addServiceButton.addActionListener(this);
        RemoveServiceButton.addActionListener(this);
        addServiceFromFolderButton.addActionListener(this);
        logo = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setMinimumSize(new java.awt.Dimension(800, 400));
        setPreferredSize(new java.awt.Dimension(800, 400));
        serviceScrollpane.setViewportView(serviceList);

        add(serviceScrollpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 30, 300, 250));

        queryScrollpane.setViewportView(queryList);

        add(queryScrollpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 340, 150));

        serviceLabel.setText("Registered services");
        add(serviceLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 10, 210, -1));

        queryLabel.setText("Service requests");
        add(queryLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 250, -1));

        answerScrollpane.setViewportView(answerList);

        add(answerScrollpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 210, 330, 170));

        answerLabel.setText("Relevance set");
        add(answerLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 190, 280, -1));

        addServiceButton.setText("Add selected services to relevance set");
        add(addServiceButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 290, 300, -1));

        RemoveServiceButton.setText("Remove selected services");
        add(RemoveServiceButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 320, 300, -1));

        addServiceFromFolderButton.setText("Add services from filefolder to relevance set");
        add(addServiceFromFolderButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 350, 300, -1));

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource(GUIState.logoPath)));
        add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, -1, -1));
        

        markAllServices = new JCheckBox();
        markAllServices.setText("Select all");
        markAllServices.addActionListener(this);
        add(markAllServices, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 5, 80, -1));

        markAllAnswers = new JCheckBox();
        markAllAnswers.setText("Select all");
        markAllAnswers.addActionListener(this);
        add(markAllAnswers, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 185, 80, -1));
        fc_open  = new JFileChooser();
        fc_open.addChoosableFileFilter(new ServiceFilter());
    }
    
    

    


	public void valueChanged(ListSelectionEvent event) {
		if (event.getSource().equals(queryList)) {
			Query currentQuery = (Query)queryList.getSelectedValue();
			if (currentQuery==null)
				return;
			TestCollection.getInstance().updateAnswerset(currentQuery);
			answerList.setModel(TestCollection.getInstance().getAnswerSetDataModel());			
			answerLabel.setText("Relevance set for query " + currentQuery.getName());
			//System.out.println("Size is " + answerList.getModel().getSize());
		}
	}

	public void actionPerformed(ActionEvent event) {
	    if (event.getSource().equals(RemoveServiceButton)){
	    	if (queryList.getSelectedIndex()<0) {
	    		GUIState.displayWarning(this,"No query selected", "As no query was selected it is unknown which relevance set to add the services to.\nPlease select a query first.");
	    		return;
	    	}
	    	if ((answerList.getSelectedIndex()<0) ) {
	    		GUIState.displayWarning(this,"No service selected", "As no service from the relevance set was selected it is unknown which to remove.\nPlease select some first.");
	    		return;
	    	}
		    if (event.getSource().equals(RemoveServiceButton)){
		    	if ( (answerList.getSelectedIndex()<0) || (queryList.getSelectedIndex()<0) )
		    		return;
		    	TestCollection.getInstance().removeServicesFromAnswerset(
		    			((Query)queryList.getSelectedValue()).getURI(), 
		    			answerList.getSelectedValues());	   
		    }
	    }
		else if (event.getSource().equals(timer)) {
	        progressMonitor.setProgress(task.getCurrent());        
	        progressMonitor.setNote(task.getMessage());
	        if (progressMonitor.isCanceled() || task.isDone()) {
	            progressMonitor.close();
	            task.stop();
	            timer.stop();
	            if (!task.isDone()) {
	                GUIState.displayWarning(this.getClass().toString(), "Added only " + task.getCurrent() + " of " + task.getLengthOfTask() + " services");
	            }         
	        }
	        updateData();
		}
	    else if (event.getSource().equals(addServiceFromFolderButton)) {
	    	if (queryList.getSelectedIndex()<0) {
	    		GUIState.displayWarning(this,"No query selected", "As no query was selected it is unknown which relevance set to add the services to.\nPlease select a query first.");
	    		return;
	    	}
	    	if ( (task!=null) && (!task.isDone())) {
	    			GUIState.displayWarning(this, "Task still running", "There is still a task running");
	    			return;
	    	}
			fc_open.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (!(fc_open.showOpenDialog(this) == JFileChooser.APPROVE_OPTION))
				return;
			File[] files = fc_open.getSelectedFile().listFiles();
			progressMonitor = new ProgressMonitor(
					this, "Adding services to answer set","", 0, files.length-1);
			progressMonitor.setProgress(1);
			progressMonitor.setMillisToDecideToPopup( 0 );
			progressMonitor.setMillisToPopup( 0 );	        
			timer = new Timer(500,this);
			timer.start();
			timer.addActionListener(this);				
			task  =  new AddServiceToRelevanceset(files,(Query)queryList.getSelectedValue());		        
	        progressMonitor.setNote(task.getMessage());
			task.go();
			
			updateData();   
	    }
	    else if (event.getSource().equals(addServiceButton)) {
	    	if (queryList.getSelectedIndex()<0) {
	    		GUIState.displayWarning(this,"No query selected", "As no query was selected it is unknown which relevance set to add the services to.\nPlease select a query first.");
	    		return;
	    	}
	    	if (serviceList.getSelectedIndex()<0) {
	    		GUIState.displayWarning(this,"No service selected", "As no service was selected it is which to add the relevance set.\nPlease select some first.");
	    		return;
	    	}
	    	TestCollection.getInstance().addServicesToAnswerset(
	    			((Query)queryList.getSelectedValue()).getURI(), 
	    			serviceList.getSelectedValues());	    	
	    }
		else if (event.getSource().equals(markAllServices)) {			
			if (markAllServices.isSelected()) {		    	
				serviceList.addSelectionInterval(0, serviceList.getModel().getSize()-1);
			}
			else {
				serviceList.removeSelectionInterval(0,serviceList.getModel().getSize()-1);		
			}
		}
		else if (event.getSource().equals(markAllAnswers)) {
			if ( (queryList.getSelectedIndex()<0) ) {
	    		GUIState.displayWarning(this,"No query selected", "As no query was selected it is unknown which relevance set to use.\nPlease select a query first.");
	    		return;
	    	}
			if (markAllAnswers.isSelected()) {
				answerList.addSelectionInterval(0, answerList.getModel().getSize()-1);
			}
			else {
				answerList.removeSelectionInterval(0,answerList.getModel().getSize()-1);		
			}
		}
		
	}



	public void updateData() {
		int[] selectedAnswers = answerList.getSelectedIndices();
		int[] selectedServices = serviceList.getSelectedIndices();
		int[] selectedQueries = queryList.getSelectedIndices();
		serviceList.setModel(TestCollection.getInstance().getServiceDataModel());
		queryList.setModel(TestCollection.getInstance().getQueryDataModel());
		answerList.setModel(TestCollection.getInstance().getAnswerSetDataModel());
		answerList.setSelectedIndices(selectedAnswers);
		serviceList.setSelectedIndices(selectedServices);
		serviceList.setSelectedIndices(selectedQueries);
	}
    
}
