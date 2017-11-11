package GUI.HTTP;

import javax.microedition.io.*;

import java.io.*;
import java.util.Vector;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import GUI.PlaceSearchForm; 
import GUI.CONTROL.GUIController;
import GUI.MODEL.ServerItem;
import GUI.MODEL.ServerListItem;
import GUI.MODEL.SETTING;


public class GetHttpHead {
    private  String  url;
    private PlaceSearchForm placesearchForm=null;
    private GUIController controller;
	public Display display;
	public Displayable parent;

    private boolean B_gethttpheadfinish=false;
	
	public Vector sendHeadRequest(final Display d,final Displayable p,GUIController control,final ServerListItem item) {
        final Vector head = new Vector();
    	/* retrieve header information */
		this.url=item.getServerURL();
    	System.out.println("This url is in GetHttpHead: "+url) ;
    	display=d;//null
        parent=p; //null
        controller=control;

        
        
  	Thread getThread = new Thread() {   	
	public void run(){
            HttpConnection hc = null;
            InputStream is = null;
            String S_server = "";
            String S_content_type = "";
            ServerItem serverItem = new ServerItem();
        try {
              if (B_gethttpheadfinish==false){
                	Alert Alert_wait4parse = new Alert("");
                	Alert_wait4parse.setType(AlertType.INFO);
                	Alert_wait4parse.setString("   Now is connecting to WMS \n"+"   Please waiting......");
                	Alert_wait4parse.setTimeout(SETTING.GethttpheadWait_Time);
	                Command cancelCommand = new Command("Cancel",Command.CANCEL,2);
  	                Alert_wait4parse.addCommand(cancelCommand);
                	controller.setCurrent(Alert_wait4parse);
                }
           // openning up http connection with the web server
           hc = (HttpConnection) Connector.open(url);

           // setting request method to HEAD
           hc.setRequestMethod(HttpConnection.HEAD);
           serverItem.setServerURL(url);
           // establishing input stream from the connection
           is = hc.openInputStream();

          // retrieving the value pairs of HTTP header information
           int i = 1;
           String key = "";
           String value = "";
           String power=" is powered by ";
           while ( (value = hc.getHeaderField(i)) != null) 
           {
             key = hc.getHeaderFieldKey(i++);
             System.out.println("key is "+key) ;
             if (key.equals("server")){
             S_server =  S_server + key + power +":" +"\n"+ value + "\n";

             serverItem.setServerHead(S_server);
                    	
             head.addElement(S_server);
             System.out.println("Vector head is "+head.toString()) ;
             }
             else if (key.equals("content-type")){
            	 S_content_type =  S_content_type + key +" is:" +"\n"+ value + "\n";

                 serverItem.setServerContentType(S_content_type);
                        	
                 head.addElement(S_content_type);
                 System.out.println("Vector head is "+head.toString()) ;
                 }
           }
        B_gethttpheadfinish=true;
       	System.out.println("Result in thread sendheadrequest: "+S_server) ;
        if (B_gethttpheadfinish==true){
       	placesearchForm=new PlaceSearchForm(SETTING.FORM_TITLE_PLACESEARCHFORM, d, placesearchForm,controller,item,serverItem);
       	controller.setCurrent(placesearchForm);
        }
        
        } catch (IOException ioe) {
           S_server = "ERROR";
        } finally {
          try { if (hc != null) hc.close();} catch (IOException ignored) {}
          try { if (is != null) is.close();} catch (IOException ignored) {} 
        }
	}



	 } ;//Thread 
	    getThread.start(); 
	    
	    return head;
    }

	
	



}