package de.dfki.wsdlanalyzer.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Set;
import java.util.Map.Entry;

/**
 * class representing the bindings defined in a wsdlfile
 * uses bindingname as key and binding as value
 * 
 */
public class BindingList 
{
	//Hashmap for bindings with bindingname as key 
	
	private HashMap<String,Binding> bindings;
	
	public BindingList()
	{
		bindings = new HashMap<String,Binding>();
	}

	public void clear() {
		// TODO Auto-generated method stub
		bindings.clear();
	}

	public Object clone() {
		// TODO Auto-generated method stub
		return bindings.clone();
	}

	public boolean containsKey(Object arg0) {
		// TODO Auto-generated method stub
		return bindings.containsKey(arg0);
	}

	public boolean containsValue(Object arg0) {
		// TODO Auto-generated method stub
		return bindings.containsValue(arg0);
	}

	public Set<Entry<String, Binding>> entrySet() {
		// TODO Auto-generated method stub
		return bindings.entrySet();
	}

	public Binding get(Object arg0) {
		// TODO Auto-generated method stub
		return bindings.get(arg0);
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return bindings.isEmpty();
	}

	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return bindings.keySet();
	}

	public Binding put(String arg0, Binding arg1) {
		// TODO Auto-generated method stub
		return bindings.put(arg0, arg1);
	}

	

	public Binding remove(Object arg0) {
		// TODO Auto-generated method stub
		return bindings.remove(arg0);
	}

	public int size() {
		// TODO Auto-generated method stub
		return bindings.size();
	}

	public Collection<Binding> values() {
		// TODO Auto-generated method stub
		return bindings.values();
	}
	
	public void printList()
	{
		System.out.println("\nbindinglist\n");
		for(Iterator<String> iterator = bindings.keySet().iterator();iterator.hasNext();)
		{
			System.out.println("binding: "+iterator.next());
		}
	}
}
