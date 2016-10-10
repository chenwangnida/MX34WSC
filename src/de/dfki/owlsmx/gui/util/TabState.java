/*
 * TabState.java
 *
 * Created on 8. August 2005, 20:16
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package de.dfki.owlsmx.gui.util;

/**
 *
 * @author B
 */
public abstract class TabState{
        public static final int TASK = 0;
        public static final int SERVICES = 1;
        public static final int QUERIES = 2;
        public static final int ANSWERSET = 3;
        public static final int TESTCOLLECTION = 4;
        public static final int MATCHMAKER = 5;
        public static final int EVALUATION = 6;
        public static final int RESULT = 7;
        public static final int PERFORMANCE = 8;
        public static final int SETTINGS = 9;
        public static final int ABOUT = 10;
        public static final int UNKNOWN = -1;      
    }
