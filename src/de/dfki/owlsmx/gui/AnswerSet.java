/*
 * Created on 31. Juli 2005, 19:05
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


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.dfki.owlsmx.OWLSMXMatchmaker;
import de.dfki.owlsmx.gui.data.HybridServiceItem;
import de.dfki.owlsmx.gui.data.Query;
import de.dfki.owlsmx.gui.data.SemanticServiceItem;
import de.dfki.owlsmx.gui.data.ServiceItem;
import de.dfki.owlsmx.gui.data.SyntacticServiceItem;
import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.SortingMethod;
import de.dfki.owlsmx.gui.util.UpdateDataListener;
import de.dfki.owlsmx.gui.util.Utils;
import de.dfki.owlsmx.gui.util.filefilter.ResultFilter;

/**
 *
 * @author  Ben
 */
public class AnswerSet extends javax.swing.JPanel implements UpdateDataListener, ActionListener, ItemListener, TreeSelectionListener {
    
	protected int sort = GUIState.SORT_HYBRID;
	protected javax.swing.JButton explain;
	protected javax.swing.JButton queryDetails;
	protected javax.swing.JButton queryGrounding;
	protected javax.swing.JScrollPane queryInputScrollPane;
	protected javax.swing.JList queryInputs;
	protected javax.swing.JLabel queryInputsLabel;
	protected javax.swing.JLabel queryLabel;
	protected javax.swing.JTextField queryName;
	protected javax.swing.JList queryOutputs;
	protected javax.swing.JLabel queryOutputsLabel;
	protected javax.swing.JScrollPane queryOutputsScrollPane;
	protected javax.swing.JTree result;
	protected javax.swing.JLabel resultLabel;
	protected javax.swing.JScrollPane resultScrollPane;
	protected javax.swing.JButton save;
	protected javax.swing.JButton serviceDetails;
	protected javax.swing.JButton serviceGrounding;
	protected javax.swing.JList serviceInputs;
	protected javax.swing.JLabel serviceInputsLabel;
	protected javax.swing.JScrollPane serviceInputsScrollPane;
	protected javax.swing.JLabel serviceLabel;
	protected javax.swing.JTextField serviceName;
	protected javax.swing.JList serviceOutputs;
	protected javax.swing.JLabel serviceOutputsLabel;
	protected javax.swing.JScrollPane serviceOutputsScrollPane;
	protected javax.swing.JComboBox sortingMethod;
	private DefaultListModel serviceInputsDataModel = new DefaultListModel();
	private DefaultListModel serviceOutputsDataModel = new DefaultListModel();
	private DefaultListModel queryInputsDataModel = new DefaultListModel();
	private DefaultListModel queryOutputsDataModel = new DefaultListModel();
	private Map resultSet = new HashMap();
	private JFileChooser fc = new JFileChooser();
	
    /**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	/** Creates new form Result */
    public AnswerSet() {
        initComponents();
        updateData();
    }
    
    public AnswerSet(Set result) {
    	initComponents();
    }
    
    private void setSorting(int sort){
    	for (int i=0; i<sortingMethod.getItemCount();i++){
    		if ( ( (SortingMethod)sortingMethod.getItemAt(i)).getSortingMethod()==sort )
    			sortingMethod.setSelectedIndex(i);
    	}
    }
    
    public AnswerSet(Set result, int sorting) {
    	initComponents();
    	setSorting(sorting);
    }
    
    public AnswerSet(Set result, SortingMethod sort) {
    	initComponents();
    	setSorting(sort.getSortingMethod());
    }
    
    

    protected void initComponents() {
        resultScrollPane = new javax.swing.JScrollPane();
        result = new javax.swing.JTree();
        result.addTreeSelectionListener(this);
        result.setCellRenderer(new ResultCellRenderer());
        resultLabel = new javax.swing.JLabel();
        queryName = new javax.swing.JTextField();
        queryLabel = new javax.swing.JLabel();
        queryInputScrollPane = new javax.swing.JScrollPane();
        queryInputs = new javax.swing.JList();
        queryOutputsScrollPane = new javax.swing.JScrollPane();
        queryOutputs = new javax.swing.JList();
        queryInputsLabel = new javax.swing.JLabel();
        queryOutputsLabel = new javax.swing.JLabel();
        serviceLabel = new javax.swing.JLabel();
        serviceName = new javax.swing.JTextField();
        serviceInputsScrollPane = new javax.swing.JScrollPane();
        serviceInputs = new javax.swing.JList();
        serviceOutputsScrollPane = new javax.swing.JScrollPane();
        serviceOutputs = new javax.swing.JList();
        serviceInputsLabel = new javax.swing.JLabel();
        serviceOutputsLabel = new javax.swing.JLabel();
        queryDetails = new javax.swing.JButton();
        queryGrounding = new javax.swing.JButton();
        serviceDetails = new javax.swing.JButton();
        serviceGrounding = new javax.swing.JButton();
        save = new javax.swing.JButton();
        explain = new javax.swing.JButton();
        sortingMethod = new javax.swing.JComboBox();
        

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setMinimumSize(new java.awt.Dimension(650, 450));
        setPreferredSize(new java.awt.Dimension(650, 450));
        
        resultScrollPane.setViewportView(result);
        add(resultScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 280, 300));

        resultLabel.setText("Answer set");
        add(resultLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 140, -1));

        add(queryName, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 20, 330, -1));

        queryLabel.setText("Query");
        add(queryLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, 80, -1));

        queryInputScrollPane.setViewportView(queryInputs);

        add(queryInputScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 60, 160, 115));

        queryOutputsScrollPane.setViewportView(queryOutputs);

        add(queryOutputsScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 60, 160, 115));

        queryInputsLabel.setText("Inputs");
        add(queryInputsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, 180, 20));

        queryOutputsLabel.setText("Outputs");
        add(queryOutputsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 40, 150, 20));

        serviceLabel.setText("Service");
        add(serviceLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 175, 80, -1));

        add(serviceName, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 195, 330, -1));

        serviceInputsScrollPane.setViewportView(serviceInputs);

        add(serviceInputsScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 235, 150, 115));

        serviceOutputsScrollPane.setViewportView(serviceOutputs);

        add(serviceOutputsScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 235, 150, 115));

        serviceInputsLabel.setText("Inputs");
        add(serviceInputsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 215, 160, 20));

        serviceOutputsLabel.setText("Ouputs");
        add(serviceOutputsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 215, 160, 20));
       
        
        sortingMethod.addItem(new SortingMethod(GUIState.SORT_SYNTACTIC));
        sortingMethod.addItem(new SortingMethod(GUIState.SORT_SEMANTIC));
        sortingMethod.addItem(new SortingMethod(GUIState.SORT_HYBRID));
        sortingMethod.addItemListener(this);
        sortingMethod.setSelectedIndex(sortingMethod.getItemCount()-1);
        add(sortingMethod, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 280, 20));          
        
//        Dimension dim = new Dimension(800,400);
//        this.setSize(dim);
//        this.setMinimumSize(dim);

        queryDetails.setText("Querydetails");
        queryDetails.addActionListener(this);
        add(queryDetails, new org.netbeans.lib.awtextra.AbsoluteConstraints(485, 355, 140, -1));
        
        queryGrounding.setText("Querygrounding");
        queryGrounding.addActionListener(this);
        add(queryGrounding, new org.netbeans.lib.awtextra.AbsoluteConstraints(485, 385, 140, -1));

        serviceDetails.setText("Servicedetails");
        serviceDetails.addActionListener(this);
        add(serviceDetails, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 355, 140, -1));
        
        serviceGrounding.setText("Servicegrounding");
        serviceGrounding.addActionListener(this);
        add(serviceGrounding, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 385, 140, -1));

        save.setText("Save");
        save.addActionListener(this);
        add(save, new org.netbeans.lib.awtextra.AbsoluteConstraints(169, 355, 120, -1));

        explain.setText("Explain");
        explain.setEnabled(false);
        explain.addActionListener(this);
        add(explain, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 355, 120, -1)); 
        fc.addChoosableFileFilter(new ResultFilter());
        
    }
    
    private void prepareServicesForQuery(DefaultMutableTreeNode queryNode, SortedSet services){    	
    	Iterator serviceIterator = services.iterator();
    	if ( (sort == GUIState.SORT_HYBRID) || (sort == GUIState.SORT_SEMANTIC)) {    		
    	//GUIState.displayWarning(((Query)me.getKey()).getName(),"Size of serviceset " + ((SortedSet)me.getValue()).size());
    		DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[6];
    		nodes[OWLSMXMatchmaker.EXACT] = new DefaultMutableTreeNode("Exact");
    		nodes[OWLSMXMatchmaker.PLUGIN] = new DefaultMutableTreeNode("Plugin");
    		nodes[OWLSMXMatchmaker.SUBSUMES] =  new DefaultMutableTreeNode("Subsumes");
    		nodes[OWLSMXMatchmaker.SUBSUMED_BY] = new DefaultMutableTreeNode("Subsumed-By"); 
    		nodes[OWLSMXMatchmaker.NEAREST_NEIGHBOUR] = new DefaultMutableTreeNode("Nearest Neighbour");
    		nodes[OWLSMXMatchmaker.FAIL] = new DefaultMutableTreeNode("Fail");
    		HybridServiceItem hItem;
    		SemanticServiceItem sItem;
			while (serviceIterator.hasNext()) {
				if (sort == GUIState.SORT_HYBRID){
					hItem = (HybridServiceItem) serviceIterator.next();	
					nodes[hItem.getDegreeOfMatch()].add(new DefaultMutableTreeNode(hItem));
					}
				else {
					sItem = (SemanticServiceItem) serviceIterator.next();
					
					// Pure precaution that is necessary for the random dummy results
					// should neve appear during the real matching process
					if(sItem.getDegreeOfMatch() == OWLSMXMatchmaker.NEAREST_NEIGHBOUR)
						sItem.setDegreeOfMatch(OWLSMXMatchmaker.FAIL);
					nodes[sItem.getDegreeOfMatch()].add(new DefaultMutableTreeNode(sItem));
					}
				}
			for (int i = 0; i< nodes.length;i++) {
				if (nodes[i].getChildCount()>0){					
					queryNode.add(nodes[i]);
					String tmp = nodes[i].getUserObject().toString();
					int count = nodes[i].getChildCount();
					nodes[i].setUserObject(tmp + " (" + count + ")");
					//nodes[i].setUserObject(nodes[i].getUserObject().toString() + " (" + nodes[i].getChildCount() + ")");
				}
			}
			return;
	    	}
    	serviceIterator = services.iterator();
    	while (serviceIterator.hasNext()) {
    		SyntacticServiceItem synItem = (SyntacticServiceItem) serviceIterator.next();						
    		queryNode.add(new DefaultMutableTreeNode(synItem));
    	}
		
    }
    
	public void updateData() {
		this.result.removeAll();
		resultSet = TestCollection.getInstance().getMatchmakerAnswerset(sort);
//		if ( (MatchmakerInterface.getInstance().didRun()) &&(resultSet.size()<=0) ) {
//		GUIState.displayWarning(this,"Empty answer set", "No relevant services found for any query  ");
//	}
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Result");
		DefaultTreeModel model = new DefaultTreeModel(root);
		Iterator iter = resultSet.entrySet().iterator();		
		Map.Entry me;
		DefaultMutableTreeNode node;		
		while(iter.hasNext()) {
			me = (Map.Entry) iter.next();
			node = new DefaultMutableTreeNode((Query)me.getKey());
			root.add(node);
			if (((SortedSet)me.getValue()).size()<=0) {
//				GUIState.displayWarning(this,"Empty answer set", "No relevant services found for query  " + ((Query)me.getKey()).getName());
			}
			else
				prepareServicesForQuery(node,(SortedSet)me.getValue());
			
		}
		this.result.setModel(model);
	}

	public void itemStateChanged(ItemEvent event) {
		if (event.getSource().equals(sortingMethod)){	
			//GUIState.displayWarning("Attention","Changing sorting method to " + sortingMethod.getSelectedItem().toString());
			sort = ( (SortingMethod) sortingMethod.getSelectedItem() ).getSortingMethod();
			GUIState.getInstance().setSorting(sort);
			updateData();
		}		
	}
	
	private void displayQuery(Query query) {
		if (query==null)
			return;
		queryName.setText(query.getName());
		queryInputsDataModel.removeAllElements();
		for (Iterator iter = query.getInputs().iterator();iter.hasNext();) {
			queryInputsDataModel.addElement(Utils.getConcept((URI)iter.next(),true));	
		}
		queryInputs.setModel(queryInputsDataModel);
		
		queryOutputsDataModel.removeAllElements();
		for (Iterator iter = query.getOutputs().iterator();iter.hasNext();) {
			queryOutputsDataModel.addElement(Utils.getConcept((URI)iter.next(),true));	
		}
		queryOutputs.setModel(queryOutputsDataModel);
	}
	
	private void displayService(ServiceItem service) {
		if (service==null)
			return;
		serviceName.setText(service.getName());
		serviceInputsDataModel.removeAllElements();
		for (Iterator iter = service.getInputs().iterator();iter.hasNext();) {
			serviceInputsDataModel.addElement(Utils.getConcept((URI)iter.next(),true));	
		}
		serviceInputs.setModel(serviceInputsDataModel);
		
		serviceOutputsDataModel.removeAllElements();
		for (Iterator iter = service.getOutputs().iterator();iter.hasNext();) {
			serviceOutputsDataModel.addElement(Utils.getConcept((URI)iter.next(),true));	
		}
		serviceOutputs.setModel(serviceOutputsDataModel);		
	}
	
	private Query getQuery() {
		TreePath path = result.getSelectionPath();
		if ( (path==null) || (path.getPathCount()<=1) )
			return null;
		return (Query) ((DefaultMutableTreeNode)path.getPathComponent(1)).getUserObject();
	}
	
	private ServiceItem getService() {		
		TreePath path = result.getSelectionPath();
		if ( (path==null) || (path.getPathCount()<3) )
			return null;
		if ( (path.getPathCount()>3) || ((path.getPathCount()>2) && (sort == GUIState.SORT_SYNTACTIC)) )
			return (ServiceItem)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
		return null;
	}
	
	private ServiceItem getService(TreePath path) {	
		if ( (path==null) || (path.getPathCount()<3) )
			return null;
		if ( (path.getPathCount()>3) || ((path.getPathCount()>2) && (sort == GUIState.SORT_SYNTACTIC)) )
			return (ServiceItem)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
		return null;
	}
	
	private Set getServices(){
		Set res = new HashSet();
		TreePath[] paths = result.getSelectionPaths();
		ServiceItem service;
		for (int i=0; i<paths.length;i++) {
			service = getService(paths[i]);
			if (service!=null) {
				res.add(service);
				}
		}
		return res;
	}

	public void valueChanged(TreeSelectionEvent event) {
		displayQuery( getQuery() );
		displayService(getService());
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(queryDetails)){			
			java.awt.EventQueue.invokeLater(new ShowPanelFrame(new DisplayService(getQuery()),false));	
		}
		
		else if (event.getSource().equals(serviceDetails)){
			for (Iterator iter = getServices().iterator();iter.hasNext();) {
				ServiceItem service = (ServiceItem) iter.next();
				if (service!=null){
					java.awt.EventQueue.invokeLater(new ShowPanelFrame(new DisplayService(service),false));
				}
			}
			
		}
		
		else if (event.getSource().equals(queryGrounding)) {
			ServiceItem service = (ServiceItem) getQuery();
			if(service != null) {
				if(service.hasWSDLGrounding())
					java.awt.EventQueue.invokeLater(new ShowPanelFrame(new DisplayGrounding(service),false));
				else
					javax.swing.JOptionPane.showMessageDialog(null, "The service \"" + service.getName() + "\" has no WSDL Grounding!", "No Grounding!", javax.swing.JOptionPane.WARNING_MESSAGE);
			}				
		}
		
		else if (event.getSource().equals(serviceGrounding)) {
			for (Iterator iter = getServices().iterator(); iter.hasNext(); ) {
				ServiceItem service = (ServiceItem) iter.next();
				if (service != null) {
					if(service.hasWSDLGrounding())
						java.awt.EventQueue.invokeLater(new ShowPanelFrame(new DisplayGrounding(service),false));
					else
						javax.swing.JOptionPane.showMessageDialog(null, "The service \"" + service.getName() + "\" has no WSDL Grounding!", "No Grounding!", javax.swing.JOptionPane.WARNING_MESSAGE);			
				}
			}
		}
		
		else if (event.getSource().equals(save)){
	        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
	        	saveResult(fc.getSelectedFile().getAbsolutePath());
	        }
		}
				
		else if (event.getSource().equals(explain)){
			GUIState.displayWarning("Not yet implemented", "Sorry the explanation component is not yet implemented");
		}
	}
	
	private void saveResult(String path) {
		try {
			if (!path.toLowerCase().endsWith(".res"))
			   path +=  ".res";
			BufferedWriter output = new BufferedWriter(new FileWriter(new File(path)));
			Query query;
			Iterator answerSet;
			int queries = 1;
			int counter = 1;
			String write;
			String uri,s_uri,result_count;
			HybridServiceItem item;			
			for(Iterator iter = resultSet.keySet().iterator();iter.hasNext();){
				query = (Query) iter.next();
				uri = query.getURI().toString();
				if (uri.contains("/"))
					uri = uri.substring(uri.lastIndexOf("/")+1);
				write = "Query " + queries + " " + uri;				
				//write = write.replaceAll("[\\r\\f]","");
				output.write(write);				
				output.newLine();
				
				for (answerSet = ((SortedSet) resultSet.get(query)).iterator();answerSet.hasNext();) {
					item = (HybridServiceItem) answerSet.next() ;
					s_uri = item.getURI().toString();
					if (s_uri.contains("/"))
						s_uri = s_uri.substring(s_uri.lastIndexOf("/")+1);
					if (counter<10)
						result_count = "0" + counter;
					else
						result_count = "" + counter;
					write = result_count + "  (" + item.getDegreeOfMatch() + "," + item.getSyntacticSimilarity() + ") " + s_uri + "\n";
					//write = write.replaceAll("[\\r\\f]","");
					output.write(write);
					output.newLine();
					counter++;
				}
				counter =1;
				queries++;
			}
			output.close();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
