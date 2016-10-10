
package de.dfki.wsdlanalyzer.mapping;


import java.io.Serializable;
import java.util.ArrayList;

import java.util.Iterator;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import de.dfki.wsdlanalyzer.parser.WsdlFileParser;
import de.dfki.wsdlanalyzer.types.NodeIdentifier;







/**
 * class representing the 'mapping' of two 'high level' objects
 * such as porttype,operation and complextype
 * mapping consists of the names of the two hihg level objects
 * requirementsname and targetname the score of the mapping which
 * is the sum of the scores of the contributing matches 
 * and a arraylist of alternative mappingpartlists containing
 * these matches
 * the keys of the double hashmap are the names of the objects
 * that build the match value
 *
 * e.g.
 * 
 * a mapping for two porttypes "porttypeA" and "porttypeB"
 * has requirementsname: porttypeA , targetname: porttypeB
 * and in the mappingPartLists there are alternative Mappings of operations for an operation
 * of porttypeA and operations of porttypeB that form the porttype-mapping
 
 */
public class Mapping implements Serializable
{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * names of the entities which are mapped
	 */
	private String requirementsName,candidateName;
	/**
	 * wsdl-path of the mapped entities (for visualization only)
	 */
	private NodeIdentifier requirementsIdentifier,candidateIdentifier;
	/**
	 * list of alternative MappingPartLists of subentities which build the
	 * higher level mapping
	 */ 
	private ArrayList<MappingPartList> mappingPartLists;
	/** 
	 * matching-score of the mapping
	 */ 
	private int score;
	private int size;
	/**
	 * flag indicating whether computaion of a mapping
	 * is possible or if all mappings have the same score 
	 */
	private boolean anymatch;
	/**
	 * min/maxoccurs for requirement and candidate
	 * (only used for transformation of messages, can be ignored otherwise)
	 */
	private int requirementminoccur,requirementmaxoccur,candidateminoccur,candidatemaxoccur;
	
	
	
	/**
	 * unparameterized constructor
	 * creates empty unnamed mapping
	 */
	 public Mapping()
	 {
		mappingPartLists = new ArrayList<MappingPartList>();
		score = 0;
		size = 0;
		anymatch = false;
		/*
		 * set min/maxoccurs to default
		 */
		requirementminoccur = 1;
		requirementmaxoccur = 1;
		candidateminoccur = 1;
		candidatemaxoccur = 1;
	 }
	 
	 /**
	  * contstructor with requirements and candidate name
	  * creates empty named mapping
	  * @param requirements name of requirementsobject
	  * @param candidate name of candidateobject
	  */
	 public Mapping(String requirements,String candidate)
	 {
		requirementsName = requirements;
		candidateName = candidate;
		requirementsIdentifier = null;
		candidateIdentifier = null;
		mappingPartLists = new ArrayList<MappingPartList>();
		score = 0;
		size = 0;
		anymatch = false;
		/*
		 * set min/maxoccurs to default
		 */
		requirementminoccur = 1;
		requirementmaxoccur = 1;
		candidateminoccur = 1;
		candidatemaxoccur = 1;
	 }
	 
	 /**
	  * contstructor with requirements and candidate name and their nodeidentifiers
	  * creates empty named mapping
	  * @param requirements name of requirementsobject
	  * @param candidate name of candidateobject
	  * @param requirementsid nodeidentifier of requirements
	  * @param candidateid nodeidentifier of candidate	
	  */
	 public Mapping(String requirements,String candidate,NodeIdentifier requirementsId,NodeIdentifier candidateId)
	 {
		requirementsName = requirements;
		candidateName = candidate;
		requirementsIdentifier = requirementsId;
		candidateIdentifier = candidateId;
		mappingPartLists = new ArrayList<MappingPartList>();
		score = 0;
		size = 0;
		anymatch = false;
		/*
		 * set min/maxoccurs to default
		 */
		requirementminoccur = 1;
		requirementmaxoccur = 1;
		candidateminoccur = 1;
		candidatemaxoccur = 1;
	 }
	
	/*
	 * getter- and setter-methods
	 */
	 public int getScore()
	{
		return score;
	}

	public void setScore(int value)
	{
		score = value;
	}
	 
	public int getSize() 
	{
		return size;
	}

	public boolean isAnymatch() 
	{
		return anymatch;
	}
	

	public void setAnymatch(boolean anymatch) 
	{
		this.anymatch = anymatch;
	}
	

	public int getCandidatemaxoccur() 
	{
		return candidatemaxoccur;
	}
	

	public void setCandidatemaxoccur(int candidatemaxoccur) 
	{
		this.candidatemaxoccur = candidatemaxoccur;
	}
	

	public int getCandidateminoccur() 
	{
		return candidateminoccur;
	}
	

	public void setCandidateminoccur(int candidateminoccur) 
	{
		this.candidateminoccur = candidateminoccur;
	}
	

	public int getRequirementmaxoccur() 
	{
		return requirementmaxoccur;
	}
	

	public void setRequirementmaxoccur(int requirementmaxoccur) 
	{
		this.requirementmaxoccur = requirementmaxoccur;
	}
	

	public int getRequirementminoccur() 
	{
		return requirementminoccur;
	}
	

	public void setRequirementminoccur(int requirementminoccur) 
	{
		this.requirementminoccur = requirementminoccur;
	}
	

	public String getRequirementsName() 
	{
		return requirementsName;
	}
	

	public void setRequirementsName(String requirementsName) 
	{
		this.requirementsName = requirementsName;
	}
	

	public String getCandidateName() 
	{
		return candidateName;
	}
	

	public void setCandidateName(String candidateName) 
	{
		this.candidateName = candidateName;
	}
	
	public NodeIdentifier getRequirementsIdentifier()
	{
		return requirementsIdentifier;
	}
	

	public void setRequirementsIdentifier(NodeIdentifier requirementsIdentifier) 
	{
		this.requirementsIdentifier = requirementsIdentifier;
	}
	

	public NodeIdentifier getCandidateIdentifier() 
	{
		return candidateIdentifier;
	}
	

	public void setCandidateIdentifier(NodeIdentifier candidateIdentifier) 
	{
		this.candidateIdentifier = candidateIdentifier;
	}
	

	public ArrayList<MappingPartList> getMappingPartLists() 
	{
		return mappingPartLists;
	}
	

	public void setMappingPartLists(ArrayList<MappingPartList> mappingPartLists) 
	{
		score = mappingPartLists.get(0).getScore();
		this.mappingPartLists = mappingPartLists;
	}
	
	/**
	 * add a mappingpartlist to mapping 
	 * if the the score of the new mappingpartlist is greater then the old one
	 * the old one is deleted before insertion
	 * if the new score is equal to the old one the new mappingpartlist is added
	 * as alternative
	 * if the new score is smaller then the old one the new mappingpartlist is
	 * discarded
	 * @param list MappingPartList to insert
	 */
	public boolean addMappingPartList(MappingPartList list)
	{
		//new MappingPartList has greater score then old ones =>add
		if(score < list.getScore())
		{
			mappingPartLists.clear();
			score = list.getScore();
			return mappingPartLists.add(list);
		}
		//same score => add
		else if(score == list.getScore())
		{
			return mappingPartLists.add(list);
		}
		//smaller score => do nothing
		else return false;
	}

	public MappingPartList get(int arg0) 
	{
		// TODO Auto-generated method stub
		return mappingPartLists.get(arg0);
	}
	
	public Iterator<MappingPartList> mappingPartListIterator()
	{
		return mappingPartLists.iterator();
	}

	public boolean isEmpty() 
	{
		// TODO Auto-generated method stub
		return mappingPartLists.isEmpty();
	}
	
	public void addScoreBonus(int bonus)
	{
		score = score+bonus;
	}
	
	/**
	 * look for the first mapping that contains
	 * name as candidateName
	 * @param name candidate-name to look for
	 * @return mapping containing name as candidateName
	 */
	public Mapping getMappingForCandidate(String name)
	{
		if(candidateName.equals(name))
		{
			return this;
		}
		else
		{
			for(int i=0;i<mappingPartLists.size();i++)
			{
				MappingPartList mappingPartList = get(i);
				Mapping mapping = mappingPartList.getMappingForCandidate(name);
				if(mapping != null)
					return mapping;
			}
			return null;
		}
	}
	
	/**
	 * look for the first mapping that contains
	 * name as requirementName
	 * @param name requirement-name to look for
	 * @return mapping containing name as requirementName
	 */
	public Mapping getMappingForRequirement(String name)
	{
		if(requirementsName.equals(name))
		{
			return this;
		}
		else
		{
			for(int i=0;i<mappingPartLists.size();i++)
			{
				MappingPartList mappingPartList = get(i);
				Mapping mapping = mappingPartList.getMappingForRequirement(name);
				if(mapping != null)
					return mapping;
			}
			return null;
		}
	}
	
	/**
	 * returns the 'last' layer of the mapping,
	 * whose mappingPartLists is empty
	 */
	public Mapping getElementMapping()
	{
		if(mappingPartLists.isEmpty())
		{
			return this;
		}
		else
		{
			MappingPartList mappingPartList = mappingPartLists.get(0);
			return mappingPartList.get(0).getElementMapping();
		}
	}
	
	public void printMapping(int i)
	{
		switch(i)
		{
		case 0: System.out.println("******* Files: *******\n");break;
		case 1: System.out.println("******* Services: *******\n");break;
		case 2: System.out.println("******* Ports: *******\n");break;
		case 3: System.out.println("******* Bindings: *******\n");break;
		case 4: System.out.println("******* PortTypes: *******\n");break;
		case 5: System.out.println("******* Operations: *******\n");break;
		case 6: System.out.println("******* Input/Output/Fault: *******\n");break;
		case 7: System.out.println("******* Messages: *******\n");break;
		case 8: System.out.println("******* Parameters: *******\n");break;
		default: System.out.println("******* Types: *******\n");break;
		}
		//System.out.println("Names: \n");
		System.out.println(requirementsName+" mapped to "+candidateName);
		if((requirementsIdentifier!=null)&&(candidateIdentifier!=null))
		{
			System.out.println("NodeIdentifier: ");
			System.out.println(requirementsIdentifier.toString()+", "+candidateIdentifier.toString());
		}
		System.out.println("Score: "+score+"\n");
		System.out.println("Number of alternatives: "+mappingPartLists.size()+"\n");
		if(!mappingPartLists.isEmpty())
		{
			MappingPartList mappingList = mappingPartLists.get(0);
			mappingList.print(i+1);
		}
		else
		{
			if(isAnymatch())
			{
				switch(i)
				{
				case 0: System.out.println("services anyMatch!!!\n");break;
				case 1: System.out.println("ports anyMatch!!!\n");break;
				case 2: System.out.println("bindings anyMatch!!!\n");break;
				case 3: System.out.println("portTypes anyMatch!!!\n");break;
				case 4: System.out.println("operations anyMatch!!!\n");break;
				case 5: System.out.println("messages anyMatch!!!\n");break;
				case 6: System.out.println("parameters anyMatch!!!\n");break;
				case 7: System.out.println("types anyMatch!!!\n");break;
				default: System.out.println("types anyMatch!!!\n");break;
				}
			}
		}
	}
	
	public void print()
	{
		System.out.println("\n###Mapping###\n");
		System.out.println("req: "+requirementsName+", id: "+requirementsIdentifier.toString());
		System.out.println("can: "+candidateName+", id: "+candidateIdentifier.toString());
	}
	
	public String log(int i)
	{
		//String mappingLog = "\nMapping of "+requirementsName+" and "+candidateName+"\n";
		String mappingLog = "";
		switch(i)
		{
		case 0: mappingLog += ("******* Files: *******\n");break;
		case 1: mappingLog += ("******* Services: *******\n");break;
		case 2: mappingLog += ("******* Ports: *******\n");break;
		case 3: mappingLog += ("******* Bindings: *******\n");break;
		case 4: mappingLog += ("******* PortTypes: *******\n");break;
		case 5: mappingLog += ("******* Operations: *******\n");break;
		case 6: mappingLog += ("******* Input/Output/Fault: *******\n");break;
		case 7: mappingLog += ("******* Messages: *******\n");break;
		case 8: mappingLog += ("******* Parameters: *******\n");break;
		default: mappingLog += ("******* Types: *******\n");break;
		}
		//System.out.println("Names: \n");
		mappingLog += (requirementsName+" mapped to "+candidateName);
		if((requirementsIdentifier!=null)&&(candidateIdentifier!=null))
		{
			mappingLog += ("\nNodeIdentifier: \n");
			mappingLog += ((requirementsIdentifier.toString()+", "+candidateIdentifier.toString())+"\n");
		}
		mappingLog += ("Score: "+score+"\n");
		mappingLog += ("Number of alternatives: "+mappingPartLists.size()+"\n");
		if(!mappingPartLists.isEmpty())
		{
			MappingPartList mappingList = mappingPartLists.get(0);
			mappingLog += mappingList.log(i+1);
		}
		else
		{
			if(isAnymatch())
			{
				switch(i)
				{
				case 0: mappingLog += ("services anyMatch!!!\n");break;
				case 1: mappingLog += ("ports anyMatch!!!\n");break;
				case 2: mappingLog += ("bindings anyMatch!!!\n");break;
				case 3: mappingLog += ("portTypes anyMatch!!!\n");break;
				case 4: mappingLog += ("operations anyMatch!!!\n");break;
				case 5: mappingLog += ("messages anyMatch!!!\n");break;
				case 6: mappingLog += ("parameters anyMatch!!!\n");break;
				case 7: mappingLog += ("types anyMatch!!!\n");break;
				default: mappingLog += ("types anyMatch!!!\n");break;
				}
			}
		}
		return mappingLog;
	}
}
