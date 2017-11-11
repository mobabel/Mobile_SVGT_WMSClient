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
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.Vector;
import javax.microedition.rms.*;

import com.tinyline.tiny2d.*;
import com.tinyline.svg.*;

/**
 * The <tt>TinyLine</tt> is the J2ME MIDP 2.0
 * implementation of the SVGT Viewer.
 * <p>
 * @author (C) Andrew Girow
 * @version 1.9
 * <p>
 */
public class TinyLine extends MIDlet implements CommandListener, RecordComparator
{

    Display display;

		// The Main screen
		// SVG Canvas
    MIDPSVGCanvas canvas;
    Command openCommand, nextPrevCommand, linkCommand,
    panCommand, zoomCommand, origViewCommand, qualityCommand, pauseCommand,
    helpCommand, exitCommand;

    // Help screen
    Form helpScreen;
    Command helpBackCommand;

    //  Edit Bookmark screen
    Form editForm;
    TextField nameField, URLField;
    Command editBackCommand, saveCommand;
		// This is the bookmark to be edited
		Bookmark editBookmark;

		//  Bookmarks screen
    Alert alert;     // user interface alert for Bookmarks
		List bookmarkList;
    Command addCommand, editCommand, deleteCommand, defaultsCommand;
		Command listBackCommand;
		// Name of the record store
		String recordStoreName;
		// Base prefix for import the record store
    String propertyBaseName;
		// Records data structure
		Vector bookmarks;

		boolean initialized;


		/**
     * Construct a new TinyLine MIDlet and initialize the base options
     * and SVG canvas to be used when the MIDlet is started.
     */
    public TinyLine()
    {
			 display = Display.getDisplay(this);

			 // Create the SVG canvas.
			 canvas = new MIDPSVGCanvas(display);

			 // Load incons
			 canvas.init();

			 // Add commands
			 openCommand      = new Command("Open", Command.SCREEN, 1);
			 nextPrevCommand  = new Command("Next Prev", Command.SCREEN, 1);
			 linkCommand      = new Command("Link", Command.SCREEN, 1);
		   panCommand       = new Command("Pan", Command.SCREEN, 1);
			 zoomCommand      = new Command("Zoom", Command.SCREEN, 1);
			 origViewCommand  = new Command("Orig View", Command.SCREEN, 1);
       qualityCommand  =  new Command("Quality", Command.SCREEN, 1);
			 pauseCommand     = new Command("Pause", Command.SCREEN, 1);
       helpCommand      = new Command("Help", Command.SCREEN, 1);
			 exitCommand      = new Command("Exit", Command.EXIT, 2);
			 canvas.addCommand(openCommand);
 	     canvas.addCommand(nextPrevCommand);
 	     canvas.addCommand(linkCommand);
 	     canvas.addCommand(panCommand);
 	     canvas.addCommand(zoomCommand);
 	     canvas.addCommand(origViewCommand);
       canvas.addCommand(qualityCommand);
 	     canvas.addCommand(pauseCommand);
       canvas.addCommand(helpCommand);
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
					// Initialize bookmarks
          if (initialized == false) initialize();
					// Copy bookmarks pointers
					canvas.bookmarks = bookmarks;
					canvas.bookmarkList = bookmarkList;
					// Get graphics
          canvas.repaint();

					// Load the default SVG font.
          SVGDocument doc =  canvas.loadSVG("/tinyline/helvetica.svg");
          SVGFontElem font = SVGDocument.getFont(doc,SVG.VAL_DEFAULT_FONTFAMILY);
          SVGDocument.defaultFont = font;

     		  // Add the default event listener
		      PlayerListener defaultListener = new PlayerListener(canvas);
		      canvas.addEventListener("default", defaultListener, false);

					// Start the event dispatching queue
					canvas.start();

					// Loads the splash splash SVGT image
          canvas.goURL("/svg/tinyline.svg");
       }
       catch( Exception e)
       {
       }
    }

    /** Pause the MIDlet. */
    public void pauseApp()
    {
			 canvas.stop();
    }

    /**
     * Destroy the MIDlet.
     * @param unconditional Unconditional flag.
     */
    public void destroyApp(boolean unconditional)
    {
			 canvas.stop();
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
			 if (c == openCommand)
			 {
					 display.setCurrent(bookmarkList); // open form
			 }
       else if(c == nextPrevCommand)
			 {
					 canvas.selectMode(MIDPSVGCanvas.MODE_NEXTPREV);
			 }
       else if(c == linkCommand)
			 {
					 canvas.selectMode(MIDPSVGCanvas.MODE_LINK);
			 }

			 else if(c == panCommand)
			 {
					 canvas.selectMode(MIDPSVGCanvas.MODE_PAN);
			 }

			 else if(c == zoomCommand)
			 {
					 canvas.selectMode(MIDPSVGCanvas.MODE_ZOOM);
			 }

			 else if(c == origViewCommand)
			 {
				    canvas.origView();
			 }

       else if(c == qualityCommand)
       {
            canvas.switchQuality();
       }

			 else if(c == pauseCommand)
			 {
						canvas.pauseResumeAnimations();
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

       else if (c == saveCommand)
			 {
          String name = nameField.getString();
          String url = URLField.getString();
          // Validate the entry first.
          if (validate(name, url) == false)
					{
              alert.setString("Please enter a valid name and URL.");
              alert.setTimeout(Alert.FOREVER);
              display.setCurrent(alert, editForm);
          }
          // Don't allow a duplicate name except for
          // the case of editing.
          else if ((editBookmark == null && getBookmark(name) != null) ||
               (editBookmark != null && !getBookmark(name).equals(editBookmark)))
					{
              alert.setString("That name is already used");
              alert.setTimeout(Alert.FOREVER);
              display.setCurrent(alert, editForm);
          }
          else
					{
              Bookmark bookmark = new Bookmark(name, url);
              if(editBookmark == null)
							{
                 // add the bookmark
								 bookmarks.addElement(bookmark);
							}
              // For an edit, remove the old Bookmark object.
              else
							{
                 // Replace editBookmark with bookmark;
                 int i = bookmarks.indexOf(editBookmark);
                 bookmarks.setElementAt(bookmark, i);
                 editBookmark = null;
              }
              // Blank out the edit form fields.
              nameField.setString("");
              URLField.setString("");
              // Show the updated Bookmark list.
              saveBookmarks();
              fillBookmarkList();
              display.setCurrent(bookmarkList);
          }
       }
			 else if (c == editBackCommand)
			 {
          display.setCurrent(bookmarkList);
       }
			 else if (c == addCommand)
			 {
          display.setCurrent(editForm);
       }
       else if (c == List.SELECT_COMMAND)
			 {
					canvas.index = bookmarkList.getSelectedIndex();
          display.setCurrent(canvas);
					canvas.repaint();
 	        canvas.go(canvas.index);
       }
			 else if (c == editCommand)
			 {
           Bookmark f = getSelectedBookmark();
           editBookmark = f;
           nameField.setString(editBookmark.name);
           URLField.setString(editBookmark.url);
           display.setCurrent(editForm);
       }
       else if (c == deleteCommand)
			 {
           Bookmark f = getSelectedBookmark();
					 // remove the bookmark
           bookmarks.removeElement(f);
           saveBookmarks();
           fillBookmarkList();
           display.setCurrent(bookmarkList);
       }
       else if (c == defaultsCommand)
			 {
           loadBookmarksFromProperties("svg.image");
           saveBookmarks();
           fillBookmarkList();
       }
       else if (c == listBackCommand || c == helpBackCommand)
			 {
          display.setCurrent(canvas);
					canvas.repaint();
			 }
		}

	 /**
    * Compares two raw Bookmark records.
    */
   public int compare(byte[] rec1, byte[] rec2)
	 {
     int length = Math.min(rec1.length, rec2.length);
     for (int i = 0; i < length; i++) {
       if (rec1[i] < rec2[i]) return PRECEDES;
       else if (rec1[i] > rec2[i]) return FOLLOWS;
     }
     return EQUIVALENT;
   }


		/** Initialises the Bookmarks data structure */
    private void initialize()
		{

			 // Load the Bookmarks from RMS.
       recordStoreName = "svg.images";
       propertyBaseName = "svg.image";
       loadBookmarks();

       // Create the Help screen.
       helpScreen = new Form("Help");
       helpScreen.append(new StringItem("",helpString));
       helpBackCommand = new Command("Back", Command.BACK, 1);
       helpScreen.addCommand(helpBackCommand);
       helpScreen.setCommandListener(this);


       // Create the Bookmarks screen.
       addCommand = new Command("Add", Command.SCREEN, 1);
       editCommand = new Command("Edit", Command.SCREEN, 1);
       deleteCommand = new Command("Delete", Command.SCREEN, 1);
       defaultsCommand = new Command("Defaults", Command.SCREEN, 1);
       listBackCommand = new Command("Back", Command.BACK, 1);
       bookmarkList = new List("Bookmarks", List.IMPLICIT);
       fillBookmarkList();
       bookmarkList.addCommand(listBackCommand);
       bookmarkList.addCommand(addCommand);
       bookmarkList.addCommand(editCommand);
       bookmarkList.addCommand(deleteCommand);
       bookmarkList.addCommand(defaultsCommand);
       bookmarkList.setCommandListener(this);

			 
       // Create the Bookmark Edit screen.
			 
       editBackCommand = new Command("Back", Command.BACK, 1);
       saveCommand = new Command("Save", Command.SCREEN, 1);
       editForm = new Form("Edit Bookmark");
       nameField = new TextField("Name", "", 128, TextField.ANY);
       URLField = new TextField("URL", "http://www.gis-news.de/wms/getmapcap.php?VERSION=1.1.1&BBOX=437181%2C5241417%2C441981%2C5247817&LAYERS=airports%2Cctybdpy2%2Clakespy2&STYLES=%2C%2C&REQUEST=GetMap&layer1=airports&layer2=ctybdpy2&layer3=lakespy2&style=&SRS=EPSG%3A26715&minx=189775.33&miny=4816305.37&maxx=190051.33&maxy=4816525.37&WIDTH=240&HEIGHT=320&FORMAT=image%2Fsvgxml&EXCEPTIONS=application%2Fvnd.ogc.se_xml&transparent=True&button=GetMapRequest", 456, TextField.URL);
       editForm.append(nameField);
       editForm.append(URLField);
       editForm.addCommand(editBackCommand);
       editForm.addCommand(saveCommand);
       editForm.setCommandListener(this);

       initialized = true;
    }

    /** Fills the bookmarkList from the bookmark data */
    private void fillBookmarkList()
   	{
       // First remove all items from the list.
       while (bookmarkList.size() > 0) bookmarkList.delete(0);
       // Walk through the vector and add items to the list.
       Bookmark bookmark = null;
       for (int i = 0; i < bookmarks.size(); i++)
   		 {
          bookmark = (Bookmark)bookmarks.elementAt(i);
          bookmarkList.append(bookmark.name, null);
       }
    }

    /** Returns the selected Bookmark */
    private Bookmark getSelectedBookmark()
	  {
       String name = bookmarkList.getString(bookmarkList.getSelectedIndex());
       // Get the matching Bookmark.
       return getBookmark(name);
    }

    /** Returns a Bookmark by name */
    private Bookmark getBookmark(String name)
	  {
        Bookmark bookmark = null;
        for (int i = 0; i < bookmarks.size() && bookmark == null; i++)
				{
           bookmark = (Bookmark)bookmarks.elementAt(i);
           if (name.equals(bookmark.name) == false)
              bookmark = null;
        }
        return bookmark;
    }

    /** Validates the input entry */
    private boolean validate(String name, String url)
		{
       if (name == null) return false;
       if (url == null) return false;
       if (name.length() == 0) return false;
       if (url.length() == 0) return false;
       return true;
    }

    /** Loads Bookmarks from RMS or properties */
  	private void loadBookmarks()
  	{
       bookmarks = new Vector();
       try
  		 {
          loadBookmarksFromRecordStore(recordStoreName);
       }
       catch (RecordStoreNotFoundException rsnfe)
  		 {
          loadBookmarksFromProperties(propertyBaseName);
          saveBookmarks();
       }
       catch (RecordStoreException rse)
  		 {
       }
    }

    /** Loads Bookmarks from RMS */
    private void loadBookmarksFromRecordStore(String name) throws RecordStoreException
  	{
       bookmarks.removeAllElements();
       RecordStore rs = null;
       RecordEnumeration re = null;
       try
        {
          rs = RecordStore.openRecordStore(name, false);
          if (rs.getNumRecords() == 0) throw new RecordStoreNotFoundException();
          re = rs.enumerateRecords(null, this, false);
          while (re.hasNextElement())
          {
            byte[] raw = re.nextRecord();
            Bookmark f = rawToBookmark(raw);
            bookmarks.addElement(f);
          }
       }
       finally
       {
          if (re != null) re.destroy();
          if (rs != null) rs.closeRecordStore();
       }
    }

    /**
     * Clears the BookmarkStore and loads Bookmarks from using the
     * specified property base name.
     */
    private void loadBookmarksFromProperties(String baseName)
  	{
       bookmarks.removeAllElements();
       int index = 1;
       for(;;)
       {
          String propertyName = baseName + ".";
          if(index <=9)
              propertyName +="0";
          propertyName +="" + index++;
          String BookmarkString = getAppProperty(propertyName);
          if (BookmarkString == null)
          {
            break;
          }
          else
          {
            Bookmark f = Bookmark.create(BookmarkString);
            bookmarks.addElement(f);
          }
       }
    }

    /** Returns the raw byte array representation for the ith Bookmark. */
    private byte[] bookmarkToRaw(int i)
    {
       Bookmark f = (Bookmark)bookmarks.elementAt(i);
       byte[] raw = f.getRaw().getBytes();
       byte[] ordered = new byte[raw.length + 1];
       ordered[0] = (byte)i; // Store the position here.
       System.arraycopy(raw, 0, ordered, 1, raw.length);
       return ordered;
    }

    /** Creates a new Bookmark object from the given raw bytes array. */
    private Bookmark rawToBookmark(byte[] raw)
    {
       String s = new String(raw, 1, raw.length - 1);
       return Bookmark.create(s);
    }
    /** Returns true if the byte arrays equal; otherwise return false */
    private boolean byteEquals(byte[] one, byte[] two)
    {
       if (one.length != two.length) return false;
       for (int i = 0; i < one.length; i++)
          if (one[i] != two[i]) return false;
       return true;
    }

    /**
     * Saves Bookmarks to the RMS.
     */
    private void saveBookmarks()
  	{
       String name = recordStoreName;
       RecordStore rs = null;
       RecordEnumeration re = null;
       try
       {
          rs = RecordStore.openRecordStore(name, true);
          re = rs.enumerateRecords(null, null, false);
          boolean[] found = new boolean[bookmarks.size()];
          while (re.hasNextElement())
          {
             int id = re.nextRecordId();
             byte[] raw = null;
             if (re.hasPreviousElement())
             {
               re.previousRecordId();
               raw = re.nextRecord();
             }
             else if (re.hasNextElement())
             {
                re.nextRecordId();
                raw = re.previousRecord();
             }
             else
             {
                re.reset();
                raw = re.nextRecord();
             }

             // Look for a match in our internal list.
             boolean recordFound = false;
             for (int i = 0; i < bookmarks.size(); i++)
             {
                 byte[] existingRaw = bookmarkToRaw(i);
                 if (byteEquals(raw, existingRaw))
                 {
                    found[i] = true;
                    recordFound = true;
                    break;
                 }
             }
             // Remove records that have no match.
             if (recordFound == false)
             {
                rs.deleteRecord(id);
             }
          }

          // Now look through bookmarks. Anything that wasn't in
          // the recordstore should be added.
          for (int i = bookmarks.size() - 1; i >= 0; i--)
          {
             if (found[i] == false)
             {
                byte[] raw = bookmarkToRaw(i);
                rs.addRecord(raw, 0, raw.length);
             }
          }
       }
       catch (RecordStoreException rse)
       {
          System.out.println(rse);
       }
       finally
       {
          try
          {
             if (re != null) re.destroy();
             if (rs != null) rs.closeRecordStore();
          }
          catch (RecordStoreException rse) {}
       }
    }


   /** Help */
     private static String helpString =
   "TinyLine implements Scalable Vector Graphics Tiny (SVGT) for J2ME."
   +"\n\n"
   +"GETTING STARTED\n"
   +"TinyLine comes with several SVGT samples, other samples located on the tinyline.com. "
   +"From the TinyLine welcome screen, you can use RIGHT and LEFT keys to navigate "
   +"among bookmarked links. Or, you can select the <Open> command to open the bookmarks."
   +"\n\n"
   +"USING BOOKMARKS\n"
   +"Use <Add> command to enter a new link. You may edit the bookmarks using <Edit> or  "
   +"<Delete> commands or reset the bookmarks to <Default>."
   +"\n\n"
   +"NAVIGATION\n"
   +"In the <Next Prev> mode (by default) you can use RIGHT and LEFT keys to navigate "
   +"among bookmarked SVGT links.\n"
   +"In the <Link> mode you can use UP and DOWN keys to navigate links. A link will be "
   +"highlighted with a blue rectangle. You can then select it by pressing FIRE key. If your "
   +"device has a pointer, you can also select any link by tapping your pointer on it.\n"
   +"In the <Pan> mode you can scroll using LEFT, RIGHT, UP and DOWN keys. If your "
   +"device has a pointer, you can also scroll by dragging the pointer.\n"
   +"In the <Zoom> mode you can zoom in or zoom out using UP and DOWN keys.\n"
   +"The <Orig View> command returns the viewing image to its original view.\n"
   +"The <Quality> command turns off or on the antialising.\n"
   +"The <Pause> command stops or resumes animations."
   +"\n\n"
   +"MORE\n"
   +"For more about TinyLine, see http://www.tinyline.com/."
   +"\n"
   +"Copyright (c) 2002-2005 TinyLine. All rights reserved."
   +"\n";
}
