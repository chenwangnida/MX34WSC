/*
 * Created on 10. August 2005, 23:25
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

import javax.swing.JPanel;

/**
 *
 * @author  B
 */
public class ShowPanelFrame extends javax.swing.JFrame implements Runnable {
    
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Creates new form AboutFrame */
    public ShowPanelFrame() {
    	initComponents(new AboutDialog(),true);
    }
    
    public ShowPanelFrame(JPanel panel) {
    	initComponents(panel, false);
    }
    
    public ShowPanelFrame(JPanel panel, boolean startMX) {
    	initComponents(panel, startMX);
    	
    }    
    
    private void initComponents(JPanel panel, boolean startMX) {
    	//this.setSize(800, 500);    	
    	JPanel menu = new JPanel();
        close = new javax.swing.JButton();
        

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(panel, java.awt.BorderLayout.CENTER); 
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        close.setText("Close");
        close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeActionPerformed(evt);
            }
        });

        menu.add(close);

        if (startMX) {
        owlsmx = new javax.swing.JButton();
        owlsmx.setText("Run OWLS-MX");
        owlsmx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                owlsmxActionPerformed(evt);
            }
        });

        menu.add(owlsmx);
        }
        add(menu,java.awt.BorderLayout.PAGE_END);
        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void owlsmxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_owlsmxActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BaseWindow().setVisible(true);
            }
        });
        dispose();
    }//GEN-LAST:event_owlsmxActionPerformed

    private void closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeActionPerformed
        dispose();
    }//GEN-LAST:event_closeActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton close;
    private javax.swing.JButton owlsmx;
    // End of variables declaration//GEN-END:variables
	public void run() {
		setVisible(true);
		/*
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	if (panel==null)
            		new ShowPanelFrame().setVisible(true);
            	else
            		new ShowPanelFrame(panel,false).setVisible(true);
            }
        });
		*/
	}
    
}
