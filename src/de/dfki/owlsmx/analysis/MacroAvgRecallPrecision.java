/*
 * SME^2 DFKI GmbH 2007
 * Refer to license.txt for copyright details.
 *
 * Created on 18.11.2004
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
package de.dfki.owlsmx.analysis;

import java.util.ArrayList;
import java.util.List;

import java.util.Vector;

/**
 * Class representing the recall / precision metric
 *
 * @version 0.02 18/06/07
 * @author khamah
 * @version 2.0 21.10.2008
 * @author Patrick Kapahnke
 */
public class MacroAvgRecallPrecision {

    /** Lemda level */
    public final int LEMDA_LEVELS;

	private double[] recall_levels;
	private double[] precisions;
	private int numOfQueries = 0;
	
    //
    // Ctor
    //

    /**
     * Constructor.
     * @param lambda lambda levels
     */
	public MacroAvgRecallPrecision (int lambda) {
        LEMDA_LEVELS = lambda;
		
		recall_levels = new double[lambda];
		for(int i = 0; i < lambda; i++) {
			recall_levels[i] = (double) i/(lambda - 1);
		}
		precisions = new double[lambda];
		for(int i = 0; i < lambda; i++) {
			precisions[i] = 0.0;
		}
	}


    //
    // Methods
    //

	/**
	 * Process Recall Precision.
     *
	 * @param retrievals retrieved documents for a query.
	 * @param correctRetrievals relevant documents in corpus for a query.
     * @return intermediate precisions
	 */
	public void processRecallPrecision (List retrievals, ArrayList correctRetrievals)
	{
		numOfQueries++;
		
		ArrayList<RecallPrecisionPair> RPs = new ArrayList<RecallPrecisionPair>();
		
		Vector<Double> retrieved = new Vector<Double>();
		Vector<Double> retrievedRelevant = new Vector<Double>();
		
		// Number of correctly retrieved docs at any given point
		int goodRetrievals = 0;
		// Examine each ranked retrieval in order to compute rp pairs
		for(int i = 0; i < retrievals.size(); i++) 
		{			
			// Current number of retrievals considered
			int numRetrieved = i + 1;
			// Check if this retrieval is in the list of relevant docs
			if (correctRetrievals.contains(retrievals.get(i))) 
			{
				goodRetrievals++;  // This is a relevant retrieval
				retrievedRelevant.add(new Double(goodRetrievals));
				retrieved.add(new Double(numRetrieved));
    			// Compute recall and precision for first numRetrieved docs
				double recall = (double) goodRetrievals / (double) correctRetrievals.size();
				double precision = (double) goodRetrievals / (double) numRetrieved;
				
				RPs.add(new RecallPrecisionPair(recall, precision));
			}
		}
				
		double[] interpolatedPs = interpolatePrecision(RPs);
		for(int i = 0; i < LEMDA_LEVELS; i++) {
			precisions[i] += interpolatedPs[i];
		}
	}
		
	/**
	 * Get the precision.
	 * @return macro evaluation of average recall precision pairs. 
	 */
	public ArrayList<RecallPrecisionPair> getMacroAveragePrecisions ()
	{
		ArrayList<RecallPrecisionPair> rpList = new ArrayList<RecallPrecisionPair>();
		for(int i=0; i < LEMDA_LEVELS; i++)
			rpList.add(new RecallPrecisionPair(recall_levels[i], precisions[i]/numOfQueries));
		
		return rpList;
	}
	
	private double[] interpolatePrecision(ArrayList rpPairs) {
		double[] precisions = new double[LEMDA_LEVELS];
		for(int i = 0; i < LEMDA_LEVELS; i++) {
			// store the maximum precision for this recall level
			double maxPrecision = 0.0;
			// check each observed point
			for(int j = 0; j < rpPairs.size(); j++) {
				RecallPrecisionPair rpPair = (RecallPrecisionPair) rpPairs.get(j);
				if(rpPair.recall >= recall_levels[i] && rpPair.precision > maxPrecision)
					maxPrecision = rpPair.precision;
			}
			precisions[i] = maxPrecision;
		}
		
		return precisions;
	}
		
	/**
     * Interpolate precision values for each standard recall level
	 * in RECALL_LEVELS from the list of rpPairs for a given query.
	 * See chapter 3 of Modern Information Retrieval (Baeza Yates) for details.
     *
     * @param rpPairs pairs of query and result
     * @return interpolated precisions
     */
//	private double[] interpolatePrecision(ArrayList rpPairs) 
//	{
//		// Array of interpolated precisions
//		double[] precisions = new double[LEMDA_LEVELS];
//		// Compute precision value for each recall level, starting
//		// from the highest and working backwards
//		for(int i = recall_levels.length-1; 0 <= i; i--)
//		{
//			// Stores maximum precision for this recall level.
//			// Interpolated precision at level i is the max
//			// precision seen (or interpolated) at any recall
//			// value between level i and level i+1, inclusive.
//			double maxPrecision = 0.0;
//			// Check each point in rpPairs to see if it is between
//			// recall levels i and i+1, compute the max of these precision values.
//			for(int j = 0; j < rpPairs.size(); j++) 
//			{
//				RecallPrecisionPair rpPair = (RecallPrecisionPair)rpPairs.get(j);
//				if(recall_levels[i] <= rpPair.recall &&
//						(i == recall_levels.length - 1 ||  // no higher level i+1
//							rpPair.recall <= recall_levels[i+1]
//						)
//					) 
//				{
//					// If recall in correct interval, update max precision
//					if (rpPair.precision > maxPrecision)
//						maxPrecision = rpPair.precision;
//				}//end if
//			}//end for
//			
//			if(i< recall_levels.length - 1 && precisions[i+1] > maxPrecision)
//					maxPrecision = precisions[i+1];
//			precisions[i] = maxPrecision;			 
//		}//end for outer
//  	
//		return precisions;
//	}//interpolatePrecision
	
	/**
	 * P_rj = max_{rj <= r <= rj+1} P_r
	 * 
	 * METHOD FROM THE iMatcher EVALUATION IMPLEMENTATION FOR TESTING PURPOSE
	 */	
//	private double[] interpolatePrecision(ArrayList<RecallPrecisionPair> rpPairs) {
//		double[] intPR = new double[LEMDA_LEVELS];
//		
//		// initialize interpolated precision array
//		for (int i = 0; i < intPR.length; i++) {
//			intPR[i] = -1.0;
//		}
//		// interpolation
//		for (int i = 0; i < LEMDA_LEVELS - 1; i++) {
//			// intervall of std recall levels
//			double rj = recall_levels[i];
//			double rj1 = recall_levels[i + 1];
//			// find simple recall levels that fall into this intervall
//			Vector<Integer> sLevels = new Vector<Integer>();
//			for (int k = 0; k < rpPairs.size(); k++) {
//				double sLevel = rpPairs.get(k).recall;
//				if (sLevel >= rj && sLevel <= rj1) {
//					sLevels.add(k);
//				}
//			}
//			// find maximum known precision at simple recall levels
//			double max = -1.0;
//			for (int sLevel : sLevels) {
//				if (rpPairs.get(sLevel).precision >= max) {
//					max = rpPairs.get(sLevel).precision;
//					// this were the number of retrieved relevant docs and
//					// retrieved docs for that precision, used later for
//					// micro-averaging
//					// ???????????????
////					retrievedRelevantDocs[i] = retRelDocs.get(sLevel);
////					retrievedDocs[i] = retDocs.get(sLevel);
//				}
//			}
//			// set interpolated precision at rj
//			intPR[i] = max;
//		}
//		// ceiling
//		for (int i = 0; i < intPR.length - 1; i++) {
//			if (intPR[i] < intPR[i + 1]) {
//				intPR[i] = intPR[i + 1];
//			}
//		}
//		// fix trailing -1.0s
//		for (int i = 0; i < intPR.length - 1; i++) {
//			if (intPR[i + 1] == -1.0) {
//				intPR[i + 1] = intPR[i];
//			}
//		}
//		
//		return intPR;
//	}
		

}//end RecallPrecesion class
