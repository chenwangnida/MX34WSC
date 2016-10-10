package de.dfki.wsdlanalyzer.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import de.dfki.wsdlanalyzer.mapping.Mapping;
import de.dfki.wsdlanalyzer.mapping.MappingPartList;
import de.dfki.wsdlanalyzer.matrix.ScoreMap;
import de.dfki.wsdlanalyzer.parser.SimpleTypeLookupTable;
import de.dfki.wsdlanalyzer.types.Attribute;
import de.dfki.wsdlanalyzer.types.ComplexType;
import de.dfki.wsdlanalyzer.types.Element;
import de.dfki.wsdlanalyzer.types.NodeIdentifier;
import de.dfki.wsdlanalyzer.types.Sequence;
import de.dfki.wsdlanalyzer.types.TypeList;
import de.dfki.wsdlanalyzer.types.WsdlFile;

import net.didion.jwnl.JWNLException;

/**
 * TODO VORLÄUFIG!!!!!
 * 
 * @author Patrick Kapahnke
 *
 */
public class WsdlStructuralTypeMatcher {

	private TypeList requirementsTypes,candidateTypes;
	private SimpleTypeLookupTable lookupTable;
	private int maxScore = 1;
	private int groupingBonus = 0;
	private int arrayBonus = 0;
	private int sequenceBonus = 0;
	//switch for tree-edit-distance-like matching
	private boolean edit;
		
	/**
	 * switch for strict/relaxed sequence matching
	 * relaxed means the order of the sequence is not respected
	 * i.e arbitrary permutation of the elements possible
	 */
	private boolean strictSequence;
		
	/**
	 * 
	 * @param requirementsfile WsdlFile of requirementsfile
	 * @param candidatefile WsdlFile of candidatefile
	 * @param l SympleTypeLookupTable for matching requirements- and candidatefile
	 */
	public WsdlStructuralTypeMatcher(WsdlFile requirementsfile, WsdlFile candidatefile, SimpleTypeLookupTable l)
	{
		//edit = true;
		edit = false;
		
		requirementsTypes = requirementsfile.getTypeList();
		candidateTypes = candidatefile.getTypeList();
		
		lookupTable = l;		
	}
	
	/**
	 * matches two complex types by building all possible combinations
	 * of their elements and their attributes
	 */
	public Mapping matchComplexTypes(String requirementsName,ComplexType requirementsType,String candidateName,ComplexType candidateType)
	{				
		Mapping typeMapping = new Mapping(requirementsType.getName(),candidateType.getName(),requirementsType.getNodeIdentifier(),candidateType.getNodeIdentifier());
		Mapping elementListMapping = null;
		Mapping attributeMapping = null;
		if((requirementsType != null)&&(candidateType != null))
		{
			int typeScore = 0;
				
			//test for attributes of complexType
			if((requirementsType.hasAttributes())&&(candidateType.hasAttributes()))
			{
				attributeMapping = matchAttributeLists(requirementsType,candidateType);
				//set NodeIdentifier
				NodeIdentifier requirementsAttributeIdentifier = new NodeIdentifier("Attributes","complexTypeMember",requirementsType.getNodeIdentifier());
				NodeIdentifier candidateAttributeIdentifier = new NodeIdentifier("Attributes","complexTypeMember",candidateType.getNodeIdentifier());
				attributeMapping.setRequirementsIdentifier(requirementsAttributeIdentifier);
				attributeMapping.setCandidateIdentifier(candidateAttributeIdentifier);
			}
			//test for specialcase array
			if(requirementsType.isArray())
			{
				if(candidateType.isArray())
				{
					//both types are arrays
					elementListMapping = matchArrayTypes(requirementsType,candidateType);
						
				}
				else
				{
					//one type array one not => score 0
					Mapping nullMapping = new Mapping(requirementsName,candidateName);
					nullMapping.setScore(0);
					return nullMapping;
				}
			}
			else
			{
					
				if(candidateType.isArray())
				{
					//one type array one not => score 0
					Mapping nullMapping = new Mapping(requirementsName,candidateName);
					nullMapping.setScore(0);
					return nullMapping;
				}
				else
				{
					//none of the types is array
					//ScoreMap typematrix = new ScoreMap(requirementstype.getElements().size(),candidatetype.getElements().size(),requirementsname,candidatename);
						
					if((requirementsType.getNumberOfNonLeafElements() == candidateType.getNumberOfNonLeafElements()-1)&&(requirementsType.getNumberOfLeafElements() > candidateType.getNumberOfLeafElements()+1))
					{
							
						/*
						 * TODO compute nonleafcandidate for "deleting" 
						 * using structure and/or wordnet/name
						 * 
						 * test candidate leafnumber against number of unmatched req.leaves
						 */
						if(requirementsType.getNumberOfLeafElements()!= candidateType.getNumberOfLeafElements())
						{
							if(requirementsType.getNumberOfNonLeafElements() != candidateType.getNumberOfNonLeafElements())
							{
								//System.out.println(requirementsName+" = "+candidateName+" : "+requirementsType.getTotalLeaves());
								//System.out.println("\n types with different leaves: "+(requirementsType.getNumberOfLeafElements()-candidateType.getNumberOfLeafElements())+", "+(requirementsType.getNumberOfNonLeafElements()-candidateType.getNumberOfNonLeafElements()));
							}
						}
					}
					//compare groupings
					if(requirementsType.getGrouping() == candidateType.getGrouping())
					{
						typeScore =  typeScore+groupingBonus;
					}
					/*
					 * TODO own method for <choice>
					 */
					if((requirementsType.getGrouping() == 1)&&(candidateType.getGrouping() == 1)&&strictSequence)
					{
						/*
						 * both types are sequences
						 */
						
						//System.out.println("\n complexTypematch "+requirementsname+", "+candidatename+"\n");
							
						//System.out.println("Sequences in "+requirementsType.getName()+" and "+candidateType.getName());
						//both types are sequences
						Sequence requirementssequence = generateSequence(requirementsType);
						Sequence candidatesequence = generateSequence(candidateType);
						//match requirements- and candidatesequence
						elementListMapping = matchSequences(requirementsType,requirementssequence,candidateType,candidatesequence);
						/*if(requirementsType.getName().equals("orderDateType"))
						{
							System.out.println("sequencemapping-test:");
							elementListMapping.printMapping(9);
						}*/

						//add boni to score
						elementListMapping.addScoreBonus(typeScore);
						//return typeMapping;
							
					}
					else
					{
						//test for empty complextypes
						if((!requirementsType.isEmpty())&&(!candidateType.isEmpty()))
						{					
							//atleast one type is no sequence => order can be neclected
						
								
							//create new scoremap for requirements- and candidatetype
							ScoreMap typeMatrix = new ScoreMap(requirementsType.getElementList().size(),candidateType.getElementList().size(),requirementsName,candidateName, edit);
							//iterate over elements of requirementstype
							for(ListIterator<Element> requirementselements = requirementsType.getElementList().listIterator();requirementselements.hasNext();)
							{
								Element requirementselement = requirementselements.next();
								//iterate over elements of candidatetype
								for(ListIterator<Element> candidateelements = candidateType.getElementList().listIterator();candidateelements.hasNext();)
								{
									Element candidateelement = candidateelements.next();
									//System.out.println("requirements: "+requirementselement.getType()+" candidate: "+candidateelement.getType());
									//get the typemapping for the elements
									Mapping elementMapping = matchElements(requirementselement,candidateelement);
													
									//store score of element-matching in matrix
									typeMatrix.setValue(elementMapping);
								}
							}
							typeMatrix.computeMatches();
							elementListMapping = typeMatrix.getMapping();

							//add boni to score
							elementListMapping.addScoreBonus(typeScore);
								
						}
						else
						{
							//one complextype empty => null match
							Mapping nullMapping = new Mapping(requirementsName,candidateName);
							nullMapping.setScore(0);
							return nullMapping;
						}
					}
						
				}
			}
			//set NodeIdentifier for elementListMapping
			NodeIdentifier requirementsElementListIdentifier = new NodeIdentifier("Elements","complexTypeMember",requirementsType.getNodeIdentifier());
			NodeIdentifier candidateElementListIdentifier = new NodeIdentifier("Elements","complexTypeMember",candidateType.getNodeIdentifier());
			elementListMapping.setRequirementsIdentifier(requirementsElementListIdentifier);
			elementListMapping.setCandidateIdentifier(candidateElementListIdentifier);
			/*
			 * build the the typeMapping as a mappingPartList
			 * containing a Mapping for the elements
			 * and a Mapping for the attributes
			 */
			MappingPartList typePartList = new MappingPartList();
			if(elementListMapping != null)
			typePartList.addMapping(elementListMapping);
			if(attributeMapping != null)
			typePartList.addMapping(attributeMapping);
			typeMapping.addMappingPartList(typePartList);
	
			return typeMapping;
		}
		else
		{
			//at least one complextype is null i.e is not known
			Mapping nullMapping = new Mapping(requirementsName,candidateName);
			nullMapping.setScore(0);
			return nullMapping;
		}
	}
	
	/**
	 * matches two attributeLists (of complexTypes)
	 * by computing all possible combinations of the
	 * contained attributes and the selecting the
	 * combination(s) with the highest score
	 */
	private Mapping matchAttributeLists(ComplexType requirementsType,ComplexType candidateType)
	{
		//Mapping attributeListMapping = new Mapping("requirementsAttributes","candidateAttributes");
		//get attributeLists
		HashMap<String,Attribute> requirementsAttributes = requirementsType.getAttributeList();
		HashMap<String,Attribute> candidateAttributes = candidateType.getAttributeList();
		//create ScoreMap for attributeLists
		ScoreMap attributeMap = new ScoreMap(requirementsAttributes.size(),candidateAttributes.size(),"Attributes of "+requirementsType.getName(),"Attributes of "+candidateType.getName(),edit);
		//iterate over soureAttributes
		for(Iterator<String> requirementsIterator = requirementsAttributes.keySet().iterator();requirementsIterator.hasNext();)
		{
			Attribute requirementsAttribute = requirementsAttributes.get(requirementsIterator.next());
			//iterate over candidateAttributes
			for(Iterator<String> candidateIterator = candidateAttributes.keySet().iterator();candidateIterator.hasNext();)
			{
				Attribute candidateAttribute = candidateAttributes.get(candidateIterator.next());
				Mapping attributeMapping = matchAttributes(requirementsAttribute,candidateAttribute);
				attributeMap.setValue(attributeMapping);
			}
		}
		
		return attributeMap.getMapping();
	}
	
	/**
	 * match two attributes (of complexType)
	 */
	private Mapping matchAttributes(Attribute requirementsAttribute,Attribute candidateAttribute)
	{
		Mapping attributeMapping = new Mapping(requirementsAttribute.getName(),candidateAttribute.getName(),requirementsAttribute.getId(),candidateAttribute.getId());
		
		int matchScore = lookupTable.getMatchingScore(requirementsAttribute.getType(),candidateAttribute.getType());
		attributeMapping.setScore(matchScore);
		//bonus for equal use
		if((requirementsAttribute.getUse() != null)&&(candidateAttribute.getUse() != null))
		{
			if(requirementsAttribute.getUse().equals(candidateAttribute.getUse()))
			{
				attributeMapping.addScoreBonus(0);
			}
		}
	
		return attributeMapping;
	}
	
	/**
	 * matches types of arrays
	 */
	private Mapping matchArrayTypes(ComplexType requirementsType,ComplexType candidateType)
	{
		Mapping arrayMapping = new Mapping(requirementsType.getName(),candidateType.getName(),requirementsType.getNodeIdentifier(),candidateType.getNodeIdentifier());
		int arrayscore = -1;
		String requirementsarraytype = requirementsType.getArrayType();
		String candidatearraytype = candidateType.getArrayType();
		
				
		if(lookupTable.lookupSimpleType(requirementsarraytype))
		{
			if(lookupTable.lookupSimpleType(candidatearraytype))
			{
				arrayscore = lookupTable.getMatchingScore(requirementsarraytype,candidatearraytype)*arrayBonus;
				//set mappingscore
				arrayMapping.setScore(arrayscore);
				return arrayMapping;
			}
			else
			{
				//mixed types
				ComplexType complextype = candidateTypes.getType(candidatearraytype);
				//create mapping for types
				Mapping typeMapping = matchMixedTypes(requirementsType.getName(),requirementsarraytype,candidateType.getName(),complextype);
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//add typePartList to arrayMapping
				arrayMapping.addMappingPartList(typePartList);

				arrayMapping.setScore(arrayMapping.getScore());
				
				return arrayMapping;
			}
		}
		else
		{
			if(lookupTable.lookupSimpleType(candidatearraytype))
			{
				//mixed types
				ComplexType complextype = requirementsTypes.getType(requirementsarraytype);
				Mapping typeMapping = matchMixedTypes(requirementsType.getName(),complextype,candidateType.getName(),candidatearraytype);

				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//add typePartList to arrayMapping
				arrayMapping.addMappingPartList(typePartList);
				arrayMapping.setScore(arrayMapping.getScore());
				
				return arrayMapping;
			
			}
			else
			{
				//both complex types
				ComplexType complexrequirementstype = requirementsTypes.getType(requirementsarraytype);
				ComplexType complexcandidatetype = candidateTypes.getType(candidatearraytype);
				Mapping typeMapping = matchComplexTypes(requirementsType.getName(),complexrequirementstype,candidateType.getName(),complexcandidatetype);
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//add typePartList to arrayMapping
				arrayMapping.addMappingPartList(typePartList);
						

				arrayMapping.setScore(arrayMapping.getScore());

				return arrayMapping;
			}
		}
		//arrayMapping.setScore(arrayMapping.getScore()*arrayBonus);
		
		//return arrayMapping;
	}
	
	private Sequence generateSequence(ComplexType type)
	{
		Sequence sequence = new Sequence(type.getElementList().size());
		for(ListIterator<Element> elements = type.getElementList().listIterator();elements.hasNext();)
		{
			Element element = elements.next();
			sequence.setElement(elements.nextIndex()-1,element);
		}
		//System.out.println("+++Sequence+++\n");
		//sequence.print();
		return sequence;
	}
	
	/**
	 * matches two sequences by matching their elements 
	 * with respect to the sequenceorder
	 */
	private Mapping matchSequences(ComplexType requirementsType,Sequence requirements,ComplexType candidateType,Sequence candidate)
	{
		//System.out.println("\n matchsequences "+requirementsType.getName()+", "+candidateType.getName()+"\n");
		Mapping sequenceMapping = new Mapping(requirementsType.getName(),candidateType.getName(),requirementsType.getNodeIdentifier(),candidateType.getNodeIdentifier());
		//System.out.println("sequencelength: "+requirements.getLength()+" and "+candidate.getLength());
		int sequenceScore = 0;
		int elementScore = 0;
		ArrayList<Integer> index = new ArrayList<Integer>();
				
		if(requirements.getLength() == candidate.getLength())
		{
			//create new mappingPartlist
			MappingPartList mappingPartList = new MappingPartList();
			//Mapping tmpmapping = new Mapping();
			
			//sequences of equal length => match corresponding elements
			for(int i=0;i<requirements.getLength();i++)
			{
				Element requirementselement = requirements.getElement(i);
				Element candidateelement = candidate.getElement(i);
				//match the elements
				Mapping elementMapping = matchElements(requirementselement,candidateelement);
				//elementScore = elementMapping.getScore();
				//add elementmapping to mappingPartlist
				mappingPartList.addMapping(elementMapping);
				
			}
			//add mappingPartlist to mapping
			sequenceMapping.addMappingPartList(mappingPartList);
			///sequenceScore = sequenceScore+sequenceBonus;
			return sequenceMapping;
		}
		/*
		 * sequences of different length =>
		 * shift the shorter sequence over the longer
		 * and store the matching(s) with the highest score
		 */ 
		else if(requirements.getLength() < candidate.getLength())
		{
			/*
			 * create an arraylist of mappingpartlists
			 * for storing alternatives 
			 */
			ArrayList<MappingPartList> tmplist = new ArrayList<MappingPartList>();
			//shift requirements-sequence over candidatesequence
			for(int j=0;j<=candidate.getLength()-requirements.getLength();j++)
			{
				int tmpScore = 0;
				//create new mappingpartlist
				MappingPartList mappingPartList = new MappingPartList();
				for(int i=0;i<requirements.getLength();i++)
				{
					Element requirementselement = requirements.getElement(i);
					Element candidateelement = candidate.getElement(i+j);
					//match elements
					Mapping elementMapping = matchElements(requirementselement,candidateelement);
					elementScore = elementMapping.getScore();
					tmpScore = tmpScore+elementScore;
					//add elementmapping to mappingpartlist
					mappingPartList.addMapping(elementMapping);
					
					/*create new match for the elements
					Match match = new Match(requirementselement.getName(),candidateelement.getName(),elementscore,false);
					//add match to mapping for sequqence
					tmpmapping.addMatch(match);*/
				}
				//compare new sequence score with old maximum
				if(sequenceScore < tmpScore)
				{
					//new maximum => delete old
					index.clear();
					index.add(j);
					tmplist.clear();
					tmplist.add(mappingPartList);
					sequenceScore = tmpScore;
				}
				else if(sequenceScore == tmpScore)
				{
					//same as old => add in list
					index.add(j);
					tmplist.add(mappingPartList);
				}				
			}
			//add tmplist to mapping
			sequenceMapping.setMappingPartLists(tmplist);
			return sequenceMapping;
		}
		else if(requirements.getLength() > candidate.getLength())
		{
			/*
			 * create an arraylist of mappingpartlists
			 * for storing alternatives 
			 */
			ArrayList<MappingPartList> tmplist = new ArrayList<MappingPartList>();
			
			//shift candidate-sequence over requirements-sequence
			for(int j=0;j<=requirements.getLength()-candidate.getLength();j++)
			{
				int tmpScore = 0;
				//create new mappingpartlist
				MappingPartList mappingPartList = new MappingPartList();
				for(int i=0;i<candidate.getLength();i++)
				{
					Element requirementsElement = requirements.getElement(i+j);
					Element candidateElement = candidate.getElement(i);
					//match elements
					Mapping elementMapping = matchElements(requirementsElement,candidateElement);
					elementScore = elementMapping.getScore();
					tmpScore = tmpScore+elementScore;
					//add elementmapping to mappingpartlist
					mappingPartList.addMapping(elementMapping);
					
					/*elementscore = matchElements(requirementselement,candidateelement,mapping);
					tmpscore = tmpscore+elementscore;
					Match match = new Match(requirementselement.getName(),candidateelement.getName(),elementscore,false);
					tmpmapping.addMatch(match);*/
				}
				//compare new sequence score with old maximum
				if(sequenceScore < tmpScore)
				{
					//new maximum => delete old
					index.clear();
					index.add(j);
					tmplist.clear();
					tmplist.add(mappingPartList);
					sequenceScore = tmpScore;
				}
				else if(sequenceScore == tmpScore)
				{
					//same as old => add in list
					index.add(j);
					tmplist.add(mappingPartList);
				}
			}
			//add tmplist to mapping
			sequenceMapping.setMappingPartLists(tmplist);
			return sequenceMapping;
		}
		return null;	
	}
	
	/**
	 * matches two elements (of complexTypes)
	 */
	private Mapping matchElements(Element requirementsElement,Element candidateElement)
	{
		//create new typemapping
		Mapping elementMapping = new Mapping(requirementsElement.getName(),candidateElement.getName(),requirementsElement.getNodeIdentifier(),candidateElement.getNodeIdentifier());
		
		//System.out.println("matchelements: "+requirementsElement.getName()+": "+requirementsElement.getType()+", "+candidateElement.getName()+": "+candidateElement.getType());
		//ScoreMap elementmatrix = new ScoreMap(requirements)
		int elementScore = 0;
		//int score = 0;
		/*
		 * set the min/maxoccur in mapping
		 */
		elementMapping.setRequirementminoccur(requirementsElement.getMinOccur());
		elementMapping.setRequirementmaxoccur(requirementsElement.getMaxOccur());
		elementMapping.setCandidateminoccur(candidateElement.getMinOccur());
		elementMapping.setCandidatemaxoccur(candidateElement.getMaxOccur());
		//compare minoccur/maxoccurvalues
		if((!(requirementsElement.getMinOccur() <  0))&&(!(candidateElement.getMinOccur() < 0)))
		{
			if(requirementsElement.getMinOccur() == candidateElement.getMinOccur())
			{
				
				elementScore++;
			}
		}
		if((!(requirementsElement.getMaxOccur() <  0))&&(!(candidateElement.getMaxOccur() < 0)))
		{
			if(requirementsElement.getMaxOccur() == candidateElement.getMaxOccur())
			{
				
				elementScore++;
			}
		}
		/*
		 * TODO nillable attribute comparison
		 */
		if(!lookupTable.lookupSimpleType(requirementsElement.getType()))
		{
			ComplexType complexRequirementsType = requirementsTypes.getType(requirementsElement.getType());
			/*
			 * requirements complex  => set requirementsname to requirementsElement.getType()
			 * set requirementsIdentifier accordingliy
			 			
			elementMapping.setRequirementsName(requirementsElement.getType());
			elementMapping.setRequirementsIdentifier(complexRequirementsType.getNodeIdentifier());
			*/
			if(lookupTable.lookupSimpleType(candidateElement.getType()))
			{
				//candidate simple
				
				String simpletype = candidateElement.getType();
				//create Mapping for types of elements
				Mapping typeMapping = matchMixedTypes(requirementsElement.getType(),complexRequirementsType,candidateElement.getName(),candidateElement.getType());
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//addtypePartList to elementMapping
				elementMapping.addMappingPartList(typePartList);
			}
			else
			{
				//candidate complex
				//get complex types
				
				ComplexType complexCandidateType = candidateTypes.getType(candidateElement.getType());
				/*
				 * candidate complex  => set candidatename to candidateElement.getType()
				 * set candidateIdentifier accordingliy
				 
				elementMapping.setCandidateName(candidateElement.getType());
				elementMapping.setCandidateIdentifier(complexCandidateType.getNodeIdentifier());*/
				//recursion
				//System.out.println("recursion: 1: "+complexrequirementstype.getName()+" 2: "+complexcandidatetype.getName());
				/*ComplexTypeMatcher complexmatcher = new ComplexTypeMatcher(complexrequirementstype,complexcandidatetype,lookuptable,requirementslist,candidatelist,mapping);
				if(complexrequirementstype.getGrouping() == complexcandidatetype.getGrouping())
				{
					scorematrix.setValue(complexrequirementstype.getName(),complexcandidatetype.getName(),complexmatcher.getMaxScore()+maxscore);
				}
				else
				{
					scorematrix.setValue(complexrequirementstype.getName(),complexcandidatetype.getName(),complexmatcher.getMaxScore());
				}*/

				//	create Mapping for types of elements
				Mapping typeMapping = matchComplexTypes(requirementsElement.getType(),complexRequirementsType,candidateElement.getType(),complexCandidateType);
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//addtypePartList to elementMapping
				elementMapping.addMappingPartList(typePartList);
				
				//elementMapping = matchComplexTypes(requirementselement.getName(),complexrequirementstype,candidateelement.getName(),complexcandidatetype);
				//elementScore = elementScore+matchComplexTypes(requirementselement.getName(),complexrequirementstype,candidateelement.getName(),complexcandidatetype,mapping);
				//matchComplexTypes(complexrequirementstype,complexcandidatetype);
			}
		}
		else
		{
			//requirements simple
			if(lookupTable.lookupSimpleType(candidateElement.getType()))
			{
				//candidate simple
				//System.out.println("\nmatchelements both simple 1: "+requirementselement.getType()+" 2: "+candidateelement.getType());
				//elementMapping.setScore(lookupTable.getMatchingScore(requirementselement.getType(),candidateelement.getType()));
				/*
				elementMapping.setRequirementsName(requirementsElement.getName()+":"+requirementsElement.getType());
				elementMapping.setCandidateName(candidateElement.getName()+":"+candidateElement.getType());
				*/
				elementMapping.setRequirementsName(requirementsElement.getName());
				elementMapping.setCandidateName(candidateElement.getName());
				//elementMapping.getRequirementsIdentifier().addIdentifier(requirementsElement.getType(),"builtinType");
				//elementMapping.getCandidateIdentifier().addIdentifier(candidateElement.getType(),"builtinType");
				

				//int matchScore = (elementScore+lookupTable.getMatchingScore(requirementsElement.getType(),candidateElement.getType()));
				//elementMapping.setScore(matchScore);
				elementMapping.setScore(lookupTable.getMatchingScore(requirementsElement.getType(),candidateElement.getType()));
				//elementMapping.addScoreBonus(elementScore);				
			}				
			else
			{
				//candidate complex
				ComplexType complexCandidateType = candidateTypes.getType(candidateElement.getType());
				/*
				 * candidate complex  => set candidatename to candidateElement.getType()
				 * set candidateIdentifier accordingliy
				 
				elementMapping.setCandidateName(candidateElement.getType());
				elementMapping.setCandidateIdentifier(complexCandidateType.getNodeIdentifier());*/

				//create Mapping for types of elements
				Mapping typeMapping = matchMixedTypes(requirementsElement.getName(),requirementsElement.getType(),candidateElement.getType(),complexCandidateType);
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//addtypePartList to elementMapping
				elementMapping.addMappingPartList(typePartList);
				
				//elementMapping = matchMixedTypes(requirementselement.getName(),requirementselement.getType(),candidateelement.getName(),complextype);
				//elementScore = elementScore+matchMixedTypes(requirementselement.getName(),requirementselement.getType(),candidateelement.getName(),complextype,mapping);
			}			
		}		
		//elementMapping.addScoreBonus(elementScore);
		return elementMapping;
	}
	
	/**
	 * mixed-match of a complex type with a simpletype element
	 */
	public Mapping matchMixedTypes(String requirementsName,ComplexType requirementsType,String candidateName,String candidateType)
	{
		if(requirementsType != null)
		{
			//System.out.println("\nmatchMixedTypes: "+requirementsname+", "+candidatename+"\n");
			
			//create new scoremap for requirementsname and candidatename
			ScoreMap typeMatrix = new ScoreMap(requirementsType.getElementList().size(),1,requirementsName,candidateName, edit);
			if(!requirementsType.isArray())
			{
				if(!requirementsType.getElementList().isEmpty())
				{
					//create new temporary list
					//TypeMatchingList elementmatchinglist = new TypeMatchingList();
					//match each element of complextype against simpletype
					for(ListIterator<Element> elements = requirementsType.getElementList().listIterator();elements.hasNext();)
					{
						Element requirementsElement = elements.next();
						
						/*if(requirementsType.getName().equals("dateType"))
						{
							System.out.println("mixedstringmatcher_element of complextype: "+requirementsElement.getName()+" type: "+requirementsElement.getType());
						}*/
						//System.out.println("mixedstringmatcher_element of complextype: "+requirementselement.getName()+" type: "+requirementselement.getType());
						//elementof simple type, ready!
						if(lookupTable.lookupSimpleType(requirementsElement.getType()))
						{
							//create new elementmapping
							Mapping elementMapping = new Mapping(requirementsElement.getName(),candidateName);
							elementMapping.setRequirementsIdentifier(requirementsElement.getNodeIdentifier());
							// setcandidateIdentifier
							
							//int matchScore = lookupTable.getMatchingScore(requirementselement.getType(),candidatetype);
							//elementMapping.setScore(matchScore);
	
							int matchScore = lookupTable.getMatchingScore(requirementsElement.getType(),candidateType);
							elementMapping.setScore(matchScore);

							typeMatrix.setValue(elementMapping);
						}
						//elementof complex type, recurse!
						else
						{
							ComplexType complextypeelement = requirementsTypes.getType(requirementsElement.getType());
							Mapping elementMapping = matchMixedTypes(requirementsElement.getName(),complextypeelement,candidateName,candidateType);
							//Mapping elementMapping = new Mapping(requirementselement.getName(),candidatename);
							//int localMax = matchMixedTypes(requirementselement.getName(),complextypeelement,candidatename,candidatetype,elementMapping);
							//elementMapping.setScore(localMax);
	
							
							typeMatrix.setValue(elementMapping);
							
							//int localmax = matchMixedTypes(requirementselement.getName(),complextypeelement,candidatename,candidatetype,mapping);
							//System.out.println("localmax: "+localmax);
							//typematrix.setValue(requirementselement.getName(),candidatename,localmax);
						}
					
					}
					//System.out.println("\nelementmatchinglist for"+requirementsname+" and "+candidatename);
					//elementmatchinglist.print();
					typeMatrix.computeMatches();
					Mapping typeMapping = typeMatrix.getMapping();
					/*if(requirementsType.getName().equals("dateType"))
					{
						System.out.println("mixed test: ");
						typeMapping.printMapping(9);
					}*/
					//setrequirementsidentifier
					typeMapping.setRequirementsIdentifier(requirementsType.getNodeIdentifier());
					// setcandidateIdentifier

					return typeMapping;
					
				}
				else
				{
					//complextype has no elements
					Mapping nullMapping = new Mapping(requirementsName,candidateName);
					nullMapping.setScore(0);
					return nullMapping;
				}
			}
			else 
			{
				//complextype is array => score 0
				Mapping nullMapping = new Mapping(requirementsName,candidateName);
				nullMapping.setScore(0);
				return nullMapping;
			}
		}
		else
		{
			//complextype is null i.e is not known
			Mapping nullMapping = new Mapping(requirementsName,candidateName);
			nullMapping.setScore(0);
			return nullMapping;
		}
		
	}
	
	/**
	 * mixed-match of a simpletype element with a compex type
	 */
	public Mapping matchMixedTypes(String requirementsName,String requirementsType,String candidateName,ComplexType candidateType)
	{
		if(candidateType != null)
		{
		
			//System.out.println("\nmixedtypematch "+requirementsname+", "+candidatename+"\n");
			
			//create new scoremap for requirementsname and candidatename
			ScoreMap typeMatrix = new ScoreMap(1,candidateType.getElementList().size(),requirementsName,candidateName, edit);
			if(!candidateType.isArray())
			{
				if(!candidateType.getElementList().isEmpty())
				{
					//create new temporary list
					//TypeMatchingList elementmatchinglist = new TypeMatchingList();
					
					//match each element of complextype against simpletype
					for(ListIterator<Element> elements = candidateType.getElementList().listIterator();elements.hasNext();)
					{
						Element candidateelement = elements.next();
						
						
						//System.out.println("mixedstringmatcher_element of complextype: "+requirementselement.getName()+" type: "+requirementselement.getType());
						//elementof simple type, ready!
						if(lookupTable.lookupSimpleType(candidateelement.getType()))
						{
							//create new elementmapping
							Mapping elementMapping = new Mapping(requirementsName,candidateelement.getName());
							elementMapping.setCandidateIdentifier(candidateelement.getNodeIdentifier());
							// setrequirementsIdentifier

							//int matchScore = lookupTable.getMatchingScore(requirementstype,candidateelement.getType());
							//elementMapping.setScore(matchScore);
	
							int matchScore = lookupTable.getMatchingScore(requirementsType,candidateelement.getType());
							elementMapping.setScore(matchScore);
							
							typeMatrix.setValue(elementMapping);
							
							//typematrix.setValue(requirementsname,candidateelement.getName(),lookuptable.getMatchingScore(requirementstype,candidateelement.getType()));
						}
						//elementof complex type, recurse!
						else
						{
							ComplexType complextypeelement = candidateTypes.getType(candidateelement.getType());
							Mapping elementMapping = matchMixedTypes(requirementsName,requirementsType,candidateelement.getName(),complextypeelement);
							//Mapping elementMapping = new Mapping(requirementsname,candidateelement.getName());
							//int localMax = matchMixedTypes(requirementsname,requirementstype,candidateelement.getName(),complextypeelement,elementMapping);
							//elementMapping.setScore(localMax);
	
							//create MappingPartList
							//MappingPartList elementPartList = new MappingPartList();
							
							//add elementMapping to MappingPartList
							//elementPartList.addMapping(elementMapping);
							
							//add MAppingPartList to mapping
							//mapping.addMappingPartList(elementPartList);
							//elementmatchinglist.put(requirementsname,candidateelement.getName(),elementMapping);
							typeMatrix.setValue(elementMapping);
							//typeMatrix.setValue(requirementsname,candidateelement.getName(),localMax);
							
							
							//int localmax = matchMixedTypes(requirementsname,requirementstype,candidateelement.getName(),complextypeelement,mapping);
							//System.out.println("localmax: "+localmax);
							//typematrix.setValue(requirementsname,candidateelement.getName(),localmax);
						}
					
					}
					typeMatrix.computeMatches();
					Mapping typeMapping = typeMatrix.getMapping();
					// setrequirementsIdentifier
					//set candidateidentifier
					typeMapping.setCandidateIdentifier(candidateType.getNodeIdentifier());
					return typeMapping;
					
					
					//computeTypeMapping(mapping,typematrix.getMaxMappings(),elementmatchinglist);
				}
				else
				{
					//complextype has no elements
					Mapping nullMapping = new Mapping(requirementsName,candidateName);
					nullMapping.setScore(0);
					return nullMapping;
				}
			}
			else 
			{
				//complextype is array => score 0
				Mapping nullMapping = new Mapping(requirementsName,candidateName);
				nullMapping.setScore(0);
				return nullMapping;
			}
		}
		else
		{
			//complextype is null i.e is not known
			Mapping nullMapping = new Mapping(requirementsName,candidateName);
			nullMapping.setScore(0);
			return nullMapping;
		}
		
	}
}
