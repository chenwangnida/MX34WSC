/*
 * Created on 26.11.2004
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
package de.dfki.owlsmx.gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * @author bEn
 *
 */
public class Point {
    public int X,Y;
    public int drawX,drawY;
    private int pointsize=5;
    private int border=50;
    private Font font;
    private String cords;
    private boolean drawCoordinates=false;

    
    /**
     * @return Returns the border.
     */
    public int getBorder() {
        return border;
    }
    
    /**
     * @param border The border to set.
     */
    public void setBorder(int border) {
        this.border = border;
    }
    
    /**
     * @return Returns the point	size.
     */
    public int getPointsize() {
        return pointsize;
    }
    
    /**
     * @param pointsize The pointsize to set.
     */
    public void setPointsize(int pointsize) {
        this.pointsize = pointsize;
    }    
    
    public void draw(Graphics g, float basicX, float basicY, float reduceHeight) {
        Graphics2D g2 = (Graphics2D)g;
        font = g2.getFont();
        drawX = Math.round( (X*basicX) + border);
        drawY = Math.round( (Y*basicY) + reduceHeight -border);
        //System.out.println("(" + X + " "+Y + ")");
        cords = "(" + X + "/" + Y + ")";
        FontRenderContext frc= g2.getFontRenderContext();        
        Rectangle2D bounds = font.getStringBounds(cords, frc);
        //LineMetrics metrics = font.getLineMetrics(cords, frc);
        float width = (float) bounds.getWidth();     // The width of our text
        //float lineheight = metrics.getHeight();      // Total line height
        if (drawCoordinates)
            g2.drawString(cords,drawX-(width/2),drawY-font.getSize()+5);
        g2.drawOval( drawX - (2*pointsize)/3, drawY - pointsize/2, pointsize, pointsize);  
    }
    
    /**
     * 
     */
    public Point(int x, int y) {
        X=x;
        Y=y;
    }
    
    public Point(int x, int y, int border) {
        X=x;
        Y=y;
        this.border=border;
    }
    
    public int compareTo(Object obj2) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        if (this == obj2) {
            return EQUAL;
          } 
        Point p2 = (Point) obj2;
        if (this.X<p2.X)
            return BEFORE;
        else if (this.X==p2.X)
            return EQUAL;
        else return AFTER;        
    }


    /**
     * @return Returns if the coordinates will be drawn.
     */
    public boolean isDrawCoordinates() {
        return drawCoordinates;
    }
    /**
     * @param drawCoordinates  if the coordinates will be drawn..
     */
    public void setDrawCoordinates(boolean drawCoordinates) {
        this.drawCoordinates = drawCoordinates;
    }

}
