/*
 * Created on 2005-2-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package MIDlet;

import java.io.IOException;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.*;

import GUI.CONTROL.GUIController;

/**
 * @author leeglanz
 * 
 * TODO 
 */
public class MobileSVGTWMSClient extends MIDlet {
	private Display display;

	private static GUIController controller;

	private Image icon_start = null;

	private Alert startAlert;

	public boolean B_start = false;

	/**
	 * default constructor
	 */
	public MobileSVGTWMSClient() {
		super();
		display = Display.getDisplay(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {

		try {
			controller = new GUIController(this);
			// Initialize RecordStore
			controller.init();
		} catch (Exception e) {
			System.out.println("When initializing Exception happens:" + e.getMessage());
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		this.notifyPaused();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		controller = null;

	}

	public void setCurrent(Displayable disp) {
		display.setCurrent(disp);
	}

	public void setCurrent(Alert alert, Displayable disp) {
		display.setCurrent(alert, disp);
	}

	public Displayable getCurrent() {
		return display.getCurrent();
	}

	public void exit(boolean arg0) {
		try {
			destroyApp(arg0);
			notifyDestroyed();
		} catch (MIDletStateChangeException e) {
			//
		}
	}
}
