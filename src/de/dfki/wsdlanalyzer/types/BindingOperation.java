package de.dfki.wsdlanalyzer.types;

/**
 * representation of the operations described within a binding
 * only used for transformation of soap-messages
 * @author Hans
 *
 */
public class BindingOperation 
{
	private String name;
	private String soapAction;
	private boolean style,inputUse,outputUse;
	private String inputEncoding,outputEncoding,inputNameSpace,outputNameSpace;
	
	public BindingOperation(String operationname)
	{
		name = operationname;
	}

	public String getInputEncoding() 
	{
		return inputEncoding;
	}
	

	public void setInputEncoding(String inputEncoding) 
	{
		this.inputEncoding = inputEncoding;
	}
	

	public boolean isInputLiteral() 
	{
		return inputUse;
	}
	

	public void setInputLiteral(boolean inputUse) 
	{
		this.inputUse = inputUse;
	}
	

	public String getOutputEncoding() 
	{
		return outputEncoding;
	}
	

	public void setOutputEncoding(String outputEncoding) 
	{
		this.outputEncoding = outputEncoding;
	}
	

	public boolean isOutputLiteral() 
	{
		return outputUse;
	}
	

	public void setOutputLiteral(boolean outputUse) 
	{
		this.outputUse = outputUse;
	}
	

	public String getSoapAction() 
	{
		return soapAction;
	}
	

	public void setSoapAction(String soapAction) 
	{
		this.soapAction = soapAction;
	}
	

	public boolean isRPC() 
	{
		return style;
	}
	

	public void setRPC(boolean style) 
	{
		this.style = style;
	}
	

	public String getName() 
	{
		return name;
	}

	public String getInputNameSpace() {
		return inputNameSpace;
	}
	

	public void setInputNameSpace(String inputNameSpace) {
		this.inputNameSpace = inputNameSpace;
	}
	

	public String getOutputNameSpace() {
		return outputNameSpace;
	}
	

	public void setOutputNameSpace(String outputNameSpace) {
		this.outputNameSpace = outputNameSpace;
	}
	
	public void print()
	{
		System.out.println("**BindingOperation**\n");
		System.out.println("Name: "+name);
		System.out.println("Action: "+soapAction);
		if(style)
			System.out.println("Style: RPC");
		else
			System.out.println("Style: Document");
		if(inputUse)
			System.out.println("iputUse: Literal");
		else
			System.out.println("inputUse: Encoded");
		if(inputNameSpace != null)
			System.out.println("Input-Ns: "+inputNameSpace);
		if(outputUse)
			System.out.println("outputUse: Literal");
		else
			System.out.println("outputUse: Encoded");
		if(outputNameSpace != null)
			System.out.println("Output-Ns: "+outputNameSpace);
		System.out.println("\n**End of BindingOperation**");
	}

}
