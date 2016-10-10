package de.dfki.wsdlanalyzer.parser;

import java.util.HashMap;

/**
 * class containing the matching scores for XSD built-in types
 * (not complete, can be modified if needed)
 */

public class SimpleTypeLookupTable 
{
	
	private final Integer maxscore = new Integer(1);
	private final Integer minscore = new Integer(0);
	//hashtables for all predefined xsd-types
	private HashMap<String,HashMap<String,Integer>> xsdTypes;
	private HashMap<String,Integer> xsdbyte,xsddecimal,xsdint,xsdinteger,xsdlong,xsdshort;
	private HashMap<String,Integer> xsdlanguage,xsdName,xsdnormalizedString,xsdQName,xsdstring,xsdtoken;
	private HashMap<String,Integer> xsddate,xsddateTime,xsdduration, xsdtime;
	private HashMap<String,Integer> xsdanyUri,xsdboolean,xsddouble,xsdfloat,xsdbase64Binary;
	private HashMap<String,Integer> xsdNMTOKEN,xsdgYear,xsdgMonth,xsdgDay,anyType;
	private HashMap<String,String> selfdefinedtypes;
	
	public SimpleTypeLookupTable()
	{
		//matching scores for byte to ... matching
		xsdbyte = new HashMap<String,Integer>();
		xsdbyte.put("byte",maxscore);
		xsdbyte.put("decimal",maxscore);
		xsdbyte.put("int",maxscore);
		xsdbyte.put("integer",maxscore);
		xsdbyte.put("long",maxscore);
		xsdbyte.put("short",maxscore);
		xsdbyte.put("language",minscore);
		xsdbyte.put("Name",minscore);
		xsdbyte.put("normalizedString",minscore);
		xsdbyte.put("QName",minscore);
		xsdbyte.put("string",minscore);
		xsdbyte.put("token",minscore);
		xsdbyte.put("NMTOKEN",minscore);
		xsdbyte.put("date",minscore);
		xsdbyte.put("dateTime",minscore);
		xsdbyte.put("duration",minscore);
		xsdbyte.put("time",minscore);
		xsdbyte.put("gYear",minscore);
		xsdbyte.put("gMonth",minscore);
		xsdbyte.put("gDay",minscore);
		xsdbyte.put("anyUri",minscore);
		xsdbyte.put("boolean",minscore);
		xsdbyte.put("double",minscore);
		xsdbyte.put("float",minscore);
		xsdbyte.put("base64Binary",minscore);
		xsdbyte.put("anyType",maxscore);
		
		//matching scores for decimal to ... matching
		xsddecimal = new HashMap<String,Integer>();
		xsddecimal.put("byte",minscore);
		xsddecimal.put("decimal",maxscore);
		xsddecimal.put("int",minscore);
		xsddecimal.put("integer",minscore);
		xsddecimal.put("long",minscore);
		xsddecimal.put("short",minscore);
		xsddecimal.put("language",minscore);
		xsddecimal.put("Name",minscore);
		xsddecimal.put("normalizedString",minscore);
		xsddecimal.put("QName",minscore);
		xsddecimal.put("string",minscore);
		xsddecimal.put("token",minscore);
		xsddecimal.put("NMTOKEN",minscore);
		xsddecimal.put("date",minscore);
		xsddecimal.put("dateTime",minscore);
		xsddecimal.put("duration",minscore);
		xsddecimal.put("time",minscore);
		xsddecimal.put("gYear",minscore);
		xsddecimal.put("gMonth",minscore);
		xsddecimal.put("gDay",minscore);
		xsddecimal.put("anyUri",minscore);
		xsddecimal.put("boolean",minscore);
		xsddecimal.put("double",minscore);
		xsddecimal.put("float",minscore);
		xsddecimal.put("base64Binary",minscore);
		xsddecimal.put("anyType",maxscore);
		
		//matching scores for int to ... matching
		xsdint = new HashMap<String,Integer>();
		xsdint.put("byte",minscore);
		xsdint.put("decimal",maxscore);
		xsdint.put("int",maxscore);
		xsdint.put("integer",maxscore);
		xsdint.put("long",maxscore);
		xsdint.put("short",minscore);
		xsdint.put("language",minscore);
		xsdint.put("Name",minscore);
		xsdint.put("normalizedString",minscore);
		xsdint.put("QName",minscore);
		xsdint.put("string",minscore);
		xsdint.put("token",minscore);
		xsdint.put("NMTOKEN",minscore);
		xsdint.put("date",minscore);
		xsdint.put("dateTime",minscore);
		xsdint.put("duration",minscore);
		xsdint.put("time",minscore);
		xsdint.put("gYear",minscore);
		xsdint.put("gMonth",minscore);
		xsdint.put("gDay",minscore);
		xsdint.put("anyUri",minscore);
		xsdint.put("boolean",minscore);
		xsdint.put("double",minscore);
		xsdint.put("float",minscore);
		xsdint.put("base64Binary",minscore);
		xsdint.put("anyType",maxscore);
		
		//matching scores for integer to ... matching
		xsdinteger = new HashMap<String,Integer>();
		xsdinteger.put("byte",minscore);
		xsdinteger.put("decimal",maxscore);
		xsdinteger.put("int",minscore);
		xsdinteger.put("integer",maxscore);
		xsdinteger.put("long",minscore);
		xsdinteger.put("short",minscore);
		xsdinteger.put("language",minscore);
		xsdinteger.put("Name",minscore);
		xsdinteger.put("normalizedString",minscore);
		xsdinteger.put("QName",minscore);
		xsdinteger.put("string",minscore);
		xsdinteger.put("token",minscore);
		xsdinteger.put("NMTOKEN",minscore);
		xsdinteger.put("date",minscore);
		xsdinteger.put("dateTime",minscore);
		xsdinteger.put("duration",minscore);
		xsdinteger.put("time",minscore);
		xsdinteger.put("gYear",minscore);
		xsdinteger.put("gMonth",minscore);
		xsdinteger.put("gDay",minscore);
		xsdinteger.put("anyUri",minscore);
		xsdinteger.put("boolean",minscore);
		xsdinteger.put("double",minscore);
		xsdinteger.put("float",minscore);
		xsdinteger.put("base64Binary",minscore);
		xsdinteger.put("anyType",maxscore);
		
		//matching scores for long to ... matching
		xsdlong = new HashMap<String,Integer>();
		xsdlong.put("byte",minscore);
		xsdlong.put("decimal",maxscore);
		xsdlong.put("int",minscore);
		xsdlong.put("integer",maxscore);
		xsdlong.put("long",maxscore);
		xsdlong.put("short",minscore);
		xsdlong.put("language",minscore);
		xsdlong.put("Name",minscore);
		xsdlong.put("normalizedString",minscore);
		xsdlong.put("QName",minscore);
		xsdlong.put("string",minscore);
		xsdlong.put("token",minscore);
		xsdlong.put("NMTOKEN",minscore);
		xsdlong.put("date",minscore);
		xsdlong.put("dateTime",minscore);
		xsdlong.put("duration",minscore);
		xsdlong.put("time",minscore);
		xsdlong.put("gYear",minscore);
		xsdlong.put("gMonth",minscore);
		xsdlong.put("gDay",minscore);
		xsdlong.put("anyUri",minscore);
		xsdlong.put("boolean",minscore);
		xsdlong.put("double",minscore);
		xsdlong.put("float",minscore);
		xsdlong.put("base64Binary",minscore);
		xsdlong.put("anyType",maxscore);
		
		//matching scores for short to ... matching
		xsdshort = new HashMap<String,Integer>();
		xsdshort.put("byte",minscore);
		xsdshort.put("decimal",maxscore);
		xsdshort.put("int",maxscore);
		xsdshort.put("integer",maxscore);
		xsdshort.put("long",maxscore);
		xsdshort.put("short",maxscore);
		xsdshort.put("language",minscore);
		xsdshort.put("Name",minscore);
		xsdshort.put("normalizedString",minscore);
		xsdshort.put("QName",minscore);
		xsdshort.put("string",minscore);
		xsdshort.put("token",minscore);
		xsdshort.put("NMTOKEN",minscore);
		xsdshort.put("date",minscore);
		xsdshort.put("dateTime",minscore);
		xsdshort.put("duration",minscore);
		xsdshort.put("time",minscore);
		xsdshort.put("gYear",minscore);
		xsdshort.put("gMonth",minscore);
		xsdshort.put("gDay",minscore);
		xsdshort.put("anyUri",minscore);
		xsdshort.put("boolean",minscore);
		xsdshort.put("double",minscore);
		xsdshort.put("float",minscore);
		xsdshort.put("base64Binary",minscore);
		xsdshort.put("anyType",maxscore);
		
		//matching scores for language to ... matching
		xsdlanguage = new HashMap<String,Integer>();
		xsdlanguage.put("byte",minscore);
		xsdlanguage.put("decimal",minscore);
		xsdlanguage.put("int",minscore);
		xsdlanguage.put("integer",minscore);
		xsdlanguage.put("long",minscore);
		xsdlanguage.put("short",minscore);
		xsdlanguage.put("language",maxscore);
		xsdlanguage.put("Name",minscore);
		xsdlanguage.put("normalizedString",maxscore);
		xsdlanguage.put("QName",minscore);
		xsdlanguage.put("string",maxscore);
		xsdlanguage.put("token",maxscore);
		xsdlanguage.put("NMTOKEN",minscore);
		xsdlanguage.put("date",minscore);
		xsdlanguage.put("dateTime",minscore);
		xsdlanguage.put("duration",minscore);
		xsdlanguage.put("time",minscore);
		xsdlanguage.put("gYear",minscore);
		xsdlanguage.put("gMonth",minscore);
		xsdlanguage.put("gDay",minscore);
		xsdlanguage.put("anyUri",minscore);
		xsdlanguage.put("boolean",minscore);
		xsdlanguage.put("double",minscore);
		xsdlanguage.put("float",minscore);
		xsdlanguage.put("base64Binary",minscore);
		xsdlanguage.put("anyType",maxscore);
		
		//matching scores for Name to ... matching
		xsdName = new HashMap<String,Integer>();
		xsdName.put("byte",minscore);
		xsdName.put("decimal",minscore);
		xsdName.put("int",minscore);
		xsdName.put("integer",minscore);
		xsdName.put("long",minscore);
		xsdName.put("short",minscore);
		xsdName.put("language",minscore);
		xsdName.put("Name",maxscore);
		xsdName.put("normalizedString",maxscore);
		xsdName.put("QName",minscore);
		xsdName.put("string",maxscore);
		xsdName.put("token",maxscore);
		xsdName.put("NMTOKEN",minscore);
		xsdName.put("date",minscore);
		xsdName.put("dateTime",minscore);
		xsdName.put("duration",minscore);
		xsdName.put("time",minscore);
		xsdName.put("gYear",minscore);
		xsdName.put("gMonth",minscore);
		xsdName.put("gDay",minscore);
		xsdName.put("anyUri",minscore);
		xsdName.put("boolean",minscore);
		xsdName.put("double",minscore);
		xsdName.put("float",minscore);
		xsdName.put("base64Binary",minscore);
		xsdName.put("anyType",maxscore);
		
		//matching scores for normalizedString to ... matching
		xsdnormalizedString = new HashMap<String,Integer>();
		xsdnormalizedString.put("byte",minscore);
		xsdnormalizedString.put("decimal",minscore);
		xsdnormalizedString.put("int",minscore);
		xsdnormalizedString.put("integer",minscore);
		xsdnormalizedString.put("long",minscore);
		xsdnormalizedString.put("short",minscore);
		xsdnormalizedString.put("language",minscore);
		xsdnormalizedString.put("Name",minscore);
		xsdnormalizedString.put("normalizedString",maxscore);
		xsdnormalizedString.put("QName",minscore);
		xsdnormalizedString.put("string",maxscore);
		xsdnormalizedString.put("token",minscore);
		xsdnormalizedString.put("NMTOKEN",minscore);
		xsdnormalizedString.put("date",minscore);
		xsdnormalizedString.put("dateTime",minscore);
		xsdnormalizedString.put("duration",minscore);
		xsdnormalizedString.put("time",minscore);
		xsdnormalizedString.put("gYear",minscore);
		xsdnormalizedString.put("gMonth",minscore);
		xsdnormalizedString.put("gDay",minscore);
		xsdnormalizedString.put("anyUri",minscore);
		xsdnormalizedString.put("boolean",minscore);
		xsdnormalizedString.put("double",minscore);
		xsdnormalizedString.put("float",minscore);
		xsdnormalizedString.put("base64Binary",minscore);
		xsdnormalizedString.put("anyType",maxscore);
		
		//matching scores for QName to ... matching
		xsdQName = new HashMap<String,Integer>();
		xsdQName.put("byte",minscore);
		xsdQName.put("decimal",minscore);
		xsdQName.put("int",minscore);
		xsdQName.put("integer",minscore);
		xsdQName.put("long",minscore);
		xsdQName.put("short",minscore);
		xsdQName.put("language",minscore);
		xsdQName.put("Name",minscore);
		xsdQName.put("normalizedString",minscore);
		xsdQName.put("QName",maxscore);
		xsdQName.put("string",minscore);
		xsdQName.put("token",minscore);
		xsdQName.put("NMTOKEN",minscore);
		xsdQName.put("date",minscore);
		xsdQName.put("dateTime",minscore);
		xsdQName.put("duration",minscore);
		xsdQName.put("time",minscore);
		xsdQName.put("gYear",minscore);
		xsdQName.put("gMonth",minscore);
		xsdQName.put("gDay",minscore);
		xsdQName.put("anyUri",minscore);
		xsdQName.put("boolean",minscore);
		xsdQName.put("double",minscore);
		xsdQName.put("float",minscore);
		xsdQName.put("base64Binary",minscore);
		xsdQName.put("anyType",maxscore);
		
		//matching scores for string to ... matching
		xsdstring = new HashMap<String,Integer>();
		xsdstring.put("byte",minscore);
		xsdstring.put("decimal",minscore);
		xsdstring.put("int",minscore);
		xsdstring.put("integer",minscore);
		xsdstring.put("long",minscore);
		xsdstring.put("short",minscore);
		xsdstring.put("language",minscore);
		xsdstring.put("Name",minscore);
		xsdstring.put("normalizedString",minscore);
		xsdstring.put("QName",minscore);
		xsdstring.put("string",maxscore);
		xsdstring.put("token",minscore);
		xsdstring.put("NMTOKEN",minscore);
		xsdstring.put("date",minscore);
		xsdstring.put("dateTime",minscore);
		xsdstring.put("duration",minscore);
		xsdstring.put("time",minscore);
		xsdstring.put("gYear",minscore);
		xsdstring.put("gMonth",minscore);
		xsdstring.put("gDay",minscore);
		xsdstring.put("anyUri",minscore);
		xsdstring.put("boolean",minscore);
		xsdstring.put("double",minscore);
		xsdstring.put("float",minscore);
		xsdstring.put("base64Binary",minscore);
		xsdstring.put("anyType",maxscore);
		
		//matching scores for token to ... matching
		xsdtoken = new HashMap<String,Integer>();
		xsdtoken.put("byte",minscore);
		xsdtoken.put("decimal",minscore);
		xsdtoken.put("int",minscore);
		xsdtoken.put("integer",minscore);
		xsdtoken.put("long",minscore);
		xsdtoken.put("short",minscore);
		xsdtoken.put("language",minscore);
		xsdtoken.put("Name",minscore);
		xsdtoken.put("normalizedString",maxscore);
		xsdtoken.put("QName",minscore);
		xsdtoken.put("string",maxscore);
		xsdtoken.put("token",maxscore);
		xsdtoken.put("NMTOKEN",minscore);
		xsdtoken.put("date",minscore);
		xsdtoken.put("dateTime",minscore);
		xsdtoken.put("duration",minscore);
		xsdtoken.put("time",minscore);
		xsdtoken.put("gYear",minscore);
		xsdtoken.put("gMonth",minscore);
		xsdtoken.put("gDay",minscore);
		xsdtoken.put("anyUri",minscore);
		xsdtoken.put("boolean",minscore);
		xsdtoken.put("double",minscore);
		xsdtoken.put("float",minscore);
		xsdtoken.put("base64Binary",minscore);
		xsdtoken.put("anyType",maxscore);
		
		//matching scores for NMTOKEN to ... matching
		xsdNMTOKEN = new HashMap<String,Integer>();
		xsdNMTOKEN.put("byte",minscore);
		xsdNMTOKEN.put("decimal",minscore);
		xsdNMTOKEN.put("int",minscore);
		xsdNMTOKEN.put("integer",minscore);
		xsdNMTOKEN.put("long",minscore);
		xsdNMTOKEN.put("short",minscore);
		xsdNMTOKEN.put("language",minscore);
		xsdNMTOKEN.put("Name",minscore);
		xsdNMTOKEN.put("normalizedString",maxscore);
		xsdNMTOKEN.put("QName",minscore);
		xsdNMTOKEN.put("string",maxscore);
		xsdNMTOKEN.put("token",maxscore);
		xsdNMTOKEN.put("NMTOKEN",maxscore);
		xsdNMTOKEN.put("date",minscore);
		xsdNMTOKEN.put("dateTime",minscore);
		xsdNMTOKEN.put("duration",minscore);
		xsdNMTOKEN.put("time",minscore);
		xsdNMTOKEN.put("gYear",minscore);
		xsdNMTOKEN.put("gMonth",minscore);
		xsdNMTOKEN.put("gDay",minscore);
		xsdNMTOKEN.put("anyUri",minscore);
		xsdNMTOKEN.put("boolean",minscore);
		xsdNMTOKEN.put("double",minscore);
		xsdNMTOKEN.put("float",minscore);
		xsdNMTOKEN.put("base64Binary",minscore);
		xsdNMTOKEN.put("anyType",maxscore);
		
		//matching scores for date to ... matching
		xsddate = new HashMap<String,Integer>();
		xsddate.put("byte",minscore);
		xsddate.put("decimal",minscore);
		xsddate.put("int",minscore);
		xsddate.put("integer",minscore);
		xsddate.put("long",minscore);
		xsddate.put("short",minscore);
		xsddate.put("language",minscore);
		xsddate.put("Name",minscore);
		xsddate.put("normalizedString",minscore);
		xsddate.put("QName",minscore);
		xsddate.put("string",minscore);
		xsddate.put("token",minscore);
		xsddate.put("NMTOKEN",minscore);
		xsddate.put("date",maxscore);
		xsddate.put("dateTime",minscore);
		xsddate.put("duration",minscore);
		xsddate.put("time",minscore);
		xsddate.put("gYear",minscore);
		xsddate.put("gMonth",minscore);
		xsddate.put("gDay",minscore);
		xsddate.put("anyUri",minscore);
		xsddate.put("boolean",minscore);
		xsddate.put("double",minscore);
		xsddate.put("float",minscore);
		xsddate.put("base64Binary",minscore);
		xsddate.put("anyType",maxscore);
		
		//matching scores for dateTime to ... matching
		xsddateTime = new HashMap<String,Integer>();
		xsddateTime.put("byte",minscore);
		xsddateTime.put("decimal",minscore);
		xsddateTime.put("int",minscore);
		xsddateTime.put("integer",minscore);
		xsddateTime.put("long",minscore);
		xsddateTime.put("short",minscore);
		xsddateTime.put("language",minscore);
		xsddateTime.put("Name",minscore);
		xsddateTime.put("normalizedString",minscore);
		xsddateTime.put("QName",minscore);
		xsddateTime.put("string",minscore);
		xsddateTime.put("token",minscore);
		xsddateTime.put("NMTOKEN",minscore);
		xsddateTime.put("date",minscore);
		xsddateTime.put("dateTime",maxscore);
		xsddateTime.put("duration",minscore);
		xsddateTime.put("time",minscore);
		xsddateTime.put("gYear",minscore);
		xsddateTime.put("gMonth",minscore);
		xsddateTime.put("gDay",minscore);
		xsddateTime.put("anyUri",minscore);
		xsddateTime.put("boolean",minscore);
		xsddateTime.put("double",minscore);
		xsddateTime.put("float",minscore);
		xsddateTime.put("base64Binary",minscore);
		xsddateTime.put("anyType",maxscore);
		
		//matching scores for duration to ... matching
		xsdduration = new HashMap<String,Integer>();
		xsdduration.put("byte",minscore);
		xsdduration.put("decimal",minscore);
		xsdduration.put("int",minscore);
		xsdduration.put("integer",minscore);
		xsdduration.put("long",minscore);
		xsdduration.put("short",minscore);
		xsdduration.put("language",minscore);
		xsdduration.put("Name",minscore);
		xsdduration.put("normalizedString",minscore);
		xsdduration.put("QName",minscore);
		xsdduration.put("string",minscore);
		xsdduration.put("token",minscore);
		xsdduration.put("NMTOKEN",minscore);
		xsdduration.put("date",minscore);
		xsdduration.put("dateTime",minscore);
		xsdduration.put("duration",maxscore);
		xsdduration.put("time",minscore);
		xsdduration.put("gYear",minscore);
		xsdduration.put("gMonth",minscore);
		xsdduration.put("gDay",minscore);
		xsdduration.put("anyUri",minscore);
		xsdduration.put("boolean",minscore);
		xsdduration.put("double",minscore);
		xsdduration.put("float",minscore);
		xsdduration.put("base64Binary",minscore);
		xsdduration.put("anyType",maxscore);
		
		//matching scores for time to ... matching
		xsdtime = new HashMap<String,Integer>();
		xsdtime.put("byte",minscore);
		xsdtime.put("decimal",minscore);
		xsdtime.put("int",minscore);
		xsdtime.put("integer",minscore);
		xsdtime.put("long",minscore);
		xsdtime.put("short",minscore);
		xsdtime.put("language",minscore);
		xsdtime.put("Name",minscore);
		xsdtime.put("normalizedString",minscore);
		xsdtime.put("QName",minscore);
		xsdtime.put("string",minscore);
		xsdtime.put("token",minscore);
		xsdtime.put("NMTOKEN",minscore);
		xsdtime.put("date",minscore);
		xsdtime.put("dateTime",minscore);
		xsdtime.put("duration",minscore);
		xsdtime.put("time",maxscore);
		xsdtime.put("gYear",minscore);
		xsdtime.put("gMonth",minscore);
		xsdtime.put("gDay",minscore);
		xsdtime.put("anyUri",minscore);
		xsdtime.put("boolean",minscore);
		xsdtime.put("double",minscore);
		xsdtime.put("float",minscore);
		xsdtime.put("base64Binary",minscore);
		xsdtime.put("anyType",maxscore);
		
		//matching scores for gYear to ... matching
		xsdgYear = new HashMap<String,Integer>();
		xsdgYear.put("byte",minscore);
		xsdgYear.put("decimal",minscore);
		xsdgYear.put("int",minscore);
		xsdgYear.put("integer",minscore);
		xsdgYear.put("long",minscore);
		xsdgYear.put("short",minscore);
		xsdgYear.put("language",minscore);
		xsdgYear.put("Name",minscore);
		xsdgYear.put("normalizedString",minscore);
		xsdgYear.put("QName",minscore);
		xsdgYear.put("string",minscore);
		xsdgYear.put("token",minscore);
		xsdgYear.put("NMTOKEN",minscore);
		xsdgYear.put("date",minscore);
		xsdgYear.put("dateTime",minscore);
		xsdgYear.put("duration",minscore);
		xsdgYear.put("time",minscore);
		xsdgYear.put("gYear",maxscore);
		xsdgYear.put("gMonth",minscore);
		xsdgYear.put("gDay",minscore);
		xsdgYear.put("anyUri",minscore);
		xsdgYear.put("boolean",minscore);
		xsdgYear.put("double",minscore);
		xsdgYear.put("float",minscore);
		xsdgYear.put("base64Binary",minscore);
		xsdgYear.put("anyType",maxscore);
		
		//matching scores for gMonth to ... matching
		xsdgMonth = new HashMap<String,Integer>();
		xsdgMonth.put("byte",minscore);
		xsdgMonth.put("decimal",minscore);
		xsdgMonth.put("int",minscore);
		xsdgMonth.put("integer",minscore);
		xsdgMonth.put("long",minscore);
		xsdgMonth.put("short",minscore);
		xsdgMonth.put("language",minscore);
		xsdgMonth.put("Name",minscore);
		xsdgMonth.put("normalizedString",minscore);
		xsdgMonth.put("QName",minscore);
		xsdgMonth.put("string",minscore);
		xsdgMonth.put("token",minscore);
		xsdgMonth.put("NMTOKEN",minscore);
		xsdgMonth.put("date",minscore);
		xsdgMonth.put("dateTime",minscore);
		xsdgMonth.put("duration",minscore);
		xsdgMonth.put("time",minscore);
		xsdgMonth.put("gYear",minscore);
		xsdgMonth.put("gMonth",maxscore);
		xsdgMonth.put("gDay",minscore);
		xsdgMonth.put("anyUri",minscore);
		xsdgMonth.put("boolean",minscore);
		xsdgMonth.put("double",minscore);
		xsdgMonth.put("float",minscore);
		xsdgMonth.put("base64Binary",minscore);
		xsdgMonth.put("anyType",maxscore);
		
		//matching scores for gDay to ... matching
		xsdgDay = new HashMap<String,Integer>();
		xsdgDay.put("byte",minscore);
		xsdgDay.put("decimal",minscore);
		xsdgDay.put("int",minscore);
		xsdgDay.put("integer",minscore);
		xsdgDay.put("long",minscore);
		xsdgDay.put("short",minscore);
		xsdgDay.put("language",minscore);
		xsdgDay.put("Name",minscore);
		xsdgDay.put("normalizedString",minscore);
		xsdgDay.put("QName",minscore);
		xsdgDay.put("string",minscore);
		xsdgDay.put("token",minscore);
		xsdgDay.put("NMTOKEN",minscore);
		xsdgDay.put("date",minscore);
		xsdgDay.put("dateTime",minscore);
		xsdgDay.put("duration",minscore);
		xsdgDay.put("time",minscore);
		xsdgDay.put("gYear",minscore);
		xsdgDay.put("gMonth",minscore);
		xsdgDay.put("gDay",maxscore);
		xsdgDay.put("anyUri",minscore);
		xsdgDay.put("boolean",minscore);
		xsdgDay.put("double",minscore);
		xsdgDay.put("float",minscore);
		xsdgDay.put("base64Binary",minscore);
		xsdgDay.put("anyType",maxscore);
		
		
		//matching scores for anyUri to ... matching
		xsdanyUri = new HashMap<String,Integer>();
		xsdanyUri.put("byte",minscore);
		xsdanyUri.put("decimal",minscore);
		xsdanyUri.put("int",minscore);
		xsdanyUri.put("integer",minscore);
		xsdanyUri.put("long",minscore);
		xsdanyUri.put("short",minscore);
		xsdanyUri.put("language",minscore);
		xsdanyUri.put("Name",minscore);
		xsdanyUri.put("normalizedString",minscore);
		xsdanyUri.put("QName",minscore);
		xsdanyUri.put("string",minscore);
		xsdanyUri.put("token",minscore);
		xsdanyUri.put("NMTOKEN",minscore);
		xsdanyUri.put("date",minscore);
		xsdanyUri.put("dateTime",minscore);
		xsdanyUri.put("duration",minscore);
		xsdanyUri.put("time",minscore);
		xsdanyUri.put("gYear",minscore);
		xsdanyUri.put("gMonth",minscore);
		xsdanyUri.put("gDay",minscore);
		xsdanyUri.put("anyUri",maxscore);
		xsdanyUri.put("boolean",minscore);
		xsdanyUri.put("double",minscore);
		xsdanyUri.put("float",minscore);
		xsdanyUri.put("base64Binary",minscore);
		xsdanyUri.put("anyType",maxscore);
		
		//matching scores for boolean to ... matching
		xsdboolean = new HashMap<String,Integer>();
		xsdboolean.put("byte",minscore);
		xsdboolean.put("decimal",minscore);
		xsdboolean.put("int",minscore);
		xsdboolean.put("integer",minscore);
		xsdboolean.put("long",minscore);
		xsdboolean.put("short",minscore);
		xsdboolean.put("language",minscore);
		xsdboolean.put("Name",minscore);
		xsdboolean.put("normalizedString",minscore);
		xsdboolean.put("QName",minscore);
		xsdboolean.put("string",minscore);
		xsdboolean.put("token",minscore);
		xsdboolean.put("NMTOKEN",minscore);
		xsdboolean.put("date",minscore);
		xsdboolean.put("dateTime",minscore);
		xsdboolean.put("duration",minscore);
		xsdboolean.put("time",minscore);
		xsdboolean.put("gYear",minscore);
		xsdboolean.put("gMonth",minscore);
		xsdboolean.put("gDay",minscore);
		xsdboolean.put("anyUri",minscore);
		xsdboolean.put("boolean",maxscore);
		xsdboolean.put("double",minscore);
		xsdboolean.put("float",minscore);
		xsdboolean.put("base64Binary",minscore);
		xsdboolean.put("anyType",maxscore);
		
		//matching scores for double to ... matching
		xsddouble = new HashMap<String,Integer>();
		xsddouble.put("byte",minscore);
		xsddouble.put("decimal",minscore);
		xsddouble.put("int",minscore);
		xsddouble.put("integer",minscore);
		xsddouble.put("long",minscore);
		xsddouble.put("short",minscore);
		xsddouble.put("language",minscore);
		xsddouble.put("Name",minscore);
		xsddouble.put("normalizedString",minscore);
		xsddouble.put("QName",minscore);
		xsddouble.put("string",minscore);
		xsddouble.put("token",minscore);
		xsddouble.put("NMTOKEN",minscore);
		xsddouble.put("date",minscore);
		xsddouble.put("dateTime",minscore);
		xsddouble.put("duration",minscore);
		xsddouble.put("time",minscore);
		xsddouble.put("gYear",minscore);
		xsddouble.put("gMonth",minscore);
		xsddouble.put("gDay",minscore);
		xsddouble.put("anyUri",minscore);
		xsddouble.put("boolean",minscore);
		xsddouble.put("double",maxscore);
		xsddouble.put("float",minscore);
		xsddouble.put("base64Binary",minscore);
		xsddouble.put("anyType",maxscore);
		
		//matching scores for float to ... matching
		xsdfloat = new HashMap<String,Integer>();
		xsdfloat.put("byte",minscore);
		xsdfloat.put("decimal",minscore);
		xsdfloat.put("int",minscore);
		xsdfloat.put("integer",minscore);
		xsdfloat.put("long",minscore);
		xsdfloat.put("short",minscore);
		xsdfloat.put("language",minscore);
		xsdfloat.put("Name",minscore);
		xsdfloat.put("normalizedString",minscore);
		xsdfloat.put("QName",minscore);
		xsdfloat.put("string",minscore);
		xsdfloat.put("token",minscore);
		xsdfloat.put("NMTOKEN",minscore);
		xsdfloat.put("date",minscore);
		xsdfloat.put("dateTime",minscore);
		xsdfloat.put("duration",minscore);
		xsdfloat.put("time",minscore);
		xsdfloat.put("gYear",minscore);
		xsdfloat.put("gMonth",minscore);
		xsdfloat.put("gDay",minscore);
		xsdfloat.put("anyUri",minscore);
		xsdfloat.put("boolean",minscore);
		xsdfloat.put("double",minscore);
		xsdfloat.put("float",maxscore);
		xsdfloat.put("base64Binary",minscore);
		xsdfloat.put("anyType",maxscore);
		
		//matching scores for base64Binary to ... matching
		xsdbase64Binary = new HashMap<String,Integer>();
		xsdbase64Binary.put("byte",minscore);
		xsdbase64Binary.put("decimal",minscore);
		xsdbase64Binary.put("int",minscore);
		xsdbase64Binary.put("integer",minscore);
		xsdbase64Binary.put("long",minscore);
		xsdbase64Binary.put("short",minscore);
		xsdbase64Binary.put("language",minscore);
		xsdbase64Binary.put("Name",minscore);
		xsdbase64Binary.put("normalizedString",minscore);
		xsdbase64Binary.put("QName",minscore);
		xsdbase64Binary.put("string",minscore);
		xsdbase64Binary.put("token",minscore);
		xsdbase64Binary.put("NMTOKEN",minscore);
		xsdbase64Binary.put("date",minscore);
		xsdbase64Binary.put("dateTime",minscore);
		xsdbase64Binary.put("duration",minscore);
		xsdbase64Binary.put("time",minscore);
		xsdbase64Binary.put("gYear",minscore);
		xsdbase64Binary.put("gMonth",minscore);
		xsdbase64Binary.put("gDay",minscore);
		xsdbase64Binary.put("anyUri",minscore);
		xsdbase64Binary.put("boolean",minscore);
		xsdbase64Binary.put("double",minscore);
		xsdbase64Binary.put("float",minscore);
		xsdbase64Binary.put("base64Binary",maxscore);
		xsdbase64Binary.put("anyType",maxscore);
		
		//matching scores for anyType to ... matching
		anyType = new HashMap<String,Integer>();
		anyType.put("byte",minscore);
		anyType.put("decimal",minscore);
		anyType.put("int",minscore);
		anyType.put("integer",minscore);
		anyType.put("long",minscore);
		anyType.put("short",minscore);
		anyType.put("language",minscore);
		anyType.put("Name",minscore);
		anyType.put("normalizedString",minscore);
		anyType.put("QName",minscore);
		anyType.put("string",minscore);
		anyType.put("token",minscore);
		anyType.put("NMTOKEN",minscore);
		anyType.put("date",minscore);
		anyType.put("dateTime",minscore);
		anyType.put("duration",minscore);
		anyType.put("time",minscore);
		anyType.put("gYear",minscore);
		anyType.put("gMonth",minscore);
		anyType.put("gDay",minscore);
		anyType.put("anyUri",minscore);
		anyType.put("boolean",minscore);
		anyType.put("double",minscore);
		anyType.put("float",minscore);
		anyType.put("base64Binary",minscore);
		anyType.put("anyType",maxscore);
		
		//fill xsdtypes not complete!
		xsdTypes = new HashMap<String,HashMap<String,Integer>>();
		//numeric types
		xsdTypes.put("byte",xsdbyte);
		xsdTypes.put("decimal",xsddecimal);
		xsdTypes.put("int",xsdint);
		xsdTypes.put("integer",xsdinteger);
		xsdTypes.put("long",xsdlong);
		xsdTypes.put("short",xsdshort);
		//string types
		xsdTypes.put("language",xsdlanguage);
		xsdTypes.put("Name",xsdName);
		xsdTypes.put("normalizedString",xsdnormalizedString);
		xsdTypes.put("QName",xsdQName);
		xsdTypes.put("string",xsdstring);
		xsdTypes.put("token",xsdtoken);
		xsdTypes.put("NMTOKEN",xsdNMTOKEN);
		//date types
		xsdTypes.put("date",xsddate);
		xsdTypes.put("dateTime",xsddateTime);
		xsdTypes.put("duration",xsdduration);
		xsdTypes.put("time",xsdtime);
		xsdTypes.put("gYear",xsdgYear);
		xsdTypes.put("gMonth",xsdgMonth);
		xsdTypes.put("gDay",xsdgDay);
		//miscellaneous types
		xsdTypes.put("anyUri",xsdanyUri);
		xsdTypes.put("boolean",xsdboolean);
		xsdTypes.put("double",xsddouble);
		xsdTypes.put("float",xsdfloat);
		xsdTypes.put("base64Binary",xsdbase64Binary);
		xsdTypes.put("anyType",anyType);
		
		//create hashtable for selfdefined(restricted) simple types
		selfdefinedtypes = new HashMap<String,String>();
	}
	
	public HashMap getXsdTypes()
	{
		return xsdTypes;
	}
	
	public boolean lookupSimpleType(String s)
	{
		return xsdTypes.containsKey(s);
	}
	
	public String getBuiltInType(String type)
	{
		if(lookupSimpleType(type))
		{
			if(xsdTypes.get(type) == null)
			{
				return selfdefinedtypes.get(type);
			}
			else return type;
		}
		else return null;
	}
	
	public int getMatchingScore(String a,String b)
	{
		String firsttype,secondtype;
		firsttype = a;
		secondtype = b;
		if(xsdTypes.get(a).isEmpty())
		{
			firsttype = selfdefinedtypes.get(a);
		}
		if(xsdTypes.get(b).isEmpty())
		{
			secondtype = selfdefinedtypes.get(b);
		}
		return getScore(firsttype,secondtype);
	}
	
	public int getScore(String ta,String tb)
	{
		HashMap<String,Integer> tmp = xsdTypes.get(ta);
		Integer score = tmp.get(tb);
		return score.intValue();
		
	}
	
	/**
	 * inserts selfdefined type name with predefined type type
	 * in hashtable selfdefined types
	 * and puts name in hashmap xsdtypes
	 * @deprecated
	 */
	public void insertSimpleType(String name,String type)
	{
		HashMap<String,Integer> hlp = new HashMap<String,Integer>();
		xsdTypes.put(name,hlp);
		selfdefinedtypes.put(name,type);
	}
	
	/**
	 * @deprecated
	 * @param simpletype
	 * @param complextype
	 * @param score
	 */
	public void insertComplexType(String simpletype,String complextype,int score)
	{
		//HashMap<String,Integer> hlp = xsdTypes.get(simpletype);
		xsdTypes.get(simpletype).put(complextype,new Integer(score));
	}

}

