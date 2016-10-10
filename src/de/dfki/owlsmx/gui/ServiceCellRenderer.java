package de.dfki.owlsmx.gui;

import javax.swing.JLabel;
import javax.swing.ListCellRenderer;

import de.dfki.owlsmx.gui.data.ServiceItem;
import de.dfki.owlsmx.gui.util.GUIState;

/**
 * This cell renderer is used to add an icon to a service list, that
 * specifies, if the service has a WSDL grounding or not. It is used
 * in the Services GUI as well as the Requests GUI.
 * 
 * @author Patrick Kapahnke
 *
 */
public class ServiceCellRenderer extends JLabel implements ListCellRenderer {

	private final javax.swing.ImageIcon wsdlIcon = new javax.swing.ImageIcon(getClass().getResource("/images/wsdl.gif"));
	private final javax.swing.ImageIcon noGroundingIcon = new javax.swing.ImageIcon(getClass().getResource("/images/nogrounding.gif"));
	
	public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		ServiceItem service = (ServiceItem) value;
		setText(service.toString());
		if(service.hasWSDLGrounding()) setIcon(wsdlIcon);
		else setIcon(noGroundingIcon);
		
		if(isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setOpaque(true);
		
		return this;
	}
}
