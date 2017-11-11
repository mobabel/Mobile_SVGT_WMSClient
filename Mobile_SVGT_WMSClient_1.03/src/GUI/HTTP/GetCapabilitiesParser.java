package GUI.HTTP;


import org.kxml2.io.*;
import org.xmlpull.v1.*;

import com.tinyline.app.MobileSVGForm;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import java.io.*;
import java.util.Vector;

import GUI.LayerSelectForm;
import GUI.PlaceSearchForm;
import GUI.Common_ExceptionForm;
//import GUI.PlaceSearchForm.RSSListener;
import GUI.CONTROL.GUIController;
import GUI.MODEL.SETTING;
import GUI.MODEL.ServerItem;
import GUI.MODEL.ServerListItem;
import GUI.MODEL.XMLItem;
import GUI.Wait4Parsethread;
import GUI.Wait4ParseAnimation;
//import GUI.T_Wait4LayerAlert;

public class GetCapabilitiesParser {
  	 private LayerSelectForm layerselectForm = null;
 	 private PlaceSearchForm placesearchForm = null;
 	 private Common_ExceptionForm common_exceptionForm = null;
 	 //private T_Wait4LayerAlert wait4layerAlert;
	//protected RSSListener mRSSListener; 
  	 //public void setRSSListener(RSSListener listener){
  	         //mRSSListener = listener; 
  	          //} 
 	 public Display display;
	 public Displayable parent;
	
  	 private Vector layernamesvector = new Vector();
  	 private Vector layertitlesvector = new Vector();
  	 private Vector layercrssvector = new Vector();
     private GUIController controller;
  	 private XMLItem xmlitem;
     public boolean B_paserfinish=false;

  	 
  		
  	 // Non-blocking. 
     public void getcapparse( final String urlstring,final Display d,final Displayable p,GUIController control) { 
		 final XMLItem xmlitem = new XMLItem();
	     controller=control;
	     placesearchForm=(PlaceSearchForm) p;
	     display=d;
	     parent=p;
	     
	     
		 Thread ReadThread = new Thread() {
			private boolean B_parseStop;
			public void run() {
      		  String wmsversion = null;
      		  B_parseStop = false;
      				try {
      	                if (B_paserfinish==false){
      	                	//If there is problem when connecting,user can canel the parsing
      	                	final Command cancelCommand = new Command("Cancel",Command.CANCEL,2);
                            final Wait4ParseAnimation waitAnimation=new Wait4ParseAnimation();
      	                	waitAnimation.start();
      	                	waitAnimation.addCommand(cancelCommand);
      	                	waitAnimation.setCommandListener(new CommandListener() {
      	                    public void commandAction(Command c, Displayable p) {
      	                    	 if(c==cancelCommand){
      	                    		waitAnimation.stop();
      	                    		
      	               	 	        p=parent;
      	                            controller.setCurrent(p);
      	                    	 }
      	                    }
      	                });
      	                	controller.setCurrent(waitAnimation);
      	                	}
      					
      					System.out.println("Want to parse the xml file");
      					KXmlParser parser = new KXmlParser();
      					System.out.println("Create parsing Instance");
      					xmlitem.setRequest_getmapurl(urlstring);
      					HttpConnection httpConnection = (HttpConnection) Connector.open(urlstring);
      					InputStream is = httpConnection.openInputStream();
      					InputStreamReader isr=new InputStreamReader(is) ;
      					//parser.setInput(new InputStreamReader(httpConnection.openInputStream()));
      		   			parser.setInput( new InputStreamReader(is));
      		   		

      		         /* Those code below is design to find <?xml..."
      		          * Because many XML file are not of standard,
      		          * maybe these is whitespace before <?xml...
      		          * so the parsing will be corrupt and Report as below:
      		          * org.xmlpull.v1.XmlPullParserException: PI must not start with xml
      		          *It works with stream and file format
      		          */
      	   			
                      ///////////////////////////////////////
      	   			/*
      	   			 * The code below does work, but the theory is not good as below,
      	   			 * because it must find every "<", and then find whether these is one "?" following it.
      	   			 * that costes more memory.
      	   			 */
      				/// Search the file and justify whether it begin with <?xml...
      				boolean byteHasXMLEntity = false;
      				boolean chatHasXMLEntity = false;
      	
 
 // /**
      				System.out.println("Begin to check in charactor");	
     	   			int ic ;
      	   			int ic1;
      	   			while( (ic = isr.read()) != -1 ){
      	   				//line.append((char)ic) ;
                          if((char)ic=='<'){
                          	 //System.out.println("Find < successfully");
                          	 if( (ic1 = isr.read()) != -1 ){
                          	 if((char)(ic1)=='?'){
                          		 System.out.println("And Find <?xml... successfully");
      							    //chaHasXMLEntity = true;
                          		    System.out.println("Now parse from <?xml...");
      							    //parser.nextTag();
      							    parser.next();
      								parser.nextToken();
      								parser.require(XmlPullParser.START_TAG, null,"WMT_MS_Capabilities");
   								System.out.println("The number of Element Attribute: "+parser.getAttributeCount());
       	    					for(int i=0;i<parser.getAttributeCount();i++){
       	    						System.out.println("Attribute Name: "+parser.getAttributeName(i));
       	    						System.out.println("Attribute Type: "+parser.getAttributeType(i));
       	    						System.out.println("Attribute Value: "+parser.getAttributeValue(i));
       	    						if(parser.getAttributeName(i).equals("version"))
       	    					       wmsversion =parser.getAttributeValue(i);
       	    					}
   	    						System.out.println("Version is: "+wmsversion);
                                xmlitem.setRequest_wmsversion(wmsversion);
   	    						
      								while (parser.nextTag () != XmlPullParser.END_TAG)
      									readCAPxml(parser);
      								parser.require(XmlPullParser.END_TAG, null, "WMT_MS_Capabilities");
      								parser.next();
      								//WMT_MS_Capabilities
      								parser.require(XmlPullParser.END_DOCUMENT, null, null);	
      								B_paserfinish=true;
                          	 }//if
                          	 }
                          }
      	   			}//while
                      ///////////////////////////////////////
// */

                      
                    ///////////////////////////////////////
 /*
    	   			 * The code below seems not to be work, but the theory is better
    	   			 * than the code before, because it doesnt need to find every "<",
    	   			 * just find the first "<", and then find "?". 
    	   			 * This way saves memory.
    	   			 * It works with file format
    	   			 */
      	   		  /**
    				System.out.println("Begin to check in byte");
    				byte[] srcBuf = new byte[2];
    	   			int srcCount = is.read(srcBuf, 0, srcBuf.length);
    				int nBufChar;
    			        if (srcCount <= 0)
    						nBufChar = -1;
    			        else
    						nBufChar = srcBuf[0];
    					    if (nBufChar == '<') {
    						    System.out.println("byte Find < successfully");
    						    nBufChar = srcBuf[1];
    						    if (nBufChar == '?'){
    							    System.out.println("byte And Find <?xml... successfully");
    							    //byteHasXMLEntity = true;
    						       	System.out.println("byte Now parse form <?xml...");
    						       	System.out.println(nBufChar);
    						       	//parser.nextTag();
    						       	parser.next();
      								parser.nextToken();
      								parser.require(XmlPullParser.START_TAG, null,"WMT_MS_Capabilities");
   								System.out.println("The number of Element Attribute: "+parser.getAttributeCount());
       	    					for(int i=0;i<parser.getAttributeCount();i++){
       	    						System.out.println("Attribute Name: "+parser.getAttributeName(i));
       	    						System.out.println("Attribute Type: "+parser.getAttributeType(i));
       	    						System.out.println("Attribute Value: "+parser.getAttributeValue(i));
       	    						if(parser.getAttributeName(i).equals("version"))
       	    					       wmsversion =parser.getAttributeValue(i);
       	    					}
   	    						System.out.println("Version is: "+wmsversion);
                                xmlitem.setRequest_wmsversion(wmsversion);
   	    						
      								while (parser.nextTag () != XmlPullParser.END_TAG)
      									readCAPxml(parser);
      								parser.require(XmlPullParser.END_TAG, null, "WMT_MS_Capabilities");
      								parser.next();
      								//WMT_MS_Capabilities
      								parser.require(XmlPullParser.END_DOCUMENT, null, null);	
      								B_paserfinish=true;
      								}
      							
    						    }//if (nBufChar == '?')
    			             

    				//////////////////////////////////////
  */	
  				 System.out.println("Parsing to the last line");

                if(B_paserfinish==true){
				layerselectForm = new LayerSelectForm(SETTING.FORM_TITLE_LAYERSELECTFORM,display, parent, controller, xmlitem);
				controller.setCurrent(layerselectForm); 
                }

               
      				} catch (Exception e) {
      					e.printStackTrace();
      					//mRSSListener.exception(ioe);
//      					layersmenu.append("Error", null);
      					common_exceptionForm = new Common_ExceptionForm(SETTING.FORM_TITLE_EXCEPTIONFORM,display, parent, controller);
      					common_exceptionForm.append(new StringItem("Exception : ",e.toString()));
      					controller.setCurrent(common_exceptionForm);
      				}
      			} //run

		    public void stop() {
		        B_parseStop = true;
		    }
      			/**
      			 * read the text between START_TAG and END_TAG
      			 * @param parser kxml
      			 */

      			void readCAPxml(KXmlParser parser)
      				throws IOException, XmlPullParserException {

      		        if (parser.getName().equals("Service"))
      		        {
      				//parser.require(XmlPullParser.START_TAG, null, "Service");

      				String wmstitle = null;
      				String description = null;
      				
      				System.out.println (XmlPullParser.START_TAG);//2
      				while (parser.nextTag() != XmlPullParser.END_TAG) {

      					parser.require(XmlPullParser.START_TAG, null, null);
      					/*
      					 * Begin to parse the <XXX> TAG  --> parser.getName()
      					 */
      					String name = parser.getName();
      					//AT FIRST Skip the nodes that dont needed. 
      					if (name.equals("KeywordList")||(name.equals("ContactInformation"))||(name.equals("VersionList"))){                                        
      						System.out.println ("skip subtree:"+name);                                        
      						parser.skipSubTree();                                
      						}
      					//THEN parse the content between the <XXX> and </XXX> TAG --> parser.nextText()
      					else{
      					///System.out.println ("Now is parsing NODE:"+name); 
      					String text = parser.nextText();
      					///System.out.println ("<"+name+">"+text);

      					if (name.equals("Title"))
      						wmstitle = text.trim();
      					else if (name.equals("Name"))
      						description = text.trim();
      					//System.out.println (title +"||" +description);
      					}
      					/*
      					 * END the Parsing
      					 */
      					//mRSSListener.wmstitleParsed(wmstitle);
      					parser.require(XmlPullParser.END_TAG, null, name);
      					System.out.println (name);
      					
      					//System.out.println (XmlPullParser.END_TAG);//3
      					   
      				}
      				if (wmstitle != null) {
//      				    layersmenu.setTicker(new Ticker(wmstitle));
      					xmlitem.setRequest_wmstitle(wmstitle);

       				}

      					}//	        if "Service"
      		        
      		        
      		        
      		        else if (parser.getName().equals("Capability"))
      		        {
      			    String exceptions       = null;
      			    Vector format           = new Vector();
      				String layername        = null;
      				String layertitle       = null;
      				String getmapurl        = null;
      				Vector layercrs         = new Vector();
      				Vector layerCRS         = new Vector();
      				Vector BBox_attributes  = new Vector();
      				//For further dividing the SRS and the Layer selection Form
      				Vector Vlayername        = new Vector();
      				Vector Vlayertitle       = new Vector();
      				Vector layername_Array        = new Vector();
      				Vector layertitle_Array       = new Vector();

      				
      				while (parser.nextTag() != XmlPullParser.END_TAG) {

      					parser.require(XmlPullParser.START_TAG, null, null);
      					/*
      					 * Begin to parse the <XXX> TAG  --> parser.getName()
      					 */
      					String name = parser.getName();
      					//AT FIRST Skip the nodes that dont needed. 				
      					if (name.equals("Exception")){                                        
      	 					while(parser.nextTag() != XmlPullParser.END_TAG){
      	   						String name1 = parser.getName();
      	   						//System.out.println (name1); 
      	 						parser.require(XmlPullParser.START_TAG, null, null);
      	       					String text = parser.nextText();

      	   						if(name1.equals("Format"))
      	   							exceptions=text;
      	   						System.out.println ("EXCEPTIONS is: "+exceptions); 
      	   						xmlitem.setRequest_exceptions(exceptions);
      						}
      						}
      					else if(name.equals("Request")){
   						while(parser.nextTag() != XmlPullParser.END_TAG){
   							String name1 = parser.getName();
   							if (name1.equals("GetMap")){
   								System.out.println ("Now it is in Layer:"+name1);
   			                    while(parser.nextTag() == XmlPullParser.START_TAG ){
   			                    	String name2 = parser.getName();
   	                                if(name2.equals("Format")){ 
   	                                	String text = parser.nextText();
   	        							format.addElement(text);
   	        							for (int i=0;i<format.size();i++)
   	        	   						System.out.println ("FORMAT is: "+(String)format.elementAt(i));
   	        	   					    
   	                                 }
   	                                else if(name2.equals("DCPType")){
   	                                	 System.out.println ("Now it is in Layer:"+name2);
   	                                	 while(parser.nextTag() == XmlPullParser.START_TAG ){
   	                                		 String name3 = parser.getName();
   	                                		 if(name3.equals("HTTP")){
   	                                			 System.out.println ("Now it is in Layer:"+name3);
   	                                			 while(parser.nextTag() == XmlPullParser.START_TAG ){
   	                                				 String name4 = parser.getName();
   	                                				 if(name4.equals("Get")){
   	                                					 System.out.println ("Now it is in Layer:"+name4);
   	                                					 while(parser.nextTag() == XmlPullParser.START_TAG ){

   	                                	    					String name5 = parser.getName(); 
   	                                                            if(name5.equals("OnlineResource")){
   	                                                            	
   	                                                            	System.out.println("The number of Element Attribute: "+parser.getAttributeCount());
   	                                    	    					for(int i=0;i<parser.getAttributeCount();i++){
   	                                    	    						System.out.println("Attribute Name: "+parser.getAttributeName(i));
   	                                    	    						System.out.println("Attribute Type: "+parser.getAttributeType(i));
   	                                    	    						System.out.println("Attribute Value: "+parser.getAttributeValue(i));
   	                                    	    						//attributes.addElement(new String[]{
   	                                    	    								//parser.getAttributeName(i),
   	                                    	    								//parser.getAttributeType(i),
   	                                    	    								//parser.getAttributeValue(i)
   	                                    	    								//});
   	                                    	    						if(parser.getAttributeName(i).equals("xlink:href"))
   	                                    	    						getmapurl=parser.getAttributeValue(i);
   	                                    	    					}
   	                                    	    					System.out.println ("The GETMap URL is: "+getmapurl);
   	                                    	    				    xmlitem.setRequest_getmapurl(getmapurl);
   	                                    	    					String text = parser.nextText();
   	                                                            
   	                                                            }
   	                                					 }
   	                                						//if (getmapurl != null) {
   	                                							//System.out.println ("The GETMap URL is:"+getmapurl);
   	                                						//}
   	                                				 }
   	                                			 }
   	                                			 
   	                                		 }
   	                                	 }
   	                                 }
   	                                 else {
   	         							//parser.skipSubTree();
   	        							//System.out.println ("SkIP:"+name2);
   	                                 }
   			                    	
   			                        }
   							}
   							else if(name1.equals("GetCapabilities")){
   								parser.skipSubTree();
   								System.out.println ("SkIP:"+name1);
   							}
   							else{
   								parser.skipSubTree();
   								System.out.println ("SkIP:"+name1);//SkIP:GetFeatureInfo
   							}
   						}
   					}
      					//Begin to parse the NODE Layer
      					//THEN parse the content between the <XXX> and </XXX> TAG --> parser.nextText()
   					else if(name.equals("Layer")){
      					///System.out.println ("Now is parsing NODE:"+name);

      					while(parser.nextTag() != XmlPullParser.END_TAG){
      						String name1 = parser.getName();
      						parser.require(XmlPullParser.START_TAG, null, null);
//      						for (int crs=0;crs<layerCRS.size();crs++){
//      							String text4=null;
//	                        	while(text4.equals((String)layerCRS.elementAt(crs)))
//      						}
       	   					
      							
      					if (name1.equals("Layer")){ 

      						///System.out.println ("Now it is in Layer:"+name1); 
      						
      	                     while(parser.nextTag() == XmlPullParser.START_TAG ){
      	                    	parser.require(XmlPullParser.START_TAG, null, null);
      	    					String name2 = parser.getName();
      	                    	///System.out.println ("Now it is in NODE:"+name2);
      	                    	///System.out.println ("<"+name2+">"+text);
      	                            if(name2.equals("Name")){
      	                            	String text1 = parser.nextText();
      	                        	layername = text1.trim();
      	                            }
      	                        	else if(name2.equals("Title")){
      	                        		String text2 = parser.nextText();
      								layertitle = text2.trim();
      								}
      	                        	else if(name2.equals("SRS")){
      	                        		String text3 = parser.nextText();
      	                        	    layercrs.addElement(text3.trim());
//      	                        		for (int crs=0;crs<layerCRS.size();crs++)
//      	                        	     if(text3.equals((String)layerCRS.elementAt(crs))){
//      	                        	    	Vlayername.addElement(layername); 
//      	                        	    	Vlayertitle.addElement(layertitle);
//      	                        	     }
      	                        	}
      	       	   					else {
      	       	   					    //String text1 = parser.nextText();
      	       	   	   					parser.skipSubTree();
      	       	   					    System.out.println ("SkIP the node in Layer: "+name2);
      	       	   	   					}
      	                        }
      						System.out.println (layertitle+"||"+layername+"||");
                            //Insert the mark for the further String spliting, very important!!!!
          					layercrs.addElement("|");
      						if (layername != null || layertitle!=null ) {
      							layernamesvector.addElement(layername);
      							layertitlesvector.addElement(layertitle);
      							//mRSSListener.layerParsed(layername, layertitle);
    							      					        
      						}
      	                	                    //}//while2

      						}
      					else if (name1.equals("SRS")){
      						    String text = parser.nextText();
    	   						layerCRS.addElement(text.trim());
    	   						for (int i=0;i<layerCRS.size();i++)
    	   							System.out.println ("When parsing, SRS is "+(String)layerCRS.elementAt(i));
    	   					}
      					else if (name1.equals("BoundingBox")){
   	   						System.out.println(name1);
      						while(parser.nextTag() == XmlPullParser.START_TAG ){
      						parser.require(XmlPullParser.START_TAG, null, null);
   	                    	System.out.println("The number of Element Attribute: "+parser.getAttributeCount());
   	    					for(int i=0;i<parser.getAttributeCount();i++){
   	    						System.out.println("Attribute Name: "+parser.getAttributeName(i));
   	    						//System.out.println("Attribute Type: "+parser.getAttributeType(i));
   	    						System.out.println("Attribute Value: "+parser.getAttributeValue(i));

   	    					}
                            /*
                             * i=1 minx
                             * i=2 miny
                             * i=3 maxx
                             * i=4 maxy
                             */
   							BBox_attributes.addElement(new String[]{
   									parser.getAttributeValue(1),
   									parser.getAttributeValue(2),
   									parser.getAttributeValue(3),
   									parser.getAttributeValue(4)
   							});
   	   	    					for(int j=0;j<BBox_attributes.size();j++){
   	    						String[] BBox_attributesStrArray=(String[])BBox_attributes.elementAt(j);
   	    						System.out.println(j+" minx: "+BBox_attributesStrArray[0]+
   	    								" miny: "+BBox_attributesStrArray[1]+
   	    								" maxx: "+BBox_attributesStrArray[2]+
   	    								" maxx: "+BBox_attributesStrArray[3]);
   	    						// Can add one vector for each xy for comparing later!
   	    					}
      						}
   	   					}
      					else{
      					        System.out.println ("SkIP the nodE except Layer,SRS,BoundingBox: "+name1);
      						    parser.skipSubTree();
           					}
      					}//while1
      					}//if
      		            else {
      						parser.skipSubTree();
      					    System.out.println ("SkIP the node except Layer: "+name);
      		         }
      					///System.out.println ("<"+name+">");
      					parser.require(XmlPullParser.END_TAG, null, name);
      				}

      				if (layername != null || layertitle!=null) {
      					     System.out.println("Layer Name Vector is "+layernamesvector.toString());
      				         xmlitem.setlayernamesvector(layernamesvector);
                             xmlitem.setlayertitlesvector(layertitlesvector);
      				}

      				if(layerCRS != null){
    				      for(int i=0;i<layerCRS.size();i++){
    							System.out.println ("WMS's SRS is "+(String)layerCRS.elementAt(i));
   				         }
						    xmlitem.setRequest_layersCRS(layerCRS);
      				}
      				if(layercrs != null){
  				      for(int i=0;i<layercrs.size();i++){
  							System.out.println ("Each layers srs is "+(String)layercrs.elementAt(i));						   
 				         }
					    //xmlitem.setlayercrssvector(layercrs);
  				    System.out.println ("Result layers srs is "+layercrs.toString());
  				    xmlitem.split(layercrs.toString(),"|");
    				}
//      				if(Vlayername != null){
//  				      for(int i=0;i<Vlayername.size();i++){
//  							//System.out.println ("layers names in srs "+""+" are: "+(String)Vlayername.elementAt(i));
//  				    	//layername_Array.addElement(new String[]{(String)Vlayername.elementAt(i)});
//  				    	layername_Array.addElement((String)Vlayername.elementAt(i));
//    						    //xmlitem.setRequest_layercrs(layercrs);
// 				         }
//						System.out.println ("layers names in srs "+""+" are: "+layername_Array.toString());
////  	    					for(int j=0;j<layername_Array.size();j++){
////   	    						String[] layername_String_Array=(String[])layername_Array.elementAt(j);
////   	    						System.out.println(j+" layername: "+layername_String_Array[0]);
////
////   	    					}
//    				}
//      				if(Vlayertitle != null){
//    				      for(int i=0;i<Vlayertitle.size();i++){
//    							//System.out.println ("layers titles in srs "+""+" are: "+(String)Vlayertitle.elementAt(i));
//    				    	  layertitle_Array.addElement(new String[]{(String)Vlayertitle.elementAt(i)});
//
//    				    	  System.out.println ("layers titles in srs "+""+" are: "+(String[])layertitle_Array.elementAt(i));
//    							//xmlitem.setRequest_layercrs(layercrs);
//   				         }
//							//System.out.println ("layers titles in srs "+""+" are: "+layertitle_Array.toString());
//      				}
    	      		if(format != null){
  				      for(int i=0;i<format.size();i++){
  							System.out.println ("Format is "+(String)format.elementAt(i));
 				                                      }  
						xmlitem.setRequest_format(format);
			                    }


      					}//	        if "Capability"
      			}//readxml
	 
      	 } ;//Thread 
      						 ReadThread.start(); 
      						 

      						 } 

       }