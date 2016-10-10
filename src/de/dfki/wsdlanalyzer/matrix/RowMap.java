package de.dfki.wsdlanalyzer.matrix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * class for row indices
 * maps ColumnLists to row indices i.e. sourcetypes
 */
public class RowMap 
{
	//private ColumnList columnlist;
	private HashMap<String,ColumnList> rowMap;
	
	public RowMap()
	{
		rowMap = new HashMap<String,ColumnList>();
	}
	
	public ColumnList putRow(String s,ColumnList c)
	{
		return rowMap.put(s,c);
	}
	
	public boolean containsKey(String s)
	{
		return rowMap.containsKey(s);
	}
	
	public ColumnList getColumnList(String s)
	{
		return rowMap.get(s);
	}
	
	public Iterator<String> getIterator()
	{
		return rowMap.keySet().iterator();
	}

	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return rowMap.keySet();
	}
	
	public void print()
	{
		for(Iterator<String> keyIterator=rowMap.keySet().iterator();keyIterator.hasNext();)
		{
			String row = keyIterator.next();
			//System.out.println("row: "+row);
			ColumnList columnList = rowMap.get(row);
			columnList.print();
		}
	}
}
