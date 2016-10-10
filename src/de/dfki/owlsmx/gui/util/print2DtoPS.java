package de.dfki.owlsmx.gui.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.SimpleDoc;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JPanel;

public class print2DtoPS implements Printable{
	private int width;
	private int height;
	
	public print2DtoPS(String path, JPanel panel, int width, int height) {
		this.width = width;
		this.height = height;
		
		/* Use the pre-defined flavor for a Printable from an InputStream */
		DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

 		/* Specify the type of the output stream */
		String psMimeType = DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType();

		/* Locate factory which can export a GIF image stream as Postscript */
		StreamPrintServiceFactory[] factories =
		StreamPrintServiceFactory.lookupStreamPrintServiceFactories(
					flavor, psMimeType);
		if (factories.length == 0) {
			System.err.println("No suitable factories");
			System.exit(0);
		}

		try {
			/* Create a file for the exported postscript */
			FileOutputStream fos = new FileOutputStream(path);

			/* Create a Stream printer for Postscript */
			StreamPrintService sps = factories[0].getPrintService(fos);

			/* Create and call a Print Job */
			DocPrintJob pj = sps.createPrintJob();
			PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();

			Doc doc = new SimpleDoc(this, flavor, null);

			pj.print(doc, aset);
			fos.close();

		} catch (PrintException pe) { 
			System.err.println(pe);
		} catch (IOException ie) { 
			System.err.println(ie);
		}
	}
	
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
		if (pageIndex == 0) {
			Graphics2D g2d = (Graphics2D)g;
			GraphicsConfiguration gc = g2d.getDeviceConfiguration();
			BufferedImage image = gc.createCompatibleImage(width,height, BufferedImage.TYPE_INT_RGB);
		    Graphics2D g2 = image.createGraphics();    
		    g2.setPaint(Color.white);
		    g2.fillRect(0,0,width, height);
		    g2.dispose();
			//int width = panel.getWidth();
			//int height = panel.getHeight();
			//panel.setSize((new Double(pf.getWidth()/2)).intValue(),(new Double(pf.getHeight()/2)).intValue());
			//g.setColor(Color.WHITE);
			//g.drawRect(0,0,width,height);
			//g.setColor(Color.BLACK);
			//panel.paint((Graphics2D)g);			
			//panel.setSize(width,height);
			
			return Printable.PAGE_EXISTS;			
		} else {
			return Printable.NO_SUCH_PAGE;
		}
	}

}


