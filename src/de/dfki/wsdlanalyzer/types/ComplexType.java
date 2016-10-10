package de.dfki.wsdlanalyzer.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


//import java.util.HashMap;

import java.util.ListIterator;

import de.dfki.wsdlanalyzer.matcher.NameTokens;






/**class for representing (named)xsd-complexTypes from wsdl-file
 * can consist of elements of potentially complex types
 * or (xsd-predefined) simple types
 * grouped by <sequence>, <all> or <choice>
 * 
 * corresponds to <complexType ...>...</complexType>
 */
public class ComplexType 
{

	//name of complex type (which can be used as a  type in other typedefinition
	private String name;
	//namespace 
	private String nameSpace;
	/**
	 * grouping of the complex type 1=:"sequence", 2:="all", 3:="choice"
	 */
	private int grouping;
	//flag for array-types?
	private boolean array;
	//prefix of array-type
	private String arrayPrefix;
	//type of array
	private String arrayType;
	//list of elements
	private ArrayList<Element> elements;
	//list of attributes
	private HashMap<String,Attribute> attributeList;
	
	//number of leaf/nonleaf elements
	private int numberOfLeafElements,numberOfNonLeafElements,totalLeaves;
	private NameTokens token;
	//NodeIdentifier for visualization
	NodeIdentifier nodeIdentifier;
	
	public ComplexType()
	{
		
		array = false;
		arrayType = null;
		elements = new ArrayList<Element>();
		attributeList = new HashMap<String,Attribute>();
		grouping = 0;
		numberOfLeafElements = 0;
		numberOfNonLeafElements = 0;
		totalLeaves = -1;
		nodeIdentifier = new NodeIdentifier();
	}
	
	public ComplexType(String s)
	{
		
		name = s;
		array = false;
		arrayType = null;
		elements = new ArrayList<Element>();
		attributeList = new HashMap<String,Attribute>();
		grouping = 0;
		numberOfLeafElements = 0;
		numberOfNonLeafElements = 0;
		totalLeaves = -1;
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
	
	public void setNameSpace(String s)
	{
		nameSpace = s;
	}
	
	public String getNameSpace()
	{
		return nameSpace;
	}
	
	public void setGrouping(int i)
	{
		grouping = i;
	}
	
	public int getGrouping()
	{
		return grouping;		
	}
	
	public void setArray()
	{
		array = true;
	}
	
	public boolean isArray()
	{
		return array;
	}
	
	public void setArrayPrefix(String s)
	{
		arrayPrefix = s;
	}
	
	public String getArrayPrefix()
	{
		return arrayPrefix;
	}
	
	public void setArrayType(String s)
	{
		arrayType = s;		
	}
	
	public String getArrayType()
	{
		return arrayType;
	}
	
	public void addElement(Element t)
	{
		elements.add(t);
	}
	
	public void addElementList(ArrayList<Element> list)
	{
		elements.addAll(list);
	}
	
	public ArrayList<Element> getElementList()
	{
		return elements;
	}
	
	public boolean hasElements()
	{
		return !elements.isEmpty();
	}
	
	public boolean hasAttributes()
	{
		return !attributeList.isEmpty();
	}

	public boolean hasAttribute(Object arg0)
	{
		return attributeList.containsKey(arg0);
	}

	public Attribute getAttribute(Object arg0)
	{
		return attributeList.get(arg0);
	}

	public Attribute addAttribute(String arg0, Attribute arg1)
	{
		return attributeList.put(arg0, arg1);
	}

	public int attributeListSize() 
	{
		return attributeList.size();
	}
	
	public HashMap<String,Attribute> getAttributeList()
	{
		return attributeList;
	}
	
	public Iterator<Attribute> attributeIterator()
	{
		return attributeList.values().iterator();
	}

	public boolean equals(ComplexType t)
	{
		if (name.equals(t.getName()))
		{
			if (grouping == t.getGrouping())
				return true;
		}
		return false;
	}
	
	/*public void insertMatch(String type, int score)
	{
		Integer hlp = new Integer(score);
		matchedtypes.put(type,hlp);
	}*/
	
	/*public int getMatch(String type)
	{
		if(matchedtypes.containsKey(type))
		{
			return matchedtypes.get(type).intValue();
		}
		else return -1;
	}*/
	
	/*public HashMap<String,Integer> getMatches()
	{
		return matchedtypes;
	}*/
	
	
	
	public int getNumberOfLeafElements() {
		return numberOfLeafElements;
	}
	

	public void setNumberOfLeafElements(int numberOfLeaafElements) 
	{
		this.numberOfLeafElements = numberOfLeaafElements;
	}
	
	public void increaseNumberOfLeafElements()
	{
		numberOfLeafElements++;
	}

	public int getNumberOfNonLeafElements() 
	{
		return numberOfNonLeafElements;
	}
	

	public void setNumberOfNonLeafElements(int numberOfNonLeafElements) 
	{
		this.numberOfNonLeafElements = numberOfNonLeafElements;
	}
	
	public void increaseNumberOfNonLeafElements()
	{
		numberOfNonLeafElements++;
	}

	public int getTotalLeaves() 
	{
		
		return totalLeaves;
	}
	

	public void setTotalLeaves(int totalLeaves) 
	{
		this.totalLeaves = totalLeaves;
	}
	
	public void increaseTotalLeaves(int i)
	{
		totalLeaves += i;
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
	
	public NodeIdentifier getElementMemberIdentifier()
	{
		if(hasElements())
		{
			return new NodeIdentifier("Elements","complexTypeMember",nodeIdentifier);
		}
		else return null;
	}

	public NodeIdentifier getAttributeMemberIdentifier()
	{
		if(hasAttributes())
		{
			return new NodeIdentifier("Attributes","complexTypeMember",nodeIdentifier);
		}
		else return null;
	}
	
	public void printComplexType()
	{
		System.out.println("*** ComplexType ***\n");
		//System.out.println("wsdl-file: ");
		System.out.println("targetnamespace: "+nameSpace);
		System.out.println("name: "+name);
		System.out.println("grouping: "+grouping);
		System.out.println("array: "+array);
		System.out.println("type of array: "+arrayType);
		System.out.println("** elements**\n");
		ListIterator<Element> enumeration = elements.listIterator();
		while (enumeration.hasNext())
		{
			Element element = enumeration.next();
			element.printElement();
		}
		System.out.println("***end of complexType***\n\n");
	}

	public boolean isEmpty() 
	{
		// TODO Auto-generated method stub
		return elements.isEmpty();
	}
}
