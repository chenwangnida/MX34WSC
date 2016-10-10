package de.dfki.wsdlanalyzer.matrix;


import java.util.HashMap;
import java.util.Iterator;

import de.dfki.wsdlanalyzer.mapping.Mapping;



/**
 * class for columnindices 
 * contains the indices of the columns i.e. targettypes 
 * for a row i.e. sourcetype which have the same
 * matching score
 */
public class ColumnList 
{
	private HashMap<String,Mapping> columnList;
	
	public ColumnList()
	{
		columnList = new HashMap<String,Mapping>();
	}
	
	public Mapping addColumn(String s,Mapping m)
	{
		return columnList.put(s,m);
	}
	
	public Iterator<String> getColumnIterator()
	{
		return columnList.keySet().iterator();
	}
	
	public Iterator<Mapping> getMappingIterator()
	{
		return columnList.values().iterator();
	}
	
	public Mapping getMapping(String s)
	{
		return columnList.get(s);
	}
	
	public void print()
	{
		for(Iterator<String> keyIterator=columnList.keySet().iterator();keyIterator.hasNext();)
		{
			String column = keyIterator.next();
			Mapping mapping = columnList.get(column);
			//System.out.println("column: "+column);
			System.out.println(mapping.getRequirementsName()+", "+mapping.getCandidateName());
		}
	}
}
