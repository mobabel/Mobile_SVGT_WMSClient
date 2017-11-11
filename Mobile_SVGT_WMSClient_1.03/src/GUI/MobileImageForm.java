package GUI;

import java.io.*;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import java.util.Vector;
import javax.microedition.rms.*;
import javax.microedition.lcdui.Image;
import com.tinyline.tiny2d.*;
import com.tinyline.svg.*;
import GUI.PlaceSearchForm;
import GUI.Format_PositionForm;
import GUI.CONTROL.GUIController;


public class MobileImageForm extends Form implements CommandListener{
    private Display display;
    private Displayable parent;
	private GUIController controller;
	private Format_PositionForm    format_positionform   = null;
	
	// The Main screen
	// SVG Canvas
    ImageCanvas canvas;
    Command panCommand, zoomCommand, origViewCommand, qualityCommand, pauseCommand,
    helpCommand, backCommand,helpBackCommand;

    // Help screen
    Form helpScreen;

	boolean initialized;
	String StringGoURL="";
	
	private Image originalPNG=null;

	/**
 * Construct a new TinyLine MIDlet and initialize the base options
 * and SVG canvas to be used when the MIDlet is started.
 */
	

 public MobileImageForm(String title,Display d, Displayable p,GUIController control,Format_PositionForm format_positionForm){
	             super(title);
			     display=d;
			     parent=p;
			     controller=control;
			     format_positionform=format_positionForm;
 
				 panCommand       = new Command("Pan", Command.SCREEN, 1);
			     zoomCommand      = new Command("Zoom", Command.SCREEN, 1);
				 origViewCommand  = new Command("Orig View", Command.SCREEN, 1);
			     helpCommand      = new Command("Help", Command.SCREEN, 1);
				 backCommand      = new Command("Back", Command.BACK, 2);
                 //Create the Image canvas.
				 canvas = new ImageCanvas(display);
				 
           		 canvas.addCommand(panCommand);
        		 canvas.addCommand(zoomCommand);
        		 canvas.addCommand(origViewCommand);
        		 canvas.addCommand(helpCommand);
        		 canvas.addCommand(backCommand);
        		 
        		 canvas.setCommandListener(this);

				 System.out.println("Step 1==================");
				 // Load incons
				 canvas.init();
				 //canvas.repaint();
				 System.out.println("Step 2==================");

	             try {
					 if (initialized == false) 
					    	initialize(); 
					 
				  	 // Add the default event listener
					 //PlayerListener defaultListener = new PlayerListener(canvas);
					 //canvas.addEventListener("default", defaultListener, false);

					 // Start the event dispatching queue
					 canvas.start();

		             String StringGoURL=format_positionform.getString_FinalURL();
					 // Loads the raster PNG image
		             System.out.println("Ready to load: "+StringGoURL);
	            	 canvas.originalPNG=canvas.createImage(StringGoURL);
	            	 //canvas.originalPNG=canvas.createImage("http://localhost/test/wms.png");
	            	 originalPNG=canvas.originalPNG;
	            	 
				} catch (IOException e) {
					canvas.alertError("e.printStackTrace()");
				}
 			     RepaintThread repaintthread=new RepaintThread();
			     repaintthread.start();
			     
	    }

 public class RepaintThread extends Thread{
	 public void run(){
 		 controller.setCurrent(canvas);

         System.out.println("repait in thread, and boolean loaded is: "+canvas.loaded);
    	 canvas.repaint();
	 }
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

			 if(c == panCommand)
			 {
					 canvas.selectMode(canvas.MODE_PAN);
			 }

			 else if(c == zoomCommand)
			 {
					 canvas.selectMode(ImageCanvas.MODE_ZOOM);

			 }

			 else if(c == origViewCommand)
			 {
				 canvas.selectMode(ImageCanvas.MODE_ORIGVIEW);
				 canvas.origView(originalPNG);
				 canvas.repaint();
			 }

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
 private void initialize(){
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
	 "Raster Image Viewer for J2ME."
	 +"\n\n"
	 +"GETTING STARTED\n"
	 +"From the welcome screen, you can use RIGHT, LEFT, UP and DOWN keys to navigate "
	 +"\n\n"
	 +"NAVIGATION\n"
	 +"In the <Zoom> mode you can zoom in or zoom out using UP and DOWN keys.\n"
	 +"The <Orig View> command returns the viewing image to its original view.\n"
	 +"\n\n"
	 +"\n"
	 +"Copyright (c) 2005-2006 Leelight. All rights reserved."
	 +"\n";
	
}
