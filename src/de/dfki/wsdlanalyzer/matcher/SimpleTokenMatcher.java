package de.dfki.wsdlanalyzer.matcher;

import java.util.Iterator;

/**
 * matches two wsdlnames by counting the tokens
 * they have in common
 */
public class SimpleTokenMatcher
{
	private int score;
	private int maxscore = 10;
	private NameTokens requirementsTokens,candidateTokens;
	
	public SimpleTokenMatcher(NameTokens requirements,NameTokens candidate)
	{
		requirementsTokens = requirements;
		candidateTokens = candidate;
		score = 0;
	}
	
	public int match()
	{
		for(Iterator<String> candidateiterator = candidateTokens.iterator();candidateiterator.hasNext();)
		{
			if(requirementsTokens.contains(candidateiterator.next().toLowerCase()))
			{
				score++;
			}
		}
		return score*maxscore;
	}
}
