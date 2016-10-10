package de.dfki.wsdlanalyzer.types;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * class representing the porttypes defined in awsdl file
 * uses porttypename as key and porttype as value
 */
public class PortTypeList 
{
	//	list of porttypes from wsdl file
	private HashMap<String,PortType> porttypelist;
	//wsdl-filename
	private String wsdlfilename;
	//matching score
	private int score;
	
	public PortTypeList()
	{
		porttypelist = new HashMap<String,PortType>();
	}

	public int getScore() {
		return score;
	}
	

	public void setScore(int score) {
		this.score = score;
	}

	public String getWsdlfilename() {
		return wsdlfilename;
	}
	

	public void setWsdlfilename(String wsdlfilename) {
		this.wsdlfilename = wsdlfilename;
	}

	
	

	public void setPorttypelist(HashMap<String, PortType> porttypelist) {
		this.porttypelist = porttypelist;
	}

	public void clear() {
		// TODO Auto-generated method stub
		porttypelist.clear();
	}

	public Object clone() {
		// TODO Auto-generated method stub
		return porttypelist.clone();
	}

	public boolean containsKey(Object arg0) {
		// TODO Auto-generated method stub
		return porttypelist.containsKey(arg0);
	}

	public boolean containsValue(Object arg0) {
		// TODO Auto-generated method stub
		return porttypelist.containsValue(arg0);
	}

	public Set<Entry<String, PortType>> entrySet() {
		// TODO Auto-generated method stub
		return porttypelist.entrySet();
	}

	public PortType get(Object arg0) {
		// TODO Auto-generated method stub
		return porttypelist.get(arg0);
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return porttypelist.isEmpty();
	}

	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return porttypelist.keySet();
	}

	public PortType put(String arg0, PortType arg1) {
		// TODO Auto-generated method stub
		return porttypelist.put(arg0, arg1);
	}

	public void putAll(Map<? extends String, ? extends PortType> arg0) {
		// TODO Auto-generated method stub
		porttypelist.putAll(arg0);
	}

	public PortType remove(Object arg0) {
		// TODO Auto-generated method stub
		return porttypelist.remove(arg0);
	}

	public int size() {
		// TODO Auto-generated method stub
		return porttypelist.size();
	}

	public Iterator<PortType> portTypeIterator() {
		// TODO Auto-generated method stub
		return porttypelist.values().iterator();
	}
	
	public void printList()
	{
		System.out.println("\nporttypelist\n");
		for(Iterator<String> iterator = porttypelist.keySet().iterator();iterator.hasNext();)
		{
			System.out.println("porttype: "+iterator.next());
		}
	}

	public PortType get(String arg0) 
	{
		// TODO Auto-generated method stub
		return porttypelist.get(arg0);
	}
	
	
	
	
}
