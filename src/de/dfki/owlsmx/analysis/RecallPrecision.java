/*
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
/**
 * @author khamah
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * 
 * @deprecated 21.10.2008
 */
public class RecallPrecision {
	
	/**sigma value*/
	public static final double SIGMA = 0.01;
	
	/**Lemda level**/
	public static final int LEMDA_LEVELS = 20;
	
	/** The standard recall levels for which we want to plot precision values */
	public static final double[] RECALL_LEVELS = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
	
	public double[] PRECISION_LEVEL_SUM = null;
	
	public int[] LEVEL_RELEVANT_SUM = null;
	public int[] LEVEL_RETRIEVED_SUM = null;
	
	private int querySize = 0;
	
	private double MaxRecall = 0.0;
	
	private int ALL_RELEVANT_SUM = 0;
		
	/**constructor*/
	public RecallPrecision()
	{
		//constructor is called
		PRECISION_LEVEL_SUM = new double [RECALL_LEVELS.length];
		
		LEVEL_RELEVANT_SUM = new int [LEMDA_LEVELS];
		LEVEL_RETRIEVED_SUM = new int [LEMDA_LEVELS];			
	}
	
	/**
	 * get query numbers, which are process for Recall/Precision. 
	 * @return queryNum
	 */
	public int getQuerySize()
	{
		return querySize;
	}
	
	/**
	 * process Recall Precision, it increase query number.
	 * @param retrievals retrieved documents for a query.
	 * @param correctRetrievals relevant documents in corpus for a query.
	 */
	public double[] processRecallPrecision(List retrievals, ArrayList correctRetrievals)
	{
		querySize++;
		double [] interPrecisions = new double[PRECISION_LEVEL_SUM.length];
		MaxRecall = 0.0;
		ArrayList Precisions = evalRetrievals(retrievals, correctRetrievals);
//		System.out.println("maximum recall=" + MaxRecall);
//		System.out.println("end recall=" + ((RecallPrecisionPair)Precisions.get(Precisions.size()-1)).recall);
		
		ALL_RELEVANT_SUM += correctRetrievals.size();
	//	System.out.println(querySize + " MaxRecall: " + MaxRecall);
		if(MaxRecall > 0.0)
		{		
			interPrecisions = interpolatePrecision(Precisions);
			
			for(int i=0; i < interPrecisions.length; i++) {
				PRECISION_LEVEL_SUM[i] += interPrecisions[i];
			}
						
			MicroEvalRetrievals(retrievals, correctRetrievals);			
		}
		return interPrecisions;
	}
	
	/**
	 * 
	 * @return average precision at 11 points of 
	 */
	
	public double [] getAveragePrecisionAtElevenPoints()
	{
	    double [] result = new double[PRECISION_LEVEL_SUM.length];
		for(int i=0; i < PRECISION_LEVEL_SUM.length; i++)
		    result[i] = PRECISION_LEVEL_SUM[i] / this.getQuerySize();
		
		smoothRPcurve(result);
		return result;
	}
	
	/**
	 * 
	 * @return macro evaluation of average recall precision pairs. 
	 */
	public ArrayList getMicroAveragePrecisions()
	{
		System.out.println("ALL Relevant Sum: " + ALL_RELEVANT_SUM);
		
		ArrayList rpList = new ArrayList();
		for(int i=0; i < LEVEL_RELEVANT_SUM.length; i++)
		{
			double recall    = (double) LEVEL_RELEVANT_SUM[i]/ALL_RELEVANT_SUM;
			double precision = 0.0;
			
			if(LEVEL_RELEVANT_SUM[i] > 0)
			  precision =  (double) LEVEL_RELEVANT_SUM[i]/LEVEL_RETRIEVED_SUM[i];
			rpList.add(new RecallPrecisionPair(recall, precision));
			
			System.out.println("At lemda " + (i+1) + " LEVEL_RELEVANT_SUM: " + LEVEL_RELEVANT_SUM[i] 
											+ " LEVEL_RETRIEVED_SUM: " + LEVEL_RETRIEVED_SUM[i] + " precision "+ (double)LEVEL_RELEVANT_SUM[i]/LEVEL_RETRIEVED_SUM[i]);
		}
		
		return rpList;
	}
	
	/**
	 * 
	 * @return macro evaluation of average recall precision pairs. 
	 */
//	public ArrayList getInterpolatedMicroAveragePrecisions()
//	{
//			ArrayList rpList = getMacroAveragePrecisions(); 
//			//Compute precision value for each recall level, starting
//			// from the highest and working backwards
//			 for(int i = rpList.size()-2; i >= 0; i--) 
//			 {
//				RecallPrecisionPair rpPair = (RecallPrecisionPair) rpList.get(i);
//				RecallPrecisionPair rpPair1 = (RecallPrecisionPair) rpList.get(i+1); 
//				if(rpPair1.precision > rpPair.precision)
//						 rpPair.precision = rpPair1.precision;
//			 }//end for
//			 return rpList;
//	}
	
	/**
	 * 
	 * @param retrievals
	 * @param correctRetrievals
	 * @return array list of recall/precision pairs
	 */
	private ArrayList evalRetrievals(List retrievals, ArrayList correctRetrievals) 
	{
		ArrayList rpList = new ArrayList();
		// Number of correctly retrieved docs at any given point
		double goodRetrievals = 0;
		// Examine each ranked retrieval in order to compute rp pairs
		for(int i = 0; i < retrievals.size(); i++) 
		{
			// Current number of retrievals considered
			int numRetrieved = i + 1;
			// Check if this retrieval is in the list of relevant docs
			if (correctRetrievals.contains(retrievals.get(i))) 
			{
				goodRetrievals++;  // This is a relevant retrieval
			// Compute recall and precision for first numRetrieved docs
				double recall = goodRetrievals / correctRetrievals.size();
				double precision = goodRetrievals / numRetrieved;
				
//System.out.println(numRetrieved + " is relevant: Recall = " + Math.round(100*recall) + "% Precision = " + Math.round(100*precision) + "%");
			// Create a RecallPrecisionPair for this point and add to rpList
				rpList.add(new RecallPrecisionPair(recall, precision));
				
				this.MaxRecall = recall;
			}
		}
		return rpList;
	}
	
	
	
	/**
	 * compute level precision and recall, as in chapter 7 of Keith book
	 * of INFORMATION RETRIEVAL at http://www.dcs.gla.ac.uk/Keith/pdf/
	 * @param retrievals
	 * @param correctRetrievals
	 */
	
	private void MicroEvalRetrievals(List retrievals, ArrayList correctRetrievals)
	{
		int []LEVEL_RELEVANT = new int[LEMDA_LEVELS];
		int []LEVEL_RETRIEVED = new int[LEMDA_LEVELS];
		
		double slot = MaxRecall/LEMDA_LEVELS;
		 
		
		double [] T_RECALL_LEVELS = new double [LEMDA_LEVELS];
		
		int num=0;
		
		if(MaxRecall > 0.5)
		{
			T_RECALL_LEVELS[0] = 0.1;
			num = 1;			
		}
				
		for(; num < T_RECALL_LEVELS.length; num++)
		{
			T_RECALL_LEVELS[num] = (double)Math.round(slot*(num+1)*100)/100;			
		}
		
		int lemda = 0; 
		
		double goodRetrievals = 0; 
				// Examine each ranked retrieval in order to compute rp pairs
		for(int i = 0; i < retrievals.size(); i++) 
		{
			// Current number of retrievals considered
			int numRetrieved = i + 1;
			// Check if this retrieval is in the list of relevant docs
			if (correctRetrievals.contains(retrievals.get(i))) 
			{
				goodRetrievals++;  // This is a relevant retrieval
			// Compute recall and precision for first numRetrieved docs
				double recall = goodRetrievals / correctRetrievals.size();
				double checkRecall = (double)Math.round(recall*100)/100;
				//double precision = goodRetrievals / numRetrieved;
				
				if(lemda >= T_RECALL_LEVELS.length)
					lemda = T_RECALL_LEVELS.length-1;
								
				if(checkRecall == T_RECALL_LEVELS[lemda] 
					|| (checkRecall >= T_RECALL_LEVELS[lemda]-SIGMA && checkRecall <= T_RECALL_LEVELS[lemda]+SIGMA))
				{	
					LEVEL_RELEVANT[lemda] = (int)goodRetrievals;
					LEVEL_RETRIEVED[lemda] = numRetrieved;
					lemda++;		
				}
				else if(checkRecall > T_RECALL_LEVELS[lemda])
				{
					while(checkRecall > T_RECALL_LEVELS[lemda]+SIGMA && lemda < T_RECALL_LEVELS.length-1)
					{						
						if(goodRetrievals > 1)
					  {
					  	LEVEL_RELEVANT[lemda] = (int)goodRetrievals;
							LEVEL_RETRIEVED[lemda] = numRetrieved;
					  }
					  lemda++;
					}
					
					LEVEL_RELEVANT[lemda] = (int)goodRetrievals;
					LEVEL_RETRIEVED[lemda] = numRetrieved;
				  
				}
			}
		}//end for loop
						
		for(int i=0; i < LEVEL_RELEVANT.length; i++)
		{
			LEVEL_RELEVANT_SUM[i] += LEVEL_RELEVANT[i];
			LEVEL_RETRIEVED_SUM[i] += LEVEL_RETRIEVED[i];  
		}		
	}
	
	/** Interpolate precision values for each standard recall level
	 * in RECALL_LEVELS from the list of rpPairs for a given query.
	 * See chapter 3 of Modern Information Retrieval (Baeza Yates) for details.
	 */
	private double[] interpolatePrecision(ArrayList rpPairs) 
	{
		// Array of interpolated precisions
		double[] precisions = new double[RECALL_LEVELS.length];
		// Compute precision value for each recall level, starting
		// from the highest and working backwards
		for(int i = RECALL_LEVELS.length-1; i >= 0 ; i--) 
		{
			// Stores maximum precision for this recall level.
			// Interpolated precision at level i is the max
			// precision seen (or interpolated) at any recall
			// value between level i and level i+1, inclusive.
			double maxPrecision = 0.0;
			// Check each point in rpPairs to see if it is between
			// recall levels i and i+1, compute the max of these precision values.
			for(int j = 0; j < rpPairs.size(); j++) 
			{
				RecallPrecisionPair rpPair = (RecallPrecisionPair)rpPairs.get(j);
				if(RECALL_LEVELS[i] <= rpPair.recall &&
						(i == RECALL_LEVELS.length - 1 ||  // no higher level i+1
							rpPair.recall <= RECALL_LEVELS[i+1]
						)
					) 
				{
					// If recall in correct interval, update max precision
					if (rpPair.precision > maxPrecision)
						maxPrecision = rpPair.precision;
				}//end if
			}//end for
			
			if(i< RECALL_LEVELS.length - 1 && precisions[i+1] > maxPrecision)
					maxPrecision = precisions[i+1];
			precisions[i] = maxPrecision;			 
		}//end for outer
  	
		return precisions;
	}//interpolatePrecision
		
	/** Interpolate precision values for each standard recall level
	 * in RECALL_LEVELS from the list of rpPairs for a given query.
	 * See textbook for details.
	 */
	private void smoothRPcurve(double [] precisions) 
	{
		// Compute precision value for each recall level, starting
		// from the highest and working backwards
		for(int i = precisions.length-1; i >= 0; i--) 
		{
			double maxPrecision = precisions[i];
			
			if(i < precisions.length-1 && precisions[i+1] > precisions[i])
					maxPrecision = precisions[i+1];
			precisions[i] = maxPrecision;	 
		}//end for outer
	}//interpolatePrecision	
	
}//end RecallPrecesion class
