package de.dfki.wsdlanalyzer.types;
/**
 * representation of the grouping "sequence" of a complex type
 * the elements of the sequence are stored in an array
 * @author Hans
 *
 */
public class Sequence 
{
	private Element[] sequence;
	
	public Sequence(int number)
	{
		sequence = new Element[number];
	}
	
	public void setElement(int index,Element element)
	{
		sequence[index] = element;
	}
	
	public Element getElement(int index)
	{
		return sequence[index];
	}
	
	public int getLength()
	{
		return sequence.length;
	}
	
	public void print()
	{
		for(int i=0;i<sequence.length;i++)
		{
			sequence[i].printElement();
		}
	}
}
