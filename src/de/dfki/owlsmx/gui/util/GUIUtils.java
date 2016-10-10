/*
 * GUIUtils.java
 *
 * Created on 11. August 2005, 23:25
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package de.dfki.owlsmx.gui.util;
import java.awt.Color;
import javax.swing.UIManager;

/**
 *
 * @author B
 */
public class GUIUtils {
    
    public static void makeTransparent(javax.swing.JComponent area){
        area.setBackground((Color)UIManager.get("Label.background"));
        area.setForeground((Color)UIManager.get("Label.foreground"));
        //area.setFont((Font)UIManager.get("Label.font")); 
    }  
        public static void makeTransparent(javax.swing.JTextArea area){
        area.setBackground((Color)UIManager.get("Label.background"));
        area.setForeground((Color)UIManager.get("Label.foreground"));
        //area.setFont((Font)UIManager.get("Label.font")); 
    }    
    
}
