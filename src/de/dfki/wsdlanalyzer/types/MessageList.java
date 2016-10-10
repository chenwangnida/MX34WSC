package de.dfki.wsdlanalyzer.types;



import java.util.HashMap;
import java.util.Iterator;



/**
 * class representing the list of all messages of a wsdl-file
 * uses messagename as key and the message itself as value in
 * the hashmap
 */

public class MessageList 
{
	//list of messages from wsdl file
	private HashMap<String,Message> messagelist;
	
	//matching score
	private int score;
	
	public MessageList()
	{
		
		messagelist = new HashMap<String,Message>();
	}
	
		
	public void insertMessage(Message m)
	{
		messagelist.put(m.getName(),m);
	}
	
	
	
	//get messagee with name s
	public Message getMessage(String s)
	{
		return messagelist.get(s);
		
	}
	
	public boolean isEmpty()
	{
		return messagelist.isEmpty();
	}
	
	public Iterator<Message> messageIterator()
	{
		return messagelist.values().iterator();
	}
	
	public int length()
	{
		return messagelist.size();
	}
	
	public void setScore(int i)
	{
		score = i;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public void printList()
	{
		System.out.println("\nmessagelist\n");
		for(Iterator<String> messages = messagelist.keySet().iterator();messages.hasNext();)
		{
			System.out.println("message: "+messages.next());
		}
	}
	
	

}
