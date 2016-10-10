package de.dfki.wsdlanalyzer.matcher;

/**
 * class for constructing a ranking of the wsdlfile-matchings
 * 
 */
public class FileScore implements Comparable<FileScore>
{
	private String wsdlfile;
	private int score;
	private float similarity;

	/**
	 * 
	 * @param s name of the WSDL-File
	 * @param n matchingscore of this file
	 */
	public FileScore(String s,int n)
	{
		wsdlfile = s;
		score = n;
	}
	
	public String getWsdlFile()
	{
		return wsdlfile;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public int compareTo(FileScore s)
	{
		if(this.wsdlfile.equals(s.getWsdlFile()))
		{
			// TODO Auto-generated method stub
			return 0;
		}
		else
		{
			if(this.score == s.getScore())
			{
				return this.wsdlfile.compareTo(s.getWsdlFile());
			}
			else if(this.score<s.getScore())
			{
				return -1;
			}
			else if(this.score>s.getScore())
			{
				return 1;
			}
		}
		return 0;
	}

	public float getSimilarity() {
		return similarity;
	}
	

	public void setSimilarity(float similarity) {
		this.similarity = similarity;
	}
	

}

