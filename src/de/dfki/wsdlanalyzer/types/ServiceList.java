package de.dfki.wsdlanalyzer.types;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
/**
 * Hashmap with servicename as key and service as value
 * contains all services defined in wsdl-doc
 * @author Hans
 *
 */
public class ServiceList 
{
	//list of services from wsdl file
	private HashMap<String,Service> servicelist;
	//wsdl-filename
	private String wsdlfilename;
	//matching score
	private int score;
	
	public ServiceList()
	{
		servicelist = new HashMap<String,Service>();
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

	public HashMap<String, Service> getServicelist() {
		return servicelist;
	}
	

	public void setServicelist(HashMap<String, Service> servicelist) {
		this.servicelist = servicelist;
	}

	public void clear() {
		// TODO Auto-generated method stub
		servicelist.clear();
	}

	public Object clone() {
		// TODO Auto-generated method stub
		return servicelist.clone();
	}

	public boolean containsKey(Object arg0) {
		// TODO Auto-generated method stub
		return servicelist.containsKey(arg0);
	}

	public boolean containsValue(Object arg0) {
		// TODO Auto-generated method stub
		return servicelist.containsValue(arg0);
	}

	public Set<Entry<String, Service>> entrySet() {
		// TODO Auto-generated method stub
		return servicelist.entrySet();
	}

	public Service get(Object arg0) {
		// TODO Auto-generated method stub
		return servicelist.get(arg0);
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return servicelist.isEmpty();
	}

	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return servicelist.keySet();
	}

	public Service put(String arg0, Service arg1) {
		// TODO Auto-generated method stub
		return servicelist.put(arg0, arg1);
	}

	public void putAll(Map<? extends String, ? extends Service> arg0) {
		// TODO Auto-generated method stub
		servicelist.putAll(arg0);
	}

	public Service remove(Object arg0) {
		// TODO Auto-generated method stub
		return servicelist.remove(arg0);
	}

	public int size() {
		// TODO Auto-generated method stub
		return servicelist.size();
	}

	public Iterator<Service> serviceIterator() {
		// TODO Auto-generated method stub
		return servicelist.values().iterator();
	}
	
	public void printList()
	{
		System.out.println("\nservicelist\n");
		for(Iterator<String> iterator = servicelist.keySet().iterator();iterator.hasNext();)
		{
			System.out.println("service: "+iterator.next());
		}
	}
	
	
	
}
