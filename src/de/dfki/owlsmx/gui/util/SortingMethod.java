package de.dfki.owlsmx.gui.util;

public class SortingMethod {
	
	private int sort;
	
	public SortingMethod(){
		
	}
	
	public SortingMethod(int sort) {
		if ( (sort == GUIState.SORT_HYBRID) ||
			 (sort == GUIState.SORT_SEMANTIC) ||
			 (sort == GUIState.SORT_SYNTACTIC) )
			this.sort = sort;
		else
			this.sort = GUIState.SORT_HYBRID;
	}
	
	public String toString() {
		switch(sort){
		case GUIState.SORT_HYBRID:
			return "Hybrid sorting";
		case GUIState.SORT_SEMANTIC:
			return "Semantic sorting";
		case GUIState.SORT_SYNTACTIC:
			return "Syntactic sorting";
	    default:
	    	return "Unsorted";
		}
	}
	
	public int getSortingMethod() {
		return sort;
	}

}
