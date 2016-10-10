package de.dfki.wsdlanalyzer.types;

import de.dfki.wsdlanalyzer.matcher.NameTokens;
/**
 * representation of an attribute of a complexType
 * @author Hans
 *
 */
public class Attribute 
{
	private String name,type,use;
	private NodeIdentifier id;
	private NameTokens token;
	
	public Attribute(String attributeName,String attributeType,String attributeUse)
	{
		name = attributeName;
		type = attributeType;
		use = attributeUse;
		id = new NodeIdentifier();
	}

	public String getName() 
	{
		return name;
	}
	

	public void setName(String name) 
	{
		this.name = name;
	}
	

	public String getType() 
	{
		return type;
	}
	

	public void setType(String type) 
	{
		this.type = type;
	}
	

	public String getUse() 
	{
		return use;
	}
	

	public void setUse(String use) 
	{
		this.use = use;
	}

	public NodeIdentifier getId() 
	{
		return id;
	}
	

	public void setId(NodeIdentifier id) 
	{
		this.id = id;
	}

	public NameTokens getToken() 
	{
		return token;
	}
	

	public void setToken(NameTokens token) 
	{
		this.token = token;
	}
	
	
	
}
