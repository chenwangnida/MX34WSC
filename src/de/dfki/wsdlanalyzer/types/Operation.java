package de.dfki.wsdlanalyzer.types;

import de.dfki.wsdlanalyzer.matcher.NameTokens;

/**
 * class for representing a wsdl operation i.e
 * <wsdl:operation...>...</wsdl:operation>
 * references to the contained messages(input/output/fault)
 * by messagenames(i.e. strings)
 */

public class Operation 
{
	private String name;
	private String input,output,fault;
	//nodeidentifier for input/output/fault-message
	private NodeIdentifier inputId,outputId,faultId;
	String[] parameterorder;
	private NameTokens token;
	//	NodeIdentifier for visualization
	NodeIdentifier nodeIdentifier;
	
	public Operation(String s)
	{
		name = s;
		nodeIdentifier = new NodeIdentifier();
	}
	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}

	public String getInput() 
	{
		return input;
	}
	

	public void setInput(String input) 
	{
		this.input = input;
	}

	public String getOutput() {
		return output;
	}
	

	public void setOutput(String output) 
	{
		this.output = output;
	}

	public String getFault() 
	{
		return fault;
	}
	

	public void setFault(String fault) 
	{
		this.fault = fault;
	}

	public String[] getParameterorder() 
	{
		return parameterorder;
	}
	

	public void setParameterorder(String[] parameterorder) 
	{
		this.parameterorder = parameterorder;
	}

	public NameTokens getToken() {
		return token;
	}
	

	public void setToken(NameTokens token) {
		this.token = token;
	}
	

	public NodeIdentifier getNodeIdentifier() 
	{
		return nodeIdentifier;
	}
	

	public void setNodeIdentifier(NodeIdentifier nodeIdentifier) 
	{
		this.nodeIdentifier = nodeIdentifier;
	}

	public NodeIdentifier getFaultId() {
		return faultId;
	}
	

	public void setFaultId(NodeIdentifier faultId) {
		this.faultId = faultId;
	}
	

	public NodeIdentifier getInputId() {
		return inputId;
	}
	

	public void setInputId(NodeIdentifier inputId) {
		this.inputId = inputId;
	}
	

	public NodeIdentifier getOutputId() {
		return outputId;
	}
	

	public void setOutputId(NodeIdentifier outputId) {
		this.outputId = outputId;
	}
	
	
	
	
	
	
	
}
