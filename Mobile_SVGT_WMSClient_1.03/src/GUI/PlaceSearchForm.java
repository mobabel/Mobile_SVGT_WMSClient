package GUI;


import javax.microedition.midlet.*;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.*;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Calendar;

import GUI.ServerSelectForm;
import GUI.HTTP.GetHttpHead;
import GUI.HTTP.GetRequest;
import GUI.HTTP.GetCapabilitiesParser;
//import GUI.HTTP.GetCapabilitiesParser.RSSListener;
import GUI.CONTROL.GUIController;
import GUI.MODEL.ServerItem;
import GUI.MODEL.ServerListItem;
import GUI.LayerSelectForm;
import GUI.MODEL.SETTING;



public class PlaceSearchForm extends Form implements CommandListener{//,RSSListener{
    private GUIController controller;
	public Display display;
	public Displayable parent;

    private Command layerselectCommand =new Command("Select Layers", Command.OK, 2);
    private Command backCommand = new Command("Back", Command.BACK, 1);


	private ServerSelectForm serverselectForm=null;
	private GetHttpHead      gethttphead;
    private PlaceSearchForm  placesearchForm=null;
    private LayerSelectForm  layerselectForm = null;
    private ServerListItem   item;
    private ServerItem       serveritem;

    public  StringItem     resultheadField;
    private String         serverurlstring=null;
    private String         url="";
    private  StringItem     resultheadField_h=null;
    private  StringItem     resultheadField_c=null;
    

    public static double         BBOX_leftup_X;
    public static double         BBOX_leftup_Y;
    public static double         BBOX_rightdown_X;
    public static double         BBOX_rightdown_Y;

    
    //SRS=EPSG%3A26715&minx=189775.33&miny=4816305.37&maxx=190051.33&maxy=4816525.37&
    public  double         SRS_minx=189775.33;
    public  double         SRS_miny=4816305.37;
    public  double         SRS_maxx=190051.33;
    public  double         SRS_maxy=4816525.37;    
        
    private ChoiceGroup CGscale;
    
    private boolean mInitialized; 
    
    
    public PlaceSearchForm(String title, Display d, Displayable p,GUIController control,ServerListItem item,ServerItem serverItem) {
        super(title);
        display=d;
        parent=p;
        addCommand(backCommand);
    	addCommand(layerselectCommand);
        setCommandListener(this);
        controller=control;

        //For the other function which is not in this function
        serveritem=serverItem;

        //public PlaceSearchForm() {
    	//mInitialized = false; 

        //}

       	//if (mInitialized == false) { 
    		// Put up the waiting screen. 
    		//Screen waitScreen = new Form("Connecting..."); 
    		//display.setCurrent(waitScreen); 
    		// Create the title list. 
    	    //layersmenu.addCommand(backCommand);
    		//layersmenu.addCommand(sendCommand);
    		//layersmenu.setCommandListener(this);
    		//display.setCurrent(layersmenu); 
    		// Start parsing. 
   	
		    //serverurlstring=item.getServerURL();
		    serverurlstring=serverItem.getServerURL();
	   		//initializeHead();
   		    System.out.println("ServerURL in PlaceSearchForm: "+ serverurlstring) ;
    		//mInitialized = true; 
    		//} 
    	//else display.setCurrent(layersmenu); 


    		System.out.println("TEST111111111111111") ;

            
    		System.out.println("TEST222222222222222") ;
        
	//RMSPlaceSearch Time
	//DateField df= new DateField("Date and Time",DateField.DATE_TIME);
	//df.setDate(new Date(System.currentTimeMillis()));
	//append(df);

	//PlaceSearch post code
	//append(new TextField("Post code", "5", 10,TextField.NUMERIC)); 
		
	/**X coordinate*/
	append(new TextField("X coordinate","439581",15,TextField.NUMERIC));
	//append("X must be between \n 189775.33 and 761662.27");	
	/**Y coordinate*/
	append(new TextField("Y coordinate","5244617",15,TextField.NUMERIC));
	//append("Y must be between \n 4816305.37 and 5472414.18");	
	/**Scale ratio*/
	CGscale= new ChoiceGroup("Choose the scale",Choice.EXCLUSIVE,SETTING.Sscale,null);
	append(CGscale);
	
	/**Get server Head from ServerItem*/
    resultheadField_h  = new StringItem(null, serveritem.getServerHead());
    /**Get server ContentType from ServerItem*/
    resultheadField_c  = new StringItem(null, serveritem.getServerContentType());

   	append(resultheadField_h);
   	append(resultheadField_c);
    }
	
//    public void setRMSPlaceSearch(RMSPlaceSearch _papp) {
//        papp= _papp;
//	//RMSPlaceSearch Time
//	DateField df= (DateField) get(0);
//	//df.setDate(new Date(papp.getTime()));
//
//	//RMSPlaceSearch post code
//	TextField tf_code= (TextField) get(1);
//        tf_code.setString(String.valueOf(papp.getCode()));
//        	
//	//RMSPlaceSearch location
//        TextField tf_location= (TextField) get(2);
//        tf_location.setString(papp.getLocation());
//		
//        //RMSPlaceSearch destination
//        TextField tf_destination= (TextField) get(3);
//        tf_destination.setString(papp.getDestination());
//    }

    
    
    /**
     * Called by the framework before the application is unloaded
     */
    public void destroyApp(boolean unconditional) {
        //placesearchForm= null;

    }


    public void commandAction(Command c, Displayable d) {
	 if(c==layerselectCommand){
        d=this;
	    //go to select the layers
		serverurlstring=serveritem.getServerURL();
		System.out.println("serverurlstring in layerselectCommand is : "+serverurlstring) ;
 		GetCapabilitiesParser getcapabilitiesparser = new GetCapabilitiesParser(); 
        getcapabilitiesparser.getcapparse(serverurlstring,display,this, controller); 
		 
		System.out.println("---------------------Step 1 ") ;
		//controller.handleEvent(GUIController.EventID.EVENT_SEARCH,null); 

        TextField tfx, tfy;
        /**X coordinate*/
        tfx= (TextField) get(0);
        long cal_X=Integer.parseInt(tfx.getString());
        /**Y coordinate*/
        tfy= (TextField) get(1);
        long cal_Y=Integer.parseInt(tfy.getString()); 

        /*calculate the rectangue coordinate
        **BBOX=437181%2C5241417%2C441981%2C5247817&
        **/
        int i = CGscale.getSelectedIndex();
        if (i==0){
        BBOX_leftup_X=cal_X-(SETTING.realwidth_dis_s0/2);
        BBOX_leftup_Y=cal_Y-(SETTING.realheight_dis_s0/2);
        BBOX_rightdown_X=cal_X+(SETTING.realwidth_dis_s0/2);
        BBOX_rightdown_Y=cal_Y+(SETTING.realheight_dis_s0/2);
        }
        else if (i==1){
            BBOX_leftup_X=cal_X-(SETTING.realwidth_dis_s1/2);
            BBOX_leftup_Y=cal_Y-(SETTING.realheight_dis_s1/2);
            BBOX_rightdown_X=cal_X+(SETTING.realwidth_dis_s1/2);
            BBOX_rightdown_Y=cal_Y+(SETTING.realheight_dis_s1/2);
            }
        else if (i==2){
            BBOX_leftup_X=cal_X-(SETTING.realwidth_dis_s2/2);
            BBOX_leftup_Y=cal_Y-(SETTING.realheight_dis_s2/2);
            BBOX_rightdown_X=cal_X+(SETTING.realwidth_dis_s2/2);
            BBOX_rightdown_Y=cal_Y+(SETTING.realheight_dis_s2/2);
            }
        else if (i==3){
            BBOX_leftup_X=cal_X-(SETTING.realwidth_dis_s3/2);
            BBOX_leftup_Y=cal_Y-(SETTING.realheight_dis_s3/2);
            BBOX_rightdown_X=cal_X+(SETTING.realwidth_dis_s3/2);
            BBOX_rightdown_Y=cal_Y+(SETTING.realheight_dis_s3/2);
            }
        else if (i==4){
            BBOX_leftup_X=cal_X-(SETTING.realwidth_dis_s4/2);
            BBOX_leftup_Y=cal_Y-(SETTING.realheight_dis_s4/2);
            BBOX_rightdown_X=cal_X+(SETTING.realwidth_dis_s4/2);
            BBOX_rightdown_Y=cal_Y+(SETTING.realheight_dis_s4/2);
            }
        else if (i==5){
            BBOX_leftup_X=cal_X-(SETTING.realwidth_dis_s5/2);
            BBOX_leftup_Y=cal_Y-(SETTING.realheight_dis_s5/2);
            BBOX_rightdown_X=cal_X+(SETTING.realwidth_dis_s5/2);
            BBOX_rightdown_Y=cal_Y+(SETTING.realheight_dis_s5/2);
            }
        ////urlstring_BBOX="BBOX="+BBOX_leftup_X+"%2C"+BBOX_leftup_Y+"%2C"+BBOX_rightdown_X+"%2C"+BBOX_rightdown_Y+"&";
        /**http://www.gis-news.de/wms/getmapcap.php?VERSION=1.1.1&BBOX=437181%2C5241417%2C441981%2C5247817&*/
        ////urlstring = urlstring+urlstring_VERSION+urlstring_BBOX; 
        

        //FinalGetMapRequestURLSTRING.append(Request_getmapurl+"VERSION="+Request_wmsversion+"&"+"BBOX="+BBOX_leftup_X+","+BBOX_leftup_Y+","+BBOX_rightdown_X+","+BBOX_rightdown_Y+"&");
        ///System.out.println("FinalGetMapRequestURLSTRING in layerselectCommand 2nd: "+FinalGetMapRequestURLSTRING.toString()) ;
  
	 }
 	else if(c==backCommand) {
			System.out.println("CACEL ") ;
			controller.handleEvent(GUIController.EventID.EVENT_SEARCH_BACK,null);   
            //display.setCurrent(parent);
	}

    }

    public interface RSSListener { 
          public void wmstitleParsed(String wmstitle);
     	  public void layerParsed(String layername, String layertitle); 
     	  public void exception(java.io.IOException ioe); 
     	  }
    

    public void exception(java.io.IOException ioe) { 
    	Alert a = new Alert("Exception", ioe.toString(), null, null); 
    	a.setTimeout(Alert.FOREVER); 
    	display.setCurrent(a, this); 
    	} 
       
}						

