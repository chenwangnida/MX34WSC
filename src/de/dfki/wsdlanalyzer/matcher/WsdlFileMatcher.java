package de.dfki.wsdlanalyzer.matcher;


import java.util.Iterator;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import de.dfki.wsdlanalyzer.mapping.Mapping;
import de.dfki.wsdlanalyzer.mapping.MappingPartList;
import de.dfki.wsdlanalyzer.parser.SimpleTypeLookupTable;
import de.dfki.wsdlanalyzer.parser.WsdlFileParser;
import de.dfki.wsdlanalyzer.types.Service;
import de.dfki.wsdlanalyzer.types.ServiceList;
import de.dfki.wsdlanalyzer.types.WsdlFile;
import de.dfki.wsdlanalyzer.wordnet.DictInstance;

import net.didion.jwnl.JWNLException;






/**
 * class for matching two wsdlfiles
 * by building all possible combinations of the services defined
 * in these files
 * 
 */
public class WsdlFileMatcher 
{
	private String requirementsFileName,candidateFileName;
	private ServiceList requirementsServices,candidateServices;
	private WsdlFile requirementsFile,candidateFile;
	
	private SimpleTypeLookupTable lookupTable;
	
	
	private Mapping fileMapping;
	/**
	*switch for tree-edit-distance-like matching
	*/
	private boolean edit;
	
	/**
	 * switch to turn on/off different match strategies
	 * there are 3 atomic matchers:
	 * 
	 * 0: structural (default)
	 * 1: name
	 * 2: wordnet
	 * 
	 * and 4 combined
	 * 
	 * 3: strucutural and name
	 * 4: structural and wordnet
	 * 5: name and wordnet
	 * 6: structural and name and wordnet
	 */
	private int matchStrategy;
	
	/**
	 * weight factors for structure/name/wordnet-score
	 * default =1
	 */
	private int structureWeight = 1;
	private int nameWeight = 1;
	private int wordnetWeight = 1;
	
	/**
	 * instance of wordnet dictionary
	 */
	private DictInstance dictionary;
	
	/**
	 * switch for strict/relaxed sequence matching
	 * relaxed means the order of the sequence is not respected
	 * i.e arbitrary permutation of the elements possible
	 * default: true
	 */
	private boolean strictSequence;
	
	//	logger
	protected static Log log = LogFactory.getLog(WsdlFileMatcher.class.getName());
	
	/**
	 * @param requirements name of requirements-wsdlfile
	 * @param candidate name of candidate-wsdlfile
	 * @param dict WordNet-Dictionary
	 * @deprecated
	 */
	public WsdlFileMatcher(String requirements,String candidate,DictInstance dict)
	{
		//create new lookuptable
		lookupTable = new SimpleTypeLookupTable();
		requirementsFileName = requirements;
		//parse requirementsfile
		WsdlFileParser requirementsparser = new WsdlFileParser(requirementsFileName,lookupTable);
		requirementsparser.parseWsdl();
		requirementsFile = requirementsparser.getWsdlfile();
		requirementsServices = requirementsFile.getServicelist();
		
		
		candidateFileName = candidate;
		//parse candidatefile
		WsdlFileParser candidateparser = new WsdlFileParser(candidateFileName,lookupTable);
		candidateparser.parseWsdl();
		candidateFile = candidateparser.getWsdlfile(); 
		candidateServices = candidateFile.getServicelist();
		matchStrategy = 0;		
		//strictSequence = false;
		strictSequence = true;
		dictionary = dict;
		
		//create new filemapping
		fileMapping = new Mapping(requirementsFileName,candidateFileName);
		
		
	}
	/**
	 * modified constructor
	 * @param requirements name of requirements-file
	 * @param reqFile WsdlFile of the requirementsfile (already parsed)
	 * @param candidate name of the candidatefile
	 * @param lookup SimpleTypeLookupTable
	 * @param dict WordNet-dictionary
	 */
	public WsdlFileMatcher(String requirements,WsdlFile reqFile,String candidate,SimpleTypeLookupTable lookup,DictInstance dict)
	{
		
		lookupTable = lookup;
		requirementsFileName = requirements;
		//parse requirementsfile
		//WsdlFileParser requirementsparser = new WsdlFileParser(requirementsFileName,lookupTable);
		//requirementsparser.parseWsdl();
		requirementsFile = reqFile;
		requirementsServices = requirementsFile.getServicelist();
		
		
		candidateFileName = candidate;
		//parse candidatefile
		WsdlFileParser candidateparser = new WsdlFileParser(candidateFileName,lookupTable);
		candidateparser.parseWsdl();
		candidateFile = candidateparser.getWsdlfile(); 
		candidateServices = candidateFile.getServicelist();
		matchStrategy = 0;		
		//strictSequence = false;
		strictSequence = true;
		dictionary = dict;
		
		//create new filemapping
		fileMapping = new Mapping(requirementsFileName,candidateFileName);
		
		
	}
	
	/**
	 * builds all possible combinations of services from
	 * requirements and candidate wsdl-file and generates a mapping
	 * of the services with highest score
	 */
	public void match() throws JWNLException
	{
		
		//iterate over services from requirements
		for(Iterator<String> requirementsserviceiterator=requirementsServices.keySet().iterator();requirementsserviceiterator.hasNext();)
		{
			String requirementsname = requirementsserviceiterator.next();
			//System.out.println("requirementsservice: "+requirementsname);
			Service requirementsservice = requirementsServices.get(requirementsname);
			
			//iterate over services from candidate
			for(Iterator<String> candidateserviceiterator = candidateServices.keySet().iterator();candidateserviceiterator.hasNext();)
			{
				String candidatename = candidateserviceiterator.next();
				//System.out.println("candidateservice: "+candidatename);
				Service candidateservice = candidateServices.get(candidatename);
				//generate WsdlServiceMatcher for requirements and candidate-service
				WsdlServiceMatcher matcher = new WsdlServiceMatcher(requirementsservice,requirementsFile,candidateservice,candidateFile,lookupTable,matchStrategy,structureWeight,nameWeight,wordnetWeight,strictSequence,dictionary);
				matcher.setEdit(edit);
				int score = matcher.matchServices();
				//create MappingPartList for serviceMapping
				MappingPartList servicePartList = new MappingPartList();
				servicePartList.addMapping(matcher.getServiceMapping());
				//add servicePartList to fileMapping
				fileMapping.addMappingPartList(servicePartList);
				
			}
			
		}
		int wsdlscore = fileMapping.getScore();
		//System.out.println("\n******* Service matching for wsdlfiles "+requirementsFileName+" and "+candidateFileName+" *******\n");
		//System.out.println("\n score: "+wsdlscore+"\n");
		if (log.isDebugEnabled())
		{
			log.debug(fileMapping.log(0));
		}
		/*
		 * TODO decrease score for wsdlfiles with different 
		 * number of services?
		 */
		
	}	

	public Mapping getFilemapping() 
	{
		return fileMapping;
	}

	public boolean isEdit() 
	{
		return edit;
	}
	

	public void setEdit(boolean edit) 
	{
		this.edit = edit;
	}
	

	public int getMatchStrategy() 
	{
		return matchStrategy;
	}
	
	public void setStructural() 
	{
		matchStrategy = 0;
	}
	
	public void setName() 
	{
		matchStrategy = 1;
	}

	public void setWordnet() 
	{
		matchStrategy = 2;
	}	
	
	public void setStructuralName() 
	{
		matchStrategy = 3;
	}	
	
	public void setStructuralWordnet()
	{
		matchStrategy = 4;
	}
	
	public void setNameWordnet()
	{
		matchStrategy = 5;
	}
	
	public void setAll() 
	{
		matchStrategy = 6;
	}

	public int getNameWeight() 
	{
		return nameWeight;
	}
	

	public void setNameWeight(int nameWeight) 
	{
		this.nameWeight = nameWeight;
	}
	

	public int getStructureWeight() 
	{
		return structureWeight;
	}
	

	public void setStructureWeight(int structureWeight) 
	{
		this.structureWeight = structureWeight;
	}
	

	public int getWordnetWeight() 
	{
		return wordnetWeight;
	}
	

	public void setWordnetWeight(int wordnetweight) 
	{
		this.wordnetWeight = wordnetweight;
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
