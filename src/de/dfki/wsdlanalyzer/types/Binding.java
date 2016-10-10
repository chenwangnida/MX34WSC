package de.dfki.wsdlanalyzer.types;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import de.dfki.wsdlanalyzer.matcher.NameTokens;







/**
 * class representing a wsdl-binding by its name and the name
 * of the implemented porttype
 * (very basic version)
 */
public class Binding 
{
	private String name,portType;
	private NameTokens token;
	/**
	 * bindingtype soap, http, ...
	 * 
	 */
	private String bindingType;
	private boolean bindingStyle;
	private String transport;
	/**
	 * Hashmap containing the operations of this binding
	 * operationname as key and BinddingOperation as value
	 * used for transformation of Soap-messages
	 */
	private HashMap<String,BindingOperation> operationList;
	//	NodeIdentifier for visualization
	NodeIdentifier nodeIdentifier;
	
	public Binding()
	{
		name = null;
		operationList = new HashMap<String,BindingOperation>();
		nodeIdentifier = new NodeIdentifier();
	}
	
	public Binding(String s)
	{
		name = s;
		operationList = new HashMap<String,BindingOperation>();
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
	

	public String getPortType() 
	{
		return portType;
	}
	

	public void setPortType(String portType) 
	{
		this.portType = portType;
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

	public boolean isRPCStyle() {
		return bindingStyle;
	}
	

	public void setRPCStyle(boolean bindingStyle) {
		this.bindingStyle = bindingStyle;
	}
	

	public String getBindingType() {
		return bindingType;
	}
	

	public void setBindingType(String bindingType) {
		this.bindingType = bindingType;
	}
	

	public String getTransport() {
		return transport;
	}
	

	public void setTransport(String transport) {
		this.transport = transport;
	}
	

	public HashMap<String, BindingOperation> getOperationList() {
		return operationList;
	}
	
	public BindingOperation addBindingOperation(String name,BindingOperation operation)
	{
		return operationList.put(name,operation);
	}
	
	public void print()
	{
		System.out.println("++Binding**\n");
		System.out.println("Name: "+name);
		System.out.println("PortType: "+portType);
		System.out.println("Type: "+bindingType);
		if(bindingStyle)
			System.out.println("Style: RPC");
		else
			System.out.println("Style: Document");
		System.out.println("Transport: "+transport);
		Set<String> kset = operationList.keySet();
		for(Iterator<String> kit = kset.iterator();kit.hasNext();)
		{
			String key = kit.next();
			BindingOperation op = operationList.get(key);
			op.print();
		}
		System.out.println("\n++End of Binding++");
	}
}
