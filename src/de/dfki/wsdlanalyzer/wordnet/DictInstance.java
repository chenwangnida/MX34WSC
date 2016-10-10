package de.dfki.wsdlanalyzer.wordnet;



import java.io.FileInputStream;
import java.util.Iterator;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.relationship.Relationship;
import net.didion.jwnl.data.relationship.RelationshipFinder;
import net.didion.jwnl.data.relationship.RelationshipList;
import net.didion.jwnl.dictionary.Dictionary;

public class DictInstance 
{
	String propsFile;
	
	public DictInstance(String s)
	{
		propsFile = s;
		try 
		{
			// initialize JWNL (this must be done before JWNL can be used)
			JWNL.initialize(new FileInputStream(propsFile));
			
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
			System.exit(-1);
		}
	}
	
	public int getCommonSynonyms(String requirements, String candidate) throws JWNLException
	{
		//System.out.println("\n+++commonsynonyms+++\n"+requirements+", "+candidate);
		int depth = 100;
		int requirementsIndex = 0;
		int candidateIndex = 0;
		//get IndexWord for noun and verb POS of requirements and candidate
		IndexWord requirementsNoun = Dictionary.getInstance().lookupIndexWord(POS.NOUN, requirements);
		IndexWord requirementsVerb = Dictionary.getInstance().lookupIndexWord(POS.VERB, requirements);
		IndexWord candidateNoun = Dictionary.getInstance().lookupIndexWord(POS.NOUN, candidate);
		IndexWord candidateVerb = Dictionary.getInstance().lookupIndexWord(POS.VERB, candidate);
		
		
		//System.out.println("requirementslemma: "+requirementsNoun.getLemma());
		//System.out.println("candidateLemma: "+candidateNoun.getLemma());
		//System.out.println("requirementssensecount: "+requirementsNoun.getSenseCount());
		
		if(requirementsNoun != null && candidateNoun != null)
		{
			for(int i=1;i<=requirementsNoun.getSenseCount();i++)
			{
				for(int j=1;j<=candidateNoun.getSenseCount();j++)
				{
					//System.out.println("requirementssense:"+requirementsNoun.getSense(i).toString());
					//System.out.println("candidatesense:"+candidateNoun.getSense(j).toString());
					RelationshipList list = RelationshipFinder.getInstance().findRelationships(requirementsNoun.getSense(i), candidateNoun.getSense(j), PointerType.SIMILAR_TO);
					if(!list.isEmpty())
					{
						//printRelationshipList(list);
						Relationship shallowest = list.getShallowest();
						//System.out.println("\nshallowest: requirements "+shallowest.getSourcePointerTarget().toString());
						//System.out.println("\nshallowest: candidate "+shallowest.getTargetPointerTarget().toString());
						//System.out.println("depth: "+shallowest.getDepth());
						if(shallowest.getDepth()<depth)
						{
							depth = shallowest.getDepth();
							requirementsIndex = i;
							candidateIndex = j;
						}
					}
				}
			}
			//System.out.println("\n result: depth="+depth+" requirementsindex="+requirementsIndex+" candidateindex="+candidateIndex);
			return depth;
		}
		return depth;			
	}
		
	public int getHypernymRelation(String requirements, String candidate) throws JWNLException
	{
		//System.out.println("\n+++HypernymRelation+++\n");
		int depth = 100;
		int requirementsIndex = 0;
		int candidateIndex = 0;
		//get IndexWord for noun and verb POS of requirements and candidate
		IndexWord requirementsNoun = Dictionary.getInstance().lookupIndexWord(POS.NOUN, requirements);
		IndexWord requirementsVerb = Dictionary.getInstance().lookupIndexWord(POS.VERB, requirements);
		IndexWord candidateNoun = Dictionary.getInstance().lookupIndexWord(POS.NOUN, candidate);
		IndexWord candidateVerb = Dictionary.getInstance().lookupIndexWord(POS.VERB, candidate);
		//System.out.println("requirementslemma: "+requirementsNoun.getLemma());
		//System.out.println("candidateLemma: "+candidateNoun.getLemma());
		if(requirementsNoun != null && candidateNoun != null)
		{
			for(int i=1;i<=requirementsNoun.getSenseCount();i++)
			{
				for(int j=1;j<=candidateNoun.getSenseCount();j++)
				{
					//System.out.println("requirementssense:"+requirementsNoun.getSense(i).toString());
					//System.out.println("candidatesense:"+candidateNoun.getSense(j).toString());
					RelationshipList list = RelationshipFinder.getInstance().findRelationships(requirementsNoun.getSense(i), candidateNoun.getSense(j), PointerType.HYPERNYM);
					if(!list.isEmpty())
					{
						//printRelationshipList(list);
						Relationship shallowest = list.getShallowest();
						//System.out.println("\nshallowest: requirements "+shallowest.getSourcePointerTarget().toString());
						//System.out.println("\nshallowest: candidate "+shallowest.getTargetPointerTarget().toString());
						//System.out.println("depth: "+shallowest.getDepth());
						if(shallowest.getDepth()<depth)
						{
							depth = shallowest.getDepth();
							requirementsIndex = i;
							candidateIndex = j;
						}
					}
				}
			}
			//System.out.println("\n result: depth="+depth+" requirementsindex="+requirementsIndex+" candidateindex="+candidateIndex);
		}
		return depth;
					
	}
	
	public int getDerivedRelation(String requirements, String candidate) throws JWNLException
	{
		System.out.println("\n+++DerivedRelation+++\n");
		int depth = 100;
		int requirementsIndex = 0;
		int candidateIndex = 0;
		//get IndexWord for noun and verb POS of requirements and candidate
		IndexWord requirementsNoun = Dictionary.getInstance().getIndexWord(POS.NOUN, requirements);
		IndexWord requirementsVerb = Dictionary.getInstance().getIndexWord(POS.VERB, requirements);
		IndexWord candidateNoun = Dictionary.getInstance().getIndexWord(POS.NOUN, candidate);
		IndexWord candidateVerb = Dictionary.getInstance().getIndexWord(POS.VERB, candidate);
		//System.out.println("requirementslemma: "+requirementsNoun.getLemma());
		//System.out.println("candidateLemma: "+candidateNoun.getLemma());
		for(int i=1;i<=requirementsNoun.getSenseCount();i++)
		{
			for(int j=1;j<=candidateNoun.getSenseCount();j++)
			{
				//System.out.println("requirementssense:"+requirementsNoun.getSense(i).toString());
				//System.out.println("candidatesense:"+candidateNoun.getSense(j).toString());
				RelationshipList list = RelationshipFinder.getInstance().findRelationships(requirementsNoun.getSense(i), candidateNoun.getSense(j), PointerType.DERIVED);
				if(!list.isEmpty())
				{
					//printRelationshipList(list);
					Relationship shallowest = list.getShallowest();
					//System.out.println("\nshallowest: requirements "+shallowest.getSourcePointerTarget().toString());
					//System.out.println("\nshallowest: candidate "+shallowest.getTargetPointerTarget().toString());
					//System.out.println("depth: "+shallowest.getDepth());
					if(shallowest.getDepth()<depth)
					{
						depth = shallowest.getDepth();
						requirementsIndex = i;
						candidateIndex = j;
					}
				}
			}
		}
		System.out.println("\n result: depth="+depth+" requirementsindex="+requirementsIndex+" candidateindex="+candidateIndex);
		return depth;
					
	}
	
	public int getCategoryRelation(String requirements, String candidate) throws JWNLException
	{
		System.out.println("\n+++CategoryRelation+++\n");
		int depth = 100;
		int requirementsIndex = 0;
		int candidateIndex = 0;
		//get IndexWord for noun and verb POS of requirements and candidate
		IndexWord requirementsNoun = Dictionary.getInstance().getIndexWord(POS.NOUN, requirements);
		IndexWord requirementsVerb = Dictionary.getInstance().getIndexWord(POS.VERB, requirements);
		IndexWord candidateNoun = Dictionary.getInstance().getIndexWord(POS.NOUN, candidate);
		IndexWord candidateVerb = Dictionary.getInstance().getIndexWord(POS.VERB, candidate);
		//System.out.println("requirementslemma: "+requirementsNoun.getLemma());
		//System.out.println("candidateLemma: "+candidateNoun.getLemma());
		for(int i=1;i<=requirementsNoun.getSenseCount();i++)
		{
			for(int j=1;j<=candidateNoun.getSenseCount();j++)
			{
				//System.out.println("requirementssense:"+requirementsNoun.getSense(i).toString());
				//System.out.println("candidatesense:"+candidateNoun.getSense(j).toString());
				RelationshipList list = RelationshipFinder.getInstance().findRelationships(requirementsNoun.getSense(i), candidateNoun.getSense(j), PointerType.CATEGORY);
				if(!list.isEmpty())
				{
					printRelationshipList(list);
					Relationship shallowest = list.getShallowest();
					System.out.println("\nshallowest: requirements "+shallowest.getSourcePointerTarget().toString());
					System.out.println("\nshallowest: candidate "+shallowest.getTargetPointerTarget().toString());
					System.out.println("depth: "+shallowest.getDepth());
					if(shallowest.getDepth()<depth)
					{
						depth = shallowest.getDepth();
						requirementsIndex = i;
						candidateIndex = j;
					}
				}
			}
		}
		System.out.println("\n result: depth="+depth+" requirementsindex="+requirementsIndex+" candidateindex="+candidateIndex);
		return depth;
					
	}
	
	private void printRelationshipList(RelationshipList l)
	{
		System.out.println("\n++ RelationshipList ++\n");
		for(Iterator relationIterator = l.iterator();relationIterator.hasNext();)
		{
			((Relationship) relationIterator.next()).getNodeList().print();
		}
		System.out.println("\n");
	}
}
