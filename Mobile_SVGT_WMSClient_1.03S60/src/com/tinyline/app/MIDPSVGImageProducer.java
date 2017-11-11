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
package com.tinyline.app;

import com.tinyline.svg.*;
import com.tinyline.tiny2d.*;

/**
 * The <tt>MIDPSVGImageProducer</tt> is the J2ME Personal Profile
 * implementation of the SVGImageProducer interface.
 * <p>
 * @author (C) Andrew Girow
 * @version 1.9
 * <p>
 */

public class MIDPSVGImageProducer implements SVGImageProducer
{

    /** The ImageConsumer associated with this SVGImageProducer */
    private ImageConsumer theConsumer;

    private SVGRaster  raster;

	 /**
    * Constructs a new <tt>MIDPSVGImageProducer</tt>.
    */
    public MIDPSVGImageProducer(SVGRaster renderer)
    {
			 raster = renderer;
    }

		/**
     * Sets the ImageConsumer for this renderer
     * @param imageconsumer the specified <code>ImageConsumer</code>
     */
    public void setConsumer(ImageConsumer consumer)
    {
        theConsumer = consumer;
    }

    /**
     * Returns true if this renderer has a consumer; otherwise
     * returns false
     */
    public boolean hasConsumer()
    {
        return (theConsumer != null);
    }

    /**
     * Sends pixel data to the consumer.
     */
    public void sendPixels()
    {
				theConsumer.newPixels(raster.clipRect.xmin, raster.clipRect.ymin, raster.clipRect.xmax -
				raster.clipRect.xmin, raster.clipRect.ymax - raster.clipRect.ymin);
		}

    /**
     * Notifies the consumer.
     */
    public void  imageComplete()
    {
    }
}
