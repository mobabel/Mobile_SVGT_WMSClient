package GUI.HTTP;

import org.kxml2.io.*;
import org.xmlpull.v1.*;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import java.io.*;
import java.util.Vector;

import GUI.Common_ExceptionForm;
import GUI.Format_PositionForm;
import GUI.LayerSelectForm;
import GUI.MobileImageForm;
import GUI.PlaceSearchForm;
import GUI.Wait4ParseAnimation;
import GUI.Wait4Parsethread;
//import GUI.PlaceSearchForm.RSSListener;
import GUI.CONTROL.GUIController;
import GUI.MODEL.SETTING;
import GUI.MODEL.XMLItem;
import com.tinyline.app.MobileSVGForm;

public class Result_ExceptionParser {
	private MobileSVGForm    mobilesvgForm   = null;
	private Common_ExceptionForm    common_exceptionForm   = null;
	private MobileImageForm    mobileimageForm   = null;
	private Display display;
	private Displayable parent;
	private GUIController controller;
	private Format_PositionForm    format_positionform   = null;
	/**Check the Wait4ParseAnimation status*/
	public boolean B_validatefinish=false;
	public boolean B_validatefinish_go=false;
	public boolean B_validatefinish_goimage=false;
	private	String S_exception = null;
	
    public void getcapparse( final String urlstring,final Display d,final Displayable p,GUIController control,Format_PositionForm format_positionForm) { 
    	 controller=control;
	     display=d;
	     parent=p;
	     format_positionform=format_positionForm;
    	
		 Thread ReadThread = new Thread() {
			public void run() {
	      		  
	      				try {
	      	                if (B_validatefinish==false&&B_validatefinish_goimage==false){
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
	      					
	      					System.out.println("Go to validate the result file");
	      					KXmlParser parser = new KXmlParser();
	      					System.out.println("Create parsing Instance");
	      					
	      					HttpConnection httpConnection = (HttpConnection) Connector.open(urlstring);
	      					InputStream is = httpConnection.openInputStream();
	      					InputStreamReader isr=new InputStreamReader(is) ;
	      					//parser.setInput(new InputStreamReader(httpConnection.openInputStream()));
	      		   			parser.setInput( new InputStreamReader(is));
	      		   		    String type = httpConnection.getType();
//	      		   	 if (type.equals("images/svg+xml") && type.equals("text/html")){
	      		         /* Those code below is design to find <?xml..."
	      		          * Because many XML file are not of standard,
	      		          * maybe these is whitespace before <?xml...
	      		          * so the parsing will be corrupt and Report as below:
	      		          * “org.xmlpull.v1.XmlPullParserException: PI must not start with xml”
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
	      	
  /**
	      				System.out.println("Begin to check in charactor");	
	      	   			int ic ;
	      	   			int ic1;
	      	   			while( (ic = isr.read()) != -1 ){

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
	      								String startTagName = parser.getName();
	      								//If this is one exception xml file,then parse it to get the error message
	      								if(startTagName.equals("ServiceExceptionReport")){
		      								parser.require(XmlPullParser.START_TAG, null,"ServiceExceptionReport");
		   	    						    System.out.println("This is : ServiceExceptionReport xml file");
		      								while (parser.nextTag () != XmlPullParser.END_TAG)
		      									readExceptionxml(parser);
		      								parser.require(XmlPullParser.END_TAG, null, "ServiceExceptionReport");
		      								parser.next();
		      								parser.require(XmlPullParser.END_DOCUMENT, null, null);	
		      								//B_validatefinish=true;
	      								}
	      								
	                                    //If this is svg file, then load SVGTinyLine
	      								else if(startTagName.equals("svg")){
		      								System.out.println("This is : svg xml file");
		      						 		mobilesvgForm = new MobileSVGForm(display, parent,controller);
		      								controller.setCurrent(mobilesvgForm);
	      								}

	                          	 }//if char=?
	                          	 
	                          	 }
 	                          }
	      	   			}//while
	                      ///////////////////////////////////////
	      	   			  */
	
	      	   			// /**
	                    ///////////////////////////////////////
	    	   			/*
	    	   			 * The code below seems not to be work, but the theory is better
	    	   			 * than the code before, because it doesnt need to find every "<",
	    	   			 * just find the first "<", and then find "?". 
	    	   			 * This way saves memory.
	    	   			 * It works with file format
	    	   			 */
	    				System.out.println("Begin to check in byte");	
	    				byte[] srcBuf = new byte[2];
	    	   			int srcCount = is.read(srcBuf, 0, srcBuf.length);
	    				int nBufChar;
	    			        if (srcCount <= 0){
	    						nBufChar = -1;
	    			           }
	    			        else{
	    						nBufChar = srcBuf[0];
	    			            }	
	    			        
								//If this is PNG file--just GetMapRequest result from the raster image based WMS 
								//And the begin byte is PNG
								 if (type.equals("image/png")){
 									System.out.println("Begin to load raster PNG image");
 									B_validatefinish_goimage=true;
// 									mobileimageForm = new MobileImageForm(SETTING.FORM_TITLE_MOBILEIMAGEFORM,display, parent,controller,format_positionform);
//      								controller.setCurrent(mobileimageForm);
 								}   
								 
								 else if (nBufChar == '<') {
	    						    System.out.println("byte Find < successfully");
	    						    nBufChar = srcBuf[1];
	    						    if (nBufChar == '?'){
	    							    System.out.println("byte And Find <?xml... successfully");
	    							    //byteHasXMLEntity = true;
	    						       	System.out.println("byte Now parse form <?xml...");
	    						       	
	    						       	//parser.nextTag();
	    								parser.next();
	    								parser.nextToken();
	      								String startTagName = parser.getName();
	      								//If this is one exception xml file,then parse it to get the error message
	      								if(startTagName.equals("ServiceExceptionReport")){
		      								parser.require(XmlPullParser.START_TAG, null,"ServiceExceptionReport");
		   	    						    System.out.println("This is : ServiceExceptionReport xml file");
		      								while (parser.nextTag () != XmlPullParser.END_TAG)
		      									readExceptionxml(parser);
		      								    parser.require(XmlPullParser.END_TAG, null, "ServiceExceptionReport");
		      								    parser.next();
		      								    parser.require(XmlPullParser.END_DOCUMENT, null, null);	
		      								    B_validatefinish=true;
		      								    System.out.println("Parsing to the last line");
	      								}
	      								
	                                    //If this is svg file, then load SVGTinyLine
	      								else if(startTagName.equals("svg")){
		      								System.out.println("This is : svg xml file");
		      						 		mobilesvgForm = new MobileSVGForm(SETTING.FORM_TITLE_MOBILESVGFORM,display, parent,controller,format_positionform);
		      								controller.setCurrent(mobilesvgForm);
	      								}

	    						    }//if (nBufChar == '?')
	    			             }
//	      		   		}



// 								else if (type.equals("image/jpeg")&&type.equals("image/geotiff")&&type.equals("image/tiff")){
// 									B_validatefinish=true;
// 									common_exceptionForm = new Common_ExceptionForm(SETTING.FORM_TITLE_EXCEPTIONFORM,display, parent, controller);
// 									common_exceptionForm.append(new StringItem("Exception : \n","The image format is not PNG but is: "+type));
// 			      					controller.setCurrent(common_exceptionForm);
// 								}
 								else {
 	 									common_exceptionForm = new Common_ExceptionForm(SETTING.FORM_TITLE_EXCEPTIONFORM,display, parent, controller);
 	 									common_exceptionForm.append(new StringItem("Exception : \n","The image format is not PNG but is: "+type));
 	 			      					controller.setCurrent(common_exceptionForm);
									}

	    				//////////////////////////////////////
	    				//  */	
	    			 System.out.println("Memory used: " +Runtime.getRuntime().freeMemory() +" / " + Runtime.getRuntime().totalMemory());		
	  				

	                if(B_validatefinish==true){
	                	common_exceptionForm = new Common_ExceptionForm(SETTING.FORM_TITLE_EXCEPTIONFORM,display, parent, controller);
	                	common_exceptionForm.append(new StringItem("Exception : \n",S_exception));
      					controller.setCurrent(common_exceptionForm);
	                }
	                if(B_validatefinish_goimage==true){
						mobileimageForm = new MobileImageForm(SETTING.FORM_TITLE_MOBILEIMAGEFORM,display, parent,controller,format_positionform);
						controller.setCurrent(mobileimageForm);
	                }
	               
	      				} catch (Exception e) {
	      					e.printStackTrace();
	      					common_exceptionForm = new Common_ExceptionForm(SETTING.FORM_TITLE_EXCEPTIONFORM,display, parent, controller);
	      					common_exceptionForm.append(new StringItem("Exception : \n",e.toString()));
	      					controller.setCurrent(common_exceptionForm);
	      				}
	      			} //run
			
 			void readExceptionxml(KXmlParser parser)
				throws IOException, XmlPullParserException {
  		        if (parser.getName().equals("ServiceException"))
  		        {
  		        	System.out.println("This is : ServiceException");

  					parser.require(XmlPullParser.START_TAG, null, null);
  	  				String text = parser.nextText();
  	  				S_exception = text.trim();
  	  			    System.out.println("S_exception is : "+S_exception);
  				}
  				if (S_exception != null) {
//  					B_validatefinish=true;
                   }

  					}//if "ServiceException"

		 };//thread
		 ReadThread.start(); 
    }
	

}
