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

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.util.*;

import com.tinyline.tiny2d.*;
import com.tinyline.svg.*;
import com.tinyline.util.GZIPInputStream;
import javax.microedition.lcdui.Image;

/**
 * This class represents a TinyLine SVG Canvas for MIDP 2.0.
 * 
 * @author <a href="mailto:andrewgirow@yahoo.com">Andrew Girow</a>
 * @version 1.9
 */

public class MIDPSVGCanvas extends Canvas implements Runnable, ImageConsumer,
		ImageLoader, org.w3c.dom.events.EventTarget {

	/** The SVG renderer */
	public SVGRaster raster;

	MIDPSVGImageProducer imageProducer;

	/** The events queue */
	SVGEventQueue eventQueue;

	/** The events dispatching thread */
	Thread thread;

	/** The events listeners */
	TinyVector listeners;

	/* The clock image */
	Image wait;

	/* The current SVG document URL */
	String currentURL = "";

	/* The current loading status */
	boolean load = true;

	/* The current display */
	Display display;

	// Records data structure
	Vector bookmarks;

	List bookmarkList;

	/* The image cash */
	Hashtable imageCash;

	/* The UI modes */
	final static int MODE_NEXTPREV = 0; // Navigation mode === Default mode

	final static int MODE_PAN = 1; // Pan mode

	final static int MODE_LINK = 2; // Link mode

	final static int MODE_ZOOM = 3; // Zoom mode

	final static int MODE_MAXCOUNT = 4; // MAX

	/* The mode index */
	int index;

	/* The current mode */
	int mode = MODE_NEXTPREV;

	/* The pointer device data */
	int pressedX;

	int pressedY;

	int draggedX;

	int draggedY;

	static final int PAN_STEP = 4;

	static final int MENU_HEIGHT = 18;

	int x, y, width, height;

	/* Contructor a new MIDPSVGCanvas */
	public MIDPSVGCanvas(Display display) {
		this.display = display;
		width = getWidth();
		height = getHeight();

		// Creates the SVG raster
		TinyPixbuf buffer = new TinyPixbuf(width, height);
		raster = new SVGRaster(buffer);
		imageProducer = new MIDPSVGImageProducer(raster);
		imageProducer.setConsumer(this);
		raster.setSVGImageProducer(imageProducer);

		// Sets the ImageLoader implementation needed for
		// loading bitmaps
		SVGImageElem.setImageLoader(this);
		// Uncomment the following line for full antialiasing
		raster.setAntialiased(true);
		// Creates the event queue and listeners array.
		eventQueue = new SVGEventQueue();
		listeners = new TinyVector(4);
		// Creates the image cash.
		imageCash = new Hashtable();
	}

	/** Starts the events dispatching thread */
	public synchronized void start() {
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	/** Stops the event dispatching thread */
	public synchronized void stop() {
		thread = null;
	}

	/**
	 * The events dispatching thread run()
	 */
	public void run() {
		Thread currentThread = Thread.currentThread();
		try {
			while (currentThread == thread) {
				eventQueue.handleEvent(eventQueue.getNextEvent());
				try {
					Thread.currentThread().sleep(50);
				} catch (InterruptedException e) {
				}
				Thread.currentThread().yield();
			}
		} catch (Throwable thr) {
			thr.printStackTrace();
			alertError("Internal Error");
		}
	}

	/**
	 * Loads and dispalys an SVGT document from the given URL. External
	 * hyperlinks handling
	 */
	synchronized public void goURL(String url) {
		SVGEvent event = new SVGEvent(SVGEvent.EVENT_LOAD, url);
		postEvent(event);
	}

	/**
	 * Returns the current SVGT document to its original view.
	 */
	public void origView() {
		SVGEvent event = new SVGEvent(SVGEvent.EVENT_ORIGVIEW, null);
		postEvent(event);
	}

	/**
	 * Switches the rendering quality.
	 */
	public void switchQuality() {
		SVGEvent event = new SVGEvent(SVGEvent.EVENT_QUALITY, null);
		postEvent(event);
	}

	/**
	 * Suspends or unsuspends all animations that are defined within the current
	 * SVGT document fragment.
	 */
	public void pauseResumeAnimations() {
		SVGEvent event = new SVGEvent(SVGEvent.EVENT_PAUSERESUME, null);
		postEvent(event);
	}

	/**
	 * Inits this canvas. Loads icons
	 */
	public void init() {
		try {
			wait = Image.createImage("/tinyline/wait.png");
			// Load svg font
			load = false;
			// ? More or less the same time
			// Faster to use HelveticaFont class
			// HelveticaFont.getFont();
		} catch (Exception e) {
			alertError("Resources (helvetica.svg and/or icons) could not be loaded!");
		}
	}

	/**
	 * Delivers the pixels of the image. The pixel (px,py) is stored in the
	 * pixels array at index (px * scansize + py + off).
	 * 
	 * @param x,&nbsp;y
	 *            the coordinates of the upper-left corner of the area of pixels
	 *            to be set
	 * @param w
	 *            the width of the area of pixels
	 * @param h
	 *            the height of the area of pixels
	 * @see ImageConsumer
	 */
	public void newPixels(int x, int y, int w, int h) {
		repaint(x, y, w, h);
		// paint it now!
		serviceRepaints();
	}

	/**
	 * Returns a TinyBitmap for the given image URL or path.
	 * 
	 * @param imgRef
	 *            The image URL or path.
	 * @return a TinyBitmap object which gets its pixel data from the specified
	 *         URL or path.
	 */
	public TinyBitmap createTinyBitmap(TinyString uri) {
		String imgRef = new String(uri.data);
		TinyBitmap bitmap = null;
		if (imgRef.startsWith("..") || !imgRef.startsWith("http:")) {
			// This is relative path, then attach the basePath
			int p = currentURL.lastIndexOf('/');
			if (p != -1) {
				imgRef = currentURL.substring(0, p) + '/' + imgRef;
				// System.out.println("imgRef "+imgRef);
			} else {
				return null;
			}
		}
		try {
			// check in the cash
			bitmap = (TinyBitmap) imageCash.get(imgRef);
			// not found
			if (bitmap == null) {
				Image image = createImage(imgRef);
				bitmap = new TinyBitmap();
				bitmap.width = image.getWidth();
				bitmap.height = image.getHeight();

				// Grap bits
				bitmap.pixels32 = new int[bitmap.width * bitmap.height];
				image.getRGB(bitmap.pixels32, 0, bitmap.width, 0, 0,
						bitmap.width, bitmap.height);
				imageCash.put(imgRef, bitmap);
			}
		} catch (Exception ex) {
		}
		return bitmap;
	}

	/**
	 * Loads <tt> TinyBitmap </tt> raster image.
	 * 
	 * @param imageData
	 *            The input image data buffer.
	 * @param imageOffset
	 *            The input image data buffer pointer.
	 * @param imageLength
	 *            The input image data buffer length.
	 * @return The raster image. @ see ImageLoader Interface
	 */
	public TinyBitmap createTinyBitmap(byte[] imageData, int imageOffset,
			int imageLength) {
		TinyBitmap bitmap = new TinyBitmap();
		try {
			Image image = Image
					.createImage(imageData, imageOffset, imageLength);
			bitmap.width = image.getWidth();
			bitmap.height = image.getHeight();

			// Grap bits
			bitmap.pixels32 = new int[bitmap.width * bitmap.height];
			image.getRGB(bitmap.pixels32, 0, bitmap.width, 0, 0, bitmap.width,
					bitmap.height);
		} catch (Throwable thr) {
			// alertError(imgRef + " image could not be loaded.");
			return null;
		}
		return bitmap;
	}

	/**
	 * Selects mode.
	 * 
	 * @param newmode
	 *            The new mode id.
	 */
	void selectMode(int newmode) {
		if (mode == MODE_LINK) {
			SVGEvent event = new SVGEvent(SVGEvent.EVENT_FOCUSHIDE, null);
			postEvent(event);
		}
		mode = newmode;
		if (mode == MODE_LINK) {
			SVGEvent event = new SVGEvent(SVGEvent.EVENT_FOCUSSHOW, null);
			postEvent(event);
		}
	}

	/**
	 * Loads an SVG document.
	 * 
	 * @param url
	 *            The SVG document URL.
	 * @return The loaded document.
	 */
	public SVGDocument loadSVG(String url) {
		System.out.println("" + url);
		load = true;
		repaint(0, height, getWidth(), MENU_HEIGHT);

		SVGDocument doc = raster.createSVGDocument();
		ContentConnection c = null;
		InputStream is = null;
		Runtime.getRuntime().gc();
		try {
			if (url.startsWith("/")) {
				is = getClass().getResourceAsStream(url);
			} else if (url.startsWith("http:")) {
				c = (ContentConnection) Connector.open(url);
				is = c.openInputStream();
				if (url.endsWith("svgz")) {
					is = new GZIPInputStream(is);
				}
			} else {
				alertError("Wrong URL " + url);
				load = false;
				return doc; // The stream is not open so it is safe to return
			}
			// Read and parse the SVGT stream
			TinyPixbuf pixbuf = raster.getPixelBuffer();
			// Create the SVGT attributes parser
			SVGAttr attrParser = new SVGAttr(pixbuf.width, pixbuf.height);
			// Create the SVGT stream parser
			SVGParser parser = new SVGParser(attrParser);
			// Parse the input SVGT stream parser into the document
			parser.load(doc, is);
			load = true;
		} catch (IOException ioe) {
			doc = null;
			alertError(ioe.getMessage());
		} catch (OutOfMemoryError memerror) {
			doc = null;
			alertError("Not enought memory");
			Runtime.getRuntime().gc();
		} catch (Throwable thr) {
			doc = null;
			alertError("Not in SVGT format");
		} finally {
			try {
				if (is != null)
					is.close();
				if (c != null)
					c.close();
			} catch (IOException ioe) {
			}
		}
		load = false;
		return doc;
	}

	/**
	 * Draws the canvas
	 * 
	 * @param g
	 *            The Graphics surface.
	 */
	protected void paint(Graphics g) {
		// pixels
		if (!load) {
			TinyPixbuf pixbuf = raster.getPixelBuffer();

			// NOKIA UI Series 60
			/*
			 * com.nokia.mid.ui.DirectGraphics dg =
			 * com.nokia.mid.ui.DirectUtils.getDirectGraphics(g); dg.drawPixels(
			 * pixbuf.pixels32,false, 0, pixbuf.width, 0, 0, pixbuf.width,
			 * pixbuf.height, 0,
			 * com.nokia.mid.ui.DirectGraphics.TYPE_INT_8888_ARGB);
			 */
			/**/

			// MIDP2.0
			g.drawRGB(pixbuf.pixels32, 0, pixbuf.width, 0, 0, pixbuf.width,
					pixbuf.height, false);
			/**/
		} else // if(load)
		{
			// draw the clock
			g.setColor(0xffffff);
			g.fillRect(0, 0, width, height);
			g.setColor(0x000000);
			g.drawString("Wait ...", width / 2, height / 2, Graphics.LEFT
					| Graphics.TOP);
			if (wait != null) {
				g.drawImage(wait, 0, height - MENU_HEIGHT, g.TOP | g.LEFT);
			}
		}
	}

	/**
	 * Handle keyboard input.
	 * 
	 * @param keyCode
	 *            pressed key.
	 */
	protected void keyRepeated(int keyCode) {
		keyPressed(keyCode);
	}

	/**
	 * Handle keyboard input.
	 * 
	 * @param keyCode
	 *            pressed key.
	 */
	protected void keyPressed(int keyCode) {
		if (load)
			return;
		int action = getGameAction(keyCode);
		SVGEvent event = null;
		switch (action) {
		case Canvas.LEFT:
			if (mode == MODE_LINK) {
				event = new SVGEvent(SVGEvent.EVENT_FOCUSPREV, null);
				postEvent(event);
			} else if (mode == MODE_PAN) {
				event = new SVGEvent(SVGEvent.EVENT_SCROLL, new TinyPoint(
						-PAN_STEP, 0));
				postEvent(event);
			} else if (mode == MODE_NEXTPREV) {
				previous();
			}
			break;

		case Canvas.RIGHT:
			if (mode == MODE_LINK) {
				event = new SVGEvent(SVGEvent.EVENT_FOCUSNEXT, null);
				postEvent(event);
			} else if (mode == MODE_PAN) {
				event = new SVGEvent(SVGEvent.EVENT_SCROLL, new TinyPoint(
						PAN_STEP, 0));
				postEvent(event);
			} else if (mode == MODE_NEXTPREV) {
				next();
			}
			break;

		case Canvas.UP:
			if (mode == MODE_LINK) {
				event = new SVGEvent(SVGEvent.EVENT_FOCUSPREV, null);
				postEvent(event);
			} else if (mode == MODE_PAN) {
				event = new SVGEvent(SVGEvent.EVENT_SCROLL, new TinyPoint(0,
						-PAN_STEP));
				postEvent(event);
			} else if (mode == MODE_ZOOM) {
				event = new SVGEvent(SVGEvent.EVENT_ZOOM, new TinyNumber(0));
				postEvent(event);
			}
			break;
		case Canvas.DOWN:
			if (mode == MODE_LINK) {
				event = new SVGEvent(SVGEvent.EVENT_FOCUSNEXT, null);
				postEvent(event);
			} else if (mode == MODE_PAN) {
				event = new SVGEvent(SVGEvent.EVENT_SCROLL, new TinyPoint(0,
						PAN_STEP));
				postEvent(event);
			} else if (mode == MODE_ZOOM) {
				event = new SVGEvent(SVGEvent.EVENT_ZOOM, new TinyNumber(1));
				postEvent(event);
			}
			break;
		case Canvas.FIRE:
			if (mode == MODE_LINK) {
				event = new SVGEvent(SVGEvent.EVENT_FOCUSPRESSED, null);
				postEvent(event);
			}
			break;
		} // end of switch
	}

	/**
	 * Go to the given image and update controls.
	 * 
	 * @param i
	 *            index of the given image.
	 */
	void go(int i) {
		index = i;
		goImpl(index);
	}

	/**
	 * The go implemetation.
	 * 
	 * @param i
	 *            index of the given image.
	 */
	private void goImpl(int i) {
		if ((bookmarkList == null) || (bookmarks == null))
			return;
		// update the List seletced position
		bookmarkList.setSelectedIndex(i, true);
		Bookmark bookmark = (Bookmark) bookmarks.elementAt(i);
		if (bookmark == null)
			return;
		goURL(bookmark.url);
	}

	/**
	 * Advance to the next image and wrap around if necessary.
	 */
	void next() {
		if (bookmarks == null)
			return;
		if (index == bookmarks.size() - 1) {
			return;
		}
		index++;
		goImpl(index);
	}

	/**
	 * Back up to the previous image. Wrap around to the end if at the
	 * beginning.
	 */
	void previous() {
		if (index == 0) {
			return;
		}
		index--;
		goImpl(index);
	}

	/**
	 * Fetch the image. If the name begins with "http:" fetch it with
	 * connector.open and http. If it starts with "/" then load it from the
	 * resource file.
	 * 
	 * @param name
	 *            of the image to load
	 * @return image created
	 * @exception IOException
	 *                if errors occuring doing loading
	 */
	private Image createImage(String name) throws IOException {
		if (name.startsWith("/")) {

			// Load as a resource with Image.createImage
			if (name.endsWith(".png")) {
				return Image.createImage(name);
			} else
				throw new IOException("Expecting PNG image");

		} else if (name.startsWith("http:")) {
			// Load from a ContentConnection
			HttpConnection c = null;
			DataInputStream is = null;
			try {
				c = (HttpConnection) Connector.open(name);
				int status = c.getResponseCode();
				if (status != 200) {
					throw new IOException("HTTP Response Code = " + status);
				}

				int len = (int) c.getLength();
				String type = c.getType();
				if (!type.equals("image/png")) {
					throw new IOException("Expecting image, received " + type);
				}

				if (len > 0) {
					is = c.openDataInputStream();
					byte[] data = new byte[len];
					is.readFully(data);
					return Image.createImage(data, 0, len);
				} else {
					throw new IOException("Content length is missing");
				}
			} finally {
				if (is != null)
					is.close();
				if (c != null)
					c.close();
			}
		} else {
			throw new IOException("Unsupported media");
		}
	}

	// ////////////////////////////////////////////////////////////////

	/**
	 * Posts an event to the event queue.
	 * 
	 * @param theEvent
	 *            an instance of Event, or a subclass of it.
	 */
	public synchronized void postEvent(SVGEvent theEvent) {
		// IMPORTANT
		theEvent.eventTarget = this;
		eventQueue.postEvent(theEvent);
	}

	/*
	 * Methods inherited from interface org.w3c.dom.events.EventTarget
	 */
	/**
	 * 
	 * <b>uDOM:</b> This method allows the registration of event listeners on
	 * the event target.
	 * 
	 * @param type
	 *            The event type for which the user is registering
	 * 
	 * @param listener
	 *            The listener parameter takes an interface implemented by the
	 *            user which contains the methods to be called when the event
	 *            occurs.
	 * 
	 * @param useCapture
	 *            If true, useCapture indicates that the user wishes to initiate
	 *            capture. After initiating capture, all events of the specified
	 *            type will be dispatched to the registered EventListener before
	 *            being dispatched to any EventTargets beneath them in the tree.
	 *            Events which are bubbling upward through the tree will not
	 *            trigger an EventListener designated to use capture.
	 */
	public void addEventListener(java.lang.String type,
			org.w3c.dom.events.EventListener listener, boolean useCapture) {
		listeners.addElement(listener);
	}

	/**
	 * 
	 * <b>uDOM:</b> This method allows the removal of event listeners from the
	 * event target.
	 * 
	 * @param type
	 *            Specifies the event type of the EventListener being removed.
	 * @param listener
	 *            The listener parameter indicates the EventListener to be
	 *            removed.
	 * @param useCapture
	 *            Specifies whether the EventListener being removed was
	 *            registered as a capturing listener or not.
	 */
	public void removeEventListener(java.lang.String type,
			org.w3c.dom.events.EventListener listener, boolean useCapture) {
		int i = listeners.indexOf(listener, 0);
		if (i > 0) {
			listeners.removeElementAt(i);
		}
	}

	/**
	 * <b>uDOM:</b> This method allows the dispatch of events into the
	 * implementations event model. Events dispatched in this manner will have
	 * the same behavior as events dispatched directly by the implementation.
	 * 
	 * @param evt
	 *            Specifies the event type, behavior, and contextual information
	 *            to be used in processing the event.
	 * @return The return value of dispatchEvent indicates whether any of the
	 *         listeners which handled the event called preventDefault. If
	 *         preventDefault was called the value is false, else the value is
	 *         true.
	 */
	public boolean dispatchEvent(org.w3c.dom.events.Event evt) {
		org.w3c.dom.events.EventListener h;
		for (int i = 0; i < listeners.count; i++) {
			h = (org.w3c.dom.events.EventListener) listeners.data[i];
			if (h != null)
				h.handleEvent(evt);
		}
		return true;
	}

	/**
	 * Display error message.
	 * 
	 * @param message
	 *            the error message
	 */
	void alertError(String message) {
		Alert alert = new Alert("Error", message, null, AlertType.ERROR);
		Displayable current = display.getCurrent();
		if (!(current instanceof Alert)) {
			alert.setTimeout(Alert.FOREVER);
			// This next call can't be done when current is an Alert
			display.setCurrent(alert, current);
		}
	}

}
