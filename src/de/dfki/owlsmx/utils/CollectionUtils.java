/*
 * Created on 08.12.2004
 * 
 * COPYRIGHT NOTICE
 * 
 * Copyright (C) 2005 DFKI GmbH, Germany
 * Developed by Benedikt Fries, Matthias Klusch
 * 
 * The code is free for non-commercial use only.
 * You can redistribute it and/or modify it under the terms
 * of the Mozilla Public License version 1.1  as
 * published by the Mozilla Foundation at
 * http://www.mozilla.org/MPL/MPL-1.1.txt
 */
package de.dfki.owlsmx.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.utils.SetUtils;

/**
 * @author bEn
 *
 */
public class CollectionUtils extends SetUtils {

    public static void print(Set set) {
        Iterator iter = set.iterator();
        System.out.println("Printing Set: ");
        while ( iter.hasNext() ){
          System.out.println("   " + iter.next());
        }
    }
    
    public static void print(Map map) {
        Iterator iter = map.entrySet().iterator();
        Map.Entry me;
        System.out.println("Printing Map: ");
        while ( iter.hasNext() ){
          me = (Map.Entry) iter.next();
          System.out.println("   " + me.getKey() + " - " + me.getValue());
        }
    }
    
    public static ArrayList collectionToArrayList(java.util.Collection set) {
        ArrayList array = new ArrayList();
        Iterator iter = set.iterator();                
        while(iter.hasNext()){            
            array.add(iter.next());
        }
        return array;
    }
    
    public static List collectionToList(java.util.Collection set) {
        List array = new ArrayList();
        Iterator iter = set.iterator();                
        while(iter.hasNext()){            
            array.add(iter.next());
        }
        return array;
    }
}
