

/*
 *   Splash.java
 *
 *   A simple splashscreen application.
 *  
 *   When a large java program starts the JVM often has to load and verify
 *   hundreds of classes, often taking a considerable period of time.  To
 *   avoid users from mistaking this for a problem, many java applications
 *   display a simple graphic on the screen while the application is loading.
 *
 *   Doing this in java is a little tricky, as the code that displays the
 *   graphic must not contain a direct reference to the subsequent classes,
 *   as the splash screen code will not complete loading (and thus cannot
 *   run) until any classes it may be dependant upon are loaded.  
 *
 *   This program functions as a simple wrapper application - it can be
 *   called with the name of another application as a parameter, and it
 *   displays a splash graphic before loading and launching said target
 *   application.  No alteration (or recompilation) to the target application 
 *   of the target application is needed.
 *
 *   Calling:
 *      java Splash imgname.jpg targetClass arg0 arg1 arg2 ...
 *
 *                    |           |           |    |    |
 *                    |           |           subsequent arguments are
 *                    |           |           passed to the targe program
 *                    |           |
 *                    |           this is the name of the class (it must
 *                    |           be on the classpath) which the splash program
 *                    |           should load once it has displayed the
 *                    |           splash image.  This must contain the main
 *                    |           method of the target program.
 *                    |
 *                    this is the name of an imagefile which the splash
 *                    program should display on the screen while targetClass
 *                    is loaded.
 *
 *   Properties:
 *     The system property "Splash.delaytime" is used to specify a time, in
 *     seconds, for which the splash program waits between classloading the
 *     target and actually starting it.  This is mostly used for debug and demo
 *     purposes, as typically small java programs classload so quickly that
 *     they don't need a splashscreen.  By default (and in most practical
 *     use) this property is 0, meaning no extra delay is inserted.
 *
 *   Technical notes:
 *     This application is illustrative of two parts of the java api:
 *
 *     1. The use of a mediatracker to wait for an image to be loaded.
 *        In this case this is done asynchronously (we spawn a new thread
 *        in which the mediatracker waits) to allow classloading to
 *        operate concurrently with image loading.  For most simple
 *        applications a mediatracker proves to be greatly simpler
 *        than implementing an imageobserver to watch the image load.
 *
 *     2. Unlike many other OO frameworks, java chooses not to have
 *        applications be concrete subclasses of an abstract Application
 *        type.  It's hard to see why this was thought to be a good idea
 *        (perhaps some bizarre attempt at symmetry with C).  This proves
 *        to be a considerable complication when one program loads and
 *        runs another - it's necessary to do rather a lot of legwork
 *        using the java reflection API to be able to call the main()
 *        method of the target.  Had java applications been instances
 *        of a class or interface then one would need only to downcast
 *        the application class to that base class and one could call
 *        main without using reflection (this, for example, is how
 *        applets work).  The great bulk of Splash.main() is thus 
 *        consumed with doing the requisite reflection calls to
 *        call its counterpart - readers might find this an entertaining
 *        introduction to the reflection API.
 *
 *  Copyright (c) 2002 W.Finlay McWalter.  Licenced under version 2.0
 *  of the GNU General Public Licence.
 */

package de.dfki.owlsmx.gui;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


public class Splash extends Frame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Image   splashImage;
    static boolean imageLoaded = false;
    static String class_to_load = "owlsmx.GUI.Window";
    static String image_to_load = Thread.currentThread().getContextClassLoader().getResource("owlsmx/GUI/owlsmx-logo.jpg").getFile();
    

    static class AsyncImageLoader implements Runnable {        
	String  imageFileName;
	Thread  loaderThread;
	Splash  parentFrame;

	public AsyncImageLoader(Splash parent, String fileName){
	    parentFrame   = parent;
	    imageFileName = fileName;
	    loaderThread  = new Thread(this);
	    loaderThread.start();
	}
	
	public void run() {
	    // start loading the image
	    splashImage = Toolkit.getDefaultToolkit().createImage(imageFileName);
	    
	    // wait for image to be loaded
	    MediaTracker tracker = new MediaTracker(parentFrame);
	    tracker.addImage(splashImage,0);
	    try {
		tracker.waitForID(0);
	    }
	    catch(InterruptedException e){
		e.printStackTrace();		
	    }
	    	    
	    // check to ensure the image loaded okay. It would be nice to give
	    // a more specific error message here, but the Image load/observe
	    // API doesn't give us further details.
	    if(tracker.isErrorID(0)){
		de.dfki.owlsmx.io.ErrorLog.instanceOf().report("splashloader: error loading image \"" +
				   imageFileName +
				   "\"");

		// this isn't a fatal error - the target class should be able
		// to load.
		return;
	    }

	    // resize frame to match size of image, and keep frame at centre of screen
	    parentFrame.positionAtCenter(splashImage.getWidth(null), 
					 splashImage.getHeight(null));

	    // signal a redraw, so the image can be displayed
	    imageLoaded = true;
	    parentFrame.repaint();
	}
    } /* end of static inner class AsyncImageLoader */
    
    /**
     *  Positions the window at the centre of the screen, taking into account
     *  the specified width and height
     */
    private void positionAtCenter(int width, int height){
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	setBounds((screenSize.width-width)/2,
		  (screenSize.height-height)/2,
		  width,
		  height);
    }

    public void paint(Graphics g){
	if(imageLoaded){
	    g.drawImage(splashImage,0,0,null);
	}
    }

    public void update(Graphics g){
	paint(g);
    }

    public static final void main(String [] args){
	// create a splash window, in which we'll display an image
	Splash f = new Splash();
	f.setUndecorated(true);
	f.positionAtCenter(1,1);	
	f.setVisible(true);
	//f.show();
	// start loading the image, asynchronously
	//AsyncImageLoader loader = new AsyncImageLoader(f, image_to_load);
    
	try {
	    
	    // load the target class, and get a reference to its main method
	    Class [] mainArgs = new Class[1];
	    
	    mainArgs[0] = Class.forName("[Ljava.lang.String;");
	    Method mainMethod = Class.forName(class_to_load).getMethod("main", mainArgs);

	    if((mainMethod.getModifiers() & Modifier.STATIC)==0){
	    	de.dfki.owlsmx.io.ErrorLog.instanceOf().report("splashloader: main method in target class is not static");
		System.exit(-1);
	    }
	    
	    // verify that the main method returns a void
	    if(mainMethod.getReturnType() != void.class){
		de.dfki.owlsmx.io.ErrorLog.instanceOf().report("splashloader: target class main must return void");
		System.exit(-1);
	    }

	    // wait for a time (specified in seconds by property "Splash.delaytime") and
	    // then remove the splashscreen window.  If the property isn't specified,
	    // then there is no delay.  This delay is mostly used for debugging.
	    Thread.sleep(1000 * (Integer.getInteger("Splash.delaytime",0)).intValue() + 3000);
	    f.dispose();
	    //f.hide();
	    
	    // we need to make a copy of our args, with the first two (which were
	    // intended for the splashloader) stripped off	    
	    String [] processedArgs = new String[args.length];
	    System.arraycopy(args,0,processedArgs,0,args.length);

	    // call the target class main method
	    Object[] actualArgs = new Object[1];
	    actualArgs[0] = (Object)processedArgs;
	    mainMethod.invoke(null,actualArgs);
	}
	catch(ClassNotFoundException e){
	    de.dfki.owlsmx.io.ErrorLog.instanceOf().report("splashloader: can't find class \"" + 
			       e.getMessage() +
			       "\" when loading target class \"" + 
			       class_to_load
			       +"\"");
	    System.exit(-1);
	}
	catch(NoSuchMethodException e){
	    de.dfki.owlsmx.io.ErrorLog.instanceOf().report("splashloader: no main(String[]) method found in class " + class_to_load);
	    System.exit(-1);
	}
	catch(IllegalAccessException e){
	    de.dfki.owlsmx.io.ErrorLog.instanceOf().report("splashloader: error calling main method in class " + class_to_load);
	    System.exit(-1);
	}
	catch(InvocationTargetException e){
	    de.dfki.owlsmx.io.ErrorLog.instanceOf().report("splashloader: error calling main method in class " + class_to_load);
	    System.exit(-1);
	}
	catch(InterruptedException e){}
    }
}

