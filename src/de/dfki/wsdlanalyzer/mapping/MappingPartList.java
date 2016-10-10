package de.dfki.wsdlanalyzer.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * representation of list of mappings that build 
 * the mapping of a higher level object
 * 
 * e.g the list of operation-mappings that form a porttype-mapping
 */

public class MappingPartList implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	/**
	 * ArrayList of "sub"-Mappings which builds higher level Mapping
	 */
	private ArrayList<Mapping> mappingPartList;
	private int score;
	
	public MappingPartList()
	{
		mappingPartList = new ArrayList<Mapping>();
		score = 0;
	}
	
	public int getScore() 
	{
		return score;
	}
	

	public void setScore(int score)
	{
		this.score = score;
	}
	

	/**
	 * add a Mapping to the mappingPartList
	 * @param mapping Mapping to add
	 */
	public void addMapping(Mapping mapping)
	{
		//System.out.println("score: "+mapping.getScore());
		score = score+mapping.getScore();
		mappingPartList.add(mapping);
	}
	
	public Iterator<Mapping> mappingIterator()
	{
		return mappingPartList.iterator();
	}
	
	public Mapping get(int arg0) 
	{
		// TODO Auto-generated method stub
		return mappingPartList.get(arg0);
	}

	public boolean isEmpty() 
	{
		// TODO Auto-generated method stub
		return mappingPartList.isEmpty();
	}

	public int size() 
	{
		// TODO Auto-generated method stub
		return mappingPartList.size();
	}
	
	public MappingPartList copy()
	{
		MappingPartList copy = new MappingPartList();
		for(Iterator<Mapping> mappingIterator = mappingPartList.iterator();mappingIterator.hasNext();)
		{
			copy.addMapping(mappingIterator.next());
		}
		return copy;
	}
	
	/**
	 * look for the first mapping that contains
	 * name as CandidateName
	 * @param name candidate-name to look for
	 * @return mapping containing name as candidateName
	 */
	public Mapping getMappingForCandidate(String name)
	{
		if(isEmpty())
		{
			return null;
		}
		else
		{
			for(int i=0;i<mappingPartList.size();i++)
			{
				Mapping mapping = get(i).getMappingForCandidate(name);
				if(mapping != null)
					return mapping;
			}
			return null;
		}
	}
	
	/**
	 * look for the first mapping that contains
	 * name as RequirementName
	 * @param name requirement-name to look for
	 * @return mapping containing name as requirementName
	 */
	public Mapping getMappingForRequirement(String name)
	{
		if(isEmpty())
		{
			return null;
		}
		else
		{
			for(int i=0;i<mappingPartList.size();i++)
			{
				Mapping mapping = get(i).getMappingForRequirement(name);
				if(mapping != null)
					return mapping;
			}
			return null;
		}
	}
	
	public void print(int i)
	{
		if(!mappingPartList.isEmpty())
		{
			for(int j=0;j<mappingPartList.size();j++)
			{
				Mapping mapping = mappingPartList.get(j);
				mapping.printMapping(i);
			}
		}
	}
	
	public String log(int i)
	{
		String mappingPartListLog = "";
		if(!mappingPartList.isEmpty())
		{
			for(int j=0;j<mappingPartList.size();j++)
			{
				Mapping mapping = mappingPartList.get(j);
				mappingPartListLog += ("\n"+mapping.log(i));
			}
		}
		return mappingPartListLog;
	}
}
