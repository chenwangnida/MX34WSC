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
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Iterator;
import javax.swing.Timer;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.dfki.owlsmx.gui.ServiceCellRenderer;
import de.dfki.owlsmx.gui.data.ServiceItem;
import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.UpdateDataListener;
import de.dfki.owlsmx.gui.util.Utils;
import de.dfki.owlsmx.gui.util.filefilter.ServiceFilter;
import de.dfki.owlsmx.gui.util.tasks.AddServicesToCollectionTask;

/**
 *
 * @author  B
 */
public class Services extends javax.swing.JPanel implements ListSelectionListener, ActionListener,UpdateDataListener {
	
	private javax.swing.JList services;
	private javax.swing.JButton addDirectory;
	private javax.swing.JButton addService;
	private javax.swing.JList inputs;
	private javax.swing.JLabel inputsLabel;
	private javax.swing.JScrollPane inputsScrollPane;
	private javax.swing.JLabel logo;
	private javax.swing.JButton moreInfo;
	private javax.swing.JTextField name;
	private javax.swing.JLabel nameLabel;
	private javax.swing.JList outputs;
	private javax.swing.JLabel outputsLabel;
	private javax.swing.JScrollPane outputsScrollPane;
	private javax.swing.JButton removeService;
	private javax.swing.JLabel servicesLabel;
	private javax.swing.JScrollPane servicesPane;
	private javax.swing.JTextField uri;
	private javax.swing.JLabel uriLabel;
	private DefaultListModel inputsDataModel = new DefaultListModel();
	private DefaultListModel outputsDataModel = new DefaultListModel();
	private ServiceItem currentService;
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
    public Services() {
        initComponents();
    }
    

    private void initComponents() {
        servicesPane = new javax.swing.JScrollPane();
        services = new javax.swing.JList(TestCollection.getInstance().getServiceDataModel());
        services.addListSelectionListener(this);
        // add a cell renderer, which displays an icon, if the service in that cell
        // has a WSDL grounding
        services.setCellRenderer(new ServiceCellRenderer());       
        addService = new javax.swing.JButton();
        removeService = new javax.swing.JButton();
        servicesLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        uriLabel = new javax.swing.JLabel();
        uri = new javax.swing.JTextField();
        moreInfo = new javax.swing.JButton();
        addDirectory = new javax.swing.JButton();
        logo = new javax.swing.JLabel();
        inputsLabel = new javax.swing.JLabel();
        inputsScrollPane = new javax.swing.JScrollPane();
        inputs = new javax.swing.JList();
        outputsLabel = new javax.swing.JLabel();
        outputsScrollPane = new javax.swing.JScrollPane();
        outputs = new javax.swing.JList();
        seeGrounding = new javax.swing.JButton();
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setMinimumSize(new java.awt.Dimension(800, 400));
        setPreferredSize(new java.awt.Dimension(800, 400));

        servicesPane.setViewportView(services);

        add(servicesPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 300, 290));

        addService.setText("Register service (file)");
        addService.addActionListener(this);
        add(addService, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 330, 300, -1));        

        removeService.setText("Remove selected services");
        removeService.addActionListener(this);
        add(removeService, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 390, 300, -1));

        servicesLabel.setText("Registered services");
        add(servicesLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 300, -1));

        nameLabel.setText("Name");
        add(nameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 10, 300, -1));

        add(name, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 30, 300, -1));

        uriLabel.setText("URI");
        add(uriLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 60, 300, -1));

        add(uri, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 80, 300, -1));

        moreInfo.setText("More information");
        moreInfo.addActionListener(this);
        add(moreInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 330, 300, -1));
        seeGrounding.setText("Show grounding");
        seeGrounding.addActionListener(this);
        add(seeGrounding, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 360, 300, -1));

        addDirectory.setText("Register services (filefolder)");
        addDirectory.addActionListener(this);
        add(addDirectory, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 360, 300, -1));

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource(GUIState.logoPath)));
        add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, -1, -1));

        inputsLabel.setText("Inputs");
        add(inputsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 100, 300, 20));

        inputsScrollPane.setViewportView(inputs);

        add(inputsScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 120, 300, 90));

        outputsLabel.setText("Outputs");
        add(outputsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 210, 300, 20));

        outputsScrollPane.setViewportView(outputs);

        add(outputsScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 230, 300, 90));
        fc_open  = new JFileChooser();
        fc_open.addChoosableFileFilter(new ServiceFilter());

        markAll = new JCheckBox();
        markAll.setText("Select all");
        markAll.addActionListener(this);
        add(markAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 5, 80, -1));
    }
    
    
    
    private void updateDisplayedService() {
		ServiceItem tmp = (ServiceItem)services.getSelectedValue();
		if ( ( (currentService!=null) && (currentService.equals(tmp))) || (tmp==null)) 
			return;		
		currentService = tmp;			
		this.name.setText(currentService.getName());
		this.uri.setText(currentService.getURI().toString());
		inputs.removeAll();
		Iterator iter = currentService.getInputs().iterator();
		
		URI uri;
		inputsDataModel.removeAllElements();
		while(iter.hasNext()) {
			uri = (URI)iter.next();
			inputsDataModel.addElement( Utils.getConcept(uri.toString(),true) );
		}
		inputs.setModel(inputsDataModel);
		
		outputsDataModel.removeAllElements();
		iter = currentService.getOutputs().iterator();
		while(iter.hasNext()) {
			uri = (URI)iter.next();
			outputsDataModel.addElement( Utils.getConcept(uri.toString(),true) );
		}
		outputs.setModel(outputsDataModel);
    }
    
	public void valueChanged(ListSelectionEvent event) {
		if (event.getSource().equals(services)) {
			updateDisplayedService();
		}
					
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(addDirectory)) {
			fc_open.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fc_open.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){				
				File[] files = fc_open.getSelectedFile().listFiles();
				progressMonitor = new ProgressMonitor(
						this, "Adding services to testcollection","", 0, files.length-1);
				progressMonitor.setProgress(1);
				progressMonitor.setMillisToDecideToPopup( 0 );
				progressMonitor.setMillisToPopup( 0 );	        
				timer = new Timer(1000,this);
				timer.start();
				timer.addActionListener(this);				
				task  =  new AddServicesToCollectionTask(this,files,false);		        
		        progressMonitor.setNote(task.getMessage());
				task.go();
				}
			updateData();
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
		else if (event.getSource().equals(addService)) {
			fc_open.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fc_open.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
				try {
					
					TestCollection.getInstance().addService(fc_open.getSelectedFile().toURI());
				} catch (FileNotFoundException e) {
					if (GUIState.debug)
						JOptionPane.showMessageDialog(null,
							    "Couldn't read selected service.",
							    "FileNotFound Warning",
							    JOptionPane.WARNING_MESSAGE);
					e.printStackTrace();
				}
			}
			updateData();
		}
		else if (event.getSource().equals(moreInfo)) {		
			//GUIState.displayWarning("Note", "Trying to show info");
			Object[] values = services.getSelectedValues();
			ServiceItem tmpService;
			DisplayService dService;
			for (int i = 0; i<values.length;i++) {					
				tmpService = (ServiceItem) values[i];
				//GUIState.displayWarning("Note", "Trying to show info of service " + tmpService.getName());
				dService = new DisplayService(tmpService);
				java.awt.EventQueue.invokeLater(new ShowPanelFrame(dService,false));				
			}
		}
		
		else if (event.getSource().equals(removeService)) {
			TestCollection.getInstance().removeServices(services.getSelectedValues());	
			updateData();
		}
		
		else if (event.getSource().equals(markAll)) {
			if (markAll.isSelected()) {
				services.addSelectionInterval(0, services.getModel().getSize()-1);
			}
			else {
				services.removeSelectionInterval(0,services.getModel().getSize()-1);
			}
		}
		
		else if (event.getSource().equals(seeGrounding)) {
			Object[] values = services.getSelectedValues();
			if(values.length == 1) {
				ServiceItem serviceItem = (ServiceItem) values[0];
				if(serviceItem.hasWSDLGrounding())
					java.awt.EventQueue.invokeLater(new ShowPanelFrame(new DisplayGrounding(serviceItem),false));
				else
					javax.swing.JOptionPane.showMessageDialog(null, "The service \"" + serviceItem.getName() + "\" has no WSDL Grounding!", "No Grounding!", javax.swing.JOptionPane.WARNING_MESSAGE);
			}
		}
		
	}

	public void updateData() {
		int[] selected = services.getSelectedIndices();
		services.setModel(TestCollection.getInstance().getServiceDataModel());
		services.setSelectedIndices(selected);
	}

}
