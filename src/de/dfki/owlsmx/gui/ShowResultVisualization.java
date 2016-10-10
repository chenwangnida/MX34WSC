/*
 * Created on 22.10.2005
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
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jibble.epsgraphics.EpsGraphics2D;

import de.dfki.owlsmx.gui.util.Converter;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.filefilter.EPSFilter;
import de.dfki.owlsmx.gui.util.filefilter.JPGFilter;
import de.dfki.owlsmx.gui.util.filefilter.PDFFilter;
import de.dfki.owlsmx.gui.util.filefilter.PNGFilter;

public class ShowResultVisualization extends javax.swing.JFrame implements Runnable,ActionListener {
    
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private javax.swing.JButton close;
    private javax.swing.JButton save;
    private JFreeChart chart;
    private JPanel chartPanel;
	private JFileChooser fc;
    
	/** Creates new form AboutFrame */
    public ShowResultVisualization(JFreeChart chart) {
    	this.chart = chart;
    	chartPanel = new ChartPanel(chart);
    	initComponents();
    }

    
    private void initComponents() {
    	JPanel menu = new JPanel();
        close = new javax.swing.JButton();
        

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(chartPanel, java.awt.BorderLayout.CENTER); 
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        close.setText("Close");
        close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeActionPerformed(evt);
            }
        });

        menu.add(close);

       	save = new javax.swing.JButton();
       	save.setText("Save");
       	save.addActionListener(this);
        menu.add(save);
        
        add(menu,java.awt.BorderLayout.PAGE_END);
        fc  = new JFileChooser();
        fc.addChoosableFileFilter(new PNGFilter());
        fc.addChoosableFileFilter(new JPGFilter());     
        fc.addChoosableFileFilter(new PDFFilter());
        fc.addChoosableFileFilter(new EPSFilter());
        pack();
    }

    private void closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeActionPerformed
        dispose();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

    }
    

    // End of variables declaration//GEN-END:variables
	public void run() {
		setVisible(true);
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(save))
			saveActionPerformed(e);
	}
	
   public void printGraphics2DtoEPS(String path)  {
	   try {
		   if (!path.toLowerCase().endsWith(".eps"))
			   path +=  ".eps";
		   java.io.FileOutputStream outputStream = new java.io.FileOutputStream(path);	
		   EpsGraphics2D g = new EpsGraphics2D("", outputStream, 0, 0, ResultVisualization.graphPrintWidth, ResultVisualization.graphPrintWidth);
		   
		   
		   g.flush();
		   g.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
   }
	   
   private void saveActionPerformed(java.awt.event.ActionEvent event) {//GEN-FIRST:event_saveActionPerformed
        try {
    		if (event.getSource().equals(save)) {    			
    			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
    				if (fc.getFileFilter().getClass().equals((new PNGFilter()).getClass()))
    					if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".png"))
    						ChartUtilities.saveChartAsPNG(fc.getSelectedFile(), chart, ResultVisualization.graphPrintWidth, ResultVisualization.graphPrintHeight);
    					else
    						ChartUtilities.saveChartAsPNG(new File (fc.getSelectedFile().getAbsolutePath() + ".png"), chart, ResultVisualization.graphPrintWidth, ResultVisualization.graphPrintHeight);
    				
    				else if (fc.getFileFilter().getClass().equals((new JPGFilter()).getClass()))
    					if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".png"))
    						ChartUtilities.saveChartAsJPEG(fc.getSelectedFile(), chart, ResultVisualization.graphPrintWidth, ResultVisualization.graphPrintHeight);
    					else
    						ChartUtilities.saveChartAsJPEG(new File (fc.getSelectedFile().getAbsolutePath() + ".png"), chart, ResultVisualization.graphPrintWidth, ResultVisualization.graphPrintHeight);			
    				
    				else if (fc.getFileFilter().getClass().equals((new PDFFilter()).getClass()))
    					if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".pdf"))
    						Converter.convertToPdf(chart, ResultVisualization.graphPrintWidth,ResultVisualization.graphPrintHeight,fc.getSelectedFile().getAbsolutePath());
    					else
    						Converter.convertToPdf(chart, ResultVisualization.graphPrintWidth,ResultVisualization.graphPrintHeight,fc.getSelectedFile().getAbsolutePath() + ".pdf");
    				else if (fc.getFileFilter().getClass().equals((new EPSFilter()).getClass()))
    						printGraphics2DtoEPS(fc.getSelectedFile().getAbsolutePath());
    			}
        	}
        }
        catch(Exception e) {        	
        	GUIState.displayWarning(e.getClass().toString(),"Couldn't save file " + fc.getSelectedFile().getAbsolutePath());
        	e.printStackTrace();
        }
    }




}
