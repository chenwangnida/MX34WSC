package de.dfki.wsdlanalyzer.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Set;
import java.util.Map.Entry;

import de.dfki.wsdlanalyzer.matcher.NameTokens;


/**
 * representation of a wsdl-service by a Hashmap containing 
 * the portnames as keys and the ports as value
 * @author Hans
 *
 */
public class Service 
{
	private String name;
	//Hashmap containing the portnames as keys 
	//and the ports as value
	private HashMap<String,Port> ports;
	private NameTokens token;
	//	NodeIdentifier for visualization
	NodeIdentifier nodeIdentifier;
	
	public Service(String s)
	{
		name = s;
		ports = new HashMap<String,Port>();
		nodeIdentifier = new NodeIdentifier();
	}
	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}

	public HashMap<String, Port> getPorts() 
	{
		return ports;
	}
	

	public void setPorts(HashMap<String, Port> ports) 
	{
		this.ports = ports;
	}

	public void clear() 
	{
		// TODO Auto-generated method stub
		ports.clear();
	}

	public Object clone() 
	{
		// TODO Auto-generated method stub
		return ports.clone();
	}

	public boolean containsKey(Object arg0) 
	{
		// TODO Auto-generated method stub
		return ports.containsKey(arg0);
	}

	public boolean containsValue(Object arg0)
	{
		// TODO Auto-generated method stub
		return ports.containsValue(arg0);
	}

	public Set<Entry<String, Port>> entrySet() 
	{
		// TODO Auto-generated method stub
		return ports.entrySet();
	}

	public Port get(Object arg0) 
	{
		// TODO Auto-generated method stub
		return ports.get(arg0);
	}

	public boolean isEmpty() 
	{
		// TODO Auto-generated method stub
		return ports.isEmpty();
	}

	public Set<String> keySet() 
	{
		// TODO Auto-generated method stub
		return ports.keySet();
	}

	public Port put(String arg0, Port arg1)
	{
		// TODO Auto-generated method stub
		return ports.put(arg0, arg1);
	}

	

	public Port remove(Object arg0)
	{
		// TODO Auto-generated method stub
		return ports.remove(arg0);
	}

	public int size() 
	{
		// TODO Auto-generated method stub
		return ports.size();
	}

	public Collection<Port> values() 
	{
		// TODO Auto-generated method stub
		return ports.values();
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
	
	public void print()
	{
		for(Iterator<String> iterator = ports.keySet().iterator();iterator.hasNext();)
		{
			Port port = ports.get(iterator.next());
			System.out.println("port: "+port.getName());
			System.out.println("binding: "+port.getBinding());
			System.out.println("endpoint: "+port.getAddress().toString());
		}
	}
	
	public String log()
	{
		String serviceLog = "Service: "+name+"\n";
		for(Iterator<String> iterator = ports.keySet().iterator();iterator.hasNext();)
		{
			Port port = ports.get(iterator.next());
			serviceLog += ("port: "+port.getName()+"\n");
			serviceLog += ("binding: "+port.getBinding()+"\n");
			serviceLog += ("endpoint: "+port.getAddress().toString()+"\n");
		}
		return serviceLog;
	}
	
}
