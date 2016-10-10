package de.dfki.wsdlanalyzer.parser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;


import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.dfki.wsdlanalyzer.matcher.NameTokens;
import de.dfki.wsdlanalyzer.matcher.StopWords;
import de.dfki.wsdlanalyzer.types.Attribute;
import de.dfki.wsdlanalyzer.types.Binding;
import de.dfki.wsdlanalyzer.types.BindingOperation;
import de.dfki.wsdlanalyzer.types.ComplexType;
import de.dfki.wsdlanalyzer.types.Element;
import de.dfki.wsdlanalyzer.types.Message;
import de.dfki.wsdlanalyzer.types.MessageParameter;
import de.dfki.wsdlanalyzer.types.NodeIdentifier;
import de.dfki.wsdlanalyzer.types.Operation;
import de.dfki.wsdlanalyzer.types.Port;
import de.dfki.wsdlanalyzer.types.PortType;
import de.dfki.wsdlanalyzer.types.Service;
import de.dfki.wsdlanalyzer.types.TypeList;
import de.dfki.wsdlanalyzer.types.WsdlFile;


/**
 * class transforming the dom-tree of a wsdlfile into the 
 * internal datastructure
 * "WsdlFile" (which reflects the structure of webservices)
 *  containing all information
 * (typelists,messagelists, etc.) needed for the matching
 * algorithm
 */
public class WsdlFileParser 
{
	//WsdlFile parsed from inputfile
	private WsdlFile wsdlFile;
	//DOM-parser
	private Parser parser;
	//lookuptable for simpletypes
	private SimpleTypeLookupTable lookupTable;
	//dom model of inputfile
	private Document document;
	//temporary list of element-nodes for postprocessing
	private HashMap<String,Node> elementNodes;
	//temporary list of complexContent-nodes for postprocessing
	private HashMap<String,Node> complexContentNodes;
	//hashmap for namespaces, key:prefix, value: namespace
	private HashMap<String,String> nameSpaceMap;
	
	//stopwords for name-tokenization
	StopWords stopWords;
	//strings for types in nodeidentifier
	private String serviceType = "Service";
	private String portType = "Port";
	private String bindingType = "Binding";
	private String portTypeType = "PortType";
	private String operationType = "Operation";
	private String messageType = "Message";
	private String inputType = "Input";
	private String outputType = "Output";
	private String faultType = "Fault";
	private String parameterType = "Parameter";
	private String complexTypeType = "ComplexType";
	private String localTypeType = "LocalType";
	private String simpleType = "SimpleType";
	private String elementType = "Element";
	private String attributeType = "Attribute";
	//logger
	protected static Log log = LogFactory.getLog(WsdlFileParser.class.getName());
	
	public WsdlFileParser(String filename,SimpleTypeLookupTable t)
	{
		wsdlFile = new WsdlFile(filename);
		lookupTable = t;
		parser = new Parser(filename);
		document = parser.getDomTree();
		stopWords = new StopWords();
		nameSpaceMap = new HashMap<String,String>();
	}
	
	/**
	 * start the parsing
	 *
	 */
	public void parseWsdl()
	{
		parseNameSpaces();
		if(hasTypes())
		{
			//parseSimpleTypes();
			parseTypes();
			computeAllLeafNumbers();
		}
		parseMessages();
		parsePortTypes();
		parseBindings();
		parseServices();
		if (log.isDebugEnabled())
		{
			log.debug(wsdlFile.log());
		}
	}
	
	/**
	 * parse the namespaces in the definition-tag
	 */
	private void parseNameSpaces()
	{
		//get <definitons>
		//Node definitionsNode = document.getFirstChild();
		//System.out.println("firstchild: "+definitionsNode.getLocalName());
		NodeList definitionList = document.getElementsByTagName("wsdl:definitions");
		if(definitionList.getLength() == 0)
		{
			definitionList = document.getElementsByTagName("definitions");
		}
		//System.out.println("definitionnumber: "+definitionList.getLength());
		Node definitionsNode = definitionList.item(0);
		//get attributes
		NamedNodeMap definitionsAttributeMap = definitionsNode.getAttributes();
		//get targetnamespace
		String targetNamespace = null;
		if(definitionsAttributeMap != null)
		{
			/*for(int o=0;o<definitionsAttributeMap.getLength();o++)
			{
				Node attr = definitionsAttributeMap.item(o);
				System.out.println("name: "+attr.getLocalName()+" value: "+attr.getNodeValue());
			}*/
			if(definitionsAttributeMap.getNamedItem("targetNamespace") != null)
			{
				targetNamespace = definitionsAttributeMap.getNamedItem("targetNamespace").getNodeValue();
			}
		
			//System.out.println("targetNameSpace: "+targetNamespace+"\n");
			wsdlFile.setTargetNameSpace(targetNamespace);
			//other defined namespaces
			for(int i=0;i<definitionsAttributeMap.getLength();i++)
			{
				 Node attribute = definitionsAttributeMap.item(i);
				 //System.out.println("attribute: "+attribute.getNodeName()+", "+attribute.getNodeValue()); 
				 if(attribute.getNodeName().startsWith("xmlns"))
				 {
					if(attribute.getNodeName().contains(":"))
					{
						String prefix = withoutPrefix(attribute.getNodeName());
						nameSpaceMap.put(prefix,attribute.getNodeValue());
					}
					else
					{
						nameSpaceMap.put("default",attribute.getNodeValue());
					}
				 }
			}
		}
	}
	
	/**
	 * test for type definition in wsdl-file
	 * i.e. <types> tag
	 */
	private boolean hasTypes()
	{
		//get <definitions>
		NodeList documentchilds = document.getChildNodes();
		for(int h=0;h<documentchilds.getLength();h++)
		{
			//get childs of <definitions>
			NodeList grandchilds = documentchilds.item(h).getChildNodes();
			//System.out.println("documentchilds: "+documentchilds.item(h).getNodeName());
			for(int r=0;r<grandchilds.getLength();r++)
			{
				if(grandchilds.item(r).getNodeType() == Node.ELEMENT_NODE)
				{
					//test for nodename containing "types"
					if(grandchilds.item(r).getNodeName().contains("types"))
					{
						//System.out.println("grandchilds: "+grandchilds.item(r).getNodeName());
						return true;
					}
				}
				
			}
		}
		return false;
	}
	
	
	
	/**
	 * extracts types from wsdlfile and stores them in WsdlTypeList
	 * or SimpleTypeLookupTable according to their type(complex or simple)
	 * by inspecting all <schema>-tags which are the direct parents
	 * of all (named) <complexType>,<simpleType> or <element>-tags
	 *  and contain the targetnamespace
	 * for these types
	 */
	private void parseTypes()
	{
		/*
		 * create temporary list of element-nodes
		 * whose types are not known at their parsing-time
		 * for postprocessing
		 */
		elementNodes = new HashMap<String,Node>();
		/*
		 * create new tmplist for nodes of complextypes
		 * for post-processing extended/restricted complex
		 * types
		 */
		complexContentNodes = new HashMap<String,Node>();
		
		//get nodelist of <schema>-tags
		NodeList schemaList = document.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema","schema");
		
		//iterate over  <schema>-tags
		for (int i=0;i<schemaList.getLength();i++)
		{
			//get targetnmaespace
			NamedNodeMap schemaAttributeMap = schemaList.item(i).getAttributes();
			String targetNamespace = null;
			if(schemaAttributeMap.getNamedItem("targetNamespace") != null)
			{
				targetNamespace = schemaAttributeMap.getNamedItem("targetNamespace").getNodeValue();
			}
			
			/*
			 * get nodelist of childnodes of schemanode i.e
			 * go one stage down in xml-tree
			 */
			NodeList types = schemaList.item(i).getChildNodes();
			
			//iterate over typenodes
			for(int j=0;j<types.getLength();j++)
			{
				//test for elementnodes
				if (types.item(j).getNodeType() == Node.ELEMENT_NODE)
				{
					String kindofnode = types.item(j).getLocalName();
					//System.out.println("\n kind of node: "+kindofnode+"\n");
					/*
					 * test for kind of node
					 * <element>,<complexType> or <simpleType>
					 */
					if(kindofnode.equals("element"))
					{
						parseElementNode(types.item(j),targetNamespace);
					}
					else if(kindofnode.equals("complexType"))
					{
						//get typename from attributelist
						String typename = getAttributeValue(types.item(j),"name");
						if(typename != null)
						parseComplexTypeNode(typename,types.item(j),targetNamespace,false,null);
					}
					else if(kindofnode.equals("simpleType"))
					{
						parseSimpleTypeNode(types.item(j));
					}
					
					
					
				}
			}
		}
		//postprocessing
		if(!elementNodes.isEmpty())
		{
			//compute types for elementNodes in temporary list 
			for(Iterator<String> elementNameIterator = elementNodes.keySet().iterator();elementNameIterator.hasNext();)
			{
				String name = elementNameIterator.next();
				Node elementNode = elementNodes.get(name);
				parseElementNode(elementNode,null);
			}
		}
		if(!complexContentNodes.isEmpty())
		{
			//post-process nodes from tmpnodes
			for(Iterator<String> siter = complexContentNodes.keySet().iterator();siter.hasNext();)
			{
				String typename = siter.next();
				Node complexcontentnode = complexContentNodes.get(typename);
				ComplexType complextype = wsdlFile.getTypeList().getType(typename);
				parseComplexContent(complexcontentnode,complextype);
			}
		}
	}
	
	/**
	 * parses an elementnode
	 * 2 cases:
	 * 1. "simply typed" element node:
	 * 
	 * 		<element name="..." type="..."/>
	 * 
	 * 2."anonymous complex type:
	 * 
	 * <element name="...">
	 *   <complexType>
	 *   ...
	 *   </complexType>
	 * </element>
	 * by creating an anonymous type in wsdlfile
	 * by taking the elementname  as its typename
	 * and parsing the elements of the <complexType> tag
	 */
	private void parseElementNode(Node elementNode,String targetNamespace)
	{
		
		//get element name
		String elementName = getAttributeValue(elementNode,"name");
		//	create NodeIdentifier
		NodeIdentifier elementIdentifier = new NodeIdentifier(elementName,elementType);
		//elementIdentifier.printNodeIdentifier();
		//System.out.println("!!!!parseElementNode: "+elementName);
		if(elementNode.hasChildNodes())
		{
			
			//System.out.println("elementhaschilds: "+elementName);
			//get childnodes of elementNode
			NodeList children = elementNode.getChildNodes();
			//System.out.println("elementhaschildsnumber: "+elementname+", "+children.getLength());
			//iterate over childnodes
			for(int h=0;h<children.getLength();h++)
			{
				if(children.item(h).getNodeType() == Node.ELEMENT_NODE)
				{
					/*
					 * look for <complexType> tag => anonymous complexType
					 * treat it like complexType
					 */
					if(children.item(h).getLocalName().equals("complexType"))
					{
						parseComplexTypeNode(elementName,children.item(h),targetNamespace,false,null);
					}
					else
					{
						//TODO other tags then complexType????
						//System.out.println("\n+++++++++++ Attention ++++++++++\n"+children.item(h).getLocalName());
						if(log.isDebugEnabled())
							log.debug("unknown tag: "+children.item(h).getLocalName());
						
					}
				}
			}
		}
		else
		{
			/*
			 * element definition which only assigns a type to
			 * element e.g <element name="..." type="..."/>
			 */						
			//get typename						
			String typeName = getAttributeValue(elementNode,"type");
			
			String typePrefix = getPrefix(typeName);
			typeName = withoutPrefix(typeName);
			 
			//System.out.println(elementName+": type: "+typename);
			/*if(elementName.equals("name"))
			{
				System.out.println(elementName+": type: "+typename);
			}*/
			
			if(!elementName.equals(typeName))
			{
				if(lookupTable.lookupSimpleType(typeName))
				{
					//element has simpletype
					//insert elementname with simpletype typename in simpletypelookuptable
					lookupTable.insertSimpleType(elementName,typeName);
				}
				else
				{
					//element has complextype
					
					//get the complex type of the element
					ComplexType type = wsdlFile.getTypeList().getType(typeName);
					if(type != null)
					{
						/*
						*complexType known, i.e already parsed
						*insert element with its complexType 
						*in typelist of WsdlFile
						*/
						//System.out.println("insert: "+elementName+": type: "+type.getName());
						wsdlFile.getTypeList().insertComplexElement(elementName,type);
					}
					else
					{
						/*
						 * complex Type not known, i.e will be
						 * parsed later => add elementnode in
						 * temporary hashmap for postprocessing
						 */
						//System.out.println("postprocess: "+elementName+": type: "+typename);
						elementNodes.put(elementName,elementNode);
					}
						
				}
				//TODO not sure if this is correct
				Element element = new Element(elementName);
				element.setType(typeName);
				element.setTypePrefix(typePrefix);
				element.setNodeIdentifier(elementIdentifier);
				wsdlFile.getElementlist().put(elementName,element);
				
			}
		}
	}
	
	/**
	 * parses the content of the <complexType>...</complexType> tag
	 * and creates a complexType of name typename
	 */
	private void parseComplexTypeNode(String typename,Node complextypenode,String targetnamespace,boolean localtype,NodeIdentifier parentIdentifier)
	{
		//System.out.println("typ: "+typename+" node: "+complextypenode.getNodeName());
		//	create NodeIdentifier
		NodeIdentifier complexTypeIdentifier = new NodeIdentifier();
		// TODO nodeIdentifier!!!
		if(localtype)
		{
			//complexTypeIdentifier = getNodeIdentifier(complextypenode,typename);
			complexTypeIdentifier = new NodeIdentifier(parentIdentifier);
			complexTypeIdentifier.addIdentifier(typename,localTypeType);
		}
		else
		{
			complexTypeIdentifier.addIdentifier(typename,complexTypeType);
		}
		//complexTypeIdentifier.printNodeIdentifier();
		
		//create new complextype
		ComplexType complexType = new ComplexType(typename);
		//create name token for complex type
		NameTokens token = new NameTokens(typename,stopWords);
		complexType.setToken(token);
		complexType.setNodeIdentifier(complexTypeIdentifier);
		//explore childnodes
		if(complextypenode.hasChildNodes())
		{
			//get child nodes
			NodeList childs = complextypenode.getChildNodes();
			//iterate over childnodes
			for (int h=0;h<childs.getLength();h++)
			{
				//System.out.println("name: "+help.item(h).getLocalName()+" type "+help.item(h).getNodeType());
				if (childs.item(h).getNodeType() == Node.ELEMENT_NODE)
				{
					//System.out.println("parsecomplexType:element-name: "+childs.item(h).getLocalName());
					// look for <complexContent> tag
					if(childs.item(h).getLocalName().equals("complexContent"))
					{
						
						//System.out.println("\n++++ complexcontent ++++\n"+wsdlFile.getWsdlFileName());
						parseComplexContent(childs.item(h),complexType);
						
					}
					else if(childs.item(h).getLocalName().equals("simpleContent"))
					{
						parseSimpleContent(childs.item(h),complexType);
					}
					//	look for <sequence> tag
					else if(childs.item(h).getLocalName().equals("sequence"))
					{
						
						//System.out.println("Grouping: "+grouping+" length "+help.getLength());
						complexType.setGrouping(1);
						//get elements of this complex type
						if(childs.item(h).hasChildNodes())
						{
							NodeList elements = childs.item(h).getChildNodes();
							parseElementsOfComplexType(complexType,elements);
						}
					}
					//look for <all> tag
					else if(childs.item(h).getLocalName().equals("all"))
					{
						complexType.setGrouping(2);
						//get elements of this complex type
						if(childs.item(h).hasChildNodes())
						{
							NodeList elements = childs.item(h).getChildNodes();
							parseElementsOfComplexType(complexType,elements);
						}
					}
					//look for <choice> tag
					else if(childs.item(h).getLocalName().equals("choice"))
					{
						complexType.setGrouping(3);
						//get elements of this complex type
						if(childs.item(h).hasChildNodes())
						{
							NodeList elements = childs.item(h).getChildNodes();
							parseElementsOfComplexType(complexType,elements);
						}
					}
					//attribute of complexType
					else if(childs.item(h).getLocalName().equals("attribute"))
					{
						//System.out.println("\nattribute of complextype");
						if(complexType.getGrouping() == 0)
						{
							//complextype has no grouping i.e elements
							complexType.setGrouping(-1);
						}
							
						parseAttributeOfComplexType(complexType,childs.item(h));
					}
					else
					{
						//System.out.println("\n****** Attention ******\n"+childs.item(h).getLocalName());
						//TODO other childNodes of <complexType> ????
						if(log.isDebugEnabled())
							log.debug("unknown tag: "+childs.item(h).getLocalName());
					}
						
				}
			}
		}
		
		else
		{
			//TODO no childnodes
		}
		wsdlFile.getTypeList().insertComplexType(complexType);
	}
	
	
	
	/**
	 * parses simpletypenode
	 * by putting its name and its base-type 
	 * into simpletypelooktable
	 */
	private void parseSimpleTypeNode(Node simpletypenode)
	{
		//get typename from attributelist
		NamedNodeMap attributeMap = simpletypenode.getAttributes();
		String name = null;
		//System.out.println("length: "+attributeMap.getLength());
		if(attributeMap.getLength() > 0)
		{
			name = attributeMap.getNamedItem("name").getNodeValue();
			//System.out.println("simplename: "+name);
		}
		
		//get children of typenode
		NodeList simpleChilds = simpletypenode.getChildNodes();
		if(simpleChilds.getLength() > 0)
		{
			//iterate over children
			for(int h = 0;h<simpleChilds.getLength();h++)
			{
				if (simpleChilds.item(h).getNodeType() == Node.ELEMENT_NODE)
				{
					//System.out.println("typenode: "+simplechilds.item(i).getLocalName());
					//look for <restriction> tag
					if(simpleChilds.item(h).getLocalName().equals("restriction"))
					{
						NamedNodeMap restrictionattributes = simpleChilds.item(h).getAttributes();
						//get base-type
						String type = restrictionattributes.getNamedItem("base").getNodeValue();
						//System.out.println("type: "+type);
						//write typename with basetype in simpletypelookuptable
						if(type.indexOf(":") != -1)
						{
							String[] prefixtype = type.split(":");
							lookupTable.insertSimpleType(name,prefixtype[1]);
						}
						else
						{
							lookupTable.insertSimpleType(name,type);
						}
					}
				}
			}
		}
	}
	
	/**
	 * parses the elementnodes of a complextype 
	 * i.e the child nodes of a <sequence/all/choice> tag
	 * which needn't to be only <element> tags but can itself
	 * be again (nested) <sequence/all/choice> tags
	 */
	private void parseElementsOfComplexType(ComplexType complexType,NodeList elements)
	{
		//System.out.println("\n complextypename: "+complexType.getName());
		//System.out.println("elements: "+elements.getLength());
		//iterate over elementnodes
		for (int i=0;i<elements.getLength();i++)
		{
			if (elements.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				Node elementNode = elements.item(i);
				//System.out.println("node: "+elementNode.getLocalName());
				if (elementNode.getLocalName() != null)
				{
					//node is element
					if (elementNode.getLocalName().equals("element"))
					{
						//System.out.println("node: "+elementnode.getLocalName());
						//create new element
						Element element = new Element();
						
						//get elementname
						String elementName = getAttributeValue(elementNode,"name");
						//System.out.println("elementname: "+elementName);
						//node has name attribute
						if(elementName != null)
						{
							
							element.setName(elementName);
							//	create NodeIdentifier
							NodeIdentifier elementIdentifier = new NodeIdentifier(elementName,elementType,complexType.getNodeIdentifier());
							//elementIdentifier.addIdentifier(elementName,elementType);
							//elementIdentifier.printNodeIdentifier();
							
							//create name token for element
							//System.out.println("elementname: "+elementName);
							NameTokens token = new NameTokens(elementName,stopWords);
							element.setToken(token);
							element.setNodeIdentifier(elementIdentifier);
							//System.out.println("parseElement: "+elementattributes.getNamedItem("name").getNodeValue());
							
							
							
							//test for type of element
							String typehlp = getAttributeValue(elementNode,"type");
							//System.out.println("elementType: "+typehlp);
							//type defined for element
							if(typehlp != null)
							{
								//System.out.println("\nparseelement typeattribute prefix: "+elementattributes.getNamedItem("type").getPrefix()+" localname: "+elementattributes.getNamedItem("type").getLocalName());
								
								//System.out.println("nodevalue: "+hlp);
								//get elementtype
								//element.setType(elementattributes.getNamedItem("type").getNodeValue());
								int colon = typehlp.indexOf(":");
								if(colon == -1)
								{
									element.setType(typehlp);
									element.setElementPrefix(null);
									//test for simpletype
									if(lookupTable.lookupSimpleType(typehlp))
									{
										/*
										 * elementtype simple => leafelement
										 */
										//complexType.increaseNumberOfLeafElements();
										//complextype.increaseTotalLeaves(1);
										element.setSimple(true);
									}
									else
									{
										/*
										 * elementtype complex => nonleafelement
										 */
										//complexType.increaseNumberOfNonLeafElements();
										//ComplexType elementType =  wsdlFile.getTypeList().getType(typehlp);
										//complextype.increaseTotalLeaves(elementType.getTotalLeaves());
										element.setSimple(false);
									}
									
								}
								else
								{
									//get prefix and name
									String[] prefixname = typehlp.split(":");
									element.setElementPrefix(prefixname[0]);
									element.setType(prefixname[1]);
									//test for simpletype
									if(lookupTable.lookupSimpleType(prefixname[1]))
									{
										/*
										 * elementtype simple => leafelement
										 */
										//complexType.increaseNumberOfLeafElements();
										//complextype.increaseTotalLeaves(1);
										element.setSimple(true);
									}
									else
									{
										/*
										 * elementtype complex => nonleafelement
										 */
										//complexType.increaseNumberOfNonLeafElements();
										//System.out.println("elementType: "+prefixname[1]);
										//ComplexType elementType =  wsdlFile.getTypeList().getType(prefixname[1]);
										//complextype.increaseTotalLeaves(elementType.getTotalLeaves());
										element.setSimple(false);
									}
									
									
								}
								//System.out.println("type: "+element.getPrefix()+"\":\""+element.getType());
								
								
							}
							//no type defined for element
							else
							{
								
								if (elementNode.hasChildNodes())
								{
									/*
									 * element defined as 'anonymous' complexType
									 * e.g
									 * <element ...>
									 *   <complexType>
									 *    .
									 *    .
									 *    .
									 *   </complexType>
									 * </element>
									 * create complexType with name elementname+"LocalType"
									 */
									//System.out.println("hasChilds!");
									//get childs of elementnode
									NodeList childs = elementNode.getChildNodes();
									//iterate over childs
									//System.out.println("length: "+childs.getLength());
									//System.out.println("test: "+childs.item(1).getNodeName());
									for (int k=0;k<childs.getLength();k++)
									{
										//System.out.println("k: "+k);
										if(childs.item(k).getNodeType() == Node.ELEMENT_NODE)
										{
											//System.out.println("childs: "+childs.item(k).getLocalName());
											if (childs.item(k).getLocalName() != null)
											{
												//element has complex content 
												if (childs.item(k).getLocalName().equals("complexType"))
												{
													String localtype = elementName+"LocalType";
													element.setType(localtype);
													element.setSimple(false);
													//System.out.println("localcomplextype: "+localtype);
													
													parseComplexTypeNode(localtype,childs.item(k),complexType.getNameSpace(),true,complexType.getNodeIdentifier());
													
													//parseComplexTypeNode(localtype,childs.item(k),complexType.getNameSpace(),true);
													
													//complexType.increaseNumberOfNonLeafElements();
													//ComplexType elementType =  wsdlFile.getTypeList().getType(localtype);
													//complextype.increaseTotalLeaves(elementType.getTotalLeaves());
													
												}
												else
												{
													//TODO element has other childs then <complexType>????
													if(log.isDebugEnabled())
														log.debug("unknown tag: "+childs.item(k).getLocalName());
												}
											}
										}
										/*else
										{
											System.out.println("komisch");
											System.out.println(childs.item(k).getLocalName());
										}*/
									}
								}
								else
								{
									/*
									 * no type defined for element => anyType
									 * not sure if this correct??
									 */
									element.setType("anyType");
									element.setElementPrefix(null);
									element.setSimple(true);
									//complextype.increaseNumberOfLeafElements();
									//complextype.increaseTotalLeaves(1);
								}
							}
							
						}
						//node has no name-attribute
						else
						{
							/*
							 * elementNode has no Name-Attrubute
							 * element defined by reference "ref"
							 * set name and type of element to referenced type
							 */
							String ref = getAttributeValue(elementNode,"ref");
							/*
							 * ref case
							 */
							if(ref != null)
							{
								//elementName = elementattributes.getNamedItem("ref").getNodeValue();
								element.setName(ref);
								element.setType(ref);
								
								//create NodeIdentifier
								NodeIdentifier elementIdentifier = new NodeIdentifier(ref,elementType,complexType.getNodeIdentifier());
								//elementIdentifier.addIdentifier(elementName,elementType);
								//elementIdentifier.printNodeIdentifier();
								
								//create name token for element
								//System.out.println("elementname: "+elementName);
								NameTokens token = new NameTokens(ref,stopWords);
								element.setToken(token);
								element.setNodeIdentifier(elementIdentifier);
								//System.out.println("parseElement: "+elementattributes.getNamedItem("name").getNodeValue());
								
							}
							
						}
						/*
						 * additional attributes like
						 * nillable, min/maxOccurs
						 */
						
						//test for "nillability"
						String nillable = getAttributeValue(elementNode,"nillable");
						if(nillable != null)
						{
							
							if(nillable.equals("true"))
							{
								element.setNillable(true);
							}
							else
							{
								element.setNillable(false);
							}
						}
						//get min/max occurs
						String minOccurs = getAttributeValue(elementNode,"minOccurs");
						if (minOccurs != null)
						{
							
							Integer minoccur = new Integer(minOccurs);
							element.setMinOccur(minoccur.intValue());
						}
						else
							//minoccur not specified, default 1 
							element.setMinOccur(1);
						String maxOccurs = getAttributeValue(elementNode,"maxOccurs");
						if (maxOccurs != null)
						{
							
							if(maxOccurs.equals("unbounded"))
							{
								Integer maxoccur = new Integer(Integer.MAX_VALUE);
								element.setMaxOccur(maxoccur.intValue());
							}
							else
							{
								Integer maxoccur = new Integer(maxOccurs);
								element.setMaxOccur(maxoccur.intValue());
							}
						}
						else
							//maxoccur not specified, default 1 
							element.setMaxOccur(1);
						
						
						//add element to complex type
						complexType.addElement(element);
						//store element in elementlist TODO????
						//wsdlFile.getElementlist().put(elementName,element);
					}
					//node is not element
					else
					/*
					 * nested <all/sequence/choice> case
					 * e.g.
					 * <sequence>
					 *   <element ...>
					 *    ...
					 *    <choice>
					 *      <element..>
					 *      ...
					 *   </choice>
					 *   ...
					 * </sequence>
					 * create a new element for <all/sequence/choice> tag of
					 * a new artificial type (containing the elements of 
					 * <all/sequence/choice> tag)
					 * then create this artificial type as complex
					 * type and parse its elements 
					 */
					{
						//System.out.println("\n§§§§§§§§§ nested sequence §§§§§§§§§§\n"+wsdlFile.getWsdlFileName());
						
						if(elementNode.getLocalName().equals("all"))
						{
							Element artificialElement = new Element(complexType.getName()+"All");
							artificialElement.setType(complexType.getName()+"All");
							NodeIdentifier elementIdentifier = new NodeIdentifier(artificialElement.getName(),elementType,complexType.getNodeIdentifier());
							artificialElement.setNodeIdentifier(elementIdentifier);
							NameTokens token = new NameTokens(artificialElement.getName(),stopWords);
							artificialElement.setToken(token);
							
							parseArtificialComplexType(complexType,artificialElement,2,elementNode);
														
						}
						else if(elementNode.getLocalName().equals("choice"))
						{
							Element artificialElement = new Element(complexType.getName()+"Choice");
							artificialElement.setType(complexType.getName()+"Choice");
							NodeIdentifier elementIdentifier = new NodeIdentifier(artificialElement.getName(),elementType,complexType.getNodeIdentifier());
							artificialElement.setNodeIdentifier(elementIdentifier);
							NameTokens token = new NameTokens(artificialElement.getName(),stopWords);
							artificialElement.setToken(token);
							
							parseArtificialComplexType(complexType,artificialElement,3,elementNode);
														
						}
						else if(elementNode.getLocalName().equals("sequence"))
						{
							Element artificialElement = new Element(complexType.getName()+"Sequence");
							artificialElement.setType(complexType.getName()+"Sequence");
							NodeIdentifier elementIdentifier = new NodeIdentifier(artificialElement.getName(),elementType,complexType.getNodeIdentifier());
							artificialElement.setNodeIdentifier(elementIdentifier);
							NameTokens token = new NameTokens(artificialElement.getName(),stopWords);
							artificialElement.setToken(token);
							
							parseArtificialComplexType(complexType,artificialElement,1,elementNode);
														
						}
						else
						{
							System.out.println("\n&&&&&&& unknown child of <all/choice/sequence> &&&&&&\n"+wsdlFile.getWsdlFileName());
							//TODO handle new case other then <all/sequence/choice>
							if(log.isDebugEnabled())
								log.debug("unknown child-tag of <all/choice/sequence>: "+elementNode.getLocalName());
						}
						
					}
				}
			}
		}
	}
	
	/**
	 * parses content of <complexContent>...</complexContent>
	 */
	private void parseComplexContent(Node complexContentNode,ComplexType complexType)
	{
		//get childnodes of complexcontent-node
		NodeList childs = complexContentNode.getChildNodes();
		//System.out.println("childs: "+n.hasChildNodes()+" nr: "+arraychilds.getLength());
		//iterate over childs
		for (int i=0;i<childs.getLength();i++)
		{
			if (childs.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				if(childs.item(i).getLocalName()!= null)
				{
					//look for <restriction> tag
					if(childs.item(i).getLocalName().equals("restriction"))
					{
						
						//look for array-type
						String base = getAttributeValue(childs.item(i),"base");
						if(base.equals("soapenc:Array"))
						{
							complexType.setArray();
							//get child-tags of <restriction>
							NodeList restrictionchilds = childs.item(i).getChildNodes();
							//System.out.println("childs: "+arraychilds.item(i).hasChildNodes()+" nr: "+restrictionchilds.getLength());
							for(int j=0;j<restrictionchilds.getLength();j++)
							{
								if(restrictionchilds.item(j).getLocalName() != null)
								{
									//look for <attribute>
									if(restrictionchilds.item(j).getLocalName().equals("attribute"))
									{
										//get type of array from attribute
										String arrayType = getAttributeValue(restrictionchilds.item(j),"wsdl:arrayType");
										if(arrayType != null)
										{
											//System.out.println("arraytype: "+arraytype);
											String[] prefixType = arrayType.split(":");
											//int b = prefixtype[1].indexOf("[");
											//System.out.println("prefix: "+prefixtype[0]+" type: "+prefixtype[1]);
											//eliminate the [] from the type
											String hlp = prefixType[1].substring(0,prefixType[1].length()-2);
											//System.out.println("help: "+hlp);
											complexType.setArrayPrefix(prefixType[0]);
											complexType.setArrayType(hlp);
											break;
										}
									}
									//look for <sequence>
									else if(restrictionchilds.item(j).getLocalName().equals("sequence"))
									{
										NodeList sequencechilds = restrictionchilds.item(j).getChildNodes();
										for(int h=0;h<sequencechilds.getLength();h++)
										{
											if(sequencechilds.item(h).getNodeType() == Node.ELEMENT_NODE)
											{
												
												String typeName = getAttributeValue(sequencechilds.item(h),"type");
												if(typeName != null)
												{
													String[] type = typeName.split(":");
													complexType.setArrayPrefix(type[0]);
													complexType.setArrayType(type[1]);
												}
											}
										}
									}
								}
							}
						}
						//type is not an array
						else 
						{
							//TODO restriction of complextype which is no arraytype
						}
					}
					//look for <extension> tag
					else if(childs.item(i).getLocalName().equals("extension"))
					{
						//System.out.println("\ncomplexType: "+complextype.getName()+"\n");
						//get base-type of the extension
						
						String baseName = getAttributeValue(childs.item(i),"base");
						if(baseName != null)
						{
							String[] base = baseName.split(":");
							//TODO what if no ':' contained
							//System.out.println("base: "+base[1]);
							//base type already parsed
							if(wsdlFile.getTypeList().containsKey(base[1]))
							{
								//System.out.println("base known");
								//get basetype
								ComplexType basetype = wsdlFile.getTypeList().getType(base[1]);
								//add elementlist from basetype to complextype
								complexType.addElementList(basetype.getElementList());
								//parse childnodes
								NodeList extensionchilds = childs.item(i).getChildNodes();
								//iterate over childs
								for(int m=0;m<extensionchilds.getLength();m++)
								{
									if(extensionchilds.item(m).getNodeType() == Node.ELEMENT_NODE)
									{
									
										//look for <sequence> tag
										if(extensionchilds.item(m).getLocalName().equals("sequence"))
										{
											
											//System.out.println("Grouping: "+grouping+" length "+help.getLength());
											complexType.setGrouping(1);
					
											//get elements of this complex type
											if(extensionchilds.item(m).hasChildNodes())
											{
												NodeList elements = extensionchilds.item(m).getChildNodes();
												parseElementsOfComplexType(complexType,elements);
											}
										}
										//look for <all> tag
										else if(extensionchilds.item(m).getLocalName().equals("all"))
										{
											complexType.setGrouping(2);
											//get elements of this complex type
											if(extensionchilds.item(m).hasChildNodes())
											{
												NodeList elements = extensionchilds.item(m).getChildNodes();
												parseElementsOfComplexType(complexType,elements);
											}
										}
										//look for <choice> tag
										else if(extensionchilds.item(m).getLocalName().equals("choice"))
										{
											complexType.setGrouping(3);
											//get elements of this complex type
											if(extensionchilds.item(m).hasChildNodes())
											{
												NodeList elements = extensionchilds.item(m).getChildNodes();
												parseElementsOfComplexType(complexType,elements);
											}
										}
										else if(extensionchilds.item(m).getLocalName().equals("attribute"))
										{
											//System.out.println("\nattribute of complextype");
											//set grouping of complextype to grouping of basetype
											complexType.setGrouping(basetype.getGrouping());
											
											
											parseAttributeOfComplexType(complexType,extensionchilds.item(m));
										}
										else
										{
											//TODO other tags???
											if(log.isDebugEnabled())
												log.debug("unknown tag: "+extensionchilds.item(m).getLocalName());
											
										}
									}
								}
							}
							//base type not yet parsed => put int temporary list
							else
							{
								//System.out.println("base unknown!!!");
								complexContentNodes.put(complexType.getName(),complexContentNode);
							}
						}
					
					}		
				}
			}
		}
			
		
	}
	
	/**
	 * parses content of <simpleContent>...</simpleContent>
	 */
	private void parseSimpleContent(Node simpleContentNode,ComplexType complexType)
	{
		NodeList simpleContentChilds = simpleContentNode.getChildNodes();
		for(int i=0;i<simpleContentChilds.getLength();i++)
		{
			if(simpleContentChilds.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				/*extension
				 * => create artificial element of basetype
				 * and add it to complexType's elementlist
				*/
				if(simpleContentChilds.item(i).getLocalName().equals("extension"))
				{
					String baseType = getAttributeValue(simpleContentChilds.item(i),"base");
					if(baseType != null)
					{
						Element artificialElement = new Element(complexType.getName());
						artificialElement.setType(withoutPrefix(baseType));
						NodeIdentifier artificialIdentifier = new NodeIdentifier(complexType.getName(),elementType,complexType.getNodeIdentifier());
						artificialElement.setNodeIdentifier(artificialIdentifier);
						NameTokens token = new NameTokens(complexType.getName(),stopWords);
						artificialElement.setToken(token);
						complexType.getElementList().add(artificialElement);
					}
					if(simpleContentChilds.item(i).hasChildNodes())
					{
						parseExtensionNode(simpleContentChilds.item(i),complexType);
					}
				}
				//restriction
				else if(simpleContentChilds.item(i).getLocalName().equals("restriction"))
				{
					//TODO
				}
				//other tag ?
				else
				{
					//TODO
					if(log.isDebugEnabled())
						log.debug("unknown tag: "+simpleContentChilds.item(i).getLocalName());
				}
			}
		}
		
	}
	
	/**
	 * parse the attribtes of a complexType
	 * @param complexType
	 * @param attribute
	 */
	private void parseAttributeOfComplexType(ComplexType complexType,Node attribute)
	{
		//System.out.println("\n####### Attribute ########\n"+wsdlFile.getWsdlFileName());
		
		//get the attribute from the "<attribute>"-tag
		String attributeName = getAttributeValue(attribute,"name");
		String attributesType = null;
		String attributeUse = null;
		//System.out.println("prefix-test: "+getAttributeValue(attribute,"type")+", "+attributeName);
		if(getAttributeValue(attribute,"type") != null)
		{
			attributesType = withoutPrefix(getAttributeValue(attribute,"type"));
		}
		if(getAttributeValue(attribute,"use") != null)
		{
			attributeUse = getAttributeValue(attribute,"use");
		}
		//TODO default or fixed values
		//creat new attribute
		Attribute complexTypeAttribute = new Attribute(attributeName,attributesType,attributeUse);
		NodeIdentifier attributeIdentifier = new NodeIdentifier(attributeName,attributeType,complexType.getNodeIdentifier());
		complexTypeAttribute.setId(attributeIdentifier);
		//System.out.println("test: "+complexTypeAttribute.getId().toString());
		NameTokens attributeToken = new NameTokens(attributeName,stopWords);
		complexTypeAttribute.setToken(attributeToken);
		if(attribute.hasChildNodes())
		{
			NodeList attributeChilds = attribute.getChildNodes();
			for(int i=0;i<attributeChilds.getLength();i++)
			{
				Node attributeChild = attributeChilds.item(i);
				if(attributeChild.getNodeType() == Node.ELEMENT_NODE)
				{
					if(attributeChild.getLocalName().equals("simpleType"))
					{
						NodeList simpleTypeChilds = attributeChild.getChildNodes();
						for(int j=0;j<simpleTypeChilds.getLength();j++)
						{
							Node simpleTypeChild = simpleTypeChilds.item(j);
							if(simpleTypeChild.getNodeType() == Node.ELEMENT_NODE)
							{
								if(simpleTypeChild.getLocalName().equals("restriction"))
								{
									String baseType = withoutPrefix(getAttributeValue(simpleTypeChild,"base"));
									complexTypeAttribute.setType(baseType);
									if(simpleTypeChild.hasChildNodes())
									{
										//TODO handle the enumeration/min/maxinclusive etc. tags
									}
								}
							}
						}
					}
				}
			}
		}
		complexType.addAttribute(attributeName,complexTypeAttribute);
	}
	
	private void parseExtensionNode(Node extensionNode,ComplexType complexType)
	{
		NodeList extensionChildren = extensionNode.getChildNodes();
		for(int i=0;i<extensionChildren.getLength();i++)
		{
			Node extensionChild = extensionChildren.item(i);
			if(extensionChild.getNodeType() == Node.ELEMENT_NODE)
			{
				if(extensionChild.getLocalName().equals("attribute"))
				{
					parseAttributeOfComplexType(complexType,extensionChild);
				}
				else
				{
					//TODO other tags then <attribute>
					if(log.isDebugEnabled())
						log.debug("unknown tag: "+extensionChild.getLocalName());
				}
			}
		}
	}
	/**
	 * parse the <message> tags
	 *
	 */
	private void parseMessages()
	{
		//	get nodelist of <message>-tags
		NodeList messagelist = document.getElementsByTagName("wsdl:message");
		if(messagelist.getLength() == 0)
		{
			messagelist = document.getElementsByTagName("message");
		}
		//iterate over messages
		for (int i=0;i<messagelist.getLength();i++)
		{
			//get message name
			String messageName = getAttributeValue(messagelist.item(i),"name");
			
			//create NodeIdentifier
			NodeIdentifier messageIdentifier = new NodeIdentifier(messageName,messageType);
			//messageIdentifier.printNodeIdentifier();
			//create new message
			Message message = new Message(messageName);
			//create name token for message
			NameTokens token = new NameTokens(messageName,stopWords);
			message.setToken(token);
			message.setNodeIdentifier(messageIdentifier);
			//get parameters
			NodeList parameters = messagelist.item(i).getChildNodes();
			//System.out.println("number of parameter of <message> tag: "+parameters.getLength());
			for (int j=0;j<parameters.getLength();j++)
			{
				if (parameters.item(j).getNodeType() == Node.ELEMENT_NODE)
				{
					if (parameters.item(j).getLocalName().contains("part"))
					{
						//get parametername
						String parameterName = getAttributeValue(parameters.item(j),"name");
						//create name token for parameter
						NameTokens parameterToken = new NameTokens(parameterName,stopWords);
						//	create NodeIdentifier
						NodeIdentifier parameterIdentifier = new NodeIdentifier(messageIdentifier);
						parameterIdentifier.addIdentifier(parameterName,parameterType);
						//parameterIdentifier.printNodeIdentifier();
						String parametertype = "";
						//get parametertype
						String element = getAttributeValue(parameters.item(j),"element");
						if(element != null)
						{
							parametertype = element;
						}
						else 
						{
							String type = getAttributeValue(parameters.item(j),"type");
							if(type != null)
							{
								parametertype = type;
							}
						}
						String[] type = parametertype.split(":");
						if(lookupTable.lookupSimpleType(type[1]))
						{
							parameterIdentifier.addIdentifier(type[1],simpleType);
						}
						else
						{
							parameterIdentifier.addIdentifier(type[1],complexTypeType);
						}
						
						//create new messageparameter
						MessageParameter messageParameter = new MessageParameter(parameterName,type[0],type[1]);
						messageParameter.setToken(parameterToken);
						messageParameter.setNodeIdentifier(parameterIdentifier);
						//add parameter to message
						message.insertParameter(messageParameter);
					}
				}
			}
			//add message to wsdlfile
			wsdlFile.getMessageList().insertMessage(message);
		}
	}
	
	/**
	 * parses nested <all/choice/sequence>-tags
	 */
	private void parseArtificialComplexType(ComplexType complexType,Element artificialElement,int grouping, Node elementNode)
	{
		//		add element to complex type
		complexType.addElement(artificialElement);
		//store element in elementlist TODO????
		//wsdlFile.getElementlist().put(artificialElement.getName(),artificialElement);
		//create and new complexType
		ComplexType artificialComplexType = new ComplexType(artificialElement.getName());
		artificialComplexType.setGrouping(grouping);
		artificialComplexType.setNodeIdentifier(complexType.getNodeIdentifier());
		NameTokens artificialToken = new NameTokens(artificialComplexType.getName(),stopWords);
		artificialComplexType.setToken(artificialToken);
		/*
		 * parse this "type" i.e the
		 * content of the nested <all/choice/sequence>-tag
		 */
		NodeList nestedElements = elementNode.getChildNodes();
		parseElementsOfComplexType(artificialComplexType,nestedElements);
		wsdlFile.getTypeList().insertComplexType(artificialComplexType);
	}
	
	/**
	 * parses <portType> tags
	 *
	 */
	private void parsePortTypes()
	{
		//get list of <wsdl:portType> tag(s)
		NodeList porttypelist = document.getElementsByTagName("wsdl:portType");
		if(porttypelist.getLength() == 0)
		{
			porttypelist = document.getElementsByTagName("portType");
		}
		//iterate over porttypes
		for (int i=0;i<porttypelist.getLength();i++)
		{
			//get porttype name
			String portTypeName = getAttributeValue(porttypelist.item(i),"name");
			//create NodeIdentifier
			NodeIdentifier portTypeIdentifier = new NodeIdentifier(portTypeName,portTypeType);
			//portTypeIdentifier.printNodeIdentifier();
			//create new porttype
			PortType portType = new PortType(portTypeName);
			//create name token for porttype
			NameTokens token = new NameTokens(portTypeName,stopWords);
			portType.setToken(token);
			portType.setNodeIdentifier(portTypeIdentifier);
			//parse the operations of the porttype
			parseOperations(porttypelist.item(i),portType);
			//add porttype to porttypelist of wsdlfile
			wsdlFile.getPorttypelist().put(portTypeName,portType);
			
		}
	}
	/**
	 * parses <operation> tags
	 * @param porttypenode
	 * @param porttype
	 */
	private void parseOperations(Node porttypenode,PortType porttype)
	{
		if(porttypenode.hasChildNodes())
		{
			//get childnodes
			NodeList portTypeChilds = porttypenode.getChildNodes();
			//iterate over childs
			for(int j=0;j<portTypeChilds.getLength();j++)
			{
				Node operationNode = portTypeChilds.item(j);
				if(operationNode.getNodeType() == Node.ELEMENT_NODE)
				{
					if(operationNode.getNodeName().contains("operation"))
					{
						//get operation name
						String operationName = getAttributeValue(operationNode,"name");
						//		create NodeIdentifier
						NodeIdentifier operationIdentifier = new NodeIdentifier(porttype.getNodeIdentifier());
						operationIdentifier.addIdentifier(operationName,operationType);
						//operationIdentifier.printNodeIdentifier();
						//create new operation
						Operation operation = new Operation(operationName);
						operation.setNodeIdentifier(operationIdentifier);
						//create name token for operation
						NameTokens token = new NameTokens(operationName,stopWords);
						operation.setToken(token);
						//get parameterorder
						String parameterOrder = getAttributeValue(operationNode,"parameterOrder");
						if(parameterOrder != null)
						{
							
							operation.setParameterorder(parameterOrder.split(" "));
						}
						//get the input/outputmessages for the operation
						parseMessages(operationNode,operation);
						//add operation to porttype
						porttype.insertOperation(operation);
						//add operation to operationList
						wsdlFile.getOperationlist().insertOperation(operation);
					}
				}
			}
		}
	}
	/**
	 * parses the <input/output/fault> tags of an operation
	 * @param operationnode
	 * @param operation
	 */
	private void parseMessages(Node operationnode,Operation operation)
	{
		if(operationnode.hasChildNodes())
		{
			//get childnodes
			NodeList operationchilds = operationnode.getChildNodes();
			//iterate over childs
			for(int j=0;j<operationchilds.getLength();j++)
			{
				Node messagenode = operationchilds.item(j);
				if(messagenode.getNodeType() == Node.ELEMENT_NODE)
				{
					NamedNodeMap attributes = messagenode.getAttributes();
					//get inputmessage
					if(messagenode.getNodeName().contains("input"))
					{
						String inputName = attributes.getNamedItem("message").getNodeValue();
						inputName = withoutPrefix(inputName);
						//	create NodeIdentifier
						NodeIdentifier inputIdentifier = new NodeIdentifier(operation.getNodeIdentifier());
						inputIdentifier.addIdentifier(inputName,inputType);
						//inputIdentifier.printNodeIdentifier();
						//add inputmessage to operation
						operation.setInput(inputName);
						operation.setInputId(inputIdentifier);
					}
					//get outputmessage
					else if(messagenode.getNodeName().contains("output"))
					{
						String outputName = attributes.getNamedItem("message").getNodeValue();
						outputName = withoutPrefix(outputName);
						//	create NodeIdentifier
						NodeIdentifier outputIdentifier = new NodeIdentifier(operation.getNodeIdentifier());
						outputIdentifier.addIdentifier(outputName,outputType);
						//outputIdentifier.printNodeIdentifier();
						//add outputmessage to operation
						operation.setOutput(outputName);
						operation.setOutputId(outputIdentifier);
					}
					//get faultmessage
					else if(messagenode.getNodeName().contains("fault"))
					{
						String faultName = attributes.getNamedItem("message").getNodeValue();
						faultName = withoutPrefix(faultName);
						//	create NodeIdentifier
						NodeIdentifier faultIdentifier = new NodeIdentifier(operation.getNodeIdentifier());
						faultIdentifier.addIdentifier(faultName,faultType);
						//faultIdentifier.printNodeIdentifier();
						//add faultmessage to operation
						operation.setFault(faultName);
						operation.setFaultId(faultIdentifier);
					}
				}
			}
		}
	}
	/**
	 * parses <binding> tags
	 *
	 */
	private void parseBindings()
	{
		//get list of <wsdl:binding> tag(s)
		NodeList bindinglist = document.getElementsByTagName("wsdl:binding");
		if(bindinglist.getLength() == 0)
		{
			bindinglist = document.getElementsByTagName("binding");
		}
		//iterate over bindings
		for (int i=0;i<bindinglist.getLength();i++)
		{
			//get binding name
			NamedNodeMap attributemap = bindinglist.item(i).getAttributes();
			String bindingName = attributemap.getNamedItem("name").getNodeValue();
			//create new binding
			Binding binding = new Binding(bindingName);
			//get porttype for this binding
			String porttype = attributemap.getNamedItem("type").getNodeValue();
			porttype = withoutPrefix(porttype);
			binding.setPortType(porttype);
			//create name token for binding
			NameTokens token = new NameTokens(bindingName,stopWords);
			binding.setToken(token);
			//create nodeidentifier
			NodeIdentifier bindingId = new NodeIdentifier(bindingName,bindingType);
			binding.setNodeIdentifier(bindingId);
			
			/*
			 * explore children of binding node
			 */
			NodeList children = bindinglist.item(i).getChildNodes();
			for (int j=0;j<children.getLength();j++)
			{
				Node child = children.item(j);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					//System.out.println("Name: "+child.getNodeName());
					if(child.getNodeName().contains("soap:binding"))
					{
						binding.setBindingType("SOAP");
						NamedNodeMap soapBindingAttributes = child.getAttributes();
						if(soapBindingAttributes.getNamedItem("style") != null)
						{
							String style = soapBindingAttributes.getNamedItem("style").getNodeValue();
							if(style.equals("rpc"))
							{
								//System.out.println("Style: "+style);
								binding.setRPCStyle(true);
							}
							else
							{
								binding.setRPCStyle(false);
							}
						}
						else
							binding.setRPCStyle(false);
						String transport = soapBindingAttributes.getNamedItem("transport").getNodeValue();
						binding.setTransport(transport);
						
					}
					else if(child.getNodeName().contains("operation"))
					{
						
						BindingOperation bindingOperation = parseBindingOperation(child,binding.isRPCStyle());
						binding.addBindingOperation(bindingOperation.getName(),bindingOperation);
					}
					else
					{
						/*
						 * TODO hhtp-get and -post
						 */
					}
				}
				
			}
			//add binding to bindings of wsdlfile
			wsdlFile.getBindings().put(bindingName,binding);
		}
	}
	/**
	 * parses <operation> of a binding
	 * @param node
	 * @param style
	 * @return
	 */
	private BindingOperation parseBindingOperation(Node node, boolean style)
	{
		NamedNodeMap operationAttributes = node.getAttributes();
		BindingOperation bindingOperation = new BindingOperation(operationAttributes.getNamedItem("name").getNodeValue());
		NodeList operationChildren = node.getChildNodes();
		for(int i=0;i<operationChildren.getLength();i++)
		{
			Node child = operationChildren.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE)
			{
				if(child.getNodeName().contains("soap:operation"))
				{
					//System.out.println("Operation: "+child.getNodeName());
					NamedNodeMap soapAttributes = child.getAttributes();
					if(soapAttributes.getNamedItem("soapAction") != null)
						bindingOperation.setSoapAction(soapAttributes.getNamedItem("soapAction").getNodeValue());
					if(soapAttributes.getNamedItem("style") != null)
					{
						String type = soapAttributes.getNamedItem("style").getNodeValue();
						if(type.equals("rpc"))
							bindingOperation.setRPC(true);
						else
							bindingOperation.setRPC(false);
					}
					else
						bindingOperation.setRPC(style);
				}
				else if(child.getNodeName().contains("input"))
				{
					NodeList inputChildren = child.getChildNodes();
					for(int j=0;j<inputChildren.getLength();j++)
					{
						Node inputChild = inputChildren.item(j);
						if(inputChild.getNodeType() == Node.ELEMENT_NODE)
						{
							if(inputChild.getNodeName().contains("soap:body"))
							{
								NamedNodeMap inputAttributes = inputChild.getAttributes();
								String use = inputAttributes.getNamedItem("use").getNodeValue();
								//System.out.println("use: "+use);
								if(use.equals("literal"))
								{
									bindingOperation.setInputLiteral(true);
									bindingOperation.setInputEncoding(null);
								}
								else
								{
									bindingOperation.setInputLiteral(false);
									bindingOperation.setInputEncoding(inputAttributes.getNamedItem("encodingStyle").getNodeValue());
								}
								if(inputAttributes.getNamedItem("namespace") != null)
								{
									bindingOperation.setInputNameSpace(inputAttributes.getNamedItem("namespace").getNodeValue());
								}
							}
						}
					}
				}
				else if(child.getNodeName().contains("output"))
				{
					NodeList outputChildren = child.getChildNodes();
					for(int j=0;j<outputChildren.getLength();j++)
					{
						Node outputChild = outputChildren.item(j);
						if(outputChild.getNodeType() == Node.ELEMENT_NODE)
						{
							if(outputChild.getNodeName().contains("soap:body"))
							{
								NamedNodeMap outputAttributes = outputChild.getAttributes();
								String use = outputAttributes.getNamedItem("use").getNodeValue();
								if(use.equals("literal"))
								{
									bindingOperation.setOutputLiteral(true);
									bindingOperation.setOutputEncoding(null);
								}
								else
								{
									bindingOperation.setOutputLiteral(false);
									bindingOperation.setOutputEncoding(outputAttributes.getNamedItem("encodingStyle").getNodeValue());
								}
								if(outputAttributes.getNamedItem("namespace") != null)
								{
									bindingOperation.setOutputNameSpace(outputAttributes.getNamedItem("namespace").getNodeValue());
								}
							}
						}
					}
				}
				else if(child.getNodeName().contains("fault"))
				{
					/*
					 * TODO fault-handling
					 */
				}
			}
			
		}
		//bindingOperation.print();
		return bindingOperation;
	}
	/**
	 * parses <service> tag
	 *
	 */
	private void parseServices()
	{
		//get list of <wsdl:service> tag(s)
		NodeList servicelist = document.getElementsByTagName("wsdl:service");
		if(servicelist.getLength() == 0)
		{
			servicelist = document.getElementsByTagName("service");
		}
		for (int i=0;i<servicelist.getLength();i++)
		{
			//get service name
			NamedNodeMap attributemap = servicelist.item(i).getAttributes();
			String serviceName = attributemap.getNamedItem("name").getNodeValue();
			//get name tokens
			NameTokens token = new NameTokens(serviceName,stopWords);
			
			//create NodeIdentifier for servicenode
			NodeIdentifier serviceIdentifier = new NodeIdentifier(serviceName,serviceType);
			//serviceIdentifier.printNodeIdentifier();
			//create new service
			Service service = new Service(serviceName);
			service.setToken(token);
			service.setNodeIdentifier(serviceIdentifier);
			//get childs of service
			NodeList servicechilds = servicelist.item(i).getChildNodes();
			//iterate over childs
			for(int j=0;j<servicechilds.getLength();j++)
			{
				Node portNode = servicechilds.item(j);
				if(portNode.getNodeType() == Node.ELEMENT_NODE)
				{
					//System.out.println("Nodename: "+port.getNodeName());
					//get port
					if(portNode.getNodeName().contains("port"))
					{
						NamedNodeMap portattributemap = portNode.getAttributes();
						String portName = portattributemap.getNamedItem("name").getNodeValue();
						//create new Port
						Port port = new Port(portName);
						//create name token for portname
						NameTokens portToken = new NameTokens(portName,stopWords);
						//create nodeidentifir for portnode
						NodeIdentifier portId = new NodeIdentifier(serviceIdentifier);
						portId.addIdentifier(portName,portType);
						String bindingName = portattributemap.getNamedItem("binding").getNodeValue();
						bindingName = withoutPrefix(bindingName);
						port.setBinding(bindingName);
						port.setToken(portToken);
						port.setNodeIdentifier(portId);
						//get the endpoint-address for this port
						NodeList portChildren = portNode.getChildNodes();
						for(int h=0;h<portChildren.getLength();h++)
						{
							Node addressNode = portChildren.item(h);
							if(addressNode.getNodeType() == Node.ELEMENT_NODE)
							{
								if(addressNode.getNodeName().contains("address"))
								{
									NamedNodeMap addressattributemap = addressNode.getAttributes();
									String address = addressattributemap.getNamedItem("location").getNodeValue();
									//System.out.println("address: "+address);
									try
									{
										port.setAddress(new URL(address));
									}
									catch(MalformedURLException ex)
									{
										ex.printStackTrace();
									}
								}
							}
						}
						//add port,binding to service
						service.put(portName,port);
					}
				}
			}
			//addservice to servicelist of wsdlfile
			wsdlFile.getServicelist().put(serviceName,service);
		}
	}
	
	/*
	 * get the suffix of string after ":"
	 */
	private String withoutPrefix(String s)
	{
		if(s.contains(":"))
		{
			String[] split= s.split(":");
			return split[1];
		}
		else
			return s;
	}	
	
	/*
	 * get the prefix of string before ":"
	 */
	private String getPrefix(String s)
	{
		if(s.contains(":"))
		{
			String[] split= s.split(":");
			return split[0];
		}
		else
			return null;
		
	}
	
	private NodeIdentifier getNodeIdentifier(Node node,String name)
	{
		Node help = node;
		String nodeName = name;
		//String nodeName = node.getNodeName()+" "+name;
		NodeIdentifier identifier = new NodeIdentifier(nodeName,complexTypeType);
		while(help.getParentNode() != null)
		{
			help = help.getParentNode();
			
			String parentType = getNodeType(help);
			if(parentType!=null)
			{
				String parentName = getAttributeValue(help,"name");
				if(parentName != null)
				{
					if(!name.equals(parentName))
					{
						nodeName = parentName;
						//nodeName = help.getNodeName()+" "+parentName;
						identifier.addParentIdentifier(nodeName,parentType);
					}
				}
			}
			//else nodeName = help.getNodeName();
			//identifier.addParent(nodeName);
		}
		return identifier;
	}
	
	
	/*
	 * returns the value of the attribute with the specified
	 * name or null if it doesn't exist
	 */
	private String getAttributeValue(Node node, String attributeName)
	{
		//get attributelist of node
		NamedNodeMap attributeMap = node.getAttributes();
		if(attributeMap != null)
		{
			//get attributeNode for attributeName
			Node attributeNode = attributeMap.getNamedItem(attributeName);
			if(attributeNode != null)
			{
				return attributeNode.getNodeValue();
			}
			else return null;
		}
		return null;
	}
	
	/*
	private String getNameAttribute(Node node)
	{
		NamedNodeMap attributeMap = node.getAttributes();
		if(attributeMap != null)
		{
			Node nameAttribute = attributeMap.getNamedItem("name");
			if(nameAttribute != null)
			{
				return nameAttribute.getNodeValue();
			}
			else return null;
		}
		else return null;
	}
	*/
	private String getNodeType(Node node)
	{
		String type = node.getLocalName();
		//System.out.println("\n+++ type: "+type);
		if(type.equals("element"))
			return type;
		else if(type.equals("complexType"))
			return type;
		else
			return null;
	}
	
	/*
	 * computes leaf/non-leaf numbers for all complexTypes
	 */
	private void computeAllLeafNumbers()
	{
		TypeList typeList = wsdlFile.getTypeList();
		for(Iterator<String> it = typeList.nameIterator();it.hasNext();)
		{
			String name = it.next();
			ComplexType type = typeList.getType(name);
			if(name.equals(type.getName()))
			{
				computeLeafNumbers(type);
			}
		}
		/*for(Iterator<ComplexType> tyit=typeList.typeIterator();tyit.hasNext();)
		{
			ComplexType type = tyit.next();
			//System.out.println("computeAllTotaleaves for "+type.getName()+": "+type.getTotalLeaves());
			//System.out.println("computeTotaleaves for "+type.getName());
			computeLeafNumbers(type);
			
		}*/
	}
	
	/*  
	 * computes the number of leaf and non-leaf nodes
	 * of a complexType
	 */
	private void computeLeafNumbers(ComplexType type)
	{
		//System.out.println("computeTotaleaves for "+type.getName());
		for(Iterator<Element> elit = type.getElementList().iterator();elit.hasNext();)
		{
			Element element = elit.next();
			//System.out.println("element: "+element.getName());
			if(!element.getType().equals("anyType"))
			{
				//check type-prefix
				if(element.getElementPrefix() == null)
				{
					/*
					 * no prefix first try simpletypelookup
					 */
					//simpleType-element
					if(lookupTable.lookupSimpleType(element.getType()))
					{
						//System.out.println("simple element: "+element.getName());
						
						type.increaseNumberOfLeafElements();
					}
					//complexType-element
					else
					{
						//System.out.println("complex element: "+element.getName()+" type: "+element.getType());
						if(wsdlFile.getTypeList().getType(element.getType()) != null)
						{
							//computeTotalLeaves(elementType);
							type.increaseNumberOfNonLeafElements();
						}
					}
				}
				else if(element.getElementPrefix().startsWith("xs"))
				{
					/*
					 * prefix starts with xs => first
					 * try simpletypelookup
					 */
					//simpleType-element
					if(lookupTable.lookupSimpleType(element.getType()))
					{
						//System.out.println("simple element: "+element.getName());
						
						type.increaseNumberOfLeafElements();
					}
					//complexType-element
					else
					{
						//System.out.println("complex element: "+element.getName()+" type: "+element.getType());
						if(wsdlFile.getTypeList().getType(element.getType()) != null)
						{
							//computeTotalLeaves(elementType);
							type.increaseNumberOfNonLeafElements();
						}
					}
				}
				else
				{
					/*
					 * other prefix => first try typelistlookup
					 */
					if(wsdlFile.getTypeList().getType(element.getType()) != null)
					{
						//computeTotalLeaves(elementType);
						type.increaseNumberOfNonLeafElements();
					}
					else
					{
						if(lookupTable.lookupSimpleType(element.getType()))
						{
							//System.out.println("simple element: "+element.getName());
							
							type.increaseNumberOfLeafElements();
						}
					}
				}
			}
		}
		
	}
	
	public WsdlFile getWsdlfile() 
	{
		return wsdlFile;
	}

	public SimpleTypeLookupTable getLookuptable() {
		return lookupTable;
	}
	
	
}
