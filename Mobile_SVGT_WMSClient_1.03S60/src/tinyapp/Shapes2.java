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

import com.tinyline.app.MIDPSVGCanvas;
import com.tinyline.app.SVGEvent;
import com.tinyline.app.PlayerListener;

/**
 * The <tt>Shapes2</tt> shows how to build and display basic SVG shapes
 * using TinyLine SVGT SDK.
 * is the J2ME MIDP 2.0
 * implementation of the SVGT Viewer.
 * <p>
 * @author (C) Andrew Girow
 * @version 1.9
 * <p>
 */
public class Shapes2 extends MIDlet implements CommandListener
{
	  public static final int SHAPE_CIRCLE   = 0;
	  public static final int SHAPE_RECT     = 1;
	  public static final int USER_EVENT     = 100;

		// The Main screen
    Display display;

		// The SVG canvas
    MIDPSVGCanvas canvas;

    Command exitCommand, menuCircle, menuRect;

	  // The red color
    TinyColor redColor    =  new TinyColor(0xFFFF0000);
	  // The yellow color
    TinyColor yellowColor =  new TinyColor(0xFFFFFF00);
	  // The navy color
    TinyColor navyColor   =  new TinyColor(0xFF000080);


		/**
     * Construct a new TinyLine MIDlet and initialize the base options
     * and SVG canvas to be used when the MIDlet is started.
     */
    public Shapes2()
    {
			 display = Display.getDisplay(this);
			 // Create the Main screen.
			 canvas = new MIDPSVGCanvas(display);
			 // Load incons
			 canvas.init();

			 menuCircle      = new Command("Circle", Command.SCREEN, 1);
       menuRect        = new Command("Rect", Command.SCREEN, 1);
			 exitCommand     = new Command("Exit", Command.EXIT, 2);
			 canvas.addCommand(menuCircle);
       canvas.addCommand(menuRect);
 	     canvas.addCommand(exitCommand);
			 canvas.setCommandListener(this);
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

     		  // Add the default events listener
		      PlayerListener defaultListener = new PlayerListener(canvas);
		      canvas.addEventListener("default", defaultListener, false);

					// Add the user defined (custom) events listener
		      ShapesListener shapesListener = new ShapesListener(canvas);
		      canvas.addEventListener("shapes", shapesListener, false);

					// Start the event queue
		      canvas.start();

					// Loads the SVGT image
          canvas.goURL("/svg/shapes.svg");
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
     * they are created.  Each screen uses the Shapes MIDlet as the
     * CommandListener.
		 * @param c the command that triggered this callback
     * @param s the screen that contained the command
     */
    public void commandAction(Command c, Displayable s)
		{
///System.out.println("Command " +c);
				SVGEvent event;
        if(c == menuCircle)
		    {
           event = new SVGEvent(USER_EVENT, new TinyNumber(SHAPE_CIRCLE ));
           canvas.postEvent(event);
		    }
        else if(c == menuRect)
		    {
           event = new SVGEvent(USER_EVENT, new TinyNumber(SHAPE_RECT ));
           canvas.postEvent(event);
		    }
        else if(c == exitCommand)
			  {
				   destroyApp(true);
	         notifyDestroyed();
			  }
		}
}
