package de.dfki.owlsmx.mxp;

import java.util.Vector;
import java.io.Serializable;

/**
 * The OWLSMXPCompatibilityMatrix is used as the internal data
 * structure of OWLS-MXP. It implements the Serializable interface
 * to allow OWLSMXP to exchange it's precomputed data between agents.
 * 
 * @author Patrick Kapahnke
 */
public class OWLSMXPCompatibilityMatrix implements Serializable {
	
	private static final long serialVersionUID = 489323039384L;
	
	private Vector rowNames	= new Vector();
	private Vector colNames = new Vector();
	private Vector matrix	= new Vector();
	
	public OWLSMXPCompatibilityMatrix() {}
	
	/**
	 * Adds a new named row to the matrix 
	 * 
	 * @param rowName the name of the row
	 */
	public void addRow(String rowName) {
		rowNames.add(rowName);
		matrix.add(new Vector());
		for(int i = 0; i < colNames.size(); i++) {
			((Vector) (matrix.lastElement())).add(null);
		}
	}
	
	/**
	 * Adds a new named column to the matrix
	 * 
	 * @param colName the name of the column
	 */
	public void addColumn(String colName) {
		colNames.add(colName);
		for(int i = 0; i < rowNames.size(); i++) {
			((Vector) matrix.elementAt(i)).add(null);
		}
	}
	
	/**
	 * Sets the value for a cell of the matrix specified by
	 * its row and column number.
	 * 
	 * @param row the number of the row, starting with 0
	 * @param col the number of the column, starting with 0
	 * @param value
	 */
	public void setEntry(int row, int col, boolean value) {
		if(row >= rowNames.size()) return;
		if(col >= colNames.size()) return;
		((Vector) matrix.elementAt(row)).set(col, new Boolean(value));
	}
	
	/**
	 * Returns the current value of the cell of the matrix specified
	 * by its row and column number.
	 * 
	 * @param row the number of the row, starting with 0
	 * @param col the number of the column, starting with 0
	 * @return a Boolean containing the value of the cell or null, if no value has been set before
	 */
	public Boolean getEntry(int row, int col) {
		if(row >= rowNames.size()) return null;
		if(col >= colNames.size()) return null;
		
		return (Boolean) (((Vector) matrix.elementAt(row)).elementAt(col));
	}
	
	/**
	 * Returns the current value of the cell of the matrix specified
	 * by its row and column name.
	 * 
	 * @param rowName the name of the row
	 * @param columnName the name of the column
	 * @return a Boolean containing the value of the cell or null, if no value has been set before or one of the names does not exist in the matrix
	 */
	public Boolean getEntry(String rowName, String columnName) {
		int row = rowNames.indexOf(rowName);
		int col = colNames.indexOf(columnName);
		
		return getEntry(row, col);
	}

	/**
	 * Returns the number of rows.
	 * 
	 * @return number of rows
	 */
	public int getNumberOfRows() {
		return rowNames.size();
	}
	
	/**
	 * Returns the number of columns.
	 * 
	 * @return number of columns
	 */
	public int getNumberOfColumns() {
		return colNames.size();
	}
	
	/**
	 * Returns the name of the row specified by
	 * its row number.
	 * 
	 * @param row the number of the row, starting with 0
	 * @return the name of the row as string
	 */
	public String getRowName(int row) {
		if(row >= rowNames.size()) return null;
		return (String) (rowNames.elementAt(row));
	}
	
	/**
	 * Returns the name of the column specified by
	 * its column number.
	 * 
	 * @param col the number of the column, starting with 0
	 * @return the name of the column as string
	 */
	public String getColumnName(int col) {
		if(col >= colNames.size()) return null;
		return (String) (colNames.elementAt(col));
	}
	
	/**
	 * Returns a string representation of the compatibility matrix.
	 * 
	 * @return string representation
	 */
	public String toString() {
    	String result = "\ncompatibility matrix:\n" +
    		"--------------------\n" +
    		"rows: ";
    	for(int i = 0; i < rowNames.size(); i++) result += rowNames.elementAt(i) + " ";
    	result += "\ncols: ";
    	for(int i = 0; i < colNames.size(); i++) result += colNames.elementAt(i) + " ";
    	result += "\n\n";
    	for(int i = 0; i < rowNames.size(); i++) {
    		for(int j = 0; j < colNames.size(); j++) {
    			Boolean value = getEntry(i, j);
    			if(value == null) result += "n/a ";
    			else if(value.booleanValue()) result += "yes ";
    			else result += " no ";
    		}
    		result += "\n";
    	}    	
    	result += "\n";

    	return result;
	}
}
