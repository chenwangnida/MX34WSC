package de.dfki.wsdlanalyzer.types;

import de.dfki.wsdlanalyzer.matcher.NameTokens;

/**
 * class for parameter of a wsdl-message i.e.
 * <wsdl:part .../> tags
 */

public class MessageParameter 
{
	private String name,prefix,type;
	private NameTokens token;
	private NodeIdentifier nodeIdentifier;
	
	public MessageParameter(String n,String p,String t)
	{
		name = n;
		prefix = p;
		type = t;
	}
	
	public void setName(String s)
	{
		name = s;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setPrefix(String s)
	{
		prefix = s;
	}
	
	public String getPrefix()
	{
		return prefix;
	}
	
	public void setType(String s)
	{
		type = s;
	}
	
	public String getType()
	{
		return type;
	}

	public NameTokens getToken() 
	{
		return token;
	}
	

	public void setToken(NameTokens token) 
	{
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
