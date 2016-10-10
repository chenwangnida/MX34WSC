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
 * 
 */
package de.dfki.owlsmx.gui.data;



import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
//import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.DefaultListModel;
import de.dfki.owlsmx.gui.util.GUIState;
import de.dfki.owlsmx.gui.util.MatchmakerInterface;
import de.dfki.owlsmx.gui.util.UpdateDataListener;
import de.dfki.owlsmx.gui.util.UpdateDataListenerRegistry;



/**
 * Testcollection is a singleton that stores services, queries and the answersets for the queries.
 * In addition it connects the GUI to the OWLS-MX Matchmaker and stores the datamodels for
 * services and queries. 
 * 
 * @author Ben
 *
 */
public class TestCollection implements java.io.Serializable, UpdateDataListenerRegistry, UpdateDataListener{

	private static final long serialVersionUID = -6345043491596293272L;
	private Map services = new HashMap();
	private Map queries = new HashMap();
	private Map answerset = new HashMap();
	private Map matchmakerAnswerset = new HashMap();
	private Map result = new HashMap();
	private String name = "";
	private String version = "";
	private String comment = "";
    private DefaultListModel serviceDataModel = new DefaultListModel();
    private DefaultListModel queryDataModel = new DefaultListModel();
    private DefaultListModel answersDataModel = new DefaultListModel();
    private Query activeQuery;
    private Set registry = new HashSet();
    private static TestCollection instance = new TestCollection();    
    
    /**
     * Empty Constructor
     */
    private TestCollection() {		 
    }
   
    /**
     * @return single instance of the TestCollection
     */
    public static TestCollection getInstance() {
    	return instance;
    }
	
	/**
	 * Clear the testcollection, resets all variables
	 */
	public void clearTC() {
		services = new HashMap();
		queries = new HashMap();
		answerset = new HashMap();
		matchmakerAnswerset = new HashMap();
		result = new HashMap();
		name = "";
		version = "";
		comment = "";
	    serviceDataModel = new DefaultListModel();
	    queryDataModel = new DefaultListModel();
	    answersDataModel = new DefaultListModel();
	    updateData();
	}
    
	/**
	 * Adds the services with the given URI
	 * Take care as the services will be added with the URI stored IN the service
	 * (to avoid unecessary duplicates if the same service is added from different
	 * locations)	 * 
	 * 
	 * @param uri	URI of the service to be added
	 * @return		the loaded Service
	 * @throws FileNotFoundException	thrown if the service can't be loaded from the given URI
	 */
	public ServiceItem addService(URI uri) throws FileNotFoundException {
		uri = GUIState.getInstance().replaceString(uri);		
		if (services.containsKey(uri)) {
			de.dfki.owlsmx.io.ErrorLog.instanceOf().report(
        			"Service " + uri + " is already in the set of registered services");
			return (ServiceItem)services.get(uri);
		}
		ServiceItem item = new ServiceItem(uri);
		uri = item.getURI();
		if (services.containsKey(uri)) {
			de.dfki.owlsmx.io.ErrorLog.instanceOf().report(
        			"Service with real URI " + uri + " is already in the set of registered services");
			return (ServiceItem)services.get(uri);
		}
		services.put(uri, item);
		serviceDataModel.addElement(item);
		return item;
	}
	
	/**
	 * Removes the service with the given URI from the test collection
	 * (If no services is stored with the given URI it will try to load
	 * the service from this URI and remove that one)
	 * 
	 * @param uri		uri of the service that should be removed
	 * @param update	if the underlying data models should be updated
	 */
	private void removeService(URI uri, boolean update){		
		if (!services.containsKey(uri)) {
			ServiceItem item = new ServiceItem(uri);
			uri = item.getURI();
		}
		services.remove(uri);		
		Iterator iter = answerset.keySet().iterator();
		while(iter.hasNext()) {
			removeServiceFromAnswerset((URI) iter.next(), uri);
		}
		if (update)
			updateDataModels();
		if (activeQuery!=null)
			updateAnswerset(activeQuery);
	}
	
	
	/**
	 * Removes the service with the given URI from the test collection
	 * (If no services is stored with the given URI it will try to load
	 * the service from this URI and remove that one)
	 * This function will also trigger an update the underlying data models
	 * 
	 * @param uri		uri of the service that should be removed
	 */
	public void removeService(URI uri) {
		removeService(uri, true);
	}
	
	/**
	 * Removes multiple services from the test collection
	 * 
	 * @param services Vector of ServiceItems that should be removed
	 */
	public void removeServices(Object[] services) {
		for (int i = 0; i<services.length;i++) {
			removeService(((ServiceItem)services[i]).getURI(),false);			
		}
		updateDataModels();
		if (activeQuery!=null)
			updateAnswerset(activeQuery);
	}
	
	/**
	 * Removes multiple services from the test collection
	 * 
	 * @param services Set of ServiceItems that should be removed
	 */
	public void removeServices(Set services) {
		removeServices(services.toArray());
	}
	
	/**
	 * returns the service of the given URI
	 * (If no services is stored with the given URI it will try to load
	 * the service from this URI and remove that one)
	 * 
	 * @param uri	URI of the service
	 * @return		ServiceItem of the service
	 */
	public ServiceItem getService(URI uri) {		
		if (!services.containsKey(uri)) {
			ServiceItem item = new ServiceItem(uri);
			uri = item.getURI();
		}
		return (ServiceItem)services.get(uri);
	}
	
	/**
	 * Returns all services in the test collection
	 * 
	 * @return Set of Services
	 */
	public Set getServices() {
		return new HashSet(services.values());
	}
	
	
	/**
	 * Adds the query with the given URI
	 * Take care as the query will be added with the URI stored IN the query
	 * (to avoid unecessary duplicates if the same service is added from different
	 * locations)
	 * 
	 * @param uri	URI of the query to be added
	 * @return		the loaded Query
	 * @throws FileNotFoundException	thrown if the query can't be loaded from the given URI
	 */
	public Query addQuery(URI uri) {
		try {
			uri = GUIState.getInstance().replaceString(uri);			
			if (queries.containsKey(uri))
				return (Query)queries.get(uri);
			Query item = new Query(uri);
			if (queries.containsKey(item.getURI()))
				return (Query)queries.get(uri);
			queries.put(item.getURI(), item);
			queryDataModel.addElement(item);
			answerset.put(item.getURI(),new TreeSet());
			matchmakerAnswerset.put(item.getURI(),new TreeSet());		
			return item;
		}
		catch(Exception e) {
			e.printStackTrace();
			return new Query(null);
		}
	}
	
	/**
	 * Removes the query with the given URI from the test collection
	 * (If no services is stored with the given URI it will try to load
	 * the service from this URI and remove that one)
	 * 
	 * @param uri		uri of the query that should be removed
	 * @param update	if the underlying data models should be updated
	 */
	public void removeQuery(URI uri, boolean update) {
		answerset.remove(uri);
		matchmakerAnswerset.remove(uri);		
		if (!queries.containsKey(uri)) {
			Query item = new Query(uri);
			uri = item.getURI();
		}		
		queries.remove(uri);
		answerset.remove(uri);
		matchmakerAnswerset.remove(uri);
		if (update)
			updateDataModels();
		if( (activeQuery!=null) && (activeQuery.getURI().equals(uri)) )
			updateAnswerset(activeQuery);
	}
	
	/**
	 * Removes the query with the given URI from the test collection
	 * (If no services is stored with the given URI it will try to load
	 * the service from this URI and remove that one)
	 * This function will also trigger an update the underlying data models
	 * 
	 * @param uri		uri of the query that should be removed
	 */
	public void removeQuery(URI uri) {		
		removeQuery(uri,true);
	}
	
	/**
	 * Removes multiple queries from the test collection
	 * 
	 * @param queries Vector of queries that should be removed
	 */
	public void removeQueries(Object[] queries) {
		for (int i = 0; i<queries.length;i++) {
			removeQuery(((Query)queries[i]).getURI(),false);			
		}
		updateDataModels();
		if (activeQuery!=null)
			updateAnswerset(activeQuery);
	}
	
	
	/**
	 * Removes multiple queries from the test collection
	 * 
	 * @param queries Set of queries that should be removed
	 */
	public void removeQueries(Set queries) {
		removeQueries(queries.toArray());
	}
	
	public SortedSet getQueries() {
		return new TreeSet(queries.values());
	}
	
	/**
	 * Adds a service to the answerset of a given query
	 * Assumes that both service and query where already added to the collection
	 * Assumes that the URI equals the URI specified in the service or query, 
	 * but loads the query or service if not.
	 * 
	 * @param query		URI of the query to whose answerset the service should be added
	 * @param service	URI of the service to be added to the answerset
	 */
	public void addServiceToAnswerset(URI query, URI service) {
		try {
		URI localQuery = GUIState.getInstance().replaceString(query);
		URI localService = GUIState.getInstance().replaceString(service);
		if (!services.containsKey(localService)) {
			ServiceItem item = new ServiceItem(localService);
			GUIState.displayWarning(this.getClass().toString(), "Tried "+ item.getURI().toString() + 
					"\n instead of " + localService.toString());
			if (services.containsKey(item.getURI())) {				
				localService = item.getURI();			
			}
		}
		
		if (!queries.containsKey(localQuery)){
			Query queryItem = new Query(localQuery);
			GUIState.displayWarning(this.getClass().toString(), "Tried "+ queryItem.getURI().toString() + 
					"\n instead of " + localQuery.toString());
			if (queries.containsKey(queryItem.getURI()))
				localQuery = queryItem.getURI();
		}
			
		if ( (!queries.containsKey(localQuery))  || (!services.containsKey(localService)) 
											|| (!answerset.containsKey(localQuery)) ){			
			GUIState.displayWarning(this.getClass().toString() +"-addServiceToAnswerset:", 
    	    		"Didn't add " + localService.toString() + "\n to Answerset of query " + 
    	    		localQuery.toString() + "\n" + "(Query " + (queries.containsKey(localQuery)) + ")" +    	    		
    	    		"(Service " + (services.containsKey(localQuery)) + ")" +
    	    		"(AnswerQuery " + (answerset.containsKey(localQuery)) + ")"	);	  
			return;
		} 
		
		if (((TreeSet)answerset.get(query)).contains(service))
			return;
		((TreeSet)answerset.get(query)).add(service);
		if( (activeQuery!=null) && (activeQuery.getURI().equals(query)) )
			updateAnswerset(activeQuery);
		}
		catch(Exception e) {
			GUIState.displayWarning(this.getClass().toString() + ": ", e.getMessage());
		}
	}
	
	public void addServicesToAnswerset(URI query, Object[] services) {	
	    	for (int i=0;i<services.length;i++) {
	    		try{
	    			addServiceToAnswerset(query,((ServiceItem)services[i]).getURI());
	    		}
	    	    catch(Exception e) {
	    	    	GUIState.displayWarning(this.getClass().toString() +": " + e.getMessage(), 
	    	    		"Didn't add " + services[i].toString() + " to Answerset of query " + 
	    	    		query.toString());	    	    	
	    	    }
	    	}	    	
	}
	
	public void addServicesToAnswerset(URI query, Set services) {	
		addServicesToAnswerset( query,services.toArray());
	}
	
	public void removeServiceFromAnswerset(URI query, URI service) {
		if ( (!queries.containsKey(query)) || (!answerset.containsKey(query)))
			return;				
		((TreeSet)answerset.get(query)).remove(service);
		if( (activeQuery!=null) && (activeQuery.getURI().equals(query)) )
			updateAnswerset(activeQuery);
	}
	
	public void removeServicesFromAnswerset(URI query, Object[] services) {	
    	for (int i=0;i<services.length;i++) {
    		try{
    			removeServiceFromAnswerset(query,((ServiceItem)services[i]).getURI());
    		}
    	    catch(Exception e) {
    	    	GUIState.displayWarning(this.getClass().toString() +": " + e.getMessage(), "Didn't add " + services[i].toString() + " to Answerset of query " + query.toString());
    	    }
    	}	    	
	}

	public void removeServicesFromAnswerset(URI query, Set services) {	
		removeServicesFromAnswerset( query,services.toArray());
	}
	
	/**
	 * Returns the sorted answerset for a given query, if the query has not yet been executed or
	 * if the query does not exist (e.g. has not been added) an empty Set will be returned.
	 * ATTENTION this returns the previously STORED answerset, not the one calculated by the matchmaker
	 * 
	 * @param query	URI of the query
	 * @return	SoretedSet of ServiceItems that are the answerset of the query
	 */
	public SortedSet getAnswerset(URI query) {
		URI localQuery = query;
		if (!answerset.containsKey(query)) {
			Query item = new Query(localQuery);
			if (answerset.containsKey(item.getURI()))
				localQuery = item.getURI();
			else
				return new TreeSet();
		}
		return (TreeSet)answerset.get(localQuery);
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
    public DefaultListModel getServiceDataModel() {
    	return serviceDataModel;
    }
    
    public DefaultListModel getQueryDataModel() {
    	return queryDataModel;
    }
    
    public DefaultListModel getAnswerSetDataModel() {
    	return answersDataModel;
    }
    
	private void updateDataModels() {
		//this.answerList = new JList(TestCollection.getInstance().getServices().toArray());
		serviceDataModel.removeAllElements();
		SortedSet sortedServices = new TreeSet();
		sortedServices.addAll(services.values());
		Iterator iter = sortedServices.iterator();		
		while(iter.hasNext()) {			
			serviceDataModel.addElement( ((ServiceItem)iter.next()));
		}
		
		SortedSet sortedQueries = new TreeSet();
		sortedQueries.addAll(queries.values());
		queryDataModel.removeAllElements();
		iter = sortedQueries.iterator();		
		while(iter.hasNext()) {			
			queryDataModel.addElement( ((Query)iter.next()));
		}		
		
	}
	
    public void updateAnswerset(Query query) {
    	try {
    		activeQuery = query;
        	Set set = getAnswerset(query.getURI());
        	Iterator iter = set.iterator();
        	answersDataModel.removeAllElements();        	
    		while(iter.hasNext()) {	
    			answersDataModel.addElement( TestCollection.getInstance().getService((URI)iter.next()));
    			}
        } catch(Exception e) {
        	GUIState.displayWarning(this.getClass().toString() +" | " + "updateAnswerset" + ": ",e.getClass().toString() +"\n" + "Problem computing answerset for " + query + "\n" + e.getMessage());
        	}
        }

    /**
     * Restores a testcollection from the given path
     * @param path Path to load the testcollection from
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws ClassNotFoundException 
     */
    public void load(String path) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream s = new ObjectInputStream(new FileInputStream(path));
		//instance = new TestCollection();	
		services = (HashMap)s.readObject();			
		queries  = (HashMap)s.readObject();
		answerset= (HashMap)s.readObject();
		matchmakerAnswerset =  (HashMap)s.readObject();
		name     =  (String)s.readObject();			
		version  =  (String)s.readObject();
		comment  =  (String)s.readObject();
		TestCollection.getInstance().setName(name);
		TestCollection.getInstance().setVersion(version);
		TestCollection.getInstance().setComment(comment);
		s.close();
		updateDataModels();
	}
    
    /**
     * Restores a testcollection from the given path
     * @param path Path to load the testcollection from
     * @param comp Component for which an error should be displayed
     */
    public void load(String path, Component comp) {
		try {
			load(path);
			GUIState.displayWarning(comp,"Loaded test collection " + name,
					"Loaded " + services.size() + " Services\n" + 
					"Loaded " + queries.size() + " Requests\n");
		} catch (Exception e) {
			GUIState.displayWarning(comp,"Attempted to load test collection", "Couldn't load test collection from path\n" + path + "\n" + e.getMessage());
			e.printStackTrace();
		}
//		updateDataModels();
//		updateData();
	}
	
	public void save(String path) {
		ObjectOutputStream s;
		try {
			s = new ObjectOutputStream(new FileOutputStream(path));		
			s.writeObject(services);
			s.writeObject(queries);
			s.writeObject(answerset);
			s.writeObject(matchmakerAnswerset);
			s.writeObject(name);
			s.writeObject(version);
			s.writeObject(comment);
			s.flush();
			s.close();
		} catch (Exception e) {
			GUIState.displayWarning(this.getClass().toString() + "|save:", e.getClass().toString() + "\n" + "Couldn't save file " + path + "\n" + e.getMessage());
			e.printStackTrace();
		}		
	}
	
    /**
     * Restores a testcollection from the given path 
     */
	public void load(){
		try {
			load("testcollection.dat");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void save() {
		save("testcollection.dat");
	}
	
//	private SortedSet createDummyData(Query query){
//		Random rand = new Random();
//		SortedSet set = new TreeSet();
//		Iterator iter = services.entrySet().iterator();
//		ServiceItem service;
//		HybridServiceItem item;
//		while(iter.hasNext()) {
//			service = (ServiceItem)((Map.Entry)iter.next()).getValue();
//			item = new HybridServiceItem(service);	
//			item.setDegreeOfMatch(rand.nextInt(6));
//			item.setSyntacticSimilarity( ((double)rand.nextInt(101))/100);
//			set.add(item);
//		}
//		return set;
//	}
	
	public boolean runQuery(Query query) {	
		return runQuery(query,true);
	}
	
	public boolean runQuery(Query query, boolean updateDatamodel) {
		SortedSet set = new TreeSet();
		if(GUIState.getInstance().getOWLSMXP())
			MatchmakerInterface.getInstance().setOWLSMXP(true);
		else
			MatchmakerInterface.getInstance().setOWLSMXP(false);
		if(GUIState.getInstance().isIntegrative())
			MatchmakerInterface.getInstance().setIntegrative(true);
		else
			MatchmakerInterface.getInstance().setIntegrative(false);
		set = MatchmakerInterface.getInstance().matchRequest(query.getURI(),GUIState.getInstance().getMinDegree(), GUIState.getInstance().getTreshold());		
		result.put(query,set);
		if (updateDatamodel)
			updateData();		
		return true;
	}
	
//	public void runAllQueries(){
//		Iterator iter = queries.entrySet().iterator();
//		Map.Entry entry;
//		while (iter.hasNext()){
//			entry = (Map.Entry)iter.next();
//			runQuery((Query)entry.getValue(),false);
//		}
//		updateData();
//	}	
		
	public SortedSet getMatchmakerAnswerset(Query query, int sorting) {
		SortedSet set = new TreeSet();		
		Iterator iter = ( (TreeSet) result.get(query)).iterator();	
		if (sorting==GUIState.SORT_SEMANTIC)
			while (iter.hasNext()) {			
				set.add(new SemanticServiceItem((HybridServiceItem)iter.next()));			
			}		
		else if (sorting==GUIState.SORT_SYNTACTIC)
			while (iter.hasNext()) {			
				set.add(new SyntacticServiceItem((HybridServiceItem)iter.next()));			
			}			
		else
			while (iter.hasNext()) {			
				set.add((HybridServiceItem)iter.next());			
			}						
		return set;
	}
	
	public Map getMatchmakerAnswerset(int sorting) {
		Map map = new HashMap();		
		Iterator iter = result.entrySet().iterator();
		Query currentQuery;
		while (iter.hasNext()) {			
			currentQuery = (Query)((Map.Entry) iter.next()).getKey();
//			if (currentQuery.isProcessed())
				map.put( currentQuery, getMatchmakerAnswerset(currentQuery, sorting) );
		}
		return map;
	}

	public void registerUpdateDataListener(UpdateDataListener listener) {
		registry.add(listener);		
	}
	
	public void updateData() {
		updateDataModels();
		Iterator iter = registry.iterator();
		while (iter.hasNext()) {
			((UpdateDataListener)iter.next()).updateData();
		}
	}
	
	public void setServiceProcessed(ServiceItem service,boolean processed) {
		((ServiceItem)services.get(service.getURI())).setProcessed(processed);
	}
	

	
	public boolean checkRelevanceSets(Component parent) {
//		if ( !(MatchmakerInterface.getInstance().didRun()) )
//			return true;
		if (!GUIState.getInstance().getPrecisionRecall())
			return true;
		Query check;
		for (Iterator iter = queries.values().iterator();iter.hasNext();) {
			check = (Query) iter.next();
			if ( (getAnswerset(check.getURI()).size()<=0)) {
					GUIState.displayWarning(parent, "Empty Answerset", 
							"The relevance set of service request " + check.getName() + " is empty.\n" +
							"Please define a relevance set for recall/precision measurement.");
					return false;
			}
//			owlsmx.io.ErrorLog.instanceOf().report("Check 1: " + check.getURI().toString() + " " + (getAnswerset(check.getURI()).size()<=0) );
		}
//		Map.Entry me;
//		for (Iterator iter = answerset.entrySet().iterator();iter.hasNext();) {
//			me = (Map.Entry) iter.next();
//			if ( ( (Set) me.getValue() ).size()<=0) 
//				GUIState.displayWarning(parent, "Empty Answerset", 
//							"The relevance set of service request " + ( (URI)me.getKey()).toString() + " is empty.\n" +
//							"Please define a relevance set for recall/precision measurement.");
//			owlsmx.io.ErrorLog.instanceOf().report("Check 2: " + ( (URI)me.getKey()).toString() + " " + ( ( (Set) me.getValue() ).size()<=0)  );
//		}
		return true;
	}
	
	public boolean checkQueriesAndServicesSets(Component parent) {
//		if ( !(MatchmakerInterface.getInstance().didRun()) )
//				return true;
		if ( (services.isEmpty()) || (services.size()<=0) ) {
			GUIState.displayWarning(parent,"Test collection incomplete","There were no services found in the test collection. Please add some.");
			return false;
		}
		if ( (queries.isEmpty()) || (queries.size()<=0) ) {
			GUIState.displayWarning(parent,"Test collection incomplete","There were no service requests found in the test collection. Please add some.");
			return false;
		}
		return checkRelevanceSets(parent);
	}
	
	public static void main(String[] args) {
		TestCollection tc = TestCollection.getInstance();
		String servicesPath = args[0] + File.separator + "services" + File.separator + "1.1";
		String queriesPath = args[0] + File.separator + "queries" + File.separator + "1.1";
		String rsPath = args[0] + File.separator + "relevance_sets";
		
		// register services
		File services = new File(servicesPath);
		HashMap<String, URI> localServiceNames = new HashMap<String, URI>();
		for(File service : services.listFiles()) {
			if(service.isFile()) {
				try {
					tc.addService(service.toURI());
					localServiceNames.put(service.getName(), service.toURI());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// register queries
		File queries = new File(queriesPath);
		HashMap<String, URI> localQueryNames = new HashMap<String, URI>();
		for(File query : queries.listFiles()) {
			if(query.isFile()) {
				try {
					tc.addQuery(query.toURI());
					localQueryNames.put(query.getName(), query.toURI());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// load relevance sets
		File rSets = new File(rsPath);
		for(File rs : rSets.listFiles()) {
			if(rs.isDirectory()) {
				String localName = rs.getName().split("-")[1];
				for(File rFile : rs.listFiles()) {
					tc.addServiceToAnswerset(localQueryNames.get(localName), localServiceNames.get(rFile.getName()));
				}
			}
		}
		
		// save the tc dat
		tc.save(args[1]);
	}
}
