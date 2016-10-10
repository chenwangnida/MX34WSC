/*
 * Created on 10. August 2005, 23:11
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

import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.GUIUtils;

/**
 *
 * @author  B
 */
public class SplashScreen extends javax.swing.JFrame {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Creates new form SplashScreen */
    public SplashScreen() {
        initComponents();
        GUIUtils.makeTransparent(Licence);
        Licence.setAlignmentX(javax.swing.JComponent.CENTER_ALIGNMENT);

    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        about = new javax.swing.JButton();
        Licence = new javax.swing.JTextArea();
        Headline = new javax.swing.JLabel();
        OK = new javax.swing.JButton();
        logo = new javax.swing.JLabel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        about.setText("About");
        about.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutActionPerformed(evt);
            }
        });

        jPanel1.add(about, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 100, 90, -1));

        Licence.setBackground(new java.awt.Color(246, 246, 246));
        Licence.setEditable(false);
        Licence.setFont(new java.awt.Font("Tahoma", 0, 12));
        Licence.setText("(c) 2005,  Benedikt Fries and Matthias Klusch\nGerman Research Center for Artificial Intelligence\nReleased under the Mozilla Public Licence MPL 1.1");
        jPanel1.add(Licence, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 40, 280, 50));

        Headline.setFont(new java.awt.Font("Tahoma", 1, 18));
        Headline.setText("OWLS-MX Matchmaker " + GUIState.version);
        jPanel1.add(Headline, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, -1, -1));

        OK.setText("OK");
        OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKActionPerformed(evt);
            }
        });

        jPanel1.add(OK, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 100, 90, -1));

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource(GUIState.logoPath)));
        jPanel1.add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 0, -1, 130));
        setSize(465,165);
        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 130));

        //pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void aboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ShowPanelFrame().setVisible(true);
            }
        });
        dispose();
    }//GEN-LAST:event_aboutActionPerformed

    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BaseWindow().setVisible(true);
            }
        });
        dispose();
    }//GEN-LAST:event_OKActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SplashScreen().setVisible(true);                
            }
        });        
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Headline;
    private javax.swing.JTextArea Licence;
    private javax.swing.JButton OK;
    private javax.swing.JButton about;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel logo;
    // End of variables declaration//GEN-END:variables
    
}
