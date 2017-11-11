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


public class LayerSelectForm extends Form implements CommandListener{
    private GUIController controller;
    private ServerListItem itemer;
    private Display display;
    private Displayable parent;
    private XMLItem xmlitem;
	 
    private Command selectformatCommand = new Command("Select Format", Command.OK, 2);
    private Command backCommand = new Command("Back", Command.BACK, 1);
    

    private PlaceSearchForm placesearchForm;
    private Format_PositionForm    format_positionForm   = null;
    private StringItem     stringitem_wmstitle=null;
    private ChoiceGroup CGcrss = null;
    private ChoiceGroup CGlayers=null;
    
    private String string_layer="layer";
    private String string_layername=null;
    private String string_layer_name=null;
    
    private String urlstring_STYLES="%2C%2C&";
    private String urlstring_FORMAT="FORMAT=image%2Fsvgxml&";
    public StringBuffer FinalGetMapRequestURLSTRING=new StringBuffer("");
    public static String   FinalURL_Transfer2FormatString;
	//final url the Form created in the last time
	private String String_TransferURL="";
    
	//private XMLItem xmlitem;	
    private Vector Request_layersname, Request_layerstitle,layernamesvector,layertitlesvector,
    Request_layersCRS,Request_format,layercrssvector_devide ;
	private String  Request_wmsversion, Request_getmapurl, Request_wmstitle, Request_exceptions,Request_selectedCRSs;

	
    public LayerSelectForm(String title,Display d, Displayable p,GUIController control,XMLItem xmlItem) {
        super(title);
        display=d;
        parent=p;
        addCommand(backCommand);
    	addCommand(selectformatCommand);
        setCommandListener(this);
        controller=control;
        xmlitem = xmlItem;

        
    	Request_layersname  = new Vector(); 
    	Request_layerstitle = new Vector(); 
    	Request_layersCRS   = new Vector();
   		layernamesvector    = new Vector();
   		layertitlesvector   = new Vector();
   		layercrssvector_devide   = new Vector();

   		//prepare for the send command
   		Request_layersname       = xmlItem.getlayernamesvector();
   		Request_exceptions       = xmlItem.getRequest_exceptions();
   		Request_format           = xmlItem.getRequest_format();
   		Request_wmsversion       = xmlItem.getRequest_wmsversion();
   		Request_getmapurl        = xmlItem.getRequest_getmapurl();
   		Request_wmstitle         = xmlItem.getRequest_wmstitle();
   		layercrssvector_devide   = xmlitem.GetDevidelayercrsVector();
   		
		System.out.println("---------------------01-----------------------") ;
		
		//System.out.println(Request_wmstitle) ;
        stringitem_wmstitle=new StringItem(null,Request_wmstitle);
        append(stringitem_wmstitle);
		System.out.println("---------------------02-----------------------") ;       
		  /**Layer and CRSselecting list */
	      CGcrss= new ChoiceGroup("Layers' CRS",Choice.EXCLUSIVE);
	      Request_layersCRS=xmlItem.getRequest_layersCRS();
	      for(int i=0;i<Request_layersCRS.size();i++){
				System.out.println ("SRS is "+(String)Request_layersCRS.elementAt(i));
	    	    CGcrss.append( (String) Request_layersCRS.elementAt(i),null);  
	         }
		System.out.println("---------------------03-----------------------") ;
	      /**Layers' list */
	      CGlayers= new ChoiceGroup("Layers",Choice.MULTIPLE);
	      Request_layerstitle=xmlItem.getlayertitlesvector();
		  System.out.println(Request_layerstitle.toString()) ;
		  System.out.println(layercrssvector_devide.toString()) ;
	      for(int i=0;i<Request_layerstitle.size();i++){
	    	CGlayers.append( (String) Request_layerstitle.elementAt(i)+" "+(String)layercrssvector_devide.elementAt(i),null); 
	         }
	      
		 append(CGcrss);
	     append(CGlayers);
    }
    
	
    public void commandAction(Command c, Displayable p) {
	if(c==selectformatCommand) {
     //Send tansfer request to the next Form	
		
 		System.out.println("Prepare to send request: "+xmlitem.getRequest_getmapurl()) ;
 		System.out.println("Layers name: "+Request_layersname.toString()) ;
		//gether the select value of layers
		/***LAYERS=airports%2Cctybdpy2%2Clakespy2&
		** STYLES=%2C%2C&
		** REQUEST=GetMap&layer1=airports&layer2=ctybdpy2&layer3=lakespy2&style=
		***/
		boolean[] selectedlayers=new boolean[Request_layersname.size()];
		//((List)d).getSelectedFlags(selectedlayers);//old list
		CGlayers.getSelectedFlags(selectedlayers);
		String komma_r="%2C";
		boolean komma=true;
		StringBuffer string_layernameTEMP = new StringBuffer("");
		StringBuffer string_layer_nameTEMP = new StringBuffer("");
		System.out.println("---------------------04-----------------------") ;
		for(int i=0; i<Request_layersname.size();i++){
			if(selectedlayers[i]){
				String text = (String) Request_layersname.elementAt(i);
				string_layername=text;
				string_layer_name=string_layer+(i+1)+"="+text+"&";	
				if (komma) {
			        string_layername=text;
			        komma = false;
			        } else {
			         string_layername=komma_r+text;
			        }
				System.out.println(string_layername) ;
				System.out.println(string_layer_name) ;
				//string_layernameVector.addElement(string_layername);
				string_layernameTEMP.append(string_layername);
				string_layer_nameTEMP.append(string_layer_name);
				}

			}
		System.out.println(string_layernameTEMP.toString()) ;
		System.out.println(string_layer_nameTEMP.toString()) ;

		/***
		** urlstring_LAYERS="LAYERS="+string_airport+string_city+string_lake+"&"+"STYLES=%2C%2C&";
		** urlstring_REQUEST="REQUEST=GetMap&"+string_layer_airport+ string_layer_city+string_layer_lake+"style=&";
		**    LAYERS=airports%2Cctybdpy2%2Clakespy2&STYLES=%2C%2C&REQUEST=GetMap&layer1=airports&layer2=ctybdpy2&layer3=lakespy2&style=&
		** String urlstring=urlstring_f;
		** urlstring = urlstring+urlstring_LAYERS+urlstring_REQUEST;
		 */
        //Reset the FinalGetMapRequestURLSTRING for the new layer selection
        FinalGetMapRequestURLSTRING.delete(0,FinalGetMapRequestURLSTRING.length());
        
		System.out.println(FinalGetMapRequestURLSTRING.toString()) ;
        FinalGetMapRequestURLSTRING.append(Request_getmapurl+"VERSION="+Request_wmsversion+"&"+"BBOX="+placesearchForm.BBOX_leftup_X+","+placesearchForm.BBOX_leftup_Y+","+placesearchForm.BBOX_rightdown_X+","+placesearchForm.BBOX_rightdown_Y+"&");
		FinalGetMapRequestURLSTRING.append("LAYERS="+string_layernameTEMP.toString()+"&"+"STYLES=%2C%2C&").append("REQUEST=GetMap&"+string_layer_nameTEMP+"style=&");
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
		int i = CGcrss.getSelectedIndex();
		Request_selectedCRSs=CGcrss.getString(i);
		FinalGetMapRequestURLSTRING.append("SRS="+Request_selectedCRSs+"&");
		FinalGetMapRequestURLSTRING.append("WIDTH="+SETTING.Srceen_WIDTH+"&HEIGHT="+SETTING.Srceen_HEIGHT+"&");
		//FinalGetMapRequestURLSTRING.append("FORMAT="+ Request_format+"&");
		//FinalGetMapRequestURLSTRING.append("EXCEPTIONS="+ Request_exceptions+"&");
        //FinalGetMapRequestURLSTRING.append("transparent=TRUE&button=GetMapRequest");
		System.out.println("finalurlstring in selectformatCommand: "+FinalGetMapRequestURLSTRING.toString());
		
		FinalURL_Transfer2FormatString=FinalGetMapRequestURLSTRING.toString();
		//Store and prepare to transfer the viarient


		setString_TransferURL(FinalGetMapRequestURLSTRING.toString());

		//Result_ExceptionParser exceptionparser = new Result_ExceptionParser() ;
		//exceptionparser.getcapparse(FinalURL_Transfer2FormatString,display,this, controller);
		
		format_positionForm = new Format_PositionForm(SETTING.FORM_TITLE_FORMATPOSITIONFORM,display, this,controller,xmlitem,this);
		controller.setCurrent(format_positionForm);

	}
 	else if(c==backCommand) {
 	 	    p=parent;
            controller.setCurrent(p);
			System.out.println("CACEL ") ;
	}
    }


 	

    //use the url in other Form
    public String getString_TransferURL() {
    		return String_TransferURL;
    	}
    //transfer the url in current Form
    public void setString_TransferURL(String String_TransferURL) {
    		this.String_TransferURL = String_TransferURL;
    	}

}


