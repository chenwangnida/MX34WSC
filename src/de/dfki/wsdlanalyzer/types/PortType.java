package de.dfki.wsdlanalyzer.types;

import java.util.Iterator;

import de.dfki.wsdlanalyzer.matcher.NameTokens;




/**
 * class representing a portype by its name and operationlist
 * i.e. <wsdl:portType...>...</wsdl:portType>
 */  
public class PortType 
{
	private String name;
	private OperationList operationlist;
	private NameTokens token;
	//	NodeIdentifier for visualization
	NodeIdentifier nodeIdentifier;
	
	public PortType(String s)
	{
		name = s;
		operationlist = new OperationList();
		nodeIdentifier = new NodeIdentifier();
	}

	public String getName() 
	{
		return name;
	}

	public OperationList getOperationlist() 
	{
		return operationlist;
	}
	

	public void setOperationlist(OperationList operationlist) 
	{
		this.operationlist = operationlist;
	}

	public Operation getOperation(String s) 
	{
		// TODO Auto-generated method stub
		return operationlist.getOperation(s);
	}

	public int getScore() 
	{
		// TODO Auto-generated method stub
		return operationlist.getScore();
	}

	

	public void insertOperation(Operation m) 
	{
		// TODO Auto-generated method stub
		operationlist.insertOperation(m);
	}

	public boolean isEmpty() 
	{
		// TODO Auto-generated method stub
		return operationlist.isEmpty();
	}

	public int length() 
	{
		// TODO Auto-generated method stub
		return operationlist.length();
	}

	public Iterator<Operation> operationIterator() 
	{
		// TODO Auto-generated method stub
		return operationlist.operationIterator();
	}

	public void setScore(int i) 
	{
		// TODO Auto-generated method stub
		operationlist.setScore(i);
	}

	public NameTokens getToken() {
		return token;
	}
	

	public void setToken(NameTokens token) {
		this.token = token;
	}
	

	public NodeIdentifier getNodeIdentifier() 
	{
		return nodeIdentifier;
	}
	

	public void setNodeIdentifier(NodeIdentifier nodeIdentifier)
	{
		this.nodeIdentifier = nodeIdentifier;
	}
	
	
	
}
