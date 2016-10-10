package de.dfki.owlsmx.data;


import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Set;

import org.mindswap.owl.OWLClass;
import org.mindswap.owl.OWLKnowledgeBase;


import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Resource;

import de.dfki.owlsmx.io.ErrorLog;

/**
 * A container that handles the local ontology
 * (creating, saving, loading etc.)
 * 
 * @author bEn
 *
 */
public class LocalOntologyContainer {
	static private org.mindswap.owl.OWLKnowledgeBase base = org.mindswap.owl.OWLFactory.createKB();
	static private org.mindswap.owl.OWLOntology localOntology=base.createOntology();
	static final String localOntologyName = "localOntology.owl"; 
	private final static boolean debug = false;
	private boolean ontologyHasChanged = false;
	
	/**
	 * Constructor
	 * Attaches Pellet to the localOntology
	 */
	public LocalOntologyContainer() {
		localOntology.setReasoner("Pellet");
	}
	
	/**
	 * Just a debugging function that prints text to system.out required
	 * 
	 * @param string
	 */
	private  void debugDisplay(String string) {
		if (debug)
			de.dfki.owlsmx.io.ErrorLog.debug(string);
	}
	
    /**
     * Stores the local ontology to a file (localOntology.owl)
     * 
     * @return if saving was successful
     */
    public boolean save() {
		try {
			localOntology.write(new java.io.FileOutputStream(new java.io.File(localOntologyName)));
			debugDisplay("Stored localOntology.");
	    	return true;
		} catch (FileNotFoundException e) {
			ErrorLog.instanceOf().report(this.getClass().toString() + "|save(): Could not save local ontology " + e.getMessage());
			e.printStackTrace();
		}
		return false;
    }
    
    /**
     * Loads the local matchmaker ontology from a file (localOntology.owl)
     * 
     * @return if loading was successful
     */
    public static boolean load() {
    	try {
    		File file = new File(localOntologyName);
    		if (file.isFile())
    			localOntology = base.read(file.toURI());
    		return true;
		} catch (FileNotFoundException e) {
			ErrorLog.instanceOf().report("LocalOntologyContainer" + "|load(): Couldn load local ontology " + e.getMessage());
			e.printStackTrace();
		}
		return false;
    }
	    
    /**
     * If a restriction is already in the local ontoogy
     * 
     * @param r restriction which is checked
     * @return if the local ontology contains r
     */
    private boolean contains(Restriction r) {
    	if (r.getURI()==null) {
    		return ((OntModel)localOntology.getImplementation()).containsResource(r);
    	}    	
    	return (((OntModel)localOntology.getImplementation()).getRestriction(r.getURI())!=null);
    }
    
    /**
     * Creats a OntProperty in the local ontology with the URI of a given
     * OntProperty
     * 
     * @param prop	OntProperty which should be copied to the local Ontology
     * @return OntProperty in the local Ontology
     */
    private OntProperty createProperty(OntProperty prop) {
//    	OntModel local = (OntModel)localOntology.getImplementation();
//    	try {
//			if (localOntology.getProperty(new URI(prop.getURI()))!=null)
//				return local.getOntProperty(prop.getURI());
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
//    	return local.createOntProperty(prop.getURI());
    	return createProperty(prop.getURI());
    }
    
    /**
     * Creates a property with the given URI (String)
     * 
     * @param uri	URI as String of the OntProperty which should be created
     * @return
     */
    private OntProperty createProperty(String  uri) {
    	OntModel local = (OntModel)localOntology.getImplementation();
    	try {
			if (localOntology.getProperty(new URI(uri))!=null)
				return local.getOntProperty(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    	return local.createOntProperty(uri);
    }
    
    
    private OntProperty createOnProperty(Restriction r){
    	try{
    		return r.getOnProperty();
    	}
    	catch(Exception e) {
    		//e.printStackTrace();
    		de.dfki.owlsmx.io.ErrorLog.instanceOf().report(this.getClass().toString() + "|createOnProperty: Couldn't create onProperty for restriction " + r.toString());
    		de.dfki.owlsmx.io.ErrorLog.instanceOf().report("                    returned null instead" );
    		return null;
    	}
    }
    
    private Resource addResourceIfNecessary(Resource r, OWLKnowledgeBase kbase) {
    	try {
//    		OntModel local = (OntModel)localOntology.getImplementation();
//	    	OntClass tmp = (OntClass) r.as(OntClass.class);
//	    	if (local.containsResource(tmp))
//	    		return tmp;
//	    	else if (kbase.getClass(new URI(r.getURI()))!=null)
//    		System.err.println("Resource:" + r);
//    		if (r.toString().contains("http://www.w3.org/2001/XMLSchema"))
//    			return r;
    		URI uri = new URI(r.toString());
    		if (localOntology.getClass(uri)!=null)
    			return (Resource)localOntology.getClass(uri).getImplementation();
    		else if (kbase.getClass(uri)!=null)
    			return (Resource)((OWLClass)createClass( kbase, kbase.getClass(uri))).getImplementation();
    		else
    			return r;
    	}
    	catch(Exception e) {
			e.printStackTrace();
    	}
    	
    	try {
			return (Resource)createClass( kbase,localOntology.createClass(new URI(r.getURI()))).getImplementation(); 
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return r;
		}
    }
    
    private Resource createRestriction(OWLKnowledgeBase base, Restriction r) {
    	if (r==null)
    		return null;
    	OntModel local = (OntModel)localOntology.getImplementation();
    	if (contains(r))
    		return r;
    	Restriction restrict = null;
    	OntProperty ont = null;
    	if (r.isAllDifferent()) {
    		debugDisplay("  asAllDifferent              ");// + r.asAllDifferent() );    		
    		return local.createAllDifferent(r.asAllDifferent().getDistinctMembers());
    		}
    	else if (r.isAllValuesFromRestriction()) {
    		debugDisplay("  isAllValuesFromRestriction  ");// +  );
    		ont = createOnProperty(r);
    		if (ont==null)
    			return null;
    		return local.createAllValuesFromRestriction(r.getURI(),createProperty(ont),addResourceIfNecessary(r.asAllValuesFromRestriction().getAllValuesFrom(),base));
    	}
    	else if (r.isDataRange()) {
    		debugDisplay("  asDataRange  				");// + r.asDataRange().toString() );
//    		local.createDataRange(r.asDataRange().getOneOf());
    	}	
    	else if (r.isHasValueRestriction()) {
    		debugDisplay("  asHasValueRestriction		");// + r.asHasValueRestriction().toString() );
    		ont = createOnProperty(r);
    		if (ont==null)
    			return null;
    		return local.createHasValueRestriction(r.getURI(),createProperty(ont),r.asHasValueRestriction().getHasValue());
    	}
    	else if (r.isMaxCardinalityRestriction()) {
    		debugDisplay("  asMaxCardinalityRestriction	");// + r.asMaxCardinalityRestriction().toString() );
    		ont = createOnProperty(r);
    		if (ont==null)
    			return null;
    		return local.createMaxCardinalityRestriction(r.getURI(),createProperty(ont),r.asMaxCardinalityRestriction().getMaxCardinality());
    	}	
    	else if (r.isMinCardinalityRestriction()) {
    		debugDisplay("  asMinCardinalityRestriction	");// + r.asMinCardinalityRestriction().toString() );
    		//System.out.println("error " + r.toString());
    		ont = createOnProperty(r);
    		if (ont==null)
    			return null;
    		return local.createMinCardinalityRestriction(r.getURI(),createProperty(ont),r.asMinCardinalityRestriction().getMinCardinality());
    	}	
    	else if (r.isCardinalityRestriction()) {
    		debugDisplay("  asCardinalityRestriction    ");// + r.asCardinalityRestriction() );
    		ont = createOnProperty(r);
    		if (ont==null)
    			return null;
    		return local.createCardinalityRestriction(r.getURI(),createProperty(ont),r.asCardinalityRestriction().getCardinality());
    	}
    	else if (r.isSomeValuesFromRestriction()) {
    		debugDisplay("  asSomeValuesFromRestriction	");// + r.asSomeValuesFromRestriction().toString() );
    		ont = createOnProperty(r);
    		if (ont==null)
    			return null;
    		return local.createSomeValuesFromRestriction(r.getURI(),createProperty(ont),addResourceIfNecessary(r.asSomeValuesFromRestriction().getSomeValuesFrom(),base));
    	}		
    	else if (r.isDatatypeProperty()) {    		
    		debugDisplay("  asDatatypeProperty			");// + r.asDatatypeProperty().toString() );
    		return local.createDatatypeProperty(r.getURI());
    	}
    	else if (r.isObjectProperty()) {
			debugDisplay("  asObjectProperty			");// + r.asObjectProperty().toString() );
			return local.createObjectProperty(r.getURI());
		}	
    	else if (r.isAnnotationProperty()) {
			debugDisplay("  isAnnotationProperty        ");// + r.asAnnotationProperty() );
			local.createAnnotationProperty(r.getURI());
		}
    	else {
    		debugDisplay("  unknown						");// + r.toString() );
    		
    	}		
    	return restrict;	
    }
    
//    private void addSuperClass(OWLClass subclass, OWLClass superclass) {
//		Resource superClass = (Resource)superclass.getImplementation();		
//		OntClass subClass = (OntClass) ((Resource)subclass.getImplementation()).as(OntClass.class);
//		subClass.setSuperClass(superClass);
//    }
    
    private void addSuperClass(OWLClass subclass, Resource superClass) {
		OntClass subClass = (OntClass) ((Resource)subclass.getImplementation()).as(OntClass.class);
		if (superClass.getURI()!=null) {
//			subClass.setSuperClass(superClass);
			subClass.addSuperClass(superClass);
			debugDisplay("Superclass of " + subclass.toPrettyString() + " is " + superClass.getURI());
		}
    }
    
    private OWLClass addClass(OWLClass clazz) {
		return localOntology.createClass(clazz.getURI());
    }
    
    private boolean contains(OWLClass clazz) {
    	if (clazz==null)
    		return true;
    	//debugDisplay("Contains class " + clazz.toPrettyString() + " : "  + (localOntology.getClass(clazz.getURI())!=null));
    	OWLClass clazz1=localOntology.getClass(clazz.getURI());
    	if (clazz1!=null)
    	   	return (clazz1.isClass());
    	else return false;
    }
    
    private OWLClass createClass(OWLKnowledgeBase kbase, OWLClass clazz) {
    	if ( (clazz!=null) && (clazz.getURI()!=null) && contains(clazz))
    		return localOntology.getClass(clazz.getURI());
    	OntModel local = (OntModel)localOntology.getImplementation();
    	ontologyHasChanged=true;
    	OWLClass currentClazz = addClass(clazz);
    	Set set =  kbase.getSuperClasses(clazz, true);//localOntology.getSuperClasses(base.getClass(new URI("http://127.0.0.1/ontology/univ-bench.owl#Chair")),true);
    	set.remove(clazz);
    	debugDisplay("Superclasses of " + clazz.getURI().toString());
    	debugDisplay(set.toString());
    	OntClass c_ont;
    	OWLClass c_owl;
    	Resource res = null;
    	for(Iterator iter= set.iterator(); iter.hasNext();) {
    		c_owl = (OWLClass)iter.next();
    		c_ont = (OntClass) ( (Resource)c_owl.getImplementation()).as(OntClass.class);  	
	    	if(c_ont.toString().indexOf("http://www.w3.org/2000/01/rdf-schema")>=0)
				debugDisplay(" - Skipped - " + c_ont);
	    	else if (c_ont.isRestriction()) {
	    		res = createRestriction(base,c_ont.asRestriction());
			}		
	    	else if (c_ont.isIndividual()) {
	    		debugDisplay("  asIndividual	  			");// + r.asIndividual().toString() );
	    		res = local.createIndividual(c_ont);
	    	}	
			else if (c_ont.isIntersectionClass()) {
				debugDisplay("  asIntersectionClass " + c_ont.asIntersectionClass().toString());
				res = local.createIntersectionClass(c_ont.getURI(),c_ont.asIntersectionClass().getOperands());
			}
			else if(c_ont.isComplementClass() && (!c_ont.toString().contains("http://www.w3.org/2002/07/owl")) ) {
				debugDisplay("  asComplementClass   " +c_ont.asComplementClass().toString());
				res = local.createComplementClass(c_ont.getURI(), c_ont.asComplementClass().getOperands());
			}
			else if(c_ont.isEnumeratedClass() && (!c_ont.toString().contains("http://www.w3.org/2002/07/owl")) ) {
				debugDisplay("  asEnumeratedClass   " + c_ont.asEnumeratedClass().toString());
				res = local.createEnumeratedClass(c_ont.getURI(),c_ont.asEnumeratedClass().getOneOf());
			}
			else if(c_ont.isUnionClass() && (!c_ont.toString().contains("http://www.w3.org/2002/07/owl")) ) {
				debugDisplay("  asUnionClass        " + c_ont.asUnionClass().toString());
				res = local.createUnionClass(c_ont.getURI(),c_ont.asUnionClass().getOperands());
			}
			else {
				debugDisplay("  OntClass            " + c_ont.toString());
				res = (Resource) createClass(kbase,c_owl).getImplementation();
			}
	    	if (res!=null)
	    		addSuperClass(currentClazz,res);
	    	else 
	    		debugDisplay("Problems with " +  currentClazz.toPrettyString() + " couldn add superclass " + c_owl.toPrettyString());
    	}
		return currentClazz;
    }
        
    boolean processClass(OWLKnowledgeBase kbase, URI clazzURI) {
    	OWLClass clazz =kbase.getClass(clazzURI);
    	if (clazz==null) {
    		de.dfki.owlsmx.io.ErrorLog.instanceOf().report("Couldn't find the clazz " + clazzURI + " in base " + kbase);
    		return false;
    	}
    	debugDisplay("Processing            " + clazz.getURI().toString());
//    	kbase.setReasoner("RDFS");
    	createClass(kbase, clazz);
//    	kbase.setReasoner(null);
    	
//    	debugDisplay("Process class " + clazzURI);
//    	ontologyHasChanged = false;
//    	OWLClass clazz =kbase.getClass(clazzURI);
//    	debugDisplay("Processing            " + clazz.getURI().toString());
//
//    	createClass(kbase, clazz);
    	//save();
    	return ontologyHasChanged;
    }
    
    public boolean processClasses(OWLKnowledgeBase kbase, Set classURIs) {
//    	debugDisplay("Classify");
    	kbase.setReasoner("RDFS");
    	boolean hasChanged = false;
    	ontologyHasChanged = false;
    	for(Iterator iter = classURIs.iterator();iter.hasNext();) {
    		if (processClass(kbase, (URI)iter.next()))
    			hasChanged = true;
    	}

    	kbase.setReasoner(null);
    	ontologyHasChanged = hasChanged;
    	return hasChanged;
    }
    
    public boolean hasOntologyChanged() {
    	return ontologyHasChanged;
    }
	
    public org.mindswap.owl.OWLOntology getOntology() {
    	return localOntology;
    }
    
    public OWLClass getClass(String uri) throws URISyntaxException{
			return getClass(new URI(uri));
    }
    
    public  OWLClass getClass(URI uri) {
    	return localOntology.getClass(uri);
    }
    
    public OntClass getOntClass(String uri) {
    	return ((OntModel)localOntology.getImplementation()).getOntClass(uri);
    }
    
    public OntClass getOntClass(URI uri) {
    	return getOntClass(uri.toString());
    }
}
