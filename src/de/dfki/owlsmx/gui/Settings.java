/*
 * Created on 9. August 2005, 17:18
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

import javax.swing.UIManager;
import java.util.HashMap;
import javax.swing.SwingUtilities;

import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.UpdateDataListener;

/**
 * Settings screen for the OWLS-MX GUI
 * Allows adjustment of used SwingStyle, when Services are processed by the matchmaker,
 *
 * @author  Ben
 */
public class Settings extends javax.swing.JPanel implements UpdateDataListener {
	
	private javax.swing.JComboBox Style;
	private javax.swing.JRadioButton addServicesDirectly;
	private javax.swing.JRadioButton addServicesDuringMatching;
	private javax.swing.ButtonGroup addServicesWhen;
	private javax.swing.JLabel swingStyleLabel;
	private javax.swing.JLabel processServicesLabel;
	private javax.swing.JLabel logo;
    private javax.swing.JTextField replaceURI;
    private javax.swing.JLabel replaceURILabel;
    private javax.swing.JTextField withURI;
    private javax.swing.JLabel withURILabel;
    private javax.swing.JButton apply;
    private javax.swing.JCheckBox useReplaceURI;
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap look = new HashMap();
    BaseWindow frame;
    
    /** Creates new form Settings
     * 
     * - Loads all Installed LookAndFeels
     * - Loads Data from GUIState 
     * 
     */
    public Settings(BaseWindow frame) {   
        this.frame = frame;
        initComponents();
        addServicesWhen.add(addServicesDirectly);
        addServicesWhen.add(addServicesDuringMatching);           
        
        UIManager.LookAndFeelInfo[] feel = UIManager.getInstalledLookAndFeels();
        
        for (int i=0;i<feel.length;i++) {
            look.put(feel[i].getName(),feel[i].getClassName());
            Style.addItem(feel[i].getName());          
        }
        updateData();
    }
    
    private void initComponents() {
        addServicesWhen = new javax.swing.ButtonGroup();
        Style = new javax.swing.JComboBox();
        swingStyleLabel = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        addServicesDirectly = new javax.swing.JRadioButton();
        addServicesDuringMatching = new javax.swing.JRadioButton();
        processServicesLabel = new javax.swing.JLabel();
        replaceURI = new javax.swing.JTextField();
        replaceURI.setEnabled(GUIState.getInstance().isUseReplaceWith());
        withURI = new javax.swing.JTextField();
        withURI.setEnabled(GUIState.getInstance().isUseReplaceWith());
        apply = new javax.swing.JButton();
        replaceURILabel = new javax.swing.JLabel();
        withURILabel = new javax.swing.JLabel();
        useReplaceURI = new javax.swing.JCheckBox();
        useReplaceURI.setSelected(GUIState.getInstance().isUseReplaceWith());
        useReplaceURI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	useReplaceURIPerformed(evt);
            }
        });
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setMinimumSize(new java.awt.Dimension(800, 400));
        setPreferredSize(new java.awt.Dimension(800, 400));
        Style.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StyleActionPerformed(evt);
            }
        });

        add(Style, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 100, 200, 30));

        swingStyleLabel.setText("Used Swing style");
        add(swingStyleLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, -1, -1));

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource(GUIState.logoPath)));
        add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, -1, -1));

        addServicesDirectly.setText("Directly when they are added");
//        add(addServicesDirectly, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, -1, -1));

        addServicesDuringMatching.setSelected(true);
        addServicesDuringMatching.setText("During the matching process");
//        add(addServicesDuringMatching, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, -1, -1));

        processServicesLabel.setText("Process services when");
//        add(processServicesLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, -1, -1));

        add(replaceURI, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 190, 450, -1));

        add(withURI, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 240, 450, -1));

        apply.setText("apply");
        apply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	applyPerformed(evt);
            }
        });
        add(apply, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 280, 450, -1));

        replaceURILabel.setText("Replace URI substring");
        add(replaceURILabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 170, 450, -1));

        withURILabel.setText("with string");
        add(withURILabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 220, 450, -1));
        
        useReplaceURI.setText("Replace URI substrings");
        add(useReplaceURI, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 140, 450, -1));
    }
 
    /**
     * Called when the Swing style is changed in the combobox.
     * Adjusts the style for entire GUI (baseWindow, this window and parent.
     * 
     * @param evt ActionEvent caused by ComboBox Style
     */
    private void StyleActionPerformed(java.awt.event.ActionEvent evt) {
        try{
        	if ( (Style==null) || (look==null) || (frame==null) || (getParent()==null) )
        		return;        	
	        UIManager.setLookAndFeel( (String) look.get( Style.getSelectedItem() ) );        
	        SwingUtilities.updateComponentTreeUI(frame);
	        SwingUtilities.updateComponentTreeUI(getParent());        
	        SwingUtilities.updateComponentTreeUI(this);
	        this.updateUI();
	        	frame.pack();
        }
        catch(Exception e){
        	de.dfki.owlsmx.io.ErrorLog.instanceOf().report(this.getClass().toString() + "\n" + e.getClass() + "\n" + "Couldn't change look and feel");        	
        }
    }
    
    private void addImmediatelyToMatchmaker(java.awt.event.ActionEvent evt) {
    	if (addServicesDirectly.isSelected())
    		GUIState.getInstance().setAddImmediately(true);
    	else if (addServicesDuringMatching.isSelected())
        	GUIState.getInstance().setAddImmediately(false);
    }

    private void useReplaceURIPerformed(java.awt.event.ActionEvent evt) {
    	System.out.println(useReplaceURI.isSelected() + "isSelected");
    	System.out.println(useReplaceURI.isEnabled() + "isEnabled ");
    	GUIState.getInstance().setUseReplaceWith(useReplaceURI.isSelected());
    	updateData();
    }
    
    private void applyPerformed(java.awt.event.ActionEvent evt) {
    	GUIState.getInstance().setReplaceFrom(replaceURI.getText(),withURI.getText());
    	addImmediatelyToMatchmaker(evt);
    	updateData();
    }
    
	public void updateData() {
		addServicesDirectly.setSelected(GUIState.getInstance().isAddImmediately());
		addServicesDuringMatching.setSelected(!GUIState.getInstance().isAddImmediately());
		//useReplaceURI.setSelected(GUIState.getInstance().isUseReplaceWith());
        replaceURI.setEnabled(useReplaceURI.isSelected());
        withURI.setEnabled(useReplaceURI.isSelected());		
	}
    
}
