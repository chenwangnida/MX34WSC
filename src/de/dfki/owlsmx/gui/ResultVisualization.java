/*
 * Created on 31. Juli 2005, 19:29
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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jibble.epsgraphics.EpsGraphics2D;

import de.dfki.owlsmx.analysis.MemoryContainer;
import de.dfki.owlsmx.analysis.MacroAvgRecallPrecision;
import de.dfki.owlsmx.analysis.RecallPrecisionPair;
import de.dfki.owlsmx.analysis.PassedTimeContainer;
import de.dfki.owlsmx.gui.data.HybridServiceItem;
import de.dfki.owlsmx.gui.data.Query;
import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.gui.util.Converter;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.GUIUtils;
import de.dfki.owlsmx.gui.util.MatchmakerInterface;
import de.dfki.owlsmx.gui.util.UpdateDataListener;
import de.dfki.owlsmx.gui.util.print2DtoPS;
import de.dfki.owlsmx.gui.util.filefilter.EPSFilter;
import de.dfki.owlsmx.gui.util.filefilter.JPGFilter;
import de.dfki.owlsmx.gui.util.filefilter.PDFFilter;
import de.dfki.owlsmx.gui.util.filefilter.PNGFilter;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JPanel;


/**
 *
 * @author  Ben
 */
public class ResultVisualization extends javax.swing.JPanel implements UpdateDataListener, ActionListener {

	private static final long serialVersionUID = 1L;	
	private javax.swing.JButton save;
	private javax.swing.JLabel graphLabel;
	private javax.swing.JLabel logo;
	private JFileChooser fc;
	private JFreeChart chart;
	public final static int graphWidth = 400;
	public final static int graphHeight = 400;
	public final static int graphPrintWidth = 600;
	public final static int graphPrintHeight =600;		
	//private String[] graphs = new String[] { "Recall/Precision", "average QRT", "memory consumption"};
    private JPanel chartPanel = createRPPanel();
    
    private javax.swing.JButton launch;
    private javax.swing.JButton show;
//    private javax.swing.JLabel showWindow;
    private javax.swing.JRadioButton showAQRT;
    private javax.swing.JRadioButton showRP;
    private javax.swing.JRadioButton showMemory;
    private ButtonGroup showGraph;
    
    
    /** Creates new form ResultVisualization */
    public ResultVisualization() {
        initComponents();
        chartPanel = new JPanel();
        add(chartPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 5, graphWidth, graphHeight));
        showRP.setSelected(true);
    }
    
    public javax.swing.JPanel createRPPanel() {
    	if (!GUIState.getInstance().getPrecisionRecall())
    		return new JPanel();
    	chart = createRPChart();
    	JPanel panel= new ChartPanel(chart);
    	GUIUtils.makeTransparent(panel);
    	return panel;
    }    
    
    public javax.swing.JPanel createAQRTPanel() { 
    	if (!GUIState.getInstance().getDisplayAQR())
    		return new JPanel();
    	chart = createAQRTChart();
    	JPanel panel= new ChartPanel(chart);
    	GUIUtils.makeTransparent(panel);
    	return panel;
    }    
    
    public javax.swing.JPanel createMemoryPanel() {
    	if (!GUIState.getInstance().getMemoryConsumption())
    		return new JPanel();
    	chart = createMemoryChart();
    	JPanel panel= new ChartPanel(chart);
    	GUIUtils.makeTransparent(panel);
    	return panel;
    }    
    
    private XYSeries createSeriesFromDataMap(Map datamap) {    	
     	XYSeries ser = new XYSeries("OWLS-MX");
    	if (datamap.size()<=0) {
//    		System.err.println(this.getClass().toString() + "|createSeriesFromDataMap: Datamap is empty");    	
    		return ser;
    	}
    	Map.Entry entry;
    	for (Iterator iter = datamap.entrySet().iterator();iter.hasNext();) {
    		entry = (Map.Entry) iter.next();
    		ser.add(((Integer)entry.getKey()).doubleValue(),((Long)entry.getValue()).doubleValue());
    	}
//    	System.err.println("createSeriesFromDataMap items: " + ser.getItemCount());
    	return ser;
    }
    
    private JFreeChart createMemoryChart() {    	
    	XYSeriesCollection data = new XYSeriesCollection();
    	Map memory = MemoryContainer.getInstance().getStoredValues();    	    	
//    	System.err.println("Memory consumption: " + memory);
    	data.addSeries(createSeriesFromDataMap(memory));
    	JFreeChart chart = ChartFactory.createXYLineChart("Memoryconsumption", "Services", "Memory (KByte)", data, org.jfree.chart.plot.PlotOrientation.VERTICAL, true,true, false);
    	XYPlot plot = chart.getXYPlot();        
    	XYItemRenderer renderer = (XYItemRenderer) plot.getRenderer();
    	renderer.setSeriesPaint(0, Color.red);   
    	chart.removeLegend();
    	return chart;
    }
    
    private JFreeChart createAQRTChart() {   	    	    	
    	XYSeriesCollection data = new XYSeriesCollection();
    	Map passedTime = PassedTimeContainer.getInstance().getStoredValues();
//    	System.err.println("Passed time: " + passedTime);
    	passedTime.put(new Integer(0),new Long(0));
    	data.addSeries(createSeriesFromDataMap(passedTime));    	
    	JFreeChart chart = ChartFactory.createXYLineChart("Average query response time", "Services", "Time (ms)", data, org.jfree.chart.plot.PlotOrientation.VERTICAL, true,true, false);
    	XYPlot plot = chart.getXYPlot();        
    	XYItemRenderer renderer = (XYItemRenderer) plot.getRenderer();
    	renderer.setSeriesPaint(0, Color.red);      
    	chart.removeLegend();
    	return chart;
    }
    
/*
    private  XYSeries createDummySeries() {
    	XYSeries ser = new XYSeries("OWLS-MX Matchmaker");
    	ser.add(0.12345679012345678, 0.9090909090909091);
    	ser.add(0.1382716049382716, 0.9180327868852459);
    	ser.add(0.19012345679012346, 0.927710843373494);
    	ser.add(0.2419753086419753, 0.9074074074074074);
    	ser.add(0.2765432098765432, 0.7887323943661971);
    	ser.add(0.3308641975308642, 0.7528089887640449);
    	ser.add(0.37777777777777777, 0.75);
    	ser.add(0.41728395061728396, 0.7647058823529411);
    	ser.add(0.4666666666666667, 0.75);
    	ser.add(0.5209876543209877, 0.7301038062283737);
    	ser.add(0.5580246913580247, 0.7361563517915309);
    	ser.add(0.6074074074074074, 0.7365269461077845);
    	ser.add(0.6592592592592592, 0.7255434782608695);
    	ser.add(0.7061728395061728, 0.731457800511509);
    	ser.add(0.7481481481481481, 0.7129411764705882);
    	ser.add(0.8049382716049382, 0.692144373673036);
    	ser.add(0.8493827160493828, 0.694949494949495);
    	ser.add(0.8888888888888888, 0.6605504587155964);
    	ser.add(0.928024691358025, 0.635681063122923);
    	ser.add(0.932098765432099, 0.631404958677686);
    	return ser;
    }
*/
    
    private String getFileName(String uri) {
    	return uri.substring(uri.lastIndexOf("/"));
    }
    
    private ArrayList getURIasStringFromServiceSet(SortedSet services){
    	ArrayList result = new ArrayList();
    	HybridServiceItem item;
    	for(Iterator iter = services.iterator();iter.hasNext();) {
    		item = (HybridServiceItem)iter.next();
//    		ErrorLog.instanceOf().report(item.getDegreeOfMatch() + " " + getFileName(item.getURI().toString()));
//    		System.err.println("Matchmaker " + (item).getURI());
    		result.add(getFileName(item.getURI().toString()));
    	}
    	return result;
    }
    
    private ArrayList getURIasStringFromURISet(SortedSet services){
    	ArrayList result = new ArrayList();
    	String uri;
    	for(Iterator iter = services.iterator();iter.hasNext();) {
    		uri = ((URI)iter.next()).toString();
//    		System.err.println("Relevant " + uri);    		
    		result.add(getFileName(uri.toString()));
    	}
    	return result;
    }
    
    
    
    private XYSeries createRPSeries(MacroAvgRecallPrecision recall,boolean exists) {
    	XYSeries ser = new XYSeries("OWLS-MX Matchmaker");

//        System.err.println("RP ");
    	if (exists){
    		//ser.add(0,0);
    		return ser;
    	}
        ArrayList resultValues = recall.getMacroAveragePrecisions();
        RecallPrecisionPair pair;
        boolean begins = false;
//        System.err.println("RP " + resultValues);
    	for (int i=0; i<resultValues.size();i++) {
	    	pair = (RecallPrecisionPair) resultValues.get(i);
	    	if (pair.precision>0)
	    		begins = true;
	    	if (begins && (!(new Double(pair.precision)).isNaN()) )
	    		ser.add(pair.recall, pair.precision);  
//	    	System.err.println("(" + pair.recall + "," + pair.precision + ")");
    	}
    	return ser;
    }
    
    public JFreeChart createRPChart() {
    	
    	Query query;
    	Map.Entry me;
        MacroAvgRecallPrecision recall = new MacroAvgRecallPrecision(20);
    	ArrayList retrievedServices = new ArrayList();
    	ArrayList relevantServices = new ArrayList();
    	for (Iterator iter = TestCollection.getInstance().getMatchmakerAnswerset(GUIState.getInstance().getSorting()).entrySet().iterator();iter.hasNext();) {
    		me = (Map.Entry)(iter.next());
    		query = (Query) me.getKey();
    		retrievedServices = getURIasStringFromServiceSet((SortedSet)me.getValue());    		    		
    		relevantServices = getURIasStringFromURISet(TestCollection.getInstance().getAnswerset(query.getURI()));
//    		owlsmx.io.ErrorLog.instanceOf().report(query.toString() + ":");
//    		owlsmx.io.ErrorLog.instanceOf().report("  " + retrievedServices.toString());
//    		owlsmx.io.ErrorLog.instanceOf().report("  " + relevantServices.toString());
    		if (relevantServices.size()<=0)
    			GUIState.displayWarning(this, "Error when computing recall/precision graph", "Relevance set is empty. Maybe forgotten to define?");
    		recall.processRecallPrecision(retrievedServices,relevantServices);
    	}
 
    	XYSeriesCollection data = new XYSeriesCollection();
    	data.addSeries(createRPSeries(recall,(retrievedServices.size()<=0)));
    	
    	JFreeChart chart = ChartFactory.createXYLineChart("Recall/Precision", "Recall", "Precision", data, org.jfree.chart.plot.PlotOrientation.VERTICAL, true,true, false);
    	chart.removeLegend();
    	XYPlot plot = chart.getXYPlot();        
    	XYItemRenderer renderer = (XYItemRenderer) plot.getRenderer();
    	renderer.setSeriesPaint(0, Color.red);              
    	return chart;
    }
    
    private void initComponents() {        
        graphLabel = new javax.swing.JLabel();
        save = new javax.swing.JButton();
        logo = new javax.swing.JLabel();        
        showAQRT = new javax.swing.JRadioButton();
        showRP = new javax.swing.JRadioButton();
        showMemory = new javax.swing.JRadioButton();
        launch = new javax.swing.JButton();
        show = new javax.swing.JButton();
        showGraph = new ButtonGroup();
        showGraph.add(showAQRT);
        showGraph.add(showRP);
        showGraph.add(showMemory);
//        showWindow = new javax.swing.JLabel();
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        
//        displayGraph = new javax.swing.JComboBox();
//        displayGraph.setModel(new javax.swing.DefaultComboBoxModel( graphs ));
//        displayGraph.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                displayGraphActionPerformed(evt);
//            }
//        });
//
//        add(displayGraph, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 180, 30));

        graphLabel.setText("Show graph");
        add(graphLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 160, 170, -1));

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource(GUIState.logoPath)));
        add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 10, -1, -1));
        fc  = new JFileChooser();
        fc.addChoosableFileFilter(new PNGFilter());
        fc.addChoosableFileFilter(new JPGFilter());     
        fc.addChoosableFileFilter(new PDFFilter());
        fc.addChoosableFileFilter(new EPSFilter());
        
        showAQRT.setText("Average query response time");
        add(showAQRT, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, -1, -1));

        showRP.setText("Recall/Precision");
        add(showRP, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, -1, -1));

        showMemory.setText("Memory consumption");
        add(showMemory, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, -1, -1));

        
        save.setText("Save displayed graph");
        save.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		saveActionPerformed(evt);
        	}
        });        
        add(save, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 280, 245, -1));
        
        show.setText("Show");
        add(show, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 250, 120, -1));
        show.addActionListener(this);
        
        
        launch.setText("New window");
        add(launch, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 250, 120, -1));
        launch.addActionListener(this);
        
//        showWindow.setText("Show in seperate window");
//        add(showWindow, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 200, -1, -1));    
        
        setMinimumSize(new java.awt.Dimension(800, 400));
        setPreferredSize(new java.awt.Dimension(800, 400));
    }

    private void saveActionPerformed(java.awt.event.ActionEvent event) {//GEN-FIRST:event_saveActionPerformed
        try {
    		if (event.getSource().equals(save)) {    			
    			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
    				if (fc.getFileFilter().getClass().equals((new PNGFilter()).getClass()))
    					if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".png"))
    						ChartUtilities.saveChartAsPNG(fc.getSelectedFile(), chart, graphPrintWidth, graphPrintHeight);
    					else
    						ChartUtilities.saveChartAsPNG(new File (fc.getSelectedFile().getAbsolutePath() + ".png"), chart, graphPrintWidth, graphPrintHeight);
    				
    				else if (fc.getFileFilter().getClass().equals((new JPGFilter()).getClass()))
    					if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".png"))
    						ChartUtilities.saveChartAsJPEG(fc.getSelectedFile(), chart, graphPrintWidth, graphPrintHeight);
    					else
    						ChartUtilities.saveChartAsJPEG(new File (fc.getSelectedFile().getAbsolutePath() + ".png"), chart, graphPrintWidth, graphPrintHeight);			
    				
    				else if (fc.getFileFilter().getClass().equals((new PDFFilter()).getClass()))
    					if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".pdf"))
    						Converter.convertToPdf(chart, graphPrintWidth,graphPrintHeight,fc.getSelectedFile().getAbsolutePath());
    					else
    						Converter.convertToPdf(chart, graphPrintWidth,graphPrintHeight,fc.getSelectedFile().getAbsolutePath() + ".pdf");
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
    
    public void PrintGraphics2DToPS(String path) throws IOException {
    	try {
            //TODO implement saving to PS
    		GUIState.displayWarning("Incomplete feature", "This feature is still under heavy development and can not yet be used");
    		print2DtoPS printer = new print2DtoPS(path, chartPanel ,graphPrintHeight, graphPrintHeight);
    		printer.print(this.createRPPanel().getGraphics(),new PageFormat(),0);
		} catch (PrinterException pe) { 
			GUIState.displayWarning(pe.getClass().toString(), "Couldn't create PS file!\n" + pe.getMessage());
		}
	}
    
   public void printGraphics2DtoEPS(String path)  {
	   try {
		   if (!path.toLowerCase().endsWith(".eps"))
			   path +=  ".eps";
		   java.io.FileOutputStream outputStream = new java.io.FileOutputStream(path);	
		   EpsGraphics2D g = new EpsGraphics2D("", outputStream, 0, 0, graphWidth, graphHeight);
		   //chart.draw(g,new java.awt.Rectangle(graphWidth,graphHeight));
		   g.flush();
		   g.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
   }


    
	public void updateData() {
		if ( (GUIState.getInstance().getDisplayAQR()) ||
			(GUIState.getInstance().getPrecisionRecall()) ||
			(GUIState.getInstance().getMemoryConsumption()))
			showPanel();
		updateUI();
	}
	
	public void actionPerformed(ActionEvent evt) {
    	if (evt.getSource().equals(launch)) {
    		if (showAQRT.isSelected()){
    			if (GUIState.getInstance().getDisplayAQR()) {    				
    				showGraphWindow(createAQRTChart());
    			}
    			else
    				GUIState.displayWarning(this, "Graph not available", "Average query response time is not available, as the necessary measurement were not executed during the matching process");
    		}
    		else if (showMemory.isSelected()) {
    			if (GUIState.getInstance().getMemoryConsumption())
    				showGraphWindow(createMemoryChart());
    			else
    				GUIState.displayWarning(this, "Graph not available", "Memory consumption is not available, as the necessary measurement were not executed during the matching process");
    		}
    		else if (showRP.isSelected()) {
    			if (GUIState.getInstance().getPrecisionRecall())
    				showGraphWindow(createRPChart());
    			else
    				GUIState.displayWarning(this, "Graph not available", "Recall/Precision is not available, as the necessary measurement were not executed during the matching process");
    			}
    		}
    	else if (evt.getSource().equals(show)) {
    		showPanel(); 
    		}
    	updateUI();
	}
	
	private void showPanel() {
		remove(chartPanel);
		if (showAQRT.isSelected()) {
			if ( (!GUIState.getInstance().getDisplayAQR() )&& 
					(MatchmakerInterface.getInstance().didRun()) ) {
				if (GUIState.getInstance().getDisplayAQR())
					GUIState.displayWarning(this, "Graph not available", "Average query response time is not available, as the necessary measurement were not executed during the matching process");
				else
					chartPanel = new JPanel();
				return;
			}
			chartPanel = createAQRTPanel();
		}
		else if (showMemory.isSelected()) {
			if ( (!GUIState.getInstance().getMemoryConsumption())&& 
					(MatchmakerInterface.getInstance().didRun()) ) {
				if (GUIState.getInstance().getMemoryConsumption())
					GUIState.displayWarning(this, "Graph not available", "Memory consumption is not available, as the necessary measurement were not executed during the matching process");
				else
					chartPanel = new JPanel();
				return;
				}
			chartPanel = createMemoryPanel(); 
			
			}
		else if (showRP.isSelected()) {
			if ( (!GUIState.getInstance().getPrecisionRecall())&& 
					(MatchmakerInterface.getInstance().didRun()) ) {
				if (GUIState.getInstance().getPrecisionRecall())
					GUIState.displayWarning(this, "Graph not available", "Recall/Precision is not available, as the necessary measurement were not executed during the matching process");
				else
					chartPanel = new JPanel();
				return;
			}
			chartPanel = createRPPanel();
		}		
		add(chartPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 5, graphWidth, graphHeight));
	}
	
	private void showGraphWindow(JFreeChart Thischart) {
		ShowResultVisualization result = new ShowResultVisualization(Thischart);
        result.run();
	}

    
}
