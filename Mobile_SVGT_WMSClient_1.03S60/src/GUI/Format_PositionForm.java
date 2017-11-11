package GUI;

import javax.microedition.midlet.*;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.*;

import com.tinyline.app.MobileSVGForm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Calendar;
import java.util.Vector;

import GUI.HTTP.GetCapabilitiesParser;
import GUI.HTTP.Result_ExceptionParser;
import GUI.MODEL.SETTING;
import GUI.MODEL.ServerListItem;
import GUI.MODEL.XMLItem;
import GUI.CONTROL.GUIController;
import GUI.PlaceSearchForm;
import GUI.LayerSelectForm;

public class Format_PositionForm extends Form implements CommandListener{
    private GUIController controller;
    private ServerListItem itemer;
    private Display display;
    private Displayable parent;
    private XMLItem xmlitem;
	 
    private Command sendCommand = new Command("Send", Command.OK, 2);
    private Command backCommand = new Command("Back", Command.BACK, 1);

    private PlaceSearchForm  placesearchForm  = null;
    private LayerSelectForm  layerselectform   = null;
    private StringItem       stringitem_wmstitle=null;
    private ChoiceGroup      CGformats = null;
	
    private Vector Request_format ;
	private String Request_wmstitle,Request_selectedFORMATs,Request_exceptions;
	private String String_FinalURL="";
	public StringBuffer FinalGetMapRequestURLSTRING=new StringBuffer("");
	
    public Format_PositionForm(String title,Display d, Displayable p,GUIController control,XMLItem xmlItem,LayerSelectForm layerselectForm) {
        super(title);
        display=d;
        parent=p;
        addCommand(backCommand);
    	addCommand(sendCommand);
        setCommandListener(this);
        controller=control;
        xmlitem = xmlItem;
        layerselectform=layerselectForm;
        
        Request_format  = new Vector(); 
   		//prepare for the send command
   		Request_format           = xmlItem.getRequest_format();
   		Request_wmstitle         = xmlItem.getRequest_wmstitle();
   		Request_exceptions       = xmlItem.getRequest_exceptions();
   		
   		
   		
		System.out.println("---------------------01-----------------------") ;
		Request_wmstitle=xmlItem.getRequest_wmstitle();
		//System.out.println(Request_wmstitle) ;
        stringitem_wmstitle=new StringItem(null,Request_wmstitle);
        append(stringitem_wmstitle);
        
		System.out.println("---------------------02-----------------------") ;       
		  /**Layer and CRSselecting list */
		CGformats= new ChoiceGroup("Choose Image's Format",Choice.EXCLUSIVE);
	      for(int i=0;i<Request_format.size();i++){
				System.out.println ("SRS is "+(String)Request_format.elementAt(i));
				CGformats.append( (String) Request_format.elementAt(i),null);  
	         }
		System.out.println("---------------------03-----------------------") ;

	    append(CGformats);
    }
	
    public void commandAction(Command c, Displayable p) {
    	if(c==sendCommand) {
         //Send last request to the server	
    		
     		System.out.println("Prepare to send request: "+layerselectform.getString_TransferURL()) ;

    		/***
    		** urlstring_LAYERS="LAYERS="+string_airport+string_city+string_lake+"&"+"STYLES=%2C%2C&";
    		** urlstring_REQUEST="REQUEST=GetMap&"+string_layer_airport+ string_layer_city+string_layer_lake+"style=&";
    		**    LAYERS=airports%2Cctybdpy2%2Clakespy2&STYLES=%2C%2C&REQUEST=GetMap&layer1=airports&layer2=ctybdpy2&layer3=lakespy2&style=&
    		** String urlstring=urlstring_f;
    		** urlstring = urlstring+urlstring_LAYERS+urlstring_REQUEST;
    		 */
            //Reset the FinalGetMapRequestURLSTRING for the new layer selection
            FinalGetMapRequestURLSTRING.delete(0,FinalGetMapRequestURLSTRING.length());
            FinalGetMapRequestURLSTRING.append(layerselectform.getString_TransferURL());
    		System.out.println(FinalGetMapRequestURLSTRING.toString()) ;
            //FinalGetMapRequestURLSTRING.append(Request_getmapurl+"VERSION="+Request_wmsversion+"&"+"BBOX="+placesearchForm.BBOX_leftup_X+","+placesearchForm.BBOX_leftup_Y+","+placesearchForm.BBOX_rightdown_X+","+placesearchForm.BBOX_rightdown_Y+"&");
    		System.out.println(FinalGetMapRequestURLSTRING.toString()) ;
    		System.out.println("---------------------05-----------------------") ;
    		
            //	Ready to connect to the server
    		
    		/*
    		 * 	//SRS=EPSG%3A26715&minx=189775.33&miny=4816305.37&maxx=190051.33&maxy=4816525.37&
    		    urlstring_SRS="SRS="+"EPSG%3A26715"+"&minx="+SRS_minx+"&miny="+SRS_miny+"&maxx="+SRS_maxx+"&maxy="+SRS_maxy+"&";
    		    //minx=189775.33&miny=4816305.37&maxx=190051.33&maxy=4816525.37&  doesnt need
    		    //WIDTH=240&HEIGHT=320&
    		    urlstring_Srceen_WH="WIDTH="+Srceen_WIDTH+"&HEIGHT="+Srceen_HEIGHT+"&";
    		    //FORMAT=image/svgxml&EXCEPTIONS=application%2Fvnd.ogc.se_xml&transparent=True&button=GetMapRequest
    		    urlstring_Left="FORMAT=image%2Fsvgxml&EXCEPTIONS=application%2Fvnd.ogc.se_xml&transparent=True&button=GetMapRequest";
    		    urlstring=urlstring+urlstring_SRS+urlstring_Srceen_WH+urlstring_Left;
    		    String finalurlstring=urlstring;
    		    System.out.println("finalurlstring in SendCommand: "+finalurlstring) ;
    		 * 
    		 */
    		int i = CGformats.getSelectedIndex();
    		Request_selectedFORMATs=CGformats.getString(i);
    		FinalGetMapRequestURLSTRING.append("FORMAT="+ Request_selectedFORMATs+"&");
    		FinalGetMapRequestURLSTRING.append("EXCEPTIONS="+ Request_exceptions+"&");
            FinalGetMapRequestURLSTRING.append("transparent=TRUE&button=GetMapRequest");
    		System.out.println("finalurlstring in SendCommand: "+FinalGetMapRequestURLSTRING.toString());
    		
    		//FinalURL_Transfer2FormatString=FinalGetMapRequestURLSTRING.toString();
    		//Store and prepare to transfer the viarient


    		setString_FinalURL(FinalGetMapRequestURLSTRING.toString());

    		Result_ExceptionParser exceptionparser = new Result_ExceptionParser() ;
    		exceptionparser.getcapparse(getString_FinalURL(),display,this, controller,this);      

    	}
     	else if(c==backCommand) {
     	 	    p=parent;
                controller.setCurrent(p);
    			System.out.println("CACEL ") ;
    	}
        }
	
    //use the url in other Form
    public String getString_FinalURL() {
    		return String_FinalURL;
    	}
    //transfer the url in current Form
    public void setString_FinalURL(String String_FinalURL) {
    		this.String_FinalURL = String_FinalURL;
    	}
}
