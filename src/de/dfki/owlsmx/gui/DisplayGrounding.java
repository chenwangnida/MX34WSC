package de.dfki.owlsmx.gui;

import java.util.Iterator;
import java.net.URI;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.mindswap.owl.OWLOntology;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Output;

import de.dfki.owlsmx.gui.data.ServiceItem;
import de.dfki.owlsmx.mxp.OWLSMXPServiceInformation;
import de.dfki.wsdlanalyzer.parser.SimpleTypeLookupTable;
import de.dfki.wsdlanalyzer.types.Attribute;
import de.dfki.wsdlanalyzer.types.ComplexType;
import de.dfki.wsdlanalyzer.types.Element;

/**
 * This class creates a GUI that displays the WSDL grounding of a
 * service. 
 * 
 * @author Patrick Kapahnke
 */
public class DisplayGrounding extends JPanel {

	private static final long serialVersionUID = -183893492834L;
	
	private JScrollPane			scrollPane;
	private DefaultTreeModel	groundingTreeModel;
	private JTree				groundingTree;
	
	/**
	 * Constructor takes the service as ServiceItem from the
	 * parent GUI.
	 * 
	 * @param service the ServiceItem for which the WSDL grounding should be displayed
	 */
	public DisplayGrounding(ServiceItem service) {
		initComponents();
		buildTree(service);
		
		groundingTree.setModel(groundingTreeModel);
	}
	
	private void initComponents() {
		scrollPane = new JScrollPane();
		groundingTree = new JTree();
		
		setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
		
		scrollPane.setViewportView(groundingTree);
        add(scrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 310, 350));
	}
	
	/**
	 * This method is called internally to setup the tree that represents
	 * the WSDL grounding.
	 * 
	 * @param service the ServiceItem of the service to display
	 */
	private void buildTree(ServiceItem service) {
		// set the root node of the new tree
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(service.getName());
		groundingTreeModel = new DefaultTreeModel(root);
		
		// load service information
		OWLSMXPServiceInformation serviceInfo = null;
		OWLOntology serviceOnto = null;
		try {
			serviceOnto = org.mindswap.owl.OWLFactory.createKB().read(service.getURI());
			serviceInfo = new OWLSMXPServiceInformation(serviceOnto, new SimpleTypeLookupTable());
		}
		catch(Exception e) {
			return;
		}
				
		// add inputs
		DefaultMutableTreeNode inputs = new DefaultMutableTreeNode("inputs");
		root.add(inputs);
		
		DefaultMutableTreeNode node;
		DefaultMutableTreeNode subNode;
		for(Iterator iter = serviceOnto.getService().getProcess().getInputs().iterator(); iter.hasNext(); ) {
			Input input = (Input) iter.next();
			String inputType = input.getParamType().getURI().getFragment();
			String inputName = input.getLocalName();
			node = new DefaultMutableTreeNode(inputName + ":" + inputType);
			inputs.add(node);
			try {
				if(serviceInfo.hasSimpleType(inputName)) {
					String simpleType = serviceInfo.getSimpleType(inputName);
					subNode = new DefaultMutableTreeNode(simpleType);
					node.add(subNode);
				}
				else {
					addComplexTypeNode(node, serviceInfo.getComplexType(inputName), serviceInfo);
				}
			}
			catch(Exception e) {
			}
		}
		
		// add outputs
		DefaultMutableTreeNode outputs = new DefaultMutableTreeNode("outputs");
		root.add(outputs);
		
		for(Iterator iter = serviceOnto.getService().getProcess().getOutputs().iterator(); iter.hasNext(); ) {
			Output output = (Output) iter.next();
			String outputType = output.getParamType().getURI().getFragment();
			String outputName = output.getLocalName();
			node = new DefaultMutableTreeNode(outputName + ":" + outputType);
			outputs.add(node);
			try {
				if(serviceInfo.hasSimpleType(outputName)) {
					String simpleType = serviceInfo.getSimpleType(outputName);
					subNode = new DefaultMutableTreeNode(simpleType);
					node.add(subNode);
				}
				else {
					addComplexTypeNode(node, serviceInfo.getComplexType(outputName), serviceInfo);
				}
			}
			catch(Exception e) {
			}
		}		
	}
	
	/**
	 * Adds nodes and subnodes for complex WSDL types to the tree.
	 * 
	 * @param node the node to add the complex type to
	 * @param complexType the complex type to add
	 * @param serviceInfo the service information as given by the parent GUI
	 */
	private void addComplexTypeNode(DefaultMutableTreeNode node, ComplexType complexType, OWLSMXPServiceInformation serviceInfo) {
		// add a node for the grouping
		DefaultMutableTreeNode groupingNode = null;
		if(complexType.getGrouping() == 1) groupingNode = new DefaultMutableTreeNode("sequence");
		if(complexType.getGrouping() == 2) groupingNode = new DefaultMutableTreeNode("all");
		if(complexType.getGrouping() == 3) groupingNode = new DefaultMutableTreeNode("choice");
		node.add(groupingNode);
		
		// add elements to the grouping node
		DefaultMutableTreeNode elementNode;
		for(Iterator iter = complexType.getElementList().iterator(); iter.hasNext(); ) {
			Element element = (Element) iter.next();
			elementNode = new DefaultMutableTreeNode(element.getName() + ":" + element.getType());
			groupingNode.add(elementNode);
			if(!element.isSimple()) {
				// recursion
				ComplexType subType = serviceInfo.getWsdlFile().getTypeList().getType(element.getType());
				addComplexTypeNode(elementNode, subType, serviceInfo);
			}
		}
		
		// add attributes to the complex type node
		DefaultMutableTreeNode attributeNode;
		for(Iterator iter = complexType.attributeIterator(); iter.hasNext(); ) {
			Attribute attribute = (Attribute) iter.next();
			attributeNode = new DefaultMutableTreeNode(attribute.getName() + ":" + attribute.getType());
			node.add(attributeNode);
		}
	}
}
