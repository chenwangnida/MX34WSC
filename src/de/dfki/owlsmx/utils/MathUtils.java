/*
 * Created on 12.12.2004
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

import de.dfki.owlsmx.similaritymeasures.SimilarityMeasures;

/**
 * @author bEn
 *
 */
public class MathUtils {
    public static final double log2 = Math.log(2);
    
    public static double vectorNorm(double[] vector) {        
        return Math.sqrt(vectorDotProduct(vector,vector));
    }
    
    public static double vectorDotProduct(double[] v1,double[] v2) {
        if (v1.length!=v2.length)
            return 0;
        double sum=0;
        for (int i=0;i<v1.length;i++)
            sum += v1[i] * v2[i];
        return sum;
    }
    
    public static double vectorNorm(int[] vector) {        
        return Math.sqrt(vectorDotProduct(vector,vector));
    }
    
    public static double vectorDotProduct(int[] v1,int[] v2) {
        if (v1.length!=v2.length)
            return 0;
        double sum=0;
        for (int i=0;i<v1.length;i++)
            sum += v1[i] * v2[i];
        return sum;
    }
    
    public static int vectorSum(int[] vector) {
        int sum=0;
        for (int i=0;i<vector.length;i++)
            sum += vector[i];
        return sum;
    }
    
    public static double vectorSum(double[] vector) {
        double sum=0;
        for (int i=0;i<vector.length;i++)
            sum += vector[i];
        return sum;
    }
    
    public static void main(String[] args ) {
        System.out.println("Testing MathUtils:");
        double[] fun = {1,1,1,1};
        System.out.println("");
        System.out.println("Testing vectorNorm");
        if (vectorNorm(fun)==2)
            System.out.println("                    Success");
         else
             System.out.println("                    Fail");
        System.out.println("");
        System.out.println("Testing vectorDotProduct");
        if (vectorDotProduct(fun,fun)==4)
            System.out.println("                    Success");
         else
             System.out.println("                    Fail");
        System.out.println("");
        System.out.println("Testing vectorSum");
        if (vectorSum(fun)==4)
            System.out.println("                    Success");
         else
             System.out.println("                    Fail");       
        
        System.out.println("Testing CosineSimilarity:");
        int[] tf1 = {1,4};
        //int[] tf2 = {1,0,3,0};
        SimilarityMeasures similar = new SimilarityMeasures();
        double[] result = similar.logrithmicTermFrequency(tf1);
        for (int i=0;  i<result.length;i++) {
            System.out.print(result[i] + " ");
        }
        
    }

    public static String toString(double[] vector){
        String result="";
        for (int i=0; i<vector.length;i++)
            result += " " + vector[i];
        return result.trim();
    }
    
    public static String toString(int[] vector){
        String result="";
        for (int i=0; i<vector.length;i++)
            result += " " + vector[i];
        return result.trim();
    }
}
