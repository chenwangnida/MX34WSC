/*
 * Created on 10.11.2004
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
package de.dfki.owlsmx.analysis;

/**
 * @author bEn
 *
 */
public class PerformanceEntry {
    
private int overallTime=0;

private Long openService;

public PerformanceEntry() {
    
}

public void start() {    
    openService = new Long(System.currentTimeMillis());
    
}

public void stop() {
    //System.out.println("Starting: " + openService.peek() + " - " + System.currentTimeMillis());
    overallTime+=(System.currentTimeMillis()-openService.longValue());
}

public int getOverallTime() {
    return overallTime;
}

}
