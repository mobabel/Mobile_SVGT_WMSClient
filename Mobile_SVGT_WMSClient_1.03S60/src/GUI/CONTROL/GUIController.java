/*
 * Created on 2005-2-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package GUI.CONTROL;

import java.io.IOException;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.midlet.MIDlet;


import GUI.RMS.ServerListRecordStore;
import GUI.AboutAlert;
import GUI.LayerSelectForm;
import GUI.ServerAddForm;
import GUI.ServerEditForm;
import GUI.ServerSelectForm;
import GUI.ServerDeleteForm;
import GUI.PlaceSearchForm;
//import GUI.LayerSelectForm;
import GUI.HTTP.GetHttpHead;
import MIDlet.MobileSVGTWMSClient;
import GUI.MODEL.ServerListItem;
import GUI.MODEL.SETTING;

/**
 * 
 */
public class GUIController {
	private MobileSVGTWMSClient     mobilesvgtMidlet;
	private ServerListRecordStore   serverlistRecords;
	private ServerSelectForm        serverselectForm;
	private ServerAddForm           serveraddForm;
	private ServerDeleteForm        serverdeleteForm;
	private ServerEditForm          servereditForm;
	private PlaceSearchForm         placesearchForm;
	private LayerSelectForm         layerselectForm;
	private Alert                   alert;
	private AboutAlert              aboutAlert;
    private Image                   icon_start=null; 
    private Alert                   startAlert;
	
	public GUIController(MobileSVGTWMSClient mobilesvgClient){
		mobilesvgtMidlet=mobilesvgClient;
	}
	
	public void init() {
	       try {
	        	icon_start=Image.createImage("/icon/icon_start.png");
		    } catch (java.io.IOException e) {
		    	icon_start=null;
				System.out.println("Load image error when initializing Exception happens:" + e.getMessage());
		    }
		    startAlert=new Alert(SETTING.FORM_TITLE_ABOUTALERT);
		    startAlert.setTimeout(SETTING.Initial_Time);
		    startAlert.setImage(icon_start);
		    startAlert.setString("                 System is initializeing...");

		
		try{
		serverlistRecords = ServerListRecordStore.getInstance(this, (MIDlet)mobilesvgtMidlet);
		}catch(Exception exc)
		{
			System.out.println("When load ServerListRecordStore Exception happens:" + exc.getMessage());
		}
		//init GUI
		serverselectForm=new ServerSelectForm(SETTING.FORM_TITLE_SERVERSELECTFORM,this,serverlistRecords.getRSs());
		serveraddForm=new ServerAddForm(SETTING.FORM_TITLE_SERVERADDFORM, null, serverselectForm, this);
		serverdeleteForm=new ServerDeleteForm(SETTING.FORM_TITLE_SERVERDELETEFORM, null, serverselectForm, this);
		servereditForm=new ServerEditForm(SETTING.FORM_TITLE_SERVEREDITFORM, null, serverselectForm, this);
		aboutAlert=new AboutAlert(SETTING.FORM_TITLE_ABOUTALERT);
		setCurrent(startAlert,serverselectForm);
	}
	/**********************************************************
	//	 GUIController-->getserverlistRecords()
	//	 Return the ServerListRecordStore which envelope the RMS controll
	**********************************************************/
	public ServerListRecordStore getserverlistRecords(){
		return serverlistRecords;
	}
	/**********************************************************
	//	 GUIController::setCurrent()
	**********************************************************/
	public void setCurrent(Displayable disp){
		mobilesvgtMidlet.setCurrent(disp);
    }
	public void setCurrent(Alert alert, Displayable disp){
		mobilesvgtMidlet.setCurrent(alert, disp);
    }
	
	/**********************************************************
	//	 GUIController::EventID
    //Define Event ID inside Class
	**********************************************************/
    public static class EventID{
        private EventID(){
        }
        
        public static final byte EVENT_EXIT=0;//exit
        public static final byte EVENT_NEW_RECORD =1;//add
        public static final byte EVENT_NEW_SAVE =2;//add save
        public static final byte EVENT_NEW_BACK =3;//add back
        public static final byte EVENT_DELETE =4;//delete
        public static final byte EVENT_DELETE_DETAIL =5;//delete and check
        public static final byte EVENT_DELETE_BACK= 6;//delete return
        public static final byte EVENT_EDIT=7;//edit
        public static final byte EVENT_EDIT_BACK=8;//edit return
        public static final byte EVENT_EDIT_SAVE=9;//edit save
        public static final byte EVENT_ABOUT=10;//about
        public static final byte EVENT_CONNECT=11;//connect to server
        public static final byte EVENT_SEARCH_BACK=12;//search return
        public static final byte EVENT_SEARCH=13;//search select layers
        
    }
    
	/**********************************************************
	//	 GUIController::deleteAll
	//   delete all server list
	**********************************************************/
	private synchronized void deleteAllServer()
	{
		System.out.println("GUIController::Enter deleteAllServer");
		
		// This can only delete half of the list
		/*for(int i = 0;i < list();i++){
			
			list.delete(i);
			System.out.println("delete>>" + i);
		}*/
		
		// This can delete all
		while(serverselectForm.size()>0)
			serverselectForm.delete(0);
	}
	
	/**********************************************************
	//	 GUIController::RefreshList
	//refresh the server list from the RS
	**********************************************************/
	private synchronized void RefreshServerList()
	{
		System.out.println("GUIController::Enter RefreshServerList");
				
		deleteAllServer();
		serverselectForm.refresh(serverlistRecords.getRSs());
	}
    
	/**********************************************************
	//	 GUIController::handleEvent
    //Event process
	**********************************************************/
    public void handleEvent( int eventID,Object[] args){   
    	switch (eventID)
        {   
    	    case EventID.EVENT_EXIT:
    	    {   
    	    	System.gc();// collect rabage
    	    	Thread.yield();//pause the thread to collect rabage
    	    	mobilesvgtMidlet.exit(false);
    	    	break;
    	    }
    	    case EventID.EVENT_DELETE_DETAIL:
    	    {
    	        serverdeleteForm.setServer((ServerListItem)args[0]);
    	        setCurrent(serverdeleteForm);
    	        break;
    	    }
    	    case EventID.EVENT_NEW_RECORD:
    	    {
    	        serveraddForm.clear();
    	    	setCurrent(serveraddForm);
    	    	break;
    	    }
    	    case EventID.EVENT_NEW_BACK:
    	    case EventID.EVENT_DELETE_BACK:    
    	    case EventID.EVENT_EDIT_BACK:
    	    case EventID.EVENT_SEARCH_BACK:
    	    {
    	    	setCurrent(serverselectForm);
    	    	break;
    	    }
    	    case EventID.EVENT_NEW_SAVE:
    	    {
    	        ServerListItem item=new ServerListItem((String)args[0],(String)args[1]);
				System.out.println("name="+ (String)args[0] + " url="+ (String)args[1]);
    	        ///serverlistRecords.addRS(item);///
                //create an alert
                Alert saveInfo= new Alert("","",null,AlertType.INFO);
                saveInfo.setTimeout(Alert.FOREVER);
                if(serverlistRecords.addRSb(item)) {
                    saveInfo.setString("        Add a new Server Successfully!");
                }
                else {
                    saveInfo.setString("        Fail to add a new Server!");
                } 

    	        RefreshServerList();
    	        setCurrent(saveInfo,serverselectForm);
    	      ///setCurrent(serverselectForm);///
    	        break;
    	    }
    	    case EventID.EVENT_EDIT:
    	    {
    	        servereditForm.setServer((ServerListItem)args[0]);
    	        setCurrent(servereditForm);
    	        break;
    	    }
    	    case EventID.EVENT_EDIT_SAVE:
    	    {
    	    	ServerListItem item=(ServerListItem)args[0];
    	        ///serverlistRecords.updateRS(item);///
                //    	      create an alert
                Alert editInfo= new Alert("","",null,AlertType.INFO);
                editInfo.setTimeout(Alert.FOREVER);
                if(serverlistRecords.updateRSb(item)) {
                	editInfo.setString("        Edit this Server Successfully!");
                }
                else {
                	editInfo.setString("        Fail to edit this Server!");
                } 
    	        RefreshServerList();
    	        setCurrent(editInfo,serverselectForm);
    	        ///setCurrent(serverselectForm);///
    	        break;
    	    }
    	    case EventID.EVENT_DELETE:
    	    {
    	        ///serverlistRecords.deleteRS(((ServerListItem)args[0]).getId());///
                //create an alert
                Alert deleteInfo= new Alert("","",null,AlertType.INFO);
                deleteInfo.setTimeout(Alert.FOREVER);
                if(serverlistRecords.deleteRSb(((ServerListItem)args[0]).getId())) {
                	deleteInfo.setString("        Delete this Server Successfully!");
                }
                else {
                	deleteInfo.setString("        Fail to delete this Server!");
                } 

    	        RefreshServerList();
    	        setCurrent(deleteInfo,serverselectForm);
    	        ///setCurrent(serverselectForm);///
    	        break;
    	    }
    	    case EventID.EVENT_ABOUT:
    	    {
    	        setCurrent(aboutAlert);
    	        break;
    	    }
    	    case EventID.EVENT_CONNECT:
    	    {
    	    	GetHttpHead gethttphead=new GetHttpHead();
    	    	gethttphead.sendHeadRequest(null,null,this,(ServerListItem)args[0]);
    	    	/**
    	    	 * URL -->((ServerListItem)args[0]).getServerURL()
    	    	 */
    			System.out.println("Go to placesearchForm") ;
    	        break;
    	    }
    	    case EventID.EVENT_SEARCH:
    	    {
    			System.out.println("Go to select layer") ;
    	        //setCurrent(layerselectForm);
    	        break;
    	    }
         	default:
         	    break;
        }
    }
    
}
