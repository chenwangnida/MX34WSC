package de.dfki.owlsmx.mxp;

/**
 * This class is for storing all possible mappings between
 * parameters. When all possible mappings have been specified,
 * a method can be called to test, if a mapping between every
 * source parameter to a target parameter exists.
 * 
 * @author Patrick Kapahnke
 *
 */
public class OWLSMXPMapping {

	private boolean[][] matrix;
	private int numberOfSources;
	private int numberOfTargets;
	
	/**
	 * Constructor initializes a new mapping.
	 * 
	 * @param numberOfSources the number of source parameters
	 * @param numberOfTargets the number of target parameters
	 */
	OWLSMXPMapping(int numberOfSources, int numberOfTargets) {
		this.numberOfSources = numberOfSources;
		this.numberOfTargets = numberOfTargets;
		matrix = new boolean[numberOfTargets][numberOfSources];
		for(int i = 0; i < numberOfTargets; i++) {
			for(int j = 0; j < numberOfSources; j++) {
				matrix[i][j] = false;
			}
		}
	}
	
	/**
	 * Adds a possible mapping to the internal data structure.
	 * 
	 * @param source the ID of the source parameter
	 * @param target the ID of the target parameter
	 */
	void addPossibleMappingElement(int source, int target) {
		matrix[target][source] = true;
	}
	
	/**
	 * Testts, if a mapping from every source parameter to
	 * a target parameter exits. target parameters must be
	 * mapped to only once.
	 * 
	 * @return true, if a mapping exists
	 */
	boolean existsMapping() {
		if(numberOfSources == 0) return true;
		if(numberOfTargets == 0) return false;
		
//		printMatrix();
		
		for(int i = 0; i < numberOfTargets; i++) {
			if(!matrix[i][0]) continue;
			OWLSMXPMapping subMapping = new OWLSMXPMapping(numberOfSources - 1, numberOfTargets - 1);
			for(int l = 1; l < numberOfSources; l++) {
				for(int k = 0; k < numberOfTargets; k++) {
					if(k == i) continue;
					int source = l - 1;
					int target;
					if(k < i) target = k;
					else target = k - 1;
					if(matrix[k][l]) subMapping.addPossibleMappingElement(source, target);
				}
			}
			if(subMapping.existsMapping()) return true;
		}
		
		return false;		
	}
	
	/**
	 * Prints the internal matrix for debugging purpose to
	 * System.out. 
	 */
	void printMatrix() {
		for(int i = 0; i < numberOfSources; i++) {
			for(int j = 0; j < numberOfTargets; j++) {
				if(matrix[j][i]) System.out.print("1");
				else System.out.print("0");
			}
			System.out.println();
		}
		System.out.println();
	}
}
