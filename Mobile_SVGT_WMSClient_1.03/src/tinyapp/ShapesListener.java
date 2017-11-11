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

import org.w3c.dom.events.*;
import com.tinyline.tiny2d.*;
import com.tinyline.svg.*;


import com.tinyline.app.MIDPSVGCanvas;
import com.tinyline.app.SVGEvent;


/**
 * This class represents the custom event listener
 * <p>
 * @author (C) Andrew Girow
 * @version 1.9
 * <p>
 */
public class ShapesListener implements org.w3c.dom.events.EventListener
{
		MIDPSVGCanvas canvas;

	  // The red color
    TinyColor redColor    =  new TinyColor(0xFFFF0000);
	  // The yellow color
    TinyColor yellowColor =  new TinyColor(0xFFFFFF00);
	  // The navy color
    TinyColor navyColor   =  new TinyColor(0xFF000080);

		public ShapesListener(MIDPSVGCanvas canvas)
		{
			 this.canvas = canvas;
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
           SVGEvent event = new SVGEvent(SVGEvent.EVENT_UPDATE, node.getDevBounds(raster) );
           canvas.postEvent(event);
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
			 // 3. Fire the update event
       SVGEvent event = new SVGEvent(SVGEvent.EVENT_UPDATE, circle.getDevBounds(raster) );
       canvas.postEvent(event);
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
           SVGEvent event = new SVGEvent(SVGEvent.EVENT_UPDATE, node.getDevBounds(raster) );
           canvas.postEvent(event);
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

			  // 3. Fire the update event
        SVGEvent event = new SVGEvent(SVGEvent.EVENT_UPDATE, rect.getDevBounds(raster) );
        canvas.postEvent(event);
    }

    /**
     *  <b>uDOM:</b> Invoked whenever an event occurs of the type for which
     *  the <tt> EventListener</tt> interface was registered.
     *  @param evt The <tt>Event</tt> contains contextual information
     *             about the event.
     */
    public void handleEvent(Event evt)
    {
//  System.out.println("handleEvent " + evt.getType());

         SVGEvent theEvent = (SVGEvent) evt;

         switch(theEvent.id)
         {
							case Shapes2.USER_EVENT:
                   TinyNumber subtype = (TinyNumber)theEvent.data;
									 switch ( subtype.val )
									 {
									 	    case Shapes2.SHAPE_CIRCLE:
														 Circle();
														 break;
									 	    case Shapes2.SHAPE_RECT:
														 Rect();
														 break;
									 }
                   break;


         } //switch

    }
}
