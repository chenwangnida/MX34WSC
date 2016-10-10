/*
 * Created on 07.02.2005
 * OWL-S Matchmaker
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
package de.dfki.owlsmx.data;

import de.dfki.owlsmx.SimilarityMatchmaker;

/**
 * An abstract representation of the degree of match
 * 
 * @author bEn
 *
 */
public abstract class DegreeOfMatch {
    public static final int EXACT = SimilarityMatchmaker.EXACT;
    public static final int PLUGIN = SimilarityMatchmaker.PLUGIN;
    public static final int SUBSUMES = SimilarityMatchmaker.SUBSUMES;
    public static final int SUBSUMED_BY = SimilarityMatchmaker.SUBSUMED_BY;
    public static final int NEAREST_NEIGHBOUR = SimilarityMatchmaker.NEAREST_NEIGHBOUR;
    public static final int FAIL = SimilarityMatchmaker.FAIL;
    
    abstract boolean isExact();
    abstract boolean isPlugin();
    abstract boolean isSubsumes();
    abstract boolean isSubsumedBy();
    abstract boolean isNearestNeighbour();
    abstract boolean isFail();    
}
