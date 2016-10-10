package de.dfki.wsdlanalyzer.matcher;

import java.util.ArrayList;


import java.util.Iterator;
import java.util.ListIterator;

import de.dfki.wsdlanalyzer.mapping.Mapping;
import de.dfki.wsdlanalyzer.mapping.MappingPartList;
import de.dfki.wsdlanalyzer.matrix.ScoreMap;
import de.dfki.wsdlanalyzer.parser.SimpleTypeLookupTable;
import de.dfki.wsdlanalyzer.types.BindingList;
import de.dfki.wsdlanalyzer.types.ComplexType;
import de.dfki.wsdlanalyzer.types.Element;
import de.dfki.wsdlanalyzer.types.ElementList;
import de.dfki.wsdlanalyzer.types.Message;
import de.dfki.wsdlanalyzer.types.MessageList;
import de.dfki.wsdlanalyzer.types.MessageParameter;
import de.dfki.wsdlanalyzer.types.Operation;
import de.dfki.wsdlanalyzer.types.OperationList;
import de.dfki.wsdlanalyzer.types.PortType;
import de.dfki.wsdlanalyzer.types.PortTypeList;
import de.dfki.wsdlanalyzer.types.Sequence;
import de.dfki.wsdlanalyzer.types.Service;
import de.dfki.wsdlanalyzer.types.TypeList;
import de.dfki.wsdlanalyzer.types.WsdlFile;














/**
 * class for matching two services
 * which "punishes" services of different size
 * by decreasing the matching score for entities of one service
 * which cannot be mapped to corresponding entities of the other
 * service
 * @deprecated
 */
public class TreeEditMatcher 
{
	private String sourceFileName,targetFileName;
	private Service sourceService,targetService;
	//private WsdlFile sourcefile,targetfile;
	private BindingList sourceBindings,targetBindings;
	private PortTypeList sourcePortTypes,targetPortTypes;
	private MessageList sourceMessages,targetMessages;
	private TypeList sourceTypes,targetTypes;
	private ElementList sourceElements,targetElements;
	private SimpleTypeLookupTable lookupTable;
	private ScoreMap scoreMatrix;
	private int minScore = 0;
	private int maxScore = 10;
	private int groupingBonus = 10;
	private int arrayBonus = 2;
	private int sequenceBonus = 5;
	private boolean edit;
	
	private Mapping serviceMapping;
	
	
	
	
	/*
	 * input:
	 * 
	 * source: Service from sourcefile
	 * sourcefile: WsdlFile of sourcefile
	 * target: Service from targetfile
	 * targetfile: WsdlFile of targetfile
	 * l: SympleTypeLookupTable for matching source- and targetfile
	 */
	public TreeEditMatcher(Service source,WsdlFile sourcefile,Service target, WsdlFile targetfile, SimpleTypeLookupTable l)
	{
		edit = true;
		
		sourceFileName = sourcefile.getWsdlFileName();
		sourceService = source;
		sourceBindings = sourcefile.getBindings();
		sourcePortTypes = sourcefile.getPorttypelist();
		sourceMessages = sourcefile.getMessageList();
		sourceTypes = sourcefile.getTypeList();
		sourceElements = sourcefile.getElementlist();
		
		targetFileName = targetfile.getWsdlFileName();
		targetService = target;
		targetBindings = targetfile.getBindings();
		targetPortTypes = targetfile.getPorttypelist();
		targetMessages = targetfile.getMessageList();
		targetTypes = targetfile.getTypeList();
		targetElements = targetfile.getElementlist();
		
		lookupTable = l;
		
		serviceMapping = new Mapping(sourceService.getName(),targetService.getName(),sourceService.getNodeIdentifier(),targetService.getNodeIdentifier());
		
	}
	
	
	
	/*
	 * matches two services by building all possible combinations
	 * of their ports/porttypes
	 */
	public int matchServices()
	{
		
		//iterate over sourceports
		for(Iterator<String> sourcePorts = sourceService.keySet().iterator();sourcePorts.hasNext();)
		{
			//get sourceportname
			String sourcePortName = sourcePorts.next();
			//get binding for port
			String sourceBinding = sourceService.get(sourcePortName).getBinding();
			//get name of sourceporttype
			String sourceName = sourceBindings.get(sourceBinding).getPortType();
			PortType sourcePortType = sourcePortTypes.get(sourceName);
						
			//iterate over all targetports 
			for(Iterator<String> targetPorts = targetService.keySet().iterator();targetPorts.hasNext();)
			{
				//get targetportname
				String targetPortName = targetPorts.next();
				Mapping portMapping = new Mapping(sourcePortName,targetPortName,sourceService.get(sourcePortName).getNodeIdentifier(),targetService.get(targetPortName).getNodeIdentifier());
				//get binding for port
				String targetBinding = targetService.get(targetPortName).getBinding();
				Mapping bindingMapping = new Mapping(sourceBinding,targetBinding,sourceBindings.get(sourceBinding).getNodeIdentifier(),targetBindings.get(targetBinding).getNodeIdentifier());
				//get name of targetporttype
				String targetName = targetBindings.get(targetBinding).getPortType();
				PortType targetPortType = targetPortTypes.get(targetName);
				
				
				//match source and targetporttype
				Mapping portTypeMapping = matchPortTypes(sourcePortType,targetPortType);
				
				/*
				 * TODO decrease score for services with different
				 * number of ports, bindings and porttypes?
				 */
				
				
				if(portTypeMapping != null)
				{
					//create new MappingPartList
					MappingPartList portTypePartList = new MappingPartList();
					//add porttypemapping to mappingpartlist
					portTypePartList.addMapping(portTypeMapping);
					
					//add porttypeMappingPartList to bindingMapping
					bindingMapping.addMappingPartList(portTypePartList);
					//add bindingMapping to mappingpartlist
					MappingPartList bindingPartList = new MappingPartList();
					bindingPartList.addMapping(bindingMapping);
					//add bindingPartList to portMapping
					portMapping.addMappingPartList(bindingPartList);
					//add portMapping to mappingpartlist
					MappingPartList portPartList = new MappingPartList();
					portPartList.addMapping(portMapping);
					//add portPartList to serviceMapping
					serviceMapping.addMappingPartList(portPartList);
				}
			}
			
		}
		
		return serviceMapping.getScore();
	}
	
	/*
	 * matches two portTypes by building all possible combinations
	 * of source and target operations
	 */
	public Mapping matchPortTypes(PortType sourceporttype,PortType targetporttype)
	{
		//get the operationlists for the porttypes
		OperationList sourceOperations = sourceporttype.getOperationlist();
		OperationList targetOperations = targetporttype.getOperationlist();
		//test for empty operationlists
		if((!sourceOperations.isEmpty())&&(!targetOperations.isEmpty()))
		{
			ScoreMap operationMatrix = new ScoreMap(sourceOperations.length(),targetOperations.length(),sourceporttype.getName(), targetporttype.getName(), edit);
			int porttypescore = -1;
			//System.out.println("matchporttypes\n");
			//create new porttypemapping for source and targetportype
			//Mapping mapping = new Mapping(sourceporttype.getName(),targetporttype.getName());
			
			
			
			
			/*
			 * list to store operationmappings temporarily
			 */
			//OperationMatchingList matchlist = new OperationMatchingList();
			
			//create operationmatrix
			
			//iterate over source-operations
			for(Iterator<String> sourceOperationNames = sourceOperations.keySet().iterator();sourceOperationNames.hasNext();)
			{
				String sourceName = sourceOperationNames.next();
				//System.out.println("sourceoperation: "+sourcename);
				
				//get sourceoperation
				Operation sourceOperation = sourceOperations.getOperation(sourceName);
				
				//iterate over the target operations
				for(Iterator<String> targetOperationNames = targetOperations.keySet().iterator();targetOperationNames.hasNext();)
				{
					String targetName = targetOperationNames.next();
					//System.out.println("targetoperation: "+targetname);
					
					//get targetoperation
					Operation targetOperation = targetOperations.getOperation(targetName);
					
					//match source and targetoperation
					Mapping operationMapping = matchOperations(sourceOperation,targetOperation);
					
					//store operationmatch in list
					//matchlist.put(sourceoperation.getName(),targetoperation.getName(),operationmapping);
					
					//int score = operationMapping.getScore();
					//write matchingscore in matrix
					operationMatrix.setValue(operationMapping);
					
				}
				
			}
			//compute matches from operationmatrix
			operationMatrix.computeMatches();
			Mapping portTypeMapping = operationMatrix.getMapping();
			
			
			
			
			
			return portTypeMapping;
		}
		else
		{
			Mapping nullMapping = new Mapping(sourceporttype.getName(),targetporttype.getName(),sourceporttype.getNodeIdentifier(),targetporttype.getNodeIdentifier());
			nullMapping.setScore(0);
			return nullMapping;
		}
	}
	
	/*
	 * matches two operations by matching their
	 * input, output and fault messages
	 */
	private Mapping matchOperations(Operation sourceop,Operation targetop)
	{
		
		//create new mapping for sourceop and targetop
		Mapping operationMapping = new Mapping(sourceop.getName(),targetop.getName(),sourceop.getNodeIdentifier(),targetop.getNodeIdentifier());
		
		//create new MappingPartList for input/output/fault-messages
		MappingPartList messagePartList = new MappingPartList();
		
		//match input-messages
		//System.out.println("sourceinput: "+sourceop.getInput());
		Message sourceInputMessage = sourceMessages.getMessage(sourceop.getInput());
		//System.out.println("targetinput: "+targetop.getInput());
		Message targetInputMessage = targetMessages.getMessage(targetop.getInput());
		
		Mapping inputMapping = matchMessages(sourceInputMessage,targetInputMessage); 
		//System.out.println("inputmapping: "+inputMapping.getSourceName()+", "+inputMapping.getTargetName());
		//add inputmapping to MappingPartList
		messagePartList.addMapping(inputMapping);
		
		
		//System.out.println("inputscore: "+inputscore);
		
		//match output-messages
		//System.out.println("sourceoutput: "+sourceop.getOutput());
		Message sourceOutputMessage = sourceMessages.getMessage(sourceop.getOutput());
		//System.out.println("targetoutput: "+targetop.getOutput());
		Message targetOutputMessage = targetMessages.getMessage(targetop.getOutput());
		
		Mapping outputMapping = matchMessages(sourceOutputMessage,targetOutputMessage);
		
		//add outputmapping to MappingPartList
		messagePartList.addMapping(outputMapping);
		//operationmatch.addScore(outputscore);
		
		
		//System.out.println("outputscore: "+outputscore);
		
		//match fault-messages
		if((sourceop.getFault()!=null)&&(targetop.getFault()!=null))
		{
			Message sourceFaultMessage = sourceMessages.getMessage(sourceop.getFault());
			Message targetFaultMessage = targetMessages.getMessage(targetop.getFault());
			
			Mapping faultMapping = matchMessages(sourceFaultMessage,targetFaultMessage);
			//int faultscore = faultmapping.getScore();
			messagePartList.addMapping(faultMapping);
			
			
		}
		//add MappingPartList of messages to Mapping for operation
		operationMapping.addMappingPartList(messagePartList);
		//System.out.println("score: "+score);
		//}
		return operationMapping;
	}
	
	/*
	 * matches two messages by building all possible combinations
	 * of their parameters
	 */
	private Mapping matchMessages(Message sourcemessage,Message targetmessage)
	{
		//System.out.println("\n messages "+sourcemessage.getName()+", "+targetmessage.getName()+"\n");
		//create new messagemapping
		//MessageMapping messagemapping = new MessageMapping(sourcemessage.getName(),targetmessage.getName());
		
		//create new scoremap for parameterlists of source- and targetmessage
		ScoreMap parameterMatrix = new ScoreMap(sourcemessage.getParameterList().size(),targetmessage.getParameterList().size(),sourcemessage.getName(),targetmessage.getName(), edit);
		
		//test for empty messages
		if((!sourcemessage.isEmpty())&&(!targetmessage.isEmpty()))
		{
			/*
			 * list for temporarily storing
			 * parametermappings
			 */
			//ParameterMatchingList matchinglist = new ParameterMatchingList();
			
			//create parameter-matrix
			
			//iterate over parameter of source message
			for(ListIterator<MessageParameter> sourceList = sourcemessage.getParameterList().listIterator();sourceList.hasNext();)
			{
				MessageParameter sourceParameter = sourceList.next();
				//iterate over parameter of target message
				for(ListIterator<MessageParameter> targetList = targetmessage.getParameterList().listIterator();targetList.hasNext();)
				{
					MessageParameter targetParameter = targetList.next();
					Mapping parameterMapping = matchParameter(sourceParameter,targetParameter);
					//int score = parameterMapping.getScore();
					
					//matchinglist.put(sourceparameter.getName(),targetparameter.getName(),parametermapping);
					
					parameterMatrix.setValue(parameterMapping);
				}
			}
			//compute parameter-matches from matrix
			parameterMatrix.computeMatches();
			Mapping messageMapping = parameterMatrix.getMapping();
			
			
			return messageMapping;
		}
		else
		{
			// message empty => null match
			Mapping nullMapping = new Mapping(sourcemessage.getName(),targetmessage.getName(),sourcemessage.getNodeIdentifier(),targetmessage.getNodeIdentifier());
			nullMapping.setScore(0);
			return nullMapping;
		}
		
	}
	
	/*
	 * matches two (message)parameters by matching their types
	 */
	private Mapping matchParameter(MessageParameter sourceparameter,MessageParameter targetparameter)
	{
		//create new mapping
		Mapping parameterMapping = new Mapping(sourceparameter.getName(),targetparameter.getName());
		
		//System.out.println(sourceparameter.getName()+" sourcetype: "+sourceparameter.getType());
		//System.out.println(targetparameter.getName()+" targettype: "+targetparameter.getType());
		//sourceparameter is of predefined type
		if(lookupTable.lookupSimpleType(sourceparameter.getType()))
		{
			//targetparameter is of predefined type
			if(lookupTable.lookupSimpleType(targetparameter.getType()))
			{
				parameterMapping.setScore(lookupTable.getMatchingScore(sourceparameter.getType(),targetparameter.getType()));
				//ready
			}
			//targetparameter is of complextype
			else
			{
				ComplexType complextype = targetTypes.getType(targetparameter.getType());
				Mapping  typeMapping = matchMixedTypes(sourceparameter.getName(),sourceparameter.getType(),targetparameter.getName(),complextype);
				//Mapping  typeMapping = new Mapping(sourceparameter.getName(),complextype.getName());
				//typeMapping.setScore(matchMixedTypes(sourceparameter,targetparameter.getName(),complextype,typeMapping));
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to MappingPartList
				typePartList.addMapping(typeMapping);
				
				//add MappingPartList to parameter-mapping
				parameterMapping.addMappingPartList(typePartList);
				
				
				
			}
		}
		//sourceparameter is of complextype
		else
		{
			//targetparameter is of predefined type
			if(lookupTable.lookupSimpleType(targetparameter.getType()))
			{
				ComplexType complextype = sourceTypes.getType(sourceparameter.getType());
				Mapping typeMapping = matchMixedTypes(sourceparameter.getName(),complextype,targetparameter.getName(),targetparameter.getType());
				//Mapping typeMapping = new Mapping(complextype.getName(),targetparameter.getName());
				//typeMapping.setScore(matchMixedTypes(sourceparameter.getName(),complextype,targetparameter,typeMapping));

				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to MappingPartList
				typePartList.addMapping(typeMapping);
				
				//add MappingPartList to parameter-mapping
				parameterMapping.addMappingPartList(typePartList);
				
			}
			//targetparameter is of complextype
			else
			{
				//System.out.println("typeindex: "+sourcetypes.indexOf(s.getType()));
				ComplexType sourcetype = sourceTypes.getType(sourceparameter.getType());
				ComplexType targettype = targetTypes.getType(targetparameter.getType());
				
				/*System.out.println("sourcetype: \n");
				sourcetype.printComplexType();
				targettype.printComplexType();*/
				Mapping typeMapping = matchComplexTypes(sourceparameter.getName(),sourcetype,targetparameter.getName(),targettype);
				//Mapping typeMapping = new Mapping(sourcetype.getName(),targettype.getName());
				//typeMapping.setScore(matchComplexTypes(sourceparameter.getName(),sourcetype,targetparameter.getName(),targettype));
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to MappingPartList
				typePartList.addMapping(typeMapping);
				
				//add MappingPartList to parameter-mapping
				parameterMapping.addMappingPartList(typePartList);
			}
		}
		// score;
		return parameterMapping;
	}
	
	/*
	 * compute score for identical types
	 * i.e number of elements times maxscore
	 * at each level(subtype/elementtype) of the type "structure"
	 */
	private int matchIdenticalTypes(ComplexType type)
	{
		int elementcount = type.getElementList().size();
		int groupingcount = 1;
		for(Iterator<Element> elements = type.getElementList().iterator();elements.hasNext();)
		{
			Element element = elements.next();
			if(!lookupTable.lookupSimpleType(element.getType()))
			{
				System.out.println("element: "+element.getType());
				ComplexType complextype = sourceTypes.getType(element.getType());
				elementcount = elementcount+matchIdenticalTypes(complextype);
				groupingcount++;
			}
		}
		return (elementcount+groupingcount)*maxScore*2;
	}
	
	
	
	
	
	
	/*
	 * matches a complex type with a simpletype element
	 */
	private Mapping matchMixedTypes(String sourcename,ComplexType sourcetype,String targetname,String targettype)
	{
		//create new typemapping
		//TypeMapping mapping = new TypeMapping(sourcetype.getName(),targetname);
		//Mapping typeMapping;
		//System.out.println("\nmatchMixedTypes: "+sourcename+", "+targetname+"\n");
		
		//create new scoremap for sourcename and targetname
		ScoreMap typeMatrix = new ScoreMap(sourcetype.getElementList().size(),1,sourcename,targetname, edit);
		int typeScore = -1;
		if(!sourcetype.isArray())
		{
			if(!sourcetype.getElementList().isEmpty())
			{
				//create new temporary list
				//TypeMatchingList elementmatchinglist = new TypeMatchingList();
				//match each element of complextype against simpletype
				for(ListIterator<Element> elements = sourcetype.getElementList().listIterator();elements.hasNext();)
				{
					Element sourceelement = elements.next();
					
					//System.out.println("mixedstringmatcher_element of complextype: "+sourceelement.getName()+" type: "+sourceelement.getType());
					//elementof simple type, ready!
					if(lookupTable.lookupSimpleType(sourceelement.getType()))
					{
						//create new elementmapping
						Mapping elementMapping = new Mapping(sourceelement.getName(),targetname);
						elementMapping.setRequirementsIdentifier(sourceelement.getNodeIdentifier());
						int matchScore = lookupTable.getMatchingScore(sourceelement.getType(),targettype);
						elementMapping.setScore(matchScore);

						
						typeMatrix.setValue(elementMapping);
					}
					//elementof complex type, recurse!
					else
					{
						ComplexType complextypeelement = sourceTypes.getType(sourceelement.getType());
						Mapping elementMapping = matchMixedTypes(sourceelement.getName(),complextypeelement,targetname,targettype);
						//Mapping elementMapping = new Mapping(sourceelement.getName(),targetname);
						//int localMax = matchMixedTypes(sourceelement.getName(),complextypeelement,targetname,targettype,elementMapping);
						//elementMapping.setScore(localMax);

						
						typeMatrix.setValue(elementMapping);
						
						//int localmax = matchMixedTypes(sourceelement.getName(),complextypeelement,targetname,targettype,mapping);
						//System.out.println("localmax: "+localmax);
						//typematrix.setValue(sourceelement.getName(),targetname,localmax);
					}
				
				}
				//System.out.println("\nelementmatchinglist for"+sourcename+" and "+targetname);
				//elementmatchinglist.print();
				typeMatrix.computeMatches();
				Mapping typeMapping = typeMatrix.getMapping();
				return typeMapping;
				/*
				 *convert output from scoremap
				 * into elementmappings i.e
				 * build for each mapping from the mappinglist
				 * the corresponding elementmappinglist from
				 * the temporary list of all elementmappings
				 */
				
				//computeTypeMapping(mapping,typematrix.getMaxMappings(),elementmatchinglist);
			}
			else
			{
				//complextype has no elements
				Mapping nullMapping = new Mapping(sourcename,targetname);
				nullMapping.setScore(0);
				return nullMapping;
			}
		}
		else 
		{
			Mapping nullMapping = new Mapping(sourcename,targetname);
			nullMapping.setScore(0);
			return nullMapping;
		}
		
	}
	
	/*
	 * matches a simpletype element with a compex type
	 */
	private Mapping matchMixedTypes(String sourcename,String sourcetype,String targetname,ComplexType targettype)
	{
		//create new typemapping
		//TypeMapping mapping = new TypeMapping(sourcename,targettype.getName());
		//Mapping typeMapping;
		
		//System.out.println("\nmixedtypematch "+sourcename+", "+targetname+"\n");
		
		//create new scoremap for sourcename and targetname
		ScoreMap typeMatrix = new ScoreMap(1,targettype.getElementList().size(),sourcename,targetname, edit);
		int typeScore = -1;
		if(!targettype.isArray())
		{
			if(!targettype.getElementList().isEmpty())
			{
				//create new temporary list
				//TypeMatchingList elementmatchinglist = new TypeMatchingList();
				
				//match each element of complextype against simpletype
				for(ListIterator<Element> elements = targettype.getElementList().listIterator();elements.hasNext();)
				{
					Element targetelement = elements.next();
					
					
					//System.out.println("mixedstringmatcher_element of complextype: "+sourceelement.getName()+" type: "+sourceelement.getType());
					//elementof simple type, ready!
					if(lookupTable.lookupSimpleType(targetelement.getType()))
					{
						//create new elementmapping
						Mapping elementMapping = new Mapping(sourcename,targetelement.getName());
						elementMapping.setCandidateIdentifier(targetelement.getNodeIdentifier());
						int matchScore = lookupTable.getMatchingScore(sourcetype,targetelement.getType());
						elementMapping.setScore(matchScore);

						//create MappingPartList
						//MappingPartList elementPartList = new MappingPartList();
						
						//add elementMapping to MappingPartList
						//elementPartList.addMapping(elementMapping);
						
						//add MAppingPartList to mapping
						//mapping.addMappingPartList(elementPartList);
						//store elementmapping in temporary list
						//elementmatchinglist.put(sourcename,targetelement.getName(),elementMapping);
						
						typeMatrix.setValue(elementMapping);
						
						//typematrix.setValue(sourcename,targetelement.getName(),lookuptable.getMatchingScore(sourcetype,targetelement.getType()));
					}
					//elementof complex type, recurse!
					else
					{
						ComplexType complextypeelement = targetTypes.getType(targetelement.getType());
						Mapping elementMapping = matchMixedTypes(sourcename,sourcetype,targetelement.getName(),complextypeelement);
						//Mapping elementMapping = new Mapping(sourcename,targetelement.getName());
						//int localMax = matchMixedTypes(sourcename,sourcetype,targetelement.getName(),complextypeelement,elementMapping);
						//elementMapping.setScore(localMax);

						//create MappingPartList
						//MappingPartList elementPartList = new MappingPartList();
						
						//add elementMapping to MappingPartList
						//elementPartList.addMapping(elementMapping);
						
						//add MAppingPartList to mapping
						//mapping.addMappingPartList(elementPartList);
						//elementmatchinglist.put(sourcename,targetelement.getName(),elementMapping);
						typeMatrix.setValue(elementMapping);
						//typeMatrix.setValue(sourcename,targetelement.getName(),localMax);
						
						
						//int localmax = matchMixedTypes(sourcename,sourcetype,targetelement.getName(),complextypeelement,mapping);
						//System.out.println("localmax: "+localmax);
						//typematrix.setValue(sourcename,targetelement.getName(),localmax);
					}
				
				}
				typeMatrix.computeMatches();
				Mapping typeMapping = typeMatrix.getMapping();
				return typeMapping;
				/*
				 *convert output from scoremap
				 * into elementmappings i.e
				 * build for each mapping from the mappinglist
				 * the corresponding elementmappinglist from
				 * the temporary list of all elementmappings
				 */
				
				//computeTypeMapping(mapping,typematrix.getMaxMappings(),elementmatchinglist);
			}
			else
			{
				//complextype has no elements
				Mapping nullMapping = new Mapping(sourcename,targetname);
				nullMapping.setScore(0);
				return nullMapping;
			}
		}
		else 
		{
			Mapping nullMapping = new Mapping(sourcename,targetname);
			nullMapping.setScore(0);
			return nullMapping;
		}
		
	}
	
	/*
	 * matches two complex types by building all possible combinations
	 * of their elements
	 */
	private Mapping matchComplexTypes(String sourcename,ComplexType sourcetype,String targetname,ComplexType targettype)
	{
		int typeScore = 0;
		//Mapping typeMapping;
		/*test for identical types
		if(sourcetype.getName().equals(targettype.getName()))
		{
			if(sourcetype.getNameSpace().equals(targettype.getNameSpace()))
			{
				System.out.println("\nidentical types: "+sourcetype.getName()+", "+sourcetype.getNameSpace()+"\n");
				typescore = matchIdenticalTypes(sourcetype);
				mapping.setScore(typescore);
				return typescore;
			}
		}*/
		
		//test for specialcase array
		if(sourcetype.isArray())
		{
			if(targettype.isArray())
			{
				//both types are arrays
				return matchArrayTypes(sourcetype,targettype);
				
				
				/*create typemappinglist
				TypeMappingList typeMappingList = new TypeMappingList();
				//get typemapping for arraytypes
				TypeMapping arrayMapping = matchArrayTypes(sourcetype,targettype);
				typescore = arrayMapping.getScore();
				//put arraymapping in typemappinglist
				typeMappingList.add(arrayMapping);
				//add typemappinglist to mapping
				mapping.add(typeMappingList);*/
			}
			else
			{
				//one type array one not => score 0
				Mapping nullMapping = new Mapping(sourcename,targetname);
				nullMapping.setScore(0);
				return nullMapping;
			}
		}
		else
		{
			
			if(targettype.isArray())
			{
				//one type array one not => score 0
				Mapping nullMapping = new Mapping(sourcename,targetname);
				nullMapping.setScore(0);
				return nullMapping;
			}
			else
			{
				//none of the types is array
				//ScoreMap typematrix = new ScoreMap(sourcetype.getElements().size(),targettype.getElements().size(),sourcename,targetname);
				int Score = 0;
				
				if(sourcetype.getTotalLeaves() == targettype.getTotalLeaves())
				{
					if(sourcetype.getNumberOfLeafElements()!= targettype.getNumberOfLeafElements())
					{
						if(sourcetype.getNumberOfNonLeafElements() != targettype.getNumberOfNonLeafElements())
						{
							System.out.println(sourcename+" = "+targetname+" : "+sourcetype.getTotalLeaves());
							System.out.println("\n types with different leaves: "+(sourcetype.getNumberOfLeafElements()-targettype.getNumberOfLeafElements())+", "+(sourcetype.getNumberOfNonLeafElements()-targettype.getNumberOfNonLeafElements()));
						}
					}
				}
				//compare groupings
				if(sourcetype.getGrouping() == targettype.getGrouping())
				{
					typeScore =  typeScore+groupingBonus;
				}
				if((sourcetype.getGrouping() == 1)&&(targettype.getGrouping() == 1))
				{
					/*
					 * both types are sequences
					 */
					
					//System.out.println("\n complexTypematch "+sourcename+", "+targetname+"\n");
					//create new scoremap for sourcename and targetname
					
					//ScoreMap typematrix = new ScoreMap(1,1,sourcename,targetname);
					
					//System.out.println("Sequences in "+sourcetype.getName()+" and "+targettype.getName());
					//both types are sequences
					Sequence sourcesequence = generateSequence(sourcetype);
					Sequence targetsequence = generateSequence(targettype);
					//match source- and targetsequence
					Mapping typeMapping = matchSequences(sourcetype,sourcesequence,targettype,targetsequence);
					//add boni to score
					typeMapping.addScoreBonus(typeScore);
					return typeMapping;
					//typematrix.setValue(sourcetype.getName(),targettype.getName(),typescore);
					//int testscore = typematrix.getMaxScore();
					
					/*int testscore = typescore;
					
					Match test = new Match(sourcetype.getName(),targettype.getName(),typescore,false);
					Mapping testmap = new Mapping();
					testmap.addMatch(test);
					ArrayList<Mapping> testlist = new ArrayList<Mapping>();
					testlist.add(testmap);*/
					/*
					 * add maptch for sourcetype and targettype
					 * with keys sourcename and target name to typemapping
					 */
					//mapping.putTypeMapping(sourcename,targetname,testlist);
					//System.out.println("testscore: "+testscore+" typescore: "+typescore+" matrixentries: "+typematrix.getEntries());
					//System.out.println("sourcename: "+sourcename+" targetname: "+targetname);
					//Mapping testmapping = typematrix.getMaxMapping();
					/*System.out.println("\n*** complextype mapping ***");
					testmapping.printMapping();*/
					//System.out.println("\n+++ addtypemapping for complex types "+sourcetype.getName()+" and "+targettype.getName()+" +++");
					//Attention ????
					
					//add mappings from typematrix to servicemapping
					//mapping.addTypeMapping(sourcename,targetname,typematrix.getMaxMappings());
					//mapping.printTypeMapping();
				}
				else
				{
					//test for empty complextypes
					if((!sourcetype.isEmpty())&&(!targettype.isEmpty()))
					{					
						//atleast one type is no sequence => order can be neclected
					
						//create new temporary list
						//TypeMatchingList elementmatchinglist = new TypeMatchingList();
						
						//create new scoremap for source- and targettype
						ScoreMap typeMatrix = new ScoreMap(sourcetype.getElementList().size(),targettype.getElementList().size(),sourcename,targetname, edit);
						//iterate over elements of sourcetype
						for(ListIterator<Element> sourceelements = sourcetype.getElementList().listIterator();sourceelements.hasNext();)
						{
							Element sourceelement = sourceelements.next();
							//iterate over elements of targettype
							for(ListIterator<Element> targetelements = targettype.getElementList().listIterator();targetelements.hasNext();)
							{
								Element targetelement = targetelements.next();
								//System.out.println("source: "+sourceelement.getType()+" target: "+targetelement.getType());
								//get the typemapping for the elements
								Mapping elementMapping = matchElements(sourceelement,targetelement);

								//create MappingPartList
								//MappingPartList elementPartList = new MappingPartList();
								
								//add elementMapping to MappingPartList
								//elementPartList.addMapping(elementMapping);
								
								//add MAppingPartList to mapping
								//mapping.addMappingPartList(elementPartList);
								
								//store elementmapping in temporary list
								//elementmatchinglist.put(sourceelement.getName(),targetelement.getName(),elementMapping);
								//store score of element-matching in matrix
								typeMatrix.setValue(elementMapping);
							}
						}
						typeMatrix.computeMatches();
						Mapping typeMapping = typeMatrix.getMapping();
						return typeMapping;
						/*
						 *convert output from scoremap
						 * into elementmappings i.e
						 * build for each mapping from the mappinglist
						 * the corresponding elementmappinglist from
						 * the temporary list of all elementmappings
						 */
						
						//computeTypeMapping(mapping,typematrix.getMaxMappings(),elementmatchinglist);
					}
					else
					{
						//one complextype empty => null match
						Mapping nullMapping = new Mapping(sourcename,targetname);
						nullMapping.setScore(0);
						return nullMapping;
					}
				}
				//test
				//mapping.addTypeMapping(typematrix.getMaxMapping());
			}
		}
		
	}
	
	/*
	 * matches types of array-types
	 */
	private Mapping matchArrayTypes(ComplexType sourcetype,ComplexType targettype)
	{
		Mapping arrayMapping = new Mapping(sourcetype.getName(),targettype.getName(),sourcetype.getNodeIdentifier(),targettype.getNodeIdentifier());
		int arrayscore = -1;
		String sourcearraytype = sourcetype.getArrayType();
		String targetarraytype = targettype.getArrayType();
		//create new typemapping
		//TypeMapping mapping = new TypeMapping(sourcearraytype,targetarraytype);
		
		if(lookupTable.lookupSimpleType(sourcearraytype))
		{
			if(lookupTable.lookupSimpleType(targetarraytype))
			{
				//both array of simple type
				arrayscore = lookupTable.getMatchingScore(sourcearraytype,targetarraytype)*arrayBonus;
				//set mappingscore
				arrayMapping.setScore(arrayscore);
			}
			else
			{
				//mixed types
				ComplexType complextype = targetTypes.getType(targetarraytype);
				//create mapping for types
				Mapping typeMapping = matchMixedTypes(sourcetype.getName(),sourcearraytype,targettype.getName(),complextype);
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//add typePartList to arrayMapping
				arrayMapping.addMappingPartList(typePartList);
				
				/*create new typemappinglist
				TypeMappingList typeList = new TypeMappingList();
				TypeMapping subtypemapping = matchMixedTypes(sourcetype.getName(),sourcearraytype,targettype.getName(),complextype);
				//set score times arraybonus
				subtypemapping.setScore(subtypemapping.getScore()*arraybonus);
				//add subtypemapping to typelist
				typeList.add(subtypemapping);
				//add typelist to mapping
				mapping.add(typeList);*/
			}
		}
		else
		{
			if(lookupTable.lookupSimpleType(targetarraytype))
			{
				//mixed types
				ComplexType complextype = sourceTypes.getType(sourcearraytype);
				Mapping typeMapping = matchMixedTypes(sourcetype.getName(),complextype,targettype.getName(),targetarraytype);

				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//add typePartList to arrayMapping
				arrayMapping.addMappingPartList(typePartList);
				
				/*create new typemappinglist
				TypeMappingList typeList = new TypeMappingList();
				TypeMapping subtypemapping = matchMixedTypes(sourcetype.getName(),complextype,targettype.getName(),targetarraytype);
				//set score times arraybonus
				subtypemapping.setScore(subtypemapping.getScore()*arraybonus);
				//add subtypemapping to typelist
				typeList.add(subtypemapping);
				//add typelist to mapping
				mapping.add(typeList);*/
				
				//arrayscore = matchMixedTypes(sourcetype.getName(),complextype,targettype.getName(),targetarraytype,mapping)*arraybonus;
			
			}
			else
			{
				//both complex types
				ComplexType complexsourcetype = sourceTypes.getType(sourcearraytype);
				ComplexType complextargettype = targetTypes.getType(targetarraytype);
				Mapping typeMapping = matchComplexTypes(sourcetype.getName(),complexsourcetype,targettype.getName(),complextargettype);
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//add typePartList to arrayMapping
				arrayMapping.addMappingPartList(typePartList);
				
				
				
				//arrayscore = matchComplexTypes(sourcetype.getName(),complexsourcetype,targettype.getName(),complextargettype,mapping)*arraybonus;
			}
		}
		arrayMapping.setScore(arrayMapping.getScore()*arrayBonus);
		return arrayMapping;
	}
	
	/*
	 * matches two elements
	 */
	private Mapping matchElements(Element sourceelement,Element targetelement)
	{
		//create new typemapping
		Mapping elementMapping = new Mapping(sourceelement.getName(),targetelement.getName(),sourceelement.getNodeIdentifier(),targetelement.getNodeIdentifier());
		
		//System.out.println("matchelements: "+sourceelement.getName()+", "+targetelement.getName());
		//ScoreMap elementmatrix = new ScoreMap(source)
		int elementScore = 0;
		//int score = 0;
		
		//compare minoccur/maxoccurvalues
		if((!(sourceelement.getMinOccur() <  0))&&(!(targetelement.getMinOccur() < 0)))
		{
			if(sourceelement.getMinOccur() == targetelement.getMinOccur())
			{
				
				elementScore++;
			}
		}
		if((!(sourceelement.getMaxOccur() <  0))&&(!(targetelement.getMaxOccur() < 0)))
		{
			if(sourceelement.getMaxOccur() == targetelement.getMaxOccur())
			{
				
				elementScore++;
			}
		}
		if(!lookupTable.lookupSimpleType(sourceelement.getType()))
		{
			//source complex 
			if(lookupTable.lookupSimpleType(targetelement.getType()))
			{
				//target simple
				ComplexType complextype = sourceTypes.getType(sourceelement.getType());
				String simpletype = targetelement.getType();
				//create Mapping for types of elements
				Mapping typeMapping = matchMixedTypes(sourceelement.getName(),complextype,targetelement.getName(),targetelement.getType());
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//addtypePartList to elementMapping
				elementMapping.addMappingPartList(typePartList);
			}
			else
			{
				//target complex
				//get complex types
				ComplexType complexsourcetype = sourceTypes.getType(sourceelement.getType());
				ComplexType complextargettype = targetTypes.getType(targetelement.getType());
				//recursion
				//System.out.println("recursion: 1: "+complexsourcetype.getName()+" 2: "+complextargettype.getName());
				/*ComplexTypeMatcher complexmatcher = new ComplexTypeMatcher(complexsourcetype,complextargettype,lookuptable,sourcelist,targetlist,mapping);
				if(complexsourcetype.getGrouping() == complextargettype.getGrouping())
				{
					scorematrix.setValue(complexsourcetype.getName(),complextargettype.getName(),complexmatcher.getMaxScore()+maxscore);
				}
				else
				{
					scorematrix.setValue(complexsourcetype.getName(),complextargettype.getName(),complexmatcher.getMaxScore());
				}*/

				//	create Mapping for types of elements
				Mapping typeMapping = matchComplexTypes(sourceelement.getName(),complexsourcetype,targetelement.getName(),complextargettype);
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//addtypePartList to elementMapping
				elementMapping.addMappingPartList(typePartList);
				
				//elementMapping = matchComplexTypes(sourceelement.getName(),complexsourcetype,targetelement.getName(),complextargettype);
				//elementScore = elementScore+matchComplexTypes(sourceelement.getName(),complexsourcetype,targetelement.getName(),complextargettype,mapping);
				//matchComplexTypes(complexsourcetype,complextargettype);
			}
		}
		else
		{
			//source simple
			if(lookupTable.lookupSimpleType(targetelement.getType()))
			{
				//target simple
				//System.out.println("\nmatchelements both simple 1: "+sourceelement.getType()+" 2: "+targetelement.getType());
				elementMapping.setScore(lookupTable.getMatchingScore(sourceelement.getType(),targetelement.getType()));
				
				
			}				
			else
			{
				//target complex
				ComplexType complextype = targetTypes.getType(targetelement.getType());
				//String simpletype = firstelement.getType();
				//MixedTypeMatcher mixedmatcher = new MixedTypeMatcher(complextype,simpletype,lookuptable,targetlist,mapping);
				//scorematrix.setValue(firstelement.getName(),complextype.getName(),mixedmatcher.getMaxScore());

				//create Mapping for types of elements
				Mapping typeMapping = matchMixedTypes(sourceelement.getName(),sourceelement.getType(),targetelement.getName(),complextype);
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//addtypePartList to elementMapping
				elementMapping.addMappingPartList(typePartList);
				
				//elementMapping = matchMixedTypes(sourceelement.getName(),sourceelement.getType(),targetelement.getName(),complextype);
				//elementScore = elementScore+matchMixedTypes(sourceelement.getName(),sourceelement.getType(),targetelement.getName(),complextype,mapping);
			}			
		}		
		elementMapping.addScoreBonus(elementScore);
		return elementMapping;
	}
	
	/*
	 * matches two sequences by matching their elements with respect to the sequenceorder
	 */
	private Mapping matchSequences(ComplexType sourcetype,Sequence source,ComplexType targettype,Sequence target)
	{
		//System.out.println("\n matchsequences "+sourcename+", "+targetname+"\n");
		Mapping sequenceMapping = new Mapping(sourcetype.getName(),targettype.getName(),sourcetype.getNodeIdentifier(),targettype.getNodeIdentifier());
		//System.out.println("sequencelength: "+source.getLength()+" and "+target.getLength());
		int sequenceScore = 0;
		int elementScore = 0;
		ArrayList<Integer> index = new ArrayList<Integer>();
		//create list of mappings
		//ArrayList<Mapping> mappinglist = new ArrayList<Mapping>();
		
		//create new typemappinglist
		//TypeMappingList typeMappingList = new TypeMappingList();
		
		if(source.getLength() == target.getLength())
		{
			//create new mappingPartlist
			MappingPartList mappingPartList = new MappingPartList();
			//Mapping tmpmapping = new Mapping();
			
			//sequences of equal length => match corresponding elements
			for(int i=0;i<source.getLength();i++)
			{
				Element sourceelement = source.getElement(i);
				Element targetelement = target.getElement(i);
				//match the elements
				Mapping elementMapping = matchElements(sourceelement,targetelement);
				//elementScore = elementMapping.getScore();
				//add elementmapping to mappingPartlist
				mappingPartList.addMapping(elementMapping);
				//sequenceScore = sequenceScore+elementScore;
				/*create new match for the elements
				Match match = new Match(sourceelement.getName(),targetelement.getName(),elementscore,false);
				//add match to mapping
				tmpmapping.addMatch(match);
				//store mapping in list
				mappinglist.add(tmpmapping);
				sequencescore = sequencescore+elementscore;
				*/
			}
			//add mappingPartlist to mapping
			sequenceMapping.addMappingPartList(mappingPartList);
			///sequenceScore = sequenceScore+sequenceBonus;
			sequenceMapping.addScoreBonus(sequenceBonus);
			return sequenceMapping;
		}
		//sequences of different length
		else if(source.getLength() < target.getLength())
		{
			/*
			 * create an arraylist of mappingpartlists
			 * for storing alternatives 
			 */
			ArrayList<MappingPartList> tmplist = new ArrayList<MappingPartList>();
			//shift source-sequence over targetsequence
			for(int j=0;j<=target.getLength()-source.getLength();j++)
			{
				int tmpScore = 0;
				//create new mappingpartlist
				MappingPartList mappingPartList = new MappingPartList();
				for(int i=0;i<source.getLength();i++)
				{
					Element sourceelement = source.getElement(i);
					Element targetelement = target.getElement(i+j);
					//match elements
					Mapping elementMapping = matchElements(sourceelement,targetelement);
					elementScore = elementMapping.getScore();
					tmpScore = tmpScore+elementScore;
					//add elementmapping to mappingpartlist
					mappingPartList.addMapping(elementMapping);
					
					/*create new match for the elements
					Match match = new Match(sourceelement.getName(),targetelement.getName(),elementscore,false);
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
		else if(source.getLength() > target.getLength())
		{
			/*
			 * create an arraylist of mappingpartlists
			 * for storing alternatives 
			 */
			ArrayList<MappingPartList> tmplist = new ArrayList<MappingPartList>();
			
			//shift target-sequence over source-sequence
			for(int j=0;j<=source.getLength()-target.getLength();j++)
			{
				int tmpScore = 0;
				//create new mappingpartlist
				MappingPartList mappingPartList = new MappingPartList();
				for(int i=0;i<target.getLength();i++)
				{
					Element sourceElement = source.getElement(i+j);
					Element targetElement = target.getElement(i);
					//match elements
					Mapping elementMapping = matchElements(sourceElement,targetElement);
					elementScore = elementMapping.getScore();
					tmpScore = tmpScore+elementScore;
					//add elementmapping to mappingpartlist
					mappingPartList.addMapping(elementMapping);
					
					/*elementscore = matchElements(sourceelement,targetelement,mapping);
					tmpscore = tmpscore+elementscore;
					Match match = new Match(sourceelement.getName(),targetelement.getName(),elementscore,false);
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
	
	private Sequence generateSequence(ComplexType type)
	{
		Sequence sequence = new Sequence(type.getElementList().size());
		for(ListIterator<Element> elements = type.getElementList().listIterator();elements.hasNext();)
		{
			Element element = elements.next();
			sequence.setElement(elements.nextIndex()-1,element);
		}
		return sequence;
	}
	
	/*
	 *convert output from scoremap
	 * into elementmappings i.e
	 * build for each mapping from the mappinglist
	 * the corresponding typemappinglist from
	 * the temporary list of all typemappings
	 */
	/*private void computeTypeMapping(TypeMapping mapping,ArrayList<Mapping>maxElementMappings,TypeMatchingList temp)
	{
		//temp.print();
		
		for(int i=0;i<maxElementMappings.size();i++)
		{
			Mapping tmpMapping = maxElementMappings.get(i);
			//create new elementmappinglist for mapping
			TypeMappingList elementMappingList = new TypeMappingList();
			//get matches from mapping
			for(Iterator<String> sourceIterator = tmpMapping.getSourceNameIterator();sourceIterator.hasNext();)
			{
				String sourceElement = sourceIterator.next();
				for(Iterator<String> targetIterator = tmpMapping.getTargetNameIterator(sourceElement);targetIterator.hasNext();)
				{
					String targetElement = targetIterator.next();
					//get match
					Match match = tmpMapping.getMatch(sourceElement,targetElement);
					if(match.isAnytype())
					{
						mapping.setAnymatch(true);
						//System.out.println("anymatch !!!!!!!!!!!!!!!!!!!!!!!");
					}
					else
					{
						//load corresponding elementmapping from temporary list
						//System.out.println("row: "+match.getRow()+", column: "+match.getColumn());
						
						TypeMapping elMapping = temp.get(match.getRow(),match.getColumn());
						//add elementmapping to elementmappinglist
						elementMappingList.add(elMapping);
					}
				}
				mapping.add(elementMappingList);
			}
		}
	}*/
	
	
	
	public Mapping getServiceMapping()
	{
		//System.out.println("MappingMap-keys: ");
		//mappingmap.printKeys();
		//MappingList hlp = mappingmap.get(maximummatch.getRow());
		//System.out.println("MappingList-keys");
		//hlp.printKeys();
		//return hlp.get(maximummatch.getColumn());
		return serviceMapping;
	}



	public boolean isEdit() 
	{
		return edit;
	}
	



	public void setEdit(boolean edit) 
	{
		this.edit = edit;
	}
	
	
	
}

