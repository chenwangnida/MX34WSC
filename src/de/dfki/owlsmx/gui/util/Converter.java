/*
 * Created on 11.10.2005
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

package de.dfki.owlsmx.gui.util;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.jfree.chart.JFreeChart;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public abstract class Converter {


    public static void convertToPdf(JFreeChart chart, int width, int height, String filename) {
	    // step 1    	
	    Document document = new Document(new Rectangle(width, height));
	    try {
		    // step 2
		    PdfWriter writer;
			writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
			// step 3
		    document.open();
		    // step 4
		    PdfContentByte cb = writer.getDirectContent();
		    PdfTemplate tp = cb.createTemplate(width, height);
		    Graphics2D g2d = tp.createGraphics(width, height, new DefaultFontMapper());
		    Rectangle2D r2d = new Rectangle2D.Double(0, 0, width, height);
		    chart.draw(g2d, r2d);
		    g2d.dispose();
		    cb.addTemplate(tp, 0, 0);
	    } catch(DocumentException de) {
	    	
	    }
	    catch (FileNotFoundException e) {
	    	
	    }
	    // step 5
	    document.close();
    }
}
