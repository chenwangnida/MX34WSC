package de.dfki.wsdlanalyzer.types;



import java.net.URL;

import de.dfki.wsdlanalyzer.matcher.NameTokens;


/**
 * represantation of a port by its name and the corresponding
 * binding
 * @author Hans
 *
 */
public class Port 
{
	private String name;
	private String binding;
	private URL address;
	private NameTokens token;
	//	NodeIdentifier for visualization
	NodeIdentifier nodeIdentifier;
	
	public Port(String s)
	{
		name = s;
		binding = null;
		nodeIdentifier = new NodeIdentifier();
	}

	public String getBinding() 
	{
		return binding;
	}
	

	public void setBinding(String binding) 
	{
		this.binding = binding;
	}
	

	public String getName() 
	{
		return name;
	}
	

	public void setName(String name)
{
		this.name = name;
	}
	

	public NodeIdentifier getNodeIdentifier()
	{
		return nodeIdentifier;
	}
	

	public void setNodeIdentifier(NodeIdentifier nodeIdentifier) 
	{
		this.nodeIdentifier = nodeIdentifier;
	}
	

	public NameTokens getToken() 
	{
		return token;
	}
	

	public void setToken(NameTokens token) 
	{
		this.token = token;
	}

	public URL getAddress() 
	{
		return address;
	}
	

	public void setAddress(URL address) 
	{
		this.address = address;
	}
	
	
}
