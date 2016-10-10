package de.dfki.wsdlanalyzer.matcher;

import java.util.ArrayList;


import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import de.dfki.wsdlanalyzer.mapping.Mapping;
import de.dfki.wsdlanalyzer.mapping.MappingPartList;
import de.dfki.wsdlanalyzer.matrix.ScoreMap;
import de.dfki.wsdlanalyzer.parser.SimpleTypeLookupTable;
import de.dfki.wsdlanalyzer.types.Attribute;
import de.dfki.wsdlanalyzer.types.BindingList;
import de.dfki.wsdlanalyzer.types.ComplexType;
import de.dfki.wsdlanalyzer.types.Element;
import de.dfki.wsdlanalyzer.types.ElementList;
import de.dfki.wsdlanalyzer.types.Message;
import de.dfki.wsdlanalyzer.types.MessageList;
import de.dfki.wsdlanalyzer.types.MessageParameter;
import de.dfki.wsdlanalyzer.types.NodeIdentifier;
import de.dfki.wsdlanalyzer.types.Operation;
import de.dfki.wsdlanalyzer.types.OperationList;
import de.dfki.wsdlanalyzer.types.PortType;
import de.dfki.wsdlanalyzer.types.PortTypeList;
import de.dfki.wsdlanalyzer.types.Sequence;
import de.dfki.wsdlanalyzer.types.Service;
import de.dfki.wsdlanalyzer.types.TypeList;
import de.dfki.wsdlanalyzer.types.WsdlFile;
import de.dfki.wsdlanalyzer.wordnet.DictInstance;

import net.didion.jwnl.JWNLException;














/**
 * class for matching two services
 */
public class WsdlServiceMatcher 
{
	private String requirementsFileName,candidateFileName;
	private Service requirementsService,candidateService;
	//private WsdlFile requirementsfile,candidatefile;
	private BindingList requirementsBindings,candidateBindings;
	private PortTypeList requirementsPortTypes,candidatePortTypes;
	private MessageList requirementsMessages,candidateMessages;
	private TypeList requirementsTypes,candidateTypes;
	private ElementList requirementsElements,candidateElements;
	private SimpleTypeLookupTable lookupTable;
	private ScoreMap scoreMatrix;
	private int minScore = 0;
	private int maxScore = 10;
	private int groupingBonus = 10;
	private int arrayBonus = 2;
	private int sequenceBonus = 5;
	//switch for tree-edit-distance-like matching
	private boolean edit;
	
	private Mapping serviceMapping;
	private StopWords stopWords;
	/**
	 * switches to turn on/off different match strategies
	 * look at WsdlFileMatcher
	 */
	private int matchStrategy;
	
	/**
	 * weight factors for structure/name/wordnet-score
	 * default =1
	 */
	private int structureWeight;
	private int nameWeight;
	private int wordnetWeight;
	
	/**
	 * switch for strict/relaxed sequence matching
	 * relaxed means the order of the sequence is not respected
	 * i.e arbitrary permutation of the elements possible
	 */
	private boolean strictSequence;
	
	private DictInstance dictionary;
	//	logger
	//protected static Log log = LogFactory.getLog(WsdlServiceMatcher.class.getName());
	/**
	 * 
	 * @param requirements Service from requirementsfile
	 * @param requirementsfile WsdlFile of requirementsfile
	 * @param candidate Service from candidatefile
	 * @param candidatefile WsdlFile of candidatefile
	 * @param l SympleTypeLookupTable for matching requirements- and candidatefile
	 * @param strategy number of mapping-strategy
	 * @param dict wordnet dictionary-instance
	 * @deprecated
	 */
	public WsdlServiceMatcher(Service requirements,WsdlFile requirementsfile,Service candidate, WsdlFile candidatefile, SimpleTypeLookupTable l,int strategy,DictInstance dict)
	{
		edit = true;
		matchStrategy = strategy;
		dictionary = dict;
		
		requirementsFileName = requirementsfile.getWsdlFileName();
		requirementsService = requirements;
		requirementsBindings = requirementsfile.getBindings();
		requirementsPortTypes = requirementsfile.getPorttypelist();
		requirementsMessages = requirementsfile.getMessageList();
		requirementsTypes = requirementsfile.getTypeList();
		requirementsElements = requirementsfile.getElementlist();
		
		candidateFileName = candidatefile.getWsdlFileName();
		candidateService = candidate;
		candidateBindings = candidatefile.getBindings();
		candidatePortTypes = candidatefile.getPorttypelist();
		candidateMessages = candidatefile.getMessageList();
		candidateTypes = candidatefile.getTypeList();
		candidateElements = candidatefile.getElementlist();
		
		lookupTable = l;
		stopWords = new StopWords();
		
		serviceMapping = new Mapping(requirementsService.getName(),candidateService.getName(),requirementsService.getNodeIdentifier(),candidateService.getNodeIdentifier());
		
	}
	
	/**
	 * 
	 * @param requirements Service from requirementsfile
	 * @param requirementsfile WsdlFile of requirementsfile
	 * @param candidate Service from candidatefile
	 * @param candidatefile WsdlFile of candidatefile
	 * @param l SympleTypeLookupTable for matching requirements- and candidatefile
	 * @param strategy number of mapping-strategy
	 * @param strucWeight weight for structure scores
	 * @param namWeight weight for name scores
	 * @param wordntWeight weight for wordnetscores
	 * @param dict wordnet dictionary-instance
	 */
	public WsdlServiceMatcher(Service requirements,WsdlFile requirementsfile,Service candidate, WsdlFile candidatefile, SimpleTypeLookupTable l,int strategy,int strucWeight,int namWeight,int wordntWeight,boolean strictseq,DictInstance dict)
	{
		edit = true;
		matchStrategy = strategy;
		structureWeight = strucWeight;
		nameWeight = namWeight;
		wordnetWeight = wordntWeight;
		strictSequence = strictseq;
		dictionary = dict;
		
		requirementsFileName = requirementsfile.getWsdlFileName();
		requirementsService = requirements;
		requirementsBindings = requirementsfile.getBindings();
		requirementsPortTypes = requirementsfile.getPorttypelist();
		requirementsMessages = requirementsfile.getMessageList();
		requirementsTypes = requirementsfile.getTypeList();
		requirementsElements = requirementsfile.getElementlist();
		
		candidateFileName = candidatefile.getWsdlFileName();
		candidateService = candidate;
		candidateBindings = candidatefile.getBindings();
		candidatePortTypes = candidatefile.getPorttypelist();
		candidateMessages = candidatefile.getMessageList();
		candidateTypes = candidatefile.getTypeList();
		candidateElements = candidatefile.getElementlist();
		
		lookupTable = l;
		stopWords = new StopWords();
		
		//strictSequence = true;
		
		serviceMapping = new Mapping(requirementsService.getName(),candidateService.getName(),requirementsService.getNodeIdentifier(),candidateService.getNodeIdentifier());
		
	}
	
	
	
	/**
	 * matches two services by building all possible combinations
	 * of their ports/bindings/porttypes
	 */
	public int matchServices() throws JWNLException
	{
		
		//iterate over requirementsports
		for(Iterator<String> requirementsPorts = requirementsService.keySet().iterator();requirementsPorts.hasNext();)
		{
			//get requirementsportname
			String requirementsPortName = requirementsPorts.next();
			//get binding for port
			String requirementsBinding = requirementsService.get(requirementsPortName).getBinding();
			//get name of requirementsporttype
			String requirementsName = requirementsBindings.get(requirementsBinding).getPortType();
			PortType requirementsPortType = requirementsPortTypes.get(requirementsName);
						
			//iterate over all candidateports 
			for(Iterator<String> candidatePorts = candidateService.keySet().iterator();candidatePorts.hasNext();)
			{
				//get candidateportname
				String candidatePortName = candidatePorts.next();
				Mapping portMapping = new Mapping(requirementsPortName,candidatePortName,requirementsService.get(requirementsPortName).getNodeIdentifier(),candidateService.get(candidatePortName).getNodeIdentifier());
				//get binding for port
				String candidateBinding = candidateService.get(candidatePortName).getBinding();
				Mapping bindingMapping = new Mapping(requirementsBinding,candidateBinding,requirementsBindings.get(requirementsBinding).getNodeIdentifier(),candidateBindings.get(candidateBinding).getNodeIdentifier());
				//get name of candidateporttype
				String candidateName = candidateBindings.get(candidateBinding).getPortType();
				PortType candidatePortType = candidatePortTypes.get(candidateName);
				
				
				//match requirements and candidateporttype
				Mapping portTypeMapping = matchPortTypes(requirementsPortType,candidatePortType);
				
				/*if(edit)
				{
					int malus = java.lang.Math.abs(requirementsPortTypes.size()-candidatePortTypes.size());
					int portTypeScore = portTypeMapping.getScore()-malus*maxScore;
					if(portTypeScore < 0)
					{
						portTypeMapping.setScore(minScore);
					}
					else
					{
						portTypeMapping.setScore(portTypeScore);
					}
				}*/
				
				
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
		/*
		 * compute matchingscore according to matchstrategy and
		 * choosen weight
		 */
		switch(matchStrategy)
		{
		case 0:
		{
			//multiply score with structure-weight
			//serviceMapping.setScore(structureWeight*serviceMapping.getScore());
			break;
		}
		case 1:
		{
			//get namematch for services
			int nameScore = nameWeight*matchNames(requirementsService.getName(),candidateService.getName());
			if(nameScore > 0)
			{
				serviceMapping.addScoreBonus(nameScore);
			}
			break;
		}
		case 2:
		{
			//get wordnet-matcg for service names
			int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsService.getToken(),requirementsService.getName(),candidateService.getToken(),candidateService.getName());
			if(wordnetScore > 0)
			{
				serviceMapping.addScoreBonus(wordnetScore);
			}
			break;
		}
		case 3:
		{
			//multiply score with structure-weight
			//serviceMapping.setScore(structureWeight*serviceMapping.getScore());
			//get namematch for services
			int nameScore = nameWeight*matchNames(requirementsService.getName(),candidateService.getName());
			if(nameScore > 0)
			{
				serviceMapping.addScoreBonus(nameScore);
			}
			break;
		}
		case 4:
		{
			//multiply score with structure-weight
			//serviceMapping.setScore(structureWeight*serviceMapping.getScore());
			//get wordnet-match for service names
			int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsService.getToken(),requirementsService.getName(),candidateService.getToken(),candidateService.getName());
			if(wordnetScore > 0)
			{
				serviceMapping.addScoreBonus(wordnetScore);
			}
			break;
		}
		case 5:
		{
			//get namematch for services + wordnet -match for service-names
			int nameScore = nameWeight*matchNames(requirementsService.getName(),candidateService.getName())+wordnetWeight*simpleWordnetMatch(requirementsService.getToken(),requirementsService.getName(),candidateService.getToken(),candidateService.getName());
			if(nameScore > 0)
			{
				serviceMapping.addScoreBonus(nameScore);
			}
			break;
		}
		case 6:
		{
			//multiply score with structure-weight
			//serviceMapping.setScore(structureWeight*serviceMapping.getScore());
			//get namematch for services + wordnet -match for service-names
			int nameScore = nameWeight*matchNames(requirementsService.getName(),candidateService.getName())+wordnetWeight*simpleWordnetMatch(requirementsService.getToken(),requirementsService.getName(),candidateService.getToken(),candidateService.getName());
			if(nameScore > 0)
			{
				serviceMapping.addScoreBonus(nameScore);
			}
			break;
		}
		}
		return serviceMapping.getScore();
	}
	
	/**
	 * matches two portTypes by building all possible combinations
	 * of requirements and candidate operations
	  */
	public Mapping matchPortTypes(PortType requirementsPortType,PortType candidatePortType) throws JWNLException
	{
		//get the operationlists for the porttypes
		OperationList requirementsOperations = requirementsPortType.getOperationlist();
		OperationList candidateOperations = candidatePortType.getOperationlist();
		//System.out.println("# of operations: "+requirementsOperations.length()+", "+candidateOperations.length());
		//test for empty operationlists
		if((!requirementsOperations.isEmpty())&&(!candidateOperations.isEmpty()))
		{
			//create operationmatrix
			ScoreMap operationMatrix = new ScoreMap(requirementsOperations.length(),candidateOperations.length(),requirementsPortType.getName(), candidatePortType.getName(), edit);
			int porttypescore = -1;
			//System.out.println("matchporttypes\n");
			//create new porttypemapping for requirements and candidateportype
			//Mapping mapping = new Mapping(requirementsporttype.getName(),candidateporttype.getName());
			
			
			//iterate over requirements-operations
			for(Iterator<String> requirementsOperationNames = requirementsOperations.keySet().iterator();requirementsOperationNames.hasNext();)
			{
				String requirementsName = requirementsOperationNames.next();
				//System.out.println("requirementsoperation: "+requirementsName);
				
				//get requirementsoperation
				Operation requirementsOperation = requirementsOperations.getOperation(requirementsName);
				
				//iterate over the candidate operations
				for(Iterator<String> candidateOperationNames = candidateOperations.keySet().iterator();candidateOperationNames.hasNext();)
				{
					String candidateName = candidateOperationNames.next();
					//System.out.println("candidateoperation: "+candidateName);
					
					//get candidateoperation
					Operation candidateOperation = candidateOperations.getOperation(candidateName);
					
					//match requirements and candidateoperation
					Mapping operationMapping = matchOperations(requirementsOperation,candidateOperation);
					
					
					//write matchingscore in matrix
					operationMatrix.setValue(operationMapping);
					
				}
				
			}
			//compute matches from operationmatrix
			operationMatrix.computeMatches();
			Mapping portTypeMapping = operationMatrix.getMapping();
			//set NodeIdentifier
			portTypeMapping.setRequirementsIdentifier(requirementsPortType.getNodeIdentifier());
			portTypeMapping.setCandidateIdentifier(candidatePortType.getNodeIdentifier());
			if(portTypeMapping.isAnymatch())
			{
				//System.out.println("portType: anymatch");
			}
			//portTypeMapping.printMapping(4);
			switch(matchStrategy)
			{
			case 0:
			{
				//multiply score with structureweight
				//portTypeMapping.setScore(structureWeight*portTypeMapping.getScore());
				break;
			}
			case 1:
			{
				//get namematch for portTypes
				int nameScore = nameWeight*matchNames(requirementsPortType.getName(),candidatePortType.getName());
				if(nameScore > 0)
				{
					portTypeMapping.addScoreBonus(nameScore);
				}
				break;
			}
			case 2:
			{
				//get wordnet-matcg for portType names
				int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsPortType.getToken(),requirementsPortType.getName(),candidatePortType.getToken(),candidatePortType.getName());
				if(wordnetScore > 0)
				{
					portTypeMapping.addScoreBonus(wordnetScore);
				}
				break;
			}
			case 3:
			{
				//multiply score with structureweight
				//portTypeMapping.setScore(structureWeight*portTypeMapping.getScore());
				//get namematch for portTypes
				//System.out.println("nameweight: "+nameWeight);
				int nameScore = nameWeight*matchNames(requirementsPortType.getName(),candidatePortType.getName());
				//System.out.println("porttype-namescore: "+nameScore);
				//System.out.println("porttype-score: "+portTypeMapping.getScore());
				if(nameScore > 0)
				{
					portTypeMapping.addScoreBonus(nameScore);
				}
				break;
			}
			case 4:
			{
				//multiply score with structureweight
				//portTypeMapping.setScore(structureWeight*portTypeMapping.getScore());
				//get wordnet-match for portType names
				int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsPortType.getToken(),requirementsPortType.getName(),candidatePortType.getToken(),candidatePortType.getName());
				if(wordnetScore > 0)
				{
					portTypeMapping.addScoreBonus(wordnetScore);
				}
				break;
			}
			case 5:
			{
				//get namematch for portTypes + wordnet -match for portType-names
				int nameScore = nameWeight*matchNames(requirementsPortType.getName(),candidatePortType.getName())+wordnetWeight*simpleWordnetMatch(requirementsPortType.getToken(),requirementsPortType.getName(),candidatePortType.getToken(),candidatePortType.getName());
				if(nameScore > 0)
				{
					portTypeMapping.addScoreBonus(nameScore);
				}
				break;
			}
			case 6:
			{
				//multiply score with structureweight
				//portTypeMapping.setScore(structureWeight*portTypeMapping.getScore());
				//get namematch for portTypes + wordnet -match for portType-names
				int nameScore = nameWeight*matchNames(requirementsPortType.getName(),candidatePortType.getName())+wordnetWeight*simpleWordnetMatch(requirementsPortType.getToken(),requirementsPortType.getName(),candidatePortType.getToken(),candidatePortType.getName());
				if(nameScore > 0)
				{
					portTypeMapping.addScoreBonus(nameScore);
				}
				break;
			}
			}
						
			
			return portTypeMapping;
		}
		else
		{
			Mapping nullMapping = new Mapping(requirementsPortType.getName(),candidatePortType.getName(),requirementsPortType.getNodeIdentifier(),candidatePortType.getNodeIdentifier());
			nullMapping.setScore(0);
			return nullMapping;
		}
	}
	
	/**
	 * matches two operations by matching their
	 * input, output and fault messages
	 */
	private Mapping matchOperations(Operation requirementsOp,Operation candidateOp) throws JWNLException
	{
		boolean mapped = false;
		//create new mapping for requirementsop and candidateop
		Mapping operationMapping = new Mapping(requirementsOp.getName(),candidateOp.getName(),requirementsOp.getNodeIdentifier(),candidateOp.getNodeIdentifier());
		
		//create new MappingPartList for input/output/fault-messages
		MappingPartList messagePartList = new MappingPartList();
		
		//match input-messages
		if((requirementsOp.getInput()!=null)&&(candidateOp.getInput()!=null))
		{
			//System.out.println("requirementsinput: "+requirementsop.getInput());
			Message requirementsInputMessage = requirementsMessages.getMessage(requirementsOp.getInput());
			//System.out.println("candidateinput: "+candidateop.getInput());
			Message candidateInputMessage = candidateMessages.getMessage(candidateOp.getInput());
			
			//Mapping inputMapping = matchMessages(requirementsInputMessage,candidateInputMessage);
			Mapping inputMapping = new Mapping(requirementsOp.getInput(),candidateOp.getInput(),requirementsOp.getInputId(),candidateOp.getInputId());
			Mapping messageMapping = matchMessages(requirementsInputMessage,candidateInputMessage);
			//set nodeIdentifier
			/*
			inputMapping.setRequirementsIdentifier(requirementsOp.getInputId());
			inputMapping.setCandidateIdentifier(candidateOp.getInputId());
			*/
			messageMapping.setRequirementsIdentifier(requirementsInputMessage.getNodeIdentifier());
			messageMapping.setCandidateIdentifier(candidateInputMessage.getNodeIdentifier());
			MappingPartList inputPartList = new MappingPartList();
			inputPartList.addMapping(messageMapping);
			inputMapping.addMappingPartList(inputPartList);
			//System.out.println("inputmapping: "+inputMapping.getRequirementsName()+", "+inputMapping.getCandidateName());
			//add inputmapping to MappingPartList
			messagePartList.addMapping(inputMapping);
			mapped = true;
		}
		
		
		//System.out.println("inputscore: "+inputscore);
		
		//match output-messages
		if((requirementsOp.getOutput()!=null)&&(candidateOp.getOutput()!=null))
		{
			//System.out.println("requirementsoutput: "+requirementsOp.getOutput());
			Message requirementsOutputMessage = requirementsMessages.getMessage(requirementsOp.getOutput());
			//System.out.println("candidateoutput: "+candidateOp.getOutput());
			Message candidateOutputMessage = candidateMessages.getMessage(candidateOp.getOutput());
			
			//Mapping outputMapping = matchMessages(requirementsOutputMessage,candidateOutputMessage);
			Mapping outputMapping = new Mapping(requirementsOp.getOutput(),candidateOp.getOutput(),requirementsOp.getOutputId(),candidateOp.getOutputId());
			Mapping messageMapping = matchMessages(requirementsOutputMessage,candidateOutputMessage);
			//set nodeIdentifier
			/*
			inputMapping.setRequirementsIdentifier(requirementsOp.getInputId());
			inputMapping.setCandidateIdentifier(candidateOp.getInputId());
			*/
			messageMapping.setRequirementsIdentifier(requirementsOutputMessage.getNodeIdentifier());
			messageMapping.setCandidateIdentifier(candidateOutputMessage.getNodeIdentifier());
			MappingPartList outputPartList = new MappingPartList();
			outputPartList.addMapping(messageMapping);
			outputMapping.addMappingPartList(outputPartList);
			//System.out.println("inputmapping: "+inputMapping.getRequirementsName()+", "+inputMapping.getCandidateName());
			//add inputmapping to MappingPartList
			messagePartList.addMapping(outputMapping);
			mapped = true;
		}
		
		
		//System.out.println("outputscore: "+outputscore);
		
		//match fault-messages
		if((requirementsOp.getFault()!=null)&&(candidateOp.getFault()!=null))
		{
			Message requirementsFaultMessage = requirementsMessages.getMessage(requirementsOp.getFault());
			Message candidateFaultMessage = candidateMessages.getMessage(candidateOp.getFault());
			
			//Mapping faultMapping = matchMessages(requirementsFaultMessage,candidateFaultMessage);
			Mapping faultMapping = new Mapping(requirementsOp.getFault(),candidateOp.getFault(),requirementsOp.getFaultId(),candidateOp.getFaultId());
			Mapping messageMapping = matchMessages(requirementsFaultMessage,candidateFaultMessage);
			//set nodeIdentifier
			/*
			inputMapping.setRequirementsIdentifier(requirementsOp.getInputId());
			inputMapping.setCandidateIdentifier(candidateOp.getInputId());
			*/
			messageMapping.setRequirementsIdentifier(requirementsFaultMessage.getNodeIdentifier());
			messageMapping.setCandidateIdentifier(candidateFaultMessage.getNodeIdentifier());
			MappingPartList outputPartList = new MappingPartList();
			outputPartList.addMapping(messageMapping);
			faultMapping.addMappingPartList(outputPartList);
			//System.out.println("inputmapping: "+inputMapping.getRequirementsName()+", "+inputMapping.getCandidateName());
			//add inputmapping to MappingPartList
			messagePartList.addMapping(faultMapping);
			mapped = true;
			
		}
		if(mapped)
		{
			//add MappingPartList of messages to Mapping for operation
			operationMapping.addMappingPartList(messagePartList);
			//System.out.println("score: "+score);
			switch(matchStrategy)
			{
			case 0:
			{
				//multiply score with structureweight
				//operationMapping.setScore(structureWeight*operationMapping.getScore());
				break;
			}
			case 1:
			{
				//get namematch for Operations
				int nameScore = nameWeight*matchNames(requirementsOp.getName(),candidateOp.getName());
				if(nameScore > 0)
				{
					operationMapping.addScoreBonus(nameScore);
				}
				break;
			}
			case 2:
			{
				//get wordnet-matcg for Operation names
				int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsOp.getToken(),requirementsOp.getName(),candidateOp.getToken(),candidateOp.getName());
				if(wordnetScore > 0)
				{
					operationMapping.addScoreBonus(wordnetScore);
				}
				break;
			}
			case 3:
			{
				//multiply score with structureweight
				//operationMapping.setScore(structureWeight*operationMapping.getScore());
				//get namematch for Operations
				//System.out.println("nameweight: "+nameWeight);
				int nameScore = nameWeight*matchNames(requirementsOp.getName(),candidateOp.getName());
				//System.out.println("operation-namescore: "+nameScore);
				//System.out.println("operation-score: "+operationMapping.getScore());
				if(nameScore > 0)
				{
					operationMapping.addScoreBonus(nameScore);
				}
				break;
			}
			case 4:
			{
				//multiply score with structureweight
				//operationMapping.setScore(structureWeight*operationMapping.getScore());
				//get wordnet-match for Operation names
				int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsOp.getToken(),requirementsOp.getName(),candidateOp.getToken(),candidateOp.getName());
				if(wordnetScore > 0)
				{
					operationMapping.addScoreBonus(wordnetScore);
				}
				break;
			}
			case 5:
			{
				//get namematch for Operations + wordnet -match for Operation-names
				int nameScore = nameWeight*matchNames(requirementsOp.getName(),candidateOp.getName())+wordnetWeight*simpleWordnetMatch(requirementsOp.getToken(),requirementsOp.getName(),candidateOp.getToken(),candidateOp.getName());
				if(nameScore > 0)
				{
					operationMapping.addScoreBonus(nameScore);
				}
				break;
			}
			case 6:
			{
				//multiply score with structureweight
				//operationMapping.setScore(structureWeight*operationMapping.getScore());
				//get namematch for Operations + wordnet-match for Operation-names
				int nameScore = nameWeight*matchNames(requirementsOp.getName(),candidateOp.getName())+wordnetWeight*simpleWordnetMatch(requirementsOp.getToken(),requirementsOp.getName(),candidateOp.getToken(),candidateOp.getName());
				if(nameScore > 0)
				{
					operationMapping.addScoreBonus(nameScore);
				}
				break;
			}
			}
		}
		else
		{
			
			operationMapping.setScore(minScore);
			
		}
		//System.out.println("\npoperationmapping\n");
		//operationMapping.printMapping(5);
		return operationMapping;
	}
	
	/**
	 * matches two messages by building all possible combinations
	 * of their parameters
	 */
	private Mapping matchMessages(Message requirementsMessage,Message candidateMessage) throws JWNLException
	{
		//System.out.println("\n messages "+requirementsmessage.getName()+", "+candidatemessage.getName()+"\n");
		//create new messagemapping
		//MessageMapping messagemapping = new MessageMapping(requirementsmessage.getName(),candidatemessage.getName());
		
		//create new scoremap for parameterlists of requirements- and candidatemessage
		ScoreMap parameterMatrix = new ScoreMap(requirementsMessage.getParameterList().size(),candidateMessage.getParameterList().size(),requirementsMessage.getName(),candidateMessage.getName(), edit);
		
		//test for empty messages
		if((!requirementsMessage.isEmpty())&&(!candidateMessage.isEmpty()))
		{
			
			//create parameter-matrix
			
			//iterate over parameter of requirements message
			for(ListIterator<MessageParameter> requirementsList = requirementsMessage.getParameterList().listIterator();requirementsList.hasNext();)
			{
				MessageParameter requirementsParameter = requirementsList.next();
				//iterate over parameter of candidate message
				for(ListIterator<MessageParameter> candidateList = candidateMessage.getParameterList().listIterator();candidateList.hasNext();)
				{
					MessageParameter candidateParameter = candidateList.next();
					Mapping parameterMapping = matchParameter(requirementsParameter,candidateParameter);
					//int score = parameterMapping.getScore();
					
					//matchinglist.put(requirementsparameter.getName(),candidateparameter.getName(),parametermapping);
					
					parameterMatrix.setValue(parameterMapping);
				}
			}
			//compute parameter-matches from matrix
			parameterMatrix.computeMatches();
			Mapping messageMapping = parameterMatrix.getMapping();
			switch(matchStrategy)
			{
			case 0:
			{
				//multiply score with structureweight
				//messageMapping.setScore(structureWeight*messageMapping.getScore());
				break;
			}
			case 1:
			{
				//get namematch for Messages
				int nameScore = nameWeight*matchNames(requirementsMessage.getName(),candidateMessage.getName());
				if(nameScore > 0)
				{
					messageMapping.addScoreBonus(nameScore);
				}
				break;
			}
			case 2:
			{
				//get wordnet-matcg for Message names
				int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsMessage.getToken(),requirementsMessage.getName(),candidateMessage.getToken(),candidateMessage.getName());
				if(wordnetScore > 0)
				{
					messageMapping.addScoreBonus(wordnetScore);
				}
				break;
			}
			case 3:
			{
				//multiply score with structureweight
				//messageMapping.setScore(structureWeight*messageMapping.getScore());
				//get namematch for Messages
				int nameScore = nameWeight*matchNames(requirementsMessage.getName(),candidateMessage.getName());
				if(nameScore > 0)
				{
					messageMapping.addScoreBonus(nameScore);
				}
				break;
			}
			case 4:
			{
				//multiply score with structureweight
				//messageMapping.setScore(structureWeight*messageMapping.getScore());
				//get wordnet-match for Message names
				int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsMessage.getToken(),requirementsMessage.getName(),candidateMessage.getToken(),candidateMessage.getName());
				if(wordnetScore > 0)
				{
					messageMapping.addScoreBonus(wordnetScore);
				}
				break;
			}
			case 5:
			{
				//get namematch for Messages + wordnet -match for Message-names
				int nameScore = nameWeight*matchNames(requirementsMessage.getName(),candidateMessage.getName())+wordnetWeight*simpleWordnetMatch(requirementsMessage.getToken(),requirementsMessage.getName(),candidateMessage.getToken(),candidateMessage.getName());
				if(nameScore > 0)
				{
					messageMapping.addScoreBonus(nameScore);
				}
				break;
			}
			case 6:
			{
				//multiply score with structureweight
				//messageMapping.setScore(structureWeight*messageMapping.getScore());
				//get namematch for Messages + wordnet-match for Message-names
				int nameScore = nameWeight*matchNames(requirementsMessage.getName(),candidateMessage.getName())+wordnetWeight*simpleWordnetMatch(requirementsMessage.getToken(),requirementsMessage.getName(),candidateMessage.getToken(),candidateMessage.getName());
				if(nameScore > 0)
				{
					messageMapping.addScoreBonus(nameScore);
				}
				break;
			}
			}
			
			return messageMapping;
		}
		else
		{
			// message empty => null match
			Mapping nullMapping = new Mapping(requirementsMessage.getName(),candidateMessage.getName(),requirementsMessage.getNodeIdentifier(),candidateMessage.getNodeIdentifier());
			nullMapping.setScore(0);
			return nullMapping;
		}
		
	}
	
	/**
	 * matches two (message)parameters depending on their types
	 */
	private Mapping matchParameter(MessageParameter requirementsParameter,MessageParameter candidateParameter) throws JWNLException
	{
		//create new mapping
		Mapping parameterMapping = new Mapping(requirementsParameter.getName(),candidateParameter.getName(),requirementsParameter.getNodeIdentifier(),candidateParameter.getNodeIdentifier());
		
		//System.out.println("parameter: "+requirementsParameter.getName()+" requirementstype: "+requirementsParameter.getType());
		//System.out.println("parameter: "+candidateParameter.getName()+" candidatetype: "+candidateParameter.getType()+"\n");
		//requirementsparameter is of predefined type
		if(lookupTable.lookupSimpleType(requirementsParameter.getType()))
		{
			//candidateparameter is of predefined type
			if(lookupTable.lookupSimpleType(candidateParameter.getType()))
			{
				if((matchStrategy ==0)||(matchStrategy == 3)||(matchStrategy == 4)||(matchStrategy ==6))
				{
					parameterMapping.setScore(structureWeight*lookupTable.getMatchingScore(requirementsParameter.getType(),candidateParameter.getType()));
				}
				//ready
			}
			//candidateparameter is of complextype
			else
			{
				ComplexType complextype = candidateTypes.getType(candidateParameter.getType());
				Mapping  typeMapping = matchMixedTypes(requirementsParameter.getName(),requirementsParameter.getType(),candidateParameter.getName(),complextype, null);
				//Mapping  typeMapping = new Mapping(requirementsparameter.getName(),complextype.getName());
				//typeMapping.setScore(matchMixedTypes(requirementsparameter,candidateparameter.getName(),complextype,typeMapping));
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to MappingPartList
				typePartList.addMapping(typeMapping);
				
				//add MappingPartList to parameter-mapping
				parameterMapping.addMappingPartList(typePartList);
				
				
				
			}
		}
		//requirementsparameter is of complextype
		else
		{
			//candidateparameter is of predefined type
			if(lookupTable.lookupSimpleType(candidateParameter.getType()))
			{
				ComplexType complextype = requirementsTypes.getType(requirementsParameter.getType());
				Mapping typeMapping = matchMixedTypes(requirementsParameter.getName(),complextype,candidateParameter.getName(),candidateParameter.getType(), null);
				//Mapping typeMapping = new Mapping(complextype.getName(),candidateparameter.getName());
				//typeMapping.setScore(matchMixedTypes(requirementsparameter.getName(),complextype,candidateparameter,typeMapping));

				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to MappingPartList
				typePartList.addMapping(typeMapping);
				
				//add MappingPartList to parameter-mapping
				parameterMapping.addMappingPartList(typePartList);
				
			}
			//candidateparameter is of complextype
			else
			{
				//System.out.println("typeindex: "+requirementstypes.indexOf(s.getType()));
				ComplexType requirementstype = requirementsTypes.getType(requirementsParameter.getType());
				ComplexType candidatetype = candidateTypes.getType(candidateParameter.getType());
				
				/*System.out.println("requirementstype: \n");
				requirementstype.printComplexType();
				candidatetype.printComplexType();*/
				Mapping typeMapping = matchComplexTypes(requirementsParameter.getName(),requirementstype,candidateParameter.getName(),candidatetype);
				//Mapping typeMapping = new Mapping(requirementstype.getName(),candidatetype.getName());
				//typeMapping.setScore(matchComplexTypes(requirementsparameter.getName(),requirementstype,candidateparameter.getName(),candidatetype));
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to MappingPartList
				typePartList.addMapping(typeMapping);
				
				//add MappingPartList to parameter-mapping
				parameterMapping.addMappingPartList(typePartList);
			}
		}
		switch(matchStrategy)
		{
		case 0:break;
		case 1:
		{
			//get namematch for Parameters
			int nameScore = nameWeight*matchNames(requirementsParameter.getName(),candidateParameter.getName());
			if(nameScore > 0)
			{
				parameterMapping.addScoreBonus(nameScore);
			}
			break;
		}
		case 2:
		{
			//get wordnet-matcg for Parameter names
			int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsParameter.getToken(),requirementsParameter.getName(),candidateParameter.getToken(),candidateParameter.getName());
			if(wordnetScore > 0)
			{
				parameterMapping.addScoreBonus(wordnetScore);
			}
			break;
		}
		case 3:
		{
			//get namematch for Parameters
			int nameScore = nameWeight*matchNames(requirementsParameter.getName(),candidateParameter.getName());
			if(nameScore > 0)
			{
				parameterMapping.addScoreBonus(nameScore);
			}
			break;
		}
		case 4:
		{
			//get wordnet-match for Parameter names
			int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsParameter.getToken(),requirementsParameter.getName(),candidateParameter.getToken(),candidateParameter.getName());
			if(wordnetScore > 0)
			{
				parameterMapping.addScoreBonus(wordnetScore);
			}
			break;
		}
		case 5:
		{
			//get namematch for Parameters + wordnet-match for Parameter-names
			int nameScore = nameWeight*matchNames(requirementsParameter.getName(),candidateParameter.getName())+wordnetWeight*simpleWordnetMatch(requirementsParameter.getToken(),requirementsParameter.getName(),candidateParameter.getToken(),candidateParameter.getName());
			if(nameScore > 0)
			{
				parameterMapping.addScoreBonus(nameScore);
			}
			break;
		}
		case 6:
		{
			//get namematch for Parameters + wordnet-match for Parameter-names
			int nameScore = nameWeight*matchNames(requirementsParameter.getName(),candidateParameter.getName())+wordnetWeight*simpleWordnetMatch(requirementsParameter.getToken(),requirementsParameter.getName(),candidateParameter.getToken(),candidateParameter.getName());
			if(nameScore > 0)
			{
				parameterMapping.addScoreBonus(nameScore);
			}
			break;
		}
		}
		return parameterMapping;
	}
	
	/**
	 * compute score for identical types
	 * i.e number of elements times maxscore
	 * at each level(subtype/elementtype) of the type "structure"
	 * !!!not used!!!
	 * @deprecated
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
				//System.out.println("element: "+element.getType());
				ComplexType complextype = requirementsTypes.getType(element.getType());
				elementcount = elementcount+matchIdenticalTypes(complextype);
				groupingcount++;
			}
		}
		return (elementcount+groupingcount)*maxScore*2;
	}
	
	
	
	
	
	
	/**
	 * mixed-match of a complex type with a simpletype element
	 */
	private Mapping matchMixedTypes(String requirementsName,ComplexType requirementsType,String candidateName,String candidateType, NodeIdentifier id) throws JWNLException
	{
		if(requirementsType != null)
		{
			//System.out.println("\nmatchMixedTypes: "+requirementsname+", "+candidatename+"\n");
			
			//create new scoremap for requirementsname and candidatename
			ScoreMap typeMatrix = new ScoreMap(requirementsType.getElementList().size(),1,requirementsName,candidateName, edit);
			int typeScore = -1;
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
							if(id != null)
							{
								elementMapping.setCandidateIdentifier(id);
							}
							
							//int matchScore = lookupTable.getMatchingScore(requirementselement.getType(),candidatetype);
							//elementMapping.setScore(matchScore);
	
							switch(matchStrategy)
							{
							case 0:
							{
								int matchScore = structureWeight*lookupTable.getMatchingScore(requirementsElement.getType(),candidateType);
								elementMapping.setScore(matchScore);
								break;
							}
							case 1:
							{
								//get namematch for Elements
								int nameScore = nameWeight*matchNames(requirementsElement.getName(),candidateName);
								if(nameScore > 0)
								{
									elementMapping.setScore(nameScore);
								}
								break;
							}
							case 2:
							{
								//get wordnet-matcg for Element names
								int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsElement.getToken(),requirementsElement.getName(),new NameTokens(candidateName,stopWords),candidateName);
								if(wordnetScore > 0)
								{
									elementMapping.setScore(wordnetScore);
								}
								break;
							}
							case 3:
							{
								int matchScore = structureWeight*lookupTable.getMatchingScore(requirementsElement.getType(),candidateType);
								elementMapping.setScore(matchScore);
								//get namematch for Elements
								int nameScore = nameWeight*matchNames(requirementsElement.getName(),candidateName);
								if(nameScore > 0)
								{
									elementMapping.addScoreBonus(nameScore);
									/*if(requirementsType.getName().equals("dateType"))
									{
										System.out.println("mixed elementmapping:\n");
										elementMapping.printMapping(9);
									}*/
								}
								break;
							}
							case 4:
							{
								int matchScore = structureWeight*lookupTable.getMatchingScore(requirementsElement.getType(),candidateType);
								elementMapping.setScore(matchScore);
								//get wordnet-match for Element names
								int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsElement.getToken(),requirementsElement.getName(),new NameTokens(candidateName,stopWords),candidateName);
								if(wordnetScore > 0)
								{
									elementMapping.addScoreBonus(wordnetScore);
								}
								break;
							}
							case 5:
							{
								//get namematch for Elements + wordnet-match for Element-names
								int nameScore = nameWeight*matchNames(requirementsElement.getName(),candidateName)+wordnetWeight*simpleWordnetMatch(requirementsElement.getToken(),requirementsElement.getName(),new NameTokens(candidateName,stopWords),candidateName);
								if(nameScore > 0)
								{
									elementMapping.setScore(nameScore);
								}
								break;
							}
							case 6:
							{
								int matchScore = structureWeight*lookupTable.getMatchingScore(requirementsElement.getType(),candidateType);
								elementMapping.setScore(matchScore);
								//get namematch for Elements + wordnet-match for Element-names
								int nameScore = nameWeight*matchNames(requirementsElement.getName(),candidateName)+wordnetWeight*simpleWordnetMatch(requirementsElement.getToken(),requirementsElement.getName(),new NameTokens(candidateName,stopWords),candidateName);
								if(nameScore > 0)
								{
									elementMapping.addScoreBonus(nameScore);
								}
								break;
							}
							}
							typeMatrix.setValue(elementMapping);
						}
						//elementof complex type, recurse!
						else
						{
							ComplexType complextypeelement = requirementsTypes.getType(requirementsElement.getType());
							Mapping elementMapping = matchMixedTypes(requirementsElement.getName(),complextypeelement,candidateName,candidateType, id);
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
					if(id != null)
					{
						typeMapping.setCandidateIdentifier(id);
					}
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
	private Mapping matchMixedTypes(String requirementsName,String requirementsType,String candidateName,ComplexType candidateType, NodeIdentifier id) throws JWNLException
	{
		if(candidateType != null)
		{
		
			//System.out.println("\nmixedtypematch "+requirementsname+", "+candidatename+"\n");
			
			//create new scoremap for requirementsname and candidatename
			ScoreMap typeMatrix = new ScoreMap(1,candidateType.getElementList().size(),requirementsName,candidateName, edit);
			int typeScore = -1;
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
							if(id != null)
							{
								elementMapping.setRequirementsIdentifier(id);
							}
							//int matchScore = lookupTable.getMatchingScore(requirementstype,candidateelement.getType());
							//elementMapping.setScore(matchScore);
	
							switch(matchStrategy)
							{
							case 0:
							{
								int matchScore = structureWeight*lookupTable.getMatchingScore(requirementsType,candidateelement.getType());
								elementMapping.setScore(matchScore);
								break;
							}
							case 1:
							{
								//get namematch for Elements
								int nameScore = nameWeight*matchNames(requirementsName,candidateelement.getName());
								if(nameScore > 0)
								{
									elementMapping.setScore(nameScore);
								}
								break;
							}
							case 2:
							{
								//get wordnet-matcg for Element names
								int wordnetScore = wordnetWeight*simpleWordnetMatch(new NameTokens(requirementsName,stopWords),requirementsName,candidateelement.getToken(),candidateelement.getName());
								if(wordnetScore > 0)
								{
									elementMapping.setScore(wordnetScore);
								}
								break;
							}
							case 3:
							{
								int matchScore = structureWeight*lookupTable.getMatchingScore(requirementsType,candidateelement.getType());
								elementMapping.setScore(matchScore);
								//get namematch for Elements
								int nameScore = nameWeight*matchNames(requirementsName,candidateelement.getName());
								if(nameScore > 0)
								{
									elementMapping.addScoreBonus(nameScore);
								}
								break;
							}
							case 4:
							{
								int matchScore = structureWeight*lookupTable.getMatchingScore(requirementsType,candidateelement.getType());
								elementMapping.setScore(matchScore);
								//get wordnet-match for Element names
								int wordnetScore = wordnetWeight*simpleWordnetMatch(new NameTokens(requirementsName,stopWords),requirementsName,candidateelement.getToken(),candidateelement.getName());
								if(wordnetScore > 0)
								{
									elementMapping.addScoreBonus(wordnetScore);
								}
								break;
							}
							case 5:
							{
								//get namematch for Elements + wordnet-match for Element-names
								int nameScore = nameWeight*matchNames(requirementsName,candidateelement.getName())+wordnetWeight*simpleWordnetMatch(new NameTokens(requirementsName,stopWords),requirementsName,candidateelement.getToken(),candidateelement.getName());
								if(nameScore > 0)
								{
									elementMapping.setScore(nameScore);
								}
								break;
							}
							case 6:
							{
								int matchScore = structureWeight*lookupTable.getMatchingScore(requirementsType,candidateelement.getType());
								elementMapping.setScore(matchScore);
								//get namematch for Elements + wordnet-match for Element-names
								int nameScore = nameWeight*matchNames(requirementsName,candidateelement.getName())+wordnetWeight*simpleWordnetMatch(new NameTokens(requirementsName,stopWords),requirementsName,candidateelement.getToken(),candidateelement.getName());
								if(nameScore > 0)
								{
									elementMapping.addScoreBonus(nameScore);
								}
								break;
							}
							}
							
							typeMatrix.setValue(elementMapping);
							
							//typematrix.setValue(requirementsname,candidateelement.getName(),lookuptable.getMatchingScore(requirementstype,candidateelement.getType()));
						}
						//elementof complex type, recurse!
						else
						{
							ComplexType complextypeelement = candidateTypes.getType(candidateelement.getType());
							Mapping elementMapping = matchMixedTypes(requirementsName,requirementsType,candidateelement.getName(),complextypeelement, id);
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
					if(id != null)
					{
						typeMapping.setRequirementsIdentifier(id);
						//System.out.println("requirements: "+typeMapping.getRequirementsName()+" candidate: "+typeMapping.getCandidateName());
						//System.out.println("nodeIdentifier: "+typeMapping.getRequirementsIdentifier()+", "+typeMapping.getCandidateIdentifier());
					}
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
	
	
	/**
	 * matches two complex types by building all possible combinations
	 * of their elements and their attributes
	 */
	private Mapping matchComplexTypes(String requirementsName,ComplexType requirementsType,String candidateName,ComplexType candidateType) throws JWNLException
	{
		//System.out.println("matchcomplexTypes: "+requirementsName+": "+requirementsType.getName()+", "+candidateName+": "+candidateType.getName());
		//System.out.println("Id: "+requirementsType.getNodeIdentifier().toString()+", "+candidateType.getNodeIdentifier().toString()+", ");
		/*
		if(((requirementsType.getNumberOfLeafElements()==0)&&(requirementsType.getNumberOfNonLeafElements()==1))&&!((candidateType.getNumberOfLeafElements()==0)&&(candidateType.getNumberOfNonLeafElements()==1)))
		{
			/*
			 * requirementtype consists only of a complexType
			 * element => replace requirementsType by the type
			 * of its single element
			 */
			/*
			Element singleReqElement = requirementsType.getElementList().get(0);
			ComplexType singleReqElementType = requirementsTypes.getType(singleReqElement.getType());
						
			Mapping typeMapping = new Mapping(requirementsType.getName(),candidateType.getName(),requirementsType.getNodeIdentifier(),candidateType.getNodeIdentifier());
			MappingPartList typePartList = new MappingPartList();
			Mapping splitMapping = matchComplexTypes(singleReqElement.getName(),singleReqElementType,candidateName,candidateType);
			typePartList.addMapping(splitMapping);
			typeMapping.addMappingPartList(typePartList);
							
			//Mapping typeMapping = matchComplexTypes(singleReqElement.getName(),singleReqElementType,candidateName,candidateType);
			//compare groupings
			if(requirementsType.getGrouping() == candidateType.getGrouping())
			{
				typeMapping.addScoreBonus(groupingBonus);
			}
				
			switch(matchStrategy)
			{
				case 0:break;
				case 1:
				{
					//get namematch for ComplexTypes
					int nameScore = nameWeight*matchNames(requirementsName,candidateName);
					if(nameScore > 0)
					{
						typeMapping.addScoreBonus(nameScore);
					}
					break;
				}
				case 2:
				{
					//get wordnet-matcg for ComplexType names
					int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsType.getToken(),requirementsName,candidateType.getToken(),candidateName);
					if(wordnetScore > 0)
					{
						typeMapping.addScoreBonus(wordnetScore);
					}
					break;
				}
				case 3:
				{
					//get namematch for ComplexTypes
					int nameScore = nameWeight*matchNames(requirementsName,candidateName);
					if(nameScore > 0)
					{
						typeMapping.addScoreBonus(nameScore);
					}
					break;
				}
				case 4:
				{
					//get wordnet-match for ComplexType names
					int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsType.getToken(),requirementsName,candidateType.getToken(),candidateName);
					if(wordnetScore > 0)
					{
						typeMapping.addScoreBonus(wordnetScore);
					}
					break;
				}
				case 5:
				{
					//get namematch for ComplexTypes + wordnet-match for ComplexType-names
					int nameScore = nameWeight*matchNames(requirementsName,candidateName)+wordnetWeight*simpleWordnetMatch(requirementsType.getToken(),requirementsName,candidateType.getToken(),candidateName);
					if(nameScore > 0)
					{
						typeMapping.addScoreBonus(nameScore);
					}
					break;
				}
				case 6:
				{
					//get namematch for ComplexTypes + wordnet-match for ComplexType-names
					//System.out.println("complexTypes: "+requirementsName+", "+candidateName);
					//requirementsType.getToken().printTokens();
					//candidateType.getToken().printTokens();
					int nameScore = nameWeight*matchNames(requirementsName,candidateName)+wordnetWeight*simpleWordnetMatch(requirementsType.getToken(),requirementsName,candidateType.getToken(),candidateName);
					if(nameScore > 0)
					{
						typeMapping.addScoreBonus(nameScore);
					}
					break;
				}
			}
			return typeMapping;
		}*/
		/*
		if(((candidateType.getNumberOfLeafElements()==0)&&(candidateType.getNumberOfNonLeafElements()==1))&&!((requirementsType.getNumberOfLeafElements()==0)&&(requirementsType.getNumberOfNonLeafElements()==1)))
		{
			/*
			 * candidatetype consists only of a complexType
			 * element => replace candidateType by the type
			 * of its single element
			 */
			/*
			Element singleElement = candidateType.getElementList().get(0);
			ComplexType singleElementType = candidateTypes.getType(singleElement.getType());
			
			Mapping typeMapping = new Mapping(requirementsType.getName(),candidateType.getName(),requirementsType.getNodeIdentifier(),candidateType.getNodeIdentifier());
			MappingPartList typePartList = new MappingPartList();
			Mapping splitMapping = matchComplexTypes(requirementsName,requirementsType,singleElement.getName(),singleElementType);
			typePartList.addMapping(splitMapping);
			typeMapping.addMappingPartList(typePartList);
			
			//Mapping typeMapping = matchComplexTypes(requirementsName,requirementsType,singleElement.getName(),singleElementType);
			
			//compare groupings
			if(requirementsType.getGrouping() == candidateType.getGrouping())
			{
				typeMapping.addScoreBonus(groupingBonus);
			}
			
			switch(matchStrategy)
			{
				case 0:break;
				case 1:
				{
					//get namematch for ComplexTypes
					int nameScore = nameWeight*matchNames(requirementsName,candidateName);
					if(nameScore > 0)
					{
						typeMapping.addScoreBonus(nameScore);
					}
					break;
				}
				case 2:
				{
					//get wordnet-matcg for ComplexType names
					int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsType.getToken(),requirementsName,candidateType.getToken(),candidateName);
					if(wordnetScore > 0)
					{
						typeMapping.addScoreBonus(wordnetScore);
					}
					break;
				}
				case 3:
				{
					//get namematch for ComplexTypes
					int nameScore = nameWeight*matchNames(requirementsName,candidateName);
					if(nameScore > 0)
					{
						typeMapping.addScoreBonus(nameScore);
					}
					break;
				}
				case 4:
				{
					//get wordnet-match for ComplexType names
					int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsType.getToken(),requirementsName,candidateType.getToken(),candidateName);
					if(wordnetScore > 0)
					{
						typeMapping.addScoreBonus(wordnetScore);
					}
					break;
				}
				case 5:
				{
					//get namematch for ComplexTypes + wordnet-match for ComplexType-names
					int nameScore = nameWeight*matchNames(requirementsName,candidateName)+wordnetWeight*simpleWordnetMatch(requirementsType.getToken(),requirementsName,candidateType.getToken(),candidateName);
					if(nameScore > 0)
					{
						typeMapping.addScoreBonus(nameScore);
					}
					break;
				}
				case 6:
				{
					//get namematch for ComplexTypes + wordnet-match for ComplexType-names
					//System.out.println("complexTypes: "+requirementsName+", "+candidateName);
					//requirementsType.getToken().printTokens();
					//candidateType.getToken().printTokens();
					int nameScore = nameWeight*matchNames(requirementsName,candidateName)+wordnetWeight*simpleWordnetMatch(requirementsType.getToken(),requirementsName,candidateType.getToken(),candidateName);
					if(nameScore > 0)
					{
						typeMapping.addScoreBonus(nameScore);
					}
					break;
				}
			}
			return typeMapping;
		}
		*/
		/*
		 * end of the special cases
		 * here begins the normal matching of complexTypes
		 */
				
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
					int Score = 0;
						
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
						//add grouping bonus
						if((matchStrategy ==0)||(matchStrategy == 3)||(matchStrategy == 4)||(matchStrategy ==6))
						{
							//add boni to score
							elementListMapping.addScoreBonus(structureWeight*typeScore);
						}
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
							//add grouping bonus
							if((matchStrategy ==0)||(matchStrategy == 3)||(matchStrategy == 4)||(matchStrategy ==6))
							{
								//add boni to score
								elementListMapping.addScoreBonus(structureWeight*typeScore);
							}
								
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
			switch(matchStrategy)
			{
				case 0:break;
				case 1:
				{
					//get namematch for ComplexTypes
					int nameScore = nameWeight*matchNames(requirementsName,candidateName);
					if(nameScore > 0)
					{
						typeMapping.addScoreBonus(nameScore);
					}
					break;
				}
				case 2:
				{
					//get wordnet-matcg for ComplexType names
					int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsType.getToken(),requirementsName,candidateType.getToken(),candidateName);
					if(wordnetScore > 0)
					{
						typeMapping.addScoreBonus(wordnetScore);
					}
					break;
				}
				case 3:
				{
					//get namematch for ComplexTypes
					int nameScore = nameWeight*matchNames(requirementsName,candidateName);
					if(nameScore > 0)
					{
						typeMapping.addScoreBonus(nameScore);
					}
					break;
				}
				case 4:
				{
					//get wordnet-match for ComplexType names
					int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsType.getToken(),requirementsName,candidateType.getToken(),candidateName);
					if(wordnetScore > 0)
					{
						typeMapping.addScoreBonus(wordnetScore);
					}
					break;
				}
				case 5:
				{
					//get namematch for ComplexTypes + wordnet-match for ComplexType-names
					int nameScore = nameWeight*matchNames(requirementsName,candidateName)+wordnetWeight*simpleWordnetMatch(requirementsType.getToken(),requirementsName,candidateType.getToken(),candidateName);
					if(nameScore > 0)
					{
						typeMapping.addScoreBonus(nameScore);
					}
					break;
				}
				case 6:
				{
					//get namematch for ComplexTypes + wordnet-match for ComplexType-names
					//System.out.println("complexTypes: "+requirementsName+", "+candidateName);
					//requirementsType.getToken().printTokens();
					//candidateType.getToken().printTokens();
					int nameScore = nameWeight*matchNames(requirementsName,candidateName)+wordnetWeight*simpleWordnetMatch(requirementsType.getToken(),requirementsName,candidateType.getToken(),candidateName);
					if(nameScore > 0)
					{
						typeMapping.addScoreBonus(nameScore);
					}
					break;
				}
			}
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
	 * matches types of arrays
	 */
	private Mapping matchArrayTypes(ComplexType requirementsType,ComplexType candidateType) throws JWNLException
	{
		Mapping arrayMapping = new Mapping(requirementsType.getName(),candidateType.getName(),requirementsType.getNodeIdentifier(),candidateType.getNodeIdentifier());
		int arrayscore = -1;
		String requirementsarraytype = requirementsType.getArrayType();
		String candidatearraytype = candidateType.getArrayType();
		
				
		if(lookupTable.lookupSimpleType(requirementsarraytype))
		{
			if(lookupTable.lookupSimpleType(candidatearraytype))
			{
				//both array of simple type
				if((matchStrategy ==0)||(matchStrategy == 3)||(matchStrategy == 4)||(matchStrategy ==6))
				{
					arrayscore = lookupTable.getMatchingScore(requirementsarraytype,candidatearraytype)*arrayBonus;
					//set mappingscore
					arrayMapping.setScore(structureWeight*arrayscore*arrayBonus);
				}
				return arrayMapping;
			}
			else
			{
				//mixed types
				ComplexType complextype = candidateTypes.getType(candidatearraytype);
				//create mapping for types
				Mapping typeMapping = matchMixedTypes(requirementsType.getName(),requirementsarraytype,candidateType.getName(),complextype, null);
				
				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//add typePartList to arrayMapping
				arrayMapping.addMappingPartList(typePartList);
				if((matchStrategy ==0)||(matchStrategy == 3)||(matchStrategy == 4)||(matchStrategy ==6))
				{
					arrayMapping.setScore(structureWeight*arrayMapping.getScore()*arrayBonus);
				}
				return arrayMapping;
			}
		}
		else
		{
			if(lookupTable.lookupSimpleType(candidatearraytype))
			{
				//mixed types
				ComplexType complextype = requirementsTypes.getType(requirementsarraytype);
				Mapping typeMapping = matchMixedTypes(requirementsType.getName(),complextype,candidateType.getName(),candidatearraytype, null);

				//create MappingPartList
				MappingPartList typePartList = new MappingPartList();
				
				//add typeMapping to typePartList
				typePartList.addMapping(typeMapping);
				
				//add typePartList to arrayMapping
				arrayMapping.addMappingPartList(typePartList);
				if((matchStrategy ==0)||(matchStrategy == 3)||(matchStrategy == 4)||(matchStrategy ==6))
				{
					arrayMapping.setScore(structureWeight*arrayMapping.getScore()*arrayBonus);
				}
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
				
				
				switch(matchStrategy)
				{
				case 0:
				{
					arrayMapping.setScore(structureWeight*arrayMapping.getScore()*arrayBonus);
					break;
				}
				case 1:
				{
					//get namematch for ComplexTypes
					int nameScore = nameWeight*matchNames(requirementsarraytype,candidatearraytype);
					if(nameScore > 0)
					{
						arrayMapping.addScoreBonus(nameScore);
					}
					break;
				}
				case 2:
				{
					//get wordnet-matcg for ComplexType names
					int wordnetScore = wordnetWeight*simpleWordnetMatch(complexrequirementstype.getToken(),complexrequirementstype.getName(),complexcandidatetype.getToken(),complexcandidatetype.getName());
					if(wordnetScore > 0)
					{
						arrayMapping.addScoreBonus(wordnetScore);
					}
					break;
				}
				case 3:
				{
					arrayMapping.setScore(structureWeight*arrayMapping.getScore()*arrayBonus);
					//get namematch for ComplexTypes
					int nameScore = nameWeight*matchNames(requirementsarraytype,candidatearraytype);
					if(nameScore > 0)
					{
						arrayMapping.addScoreBonus(nameScore);
					}
					break;
				}
				case 4:
				{
					arrayMapping.setScore(structureWeight*arrayMapping.getScore()*arrayBonus);
					//get wordnet-match for ComplexType names
					int wordnetScore = wordnetWeight*simpleWordnetMatch(complexrequirementstype.getToken(),complexrequirementstype.getName(),complexcandidatetype.getToken(),complexcandidatetype.getName());
					if(wordnetScore > 0)
					{
						typeMapping.addScoreBonus(wordnetScore);
					}
					break;
				}
				case 5:
				{
					//get namematch for ComplexTypes + wordnet-match for ComplexType-names
					int nameScore = nameWeight*matchNames(requirementsarraytype,candidatearraytype)+wordnetWeight*simpleWordnetMatch(complexrequirementstype.getToken(),complexrequirementstype.getName(),complexcandidatetype.getToken(),complexcandidatetype.getName());
					if(nameScore > 0)
					{
						arrayMapping.addScoreBonus(nameScore);
					}
					break;
				}
				case 6:
				{
					arrayMapping.setScore(structureWeight*arrayMapping.getScore()*arrayBonus);
					//get namematch for ComplexTypes + wordnet-match for ComplexType-names
					int nameScore = nameWeight*matchNames(requirementsarraytype,candidatearraytype)+wordnetWeight*simpleWordnetMatch(complexrequirementstype.getToken(),complexrequirementstype.getName(),complexcandidatetype.getToken(),complexcandidatetype.getName());
					if(nameScore > 0)
					{
						arrayMapping.addScoreBonus(nameScore);
					}
					break;
				}
				}
				return arrayMapping;
			}
		}
		//arrayMapping.setScore(arrayMapping.getScore()*arrayBonus);
		
		//return arrayMapping;
	}
	
	/**
	 * matches two elements (of complexTypes)
	 */
	private Mapping matchElements(Element requirementsElement,Element candidateElement) throws JWNLException
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
				Mapping typeMapping = matchMixedTypes(requirementsElement.getType(),complexRequirementsType,candidateElement.getName(),candidateElement.getType(),candidateElement.getNodeIdentifier());
				
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
				
				switch(matchStrategy)
				{
				case 0:
				{
					int matchScore = structureWeight*(elementScore+lookupTable.getMatchingScore(requirementsElement.getType(),candidateElement.getType()));
					elementMapping.setScore(matchScore);
					//elementMapping.addScoreBonus(elementScore);
					break;
				}
				case 1:
				{
					//get namematch for Elements
					int nameScore = nameWeight*matchNames(requirementsElement.getName(),candidateElement.getName());
					if(nameScore > 0)
					{
						elementMapping.setScore(nameScore);
					}
					break;
				}
				case 2:
				{
					//get wordnet-matcg for Element names
					int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsElement.getToken(),requirementsElement.getName(),candidateElement.getToken(),candidateElement.getName());
					if(wordnetScore > 0)
					{
						elementMapping.setScore(wordnetScore);
					}
					break;
				}
				case 3:
				{
					int matchScore = structureWeight*(elementScore+lookupTable.getMatchingScore(requirementsElement.getType(),candidateElement.getType()));
					elementMapping.setScore(matchScore);
					//elementMapping.addScoreBonus(elementScore);
					//get namematch for Elements
					int nameScore = nameWeight*matchNames(requirementsElement.getName(),candidateElement.getName());
					if(nameScore > 0)
					{
						elementMapping.addScoreBonus(nameScore);
					}
					break;
				}
				case 4:
				{
					int matchScore = structureWeight*(elementScore+lookupTable.getMatchingScore(requirementsElement.getType(),candidateElement.getType()));
					elementMapping.setScore(matchScore);
					//elementMapping.addScoreBonus(elementScore);
					//get wordnet-match for Element names
					int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsElement.getToken(),requirementsElement.getName(),candidateElement.getToken(),candidateElement.getName());
					if(wordnetScore > 0)
					{
						elementMapping.addScoreBonus(wordnetScore);
					}
					break;
				}
				case 5:
				{
					//get namematch for Elements + wordnet-match for Element-names
					int nameScore = nameWeight*matchNames(requirementsElement.getName(),candidateElement.getName())+wordnetWeight*simpleWordnetMatch(requirementsElement.getToken(),requirementsElement.getName(),candidateElement.getToken(),candidateElement.getName());
					if(nameScore > 0)
					{
						elementMapping.setScore(nameScore);
					}
					break;
				}
				case 6:
				{
					int matchScore = structureWeight*(elementScore+lookupTable.getMatchingScore(requirementsElement.getType(),candidateElement.getType()));
					elementMapping.setScore(matchScore);
					//elementMapping.addScoreBonus(elementScore);
					//get namematch for Elements + wordnet-match for Element-names
					//System.out.println("elements: "+requirementsElement.getName()+", "+candidateElement.getName());
					int nameScore = nameWeight*matchNames(requirementsElement.getName(),candidateElement.getName())+wordnetWeight*simpleWordnetMatch(requirementsElement.getToken(),requirementsElement.getName(),candidateElement.getToken(),candidateElement.getName());
					if(nameScore > 0)
					{
						elementMapping.addScoreBonus(nameScore);
					}
					break;
				}
				}
				
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
				Mapping typeMapping = matchMixedTypes(requirementsElement.getName(),requirementsElement.getType(),candidateElement.getType(),complexCandidateType, requirementsElement.getNodeIdentifier());
				
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
	 * matches two sequences by matching their elements 
	 * with respect to the sequenceorder
	 */
	private Mapping matchSequences(ComplexType requirementsType,Sequence requirements,ComplexType candidateType,Sequence candidate) throws JWNLException
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
			sequenceMapping.addScoreBonus(sequenceBonus);
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
	 * matches two attributeLists (of complexTypes)
	 * by computing all possible combinations of the
	 * contained attributes and the selecting the
	 * combination(s) with the highest score
	 */
	private Mapping matchAttributeLists(ComplexType requirementsType,ComplexType candidateType) throws JWNLException
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
	private Mapping matchAttributes(Attribute requirementsAttribute,Attribute candidateAttribute) throws JWNLException
	{
		Mapping attributeMapping = new Mapping(requirementsAttribute.getName(),candidateAttribute.getName(),requirementsAttribute.getId(),candidateAttribute.getId());
		
		switch(matchStrategy)
		{
		case 0:
		{
			int matchScore = structureWeight*lookupTable.getMatchingScore(requirementsAttribute.getType(),candidateAttribute.getType());
			attributeMapping.setScore(matchScore);
			//bonus for equal use
			if((requirementsAttribute.getUse() != null)&&(candidateAttribute.getUse() != null))
			{
				if(requirementsAttribute.getUse().equals(candidateAttribute.getUse()))
				{
					attributeMapping.addScoreBonus(structureWeight*maxScore/2);
				}
			}
			break;
		}
		case 1:
		{
			//get namematch for Attributes
			int nameScore = nameWeight*matchNames(requirementsAttribute.getName(),candidateAttribute.getName());
			if(nameScore > 0)
			{
				attributeMapping.setScore(nameScore);
			}
			break;
		}
		case 2:
		{
			//get wordnet-match for Attribute names
			int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsAttribute.getToken(),requirementsAttribute.getName(),candidateAttribute.getToken(),candidateAttribute.getName());
			if(wordnetScore > 0)
			{
				attributeMapping.setScore(wordnetScore);
			}
			break;
		}
		case 3:
		{
			//System.out.println("types: "+requirementsAttribute.getType()+", "+candidateAttribute.getType());
			int matchScore = structureWeight*lookupTable.getMatchingScore(requirementsAttribute.getType(),candidateAttribute.getType());
			attributeMapping.setScore(matchScore);
			//bonus for equal use
			if((requirementsAttribute.getUse() != null)&&(candidateAttribute.getUse() != null))
			{
				if(requirementsAttribute.getUse().equals(candidateAttribute.getUse()))
				{
					attributeMapping.addScoreBonus(structureWeight*maxScore/2);
				}
			}
			//get namematch for Attributes
			int nameScore = nameWeight*matchNames(requirementsAttribute.getName(),candidateAttribute.getName());
			if(nameScore > 0)
			{
				attributeMapping.addScoreBonus(nameScore);
			}
			break;
		}
		case 4:
		{
			int matchScore = structureWeight*lookupTable.getMatchingScore(requirementsAttribute.getType(),candidateAttribute.getType());
			attributeMapping.setScore(matchScore);
			//bonus for equal use
			if((requirementsAttribute.getUse() != null)&&(candidateAttribute.getUse() != null))
			{
				if(requirementsAttribute.getUse().equals(candidateAttribute.getUse()))
				{
					attributeMapping.addScoreBonus(structureWeight*maxScore/2);
				}
			}
			//get wordnet-match for Attribute names
			int wordnetScore = wordnetWeight*simpleWordnetMatch(requirementsAttribute.getToken(),requirementsAttribute.getName(),candidateAttribute.getToken(),candidateAttribute.getName());
			if(wordnetScore > 0)
			{
				attributeMapping.addScoreBonus(wordnetScore);
			}
			break;
		}
		case 5:
		{
			//get namematch for Attributes + wordnet-match for Attribute-names
			int nameScore = nameWeight*matchNames(requirementsAttribute.getName(),candidateAttribute.getName())+wordnetWeight*simpleWordnetMatch(requirementsAttribute.getToken(),requirementsAttribute.getName(),candidateAttribute.getToken(),candidateAttribute.getName());
			if(nameScore > 0)
			{
				attributeMapping.setScore(nameScore);
			}
			break;
		}
		case 6:
		{
			int matchScore = structureWeight*lookupTable.getMatchingScore(requirementsAttribute.getType(),candidateAttribute.getType());
			attributeMapping.setScore(matchScore);
			//bonus for equal use
			if((requirementsAttribute.getUse() != null)&&(candidateAttribute.getUse() != null))
			{
				if(requirementsAttribute.getUse().equals(candidateAttribute.getUse()))
				{
					attributeMapping.addScoreBonus(structureWeight*maxScore/2);
				}
			}
			//get namematch for Attribute + wordnet-match for Attribute-names
			int nameScore = nameWeight*matchNames(requirementsAttribute.getName(),candidateAttribute.getName())+wordnetWeight*simpleWordnetMatch(requirementsAttribute.getToken(),requirementsAttribute.getName(),candidateAttribute.getToken(),candidateAttribute.getName());
			if(nameScore > 0)
			{
				attributeMapping.addScoreBonus(nameScore);
			}
			break;
		}
		}
		return attributeMapping;
	}
	
	/**
	 * match two names by counting the token
	 * they have in common
	 */
	private int matchNames(String requirementsName,String candidateName)
	{
		//get token from names
		NameTokens requirementsTokens = new NameTokens(requirementsName,stopWords);
		//System.out.println("req:");
		//requirementsTokens.printTokens();
		NameTokens candidateTokens = new NameTokens(candidateName,stopWords);
		//System.out.println("can:");
		//candidateTokens.printTokens();
		//match token
		SimpleTokenMatcher serviceNameMatcher = new SimpleTokenMatcher(requirementsTokens,candidateTokens);
		return serviceNameMatcher.match();
	}
	
	/**
	 * computes WordNet-similarity for "Synonyms" of two NameToken
	 */
	private int simpleWordnetMatch(NameTokens requirements,String requirementsName,NameTokens candidate,String candidateName)throws JWNLException
	{
		if(requirements.size()>0 && candidate.size()>0)
		{
			ScoreMap map = new ScoreMap(requirements.size(),candidate.size(),requirementsName,candidateName, false);
			for(Iterator<String> requirementsit=requirements.iterator();requirementsit.hasNext();)
			{
				String requirementsToken = requirementsit.next();
				for(Iterator<String> candidateit=candidate.iterator();candidateit.hasNext();)
				{
					String candidateToken = candidateit.next();
					//System.out.println("\n**token: "+requirementsToken+", "+candidateToken);
					Mapping tokenMapping = new Mapping(requirementsToken,candidateToken);
					int depth = dictionary.getCommonSynonyms(requirementsToken,candidateToken);
					//System.out.println("snyonymdepth: "+depth);
					if(depth == 0)
					{
						tokenMapping.setScore(maxScore);
						//map.setValueOld(requirementsToken,candidateToken,maxScore);
					}
					else if(depth <10)
					{
						tokenMapping.setScore(maxScore-depth);
						//map.setValueOld(requirementsToken,candidateToken,maxScore-depth);
					}
					else if(depth >= 10)
					{
						/*
						 * no synonyms found => look for hypernymrelation
						 */
						//System.out.println("no synonyms");
						//depth = dictionary.getHypernymRelation(requirementsToken,candidateToken);
						//System.out.println("hypernymdepth: "+depth);
						if(depth == 0)
						{
							tokenMapping.setScore(maxScore/2);
							//map.setValueOld(requirementsToken,candidateToken,maxScore/2);
						}
						else if(depth < 5)
						{
							tokenMapping.setScore(maxScore/2-depth);
							//map.setValueOld(requirementsToken,candidateToken,maxScore/2-depth);
						}
						else
						{
							//System.out.println("no hypernyms");
							tokenMapping.setScore(minScore);
							//map.setValueOld(requirementsToken,candidateToken,minScore);
						}
					}
					map.setValue(tokenMapping);
					
					/*int hyperdepth = dictionary.getHypernymRelation(requirementstoken,candidatetoken);
					int derdepth = dictionary.getDerivedRelation(requirementstoken,candidatetoken);
					int catdepth = dictionary.getCategoryRelation(requirementstoken,candidatetoken);
					*/
				}
			}
			map.computeMatches();
			//System.out.println("Wordnetresult: "+map.getMapping().getScore());
			return map.getMapping().getScore();
		}
		else
		{
			return minScore;
		}
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



	public boolean isEdit() {
		return edit;
	}
	



	public void setEdit(boolean edit) {
		this.edit = edit;
	}



	public int getMatchStrategy() 
	{
		return matchStrategy;
	}

	public boolean isStrictSequence() 
	{
		return strictSequence;
	}
	

	public void setStrictSequence(boolean strictSequence) 
	{
		this.strictSequence = strictSequence;
	}
	
	

	
}
