package de.dfki.wsdlanalyzer.types;

import java.util.ArrayList;

import de.dfki.wsdlanalyzer.matcher.NameTokens;



/**
 * class representing a wsdl-message
 * by its name and parameterlist i.e.
 * <wsdl:message ...>...</wsdl:message>
 */
public class Message 
{
	private String name;
	private ArrayList<MessageParameter> parameterlist;
	private NameTokens token;
	//	NodeIdentifier for visualization
	NodeIdentifier nodeIdentifier;
	
	public Message()
	{
		name = null;
		parameterlist = new ArrayList<MessageParameter>();
		nodeIdentifier = new NodeIdentifier();
	}
	
	public Message(String s)
	{
		name = s;
		parameterlist = new ArrayList<MessageParameter>();
		nodeIdentifier = new NodeIdentifier();
	}
	
	public void setName(String s)
	{
		name = s;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void insertParameter(MessageParameter p)
	{
		parameterlist.add(p);
	}
	
	public ArrayList<MessageParameter> getParameterList()
	{
		return parameterlist;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return parameterlist.isEmpty();
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
