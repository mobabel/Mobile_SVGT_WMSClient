package GUI.HTTP;
import javax.microedition.io.*;
import java.io.*;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;


public class GetRequest extends Thread
{	
	String urlstring ;
	Display display ;
    public static String resultsvgcode;
 


    public GetRequest(String urlstring) 
	{
		this.urlstring = urlstring ;

		System.out.println("Ready to connect: "+this.urlstring) ;

		
	}
	public void run(){
		HttpConnection hc = null;
		InputStream is = null;
        InputStreamReader isr=null;

		StringBuffer resultsvg = new StringBuffer("");
		try {
			// openning up http connection with the web server
    		hc = (HttpConnection)Connector.open(urlstring);
			System.out.println("Length is" + hc.getLength()) ;
			
		
            //decide whether successfully connect
            //int grc = hc.getResponseCode(); 
            //if (grc != HttpConnection.HTTP_OK) {
                    //throw new Exception(hc.getResponseMessage());
            	//placesearchForm1.delete(placesearchForm.size()-1);
            	//placesearchForm1.append(new Gauge("Connecting......",false,100,50));
            	//System.out.println("Not ready" ) ;
            //}

			//establishing input stream from the connection
   			is = hc.openInputStream();	
   			
            isr= new InputStreamReader(is);
            int ic;
            while((ic=isr.read())!=-1){
            	resultsvg.append((char)ic);
                //String resultsvgcode=resultsvg.toString();
        		//System.out.println(resultsvg.toString());
            }          

        } catch (IOException ioe) {
			System.out.println(ioe);

        } 
        
        finally
 		{
 			try
 			{
 				if(hc!=null)
 					hc.close();
 			}catch(Exception e){}
 		}
        
        String resultsvgcode=resultsvg.toString();
		System.out.println(resultsvgcode);
	}
}