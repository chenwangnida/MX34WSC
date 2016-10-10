package de.dfki.wsdlanalyzer.matrix;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Set;
import java.util.TreeMap;

import de.dfki.wsdlanalyzer.mapping.Mapping;
import de.dfki.wsdlanalyzer.mapping.MappingPartList;



/**
 * class for matching scores
 * maps RowMaps to their matching score
 * 
 * the 'matching-matrix' is stored in a 'bucket-sort'-like manner
 * a bucket with key "score" contains all matching pairs with this score
 * and stores them first by the name of the requirementsobject(row)
 * in a rowmap and second by the name of the candidateobject(column)
 * in a columnlist.
 * in order to compute not only the matchingscore but also the
 * corresponding mapping the cells of the matrix contain mappings
 * instead of numbers.
 * (a switch for computing a more "tree-edit-distance" like
 * score is also implemented, should be set to "true")
 */
public class ScoreMap 
{
	//number of matches which contribute to maxscore
	private int matchCount,sizeDifference;
	private int maxScore = 10;
	private int minScore = 0;
	
	private String requirementsName,candidateName;
	//private HashMap<Integer,Boolean> selectedrows,selectedcolumns;
	//arraylist of potentially maximum scores
	//private TreeMap<Integer,ArrayList<MatrixCell>> matchingmap;
	
	private TreeMap<Integer,ArrayList<Mapping>> matchingMap;
	private TreeMap<Integer,RowMap> scoreMap;
	private Mapping mapping;
	private int entries;
	/**
	 * switch for turning on/off "tree-edit-distance-like" score
	 * i.e decreasing score for each element which cannot be mapped
	 * (because of different sizes(lengths) of matched entities
	 */ 
	private boolean treeEdit;
	
	private boolean debug;
	
	/**
	 * constructs an empty Scoremap
	 * @param i number of lines
	 * @param j number of columns
	 * @param requirements name of the requirement-entity
	 * @param candidate name of the candidate-entity
	 * @param edit 
	 */
	public ScoreMap(int i,int j, String requirements, String candidate, Boolean edit)
	{
		matchCount = java.lang.Math.min(i,j);
		sizeDifference = java.lang.Math.abs(i-j);
		scoreMap = new TreeMap<Integer,RowMap>(new DecreasingSorter());
		requirementsName = requirements;
		candidateName = candidate;
		//to catch all mappings witha certain score
		
		
		matchingMap = new TreeMap<Integer,ArrayList<Mapping>>();
		entries = i*j;
		
		treeEdit = edit;
		//create new Mapping
		
		mapping = new Mapping(requirementsName,candidateName);
		debug = false;
		/*if(requirementsName.equals("dateType"))
			debug = true;*/
		//System.out.println("names: "+mapping.getSourceName()+", "+mapping.getTargetName());
	}
	
	/**
	 * writes a new entry in the ScoreMap
	 */
	public void setValue(Mapping mappingpart)
	{
		String row = mappingpart.getRequirementsName();
		String column = mappingpart.getCandidateName();
		
		//put value in orderedscores-treemap
		Integer score = new Integer(mappingpart.getScore());
		//Integer row = new Integer(firstindex);
		//Integer column = new Integer(secondindex);
		/*
		 * value isn't cotained in treemap
		 * => create new arraylist for columnindex
		 *    new hashmap for rowindex
		 *    add all to treemap
		 */
		if(!scoreMap.containsKey(score))
		{
			//create arraylist for column index
			ColumnList columnindices = new ColumnList();
			//put column index in arraylist
			columnindices.addColumn(column,mappingpart);
			//create new hashmap for row index
			RowMap rowindices = new RowMap();
			//put row index with column arraylist in hashmap
			rowindices.putRow(row,columnindices);
			//put value with hashmap for rowindex in treemap
			scoreMap.put(score,rowindices);
		}
		//value exists in orderedscores-treemap
		else
		{
			/*
			 * 
			 *firstindex i.e. row isn't contained in the hashmap
			 *for this value
			 *=>create new arraylist of columns for this row
			 *  and add to the hashmap for this value
			 */
			if(!scoreMap.get(score).containsKey(row))
			{
				//create arraylist for column index
				ColumnList columnindices = new ColumnList();
				//put column index in arraylist
				columnindices.addColumn(column,mappingpart);
				//put row index with column arraylist in hashmap
				scoreMap.get(score).putRow(row,columnindices);
			}
			/*
			 * row already cotained in hashmap for this value
			 * => add column to its arraylist for columnindices
			 */
			else
			{
				scoreMap.get(score).getColumnList(row).addColumn(column,mappingpart);
			}
		}
		//matrix.setCell(row,column,value);
		//System.out.println("scorematrix: ("+firstindex+","+secondindex+"): "+getCell(firstindex,secondindex));
	}
	
	
	
	
	
	/**
	 * computes maximum mapping for requirement and candidate
	 */
	public void computeMatches()
	{
		int score = -1;
		
		if(debug)
			print();
		
				
		//	matrix contains only one score for all matches
		if(scoreMap.size() == 1)
		{
			score = matchCount*scoreMap.firstKey().intValue();
			if(entries > 1)
			{
				//System.out.println("matchcount: "+matchcount+"entries: "+entries+"score: "+score);
				mapping.setScore(score);
				mapping.setAnymatch(true);
			}
			else
			//matrix contains exactly one entry (one row, one column)
			{
				RowMap rows = scoreMap.get(scoreMap.firstKey());
				for(Iterator<String> rowiterator = rows.getIterator();rowiterator.hasNext();)
				{
					//select row
					String row = rowiterator.next();
					//get columnlist for row
					ColumnList columns = scoreMap.get(scoreMap.firstKey()).getColumnList(row);
					//iterator over columns
					for(Iterator<String> columniterator = columns.getColumnIterator();columniterator.hasNext();)
					{
						//select column
						String column = columniterator.next();
						//create mapping for "subobjects
						Mapping subMapping = columns.getMapping(column);
						
						//Mapping subMapping = new Mapping(row,column);
						//subMapping.setScore(scoremap.firstKey().intValue());
						
						//create new MappingPartList
						MappingPartList mappingPartList = new MappingPartList();
						
						//add submapping to MappingPartList
						mappingPartList.addMapping(subMapping);
						
						//add MappingPartList to mapping
						mapping.addMappingPartList(mappingPartList);
						
						
					}
				}
				/*
				 * compute tree-edit-distance like score
				 */
				if(treeEdit)
				{
					int malus = sizeDifference*maxScore;
					int editScore = mapping.getScore()-malus;
					if(editScore < 0)
					{
						mapping.setScore(minScore);
					}
					else
					{
						mapping.setScore(editScore);
					}
						
				}
			}
		}
		//matrix contains different scores
		else
		{
			//get maximum score
			Integer max = scoreMap.firstKey();
			//get rows with maximum score
			RowMap rows = scoreMap.get(max);
			
			//iterator over maximum rows
			for(Iterator<String> rowiterator = rows.getIterator();rowiterator.hasNext();)
			{
				//select row
				String row = rowiterator.next();
				//get columnlist for row
				ColumnList columns = rows.getColumnList(row);
				//iterator over columns
				for(Iterator<String> columniterator = columns.getColumnIterator();columniterator.hasNext();)
				{
					
					//select column
					String column = columniterator.next();
					/*
					*hashmaps for storing rows and columns which have been 
					*selected , i.e which cannot be used in further computation
					*/
					HashMap<String,Boolean> selectedrows = new HashMap<String,Boolean>();
					HashMap<String,Boolean> selectedcolumns = new HashMap<String,Boolean>();
					selectedrows.put(row,true);
					selectedcolumns.put(column,true);
					
					//create new Mapping for "sub-objects"
					Mapping subMapping = columns.getMapping(column);
					//Mapping subMapping = new Mapping(row,column);
					//subMapping.setScore(max.intValue());
					
					//create new MappingPartList
					MappingPartList mappingPartList = new MappingPartList();
					
					//add submapping to MappingPartList
					mappingPartList.addMapping(subMapping);
					
					//recursion					
					if(!computeMatches(selectedrows,selectedcolumns,mappingPartList,matchCount-1))
					{
						//add MappingPartList to mapping
						mapping.addMappingPartList(mappingPartList);
					}
					
				}
			}
			/*
			 * compute tree-edit-distance like score
			 */
			if(treeEdit)
			{
				int malus = sizeDifference*maxScore;
				int editScore = mapping.getScore()-malus;
				if(editScore < 0)
				{
					mapping.setScore(minScore);
				}
				else
				{
					mapping.setScore(editScore);
				}
					
			}
		}		
	}
	
	private boolean computeMatches(HashMap<String,Boolean> rows,HashMap<String,Boolean> columns,MappingPartList list,int count)
	{
		/*
		*test for count
		* if >0 recursion
		* else mapping fnished
		*/
		if(count > 0)
		{
			MappingPartList base = list;
			
			//get iterator over values
			for(Iterator<Integer> valueiterator = scoreMap.keySet().iterator();valueiterator.hasNext();)
			{
				
				Integer value = valueiterator.next();
				//get rows for this value
				RowMap tmprows = scoreMap.get(value);
					
				//iterator over  rows
				for(Iterator<String> rowiterator = tmprows.getIterator();rowiterator.hasNext();)
				{
						
					String row = rowiterator.next();
					//System.out.println("row: "+row+", "+count);
					//test if row selected
					if(!rows.containsKey(row))
					{
						
						//get columnlist for row
						ColumnList tmpcolumns = scoreMap.get(value).getColumnList(row);
						//iterator over columns
						for(Iterator<String> columniterator = tmpcolumns.getColumnIterator();columniterator.hasNext();)
						{
							String column = columniterator.next();
							//test if column selected
							if(!columns.containsKey(column))
							{
								/*
								 * create a new copy of base 
								 * to build one alternative
								 */
								MappingPartList alternative = base.copy();
								//TODO also a copy selected rows and columns???
								//set row selected
								rows.put(row,true);
								//set column selected
								columns.put(column,true);
								//create new mapping
								Mapping subMapping = tmpcolumns.getMapping(column);
								//System.out.println("added: "+row+", "+column+", "+count+", "+subMapping.getScore()+"\n");
								//Mapping subMapping = new Mapping(row,column);
								//subMapping.setScore(value);
								
								//add subMapping to MappingPartList
								alternative.addMapping(subMapping);
								
								//recurse
								
								if(!computeMatches(rows,columns,alternative,count-1))
								{
									mapping.addMappingPartList(alternative);
								}
								//break;
							}
						}
						//break;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	/*private void computeMatchesold(HashMap<String,Boolean> rows,HashMap<String,Boolean> columns,MappingPartList list,int count)
	{
		//test if count>0
		if(count > 0)
		{
			MappingPartList base = list;
			
			//get iterator over values
			for(Iterator<Integer> valueiterator = scoreMap.keySet().iterator();valueiterator.hasNext();)
			{
				
				Integer value = valueiterator.next();
				//get rows for this value
				RowMap tmprows = scoreMap.get(value);
					
				//iterator over  rows
				for(Iterator<String> rowiterator = tmprows.getIterator();rowiterator.hasNext();)
				{
						
					String row = rowiterator.next();
					//System.out.println("row: "+row+", "+count);
					//test if row selected
					if(!rows.containsKey(row))
					{
						/*
						 * create a new copy of base 
						 * to build one alternative
						
						//MappingPartlist rowAlternative =;
						//select row
						//get columnlist for row
						ColumnList tmpcolumns = scoreMap.get(value).getColumnList(row);
						//iterator over columns
						for(Iterator<String> columniterator = tmpcolumns.getColumnIterator();columniterator.hasNext();)
						{
							String column = columniterator.next();
							//test if column selected
							if(!columns.containsKey(column))
							{
								/*
								 * create a new copy of base 
								 * to build one alternative
								 
								//MappingPartlist columnAlternative =;
								
								//set row selected
								rows.put(row,true);
								//set column selected
								columns.put(column,true);
								//create new mapping
								Mapping subMapping = tmpcolumns.getMapping(column);
								//System.out.println("added: "+row+", "+column+", "+count+", "+subMapping.getScore()+"\n");
								//Mapping subMapping = new Mapping(row,column);
								//subMapping.setScore(value);
								
								//add subMapping to MappingPartList
								list.addMapping(subMapping);
								
								//recurse
								
								computeMatches(rows,columns,list,count-1);
								break;
							}
						}
						break;
					}
				}
			}
		}
	}*/
	
		
	
	
	
	
	
	public Mapping getMapping()
	{
		return mapping;
	}
	
	public int getEntryNumber()
	{
		return entries;
	}
	
	public Set<String> getMaxRows()
	{
		RowMap rows = scoreMap.get(scoreMap.firstKey());
		return rows.keySet();
	}
	
	public int getMaxValue()
	{
		return scoreMap.firstKey().intValue();
	}

	public int getEntries() {
		return entries;
	}
			
	private void print()
	{
		for(Iterator<Integer> rowMapIterator = scoreMap.keySet().iterator();rowMapIterator.hasNext();)
		{
			Integer score = rowMapIterator.next();
			System.out.println("ScoreKey: "+score.intValue()+"\n");
			RowMap rowMap = scoreMap.get(score);
			rowMap.print();
			System.out.println("** end of score **\n");
		}
	}
}
