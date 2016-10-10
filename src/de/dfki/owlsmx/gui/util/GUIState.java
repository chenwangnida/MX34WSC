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
package de.dfki.owlsmx.gui.util;


import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import de.dfki.owlsmx.gui.data.TaskContent;
import de.dfki.owlsmx.gui.data.TestCollection;



public class GUIState implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static GUIState instance = new GUIState();
	private String pathToServices = "";
	private String pathToQueries = "";
	private String pathToTestCollection = "";
	private double treshold=0.8;
	private int minDegree=4;
	private short simMeasure=1;
	private boolean displayAnswerset = true;
	private boolean getAQR = false;
	private boolean getMemoryConsumption = false;
	private boolean getPrecisionRecall = false;
	private Map wizardContent = new HashMap();
	private int currentTask = NO_TASK; 
	private int currentStep = 1;	
	private boolean addImmediately = false;
	private String replaceWith = "";
	private String replaceFrom = "";
	private boolean useReplaceWith = false;
	public static boolean debug = false;	
	public static final String logoPath = "/images/owlsMX1_small_rectangle.jpg"; 
	public static final String version = "2.0";
	
	private boolean owlsmxp = false;
	private boolean integrative = false;
	
	//Sorting
	public final static int SORT_HYBRID = 0;
	public final static int SORT_SEMANTIC = 1;
	public final static int SORT_SYNTACTIC = 2;	
	//Tasks
	public final static int NO_TASK = -1;	
	public final static int QUERY_TASK = 0;
	public final static int CREATE_TC_TASK = 1;
	public final static int RUN_TEST_TASK = 2;
	public final static int OTHER_TASK = 4;
	

	private int sorting = SORT_HYBRID;
	
	public static GUIState getInstance(){
		if (instance!=null)
			return instance;
		else return new GUIState();
	}
    
	private GUIState() {
		loadWizardContent();
	}
	
	public int setCurrentStep(){
		return currentStep;
	}
	
	public void setCurrentStep(int step) {
		this.currentStep = step;
	}
	
	public int getCurrentTask(){
		return currentTask;
	}
	
	public void setCurrentTask(int task) {
		this.currentTask = task;
	}
	
	public boolean getDisplayAnswerSet() {
		return displayAnswerset;
	}
	
	public void setDisplayAnswerSet(boolean state) {
		displayAnswerset = state;
	}
	
	public boolean getDisplayAQR() {
		return getAQR;
	}
	
	public void setDisplayAQR(boolean state) {
		getAQR = state;
	}
	
	public boolean getMemoryConsumption() {
		return getMemoryConsumption;
	}
	
	public void setMemoryConsumption(boolean state) {
		getMemoryConsumption = state;
	}

	public boolean getPrecisionRecall() {
		return getPrecisionRecall;
	}
	
	public void setPrecisionRecall(boolean state) {
		getPrecisionRecall = state;
	}

	public void setMeasures(int minDegree, short simMeasure, double treshold) {
		this.minDegree = minDegree;
//		System.err.println("MinDOM:               " + this.minDegree);
		this.simMeasure = simMeasure;
//		System.err.println("SimilarityMeasure:    " + this.simMeasure);
		this.treshold = treshold;
//		System.err.println("Similiarity treshold: " + this.treshold);
	}
	
	public double getTreshold() {
		return treshold;
	}
	
	public int getMinDegree() {
		return minDegree;
	}
	
	public short getSimilarityMeasure() {
		return simMeasure;
	}
	
	public void setPathToServices(String pathToServices) {
		this.pathToServices = pathToServices;
	}

	public String getPathToServices() {
		return pathToServices;
	}

	public void setPathToQueries(String pathToQueries) {
		this.pathToQueries = pathToQueries;
	}

	public String getPathToQueries() {
		return pathToQueries;
	}

	public void setPathToTestCollection(String pathToTestCollection) {
		this.pathToTestCollection = pathToTestCollection;
	}

	public String getPathToTestCollection() {
		return pathToTestCollection;
	}
	
	public static void load(String path) {		
		try {
			ObjectInputStream s = new ObjectInputStream(new FileInputStream(path));
			GUIState state = (GUIState)s.readObject();
		} catch (FileNotFoundException e) {
			GUIState.displayWarning("TestCollection: " +e.getClass().toString(), e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			GUIState.displayWarning("TestCollection: " +e.getClass().toString(), e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			GUIState.displayWarning("TestCollection: " +e.getClass().toString(), e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void save(String path){
		try {
			ObjectOutputStream s = new ObjectOutputStream(new FileOutputStream(path));		
			s.writeObject(GUIState.getInstance());
			s.flush();
		} catch (Exception e) {
			GUIState.displayWarning("GUIState" + "|save:", e.getClass().toString() + "\n" + "Couldn't save file " + path + "\n" + e.getMessage());
			e.printStackTrace();
		}	
	}
	
	public static void load() {
		load("guistate.conf");
	}
	
	public static void save() {
		save("guistate.conf");
	}

	/**
	 * Test for the TestCollection class, including saving and loading
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws URISyntaxException
	 */
	public static void main(String args[]) throws FileNotFoundException, IOException, ClassNotFoundException, URISyntaxException {		
		generateDummyData();		
	}
	
	/**
	 * Displays a warning message for debug purposes, iff debuging is enabled
	 * 
	 * @param headline
	 * @param message
	 */
	public static void displayWarning(String headline, String message){
			JOptionPane.showMessageDialog(null, message, headline, JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Displays a warning message for debug purposes, iff debuging is enabled
	 * 
	 * @param headline
	 * @param message
	 */
	public static void displayWarning(Component comp, String headline, String message){		
			JOptionPane.showMessageDialog(comp, message, headline, JOptionPane.WARNING_MESSAGE);			
	}
	
	
	
	public static void generateDummyData() {
		try {
			System.out.println("Loading dummy data to TestCollection:");		
			URI query1 = new URI("http://127.0.0.1/queries/1.1/1personbicyclecar_price_service.owls");
			URI query2 = new URI("http://127.0.0.1/queries/1.1/book_price_service.owls");
			TestCollection.getInstance().addQuery(query1);
			System.out.println("--------------------------------------");	
			TestCollection.getInstance().addQuery(query2);
			TestCollection.getInstance().addService(new URI("http://127.0.0.1/services/1.1/_3WheeledAudiCarprice_service.owls"));
			TestCollection.getInstance().addService(new URI("http://127.0.0.1/services/1.1/__destination_MyOfficeservice.owls"));
			TestCollection.getInstance().addService(new URI("http://127.0.0.1/services/1.1/__luxuryhotel_Heidelburgservice.owls"));
			TestCollection.getInstance().addService(new URI("http://127.0.0.1/services/1.1/_aps-slrpricereport_Musuemservice.owls"));
			TestCollection.getInstance().addService(new URI("http://127.0.0.1/services/1.1/_author_CompJservice.owls"));
			TestCollection.getInstance().addService(new URI("http://127.0.0.1/services/1.1/_author_DMservice.owls"));		
			TestCollection.getInstance().addServiceToAnswerset(query1,new URI("http://127.0.0.1/services/1.1/_3WheeledAudiCarprice_service.owls"));
			TestCollection.getInstance().addServiceToAnswerset(query1,new URI("http://127.0.0.1/services/1.1/__destination_MyOfficeservice.owls"));
			TestCollection.getInstance().addServiceToAnswerset(query1,new URI("http://127.0.0.1/services/1.1/__luxuryhotel_Heidelburgservice.owls"));
			TestCollection.getInstance().addServiceToAnswerset(query2,new URI("http://127.0.0.1/services/1.1/_aps-slrpricereport_Musuemservice.owls"));
			TestCollection.getInstance().addServiceToAnswerset(query2,new URI("http://127.0.0.1/services/1.1/_author_CompJservice.owls"));
			TestCollection.getInstance().addServiceToAnswerset(query2,new URI("http://127.0.0.1/services/1.1/_author_DMservice.owls"));
			//TestCollection.getInstance().removeService(new URI("http://www.develin.de/service1"));
			System.out.println("Before:");
			System.out.println("Services     : " + TestCollection.getInstance().getServices().size());
			System.out.println("Requests      : " + TestCollection.getInstance().getQueries().size());
			System.out.println("Answerset 1  : " + TestCollection.getInstance().getAnswerset(query1).size());
			System.out.println("Answerset 2  : " + TestCollection.getInstance().getAnswerset(query2).size());		
			System.out.println("--------------------------------------");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public TaskContent getStepContent(int task, int step) throws Exception {    	
    	TaskContent[] content =(TaskContent[])wizardContent.get(new Integer(task));
    	if (content==null) { 
    		//System.out.println("No content loaded for wizard");
    		throw new Exception("No content loaded for wizard");    		
    	}
    		
    	if (content[step-1]==null) { 
    		//System.out.println("No content for step " + (step-1) + ", is there is content for step+1 " + (content[step]==null));
    		throw new Exception("No content for step " + (step-1) + ", is there is content for  step+1 " + (content[step]==null));    		
    	}
    		
    	if (  ( step > content.length) || ( (step-1) < 0) ) { 
    		//System.out.println("Wizard step " + step + " of task " + task + " does not exist.\n");
    		throw new Exception("Wizard step " + step + " of task " + task + " does not exist.\n");    		
    	}
    	 
    		// Step(" + (content[step]==null) + ")"
    		// + "Content(" + (wizardContent.size())  + ") Task(" + (content!=null) + ")")
    	return content[step-1];
    }
    public TaskContent getCurrentStepContent() throws Exception {
    	return getStepContent(currentTask,currentStep);
    }
    
    private void processLine(String line, Map wizardContent) {
    	TaskContent contentItem = new TaskContent(line);
    	TaskContent[] tasks;
    	if (wizardContent.containsKey(contentItem.task))
    		tasks = (TaskContent[])wizardContent.get(contentItem.task);
    	else
    		tasks = new TaskContent[contentItem.totalSteps];
    	if ( ((contentItem.step-1)<tasks.length) &&
    			((contentItem.step-1)>=0) )
    		tasks[contentItem.step-1] = contentItem;
    	else
    		GUIState.displayWarning(this.getClass().toString()+"|processLine", "Current step does not fit in range: 0 > " + (contentItem.step-1) + " >" + tasks.length);
    	wizardContent.put(contentItem.task,tasks);    
    }
    
    public boolean hasNextStep() {
    	try {
    		TaskContent[] taskContent =(TaskContent[])wizardContent.get(new Integer(currentTask));
	    	TaskContent content = getStepContent(currentTask,currentStep);
			return ( (currentStep<taskContent.length) && (currentStep < content.totalSteps) && (taskContent[currentStep]!=null) );
	    } catch (Exception e) {
			GUIState.displayWarning(getClass().toString() + "|setNextStep:",e.getClass().toString()+ "\n" + e.getMessage());
			e.printStackTrace();
			return false;
		}
    }
    
    public void setNextStep() {
    	try {
			TaskContent content = getStepContent(currentTask,currentStep);
			if (hasNextStep()) {
				currentStep++;
			}
			else
				GUIState.displayWarning(getClass().toString() + "|setNextStep:", "No next step: " + currentStep + "<" + content.totalSteps);
			TestCollection.getInstance().updateData();
		} catch (Exception e) {
			GUIState.displayWarning(getClass().toString() + "|setNextStep:",e.getClass().toString()+ "\n" + e.getMessage());
			e.printStackTrace();
		}
    }
    
    public boolean hasLastStep() {
    	TaskContent[] taskContent =(TaskContent[])wizardContent.get(new Integer(currentTask));
    	return ( (currentStep > 1) && (taskContent[currentStep-2]!=null) );
    }
    
    public void setLastStep() {
    	try {
    		if (hasLastStep())
    			currentStep--;
    		else
    			GUIState.displayWarning(getClass().toString() + "|setLastStep:","No last step current step is " + currentStep);
    		TestCollection.getInstance().updateData();
			
		} catch (Exception e) {
			GUIState.displayWarning(getClass().toString() + "|setNextStep:",e.getClass().toString()+ "\n" + e.getMessage());
			e.printStackTrace();
		}
    }
    
    /**
     * Loads the texts and steps for the Wizard from the included data/wizard.dat
     * 
     */
    private void loadWizardContent() {
    	try {
    		Map wizardContent = new HashMap();
    		String line;
			BufferedReader reader = new BufferedReader(new FileReader(new File(getClass().getResource("/data/wizard.dat").getFile())));			
			while( (line = reader.readLine()) != null) {
				processLine(line,wizardContent);
			}			
			this.wizardContent = wizardContent;
			setCurrentTask(GUIState.NO_TASK);
			setCurrentStep(1);
		} catch (FileNotFoundException e) {
			//GUIState.displayWarning(this.getClass().toString() + ": " + e.getClass().toString(), e.getMessage());
//			owlsmx.io.ErrorLog.instanceOf().report(this.getClass().toString() + e.getMessage());
		} catch (IOException e) {
			de.dfki.owlsmx.io.ErrorLog.instanceOf().report(this.getClass().toString() + e.getMessage());
//			e.printStackTrace();
		} catch(Exception e) {
			de.dfki.owlsmx.io.ErrorLog.instanceOf().report(this.getClass().toString() + e.getMessage());
		}
    }

	/**
	 * @return Returns if services should be added immediately.
	 */
	public boolean isAddImmediately() {
		return addImmediately;
	}

	/**
	 * @param addImmediately If services should be added immediately.
	 */
	public void setAddImmediately(boolean addImmediately) {
		this.addImmediately = addImmediately;
	}

	/**
	 * @return Returns the String to be replaced in the URIs.
	 */
	public String getReplaceFrom() {
		return replaceFrom;
	}

	/**
	 * @param replaceFrom The String to be replaced in the URIs.
	 * @param replaceWith String which will be used instead
	 */
	public void setReplaceFrom(String replaceFrom,String replaceWith) {
		if (!useReplaceWith)
			return;
		this.replaceFrom = replaceFrom;
		this.replaceWith = replaceWith;
	}

	/**
	 * @return Returns String which will be used instead.
	 */
	public String getReplaceWith() {
		return replaceWith;
	}

	/**
	 * @return Returns if URI substrings should be replaced
	 */
	public boolean isUseReplaceWith() {
		return ( (replaceFrom!=null) && (replaceWith!=null)  && useReplaceWith && (!replaceFrom.equals("")) && (!replaceWith.equals("")) ) ;
	}

	/**
	 * @param useReplaceWith if URI substrings should be replaced
	 */
	public void setUseReplaceWith(boolean useReplaceWith) {
		this.useReplaceWith = useReplaceWith;
	}
	
	public URI replaceString(URI uri) {		
		//System.out.println("Replacing in URI " + uri.toString());
		if (!isUseReplaceWith()) {
			return uri;
		}
		try {
			URI testURI;
			testURI = new URI (uri.toString().replaceAll(GUIState.getInstance().getReplaceFrom(), 
					GUIState.getInstance().getReplaceWith()) );
			return testURI;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return uri;
		}			
	}
	
	public void setSorting(int sort) {
		this.sorting = sort;
	}
	
	public int getSorting() {
		return sorting;
	}
	
	public void setOWLSMXP(boolean value) {
		owlsmxp = value;
	}
	
	public boolean getOWLSMXP() {
		return owlsmxp;
	}

	/**
	 * @return the integrative
	 */
	public boolean isIntegrative() {
		return integrative;
	}

	/**
	 * @param integrative the integrative to set
	 */
	public void setIntegrative(boolean integrative) {
		this.integrative = integrative;
	}

}
