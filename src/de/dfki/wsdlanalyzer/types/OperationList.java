package de.dfki.wsdlanalyzer.types;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;



/**
 * class representing the list of operations of a wsdl-file
 * uses operationname as key and the operation as value
 */

public class OperationList 
{
	//list of operations from wsdl file
	private HashMap<String,Operation> operationlist;
	
	//matching score
	private int score;
		
	public OperationList()
	{
		operationlist = new HashMap<String,Operation>();
	}
		
	
	
	public void insertOperation(Operation m)
	{
		operationlist.put(m.getName(),m);
	}
		
	//get operations with name s
	public Operation getOperation(String s)
	{
		return operationlist.get(s);
	}
		
	public boolean isEmpty()
	{
		return operationlist.isEmpty();
	}
		
	public Iterator<Operation> operationIterator()
	{
		return operationlist.values().iterator();
	}
		
	public int length()
	{
		return operationlist.size();
	}
		
	public void setScore(int i)
	{
		score = i;
	}
		
	public int getScore()
	{
		return score;
	}



	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return operationlist.keySet();
	}
	
	public void print()
	{
		for(Iterator<String> niter = operationlist.keySet().iterator();niter.hasNext();)
		{
			System.out.println(niter.next());
		}
	}
}
