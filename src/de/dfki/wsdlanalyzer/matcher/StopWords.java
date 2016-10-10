package de.dfki.wsdlanalyzer.matcher;

import java.util.HashSet;

/**
 * list of "Stopwords" which can/should be discarded when
 * comparing the NameTokens
 * (can be modified)
 * @author Hans
 *
 */
public class StopWords 
{
	private HashSet<String> stopwords;
	
	public StopWords()
	{
		stopwords = new HashSet<String>();
		stopwords.add("soap");
		stopwords.add("http");
		stopwords.add("in");
		stopwords.add("out");
		stopwords.add("get");
		stopwords.add("post");
		stopwords.add("and");
		stopwords.add("by");
		stopwords.add("body");
		//stopwords.add("result");
		//stopwords.add("response");
		stopwords.add("array");
		stopwords.add("for");
	}

	public boolean contains(String arg0) 
	{
		// TODO Auto-generated method stub
		return stopwords.contains(arg0);
	}
}
