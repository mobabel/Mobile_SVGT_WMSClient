/******************************************************************
 * Copyright (C) 2002-2005 Andrew Girow. All rights reserved.     *
 * ---------------------------------------------------------------*
 * This software is published under the terms of the TinyLine     *
 * License, a copy of which has been included with this           *
 * distribution in the TINYLINE_LICENSE.TXT file.                 *
 *                                                                *
 * For more information on the TinyLine,                          *
 * please see <http://www.tinyline.com/>.                         *
 *****************************************************************/
package tinyapp;

import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.Vector;
import javax.microedition.rms.*;

import com.tinyline.tiny2d.*;
import com.tinyline.svg.*;


/**
 * The <tt>Viewer</tt> application demostrates how to use the
 * TinyLine SVG Library for creating a very basic SVG Map Viewer.
 * <p>
 * @author (C) Andrew Girow
 * @version 1.9
 * <p>
 */
public class Viewer extends MIDlet implements CommandListener
{

		// The Main screen
    Display display;

		// The SVG canvas
    ViewerCanvas canvas;

    // Commands
		Command linkCommand,
    panCommand, zoomCommand, origViewCommand,
    helpCommand, exitCommand;

    // Help screen
    Form helpScreen;
    Command helpBackCommand;


		/**
     * Construct a new Viewer MIDlet and initialize the base options
     * and SVG canvas to be used when the MIDlet is started.
     */
    public Viewer()
    {
			 display = Display.getDisplay(this);
			 // Create the Main screen.
			 canvas = new ViewerCanvas(display);
			 // Load incons
			 canvas.init();

			 linkCommand      = new Command("Link", Command.SCREEN, 1);
		   panCommand       = new Command("Pan", Command.SCREEN, 1);
			 zoomCommand      = new Command("Zoom", Command.SCREEN, 1);
			 origViewCommand  = new Command("Orig View", Command.SCREEN, 1);
       helpCommand      = new Command("Help", Command.SCREEN, 1);
			 exitCommand      = new Command("Exit", Command.EXIT, 2);
 	     canvas.addCommand(linkCommand);
 	     canvas.addCommand(panCommand);
 	     canvas.addCommand(zoomCommand);
 	     canvas.addCommand(origViewCommand);
       canvas.addCommand(helpCommand);
 	     canvas.addCommand(exitCommand);
			 canvas.setCommandListener(this);

			 // Create the Help screen.
       helpScreen = new Form("Help");
       helpScreen.append(new StringItem("",helpString));
       helpBackCommand = new Command("Back", Command.BACK, 1);
       helpScreen.addCommand(helpBackCommand);
       helpScreen.setCommandListener(this);
    }

    /**
     * Start up the MIDlet by setting the canvas
     * and loading the default SVG font and the splash SVGT image.
     */
    public void startApp() throws MIDletStateChangeException
    {
			 try
       {
          display.setCurrent(canvas);
          canvas.repaint();
					// Loads the default SVG font
		      // This is faster then to load the font from the svg file
          // TinyFont font = HelveticaFont.getFont();
          SVGDocument doc =  canvas.loadSVG("/tinyline/helvetica.svg");
          SVGFontElem font = SVGDocument.getFont(doc,SVG.VAL_DEFAULT_FONTFAMILY);
          SVGDocument.defaultFont = font;
					// Loads the SVGT image
          canvas.goURL("/svg/tgirl.svg");
       }
       catch( Exception e)
       {
       }
    }

    /** Pause the MIDlet. */
    public void pauseApp()
    {
    }

    /**
     * Destroy the MIDlet.
     * @param unconditional Unconditional flag.
     */
    public void destroyApp(boolean unconditional)
    {
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
       if(c == linkCommand)
			 {
					 canvas.type = ViewerCanvas.TYPE_LINK;
			 }

			 else if(c == panCommand)
			 {
					 canvas.type = ViewerCanvas.TYPE_PAN;
			 }

			 else if(c == zoomCommand)
			 {
					 canvas.type = ViewerCanvas.TYPE_ZOOM;
			 }

			 else if(c == origViewCommand)
			 {
					 canvas.type = ViewerCanvas.TYPE_LINK;
 			     canvas.origView();
			 }

			 else if(c == helpCommand)
			 {
           display.setCurrent(helpScreen);
			 }

       else if(c == exitCommand)
			 {
					 destroyApp(true);
	         notifyDestroyed();
			 }
       else if (c == helpBackCommand)
			 {
           display.setCurrent(canvas);
					 canvas.repaint();
			 }
		}

   /** Help */
     private static String helpString =
   "Viewer ."
   +"\n\n"
   +"NAVIGATION\n"
   +"In the <Link> mode you can use UP and DOWN keys to navigate links. A link will be "
   +"highlighted with a blue rectangle. You can then select it by pressing FIRE key. If your "
   +"device has a pointer, you can also select any link by tapping your pointer on it.\n"
   +"In the <Pan> mode you can scroll using LEFT, RIGHT, UP and DOWN keys. If your "
   +"device has a pointer, you can also scroll by dragging the pointer.\n"
   +"In the <Zoom> mode you can zoom in or zoom out using UP and DOWN keys.\n"
   +"The <Orig View> command returns the viewing image to its original view.\n"
   +"\n\n"
   +"MORE\n"
   +"For more about TinyLine, see http://www.tinyline.com/."
   +"\n"
   +"Copyright (c) 2002-2005 TinyLine. All rights reserved."
   +"\n";
}
