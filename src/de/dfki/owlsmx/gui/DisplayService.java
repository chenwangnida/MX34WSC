package de.dfki.owlsmx.gui;

import java.net.URI;
import java.util.Iterator;

import javax.swing.DefaultListModel;

import de.dfki.owlsmx.gui.data.ServiceItem;
import de.dfki.owlsmx.gui.util.Utils;

/*
 * Created on 6. Oktober 2005, 21:45
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

/**
 *
 * @author  B
 */
public class DisplayService extends javax.swing.JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private javax.swing.JList inputs;
	private javax.swing.JLabel inputsLabel;
	private javax.swing.JScrollPane inputsScrollPane;
	private javax.swing.JTextField name;
	private javax.swing.JLabel nameLabel;
	private javax.swing.JList outputs;
	private javax.swing.JLabel outputsLabel;
	private javax.swing.JScrollPane outputsScrollPane;
	private javax.swing.JTextField uri;
	private javax.swing.JLabel uriLabel;
	private DefaultListModel inputsDataModel = new DefaultListModel();
	private DefaultListModel outputsDataModel = new DefaultListModel();
    
    /** Creates new form DisplayService */
    public DisplayService(ServiceItem service) {
        initComponents();
        name.setText(service.getName());
        uri.setText(service.getURI().toString());
        
        URI tmpURI;
        Iterator iter = service.getInputs().iterator();
        inputs.removeAll();
		while(iter.hasNext()) {
			tmpURI = (URI)iter.next();
			//System.out.println("Input" + uri);
			inputsDataModel.addElement( Utils.getConcept(tmpURI.toString(),true) );
		}
		inputs.setModel(inputsDataModel);
		
		outputsDataModel.removeAllElements();
		iter = service.getOutputs().iterator();
		while(iter.hasNext()) {
			tmpURI = (URI)iter.next();
			//System.out.println(uri);
			outputsDataModel.addElement( Utils.getConcept(tmpURI.toString(),true) );
		}
		outputs.setModel(outputsDataModel);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
    private void initComponents() {
        nameLabel = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        uriLabel = new javax.swing.JLabel();
        uri = new javax.swing.JTextField();
        inputsLabel = new javax.swing.JLabel();
        inputsScrollPane = new javax.swing.JScrollPane();
        inputs = new javax.swing.JList();
        outputsLabel = new javax.swing.JLabel();
        outputsScrollPane = new javax.swing.JScrollPane();
        outputs = new javax.swing.JList();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nameLabel.setText("Name");
        add(nameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 320, -1));

        add(name, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 320, -1));

        uriLabel.setText("URI");
        add(uriLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 320, -1));

        add(uri, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 320, -1));

        inputsLabel.setText("Inputs");
        add(inputsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 230, 20));

        inputsScrollPane.setViewportView(inputs);

        add(inputsScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 320, 170));

        outputsLabel.setText("Outputs");
        add(outputsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 230, 20));

        outputsScrollPane.setViewportView(outputs);

        add(outputsScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 320, 170));

    }
    // </editor-fold>
    
}

