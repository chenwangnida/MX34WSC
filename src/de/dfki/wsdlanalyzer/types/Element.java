package de.dfki.wsdlanalyzer.types;

import de.dfki.wsdlanalyzer.matcher.NameTokens;




/**
 * class for representing <element> tags within complex types
 */
public class Element 
{
	//elementname
	private String name;
	//type of the element (complex type possible)
	private String type;
	//prefix of type
	private String typePrefix;
	//prefix of element
	private String elementPrefix;
	//min/max-occurences
	private int minoccur;
	private int maxoccur;
	//flag for xsd-predefined(simple)types(e.g. int, string etc.) or selfdefined(complex)types
	private boolean simple;
	//flag for nillable elements
	private boolean nillable;
	private NameTokens token;
	//NodeIdentifier for visualization
	private NodeIdentifier nodeIdentifier;
	
	
	public Element()
	{
		name = null;
		simple = true;
		minoccur = -1;
		maxoccur = -1;
		nodeIdentifier = new NodeIdentifier();
	}
	
	public Element(String s)
	{
		name = s;
		simple = true;
		minoccur = -1;
		maxoccur = -1;
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
	
	public void setType(String s)
	{
		type = s;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getTypePrefix() 
	{
		return typePrefix;
	}
	

	public void setTypePrefix(String typePrefix) 
	{
		this.typePrefix = typePrefix;
	}
	

	public void setElementPrefix(String s)
	{
		elementPrefix = s;
	}
	
	public String getElementPrefix()
	{
		return elementPrefix;
	}
	
	public void setMinOccur(int i)
	{
		minoccur = i;
	}
	
	public int getMinOccur()
	{
		return minoccur;
	}
	
	public void setMaxOccur(int i)
	{
		maxoccur = i;
	}
	
	public int getMaxOccur()
	{
		return maxoccur;
	}
	
	public void setSimple(boolean b)
	{
		simple = b;
	}
	
	public boolean isSimple()
	{
		return simple;
	}
	
	public void setNillable(boolean b)
	{
		nillable = b;
	}
	
	public boolean isNillable()
	{
		return nillable;
	}
	
	
	
	//test for equality of xsd-elements
	public boolean equals(Element t)
	{
		if (name.equals(t.getName()))
		{
			if (type.equals(t.getType()))
				return true;
		}
		return false;
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
	

	public void printElement()
	{
		System.out.println("*element*\n");
		System.out.println("name: "+name);
		System.out.println("type: "+type);
		System.out.println("prefix: "+elementPrefix);
		//System.out.println("simple: "+simple);
		System.out.println("minoccur: "+minoccur);
		System.out.println("maxoccur: "+maxoccur);
		System.out.println("id: "+nodeIdentifier.toString()+"\n");
		
		
	}

}
