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
 * The <tt>Shapes</tt> shows how to build and display basic SVG shapes
 * using TinyLine SVGT SDK.
 * is the J2ME MIDP 2.0
 * implementation of the SVGT Viewer.
 * <p>
 * @author (C) Andrew Girow
 * @version 1.9
 * <p>
 */
public class Shapes extends MIDlet implements CommandListener
{

		// The Main screen
    Display display;

		// The SVG canvas
    ViewerCanvas canvas;

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
    public Shapes()
    {
			 display = Display.getDisplay(this);
			 // Create the Main screen.
			 canvas = new ViewerCanvas(display);
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
			 if (c == menuCircle)
			 {
					 Circle();
			 }
       else if(c == menuRect)
       {
            Rect();
       }

       else if(c == exitCommand)
			 {
					destroyApp(true);
	        notifyDestroyed();
			 }
		}


    /**
     * Creates the circle SVG element.
     */
    void Circle()
		{
        // We should add the circle element after the 'whiteboard'
        // <circle id="mycircle" cx="70" cy="100" r="30" fill="red"
				// stroke="yellow" stroke-width="4"  />
				//
				//
        // Get the raster
        SVGRaster   raster   = canvas.raster;
        // Get the SVGT document
        SVGDocument document = raster.getSVGDocument();

        // Get the root of the SVGT document
        SVGSVGElem root = (SVGSVGElem)document.root;

				// If there is a node with id 'mycircle' then remove it.
				// Otherwise create and add it.
				SVGNode node = SVGNode.getNodeById(root, new TinyString("mycircle".toCharArray()));
				if(node != null)
				{
			     // 3. Fire the update event
					 SVGNode parent = node.parent;
					 int index = parent.children.indexOf(node,0);
           parent.removeChild(index);

				// Repaint the raster.
					 raster.setDevClip(node.getDevBounds(raster));
           raster.update();
           raster.sendPixels();
           return;
        }

        // Create a new circle element
        SVGEllipseElem circle =
          (SVGEllipseElem)document.createElement(SVG.ELEM_CIRCLE);
        // Add the circle element AFTER the whiteboard element
				node = SVGNode.getNodeById(root, new TinyString("whiteboard".toCharArray()));
				if(node != null)
				{
					 SVGNode parent = node.parent;
					 int index = parent.children.indexOf(node,0);
           parent.addChild(circle, index + 1);
				}
        // Use the DOM-like API to change properties
        TinyNumber cx = new TinyNumber(70<<TinyUtil.FIX_BITS);
        TinyNumber cy = new TinyNumber(100<<TinyUtil.FIX_BITS);
        TinyNumber r = new TinyNumber(30<<TinyUtil.FIX_BITS);
        TinyNumber stroke_width = new TinyNumber(4<<TinyUtil.FIX_BITS);
        TinyColor fillColor = redColor;
        TinyColor strokeColor = yellowColor;
        try
        {
				   circle.setAttribute(SVG.ATT_ID, new TinyString("mycircle".toCharArray()));
				   circle.setAttribute(SVG.ATT_CX,cx);
				   circle.setAttribute(SVG.ATT_CY,cy);
				   circle.setAttribute(SVG.ATT_R,r);
				   circle.setAttribute(SVG.ATT_FILL,fillColor);
				   circle.setAttribute(SVG.ATT_STROKE,strokeColor);
				   circle.setAttribute(SVG.ATT_STROKE_WIDTH,stroke_width);
           // Init the element.
           circle.createOutline();
        }
        catch(Exception ex)
        {
           ex.printStackTrace();
        }

				// Repaint the raster.
        raster.setDevClip(circle.getDevBounds(raster));
        raster.update();
        raster.sendPixels();
		}

    /**
     * Creates the rect SVG element.
     */
    void Rect()
    {
        // We should add the rect element after the 'whiteboard'
        // <rect id="myrect" x="120" y="80" width="40" height="40"
				// fill="yellow" stroke="navy" stroke-width="4"  />
        // Get the raster
        SVGRaster   raster   = canvas.raster;
        // Get the SVGT document
        SVGDocument document = raster.getSVGDocument();

        // Get the root of the SVGT document
        SVGSVGElem root = (SVGSVGElem)document.root;

				// If there is a node with id 'myrect' then remove it.
				// Otherwise create and add it.
				SVGNode node = SVGNode.getNodeById(root, new TinyString("myrect".toCharArray()));
				if(node != null)
				{
			     // 3. Fire the update event
					 SVGNode parent = node.parent;
					 int index = parent.children.indexOf(node,0);
           parent.removeChild(index);
				// Repaint the raster.
           raster.setDevClip(node.getDevBounds(raster));
           raster.update();
           raster.sendPixels();
           return;
        }

        // Create a new rect element
        SVGRectElem rect =
          (SVGRectElem)document.createElement(SVG.ELEM_RECT);

        // Add the rect element AFTER the whiteboard element
				node = SVGNode.getNodeById(root, new TinyString("whiteboard".toCharArray()));
				if(node != null)
				{
					 SVGNode parent = node.parent;
					 int index = parent.children.indexOf(node,0);
           parent.addChild(rect, index + 1);
				}

        // Use the Direct TinyLine API to change properties
				rect.id = new TinyString("myrect".toCharArray());
        rect.x = 120 << TinyUtil.FIX_BITS;
        rect.y =  80 << TinyUtil.FIX_BITS;
        rect.width = 40 << TinyUtil.FIX_BITS;
        rect.height = 40 << TinyUtil.FIX_BITS;
        rect.fill   = yellowColor;
        rect.stroke = navyColor;
        rect.strokeWidth = 4 << TinyUtil.FIX_BITS;
			  // Create the outline.
        rect.createOutline();

				// Repaint the raster.
        raster.setDevClip(rect.getDevBounds(raster));
        raster.update();
        raster.sendPixels();
    }

}
