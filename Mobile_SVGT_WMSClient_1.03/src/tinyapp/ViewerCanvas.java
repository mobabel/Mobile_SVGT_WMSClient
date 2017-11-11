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
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.util.Vector;

import com.tinyline.tiny2d.*;
import com.tinyline.svg.*;
import com.tinyline.util.GZIPInputStream;

import com.tinyline.app.ImageConsumer;
import com.tinyline.app.MIDPSVGImageProducer;


/**
 * This class represents an SVG canvas used for
 * the aimple SVG Viewer demo application.
 * <p>
 * @author (C) Andrew Girow
 * @version 1.9
 * <p>
 */

public class ViewerCanvas extends Canvas
implements ImageConsumer, ImageLoader
{

   /**
    * Type of the UI
    */
	 final static int  TYPE_LINK      = 0;  // Link mode
	 final static int  TYPE_PAN       = 1;  // Pan mode
	 final static int  TYPE_ZOOM      = 3;  // Zoom mode
	 final static int  TYPE_MAXCOUNT  = 4;  // MAX
	 /* The current mode */
	 int type = TYPE_LINK;

   /**
    * The original values to calculate pan.
    */
   int pressedX;
   int pressedY;
   int draggedX;
   int draggedY;


	// The pan step
	static final int PAN_STEP    = 4;
 	static final int MENU_HEIGHT = 18;

	// Zooms levels
  private  static int MAX_ZOOMLEVEL = 5;
  private  static int MIN_ZOOMLEVEL = -5;
  private  int zoomLevel = 0;

		/* The clock image */
		Image wait;

		/** The SVG renderer */
    SVGRaster raster;
    MIDPSVGImageProducer imageProducer;

		/* The current SVG document URL */
		String  currentURL="";
		/* The current loading status */
		boolean load = true;
		/* The current dislpay */
    Display display;

		/* The MIDPSVGCanvas bounds */
		int x,y,width,height;

		/* Contructor a new MIDPSVGCanvas */
    public ViewerCanvas(Display display)
    {
				this.display = display;

				width = getWidth();
        height = getHeight();

        // Creates the SVG raster
        TinyPixbuf	buffer = new TinyPixbuf(width, height);
        raster = new SVGRaster(buffer);
				imageProducer = new MIDPSVGImageProducer(raster);
        imageProducer.setConsumer(this);
				raster.setSVGImageProducer(imageProducer);

				SVGImageElem.setImageLoader(this);
				raster.setAntialiased(true);
    }

    /**
     * Inits this canvas
		 */
		void init()
		{
				try
        {
					   wait    = Image.createImage("/tinyline/wait.png");
			       // Load svg font
						 load = false;
						 // ? More or less the same time
						 //   Faster to use HelveticaFont class
						 // HelveticaFont.getFont();
        }
        catch(Exception e)
        {
						 alertError("Resources (helvetica.svg and/or icons) could not be loaded!");
        }
		}

    /**
     * Delivers the pixels of the image. The pixel (px,py) is
		 * stored in the pixels array at index (px * scansize + py + off).
     * @param x,&nbsp;y the coordinates of the upper-left corner of the
     *        area of pixels to be set
     * @param w the width of the area of pixels
     * @param h the height of the area of pixels
		 * @see  ImageConsumer
     */
    public void newPixels(int x, int y, int w, int h)
		{
				repaint(x,y,w,h);
				// paint it now!
				serviceRepaints();
		}

    /**
     * Loads <tt>TinyBitmap</tt> raster image.
     * @param imgRef The raster image URL.
     * @return The raster image.
		 * @ see ImageLoader Interface
     */
		public TinyBitmap createTinyBitmap(TinyString uri)
		{
        String imgRef = new String(uri.data);
				TinyBitmap bitmap = new TinyBitmap();
        try
        {
					 if(imgRef.startsWith(".."))
					 {
							 // This is relative path, then attach the basePath
							 int p = currentURL.lastIndexOf('/');
							 if(p!=-1)
							 {
									 imgRef = currentURL.substring(0,p)+'/' + imgRef;
// System.out.println("imgRef "+imgRef);
							 }
							 else
							 {
									 return null;
							 }
					 }
					 Image image  = createImage(imgRef);
           bitmap.width  = image.getWidth();
           bitmap.height = image.getHeight();

					 // Grap bits
           bitmap.pixels32 = new int[bitmap.width * bitmap.height];
           image.getRGB(bitmap.pixels32, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height);
        }
        catch (Throwable thr)
        {
			 //	 	 alertError(imgRef + " image could not be loaded.");
					 return null;
        }
				return bitmap;
		}

    /**
     * Loads <tt> TinyBitmap </tt> raster image.
		 * @param imageData     The input image data buffer.
		 * @param imageOffset   The input image data buffer pointer.
		 * @param imageLength   The input image data buffer length.
     * @return The raster image.
		 * @ see ImageLoader Interface
     */
		public TinyBitmap createTinyBitmap(byte[] imageData, int imageOffset, int imageLength)
		{
				TinyBitmap bitmap = new TinyBitmap();
        try
        {
					 Image image  =  Image.createImage(imageData, imageOffset, imageLength);
           bitmap.width  = image.getWidth();
           bitmap.height = image.getHeight();

					 // Grap bits
           bitmap.pixels32 = new int[bitmap.width * bitmap.height];
           image.getRGB(bitmap.pixels32, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height);
        }
        catch (Throwable thr)
        {
			 //	 	 alertError(imgRef + " image could not be loaded.");
					 return null;
        }
				return bitmap;
		}


    /**
     * Loads and dispalys an SVG document from the given URL.
     * @param url The SVG document URL.
		 * @ see LinkWalker Interface
     */

    public void goURL(String url)
    {
				currentURL = url;
        SVGDocument document = loadSVG(currentURL);
        raster.setSVGDocument(document);
        raster.invalidate();
        raster.update();
        raster.sendPixels();
		}


    /**
     * Loads an SVG document.
     * @param url The SVG document URL.
     * @return The loaded document.
     */
		public SVGDocument loadSVG(String url)
		{
        System.out.println(""+url);
				load = true;
        repaint(0, height, getWidth(), MENU_HEIGHT);

        SVGDocument doc = raster.createSVGDocument();
        ContentConnection c = null;
        InputStream is = null;
 	      Runtime.getRuntime().gc();
			  try
			  {
            if (url.startsWith("/"))
            {
                is = getClass().getResourceAsStream(url);
            }
            else if (url.startsWith("http:"))
            {
                c = (ContentConnection)Connector.open(url);
                is = c.openInputStream();
                if(url.endsWith("svgz"))
                {
                    is = new GZIPInputStream(is);
                }
						}
						else
						{
						    alertError("Wrong URL "+ url);
				        load = false;
								return doc; // The stream is not open so it is safe to return
						}
						// Read and parse the stream
						// Reads and parses the stream
            TinyPixbuf pixbuf = raster.getPixelBuffer();
            SVGAttr attrParser = new SVGAttr(pixbuf.width, pixbuf.height);
            SVGParser parser = new SVGParser(attrParser);
            parser.load(doc,is);
            load = true;
				}
				catch( IOException ioe)
				{
						doc = null;
						alertError(ioe.getMessage() );
				}
        catch(OutOfMemoryError memerror)
        {
						doc = null;
					  alertError("Not enought memory");
						Runtime.getRuntime().gc();
        }
				catch( Throwable thr)
				{
						doc = null;
						alertError("Not in SVGT format");
				}
        finally
        {
			      try
			      {
               if (is != null) is.close();
               if (c != null) c.close();
						}
				    catch( IOException ioe) {}
        }
				load = false;
				return doc;
		}


    /**
     * Draws the canvas
     * @param g The Graphics surface.
     */
    protected void paint(Graphics g)
    {
					// pixels
					if(!load)
					{
             TinyPixbuf pixbuf = raster.getPixelBuffer();

					// NOKIA UI Series 60
/*
					com.nokia.mid.ui.DirectGraphics dg = com.nokia.mid.ui.DirectUtils.getDirectGraphics(g);
					dg.drawPixels(renderer.getPixels32(),false,
               0,
               renderer.width,
               0,
               0,
               renderer.width,
               renderer.height,
               0,
               com.nokia.mid.ui.DirectGraphics.TYPE_INT_8888_ARGB);
*/
/**/

					// MIDP2.0
					g.drawRGB(pixbuf.pixels32,
               0,
               pixbuf.width,
               0,
               0,
               pixbuf.width,
               pixbuf.height,
               false);
 /**/
					}
					else //if(load)
					{
					   //draw the clock
						 g.setColor(0xffffff);
						 g.fillRect(0,0,width,height);
						 g.setColor(0x000000);
	           g.drawString("Wait ...", width/2, height/2,  Graphics.LEFT|Graphics.TOP);
					   if(wait!=null)
					   {
					      g.drawImage(wait,0,height-MENU_HEIGHT,
						                g.TOP|g.LEFT);
					   }
					}
		}


    /**
     * Fetch the image.  If the name begins with "http:"
     * fetch it with connector.open and http.
     * If it starts with "/" then load it from the
     * resource file.
     * @param      name of the image to load
     * @return     image created
     * @exception  IOException if errors occuring doing loading
     */
    private Image createImage(String name) throws IOException
    {
        if (name.startsWith("/"))
        {

						// Load as a resource with Image.createImage
						if(name.endsWith(".png"))
						{
                return Image.createImage(name);
						}
						else
                throw new IOException("Expecting PNG image");

        }
        else if (name.startsWith("http:"))
        {
            // Load from a ContentConnection
            HttpConnection c = null;
            DataInputStream is = null;
            try
            {
               c = (HttpConnection)Connector.open(name);
               int status = c.getResponseCode();
               if (status != 200)
               {
                  throw new IOException("HTTP Response Code = " + status);
               }

               int len = (int)c.getLength();
               String type = c.getType();
               if (!type.equals("image/png"))
               {
                  throw new IOException("Expecting image, received " + type);
               }

               if (len > 0)
               {
                   is = c.openDataInputStream();
                   byte[] data = new byte[len];
                   is.readFully(data);
                   return Image.createImage(data, 0, len);
               }
               else
               {
                  throw new IOException("Content length is missing");
               }
            }
            finally
            {
               if (is != null)
                    is.close();
               if (c != null)
                    c.close();
            }
        }
        else
        {
            throw new IOException("Unsupported media");
        }
    }

    /**
		 * Display error message.
		 * @param message the error message
		 */
    void alertError(String message)
    {
        Alert alert = new Alert("Error", message, null, AlertType.ERROR);
        Displayable current = display.getCurrent();
        if (!(current instanceof Alert))
        {
						alert.setTimeout(Alert.FOREVER);
            // This next call can't be done when current is an Alert
            display.setCurrent(alert, current);
        }
    }


  /**
   * Handle keyboard input.
   * @param keyCode pressed key.
   */
  protected void keyRepeated(int keyCode)
  {
		 keyPressed(keyCode);
	}

	/**
   * Handle keyboard input.
   * @param keyCode pressed key.
   */
  protected void keyPressed(int keyCode)
  {
			if(load) return;
			int action = getGameAction(keyCode);
			switch (action)
			{
			     case Canvas.LEFT:
								if(type == TYPE_PAN)
                   pan(-PAN_STEP,0);
 	              break;

					 case Canvas.RIGHT:
								if(type == TYPE_PAN)
                   pan(PAN_STEP,0);
 	              break;

					 case Canvas.UP:
								if(type == TYPE_PAN)
                   pan(0,-PAN_STEP);
								else if(type == TYPE_ZOOM)
									 zoom(0);
 	              break;
			     case Canvas.DOWN:
								if(type == TYPE_PAN)
                   pan(0,PAN_STEP);
								else if(type == TYPE_ZOOM)
									 zoom(1);
 	              break;
	         case Canvas.FIRE:
								break;
			} // end of switch
  }

  /**
   * Called when the pointer is released.
   * @param x - the X coordinate where the pointer was released.
   * @param y - the Y coordinate where the pointer was released.
   */
  protected void pointerReleased(int x, int y)
  {
			 if(load) return;
		   if(type == TYPE_LINK)
		   {
			     pointerReleased(x,y);
		   }
	     else if(type == TYPE_PAN)
	     {
	         pan(pressedX - x,pressedY - y);
	     }
	}

  /**
   * Called when the pointer is pressed.
   * @param x - the X coordinate where the pointer was pressed.
   * @param y - the Y coordinate where the pointer was pressed.
   */
  protected void pointerPressed(int x, int y)
  {
		   if(load) return;
	     if(type == TYPE_PAN)
	     {
           pressedX = x;
           pressedY = y;
           draggedX = pressedX;
           draggedY = pressedY;
		   }
	}

  /**
   * Called when the pointer is dragged.
   * @param x - the X coordinate where the pointer was dragged.
   * @param y - the Y coordinate where the pointer was dragged.
   */
	protected void pointerDragged(int x, int y)
  {
		   if(load) return;
	     if(type == TYPE_PAN)
	     {
           draggedX = x;
           draggedY = y;
		   }
	}


	/**
   *  Zooms in and out the current SVGT document.
   *  @return true if the zoom is allowed; otherwise return false.
   */
  public boolean zoom(int direction)
  {
      // zoom in '0' size / 2
      if(direction == 0)
      {
          zoomLevel--;
          if(zoomLevel < MIN_ZOOMLEVEL)
          {
             zoomLevel = MIN_ZOOMLEVEL;
             return false;
          }
      }
      else //zoom out size * 2
      {
          zoomLevel++;
          if(zoomLevel > MAX_ZOOMLEVEL)

					{

					    zoomLevel = MAX_ZOOMLEVEL;

							return false;

					}

			}

      SVGRect newView = new SVGRect();
      SVGRect view = raster.view;
      int  midX = view.x + view.width/2;
      int  midY = view.y + view.height/2;
      // zoom in '0' size / 2
      if(direction == 0)
      {
          newView.width = (view.width/2);
          newView.height = (view.height/2);
      }
      else //zoom out size * 2
      {
          newView.width = (view.width * 2 );
          newView.height = (view.height * 2);
      }
      newView.x = midX - (newView.width) / 2;
      newView.y = midY - (newView.height) / 2;

			// Set a new current viewport
      view.x = newView.x;
      view.y = newView.y;
      view.width = newView.width;
      view.height = newView.height;

			// Change the camera transform according to the new current
			// viewport and update the raster
      raster.setCamera();
      raster.update();
      raster.sendPixels();

      return true;
  }

   /**
    *  Returns the current SVGT document to its original view.

	  */

	 public void origView()

	 {

			// Reset the zoom level
			zoomLevel = 0;

			// Set a new current viewport
			raster.view = new SVGRect(raster.origview);

			// Change the camera transform according to the new current
			// viewport and update the raster
      raster.setCamera();
      raster.update();
      raster.sendPixels();
	 }


	 /**
    * Pans the current SVGT document.

	  * @param x The distance on X coordinate.

	  * @param y The distance on Y coordinate.

	  */

	 public void pan(int x , int y)

	 {

			// Get the current viewport
			SVGRect view = raster.view;
      // Get the SVGT document
      SVGDocument doc = raster.getSVGDocument();
      // Get the root of the SVGT document
			SVGSVGElem root = (SVGSVGElem)doc.root;
      // Get the current scale value
			int scale = root.getCurrentScale();
			// Scale pan distances according to the current scale factor
			// Change the current viewport
      view.x += TinyUtil.div(x<<TinyUtil.FIX_BITS,scale);

			view.y += TinyUtil.div(y<<TinyUtil.FIX_BITS,scale);

			// Change the camera transform according to the new current
			// viewport and update the raster
      raster.setCamera();
      raster.update();
      raster.sendPixels();
	 }


}
