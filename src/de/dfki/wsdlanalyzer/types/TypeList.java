package de.dfki.wsdlanalyzer.types;


import java.util.HashMap;
import java.util.Iterator;

/*
 * class for representing the complex types defined in awsdl-file
 * uses the typename as key and the type itself as value
 * in the hashmap
 */
public class TypeList 
{
	//ist of complex types from wsdl file
	private HashMap<String,ComplexType> typelist;
	
	public TypeList()
	{
		typelist = new HashMap<String,ComplexType>();
	}
	
	public TypeList(ComplexType t)
	{
		typelist = new HashMap<String,ComplexType>();
		typelist.put(t.getName(),t);
	}
	
	public void insertComplexType(ComplexType t)
	{
		if (!typelist.containsKey(t.getName()))
		{
			typelist.put(t.getName(),t);
		}
		else
		{
			//TODO check for namespace 
		}
	}
	
	public void insertComplexElement(String elementname,ComplexType t)
	{
		typelist.put(elementname,t);
	}
	
	/*get index of type
	public int indexOf(ComplexType t)
	{
		Enumeration<ComplexType> e = typelist.elements();
		while (e.hasMoreElements())
		{
			ComplexType help = e.nextElement();
			int i = typelist.indexOf(help);
			if (help.equals(t))
				return i;
		}
		return -1;
	}*/
	
	/*get index of complextype of element with name s
	public int indexOf(String s)
	{
		//
		Enumeration e = typelist.elements();
		while (e.hasMoreElements())
		{
			ComplexType help = (ComplexType)e.nextElement();
			int i = typelist.indexOf(help);
			if (help.getName().equals(s))
				return i;
		}
		return -1;
	}*/
	
	/*get complextype with index i
	public ComplexType getComplexType(int i)
	{
		return typelist.elementAt(i);
		
	}*/
	
	public ComplexType getType(String s)
	{
		return typelist.get(s);
	}
	
	public boolean isEmpty()
	{
		return typelist.isEmpty();
	}
	
	public Iterator<ComplexType> typeIterator()
	{
		return typelist.values().iterator();
	}
	
	public Iterator<String> nameIterator()
	{
		return typelist.keySet().iterator();
	}
	
	public HashMap<String,ComplexType> getTypeList()
	{
		return typelist;
	}

	public boolean containsKey(String arg0) {
		// TODO Auto-generated method stub
		return typelist.containsKey(arg0);
	}

	public boolean containsValue(Object arg0) {
		// TODO Auto-generated method stub
		return typelist.containsValue(arg0);
	}
	
	public void printList()
	{
		System.out.println("\ntypelist\n");
		for(Iterator<String> types = typelist.keySet().iterator();types.hasNext();)
		{
			ComplexType t = typelist.get(types.next());
			System.out.println("type: "+t.getName()+"\n");
			t.printComplexType();
			//System.out.println("leafelements: "+t.getNumberOfLeafElements());
			//System.out.println("nonleafelements: "+t.getNumberOfNonLeafElements());
			//System.out.println("leaves: "+t.getTotalLeaves()+"\n");
		}
	}
	
	
}
