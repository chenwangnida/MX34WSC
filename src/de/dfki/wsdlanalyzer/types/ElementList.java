package de.dfki.wsdlanalyzer.types;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
/**
 * list of elements
 * key: elementname
 * value: element
 * @author Hans
 *
 */
public class ElementList 
{
	private HashMap<String,Element> elements;
	
	public ElementList()
	{
		elements = new HashMap<String,Element>();
	}

	public boolean containsKey(Object arg0) {
		// TODO Auto-generated method stub
		return elements.containsKey(arg0);
	}

	public Element get(Object arg0) {
		// TODO Auto-generated method stub
		return elements.get(arg0);
	}

	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return elements.keySet();
	}

	public Element put(String arg0, Element arg1) {
		// TODO Auto-generated method stub
		return elements.put(arg0, arg1);
	}

	public int size() {
		// TODO Auto-generated method stub
		return elements.size();
	}

	public Iterator<Element> elementIterator() {
		// TODO Auto-generated method stub
		return elements.values().iterator();
	}
}
