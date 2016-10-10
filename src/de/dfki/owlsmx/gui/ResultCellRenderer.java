package de.dfki.owlsmx.gui;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import de.dfki.owlsmx.gui.data.HybridServiceItem;

/**
 * This cell renderer is used to mark services as execution compatible
 * at WSDL grounding level or not. It adds a custom icon for incompatible services
 * as well as a color indication the incompatibility. It is used in the AnswerSet GUI.
 * 
 * @author Patrick Kapahnke
 *
 */
public class ResultCellRenderer extends DefaultTreeCellRenderer {
	
	private final javax.swing.ImageIcon incompatibleIcon = new javax.swing.ImageIcon(getClass().getResource("/images/incompatible.gif"));
	private static final java.awt.Color incompatibleColor = new java.awt.Color(1.0f, 0.7f, 0.7f);
	private static final java.awt.Color compatibleColor = new java.awt.Color(0.8f, 1.0f, 0.8f);
	
	public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		setBackgroundNonSelectionColor(tree.getBackground());
		
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;

		if(leaf) {
			try {
				HybridServiceItem element = (HybridServiceItem) treeNode.getUserObject();
			
				if(!element.isDataTypeCompatible()) {
					setIcon(incompatibleIcon);
					setBackgroundNonSelectionColor(incompatibleColor);
				}
				else {
					setBackgroundNonSelectionColor(compatibleColor);
				}
			}
			catch(Exception e) {
			}
		}
				
		return this;
	}
}
