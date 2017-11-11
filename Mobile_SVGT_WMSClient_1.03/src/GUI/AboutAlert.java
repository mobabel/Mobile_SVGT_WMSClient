package GUI;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Image;


public class AboutAlert extends Alert{
    private Image icon_about=null;
    private final static String string_about=
         "\n                    Mobile SVGT 1.02 \n" 
        +"   This program is designed as Master Thesis\n" 
        +"                    writed by Leelight          \n"
        +"                    supervised by               \n"
        +"                    Prof. Dr.-Ing. Franz-Josef Behr    \n"
        +"                    Prof. Dr.-Ing. Dietrich Schroeder  \n"
        +"  It uses API from TinyLine(C)Andrew Girow,   \n"
        +"                           KXML(C)Stefan Haustein \n"
        +"                    leelight@hotmail.com";
    
    public AboutAlert(String title){
        super(title);
        setTimeout(FOREVER);

        try {
        	icon_about=Image.createImage("/icon/icon_about.png");
	        setImage(icon_about);
	        
	    } catch (java.io.IOException x) {
	    	icon_about=null;
    	    System.out.println("Load icon error: ");
	    }
        this.setString(string_about);
    }
}
