package de.dfki.wsdlanalyzer.matcher;

import java.io.File;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import de.dfki.wsdlanalyzer.mapping.Mapping;
import de.dfki.wsdlanalyzer.parser.SimpleTypeLookupTable;
import de.dfki.wsdlanalyzer.parser.WsdlFileParser;
import de.dfki.wsdlanalyzer.types.WsdlFile;
import de.dfki.wsdlanalyzer.wordnet.DictInstance;


import net.didion.jwnl.JWNLException;













/**
 * class for matching a requirements-wsdlfile against a list
 * (here contained in a directory in the file-system) of candidate-wsdlfiles 
 * and building a ranking of the requirementsfiles
 */
public class WsdlFileListMatcher 
{
	private WsdlFile candidate,requirementsFile;
	private SimpleTypeLookupTable lookupTable;
	private String directory,requirementsFileName;
	private TreeSet<FileScore> ranking;
	private HashMap<String,Mapping> mappinglist;
	private int maxMappingScore;
	private int fileCount;
	/*
	 * for evaluation only
	 */
	private float pr10,pr20,pr30,aus10,aus20,aus30;
	
	/**
	 * instance of wordnet dictionary
	 */
	private DictInstance dictionary;
	
	/**
	 * @param filename name of the candidatewsdlfile
	 * @param dirname name of the 'root-directory' containing the requirements-wdlfiles
	 * @param dict Wordnet-Dictionary instance (or null if Wordnet is not used)
	 * @param number (only for evaluation)
	 */
	public WsdlFileListMatcher(String filename,String dirname,DictInstance dict,int number)
	{
		requirementsFileName = filename;
		directory = dirname;
		dictionary = dict;
		ranking = new TreeSet<FileScore>();
		mappinglist = new HashMap<String,Mapping>();
		fileCount = number;
		
		
	}
	
	/**
	 * matches candidatefile against each wsdlfile
	 * found in directory or its subdirectories
	 */
	public void match()
	{
		lookupTable = new SimpleTypeLookupTable();
		//parse requirementsfile
		WsdlFileParser requirementsparser = new WsdlFileParser(requirementsFileName,lookupTable);
		requirementsparser.parseWsdl();
		requirementsFile = requirementsparser.getWsdlfile();
		try
		{
			WsdlFileMatcher matcher = new WsdlFileMatcher(requirementsFileName,requirementsFile,requirementsFileName,lookupTable,dictionary);
			//define matchStrategy
			matcher.setStructural();
			//matcher.setName();
			//matcher.setWordnet();
			//matcher.setStructuralName();
			//matcher.setStructuralWordnet();
			//matcher.setNameWordnet();
			//matcher.setAll();
			matcher.match();
			Mapping maxMapping = matcher.getFilemapping();
			maxMappingScore = maxMapping.getScore();
		}
		catch(JWNLException e)
		{
			e.printStackTrace();
		}
		
		File wsdldir = new File(directory);
		scanDirectory(wsdldir);
		printRanking();
		
		
	}
	
	/**
	 * scans all subdirectories of given file and matches
	 * the candidate-wsdlfiles in them with requirements-wsdlfile
	 */
	public void scanDirectory(File dir)
	{
		//get the files in the directory
		File[] dirs = dir.listFiles();
		for(int j=0;j<dirs.length;j++)
		{
			if(dirs[j].isDirectory())
			{
				scanDirectory(dirs[j]);
			}
			else
			{
				//test for wsdl file
				if((dirs[j].getAbsolutePath().endsWith(".wsdl"))||(dirs[j].getAbsolutePath().endsWith(".WSDL")))
				{
					matchFile(dirs[j]);
				}
			}
		}
	}
	
	/**
	 * matches wsdlfile against candidate-wsdlfile, 
	 * inserts match in ranking and saves mapping
	 * in list
	 */
	public void matchFile(File wsdlfile)
	{
		String candidateFileName = wsdlfile.getAbsolutePath();
		//System.out.println("\n***** Match for "+requirementsFileName+" *****\n");
		
		try
		{
			WsdlFileMatcher matcher = new WsdlFileMatcher(requirementsFileName,requirementsFile,candidateFileName,lookupTable,dictionary);
			//define matchStrategy
			matcher.setStructural();
			//matcher.setName();
			//matcher.setWordnet();
			//matcher.setStructuralName();
			//matcher.setStructuralWordnet();
			//matcher.setNameWordnet();
			//matcher.setAll();
			matcher.match();
			//get maxmatch for requirementsfile
			Mapping fileMapping = matcher.getFilemapping();
			//get score for fileMapping
			FileScore fileMatch = new FileScore(candidateFileName,fileMapping.getScore());
			fileMatch.setSimilarity((float)fileMapping.getScore()/(float)maxMappingScore);
			//add file to ranking
			ranking.add(fileMatch);
			//add mapping for file to mappinglist
			mappinglist.put(candidateFileName,fileMapping);
		}
		catch(JWNLException e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	public Mapping getMapping(String requirementsfilename)
	{
		return mappinglist.get(requirementsfilename);
	}
	
	public void printRanking()
	{
		System.out.println("\n!!!!!!ranked matching for "+requirementsFileName+"!!!!!!!\n");
		int baslash = requirementsFileName.lastIndexOf("\\");
		String vergleich = requirementsFileName.substring(0,baslash);
		Iterator<FileScore> it = ranking.iterator();
		int rank = ranking.size();
		int count = -1;
		int count2 = -1;
		int count3 = -1;
		while (it.hasNext())
		{
		    FileScore sco = it.next();
			//count++;
			if (rank<31)
			{
				int bslash = sco.getWsdlFile().lastIndexOf("\\");
				if(vergleich.equals(sco.getWsdlFile().substring(0,bslash)))
				{
					//System.out.println("req: "+vergleich);
					//System.out.println("req: "+sco.getWsdlFile().substring(0,bslash));
					count++;
				}
				if(rank<21)
				{
					if(vergleich.equals(sco.getWsdlFile().substring(0,bslash)))
					{
						//System.out.println("req: "+vergleich);
						//System.out.println("req: "+sco.getWsdlFile().substring(0,bslash));
						count3++;
					}
				}
				if(rank < 11)
				{
					if(vergleich.equals(sco.getWsdlFile().substring(0,bslash)))
					{
						//System.out.println("req: "+vergleich);
						//System.out.println("req: "+sco.getWsdlFile().substring(0,bslash));
						count2++;
					}
				}
					String hlp = sco.getWsdlFile();
					int le = hlp.length();
					for(int t=0;t<95-le;t++)
					{
						hlp = hlp+" ";
					}
					hlp = hlp+sco.getSimilarity()+", "+sco.getScore();
					//System.out.println(sco.getWsdlFile()+"   "+sco.getScore());
					System.out.println(rank+". "+hlp);
				//}
			}
			rank--;
			
		}
		pr10 = (float)count2/10;
		pr20 = (float)count3/20;
		pr30 = (float)count/30;
		aus10 = (float)count2/(float)(fileCount-1);
		aus20 = (float)count3/(float)(fileCount-1);
		aus30 = (float)count/(float)(fileCount-1);
		System.out.println("Treffer30: "+count+"  Treffer20: "+count3+"  Treffer10: "+count2);
		System.out.println("Präzisison30: "+pr30+"  Präzisison20: "+pr20+" Präzision10: "+pr10+"  Ausbeute30: "+aus30+"  Ausbeute20: "+aus20+"  Ausbeute10: "+aus10);
	}

	/*
	 * evaluation
	 */
	public float getAus10() {
		return aus10;
	}
	

	public float getAus30() {
		return aus30;
	}
	

	public float getPr10() {
		return pr10;
	}
	

	public float getPr30() {
		return pr30;
	}

	public float getAus20() {
		return aus20;
	}
	

	public float getPr20() {
		return pr20;
	}
	
	
	
}
