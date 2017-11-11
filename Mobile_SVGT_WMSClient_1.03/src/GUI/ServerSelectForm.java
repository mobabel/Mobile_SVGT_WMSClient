package GUI;
import javax.microedition.io.*;

import java.io.*;
import java.util.*;

import javax.microedition.lcdui.*;

//import GUI.HTTP.GetRequest;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import GUI.CONTROL.GUIController;
import GUI.MODEL.ServerListItem;




public class ServerSelectForm extends List implements CommandListener{
    //private Calendar         calendar;
    private GUIController    controller;

    public StringItem resultheadField;
    public static String finalurl;
    public static String finalurl_f;
    public static int serverlistindex ;
   
                          
    private Display        display;
    private Command        backCommand= new Command("OK",Command.BACK,1);
    private Command        connectCommand= new Command("Connect",Command.OK,3);
    private Command        exitCommand = new Command("Exit",Command.EXIT,2);
    private Command        editCommand = new Command("Edit",Command.ITEM, 3);
    private Command        addCommand= new Command("Add",Command.ITEM,3);
    private Command        deleteCommand = new Command("Delete",Command.ITEM,3);
    private Command        cancelCommand = new Command("Cancel",Command.CANCEL, 3);
    private Command        aboutCommand = new Command("About",Command.HELP,3);
    

    private Vector         apps;
    private Hashtable      app_table ;
	private Vector items;
	private Image icon;
	

    public ServerSelectForm(String title,GUIController control,Vector vec) {
    	super(title,List.IMPLICIT);
    	//load image
    	try{
    	    icon=Image.createImage("/icon/icon_server.png");
    	}catch(java.io.IOException e){
    	    icon=null;
    	    System.out.println("Load icon error: ");
    	}	
    	
    	controller=control;
    	this.items=vec;
	    this.addCommand(exitCommand);
	    this.addCommand(connectCommand);
	    this.addCommand(editCommand);
        this.addCommand(addCommand);
        this.addCommand(deleteCommand);
        this.addCommand(cancelCommand);
        this.addCommand(aboutCommand);
	    this.setCommandListener(this);
	   
    	if(vec!=null&&vec.size()>0){
    		ServerListItem sltiem=null;
    		for(int i=0;i<vec.size();i++){
    			sltiem=(ServerListItem)vec.elementAt(i);
    			this.append(sltiem.getServerName(),icon);
    		}
    	}
        //apps= rmsDB.retrieveAll();
        //for(int i=0; i<apps.size(); i++) {
            //RMSServerUrl app= (RMSServerUrl) apps.elementAt(i);
            //StringBuffer sb = new StringBuffer();
            //sb.append(app.getId()).append(" ").append(app.getServerUrl());
            //servermenu.append(sb.toString(),null);
        //}
	    
        //get a calendar
        //calendar=Calendar.getInstance();

        //open the record store that stores appointments

    }
    
    public void refresh(Vector vec){
        this.deleteAll();
        this.items=vec;
    	if(vec!=null&&vec.size()>0){
    		ServerListItem slitem=null;
    		for(int i=0;i<vec.size();i++){
    			slitem=(ServerListItem)vec.elementAt(i);
    			this.append(slitem.getServerName(),icon);
    		}
    	}
    }
    
    public void add(ServerListItem serverlist){
        this.append(serverlist.getServerName(),icon);
    }
    

   //private class PhoneListListener implements CommandListener{   
    public void commandAction(Command c, Displayable d) {
	   if(c==connectCommand) {
		
        int ind=((List)d).getSelectedIndex();
        if(ind==-1)
            return;
		Object[] args={items.elementAt(ind)};
		controller.handleEvent(GUIController.EventID.EVENT_CONNECT,args);
	    }
	
        else if(c==editCommand) {
            //retrieve serverlist and edit
            //create an server edit form
	            int ind=((List)d).getSelectedIndex();
	            if(ind==-1)
	                return;
				Object[] args={items.elementAt(ind)};    		
				controller.handleEvent(GUIController.EventID.EVENT_EDIT,args);
               }
	    else if(c==addCommand) {
                 //Add a server url
			controller.handleEvent(GUIController.EventID.EVENT_NEW_RECORD,null);
                }
	    else if(c==deleteCommand) {
            //delete a server url
            int ind=((List)d).getSelectedIndex();
            if(ind==-1)
                return;
			Object[] args={items.elementAt(ind)};
			controller.handleEvent(GUIController.EventID.EVENT_DELETE_DETAIL,args);
           }
	    else if(c==exitCommand ) {
	    	controller.handleEvent(GUIController.EventID.EVENT_EXIT,null);
	        }
        else if(c==backCommand) {
                //display.setCurrent(this);
            }
        else if(c==aboutCommand){
		    controller.handleEvent(GUIController.EventID.EVENT_ABOUT,null);
		}    
            }
   //}
    
}


