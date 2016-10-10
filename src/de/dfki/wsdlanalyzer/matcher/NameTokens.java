package de.dfki.wsdlanalyzer.matcher;

import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * the list of the tokens of the name of a WSDL-construct
 */
public class NameTokens 
{
	HashSet<String> tokens;
	
	public NameTokens(String name,StopWords stop)
	{
		//create new arraylist
		tokens = new HashSet<String>();
		/*
		 * define Token as 
		 * single lowercase word
		 * or
		 * capital letter followed by an arbitrary number
		 * of lowercase letters or digits
		 */
		Pattern capitalLetter = Pattern.compile("(\\p{Lower}[\\p{Lower}\\d]+)|(\\p{Upper}{1}?[\\p{Lower}\\d]+)");
		//Pattern capitalLetter = Pattern.compile("\\p{Upper}{1}?[\\p{Lower}\\d]+");
		Matcher matcher = capitalLetter.matcher(name);
		if(!matcher.find())
		{
			//System.out.println("not found");
			tokens.add(name);
		}
		else
		{
			matcher.reset();
			while(matcher.find())
			{
				int start = matcher.start();
				int end= matcher.end();
				//get Token
				String token = matcher.group().toLowerCase();
				/*
				System.out.println("capital letter found at: "+start+", "+end);
				System.out.println("match: "+token);
				*/
				if(!stop.contains(token))
				{
					if(token.length() > 1)
					tokens.add(token);
				}
			}
		}
		
	}

	public HashSet<String> getTokens() 
	{
		return tokens;
	}
	
	public boolean contains(Object arg0) 
	{
		// TODO Auto-generated method stub
		return tokens.contains(arg0);
	}

	public boolean isEmpty() 
	{
		// TODO Auto-generated method stub
		return tokens.isEmpty();
	}

	public Iterator<String> iterator() 
	{
		// TODO Auto-generated method stub
		return tokens.iterator();
	}

	public void printTokens()
	{
		for(Iterator<String> siter = tokens.iterator();siter.hasNext();)
		{
			System.out.println("name-token: "+siter.next());
		}
	}

	public int size() 
	{
		// TODO Auto-generated method stub
		return tokens.size();
	}
	
}

