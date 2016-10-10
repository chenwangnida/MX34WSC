package de.dfki.wsdlanalyzer.types;

import java.util.Iterator;





/**
 * representation of wsdl-file through lists of its complex 
 * types, its messages,  porttypes, bindings, services
 */
public class WsdlFile 
{
	//list of elements form wsdlfile
	private ElementList elementlist;
	//list of complex types from wsdl file
	private TypeList typelist;
	//list of messages from wsdl file
	private MessageList messagelist;
	//list of opeations from wsdl file
	private OperationList operationlist;
	//list of porttypes from wsdl file
	private PortTypeList porttypelist;
	//list of bindings
	private BindingList bindings;
	//list of services from wsdl file
	private ServiceList servicelist;
	//wsdl-filename
	private String wsdlfilename;
	//matching score
	private int score;
	//targetnamespace
	private String targetNameSpace;
	
	public WsdlFile(String s)
	{
		wsdlfilename = s;
		elementlist = new ElementList();
		typelist = new TypeList();
		messagelist = new MessageList();
		operationlist = new OperationList();
		porttypelist = new PortTypeList();
		bindings = new BindingList();
		servicelist = new ServiceList();
	}
	
	/*public WsdlFile(ComplexType t)
	{
		typelist = new Vector<ComplexType>();
		typelist.add(t);
	}*/
	
	public void setWsdlFileName(String s)
	{
		wsdlfilename = s;
	}
	
	public String getWsdlFileName()
	{
		return wsdlfilename;
	}
	
	public TypeList getTypeList()
	{
		return typelist;
	}
	
	public MessageList getMessageList()
	{
		return messagelist;
	}
	
	public void setScore(int i)
	{
		score = i;
	}
	
	public int getScore()
	{
		return score;
	}

	public OperationList getOperationlist() 
	{
		return operationlist;
	}

	public PortTypeList getPorttypelist() 
	{
		return porttypelist;
	}

	public BindingList getBindings() 
	{
		return bindings;
	}

	public ServiceList getServicelist() 
	{
		return servicelist;
	}

	public ElementList getElementlist() 
	{
		return elementlist;
	}

	public String getTargetNameSpace() 
	{
		return targetNameSpace;
	}
	

	public void setTargetNameSpace(String targetNameSpace) 
	{
		this.targetNameSpace = targetNameSpace;
	}
	
	public String log()
	{
		String wsdlLog = "\nWsdlFile: "+wsdlfilename;
		for(Iterator<Service> serviceIterator = servicelist.serviceIterator();serviceIterator.hasNext();)
		{
			Service service = serviceIterator.next();
			wsdlLog += ("\n"+service.log());
		}
		return wsdlLog;
	}
	

}
