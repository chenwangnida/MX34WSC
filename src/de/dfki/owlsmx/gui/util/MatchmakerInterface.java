/*
 * Created on 13.10.2005
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
 */
package de.dfki.owlsmx.gui.util;

import java.net.URI;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import de.dfki.owlsmx.data.MatchedService;
import de.dfki.owlsmx.exceptions.MatchingException;
import de.dfki.owlsmx.gui.data.HybridServiceItem;
import de.dfki.owlsmx.gui.data.ServiceItem;
import de.dfki.owlsmx.gui.data.TestCollection;
import de.dfki.owlsmx.similaritymeasures.SimilarityMeasure;

/**
 * @author Ben
 *
 */
public class MatchmakerInterface {	
	
	private boolean owlsmxp = false;
	private boolean integrative = false;
	
	private String getSimType(int type) {
		switch(type){		
		case SimilarityMeasure.SIMILARITY_COSINE:
			return "OWLS M3: Cosine";
		case SimilarityMeasure.SIMILARITY_EXTENDED_JACCARD:
			return "OWLS M3: exJ";
		case SimilarityMeasure.SIMILARITY_JENSEN_SHANNON:
			return "OWLS M3: Jensen Shannon";
		case SimilarityMeasure.SIMILARITY_LOI:
			return "OWLS M3: LOI";
		default:
			return "OWLS M0: Semantic";				
	}	
		
	}
	
	private de.dfki.owlsmx.SimilarityMatchingEngine matcher=null;
	private static MatchmakerInterface _instance = new MatchmakerInterface();
	private boolean ranMatchmaker = false;
	
	public static MatchmakerInterface getInstance() {
		return _instance;
	}
	
	public void createMatchmaker() {
		short type = (short)GUIState.getInstance().getSimilarityMeasure();
		de.dfki.owlsmx.io.ErrorLog.debug("Similarity measure: " + getSimType(type));
		switch(type){		
			case SimilarityMeasure.SIMILARITY_COSINE:
				matcher = new de.dfki.owlsmx.SimilarityMatchingEngine(new de.dfki.owlsmx.similaritymeasures.CosineSimilarity());
				break;
			case SimilarityMeasure.SIMILARITY_EXTENDED_JACCARD:
				matcher = new de.dfki.owlsmx.SimilarityMatchingEngine(new de.dfki.owlsmx.similaritymeasures.ExtendedJaccardMeasure());
				break;
			case SimilarityMeasure.SIMILARITY_JENSEN_SHANNON:
				matcher = new de.dfki.owlsmx.SimilarityMatchingEngine(new de.dfki.owlsmx.similaritymeasures.JensenShannonMeasure());
				break;
			case SimilarityMeasure.SIMILARITY_LOI:
				matcher = new de.dfki.owlsmx.SimilarityMatchingEngine(new de.dfki.owlsmx.similaritymeasures.ConstraintSimilarity());
				break;
			default:
				matcher = new de.dfki.owlsmx.SimilarityMatchingEngine(null);				
		}		
		matcher.clear();
	}
	
	public SortedSet matchRequest(URI profileURI, int minimumDegreeOfMatch, double treshold) {
		try {				
			de.dfki.owlsmx.io.ErrorLog.debug("Matching request: " + profileURI);
			de.dfki.owlsmx.io.ErrorLog.debug("Minimum DOM: " + minimumDegreeOfMatch);
			de.dfki.owlsmx.io.ErrorLog.debug("Similarity treshold: " + treshold);
			matcher.setIntegrative(integrative);
			ranMatchmaker = true;
			//matcher.setSimilarityMeasure(GUIState.getInstance().getSimilarityMeasure());
			SortedSet tmpResult = matcher.matchRequest(profileURI, GUIState.getInstance().getMinDegree(), GUIState.getInstance().getTreshold());
			SortedSet result;
			if(owlsmxp) {
				try {
					org.mindswap.owl.OWLKnowledgeBase kb = org.mindswap.owl.OWLFactory.createKB();
					org.mindswap.owl.OWLOntology request = kb.read(profileURI);		
					SortedSet filteredResult = matcher.useOWLSMXPFilter(tmpResult, request);
					for(Iterator iter = filteredResult.iterator(); iter.hasNext();) {
						MatchedService service = (MatchedService) iter.next();
					}
					result = MatchmakerToGUISet(tmpResult, filteredResult);
				}
				catch(Exception e) {
					e.printStackTrace();
					matcher = null;
					return new TreeSet();
				}
			}
			else {
				result = MatchmakerToGUISet(tmpResult);
			}
			de.dfki.owlsmx.io.ErrorLog.debug("Resultat:\n   " + tmpResult);	
			return result;
		} catch (MatchingException e) {
			e.printStackTrace();
			matcher = null;
			return new TreeSet();
		}
	}
	
	public void addService(URI profileURI) {
		try {
		if (matcher==null) {
			de.dfki.owlsmx.io.ErrorLog.debug(this.getClass().toString()+": Reset machmaker");
			createMatchmaker();
		}

		de.dfki.owlsmx.io.ErrorLog.debug(this.getClass().toString()+": Adding service: " + profileURI.toString());
		matcher.addService(profileURI);
		}
		catch(Exception e) {
			GUIState.displayWarning("Matchmaker", "Couldn't add service " + profileURI + " either file not found or not an valid OWL-S 1.1 file."); 
		}
		
	}
	
	private SortedSet MatchmakerToGUISet(SortedSet result) {		
		SortedSet hybrid = new TreeSet();
		if (result == null) {
			GUIState.displayWarning("Matchmaker", "Result set is empty");
//			ErrorLog.report("Matchmaker didn't return any result");
//			owlsmx.io.ErrorLog.debug("Matchmaker didn't return any result");
			return hybrid;
		}
//		owlsmx.io.ErrorLog.debug("Matchmaker result: " +  result);
		MatchedService m_result;
		HybridServiceItem h_result;
		ServiceItem s_item;
		for(Iterator iter = result.iterator();iter.hasNext();){
			m_result = (MatchedService) iter.next();
			s_item = TestCollection.getInstance().getService(m_result.serviceURI);
			h_result = new HybridServiceItem(s_item);
			h_result.setDegreeOfMatch(m_result.degreeOfMatch);
			h_result.setSyntacticSimilarity(m_result.similarity);
//			owlsmx.io.ErrorLog.debug(this.getClass().toString() + "|M2GUI: MResult " + m_result.toString());
			hybrid.add(h_result);
		}
		
		return hybrid;
	}
	
	private SortedSet MatchmakerToGUISet(SortedSet results, SortedSet compatibleResults) {
		SortedSet hybrid = new TreeSet();
		if(results == null) {
			GUIState.displayWarning("Matchmaker", "Result set is empty");
			return hybrid;
		}
		MatchedService m_result;
		HybridServiceItem h_result;
		ServiceItem s_item;
		for(Iterator iter = results.iterator(); iter.hasNext(); ) {
			m_result = (MatchedService) iter.next();
			s_item = TestCollection.getInstance().getService(m_result.serviceURI);
			h_result = new HybridServiceItem(s_item);
			h_result.setDegreeOfMatch(m_result.degreeOfMatch);
			h_result.setSyntacticSimilarity(m_result.similarity);
			h_result.setDataTypeCompatible(compatibleResults.contains(m_result));
			hybrid.add(h_result);
		}
		
		return hybrid;
	}
	
	public void clear() {
		matcher.clear();
		matcher = null;
		de.dfki.owlsmx.io.ErrorLog.debug(this.getClass().toString()+": Reset machmaker");
	}
	
	public boolean didRun() {
		return ranMatchmaker;
	}
	
	public void setOWLSMXP(boolean value) {
		owlsmxp = value;
	}
	
	public void setIntegrative(boolean value) {
		integrative = value;
	}
}
