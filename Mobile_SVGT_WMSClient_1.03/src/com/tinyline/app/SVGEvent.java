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

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

/**
 * The <tt>SVGEvent</tt> class is used to provide contextual information about
 * an event to the handler processing the event.
 * <p>
 * SVGT 1.1 only allows interactivity with declarative animation. The list of
 * event-symbols available for a given event-base element is the list of event
 * attributes available for the given element as defined by the SVG DTD, with
 * the one difference that the leading 'on' is removed from the event name
 * (i.e., the animation event name is 'click', not 'onclick').
 * <p>
 * 
 * @author Andrew Girow
 * @version 1.9
 */
public class SVGEvent implements Event {

	/** The Event Names */
	public static String[] EVENT_NAMES = { "animate", "begin", "click", "end",
			"error", "focushide", "focusin", "focusnext", "focusout",
			"focuspressed", "focusprev", "focusshow", "load", "origview",
			"pauseresume", "quality", "repeat", "scroll", "unload", "update",
			"zoom" };

	/** Internal Event. Occurs when an element is animated. */
	public static final int EVENT_ANIMATE = 0;

	/** Occurs when an animation element begins. */
	public static final int EVENT_BEGIN = 1;

	/** Occurs when the pointing device button is clicked over an element. */
	public static final int EVENT_CLICK = 2;

	/** Occurs when an animation element ends. */
	public static final int EVENT_END = 3;

	/**
	 * The error event occurs when an element does not load properly or when an
	 * error occurs during script execution.
	 */
	public static final int EVENT_ERROR = 4;

	/** Occurs when a list of linked elements is being hided. */
	public static final int EVENT_FOCUSHIDE = 5;

	/** Occurs when an element receives focus. */
	public static final int EVENT_FOCUSIN = 6;

	/** Occurs when an focus is being shifted to the next linked element. */
	public static final int EVENT_FOCUSNEXT = 7;

	/** Occurs when an element loses focus. */
	public static final int EVENT_FOCUSOUT = 8;

	/** Occurs when an focus is being shifted to the previous linked element. */
	public static final int EVENT_FOCUSPRESSED = 9;

	/** Occurs when an focus is being shifted to the previous linked element. */
	public static final int EVENT_FOCUSPREV = 10;

	/** Occurs when a list of linked elements is being showed. */
	public static final int EVENT_FOCUSSHOW = 11;

	/**
	 * The event is triggered at the point at which the user agent has fully
	 * parsed the element and its descendants and is ready to act appropriately
	 * upon that element, such as being ready to render the element to the
	 * target device.
	 */
	public static final int EVENT_LOAD = 12;

	/** Occurs when a document view is being returned to the original one. */
	public static final int EVENT_ORIGVIEW = 13;

	/** Occurs when animations are being paused or resumed. */
	public static final int EVENT_PAUSERESUME = 14;

	/** Occurs when an antialising is being switched on or off. */
	public static final int EVENT_QUALITY = 15;

	/** Occurs when an animation element repeats. */
	public static final int EVENT_REPEAT = 16;

	/**
	 * Occurs when a document view is being shifted along the X or Y or both
	 * axis
	 */
	public static final int EVENT_SCROLL = 17;

	/**
	 * The unload event occurs when the DOM implementation removes a document
	 * from a window or frame.
	 */
	public static final int EVENT_UNLOAD = 18;

	/** Internal Event. Occurs when an element is updated. */
	public static final int EVENT_UPDATE = 19;

	/** Occurs when the zoom level of a document view is being changed. */
	public static final int EVENT_ZOOM = 20;

	/** The unknown event. */
	public static final int EVENT_UNKNOWN = 21;

	/** <b>uDOM:</b> Returns the event type */
	public String getType() {
		if (id >= EVENT_UNKNOWN) {
			return "unknown";
		}
		return EVENT_NAMES[id];
	}

	/**
	 * <b>uDOM:</b> Returns the current EventTarget to which the event was
	 * originally dispatched.
	 */
	public EventTarget getCurrentTarget() {
		return eventTarget;
	}

	/**
	 * Creates a new <tt>SVGEvent</tt> object.
	 * 
	 * @param id
	 *            The event type.
	 * @param data
	 *            The event data.
	 */
	public SVGEvent(int id, Object data) {
		this.id = id;
		this.data = data;
	}

	/** The event type */
	public int id;

	/** The event data */
	public Object data;

	/** The event target */
	EventTarget eventTarget;

	/** The next event */
	SVGEvent next;
}
