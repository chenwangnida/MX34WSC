package de.dfki.wsdlanalyzer.types;

/*florian
 *
 * Created on 08.07.2005
 *
 * 
 */


import java.util.*;
import java.io.Serializable;
/**
 * helper class for the gui
 * needed for identification of constructs in the wsdl-File
 * (similar to xpath)
 * @author Florian
 *
 */
public class NodeIdentifier  implements Serializable {
	
	/*
	 * Inner Class representing an identifier item, which is built from name and type
	 */
	
	public class IdentifierItem implements Serializable {
		public String name , type;
		
		public IdentifierItem( String name, String type ) {
			this.name = name;
			this.type = type;
		}
		
		public String toString() {
			return type + " : " + name;
		}
		
		/**
		 * Equality test
		 * @param item
		 * @return
		 */
		
		public boolean equals( IdentifierItem item) 
		{
			return ( name.equals( item.name) && type.equals( item.type ));
		}
	}
	
	private ArrayList<IdentifierItem> identifierList;
	
	public NodeIdentifier() {
		identifierList = new ArrayList<IdentifierItem>();
	}
	
	/**
	 * Creates a new NodeIdentifier from name and type
	 * @param name
	 * @param type
	 */
	
	public NodeIdentifier( String name, String type ) {
		this();
		identifierList.add( new IdentifierItem( name , type ));
	}
	
	/**
	 * Creates a copy of the NodeIdentifier and a appends the new name/type pair to it
	 * @param name
	 * @param type
	 * @param ni
	 */
	
	public NodeIdentifier( String name , String type , NodeIdentifier ni) {
		this( ni );
		identifierList.add( new IdentifierItem( name , type));
	}
	
	/**
	 * Creates a new NodeIdentifier from the given
	 * @param ni
	 */
	
	public NodeIdentifier( NodeIdentifier ni ) {
		this();
		identifierList.addAll( ni.getIdentifierList());
	}
	
	/**
	 * Concatenates the Identifier-Items to a String which is ues for hashing and string
	 * comparisation	
	 * @return
	 */
 
	protected String stringConcatenation()
	{
		String ret = "";
		
		for ( Iterator<IdentifierItem> it = identifierList.iterator() ; it.hasNext(); )
			ret += it.next().toString();
		return ret;
	}
	/**
	 * Equality check
	 */
	
	
	public boolean equals( Object object ) {
		try {
			NodeIdentifier ni = (NodeIdentifier)object;
			return this.stringConcatenation().equals( ni.stringConcatenation());
		}
		catch (Exception e) {
			return false;
		}
		
	}
	
	/**
	 * Returns the hashcode of the Identifier
	 */
	
	public int hashCode() {
		return this.stringConcatenation().hashCode();
	}

	public boolean isEmpty() {
		return identifierList.isEmpty();
	}
	
	/**
	 * Adds a new IdentifierItem to the List
	 * @param name
	 * @param type
	 */
	
	public void addIdentifier( String name , String type ) {
		identifierList.add( new IdentifierItem( name , type ));
	}
	
	public ArrayList<IdentifierItem> getIdentifierList() {
		return identifierList;
	}

	public Iterator<IdentifierItem> getIdentifierIterator() {
		return identifierList.iterator();
	}
	
	public void setIdentifierList(ArrayList<IdentifierItem> identifierList) {
		this.identifierList = identifierList;
	}
	
	public String toString() {
		return identifierList.toString();
	}
	
	public NodeIdentifier getBottomSubIdentifier() {
		if ( identifierList.size() < 2)
			return null;
		else {
			NodeIdentifier ret = new NodeIdentifier();
			for (int i = 1 ; i < identifierList.size() ; i++)
				ret.addIdentifier( identifierList.get(i).name , identifierList.get(i).type );
			return ret;
		}
	}

	public NodeIdentifier getTopSubIdentifier() {
		if ( identifierList.size() < 2)
			return null;
		else {
			NodeIdentifier ret = new NodeIdentifier();
			for (int i = 0 ; i < identifierList.size()-1 ; i++)
				ret.addIdentifier( identifierList.get(i).name , identifierList.get(i).type );
			return ret;
		}
	}
	
	/**
	 * Returns the last Identifier or null, if no Identifier is set
	 * @return
	 */
	
	public IdentifierItem getLastidentifier()
	{	
		if ( identifierList.size() > 0)
			return identifierList.get(identifierList.size()-1);
		return null;
	}
	

	
	/*
	 * added by hans
	 */
	public void addParentIdentifier( String name , String type ) {
		identifierList.add(0, new IdentifierItem( name , type ));
	}
	
	
	public void printNodeIdentifier()
	{
		System.out.println("\n++ NodeIdentifier ++");
		for(Iterator<IdentifierItem> it = identifierList.iterator();it.hasNext();)
		{
			System.out.println(it.next().toString());
		}
	}
}
