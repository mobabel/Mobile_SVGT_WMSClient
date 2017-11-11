package com.tinyline.app;

import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import java.util.Vector;
import javax.microedition.rms.*;

import com.tinyline.tiny2d.*;
import com.tinyline.svg.*;
import GUI.PlaceSearchForm;
import GUI.Format_PositionForm;
import GUI.CONTROL.GUIController;



public class MobileSVGForm extends Form implements CommandListener{
    private Display display;
    private Displayable parent;
	private GUIController controller;
	private Format_PositionForm    format_positionform   = null;
	
	// The Main screen
	// SVG Canvas
    MIDPSVGCanvas canvas;
    Command panCommand, zoomCommand, origViewCommand, qualityCommand, pauseCommand,
    helpCommand, backCommand;

    // Help screen
    Form helpScreen;
    Command helpBackCommand;

	boolean initialized;
	String StringGoURL="";
	
	//private PlaceSearchForm placesearchForm ;

	/**
 * Construct a new TinyLine MIDlet and initialize the base options
 * and SVG canvas to be used when the MIDlet is started.
 */
	

 public MobileSVGForm(String title,Display d, Displayable p,GUIController control,Format_PositionForm format_positionForm){
	             super(title);
			        display=d;
			        parent=p;
			        //mobilesvgtMidlet=mobilesvgClient;
			        controller=control;
			        format_positionform=format_positionForm;
			        
				 // Create the SVG canvas.
				 canvas = new MIDPSVGCanvas(display);

				 // Load incons
				 canvas.init();

				 canvas.setCommandListener(this);

				 
			 try
		       {
			       if (initialized == false) 
			    	   initialize();
			       
					// Get graphics

					// Load the default SVG font.
			       SVGDocument doc =  canvas.loadSVG("/tinyline/helvetica.svg");
			       SVGFontElem font = SVGDocument.getFont(doc,SVG.VAL_DEFAULT_FONTFAMILY);
			       SVGDocument.defaultFont = font;

			  		  // Add the default event listener
					      PlayerListener defaultListener = new PlayerListener(canvas);
					      canvas.addEventListener("default", defaultListener, false);

								// Start the event dispatching queue
								canvas.start();

                    String StringGoURL=format_positionform.getString_FinalURL();
								// Loads the splash splash SVGT image
                   // canvas.goURL("http://localhost/test/tman.svg");
                    //canvas.goURL("http://wms.jpl.nasa.gov/wms.cgi?wms_server=wms.cgi&layers=global_mosaic&srs=EPSG:4326&width=1000&height=500&bbox=-180,-90,180,90&format=image/jpeg&styles=&zoom=");
                    System.out.println("Ready to load: "+StringGoURL);
			       canvas.goURL(StringGoURL);

		       }
		       catch( Exception e)
		       {
		       }
		       RepaintThread repaintthread=new RepaintThread();
		       repaintthread.start();
	    }

 public class RepaintThread extends Thread{
	 public void run(){

		 
	     panCommand       = new Command("Pan", Command.SCREEN, 1);
		 zoomCommand      = new Command("Zoom", Command.SCREEN, 1);
		 origViewCommand  = new Command("Orig View", Command.SCREEN, 1);
         qualityCommand   = new Command("Quality", Command.SCREEN, 1);
		 pauseCommand     = new Command("Pause", Command.SCREEN, 1);
         helpCommand      = new Command("Help", Command.SCREEN, 1);
		 backCommand      = new Command("Back", Command.BACK, 2);
		 
 	     
 	     canvas.addCommand(panCommand);
 	     canvas.addCommand(zoomCommand);
 	     canvas.addCommand(origViewCommand);
         canvas.addCommand(qualityCommand);
 	     //canvas.addCommand(pauseCommand);
         canvas.addCommand(helpCommand);
 	     canvas.addCommand(backCommand);
		 
 	    controller.setCurrent(canvas);
	     canvas.repaint();
	     System.out.println("repait in thread");
	 }
}

	
 /** Pause the MIDlet. */
 public void pauseApp()
 {
			 canvas.stop();
 }

 /**
  * Destroy the MIDlet.
  * @param unconditional Unconditional flag.
  */
 public void destroyApp(boolean unconditional)
 {
			 canvas.stop();
 }
 /**
  * Respond to commands. Commands are added to each screen as
  * they are created.  Each screen uses the TinyLine MIDlet as the
  * CommandListener.
		 * @param c the command that triggered this callback
  * @param s the screen that contained the command
  */
 public void commandAction(Command c, Displayable s)
		{
///System.out.println("Command " +c);

			 if(c == panCommand)
			 {
					 canvas.selectMode(MIDPSVGCanvas.MODE_PAN);
			 }

			 else if(c == zoomCommand)
			 {
					 canvas.selectMode(MIDPSVGCanvas.MODE_ZOOM);
			 }

			 else if(c == origViewCommand)
			 {
				    canvas.origView();
			 }

             else if(c == qualityCommand)
             {
                   canvas.switchQuality();
             }

			 //else if(c == pauseCommand)
			 //{
						//canvas.pauseResumeAnimations();
			 //}

			 else if(c == helpCommand)
			 {
				 controller.setCurrent(helpScreen);
			 }

             else if(c == backCommand)
			 {      
            	    s=parent;
            		System.out.println("back to PlaceSearchForm");
            		controller.setCurrent(s);
			 }

             else if (c == helpBackCommand)
			 {
            	 controller.setCurrent(canvas);
				  canvas.repaint();
			 }
		}
	/** Initialises the Bookmarks data structure */
 private void initialize()
		{


    // Create the Help screen.
    helpScreen = new Form("Help");
    helpScreen.append(new StringItem("",helpString));
    helpBackCommand = new Command("Back", Command.BACK, 1);
    helpScreen.addCommand(helpBackCommand);
    helpScreen.setCommandListener(this);

    initialized = true;
 }
 
 /** Help */
 private static String helpString =
"TinyLine implements Scalable Vector Graphics Tiny (SVGT) for J2ME."
+"\n\n"
+"GETTING STARTED\n"
+"TinyLine comes with several SVGT samples, other samples located on the tinyline.com. "
+"From the TinyLine welcome screen, you can use RIGHT and LEFT keys to navigate "
+"among bookmarked links. Or, you can select the <Open> command to open the bookmarks."
+"\n\n"
+"NAVIGATION\n"
+"In the <Zoom> mode you can zoom in or zoom out using UP and DOWN keys.\n"
+"The <Orig View> command returns the viewing image to its original view.\n"
+"The <Quality> command turns off or on the antialising.\n"
+"The <Pause> command stops or resumes animations."
+"\n\n"
+"MORE\n"
+"For more about TinyLine, see http://www.tinyline.com/."
+"\n"
+"Copyright (c) 2002-2005 TinyLine. All rights reserved."
+"\n";
	
}
