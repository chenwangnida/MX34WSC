/*
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
import java.io.File;
import java.net.URI;
import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import de.dfki.owlsmx.gui.data.Query;
import de.dfki.owlsmx.gui.data.ServiceItem;
import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.UpdateDataListener;
import de.dfki.owlsmx.gui.util.Utils;
import de.dfki.owlsmx.gui.util.filefilter.ServiceFilter;
import de.dfki.owlsmx.gui.util.tasks.AddServicesToCollectionTask;

/**
 *
 *
 * @author  Ben
 */
public class Requests extends javax.swing.JPanel implements ListSelectionListener,ActionListener,UpdateDataListener {

	private javax.swing.JButton addEntireDirectory;
	private javax.swing.JButton addQuery;
	private javax.swing.JList inputs;
	private javax.swing.JLabel inputsLabel;
	private javax.swing.JScrollPane inputsScrollpane;
	private javax.swing.JLabel logo;
	private javax.swing.JButton moreInfo;
	private javax.swing.JTextField name;
	private javax.swing.JLabel nameLabel;
	private javax.swing.JList outputs;
	private javax.swing.JLabel outputsLabel;
	private javax.swing.JScrollPane outputsScrollpane;
	private javax.swing.JList queries;
	private javax.swing.JScrollPane queriesScrollpane;
	private javax.swing.JLabel queryLabel;
	private javax.swing.JButton removeQuery;
//	private javax.swing.JButton runAllQueries;
//	private javax.swing.JButton runQuery;
	private javax.swing.JTextField uri;
	private javax.swing.JLabel uriLabel;
	private DefaultListModel inputsDataModel = new DefaultListModel();
	private DefaultListModel outputsDataModel = new DefaultListModel();
	public Query currentQuery;
	private JFileChooser fc_open;
	private Timer timer;
	private ProgressMonitor progressMonitor;
	private AddServicesToCollectionTask task;
	private JCheckBox markAll;
	private javax.swing.JButton seeGrounding;
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Creates new form TestCollectionDesigner */
    public Requests() {
         try {             
        UIManager.setLookAndFeel(                
            "Classic com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
         } catch (Exception e) { }
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
    private void initComponents() {
        queriesScrollpane = new javax.swing.JScrollPane();
        queries = new javax.swing.JList(TestCollection.getInstance().getQueryDataModel());
        queries.addListSelectionListener(this);
        queries.setCellRenderer(new ServiceCellRenderer());
        addQuery = new javax.swing.JButton();
        removeQuery = new javax.swing.JButton();
        moreInfo = new javax.swing.JButton();
        queryLabel = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        inputsLabel = new javax.swing.JLabel();
        outputsLabel = new javax.swing.JLabel();
        inputsScrollpane = new javax.swing.JScrollPane();
        inputs = new javax.swing.JList();
        outputsScrollpane = new javax.swing.JScrollPane();
        outputs = new javax.swing.JList();
//        runQuery = new javax.swing.JButton();
//        runAllQueries = new javax.swing.JButton();
        logo = new javax.swing.JLabel();
        uri = new javax.swing.JTextField();
        uriLabel = new javax.swing.JLabel();
        addEntireDirectory = new javax.swing.JButton();
        seeGrounding = new javax.swing.JButton();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setMinimumSize(new java.awt.Dimension(800, 400));
        setPreferredSize(new java.awt.Dimension(800, 400));
        queriesScrollpane.setViewportView(queries);

        add(queriesScrollpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 300, 290));

        addQuery.setText("Add request (file)");
        addQuery.addActionListener(this);
        add(addQuery, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 330, 300, -1));

        removeQuery.setText("Remove selected requests");
        removeQuery.addActionListener(this);
        add(removeQuery, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 390, 300, -1));//(560, 339, 210, -1));
        
        seeGrounding.setText("Show grounding");
        seeGrounding.addActionListener(this);
        add(seeGrounding, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 360, 300, -1));

        moreInfo.setText("More information");
        moreInfo.addActionListener(this);
        add(moreInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 330, 300, -1));

        queryLabel.setText("Service requests");
        add(queryLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 300, -1));

        add(name, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 30, 300, -1));

        nameLabel.setText("Name");
        add(nameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 10, 300, -1));

        inputsLabel.setText("Inputs");
        add(inputsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 100, 300, 20));

        outputsLabel.setText("Outputs");
        add(outputsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 210, 300, 20));

        inputsScrollpane.setViewportView(inputs);

        add(inputsScrollpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 120, 300, 90));

        outputsScrollpane.setViewportView(outputs);

        add(outputsScrollpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 230, 300, 90));

//        runQuery.setText("Run selected requests");
//        add(runQuery, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 370, 210, -1));

//        runAllQueries.setText("Run all queries");
//        add(runAllQueries, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 370, 210, -1));

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource(GUIState.logoPath)));
        add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, -1, -1));

        add(uri, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 80, 300, -1));

        uriLabel.setText("URI");
        add(uriLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 60, 300, -1));

        addEntireDirectory.setText("Add requests (filefolder)");
        addEntireDirectory.addActionListener(this);
        add(addEntireDirectory, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 360, 300, -1));
        fc_open  = new JFileChooser();
        fc_open.addChoosableFileFilter(new ServiceFilter());
        
        markAll = new JCheckBox();
        markAll.setText("Select all");
        markAll.addActionListener(this);
        add(markAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 5, 80, -1));
    }
    // </editor-fold>
    

    
	public void valueChanged(ListSelectionEvent event) {
		if (event.getSource().equals(queries)) {
			Query tmp = (Query)queries.getSelectedValue();
			if ( ( (currentQuery!=null) && (currentQuery.equals(tmp))) || (tmp==null))
				return;
			currentQuery = tmp;			
			this.name.setText(currentQuery.getName());
			this.uri.setText(currentQuery.getURI().toString());
			
			inputsDataModel.removeAllElements();
			for(Iterator iter = currentQuery.getInputs().iterator();iter.hasNext();) {
				inputsDataModel.addElement(Utils.getConcept((URI) iter.next(),true));
			}
			inputs.setModel(inputsDataModel);
			
			outputsDataModel.removeAllElements();
			for(Iterator iter = currentQuery.getOutputs().iterator();iter.hasNext();) {
				outputsDataModel.addElement(Utils.getConcept((URI) iter.next(),true));
			}
			outputs.setModel(outputsDataModel);
		}
					
	}
	
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(addEntireDirectory)) {
			fc_open.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fc_open.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
				File[] files = fc_open.getSelectedFile().listFiles();
				progressMonitor = new ProgressMonitor(
						this, "Testcollection","Adding queries to testcollection", 0, files.length-1);
				progressMonitor.setProgress(0);
				progressMonitor.setMillisToDecideToPopup( 0 );
				progressMonitor.setMillisToPopup( 0 );	        
				timer = new Timer(1000,this);
				timer.start();
				timer.addActionListener(this);
				task  =  new AddServicesToCollectionTask(this,files,true);				
				task.go();
			}
		}
		else if (event.getSource().equals(timer)) {
	        progressMonitor.setProgress(task.getCurrent());            
	        if (progressMonitor.isCanceled() || task.isDone()) {
	            progressMonitor.close();
	            task.stop();
	            timer.stop();
	            if (!task.isDone()) {
	                GUIState.displayWarning(this.getClass().toString(), "Added only " + task.getCurrent() + " of " + task.getLengthOfTask() + " queries");
	            }         
	        }            
		}
		else if (event.getSource().equals(addQuery)) {
			fc_open.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fc_open.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
				TestCollection.getInstance().addQuery(fc_open.getSelectedFile().toURI());
			}
		}
		else if (event.getSource().equals(moreInfo)) {					
			Object[] values = queries.getSelectedValues();
			ServiceItem tmpService;
			DisplayService dService;
			for (int i = 0; i<values.length;i++) {					
				tmpService = (ServiceItem) values[i];
				//GUIState.displayWarning("Note", "Trying to show info of service " + tmpService.getName());
				dService = new DisplayService(tmpService);
				java.awt.EventQueue.invokeLater(new ShowPanelFrame(dService,false));				
			}	
		}
		
		else if (event.getSource().equals(removeQuery)) {
			//System.out.println("Removing services" + services.getSelectedValues().length);
			TestCollection.getInstance().removeQueries(queries.getSelectedValues());
			
		}		
		else if (event.getSource().equals(markAll)) {
			if (markAll.isSelected()) {
				queries.addSelectionInterval(0, queries.getModel().getSize()-1);
			}
			else {
				queries.removeSelectionInterval(0,queries.getModel().getSize()-1);		
			}
		}
		
		else if (event.getSource().equals(seeGrounding)) {
			Object[] values = queries.getSelectedValues();
			if(values.length == 1) {
				ServiceItem serviceItem = (ServiceItem) values[0];
				if(serviceItem.hasWSDLGrounding())
					java.awt.EventQueue.invokeLater(new ShowPanelFrame(new DisplayGrounding(serviceItem),false));
				else
					javax.swing.JOptionPane.showMessageDialog(null, "The service \"" + serviceItem.getName() + "\" has no WSDL Grounding!", "No Grounding!", javax.swing.JOptionPane.WARNING_MESSAGE);
			}
		}
		
		updateData();
	    
	    
	}

	public void updateData() {
		int[] selected = queries.getSelectedIndices();
		queries.setModel(TestCollection.getInstance().getQueryDataModel());	
		queries.setSelectedIndices(selected);	
	}
    
}
