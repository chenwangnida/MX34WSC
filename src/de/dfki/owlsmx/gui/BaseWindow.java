/*
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


import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.TabState;
import de.dfki.owlsmx.gui.util.UpdateDataListener;

/**
 * Mainframe of the OWLS-MX GUI, including menubar, tabbing and wizard
 *
 * @author  Ben
 */
public class BaseWindow extends javax.swing.JFrame implements java.awt.event.ActionListener, UpdateDataListener {    
    
	private javax.swing.JButton Exit;
    private javax.swing.JTextPane Helptext;
    private javax.swing.JButton Last;
    private javax.swing.JButton Next;
    private javax.swing.JButton ShowWizard;
    private javax.swing.JLabel Step;
    private javax.swing.JTabbedPane Tabs;
    private javax.swing.JPanel Wizard;
	private static final long serialVersionUID = 1L;
	private TestCollectionGUI gui_tc=new TestCollectionGUI();
    private Services gui_services =new Services();
    private Requests gui_queries =new Requests();
    private MatchmakerGUI gui_mm =new MatchmakerGUI();
    private Testing gui_td = new Testing();
    private Result gui_rr = new Result();
    private ResultVisualization gui_rv = new ResultVisualization();
    private TaskGUI gui_intro = new TaskGUI(this);
    private Settings gui_settings  = new Settings(this);
    private RelevanceSet gui_as = new RelevanceSet();
    private int taskIndex;
    private JMenuBar menuBar;
    private JMenu menuFile, menuHelp,menuWizard, menuAbout;
    private JMenuItem item_about;
    private JMenuItem file_item_settings,file_item_quit;
    private JMenuItem wizard_item_show;
    private static final boolean wizardIsActivated = false; 
    private static final boolean helpIsActivated = false; 
    
    public BaseWindow() {  
    	initComponents(); 

        
        
		try{
			//GUIState.generateDummyData();
			//TestCollection.getInstance().runAllQueries();
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Couldn't generate dummy data");
		}   
    }
    
    
    private void initComponents() {
    	getContentPane().setLayout(new GridBagLayout());
    	GridBagConstraints grid_constraints = new GridBagConstraints();
    	grid_constraints.fill = GridBagConstraints.HORIZONTAL;
    	grid_constraints.gridx = 0;
    	grid_constraints.gridy = 1;
    	
        //creation of the tabs
        Tabs = new javax.swing.JTabbedPane();
        Wizard = new javax.swing.JPanel();
        Next = new javax.swing.JButton();
        Last = new javax.swing.JButton();
        Exit = new javax.swing.JButton();
        Helptext = new javax.swing.JTextPane();
        Step = new javax.swing.JLabel();
        ShowWizard = new javax.swing.JButton();

        

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        Tabs.setMinimumSize(new java.awt.Dimension(800, 450));
        Tabs.setPreferredSize(new java.awt.Dimension(800, 450));
        getContentPane().add(Tabs,grid_constraints);

        grid_constraints.gridx = 0;
    	grid_constraints.gridy = 2;
    	
        Wizard.setLayout(null);

        Wizard.setBackground(new java.awt.Color(255, 255, 255));
        Wizard.setBorder(new javax.swing.border.EtchedBorder());
        Wizard.setMinimumSize(new java.awt.Dimension(800, 100));
        Wizard.setPreferredSize(new java.awt.Dimension(800, 100));
        Next.setActionCommand("Next");
        Next.setText("Next");
        Next.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GUIState.getInstance().setNextStep();
//                updateWizard();
            }
        });
        
        Wizard.add(Next);
        Next.setBounds(5, 5, 75, 23);
        Last.setText("Back");
        Last.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GUIState.getInstance().setLastStep();
                updateWizard();
            }
        });
        Wizard.add(Last);
        Last.setBounds(5, 35, 75, 23);

        Exit.setText("Hide");
        Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitActionPerformed(evt);
            }
        });

        Wizard.add(Exit);
        Exit.setBounds(5, 65, 75, 23);

        Helptext.setBorder(null);
        Helptext.setFont(new java.awt.Font("MS Sans Serif", 0, 10));
        Helptext.setText("In this step you should add services as advertisements to the matchmaker. You can either add an entire predefined testcollection, an entire local directory or single services.");
        Helptext.setMaximumSize(new java.awt.Dimension(2147483647, 80));
        Helptext.setMinimumSize(new java.awt.Dimension(510, 80));
        Helptext.setPreferredSize(new java.awt.Dimension(510, 80));
        Wizard.add(Helptext);
        Helptext.setBounds(90, 20, 510, 70);

        Step.setFont(new java.awt.Font("Arial", 0, 14));
        Step.setText("Step 2/5 Matchmaker");
        Step.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Step.setMaximumSize(new java.awt.Dimension(2147483647, 20));
        Step.setMinimumSize(new java.awt.Dimension(510, 20));
        Step.setPreferredSize(new java.awt.Dimension(510, 20));
        Wizard.add(Step);
        Step.setBounds(90, 5, 510, 15);

        getContentPane().add(Wizard,grid_constraints);
        ShowWizard.setText("Show Wizard");
        ShowWizard.setMaximumSize(new java.awt.Dimension(32767, 25));
        ShowWizard.setMinimumSize(new java.awt.Dimension(590, 25));
        ShowWizard.setOpaque(false);
        ShowWizard.setPreferredSize(new java.awt.Dimension(590, 25));
        ShowWizard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowWizardActionPerformed(evt);
            }
        });

        pack(); 
        
    	setTitle("OWLS-MX:   Hybrid OWL-S Service Matchmaker   v"+ GUIState.version);
    	//creation of the menubar
    	//GridBagConstraints 
    	grid_constraints = new GridBagConstraints();
    	grid_constraints.fill = GridBagConstraints.HORIZONTAL;
    	grid_constraints.gridx = 0;
    	grid_constraints.gridy = 0;
    	
    	menuBar = new JMenuBar();
        
        menuFile = new JMenu("File");
        //menuFile.add(new JMenuItem("Open Testcollection") );
        file_item_settings = new JMenuItem("Settings");
        file_item_settings.addActionListener(this);
        file_item_settings.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.ALT_MASK));  
        menuFile.add( file_item_settings );
        file_item_quit = new JMenuItem("Quit");
        file_item_quit.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));                
        file_item_quit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	GUIState.save();
            	dispose();
            }
        });
        menuFile.add(file_item_quit );
        menuFile.getAccessibleContext().setAccessibleDescription("File operations, settings and quit");
        
        
        menuBar.add(menuFile);
        
        menuHelp = new JMenu("Help");
        menuHelp.getAccessibleContext().setAccessibleDescription("Help");
        menuHelp.add(new JMenuItem("Test collection") );
        menuHelp.add(new JMenuItem("Matchmaker") );
        menuHelp.add(new JMenuItem("Settings") );  
        if (helpIsActivated)
        	menuBar.add(menuHelp);        
        
        item_about = new JMenuItem("About");
        item_about.addActionListener(this);
        menuAbout = new JMenu("About");
        menuAbout.add(item_about);       
        menuBar.add(menuAbout);   
        
        menuWizard = new JMenu("Wizard");
        wizard_item_show = new JMenuItem("Show Wizard");
        wizard_item_show.addActionListener(this);
        wizard_item_show.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_W, ActionEvent.ALT_MASK));
        menuWizard.add(wizard_item_show);
        if (wizardIsActivated)
        	menuBar.add(menuWizard);
        
        add(menuBar,grid_constraints);
                
        if (wizardIsActivated)
        	Tabs.addTab("Tasks", gui_intro);
        Tabs.addTab("Services", gui_services);
        Tabs.addTab("Requests", gui_queries);
        Tabs.addTab("Relevance set", gui_as);
        Tabs.addTab("Test collection", gui_tc);
        Tabs.addTab("Matchmaker", gui_mm);
        Tabs.addTab("Testing", gui_td);
        Tabs.addTab("Answer set",gui_rr);
        Tabs.addTab("Result", gui_rv);        
        //Tabs.addTab("Settings", gui_settings);
        //Tabs.addTab("About", gui_about);
        taskIndex=Tabs.indexOfTab("Task");
        this.Wizard.setVisible(false);   
       TestCollection.getInstance().registerUpdateDataListener(this);
        this.pack();
    }

    private void ShowWizardActionPerformed(java.awt.event.ActionEvent evt) {
        showWizard();
    }

    private void ExitActionPerformed(java.awt.event.ActionEvent evt) {
         hideWizard();
    }
    
    private boolean containsComponent(JTabbedPane tabs, Component component) {
    	Component[] comps = tabs.getComponents();
    	for (int i = 0; i < comps.length; i++) {
    		if (comps[i].equals(component))
    			return true;
    	}
    	return false;
    }
    
    /**
     * Select a certain tab
     * 
     * @param tab Tab to be selected (see TabState for details)
     */
    public void setCurrentTab(int tab) {
        switch(tab) {
        	case TabState.TASK:        		
        		if (containsComponent(Tabs,gui_intro))
        			Tabs.setSelectedComponent(gui_intro);
        		break;
        	case TabState.SERVICES:        		
        		Tabs.setSelectedComponent(gui_services);
        		break;
        	case TabState.QUERIES:        		
        		Tabs.setSelectedComponent(gui_queries);
        		break;
        	case TabState.ANSWERSET:        		
        		Tabs.setSelectedComponent(gui_as);
        		break;
        	case TabState.TESTCOLLECTION:        		
        		Tabs.setSelectedComponent(gui_tc);
        		break;
        	case TabState.MATCHMAKER:        		
        		Tabs.setSelectedComponent(gui_mm);
        		break;
        	case TabState.EVALUATION:        		
        		Tabs.setSelectedComponent(gui_td);
        		break;
        	case TabState.RESULT:        		
        		Tabs.setSelectedComponent(gui_rr);
        		break;
        	case TabState.PERFORMANCE:        		
        		Tabs.setSelectedComponent(gui_rv);
        		break;
        	default:
        		GUIState.displayWarning(this.getClass().toString(), " Tried to set unknown tab " + tab);
        }
    }

    public int getCurrentTab() {
        if (Tabs.getSelectedComponent().equals(gui_intro))
            return TabState.TASK;
        if (Tabs.getSelectedComponent().equals(gui_services))
            return TabState.SERVICES;
        if (Tabs.getSelectedComponent().equals(gui_queries))
            return TabState.QUERIES;
        if (Tabs.getSelectedComponent().equals(gui_as))
            return TabState.ANSWERSET;
        if (Tabs.getSelectedComponent().equals(gui_tc))
            return TabState.TESTCOLLECTION;
        if (Tabs.getSelectedComponent().equals(gui_mm))
            return TabState.MATCHMAKER;
        if (Tabs.getSelectedComponent().equals(gui_td))
            return TabState.EVALUATION;
        if (Tabs.getSelectedComponent().equals(gui_rr))
            return TabState.RESULT;
        if (Tabs.getSelectedComponent().equals(gui_rv))
            return TabState.PERFORMANCE;
        /* 
        if (Tabs.getSelectedComponent().equals(gui_settings))
            return TabState.SETTINGS;
        if (Tabs.getSelectedComponent().equals(gui_intro))
            return TabState.ABOUT;
         */
        return TabState.UNKNOWN;
    }
    

    
    public void hideWizard(){ 
    	if (wizardIsActivated)
    		return;
    	wizard_item_show.setText("Show Wizard");
    	if (Tabs.getSelectedComponent().equals(gui_intro))   {
    		taskIndex = Tabs.getSelectedIndex();   		
    	}
    	int currentIndex = Tabs.getSelectedIndex();
    	taskIndex = Tabs.indexOfComponent(gui_intro);
    	Tabs.remove(gui_intro);    	
        if (currentIndex>0)
        	Tabs.setSelectedIndex(currentIndex-1);
        gui_intro.setVisible(false);
        Wizard.setVisible(false);  
        pack();
    }
    
    public void disableWizard(boolean makeDisabled){
    	if(wizardIsActivated)
    		return;
    	if (makeDisabled)
    		hideWizard();
    	else 
    		showWizard();
    	
    }
    
    public void showWizard(){
    	if ( wizardIsActivated)
    		return;
		wizard_item_show.setText("Hide Wizard");
        Wizard.setVisible(true); 
        gui_intro.setVisible(true);        
        if (Tabs.indexOfComponent(gui_intro)<0) {
            Tabs.insertTab("Task", null, gui_intro, null, taskIndex);           
        	}
        pack();
//        updateWizard();
    }
    
    /**
     * If the Wizard is visible
     * 
     * @return boolean if the Wizard is currently visible
     */
    public boolean wizardIsVisible() {
        return Wizard.isVisible();
    }
    
    /**
     * MAIN FUNCTION: Launches OWLS-MX GUI without the splashscreen
     * 
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BaseWindow().setVisible(true);
            }
        });

    }
    
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(item_about))		{
			java.awt.EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                new ShowPanelFrame(new AboutDialog(),false).setVisible(true);
	            }
	        });
		}
		
		else if (event.getSource().equals(file_item_settings)) {
			java.awt.EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                new ShowPanelFrame(gui_settings,false).setVisible(true);
	            }
	        });
		}
		
		else if (event.getSource().equals(wizard_item_show)) {			
			if(wizardIsVisible()){				
				disableWizard(true);
			}
			else {	
				disableWizard(false);
			}	
		}		
		
	
	}
	
	/**
	 * Updates the wizard with the current step 
	 */
	private void updateWizard() {
//		try {
//			TaskContent task = GUIState.getInstance().getCurrentStepContent();
//			Step.setText(task.taskTitle + " " +
//					task.step + "/" + task.totalSteps + " " + task.screenTitle);
//			Helptext.setText(task.screenText);
//			Next.setEnabled(GUIState.getInstance().hasNextStep());
//			Last.setEnabled(GUIState.getInstance().hasLastStep());
//			this.setCurrentTab(task.screen);
//		}
//		catch(Exception e) {
//			GUIState.displayWarning(getClass().toString() + ": " + e.getClass().toString(), "updateWizard()\n" + e.getMessage());
//		}
	}

	public void updateData() {
        gui_intro.updateData();
        gui_services.updateData();
        gui_queries.updateData();
        gui_as.updateData();
        gui_tc.updateData();
        gui_mm.updateData();
        gui_td.updateData();
        gui_rr.updateData();
        gui_rv.updateData();		
        updateWizard();
	}
    
}
